package com.dls.courierservice.DTO;

import com.dls.courierservice.Entity.Courier;
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
    private String email;


    public CourierResponse(Courier courier) {
        this.courierId = courier.getCourierId();
        this.name = courier.getName();
        this.phoneNumber = courier.getPhoneNumber();
        this.email = courier.getEmail();
    }


}
