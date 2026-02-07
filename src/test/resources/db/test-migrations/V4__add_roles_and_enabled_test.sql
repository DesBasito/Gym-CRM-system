CREATE TABLE IF NOT EXISTS roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (name)
SELECT 'ROLE_TRAINEE'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_TRAINEE');

INSERT INTO roles (name)
SELECT 'ROLE_TRAINER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ROLE_TRAINER');

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS enabled BOOLEAN NOT NULL DEFAULT true;

UPDATE users SET enabled = is_active;

INSERT INTO user_roles (user_id, role_id)
SELECT t.user_id, r.id
FROM trainees t
CROSS JOIN roles r
WHERE r.name = 'ROLE_TRAINEE'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = t.user_id AND ur.role_id = r.id);

INSERT INTO user_roles (user_id, role_id)
SELECT t.user_id, r.id
FROM trainers t
CROSS JOIN roles r
WHERE r.name = 'ROLE_TRAINER'
AND NOT EXISTS (SELECT 1 FROM user_roles ur WHERE ur.user_id = t.user_id AND ur.role_id = r.id);

CREATE INDEX IF NOT EXISTS idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX IF NOT EXISTS idx_users_enabled ON users(enabled);