CREATE TABLE products.product
(
    id        BIGSERIAL PRIMARY KEY,
    code      VARCHAR(50) NOT NULL,
    name      VARCHAR(50) NOT NULL,
    description      TEXT,
    create_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT NOT NULL ,
    update_by BIGINT,
    deleted   BOOLEAN DEFAULT FALSE,
    id_brand          BIGINT NOT NULL,
    id_origin         BIGINT NOT NULL,
    id_material         BIGINT NOT NULL,
    id_category         BIGINT NOT NULL,
    CONSTRAINT fk_product_brand FOREIGN KEY (id_brand) REFERENCES products.brand (id),
    CONSTRAINT fk_product_origin FOREIGN KEY (id_origin) REFERENCES products.origin (id),
    CONSTRAINT fk_product_material FOREIGN KEY (id_material) REFERENCES products.material (id),
    CONSTRAINT fk_product_category FOREIGN KEY (id_category) REFERENCES products.category (id)
);