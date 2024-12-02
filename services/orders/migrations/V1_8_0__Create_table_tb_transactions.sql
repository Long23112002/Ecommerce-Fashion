CREATE TABLE tb_transactions
(
    id                  SERIAL PRIMARY KEY,
    gateway             VARCHAR(100)             NOT NULL,
    transaction_date    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    account_number      VARCHAR(100),
    sub_account         VARCHAR(250),
    amount_in           NUMERIC(20, 2)           NOT NULL DEFAULT 0.00,
    amount_out          NUMERIC(20, 2)           NOT NULL DEFAULT 0.00,
    accumulated         NUMERIC(20, 2)           NOT NULL DEFAULT 0.00,
    code                VARCHAR(250),
    transaction_content TEXT,
    reference_number    VARCHAR(255),
    body                TEXT
);