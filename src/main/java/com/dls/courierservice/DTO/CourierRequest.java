package com.dls.courierservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourierRequest {
    private String name;
    private String phoneNumber;
    private String email;
}
