# zus-api Agent 工作指南

## 模块职责

`zus-api` 是用户接口层。它负责 REST Controller、Request、Response、Converter、SSE、facade 适配和请求级校验。

这个模块把外部协议转换成应用层 Command/Query，不实现领域算法。

## 主要包职责

- `controller`：REST Controller 接口。
- `controller/impl`：Controller 实现，负责调用应用服务。
- `request/authorization`：Check、Explain、Expand、ListObjects、ListSubjects 请求。
- `request/model`：授权模型写入请求。
- `request/tuple`：tuple 读写请求。
- `request/common`：tuple key、reference、consistency 等通用请求片段。
- `response`：API 返回 VO。
- `converter`：API 与 service DTO 转换。
- `sse`：Watch SSE 支持。
- `facade`：RPC facade 适配。
- `permission`、`verify`：接口级权限和校验支持。

## 命名与编码风格

- REST Controller 接口使用 `IFga*ApiService`，并直接声明 Spring MVC 注解。
- Controller 实现类使用 `Fga*ApiService`，放在 `controller/impl`，不额外追加 `Impl`。
- API 请求对象使用 `Fga*Request`，按语义放入 `request/authorization`、`request/model`、`request/tuple`、`request/common`。
- API 响应对象使用 `Fga*VO`、`Fga*ResponseVO` 或通用 `RestResult`、`PageResponseVO`。
- API Converter 使用 `Fga*Converter` 或 `WatchEventConverter`，优先使用 MapStruct 风格接口和 `INSTANCE = Mappers.getMapper(...)`。
- FGA 相关 API 类型统一使用 `Fga` 前缀，不混用 `OpenFga`、`Authz` 等新前缀。

## API 层规则

- Controller 只做参数校验、日志、转换、调用应用服务和返回 `RestResult`。
- Request/Response 是 API 契约，不是领域对象。
- 非领域对象转换优先使用 MapStruct 风格 Converter。
- 不在 Controller 中注入 Repository、Mapper 或基础设施 Adapter。
- 不在 `zus-api` 添加单元测试；API 行为测试优先放在 `zus-starter`。
- 使用 ReBAC 统一术语：subject、object、relation、tuple、userset、condition、consistency、zookie。

## 主要接口范围

- Store：`/fga/stores`
- Model：`/fga/stores/{storeId}/authorization-models`
- Tuple：`/write`、`/delete`、`/read`、`/changes`
- Check：`/check`、`/batch-check`
- Explain：`/check/explain`
- Relation Query：`/list-objects`、`/list-subjects`、`/expand`
- Watch：`/watch`

## 验证命令

```bash
mvn -q -pl zus-api -am test
mvn -q -pl zus-starter -am test
```
