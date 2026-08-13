CREATE TABLE profile_img (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(50) NOT NULL,
                             img_url VARCHAR(255) NOT NULL,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,
                             CONSTRAINT uq_profile_img_name UNIQUE (name)
);