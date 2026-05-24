CREATE SCHEMA IF NOT EXISTS auth;

CREATE TABLE IF NOT EXISTS auth.users (
  user_id UUID PRIMARY KEY,
  name VARCHAR(50) NOT NULL,
  lastname VARCHAR(50) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  is_active BOOLEAN NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  created_at TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS auth.tokens (
  token_id UUID PRIMARY KEY,
  token_hash VARCHAR(255) NOT NULL UNIQUE,
  expiration_date TIMESTAMP NOT NULL,
  expirated_at TIMESTAMP,
  user_id UUID REFERENCES auth.users(user_id),
  token_type VARCHAR(50) NOT NULL
);
