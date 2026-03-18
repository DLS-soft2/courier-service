package com.dls.courierservice.Repository;

import com.dls.courierservice.Entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, Long> {
    Optional<Courier> findByName(String name);
    Optional<Courier> findByPhoneNumber(String phoneNumber);
    Optional<Courier> findByEmail(String email);
}
