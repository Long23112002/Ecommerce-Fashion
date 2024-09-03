CREATE TYPE send_status_enum AS ENUM ('SUCCESS', 'FAILED');
CREATE TYPE email_type_enum AS ENUM ('IMMEDIATE', 'SCHEDULED');
CREATE TYPE email_api_enum AS ENUM ('MAIL_GUN');

CREATE TABLE emails.template
(
    id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    subject    TEXT,
    html       TEXT,
    variables  JSON,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE emails.email
(
    id BIGSERIAL PRIMARY KEY,
    content         TEXT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    filter          JSON,
    is_deleted      BOOLEAN DEFAULT FALSE,
    is_personalized BOOLEAN DEFAULT FALSE,
    send_at         JSON,
    send_from       VARCHAR(255) NOT NULL,
    type email_type_enum NOT NULL,
    template_id     BIGINT,
    CONSTRAINT fk_email_template FOREIGN KEY (template_id) REFERENCES emails.template (id)
);

CREATE TABLE emails.process_send
(
    id BIGSERIAL PRIMARY KEY,
    count_mail_failed BIGINT,
    count_mail_sent   BIGINT,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    size_process      BIGINT,
    email_id          BIGINT NOT NULL,
    CONSTRAINT fk_process_send_email FOREIGN KEY (email_id) REFERENCES emails.email (id)

);

CREATE TABLE emails.mail_send_log
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    description           TEXT,
    mail_api_type_success email_api_enum,
    mail_api_types_used   JSON,
    send_to               BIGINT,
    status send_status_enum NOT NULL,
    email_id              BIGINT,
    process_send_id       BIGINT,
    CONSTRAINT fk_mail_send_log_process_send FOREIGN KEY (process_send_id) REFERENCES emails.process_send (id),
    CONSTRAINT fk_mail_send_log_email FOREIGN KEY (email_id) REFERENCES emails.email (id)
);

CREATE TABLE emails.statistical
(
    email_id    BIGINT,
    process_ids JSON,
    CONSTRAINT fk_statistical_email FOREIGN KEY (email_id) REFERENCES emails.email (id)
);
