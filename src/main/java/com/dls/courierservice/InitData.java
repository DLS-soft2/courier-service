package com.dls.courierservice;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Repository.CourierRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class InitData implements CommandLineRunner {

    private final CourierRepository courierRepository;

    public InitData(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
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
        courierRepository.save(courier1);

        Courier courier2 = new Courier();
        courier2.setName("DLS");
        courier2.setPhoneNumber("22335433");
        courier2.setEmail("dls@gmail.com");
        courierRepository.save(courier2);

        System.out.println("Initiel data gemt til databasen!");

    }
}
