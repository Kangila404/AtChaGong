-- Flyway가 트랜잭션을 관리하므로 START TRANSACTION / COMMIT은 사용하지 않는다.

-- 기본 얼음컵: 기존 마이그레이션 기준 id=1
UPDATE beverage
SET price = 0,
    display_order = 1,
    sale_status = 'ON_SALE',
    is_default = TRUE,
    sale_ends_at = NULL
WHERE id = 1;

-- 에이드 3종을 추가한다. 이미 존재하면 INSERT는 생략하고 아래 UPDATE로 판매 상태를 맞춘다.
INSERT INTO beverage (
    name, img_url, price, display_order,
    sale_status, is_default, sale_ends_at
)
SELECT
    '레몬 에이드', '', 200, 2,
    'ON_SALE', FALSE, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM beverage WHERE name = '레몬 에이드'
);

INSERT INTO beverage (
    name, img_url, price, display_order,
    sale_status, is_default, sale_ends_at
)
SELECT
    '자몽 에이드', '', 200, 3,
    'ON_SALE', FALSE, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM beverage WHERE name = '자몽 에이드'
);

INSERT INTO beverage (
    name, img_url, price, display_order,
    sale_status, is_default, sale_ends_at
)
SELECT
    '청포도 에이드', '', 200, 4,
    'ON_SALE', FALSE, NULL
WHERE NOT EXISTS (
    SELECT 1 FROM beverage WHERE name = '청포도 에이드'
);

-- 재실행/기존 데이터에도 동일한 판매 설정을 적용한다.
UPDATE beverage
SET price = 200,
    display_order = CASE name
        WHEN '레몬 에이드' THEN 2
        WHEN '자몽 에이드' THEN 3
        WHEN '청포도 에이드' THEN 4
    END,
    sale_status = 'ON_SALE',
    is_default = FALSE,
    sale_ends_at = NULL
WHERE name IN ('레몬 에이드', '자몽 에이드', '청포도 에이드');
