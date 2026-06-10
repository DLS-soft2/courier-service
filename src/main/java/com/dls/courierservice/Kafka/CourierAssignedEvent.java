package com.dls.courierservice.Kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class CourierAssignedEvent {

    @JsonProperty("event_id")
    private String eventId = UUID.randomUUID().toString();

    @JsonProperty("event_type")
    private String eventType = "CourierAssigned";

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("courier_id")
    private String courierId;

    @JsonProperty("timestamp")
    private String timestamp = Instant.now().toString();

    public CourierAssignedEvent(String orderId, String customerId, String courierId) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.courierId = courierId;
    }
}
