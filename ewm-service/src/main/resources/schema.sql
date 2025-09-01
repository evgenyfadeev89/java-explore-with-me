
DROP TABLE categories;
-- Схема БД для категорий
CREATE TABLE IF NOT EXISTS categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

DROP TABLE users;
-- Схема БД для пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(250) NOT NULL,
    email VARCHAR(254) NOT NULL UNIQUE
);

DROP TABLE location_type;
-- Встроенный тип для координат
CREATE TYPE location_type AS (
    lat REAL,
    lon REAL
);

DROP TABLE events;
-- Таблица для событий
CREATE TABLE IF NOT EXISTS events (
    id BIGSERIAL PRIMARY KEY,
    annotation VARCHAR(2000) NOT NULL,
    category_id BIGINT NOT NULL REFERENCES categories(id) ON DELETE RESTRICT,
    created_on TIMESTAMP NOT NULL,
    description VARCHAR(7000),
    event_date TIMESTAMP NOT NULL,
    initiator_id BIGINT NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    lat REAL,
    lon REAL,
    paid BOOLEAN NOT NULL,
    participant_limit INT,
    published_on TIMESTAMP,
    request_moderation BOOLEAN,
    state VARCHAR(20),
    title VARCHAR(120) NOT NULL
);

DROP TABLE compilations;
-- Таблица для подборок
CREATE TABLE IF NOT EXISTS compilations (
    id BIGSERIAL PRIMARY KEY,
    pinned BOOLEAN NOT NULL,
    title VARCHAR(50) NOT NULL
);


DROP TABLE compilation_events;
-- Таблица-связка подборок и событий
CREATE TABLE IF NOT EXISTS compilation_events (
    compilation_id BIGINT NOT NULL REFERENCES compilations(id) ON DELETE CASCADE,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (compilation_id, event_id)
);


DROP TABLE  participation_requests;
-- Таблица для заявок на участие
CREATE TABLE IF NOT EXISTS participation_requests (
    id BIGSERIAL PRIMARY KEY,
    created TIMESTAMP NOT NULL,
    event_id BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    requester_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20)
);
