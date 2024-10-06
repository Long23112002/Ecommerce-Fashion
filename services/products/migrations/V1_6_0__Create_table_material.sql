CREATE TABLE products.material
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);