CREATE TABLE IF NOT EXISTS images.images
(
    id          SERIAL PRIMARY KEY,
    url         TEXT         NOT NULL,
    object_id   BIGINT       NOT NULL,
    object_name varchar(100) NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deleted     BOOLEAN   DEFAULT FALSE
);
