

CREATE TYPE image_type_enum AS ENUM ('JPG', 'PNG', 'SVG');

CREATE TABLE images.images
(
    id         BIGSERIAL PRIMARY KEY,
    url        VARCHAR(255) NOT NULL,
    type       image_type_enum NOT NULL,
    size       BIGINT NOT NULL,
    title      VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ,
    deleted    BOOLEAN NOT NULL DEFAULT FALSE
);
