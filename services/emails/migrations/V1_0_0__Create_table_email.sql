
CREATE TYPE email_status_enum AS ENUM ('PENDING', 'SENT');
CREATE TYPE email_type_enum AS ENUM ('SCHEDULE', 'IMMEDIATE');
CREATE TYPE email_send_log_status_enum AS ENUM ('FAILED', 'SUCCESSED');

CREATE TABLE emails.template
(
    id BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255),
    subject    VARCHAR(255),
    body       VARCHAR(255),
    variables  JSON,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE emails.process_send
(
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    count_mail_sent   BIGINT,
    count_mail_unsent BIGINT,
    size_process      BIGINT
);

CREATE TABLE emails.email
(
    id BIGSERIAL PRIMARY KEY,
    content         VARCHAR(255),
    status email_status_enum NOT NULL,
    type email_type_enum NOT NULL,
    filter          JSON,
    send_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    id_template     BIGINT,
    id_process_send BIGINT,
    FOREIGN KEY (id_template) REFERENCES template (id),
    FOREIGN KEY (id_process_send) REFERENCES process_send (id)
);

CREATE TABLE emails.email_send_log
(
    id BIGSERIAL PRIMARY KEY,
    send_to         BIGINT,
    status email_send_log_status_enum NOT NULL,
    description     VARCHAR(255),
    id_email        BIGINT,
    id_process_send BIGINT,
    FOREIGN KEY (id_email) REFERENCES email (id),
    FOREIGN KEY (id_process_send) REFERENCES process_send (id)
);