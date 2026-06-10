package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.CourierStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourierStatusRepository extends JpaRepository<CourierStatus, Long> {
}
