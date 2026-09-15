ALTER TABLE timer_setting
    DROP FOREIGN KEY fk_timer_setting_beverage;

ALTER TABLE timer_setting
    DROP COLUMN beverage_id;
