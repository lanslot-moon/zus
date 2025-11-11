package org.kitona.zus.business.entity.graph;

public enum NodeType {
    /**
     * 用途: 表示系统中的资源实体类型，通常作为权限语义图的根节点或分组节点.
     * <p>
     * 必要性: 用于在复杂的授权模型中进行跨类型关系的查找和解析。例如，在解析 tupleToUserset 规则时，需要知道目标对象（例如 document 的父对象 folder）的类型。
     * <p>
     * 示例: folder, document.
     */
    SPECIFIC_TYPE,

    /**
     * 用途: 表示类型内部的具体关系定义节点。它是图模型中主要的依赖和推导逻辑的参与者。
     * <p>
     * 必要性: 用于描述不同 relation 之间的依赖与推导逻辑。例如，viewer 权限可能依赖于 editor 权限。
     * <p>
     * 示例: folder#viewer, document#writer.
     */
    SPECIFIC_TYPE_AND_RELATION,


    /**
     * 用途: 表示权限直接来源于 tupleToUserset 规则，即权限通过元组数据的某个关系进行传递.
     * <p>
     * 必要性: 用于标记那些用户集是通过复杂计算（通过中间对象和目标关系计算）得出的关系。
     * <p>
     * 示例: 在规范的 OpenFGA 图中，这个节点通常只用于标记权限流经 TTU 规则的计算终点，或者简化为自循环关系.
     */
    TUPLE_TO_USER_SET;
}