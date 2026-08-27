# 用户接口

---

## 全局 ID 策略

自 `system/sql/33-use-snowflake-id-for-user-blog-favorite-folder.sql` 起，新注册用户、新建博客、新建收藏夹的主键 ID 由后端应用通过 MyBatis-Plus `IdType.ASSIGN_ID` 生成雪花 ID，不再依赖数据库自增。

### Long ID JSON 序列化

后端接口响应中的 Java `Long`/`long` 字段统一按 JSON 字符串返回，避免浏览器 JavaScript `number` 对雪花 ID 产生精度丢失。前端接收 `id`、`authorId`、`refId`、`targetId`、`folderId`、`blogId`、`knowledgeId` 等 ID 字段时应按字符串处理；请求路径参数或请求体中传回这些 ID 时可以继续传十进制字符串。

---

## 19. 用户公开资料与关注系统

> 相关 SQL：`system/sql/27-user-follow.sql`

### 19.1 查看用户公开资料

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/user/{id}` |
| 请求方式 | `GET` |
| 权限要求 | `user:view-public` |

成功响应 `data` 为 `UserVO`。公开资料会隐藏 `email`、`phone`，`roles`、`permissions` 返回空数组。

| 字段 | 类型 | 说明 |
|------|------|------|
| `followingCount` | Integer | 该用户关注的人数 |
| `followerCount` | Integer | 该用户粉丝数 |
| `followed` | Boolean | 当前登录用户是否已关注该用户 |

### 19.2 关注 / 取消关注用户

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/user/{id}/follow` |
| 请求方式 | `POST` |
| 权限要求 | `user:follow` |

该接口为 toggle：未关注时关注，已关注时取消关注。成功响应 `data` 为更新后的公开 `UserVO`。

### 19.3 查看关注 / 粉丝列表

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/user/{id}/following?page=1&size=20` | `GET` | `user:view-public` | 查看该用户关注的人 |
| `/api/user/{id}/followers?page=1&size=20` | `GET` | `user:view-public` | 查看该用户的粉丝 |

成功响应 `data` 对齐 `PageInfo<UserVO>`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。
列表项为公开 `UserVO`，隐藏 `email`、`phone`、`roles`、`permissions`、`lastLoginTime`。

### 19.4 题目作者头像字段

`KnowledgeVO` 新增字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `submitUserAvatar` | String | 提交人头像 URL |
| `auditUserAvatar` | String | 审核人头像 URL |

### 19.5 用户公开主页内容

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/user/{id}/knowledges?page=1&size=10` | `GET` | `user:view-public` | 查看该用户发表的题目，仅返回审核通过 `status=1` 的题目 |
| `/api/user/{id}/blogs?page=1&size=10` | `GET` | `user:view-public` | 查看该用户已发布的公开博客，仅返回 `status=1` 的博客 |

成功响应 `data` 对齐 `PageInfo`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。
题目列表项为 `KnowledgeVO`，博客列表项为 `BlogVO`。

---

## 20. 问题反馈系统

> 相关 SQL：`system/sql/35-init-bug-feedback.sql`

用户端支持提交服务器 Bug 和问题所在页面，管理端支持查看反馈内容并更新处理状态。

