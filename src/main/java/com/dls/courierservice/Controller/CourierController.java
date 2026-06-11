package com.dls.courierservice.Controller;

import com.dls.authlib.Permission;
import com.dls.authlib.RequirePermission;
import com.dls.courierservice.DTO.CourierRequest;
import com.dls.courierservice.DTO.CourierResponse;
import com.dls.courierservice.Service.CourierService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/couriers")
public class CourierController {

    private final CourierService courierService;

    public CourierController(CourierService courierService) {
        this.courierService = courierService;
    }

    @GetMapping
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<List<CourierResponse>> getAllCouriers() {
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    @GetMapping("/me")
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<CourierResponse> getCurrentCourier(HttpServletRequest request) {
        String keycloakId = request.getHeader("X-User-Id");
        return ResponseEntity.ok(courierService.getCourierByKeycloakId(keycloakId));
    }

    @GetMapping("/{id}")
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<CourierResponse> getCourierById(@PathVariable String id) {
        return ResponseEntity.ok(courierService.getCourierById(id));
    }

    @GetMapping("/name/{name}")
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<CourierResponse> getCourierByName(@PathVariable String name) {
        return ResponseEntity.ok(courierService.getCourierByName(name));
    }

    @GetMapping("/phone/{phoneNumber}")
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<CourierResponse> getCourierByPhoneNumber(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(courierService.getCourierByPhoneNumber(phoneNumber));
    }

    @GetMapping("/email/{email}")
    @RequirePermission(Permission.COURIERS_READ)
    public ResponseEntity<CourierResponse> getCourierByEmail(@PathVariable String email) {
        return ResponseEntity.ok(courierService.getCourierByEmail(email));
    }

    @PostMapping
    @RequirePermission(Permission.COURIERS_UPDATE)
    public ResponseEntity<CourierResponse> addCourier(@RequestBody CourierRequest courierRequest, HttpServletRequest request) {
        String keycloakId = request.getHeader("X-User-Id");
        return ResponseEntity.status(HttpStatus.CREATED).body(courierService.addCourier(courierRequest, keycloakId));
    }

    @PutMapping("/{id}")
    @RequirePermission(Permission.COURIERS_UPDATE)
    public ResponseEntity<CourierResponse> updateCourier(@PathVariable String id, @RequestBody CourierRequest courierRequest) {
        return ResponseEntity.ok(courierService.updateCourier(id, courierRequest));
    }

    @DeleteMapping("/{id}")
    @RequirePermission(Permission.COURIERS_UPDATE)
    public ResponseEntity<CourierResponse> deleteCourier(@PathVariable String id) {
        return ResponseEntity.ok(courierService.deleteCourier(id));
    }
}
