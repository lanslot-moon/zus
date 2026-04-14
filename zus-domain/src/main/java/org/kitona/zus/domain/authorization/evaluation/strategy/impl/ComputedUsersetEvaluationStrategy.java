package org.kitona.zus.domain.authorization.evaluation.strategy.impl;

import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.nodes.ComputedUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * computed userset 节点策略。
 *
 * <p>把当前对象投影到另一个 relation 上，再递归复用同一套 evaluator 继续判断。
 */
public final class ComputedUsersetEvaluationStrategy implements RewriteNodeEvaluationStrategy<ComputedUsersetNode> {

    /**
     * 执行 computed userset 递归。
     * 该方法用于评估计算用户集节点，通过递归方式确定主体在目标对象上的关系。
     *
     * @param node             计算用户集节点，包含资源类型和关系名称等信息
     * @param compiledRelation 已编译的关系对象，用于关系评估
     * @param runtime          评估运行时环境，提供必要的评估上下文
     * @param subject          评估的主体，即被评估的用户或组
     * @param object           评估的目标对象，包含对象类型和ID
     * @param relation         当前评估的关系名称
     * @param depth            当前递归深度，用于防止无限递归
     * @param support          提供重写节点评估支持的服务接口
     * @return 返回评估结果，表示主体是否具有对目标对象的关系
     */
    @Override
    public boolean evaluate(ComputedUsersetNode node,
                            CompiledRelation compiledRelation,
                            EvaluationRuntime runtime,
                            Subject subject,
                            ObjectRef object,
                            String relation,
                            int depth,
                            RewriteNodeEvaluationSupport support) {
        // 确定目标类型，如果节点中未指定资源类型，则使用传入对象的类型
        String targetType = StringUtils.defaultIfBlank(node.resourceType(), object.getType());
        // 递归评估关系，深度加1，继续查找更深层次的关系
        return support.evaluateRelation(runtime, subject, ObjectRef.of(targetType, object.getId()),
                node.relationName(), depth + 1);
    }
}
