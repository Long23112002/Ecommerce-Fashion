CREATE TABLE products.origin
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(50) NOT NULL,
    create_at TIMESTAMP  NOT NULL,
    update_at TIMESTAMP ,
    create_by BIGINT NOT NULL,
    update_by BIGINT,
    deleted   BOOLEAN DEFAULT FALSE
);