package org.kitona.zus.domain.authorization.evaluation.matcher;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceRecorder;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.specification.RelationRestrictionSpecification;
import org.kitona.zus.domain.authorization.evaluation.specification.SubjectMatchSpecification;
import org.kitona.zus.domain.authorization.evaluation.specification.TupleVisibilityDecision;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * direct tuple 证据匹配器。
 *
 * <p>该对象服务于 {@code self} rewrite 节点，但并不表达 self 节点本身。
 * 它只负责从当前 {@code object#relation} 的 direct tuples 中寻找能够证明请求主体的证据，
 * 并在匹配过程中记录 tuple 级 explain 信息。
 */
public final class DirectTupleEvidenceMatcher {

    /**
     * Explain 记录适配器。
     *
     * <p>该 recorder 是无状态对象，普通 check 场景下会因为 runtime 中没有 collector 而自然空转。
     */
    private final EvaluationTraceRecorder traceRecorder;

    /**
     * 创建 direct tuple 证据匹配器。
     */
    public DirectTupleEvidenceMatcher() {
        this.traceRecorder = new EvaluationTraceRecorder();
    }

    /**
     * 判断 direct tuples 是否能够证明当前 subject 对 object#relation 的关系成立。
     *
     * @param compiledRelation 编译后的 relation 定义
     * @param runtime          单次求值运行时
     * @param subject          请求主体
     * @param object           请求客体
     * @param relation         请求关系
     * @param depth            当前递归深度
     * @param support          rewrite 节点求值支持能力
     * @return direct tuple 能够证明关系成立时返回 {@code true}
     */
    public boolean matches(CompiledRelation compiledRelation, EvaluationRuntime runtime, Subject subject,
                           ObjectRef object, String relation, int depth, RewriteNodeEvaluationSupport support) {
        for (RelationTuple tuple : support.findDirectTuples(runtime, object, relation)) {
            if (matchesTuple(compiledRelation, tuple, subject, runtime, depth, support)) {
                return true;
            }
        }
        traceRecorder.mark(runtime, false, BusinessEvidenceReason.NO_TUPLE_MATCHED);
        return false;
    }

    /**
     * 判断单条 tuple 是否可以作为当前授权证明的证据。
     *
     * @param compiledRelation 编译后的 relation 定义
     * @param tuple            待判断的关系事实
     * @param subject          请求主体
     * @param runtime          单次求值运行时
     * @param depth            当前递归深度
     * @param support          rewrite 节点求值支持能力
     * @return 当前 tuple 可以证明授权成立时返回 {@code true}
     */
    private boolean matchesTuple(CompiledRelation compiledRelation, RelationTuple tuple, Subject subject,
                                 EvaluationRuntime runtime, int depth, RewriteNodeEvaluationSupport support) {
        if (!RelationRestrictionSpecification.isSatisfiedBy(compiledRelation, tuple)) {
            traceRecorder.recordTuple(runtime, tuple, false, BusinessEvidenceReason.RELATION_RESTRICTION_FAILED);
            return false;
        }
        TupleVisibilityDecision visibilityDecision = runtime.visibilitySpecification().evaluate(tuple);
        traceRecorder.recordVisibility(runtime, tuple, visibilityDecision);
        if (visibilityDecision.isNotSatisfied()) {
            return false;
        }
        if (SubjectMatchSpecification.isSatisfiedBy(tuple, subject)) {
            traceRecorder.recordTuple(runtime, tuple, true, BusinessEvidenceReason.DIRECT_TUPLE_MATCHED);
            return true;
        }
        if (tuple.hasUsersetSubject() && matchesUsersetSubject(tuple, subject, runtime, depth, support)) {
            traceRecorder.recordTuple(runtime, tuple, true, BusinessEvidenceReason.USERSET_SUBJECT_MATCHED);
            return true;
        }
        traceRecorder.recordTuple(runtime, tuple, false, BusinessEvidenceReason.SUBJECT_NOT_MATCHED);
        return false;
    }

    /**
     * 递归证明请求主体是否属于 tuple subject 指向的 userset。
     *
     * <p>例如 {@code folder:root#viewer@group:platform#member} 需要继续证明
     * 请求主体是否属于 {@code group:platform#member}。
     *
     * @param tuple   带 userset subject 的关系事实
     * @param subject 请求主体
     * @param runtime 单次求值运行时
     * @param depth   当前递归深度
     * @param support rewrite 节点求值支持能力
     * @return 请求主体属于 userset 时返回 {@code true}
     */
    private boolean matchesUsersetSubject(RelationTuple tuple, Subject subject, EvaluationRuntime runtime,
                                          int depth, RewriteNodeEvaluationSupport support) {
        ObjectRef usersetObject = ObjectRef.of(tuple.getSubjectType(), tuple.getSubjectId());
        return support.evaluateRelation(runtime, subject, usersetObject, tuple.getSubjectRelation(), depth + 1);
    }
}
