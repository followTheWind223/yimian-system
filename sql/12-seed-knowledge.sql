-- ============================================================
-- 12-seed-knowledge.sql
-- 注入测试知识题目 — 丰富标签页内容，便于前端验证
-- 执行方式：在 MySQL 中执行 source /path/to/12-seed-knowledge.sql
-- 注意：请确保后端 Spring Boot 已重启以加载此数据
-- ============================================================

USE yimian;

-- ============================================================
-- 1. 补 TAG 日志模块字典（若 11-init-tag-log-module.sql 未执行）
-- ============================================================
INSERT INTO sys_log_module (code, name, sort, enabled) VALUES
('TAG', '标签模块', 8, 1)
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort);

-- ============================================================
-- 2. 补 TAG 权限（若 09-init-knowledge-permission.sql 中未包含）
-- ============================================================
INSERT INTO sys_permission (perm_code, perm_name, description) VALUES
('tag:manage', '标签管理', '标签CRUD权限')
ON DUPLICATE KEY UPDATE perm_name = VALUES(perm_name);

-- 将 tag:manage 分配给 ROLE_ADMIN
INSERT INTO sys_role_permission (role_id, perm_id)
SELECT r.id, p.id FROM sys_role r, sys_permission p
WHERE r.role_code = 'ROLE_ADMIN' AND p.perm_code = 'tag:manage'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_permission rp2
    WHERE rp2.role_id = r.id AND rp2.perm_id = p.id
  );

-- ============================================================
-- 3. 注入 6 条多样化的知识题目（审核通过状态 status=1）
--    覆盖 Spring/Redis/并发/JVM/分布式/设计模式 等标签
--    SHA-256 每道题内容不同，不会触发去重
-- ============================================================

-- 3.1 Spring 事务传播机制
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT 'Spring 事务传播机制详解',
'## 七种传播行为

| 传播行为 | 说明 |
|----------|------|
| REQUIRED（默认） | 有事务则加入，无则新建 |
| REQUIRES_NEW | 始终新建事务，挂起当前 |
| SUPPORTS | 有事务则加入，无则非事务 |
| NOT_SUPPORTED | 非事务执行，挂起当前事务 |
| MANDATORY | 必须在事务中，否则抛异常 |
| NEVER | 必须非事务，否则抛异常 |
| NESTED | 嵌套事务，savepoint 回滚 |

## REQUIRED vs REQUIRES_NEW

`REQUIRED`：外层回滚则内层也回滚。
`REQUIRES_NEW`：内外事务独立，内层回滚不影响外层。

## 常见坑

1. 自调用失效：同类方法调用不走代理，`@Transactional` 不生效
2. 非 public 方法：AOP 代理只拦截 public 方法
3. 异常被 catch：事务只回滚 RuntimeException，checked exception 需显式 rollbackFor',
SHA2(CONCAT('spring-tx-propagation-20260714', NOW()), 256),
2, 1, 2, 580, 42, 28, 3,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = 'Spring 事务传播机制详解' AND deleted = 0);

-- 3.2 Redis 数据结构应用场景
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT 'Redis 五种数据结构及应用场景',
'## String

- 缓存：`SET key value EX 300`
- 计数器：`INCR article:1:views`
- 分布式锁：`SET lock:order uuid NX EX 10`

## Hash

- 用户信息：`HSET user:1 name "张三" age 25`
- 购物车：`HSET cart:1 sku001 3`

## List

- 消息队列：`LPUSH queue:task msg` / `BRPOP queue:task 0`
- 最新列表：`LPUSH news:latest item` / `LTRIM news:latest 0 99`

## Set

- 标签：`SADD user:1:tags "Java" "Spring"`
- 共同好友：`SINTER user:1:friends user:2:friends`

## Sorted Set

- 排行榜：`ZADD rank:score 980 user1`
- 延时队列：score = 时间戳，`ZRANGEBYSCORE` 取出到期任务',
SHA2(CONCAT('redis-data-structures-20260714', NOW()), 256),
1, 1, 2, 720, 65, 48, 5,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = 'Redis 五种数据结构及应用场景' AND deleted = 0);

-- 3.3 volatile 关键字
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT 'volatile 关键字的作用与底层原理',
'## 两大作用

1. **保证可见性**：写 volatile 变量后立即刷新到主内存，读 volatile 变量前从主内存加载
2. **禁止指令重排序**：通过内存屏障（Memory Barrier）阻止 JIT 编译器重排指令

## 不保证原子性

`count++` 是三步操作（读-改-写），volatile 不能保证复合操作的原子性。

## 底层实现

- x86 架构：`lock` 前缀指令，锁定总线/缓存行
- Java 内存模型（JMM）：StoreStore → volatile写 → StoreLoad 屏障

## 经典应用

1. **DCL 单例**：`private static volatile Singleton instance;`
2. **状态标志位**：`volatile boolean running = true;`

## 与 synchronized 对比

| 维度 | volatile | synchronized |
|------|----------|-------------|
| 可见性 | ✅ | ✅ |
| 原子性 | ❌ | ✅ |
| 性能 | 高 | 较低 |
| 适用 | 一写多读 | 读写互斥 |',
SHA2(CONCAT('volatile-keyword-20260714', NOW()), 256),
2, 1, 2, 460, 35, 22, 2,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = 'volatile 关键字的作用与底层原理' AND deleted = 0);

-- 3.4 分布式锁
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT '分布式锁的三种实现方案对比',
'## Redis 实现

```
SET lock:order uuid NX PX 30000
```

