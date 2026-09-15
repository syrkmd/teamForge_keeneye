CREATE TABLE user_skills (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    level      INTEGER NOT NULL,
    updated_at TIMESTAMPTZ,
    user_id    BIGINT NOT NULL,
    skill_id   BIGINT NOT NULL,
    CONSTRAINT ck_user_skills_level CHECK (level BETWEEN 1 AND 5),
    CONSTRAINT uk_user_skill UNIQUE (user_id, skill_id),
    CONSTRAINT fk_user_skills_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_user_skills_skill FOREIGN KEY (skill_id) REFERENCES skills (id)
);
