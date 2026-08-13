CREATE TABLE auth_account (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT,
                              provider INT NOT NULL,
                              provider_id VARCHAR(255) NOT NULL,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              CONSTRAINT uk_auth_provider UNIQUE (provider, provider_id),
                              CONSTRAINT fk_auth_account_user FOREIGN KEY (user_id) REFERENCES users(id)
);