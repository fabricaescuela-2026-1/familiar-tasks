CREATE SCHEMA IF NOT EXISTS fabrica;
CREATE TABLE IF NOT EXISTS fabrica.priorities (
  priority_id UUID PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS fabrica.status (
  status_id UUID PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL
);
CREATE TABLE IF NOT EXISTS fabrica.tasks (
  task_id UUID PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  deadline TIMESTAMP,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  priority_id UUID,
  status_id UUID,
  home_id UUID NOT NULL,
  guest_id UUID,
  FOREIGN KEY (priority_id) REFERENCES fabrica.priorities(priority_id) ON UPDATE CASCADE ON DELETE CASCADE,
  FOREIGN KEY (status_id) REFERENCES fabrica.status(status_id) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS fabrica.guest_task (
  guest_id UUID NOT NULL,
  task_id UUID NOT NULL,
  PRIMARY KEY (guest_id, task_id),
  FOREIGN KEY (task_id) REFERENCES fabrica.tasks(task_id) ON UPDATE CASCADE ON DELETE CASCADE
)