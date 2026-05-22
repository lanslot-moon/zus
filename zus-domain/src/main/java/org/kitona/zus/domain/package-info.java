/**
 * ZUS 授权系统领域层。
 *
 * <p>本包是 ZUS 的领域核心，承载 ReBAC/FGA 授权领域的聚合、实体、值对象、
 * 领域服务、规格、策略、端口以及读侧契约。当前包结构已经按 DDD 与六边形架构
 * 完成边界收敛：领域层不依赖 Spring、MyBatis、Redis、HTTP、Controller、
 * Request、Response、PO、Mapper 等技术或接口层类型；命令侧聚合仓储和 CQRS
 * 读侧端口已经分离；核心值对象与读侧分页对象已经分离。
 *
 * <h2>DDD 合规基线</h2>
 * <p>后续检查本模块时，应以本段作为判断基线，避免把已经确认合理的结构反复推翻。
 * 当前 {@code zus-domain} 在项目现有模块依赖方向下符合 DDD/六边形架构规范：
 * <ul>
 *   <li>领域层只表达授权业务概念、领域规则和外部能力契约，不持有基础设施实现。</li>
 *   <li>聚合仓储位于 {@code repository}，只负责聚合根的装载、保存和移除。</li>
 *   <li>列表、分页、Watch、读侧展示等查询能力位于 {@code read}，不混入聚合仓储。</li>
 *   <li>读侧端口返回 {@code read.view} 中的读模型，不返回聚合根或数据库 PO。</li>
 *   <li>分页结果位于 {@code read.page}，不再放入核心 {@code valueobject}。</li>
 *   <li>领域类禁止使用 {@code @Data} 和 {@code @Setter}，只允许有限 Getter 或不可变 record。</li>
 *   <li>基础设施需要实现的外部能力通过 {@code port} 暴露，适配器放在 {@code zus-infrastructure}。</li>
 * </ul>
 *
 * <h2>当前包结构</h2>
 * <pre>
 * domain/
 * │
 * ├── authorization/                         授权业务上下文
 * │   ├── audit/                             审计与 changelog 领域对象
 * │   ├── model/                             Authorization Model 聚合及模型结构
 * │   ├── store/                             Store 聚合及生命周期规则
 * │   ├── tuple/                             Relation Tuple 聚合、tuple key 与条件绑定
 * │   ├── user/                              用户画像子域
 * │   │   └── repository/                    用户画像聚合仓储
 * │   └── evaluation/                        授权求值子域
 * │       ├── compiled/                      编译后的授权模型与关系定义
 * │       ├── explain/                       Explain 解释树、证明节点和证据详情
 * │       ├── nodes/                         Rewrite AST 节点
 * │       ├── runtime/                       单次求值请求、运行时上下文和递归保护
 * │       ├── specification/                 tuple 可见性、主体匹配、关系限制等规格
 * │       └── strategy/                      rewrite 节点求值策略
 * │
 * ├── enums/                                 领域枚举
 * │   ├── EvaluationNodeType                 Explain / AST 求值节点类型
 * │   ├── ModelPublishStatus                 授权模型发布状态
 * │   └── StoreStatus                        Store 生命周期状态
 * │
 * ├── port/                                  领域出站端口
 * │   ├── IAuditContextProvider              审计上下文提供能力
 * │   ├── ICompiledModelCompiler             结构化模型编译能力
 * │   ├── IConditionEvaluator                条件表达式求值能力
 * │   ├── IDirectTupleReader                 direct tuple 读取能力
 * │   ├── ITupleLinkReader                   tuple-to-userset 链接读取能力
 * │   ├── ISubjectObjectCandidateReader      ListObjects 候选对象读取能力
 * │   ├── IObjectSubjectCandidateReader      ListSubjects 候选主体读取能力
 * │   ├── IModelSnapshotRenderer             模型发布快照渲染能力
 * │   └── IZookieSequencePort                Store 级 zookie 序列能力
 * │
 * ├── read/                                  CQRS 读侧契约
 * │   ├── criteria/                          读侧查询条件对象
 * │   ├── page/                              读侧分页结果对象
 * │   ├── port/                              读侧查询端口
 * │   └── view/                              读侧视图模型
 * │
 * ├── repository/                            命令侧聚合仓储
 * │   ├── IAuthorizationModelDomainRepository Authorization Model 聚合仓储
 * │   ├── IStoreDomainRepository             Store 聚合仓储
 * │   ├── ITupleDomainRepository             Relation Tuple 聚合仓储
 * │   └── IChangelogDomainRepository         Changelog 写侧仓储
 * │
 * ├── service/                               领域服务
 * │   ├── PermissionCheckEvaluator           单点授权证明器
 * │   ├── PermissionSearchEvaluator          ListObjects/ListSubjects 搜索求值器
 * │   └── TupleMutationDomainService         tuple 写删事实与 changelog 构建规则
 * │
 * └── valueobject/                           核心领域值对象
 *     ├── ObjectRef                          授权对象引用
 *     ├── Subject                            授权主体引用
 *     ├── Zookie                             一致性令牌
 *     ├── PermissionCheckResult              授权判定结果
 *     └── PermissionCheckStatus              授权判定状态
 * </pre>
 *
 * <h2>命令侧与读侧边界</h2>
 * <p>{@code repository} 是命令侧聚合仓储包，不承载列表、分页、按条件查询、Watch
 * 等读侧语义。聚合仓储接口只应暴露类似 {@code findById}、{@code save}、
 * {@code remove} 的聚合访问能力；创建、发布、激活、废弃、删除等业务动作属于聚合
 * 或应用用例，不应作为 Repository 方法名泄漏。
 *
 * <p>{@code read} 是领域层定义的 CQRS 读侧契约。它放在 domain 中是当前模块依赖方向下
 * 的刻意选择：基础设施适配器依赖 domain 并实现这些读侧端口，service 应用层依赖这些
 * 端口组织读用例。除非未来整体调整模块依赖方向，否则不要把 {@code read.port} 单独移动
 * 到 {@code zus-service}，否则会造成 {@code zus-infrastructure -> zus-service} 的反向依赖。
 *
 * <h2>端口放置说明</h2>
 * <p>{@code port} 中的接口是领域规则或应用协调当前必须依赖、且由基础设施实现的外部能力。
 * 例如模型编译、条件求值、tuple 读取、zookie 序列、模型快照渲染和审计上下文。它们不是
 * 技术实现本身，只是领域所需能力的抽象契约。只要基础设施仍然通过 {@code zus-domain}
 * 暴露适配点，这些端口放在 domain 中就是符合六边形架构的。
 *
 * <h2>聚合与领域服务</h2>
 * <ul>
 *   <li>{@code StoreAggregate} 维护 Store 生命周期、状态和当前生效模型指针。</li>
 *   <li>{@code AuthorizationModelAggregate} 维护授权模型结构、发布状态和 DSL 快照。</li>
 *   <li>{@code RelationTuple} 表达授权事实，包含 subject relation、wildcard、expires、condition 和 zookie。</li>
 *   <li>{@code Changelog} 表达 tuple 写删产生的审计事实。</li>
 *   <li>{@code PermissionCheckEvaluator} 负责 Check 与 Explain 的统一授权证明语义。</li>
 *   <li>{@code PermissionSearchEvaluator} 负责 ListObjects 与 ListSubjects 的搜索语义，并复用 Check 求值内核。</li>
 *   <li>{@code TupleMutationDomainService} 只构建 tuple 事实和 changelog 事实，不负责事务、持久化编排或 zookie 生成。</li>
 * </ul>
 *
 * <h2>禁止事项</h2>
 * <ul>
 *   <li>禁止在本模块引入 Spring Bean 注解、Controller、Mapper、PO、HTTP Request/Response 等技术类型。</li>
 *   <li>禁止在领域对象上使用 {@code @Data}、{@code @Setter} 或暴露可变集合。</li>
 *   <li>禁止把分页查询、列表查询、Watch 查询放回命令侧 Repository。</li>
 *   <li>禁止让读侧端口返回数据库 PO 或完整聚合根。</li>
 *   <li>禁止用字符串拆分或 {@code toString()} 承载核心业务语义。</li>
 * </ul>
 *
 * @author kitona
 * @version 2.1.0
 * @since 2025-01-15
 */
package org.kitona.zus.domain;
