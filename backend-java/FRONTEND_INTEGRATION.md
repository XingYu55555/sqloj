# 前后端对接指南

## 概述

本文档说明如何将现有的Vue前端与新的Java Spring Boot后端进行对接。前端代码**无需修改**（或仅需极少修改），只需调整API基准地址即可。

## 前后端架构

```
┌─────────────────┐         HTTP/JSON          ┌──────────────────┐
│                 │  ────────────────────────>  │                  │
│  Vue Frontend   │                             │  Spring Boot     │
│   (port 3000)   │  <────────────────────────  │  Backend         │
│                 │                             │   (port 8080)    │
└─────────────────┘                             └──────────────────┘
                                                         │
                                                         │
                                                         ▼
                                                ┌─────────────────┐
                                                │  MySQL          │
                                                │  - sqloj_biz    │
                                                │  - sqloj_test   │
                                                └─────────────────┘
```

## API接口对照表

### 原Python后端 vs 新Java后端

| 功能 | 原Python接口 | 新Java接口 | 状态 |
|------|------------|-----------|------|
| 用户登录 | POST /api/login | POST /api/user/login | ✅ 已实现 |
| 获取用户信息 | - | GET /api/user/info | ✅ 已实现 |
| 查询题目列表 | - | GET /api/problem/list | ✅ 已实现 |
| 查询题目详情 | - | GET /api/problem/detail/{id} | ✅ 已实现 |
| 新增题目 | - | POST /api/problem/add | ✅ 已实现 |
| 编辑题目 | - | PUT /api/problem/edit | ✅ 已实现 |
| 删除题目 | - | DELETE /api/problem/delete/{id} | ✅ 已实现 |
| 提交SQL | - | POST /api/submit/sql | ✅ 已实现 |
| 查询判题结果 | - | GET /api/submit/result/{id} | ✅ 已实现 |
| 查询提交历史 | - | GET /api/submit/history/{id} | ✅ 已实现 |

## 前端修改步骤

### 方法1：修改前端配置文件（推荐）

如果前端有统一的API配置文件，只需修改基准URL：

```javascript
// 查找类似以下的配置文件（可能在 src/config/api.js 或 src/utils/request.js）
const API_BASE_URL = 'http://localhost:8080/api';  // 修改为Java后端地址

// 或者使用环境变量
// .env.development
VUE_APP_API_BASE_URL=http://localhost:8080/api

// .env.production
VUE_APP_API_BASE_URL=https://your-domain.com/api
```

### 方法2：使用Nginx反向代理（生产环境推荐）

