ALTER TABLE device_token
    ADD CONSTRAINT fk_device_token_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE focus_record
    ADD CONSTRAINT fk_focus_record_user FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE focus_record
    ADD CONSTRAINT uq_focus_record_user_started UNIQUE (user_id, started_at);

ALTER TABLE timer_setting
    ADD CONSTRAINT fk_timer_setting_user FOREIGN KEY (user_id) REFERENCES users(id);