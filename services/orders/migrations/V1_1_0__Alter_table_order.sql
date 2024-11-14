ALTER TABLE orders.order
    ADD COLUMN full_name VARCHAR(50),
    ADD COLUMN discount_amount DOUBLE PRECISION DEFAULT 0,
    ADD COLUMN final_price DOUBLE PRECISION DEFAULT 0;

ALTER TABLE orders.order
    ALTER COLUMN user_id DROP NOT NULL,
    ALTER COLUMN phone_number DROP NOT NULL;
