ALTER TABLE auth_account
    MODIFY COLUMN provider VARCHAR(20) NOT NULL;

UPDATE auth_account SET provider = CASE provider
                                       WHEN '0' THEN 'GOOGLE'
                                       WHEN '1' THEN 'APPLE'
                                       WHEN '2' THEN 'KAKAO'
                                       ELSE provider
    END;