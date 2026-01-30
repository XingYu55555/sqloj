-- 示例题目数据
-- 前提：已经在sqloj_test中创建了employees表并插入了数据

USE `sqloj_biz`;

-- 示例题目1：查询所有IT部门的员工
INSERT INTO `problem` (`title`, `description`, `teacher_id`, `std_sql`, `std_headers`, `std_data`, `difficulty`) VALUES
(
  '查询IT部门员工',
  '编写SQL查询语句，查询所有IT部门的员工信息，包括id、name、salary字段。结果按照salary降序排列。',
  2,
  'SELECT id, name, salary FROM employees WHERE department = ''IT'' ORDER BY salary DESC',
  'id,name,salary',
  '[[5,"吴十",10000.00],[3,"王五",9000.00],[5,"钱七",8500.00],[1,"张三",8000.00]]',
  'EASY'
);

-- 示例题目2：统计各部门平均工资
INSERT INTO `problem` (`title`, `description`, `teacher_id`, `std_sql`, `std_headers`, `std_data`, `difficulty`) VALUES
(
  '统计部门平均工资',
  '编写SQL查询语句，统计各部门的平均工资，返回department和avg_salary两个字段。结果按照平均工资降序排列。',
  2,
  'SELECT department, AVG(salary) as avg_salary FROM employees GROUP BY department ORDER BY avg_salary DESC',
  'department,avg_salary',
  '[["IT",8875.00],["Finance",7250.00],["HR",5750.00]]',
  'MEDIUM'
);

-- 示例题目3：查询高薪员工
INSERT INTO `problem` (`title`, `description`, `teacher_id`, `std_sql`, `std_headers`, `std_data`, `difficulty`) VALUES
(
  '查询高薪员工',
  '编写SQL查询语句，查询工资大于8000的员工姓名和工资。',
  2,
  'SELECT name, salary FROM employees WHERE salary > 8000',
  'name,salary',
  '[["王五",9000.00],["钱七",8500.00],["吴十",10000.00]]',
  'EASY'
);

-- 示例题目4：查询员工数量
INSERT INTO `problem` (`title`, `description`, `teacher_id`, `std_sql`, `std_headers`, `std_data`, `difficulty`) VALUES
(
  '统计员工数量',
  '编写SQL查询语句，统计公司总共有多少名员工。',
  2,
  'SELECT COUNT(*) as total FROM employees',
  'total',
  '[[8]]',
  'EASY'
);

-- 示例题目5：复杂查询 - 子查询
INSERT INTO `problem` (`title`, `description`, `teacher_id`, `std_sql`, `std_headers`, `std_data`, `difficulty`) VALUES
(
  '查询高于平均工资的员工',
  '编写SQL查询语句，查询工资高于全公司平均工资的员工姓名、部门和工资。',
  2,
  'SELECT name, department, salary FROM employees WHERE salary > (SELECT AVG(salary) FROM employees)',
  'name,department,salary',
  '[["王五","IT",9000.00],["钱七","IT",8500.00],["吴十","IT",10000.00]]',
  'HARD'
);

-- 注意：std_data字段存储的是JSON字符串，实际使用时需要根据真实查询结果调整
