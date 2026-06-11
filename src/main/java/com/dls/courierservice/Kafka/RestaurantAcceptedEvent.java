package com.dls.courierservice.Kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
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

    @JsonProperty("delivery_address")
    private String deliveryAddress;

    @JsonProperty("restaurant_address")
    private String restaurantAddress;

    @JsonProperty("timestamp")
    private String timestamp;
}
