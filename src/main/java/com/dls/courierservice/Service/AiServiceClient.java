package com.dls.courierservice.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class AiServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AiServiceClient.class);

    private final RestClient restClient;

    public AiServiceClient(@Value("${app.ai-service.url}") String aiServiceUrl) {
        // HTTP/1.1 only: the JDK client's default h2c upgrade makes uvicorn drop the request body
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(Duration.ofSeconds(120));
        this.restClient = RestClient.builder()
                .baseUrl(aiServiceUrl)
                .requestFactory(factory)
                .build();
    }

    public List<CourierRanking> scoreAssignment(AssignmentRequest request) {
        try {
            AssignmentResponse response = restClient.post()
                    .uri("/api/v1/assignments/score")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AssignmentResponse.class);
            if (response != null && response.getRankings() != null && !response.getRankings().isEmpty()) {
                return response.getRankings();
            }
        } catch (Exception e) {
            log.warn("AI service unavailable, using fallback ranking: {}", e.getMessage());
        }
        return fallbackRanking(request.getCouriers());
    }

    private List<CourierRanking> fallbackRanking(List<CourierCandidate> couriers) {
        return couriers.stream()
                .sorted(Comparator.comparingDouble(CourierCandidate::getRating).reversed())
                .map(c -> {
                    CourierRanking r = new CourierRanking();
                    r.setCourierId(c.getCourierId());
                    r.setScore(c.getRating());
                    r.setEstimatedDeliveryMinutes(30);
                    r.setReasoning("Fallback: ranked by rating");
                    return r;
                })
                .toList();
    }

    @Getter
    @Setter
    public static class Location {
        private double lat;
        private double lng;

        public Location() {}

        public Location(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    @Getter
    @Setter
    public static class CourierCandidate {
        @JsonProperty("courier_id")
        private String courierId;
        @JsonProperty("current_location")
        private Location currentLocation;
        @JsonProperty("vehicle_type")
        private String vehicleType;
        private double rating;
        @JsonProperty("active_deliveries")
        private int activeDeliveries;
    }

    @Getter
    @Setter
    public static class AssignmentRequest {
        @JsonProperty("order_id")
        private UUID orderId;
        @JsonProperty("restaurant_location")
        private Location restaurantLocation;
        @JsonProperty("delivery_location")
        private Location deliveryLocation;
        @JsonProperty("order_total")
        private double orderTotal;
        @JsonProperty("items_count")
        private int itemsCount;
        private List<CourierCandidate> couriers;
    }

    @Getter
    @Setter
    public static class CourierRanking {
        @JsonProperty("courier_id")
        private String courierId;
        private double score;
        @JsonProperty("estimated_delivery_minutes")
        private int estimatedDeliveryMinutes;
        private String reasoning;
    }

    @Getter
    @Setter
    public static class AssignmentResponse {
        @JsonProperty("order_id")
        private String orderId;
        private List<CourierRanking> rankings;
    }
}
