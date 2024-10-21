CREATE TABLE products.product_detail
(
    id        BIGSERIAL PRIMARY KEY,
    price     DOUBLE PRECISION,
    quantity     INTEGER,
    images JSON,
    create_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT NOT NULL ,
    update_by BIGINT,
    deleted   BOOLEAN DEFAULT FALSE,
    id_product         BIGINT NOT NULL,
    id_size         BIGINT NOT NULL,
    id_color         BIGINT NOT NULL,
    CONSTRAINT fk_product_detail_product FOREIGN KEY (id_product) REFERENCES products.product (id),
    CONSTRAINT fk_product_detail_size FOREIGN KEY (id_size) REFERENCES products.size (id),
    CONSTRAINT fk_product_detail_color FOREIGN KEY (id_color) REFERENCES products.color (id)
);