INSERT INTO users (
    user_id,
    nickname,
    user_status,
    user_role,
    onboarding_completed,
    last_login_at,
    deleted_at,
    profile_id,
    created_at,
    updated_at
)
SELECT
    'app-review',
    'App Review',
    'ACTIVE',
    'USER',
    TRUE,
    NULL,
    NULL,
    1,
    NOW(),
    NOW()
WHERE NOT EXISTS (
    SELECT 1
    FROM users
    WHERE user_id = 'app-review'
);
