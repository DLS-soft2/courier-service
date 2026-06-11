package com.dls.courierservice.Controller;

import com.dls.authlib.Permission;
import com.dls.authlib.RequirePermission;
import com.dls.courierservice.DTO.DeliveryRequest;
import com.dls.courierservice.DTO.DeliveryResponse;
import com.dls.courierservice.Service.DeliveryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping
    @RequirePermission(Permission.DELIVERIES_READ)
    public ResponseEntity<List<DeliveryResponse>> getAllDeliveries() {
        return ResponseEntity.ok(deliveryService.getAllDeliveries());
    }

    @GetMapping("/courier/{courierId}")
    @RequirePermission(Permission.DELIVERIES_READ)
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByCourierId(@PathVariable Long courierId) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByCourierId(courierId));
    }

    @GetMapping("/status/{status}")
    @RequirePermission(Permission.DELIVERIES_READ)
    public ResponseEntity<List<DeliveryResponse>> getDeliveriesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(deliveryService.getDeliveriesByStatus(status));
    }

    @GetMapping("/{id}")
    @RequirePermission(Permission.DELIVERIES_READ)
    public ResponseEntity<DeliveryResponse> getDeliveryById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.getDeliveryById(id));
    }

    @PostMapping
    @RequirePermission(Permission.DELIVERIES_UPDATE)
    public ResponseEntity<DeliveryResponse> addDelivery(@RequestBody DeliveryRequest deliveryRequest) {
        return ResponseEntity.ok(deliveryService.addDelivery(deliveryRequest));
    }

    @PutMapping("/{id}")
    @RequirePermission(Permission.DELIVERIES_UPDATE)
    public ResponseEntity<DeliveryResponse> updateDelivery(@PathVariable Long id, @RequestBody DeliveryRequest deliveryRequest) {
        return ResponseEntity.ok(deliveryService.updateDelivery(id, deliveryRequest));
    }

    @PutMapping("/{orderId}/complete")
    @RequirePermission(Permission.DELIVERIES_UPDATE)
    public ResponseEntity<Void> completeDelivery(@PathVariable String orderId) {
        deliveryService.completeDelivery(orderId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @RequirePermission(Permission.DELIVERIES_UPDATE)
    public ResponseEntity<Void> deleteDelivery(@PathVariable Long id) {
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }
}
