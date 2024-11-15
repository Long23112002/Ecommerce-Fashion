CREATE TYPE payment_enum AS ENUM ('VNPAY', 'BANK_TRANSFER','CASH');
ALTER TABLE orders.order
    ADD COLUMN payment_method payment_enum DEFAULT 'CASH';

UPDATE orders.order SET payment_method = 'CASH' WHERE payment_method IS NULL;

ALTER TABLE orders.order ALTER COLUMN payment_method SET NOT NULL;

ALTER TABLE orders.order DROP COLUMN payment_method_id;
