--liquibase formatted sql

--changeset rassul:01-create-table-users

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) CHECK (role IN ('ADMIN', 'USER')) NOT NULL
);