### 20.1 提交问题反馈

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/feedback` |
| 请求方式 | `POST` |
| 认证要求 | 需携带 Token |

**Request Body**：

```json
{
  "title": "博客发布后详情页提示不存在",
  "content": "点击发布成功后跳转详情页，页面提示博客不存在。后端日志显示查询 ID 与数据库 ID 不一致。",
  "pageUrl": "http://47.105.103.37:88/#/app/blogs/write",
  "contact": "admin@example.com"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ✅ | 反馈标题，最长 120 字 |
| `content` | String | ✅ | 问题描述，最长 2000 字 |
| `pageUrl` | String | | 问题所在页面，最长 500 字 |
| `contact` | String | | 可选联系方式，最长 120 字 |

成功响应 `data` 为 `BugFeedbackVO`。

### 20.2 管理端反馈列表

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/feedback` |
| 请求方式 | `GET` |
| 权限要求 | `ROLE_ADMIN` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | | 1 | 页码 |
| `size` | Integer | | 10 | 每页条数，最大 50 |
| `keyword` | String | | - | 搜索标题、内容、用户名 |
| `status` | Integer | | - | 0=待处理 1=处理中 2=已解决 3=已忽略 |

成功响应 `data` 对齐 `PageInfo<BugFeedbackVO>`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。

### 20.3 管理端反馈详情与状态

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/admin/feedback/{id}` | `GET` | `ROLE_ADMIN` | 查看反馈详情 |
| `/api/admin/feedback/{id}/status?status=2` | `PUT` | `ROLE_ADMIN` | 更新反馈状态 |

`BugFeedbackVO` 字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 反馈 ID，JSON 中按字符串返回 |
| `userId` | Long | 反馈用户 ID，JSON 中按字符串返回 |
| `username` | String | 反馈用户名快照 |
| `title` | String | 反馈标题 |
| `content` | String | 问题描述 |
| `pageUrl` | String | 问题所在页面 |
| `contact` | String | 联系方式 |
| `status` | Integer | 0=待处理 1=处理中 2=已解决 3=已忽略 |
| `createdAt` | String | 提交时间 |
| `updatedAt` | String | 更新时间 |

---

## 21. 系统审核开关

> 相关 SQL：`system/sql/36-system-setting-audit-switch.sql`

题目提交审核开关持久化存储在 `sys_system_setting`，配置键为 `knowledge.audit.enabled`。配置文件 `audit.enabled` 仍作为首次初始化默认值，默认开启审核。

### 21.1 查询题目审核开关

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/knowledge/audit-switch` |
| 请求方式 | `GET` |
| 认证要求 | 需携带 Token |

成功响应 `data` 为 Boolean：

| 值 | 说明 |
|------|------|
| `true` | 提交后进入待审核 |
| `false` | 提交后直接公开 |

### 21.2 设置题目审核开关

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/knowledge/audit-switch?enabled=true` |
| 请求方式 | `PUT` |
| 权限要求 | `knowledge:audit` |

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `enabled` | Boolean | ✅ | `true` 开启审核，`false` 关闭审核 |

成功响应 `data` 为更新后的 Boolean 状态。该设置会持久化到数据库，重启后仍生效。

---

## 18. 点赞系统补充

> 相关 SQL：`system/sql/26-knowledge-like.sql`

### 18.1 题目点赞 / 取消点赞

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/knowledge/{id}/like` |
| 请求方式 | `POST` |
| 权限要求 | `knowledge:like` |

该接口为 toggle：未点赞时点赞，已点赞时取消点赞，并同步更新题目的 `likeCount`。

成功响应 `data` 为最新点赞数：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 12
}
```

`KnowledgeVO` 新增字段：

| 字段 | 类型 | 说明 |
|------|------|------|
| `liked` | Boolean | 当前登录用户是否已点赞该题目 |

### 18.2 博客点赞 / 取消点赞

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/blogs/{id}/like` |
| 请求方式 | `POST` |
| 权限要求 | `blog:like` |

该接口为 toggle，成功响应 `data` 为最新点赞数。

## 1. 用户注册

### 基本信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/auth/register` |
| **请求方式** | `POST` |
| **认证要求** | 无需认证 |
| **Content-Type** | `application/json` |

### 输入参数（Request Body）

```json
{
  "username": "zhangsan",
  "password": "Aa123456",
  "email": "zhangsan@example.com",
  "phone": "13800138000",
  "nickname": "张三"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | ✅ | 用户名，唯一，4-64 字符 |
| `password` | String | ✅ | 密码，6-32 字符，必须至少包含一个大写字母、一个小写字母和一个数字 |
| `email` | String | | 邮箱，格式校验 |
| `phone` | String | | 手机号，11 位 |
| `nickname` | String | | 昵称，不传则默认为用户名 |

### 输出参数（Response Body）

**成功响应** HTTP 200：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 201234567890123456,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "phone": "13800138000",
    "nickname": "张三",
    "avatar": null,
    "status": 1,
    "createdAt": "2026-07-12 15:30:00"
  },
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，200 表示成功 |
| `message` | String | 提示信息 |
| `data.id` | Long | 新建用户雪花 ID |
| `data.username` | String | 用户名 |
| `data.email` | String | 邮箱 |
| `data.phone` | String | 手机号 |
| `data.nickname` | String | 昵称 |
| `data.avatar` | String | 头像（新用户为 null） |
| `data.status` | Integer | 状态：1=正常 |
| `data.createdAt` | String | 注册时间 |

**失败响应**：

| HTTP 状态码 | code | message | 触发场景 |
|:---:|------|------|------|
| 200 | 1001 | 用户名已存在 | username 重复 |
| 200 | 1002 | 邮箱已被注册 | email 重复 |
| 400 | 400 | 请求参数错误 | 参数校验不通过 |

> 注意：业务异常统一返回 HTTP 200，通过 `code` 区分错误类型。参数校验失败由 Spring Validation 触发，返回 HTTP 400。

### 业务规则

1. 注册成功后默认分配 `ROLE_USER` 角色
2. 密码使用 BCrypt 加密存储，不返回给前端
3. 用户名全局唯一，邮箱也唯一（可为空）
4. 新用户状态默认为 `1`（启用）

---

## 2. 用户登录

### 基本信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/auth/login` |
| **请求方式** | `POST` |
| **认证要求** | 无需认证 |
| **Content-Type** | `application/json` |

### 输入参数（Request Body）

```json
{
  "username": "admin",
  "password": "admin123",
  "captchaKey": "uuid-xxx",
  "captcha": "abcd"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | ✅ | 用户名 |
| `password` | String | ✅ | 密码 |
| `captchaKey` | String | | 验证码 key（预留） |
| `captcha` | String | | 验证码（预留） |

### 输出参数（Response Body）

**成功响应** HTTP 200：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "permissions": ["user:list","user:create","role:list","perm:list","self:profile", "..."],
    "userInfo": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com",
      "phone": null,
      "nickname": "系统管理员",
      "avatar": null,
      "roles": ["ROLE_ADMIN"],
      "permissions": ["user:list","user:create","role:list","perm:list","self:profile", "..."],
      "status": 1,
      "createdAt": "2026-07-12 10:00:00",
      "lastLoginTime": "2026-07-12 15:30:00"
    }
  },
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `code` | Integer | 状态码，200 表示成功 |
| `message` | String | 提示信息 |
| `data.accessToken` | String | 访问令牌（2 小时有效） |
| `data.refreshToken` | String | 刷新令牌（7 天有效） |
| `data.tokenType` | String | Token 类型，固定为 `Bearer` |
| `data.expiresIn` | Long | 过期时间（秒） |
| `data.permissions` | Array | 权限编码列表，前端可用于按钮显隐 |
| `data.userInfo.id` | Long | 用户 ID |
| `data.userInfo.username` | String | 用户名 |
| `data.userInfo.email` | String | 邮箱 |
| `data.userInfo.phone` | String | 手机号 |
| `data.userInfo.nickname` | String | 昵称 |
| `data.userInfo.avatar` | String | 头像 URL |
| `data.userInfo.roles` | Array | 角色编码列表，如 `["ROLE_ADMIN"]` |
| `data.userInfo.permissions` | Array | 权限编码列表，如 `["user:create"]` |
| `data.userInfo.status` | Integer | 状态：1=正常，0=禁用，2=锁定 |
| `data.userInfo.createdAt` | String | 注册时间 |
| `data.userInfo.lastLoginTime` | String | 最后登录时间 |

**失败响应**：

| HTTP 状态码 | code | message | 触发场景 |
|:---:|------|------|------|
| 200 | 1003 | 用户不存在 | username 未注册 |
| 200 | 1004 | 密码错误 | password 不匹配 |
| 200 | 1005 | 账号已被禁用 | status ≠ 1 |
| 400 | 400 | 请求参数错误 | 参数校验不通过 |

---

## 2.1 管理员登录

管理员后台专用登录入口，登录成功后额外校验 `ROLE_ADMIN` 角色，否则拒绝。

### 基本信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/auth/admin/login` |
| **请求方式** | `POST` |
| **认证要求** | 无需认证 |
| **Content-Type** | `application/json` |

### 输入参数（Request Body）

请求体与 [`/auth/login`](#2-用户登录) 完全一致（`LoginDto`）。

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 输出参数（Response Body）

成功响应结构同 [`/auth/login`](#2-用户登录)（`LoginVO`），此处不再赘述。

**失败响应**：

| HTTP 状态码 | code | message | 触发场景 |
|:---:|------|------|------|
| 200 | 1003 | 用户不存在 | username 未注册 |
| 200 | 1004 | 密码错误 | password 不匹配 |
| 200 | 1005 | 账号已被禁用 | status ≠ 1 |
| 200 | 403 | 无权限访问 | 登录成功但用户无 `ROLE_ADMIN` 角色 |
| 400 | 400 | 请求参数错误 | 参数校验不通过 |

---

## 2.2 用户端登录

用户端专用登录入口，语义独立，允许所有角色登录（包括管理员）。便于将来差异化（如用户端增加验证码、并发限制等）。

### 基本信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/auth/user/login` |
| **请求方式** | `POST` |
| **认证要求** | 无需认证 |
| **Content-Type** | `application/json` |

### 输入参数（Request Body）

请求体与 [`/auth/login`](#2-用户登录) 完全一致（`LoginDto`）。

### 输出参数（Response Body）

成功响应结构同 [`/auth/login`](#2-用户登录)（`LoginVO`）。

**失败响应**：

| HTTP 状态码 | code | message | 触发场景 |
|:---:|------|------|------|
| 200 | 1003 | 用户不存在 | username 未注册 |
| 200 | 1004 | 密码错误 | password 不匹配 |
| 200 | 1005 | 账号已被禁用 | status ≠ 1 |
| 400 | 400 | 请求参数错误 | 参数校验不通过 |

---

## 3. 个人中心

> **认证要求**：需携带 Token。

### 3.1 查询个人信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/user/profile` |
| **请求方式** | `GET` |

返回当前登录用户的完整信息（含角色和权限列表）。

### 3.2 修改个人信息

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/user/profile` |
| **请求方式** | `PUT` |

```json
{ "nickname": "新昵称", "email": "new@example.com", "phone": "13800000000", "avatar": "/api/file/avatar/2026/07/13/xxx.jpg" }
```

所有字段可选，不传不更新。**不能修改密码**。`avatar` 字段为头像访问 URL，由前端先调通用上传接口 `POST /api/file/upload` 获取后传入。

| 失败场景 | code | 说明 |
|------|------|------|
| 邮箱已被注册 | 1002 | email 重复 |

### 3.3 修改密码

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/user/password` |
| **请求方式** | `PUT` |

```json
{ "oldPassword": "旧密码", "newPassword": "Aa123456" }
```

| 失败场景 | code | 说明 |
|------|------|------|
| 旧密码错误 | 1004 | 密码错误 |
| 参数校验失败 | 400 | newPassword 必须为 6-32 位，且至少包含一个大写字母、一个小写字母和一个数字 |


---

## 4. 用户管理（管理员）

> **认证要求**：需携带 Token，且用户具有 `ROLE_ADMIN` 角色。
> **请求头**：`Authorization: Bearer <token>`

### 4.1 用户列表

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users` |
| **请求方式** | `GET` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|--------|------|
| `page` | int | | 1 | 页码 |
| `size` | int | | 10 | 每页条数 |
| `keyword` | String | | — | 搜索关键词 |
| `role` | String | | — | 角色筛选，如 `ROLE_ADMIN` |
| `status` | Integer | | — | 状态筛选 |

### 4.2 用户详情

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users/{id}` |
| **请求方式** | `GET` |

### 4.3 新增用户

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users` |
| **请求方式** | `POST` |

```json
{
  "username": "newuser",
  "password": "Aa123456",
  "email": "new@example.com",
  "phone": "13800000000",
  "nickname": "新用户",
  "roleCodes": ["ROLE_INTERVIEWER"]
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | ✅ | 用户名 |
| `password` | String | ✅ | 密码，6-32 位，必须至少包含一个大写字母、一个小写字母和一个数字 |
| `roleCodes` | Array | | 角色编码列表，不传默认 `ROLE_USER` |

### 4.4 编辑用户

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users/{id}` |
| **请求方式** | `PUT` |

```json
{ "nickname": "新昵称", "email": "x@x.com", "phone": "139", "status": 1 }
```

所有字段可选。**不能修改密码**，密码通过单独接口重置。

### 4.5 重置密码

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users/{id}/password` |
| **请求方式** | `PUT` |

```json
{ "newPassword": "Aa123456" }
```

管理员无需旧密码即可重置用户密码。`newPassword` 必须为 6-32 位，且至少包含一个大写字母、一个小写字母和一个数字。

### 4.6 删除用户

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users/{id}` |
| **请求方式** | `DELETE` |

逻辑删除。

### 4.7 分配角色

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/users/{id}/roles` |
| **请求方式** | `PUT` |

```json
{ "roleCodes": ["ROLE_ADMIN", "ROLE_INTERVIEWER"] }
```

先删后增，全量替换。

---

## 5. 角色管理（管理员）

### 5.1 角色列表

| GET | `/api/admin/roles` | 全量列表，按 sort 排序 |

### 5.2 角色详情

| GET | `/api/admin/roles/{id}` |

### 5.3 新增角色

| POST | `/api/admin/roles` |

```json
{ "roleCode": "ROLE_HR", "roleName": "HR", "description": "人事", "sort": 5 }
```

### 5.4 编辑角色

| PUT | `/api/admin/roles/{id}` |

```json
{ "roleName": "人力资源", "sort": 3 }
```

### 5.5 删除角色

| DELETE | `/api/admin/roles/{id}` |

> 兼容接口：现在等价于关闭角色（`deleted=1`），不是物理删除。
>
> 新增状态接口：`PUT /api/admin/roles/{id}/enabled?enabled=true|false`，`true` 启用角色，`false` 关闭角色。成功响应 `data` 为更新后的 `Role`。
>
> `GET /api/admin/roles` 和 `GET /api/admin/roles/{id}` 会返回启用和关闭角色，响应字段包含 `deleted`：`0`/`null` 表示启用，`1` 表示关闭。

### 5.6 查询角色权限

| GET | `/api/admin/roles/{id}/permissions` |

返回 `["user:list", "user:view"]`。

### 5.7 分配权限

| PUT | `/api/admin/roles/{id}/permissions` |

```json
{ "permCodes": ["user:list", "user:create", "user:edit", "user:delete"] }
```

先删后增。

---

## 6. 权限管理（管理员）

### 6.1 权限列表

| GET | `/api/admin/permissions` |

### 6.2 权限详情

| GET | `/api/admin/permissions/{id}` |

### 6.3 新增权限

| POST | `/api/admin/permissions` |

```json
{ "permCode": "interview:review", "permName": "评阅面试", "description": "评分批注" }
```

### 6.4 编辑权限

| PUT | `/api/admin/permissions/{id}` |

```json
{ "permName": "面试评阅" }
```

### 6.5 删除权限

| DELETE | `/api/admin/permissions/{id}` |

> 兼容接口：现在等价于关闭权限（`deleted=1`），不是物理删除。
>
> 新增状态接口：`PUT /api/admin/permissions/{id}/enabled?enabled=true|false`，`true` 启用权限，`false` 关闭权限。成功响应 `data` 为更新后的 `Permission`。
>
> `GET /api/admin/permissions` 和 `GET /api/admin/permissions/{id}` 会返回启用和关闭权限，响应字段包含 `deleted`：`0`/`null` 表示启用，`1` 表示关闭。

---

## 7. 操作日志管理（管理员）

> **认证要求**：需携带 Token，且用户具有 `ROLE_ADMIN` 角色和 `log:list` 权限。

### 7.1 日志列表

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/logs` |
| **请求方式** | `GET` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|--------|------|
| `page` | int | | 1 | 页码 |
| `size` | int | | 10 | 每页条数 |
| `username` | String | | — | 操作人用户名筛选 |
| `module` | String | | — | 模块筛选，如"用户管理" |
| `status` | Integer | | — | 操作结果：1=成功 0=失败 |
| `startDate` | String | | — | 开始日期，如 `2026-07-01` |
| `endDate` | String | | — | 结束日期 |

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 52,
    "list": [
      {
        "id": 1,
        "userId": 1,
        "username": "admin",
        "module": "用户管理",
        "operation": "删除用户",
        "description": "删除了用户zhangsan(ID:3)",
        "method": "DELETE",
        "requestUri": "/api/admin/users/3",
        "classMethod": "com.yimian.system.controller.admin.AdminUserController.deleteUser",
        "requestParams": "{\"id\":3}",
        "ip": "192.168.1.100",
        "userAgent": "Mozilla/5.0 ...",
        "responseCode": 200,
        "resultCode": 200,
        "resultMsg": "操作成功",
        "errorMsg": null,
        "duration": 245,
        "status": 1,
        "createdAt": "2026-07-12 15:30:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 日志 ID |
| `userId` | Long | 操作人 ID |
| `username` | String | 操作人用户名 |
| `module` | String | 模块名称 |
| `operation` | String | 操作类型 |
| `description` | String | 操作描述（含具体数据） |
| `method` | String | HTTP 方法 |
| `requestUri` | String | 请求路径 |
| `classMethod` | String | 全限定方法名 |
| `requestParams` | String | 请求参数 JSON（敏感字段已脱敏） |
| `ip` | String | 客户端 IP |
| `userAgent` | String | User-Agent |
| `responseCode` | Integer | HTTP 状态码 |
| `resultCode` | Integer | 业务码 |
| `resultMsg` | String | 业务消息 |
| `errorMsg` | String | 异常信息 |
| `duration` | Long | 执行耗时（毫秒） |
| `status` | Integer | 1=成功 0=失败 |
| `createdAt` | String | 日志创建时间 |

> 注意：日志只追加不修改不删除，表中无 `deleted` 字段。

---

## 8. 日志模块字典管理（管理员）

> **接口前缀**：`/api/admin/logs/modules`
> **认证要求**：需携带 Token，且具有 `ROLE_ADMIN` 角色。

### 8.1 启用的模块列表（下拉框用）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/admin/logs/modules/enabled` |
| **请求方式** | `GET` |
| **认证要求** | 需认证（无需额外权限） |

返回 `enabled=1` 的模块，按 `sort` 排序。

### 8.2 全量模块列表（管理用）

| GET | `/api/admin/logs/modules` | 需 `log:module:list` |

### 8.3 模块详情

| GET | `/api/admin/logs/modules/{id}` | 需 `log:module:list` |

### 8.4 新增模块

| POST | `/api/admin/logs/modules` | 需 `log:module:edit` |

```json
{ "code": "USER", "name": "用户管理", "description": "用户CRUD", "sort": 1, "enabled": 1 }
```

### 8.5 编辑模块

| PUT | `/api/admin/logs/modules/{id}` | 需 `log:module:edit` |

```json
{ "name": "账号管理", "sort": 2 }
```

### 8.6 删除模块

| DELETE | `/api/admin/logs/modules/{id}` | 需 `log:module:edit` |

物理删除。

> **工作原理**：
> 1. `@OperationLog(module="USER", operation="新增用户")` 注解传编码
> 2. AOP 把编码写入 `sys_operation_log.module`
> 3. 查询日志列表时，从 `LogModuleCache`（内存）翻译编码为 `moduleName` 返回前端
> 4. 应用启动时自动扫描所有 `@OperationLog` 注解，把新编码 upsert 到 `sys_log_module`

---

## 9. 文件上传（通用工具）

> 通用文件上传模块，按业务场景分目录存储到服务器 `oss/` 文件夹。
> 工具接口不做文件类型判断，由各业务接口自行限制允许的后缀和大小。

### 9.1 上传文件

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/file/upload` |
| **请求方式** | `POST` |
| **认证要求** | 需携带 Token |
| **Content-Type** | `multipart/form-data` |

**表单参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `file` | File | ✅ | 上传的文件 |
| `scene` | String | | 业务场景，作为一级子目录，默认 `common`。只允许字母/数字/下划线/中划线 |

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "originalName": "头像.jpg",
    "fileName": "a1b2c3d4e5f6.jpg",
    "path": "avatar/2026/07/13/a1b2c3d4e5f6.jpg",
    "url": "/api/file/avatar/2026/07/13/a1b2c3d4e5f6.jpg",
    "size": 102400,
    "contentType": "image/jpeg",
    "extension": "jpg"
  },
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `originalName` | String | 原始文件名 |
| `fileName` | String | 存储文件名（uuid + 扩展名） |
| `path` | String | 相对存储路径 |
| `url` | String | 完整访问 URL，前端可直接用于展示 |
| `size` | Long | 文件大小（字节） |
| `contentType` | String | MIME 类型 |
| `extension` | String | 扩展名（不含点） |

**存储路径规则**：`oss/{scene}/{yyyy/MM/dd}/{uuid}.{ext}`

**失败响应**：

| code | message | 触发场景 |
|------|------|------|
| 1100 | 上传文件不能为空 | file 为空 |
| 1101 | 文件保存失败 | 磁盘写入异常或超限 |
| 1103 | 非法的文件路径 | scene 含非法字符 |

### 9.2 读取文件

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/file/{scene}/{yyyy}/{MM}/{dd}/{file}` |
| **请求方式** | `GET` |
| **认证要求** | 公开访问，无需 Token |

流式返回文件内容，`Content-Disposition: inline`，前端可直接作为 `<img src>` / `<a href>` 使用。

**失败响应**：

| code | message | 触发场景 |
|------|------|------|
| 1102 | 文件不存在 | 路径对应的文件不存在 |
| 1103 | 非法的文件路径 | 含 `../` 等穿越尝试 |

---

## 10. 错误码汇总

| code | 说明 |
|------|------|
| 200 | 成功 |
| 400 | 参数校验失败 |
| 401 | 未登录 |
| 403 | 无权限 |
| 1001 | 用户名已存在 |
| 1002 | 邮箱已被注册 |
| 1003 | 用户不存在 |
| 1004 | 密码错误 |
| 1005 | 账号已被禁用 |
| 1010 | 角色编码已存在 |
| 1011 | 角色不存在 |
| 1020 | 权限编码已存在 |
| 1021 | 权限不存在 |
| 1030 | 模块编码已存在 |
| 1100 | 上传文件不能为空 |
| 1101 | 文件保存失败 |
| 1102 | 文件不存在 |
| 1103 | 非法的文件路径 |
| 1104 | 文件读取失败 |
| 1201 | 内容已存在，请勿重复上传 |
| 1202 | 知识题目不存在 |
| 1203 | 题目状态不允许此操作 |
| 1301 | 标签不存在 |
| 1302 | 标签名称已存在 |
| 1401 | 收藏夹不存在 |
| 1402 | 无权操作该收藏夹 |
| 1403 | 该题目已收藏到此收藏夹 |
| 1404 | 收藏条目不存在 |
| 1405 | 该收藏夹未公开 |

---

## 11. 知识题目管理

> **接口前缀**：`/api/knowledge`
> **认证要求**：需携带 Token

### 11.1 题目列表（分页）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge` |
| **请求方式** | `GET` |
| **认证要求** | 需携带 Token |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|--------|------|
| `page` | int | | 1 | 页码 |
| `size` | int | | 10 | 每页条数 |
| `keyword` | String | | — | 关键词搜索（标题/内容） |
| `difficulty` | Integer | | — | 难度：1=简单 2=中等 3=困难 |
| `tagId` | Long | | — | 标签 ID 筛选 |
| `status` | Integer | | — | 状态筛选（管理员用） |

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 12,
    "list": [{
      "id": 1,
      "title": "HashMap底层原理与扩容机制",
      "content": "## 核心概念\n\nHashMap...",
      "difficulty": 2,
      "status": 1,
      "auditRemark": null,
      "auditUserId": null,
      "auditTime": null,
      "submitUserId": 2,
      "submitUserName": "zhangsan",
      "viewCount": 10,
      "likeCount": 0,
      "collectCount": 0,
      "commentCount": 0,
      "tags": [{ "id": 1, "name": "Java", "color": "#e74c3c" }],
      "createdAt": "2026-07-14T15:32:37",
      "updatedAt": "2026-07-14T15:32:58"
    }],
    "pageNum": 1,
    "pageSize": 10
  },
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `data.total` | Long | 总条数 |
| `data.list` | Array | 题目列表 |
| `data.list[].id` | Long | 题目 ID |
| `data.list[].title` | String | 标题 |
| `data.list[].content` | String | 正文（Markdown 原文） |
| `data.list[].difficulty` | Integer | 难度：1/2/3 |
| `data.list[].status` | Integer | 状态：0=待审核 1=通过 2=拒绝 3=草稿 |
| `data.list[].auditRemark` | String | 审核意见（拒绝时填写） |
| `data.list[].auditUserId` | Long | 审核人 ID |
| `data.list[].auditTime` | String | 审核时间 |
| `data.list[].submitUserId` | Long | 提交人 ID |
| `data.list[].submitUserName` | String | 提交人显示名，优先昵称，其次用户名；用户记录缺失时为 `用户#{id}` |
| `data.list[].viewCount` | Integer | 浏览次数 |
| `data.list[].likeCount` | Integer | 点赞数 |
| `data.list[].collectCount` | Integer | 收藏次数 |
| `data.list[].commentCount` | Integer | 评论数 |
| `data.list[].tags` | Array | 关联标签列表 |
| `data.pageNum` | Integer | 当前页码 |
| `data.pageSize` | Integer | 每页条数 |

> 注意：列表按 `created_at DESC` 排序。用户端默认不传 `status`，展示所有题目。

### 11.2 题目详情

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/{id}` |
| **请求方式** | `GET` |
| **认证要求** | 需携带 Token |

返回结构同列表项，包含完整 `content`（Markdown 原文）。每次调用自动增加 1 次浏览次数。

**失败响应**：

| code | message | 触发场景 |
|------|------|------|
| 1202 | 知识题目不存在 | id 无效或已被删除 |

### 11.3 热门题目

> 热点存储使用 Redis：浏览、收藏等行为会写入 Redis 计数与排行榜；当 Redis 无数据或不可用时，接口自动使用 MySQL 的 `viewCount/likeCount/collectCount/commentCount` 加时间衰减进行兜底排序。

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 今日热门 | GET | `/api/knowledge/hot/today?limit=10` | 查询今日热门题目 |
| 本周热门 | GET | `/api/knowledge/hot/weekly?limit=10` | 查询本周热门题目 |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|--------|------|
| `limit` | Integer | | 10 | 返回条数，最大 50 |

**成功响应**：

`data` 为 `KnowledgeVO[]`，字段同题目列表项。

**热度规则**：

```text
hotScore = (viewCount*1 + likeCount*3 + collectCount*5 + commentCount*2) * timeDecay
timeDecay = 1 / (1 + days_since_publish * 0.1)
```

当前已接入行为：题目详情浏览、收藏题目。点赞、评论模块实现后调用 `HotDataService.incrLike/incrComment` 接入。

### 11.4 提交题目

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/submit` |
| **请求方式** | `POST` |
| **认证要求** | 需携带 Token |

**Request Body**：

```json
{
  "title": "HashMap底层原理",
  "content": "## 核心概念\n...",
  "difficulty": 2,
  "tagIds": [1, 2]
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ✅ | 标题，最长 200 字 |
| `content` | String | ✅ | 正文（Markdown 原文） |
| `difficulty` | Integer | | 难度：1=简单 2=中等 3=困难，默认 1 |
| `tagIds` | Array | | 标签 ID 列表 |

**业务规则**：受 `audit.enabled` 开关控制。
- 开关关闭时：status 直接设为 1（审核通过）
- 开关开启时：status 设为 0（待审核）
- SHA-256 去重：内容已存在时返回 1201

### 11.5 直接上传（跳过审核）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/direct` |
| **请求方式** | `POST` |
| **权限要求** | `knowledge:direct-upload` |

请求体同 `/submit`，但跳过审核开关，直接 status=1。

### 11.6 我的题目列表

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/my` |
| **请求方式** | `GET` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认值 | 说明 |
|------|------|:---:|--------|------|
| `page` | int | | 1 | 页码 |
| `size` | int | | 10 | 每页条数 |
| `status` | Integer | | — | 状态筛选：0=待审核 1=通过 2=拒绝 3=草稿 |

### 11.7 编辑题目

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/{id}` |
| **请求方式** | `PUT` |

```json
{
  "title": "新标题",
  "content": "新内容",
  "difficulty": 1,
  "tagIds": [1, 3]
}
```

所有字段可选。仅允许编辑自己的题目且状态为草稿(3)或拒绝(2)。

### 11.8 删除题目

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/knowledge/{id}` |
| **请求方式** | `DELETE` |

软删除，仅允许删除自己的题目。

### 11.9 审核管理（管理员）

> **权限要求**：`knowledge:audit`

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 待审核列表 | GET | `/api/admin/knowledge/pending?page=1&size=10&keyword=xxx` | 查询 status=0 的题目，支持关键词筛选 |
| 审核通过 | PUT | `/api/admin/knowledge/{id}/approve` | 设置 status=1，记录审核人 ID 和审核时间 |
| 审核拒绝 | PUT | `/api/admin/knowledge/{id}/reject?remark=原因` | 设置 status=2，需填写拒绝原因，记录审核人 ID 和审核时间 |
| 批量审核 | PUT | `/api/admin/knowledge/batch-audit` | 请求体：`{"ids":[1,2,3],"approve":true,"remark":""}` |
| 审核开关查询 | GET | `/api/knowledge/audit-switch` | 需 `knowledge:audit` |
| 审核开关设置 | PUT | `/api/knowledge/audit-switch?enabled=true` | 需 `knowledge:audit`，运行时生效，重启恢复 |

**审核通过/拒绝成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "title": "HashMap底层原理",
    "content": "## 核心概念\n...",
    "difficulty": 2,
    "status": 1,
    "auditRemark": null,
    "auditUserId": 1,
    "auditTime": "2026-07-14T16:00:00",
    "submitUserId": 2,
    "viewCount": 0,
    "likeCount": 0,
    "collectCount": 0,
    "commentCount": 0,
    "tags": [{ "id": 1, "name": "Java", "color": "#e74c3c" }],
    "createdAt": "2026-07-14T15:32:37",
    "updatedAt": "2026-07-14T16:00:00"
  },
  "timestamp": 1720771200000
}
```

| 新增字段 | 类型 | 说明 |
|------|------|------|
| `auditUserId` | Long | 审核人 ID（通过/拒绝时记录） |
| `auditTime` | String | 审核时间（通过/拒绝时记录） |

**失败响应**：

| code | message | 触发场景 |
|------|------|------|
| 1202 | 知识题目不存在 | id 无效或已被删除 |
| 1203 | 题目状态不允许此操作 | 题目非待审核状态（status ≠ 0） |
| 403 | 无权限访问 | 缺少 `knowledge:audit` 权限 |

---

## 12. 标签管理

> **接口前缀**：`/api/tags`（公开）/ `/api/admin/tags`（管理员）

### 12.1 标签列表（公开）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/tags` |
| **请求方式** | `GET` |
| **认证要求** | 需携带 Token |

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `keyword` | String | | 关键词搜索标签名称 |

**成功响应**：

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [{
    "id": 1,
    "name": "Java",
    "color": "#e74c3c",
    "sort": 1,
    "usageCount": 38,
    "createdAt": "2026-07-14T10:00:00",
    "updatedAt": "2026-07-14T10:00:00"
  }],
  "timestamp": 1720771200000
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 标签 ID |
| `name` | String | 标签名称 |
| `color` | String | 颜色代码（如 `#e74c3c`） |
| `sort` | Integer | 排序号 |
| `usageCount` | Integer | 已审核通过且未删除的公开题目数量 |

### 12.2 管理员标签 CRUD

> **权限要求**：`knowledge:audit`

| 接口 | 方法 | 地址 | 说明 |
|------|------|------|------|
| 新增标签 | POST | `/api/admin/tags` | 请求体：`{"name":"Go","color":"#00ADD8","sort":27}` |
| 编辑标签 | PUT | `/api/admin/tags/{id}` | 请求体同新增，字段可选 |
| 删除标签 | DELETE | `/api/admin/tags/{id}` | 同时删除关联表数据 |

**失败响应**：

| code | message | 触发场景 |
|------|------|------|
| 1301 | 标签不存在 | id 无效 |
| 1302 | 标签名称已存在 | name 重复 |

---

## 13. 收藏夹管理

> **接口前缀**：`/api/favorites`
> **认证要求**：需携带 Token
> **权限脚本**：`system/sql/16-favorite-permissions.sql`

### 13.1 创建收藏夹

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/favorites/folders` |
| **请求方式** | `POST` |
| **权限要求** | `favorite:create` |

**Request Body**：
```json
{
  "name": "Java 高频题",
  "description": "整理 Java 面试常见问题",
  "isPublic": 0,
  "coverImage": null
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `name` | String | ✅ | 收藏夹名称，最长 100 |
| `description` | String | | 描述，最长 500 |
| `isPublic` | Integer | | 是否公开：0=私有 1=公开，默认 0 |
| `coverImage` | String | | 封面图 URL，最长 500 |

**成功响应 `data` 字段**：`FavoriteFolderVO`

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 收藏夹雪花 ID |
| `name` | String | 收藏夹名称 |
| `description` | String | 描述 |
| `userId` | Long | 所属用户 ID |
| `isPublic` | Integer | 是否公开：0=私有 1=公开 |
| `coverImage` | String | 封面图 URL |
| `itemCount` | Integer | 收藏条目数 |
| `viewCount` | Integer | 浏览次数 |
| `createdAt` | String | 创建时间 |
| `updatedAt` | String | 更新时间 |

### 13.2 编辑 / 删除收藏夹

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 编辑收藏夹 | PUT | `/api/favorites/folders/{id}` | `favorite:edit` | 请求体同创建，字段可选，仅允许操作自己的收藏夹 |
| 删除收藏夹 | DELETE | `/api/favorites/folders/{id}` | `favorite:delete` | 软删除自己的收藏夹，并同步移除收藏条目 |

### 13.3 收藏 / 取消收藏题目

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 收藏题目 | POST | `/api/favorites/folders/{folderId}/items` | `favorite:item:add` | 将审核通过的知识题目加入自己的收藏夹 |
| 条目列表/搜索 | GET | `/api/favorites/folders/{folderId}/items?page=1&size=10&keyword=java` | `favorite:view` | 分页查询当前收藏夹内收藏题目，支持标题搜索 |
| 取消收藏 | DELETE | `/api/favorites/folders/{folderId}/items/{itemId}` | `favorite:item:delete` | 从自己的收藏夹移除条目 |

**收藏题目 Request Body**：
```json
{
  "knowledgeId": 1
}
```

**收藏成功响应 `data` 字段**：`FavoriteItemVO`

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 收藏条目 ID |
| `folderId` | Long | 收藏夹 ID |
| `knowledgeId` | Long | 知识题目 ID |
| `sort` | Integer | 排序号 |
| `knowledge` | Object | 知识题目 VO，字段同 `KnowledgeVO` |
| `createdAt` | String | 收藏时间 |
| `updatedAt` | String | 更新时间 |

**条目列表/搜索成功响应**：

`data` 对齐 `PageInfo<FavoriteItemVO>`：

| 字段 | 类型 | 说明 |
|------|------|------|
| `data.list` | Array | 收藏条目列表 |
| `data.total` | Long | 总条数 |
| `data.pageNum` | Integer | 当前页码 |
| `data.pageSize` | Integer | 每页条数 |

### 13.4 收藏夹列表

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 用户公开收藏夹 | GET | `/api/favorites/folders?userId=1` | `favorite:list` | 查看指定用户公开收藏夹 |
| 我的收藏夹 | GET | `/api/favorites/my-folders` | `favorite:list` | 查看当前用户全部收藏夹，包含私有 |

响应 `data` 为 `FavoriteFolderVO[]`。

### 13.5 收藏夹详情

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/favorites/folders/{id}` |
| **请求方式** | `GET` |
| **权限要求** | `favorite:view` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | | 1 | 条目页码 |
| `size` | Integer | | 10 | 每页条数 |
| `keyword` | String | | - | 搜索当前收藏夹内题目标题 |

**成功响应 `data` 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id/name/description/userId/isPublic/coverImage/itemCount/viewCount/createdAt/updatedAt` | - | 同 `FavoriteFolderVO` |
| `items.list` | Array | 收藏条目列表 |
| `items.total` | Long | 总条数 |
| `items.pageNum` | Integer | 当前页码 |
| `items.pageSize` | Integer | 每页条数 |

> 可见性：收藏夹所有者可查看自己的私有/公开收藏夹，其他用户只能查看公开收藏夹。详情接口会增加 `viewCount`，Redis 热度计数后续接入热点模块。

### 13.6 是否已收藏检查

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/favorites/check?knowledgeId=1` |
| **请求方式** | `GET` |
| **权限要求** | `favorite:view` |

**成功响应 `data` 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `collected` | Boolean | 当前用户是否已收藏该题目 |
| `folders` | Array | 已收藏该题目的收藏夹列表 |

### 13.7 失败响应

| code | message | 触发场景 |
|------|------|------|
| 1202 | 知识题目不存在 | knowledgeId 无效、已删除或未审核通过 |
| 1401 | 收藏夹不存在 | folderId/id 无效或已删除 |
| 1402 | 无权操作该收藏夹 | 编辑、删除、增删条目时不是收藏夹所有者 |
| 1403 | 该题目已收藏到此收藏夹 | 同一收藏夹重复收藏同一题目 |
| 1404 | 收藏条目不存在 | itemId 无效或不属于该收藏夹 |
| 1405 | 该收藏夹未公开 | 非所有者查看私有收藏夹 |
| 403 | 无权限访问 | 缺少对应 `favorite:*` 权限 |

---

## 14. 博客讨论

> **接口前缀**：`/api/blogs`
> **认证要求**：需携带 Token
> **权限脚本**：`system/sql/18-blog-permissions.sql`、`system/sql/19-init-topic.sql`、`system/sql/20-blog-images.sql`、`system/sql/23-blog-topic-category.sql`

### 14.1 博客列表

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/blogs` |
| **请求方式** | `GET` |
| **权限要求** | `blog:list` |

**Query 参数**：

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | | 1 | 页码 |
| `size` | Integer | | 10 | 每页条数，最大 50 |
| `keyword` | String | | - | 搜索标题、摘要、正文、话题名 |
| `sort` | String | | `newest` | `newest` / `hot` |
| `refType` | String | | - | `knowledge` / `folder` |

成功响应 `data` 对齐 `PageInfo<BlogVO>`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。

### 14.2 发布博客 / 保存草稿

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/blogs` |
| **请求方式** | `POST` |
| **权限要求** | `blog:create` |

**Request Body**：

```json
{
  "title": "一次 Redis 缓存击穿排查复盘",
  "content": "## 背景\n...",
  "summary": "可选摘要，不填自动截取正文",
  "images": ["/api/file/blog/xxx.jpg", "/api/file/blog/yyy.jpg"],
  "status": 1,
  "refType": "knowledge",
  "refId": 1,
  "topicIds": [6, 14]
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | ✅ | 标题，最长 200 |
| `content` | String | ✅ | Markdown 正文 |
| `summary` | String | | 摘要，最长 500，不填自动生成 |
| `images` | Array | | 博客配图 URL 列表，至多 9 张 |
| `status` | Integer | | 0=草稿，1=发布，默认 1 |
| `refType` | String | | `knowledge` / `folder`，不传表示不关联 |
| `refId` | Long | | 关联对象 ID |
| `topicIds` | Array | | 话题 ID 列表，最多 8 个 |

> 关联校验：`knowledge` 必须是已审核通过题目；`folder` 必须是**自己公开的**收藏夹。

### 14.3 编辑 / 删除 / 我的博客

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 编辑博客 | PUT | `/api/blogs/{id}` | `blog:edit` | 请求体同发布，字段可选，仅作者可编辑 |
| 删除博客 | DELETE | `/api/blogs/{id}` | `blog:delete` | 软删除，同时删除图片和话题关联，仅作者可删除 |
| 我的博客 | GET | `/api/blogs/my?page=1&size=10&status=0` | `blog:list` | 查询当前用户博客，`status` 可选 |

### 14.4 管理员博客管理

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 后台博客列表 | GET | `/api/admin/blogs?page=1&size=10&keyword=&status=` | `ROLE_ADMIN` + `blog:list` | 查询未删除博客，`status` 可选：0=草稿、1=已发布、2=审核中、3=已屏蔽 |
| 更新博客状态 | PUT | `/api/admin/blogs/{id}/status?status=1` | `ROLE_ADMIN` + `blog:edit` | 管理员更新博客状态；改为已发布时会补齐 `publishedAt` |
| 后台删除博客 | DELETE | `/api/admin/blogs/{id}` | `ROLE_ADMIN` + `blog:delete` | 软删除博客，并清理图片和话题关联 |

列表成功响应 `data` 对齐 `PageInfo<BlogVO>`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。

### 14.5 博客详情

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/blogs/{id}` |
| **请求方式** | `GET` |
| **权限要求** | `blog:view` |

返回 `BlogVO`，包含作者信息、关联对象摘要和完整 Markdown 正文。新建博客的 `id` 为雪花 ID。已发布博客每次访问会增加 `viewCount`；草稿仅作者可见。

**BlogVO 字段**：

| 字段 | 类型 | 说明 |
|------|------|------|
| `id/title/content/summary` | - | 博客基础内容，其中 `id` 为博客雪花 ID |
| `authorId/authorName/authorAvatar` | - | 作者信息 |
| `status` | Integer | 0=草稿 1=已发布 2=审核中 3=已屏蔽 |
| `isPinned` | Integer | 是否置顶 |
| `viewCount/likeCount/commentCount` | Integer | 统计字段 |
| `refType/refId/ref` | - | 关联对象信息，`ref` 含 `type/id/title/description/coverImage` |
| `images` | Array | 博客配图 URL 列表，按 sort 升序 |
| `topics` | Array | 关联话题列表，每个元素含 `id/name/color` |
| `publishedAt/createdAt/updatedAt` | String | 时间字段 |

### 14.6 失败响应

| code | message | 触发场景 |
|------|------|------|
| 1501 | 博客不存在 | id 无效或已删除 |
| 1502 | 无权操作该博客 | 编辑/删除非本人博客，或查看他人草稿 |
| 1503 | 博客关联对象不存在或不可见 | refType/refId 无效 |
| 403 | 无权限访问 | 缺少对应 `blog:*` 权限 |

---

## 15. 话题模块

> **接口前缀**：`/api/topics`
> **权限脚本**：`system/sql/19-init-topic.sql`、`system/sql/23-blog-topic-category.sql`

### 15.1 话题列表（公开）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/topics` |
| **请求方式** | `GET` |
| **认证要求** | 无需认证 |

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `keyword` | String | | 搜索话题名称 |

**成功响应**：

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "name": "Java 基础",
      "description": "Java 语言核心概念与基础语法",
      "color": "#e74c3c",
      "sort": 1,
      "blogCount": 12,
      "createdAt": "2026-07-15T10:00:00",
      "updatedAt": "2026-07-15T10:00:00"
    }
  ]
}
```

不传 `keyword` 时，默认优先返回 Redis 热门话题榜（`hot:topic:rank`）中的话题；Redis 无数据时回退为数据库 `blogCount` 排序。传入 `keyword` 时按数据库话题名称搜索。

### 15.2 创建话题（用户端）

| 项目 | 值 |
|------|-----|
| **接口地址** | `/api/topics` |
| **请求方式** | `POST` |
| **权限要求** | `blog:create` |

**Query 参数**：

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `name` | String | 是 | 话题名称，最长 50 字符 |
| `description` | String | | 话题描述 |
| `color` | String | | 话题颜色 |

如果同名话题已存在，接口直接返回已有 `TopicVO`；否则创建新话题并返回。

### 15.3 话题管理（管理员）

| 接口 | 方法 | 地址 | 权限 | 说明 |
|------|------|------|------|------|
| 新增话题 | POST | `/api/admin/topics?name=&description=&color=` | `topic:create` | name 必填，最长 50 |
| 编辑话题 | PUT | `/api/admin/topics/{id}` | `topic:edit` | 参数同新增，额外支持 `sort` |
| 删除话题 | DELETE | `/api/admin/topics/{id}` | `topic:delete` | 软删除 |

> 前端已新增博客图片上传、话题选择、收藏夹公开过滤等功能。

---

## 16. 收藏夹支持博客补充

> 相关 SQL：`system/sql/24-favorite-blog-item.sql`

收藏夹条目已从“仅题目”扩展为“内容条目”，支持题目和博客两类：

```json
{
  "itemType": "knowledge",
  "targetId": 1
}
```

兼容旧题目收藏请求：

```json
{
  "knowledgeId": 1
}
```

收藏博客请求：

```json
{
  "itemType": "blog",
  "targetId": 1,
  "blogId": 1
}
```

`FavoriteItemVO` 新增/支持字段：`itemType`、`targetId`、`knowledgeId`、`blogId`、`knowledge`、`blog`。收藏夹条目列表 `data.list` 中，`itemType=knowledge` 时读取 `knowledge`，`itemType=blog` 时读取 `blog`。

博客详情收藏接口 `POST /api/blogs/{id}/collect?folderId=1` 会继续写入 `sys_blog_collect`，同时同步创建 `sys_favorite_item(itemType='blog')`，因此收藏夹详情页可以直接展示已收藏博客。

---

## 17. 评论模块

> 接口前缀：`/api/comments`
> 认证要求：需携带 Token
> 权限脚本：`system/sql/25-comment-system.sql`

评论支持题目和博客两类目标，支持评论点赞、回复评论。列表默认按热点排序，也可切换为最新评论。

### 17.1 评论列表

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/comments` |
| 请求方式 | `GET` |
| 权限要求 | `comment:view` |

Query 参数：

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `targetType` | String | 是 | - | `knowledge` / `blog` |
| `targetId` | Long | 是 | - | 目标 ID |
| `page` | Integer | 否 | 1 | 主评论页码 |
| `size` | Integer | 否 | 20 | 每页主评论数，最大 50 |
| `sort` | String | 否 | `hot` | `hot` / `newest` |

响应 `data` 对齐 `PageInfo<CommentVO>`，`data.list` 为主评论列表，每条主评论的 `replies` 包含其回复。

`CommentVO` 字段：`id`、`targetType`、`targetId`、`parentId`、`replyToCommentId`、`replyToUserId`、`replyToNickname`、`authorId`、`authorNickname`、`authorAvatar`、`content`、`likeCount`、`replyCount`、`liked`、`createdAt`、`updatedAt`、`replies`。

热点排序规则：主评论按 `likeCount * 3 + replyCount * 2` 倒序，再按创建时间倒序。

### 17.2 发表评论 / 回复

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/comments` |
| 请求方式 | `POST` |
| 权限要求 | `comment:create` |

发表评论：

```json
{
  "targetType": "blog",
  "targetId": 1,
  "content": "写得很清楚"
}
```

回复评论：

```json
{
  "targetType": "blog",
  "targetId": 1,
  "content": "我也这么理解",
  "parentId": 10,
  "replyToCommentId": 12,
  "replyToUserId": 3
}
```

创建成功后会同步更新目标 `commentCount`；题目评论还会写入热点统计。

### 17.3 评论点赞 / 取消点赞

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/comments/{id}/like` |
| 请求方式 | `POST` |
| 权限要求 | `comment:like` |

该接口为 toggle：未点赞时点赞，已点赞时取消点赞。

响应 `data`：

```json
{
  "likeCount": 12,
  "liked": true
}
```


---

## 18. 消息通知模块

> 接口前缀：/api/notifications
---

## 22. 系统公告

> 相关 SQL：`system/sql/37-system-announcement.sql`

管理员可发布系统公告。公告会投递到用户通知表：
- `important=true`：用户进入用户端页面时弹窗提醒，同时也进入消息中心。
- `important=false`：仅进入消息中心和头像下拉通知列表。

### 22.1 管理端发布系统公告

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/announcements` |
| 请求方式 | `POST` |
| 权限要求 | `ROLE_ADMIN` 或 `announcement:create` |

**Request Body**

```json
{
  "title": "系统维护通知",
  "content": "今晚 23:00-23:30 将进行系统维护，期间可能出现短暂不可用。",
  "important": true
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `title` | String | 是 | 公告标题，最长 120 字 |
| `content` | String | 是 | 公告内容，最长 2000 字 |
| `important` | Boolean | 是 | 是否重要；重要公告会弹窗 |

成功响应 `data` 为 `SystemAnnouncementVO`。发布成功后，会向所有启用用户写入 `sys_notification`，通知字段为 `type=system_announcement`、`targetType=system_announcement`、`targetId=公告ID`。

### 22.2 管理端公告列表

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/announcements` |
| 请求方式 | `GET` |
| 权限要求 | `ROLE_ADMIN` 或 `announcement:list` |

**Query 参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | 否 | 1 | 页码 |
| `size` | Integer | 否 | 10 | 每页条数，最大 50 |
| `keyword` | String | 否 | - | 搜索标题或内容 |
| `important` | Boolean | 否 | - | 是否重要 |

成功响应 `data` 对齐 `PageInfo<SystemAnnouncementVO>`：`data.list`、`data.total`、`data.pageNum`、`data.pageSize`。

### 22.3 用户端未读重要公告

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/announcements/important-unread` |
| 请求方式 | `GET` |
| 认证要求 | 需要登录 |

返回当前用户未确认的重要公告列表。成功响应 `data` 为 `SystemAnnouncementVO[]`，其中 `notificationId` 为对应通知 ID。

### 22.4 用户端确认重要公告

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/announcements/{id}/confirm` |
| 请求方式 | `POST` |
| 认证要求 | 需要登录 |

确认后会把当前用户对应的 `system_announcement` 通知标记为已读；公告仍保留在消息中心历史记录中。

### 22.5 SystemAnnouncementVO 字段

| 字段 | 类型 | 说明 |
|------|------|------|
| `id` | Long | 公告 ID，JSON 中按字符串返回 |
| `notificationId` | Long | 当前用户对应通知 ID，仅用户端未读重要公告接口返回 |
| `title` | String | 公告标题 |
| `content` | String | 公告内容 |
| `important` | Boolean | 是否重要 |
| `enabled` | Boolean | 是否启用 |
| `creatorId` | Long | 发布人 ID，JSON 中按字符串返回 |
| `creatorName` | String | 发布人显示名 |
| `createdAt` | String | 创建时间 |
| `updatedAt` | String | 更新时间 |
---

## 23. 邮箱验证码注册与忘记密码

> 相关 SQL：`system/sql/38-user-email-unique.sql`

### 23.1 发送注册邮箱验证码

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/auth/email-code/register` |
| 请求方式 | `POST` |
| 认证要求 | 无需认证 |

**Request Body**

```json
{
  "email": "user@example.com"
}
```

邮箱必须未被注册。验证码写入 Redis，key 形如 `auth:email-code:register:{email}`，有效期 5 分钟，同邮箱同用途发送冷却 60 秒。

### 23.2 邮箱验证码注册

`/api/auth/register` 的请求体更新为：

```json
{
  "username": "zhangsan",
  "password": "Aa123456",
  "email": "zhangsan@example.com",
  "emailCode": "123456",
  "phone": "13800138000",
  "nickname": "张三"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `username` | String | 是 | 用户名，4-64 位，全局唯一 |
| `password` | String | 是 | 密码，6-32 位，必须至少包含一个大写字母、一个小写字母和一个数字，BCrypt 加密存储 |
| `email` | String | 是 | 邮箱，全局唯一 |
| `emailCode` | String | 是 | 6 位邮箱验证码，注册成功后删除 |
| `phone` | String | 否 | 手机号，最长 20 位 |
| `nickname` | String | 否 | 昵称，不传则默认用户名 |

### 23.3 发送重置密码邮箱验证码

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/auth/email-code/reset-password` |
| 请求方式 | `POST` |
| 认证要求 | 无需认证 |

**Request Body**

```json
{
  "email": "user@example.com"
}
```

邮箱必须对应一个启用中的账号。验证码写入 Redis，key 形如 `auth:email-code:reset-password:{email}`，有效期 5 分钟，同邮箱同用途发送冷却 60 秒。

### 23.4 邮箱验证码重置密码

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/auth/password/reset` |
| 请求方式 | `POST` |
| 认证要求 | 无需认证 |

**Request Body**

```json
{
  "email": "user@example.com",
  "emailCode": "123456",
  "newPassword": "NewPass123"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `email` | String | 是 | 注册邮箱 |
| `emailCode` | String | 是 | 6 位邮箱验证码，重置成功后删除 |
| `newPassword` | String | 是 | 新密码，6-32 位，必须至少包含一个大写字母、一个小写字母和一个数字 |

### 23.5 失败响应补充

| code | message | 触发场景 |
|------|------|------|
| `1002` | 邮箱已被注册 | 注册验证码发送或注册时邮箱重复 |
| `1003` | 用户不存在 | 找回密码邮箱不存在 |
| `1005` | 账号已被禁用 | 找回密码邮箱对应账号未启用 |
| `1008` | 邮箱验证码错误或已过期 | 验证码不存在、过期或不匹配 |
| `1009` | 验证码发送过于频繁，请稍后再试 | 60 秒冷却内重复发送 |

---

## 24. @ 好友提醒

好友定义为互相关注：当前用户关注对方，且对方也关注当前用户。@ 提醒复用通知表 `sys_notification`，不新增数据库表。

### 24.1 查询可 @ 的互关好友

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/user/mentionable-friends` |
| 请求方式 | `GET` |
| 权限要求 | `user:view-public` |

**Query 参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `keyword` | String | 否 | - | 按 `username` 或 `nickname` 模糊搜索 |
| `limit` | Integer | 否 | 10 | 最大 20 |

成功响应 `data` 为 `UserVO[]`，字段同公开用户资料，`email`、`phone`、`roles`、`permissions` 等敏感字段不会返回。

### 24.2 @ 通知规则

评论、博客正文、题目正文中输入 `@昵称` 或 `@username` 时，后端会解析文本并只给互关好友创建通知。非互关用户即使被写进文本，也不会收到通知。

通知字段：

| 字段 | 值 |
|------|-----|
| `type` | `mention` |
| `senderId` | 发起 @ 的用户 ID |
| `receiverId` | 被 @ 的互关好友 ID |
| `targetType` | `knowledge` 或 `blog` |
| `targetId` | 对应题目或博客 ID |
| `extra` | JSON 字符串，包含 `sourceType`、`sourceId`、`targetType`、`targetId`；其中 ID 字段按字符串写入，避免前端长整型精度丢失 |

消息中心可通过 `/api/notifications?type=mention&page=1&size=20` 查看 “@我” 列表。

---

## 25. Agent 服务代理

Agent 服务部署在独立服务器。浏览器只访问 Spring 后端的 `/api/agent/**`，Spring 完成 JWT 鉴权、用户级限流和操作审计后，再调用 `AGENT_SERVICE_BASE_URL` 指向的 Agent 服务。客户端不能提交或覆盖 `userId`。

### 25.1 部署配置

Spring 后端配置：

| 环境变量 | 默认值 | 说明 |
|------|------|------|
| `AGENT_SERVICE_ENABLED` | `false` | 是否启用 Agent 代理；未启用时接口返回 HTTP 503 |
| `AGENT_SERVICE_BASE_URL` | `http://127.0.0.1:8000` | Agent 服务根地址，可配置为独立服务器 IP，例如 `http://10.0.0.8:8000` |
| `AGENT_SERVICE_INTERNAL_TOKEN` | 空 | Spring 与 Agent 之间的内部 Bearer Token；启用 Agent 时必填且至少 32 个字符 |
| `AGENT_SERVICE_CONNECT_TIMEOUT` | `5s` | 建立连接超时 |
| `AGENT_SERVICE_READ_TIMEOUT` | `120s` | 非流式请求读取超时 |
| `AGENT_SERVICE_STREAM_READ_TIMEOUT` | `10m` | SSE 流式请求读取超时 |
| `AGENT_SERVICE_RATE_LIMIT` | `20` | 单用户在一个窗口内最多发起的对话数 |
| `AGENT_SERVICE_RATE_WINDOW` | `1m` | Redis 限流窗口 |

Agent 服务配置：

| 环境变量 | 默认值 | 说明 |
|------|------|------|
| `AGENT_INTERNAL_TOKEN` | 空 | 必须与 Spring 的 `AGENT_SERVICE_INTERNAL_TOKEN` 完全一致且至少 32 个字符；无效时 `/api/chat/**` 拒绝服务 |
| `AGENT_TRUSTED_SERVICE` | `system` | Agent 要求的 `X-Yimian-Service` 请求头值 |

Spring 调用 Agent 时会携带 `Authorization: Bearer <internal-token>`、`X-Yimian-Service: system` 和 `X-Request-Id`。`X-Request-Id` 为 16-64 位字母、数字、下划线或连字符，当前 Spring 生成 32 位十六进制值。Agent 使用常量时间比较内部令牌，任一认证头不合法都会拒绝请求。生产环境应使用 HTTPS、内网或 VPN，并在 Agent 服务器防火墙中仅允许 Spring 服务器访问 Agent 端口。

浏览器不得直接调用 Agent。Agent 不开放跨域访问，`/api/chat/**` 仅接受 Spring 内部认证请求。

用户会话不会直接以原始 `sessionId` 写入 Agent。Spring 使用内部令牌对 `userId:sessionType:sessionId` 生成 HMAC 会话键，保证不同用户、不同会话类型之间的 Agent 上下文隔离。

Agent 会话和消息持久化在 MySQL 的 `agent_chat_session`、`agent_chat_message` 表中，相关迁移为 `agent/sql/002-add-chat-session-type.sql`。`session_type=support` 表示用户正式会话，可进入后续用户记忆；`session_type=quick` 表示右下角快捷助手会话，消息仍落库供系统审计，但不会进入用户正式会话列表或长期记忆。

### 25.2 非流式对话

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/agent/chat` |
| 请求方式 | `POST` |
| 认证要求 | 携带用户 JWT |
| Content-Type | `application/json` |

**Request Body**

```json
{
  "sessionId": "chat_01JABCDEF",
  "sessionType": "support",
  "message": "请根据知识库解释 Java 线程池的核心参数"
}
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|:---:|------|
| `sessionId` | String | 是 | 1-64 位，只能包含字母、数字、下划线和连字符；同一会话复用 |
| `sessionType` | String | 否 | `support`=用户正式会话，`quick`=系统可见快捷对话；默认 `support` |
| `message` | String | 是 | 用户消息，最长 4000 个字符 |

成功响应 `data`：

```json
{
  "reply": "线程池的核心参数包括……",
  "toolCalls": [],
  "sessionId": "chat_01JABCDEF",
  "requestId": "7bd7bf1d73674930b08f148b4a5dc11b"
}
```

### 25.3 SSE 流式对话

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/agent/chat/stream` |
| 请求方式 | `POST` |
| 认证要求 | 携带用户 JWT |
| Content-Type | `application/json` |
| Accept | `text/event-stream` |

请求体与非流式接口相同。浏览器应发送 `Accept: text/event-stream, application/json`，以便成功时消费 SSE、流建立前失败时读取统一 JSON 错误。响应事件：

| event | data | 说明 |
|------|------|------|
| `meta` | `{"requestId":"...","sessionId":"..."}` | Spring 首先返回的请求追踪信息 |
| `token` | `{"content":"..."}` | 模型增量文本 |
| `tool_call` | `{"name":"...","args":{}}` | Agent 工具调用提示；基础对话阶段不会产生 |
| `done` | `{"reply":"...","session_id":"...","requestId":"..."}` | 对话完成；`session_id` 已由 Spring 改写为前端原始值 |
| `error` | `{"error":"Agent 服务暂时不可用","requestId":"..."}` | 流建立后的失败事件，不暴露上游异常详情 |

成功响应由 Controller 显式设置为 `text/event-stream`，并返回 `Cache-Control: no-cache, no-transform` 与 `X-Accel-Buffering: no`，Spring 收到 Agent 的每个完整 SSE 事件后立即写出并刷新响应。如果在流建立前发生配置、限流或鉴权错误，后端仍按统一 JSON 错误结构和对应 HTTP 状态返回。Servlet 的 `ASYNC` 二次分发不重复鉴权，初始 HTTP 请求始终需要有效用户 JWT。

### 25.4 清理会话

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/agent/sessions/{sessionId}` |
| 请求方式 | `DELETE` |
| 认证要求 | 携带用户 JWT |

Spring 只会清理当前登录用户对应的 HMAC 会话，不允许跨用户清理。

### 25.5 失败响应

| HTTP 状态 | code | message | 场景 |
|------|------|------|------|
| `429` | `1705` | Agent 请求过于频繁，请稍后重试 | 超过 Redis 用户级限流，响应带 `Retry-After` |
| `502` | `1706` | Agent 服务返回异常 | Agent 返回无法接受的 4xx 或响应结构异常 |
| `503` | `1701` | Agent 服务未启用 | `AGENT_SERVICE_ENABLED=false` |
| `503` | `1702` | Agent 服务配置不完整 | 启用后未配置内部令牌 |
| `503` | `1703` | Agent 服务暂时不可用 | Agent 无法连接、上游 5xx 或 Redis 限流服务不可用 |
| `504` | `1704` | Agent 服务响应超时 | 连接成功但在配置时间内没有完成响应 |

Agent 内部接口统一错误结构如下。Spring 不会将内部错误详情直接透传给浏览器，而是映射为上表中的公开错误。

```json
{
  "error": {
    "code": "UNAUTHORIZED_SERVICE",
    "message": "Service authentication failed",
    "request_id": "7bd7bf1d73674930b08f148b4a5dc11b"
  }
}
```

| Agent HTTP 状态 | 内部错误码 | 场景 |
|------|------|------|
| `400` | `INVALID_REQUEST_ID` | `X-Request-Id` 缺失或格式错误 |
| `401` | `UNAUTHORIZED_SERVICE` | 内部 Bearer Token 缺失或错误 |
| `403` | `UNTRUSTED_SERVICE` | `X-Yimian-Service` 不受信任 |
| `422` | `VALIDATION_ERROR` | 请求体或路径参数不符合内部契约 |
| `502` | `MODEL_CALL_FAILED` | Agent 调用模型失败 |
| `503` | `SERVICE_MISCONFIGURED` | Agent 未配置内部令牌 |
| `500` | `INTERNAL_ERROR` | Agent 未预期内部错误 |

### 25.6 管理端会话与 Token 审计

管理端接口只允许具备对应权限的管理员访问。Spring 使用内部认证调用 Agent 的只读审计接口，浏览器不会取得 Agent 内部令牌。响应不会包含 `session_key`、消息 `dedupe_key`、`payload` 或 `metadata`。

#### 25.6.1 会话统计

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/agent/conversations/stats` |
| 请求方式 | `GET` |
| 权限要求 | `agent:conversation:list` |

成功响应 `data`：

```json
{
  "sessionCount": 12,
  "activeSessionCount": 9,
  "supportSessionCount": 7,
  "quickSessionCount": 5,
  "messageCount": 42,
  "inputTokens": 3800,
  "outputTokens": 6200,
  "reasoningTokens": 0,
  "cachedInputTokens": 0,
  "totalTokens": 10000,
  "estimatedMessageCount": 42,
  "totalCostUsd": 0.00000000
}
```

`estimatedMessageCount` 是 `tokenEstimated=true` 的消息条数，不是 Token 数量。当前本地 tokenizer 统计属于估算值，后续接入模型供应商 usage 后可记录精确值。

#### 25.6.2 会话列表

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/agent/conversations` |
| 请求方式 | `GET` |
| 权限要求 | `agent:conversation:list` |

**Query 参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | 否 | 1 | 页码，最小 1 |
| `size` | Integer | 否 | 20 | 每页数量，1-100 |
| `userId` | Long | 否 | - | 精确筛选用户 ID |
| `sessionType` | String | 否 | - | `support` 或 `quick` |
| `status` | Integer | 否 | - | `0`=活跃，`1`=归档 |

成功响应分页字段固定为 `data.list`、`data.total`、`data.pageNum`、`data.pageSize`。会话列表默认包含快捷会话、归档会话和软删除后的审计记录。所有数据库 BIGINT ID 以字符串返回，避免 JavaScript 精度丢失。

会话项包含 `id`、`userId`、`sessionType`、`scene`、`title`、`status`、`deleted`、`messageCount`、`inputTokens`、`outputTokens`、`reasoningTokens`、`cachedInputTokens`、`totalTokens`、`totalCostUsd`、`modelProvider`、`modelName`、`lastActiveAt`、`createdAt`、`archivedAt`、`deletedAt`。

#### 25.6.3 会话消息详情

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/agent/conversations/{sessionId}` |
| 请求方式 | `GET` |
| 权限要求 | `agent:conversation:view` |

**Query 参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `page` | Integer | 否 | 1 | 消息页码 |
| `size` | Integer | 否 | 100 | 每页消息数量，1-200 |

成功响应 `data.session` 为会话项，`data.messages` 使用标准分页结构。消息项包含角色、正文、状态、模型、逐项 Token、`tokenEstimated`、延迟、成本、完成原因和错误码。会话不存在时返回 HTTP `404`、业务码 `1707`。

Agent 内部对应接口为 `/api/chat/admin/stats`、`/api/chat/admin/sessions` 和 `/api/chat/admin/sessions/{sessionId}`，仅接受 Spring 内部认证，不对浏览器开放。

### 25.7 AI 模型控制台

浏览器只访问 System 管理接口，System 使用内部令牌代理 Agent。API Key 仅在创建或编辑提交时传输，Agent 加密存储，查询只返回 `credentialConfigured` 和 `credentialMasked`。

| 接口 | 方法 | 权限 | 说明 |
|------|------|------|------|
| `/api/admin/agent/models/providers` | `GET` | `agent:model:list` | 查询模型供应商 |
| `/api/admin/agent/models/providers` | `POST` | `agent:model:manage` | 新增供应商和 API Key |
| `/api/admin/agent/models/providers/{id}` | `PUT` | `agent:model:manage` | 修改供应商，空 API Key 表示保留原值 |
| `/api/admin/agent/models/providers/{id}/test` | `POST` | `agent:model:manage` | 测试供应商连通性 |
| `/api/admin/agent/models` | `GET` | `agent:model:list` | 查询模型部署 |
| `/api/admin/agent/models` | `POST` | `agent:model:manage` | 新增模型 |
| `/api/admin/agent/models/{id}` | `PUT` | `agent:model:manage` | 修改模型 |
| `/api/admin/agent/models/profiles` | `GET` | `agent:model:list` | 查询 Agent profile |
| `/api/admin/agent/models/profiles/{profileCode}/models` | `GET` | `agent:model:list` | 查询 profile 可用模型 |

供应商字段：`providerCode`、`displayName`、`protocolType`、`baseUrl`、`apiKey`、`status`。`protocolType` 当前支持 `openai_compatible` 和 `anthropic`。模型字段包括 `modelCode`、`providerAccountId`、`upstreamModelName`、`displayName`、`modelType`、上下文窗口、最大输出 Token、能力参数和价格参数。

Agent 内部对应接口前缀为 `/api/ai`，只接受 System 内部认证。Agent 需要配置 `AGENT_CREDENTIAL_ENCRYPTION_KEY`，建议使用 Fernet 随机密钥。连通性测试默认请求供应商的 `{baseUrl}/models`，默认禁止访问内网地址，可通过 Agent 环境变量显式开启。

### 25.8 AI 用量统计

AI 用量统计与会话审计独立展示。统计数据从现有 Agent 消息和会话表实时聚合，不包含消息正文、请求载荷或内部 metadata。

| 项目 | 值 |
|------|-----|
| 接口地址 | `/api/admin/agent/statistics/usage` |
| 请求方式 | `GET` |
| 权限要求 | `agent:statistics:view` |

**Query 参数**

| 参数 | 类型 | 必填 | 默认 | 说明 |
|------|------|:---:|------|------|
| `days` | Integer | 否 | 7 | 统计周期，仅支持 `1`、`7`、`30`；1 天按小时聚合，7/30 天按天聚合 |

成功响应 `data`：

```json
{
  "days": 7,
  "granularity": "day",
  "startAt": "2026-08-18 00:00:00",
  "endAt": "2026-08-24 20:30:00",
  "summary": {
    "sessionCount": 18,
    "activeUserCount": 12,
    "messageCount": 96,
    "inputTokens": 24500,
    "outputTokens": 31800,
    "reasoningTokens": 0,
    "cachedInputTokens": 1200,
    "totalTokens": 57500,
    "estimatedMessageCount": 84,
    "totalCostUsd": 0.13600000,
    "averageLatencyMs": 862.5
  },
  "tokenTrend": [
    {
      "bucketStart": "2026-08-18 00:00:00",
      "sessionCount": 3,
      "messageCount": 12,
      "inputTokens": 3200,
      "outputTokens": 4100,
      "reasoningTokens": 0,
      "cachedInputTokens": 100,
      "totalTokens": 7400,
      "totalCostUsd": 0.01800000
    }
  ],
  "modelUsage": [
    {
      "modelProvider": "deepseek",
      "modelName": "deepseek-chat",
      "messageCount": 96,
      "totalTokens": 57500,
      "totalCostUsd": 0.13600000,
      "averageLatencyMs": 862.5
    }
  ],
  "sessionTypeUsage": [
    {
      "sessionType": "support",
      "sessionCount": 10,
      "messageCount": 64,
      "totalTokens": 40200,
      "totalCostUsd": 0.09200000
    }
  ]
}
```

`tokenTrend` 会补齐时间范围内没有用量的小时或日期，前端可以直接绘图。`sessionCount` 和 `activeUserCount` 表示周期内实际产生消息的会话与用户数量。Agent 内部对应接口为 `/api/chat/admin/analytics?days=...`。
