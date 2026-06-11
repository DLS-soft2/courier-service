package com.dls.courierservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourierStatusResponse {
    private String id;
    private String status;
    private String description;
    private String courierId;
    private Long timestamp;
    private Double latitude;
    private Double longitude;
}
