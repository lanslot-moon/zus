# zus-infrastructure Agent 工作指南

## 模块职责

`zus-infrastructure` 是基础设施适配层。它实现领域层和应用层定义的端口，负责 MyBatis、MySQL、缓存、CEL、ANTLR、外部系统、序列号和 schema 对齐。

这个模块可以知道技术细节，但不能把领域规则写进技术实现里。

## 主要包职责

- `persistence/mysql`：MySQL 持久化相关代码。
- `persistence/mysql/entity`：数据库 PO，字段必须和表结构一致。
- `persistence/mysql/mapper`：MyBatis Mapper 和复杂 SQL。
- `persistence/mysql/repository`：Repository 实现和领域对象转换。
- `engine/parser`：OpenFGA DSL 语法解析。
- `engine/compiler`：模型编译器，把结构化模型或 DSL 转成领域 AST。
- `engine/render`：发布模型快照渲染。
- `external/cel`：CEL 条件求值适配器。
- `cache`：本地缓存、Redis 缓存和查询缓存。
- `sequence`：ID 和 zookie 序列能力。

## 命名与编码风格

- 数据库对象使用 `*PO`，例如 `StorePO`、`RelationTuplePO`、`AuthModelPO`，PO 类名应尽量贴近 `table_schema.sql` 中的真实表语义。
- MyBatis Mapper 使用 `I*Mapper`，例如 `IStoreMapper`、`ITupleMapper`。
- MyBatis Plus 直接持久化仓储接口使用 `I*PersistenceRepository`。
- 持久化实现使用 `*PersistenceRepository`，例如 `StorePersistenceRepository`、`TuplePersistenceRepository`。
- 领域仓储适配器使用 `*DomainRepositoryAdapter`，查询仓储适配器使用 `*QueryRepositoryAdapter`。
- 外部能力适配器使用 `*Adapter` 或能力名，例如 `CompiledModelCacheAdapter`、`StoreZookieSequenceAdapter`、`CelConditionEvaluator`。
- 缓存能力使用 `*CacheProvider`、`*CacheManager`、`*CacheQuery`。

## 数据库规则

- PO、Mapper、Converter、Repository 必须对齐 `zus-infrastructure/src/main/resources/db/table_schema.sql`。
- 不确定字段是否存在时，先查 schema。
- 简单 CRUD 优先用 MyBatis Plus wrapper。
- 复杂查询、批量操作、性能敏感查询可以使用 Mapper XML。
- Tuple 查询必须保留 `subject_relation`、wildcard、`expires_at`、condition、zookie 语义。
- 不要让 Repository 直接返回 PO 给 domain、service 或 api。

## 不应该放在这里的内容

- REST Controller、Request、Response。
- 应用事务编排。
- 领域聚合不变量。
- 授权 evaluator 核心算法。

## 验证命令

```bash
mvn -q -pl zus-infrastructure -am test
mvn -q -pl zus-starter -am test
```
