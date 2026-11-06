-- H2数据库表结构
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    password VARCHAR(255),
    nickname VARCHAR(255),
    email VARCHAR(255)
);