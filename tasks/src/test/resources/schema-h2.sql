CREATE SCHEMA IF NOT EXISTS task_management;

CREATE TABLE IF NOT EXISTS task_management.guests (
  guest_id UUID PRIMARY KEY,
  name VARCHAR(255),
  last_name VARCHAR(255),
  email VARCHAR(255),
  password_hash VARCHAR(255),
  is_active BOOLEAN,
  created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_management.status (
  status_id UUID PRIMARY KEY,
  name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS task_management.priorities (
  priority_id UUID PRIMARY KEY,
  name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS task_management.tasks (
  task_id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255) NOT NULL,
  status_id UUID NOT NULL,
  priority_id UUID NOT NULL,
  home_id UUID NOT NULL,
  guest_id UUID,
  created_at TIMESTAMP,
  deadline TIMESTAMP NOT NULL
);
