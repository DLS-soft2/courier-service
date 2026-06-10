package com.dls.courierservice.Kafka;

import com.dls.courierservice.Entity.ProcessedEvent;
import com.dls.courierservice.Repository.ProcessedEventRepository;
import com.dls.courierservice.Service.CourierAssignmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class RestaurantAcceptedConsumer {

    private static final Logger log = LoggerFactory.getLogger(RestaurantAcceptedConsumer.class);

    private final ProcessedEventRepository processedEventRepository;
    private final CourierAssignmentService courierAssignmentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RestaurantAcceptedConsumer(
            ProcessedEventRepository processedEventRepository,
            CourierAssignmentService courierAssignmentService) {
        this.processedEventRepository = processedEventRepository;
        this.courierAssignmentService = courierAssignmentService;
    }

    @KafkaListener(topics = "${app.kafka.topic.restaurants}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(String message) {
        JsonNode node;
        try {
            node = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse Kafka message: {}", e.getMessage());
            return;
        }

        String eventType = node.has("event_type") ? node.get("event_type").asText() : null;
        if (!"RestaurantAccepted".equals(eventType)) {
            log.info("Ignoring event with type: {}", eventType);
            return;
        }

        String eventId = node.has("event_id") ? node.get("event_id").asText() : null;
        if (eventId == null) {
            log.warn("RestaurantAccepted event missing event_id, skipping");
            return;
        }

        if (processedEventRepository.existsById(eventId)) {
            log.info("Duplicate event_id={}, skipping", eventId);
            return;
        }

        RestaurantAcceptedEvent event;
        try {
            event = objectMapper.treeToValue(node, RestaurantAcceptedEvent.class);
        } catch (JsonProcessingException e) {
            log.warn("Failed to deserialize RestaurantAcceptedEvent: {}", e.getMessage());
            return;
        }

        processedEventRepository.save(new ProcessedEvent(eventId, Instant.now()));

        courierAssignmentService.assignCourier(event);
        log.info("Processed RestaurantAccepted event_id={} for order_id={}", eventId, event.getOrderId());
    }
}
