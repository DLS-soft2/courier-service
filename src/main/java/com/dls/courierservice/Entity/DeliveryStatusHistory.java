package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_status_history")
@Getter
@Setter
public class DeliveryStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private String location;

    private String notes;
}

