# zus-common Agent 工作指南

## 模块职责

`zus-common` 是通用工具层。它只提供跨模块复用、无业务语义、无基础设施绑定的能力，例如异常基类、通用工具类、JSON 工具、校验工具、MapStruct 辅助能力和上下文工具。

这个模块是所有业务模块都可能依赖的底座，因此必须保持轻量、稳定、无业务方向性。

## 可以放在这里的内容

- 通用异常和错误抽象，例如 `SystemException`、`RestException`、`IError`。
- 通用工具类，例如 `JacksonUtil`、`ValidationUtil`、`ReflectionUtil`、`MapstructUtil`。
- 跨模块可复用的上下文能力，例如 MDC、用户上下文等不绑定具体业务流程的代码。
- 不依赖具体业务模型的校验、反射、加密、字段处理能力。

## 不应该放在这里的内容

- Store、Model、Tuple、Check、Explain、ReBAC、FGA 等业务规则。
- Domain、Service、API、Infrastructure、Starter 的反向依赖。
- Mapper、PO、SQL、Redis、CEL、ANTLR、Controller。
- 只服务单个模块的 DTO、VO、Command、Query 或转换逻辑。

## 命名与编码风格

- 工具类使用 `*Util`，一般保持无状态。
- 异常类使用明确语义命名，例如 `SystemException`、`RestException`。
- 接口仍可使用 `I` 前缀，例如 `IError`、`IUserInfoGateway`。
- 如果工具类开始出现授权领域名词，应移动到对应业务模块。
- 如果代码需要数据库、缓存、HTTP 或 Spring 生命周期，它不属于 `zus-common`。

## 验证命令

```bash
mvn -q -pl zus-common -am test
```
