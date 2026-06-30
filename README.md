# ZUS

ZUS 是一个基于 Java 17 和 Spring Boot 3 的关系型授权服务，用 ReBAC/FGA 的方式回答一个核心问题：

> 某个主体是否能通过一组业务关系，被证明拥有某个对象上的某种权限？

它适合文档、网盘、知识库、项目协作、组织空间、团队成员、层级资源等场景。
当权限不再只是“用户有没有某个角色”，而是需要经过组织、文件夹、用户组、角色对象、父子资源和例外规则推导时，ZUS 负责把这些关系推理收敛成统一的授权判断。

## TL;DR

传统 RBAC 通常这样判断权限：

```text
user -> role -> permission
```

ZUS 更关注这样的授权问题：

```text
user:erin 是否可以 viewer document:roadmap？
```

答案可能不是来自一条直接授权，而是来自一条关系证明路径：

```text
user:erin
  -> group:engineering#member
  -> group:platform#member
  -> folder:root#viewer
  -> folder:finance#viewer
  -> document:roadmap#viewer
```

也就是说，ZUS 不只是查一张权限表，而是在关系模型和关系事实上证明权限是否成立。

## 为什么不是再加几个角色

很多系统一开始用 RBAC 都很顺：

```text
admin
editor
viewer
```

但业务复杂后，很容易出现这些角色：

```text
project_admin
project_admin_external
project_admin_temp
finance_folder_viewer
finance_folder_viewer_without_sensitive_docs
```

角色开始承载太多业务上下文，最后会变成不可维护的命名游戏。

ReBAC 的思路是把这些上下文拆回业务关系：

```text
用户属于哪个组？
组被授予了哪个资源的权限？
文档属于哪个文件夹？
文件夹是否从父文件夹继承权限？
某个用户是否被显式 blocked？
```

权限判断从“匹配角色名”变成“证明关系路径”。

## 一个最小 DSL 示例

下面这个模型描述了一个常见的协作文档系统：

```dsl
model
  schema 1.1

type user

type group
  relations
    define parent: [group]
    define member: [user] or member from parent

type folder
  relations
    define parent: [folder]
    define owner: [user]
    define editor: [user, group#member] or owner
    define viewer: [user, group#member] or editor or viewer from parent

type document
  relations
    define parent: [folder]
    define owner: [user]
    define editor: [user, group#member] or owner
    define viewer: [user, group#member] or editor or viewer from parent
    define blocked: [user]
    define restricted_viewer: viewer but not blocked
```

这个模型表达了几条规则：

- `group#member` 表示一个用户组里的所有成员。
- `member from parent` 表示子组可以继承父组成员关系。
- `editor or owner` 表示 owner 自动具备 editor 权限。
- `viewer from parent` 表示文档可以从上级文件夹继承 viewer 权限。
- `viewer but not blocked` 表示先证明 viewer 成立，再排除被 blocked 的主体。

这段 DSL 的重点不是语法本身，而是让权限规则变成可推理、可复用、可组合的关系表达。

## 关系推理是怎么发生的

假设有一棵资源树：

```text
folder:root
  └── folder:finance
        └── folder:budget
              └── document:roadmap
```

关系事实如下：

```text
folder:finance#parent@folder:root
folder:budget#parent@folder:finance
document:roadmap#parent@folder:budget
folder:root#viewer@group:platform#member
group:platform#parent@group:engineering
group:engineering#member@user:erin
```

当系统询问：

```text
user:erin 是否有 document:roadmap#viewer？
```

ZUS 会沿着模型中的关系规则进行推导：

```text
document:roadmap#viewer
  -> viewer from parent
  -> folder:budget#viewer
  -> viewer from parent
  -> folder:finance#viewer
  -> viewer from parent
  -> folder:root#viewer
  -> group:platform#member
  -> member from parent
  -> group:engineering#member
  -> user:erin
```

最终证明成立，返回允许。

再看一个排除规则：

```text
document:roadmap#viewer@user:mallory
document:roadmap#blocked@user:mallory
```

如果请求是：

```text
user:mallory 是否有 document:roadmap#restricted_viewer？
```

虽然 `viewer` 成立，但 `blocked` 也成立，所以：

```text
restricted_viewer = viewer but not blocked
```

最终返回拒绝。

## ZUS 适合解决什么问题

ZUS 更适合这些授权场景：

