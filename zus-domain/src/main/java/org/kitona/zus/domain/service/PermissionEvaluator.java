package org.kitona.zus.domain.service;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationMemoKey;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.runtime.RecursionGuard;
import org.kitona.zus.domain.authorization.evaluation.specification.RelationRestrictionSpecification;
import org.kitona.zus.domain.authorization.evaluation.runtime.RecursiveEvaluationTemplate;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.specification.SubjectMatchSpecification;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeStrategyFactory;
import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 权限求值领域服务。
 *
 * <p>这是授权域的核心读模型服务，负责把“编译后的模型定义 + 运行时请求 + tuple 数据”
 * 组合成统一的授权判定结果。它不直接保存任何请求态，而是为每一次调用构建独立的
 * {@link EvaluationRuntime}，再交给统一递归模板执行。
 *
 * <p>类本身刻意只保留三类职责：
 * <ul>
 *   <li>对外暴露 {@code check/listObjects/listUsers} 三个语义入口</li>
 *   <li>组织递归执行模板、memoization 和循环检测</li>
 *   <li>把具体 rewrite 节点分发给策略对象处理</li>
 * </ul>
 */
public final class PermissionEvaluator {

    private static final int DEFAULT_MAX_DEPTH = 32;

    /**
     * 主体匹配规则
     */
    private static final SubjectMatchSpecification SUBJECT_MATCH_SPECIFICATION = new SubjectMatchSpecification();

    /**
     * 关系类型限制规则关系类型限制规则
     */
    private static final RelationRestrictionSpecification RELATION_RESTRICTION_SPECIFICATION = new RelationRestrictionSpecification();

    /**
     * 直接 tuple 读取端口
     */
    private final IDirectTupleReader directTupleReader;

    /**
     * TTU 链接 tuple 读取端口
     */
    private final ITupleLinkReader tupleLinkReader;

    /**
     * 从 subject 反查 object 候选端口
     */
    private final ISubjectObjectCandidateReader subjectObjectCandidateReader;

    /**
     * 从 object 反查 subject 候选端口。
     */
    private final IObjectSubjectCandidateReader objectSubjectCandidateReader;

    /**
     * 条件求值端口
     */
    private final IConditionEvaluator conditionEvaluator;

    private final int maxDepth;
    /**
     * 递归求值模板
     */
    private final RecursiveEvaluationTemplate recursiveEvaluationTemplate;

    /**
     * 节点策略回调接口
     */
    private final RewriteNodeEvaluationSupport evaluationSupport;

    public PermissionEvaluator(IDirectTupleReader directTupleReader,
                               ITupleLinkReader tupleLinkReader,
                               ISubjectObjectCandidateReader subjectObjectCandidateReader,
                               IObjectSubjectCandidateReader objectSubjectCandidateReader,
                               IConditionEvaluator conditionEvaluator) {
        this(directTupleReader, tupleLinkReader, subjectObjectCandidateReader, objectSubjectCandidateReader,
                conditionEvaluator, DEFAULT_MAX_DEPTH);
    }

    public PermissionEvaluator(IDirectTupleReader directTupleReader,
                               ITupleLinkReader tupleLinkReader,
                               ISubjectObjectCandidateReader subjectObjectCandidateReader,
                               IObjectSubjectCandidateReader objectSubjectCandidateReader,
                               IConditionEvaluator conditionEvaluator,
                               int maxDepth) {
        this.directTupleReader = directTupleReader;
        this.tupleLinkReader = tupleLinkReader;
        this.subjectObjectCandidateReader = subjectObjectCandidateReader;
        this.objectSubjectCandidateReader = objectSubjectCandidateReader;
        this.conditionEvaluator = conditionEvaluator;
        this.maxDepth = maxDepth;
        this.recursiveEvaluationTemplate = new RecursiveEvaluationTemplate();
        this.evaluationSupport = new EvaluatorSupport();
    }

