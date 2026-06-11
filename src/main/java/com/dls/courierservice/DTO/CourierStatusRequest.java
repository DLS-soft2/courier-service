package com.dls.courierservice.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourierStatusRequest {
    private String status;
    private String description;
    private String courierId;
    private Double latitude;
    private Double longitude;
}
