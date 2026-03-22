package com.dls.courierservice.DTO;

import com.dls.courierservice.Entity.Delivery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryResponse {

    private long deliveryId;
    private long courierId;
    private long orderId;
    private String status;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime assignedAt;
    private LocalDateTime completedAt;
    private String notes;

    public DeliveryResponse(Delivery delivery) {
        this.deliveryId = delivery.getDeliveryId();
        this.courierId = delivery.getCourier().getCourierId();
        this.orderId = delivery.getOrderId();
        this.status = delivery.getStatus().name();
        this.pickupAddress = delivery.getPickupAddress();
        this.deliveryAddress = delivery.getDeliveryAddress();
        this.assignedAt = delivery.getAssignedAt();
        this.completedAt = delivery.getCompletedAt();
        this.notes = delivery.getNotes();
    }
}
