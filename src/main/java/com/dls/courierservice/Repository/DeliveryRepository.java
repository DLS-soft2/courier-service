package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, String> {
        List<Delivery> findByCourier_CourierId(String courierId);
        List<Delivery> findByStatus(DeliveryStatus status);
        Optional<Delivery> findByOrderId(String orderId);
        int countByCourierAndStatusNotIn(Courier courier, List<DeliveryStatus> statuses);
}