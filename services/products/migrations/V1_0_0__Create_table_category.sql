CREATE TABLE products.category
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(50) NOT NULL,
    create_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    lever     INT  NOT NULL,
    parent_id BIGINT,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    create_by BIGINT NOT NULL,
    update_by BIGINT,
    deleted   BOOLEAN DEFAULT FALSE,
    CONSTRAINT fk_parent FOREIGN KEY (parent_id) REFERENCES Category (id)
);