package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.enums.EvaluationNodeType;

/**
 * explain 原因定义集合。
 *
 * <p>授权解释树中的原因需要区分为节点完成原因和业务证据原因。节点完成原因描述
 * relation 或 rewrite 节点在出栈时的最终结论，用来回答某个求值节点最终是否被证明成立。
 * 业务证据原因描述求值过程中出现的具体事实，用来回答为什么能够成立或为什么失败，例如
 * direct tuple 命中、条件失败、tuple 过期、递归深度超限和 memo 命中。
 *
 * <p>这两类原因的生命周期和语义边界不同。节点完成原因通常由 evaluator 在统一模板中
 * 根据节点类型和成功状态自动生成，业务证据原因必须由具体业务位置显式记录。如果把二者
 * 混在一个平铺枚举中，默认完成原因可能会误用 tuple 或 condition 证据，业务证据也可能
 * 被当成节点最终结论，从而导致 explain 树的层级含义不清晰。
 *
 * <p>因此该类型只保留统一契约，真正可写入解释树的原因由
 * {@link NodeCompletionReason} 和 {@link BusinessEvidenceReason} 两个枚举表达。
 * 这样既能让解释树使用统一类型保存原因，又能保证节点总结和业务证据不会相互污染。
 */
public sealed interface EvaluationExplainReason
        permits EvaluationExplainReason.NodeCompletionReason, EvaluationExplainReason.BusinessEvidenceReason {

    /**
     * 返回当前原因所属的 explain 节点类型。
     *
     * @return explain 节点类型
     */
    EvaluationNodeType getNodeType();

    /**
     * 返回 reason 的稳定编码。
     *
     * <p>两个实现都是枚举，使用枚举名作为 API/DTO 的稳定原因码。
     *
     * @return reason 编码
     */
    String name();

    /**
     * 判断当前 reason 是否表示成功证明。
     *
     * @return 成功证明原因返回 {@code true}
     */
    default boolean isSuccess() {
        return false;
    }

    /**
     * 判断当前 reason 是否属于节点完成时的默认总结原因。
     *
     * @return 节点完成原因返回 {@code true}
     */
    default boolean isNodeCompletionReason() {
        return this instanceof NodeCompletionReason;
    }

    /**
     * 按节点类型和成功状态返回该节点的默认完成原因。
     *
     * @param nodeType explain 节点类型
     * @param success  节点是否证明成立
     * @return 对应节点类型的默认完成原因
     */
    static NodeCompletionReason defaultFor(EvaluationNodeType nodeType, boolean success) {
        return NodeCompletionReason.resolve(nodeType, success);
    }

    /**
     * 节点完成原因。
     *
     * <p>该分类用于 relation/rewrite 节点出栈时的默认总结，因此必须同时携带
     * {@link EvaluationNodeType} 和成功状态。
     */
    enum NodeCompletionReason implements EvaluationExplainReason {
        /**
         * 当前 relation 已经被证明成立。
         */
        RELATION_ALLOWED(EvaluationNodeType.RELATION, true),

        /**
         * 当前 relation 无法被证明成立。
         */
        RELATION_DENIED(EvaluationNodeType.RELATION, false),

        /**
         * 未细分 rewrite 节点成功完成时的兜底原因。
         */
        NODE_ALLOWED(EvaluationNodeType.UNSUPPORTED, true),

        /**
         * 未细分 rewrite 节点失败完成时的兜底原因。
         */
        NODE_DENIED(EvaluationNodeType.UNSUPPORTED, false),

        /**
         * union 节点至少有一个分支证明成立，因此 union 证明成立。
         */
        UNION_BRANCH_ALLOWED(EvaluationNodeType.UNION, true),

        /**
         * union 节点没有任何分支证明成立，因此 union 证明失败。
         */
        UNION_NO_BRANCH_ALLOWED(EvaluationNodeType.UNION, false),

        /**
         * intersection 节点所有分支都证明成立，因此 intersection 证明成立。
         */
        INTERSECTION_ALL_BRANCHES_ALLOWED(EvaluationNodeType.INTERSECTION, true),

        /**
         * intersection 节点至少有一个分支证明失败，因此 intersection 证明失败。
         */
        INTERSECTION_BRANCH_DENIED(EvaluationNodeType.INTERSECTION, false),

        /**
         * exclusion 节点左侧证明成立且右侧未证明成立，因此 exclusion 证明成立。
         */
        EXCLUSION_LEFT_ALLOWED_RIGHT_DENIED(EvaluationNodeType.EXCLUSION, true),

        /**
         * exclusion 节点左侧未证明成立或右侧被证明成立，因此 exclusion 证明失败。
         */
        EXCLUSION_NOT_SATISFIED(EvaluationNodeType.EXCLUSION, false),

        /**
         * computed userset 节点引用的 relation 被证明成立。
         */
        COMPUTED_USERSET_ALLOWED(EvaluationNodeType.COMPUTED_USERSET, true),

        /**
         * computed userset 节点引用的 relation 未能证明成立。
         */
        COMPUTED_USERSET_DENIED(EvaluationNodeType.COMPUTED_USERSET, false),

        /**
         * direct relation reference 节点引用的 relation 被证明成立。
         */
        DIRECT_RELATION_REFERENCE_ALLOWED(EvaluationNodeType.DIRECT_RELATION_REFERENCE, true),

        /**
         * direct relation reference 节点引用的 relation 未能证明成立。
         */
        DIRECT_RELATION_REFERENCE_DENIED(EvaluationNodeType.DIRECT_RELATION_REFERENCE, false),

        /**
         * tuple-to-userset 节点通过中间对象 relation 证明成立。
         */
        TUPLE_TO_USERSET_ALLOWED(EvaluationNodeType.TUPLE_TO_USERSET, true),

        /**
         * tuple-to-userset 节点没有找到可证明成立的传播路径。
         */
        TUPLE_TO_USERSET_DENIED(EvaluationNodeType.TUPLE_TO_USERSET, false);

        /**
         * 节点类型。
         */
        private final EvaluationNodeType nodeType;

        /**
         * 节点完成结果是否成功。
         */
        private final boolean success;

        NodeCompletionReason(EvaluationNodeType nodeType, boolean success) {
            this.nodeType = nodeType;
            this.success = success;
        }

        @Override
        public EvaluationNodeType getNodeType() {
            return nodeType;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        /**
         * 按节点类型和成功状态解析默认节点完成原因。
         *
         * @param nodeType 节点类型
         * @param success  节点是否证明成立
         * @return 对应的节点完成原因；不存在精确原因时返回通用兜底原因
         */
        public static NodeCompletionReason resolve(EvaluationNodeType nodeType, boolean success) {
            for (NodeCompletionReason value : NodeCompletionReason.values()) {
                if (value.getNodeType() == nodeType && value.isSuccess() == success) {
                    return value;
                }
            }
            return success ? NODE_ALLOWED : NODE_DENIED;
        }
    }

    /**
     * 业务证据原因。
     *
     * <p>该分类用于具体业务位置记录证据，只携带“证据应该挂在哪类节点下”。
     * 成功或失败语义由枚举自身的业务含义派生，而不是作为构造参数重复传入。
     */
    enum BusinessEvidenceReason implements EvaluationExplainReason {
        /**
         * 当前 subject 直接命中了 object#relation 上的 tuple。
         */
        DIRECT_TUPLE_MATCHED(EvaluationNodeType.TUPLE, true),

        /**
         * tuple-to-userset 节点通过中间 tuple 找到了可继续传播的对象。
         */
        TUPLE_TO_USERSET_LINK_MATCHED(EvaluationNodeType.TUPLE, true),

        /**
         * 当前 object#relation 下没有任何 tuple 能够匹配请求 subject。
         */
        NO_TUPLE_MATCHED(EvaluationNodeType.TUPLE, false),

        /**
         * 编译模型中不存在当前 object type 对应的 relation 定义。
         */
        NO_RELATION_DEFINITION(EvaluationNodeType.RELATION, false),

        /**
         * tuple 存在且可见，但 tuple subject 与请求 subject 不匹配。
         */
        SUBJECT_NOT_MATCHED(EvaluationNodeType.TUPLE, false),

        /**
         * tuple 已经过期，因此不能参与授权证明。
         */
        TUPLE_EXPIRED(EvaluationNodeType.TUPLE, false),

        /**
         * tuple 绑定的条件表达式求值通过。
         */
        CONDITION_PASSED(EvaluationNodeType.TUPLE, true),

        /**
         * tuple 绑定的条件表达式求值失败。
         */
        CONDITION_FAILED(EvaluationNodeType.TUPLE, false),

        /**
         * tuple 绑定了条件定义 ID，但当前模型中找不到对应条件定义。
         */
        CONDITION_DEFINITION_MISSING(EvaluationNodeType.TUPLE, false),

        /**
         * tuple subject 不满足模型 relation 上声明的类型限制。
         */
        RELATION_RESTRICTION_FAILED(EvaluationNodeType.TUPLE, false),

        /**
         * 递归求值时发现当前目标已经在访问路径中，说明存在循环引用。
         */
        CYCLE_DETECTED(EvaluationNodeType.RELATION, false),

        /**
         * 递归深度超过 evaluator 允许的最大深度。
         */
        DEPTH_LIMIT_EXCEEDED(EvaluationNodeType.RELATION, false),

        /**
         * 当前求值目标命中了本次请求内 memo，且缓存结果为允许。
         */
        MEMO_ALLOWED(EvaluationNodeType.RELATION, true),

        /**
         * 当前求值目标命中了本次请求内 memo，且缓存结果为拒绝。
         */
        MEMO_DENIED(EvaluationNodeType.RELATION, false),

        /**
         * evaluator 遇到了未注册策略的 rewrite 节点。
         */
        UNSUPPORTED_NODE(EvaluationNodeType.UNSUPPORTED, false),

        /**
         * explain 节点数量超过上限，解释树已被截断。
         */
        TRACE_TRUNCATED(EvaluationNodeType.UNSUPPORTED, false);

        /**
         * 证据所属节点类型。
         */
        private final EvaluationNodeType nodeType;

        /**
         * 节点完成结果是否成功。
         */
        private final boolean success;

        BusinessEvidenceReason(EvaluationNodeType nodeType, boolean success) {
            this.nodeType = nodeType;
            this.success = success;
        }

        @Override
        public EvaluationNodeType getNodeType() {
            return nodeType;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }
    }
}
