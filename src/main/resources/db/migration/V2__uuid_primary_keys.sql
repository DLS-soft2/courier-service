DROP TABLE IF EXISTS delivery_status_history;
DROP TABLE IF EXISTS delivery;
DROP TABLE IF EXISTS courier_status;
DROP TABLE IF EXISTS courier;

CREATE TABLE courier (
    courier_id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    vehicle_type VARCHAR(255) NOT NULL,
    rating DOUBLE PRECISION,
    active BOOLEAN,
    CONSTRAINT uk_courier_email UNIQUE (email),
    CONSTRAINT uk_courier_phone_number UNIQUE (phone_number)
);

CREATE TABLE courier_status (
    id VARCHAR(36) PRIMARY KEY,
    courier_id VARCHAR(36) NOT NULL UNIQUE,
    status VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    last_updated TIMESTAMP,
    CONSTRAINT fk_courier_status_courier FOREIGN KEY (courier_id) REFERENCES courier (courier_id)
);

CREATE TABLE delivery (
    delivery_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36),
    customer_id VARCHAR(36),
    courier_id VARCHAR(36),
    status VARCHAR(255),
    pickup_address VARCHAR(255),
    delivery_address VARCHAR(255),
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP,
    notes VARCHAR(255),
    CONSTRAINT uk_delivery_order_id UNIQUE (order_id),
    CONSTRAINT fk_delivery_courier FOREIGN KEY (courier_id) REFERENCES courier (courier_id)
);

CREATE TABLE delivery_status_history (
    id VARCHAR(36) PRIMARY KEY,
    delivery_id VARCHAR(36),
    status VARCHAR(255),
    updated_at TIMESTAMP,
    location VARCHAR(255),
    notes VARCHAR(255),
    CONSTRAINT fk_delivery_status_history_delivery FOREIGN KEY (delivery_id) REFERENCES delivery (delivery_id)
);
