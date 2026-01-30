# 部署指南

## 快速部署（Linux）

### 1. 准备环境

```bash
# 安装JDK 8
sudo apt-get update
sudo apt-get install openjdk-8-jdk

# 安装Maven
sudo apt-get install maven

# 安装MySQL 8
sudo apt-get install mysql-server
```

### 2. 初始化数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行初始化脚本
source /path/to/backend-java/sql/01_create_database.sql
source /path/to/backend-java/sql/02_create_tables.sql
source /path/to/backend-java/sql/03_create_judge_users.sql
source /path/to/backend-java/sql/04_init_data.sql
```

### 3. 配置应用

编辑 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sqloj_biz?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: YOUR_PASSWORD

judge:
  datasource:
    read:
      url: jdbc:mysql://localhost:3306/sqloj_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      username: judge_read
      password: judge123
    write:
      url: jdbc:mysql://localhost:3306/sqloj_test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      username: judge_write
      password: judge456
```

### 4. 编译打包

```bash
cd backend-java
mvn clean package -DskipTests
```

### 5. 运行应用

#### 方式1：直接运行jar

```bash
java -jar target/sqloj-backend-1.0.0.jar
```

#### 方式2：后台运行

```bash
nohup java -jar target/sqloj-backend-1.0.0.jar > logs/app.log 2>&1 &
```

#### 方式3：使用systemd服务

创建服务文件 `/etc/systemd/system/sqloj.service`：

```ini
[Unit]
Description=SQLOJ Backend Service
After=syslog.target network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/sqloj
ExecStart=/usr/bin/java -jar /opt/sqloj/sqloj-backend-1.0.0.jar
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启动服务：

```bash
sudo systemctl daemon-reload
sudo systemctl start sqloj
sudo systemctl enable sqloj
sudo systemctl status sqloj
```

### 6. 验证部署

```bash
# 检查服务是否启动
curl http://localhost:8080/api/problem/list

# 应该返回：{"code":200,"msg":"success","data":[]}
```

## Docker部署（推荐）

### 1. 创建Dockerfile

```dockerfile
FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY target/sqloj-backend-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
```

### 2. 构建镜像

```bash
cd backend-java
mvn clean package -DskipTests
docker build -t sqloj-backend:1.0.0 .
```

### 3. 运行容器

```bash
docker run -d \
  --name sqloj-backend \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/sqloj_biz?... \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  sqloj-backend:1.0.0
```

## 使用Docker Compose

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: sqloj-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
      MYSQL_DATABASE: sqloj_biz
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=mysql_native_password

  backend:
    build: .
    container_name: sqloj-backend
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/sqloj_biz?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root123
    depends_on:
      - mysql

volumes:
  mysql-data:
```

启动：

```bash
docker-compose up -d
```

## Nginx反向代理配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端静态文件
    location / {
        root /var/www/sqloj/frontend;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端API代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

## 性能优化建议

### JVM参数优化

```bash
java -Xms512m -Xmx1024m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -jar sqloj-backend-1.0.0.jar
```

### 数据库优化

```sql
-- 创建索引
CREATE INDEX idx_student_problem ON submission(student_id, problem_id);
CREATE INDEX idx_submit_time ON submission(submit_time);

-- 优化连接池
SET GLOBAL max_connections = 500;
SET GLOBAL wait_timeout = 28800;
```

### 应用配置优化

```yaml
spring:
  datasource:
    druid:
      initial-size: 10
      min-idle: 10
      max-active: 50
      max-wait: 60000

judge:
  thread-pool:
    core-size: 10
    max-size: 20
    queue-capacity: 200
```

## 监控和日志

### 日志配置

在 `application.yml` 中配置：

```yaml
logging:
  level:
    root: INFO
    com.sqloj: DEBUG
  file:
    name: logs/sqloj.log
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
```

### 健康检查

```bash
# 检查应用状态
curl http://localhost:8080/actuator/health

# 查看线程池状态
curl http://localhost:8080/actuator/metrics/executor.active
```

## 故障恢复

### 备份数据库

```bash
# 备份业务数据库
mysqldump -u root -p sqloj_biz > sqloj_biz_backup.sql

# 备份测试数据库
mysqldump -u root -p sqloj_test > sqloj_test_backup.sql
```

### 恢复数据库

```bash
mysql -u root -p sqloj_biz < sqloj_biz_backup.sql
mysql -u root -p sqloj_test < sqloj_test_backup.sql
```

## 升级指南

1. 备份数据库
2. 停止旧版本服务
3. 部署新版本
4. 执行数据库迁移脚本（如有）
5. 启动新版本服务
6. 验证功能正常

## 安全建议

1. 修改默认密码
2. 配置防火墙规则
3. 启用HTTPS
4. 定期更新依赖
5. 限制数据库访问权限
6. 配置日志审计
