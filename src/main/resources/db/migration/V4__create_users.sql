CREATE TABLE users (
    id                        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email                     VARCHAR(255) NOT NULL,
    password_hash             VARCHAR(255) NOT NULL,
    first_name                VARCHAR(255) NOT NULL,
    last_name                 VARCHAR(255) NOT NULL,
    about                     TEXT NOT NULL,
    github_username           VARCHAR(255) NOT NULL,
    is_active                 BOOLEAN NOT NULL,
    created_at                TIMESTAMPTZ NOT NULL,
    updated_at                TIMESTAMPTZ NOT NULL,
    average_rating            DOUBLE PRECISION NOT NULL,
    reviews_count             INTEGER NOT NULL,
    completed_projects_count  INTEGER NOT NULL,
    completion_rate           DOUBLE PRECISION NOT NULL,
    system_role_id            BIGINT NOT NULL,
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_system_role FOREIGN KEY (system_role_id) REFERENCES system_roles (id)
);
