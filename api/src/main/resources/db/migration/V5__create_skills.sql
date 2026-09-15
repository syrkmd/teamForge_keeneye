CREATE TABLE skills (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    type                VARCHAR(50) NOT NULL,
    category_id         BIGINT NOT NULL,
    created_by_user_id  BIGINT,
    CONSTRAINT ck_skills_type CHECK (type IN ('GLOBAL', 'CUSTOM')),
    CONSTRAINT uk_skill_category_name UNIQUE (category_id, name),
    CONSTRAINT fk_skills_category FOREIGN KEY (category_id) REFERENCES skill_categories (id),
    CONSTRAINT fk_skills_created_by_user FOREIGN KEY (created_by_user_id) REFERENCES users (id)
);
