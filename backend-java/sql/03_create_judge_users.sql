-- 创建判题专用账号

-- 1. 只读账号（用于查询题型）
CREATE USER IF NOT EXISTS 'judge_read'@'%' IDENTIFIED BY 'judge123';
GRANT SELECT ON sqloj_test.* TO 'judge_read'@'%';
FLUSH PRIVILEGES;

-- 2. 临时写账号（用于增删改题型，使用事务回滚）
CREATE USER IF NOT EXISTS 'judge_write'@'%' IDENTIFIED BY 'judge456';
GRANT SELECT, INSERT, UPDATE ON sqloj_test.* TO 'judge_write'@'%';
FLUSH PRIVILEGES;
