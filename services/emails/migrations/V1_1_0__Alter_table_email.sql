ALTER TABLE emails.email
    ADD COLUMN subject VARCHAR(50);
ALTER TABLE emails.mail_send_log
    ALTER COLUMN send_to TYPE VARCHAR(50);