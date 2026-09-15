CREATE TABLE skill_categories (
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name      VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    CONSTRAINT uk_skill_categories_name UNIQUE (name),
    CONSTRAINT ck_skill_categories_name CHECK (name IN ('PROGRAMMING', 'DESIGN', 'MANAGEMENT')),
    CONSTRAINT fk_skill_categories_parent FOREIGN KEY (parent_id) REFERENCES skill_categories (id)
);
