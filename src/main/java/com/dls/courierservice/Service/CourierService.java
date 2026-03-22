package com.dls.courierservice.Service;

import com.dls.courierservice.DTO.CourierRequest;
import com.dls.courierservice.DTO.CourierResponse;
import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Repository.CourierRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourierService {

    private final CourierRepository courierRepository;

    public CourierService(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    public List<CourierResponse> getAllCouriers() {
        return courierRepository.findAll().stream().map(CourierResponse::new).collect(Collectors.toList());
    }

    public CourierResponse getCourierById(Long id) {
        return courierRepository.findById(id).map(CourierResponse::new).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );
    }

    public CourierResponse getCourierByName(String name) {
        return courierRepository.findByName(name)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with name: " + name));
    }

    public CourierResponse getCourierByPhoneNumber(String phoneNumber) {
        return courierRepository.findByPhoneNumber(phoneNumber)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with phone number: " + phoneNumber));
    }

    public CourierResponse getCourierByEmail(String email) {
        return courierRepository.findByEmail(email)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with email: " + email));
    }

    public CourierResponse addCourier(CourierRequest courierRequest) {
        validateCourierName(courierRequest.getName());
        validateCourierPhoneNumber(courierRequest.getPhoneNumber());
        validateCourierEmail(courierRequest.getEmail());
        validateCourierVehicleType(courierRequest.getVehicleType());
        validateCourierRating(courierRequest.getRating());
        validateCourierActive(courierRequest.getActive());

        Courier courier = new Courier();
        courier.setName(courierRequest.getName());
        courier.setPhoneNumber(courierRequest.getPhoneNumber());
        courier.setEmail(courierRequest.getEmail());
        courier.setVehicleType(courierRequest.getVehicleType());
        courier.setRating(courierRequest.getRating());
        courier.setActive(courierRequest.getActive());

        return new CourierResponse(courierRepository.save(courier));
    }

    public CourierResponse updateCourier(Long id, @Valid CourierRequest courierRequest) {
        Courier courier = courierRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );

        validateCourierName(courierRequest.getName());
        validateCourierPhoneNumber(courierRequest.getPhoneNumber());
        validateCourierEmail(courierRequest.getEmail());
        validateCourierVehicleType(courierRequest.getVehicleType());

        courier.setName(courierRequest.getName());
        courier.setPhoneNumber(courierRequest.getPhoneNumber());
        courier.setEmail(courierRequest.getEmail());
        courier.setVehicleType(courierRequest.getVehicleType());

        return new CourierResponse(courierRepository.save(courier));
    }

    public CourierResponse deleteCourier(Long id) {
        Courier courier = courierRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );
        courierRepository.delete(courier);
        return new CourierResponse(courier);
    }

    private void validateCourierName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier name cannot be null or empty");
        }
        if (name.length() < 2 || name.length() > 50) {
            throw new IllegalArgumentException("Courier name must be between 2 and 50 characters");
        }
        if (!name.matches("^[a-zA-Z ]+$")) {
            throw new IllegalArgumentException("Courier name can only contain letters and spaces");
        }
    }

    private void validateCourierPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier phone number cannot be null or empty");
        }
        if (!phoneNumber.matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Courier phone number must be between 10 and 15 digits and can start with +");
        }
    }

    private void validateCourierEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Courier email cannot be null or empty");
        }
        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Courier email must be a valid email address");
        }
        if (email.length() > 100) {
            throw new IllegalArgumentException("Courier email cannot exceed 100 characters");
        }
    }

    private void validateCourierVehicleType(VehicleType vehicleType) {
        if (vehicleType == null) {
            throw new IllegalArgumentException("Courier vehicle type cannot be null");
        }
    }

    private void validateCourierRating(String rating) {
        if (rating != null && !rating.matches("^[0-5](\\.0)?$")) {
            throw new IllegalArgumentException("Courier rating must be a number between 0 and 5, optionally with one decimal place");
        }

        if (rating != null && rating.length() > 3) {
            throw new IllegalArgumentException("Courier rating cannot exceed 3 characters");
        }
    }

    private void validateCourierActive(Boolean active) {
        if (active == null) {
            throw new IllegalArgumentException("Courier active status cannot be null");
        }
    }
}