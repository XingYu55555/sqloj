# 🚀 快速开始指南

> 5分钟快速启动SQLOJ Java后端

## 前置条件

确保已安装以下软件：

- ✅ JDK 1.8 或更高版本
- ✅ Maven 3.6+
- ✅ MySQL 8.0+

## 步骤 1: 初始化数据库 (3分钟)

### 方式A: 使用命令行（推荐）

```bash
# 1. 登录MySQL
mysql -u root -p

# 2. 执行以下命令
source backend-java/sql/01_create_database.sql
source backend-java/sql/02_create_tables.sql
source backend-java/sql/03_create_judge_users.sql
source backend-java/sql/04_init_data.sql
source backend-java/sql/05_test_data.sql
source backend-java/sql/06_sample_problems.sql

# 3. 退出MySQL
exit
```

### 方式B: 使用MySQL Workbench

1. 打开MySQL Workbench
2. 连接到MySQL服务器
3. 依次打开并执行 `backend-java/sql/` 目录下的6个SQL文件

## 步骤 2: 配置数据库连接 (1分钟)

编辑 `backend-java/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    username: root
    password: YOUR_PASSWORD  # 修改为你的MySQL密码
```

## 步骤 3: 启动后端 (1分钟)

### Linux/Mac:
```bash
cd backend-java
./start.sh
```

### Windows:
```cmd
cd backend-java
start.bat
```

### 或手动启动:
```bash
cd backend-java
mvn clean package
java -jar target/sqloj-backend-1.0.0.jar
```

## 步骤 4: 验证 (30秒)

### 测试API是否正常

打开新的终端，执行：

```bash
# 测试登录接口
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student1","password":"admin123"}'
```

**预期返回:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 3,
    "username": "student1",
    "role": "STUDENT",
    "createTime": "2024-xx-xx xx:xx:xx"
  }
}
```

如果看到以上返回，恭喜！后端启动成功！🎉

## 步骤 5: 测试完整功能 (可选)

### 1. 查询题目列表
```bash
curl http://localhost:8080/api/problem/list
```

### 2. 提交SQL
```bash
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELECT id, name, salary FROM employees WHERE department = '\''IT'\'' ORDER BY salary DESC"
  }'
```

### 3. 查询判题结果
```bash
# 替换{submissionId}为上一步返回的submissionId
curl http://localhost:8080/api/submit/result/{submissionId}
```

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 老师 | teacher1 | admin123 |
| 学生 | student1 | admin123 |

## 访问地址

- **后端API**: http://localhost:8080/api
- **健康检查**: http://localhost:8080/actuator/health (如果启用)

## 常见问题

### Q1: 数据库连接失败
**A:** 检查MySQL是否启动，用户名密码是否正确

```bash
# 检查MySQL状态
sudo systemctl status mysql  # Linux
brew services list | grep mysql  # Mac

# 测试连接
mysql -u root -p
```

### Q2: 端口被占用
**A:** 修改 `application.yml` 中的端口：

```yaml
server:
  port: 8081  # 改为其他端口
```

### Q3: 启动时报错找不到数据库
**A:** 确保已执行步骤1的数据库初始化脚本

### Q4: 判题一直PENDING
**A:** 检查是否已创建判题账号和测试表

```sql
-- 检查判题账号
SELECT user, host FROM mysql.user WHERE user LIKE 'judge%';

-- 检查测试表
USE sqloj_test;
SHOW TABLES;
```

## 下一步

- 📖 阅读 [README.md](README.md) 了解完整功能
- 🚀 阅读 [DEPLOYMENT.md](DEPLOYMENT.md) 了解生产部署
- 🔌 阅读 [FRONTEND_INTEGRATION.md](FRONTEND_INTEGRATION.md) 对接前端
- 🧪 阅读 [API_TEST.md](API_TEST.md) 进行API测试

## 停止服务

按 `Ctrl + C` 停止运行

## 需要帮助？

- 查看日志: `logs/sqloj.log` (如果配置了文件日志)
- 查看控制台输出: 查看启动终端的输出
- 阅读文档: 查看 `backend-java/` 目录下的各个 `.md` 文件

---

**祝你使用愉快！** 🎉

如有问题，请查阅详细文档或提交Issue。
