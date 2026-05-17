# zus-starter Agent 工作指南

## 模块职责

`zus-starter` 是 Spring Boot 启动层和组合根。它负责启动入口、配置文件、资源文件、静态调试页面、模块组装和端到端测试。

这个模块不定义领域规则，也不实现持久化适配。它负责把其他模块组合起来。

## 主要目录职责

- `src/main/java/org/kitona/zus/starter`：Spring Boot 启动入口和启动配置。
- `src/main/resources/application.properties`：运行时配置，例如 `server.port`。
- `src/main/resources/logback-spring.xml`：日志配置。
- `src/main/resources/static`：静态资源和本地调试页面。
- `src/test/java`：完整 Spring 上下文下的集成测试和 API 行为测试。
- `src/test/resources`：测试配置和测试 schema。

## 运行规则

- 默认本地端口以 `application.properties` 为准，当前是 `8091`。
- 本地调试页面可以放在 `src/main/resources/static`。
- starter 需要显式依赖 `zus-api` 和 `zus-infrastructure`，保证 Controller 与 Adapter Bean 被扫描。
- 启动类不承载业务逻辑。

## 测试规则

- API 行为测试放在这里。
- 测试优先使用 H2 和 test profile。
- 端到端测试可以通过 Controller 接口调用，也可以用 `JdbcTemplate` 验证落库。
- 每个测试用例需要隔离业务表数据。

## 验证命令

```bash
mvn -q -pl zus-starter -am test
mvn -q test -DskipITs
```
