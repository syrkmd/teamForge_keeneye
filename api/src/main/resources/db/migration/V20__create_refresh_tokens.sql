CREATE TABLE refresh_tokens (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    token_hash VARCHAR(64) NOT NULL,
    jti        VARCHAR(36) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked    BOOLEAN NOT NULL,
    user_id    BIGINT NOT NULL,
    CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
    CONSTRAINT uk_refresh_tokens_jti UNIQUE (jti),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
