-- 创建业务数据库
CREATE DATABASE IF NOT EXISTS `sqloj_biz` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建判题测试数据库
CREATE DATABASE IF NOT EXISTS `sqloj_test` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 切换到业务数据库
USE `sqloj_biz`;
