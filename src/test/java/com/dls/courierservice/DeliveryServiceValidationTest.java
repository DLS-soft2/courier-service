package com.dls.courierservice;

import com.dls.courierservice.DTO.DeliveryRequest;
import com.dls.courierservice.Enum.DeliveryStatus;
import com.dls.courierservice.Repository.CourierRepository;
import com.dls.courierservice.Repository.DeliveryRepository;
import com.dls.courierservice.Service.DeliveryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceValidationTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private CourierRepository courierRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        deliveryService = new DeliveryService(deliveryRepository, courierRepository, kafkaTemplate, "deliveries");
    }

    @Test
    void addDelivery_rejectsNullOrderId() {
        DeliveryRequest request = buildValidRequest();
        request.setOrderId(null);
        assertThrows(IllegalArgumentException.class, () -> deliveryService.addDelivery(request));
    }

    @Test
    void addDelivery_rejectsBlankOrderId() {
        DeliveryRequest request = buildValidRequest();
        request.setOrderId("   ");
        assertThrows(IllegalArgumentException.class, () -> deliveryService.addDelivery(request));
    }

    private DeliveryRequest buildValidRequest() {
        DeliveryRequest request = new DeliveryRequest();
        request.setOrderId("550e8400-e29b-41d4-a716-446655440001");
        request.setCourierId(1L);
        request.setStatus(DeliveryStatus.ASSIGNED);
        request.setPickupAddress("Vesterbrogade 1, 1620 København");
        request.setDeliveryAddress("Nørrebrogade 20, 2200 København");
        return request;
    }
}
