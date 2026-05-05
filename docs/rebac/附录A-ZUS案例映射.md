# 附录 A：ZUS 案例映射

## 为什么要把 ZUS 放到附录

正文讨论的是通用路线、工程原则和行业实践。  
而 `ZUS` 在这里的角色，是一个真实案例：

- 它帮助验证正文理论不是纸上谈兵
- 它帮助读者把“模型、事实、引擎、一致性、条件”这些概念映射到实际实现

但它并不是行业标准答案。  
因此附录里所有判断都应理解为：

- **案例映射**

而不是：

- **通用规范**

![ZUS 案例映射总览](/Users/kitona/IdeaProjects/zus/zus-api/docs/rebac/diagrams/exported/10-zus-case-mapping.svg)

## 一、当前 ZUS 更接近什么类型的系统

从能力边界看，当前 `ZUS` 更接近一套：

- 以 `ReBAC` 为核心语义底座
- 以 tuple 为事实层
- 通过 condition 支持 `ABAC` 混合
- 同时可以承载 `ACL / RBAC`

的细粒度授权系统。

它已经具备现代 FGA 系统的几项关键骨架：

- Store 隔离
- 模型版本化
- 结构化模型
- tuple 事实表
- 条件定义与条件实例
- `Check / ListObjects / ListUsers`
- 一致性版本
- changelog / watch

## 二、schema 映射

当前核心 schema 位于：

- `/Users/kitona/IdeaProjects/zus/zus-infrastructure/src/main/resources/db/table_schema.sql`

### 1. Store 层

`fga_store`

它承担的是：

- 授权空间隔离
- 当前模型指针
- 当前 zookie/revision 视图

这是典型的 FGA 系统顶层容器语义。

### 2. 模型层

- `fga_auth_model`
- `fga_type_definition`
- `fga_relation_definition`
- `fga_type_restriction`

这些表共同表达：

- 类型
- 关系
- 限制
- 模型版本

这符合正文中所说的“规则层与事实层分层”。

### 3. 条件层

`fga_condition_definition`

这张表的意义在于：  
`ZUS` 没有把条件简单塞进某个业务字段，而是显式地把 condition template 作为模型层一部分建了出来。

### 4. 事实层

`fga_relation_tuple`

它已经覆盖了现代 FGA 事实层的关键字段：

- `subject_relation`
- `is_wildcard`
- `condition_definition_id`
- `condition_context`
- `expires_at`
- `zookie`

这说明 `ZUS` 的 tuple 设计不是“精简版 RBAC 关系表”，而是真正面向关系传播与条件增强的事实层。

### 5. 审计层

`fga_tuple_changelog`

这张表不仅仅是日志表，还承担：

- watch 基础
- 审计
- 变更回放
- 缓存失效参考边界

## 三、DSL 映射

当前语法定义位于：

- `/Users/kitona/IdeaProjects/zus/zus-infrastructure/src/main/java/org/kitona/zus/infrastructure/engine/parser/OpenFGAModel.g4`

从能力上看，它已支持：

- `model schema 1.1`
- `type`
- `relations`
- `define relation as|: rewrite`
- `this / self`
- `or / and / but not`
- `computed userset`
- `tuple-to-userset`
- `typed wildcard`
- `conditions`

这说明 `ZUS` 已经具备表达以下模型的语言能力：

- `ACL`
- `RBAC`
- `ReBAC`
- `ABAC` 混合模式

## 四、编译器映射

当前编译器核心位于：

- `/Users/kitona/IdeaProjects/zus/zus-infrastructure/src/main/java/org/kitona/zus/infrastructure/engine/compiler/CompiledAuthorizationModelCompiler.java`

它与正文原则的对齐点在于：

- 编译器是无状态的
- 语法解析与编译编排分离
- 输出 compiled model

这意味着 `ZUS` 已经走上了：

- `DSL / 结构化模型 -> 编译产物 -> evaluator`

的正确路线，而不是在运行时直接解释原始文本。

## 五、evaluator 映射

当前 evaluator 核心位于：

- `/Users/kitona/IdeaProjects/zus/zus-domain/src/main/java/org/kitona/zus/domain/service/PermissionEvaluator.java`

从结构上看，它已经具备几个很关键的成熟信号：

### 1. 统一入口

`Check / ListObjects / ListUsers` 已被收敛到统一 evaluator 内核。

### 2. 递归模板

通过统一递归模板承载：

- 深度限制
- memoization
- 循环检测

### 3. 节点策略

通过 rewrite node strategy 分发不同节点求值，而不是把全部逻辑堆进单个大方法里。

### 4. 读侧仓储分工

通过 direct tuple reader、link reader、candidate reader 分离不同读取语义。

这与正文中的引擎设计主线是高度一致的。

## 六、条件混合模式映射

`ZUS` 当前条件设计的关键点在于：

- condition definition 在模型层
- condition instance 在 tuple 层
- evaluator 在运行时结合 context 求值

这与正文中给出的推荐路线一致：

- 关系表达结构
- 条件表达上下文

它说明 `ZUS` 并没有把条件误当成结构替代层，而是把它放到了更合理的增强层位置。

## 七、一致性与 zookie 映射

`ZUS` 当前在 schema 中显式持有：

- store current zookie
- tuple zookie

这至少表明系统已经意识到：

- 一致性版本必须成为授权系统的一等设计对象

虽然与 Zanzibar/SpiceDB 那类成熟方案相比，后续还可以继续加强：

- token 传递策略
- 多节点刷新边界
- 列表查询快照能力

但方向是正确的。

## 八、与正文原则的逐项对齐

| 正文原则 | ZUS 对应实现 |
|---|---|
| 模型与事实分层 | auth model 系列表 vs relation tuple |
| tuple 是统一事实层 | `fga_relation_tuple` |
| 条件是增强层 | `fga_condition_definition` + tuple condition |
| evaluator 必须统一 | `PermissionEvaluator` |
| 递归保护不可缺 | `RecursionGuard` + recursive template |
| 编译器应无状态 | `CompiledAuthorizationModelCompiler` |
| 一致性要显式建模 | `zookie` |
| 变更需要可追踪 | changelog + watch |

## 九、当前 ZUS 还可以继续成熟的空间

这个附录不只是肯定案例，也需要指出工程上仍可继续强化的方向：

1. Explain 能力可以进一步增强
2. 多节点一致性与 token 传递策略可以继续清晰化
3. 图示化和模型测试资产还可以继续补充
4. 开源项目对比结论可以反哺到架构能力演进上

这些并不说明方向错误，恰恰说明 `ZUS` 已经进入了“从正确骨架走向成熟系统”的阶段。

## 附录小结

`ZUS` 最值得看的地方，不是它是否已经把每个角落都做到极致，而是它已经把 ReBAC 路线中最关键的几条主线连起来了：

- 结构化模型
- tuple 事实层
- 编译器
- evaluator
- condition
- zookie
- changelog

这说明前面正文里讨论的那套方法论，并不是只能停留在论文、产品或开源项目层面，它在真实项目中完全有可映射的落点。
