# 易面 — 一款开源简单的 AI 面试助手

基于 Spring Boot 3 + Vue 3 + LangChain 构建的智能面试平台，集成大语言模型（DeepSeek）实现智能问答、简历评估、模拟面试等功能。

---

## 技术架构

**前端**：Vue 3

**后端**：Spring Boot 3.5

**Agent**：LangChain

**技术栈**：Redis，MySQL，RabbitMQ，PostgreSQL（向量数据库）

**Chat 模型**：DeepSeek-v4-pro

**Embedding 模型**：Qwen-embedding-v4

**Rerank 模型**：Qwen-rerank

---

## 后端架构设计

### 1. 整体分层架构

```
┌─────────────────────────────────────────────────┐
│                  Controller 层                    │
│          REST API · 参数校验 · 统一响应            │
├─────────────────────────────────────────────────┤
│                   Security 层                     │
│       JWT 认证过滤器 · Spring Security 鉴权        │
├─────────────────────────────────────────────────┤
│                   Service 层                      │
│          业务逻辑 · 事务管理 · DTO 转换             │
├─────────────────────────────────────────────────┤
│                    Mapper 层                      │
│        MyBatis-Plus · 数据访问 · 逻辑删除           │
├─────────────────────────────────────────────────┤
│                  数据存储层                        │
│   MySQL (业务数据) · PostgreSQL (向量存储)          │
│   Redis (缓存/Token) · RabbitMQ (异步消息)          │
└─────────────────────────────────────────────────┘
```

### 2. 模块职责

| 模块 | 路径 | 职责 |
|------|------|------|
| **controller** | `com.yimian.system.controller` | 接收 HTTP 请求，参数校验，调用 Service |
| **service** | `com.yimian.system.service` | 核心业务逻辑，事务编排 |
| **mapper** | `com.yimian.system.mapper` | MyBatis-Plus 数据访问，支持 XML 自定义 SQL |
| **entity** | `com.yimian.system.entity` | 数据库实体映射（继承 BaseEntity 公共字段） |
| **dto** | `com.yimian.system.dto` | 请求数据传输对象 |
| **vo** | `com.yimian.system.vo` | 响应视图对象（脱敏后返回前端） |
| **security** | `com.yimian.system.security` | JWT 生成/解析、认证过滤器、用户详情服务 |
| **config** | `com.yimian.system.config` | Spring 配置类（安全、数据源、跨域、MVC） |
| **common** | `com.yimian.system.common` | 统一响应体 `Result<T>`、异常处理、工具类 |

### 3. 安全设计

- **无状态 JWT 认证**：采用 Access Token（2h）+ Refresh Token（7d）双令牌机制
- **BCrypt 密码加密**：用户密码使用 `BCryptPasswordEncoder` 加盐哈希存储
- **Spring Security 过滤器链**：

```
请求 → CorsConfig(跨域) → JwtAuthenticationFilter(JWT解析)
     → AuthenticationEntryPoint(未认证处理)
     → AccessDeniedHandler(无权限处理)
     → Controller
```

- 公开端点：`/auth/login`、`/health`、API 文档路径
- 其余所有接口需要携带 `Authorization: Bearer <token>` 访问

### 4. 统一响应设计

所有 API 返回统一格式 `Result<T>`：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { ... },
  "timestamp": 1720771200000
}
```

状态码体系（`ResultCode` 枚举）：

| 范围 | 说明 | 示例 |
|------|------|------|
| `200` | 成功 | `SUCCESS(200, "操作成功")` |
| `400-405` | 客户端错误 | 参数错误、未认证、无权限 |
| `500` | 服务端错误 | 服务器内部错误 |
| `1000+` | 业务异常 | 用户不存在、密码错误、Token 过期 |

全局异常通过 `GlobalExceptionHandler` 统一拦截，业务异常抛出 `BusinessException` 即可。

### 5. 数据源设计

- **MySQL**（主数据库）：用户、权限、业务数据，使用 Druid 连接池 + MyBatis-Plus
  - 逻辑删除：`deleted = 1` 标记删除
  - 自动填充：`BaseEntity` 中的创建时间、更新时间由 `MyBatisMetaObjectHandler` 自动填充
- **PostgreSQL + pgvector**（向量数据库）：存储知识库文档向量，用于 RAG 检索增强生成
- **Redis**：Token 黑名单、验证码、热点缓存
- **RabbitMQ**：异步任务队列（如 AI 评估、简历解析等耗时操作）

### 6. 项目依赖一览

| 依赖 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 3.5.16 | 基础框架 |
| MyBatis-Plus | 3.5.12 | ORM + 分页 + 逻辑删除 |
| Druid | 1.2.24 | 数据库连接池 |
| jjwt | 0.12.6 | JWT 令牌生成与解析 |
| Knife4j | 4.5.0 | API 文档（Swagger 增强） |
| Hutool | 5.8.34 | Java 工具库 |
| Lombok | 1.18.38 | 代码简化 |

### 7. 项目结构

```
system/
├── pom.xml                              # Maven 依赖与构建配置
└── src/main/java/com/yimian/system/
    ├── SystemApplication.java           # 启动类
    ├── common/                          # 公共组件（统一响应、异常处理、工具类）
    ├── config/                          # 配置类（安全、数据源、跨域、MVC）
    ├── controller/                      # 控制器层 —— 接收 HTTP 请求，参数校验
    ├── dto/                             # 请求 DTO —— 入参对象
    ├── entity/                          # 实体层 —— 数据库表映射，继承 BaseEntity
    ├── mapper/                          # Mapper 层 —— MyBatis-Plus 数据访问
    ├── security/                        # 安全模块 —— JWT 工具、过滤器、认证/授权处理
    ├── service/                         # 服务层 —— 业务逻辑、事务编排
    └── vo/                              # 视图 VO —— 响应对象（脱敏后返回前端）
