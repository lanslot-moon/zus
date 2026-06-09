# ZUS

ZUS 是一个面向复杂业务系统的关系型授权服务。它关注的问题不是“某个用户有没有某个角色”，而是：

> 一个主体能否通过一组业务关系，被证明拥有某个对象上的某种权限？

在简单系统里，权限通常可以用角色、菜单、按钮或资源 ACL 解决。但当系统开始出现组织、项目、文件夹、文档、团队、群组、协作者、继承关系和临时授权时，权限判断就不再是简单的字段匹配，而会变成一条关系路径的证明。

ZUS 的核心目标就是把这种证明过程标准化，让业务系统可以用一致的方式表达和判断细粒度权限。

## 为什么需要 ReBAC

传统 RBAC 的思路是：

```text
用户 -> 角色 -> 权限
```

这种方式适合后台管理系统，也适合权限边界比较稳定的业务。但在协作型系统里，权限往往不是只由角色决定的。

例如：

- 一个文档在某个文件夹下面，用户拥有上级文件夹的查看权限，因此可以查看文档。
- 用户属于某个用户组，这个用户组被授予项目编辑权限，因此用户可以编辑项目。
- 一个成员是组织管理员，所以自动拥有组织下工作区的管理权限。
- 一个文档 owner 可以编辑文档，editor 又可以查看文档。
- 一个用户被加入 blocked 关系后，即使本来是 viewer，也不能访问某个资源。

这些规则的共同点是：权限来自关系传播，而不是来自某个孤立的角色名称。

ReBAC，也就是 Relationship-Based Access Control，正是为了解决这类问题。它把授权判断建模为：

```text
subject 是否能通过关系路径证明 object#relation 成立
```

其中：

- `subject` 表示访问发起方，可以是用户、用户组、服务账号或某个对象上的一组主体。
- `object` 表示被访问对象，可以是文档、文件夹、项目、组织或任意业务资源。
- `relation` 表示主体和对象之间的关系，例如 owner、editor、viewer、member。
- `path` 表示权限从一个关系传播到另一个关系的证明链路。

## ZUS 用来做什么

ZUS 可以作为业务系统的授权判断底座。业务系统不需要在每个服务里重复实现一套复杂权限逻辑，而是把权限问题转换成统一的关系判断：

```text
user:alice 是否拥有 document:roadmap 的 viewer 权限？
user:bob 是否拥有 folder:finance 的 editor 权限？
group:platform#member 是否拥有 project:infra 的 maintainer 权限？
```

ZUS 负责根据已经定义好的关系语义和业务关系，推导最终结果。

它适合这些场景：

- 文档、网盘、知识库、项目管理这类层级资源系统。
- 多人协作系统，例如 workspace、organization、team、group。
- 权限需要继承、传播、排除或组合的系统。
- 传统 RBAC 已经开始出现大量特殊角色、例外规则和硬编码判断的系统。
- 需要把 ACL、RBAC 和一部分属性条件组合到同一套授权语义里的系统。

它不适合这些场景：

- 权限非常简单，只需要判断用户是否拥有固定角色。
- 系统没有资源层级、组织关系、协作关系或传播关系。
- 权限规则完全是一次性业务判断，不需要抽象成可复用授权模型。

## 一个直观的 DSL 例子

下面是一个简化版的关系模型，用来表达“用户组成员可以获得文件夹和文档权限，文档可以从上级文件夹继承 viewer 权限”：

```dsl
model
  schema 1.1

type user

type group
  relations
    define member: [user]

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

这个 DSL 表达了几件事：

- `group#member` 表示一个用户组里的所有成员。
- `editor: ... or owner` 表示 owner 也天然具备 editor 权限。
- `viewer from parent` 表示可以沿着 parent 关系向上继承 viewer。
- `viewer but not blocked` 表示先证明 viewer 成立，再排除 blocked 主体。

它的重点不是语法本身，而是让权限规则可以被写成可推理的关系表达式。

## 关系推理示例一：从上级文件夹继承权限

假设业务中有这样的资源层级：

```text
folder:root
  └── folder:finance
        └── folder:budget
              └── document:roadmap
```

用户 `alice` 被授予了根目录的查看权限：

```text
alice 是 folder:root 的 viewer
```

系统同时知道：

```text
folder:finance 的 parent 是 folder:root
folder:budget 的 parent 是 folder:finance
document:roadmap 的 parent 是 folder:budget
```

当业务系统询问：

```text
alice 能否查看 document:roadmap？
```

ZUS 的推理过程可以理解为：

