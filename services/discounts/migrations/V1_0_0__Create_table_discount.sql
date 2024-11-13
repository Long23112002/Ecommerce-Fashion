CREATE TYPE type_enum AS ENUM ('PERCENTAGE', 'FIXED_AMOUNT');
CREATE TYPE discount_status_enum AS ENUM ('UPCOMING', 'ACTIVE', 'ENDED');
Create TABLE discounts.discount
(
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    condition JSON,
    type type_enum NOT NULL,
    value FLOAT,
    max_value FLOAT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP  NOT NULL,
    discount_status discount_status_enum NOT NULL,
    create_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ,
    update_at TIMESTAMP ,
    create_by BIGINT NOT NULL,
    update_by BIGINT,
    deleted BOOLEAN NOT NULL default FALSE
);