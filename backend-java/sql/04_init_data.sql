-- 初始化数据
USE `sqloj_biz`;

-- 插入默认用户（密码为BCrypt加密后的值，明文为：admin123）
-- BCrypt加密：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi
INSERT INTO `user` (`username`, `password`, `role`, `email`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'ADMIN', 'admin@sqloj.com'),
('teacher1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'TEACHER', 'teacher1@sqloj.com'),
('student1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'STUDENT', 'student1@sqloj.com');

-- 插入测试题目（需要先确保sqloj_test库中有employees表）
-- 注意：实际使用时需要根据sqloj_test库中的表结构来设置std_sql和std_data
