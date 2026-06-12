package com.dls.courierservice.Kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CourierAssignmentFailedEvent {

    @JsonProperty("event_id")
    private String eventId = UUID.randomUUID().toString();

    @JsonProperty("event_type")
    private String eventType = "CourierAssignmentFailed";

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("timestamp")
    private String timestamp = Instant.now().toString();

    public CourierAssignmentFailedEvent(String orderId, String customerId, String restaurantId, String reason) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.reason = reason;
    }
}
