CREATE TABLE system_roles (
    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    CONSTRAINT uk_system_roles_name UNIQUE (name),
    CONSTRAINT ck_system_roles_name CHECK (name IN ('USER', 'ADMIN', 'PROJECT_OWNER'))
);
