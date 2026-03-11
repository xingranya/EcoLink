-- 添加用户角色字段
ALTER TABLE users ADD COLUMN role VARCHAR(20) NOT NULL DEFAULT 'USER';

-- 插入默认管理员账号（密码: admin123，使用 bcrypt 编码）
INSERT INTO users (username, password_hash, nickname, phone, status, role, created_at, updated_at)
VALUES ('admin', '{bcrypt}$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', '超级管理员', NULL, 'ACTIVE', 'ADMIN', NOW(), NOW())
ON DUPLICATE KEY UPDATE role = 'ADMIN';
