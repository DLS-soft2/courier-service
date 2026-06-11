package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.AvailabilityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "courier_status")
public class CourierStatus {
    @Id
    @Column(name = "id")
    private String id;

    @OneToOne
    @JoinColumn(name = "courier_id", nullable = false, unique = true)
    private Courier courier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AvailabilityStatus status;

    private Double latitude;

    private Double longitude;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;
}
