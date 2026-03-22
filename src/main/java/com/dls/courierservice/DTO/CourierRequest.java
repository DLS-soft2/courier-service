package com.dls.courierservice.DTO;

import com.dls.courierservice.Enum.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourierRequest {
    private String name;
    private String phoneNumber;
    private String email;
    private VehicleType vehicleType;
    private String rating;
    private Boolean active;
}
