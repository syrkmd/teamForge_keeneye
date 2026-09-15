CREATE TABLE recommendations (
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT NOT NULL,
    priority     VARCHAR(50) NOT NULL,
    status       VARCHAR(50) NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL,
    resolved_at  TIMESTAMPTZ,
    team_id      BIGINT NOT NULL,
    CONSTRAINT ck_recommendations_priority CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    CONSTRAINT ck_recommendations_status CHECK (status IN ('OPEN', 'RESOLVED')),
    CONSTRAINT fk_recommendations_team FOREIGN KEY (team_id) REFERENCES teams (id)
);
