-- 测试数据：员工表
-- 用于测试SQL查询判题功能

USE `sqloj_test`;

-- 创建员工表
CREATE TABLE IF NOT EXISTS `employees` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `department` VARCHAR(50),
  `salary` DECIMAL(10, 2),
  `hire_date` DATE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试数据
INSERT INTO `employees` (`name`, `department`, `salary`, `hire_date`) VALUES
('张三', 'IT', 8000.00, '2020-01-15'),
('李四', 'HR', 6000.00, '2019-05-20'),
('王五', 'IT', 9000.00, '2021-03-10'),
('赵六', 'Finance', 7000.00, '2020-08-25'),
('钱七', 'IT', 8500.00, '2021-11-01'),
('孙八', 'HR', 5500.00, '2022-02-14'),
('周九', 'Finance', 7500.00, '2019-12-01'),
('吴十', 'IT', 10000.00, '2018-06-15');

-- 创建学生表
CREATE TABLE IF NOT EXISTS `students` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `age` INT,
  `grade` VARCHAR(20),
  `score` INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入学生测试数据
INSERT INTO `students` (`name`, `age`, `grade`, `score`) VALUES
('小明', 18, '一年级', 85),
('小红', 19, '二年级', 90),
('小刚', 18, '一年级', 78),
('小丽', 20, '三年级', 92),
('小华', 19, '二年级', 88),
('小芳', 18, '一年级', 95),
('小强', 20, '三年级', 82),
('小梅', 19, '二年级', 87);

-- 创建商品表
CREATE TABLE IF NOT EXISTS `products` (
  `id` INT PRIMARY KEY AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `category` VARCHAR(50),
  `price` DECIMAL(10, 2),
  `stock` INT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入商品测试数据
INSERT INTO `products` (`name`, `category`, `price`, `stock`) VALUES
('笔记本电脑', '电子产品', 5999.00, 50),
('鼠标', '电子产品', 99.00, 200),
('键盘', '电子产品', 299.00, 150),
('显示器', '电子产品', 1299.00, 80),
('耳机', '电子产品', 199.00, 300),
('书包', '文具', 89.00, 100),
('笔记本', '文具', 15.00, 500),
('钢笔', '文具', 25.00, 200);
