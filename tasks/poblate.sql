-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS pgcrypto;
-- ========================
-- INSERT PRIORITIES
-- ========================
INSERT INTO fabrica.priorities (priority_id, name)
VALUES (gen_random_uuid(), 'LOW'),
  (gen_random_uuid(), 'MEDIUM'),
  (gen_random_uuid(), 'HIGH'),
  (gen_random_uuid(), 'URGENT');
-- ========================
-- INSERT STATUS
-- ========================
INSERT INTO fabrica.status (status_id, name)
VALUES (gen_random_uuid(), 'PENDING'),
  (gen_random_uuid(), 'IN_PROGRESS'),
  (gen_random_uuid(), 'COMPLETED'),
  (gen_random_uuid(), 'CANCELLED');
-- ========================
-- INSERT TASKS
-- ========================
-- We reuse priorities and status via subqueries
INSERT INTO fabrica.tasks (
    task_id,
    name,
    description,
    deadline,
    priority_id,
    status_id,
    home_id
  )
VALUES (
    gen_random_uuid(),
    'Fix kitchen sink',
    'Repair the leaking sink in the kitchen',
    NOW() + INTERVAL '2 days',
    (
      SELECT priority_id
      FROM fabrica.priorities
      WHERE name = 'HIGH'
    ),
    (
      SELECT status_id
      FROM fabrica.status
      WHERE name = 'PENDING'
    ),
    gen_random_uuid()
  ),
  (
    gen_random_uuid(),
    'Clean living room',
    'Vacuum and organize the living room',
    NOW() + INTERVAL '1 day',
    (
      SELECT priority_id
      FROM fabrica.priorities
      WHERE name = 'MEDIUM'
    ),
    (
      SELECT status_id
      FROM fabrica.status
      WHERE name = 'IN_PROGRESS'
    ),
    gen_random_uuid()
  ),
  (
    gen_random_uuid(),
    'Paint bedroom',
    'Paint the walls with new color',
    NOW() + INTERVAL '5 days',
    (
      SELECT priority_id
      FROM fabrica.priorities
      WHERE name = 'LOW'
    ),
    (
      SELECT status_id
      FROM fabrica.status
      WHERE name = 'PENDING'
    ),
    gen_random_uuid()
  ),
  (
    gen_random_uuid(),
    'Fix door lock',
    'Replace broken lock in the main door',
    NOW() + INTERVAL '3 days',
    (
      SELECT priority_id
      FROM fabrica.priorities
      WHERE name = 'URGENT'
    ),
    (
      SELECT status_id
      FROM fabrica.status
      WHERE name = 'PENDING'
    ),
    gen_random_uuid()
  ),
  (
    gen_random_uuid(),
    'Wash dishes',
    'Clean all dishes after dinner',
    NOW() + INTERVAL '6 hours',
    (
      SELECT priority_id
      FROM fabrica.priorities
      WHERE name = 'LOW'
    ),
    (
      SELECT status_id
      FROM fabrica.status
      WHERE name = 'COMPLETED'
    ),
    gen_random_uuid()
  );
-- ========================
-- INSERT GUEST TASKS
-- ========================
-- Assign random guests to tasks
INSERT INTO fabrica.guest_task (guest_id, task_id)
SELECT gen_random_uuid(),
  task_id
FROM fabrica.tasks
LIMIT 3;
-- Add more assignments (same task with different guests)
INSERT INTO fabrica.guest_task (guest_id, task_id)
SELECT gen_random_uuid(),
  task_id
FROM fabrica.tasks
ORDER BY RANDOM()
LIMIT 5;