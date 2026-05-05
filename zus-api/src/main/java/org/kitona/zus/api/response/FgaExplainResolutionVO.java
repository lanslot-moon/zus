package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Explain resolution 树。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaExplainResolutionVO {

    private boolean allowed;
    private String requestedZookie;
    private String currentZookie;
    private String staleSnapshotDiagnosis;
    private boolean truncated;
    private Node root;

    /**
     * Explain 树节点 VO。
     *
     * <p>用于表达 relation、rewrite 节点和 tuple 叶子节点的结构化结果。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node {
        private String nodeType;
        private String target;
        private String subject;
        private String relation;
        private boolean allowed;
        private String reason;
        private Tuple tuple;
        private Condition condition;
        private List<Node> children;
    }

    /**
     * Explain 中展示的 tuple 明细。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tuple {
        private String object;
        private String relation;
        private String subject;
        private boolean wildcard;
        private String zookie;
        private Long expiresAt;
        private Long conditionDefinitionId;
        private String conditionName;
    }

    /**
     * Explain 中展示的条件求值明细。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Condition {
        private Long conditionDefinitionId;
        private String conditionName;
        private boolean passed;
    }
}
