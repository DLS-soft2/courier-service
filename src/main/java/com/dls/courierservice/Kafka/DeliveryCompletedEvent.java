package com.dls.courierservice.Kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class DeliveryCompletedEvent {

    @JsonProperty("event_id")
    private String eventId = UUID.randomUUID().toString();

    @JsonProperty("event_type")
    private String eventType = "DeliveryCompleted";

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("timestamp")
    private String timestamp = Instant.now().toString();

    public DeliveryCompletedEvent(String orderId, String customerId) {
        this.orderId = orderId;
        this.customerId = customerId;
    }
}
