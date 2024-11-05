ALTER TABLE products.product_detail
    ADD COLUMN if NOT EXISTS  origin_price DOUBLE PRECISION;
ALTER TABLE products.product
    ADD COLUMN if NOT EXISTS  image TEXT;
ALTER TABLE products.product
    ADD COLUMN if NOT EXISTS  min_price DOUBLE PRECISION;
ALTER TABLE products.product
    ADD COLUMN if NOT EXISTS  max_price DOUBLE PRECISION;
ALTER TABLE products.color
    ADD COLUMN if NOT EXISTS  code string;