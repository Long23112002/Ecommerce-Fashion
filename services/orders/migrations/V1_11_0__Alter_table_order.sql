CREATE TYPE payment_enum_new AS ENUM ('VNPAY', 'VIET_QR', 'CASH');

ALTER TABLE orders.order ALTER COLUMN payment_method DROP DEFAULT;

ALTER TABLE orders.order
ALTER COLUMN payment_method TYPE payment_enum_new
USING payment_method::text::payment_enum_new;

DROP TYPE payment_enum;

ALTER TYPE payment_enum_new RENAME TO payment_enum;

ALTER TABLE orders.order ALTER COLUMN payment_method SET DEFAULT 'CASH';
