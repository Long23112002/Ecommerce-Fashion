
ALTER TABLE users.user
    ADD COLUMN slug_email VARCHAR(255),
    ADD COLUMN slug_full_name VARCHAR(255);
