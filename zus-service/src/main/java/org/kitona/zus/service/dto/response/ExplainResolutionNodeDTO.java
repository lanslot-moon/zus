package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Explain 树节点 DTO。
 *
 * <p>该对象是领域 explain 节点的 API 友好投影，保留完整树形结构，
 * 但只暴露字符串化后的节点类型和 reason 编码。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainResolutionNodeDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = -3887477239760710049L;

    /**
     * explain 节点类型编码。
     */
    private String nodeType;

    /**
     * 当前节点对应的求值目标。
     */
    private String target;

    /**
     * 当前节点正在证明的主体。
     */
    private String subject;

    /**
     * 当前节点正在证明的关系。
     */
    private String relation;

    /**
     * 当前节点是否被证明成立。
     */
    private boolean allowed;

    /**
     * 当前节点的结构化原因编码。
     */
    private String reason;

    /**
     * 当前节点关联的 tuple 证据。
     */
    private ExplainTupleDTO tuple;

    /**
     * 当前节点关联的条件求值证据。
     */
    private ExplainConditionDTO condition;

    /**
     * 当前节点的子证明节点。
     */
    private List<ExplainResolutionNodeDTO> children;

    /**
     * 从领域 explain 节点构造响应节点 DTO。
     *
     * @param node 领域 explain 节点
     * @return 响应节点 DTO
     */
    static ExplainResolutionNodeDTO from(EvaluationExplainNode node) {
        if (node == null) {
            return null;
        }
        return ExplainResolutionNodeDTO.builder()
                .nodeType(node.nodeType().name())
                .target(node.target())
                .subject(node.subject())
                .relation(node.relation())
                .allowed(node.allowed())
                .reason(node.reason() != null ? node.reason().name() : null)
                .tuple(ExplainTupleDTO.from(node.tuple()))
                .condition(ExplainConditionDTO.from(node.condition()))
                .children(node.children().stream().map(ExplainResolutionNodeDTO::from).toList())
                .build();
    }
}
