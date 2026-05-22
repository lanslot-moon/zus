package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.ConditionExplainDetail;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.TupleExplainDetail;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Explain 解释树 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainResolutionDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8177550699637708506L;

    private boolean allowed;
    private String requestedZookie;
    private String currentZookie;
    private String staleSnapshotDiagnosis;
    private boolean truncated;
    private NodeDTO root;

    /**
     * 从领域 trace 构造 DTO。
     */
    public static ExplainResolutionDTO from(EvaluationTrace trace) {
        if (trace == null) {
            return null;
        }
        return ExplainResolutionDTO.builder()
                .allowed(trace.allowed())
                .requestedZookie(trace.requestedZookie())
                .currentZookie(trace.currentZookie())
                .staleSnapshotDiagnosis(trace.staleSnapshotDiagnosis().name())
                .truncated(trace.truncated())
                .root(NodeDTO.from(trace.root()))
                .build();
    }

    /**
     * Explain 树节点 DTO。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NodeDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = -3887477239760710049L;

        private String nodeType;
        private String target;
        private String subject;
        private String relation;
        private boolean allowed;
        private String reason;
        private TupleDTO tuple;
        private ConditionDTO condition;
        private List<NodeDTO> children;

        /**
         * 转换from。
         *
         * @param node node 参数
         * @return 构建结果
         */
        private static NodeDTO from(EvaluationExplainNode node) {
            if (node == null) {
                return null;
            }
            return NodeDTO.builder()
                    .nodeType(node.nodeType().name())
                    .target(node.target())
                    .subject(node.subject())
                    .relation(node.relation())
                    .allowed(node.allowed())
                    .reason(node.reason() != null ? node.reason().name() : null)
                    .tuple(TupleDTO.from(node.tuple()))
                    .condition(ConditionDTO.from(node.condition()))
                    .children(node.children().stream().map(NodeDTO::from).toList())
                    .build();
        }
    }

    /**
     * Explain 中展示的 tuple 明细 DTO。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TupleDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 6359634895431287760L;

        private String object;
        private String relation;
        private String subject;
        private boolean wildcard;
        private String zookie;
        private Long expiresAt;
        private Long conditionDefinitionId;
        private String conditionName;

        /**
         * 转换from。
         *
         * @param detail detail 参数
         * @return 构建结果
         */
        private static TupleDTO from(TupleExplainDetail detail) {
            if (detail == null) {
                return null;
            }
            return TupleDTO.builder()
                    .object(detail.object())
                    .relation(detail.relation())
                    .subject(detail.subject())
                    .wildcard(detail.wildcard())
                    .zookie(detail.zookie())
                    .expiresAt(detail.expiresAt())
                    .conditionDefinitionId(detail.conditionDefinitionId())
                    .conditionName(detail.conditionName())
                    .build();
        }
    }

    /**
     * Explain 中展示的条件求值明细 DTO。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConditionDTO implements Serializable {

        @Serial
        private static final long serialVersionUID = 6128922511098284153L;

        private Long conditionDefinitionId;
        private String conditionName;
        private boolean passed;

        /**
         * 转换from。
         *
         * @param detail detail 参数
         * @return 构建结果
         */
        private static ConditionDTO from(ConditionExplainDetail detail) {
            if (detail == null) {
                return null;
            }
            return ConditionDTO.builder()
                    .conditionDefinitionId(detail.conditionDefinitionId())
                    .conditionName(detail.conditionName())
                    .passed(detail.passed())
                    .build();
        }
    }
}
