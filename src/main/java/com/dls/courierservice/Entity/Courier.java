package com.dls.courierservice.Entity;

import com.dls.courierservice.Enum.VehicleType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "courier", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email", name = "uk_courier_email"),
        @UniqueConstraint(columnNames = "phone_number", name = "uk_courier_phone_number")
})
public class Courier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "courier_id")
    private Long courierId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    private String rating;

    private Boolean active;

}
