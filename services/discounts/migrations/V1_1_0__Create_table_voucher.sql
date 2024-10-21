CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

Create TABLE discounts.voucher
(
    id BIGSERIAL PRIMARY KEY,
    code UUID NOT NULL DEFAULT uuid_generate_v4(),
    id_discount BIGINT,
    create_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    used_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT NOT NULL,
    update_by BIGINT,
    used_by BIGINT,
    deleted BOOLEAN NOT NULL default FALSE ,
    FOREIGN KEY (id_discount) REFERENCES discounts.discount(id)
);