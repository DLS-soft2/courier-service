package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.CourierStatus;
import com.dls.courierservice.Enum.AvailabilityStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourierStatusRepository extends JpaRepository<CourierStatus, String> {
    List<CourierStatus> findByStatus(AvailabilityStatus status);
}