前端和后端都部署到同一域名下，通过路径区分：

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
    }
}
```

这样前端只需配置：
```javascript
const API_BASE_URL = '/api';  // 相对路径，自动使用当前域名
```

## 数据格式对照

### 统一返回格式

**新Java后端的所有响应格式：**

```json
{
  "code": 200,           // 200-成功, 500-失败
  "msg": "success",      // 提示信息
  "data": { ... }        // 具体数据
}
```

**前端需要根据code判断成功/失败：**

```javascript
// 示例：axios拦截器
axios.interceptors.response.use(
  response => {
    const res = response.data;
    if (res.code === 200) {
      return res.data;  // 返回data部分
    } else {
      // 错误处理
      Message.error(res.msg);
      return Promise.reject(new Error(res.msg));
    }
  },
  error => {
    Message.error(error.message);
    return Promise.reject(error);
  }
);
```

### 字段名映射

**用户对象：**
```json
{
  "id": 1,
  "username": "student1",
  "role": "STUDENT",        // STUDENT/TEACHER/ADMIN
  "createTime": "2024-01-01 00:00:00"
}
```

**题目对象：**
```json
{
  "id": 1,
  "title": "查询IT部门员工",
  "description": "编写SQL查询...",
  "difficulty": "EASY",     // EASY/MEDIUM/HARD
  "teacherId": 2,
  "stdHeaders": "id,name,salary",    // 仅详情接口返回
  "stdData": "[[1,'张三',8000]]"     // 仅详情接口返回
}
```

**提交记录对象：**
```json
{
  "id": 1,
  "problemId": 1,
  "sqlContent": "SELECT * FROM employees",
  "status": "AC",           // PENDING/AC/WA/TLE/RE
  "submitTime": "2024-01-01 12:00:00",
  "runningTime": 45         // 毫秒
}
```

**判题结果对象：**
```json
{
  "status": "AC",           // PENDING/AC/WA/TLE/RE
  "runningTime": 45,        // 毫秒
  "lackNum": 0,             // 缺失行数（仅WA时）
  "errNum": 0,              // 错误行数（仅WA时）
  "actualOutput": "[[...]]" // 实际输出（仅WA/AC时）
}
```

## 完整对接示例

### 1. 用户登录

**前端代码：**
```javascript
// Login.vue
async login() {
  try {
    const response = await axios.post('/api/user/login', {
      username: this.username,
      password: this.password
    });
    
    // 响应格式：{code: 200, msg: "success", data: {...}}
    const user = response.data;
    
    // 保存用户信息
    localStorage.setItem('user', JSON.stringify(user));
    localStorage.setItem('userId', user.id);
    localStorage.setItem('role', user.role);
    
    // 跳转到首页
    this.$router.push('/home');
  } catch (error) {
    this.$message.error('登录失败：' + error.message);
  }
}
```

### 2. 查询题目列表

**前端代码：**
```javascript
// ProblemList.vue
async fetchProblems() {
  try {
    const response = await axios.get('/api/problem/list');
    this.problems = response.data;  // 数组
  } catch (error) {
    this.$message.error('获取题目列表失败');
  }
}
```

### 3. 提交SQL并轮询结果

**前端代码：**
```javascript
// ProblemDetail.vue
async submitSql() {
  try {
    // 1. 提交SQL
    const response = await axios.post('/api/submit/sql', {
      studentId: this.userId,
      problemId: this.problemId,
      sqlContent: this.sqlCode
    });
    
    const submissionId = response.data.submissionId;
    
    // 2. 轮询判题结果
    this.pollResult(submissionId);
    
  } catch (error) {
    this.$message.error('提交失败');
  }
},

async pollResult(submissionId) {
  const timer = setInterval(async () => {
    try {
      const response = await axios.get(`/api/submit/result/${submissionId}`);
      const result = response.data;
      
      // 如果判题完成（不是PENDING状态）
      if (result.status !== 'PENDING') {
        clearInterval(timer);
        this.showResult(result);
      }
    } catch (error) {
      clearInterval(timer);
      this.$message.error('查询判题结果失败');
    }
  }, 1000);  // 每1秒轮询一次
},

showResult(result) {
  if (result.status === 'AC') {
    this.$message.success('恭喜！答案正确');
  } else if (result.status === 'WA') {
    this.$message.error(`答案错误：缺失${result.lackNum}行，错误${result.errNum}行`);
  } else if (result.status === 'TLE') {
    this.$message.error('运行超时');
  } else if (result.status === 'RE') {
    this.$message.error('运行错误');
  }
}
```

### 4. 查询提交历史

**前端代码：**
```javascript
// SubmissionHistory.vue
async fetchHistory() {
  try {
    const userId = localStorage.getItem('userId');
    const response = await axios.get(`/api/submit/history/${userId}`);
    this.submissions = response.data;  // 数组，按时间倒序
  } catch (error) {
    this.$message.error('获取提交历史失败');
  }
}
```

## 跨域问题处理

### 开发环境

**方法1：后端已配置CORS（推荐）**

Java后端已经配置了CORS支持，允许所有域名跨域访问。前端无需额外配置。

**方法2：前端配置代理**

如果需要前端代理，修改 `vue.config.js`：

```javascript
module.exports = {
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  }
}
```

### 生产环境

使用Nginx反向代理（见上文配置）。

## 测试步骤

### 1. 启动后端

```bash
cd backend-java
mvn clean package
java -jar target/sqloj-backend-1.0.0.jar
```

后端将在 http://localhost:8080 启动

### 2. 测试后端API

```bash
# 测试登录
curl -X POST http://localhost:8080/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"student1","password":"admin123"}'

