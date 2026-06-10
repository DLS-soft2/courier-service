package com.dls.courierservice;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.CourierStatus;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.AvailabilityStatus;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Kafka.CourierAssignedEvent;
import com.dls.courierservice.Kafka.RestaurantAcceptedEvent;
import com.dls.courierservice.Repository.CourierStatusRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import com.dls.courierservice.Service.AiServiceClient;
import com.dls.courierservice.Service.AiServiceClient.AssignmentRequest;
import com.dls.courierservice.Service.AiServiceClient.CourierRanking;
import com.dls.courierservice.Service.CourierAssignmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourierAssignmentServiceTest {

    @Mock
    private CourierStatusRepository courierStatusRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private AiServiceClient aiServiceClient;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private CourierAssignmentService service;

    private Courier courier1;
    private Courier courier2;
    private CourierStatus status1;
    private CourierStatus status2;

    @BeforeEach
    void setUp() {
        service = new CourierAssignmentService(
                courierStatusRepository, deliveryRepository, aiServiceClient, kafkaTemplate, "couriers");

        courier1 = new Courier();
        courier1.setCourierId(1L);
        courier1.setExternalUuid("uuid-courier-1");
        courier1.setRating(4.5);
        courier1.setActive(true);
        courier1.setVehicleType(VehicleType.BIKE);

        courier2 = new Courier();
        courier2.setCourierId(2L);
        courier2.setExternalUuid("uuid-courier-2");
        courier2.setRating(3.0);
        courier2.setActive(true);
        courier2.setVehicleType(VehicleType.CAR);

        status1 = new CourierStatus();
        status1.setCourier(courier1);
        status1.setStatus(AvailabilityStatus.AVAILABLE);
        status1.setLatitude(55.6761);
        status1.setLongitude(12.5683);

        status2 = new CourierStatus();
        status2.setCourier(courier2);
        status2.setStatus(AvailabilityStatus.AVAILABLE);
        status2.setLatitude(55.6867);
        status2.setLongitude(12.5701);
    }

    @Test
    void assignCourier_callsAiServiceAndPicksTopRanked() {
        when(courierStatusRepository.findByStatus(AvailabilityStatus.AVAILABLE))
                .thenReturn(List.of(status1, status2));
        when(deliveryRepository.countByCourierAndStatusNotIn(any(), anyList())).thenReturn(0);

        CourierRanking rank1 = new CourierRanking();
        rank1.setCourierId("uuid-courier-2");
        rank1.setScore(9.5);
        rank1.setEstimatedDeliveryMinutes(20);
        rank1.setReasoning("Closer to restaurant");

        CourierRanking rank2 = new CourierRanking();
        rank2.setCourierId("uuid-courier-1");
        rank2.setScore(7.0);
        rank2.setEstimatedDeliveryMinutes(25);
        rank2.setReasoning("Farther away");

        when(aiServiceClient.scoreAssignment(any(AssignmentRequest.class)))
                .thenReturn(List.of(rank1, rank2));

        RestaurantAcceptedEvent event = new RestaurantAcceptedEvent();
        event.setOrderId("550e8400-e29b-41d4-a716-446655440011");
        event.setCustomerId("cust-222");
        event.setRestaurantId("rest-333");

        service.assignCourier(event);

        ArgumentCaptor<Delivery> deliveryCaptor = ArgumentCaptor.forClass(Delivery.class);
        verify(deliveryRepository).save(deliveryCaptor.capture());
        Delivery saved = deliveryCaptor.getValue();
        assertEquals("550e8400-e29b-41d4-a716-446655440011", saved.getOrderId());
        assertEquals("cust-222", saved.getCustomerId());
        assertEquals(DeliveryStatus.ASSIGNED, saved.getStatus());
        assertEquals("uuid-courier-2", saved.getCourier().getExternalUuid());

        ArgumentCaptor<CourierAssignedEvent> eventCaptor = ArgumentCaptor.forClass(CourierAssignedEvent.class);
        verify(kafkaTemplate).send(eq("couriers"), eq("550e8400-e29b-41d4-a716-446655440011"), eventCaptor.capture());
        CourierAssignedEvent published = eventCaptor.getValue();
        assertEquals("CourierAssigned", published.getEventType());
        assertEquals("550e8400-e29b-41d4-a716-446655440011", published.getOrderId());
        assertEquals("cust-222", published.getCustomerId());
        assertEquals("uuid-courier-2", published.getCourierId());
        assertNotNull(published.getEventId());
        assertNotNull(published.getTimestamp());
    }

    @Test
    void assignCourier_fallbackRanking_picksHighestRated() {
        when(courierStatusRepository.findByStatus(AvailabilityStatus.AVAILABLE))
                .thenReturn(List.of(status1, status2));
        when(deliveryRepository.countByCourierAndStatusNotIn(any(), anyList())).thenReturn(0);

        // Simulate fallback result: couriers ranked by rating descending
        // courier1 has rating 4.5, courier2 has 3.0 — courier1 should be picked
        CourierRanking fallback1 = new CourierRanking();
        fallback1.setCourierId("uuid-courier-1");
        fallback1.setScore(4.5);
        fallback1.setEstimatedDeliveryMinutes(30);
        fallback1.setReasoning("Fallback: ranked by rating");

        CourierRanking fallback2 = new CourierRanking();
        fallback2.setCourierId("uuid-courier-2");
        fallback2.setScore(3.0);
        fallback2.setEstimatedDeliveryMinutes(30);
        fallback2.setReasoning("Fallback: ranked by rating");

        when(aiServiceClient.scoreAssignment(any(AssignmentRequest.class)))
                .thenReturn(List.of(fallback1, fallback2));

        RestaurantAcceptedEvent event = new RestaurantAcceptedEvent();
        event.setOrderId("550e8400-e29b-41d4-a716-446655440044");
        event.setCustomerId("cust-555");

        service.assignCourier(event);

        ArgumentCaptor<CourierAssignedEvent> eventCaptor = ArgumentCaptor.forClass(CourierAssignedEvent.class);
        verify(kafkaTemplate).send(eq("couriers"), eq("550e8400-e29b-41d4-a716-446655440044"), eventCaptor.capture());
        assertEquals("uuid-courier-1", eventCaptor.getValue().getCourierId());
    }

    @Test
    void assignCourier_usesExternalUuidNotNumericId() {
        when(courierStatusRepository.findByStatus(AvailabilityStatus.AVAILABLE))
                .thenReturn(List.of(status1));
        when(deliveryRepository.countByCourierAndStatusNotIn(any(), anyList())).thenReturn(0);

        CourierRanking ranking = new CourierRanking();
        ranking.setCourierId("uuid-courier-1");
        ranking.setScore(8.0);
        ranking.setEstimatedDeliveryMinutes(15);
        ranking.setReasoning("Only candidate");
        when(aiServiceClient.scoreAssignment(any())).thenReturn(List.of(ranking));

        RestaurantAcceptedEvent event = new RestaurantAcceptedEvent();
        event.setOrderId("550e8400-e29b-41d4-a716-446655440077");
        event.setCustomerId("cust-888");

        service.assignCourier(event);

        ArgumentCaptor<CourierAssignedEvent> eventCaptor = ArgumentCaptor.forClass(CourierAssignedEvent.class);
        verify(kafkaTemplate).send(eq("couriers"), anyString(), eventCaptor.capture());
        String publishedCourierId = eventCaptor.getValue().getCourierId();
        assertEquals("uuid-courier-1", publishedCourierId);
        assertNotEquals(String.valueOf(courier1.getCourierId()), publishedCourierId);
    }

    @Test
    void aiServiceClient_fallbackReturnsRankingsByRatingDescending() {
        // Directly test AiServiceClient with unreachable URL to verify fallback
        AiServiceClient realClient = new AiServiceClient("http://localhost:1");

        AiServiceClient.CourierCandidate c1 = new AiServiceClient.CourierCandidate();
        c1.setCourierId("low-rated");
        c1.setRating(2.0);
        c1.setCurrentLocation(new AiServiceClient.Location(0, 0));
        c1.setVehicleType("bike");
        c1.setActiveDeliveries(0);

        AiServiceClient.CourierCandidate c2 = new AiServiceClient.CourierCandidate();
        c2.setCourierId("high-rated");
        c2.setRating(4.8);
        c2.setCurrentLocation(new AiServiceClient.Location(0, 0));
        c2.setVehicleType("car");
        c2.setActiveDeliveries(0);

        AiServiceClient.AssignmentRequest request = new AiServiceClient.AssignmentRequest();
        request.setOrderId(UUID.fromString("550e8400-e29b-41d4-a716-446655440099"));
        request.setRestaurantLocation(new AiServiceClient.Location(55.0, 12.0));
        request.setDeliveryLocation(new AiServiceClient.Location(55.1, 12.1));
        request.setOrderTotal(100.0);
        request.setItemsCount(3);
        request.setCouriers(List.of(c1, c2));

        List<CourierRanking> rankings = realClient.scoreAssignment(request);

        assertFalse(rankings.isEmpty());
        assertEquals("high-rated", rankings.getFirst().getCourierId());
        assertEquals("low-rated", rankings.get(1).getCourierId());
    }
}
