package org.kitona.zus.domain.service;

import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledRelation;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationDecision;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.NodeCompletionReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceCollector;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTraceRecorder;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationMemoKey;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRuntime;
import org.kitona.zus.domain.authorization.evaluation.runtime.RecursionGuard;
import org.kitona.zus.domain.authorization.evaluation.specification.RelationRestrictionSpecification;
import org.kitona.zus.domain.authorization.evaluation.runtime.RecursiveEvaluationTemplate;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.specification.SubjectMatchSpecification;
import org.kitona.zus.domain.authorization.evaluation.specification.TupleVisibilityDecision;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationStrategy;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeEvaluationSupport;
import org.kitona.zus.domain.authorization.evaluation.strategy.RewriteNodeStrategyFactory;
import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

import java.util.List;
import java.util.Optional;

/**
 * 单点权限检查领域服务。
 *
 * <p>这是授权域的核心读模型服务，负责把“编译后的模型定义 + 运行时请求 + tuple 数据”
 * 组合成统一的授权判定结果。它不直接保存任何请求态，而是为每一次调用构建独立的
 * {@link EvaluationRuntime}，再交给统一递归模板执行。
 *
 * <p>类本身刻意只保留三类职责：
 * <ul>
 *   <li>对外暴露 {@code check/checkWithExplain} 两个单点证明入口</li>
 *   <li>组织递归执行模板、memoization 和循环检测</li>
 *   <li>把具体 rewrite 节点分发给策略对象处理</li>
 * </ul>
 */
@Slf4j
public final class PermissionCheckEvaluator {

    /**
     * 默认最大递归深度
     */
    private static final int DEFAULT_MAX_DEPTH = 32;

    /**
     * 直接 tuple 读取端口
     */
    private final IDirectTupleReader directTupleReader;

    /**
     * TTU 链接 tuple 读取端口
     */
    private final ITupleLinkReader tupleLinkReader;

    /**
     * 条件求值端口
     */
    private final IConditionEvaluator conditionEvaluator;

    /**
     * 最大递归深度
     */
    private final int maxDepth;

    /**
     * 递归求值模板
     */
    private final RecursiveEvaluationTemplate recursiveEvaluationTemplate;

    /**
     * 节点策略回调接口
     */
    private final RewriteNodeEvaluationSupport evaluationSupport;

    /**
     * Explain 记录适配器，集中处理可选 trace collector 的写入细节。
     */
    private final EvaluationTraceRecorder traceRecorder;

    public PermissionCheckEvaluator(IDirectTupleReader directTupleReader,
                                    ITupleLinkReader tupleLinkReader,
                                    IConditionEvaluator conditionEvaluator) {
        this(directTupleReader, tupleLinkReader, conditionEvaluator, DEFAULT_MAX_DEPTH);
    }

