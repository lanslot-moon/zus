# zus-service Agent 工作指南

## 模块职责

`zus-service` 是应用层。它负责用例编排、Command/Query 校验、事务边界、模型加载、缓存协调、事件发布、应用 DTO 组装和调用领域服务。

这个模块应该薄于领域层。它负责协调，不负责实现授权算法。

## 主要包职责

- `application`：应用服务接口。
- `application/impl`：应用服务实现和事务边界。
- `application/coordinator`：可复用的用例协调，例如模型加载、编译缓存、Check/Explain 编排。
- `dto/command`：写入或动作命令。
- `dto/query`：查询输入。
- `dto/response`：应用层响应 DTO。
- `conv`、`conv/assembler`：应用层转换和装配。
- `event`、`listener`：应用事件和监听器。
- `exception`：应用层异常。

## 命名与编码风格

- 应用服务接口使用 `I*ApplicationService`，例如 `IAuthorizationReadApplicationService`、`ITupleMutationApplicationService`。
- 应用层实现放在 `application/impl`，命名跟随现有类，例如 `PermissionCheckApplicationService`、`TupleMutationApplicationService`，不强制加 `Impl`。
- 复杂用例协调类使用 `*Coordinator`，例如 `PermissionCheckCoordinator`、`PermissionExplainCoordinator`、`TupleMutationCoordinator`。
- 写入或动作入参使用 `*Command`，例如 `CheckCommand`、`WriteTupleCommand`、`CreateModelCommand`。
- 查询入参使用 `*Query`，例如 `ListObjectsQuery`、`ListSubjectsQuery`、`TupleReadQuery`。
- 应用返回对象使用 `*DTO` 或 `*ResultDTO`，例如 `PermissionCheckResultDTO`、`AuthorizationModelResultDTO`、`TupleResultDTO`。
- 转换和装配类使用 `*Assembler`、`*Converter` 或既有 `*Conv`，新代码优先选 `*Assembler` / `*Converter`。

## 应用层规则

- 在用例入口校验 Command/Query。
- 写链路事务边界放在应用服务。
- 不直接调用 MyBatis Mapper。
- 不返回 API VO。
- 不持有 HTTP Request。
- 不复制领域 evaluator 逻辑。
- `authorizationModelId` 存在时必须优先使用，缺失时才回退 Store 当前模型。
- consistency 和 zookie 解析在应用边界完成。

## 验证命令

```bash
mvn -q -pl zus-service -am test
mvn -q -pl zus-starter -am test
```
