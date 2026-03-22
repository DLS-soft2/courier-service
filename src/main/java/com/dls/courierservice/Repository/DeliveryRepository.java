package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
        Delivery findByCourierId(Long courierId);
        Delivery findByDeliveryStatus(String deliveryStatus);
}
