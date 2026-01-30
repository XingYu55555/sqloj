# SQLOJ - 数据库课程在线实践平台 (Java后端版本)

<p align="center">
  <img src="../doc/images/logo.png" alt="SQLOJ Logo" width="200"/>
</p>

<p align="center">
  <img alt="Version" src="https://img.shields.io/badge/version-1.0.0--java-blue.svg" />
  <img alt="Java" src="https://img.shields.io/badge/Java-1.8+-orange.svg" />
  <img alt="Spring Boot" src="https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg" />
  <img alt="MySQL" src="https://img.shields.io/badge/MySQL-8.0+-blue.svg" />
  <img alt="License" src="https://img.shields.io/badge/License-MIT-yellow.svg" />
</p>

> 一个轻量级的数据库系统实践平台，集成SQL在线判题功能，采用前后端分离架构。本版本使用Java Spring Boot实现后端，完全兼容原有Vue前端。

## 🎯 项目特点

- ✅ **前端零修改**：复用原项目Vue前端，仅需修改API基准地址
- ✅ **Java后端**：Spring Boot + MyBatis-Plus，易维护、易扩展
- ✅ **精准判题**：独立连接+事务回滚+结果标准化，确保判题准确性
- ✅ **高并发支持**：线程池+连接池，支持多用户同时在线判题
- ✅ **数据隔离**：业务数据库与测试数据库分离，互不干扰
- ✅ **安全可靠**：BCrypt密码加密+权限控制+SQL注入防护

## 📋 目录结构

```
sqloj/
├── app/                          # Vue前端（原项目，保持不变）
├── backend/                      # Python后端（原项目）
├── backend-java/                 # Java Spring Boot后端（新增）★
│   ├── sql/                      # 数据库脚本
│   │   ├── 01_create_database.sql
│   │   ├── 02_create_tables.sql
│   │   ├── 03_create_judge_users.sql
│   │   ├── 04_init_data.sql
│   │   ├── 05_test_data.sql
│   │   └── 06_sample_problems.sql
│   ├── src/main/
│   │   ├── java/com/sqloj/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── entity/          # 实体类
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── service/         # 服务层
│   │   │   ├── util/            # 工具类
│   │   │   └── vo/              # 视图对象
│   │   └── resources/
│   │       └── application.yml
│   ├── pom.xml
│   ├── README.md
│   ├── DEPLOYMENT.md            # 部署指南
│   ├── API_TEST.md              # API测试文档
│   └── FRONTEND_INTEGRATION.md  # 前后端对接指南
├── doc/                         # 文档目录
└── README.md
```

## 🚀 快速开始

### 环境要求

- **JDK**: 1.8 或更高版本
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Node.js**: 14+ (仅前端需要)

### 1. 克隆项目

```bash
git clone https://github.com/XingYu55555/sqloj.git
cd sqloj
```

### 2. 初始化数据库

```bash
# 登录MySQL
mysql -u root -p

# 按顺序执行SQL脚本
source backend-java/sql/01_create_database.sql
source backend-java/sql/02_create_tables.sql
source backend-java/sql/03_create_judge_users.sql
source backend-java/sql/04_init_data.sql
source backend-java/sql/05_test_data.sql
source backend-java/sql/06_sample_problems.sql
```

### 3. 配置后端

编辑 `backend-java/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sqloj_biz?...
    username: root
    password: YOUR_PASSWORD
```

### 4. 启动后端

```bash
cd backend-java
mvn clean package
java -jar target/sqloj-backend-1.0.0.jar
```

后端将在 http://localhost:8080 启动

### 5. 启动前端（可选）

```bash
cd app
npm install
npm run serve
```

前端将在 http://localhost:3000 启动

### 6. 访问系统

打开浏览器访问 http://localhost:3000

**默认账号**（密码均为 admin123）：
- 管理员: `admin`
- 老师: `teacher1`
- 学生: `student1`

## 📚 核心功能

### 学生端

- 📝 **在线答题**：浏览题目列表，查看题目详情，在线编写SQL
- ⚡ **实时判题**：提交后自动判题，3秒内返回结果
- 📊 **结果详情**：查看判题状态（AC/WA/TLE/RE）、运行时间、错误详情
- 📜 **历史记录**：查看所有提交记录，支持按题目、状态筛选

### 教师端

- ➕ **题目管理**：新增、编辑、删除题目
- 🎯 **难度设置**：支持简单、中等、困难三个难度级别
- 📋 **标准答案**：设置标准SQL和预期结果
- 👥 **学生管理**：查看学生提交情况和正确率统计

### 判题系统

- 🔒 **数据隔离**：独立数据库连接，每个判题任务互不干扰
- 🔄 **事务回滚**：增删改操作自动回滚，测试数据永不污染
- ⏱️ **超时控制**：默认3秒超时，防止死循环或慢查询
- 📏 **结果标准化**：自动处理字段顺序差异，精确比对结果

## 🗄️ 数据库设计

### 业务数据库 (sqloj_biz)

- **user**: 用户表（学生、老师、管理员）
- **problem**: 题目表
- **submission**: 提交记录表
- **test_case**: 测试用例表
- **judge_detail**: 判题详情表

### 判题数据库 (sqloj_test)

