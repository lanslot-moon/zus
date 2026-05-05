package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.enums.EvaluationNodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * explain 节点构建期对象。
 *
 * <p>它只在单次 explain 过程中存在，最终会转换为不可变的
 * {@link EvaluationExplainNode}。
 */
final class TraceNodeBuilder {

    /**
     * 当前 explain 节点类型。
     */
    private final EvaluationNodeType nodeType;

    /**
     * 当前节点对应的求值目标，通常形如 {@code document:doc-1#viewer}。
     */
    private final String target;

    /**
     * 当前节点求值时使用的主体，通常形如 {@code user:alice}。
     */
    private final String subject;

    /**
     * 当前节点求值时使用的关系名称。
     */
    private final String relation;

    /**
     * 当前节点的子节点构建器集合。
     */
    private final List<TraceNodeBuilder> children = new ArrayList<>();

    /**
     * 当前节点最终是否证明成立。
     */
    private boolean allowed;

    /**
     * 当前节点的结构化解释原因。
     */
    private EvaluationExplainReason reason;

    /**
     * 当前节点关联的 tuple 详情，仅 tuple 叶子节点使用。
     */
    private TupleExplainDetail tuple;

    /**
     * 当前节点关联的条件求值详情，仅条件相关 tuple 节点使用。
     */
    private ConditionExplainDetail condition;

    private TraceNodeBuilder(EvaluationNodeType nodeType, String target, String subject, String relation) {
        this.nodeType = nodeType;
        this.target = target;
        this.subject = subject;
        this.relation = relation;
    }

    /**
     * 创建分支节点构建器。
     *
     * @param nodeType 节点类型
     * @param target   当前求值目标
     * @param subject  当前主体
     * @param relation 当前关系
     * @return 节点构建器
     */
    static TraceNodeBuilder branch(EvaluationNodeType nodeType, String target, String subject,
                                   String relation) {
        return new TraceNodeBuilder(nodeType, target, subject, relation);
    }

    /**
     * 追加子节点。
     *
     * @param child 子节点构建器
     */
    void addChild(TraceNodeBuilder child) {
        children.add(child);
    }

    /**
     * 标记当前节点结果。
     *
     * @param nextAllowed 节点是否成立
     * @param nextReason  结构化原因
     */
    void mark(boolean nextAllowed, EvaluationExplainReason nextReason) {
        this.allowed = nextAllowed;
        this.reason = nextReason;
    }

    /**
     * 完成当前节点，保留更具体的既有 reason。
     *
     * @param nextAllowed    节点最终是否成立
     * @param fallbackReason 缺省结构化原因
     */
    void complete(boolean nextAllowed, EvaluationExplainReason fallbackReason) {
        this.allowed = nextAllowed;
        if (reason == null) {
            reason = fallbackReason;
        }
    }

    /**
     * 绑定 tuple 与 condition 明细。
     *
     * @param tupleDetail     tuple 详情
     * @param conditionDetail 条件详情，可为空
     */
    void attachTuple(TupleExplainDetail tupleDetail, ConditionExplainDetail conditionDetail) {
        this.tuple = tupleDetail;
        this.condition = conditionDetail;
    }

    /**
     * 转换为不可变 explain 节点。
     *
     * @return 不可变 explain 节点
     */
    EvaluationExplainNode toImmutable() {
        return new EvaluationExplainNode(nodeType, target, subject, relation, allowed, reason, tuple, condition,
                children.stream().map(TraceNodeBuilder::toImmutable).toList());
    }
}
