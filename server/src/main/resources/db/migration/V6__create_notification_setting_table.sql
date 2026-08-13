CREATE TABLE notification_setting (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      user_id BIGINT NOT NULL,
                                      focus_start_enabled BOOLEAN NOT NULL,
                                      focus_end_enabled BOOLEAN NOT NULL,
                                      break_end_enabled BOOLEAN NOT NULL,
                                      created_at DATETIME NOT NULL,
                                      updated_at DATETIME NOT NULL,
                                      CONSTRAINT uq_notification_setting_user_id UNIQUE (user_id)
);