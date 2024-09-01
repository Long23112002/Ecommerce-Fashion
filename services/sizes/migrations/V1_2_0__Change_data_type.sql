ALTER TABLE sizes.size
ALTER COLUMN created_by TYPE BIGINT USING created_by::bigint,
ALTER COLUMN created_by DROP NOT NULL;

ALTER TABLE sizes.size
ALTER COLUMN updated_by TYPE BIGINT USING updated_by::bigint,
ALTER COLUMN updated_by DROP NOT NULL;

ALTER TABLE sizes.size
ALTER COLUMN updated_at DROP NOT NULL;

