package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * FGA Expand 结果 VO —— 树形的 userset 展开
 *
 * <p>对齐 OpenFGA 的 Expand 响应：每个节点可以是 leaf / userset / computed / ttu / union / intersection / difference。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaExpandTreeVO {

    /**
     * 根节点
     */
    private Node root;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Node {
        /**
         * 节点类型：LEAF / UNION / INTERSECTION / DIFFERENCE / COMPUTED / TTU
         */
        private NodeType nodeType;

        /**
         * 该节点解析目标形如 {@code document:doc-1#viewer}
         */
        private String target;

        /**
         * LEAF 时的主体集合
         */
        private List<FgaSubjectVO> leaves;

        /**
         * 组合节点的子节点
         */
        private List<Node> children;
    }

    public enum NodeType {
        LEAF, UNION, INTERSECTION, DIFFERENCE, COMPUTED, TTU
    }
}
