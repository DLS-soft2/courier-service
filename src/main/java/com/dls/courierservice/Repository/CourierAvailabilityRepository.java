package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierAvailabilityRepository extends JpaRepository<Courier, Long> {
        Courier findByCourierId(Long courierId);
}
