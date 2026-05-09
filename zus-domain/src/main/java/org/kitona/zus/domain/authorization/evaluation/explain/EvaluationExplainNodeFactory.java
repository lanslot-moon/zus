package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.List;

/**
 * explain 节点工厂。
 *
 * <p>集中处理节点 target、tuple 明细与兜底根节点的创建，
 * 避免 collector 和 tree builder 反复拼接字符串或重复组装节点字段。
 */
final class EvaluationExplainNodeFactory {

    /**
     * target 中对象引用与 relation 之间的分隔符。
     */
    private static final String TARGET_RELATION_SEPARATOR = "#";

    /**
     * 创建 relation/rewrite 分支节点。
     *
     * @param nodeType 节点类型
     * @param subject  当前求值主体
     * @param object   当前求值对象
     * @param relation 当前求值关系
     * @return 分支节点构建器
     */
    TraceNodeBuilder branch(EvaluationNodeType nodeType, Subject subject, ObjectRef object, String relation) {
        return TraceNodeBuilder.branch(nodeType, buildTarget(object, relation), subject.toString(), relation);
    }

    /**
     * 创建 tuple 叶子节点。
     *
     * @param tuple     参与证明的 tuple
     * @param allowed   tuple 判断是否成立
     * @param reason    结构化原因
     * @param condition 条件解释详情，可为空
     * @return tuple 叶子节点构建器
     */
    TraceNodeBuilder tuple(RelationTuple tuple, boolean allowed, EvaluationExplainReason reason,
                           ConditionExplainDetail condition) {
        TraceNodeBuilder node = TraceNodeBuilder.branch(
                EvaluationNodeType.TUPLE,
                buildTarget(tuple.getObject(), tuple.getRelation()),
                tuple.getSubject().toString(),
                tuple.getRelation()
        );
        node.mark(allowed, reason);
        node.attachTuple(TupleExplainDetail.from(tuple), condition);
        return node;
    }

    /**
     * 创建没有执行节点时使用的兜底根节点。
     *
     * @param allowed 最终授权结果
     * @return 兜底根节点
     */
    EvaluationExplainNode fallbackRoot(boolean allowed) {
        return new EvaluationExplainNode(
                EvaluationNodeType.RELATION,
                "",
                "",
                "",
                allowed,
                EvaluationExplainReason.NodeCompletionReason.resolve(EvaluationNodeType.RELATION, allowed),
                null,
                null,
                List.of()
        );
    }

    private String buildTarget(ObjectRef object, String relation) {
        return object + TARGET_RELATION_SEPARATOR + relation;
    }
}
