# CLAUDE.md

## 约束规则

### API 文档同步

每次新增或修改 API 接口时，必须同步更新 `api.md`，包含以下内容：

- **接口地址**：完整路径（如 `/api/auth/login`）
- **请求方式**：GET / POST / PUT / DELETE
- **认证要求**：是否需要携带 Token
- **输入参数**：Request Body / Query Param 的字段名、类型、必填、说明，附 JSON 示例
- **输出参数**：Response Body 的字段名、类型、说明，附成功响应 JSON 示例
- **失败响应**：列出可能的错误码及触发场景

### SQL 脚本管理

- SQL 脚本存放在 `sql/` 目录，编号递增（如 `08-xxx.sql`、`09-xxx.sql`）
- 每次新增 SQL 需新建文件，**禁止修改已有的 SQL 文件**（已录入的脚本不可追溯修改）
- 所有建表语句必须包含 `IF NOT EXISTS`
- 新增权限/日志模块字典等增量数据需单独建脚本文件
