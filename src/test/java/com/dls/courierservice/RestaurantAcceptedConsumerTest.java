package com.dls.courierservice;

import com.dls.courierservice.Entity.ProcessedEvent;
import com.dls.courierservice.Kafka.RestaurantAcceptedConsumer;
import com.dls.courierservice.Kafka.RestaurantAcceptedEvent;
import com.dls.courierservice.Repository.ProcessedEventRepository;
import com.dls.courierservice.Service.CourierAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.mockito.InOrder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantAcceptedConsumerTest {

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @Mock
    private CourierAssignmentService courierAssignmentService;

    private RestaurantAcceptedConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new RestaurantAcceptedConsumer(processedEventRepository, courierAssignmentService);
    }

    @Test
    void happyPath_marksProcessedBeforeDelegation() {
        String message = """
                {
                    "event_type": "RestaurantAccepted",
                    "event_id": "evt-100",
                    "order_id": "order-abc",
                    "customer_id": "cust-xyz",
                    "restaurant_id": "rest-001",
                    "estimated_prep_time": 15,
                    "timestamp": "2026-06-10T10:00:00Z"
                }
                """;

        when(processedEventRepository.existsById("evt-100")).thenReturn(false);

        consumer.consume(message);

        ArgumentCaptor<ProcessedEvent> processedCaptor = ArgumentCaptor.forClass(ProcessedEvent.class);
        verify(processedEventRepository).save(processedCaptor.capture());
        assertEquals("evt-100", processedCaptor.getValue().getEventId());

        ArgumentCaptor<RestaurantAcceptedEvent> eventCaptor = ArgumentCaptor.forClass(RestaurantAcceptedEvent.class);
        verify(courierAssignmentService).assignCourier(eventCaptor.capture());
        RestaurantAcceptedEvent captured = eventCaptor.getValue();
        assertEquals("order-abc", captured.getOrderId());
        assertEquals("cust-xyz", captured.getCustomerId());
        assertEquals("rest-001", captured.getRestaurantId());
        assertEquals(15, captured.getEstimatedPrepTime());

        InOrder inOrder = inOrder(processedEventRepository, courierAssignmentService);
        inOrder.verify(processedEventRepository).save(any(ProcessedEvent.class));
        inOrder.verify(courierAssignmentService).assignCourier(any(RestaurantAcceptedEvent.class));
    }

    @Test
    void duplicateEventId_skipsProcessing() {
        String message = """
                {
                    "event_type": "RestaurantAccepted",
                    "event_id": "evt-dup",
                    "order_id": "order-abc",
                    "customer_id": "cust-xyz",
                    "restaurant_id": "rest-001",
                    "estimated_prep_time": 15,
                    "timestamp": "2026-06-10T10:00:00Z"
                }
                """;

        when(processedEventRepository.existsById("evt-dup")).thenReturn(true);

        consumer.consume(message);

        verify(courierAssignmentService, never()).assignCourier(any());
        verify(processedEventRepository, never()).save(any());
    }

    @Test
    void nonRestaurantAcceptedEventType_ignored() {
        String message = """
                {
                    "event_type": "PaymentAuthorized",
                    "event_id": "evt-200",
                    "order_id": "order-abc",
                    "timestamp": "2026-06-10T10:00:00Z"
                }
                """;

        consumer.consume(message);

        verify(processedEventRepository, never()).existsById(anyString());
        verify(courierAssignmentService, never()).assignCourier(any());
        verify(processedEventRepository, never()).save(any());
    }

    @Test
    void missingEventId_skips() {
        String message = """
                {
                    "event_type": "RestaurantAccepted",
                    "order_id": "order-abc",
                    "timestamp": "2026-06-10T10:00:00Z"
                }
                """;

        consumer.consume(message);

        verify(courierAssignmentService, never()).assignCourier(any());
        verify(processedEventRepository, never()).save(any());
    }
}
