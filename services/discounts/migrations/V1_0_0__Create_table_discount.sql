CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE type_enum AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');
CREATE TYPE discount_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'EXPIRED');
Create TABLE discounts.discount
(
    id BIGSERIAL PRIMARY KEY,
    code UUID NOT NULL DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    condition JSON,
    type type_enum NOT NULL,
    value FLOAT,
    max_value FLOAT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    discount_status discount_status_enum NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT NOT NULL,
    update_by BIGINT,
    deleted BOOLEAN NOT NULL default FALSE;
)