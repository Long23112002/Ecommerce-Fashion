-- Thêm một danh mục chính
INSERT INTO products.category (name, create_at, lever, parent_id, update_at, create_by, update_by, deleted)
VALUES ('Danh mục chính', CURRENT_TIMESTAMP, 1, NULL, CURRENT_TIMESTAMP, 1, NULL, FALSE);

-- Thêm một danh mục con
INSERT INTO products.category (name, create_at, lever, parent_id, update_at, create_by, update_by, deleted)
VALUES ('Danh mục con', CURRENT_TIMESTAMP, 2, 1, CURRENT_TIMESTAMP, 1, NULL, FALSE);