    /**
     * Check 语义入口。
     *
     * <p>给定 subject / object / relation，返回“是否能够证明权限成立”。
     * 该入口不会直接做节点分支判断，而是构建单次请求运行时后交给统一递归模板。
     */
    public boolean check(CompiledAuthorizationModel model, EvaluationRequest request) {
        EvaluationRuntime runtime = createRuntime(model, request, new RecursionGuard(maxDepth));
        return evaluateRelation(runtime, request.subject().toSubject(), request.object().toObjectRef(), request.relation(), 0);
    }

    /**
     * ListObjects 语义入口。
     *
     * <p>先从读侧端口获取候选对象，再复用与 Check 完全一致的 evaluator 内核逐个验证。
     * 这样可以确保 ListObjects 和 Check 的授权语义始终一致。
     */
    public List<String> listObjects(CompiledAuthorizationModel model, EvaluationRequest request, String objectType) {
        Set<String> objects = new LinkedHashSet<>();
        EvaluationRuntime runtime = createRuntime(model, request, new RecursionGuard(maxDepth));
        for (ObjectRef candidate : collectObjectCandidates(request, objectType)) {
            EvaluationRuntime candidateRuntime = runtime.withRequest(request.withObject(candidate), conditionEvaluator);
            if (evaluateRelation(candidateRuntime, request.subject().toSubject(), candidate, request.relation(), 0)) {
                objects.add(candidate.toString());
            }
        }
        return List.copyOf(objects);
    }

    /**
     * ListUsers 语义入口。
     *
     * <p>和 ListObjects 一样先做候选裁剪，再对每个候选 subject 调用统一 evaluator。
     * 这里故意不走单独实现，避免读侧接口和 Check 出现语义漂移。
     */
    public List<Subject> listUsers(CompiledAuthorizationModel model, EvaluationRequest request) {
        List<Subject> result = new ArrayList<>();
        for (Subject subject : collectSubjectCandidates(request)) {
            EvaluationRuntime runtime = createRuntime(model, request.withSubject(subject), new RecursionGuard(maxDepth));
            if (evaluateRelation(runtime, subject, request.object().toObjectRef(), request.relation(), 0)) {
                result.add(subject);
            }
        }
        return result;
    }

    /**
     * 统一递归入口。
     *
     * <p>这里负责处理所有节点共享的横切关注点：
     * 递归深度保护、循环检测、单次请求内 memoization，以及关系定义缺失的快速失败。
     */
    private boolean evaluateRelation(EvaluationRuntime runtime, Subject subject, ObjectRef object, String relation, int depth) {
        EvaluationMemoKey memoKey = EvaluationMemoKey.of(runtime.request(), subject, object, relation);
        return recursiveEvaluationTemplate.execute(runtime, memoKey, depth, () -> {
            Optional<CompiledRelation> relationOpt = runtime.model().findRelation(object.getType(), relation);
            return relationOpt.filter(compiledRelation -> evaluateNode(runtime, compiledRelation, compiledRelation.rewriteNode(), subject, object, relation, depth)).isPresent();
        });
    }

    /**
     * 节点分发器。
     *
     * <p>不同 rewrite 节点的求值逻辑通过策略对象分发，避免把所有分支堆在一个超长方法里。
     */
    private boolean evaluateNode(EvaluationRuntime runtime, CompiledRelation compiledRelation, RewriteNode node,
                                 Subject subject, ObjectRef object, String relation, int depth) {
        RewriteNodeEvaluationStrategy<RewriteNode> strategy =
                castStrategy(RewriteNodeStrategyFactory.getStrategy(node.getClass()));
        return strategy.evaluate(node, compiledRelation, runtime, subject, object, relation, depth, evaluationSupport);
    }

