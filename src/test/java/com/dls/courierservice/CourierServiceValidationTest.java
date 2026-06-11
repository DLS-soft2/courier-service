package com.dls.courierservice;

import com.dls.courierservice.DTO.CourierRequest;
import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Service.CourierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CourierServiceValidationTest {

    @Mock
    private CourierRepository courierRepository;

    @InjectMocks
    private CourierService courierService;

    @Test
    void addCourier_acceptsDecimalRatings() {
        when(courierRepository.save(any(Courier.class))).thenAnswer(inv -> inv.getArgument(0));

        CourierRequest request = validRequest();
        request.setRating(4.5);
        assertDoesNotThrow(() -> courierService.addCourier(request));

        request.setRating(3.7);
        assertDoesNotThrow(() -> courierService.addCourier(request));

        request.setRating(0.0);
        assertDoesNotThrow(() -> courierService.addCourier(request));

        request.setRating(5.0);
        assertDoesNotThrow(() -> courierService.addCourier(request));
    }

    @Test
    void addCourier_rejectsRatingBelowZero() {
        CourierRequest request = validRequest();
        request.setRating(-0.1);
        assertThrows(IllegalArgumentException.class, () -> courierService.addCourier(request));
    }

    @Test
    void addCourier_rejectsRatingAboveFive() {
        CourierRequest request = validRequest();
        request.setRating(5.1);
        assertThrows(IllegalArgumentException.class, () -> courierService.addCourier(request));
    }

    @Test
    void addCourier_acceptsNullRating() {
        when(courierRepository.save(any(Courier.class))).thenAnswer(inv -> inv.getArgument(0));

        CourierRequest request = validRequest();
        request.setRating(null);
        assertDoesNotThrow(() -> courierService.addCourier(request));
    }

    @Test
    void addCourier_generatesUuidPrimaryKey() {
        when(courierRepository.save(any(Courier.class))).thenAnswer(inv -> inv.getArgument(0));

        CourierRequest request = validRequest();
        var response = courierService.addCourier(request);
        assertNotNull(response.getCourierId());
        assertEquals(36, response.getCourierId().length());
    }

    private CourierRequest validRequest() {
        CourierRequest request = new CourierRequest();
        request.setName("Test Courier");
        request.setPhoneNumber("1234567890");
        request.setEmail("test@example.com");
        request.setVehicleType(VehicleType.BIKE);
        request.setRating(4.0);
        request.setActive(true);
        return request;
    }
}
