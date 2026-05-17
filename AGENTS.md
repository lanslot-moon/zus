# ZUS Agent 工作指南

## 项目定位

ZUS 是一个基于 Java 17 和 Spring Boot 3.3 的多模块授权服务。当前授权核心以 ReBAC/FGA 为主线，围绕 Store、Authorization Model、Relation Tuple、Check、Explain、ListObjects、ListSubjects、Read、Watch 等能力组织。

这个文件面向 coding agent，用来说明项目结构、模块边界、开发规则和测试方式。面向人的产品介绍、架构文章和专题文档放在 `README.md` 或 `docs/` 中。

## 模块职责总览

- `zus-common`：通用工具层，提供异常、工具类、校验、JSON、MapStruct 辅助能力，不承载业务逻辑。
- `zus-domain`：领域层，承载 DDD 聚合、实体、值对象、领域服务、Specification、Strategy、Port、Repository 接口和授权求值核心。
- `zus-infrastructure`：基础设施层，承载 MyBatis、MySQL、缓存、CEL、ANTLR、外部系统和领域端口实现。
- `zus-client`：二方包和外部契约层，提供其他服务可依赖的 SDK 接口和 DTO。
- `zus-service`：应用层，承载 Command/Query、应用服务、事务边界、Coordinator、事件发布和 DTO 装配。
- `zus-api`：用户接口层，承载 REST Controller、Request、Response、Converter、SSE 和 facade 适配。
- `zus-starter`：启动层和组合根，承载 Spring Boot 启动入口、配置、静态资源、本地调试页和端到端测试。

## 依赖方向

允许的依赖方向：

```text
zus-starter -> zus-api -> zus-service -> zus-domain -> zus-common
                         -> zus-client
zus-starter -> zus-infrastructure -> zus-domain -> zus-common
```

约束：

- `zus-domain` 禁止依赖 Spring、MyBatis、Redis、HTTP、数据库 PO、Mapper、Controller、Request、Response。
- `zus-service` 可以使用 Spring 应用层能力，但不能写 SQL、不能直接调用 Mapper、不能持有 HTTP 请求对象。
- `zus-api` 只做协议适配、参数校验、转换和调用应用服务，不写授权算法。
- `zus-infrastructure` 只实现技术适配，不定义领域规则。
- `zus-common` 不能依赖任何业务模块。

## 项目命名与编码风格

以下规则基于当前代码提炼。新增代码优先跟随同包已有风格，不要为了“统一”而批量重命名历史代码。

### 通用命名

- 接口通常使用 `I` 前缀，例如 `IStoreApplicationService`、`ITupleDomainRepository`、`IConditionEvaluator`、`IStoreMapper`。
- 普通实现类如果已有接口实现关系，优先跟随所在包约定：应用服务当前多使用 `StoreApplicationService`、`AuthorizationReadApplicationService` 这类无 `Impl` 历史命名；facade 实现存在 `UserInfoFacadeImpl`；事件发布实现存在 `WatchEventPublisherImpl`。
- 枚举使用 `*Enum` 或业务名词本身，例如 `StoreStatusEnum`、`DeletedStatusEnum`、`ModelPublishStatus`、`StoreStatus`。
- 工具类使用 `*Util`，一般为无状态类，例如 `JacksonUtil`、`ValidationUtil`、`ReflectionUtil`、`MapstructUtil`。
- 配置类使用 `*Config` 或 `*Configuration`，例如 `RedisConfig`、`MybatisPlusConfig`、`InitServiceBeanConfiguration`。

### API 层命名

- REST Controller 接口使用 `IFga*ApiService`，并直接声明 Spring MVC 注解。
- Controller 实现类使用 `Fga*ApiService`，放在 `controller/impl`，不额外追加 `Impl`。
- API 请求对象使用 `Fga*Request`，按语义放入 `request/authorization`、`request/model`、`request/tuple`、`request/common`。
- API 响应对象使用 `Fga*VO`、`Fga*ResponseVO` 或通用 `RestResult`、`PageResponseVO`。
- API Converter 使用 `Fga*Converter` 或 `WatchEventConverter`，优先使用 MapStruct 风格接口和 `INSTANCE = Mappers.getMapper(...)`。
- FGA 相关 API 类型统一使用 `Fga` 前缀，不混用 `OpenFga`、`Authz` 等新前缀。

