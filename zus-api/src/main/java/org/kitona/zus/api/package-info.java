/**
 * ZUS 权限系统接口层（API Layer）
 *
 * <p>本包对外暴露 HTTP / Facade / SSE 等入口协议，
 * 负责请求对象校验、协议转换、权限上下文接入与响应封装，
 * 不直接承载领域规则，也不直接操作持久化层。
 *
 * <h2>包结构说明</h2>
 *
 * <pre>
 * api/
 * ├── controller/         HTTP API 接口与实现
 * ├── facade/             对外 Facade / RPC 接口实现
 * ├── request/            入参对象（VO / Request）
 * ├── response/           出参对象（VO / Result）
 * ├── converter/          API 协议对象转换
 * ├── event/              API 层事件发布实现（如 Watch SSE）
 * ├── sse/                SSE 连接管理
 * ├── permission/         接口层权限上下文与切面
 * └── verify/             校验分组定义
 * </pre>
 *
 * <h2>职责边界</h2>
 * <ul>
 *   <li>API 层只做协议适配和入参校验，不实现领域决策</li>
 *   <li>所有业务流程统一下沉到 {@code zus-service} 应用层</li>
 *   <li>对外返回的 VO / Result 是接口协议模型，不等同于领域对象</li>
 *   <li>Watch / SSE 等连接管理属于接口层技术职责，不反向污染领域模型</li>
 * </ul>
 */
package org.kitona.zus.api;
