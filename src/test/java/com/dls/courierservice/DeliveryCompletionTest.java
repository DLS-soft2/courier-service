package com.dls.courierservice;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Kafka.DeliveryCompletedEvent;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import com.dls.courierservice.Service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryCompletionTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService(deliveryRepository, courierRepository, kafkaTemplate, "deliveries");
    }

    @Test
    void completeDelivery_setsStatusAndPublishesEvent() {
        Courier courier = new Courier();
        courier.setCourierId("uuid-c1");
        courier.setVehicleType(VehicleType.BIKE);

        Delivery delivery = new Delivery();
        delivery.setDeliveryId("del-uuid-10");
        delivery.setOrderId("order-999");
        delivery.setCustomerId("cust-888");
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.IN_TRANSIT);

        when(deliveryRepository.findByOrderId("order-999")).thenReturn(Optional.of(delivery));

        deliveryService.completeDelivery("order-999");

        assertEquals(DeliveryStatus.DELIVERED, delivery.getStatus());
        assertNotNull(delivery.getCompletedAt());
        verify(deliveryRepository).save(delivery);

        ArgumentCaptor<DeliveryCompletedEvent> captor = ArgumentCaptor.forClass(DeliveryCompletedEvent.class);
        verify(kafkaTemplate).send(eq("deliveries"), eq("order-999"), captor.capture());

        DeliveryCompletedEvent event = captor.getValue();
        assertEquals("DeliveryCompleted", event.getEventType());
        assertEquals("order-999", event.getOrderId());
        assertEquals("cust-888", event.getCustomerId());
        assertNotNull(event.getEventId());
        assertNotNull(event.getTimestamp());
    }

    @Test
    void completeDelivery_throwsWhenOrderNotFound() {
        when(deliveryRepository.findByOrderId("nonexistent")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> deliveryService.completeDelivery("nonexistent"));
    }

    @Test
    void completeDelivery_alreadyDelivered_doesNotPublishAgain() {
        Courier courier = new Courier();
        courier.setCourierId("uuid-c1");
        courier.setVehicleType(VehicleType.BIKE);

        Delivery delivery = new Delivery();
        delivery.setDeliveryId("del-uuid-10");
        delivery.setOrderId("order-999");
        delivery.setCustomerId("cust-888");
        delivery.setCourier(courier);
        delivery.setStatus(DeliveryStatus.DELIVERED);

        when(deliveryRepository.findByOrderId("order-999")).thenReturn(Optional.of(delivery));

        deliveryService.completeDelivery("order-999");

        verify(deliveryRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(anyString(), anyString(), any());
    }

    @Test
    void completedEventFields_allNonNull() {
        DeliveryCompletedEvent event = new DeliveryCompletedEvent("order-1", "cust-1");
        assertNotNull(event.getEventId());
        assertNotNull(event.getEventType());
        assertNotNull(event.getOrderId());
        assertNotNull(event.getCustomerId());
        assertNotNull(event.getTimestamp());
        assertEquals("DeliveryCompleted", event.getEventType());
        assertEquals(36, event.getEventId().length());
    }
}
