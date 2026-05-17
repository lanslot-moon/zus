# zus-client Agent 工作指南

## 模块职责

`zus-client` 是二方包和外部契约模块。它提供其他微服务可以依赖的 SDK 接口、DTO 和稳定枚举。

这个模块最关注兼容性。修改这里的公开类型时，要默认它们可能已经被外部服务依赖。

## 可以放在这里的内容

- 对外发布的 facade 接口，例如 `IUserInfoFacade`。
- 其他服务需要依赖的 DTO。
- 可作为 SDK 契约的一部分发布的枚举。
- 与外部调用契约直接相关的轻量校验信息。

## 不应该放在这里的内容

- REST Controller。
- 应用服务实现。
- 领域聚合、领域服务、evaluator。
- Mapper、PO、SQL、Redis、CEL、ANTLR。
- starter 配置或本地调试页面。

## 命名与契约规则

- 对外接口使用 `I` 前缀。
- DTO 可以使用 Lombok，但不能承载领域不变量。
- 对外 DTO 字段视为公开 API，优先做兼容性新增，谨慎删除或重命名。
- 不要泄漏内部领域对象、PO 或基础设施对象。
- client DTO 不是 domain 对象，也不是 API VO。

## 验证命令

```bash
mvn -q -pl zus-client -am test
```
