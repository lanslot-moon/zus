# zus-domain Agent 工作指南

## 模块职责

`zus-domain` 是业务核心。它承载 DDD 领域对象、聚合、实体、值对象、领域服务、Specification、Strategy、领域端口、Repository 接口和授权求值核心。

领域层必须保持纯净，不能依赖 Spring、MyBatis、Redis、数据库 PO、REST Request/Response、Mapper 或基础设施实现。

## 主要包职责

- `authorization/store`：Store 生命周期、状态、当前模型指针、zookie 不变量。
- `authorization/model`：授权模型聚合、类型定义、关系定义、限制定义、条件定义和模型生命周期规则。
- `authorization/tuple`：关系 tuple、tuple key、条件绑定、过期时间和 subject relation 语义。
- `authorization/audit`：审计元数据和 changelog 领域对象。
- `authorization/evaluation`：编译模型、rewrite 节点、运行时上下文、求值策略、解释树、递归和 memoization。
- `read`：领域读模型、查询条件和值对象。
- `port`：领域需要的外部能力抽象，例如条件求值、模型缓存、审计上下文。
- `repository`：领域仓储接口。
- `service`：领域服务，例如权限校验和搜索求值。

## 领域建模规则

- 聚合根使用 `*Aggregate`，负责维护一致性边界和不变量。
- 值对象优先不可变，可使用 `record` 表达简单不可变结构。
- 领域类禁止使用 Lombok `@Data` 和 `@Setter`，普通领域类只允许有限 `@Getter`。
- 不允许通过 `toString()` 或字符串拆分承载业务语义。
- Specification 使用 `*Specification`，负责可复用业务判断。
- Strategy 使用 `*EvaluationStrategy`，负责 rewrite 节点差异化求值。
- Rewrite AST 节点使用 `*Node`，例如 `SelfNode`、`UnionNode`、`TupleToUsersetNode`。
- Domain Service 只承载跨实体、跨聚合规则，不做 DTO 装配和持久化细节。

## 授权引擎规则

- Check 语义归属于 `PermissionCheckEvaluator`。
- ListObjects/ListSubjects 语义归属于 `PermissionSearchEvaluator`。
- Explain 必须复用 Check 的求值路径，只额外收集解释信息。
- Tuple 可见性、subject 匹配、relation 限制、递归保护、memoization 和 condition 求值不能在多个位置复制。
- Explain 类型使用 `Evaluation*`、`*ExplainDetail`、`*Trace*`，保持和现有解释树命名一致。

## 不应该放在这里的内容

- Spring 注解、MyBatis 注解、Mapper XML、PO、Redis、CEL runtime 适配器、ANTLR parser 实现。
- API Request/Response、Controller、RestResult、HTTP 路径。
- 应用事务、DTO 装配、缓存实现和数据库查询实现。

## 验证命令

```bash
mvn -q -pl zus-domain -am test
```
