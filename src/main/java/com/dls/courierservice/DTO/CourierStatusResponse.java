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
    private Long id;
    private String status;
    private String description;
    private Long courierId;
    private Long timestamp;
}
