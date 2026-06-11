package com.dls.courierservice.Service;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.CourierStatus;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.AvailabilityStatus;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Kafka.CourierAssignedEvent;
import com.dls.courierservice.Kafka.RestaurantAcceptedEvent;
import com.dls.courierservice.Repository.CourierStatusRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import com.dls.courierservice.Service.AiServiceClient.AssignmentRequest;
import com.dls.courierservice.Service.AiServiceClient.CourierCandidate;
import com.dls.courierservice.Service.AiServiceClient.CourierRanking;
import com.dls.courierservice.Service.AiServiceClient.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourierAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(CourierAssignmentService.class);
    private static final List<DeliveryStatus> TERMINAL_STATUSES = List.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED);

    // Default pickup location (Copenhagen central) — used because RestaurantAccepted
    // carries restaurant_id but no coordinates, and there is no REST lookup to
    // restaurant-service wired yet. Documented design decision.
    private static final double DEFAULT_RESTAURANT_LAT = 55.6761;
    private static final double DEFAULT_RESTAURANT_LNG = 12.5683;

    // Default delivery location (Copenhagen Nørrebro) — RestaurantAccepted event
    // carries no delivery address. Same pragmatic approach.
    private static final double DEFAULT_DELIVERY_LAT = 55.6867;
    private static final double DEFAULT_DELIVERY_LNG = 12.5501;

    private final CourierStatusRepository courierStatusRepository;
    private final DeliveryRepository deliveryRepository;
    private final AiServiceClient aiServiceClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String couriersTopic;

    public CourierAssignmentService(
            CourierStatusRepository courierStatusRepository,
            DeliveryRepository deliveryRepository,
            AiServiceClient aiServiceClient,
            KafkaTemplate<String, Object> kafkaTemplate,
            @Value("${app.kafka.topic.couriers}") String couriersTopic) {
        this.courierStatusRepository = courierStatusRepository;
        this.deliveryRepository = deliveryRepository;
        this.aiServiceClient = aiServiceClient;
        this.kafkaTemplate = kafkaTemplate;
        this.couriersTopic = couriersTopic;
    }

    @Transactional
    public void assignCourier(RestaurantAcceptedEvent event) {
        List<CourierStatus> availableStatuses = courierStatusRepository.findByStatus(AvailabilityStatus.AVAILABLE);
        List<CourierStatus> candidates = availableStatuses.stream()
                .filter(cs -> Boolean.TRUE.equals(cs.getCourier().getActive()))
                .toList();

        if (candidates.isEmpty()) {
            log.warn("No available couriers for order_id={}", event.getOrderId());
            return;
        }

        AssignmentRequest request = buildAssignmentRequest(event, candidates);
        List<CourierRanking> rankings = aiServiceClient.scoreAssignment(request);

        if (rankings.isEmpty()) {
            log.warn("No rankings returned for order_id={}", event.getOrderId());
            return;
        }

        String topCourierId = rankings.getFirst().getCourierId();
        Courier selectedCourier = candidates.stream()
                .map(CourierStatus::getCourier)
                .filter(c -> topCourierId.equals(c.getCourierId()))
                .findFirst()
                .orElse(candidates.getFirst().getCourier());

        Delivery delivery = new Delivery();
        delivery.setDeliveryId(UUID.randomUUID().toString());
        delivery.setOrderId(event.getOrderId());
        delivery.setCustomerId(event.getCustomerId());
        delivery.setCourier(selectedCourier);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setAssignedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);

        CourierAssignedEvent assignedEvent = new CourierAssignedEvent(
                event.getOrderId(), event.getCustomerId(), selectedCourier.getCourierId());
        kafkaTemplate.send(couriersTopic, event.getOrderId(), assignedEvent);
        log.info("Published CourierAssigned for order_id={}, courier_id={}",
                event.getOrderId(), selectedCourier.getCourierId());
    }

    private AssignmentRequest buildAssignmentRequest(RestaurantAcceptedEvent event, List<CourierStatus> candidates) {
        AssignmentRequest request = new AssignmentRequest();
        request.setOrderId(UUID.fromString(event.getOrderId()));
        request.setRestaurantLocation(new Location(DEFAULT_RESTAURANT_LAT, DEFAULT_RESTAURANT_LNG));
        request.setDeliveryLocation(new Location(DEFAULT_DELIVERY_LAT, DEFAULT_DELIVERY_LNG));
        // order_total and items_count are not in RestaurantAccepted event — explicit neutral values
        request.setOrderTotal(0.0);
        request.setItemsCount(1);
        request.setCouriers(candidates.stream().map(this::toCourierCandidate).toList());
        return request;
    }

    private CourierCandidate toCourierCandidate(CourierStatus cs) {
        Courier courier = cs.getCourier();
        CourierCandidate candidate = new CourierCandidate();
        candidate.setCourierId(courier.getCourierId());
        candidate.setCurrentLocation(new Location(
                cs.getLatitude() != null ? cs.getLatitude() : 0.0,
                cs.getLongitude() != null ? cs.getLongitude() : 0.0));
        candidate.setVehicleType(courier.getVehicleType().name().toLowerCase());
        candidate.setRating(courier.getRating() != null ? courier.getRating() : 0.0);
        candidate.setActiveDeliveries(
                deliveryRepository.countByCourierAndStatusNotIn(courier, TERMINAL_STATUSES));
        return candidate;
    }
}
