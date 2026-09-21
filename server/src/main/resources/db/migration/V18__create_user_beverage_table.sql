CREATE TABLE user_beverage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    beverage_id BIGINT NOT NULL,
    acquisition_type VARCHAR(20) NOT NULL,
    acquired_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_beverage_user_beverage UNIQUE (user_id, beverage_id),
    CONSTRAINT fk_user_beverage_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_beverage_beverage FOREIGN KEY (beverage_id) REFERENCES beverage(id)
);

INSERT INTO user_beverage (
    user_id,
    beverage_id,
    acquisition_type,
    acquired_at,
    created_at,
    updated_at
)
SELECT users.id,
       beverage.id,
       'DEFAULT',
       users.created_at,
       CURRENT_TIMESTAMP,
       CURRENT_TIMESTAMP
FROM users
JOIN beverage ON beverage.is_default = TRUE
WHERE users.user_status = 'ACTIVE';