    /**
     * direct/self 节点求值。
     *
     * <p>这一步只处理“当前 object 的当前 relation 是否直接命中 subject”。
     * tuple 的类型限制、时间窗口、条件表达式和 subject 匹配都在这里完成。
     */
    private boolean evaluateSelf(CompiledRelation compiledRelation, Subject subject, ObjectRef object, String relation,
                                 EvaluationRuntime runtime) {
        List<RelationTuple> tuples = directTupleReader.findDirectTuples(
                runtime.request().storeId(), object, relation, runtime.request().zookie().getVersion());

        for (RelationTuple tuple : tuples) {
            if (matchesSelfTuple(compiledRelation, tuple, subject, runtime)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判定 direct/self tuple 是否满足当前 subject 命中条件。
     */
    private boolean matchesSelfTuple(CompiledRelation compiledRelation, RelationTuple tuple, Subject subject,
                                     EvaluationRuntime runtime) {
        if (!RELATION_RESTRICTION_SPECIFICATION.isSatisfiedBy(compiledRelation, tuple)) {
            return false;
        }
        if (runtime.visibilitySpecification().isNotSatisfiedBy(tuple)) {
            return false;
        }
        return SUBJECT_MATCH_SPECIFICATION.isSatisfiedBy(tuple, subject);
    }

    /**
     * 为单次授权请求创建运行时上下文。
     */
    private EvaluationRuntime createRuntime(CompiledAuthorizationModel model, EvaluationRequest request, RecursionGuard guard) {
        return new EvaluationRuntime(model, request, guard, System.currentTimeMillis(), conditionEvaluator);
    }

    /**
     * 从读侧端口获取对象候选集。
     *
     * <p>这里只负责缩小搜索空间，不直接代表最终权限结果。
     */
    private List<ObjectRef> collectObjectCandidates(EvaluationRequest request, String objectType) {
        return subjectObjectCandidateReader.listObjectCandidates(
                        request.storeId(), objectType, request.zookie().getVersion())
                .stream()
                .map(tuple -> ObjectRef.of(tuple.getObjectType(), tuple.getObjectId()))
                .distinct()
                .toList();
    }

    /**
     * 从读侧端口获取主体候选集。
     */
    private List<Subject> collectSubjectCandidates(EvaluationRequest request) {
        return objectSubjectCandidateReader.listSubjectCandidates(request.storeId(), request.zookie().getVersion())
                .stream()
                .map(RelationTuple::getSubject)
                .distinct()
                .toList();
    }

    @SuppressWarnings("unchecked")
    private static RewriteNodeEvaluationStrategy<RewriteNode> castStrategy(
            RewriteNodeEvaluationStrategy<? extends RewriteNode> strategy) {
        return (RewriteNodeEvaluationStrategy<RewriteNode>) strategy;
    }

    /**
     * 节点策略回调适配器。
     *
     * <p>把 PermissionEvaluator 的递归入口和 tuple 链接查询能力包装成稳定接口，
     * 让外部策略类只依赖抽象协作而不是直接反向依赖整个 evaluator。
     */
    private final class EvaluatorSupport implements RewriteNodeEvaluationSupport {

        @Override
        public boolean evaluateRelation(EvaluationRuntime runtime, Subject subject, ObjectRef object, String relation, int depth) {
            return PermissionEvaluator.this.evaluateRelation(runtime, subject, object, relation, depth);
        }

        @Override
        public boolean evaluateNode(EvaluationRuntime runtime, CompiledRelation compiledRelation, RewriteNode node,
                                    Subject subject, ObjectRef object, String relation, int depth) {
            return PermissionEvaluator.this.evaluateNode(runtime, compiledRelation, node, subject, object, relation, depth);
        }

        @Override
        public boolean evaluateSelf(CompiledRelation compiledRelation, Subject subject, ObjectRef object, String relation,
                                    EvaluationRuntime runtime) {
            return PermissionEvaluator.this.evaluateSelf(compiledRelation, subject, object, relation, runtime);
        }

        @Override
        public List<RelationTuple> findTupleLinks(EvaluationRuntime runtime, ObjectRef object, String tupleRelation) {
            return tupleLinkReader.findTupleLinks(
                    runtime.request().storeId(), object, tupleRelation, runtime.request().zookie().getVersion());
        }
    }
}
