# API测试文档

## 测试环境

- 基础URL: `http://localhost:8080/api`
- Content-Type: `application/json`

## 1. 用户模块测试

### 1.1 用户登录

**请求:**
```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "student1",
    "password": "admin123"
  }'
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 3,
    "username": "student1",
    "role": "STUDENT",
    "createTime": "2024-01-01 00:00:00"
  }
}
```

### 1.2 获取用户信息

**请求:**
```bash
curl -X GET "http://localhost:8080/api/user/info?userId=3"
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 3,
    "username": "student1",
    "role": "STUDENT",
    "createTime": "2024-01-01 00:00:00"
  }
}
```

## 2. 题目模块测试

### 2.1 查询题目列表

**请求:**
```bash
curl -X GET http://localhost:8080/api/problem/list
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "title": "查询IT部门员工",
      "description": "编写SQL查询语句...",
      "difficulty": "EASY",
      "teacherId": 2
    }
  ]
}
```

### 2.2 查询题目详情

**请求:**
```bash
curl -X GET http://localhost:8080/api/problem/detail/1
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "title": "查询IT部门员工",
    "description": "编写SQL查询语句...",
    "stdHeaders": "id,name,salary",
    "stdData": "[[1,\"张三\",8000.00]]",
    "difficulty": "EASY",
    "teacherId": 2
  }
}
```

### 2.3 新增题目（需要老师权限）

**请求:**
```bash
curl -X POST http://localhost:8080/api/problem/add \
  -H "Content-Type: application/json" \
  -d '{
    "title": "测试题目",
    "description": "这是一个测试题目",
    "teacherId": 2,
    "stdSql": "SELECT * FROM employees",
    "stdHeaders": "id,name,department,salary,hire_date",
    "stdData": "[[1,\"张三\",\"IT\",8000.00,\"2020-01-15\"]]",
    "difficulty": "EASY"
  }'
```

### 2.4 删除题目

**请求:**
```bash
curl -X DELETE http://localhost:8080/api/problem/delete/1
```

## 3. 提交模块测试

### 3.1 提交SQL

**请求:**
```bash
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELECT id, name, salary FROM employees WHERE department = '\''IT'\'' ORDER BY salary DESC"
  }'
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "submissionId": 1,
    "status": "PENDING",
    "submitTime": "2024-01-01 12:00:00"
  }
}
```

### 3.2 查询判题结果

**请求:**
```bash
curl -X GET http://localhost:8080/api/submit/result/1
```

**预期响应（PENDING状态）:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "PENDING",
    "runningTime": 0,
    "lackNum": null,
    "errNum": null,
    "actualOutput": null
  }
}
```

**预期响应（AC状态）:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "AC",
    "runningTime": 45,
    "lackNum": 0,
    "errNum": 0,
    "actualOutput": "[[5,\"吴十\",10000.00],[3,\"王五\",9000.00]]"
  }
}
```

**预期响应（WA状态）:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "WA",
    "runningTime": 38,
    "lackNum": 1,
    "errNum": 2,
    "actualOutput": "[[1,\"张三\",8000.00]]"
  }
}
```

### 3.3 查询提交历史

**请求:**
```bash
curl -X GET http://localhost:8080/api/submit/history/3
```

**预期响应:**
```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 1,
      "problemId": 1,
      "sqlContent": "SELECT * FROM employees",
      "status": "AC",
      "submitTime": "2024-01-01 12:00:00",
      "runningTime": 45
    },
    {
      "id": 2,
      "problemId": 2,
      "sqlContent": "SELECT * FROM students",
      "status": "WA",
      "submitTime": "2024-01-01 11:00:00",
      "runningTime": 38
    }
  ]
}
```

## 4. 完整测试流程

### 场景1：学生做题全流程

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "student1", "password": "admin123"}'

# 2. 查看题目列表
curl -X GET http://localhost:8080/api/problem/list

# 3. 查看题目详情
curl -X GET http://localhost:8080/api/problem/detail/1

# 4. 提交SQL
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELECT id, name, salary FROM employees WHERE department = '\''IT'\'' ORDER BY salary DESC"
  }'

# 5. 轮询查询判题结果（每隔1秒查询一次）
curl -X GET http://localhost:8080/api/submit/result/1

# 6. 查看提交历史
curl -X GET http://localhost:8080/api/submit/history/3
```

### 场景2：老师出题全流程

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "teacher1", "password": "admin123"}'

# 2. 新增题目
curl -X POST http://localhost:8080/api/problem/add \
  -H "Content-Type: application/json" \
  -d '{
    "title": "查询所有员工",
    "description": "编写SQL查询所有员工的id、name和salary",
    "teacherId": 2,
    "stdSql": "SELECT id, name, salary FROM employees",
    "stdHeaders": "id,name,salary",
    "stdData": "[[1,\"张三\",8000.00],[2,\"李四\",6000.00]]",
    "difficulty": "EASY"
  }'

# 3. 查看题目列表
curl -X GET http://localhost:8080/api/problem/list

# 4. 编辑题目
curl -X PUT http://localhost:8080/api/problem/edit \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "title": "查询所有员工（修改版）",
    "description": "新的描述...",
    "teacherId": 2,
    "stdSql": "SELECT id, name, salary FROM employees",
    "stdHeaders": "id,name,salary",
    "stdData": "[[1,\"张三\",8000.00]]",
    "difficulty": "MEDIUM"
  }'

# 5. 删除题目
curl -X DELETE http://localhost:8080/api/problem/delete/1
```

## 5. 错误测试

### 5.1 登录失败 - 用户不存在

```bash
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "notexist", "password": "admin123"}'
```

**预期响应:**
```json
{
  "code": 500,
  "msg": "用户不存在",
  "data": null
}
```

### 5.2 提交错误SQL - 语法错误

```bash
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELEC * FROM employees"
  }'
```

**判题结果:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "RE",
    "runningTime": 5,
    "lackNum": null,
    "errNum": null,
    "actualOutput": null
  }
}
```

### 5.3 提交超时SQL

```bash
curl -X POST http://localhost:8080/api/submit/sql \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": 3,
    "problemId": 1,
    "sqlContent": "SELECT SLEEP(10)"
  }'
```

**判题结果:**
```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "status": "TLE",
    "runningTime": 3000,
    "lackNum": null,
    "errNum": null,
    "actualOutput": null
  }
}
```

## 6. 使用Postman测试

导入以下集合到Postman：

```json
{
  "info": {
    "name": "SQLOJ API Tests",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "User Login",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"student1\",\"password\":\"admin123\"}"
        },
        "url": {"raw": "http://localhost:8080/api/user/login"}
      }
    },
    {
      "name": "Get Problem List",
      "request": {
        "method": "GET",
        "url": {"raw": "http://localhost:8080/api/problem/list"}
      }
    },
    {
      "name": "Submit SQL",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\"studentId\":3,\"problemId\":1,\"sqlContent\":\"SELECT * FROM employees\"}"
        },
        "url": {"raw": "http://localhost:8080/api/submit/sql"}
      }
    }
  ]
}
```

## 7. 性能测试

使用Apache Bench进行并发测试：

```bash
# 测试查询题目列表接口（100个并发，总共1000个请求）
ab -n 1000 -c 100 http://localhost:8080/api/problem/list

# 测试提交SQL接口
ab -n 100 -c 10 -p submit.json -T application/json http://localhost:8080/api/submit/sql
```

其中 submit.json 内容：
```json
{"studentId":3,"problemId":1,"sqlContent":"SELECT * FROM employees"}
```
