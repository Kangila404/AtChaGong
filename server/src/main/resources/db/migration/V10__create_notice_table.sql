CREATE TABLE notice (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        admin_id BIGINT NOT NULL,
                        title VARCHAR(100) NOT NULL,
                        content TEXT NOT NULL,
                        img_url VARCHAR(512),
                        status VARCHAR(20) NOT NULL,
                        publish_starts_at DATETIME NOT NULL,
                        publish_ends_at DATETIME,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL
);