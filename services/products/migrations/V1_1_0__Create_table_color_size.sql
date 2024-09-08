CREATE TABLE products.color
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE products.size
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);