CREATE TABLE device_token (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              token VARCHAR(512) NOT NULL,
                              platform VARCHAR(20) NOT NULL,
                              active BOOLEAN NOT NULL,
                              last_used_at DATETIME,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              CONSTRAINT uq_device_token_token UNIQUE (token)
);