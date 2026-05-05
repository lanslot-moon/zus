package org.kitona.zus.domain.authorization.evaluation.explain;

/**
 * explain 节点的结构化原因。
 */
public enum EvaluationExplainReason {
    /**
     * 当前 relation 已经被证明成立。
     */
    RELATION_ALLOWED,

    /**
     * 当前 relation 无法被证明成立。
     */
    RELATION_DENIED,

    /**
     * 当前 rewrite 节点已经被证明成立。
     */
    NODE_ALLOWED,

    /**
     * 当前 rewrite 节点无法被证明成立。
     */
    NODE_DENIED,

    /**
     * 当前 subject 直接命中了 object#relation 上的 tuple。
     */
    DIRECT_TUPLE_MATCHED,

    /**
     * tuple-to-userset 节点通过中间 tuple 找到了可继续传播的对象。
     */
    TUPLE_TO_USERSET_LINK_MATCHED,

    /**
     * 当前 object#relation 下没有任何 tuple 能够匹配请求 subject。
     */
    NO_TUPLE_MATCHED,

    /**
     * 编译模型中不存在当前 object type 对应的 relation 定义。
     */
    NO_RELATION_DEFINITION,

    /**
     * tuple 存在且可见，但 tuple subject 与请求 subject 不匹配。
     */
    SUBJECT_NOT_MATCHED,

    /**
     * tuple 已经过期，因此不能参与授权证明。
     */
    TUPLE_EXPIRED,

    /**
     * tuple 绑定的条件表达式求值通过。
     */
    CONDITION_PASSED,

    /**
     * tuple 绑定的条件表达式求值失败。
     */
    CONDITION_FAILED,

    /**
     * tuple 绑定了条件定义 ID，但当前模型中找不到对应条件定义。
     */
    CONDITION_DEFINITION_MISSING,

    /**
     * tuple subject 不满足模型 relation 上声明的类型限制。
     */
    RELATION_RESTRICTION_FAILED,

    /**
     * 递归求值时发现当前目标已经在访问路径中，说明存在循环引用。
     */
    CYCLE_DETECTED,

    /**
     * 递归深度超过 evaluator 允许的最大深度。
     */
    DEPTH_LIMIT_EXCEEDED,

    /**
     * 当前求值目标命中了本次请求内 memo，且缓存结果为允许。
     */
    MEMO_ALLOWED,

    /**
     * 当前求值目标命中了本次请求内 memo，且缓存结果为拒绝。
     */
    MEMO_DENIED,

    /**
     * evaluator 遇到了未注册策略的 rewrite 节点。
     */
    UNSUPPORTED_NODE,

    /**
     * explain 节点数量超过上限，解释树已被截断。
     */
    TRACE_TRUNCATED
}
