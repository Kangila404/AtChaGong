-- 앱의 PROFILE_IMAGES 목록과 파일명을 일치시킨다.
-- 기존 기본 프로필(id=1)은 신규 가입의 기본값으로 계속 사용된다.
UPDATE profile_img
SET name = '곰',
    img_url = 'Bear.png',
    updated_at = NOW()
WHERE id = 1;

INSERT INTO profile_img (name, img_url, created_at, updated_at)
SELECT '벨루가', 'Beluga.png', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM profile_img WHERE img_url = 'Beluga.png'
);

INSERT INTO profile_img (name, img_url, created_at, updated_at)
SELECT '토끼', 'Rabbit.png', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM profile_img WHERE img_url = 'Rabbit.png'
);

INSERT INTO profile_img (name, img_url, created_at, updated_at)
SELECT '물범', 'Seal.png', NOW(), NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM profile_img WHERE img_url = 'Seal.png'
);
