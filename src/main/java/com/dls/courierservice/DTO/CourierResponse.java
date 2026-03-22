package com.dls.courierservice.DTO;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Enum.VehicleType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CourierResponse {
    private Long courierId;
    private String name;
    private String phoneNumber;
    private VehicleType vehicleType;
    private String email;
    private String rating;
    private Boolean active;


    public CourierResponse(Courier courier) {
        this.courierId = courier.getCourierId();
        this.name = courier.getName();
        this.phoneNumber = courier.getPhoneNumber();
        this.email = courier.getEmail();
        this.vehicleType = courier.getVehicleType();
        this.rating = courier.getRating();
        this.active = courier.getActive();
    }


}
