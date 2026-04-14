package org.kitona.zus.domain.authorization.evaluation.compiled;

import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;

import java.util.Collections;
import java.util.Set;

/**
 * 已编译关系。
 *
 * @param resourceType 资源类型
 * @param relationName 关系名
 * @param rewriteNode  重写表达式节点
 * @param restrictions 类型限制
 */
public record CompiledRelation(String resourceType, String relationName, RewriteNode rewriteNode,
                               Set<String> restrictions) {

    /**
     * 统一把限制集合收口为只读集合，避免编译产物在运行期被改写。
     */
    public CompiledRelation {
        restrictions = restrictions == null ? Collections.emptySet() : Collections.unmodifiableSet(restrictions);
    }
}
