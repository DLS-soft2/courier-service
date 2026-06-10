package com.dls.courierservice.Kafka;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantAcceptedEvent {

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("customer_id")
    private String customerId;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("estimated_prep_time")
    private Integer estimatedPrepTime;

    @JsonProperty("timestamp")
    private String timestamp;
}
