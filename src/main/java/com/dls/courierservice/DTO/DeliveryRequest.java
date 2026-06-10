package com.dls.courierservice.DTO;

import com.dls.courierservice.Enum.DeliveryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryRequest {
    private String orderId;
    private String customerId;
    private Long courierId;
    private DeliveryStatus status;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private String notes;
}
