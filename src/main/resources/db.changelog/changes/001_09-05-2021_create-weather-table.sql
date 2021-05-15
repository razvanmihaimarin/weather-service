--liquibase formatted sql

--changeset weather:1

CREATE TABLE weather (
    id            SERIAL          NOT NULL    PRIMARY KEY,
    city          VARCHAR(255)    NOT NULL,
    country       VARCHAR(255)    NOT NULL,
    temperature   NUMERIC(5, 2)   NOT NULL,
    added_date    TIMESTAMP       NOT NULL,
    UNIQUE(city, added_date)
);
