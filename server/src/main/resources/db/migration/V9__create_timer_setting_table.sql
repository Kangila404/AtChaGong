CREATE TABLE timer_setting (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               beverage_id BIGINT NOT NULL,
                               focus_minutes INT NOT NULL,
                               break_minutes INT NOT NULL,
                               cycle_count INT NOT NULL,
                               created_at DATETIME NOT NULL,
                               updated_at DATETIME NOT NULL,
                               CONSTRAINT uq_timer_setting_user_id UNIQUE (user_id),
                               CONSTRAINT fk_timer_setting_beverage FOREIGN KEY (beverage_id) REFERENCES beverage(id)
);