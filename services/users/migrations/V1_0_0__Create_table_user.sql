CREATE TYPE gender_enum AS ENUM ('FEMALE', 'MALE', 'OTHER');

CREATE TABLE users.user
(
    id                  BIGSERIAL PRIMARY KEY,
    email               VARCHAR(255) NOT NULL UNIQUE,
    password            TEXT,
    full_name           VARCHAR(50),
    phone_number        VARCHAR(12) UNIQUE,
    birth               DATE,
    gender              gender_enum NOT NULL,
    avatar              TEXT,
    id_google_account   VARCHAR(255),
    id_facebook_account VARCHAR(255),
    create_at           TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    update_at           TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_admin            BOOLEAN NOT NULL DEFAULT FALSE,
    deleted             BOOLEAN DEFAULT FALSE
);

CREATE TABLE users.role
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE users.user_roles
(
    id_user BIGINT,
    id_role BIGINT,
    PRIMARY KEY (id_user, id_role),
    FOREIGN KEY (id_user) REFERENCES "user" (id),
    FOREIGN KEY (id_role) REFERENCES role (id)
);

CREATE TABLE users.permission
(
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE users.role_permission
(
    id_role       BIGINT,
    id_permission BIGINT,
    PRIMARY KEY (id_role, id_permission),
    FOREIGN KEY (id_role) REFERENCES role (id),
    FOREIGN KEY (id_permission) REFERENCES permission (id)
);

CREATE TABLE users.refresh_tokens
(
    id         BIGSERIAL PRIMARY KEY,
    token      VARCHAR(255) NOT NULL,
    user_id    BIGINT NOT NULL,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_revoked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES "user" (id)
);