### Service 层命名

- 应用服务接口使用 `I*ApplicationService`，例如 `IAuthorizationReadApplicationService`、`ITupleMutationApplicationService`。
- 应用层实现放在 `application/impl`，命名跟随现有类，例如 `PermissionCheckApplicationService`、`TupleMutationApplicationService`。
- 复杂用例协调类使用 `*Coordinator`，例如 `PermissionCheckCoordinator`、`PermissionExplainCoordinator`、`TupleMutationCoordinator`。
- 写入或动作入参使用 `*Command`，例如 `CheckCommand`、`WriteTupleCommand`、`CreateModelCommand`。
- 查询入参使用 `*Query`，例如 `ListObjectsQuery`、`ListSubjectsQuery`、`TupleReadQuery`。
- 应用返回对象使用 `*DTO` 或 `*ResultDTO`，例如 `PermissionCheckResultDTO`、`AuthorizationModelResultDTO`、`TupleResultDTO`。
- 转换和装配类使用 `*Assembler`、`*Converter` 或既有 `*Conv`，其中新代码优先选 `*Assembler` / `*Converter`。

### Domain 层命名

- 聚合根使用 `*Aggregate`，例如 `StoreAggregate`、`AuthorizationModelAggregate`。
- 领域实体和值对象使用业务名词或 `record`，例如 `RelationTuple`、`TupleKey`、`TupleCondition`、`ObjectRef`、`Subject`、`Zookie`。
- 领域服务使用明确业务能力命名，例如 `PermissionCheckEvaluator`、`PermissionSearchEvaluator`、`TupleMutationDomainService`。
- Specification 使用 `*Specification`，例如 `SubjectMatchSpecification`、`TupleVisibilitySpecification`。
- Rewrite AST 节点使用 `*Node`，例如 `SelfNode`、`UnionNode`、`TupleToUsersetNode`。
- 求值策略使用 `*EvaluationStrategy`，例如 `SelfNodeEvaluationStrategy`、`TupleToUsersetEvaluationStrategy`。
- Explain 类型使用 `Evaluation*`、`*ExplainDetail`、`*Trace*`，保持和现有解释树命名一致。
- 领域层可用 `record` 表达不可变值对象；普通领域类只允许有限 `@Getter`，禁止 `@Data` 和 `@Setter`。

### Infrastructure 层命名

- 数据库对象使用 `*PO`，例如 `StorePO`、`RelationTuplePO`、`AuthModelPO`，PO 类名应尽量贴近 `table_schema.sql` 中的真实表语义。
- MyBatis Mapper 使用 `I*Mapper`，例如 `IStoreMapper`、`ITupleMapper`。
- MyBatis Plus 直接持久化仓储接口使用 `I*PersistenceRepository`。
- 持久化实现使用 `*PersistenceRepository`，例如 `StorePersistenceRepository`、`TuplePersistenceRepository`。
- 领域仓储适配器使用 `*DomainRepositoryAdapter`，查询仓储适配器使用 `*QueryRepositoryAdapter`。
- 外部能力适配器使用 `*Adapter` 或能力名，例如 `CompiledModelCacheAdapter`、`StoreZookieSequenceAdapter`、`CelConditionEvaluator`。
- 缓存能力使用 `*CacheProvider`、`*CacheManager`、`*CacheQuery`。

## 通用开发规则

- 领域类禁止使用 Lombok `@Data` 和 `@Setter`。
- 新增公开类、公开方法、核心字段时添加中文 JavaDoc。
- 非领域 DTO 可以使用 Lombok，但不要把 DTO 当成领域对象。
- 非领域对象之间的转换优先使用 MapStruct 风格 Converter。
- 避免魔法值，稳定概念使用常量或枚举。
- 避免吞异常，异常语义必须清晰。
- 修改 API 行为时，优先在 `zus-starter` 补端到端测试。

## 常用命令

```bash
mvn -q -pl zus-domain -am test
mvn -q -pl zus-infrastructure -am test
mvn -q -pl zus-service -am test
mvn -q -pl zus-api -am test
mvn -q -pl zus-starter -am test
mvn -q test -DskipITs
```

完成工作前，先跑最小相关模块测试，再根据影响范围跑全量回归。
