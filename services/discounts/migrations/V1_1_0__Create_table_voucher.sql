CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

Create TABLE discounts.voucher
(
    id BIGSERIAL PRIMARY KEY,
    code UUID NOT NULL DEFAULT uuid_generate_v4(),
    id_discount BIGINT,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by VARCHAR(255),
    update_by VARCHAR(255),
    used_by VARCHAR(255),
    deleted BOOLEAN,
    FOREIGN KEY (id_discount) REFERENCES discounts.discount(id)
);