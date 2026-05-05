package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.enums.EvaluationNodeType;

import java.util.List;

/**
 * 授权解释树节点。
 *
 * <p>每个节点对应一次 relation、rewrite 节点或 tuple 证明步骤。
 * 节点只保存结构化事实，不把业务语义隐藏在自由文本中。
 *
 * @param nodeType  节点类型
 * @param target    当前求值目标，如 {@code document:doc-1#viewer}
 * @param subject   当前主体，如 {@code user:alice}
 * @param relation  当前关系
 * @param allowed   该节点是否证明成立
 * @param reason    结构化原因
 * @param tuple     参与该节点判断的 tuple 详情
 * @param condition 条件求值详情
 * @param children  子节点
 */
public record EvaluationExplainNode(EvaluationNodeType nodeType,
                                    String target,
                                    String subject,
                                    String relation,
                                    boolean allowed,
                                    EvaluationExplainReason reason,
                                    TupleExplainDetail tuple,
                                    ConditionExplainDetail condition,
                                    List<EvaluationExplainNode> children) {

    public EvaluationExplainNode {
        children = children == null ? List.of() : List.copyOf(children);
    }
}
