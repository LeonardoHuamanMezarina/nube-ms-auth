-- Reinicio Final con Status en Permisos
DROP TABLE IF EXISTS user_permissions CASCADE;
DROP TABLE IF EXISTS role_permissions CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS permissions CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

CREATE TABLE roles (id BIGSERIAL PRIMARY KEY, name VARCHAR(50) UNIQUE, description TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE permissions (id BIGSERIAL PRIMARY KEY, name VARCHAR(100) UNIQUE, module VARCHAR(50), description TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE role_permissions (id BIGSERIAL PRIMARY KEY, role_id BIGINT REFERENCES roles(id), permission_id BIGINT REFERENCES permissions(id), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE users (id BIGSERIAL PRIMARY KEY, tenant_id BIGINT, name VARCHAR(100), lastname VARCHAR(100), email VARCHAR(150) UNIQUE, password_hash VARCHAR(255), phone VARCHAR(20), role_id BIGINT REFERENCES roles(id), status VARCHAR(20) DEFAULT 'ACTIVE', created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

-- TABLA 5 ACTUALIZADA CON STATUS
CREATE TABLE user_permissions (
    id BIGSERIAL PRIMARY KEY, 
    user_id BIGINT REFERENCES users(id), 
    permission_id BIGINT REFERENCES permissions(id), 
    status VARCHAR(20) DEFAULT 'ACTIVE', -- Nuevo: ACTIVE o INACTIVE
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, 
    UNIQUE(user_id, permission_id)
);

INSERT INTO roles (name) VALUES ('SUPERADMIN'), ('PARROCO'), ('SECRETARIO');

INSERT INTO permissions (name, module) VALUES 
('PLAT_HOME', 'home'), ('PLAT_USERS', 'usuarios'), ('PLAT_CONFIG', 'config'),
('TEN_SACRAMENTOS', 'sacramentos'), ('TEN_AGENDA', 'agenda-parroquial'), ('TEN_SOLICITUDES', 'solicitudes'), 
('TEN_FIANZAS', 'fianzas-pagos'), ('TEN_VOLUNTARIADO', 'voluntariado'), ('TEN_PERSONAS', 'personas');

INSERT INTO role_permissions (role_id, permission_id) SELECT 1, id FROM permissions WHERE name LIKE 'PLAT_%';
INSERT INTO role_permissions (role_id, permission_id) SELECT 2, id FROM permissions WHERE name LIKE 'TEN_%';
INSERT INTO role_permissions (role_id, permission_id) SELECT 3, id FROM permissions WHERE name LIKE 'TEN_%';
