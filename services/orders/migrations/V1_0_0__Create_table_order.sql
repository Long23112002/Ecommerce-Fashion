CREATE TABLE IF NOT EXISTS orders.order
(
    id
    SERIAL
    PRIMARY
    KEY,
    user_id
    BIGINT
    NOT
    NULL,
    discount_id
    BIGINT,
    status
    VARCHAR
    NOT
    NULL,
    payment_method_id
    BIGINT
    NOT
    NULL,
    phone_number
    VARCHAR
(
    11
) NOT NULL,
    address VARCHAR NOT NULL,
    shipdate TIMESTAMP,
    money_ship DOUBLE PRECISION DEFAULT 0,
    note VARCHAR,
    total_money DOUBLE PRECISION DEFAULT 0,
    created_by BIGINT,
    updated_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted BOOLEAN DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS orders.order_detail
(
    id
    SERIAL
    PRIMARY
    KEY,
    order_id
    BIGINT
    NOT
    NULL,
    product_detail_id
    BIGINT
    NOT
    NULL,
    quantity
    INT
    NOT
    NULL,
    price
    DOUBLE
    PRECISION
    DEFAULT
    0,
    total_money
    DOUBLE
    PRECISION
    DEFAULT
    0,
    created_by
    BIGINT,
    updated_by
    BIGINT,
    created_at
    TIMESTAMP
    DEFAULT
    CURRENT_TIMESTAMP,
    updated_at
    TIMESTAMP
    DEFAULT
    CURRENT_TIMESTAMP,
    deleted
    BOOLEAN
    DEFAULT
    FALSE
);
