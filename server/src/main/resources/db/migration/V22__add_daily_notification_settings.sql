ALTER TABLE notification_setting
    ADD COLUMN seasonal_beverage_enabled BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE daily_notification_setting (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_time TIME NOT NULL,
    enabled BOOLEAN NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_notification_setting_user UNIQUE (user_id),
    CONSTRAINT fk_daily_notification_setting_user FOREIGN KEY (user_id) REFERENCES users(id)
);
