/**
 * ZUS 权限系统应用层（Application / Service Layer）
 *
 * <p>本包负责组织用例流程、事务边界、DTO 组装与提交后通知，
 * 通过编排领域对象与基础设施能力来完成业务场景，不直接承载领域规则本身。
 *
 * <h2>包结构说明</h2>
 *
 * <pre>
 * service/
 * ├── application/        应用服务接口、实现与用例编排
 * │   ├── impl/           具体用例实现
 * │   └── coordinator/    应用层编排组件
 * ├── conv/               领域对象 / 读侧视图 / DTO 转换
 * ├── dto/                Command、Query、Response 等数据传输对象
 * ├── event/              应用层事件发布契约
 * │   └── application/    提交后应用事件
 * ├── listener/           应用事件监听器（事务提交后副作用）
 * ├── bean/               领域服务装配配置
 * ├── port/               应用层端口与默认轻量实现
 * └── exception/          应用层异常
 * </pre>
 *
 * <h2>职责边界</h2>
 * <ul>
 *   <li>参数校验、事务控制、结果映射由应用层负责</li>
 *   <li>核心业务规则优先下沉到聚合根、值对象和领域服务</li>
 *   <li>只读场景优先依赖 Read Port，避免命令仓储继续膨胀</li>
 *   <li>提交后的通知使用应用事件表达，不再将这类语义混入领域事件</li>
 *   <li>外部补充查询通过共享协作契约接入，不进入领域规则与聚合边界</li>
 * </ul>
 */
package org.kitona.zus.service;
