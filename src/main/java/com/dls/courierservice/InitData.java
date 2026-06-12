package com.dls.courierservice;

import com.dls.courierservice.Entity.Courier;
import com.dls.courierservice.Entity.CourierStatus;
import com.dls.courierservice.Enum.AvailabilityStatus;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Entity.Delivery;
import com.dls.courierservice.Enum.VehicleType;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Repository.CourierStatusRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile("!test")
public class InitData implements CommandLineRunner {

    private final CourierRepository courierRepository;
    private final DeliveryRepository deliveryRepository;
    private final CourierStatusRepository courierStatusRepository;

    public InitData(CourierRepository courierRepository, DeliveryRepository deliveryRepository,
                    CourierStatusRepository courierStatusRepository) {
        this.courierRepository = courierRepository;
        this.deliveryRepository = deliveryRepository;
        this.courierStatusRepository = courierStatusRepository;
    }

    @Override
    public void run(String... args) {
        if (courierRepository.count() > 0) {
            return;
        }

        Courier courier1 = new Courier();
        courier1.setCourierId("c56a4180-65aa-42ec-a945-5fd21dec0538");
        courier1.setName("Ox");
        courier1.setPhoneNumber("22335432");
        courier1.setEmail("oxCourier@gmail.com");
        courier1.setKeycloakId("aaaaaaaa-1111-2222-3333-000000000002");
        courier1.setVehicleType(VehicleType.BIKE);
        courier1.setRating(4.5);
        courier1.setActive(true);
        courierRepository.save(courier1);

        Courier courier2 = new Courier();
        courier2.setCourierId("f47ac10b-58cc-4372-a567-0e02b2c3d479");
        courier2.setName("DLS");
        courier2.setPhoneNumber("22335433");
        courier2.setEmail("dls@gmail.com");
        courier2.setKeycloakId("aaaaaaaa-1111-2222-3333-c00000000001");
        courier2.setVehicleType(VehicleType.CAR);
        courier2.setRating(2.5);
        courier2.setActive(true);
        courierRepository.save(courier2);

        Courier courier3 = new Courier();
        courier3.setCourierId("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c01");
        courier3.setName("Flash");
        courier3.setPhoneNumber("22335434");
        courier3.setEmail("flash@courier.dls");
        courier3.setKeycloakId("aaaaaaaa-1111-2222-3333-c00000000002");
        courier3.setVehicleType(VehicleType.BIKE);
        courier3.setRating(4.8);
        courier3.setActive(true);
        courierRepository.save(courier3);

        Courier courier4 = new Courier();
        courier4.setCourierId("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c02");
        courier4.setName("Turbo");
        courier4.setPhoneNumber("22335435");
        courier4.setEmail("turbo@courier.dls");
        courier4.setKeycloakId("aaaaaaaa-1111-2222-3333-c00000000003");
        courier4.setVehicleType(VehicleType.CAR);
        courier4.setRating(3.9);
        courier4.setActive(true);
        courierRepository.save(courier4);

        Courier courier5 = new Courier();
        courier5.setCourierId("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c03");
        courier5.setName("Blaze");
        courier5.setPhoneNumber("22335436");
        courier5.setEmail("blaze@courier.dls");
        courier5.setKeycloakId("aaaaaaaa-1111-2222-3333-c00000000004");
        courier5.setVehicleType(VehicleType.VAN);
        courier5.setRating(1.5);
        courier5.setActive(true);
        courierRepository.save(courier5);

        Courier courier6 = new Courier();
        courier6.setCourierId("a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c04");
        courier6.setName("Dash");
        courier6.setPhoneNumber("22335437");
        courier6.setEmail("dash@courier.dls");
        courier6.setKeycloakId("aaaaaaaa-1111-2222-3333-c00000000005");
        courier6.setVehicleType(VehicleType.BIKE);
        courier6.setRating(3.2);
        courier6.setActive(true);
        courierRepository.save(courier6);

        CourierStatus status1 = new CourierStatus();
        status1.setId(UUID.randomUUID().toString());
        status1.setCourier(courier1);
        status1.setStatus(AvailabilityStatus.AVAILABLE);
        status1.setLatitude(55.6761);
        status1.setLongitude(12.5683);
        status1.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status1);

        CourierStatus status2 = new CourierStatus();
        status2.setId(UUID.randomUUID().toString());
        status2.setCourier(courier2);
        status2.setStatus(AvailabilityStatus.AVAILABLE);
        status2.setLatitude(55.6867);
        status2.setLongitude(12.5701);
        status2.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status2);

        CourierStatus status3 = new CourierStatus();
        status3.setId(UUID.randomUUID().toString());
        status3.setCourier(courier3);
        status3.setStatus(AvailabilityStatus.AVAILABLE);
        status3.setLatitude(55.6967);
        status3.setLongitude(12.5501);
        status3.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status3);

        CourierStatus status4 = new CourierStatus();
        status4.setId(UUID.randomUUID().toString());
        status4.setCourier(courier4);
        status4.setStatus(AvailabilityStatus.AVAILABLE);
        status4.setLatitude(55.6692);
        status4.setLongitude(12.5500);
        status4.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status4);

        CourierStatus status5 = new CourierStatus();
        status5.setId(UUID.randomUUID().toString());
        status5.setCourier(courier5);
        status5.setStatus(AvailabilityStatus.AVAILABLE);
        status5.setLatitude(55.7050);
        status5.setLongitude(12.5780);
        status5.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status5);

        CourierStatus status6 = new CourierStatus();
        status6.setId(UUID.randomUUID().toString());
        status6.setCourier(courier6);
        status6.setStatus(AvailabilityStatus.AVAILABLE);
        status6.setLatitude(55.6550);
        status6.setLongitude(12.6050);
        status6.setLastUpdated(LocalDateTime.now());
        courierStatusRepository.save(status6);

        Delivery delivery1 = new Delivery();
        delivery1.setDeliveryId(UUID.randomUUID().toString());
        delivery1.setCourier(courier1);
        delivery1.setStatus(DeliveryStatus.ASSIGNED);
        delivery1.setOrderId("550e8400-e29b-41d4-a716-446655440001");
        delivery1.setCustomerId("660e8400-e29b-41d4-a716-446655440001");
        delivery1.setPickupAddress("Vesterbrogade 1, 1620 København");
        delivery1.setDeliveryAddress("Nørrebrogade 20, 2200 København");
        delivery1.setAssignedAt(LocalDateTime.now());
        deliveryRepository.save(delivery1);

        Delivery delivery2 = new Delivery();
        delivery2.setDeliveryId(UUID.randomUUID().toString());
        delivery2.setCourier(courier2);
        delivery2.setStatus(DeliveryStatus.IN_TRANSIT);
        delivery2.setOrderId("550e8400-e29b-41d4-a716-446655440002");
        delivery2.setCustomerId("660e8400-e29b-41d4-a716-446655440002");
        delivery2.setPickupAddress("Amagerbrogade 10, 2300 København");
        delivery2.setDeliveryAddress("Østerbrogade 15, 2100 København");
        delivery2.setAssignedAt(LocalDateTime.now().minusHours(1));
        deliveryRepository.save(delivery2);
    }
}
