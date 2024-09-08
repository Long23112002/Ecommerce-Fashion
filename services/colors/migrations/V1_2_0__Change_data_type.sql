ALTER TABLE colors.color
ALTER COLUMN created_by TYPE BIGINT USING created_by::bigint,
ALTER COLUMN created_by DROP NOT NULL;

ALTER TABLE colors.color
ALTER COLUMN updated_by TYPE BIGINT USING updated_by::bigint,
ALTER COLUMN updated_by DROP NOT NULL;

ALTER TABLE colors.color
ALTER COLUMN updated_at DROP NOT NULL;