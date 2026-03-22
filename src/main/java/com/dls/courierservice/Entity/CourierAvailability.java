package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.AvailabilityStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "courier_availability")
public class CourierAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "courier_id")
    private Courier courier;

    @Enumerated(EnumType.STRING)
    private AvailabilityStatus status;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    private String currentLocation;
}
