package com.dls.courierservice;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Profile("!test")
public class InitData implements CommandLineRunner {

    private final CourierRepository courierRepository;
    private final DeliveryRepository deliveryRepository;

    public InitData(CourierRepository courierRepository, DeliveryRepository deliveryRepository) {
        this.courierRepository = courierRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Applikationen starter!");


        if (courierRepository.count() > 0) {
            System.out.println("Data findes allerede i databasen!");
            return;
        }

        Courier courier1 = new Courier();
        courier1.setName("Ox");
        courier1.setPhoneNumber("22335432");
        courier1.setEmail("oxCourier@gmail.com");
        courier1.setVehicleType(VehicleType.BIKE);
        courier1.setRating("4.5");
        courier1.setActive(true);
        courierRepository.save(courier1);

        Courier courier2 = new Courier();
        courier2.setName("DLS");
        courier2.setPhoneNumber("22335433");
        courier2.setEmail("dls@gmail.com");
        courier2.setVehicleType(VehicleType.CAR);
        courier2.setRating("2.5");
        courier2.setActive(true);
        courierRepository.save(courier2);

        Delivery delivery1 = new Delivery();
        delivery1.setCourier(courier1);
        delivery1.setStatus(DeliveryStatus.ASSIGNED);
        delivery1.setOrderId(1001L);
        delivery1.setPickupAddress("Vesterbrogade 1, 1620 København");
        delivery1.setDeliveryAddress("Nørrebrogade 20, 2200 København");
        delivery1.setAssignedAt(LocalDateTime.now());
        delivery1.setCompletedAt(null);
        delivery1.setNotes("Ring på dørklokken");
        deliveryRepository.save(delivery1);

        Delivery delivery2 = new Delivery();
        delivery2.setCourier(courier2);
        delivery2.setStatus(DeliveryStatus.IN_TRANSIT);
        delivery2.setOrderId(1002L);
        delivery2.setPickupAddress("Amagerbrogade 10, 2300 København");
        delivery2.setDeliveryAddress("Østerbrogade 15, 2100 København");
        delivery2.setAssignedAt(LocalDateTime.now().minusHours(1));
        delivery2.setCompletedAt(null);
        delivery2.setNotes("Efterlad pakken ved døren");
        deliveryRepository.save(delivery2);





        System.out.println("Initiel data gemt til databasen!");

    }
}
