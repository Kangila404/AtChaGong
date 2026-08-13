CREATE TABLE focus_record (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              beverage_id BIGINT,
                              focus_minutes INT NOT NULL,
                              focused_seconds INT NOT NULL,
                              started_at DATETIME NOT NULL,
                              completed_at DATETIME NOT NULL,
                              focused_date DATE NOT NULL,
                              cycle_count INT NOT NULL,
                              created_at DATETIME NOT NULL,
                              updated_at DATETIME NOT NULL,
                              CONSTRAINT fk_focus_record_beverage FOREIGN KEY (beverage_id) REFERENCES beverage(id)
);