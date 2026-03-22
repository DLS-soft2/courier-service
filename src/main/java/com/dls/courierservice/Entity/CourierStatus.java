package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "courier_status")
public class CourierStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courier_status_id")
    private Long courierStatusId;

    @Column(name = "courier_id", nullable = false)
    private Long courierId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;
}