```text
document:roadmap#viewer
需要证明 document:roadmap 的 viewer

document:roadmap 没有直接授予 alice viewer
于是查看它的 parent，也就是 folder:budget

folder:budget 没有直接授予 alice viewer
继续查看它的 parent，也就是 folder:finance

folder:finance 没有直接授予 alice viewer
继续查看它的 parent，也就是 folder:root

folder:root#viewer 命中 alice
因此 alice 可以查看 document:roadmap
```

这个例子说明，ReBAC 并不是只看当前对象上有没有一条直接授权，而是会沿着业务关系继续证明。

## 关系推理示例二：通过用户组获得权限

假设有一个平台团队：

```text
group:platform
```

用户 `erin` 是这个团队的成员：

```text
erin 是 group:platform 的 member
```

某个文档把 viewer 授给了这个团队的所有成员：

```text
group:platform#member 是 document:roadmap 的 viewer
```

当业务系统询问：

```text
erin 能否查看 document:roadmap？
```

ZUS 的推理过程可以理解为：

```text
document:roadmap#viewer
命中一个 userset：group:platform#member

于是问题变成：
erin 是否属于 group:platform#member？

group:platform#member 命中 erin
因此 erin 可以查看 document:roadmap
```

这就是 ReBAC 里非常重要的一类推理：授权对象不一定直接指向某个用户，也可以指向“一组由关系定义出来的主体”。

## 关系推理示例三：角色也可以被关系化

ReBAC 并不排斥 RBAC。相反，角色可以被表达成一种关系。

例如业务中有一个项目角色：

```text
role:project_admin
```

用户 `bob` 被分配到了这个角色：

```text
bob 是 role:project_admin 的 assignee
```

项目把管理员权限授给这个角色：

```text
role:project_admin#assignee 是 project:apollo 的 admin
```

当业务系统询问：

```text
bob 是否是 project:apollo 的 admin？
```

推理过程是：

```text
project:apollo#admin
命中 role:project_admin#assignee

继续证明：
bob 是否属于 role:project_admin#assignee？

role:project_admin#assignee 命中 bob
因此 bob 是 project:apollo 的 admin
```

这也是为什么 ReBAC 可以承载 RBAC：角色不再是一个孤立字段，而是关系图中的一个对象。

## 关系推理示例四：组合关系与排除关系

有些权限不是单一路径能表达的，而是需要组合。

例如：

```text
document:roadmap#restricted_viewer
表示：
  是 viewer
  但不是 blocked
```

当业务系统询问：

```text
mallory 是否是 document:roadmap 的 restricted_viewer？
```

如果系统发现：

```text
mallory 是 document:roadmap 的 viewer
mallory 也是 document:roadmap 的 blocked
```

那么最终结果应该是：

```text
false
```

因为 `restricted_viewer` 要求 viewer 成立，同时 blocked 不成立。

这个例子说明，ReBAC 不只是“向上找父级”，它还可以表达并集、交集、排除和跨对象传播等关系组合。

## 这套模型解决的核心痛点

ZUS 解决的是复杂权限系统里最容易失控的几个问题：

1. 权限逻辑分散在多个服务、多个 SQL、多个 if/else 中，难以维护。
2. 角色数量不断膨胀，最后变成 `project_admin_v2_temp_external` 这类不可理解的特殊角色。
3. 资源层级、组织层级、团队成员关系和协作关系混在一起，没有统一表达方式。
4. 业务想问“用户有没有权限”，系统却只能回答“表里有没有某条记录”。
5. 权限继承、用户组授权、角色授权、例外排除等规则无法组合。

ReBAC 的价值在于，它把这些问题统一转换成关系证明问题：

```text
给定 subject、object、relation，是否存在一条满足规则的授权路径？
```

## 和传统权限模型的关系

ZUS 不是简单替代 ACL、RBAC 或 ABAC，而是把它们放到同一套关系语义下。

ACL 可以理解为最直接的关系：

```text
user:alice 是 document:roadmap 的 viewer
```

RBAC 可以理解为通过角色对象传播：

```text
user:bob -> role:admin#assignee -> project:apollo#admin
```

ABAC 可以理解为在关系成立后再叠加上下文约束：

```text
用户与资源存在关系，并且请求满足某些动态条件
```

ReBAC 的重点不是否定这些模型，而是提供一个更统一的底座，让它们可以组合，而不是互相割裂。

## 进一步阅读

如果你想继续深入 ReBAC 的理论、工程落地和行业实现，可以阅读：

- [关系型授权专题合集](docs/rebac/README.md)

如果你要了解代码结构、模块边界和 coding agent 开发规则，请阅读：

- [Agent 工作指南](AGENTS.md)
