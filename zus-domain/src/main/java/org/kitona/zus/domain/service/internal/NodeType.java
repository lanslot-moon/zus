package org.kitona.zus.domain.service.internal;

/**
 * 图节点类型
 * <p>
 * 用于区分授权模型图中不同类型的节点。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public enum NodeType {
    /**
     * 资源实体类型节点，例如 folder、document
     * <p>
     * 用于权限语义图的根或分组
     */
    SPECIFIC_TYPE,

    /**
     * 类型+关系节点，例如 folder#viewer、document#writer
     * <p>
     * 表示依赖与推导逻辑
     */
    SPECIFIC_TYPE_AND_RELATION,
}
