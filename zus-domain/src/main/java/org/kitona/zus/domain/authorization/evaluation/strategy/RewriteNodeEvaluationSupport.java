package org.kitona.zus.domain.authorization.evaluation.strategy;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.List;

/**
 * 节点策略回调接口。
 *
 * <p>把递归求值和 tuple 链接查询这类“执行器能力”从具体策略中抽离出来，
 * 让策略只关注当前节点的业务语义，而不直接依赖 PermissionEvaluator 的内部实现。
 */
public interface RewriteNodeEvaluationSupport {

    /**
     * 递归求值另一个 relation。
     */
    boolean evaluateRelation(EvaluationRuntime runtime, Subject subject, ObjectRef object, String relation, int depth);

    /**
     * 递归求值任意 rewrite 子节点。
     */
    boolean evaluateNode(EvaluationRuntime runtime, CompiledRelation compiledRelation, RewriteNode node,
                         Subject subject, ObjectRef object, String relation, int depth);

    /**
     * 执行 direct/self 节点判定。
     */
    boolean evaluateSelf(CompiledRelation compiledRelation, Subject subject, ObjectRef object, String relation,
                         EvaluationRuntime runtime);

    /**
     * 查询 tuple-to-userset 所需的中间链接 tuple。
     */
    List<RelationTuple> findTupleLinks(EvaluationRuntime runtime, ObjectRef object, String tupleRelation);
}
