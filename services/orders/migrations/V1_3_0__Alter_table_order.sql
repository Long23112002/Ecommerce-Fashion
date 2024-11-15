ALTER TABLE orders.order
    ALTER COLUMN address TYPE JSONB USING address::jsonb;
