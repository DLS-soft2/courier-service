package com.dls.courierservice.Controller;

import com.dls.courierservice.DTO.DeliveryRequest;
import com.dls.courierservice.DTO.DeliveryResponse;
import com.dls.courierservice.Service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/courier/{courierId}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCourierId(@PathVariable Long courierId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<DeliveryResponse> addDelivery(@RequestBody DeliveryRequest deliveryRequest) {
        return ResponseEntity.ok(deliveryService.addDelivery(deliveryRequest));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<DeliveryResponse> updateDelivery(@PathVariable Long id, @RequestBody DeliveryRequest deliveryRequest) {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, deliveryRequest));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}