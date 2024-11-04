CREATE TABLE carts.cart
(
    id                   SERIAL PRIMARY KEY,
    user_id              BIGINT,
    product_detail_carts JSONB,
    create_at            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_deleted           BOOLEAN   DEFAULT FALSE
);