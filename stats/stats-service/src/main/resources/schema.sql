CREATE TABLE IF NOT EXISTS hits
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    app       VARCHAR(50)                 NOT NULL,
    uri       varchar(255)                NOT NULL,
    ip        VARCHAR(50)                 NOT NULL,
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL
);