CREATE TABLE refresh_token (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               token_hash VARCHAR(255) NOT NULL,
                               expired_at DATETIME NOT NULL,
                               revoked_at DATETIME,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME NOT NULL,
                               CONSTRAINT uq_refresh_token_user_id UNIQUE (user_id),
                               CONSTRAINT uq_refresh_token_hash UNIQUE (token_hash)
);