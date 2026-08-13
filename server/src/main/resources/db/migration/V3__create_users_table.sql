CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       user_id VARCHAR(255) NOT NULL,
                       nickname VARCHAR(255) NOT NULL,
                       user_status VARCHAR(20) NOT NULL,
                       user_role VARCHAR(20) NOT NULL,
                       onboarding_completed BOOLEAN NOT NULL,
                       last_login_at DATETIME,
                       deleted_at DATETIME,
                       profile_id BIGINT,
                       created_at DATETIME NOT NULL,
                       updated_at DATETIME NOT NULL,
                       CONSTRAINT uq_users_user_id UNIQUE (user_id),
                       CONSTRAINT fk_users_profile_img FOREIGN KEY (profile_id) REFERENCES profile_img(id)
);