```

---

## 运行指南

### 1. 环境要求

| 组件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 推荐 OpenJDK 17 / Amazon Corretto 17 |
| Maven | 3.6+ | 构建工具（已内置 Maven Wrapper） |
| MySQL | 8.0+ | 业务数据库 |
| PostgreSQL | 15+ + pgvector | 向量数据库（可选，后续知识库功能需要） |
| Redis | 7.0+ | 缓存与 Token 管理 |
| RabbitMQ | 3.12+ | 异步消息队列 |

### 2. 数据库初始化

创建 MySQL 数据库并导入表结构：

```sql
-- 创建数据库（二选一）
CREATE DATABASE yimian DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 或开发环境使用
CREATE DATABASE interview_system DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 用户表
CREATE TABLE sys_user (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    username    VARCHAR(64)  NOT NULL UNIQUE COMMENT '用户名',
    password    VARCHAR(255) NOT NULL COMMENT '密码(BCrypt)',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
    nickname    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    roles       VARCHAR(255) DEFAULT 'ROLE_USER' COMMENT '角色(逗号分隔)',
    status      TINYINT      DEFAULT 1 COMMENT '状态:0禁用,1启用,2锁定',
    last_login_time DATETIME DEFAULT NULL COMMENT '最后登录时间',
    last_login_ip   VARCHAR(64) DEFAULT NULL COMMENT '最后登录IP',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted     TINYINT      DEFAULT 0 COMMENT '逻辑删除:0未删除,1已删除',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';
```

### 3. 修改配置

编辑 `src/main/resources/application-dev.yml`，根据本地环境修改数据库连接：

```yaml
spring:
  datasource:
    mysql:
      url: jdbc:mysql://localhost:3306/interview_system?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false&allowPublicKeyRetrieval=true
      username: root
      password: 你的密码
```

> **生产环境**：复制 `application-prod.yml`，修改对应配置后通过 `--spring.profiles.active=prod` 激活。

### 4. 启动后端

**方式一：使用 Maven Wrapper（推荐，无需安装 Maven）**

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**方式二：使用 Maven**

```bash
mvn spring-boot:run
```

**方式三：打包后运行**

```bash
mvn clean package -DskipTests
java -jar target/system-0.0.1-SNAPSHOT.jar
```

### 5. 指定环境启动

```bash
# 开发环境（默认）
java -jar target/system-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 生产环境
java -jar target/system-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 6. 验证启动

启动成功后访问以下地址：

| 地址 | 说明 |
|------|------|
| `http://localhost:8080/api/health` | 健康检查接口 |
| `http://localhost:8080/api/doc.html` | Knife4j API 文档（Swagger UI） |
| `http://localhost:8080/api/swagger-ui.html` | 原生 Swagger UI |

健康检查应返回：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "status": "UP",
    "service": "Interview System",
    "version": "1.0.0",
    "timestamp": 1720771200000
  }
}
```

### 7. 测试登录接口

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "123456"}'
```

### 8. 常见问题

**Q：启动时报数据库连接失败？**
- 确保 MySQL 服务已启动且数据库已创建
- 检查 `application-dev.yml` 中的用户名密码是否正确

**Q：端口被占用？**
- 修改 `application.yml` 中 `server.port` 为其他端口

**Q：Maven 依赖下载慢？**
- 在 `pom.xml` 同级目录创建 `.mvn/maven.config`，添加阿里云镜像配置
- 或设置环境变量：`MAVEN_OPTS=-Dmaven.repo.remote=https://maven.aliyun.com/repository/public`

---

## 开发路线图

- [x] 项目骨架搭建（Spring Boot + MyBatis-Plus + Security）
- [x] JWT 认证体系
- [x] 统一响应与异常处理
- [x] API 文档（Knife4j）
- [ ] 用户注册与管理
- [ ] LangChain Agent 集成
- [ ] 知识库管理（PostgreSQL pgvector）
- [ ] 智能面试对话
- [ ] 简历解析与评估
- [ ] 前端 Vue 3 开发
