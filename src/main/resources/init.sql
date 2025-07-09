CREATE DATABASE IF NOT EXISTS one_plus_one;

use one_plus_one;

CREATE TABLE IF NOT EXISTS `users` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '유저 고유 식별자',
                                       `login_id` VARCHAR(20) NOT NULL UNIQUE COMMENT '로그인 아이디',
                                       `nickname` VARCHAR(20) NOT NULL COMMENT '닉네임',
                                       `password` VARCHAR(255) NOT NULL COMMENT '비밀번호',
                                       `user_role` ENUM('SELLER', 'BUYER') NOT NULL COMMENT '유저 역할'
);

CREATE TABLE IF NOT EXISTS `products` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '상품 고유 식별자',
                                          `seller_id` BIGINT NOT NULL COMMENT '판매자 ID',
                                          `name` VARCHAR(30) NOT NULL COMMENT '상품명',
                                          `type` VARCHAR(255) NOT NULL COMMENT '상품 종류',
                                          `price` BIGINT NOT NULL COMMENT '상품 가격',
                                          `quantity` BIGINT NOT NULL COMMENT '상품 수량',
                                          FOREIGN KEY (`seller_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `orders` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '주문 고유 식별자',
                                        `user_id` BIGINT NOT NULL COMMENT '구매자 ID',
                                        `product_id` BIGINT NOT NULL COMMENT '상품 ID',
                                        `quantity` BIGINT NOT NULL COMMENT '구매 수량',
                                        FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
                                        FOREIGN KEY (`product_id`) REFERENCES `products`(`id`)
);