- 加锁 + 过期原子操作
- Lua 脚本解锁：`if redis.call("get",KEYS[1])==ARGV[1] then return redis.call("del",KEYS[1])`
- Redisson 封装了看门狗自动续期

## Zookeeper 实现

- 临时顺序节点：`/locks/lock-0000000001`
- 最小节点获得锁
- 前一节点注册 Watcher，删除时唤醒后继
- 缺点：羊群效应，性能较差

## 数据库实现

- `INSERT INTO lock_table (lock_key) VALUES ("order_lock")`
- 唯一索引保证互斥
- 缺点：无过期机制，需额外清理

## 选型

| 方案 | 可靠性 | 性能 | 复杂度 |
|------|--------|------|--------|
| Redis | 中（AP） | 高 | 低 |
| ZK | 高（CP） | 中 | 中 |
| DB | 高 | 低 | 低 |

> 一般场景用 Redisson 即可，金融场景可考虑 ZK/etcd。',
SHA2(CONCAT('distributed-lock-20260714', NOW()), 256),
3, 1, 2, 620, 55, 38, 4,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = '分布式锁的三种实现方案对比' AND deleted = 0);

-- 3.5 JVM 类加载机制
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT 'JVM 类加载机制与双亲委派模型',
'## 类加载过程

1. **加载**：读取 .class 字节码 → 方法区
2. **验证**：文件格式、元数据、字节码验证
3. **准备**：静态变量分配内存，赋零值
4. **解析**：符号引用 → 直接引用
5. **初始化**：执行 `<clinit>`，静态变量赋值

## 双亲委派模型

```
BootstrapClassLoader (rt.jar)
    ↑
ExtensionClassLoader (jre/lib/ext)
    ↑
ApplicationClassLoader (classpath)
    ↑
自定义 ClassLoader
```

## 为什么双亲委派？

1. 防止核心类被篡改（如自定义 java.lang.String）
2. 避免类的重复加载

## 打破双亲委派

- Tomcat：每个 WebApp 独立 ClassLoader
- SPI（JDBC）：`Thread.currentThread().getContextClassLoader()`
- OSGi：网状类加载',
SHA2(CONCAT('jvm-classloader-20260714', NOW()), 256),
2, 1, 2, 390, 30, 18, 2,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = 'JVM 类加载机制与双亲委派模型' AND deleted = 0);

-- 3.6 单例模式
INSERT INTO sys_knowledge (title, content, content_hash, difficulty, status, submit_user_id, view_count, like_count, collect_count, comment_count, created_at, updated_at, deleted)
SELECT '单例模式的五种写法',
'## 1. 饿汉式

```java
private static final Singleton INSTANCE = new Singleton();
public static Singleton getInstance() { return INSTANCE; }
```

## 2. 懒汉式（双重检查锁）

```java
private static volatile Singleton instance;
public static Singleton getInstance() {
  if (instance == null) {
    synchronized (Singleton.class) {
      if (instance == null) instance = new Singleton();
    }
  }
  return instance;
}
```

## 3. 静态内部类（推荐）

```java
private static class Holder {
  static final Singleton INSTANCE = new Singleton();
}
public static Singleton getInstance() { return Holder.INSTANCE; }
```

## 4. 枚举（最安全）

```java
enum Singleton { INSTANCE; }
```

防反射、防序列化破坏。

## 5. 容器式

Spring 默认 scope=singleton，由容器管理单例。',
SHA2(CONCAT('singleton-pattern-20260714', NOW()), 256),
1, 1, 2, 340, 28, 15, 1,
NOW(), NOW(), 0
WHERE NOT EXISTS (SELECT 1 FROM sys_knowledge WHERE title = '单例模式的五种写法' AND deleted = 0);

-- ============================================================
-- 4. 为上述题目绑定标签关联
--    题目 ID 从 sys_knowledge 查询（新增的 ID 会自增）
--    先查出知识 ID，再批量插入 sys_knowledge_tag
-- ============================================================
INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'Spring 事务传播机制详解' AND t.name IN ('Spring', 'Spring Boot')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'Redis 五种数据结构及应用场景' AND t.name IN ('Redis')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'volatile 关键字的作用与底层原理' AND t.name IN ('Java', '多线程', 'JVM')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = '分布式锁的三种实现方案对比' AND t.name IN ('Redis', 'ZooKeeper', '分布式')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'JVM 类加载机制与双亲委派模型' AND t.name IN ('JVM', 'Java')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = '单例模式的五种写法' AND t.name IN ('设计模式', 'Java')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

-- ============================================================
-- 5. 补充：更新已有题目使其关联更多标签（方便标签页展示）
-- ============================================================
INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'TCP三次握手与四次挥手详解' AND t.name IN ('计算机网络')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

INSERT INTO sys_knowledge_tag (knowledge_id, tag_id)
SELECT k.id, t.id
FROM sys_knowledge k, sys_tag t
WHERE k.title = 'HashMap底层原理与扩容机制' AND t.name IN ('集合框架')
  AND NOT EXISTS (SELECT 1 FROM sys_knowledge_tag kt WHERE kt.knowledge_id = k.id AND kt.tag_id = t.id);

-- ============================================================
-- 完成
-- ============================================================
SELECT 'Seed data injection complete!' AS status,
       (SELECT COUNT(*) FROM sys_knowledge WHERE deleted = 0) AS total_knowledge,
       (SELECT COUNT(*) FROM sys_tag WHERE deleted = 0) AS total_tags,
       (SELECT COUNT(*) FROM sys_knowledge_tag) AS total_tag_relations;
