CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    confirmed_requests BIGINT,
    participant_limit  BIGINT,
    views              BIGINT,
    paid               BOOLEAN                     NOT NULL,
    request_moderation BOOLEAN,
    title              VARCHAR(120)                NOT NULL,
    annotation         VARCHAR(2000)               NOT NULL,
    description        VARCHAR(7000)               NOT NULL,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    state              VARCHAR(50),
    lat                REAL                        NOT NULL,
    lon                REAL                        NOT NULL,
    initiator_id       BIGINT                      NOT NULL REFERENCES users (id),
    category_id        BIGINT                      NOT NULL REFERENCES categories (id)
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE,
    status       VARCHAR(50),
    event_id     BIGINT NOT NULL REFERENCES events (id),
    requester_id BIGINT NOT NULL REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    pinned BOOLEAN      NOT NULL,
    title  VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS compilation_events
(
    compilation_id BIGINT NOT NULL REFERENCES compilations (id),
    event_id       BIGINT NOT NULL REFERENCES events (id),
    PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_ratings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    is_like    BOOLEAN                     NOT NULL,
    created_on TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    user_id    BIGINT                      NOT NULL REFERENCES users (id),
    event_id   BIGINT                      NOT NULL REFERENCES events (id)
);