package com.dls.courierservice.Service;

import com.dls.courierservice.DTO.DeliveryRequest;
import com.dls.courierservice.DTO.DeliveryResponse;
import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Kafka.DeliveryCompletedEvent;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;
    private final CourierRepository courierRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String deliveriesTopic;

    public DeliveryService(DeliveryRepository deliveryRepository, CourierRepository courierRepository,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           @Value("${app.kafka.topic.deliveries}") String deliveriesTopic) {
        this.deliveryRepository = deliveryRepository;
        this.courierRepository = courierRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.deliveriesTopic = deliveriesTopic;
    }

    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAll()
                .stream()
                .map(DeliveryResponse::new)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesByCourierId(String courierId) {
        return deliveryRepository.findByCourier_CourierId(courierId)
                .stream()
                .map(DeliveryResponse::new)
                .collect(Collectors.toList());
    }

    public List<DeliveryResponse> getDeliveriesByStatus(String status) {
        DeliveryStatus deliveryStatus = parseStatus(status);
        return deliveryRepository.findByStatus(deliveryStatus)
                .stream()
                .map(DeliveryResponse::new)
                .collect(Collectors.toList());
    }

    public DeliveryResponse getDeliveryById(String id) {
        return deliveryRepository.findById(id)
                .map(DeliveryResponse::new)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
    }

    public DeliveryResponse addDelivery(DeliveryRequest deliveryRequest) {
        validateDeliveryRequest(deliveryRequest);

        Courier courier = courierRepository.findById(deliveryRequest.getCourierId())
                .orElseThrow(() -> new RuntimeException("Courier not found with id: " + deliveryRequest.getCourierId()));

        Delivery delivery = new Delivery();
        delivery.setDeliveryId(UUID.randomUUID().toString());
        delivery.setCourier(courier);
        delivery.setOrderId(deliveryRequest.getOrderId());
        delivery.setCustomerId(deliveryRequest.getCustomerId());
        delivery.setStatus(deliveryRequest.getStatus());
        delivery.setPickupAddress(deliveryRequest.getPickupAddress());
        delivery.setDeliveryAddress(deliveryRequest.getDeliveryAddress());
        delivery.setAssignedAt(deliveryRequest.getAssignedAt());
        delivery.setCompletedAt(deliveryRequest.getCompletedAt());
        delivery.setNotes(deliveryRequest.getNotes());

        return new DeliveryResponse(deliveryRepository.save(delivery));
    }

    public DeliveryResponse updateDelivery(String id, DeliveryRequest deliveryRequest) {
        validateDeliveryRequest(deliveryRequest);

        Delivery existingDelivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));

        Courier courier = courierRepository.findById(deliveryRequest.getCourierId())
                .orElseThrow(() -> new RuntimeException("Courier not found with id: " + deliveryRequest.getCourierId()));

        DeliveryStatus previousStatus = existingDelivery.getStatus();
        existingDelivery.setCourier(courier);
        existingDelivery.setOrderId(deliveryRequest.getOrderId());
        existingDelivery.setCustomerId(deliveryRequest.getCustomerId());
        existingDelivery.setStatus(deliveryRequest.getStatus());
        existingDelivery.setPickupAddress(deliveryRequest.getPickupAddress());
        existingDelivery.setDeliveryAddress(deliveryRequest.getDeliveryAddress());
        existingDelivery.setAssignedAt(deliveryRequest.getAssignedAt());
        existingDelivery.setCompletedAt(deliveryRequest.getCompletedAt());
        existingDelivery.setNotes(deliveryRequest.getNotes());

        Delivery saved = deliveryRepository.save(existingDelivery);

        if (deliveryRequest.getStatus() == DeliveryStatus.DELIVERED && previousStatus != DeliveryStatus.DELIVERED) {
            publishDeliveryCompleted(saved);
        }

        return new DeliveryResponse(saved);
    }

    public void completeDelivery(String orderId) {
        Delivery delivery = deliveryRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Delivery not found for order_id: " + orderId));
        if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
            log.info("Delivery already DELIVERED for order_id={}, skipping", orderId);
            return;
        }
        delivery.setStatus(DeliveryStatus.DELIVERED);
        delivery.setCompletedAt(LocalDateTime.now());
        deliveryRepository.save(delivery);
        publishDeliveryCompleted(delivery);
    }

    private void publishDeliveryCompleted(Delivery delivery) {
        DeliveryCompletedEvent event = new DeliveryCompletedEvent(
                delivery.getOrderId(), delivery.getCustomerId());
        kafkaTemplate.send(deliveriesTopic, delivery.getOrderId(), event);
        log.info("Published DeliveryCompleted for order_id={}", delivery.getOrderId());
    }

    public DeliveryResponse deleteDelivery(String id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found with id: " + id));
        deliveryRepository.delete(delivery);
        return new DeliveryResponse(delivery);
    }

    private void validateDeliveryRequest(DeliveryRequest request) {
        if (request.getOrderId() == null || request.getOrderId().isBlank()) {
            throw new IllegalArgumentException("Order ID cannot be null or blank");
        }
        if (request.getCourierId() == null || request.getCourierId().isBlank()) {
            throw new IllegalArgumentException("Courier ID cannot be null or blank");
        }
        if (request.getStatus() == null) {
            throw new IllegalArgumentException("Delivery status cannot be null");
        }
        if (request.getPickupAddress() == null || request.getPickupAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Pickup address cannot be null or empty");
        }
        if (request.getPickupAddress().length() < 5 || request.getPickupAddress().length() > 100) {
            throw new IllegalArgumentException("Pickup address must be between 5 and 100 characters");
        }
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery address cannot be null or empty");
        }
        if (request.getDeliveryAddress().length() < 5 || request.getDeliveryAddress().length() > 100) {
            throw new IllegalArgumentException("Delivery address must be between 5 and 100 characters");
        }
        if (request.getNotes() != null && request.getNotes().length() > 500) {
            throw new IllegalArgumentException("Notes cannot exceed 500 characters");
        }
    }

    private DeliveryStatus parseStatus(String status) {
        try {
            return DeliveryStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid delivery status: " + status);
        }
    }
}