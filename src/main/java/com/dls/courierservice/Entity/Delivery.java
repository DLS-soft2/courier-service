package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery", uniqueConstraints = {
        @UniqueConstraint(columnNames = "order_id"),
})
@Getter
@Setter
public class Delivery {
    @Id
    @Column(name = "delivery_id")
    private String deliveryId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "customer_id")
    private String customerId;

    @ManyToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "pickup_address")
    private String pickupAddress;

    @Column(name = "delivery_address")
    private String deliveryAddress;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private String notes;
}
