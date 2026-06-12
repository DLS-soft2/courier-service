# courier-service

Handles courier assignment, delivery tracking, and AI-powered courier scoring for the DLS-2 food delivery platform.

## Tech Stack

- **Framework:** Spring Boot 4.0.3 (Java 21)
- **Database:** PostgreSQL (Spring Data JPA, Flyway migrations)
- **Messaging:** Apache Kafka (Spring Kafka)
- **Auth:** Keycloak (JWT via API Gateway) + shared RBAC library
- **CI/CD:** GitHub Actions → GHCR

## REST Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v2/couriers` | List all couriers |
| GET | `/api/v2/couriers/me` | Get courier by keycloakId |
| GET | `/api/v2/couriers/{id}` | Get courier by ID |
| POST | `/api/v2/couriers` | Create courier |
| GET | `/api/v2/deliveries` | List all deliveries |
| GET | `/api/v2/deliveries/courier/{courierId}` | Deliveries for a specific courier |
| PUT | `/api/v2/deliveries/{orderId}/complete` | Mark delivery complete |

## Kafka Events

**Consumes:**

- `restaurants` topic: `RestaurantAccepted` — triggers courier assignment

**Produces:**

- `couriers` topic: `CourierAssigned` — courier successfully assigned to order
- `couriers` topic: `CourierAssignmentFailed` — no courier available (triggers compensating refund)
- `deliveries` topic: `DeliveryCompleted` — delivery marked complete

## AI Integration

Calls ai-service synchronously for courier scoring and ranking when assigning a courier to an order.

## Development

```bash
./mvnw spring-boot:run
```

Requires PostgreSQL and Kafka — see `docker-compose.yaml` for local dev.

## Run Tests

```bash
./mvnw test                       # 22 tests
```
