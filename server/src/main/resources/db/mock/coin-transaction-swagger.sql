START TRANSACTION;

INSERT INTO users (
    user_id, nickname, user_status, user_role, onboarding_completed,
    last_login_at, created_at, updated_at
) VALUES
    ('mock-admin', 'Swagger Admin', 'ACTIVE', 'ADMIN', TRUE, NOW(), NOW(), NOW()),
    ('mock-coin-user', 'Coin Test User', 'ACTIVE', 'USER', TRUE, NOW(), NOW(), NOW())
ON DUPLICATE KEY UPDATE
    nickname = VALUES(nickname),
    user_status = VALUES(user_status),
    user_role = VALUES(user_role),
    onboarding_completed = VALUES(onboarding_completed),
    last_login_at = VALUES(last_login_at),
    updated_at = NOW();

INSERT INTO user_coin_balance (user_id, balance, created_at, updated_at)
SELECT id, 46, NOW(), NOW()
FROM users
WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE
    balance = VALUES(balance),
    updated_at = NOW();

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, 10, 'ATTENDANCE', 'ATTENDANCE', 900001, 10, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, 3, 'FOCUS_COMPLETION', 'FOCUS_RECORD', 900002, 13, NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, 10, 'ATTENDANCE', 'ATTENDANCE', 900003, 23, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, 70, 'ATTENDANCE', 'ATTENDANCE', 900004, 93, NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, 3, 'FOCUS_COMPLETION', 'FOCUS_RECORD', 900005, 96, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

INSERT INTO coin_transaction (
    user_id, amount, transaction_type, reference_type, reference_id,
    balance_after, created_at, updated_at
)
SELECT id, -50, 'BEVERAGE_PURCHASE', 'BEVERAGE', 900006, 46, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY
FROM users WHERE user_id = 'mock-coin-user'
ON DUPLICATE KEY UPDATE amount = VALUES(amount), balance_after = VALUES(balance_after);

COMMIT;