- 存储测试业务表（如employees、students等）
- 两个判题专用账号：
  - `judge_read`: 只读权限，用于SELECT查询
  - `judge_write`: 读写权限，用于INSERT/UPDATE/DELETE（带事务回滚）

## 🔧 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **ORM**: MyBatis-Plus 3.5.3.1
- **数据库**: MySQL 8.0
- **连接池**: Druid 1.2.16
- **安全**: Spring Security
- **工具**: Lombok, Hutool, FastJSON

### 前端
- **框架**: Vue.js
- **UI库**: Element UI
- **HTTP**: Axios

## 📖 文档

- [README.md](backend-java/README.md) - 项目说明
- [DEPLOYMENT.md](backend-java/DEPLOYMENT.md) - 部署指南
- [API_TEST.md](backend-java/API_TEST.md) - API测试文档
- [FRONTEND_INTEGRATION.md](backend-java/FRONTEND_INTEGRATION.md) - 前后端对接指南

## 🔌 API接口

所有接口基准路径：`http://localhost:8080/api`

### 用户模块
- `POST /api/user/login` - 用户登录
- `GET /api/user/info?userId={id}` - 获取用户信息

### 题目模块
- `GET /api/problem/list` - 查询所有题目
- `GET /api/problem/detail/{id}` - 查询题目详情
- `POST /api/problem/add` - 新增题目
- `PUT /api/problem/edit` - 编辑题目
- `DELETE /api/problem/delete/{id}` - 删除题目

### 提交模块
- `POST /api/submit/sql` - 提交SQL
- `GET /api/submit/result/{submissionId}` - 查询判题结果
- `GET /api/submit/history/{studentId}` - 查询提交历史

详细API文档请参考 [API_TEST.md](backend-java/API_TEST.md)

## 🎬 使用示例

### 学生提交SQL示例

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student1","password":"admin123"}'

# 2. 查看题目
curl http://localhost:8080/api/problem/list

# 3. 提交SQL
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELECT id, name, salary FROM employees WHERE department = '\''IT'\''"
  }'

# 4. 查询判题结果
curl http://localhost:8080/api/submit/result/1
```

### 判题状态说明

- **PENDING**: 判题中
- **AC** (Accepted): 答案正确
- **WA** (Wrong Answer): 答案错误
- **TLE** (Time Limit Exceeded): 运行超时
- **RE** (Runtime Error): 运行错误（语法错误、权限错误等）

## 🚢 部署

### Docker部署（推荐）

```bash
# 构建镜像
cd backend-java
mvn clean package -DskipTests
docker build -t sqloj-backend:1.0.0 .

# 运行容器
docker run -d \
  --name sqloj-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  sqloj-backend:1.0.0
```

### 传统部署

```bash
# 编译打包
cd backend-java
mvn clean package

# 后台运行
nohup java -jar target/sqloj-backend-1.0.0.jar > logs/app.log 2>&1 &
```

详细部署步骤请参考 [DEPLOYMENT.md](backend-java/DEPLOYMENT.md)

## 🔐 安全性

1. **密码加密**: 使用BCrypt算法加密存储
2. **SQL注入防护**: 使用MyBatis PreparedStatement
3. **权限控制**: Spring Security + 角色权限验证
4. **数据隔离**: 只读/读写账号分离，事务回滚防污染
5. **跨域保护**: CORS配置，仅允许白名单域名

## 🎯 性能优化

- ✅ Druid连接池，支持高并发数据库访问
- ✅ 线程池管理判题任务，避免资源耗尽
- ✅ 数据库索引优化，提升查询性能
- ✅ 异步判题，提交后立即返回，不阻塞用户操作

## 🐛 故障排查

### 常见问题

1. **数据库连接失败**
   - 检查MySQL是否启动
   - 验证application.yml中的配置

2. **判题一直PENDING**
   - 查看后端日志
   - 确认sqloj_test数据库有测试表
   - 验证判题账号权限

3. **跨域错误**
   - 确认后端CorsConfig配置
   - 检查前端API地址配置

详细故障排查请参考文档或查看日志。

## 📈 未来规划

- [ ] 支持多种数据库（PostgreSQL、Oracle等）
- [ ] 添加代码高亮和智能提示
- [ ] 实现题目分类和标签系统
- [ ] 支持比赛模式和排行榜
- [ ] 添加题解和讨论区功能
- [ ] 支持数据库设计题（DDL/DML）
- [ ] 集成AI助手，提供解题提示

## 🤝 贡献

欢迎提交Issue和Pull Request！

1. Fork本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启Pull Request

## 👥 作者

**原项目作者**: [Peter Yin](https://github.com/PTYin)

**Java后端实现**: GitHub Copilot for XingYu55555

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](../LICENSE) 文件

## 🙏 致谢

- 感谢原项目作者 [PTYin](https://github.com/PTYin) 提供优秀的前端和设计
- 感谢所有贡献者的支持和反馈
- 感谢开源社区提供的优秀框架和工具

---

<p align="center">
  Made with ❤️ by <a href="https://github.com/XingYu55555">XingYu55555</a>
</p>

<p align="center">
  ⭐ 如果这个项目对你有帮助，请给我们一个Star！
</p>
