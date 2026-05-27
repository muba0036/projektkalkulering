-- ============================================
-- Test data for development
-- ============================================

INSERT IGNORE INTO users (username, password, role) VALUES
  ('admin', 'admin123', 'ADMIN'),
  ('klaus', 'password', 'PROJECT_MANAGER'),
  ('user1', 'password', 'USER');

INSERT IGNORE INTO projects (name, description, status, budget, start_date, deadline, user_id) VALUES
  ('Website Redesign', 'Redesign of the company website', 'IN_PROGRESS', 200000, '2026-05-01', '2026-09-30', 2),
  ('CRM Implementation - Ahlsell', 'Salesforce CRM rollout', 'NOT_STARTED', 100000, '2026-06-01', '2026-12-15', 2);

INSERT IGNORE INTO sub_projects (name, description, deadline, project_id) VALUES
  ('Design', 'UI/UX design og implementering', '2026-07-01', 1),
  ('Backend', 'Backend infrastructure og API', '2026-08-01', 1),
  ('Testing', 'QA og testing', '2026-09-01', 1),
  ('Salesforce Setup', 'Initial konfiguration', '2026-08-01', 2);

INSERT IGNORE INTO tasks (name, description, estimated_hours, actual_hours, hourly_rate, material_units, material_cost_per_unit, budgeted_cost, deadline, status, sub_project_id) VALUES
  -- Design (subproject 1)
  ('UI/UX Design', 'Wireframes og prototyper', 40, 38, 500, 0, 0, 25000, '2026-06-01', 'DONE', 1),
  ('Graphics', 'Logo og grafik assets', 15, 13, 500, 10, 150, 10000, '2026-06-15', 'DONE', 1),
  ('Content', 'Tekst og indhold', 10, 9, 500, 0, 0, 7000, '2026-06-30', 'DONE', 1),
  -- Backend (subproject 2)
  ('Database', 'Database design og setup', 30, 28, 550, 0, 0, 20000, '2026-07-01', 'DONE', 2),
  ('API Development', 'REST API endpoints', 45, 40, 550, 5, 200, 26000, '2026-07-15', 'IN_PROGRESS', 2),
  ('Server Setup', 'Server konfiguration', 10, 9, 550, 2, 2000, 10000, '2026-07-30', 'DONE', 2),
  ('Security', 'Sikkerhed og authentication', 8, 7, 550, 0, 0, 5000, '2026-08-01', 'IN_PROGRESS', 2),
  -- Testing (subproject 3)
  ('Manual Testing', 'Manuel test af alle features', 20, 18, 500, 0, 0, 11000, '2026-08-15', 'IN_PROGRESS', 3),
  ('Automated Testing', 'Automatiserede tests', 15, 14, 500, 0, 0, 8000, '2026-08-30', 'IN_PROGRESS', 3),
  ('UAT', 'User acceptance testing', 10, 9, 500, 0, 0, 6000, '2026-09-15', 'NOT_STARTED', 3);
