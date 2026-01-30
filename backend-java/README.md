# SQLOJ Java后端实现

## 项目简介

本项目是数据库课程在线实践平台的Java后端实现，使用Spring Boot + MyBatis-Plus技术栈，完全兼容原有Vue前端，无需修改前端代码。

## 技术栈

- **框架**: Spring Boot 2.7.18
- **持久层**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **连接池**: Druid 1.2.16
- **安全**: Spring Security
- **工具库**: Lombok, Hutool, FastJSON
- **JDK**: 1.8+

## 项目结构

```
backend-java/
├── sql/                          # 数据库脚本
│   ├── 01_create_database.sql    # 创建数据库
│   ├── 02_create_tables.sql      # 创建表结构
│   ├── 03_create_judge_users.sql # 创建判题用户
│   └── 04_init_data.sql          # 初始化数据
├── src/main/java/com/sqloj/
│   ├── config/                   # 配置类
│   │   ├── CorsConfig.java       # 跨域配置
│   │   ├── SecurityConfig.java   # 安全配置
│   │   └── ThreadPoolConfig.java # 线程池配置
│   ├── controller/               # 控制器层
│   │   ├── UserController.java   # 用户接口
│   │   ├── ProblemController.java# 题目接口
│   │   └── SubmitController.java # 提交接口
│   ├── entity/                   # 实体类
│   │   ├── User.java
│   │   ├── Problem.java
│   │   ├── Submission.java
│   │   ├── TestCase.java
│   │   └── JudgeDetail.java
│   ├── mapper/                   # 数据访问层
│   │   ├── UserMapper.java
│   │   ├── ProblemMapper.java
│   │   ├── SubmissionMapper.java
│   │   ├── TestCaseMapper.java
│   │   └── JudgeDetailMapper.java
│   ├── service/                  # 服务层
│   │   ├── UserService.java
│   │   ├── ProblemService.java
│   │   ├── SubmissionService.java
│   │   └── JudgeService.java     # 核心判题逻辑
│   ├── util/                     # 工具类
│   │   ├── Result.java           # 统一返回结果
│   │   ├── JdbcUtil.java         # JDBC工具
│   │   └── NormalizeUtil.java    # 结果标准化工具
│   ├── vo/                       # 视图对象
│   │   ├── UserLoginVO.java
│   │   ├── ProblemListVO.java
│   │   ├── ProblemDetailVO.java
│   │   ├── SubmitResultVO.java
│   │   ├── JudgeResultVO.java
│   │   └── SubmissionHistoryVO.java
│   └── SqlojApplication.java     # 启动类
├── src/main/resources/
│   └── application.yml           # 应用配置
└── pom.xml                       # Maven配置
```

## 数据库设计

### 业务数据库 (sqloj_biz)

包含5张核心业务表：

1. **user** - 用户表（学生、老师、管理员）
2. **problem** - 题目表
3. **submission** - 提交记录表
4. **test_case** - 测试用例表
5. **judge_detail** - 判题详情表

### 判题数据库 (sqloj_test)

存储测试业务表（如employees、students等），由老师上传/导入。

### 判题账号

- **judge_read** - 只读账号（用于SELECT查询）
- **judge_write** - 临时写账号（用于INSERT/UPDATE/DELETE，使用事务回滚）

## 部署步骤

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库初始化

按顺序执行sql目录下的脚本：

```bash
# 1. 创建数据库
mysql -u root -p < sql/01_create_database.sql

# 2. 创建表结构
mysql -u root -p < sql/02_create_tables.sql

# 3. 创建判题用户
mysql -u root -p < sql/03_create_judge_users.sql

# 4. 初始化数据（可选）
mysql -u root -p < sql/04_init_data.sql
```

### 3. 配置修改

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sqloj_biz?...
    username: root
    password: your_password

judge:
  datasource:
    read:
      url: jdbc:mysql://localhost:3306/sqloj_test?...
      username: judge_read
      password: judge123
    write:
      url: jdbc:mysql://localhost:3306/sqloj_test?...
      username: judge_write
      password: judge456
```

### 4. 编译打包

```bash
cd backend-java
mvn clean package
```

### 5. 运行

```bash
java -jar target/sqloj-backend-1.0.0.jar
```

服务将在 http://localhost:8080 启动

## API接口

### 用户模块

- `POST /api/user/login` - 用户登录
- `GET /api/user/info?userId={id}` - 获取用户信息

### 题目模块

- `GET /api/problem/list` - 查询所有题目
- `GET /api/problem/detail/{id}` - 查询题目详情
- `POST /api/problem/add` - 新增题目（仅老师）
- `PUT /api/problem/edit` - 编辑题目（仅老师）
- `DELETE /api/problem/delete/{id}` - 删除题目（仅老师）

### 提交模块

- `POST /api/submit/sql` - 提交SQL
- `GET /api/submit/result/{submissionId}` - 查询判题结果
- `GET /api/submit/history/{studentId}` - 查询提交历史

## 核心特性

### 1. 判题机制

- **独立连接**: 每个判题任务使用独立的数据库连接
- **事务回滚**: 增删改操作强制回滚，防止数据污染
- **超时控制**: 默认3秒超时，可配置
- **并发控制**: 线程池管理，默认核心5线程，最大10线程

### 2. 结果标准化

- 字段名按字母序排序，消除顺序差异
- 统一数据类型比较
- 精确计算缺失行数和错误行数

### 3. 安全性

- BCrypt密码加密
- SQL注入防护（PreparedStatement）
- 权限隔离（只读/写账号分离）
- 跨域配置

## 默认账号

初始化后的默认账号（密码均为 admin123）：

- 管理员: admin / admin123
- 老师: teacher1 / admin123
- 学生: student1 / admin123

## 前端对接

前端只需修改API基准地址即可：

```javascript
// 修改前端配置文件中的API地址
const API_BASE_URL = 'http://localhost:8080/api';
```

## 注意事项

1. **数据库字符集**: 必须使用 utf8mb4，支持中文和特殊字符
2. **判题数据库**: sqloj_test 需要预先导入测试表
3. **连接池配置**: 根据实际并发量调整连接池大小
4. **超时时间**: 复杂SQL可能需要增加超时时间
5. **日志级别**: 生产环境建议调整为INFO或WARN

## 开发说明

### 添加新功能

1. 在entity包中创建实体类
2. 在mapper包中创建Mapper接口
3. 在service包中实现业务逻辑
4. 在controller包中暴露API接口
5. 在vo包中定义返回对象

### 自定义配置

所有可配置项在 application.yml 中：

- 服务器端口
- 数据库连接
- 判题超时时间
- 线程池大小
- 日志级别

## 故障排查

### 常见问题

1. **数据库连接失败**: 检查MySQL服务是否启动，账号密码是否正确
2. **判题一直PENDING**: 检查线程池配置，查看后台日志
3. **跨域错误**: 确认CorsConfig配置正确
4. **SQL执行报错**: 检查sqloj_test数据库是否有测试表

### 日志查看

```bash
# 查看实时日志
tail -f logs/spring.log
```

## 性能优化

1. 调整数据库连接池大小
2. 增加判题线程池容量
3. 开启MyBatis二级缓存
4. 使用Redis缓存热点数据
5. 数据库索引优化

## 许可证

MIT License
