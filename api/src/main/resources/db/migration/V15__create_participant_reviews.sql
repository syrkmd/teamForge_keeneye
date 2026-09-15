CREATE TABLE participant_reviews (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    score        INTEGER NOT NULL,
    comment      TEXT,
    created_at   TIMESTAMPTZ NOT NULL,
    reviewer_id  BIGINT,
    reviewee_id  BIGINT,
    CONSTRAINT ck_participant_reviews_score CHECK (score BETWEEN 1 AND 5),
    CONSTRAINT uk_reviewer_reviewee UNIQUE (reviewer_id, reviewee_id),
    CONSTRAINT fk_participant_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES team_members (id),
    CONSTRAINT fk_participant_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES team_members (id)
);