- 层级资源：文件夹、文档、项目、空间、组织。
- 协作关系：owner、editor、viewer、reviewer、member。
- 用户组授权：权限授予一个组，再由成员关系传播到用户。
- 角色关系化：把角色建模为对象，而不是散落在业务代码里的字符串。
- 权限组合：并集、交集、排除、父级继承、跨对象传播。
- 多系统复用：多个业务服务共享同一套授权语义。

它不适合为了简单权限强上复杂模型。
如果你的系统只有固定菜单和固定角色，传统 RBAC 可能已经足够。

## 和 ACL、RBAC、ABAC 的关系

ZUS 并不是要替代所有权限模型，而是把常见权限模型放到统一关系语义下。

| 模型 | 在 ZUS 中的表达 |
| --- | --- |
| ACL | `document:roadmap#viewer@user:alice` |
| RBAC | `project:apollo#admin@role:project_admin#assignee` |
| ABAC | 关系成立后，再结合请求上下文或条件判断 |
| ReBAC | 通过对象、主体和关系路径证明权限 |

所以 ReBAC 更像一个底座：它可以承载直接授权、角色授权、组织继承、用户组传播和一部分上下文条件。

## 快速体验

启动服务：

```bash
mvn -q -pl zus-starter -am spring-boot:run
```

默认端口：

```text
http://localhost:8091
```

ZUS 内置了一个前端控制台，启动后可以直接访问：

```text
http://localhost:8091/
```

你可以在控制台里完成：

- 创建 Store
- 创建授权模型
- 写入关系事实
- 执行权限检查
- 查看关系推理结果

## 最小 API 流程

如果不使用控制台，也可以用 API 体验完整流程。

创建 Store：

```bash
curl -X POST http://localhost:8091/fga/stores \
  -H 'Content-Type: application/json' \
  -d '{"name":"demo","description":"ReBAC demo store"}'
```

写入一个授权模型：

```bash
curl -X POST http://localhost:8091/fga/stores/{storeId}/authorization-models \
  -H 'Content-Type: application/json' \
  -d '{
    "schemaVersion": "1.1",
    "types": {
      "user": {
        "relations": {}
      },
      "document": {
        "relations": {
          "viewer": {
            "rewrite": "self",
            "allowedSubjectTypes": [
              { "type": "user" }
            ]
          }
        }
      }
    }
  }'
```

发布并激活模型：

```bash
curl -X POST http://localhost:8091/fga/stores/{storeId}/authorization-models/{modelId}/publish
curl -X POST http://localhost:8091/fga/stores/{storeId}/authorization-models/{modelId}/activate
```

写入一条关系事实：

```bash
curl -X POST http://localhost:8091/fga/stores/{storeId}/write \
  -H 'Content-Type: application/json' \
  -d '{
    "writes": [
      {
        "tupleKey": {
          "object": { "type": "document", "id": "roadmap" },
          "relation": "viewer",
          "subject": { "type": "user", "id": "erin" }
        }
      }
    ]
  }'
```

检查权限：

```bash
curl -X POST http://localhost:8091/fga/stores/{storeId}/check \
  -H 'Content-Type: application/json' \
  -d '{
    "tupleKey": {
      "object": { "type": "document", "id": "roadmap" },
      "relation": "viewer",
      "subject": { "type": "user", "id": "erin" }
    }
  }'
```

## 项目结构

```text
zus-common          通用工具、异常、校验、JSON、MapStruct 辅助能力
zus-domain          领域模型、关系推理、领域服务、Specification、Strategy、Port
zus-infrastructure  MySQL、MyBatis、缓存、CEL、ANTLR、外部能力适配
zus-service         应用服务、Command/Query、事务边界、用例编排
zus-api             REST API、Request/Response、Converter、SSE
zus-client          二方包与外部契约
zus-starter         Spring Boot 启动入口、配置、静态控制台
```

如果你只是想理解 ZUS 是什么，从本 README 开始即可。
如果你要参与开发，请继续看 [AGENTS.md](AGENTS.md)。

## 进一步阅读

- [关系型授权专题合集](docs/rebac/README.md)
- [Zanzibar: Google’s Consistent, Global Authorization System](https://research.google/pubs/zanzibar-googles-consistent-global-authorization-system/)
- [OpenFGA Authorization Concepts](https://openfga.dev/docs/authorization-concepts)
- [OpenFGA Modeling Design Principles](https://openfga.dev/docs/best-practices/modeling-design-principles)