    public PermissionCheckEvaluator(IDirectTupleReader directTupleReader,
                                    ITupleLinkReader tupleLinkReader,
                                    IConditionEvaluator conditionEvaluator,
                                    int maxDepth) {
        this.directTupleReader = directTupleReader;
        this.tupleLinkReader = tupleLinkReader;
        this.conditionEvaluator = conditionEvaluator;
        this.recursiveEvaluationTemplate = new RecursiveEvaluationTemplate();
        this.evaluationSupport = new EvaluatorSupport();
        this.traceRecorder = new EvaluationTraceRecorder();
        this.maxDepth = maxDepth;
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
     * Check explain 语义入口。
     *
     * <p>该入口复用普通 check 的同一套递归与策略逻辑，只是在运行时挂载 collector
     * 记录证明路径，因此不会产生“解释链路”和“真实鉴权链路”语义漂移。
     */
    public EvaluationDecision checkWithExplain(CompiledAuthorizationModel model, EvaluationRequest request) {
        EvaluationTraceCollector collector = EvaluationTraceCollector.enabled(request.zookie().toToken());
        EvaluationRuntime runtime = createRuntime(model, request, new RecursionGuard(maxDepth), collector);
        boolean allowed = evaluateRelation(runtime, request.subject().toSubject(), request.object().toObjectRef(), request.relation(), 0);
        return new EvaluationDecision(allowed, collector.toTrace(allowed));
    }

    /**
     * 统一递归入口。
     *
     * <p>这里负责处理所有节点共享的横切关注点：
     * 递归深度保护、循环检测、单次请求内 memoization，以及关系定义缺失的快速失败。
     * 该方法是权限评估的核心递归入口，处理所有节点共享的横切关注点。
     */
    private boolean evaluateRelation(EvaluationRuntime runtime, Subject subject, ObjectRef object, String relation, int depth) {
        // 创建评估记忆键，用于单次请求内的memoization优化
        EvaluationMemoKey memoKey = EvaluationMemoKey.of(runtime.request(), subject, object, relation);
        // 进入关系评估的跟踪记录
        traceRecorder.enter(runtime, EvaluationNodeType.RELATION, subject, object, relation);
        // 使用递归评估模板执行实际的评估逻辑
        boolean result = recursiveEvaluationTemplate.execute(runtime, memoKey, depth, () -> {
            // 在模型中查找关系定义
            Optional<CompiledRelation> relationOpt = runtime.model().findRelation(object.getType(), relation);
            if (relationOpt.isEmpty()) {
                log.info("PermissionCheckEvaluator evaluateRelation not find relation definition: subject={}, object={}, relation={}", subject, object, relation);
                traceRecorder.mark(runtime, false, BusinessEvidenceReason.NO_RELATION_DEFINITION);
                return false;
            }
            // 获取编译后的关系定义
            CompiledRelation compiledRelation = relationOpt.get();
            // 评估关系节点
            return evaluateNode(runtime, compiledRelation, compiledRelation.rewriteNode(), subject, object, relation, depth);
        });
        // 离开关系评估的跟踪记录，根据结果记录相应的解释原因
        traceRecorder.leave(runtime, result, NodeCompletionReason.resolve(EvaluationNodeType.RELATION, result));
        return result;
    }

    /**
     * 节点分发器。
     *
     * <p>不同 rewrite 节点的求值逻辑通过策略对象分发，避免把所有分支堆在一个超长方法里。
     */
    @SuppressWarnings("all")
    private boolean evaluateNode(EvaluationRuntime runtime, CompiledRelation compiledRelation, RewriteNode node,
                                 Subject subject, ObjectRef object, String relation, int depth) {
        traceRecorder.enter(runtime, node.explainNodeType(), subject, object, relation);
        RewriteNodeEvaluationStrategy<RewriteNode> strategy = (RewriteNodeEvaluationStrategy<RewriteNode>) RewriteNodeStrategyFactory.getStrategy(node.getClass());
        boolean result = strategy.evaluate(node, compiledRelation, runtime, subject, object, relation, depth, evaluationSupport);
        traceRecorder.leave(runtime, result, NodeCompletionReason.resolve(node.explainNodeType(), result));
        return result;
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
                traceRecorder.recordTuple(runtime, tuple, true, BusinessEvidenceReason.DIRECT_TUPLE_MATCHED);
                return true;
            }
        }
        traceRecorder.mark(runtime, false, BusinessEvidenceReason.NO_TUPLE_MATCHED);
        return false;
    }

    /**
     * 判定 direct/self tuple 是否满足当前 subject 命中条件。
     */
    private boolean matchesSelfTuple(CompiledRelation compiledRelation, RelationTuple tuple, Subject subject,
                                     EvaluationRuntime runtime) {
        if (!RelationRestrictionSpecification.isSatisfiedBy(compiledRelation, tuple)) {
            traceRecorder.recordTuple(runtime, tuple, false, BusinessEvidenceReason.RELATION_RESTRICTION_FAILED);
            return false;
        }
        TupleVisibilityDecision visibilityDecision = runtime.visibilitySpecification().evaluate(tuple);
        traceRecorder.recordVisibility(runtime, tuple, visibilityDecision);
        if (visibilityDecision.isNotSatisfied()) {
            return false;
        }
        boolean matched = SubjectMatchSpecification.isSatisfiedBy(tuple, subject);
        if (!matched) {
            traceRecorder.recordTuple(runtime, tuple, false, BusinessEvidenceReason.SUBJECT_NOT_MATCHED);
        }
        return matched;
    }

    /**
     * 为单次授权请求创建运行时上下文。
     */
    private EvaluationRuntime createRuntime(CompiledAuthorizationModel model, EvaluationRequest request, RecursionGuard guard) {
        return new EvaluationRuntime(model, request, guard, System.currentTimeMillis(), conditionEvaluator);
    }

    private EvaluationRuntime createRuntime(CompiledAuthorizationModel model, EvaluationRequest request,
                                            RecursionGuard guard, EvaluationTraceCollector collector) {
        return new EvaluationRuntime(model, request, guard, System.currentTimeMillis(), conditionEvaluator, collector);
    }

    /**
     * 节点策略回调适配器。
     *
     * <p>把 PermissionCheckEvaluator 的递归入口和 tuple 链接查询能力包装成稳定接口，
     * 让外部策略类只依赖抽象协作而不是直接反向依赖整个 evaluator。
     */
    private final class EvaluatorSupport implements RewriteNodeEvaluationSupport {

        @Override
        public boolean evaluateRelation(EvaluationRuntime runtime, Subject subject, ObjectRef object, String relation, int depth) {
            return PermissionCheckEvaluator.this.evaluateRelation(runtime, subject, object, relation, depth);
        }

        @Override
        public boolean evaluateNode(EvaluationRuntime runtime, CompiledRelation compiledRelation, RewriteNode node,
                                    Subject subject, ObjectRef object, String relation, int depth) {
            return PermissionCheckEvaluator.this.evaluateNode(runtime, compiledRelation, node, subject, object, relation, depth);
        }

        @Override
        public boolean evaluateSelf(CompiledRelation compiledRelation, Subject subject, ObjectRef object, String relation,
                                    EvaluationRuntime runtime) {
            return PermissionCheckEvaluator.this.evaluateSelf(compiledRelation, subject, object, relation, runtime);
        }

        @Override
        public List<RelationTuple> findTupleLinks(EvaluationRuntime runtime, ObjectRef object, String tupleRelation) {
            return tupleLinkReader.findTupleLinks(
                    runtime.request().storeId(), object, tupleRelation, runtime.request().zookie().getVersion());
        }
    }
}
