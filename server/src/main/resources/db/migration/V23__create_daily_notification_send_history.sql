CREATE TABLE daily_notification_send_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    notification_date DATE NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_daily_notification_send_history UNIQUE (user_id, notification_date),
    CONSTRAINT fk_daily_notification_send_history_user FOREIGN KEY (user_id) REFERENCES users(id)
);
