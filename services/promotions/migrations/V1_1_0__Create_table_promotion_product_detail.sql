CREATE TABLE promotions.promotion_product_detail
(
    id BIGSERIAL PRIMARY KEY,
    id_promotion BIGINT NOT NULL,
    id_product_detail BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ,
    created_by BIGINT,
    updated_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE
);