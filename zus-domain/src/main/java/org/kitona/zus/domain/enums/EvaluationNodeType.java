package org.kitona.zus.domain.enums;

/**
 * explain 树节点类型。
 *
 * <p>该枚举定义评估解释树中的节点类别，用于区分 relation 入口、
 * rewrite 表达式节点、tuple 叶子节点以及未知节点兜底。
 */
public enum EvaluationNodeType {
    /**
     * relation 求值入口节点，对应一次 {@code object#relation} 证明。
     */
    RELATION,

    /**
     * self rewrite 节点，对应当前对象当前关系上的 direct tuple 匹配。
     */
    SELF,

    /**
     * union rewrite 节点，任一子节点成立即可成立。
     */
    UNION,

    /**
     * intersection rewrite 节点，所有子节点成立才成立。
     */
    INTERSECTION,

    /**
     * exclusion rewrite 节点，左侧成立且右侧不成立才成立。
     */
    EXCLUSION,

    /**
     * computed userset 节点，把当前对象投影到另一个 relation。
     */
    COMPUTED_USERSET,

    /**
     * direct relation reference 节点，引用同一对象上的另一个 relation。
     */
    DIRECT_RELATION_REFERENCE,

    /**
     * tuple-to-userset 节点，通过中间 tuple 传播到另一个对象 relation。
     */
    TUPLE_TO_USERSET,

    /**
     * tuple 判断叶子节点，记录具体 tuple 的命中或失败原因。
     */
    TUPLE,

    /**
     * 未支持的 rewrite 节点兜底类型。
     */
    UNSUPPORTED
}
