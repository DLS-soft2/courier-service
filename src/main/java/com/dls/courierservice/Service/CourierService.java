package com.dls.courierservice.Service;

import com.dls.courierservice.DTO.CourierRequest;
import com.dls.courierservice.DTO.CourierResponse;
import com.dls.courierservice.Entity.Courier;
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


    public List<CourierResponse> getAllCouriers(){
        return courierRepository.findAll().stream().map(CourierResponse::new).collect(Collectors.toList());
    }

    public CourierResponse getCourierById(Long id){
        return courierRepository.findById(id).map(CourierResponse::new).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );
    }

    public CourierResponse getCourierByName(String name){
        return courierRepository.findByName(name)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with name: " + name));
    }

    public CourierResponse getCourierByPhoneNumber(String phoneNumber){
        return courierRepository.findByPhoneNumber(phoneNumber)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with phone number: " + phoneNumber));
    }

    public CourierResponse getCourierByEmail(String email){
        return courierRepository.findByEmail(email)
                .map(CourierResponse::new)
                .orElseThrow(() -> new RuntimeException("Courier not found with email: " + email));
    }

    public CourierResponse addCourier(CourierRequest courierRequest){

        validateCourierName(courierRequest.getName());
        validateCourierPhoneNumber(courierRequest.getPhoneNumber());
        validateCourierEmail(courierRequest.getEmail());

        Courier courier = new Courier();

        courier.setName(courierRequest.getName());
        courier.setPhoneNumber(courierRequest.getPhoneNumber());
        courier.setEmail(courierRequest.getEmail());


        Courier savedCourier = courierRepository.save(courier);
        return new CourierResponse(savedCourier);
    }

    public CourierResponse updateCourier(Long id, @Valid CourierRequest courierRequest){
        Courier courier = courierRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );

        courier.setName(courierRequest.getName());
        courier.setPhoneNumber(courierRequest.getPhoneNumber());
        courier.setEmail(courierRequest.getEmail());

        Courier updatedCourier = courierRepository.save(courier);
        return new CourierResponse(updatedCourier);
    }

    public CourierResponse deleteCourier(Long id){
        Courier courier = courierRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Courier not found with id: " + id)
        );
        courierRepository.delete(courier);
        return new CourierResponse(courier);
    }


    // Validation methods

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

        if (phoneNumber.length() > 15) {
            throw new IllegalArgumentException("Courier phone number cannot exceed 15 characters");
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

}
