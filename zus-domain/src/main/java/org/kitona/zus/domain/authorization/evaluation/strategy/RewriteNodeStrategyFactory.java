package org.kitona.zus.domain.authorization.evaluation.strategy;

import org.kitona.zus.domain.authorization.evaluation.nodes.ComputedUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.DirectRelationReferenceNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.ExclusionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.IntersectionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.SelfNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.TupleToUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.UnionNode;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.ComputedUsersetEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.DirectRelationReferenceEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.ExclusionNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.IntersectionNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.SelfNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.TupleToUsersetEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.UnionNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.impl.UnsupportedNodeEvaluationStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * rewrite 节点策略工厂。
 */
public final class RewriteNodeStrategyFactory {

    private static final Map<Class<? extends RewriteNode>, RewriteNodeEvaluationStrategy<? extends RewriteNode>> STRATEGIES = new HashMap<>();

    private RewriteNodeStrategyFactory() {
    }

    static {
        // 将 自身引用 节点映射到其评估策略
        STRATEGIES.put(SelfNode.class, new SelfNodeEvaluationStrategy());
        // 将 直接关系引用 节点映射到其评估策略
        STRATEGIES.put(DirectRelationReferenceNode.class, new DirectRelationReferenceEvaluationStrategy());
        // 将 跨关系引用 节点映射到其评估策略
        STRATEGIES.put(ComputedUsersetNode.class, new ComputedUsersetEvaluationStrategy());
        // 将 TupleToUsersetNode 节点映射到其评估策略
        STRATEGIES.put(TupleToUsersetNode.class, new TupleToUsersetEvaluationStrategy());
        // 将 并集 节点映射到其评估策略
        STRATEGIES.put(UnionNode.class, new UnionNodeEvaluationStrategy());
        // 将 交集 节点映射到其评估策略
        STRATEGIES.put(IntersectionNode.class, new IntersectionNodeEvaluationStrategy());
        // 将 差集 节点映射到其评估策略
        STRATEGIES.put(ExclusionNode.class, new ExclusionNodeEvaluationStrategy());
    }

    /**
     * 根据节点类型返回对应的求值策略。
     */
    public static RewriteNodeEvaluationStrategy<? extends RewriteNode> getStrategy(Class<? extends RewriteNode> nodeClass) {
        return STRATEGIES.getOrDefault(nodeClass, UnsupportedNodeEvaluationStrategy.INSTANCE);
    }
}
