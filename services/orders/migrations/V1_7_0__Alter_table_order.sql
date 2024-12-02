CREATE TABLE IF NOT EXISTS orders.order_log
(
    id          SERIAL PRIMARY KEY,
    order_id    BIGINT NOT NULL,
    old_value   VARCHAR,
    new_value   VARCHAR,
    user_id     BIGINT  NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE orders.order
    ADD COLUMN code VARCHAR NULL;

ALTER TABLE orders.order_detail
    ADD COLUMN code VARCHAR NULL;