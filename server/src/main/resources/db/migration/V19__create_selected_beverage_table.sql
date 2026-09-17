ALTER TABLE user_beverage
    ADD CONSTRAINT uk_user_beverage_user_id_id UNIQUE (user_id, id);

CREATE TABLE selected_beverage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    user_beverage_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_selected_beverage_user UNIQUE (user_id),
    CONSTRAINT uk_selected_beverage_user_beverage UNIQUE (user_beverage_id),
    CONSTRAINT fk_selected_beverage_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_selected_beverage_ownership
        FOREIGN KEY (user_id, user_beverage_id)
        REFERENCES user_beverage (user_id, id)
);

INSERT INTO user_beverage (
    user_id,
    beverage_id,
    acquisition_type,
    acquired_at,
    created_at,
    updated_at
)
SELECT timer_setting.user_id,
       timer_setting.beverage_id,
       CASE WHEN beverage.is_default = TRUE THEN 'DEFAULT' ELSE 'ADMIN' END,
       timer_setting.created_at,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM timer_setting
JOIN users ON users.id = timer_setting.user_id
JOIN beverage ON beverage.id = timer_setting.beverage_id
LEFT JOIN user_beverage
       ON user_beverage.user_id = timer_setting.user_id
      AND user_beverage.beverage_id = timer_setting.beverage_id
WHERE users.user_status = 'ACTIVE'
  AND user_beverage.id IS NULL;

INSERT INTO selected_beverage (user_id, user_beverage_id, created_at, updated_at)
SELECT timer_setting.user_id,
       user_beverage.id,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM timer_setting
JOIN users ON users.id = timer_setting.user_id
JOIN user_beverage
     ON user_beverage.user_id = timer_setting.user_id
    AND user_beverage.beverage_id = timer_setting.beverage_id
WHERE users.user_status = 'ACTIVE';

INSERT INTO selected_beverage (user_id, user_beverage_id, created_at, updated_at)
SELECT users.id,
       user_beverage.id,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM users
JOIN user_beverage ON user_beverage.user_id = users.id
JOIN beverage
     ON beverage.id = user_beverage.beverage_id
    AND beverage.is_default = TRUE
LEFT JOIN selected_beverage ON selected_beverage.user_id = users.id
WHERE users.user_status = 'ACTIVE'
  AND selected_beverage.id IS NULL;
