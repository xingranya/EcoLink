-- 修复管理员密码格式（使用 noop 明文编码，与 demo 用户一致）
UPDATE users SET password_hash = '{noop}admin123' WHERE username = 'admin';