# 测试题目列表
curl http://localhost:8080/api/problem/list
```

### 3. 启动前端

```bash
cd app
npm install
npm run serve
```

前端将在 http://localhost:3000 启动

### 4. 浏览器测试

1. 打开 http://localhost:3000
2. 使用 student1/admin123 登录
3. 查看题目列表
4. 提交SQL并查看判题结果

## 常见问题排查

### 1. 跨域错误

**错误信息：**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/user/login' from origin 'http://localhost:3000' has been blocked by CORS policy
```

**解决方案：**
- 确认后端CorsConfig配置正确
- 检查后端是否正常启动
- 尝试使用前端代理（vue.config.js）

### 2. 接口404错误

**错误信息：**
```
GET http://localhost:8080/api/problem/list 404 (Not Found)
```

**解决方案：**
- 检查后端是否正常启动
- 确认接口路径是否正确
- 查看后端日志，确认Controller是否被扫描到

### 3. 数据库连接失败

**错误信息：**
```
Cannot create PoolableConnectionFactory (Access denied for user 'root'@'localhost')
```

**解决方案：**
- 检查application.yml中的数据库配置
- 确认MySQL是否启动
- 验证数据库用户名密码是否正确

### 4. 判题一直PENDING

**现象：**
提交SQL后，状态一直显示PENDING

**解决方案：**
- 查看后端日志，确认判题任务是否执行
- 检查线程池配置
- 确认sqloj_test数据库中有测试表
- 验证判题账号权限是否正确

## 性能优化建议

### 前端优化

1. **请求防抖**：提交按钮添加防抖，避免重复提交
2. **轮询优化**：判题结果轮询使用指数退避策略
3. **缓存策略**：题目列表添加缓存，减少重复请求
4. **分页加载**：提交历史使用分页，避免一次加载过多数据

### 后端优化

1. **连接池调优**：根据并发量调整数据库连接池大小
2. **线程池调优**：调整判题线程池配置
3. **查询优化**：添加数据库索引，优化查询性能
4. **缓存热点数据**：使用Redis缓存题目列表等热点数据

## 部署架构

### 开发环境
```
Frontend (Vue Dev Server) :3000
Backend (Spring Boot) :8080
MySQL :3306
```

### 生产环境（推荐）
```
┌─────────────────────────────────┐
│         Nginx :80/443           │
│  ┌──────────┐   ┌─────────────┐ │
│  │ Frontend │   │   Backend   │ │
│  │  静态文件 │   │  Proxy      │ │
│  └──────────┘   └─────────────┘ │
└─────────────────────────────────┘
              │
              ▼
    ┌─────────────────┐
    │  MySQL :3306    │
    └─────────────────┘
```

## 安全建议

1. **HTTPS**: 生产环境必须使用HTTPS
2. **密码加密**: 已使用BCrypt加密
3. **SQL注入防护**: 已使用PreparedStatement
4. **权限控制**: 前后端都要验证用户权限
5. **输入验证**: 前端验证+后端验证双重保障

## 总结

本Java后端完全兼容原有前端，主要优势：

1. ✅ **零修改对接**：前端只需改API地址
2. ✅ **统一返回格式**：`{code, msg, data}` 结构清晰
3. ✅ **完整功能覆盖**：11个API全部实现
4. ✅ **判题逻辑复刻**：独立连接+事务回滚+超时控制
5. ✅ **生产级质量**：连接池+线程池+异常处理
6. ✅ **详细文档**：README+部署指南+API测试文档

有任何问题，请查阅相关文档或联系技术支持。
