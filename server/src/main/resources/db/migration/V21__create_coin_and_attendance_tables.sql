CREATE TABLE user_coin_balance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_coin_balance_user UNIQUE (user_id),
    CONSTRAINT fk_user_coin_balance_user FOREIGN KEY (user_id) REFERENCES users(id)
);

INSERT INTO user_coin_balance (user_id, balance, created_at, updated_at)
SELECT id, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM users
WHERE user_status = 'ACTIVE';

CREATE TABLE coin_transaction (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    reference_type VARCHAR(30) NOT NULL,
    reference_id BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_coin_transaction_source UNIQUE (user_id, transaction_type, reference_type, reference_id),
    CONSTRAINT fk_coin_transaction_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE attendance_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    consecutive_day INT NOT NULL,
    granted_coin BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_attendance_user_date UNIQUE (user_id, attendance_date),
    CONSTRAINT fk_attendance_record_user FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE focus_record
    ADD COLUMN break_minutes INT NOT NULL DEFAULT 5 AFTER focus_minutes,
    ADD COLUMN cycle_count INT NOT NULL DEFAULT 4 AFTER break_minutes;
