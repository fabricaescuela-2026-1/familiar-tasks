DROP TABLE IF EXISTS auth.tokens;

DROP TABLE IF EXISTS auth.users;

DROP TYPE IF EXISTS auth.token_type;

CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.users (
    user_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    name VARCHAR(50) NOT NULL,
    lastname VARCHAR(50) NOT NULL,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    email VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS auth.tokens (
    token_id UUID PRIMARY KEY DEFAULT gen_random_uuid (),
    token_hash TEXT NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    expired_at TIMESTAMP DEFAULT NULL,
    user_id UUID NOT NULL REFERENCES auth.users (user_id),
    token_type VARCHAR(50) NOT NULL
)