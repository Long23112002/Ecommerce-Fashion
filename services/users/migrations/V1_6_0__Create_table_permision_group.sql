CREATE TABLE users.permission_group
(
    id        BIGSERIAL PRIMARY KEY,
    name      VARCHAR(255) NOT NULL,
    group_ids BIGINT[]
);