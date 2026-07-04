package org.kitona.zus.domain.authorization.evaluation.graph;

/**
 * 授权模型图边类型。
 */
public enum GraphEdgeType {

    /**
     * self 节点的类型限制直连到关系（如 {@code user → document#viewer}）。
     */
    DIRECT,

    /**
     * 通过 computed userset 或同类型关系引用传播（如 {@code document#editor → document#viewer}）。
     */
    COMPUTED_USERSET,

    /**
     * 通过 tuple-to-userset 传播（如 {@code folder#editor → document#viewer}）。
     */
    TUPLE_TO_USERSET
}
