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

    /**
     * 本次 explain 对应的最终授权结果。
     */
    private boolean allowed;

    /**
     * 调用方请求中携带的一致性 zookie。
     */
    private String requestedZookie;

    /**
     * Store 当前最新的一致性 zookie。
     */
    private String currentZookie;

    /**
     * 当请求基于旧快照被拒绝时的诊断结果。
     */
    private String staleSnapshotDiagnosis;

    /**
     * explain 树是否因为节点数量上限被截断。
     */
    private boolean truncated;

    /**
     * 面向调试者展示的叙事化投影。
     */
    private Narrative narrative;

    /**
     * 完整机器可读 explain 树根节点。
     */
    private Node root;

    /**
     * Explain 面向调试者的叙事投影。
     *
     * <p>root 仍然保留完整机器可读证明树；narrative 只承载调试页面友好的摘要和调用时序。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Narrative {
        /**
         * 面向调试者的一句话解释摘要。
         */
        private String summary;

        /**
         * evaluator 执行过程的前序时序展开。
         */
        private List<TimelineStep> timeline;
    }

    /**
     * Explain 调用时序步骤。
     *
     * <p>该列表是 resolution tree 的前序展开，适合在调试页面按 evaluator 调用顺序展示。
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimelineStep {
        /**
         * 调用时序中的步骤序号。
         */
        private int step;

        /**
         * 当前步骤在 explain 树中的深度。
         */
        private int depth;

        /**
         * 当前步骤对应的节点类型编码。
         */
        private String nodeType;

        /**
         * 当前步骤对应的求值目标。
         */
        private String target;

        /**
         * 当前步骤正在证明的主体。
         */
        private String subject;

        /**
         * 当前步骤正在证明的关系。
         */
        private String relation;

        /**
         * 当前步骤的展示状态。
         */
        private String status;

        /**
         * 当前步骤是否证明成立。
         */
        private boolean allowed;

        /**
         * 当前步骤的结构化原因编码。
         */
        private String reason;

        /**
         * 当前步骤面向调试者的解释文案。
         */
        private String message;

        /**
         * 当前步骤命中的 tuple 摘要。
         */
        private String tuple;

        /**
         * 当前步骤是否属于关键步骤。
         */
        private boolean keyStep;
    }

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
        private Tuple tuple;

        /**
         * 当前节点关联的条件求值证据。
         */
        private Condition condition;

        /**
         * 当前节点的子证明节点。
         */
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
        /**
         * tuple 所属对象，格式通常为 type:id。
         */
        private String object;

        /**
         * tuple 绑定的对象关系。
         */
        private String relation;

        /**
         * tuple 指向的主体，可能是 direct subject 或 userset subject。
         */
        private String subject;

        /**
         * tuple 是否为 wildcard 主体。
         */
        private boolean wildcard;

        /**
         * tuple 写入时生成的一致性版本。
         */
        private String zookie;

        /**
         * tuple 过期时间戳，为空表示不过期。
         */
        private Long expiresAt;

        /**
         * tuple 绑定的条件定义 ID。
         */
        private Long conditionDefinitionId;

        /**
         * tuple 绑定的条件名称。
         */
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
        /**
         * tuple 绑定的条件定义 ID。
         */
        private Long conditionDefinitionId;

        /**
         * tuple 绑定的条件名称。
         */
        private String conditionName;

        /**
         * 条件表达式在本次请求上下文下是否求值通过。
         */
        private boolean passed;
    }
}
