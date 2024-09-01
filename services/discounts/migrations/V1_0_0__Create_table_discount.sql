CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE type_enum AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');
CREATE TYPE discount_status_enum AS ENUM ('ACTIVE', 'INACTIVE', 'EXPIRED');
Create TABLE discounts.discount
(
    id BIGSERIAL PRIMARY KEY,
    code UUID NOT NULL DEFAULT uuid_generate_v4(),
    condition JSON,
    type type_enum NOT NULL,
    value FLOAT,
    max_value FLOAT,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    discount_status discount_status_enum NOT NULL,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(255),
    update_by VARCHAR(255),
    deleted BOOLEAN
)