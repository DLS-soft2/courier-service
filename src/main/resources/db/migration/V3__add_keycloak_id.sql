ALTER TABLE courier ADD COLUMN keycloak_id VARCHAR(36);
CREATE UNIQUE INDEX uk_courier_keycloak_id ON courier(keycloak_id) WHERE keycloak_id IS NOT NULL;
