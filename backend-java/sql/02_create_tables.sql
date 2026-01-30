-- 使用业务数据库
USE `sqloj_biz`;

-- 表1：user（用户表-师生/管理员角色）
CREATE TABLE IF NOT EXISTS `user` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '用户主键ID，前端用于关联提交/出题',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名（登录账号，前端输入）',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt加密后的密码（前端不传明文，后端校验）',
  `role` ENUM('STUDENT','TEACHER','ADMIN') NOT NULL COMMENT '角色（前端识别：学生/老师/管理员，权限控制依据）',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱（前端可选填）',
  `is_delete` TINYINT(1) DEFAULT 0 COMMENT '软删除：0-未删 1-已删（前端不展示已删用户）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（前端展示用户注册时间）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台用户表（支撑登录、角色权限核心功能）';

-- 表2：problem（题目表-老师发布题目）
CREATE TABLE IF NOT EXISTS `problem` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '题目主键ID，前端提交SQL时关联',
  `title` VARCHAR(200) NOT NULL COMMENT '题目名称（前端展示题目列表）',
  `description` TEXT NOT NULL COMMENT '题目描述（前端展示，告知学生做题要求）',
  `teacher_id` INT UNSIGNED NOT NULL COMMENT '出题老师ID（关联user表，前端展示出题人）',
  `std_sql` TEXT NOT NULL COMMENT '标准答案SQL（后端判题比对基准，前端不展示）',
  `std_headers` VARCHAR(500) NOT NULL COMMENT '标准结果字段名（逗号分隔，如id,name,age，前端用于结果展示表头）',
  `std_data` TEXT NOT NULL COMMENT '标准结果集（JSON字符串格式，后端判题比对用，前端展示正确结果参考）',
  `difficulty` ENUM('EASY','MEDIUM','HARD') DEFAULT 'EASY' COMMENT '题目难度（前端展示难度标签）',
  `is_delete` TINYINT(1) DEFAULT 0 COMMENT '软删除：0-未删 1-已删（前端不展示已删题目）',
  INDEX idx_teacher_id (teacher_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目核心表（支撑老师出题、学生做题核心功能）';

-- 外键关联老师
ALTER TABLE `problem` ADD CONSTRAINT fk_problem_teacher FOREIGN KEY (teacher_id) REFERENCES user(id) ON DELETE CASCADE;

-- 表3：submission（提交记录表-学生提交SQL）
CREATE TABLE IF NOT EXISTS `submission` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '提交记录主键ID，前端轮询查判题结果用',
  `student_id` INT UNSIGNED NOT NULL COMMENT '提交学生ID（关联user表，前端关联学生提交记录）',
  `problem_id` INT UNSIGNED NOT NULL COMMENT '对应题目ID（关联problem表，前端关联题目）',
  `sql_content` TEXT NOT NULL COMMENT '学生提交的SQL内容（前端展示历史提交的SQL）',
  `judge_status` ENUM('PENDING','AC','WA','TLE','RE') DEFAULT 'PENDING' COMMENT '判题状态（前端核心展示：待判/正确/答案错/超时/运行错）',
  `running_time` INT DEFAULT 0 COMMENT 'SQL执行时间（毫秒，前端展示耗时）',
  `submit_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间（前端展示提交顺序）',
  `is_delete` TINYINT(1) DEFAULT 0 COMMENT '软删除：0-未删 1-已删（前端不展示已删记录）',
  INDEX idx_student_id (student_id),
  INDEX idx_problem_id (problem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生SQL提交记录表（支撑提交、判题状态查询功能）';

-- 外键关联
ALTER TABLE `submission` ADD CONSTRAINT fk_sub_student FOREIGN KEY (student_id) REFERENCES user(id) ON DELETE CASCADE;
ALTER TABLE `submission` ADD CONSTRAINT fk_sub_problem FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE;

-- 表4：test_case（题目测试用例表-单题多测试用例）
CREATE TABLE IF NOT EXISTS `test_case` (
  `id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '测试用例主键ID',
  `problem_id` INT UNSIGNED NOT NULL COMMENT '关联题目ID（关联problem表，1题可对应多测试用例）',
  `output_data` TEXT NOT NULL COMMENT '单测试用例标准输出（JSON格式，后端判题比对用，前端展示单例结果）',
  `is_public` TINYINT(1) DEFAULT 0 COMMENT '是否公开（0-仅老师看 1-学生看，前端控制展示权限）',
  INDEX idx_problem_id (problem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='题目多测试用例表（细化判题，支撑前端展示单例对错）';

-- 外键关联题目
ALTER TABLE `test_case` ADD CONSTRAINT fk_case_problem FOREIGN KEY (problem_id) REFERENCES problem(id) ON DELETE CASCADE;

-- 表5：judge_detail（判题详情表-展示错误原因）
CREATE TABLE IF NOT EXISTS `judge_detail` (
  `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '判题详情主键ID',
  `submission_id` BIGINT UNSIGNED NOT NULL COMMENT '关联提交记录ID（关联submission表）',
  `lack_num` INT DEFAULT 0 COMMENT '缺失行数（前端展示：学生结果比标准少多少行）',
  `err_num` INT DEFAULT 0 COMMENT '错误行数（前端展示：学生结果错误行数）',
  `actual_output` TEXT COMMENT '学生SQL实际输出（JSON格式，前端展示，方便学生对比纠错）',
  INDEX idx_submission_id (submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='判题详情表（支撑前端展示错误原因，辅助学生纠错）';

-- 外键关联提交记录
ALTER TABLE `judge_detail` ADD CONSTRAINT fk_detail_sub FOREIGN KEY (submission_id) REFERENCES submission(id) ON DELETE CASCADE;
