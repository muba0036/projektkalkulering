-- ============================================
-- Projektkalkulationsvaerktoej - Database Schema
-- Alpha Solutions Case
-- ============================================

CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE IF NOT EXISTS projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    budget DOUBLE NOT NULL DEFAULT 0,
    start_date DATE,
    deadline DATE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    user_id INT NOT NULL,
    CONSTRAINT fk_project_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS sub_projects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    deadline DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    project_id INT NOT NULL,
    CONSTRAINT fk_subproject_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    estimated_hours DOUBLE NOT NULL DEFAULT 0,
    actual_hours DOUBLE NOT NULL DEFAULT 0,
    hourly_rate DOUBLE NOT NULL DEFAULT 0,
    material_units DOUBLE NOT NULL DEFAULT 0,
    material_cost_per_unit DOUBLE NOT NULL DEFAULT 0,
    budgeted_cost DOUBLE NOT NULL DEFAULT 0,
    deadline DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED',
    sub_project_id INT NOT NULL,
    CONSTRAINT fk_task_subproject FOREIGN KEY (sub_project_id) REFERENCES sub_projects(id) ON DELETE CASCADE
);
