package org.kitona.zus.domain.authorization.evaluation.explain;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;

/**
 * 单次求值过程的 explain 收集器。
 *
 * <p>collector 是运行时对象，不应作为领域服务成员共享。
 * 它只负责把 evaluator 执行过程中的结构化事件组装成树，不参与授权决策。
 *
 * <p>该类的生命周期必须严格绑定一次 {@code checkWithExplain} 调用：
 * evaluator 在进入 relation/rewrite 节点时调用 {@link #enterNode(EvaluationNodeType, Subject, ObjectRef, String)}，
 * 在 tuple、condition、memo、cycle、depth 等关键位置写入原因，最后通过
 * {@link #toTrace(boolean)} 生成不可变的 {@link EvaluationTrace}。普通 Check 热路径不应该创建该对象。
 */
public final class EvaluationTraceCollector {

    /**
     * 单次 explain 默认允许记录的最大节点数，避免异常模型生成过大的响应体。
     */
    private static final int DEFAULT_MAX_NODES = 512;

    /**
     * 请求方携带的一致性令牌，用于在 explain 结果中说明本次判定基于哪个快照。
     */
    private final String requestedZookie;

    /**
     * 单次 explain 最多允许记录的节点数量。
     */
    private final int maxNodes;

    /**
     * explain 节点工厂，负责创建分支节点、tuple 叶子节点和兜底根节点。
     */
    private final EvaluationExplainNodeFactory nodeFactory;

    /**
     * explain 树构建器，维护当前节点栈和父子节点关系。
     */
    private final EvaluationTraceTreeBuilder treeBuilder;

    /**
     * 已记录节点数量，用于限制异常模型或深递归带来的响应膨胀。
     */
    private int recordedNodeCount;

    /**
     * 是否已经触发截断；触发后不再继续写入新节点。
     */
    private boolean truncated;

    /**
     * 创建单次 explain 收集器。
     *
     * @param requestedZookie 请求携带的一致性令牌
     * @param maxNodes        本次最多允许记录的 explain 节点数量
     */
    private EvaluationTraceCollector(String requestedZookie, int maxNodes) {
        this.requestedZookie = requestedZookie;
        this.maxNodes = Math.max(1, maxNodes);
        this.nodeFactory = new EvaluationExplainNodeFactory();
        this.treeBuilder = new EvaluationTraceTreeBuilder(nodeFactory);
    }

    /**
     * 创建启用状态的 collector。
     *
     * @param requestedZookie 请求携带的一致性令牌
     * @return 新的单次请求 collector
     */
    public static EvaluationTraceCollector enabled(String requestedZookie) {
        return new EvaluationTraceCollector(requestedZookie, DEFAULT_MAX_NODES);
    }

    /**
     * 进入一个解释节点。
     *
     * <p>relation 和 rewrite 节点通过 enter/leave 形成树形结构。
     * 如果节点数量已达到上限，本次 enter 会被忽略，并在当前节点标记截断原因。
     *
     * @param nodeType 节点类型
     * @param subject  当前求值主体
     * @param object   当前求值对象
     * @param relation 当前求值关系
     */
    public void enterNode(EvaluationNodeType nodeType, Subject subject, ObjectRef object, String relation) {
        if (cannotRecordMoreNodes()) {
            return;
        }
        treeBuilder.enter(nodeFactory.branch(nodeType, subject, object, relation));
        recordedNodeCount++;
    }

    /**
     * 记录一个 tuple 判断节点。
     *
     * <p>tuple 节点是 explain 树的叶子证据，用于说明 direct tuple、TTU link tuple、
     * subject 不匹配、类型限制失败、过期等事实层原因。
     *
     * @param tuple   参与判断的 tuple
     * @param allowed 该 tuple 是否证明成立
     * @param reason  结构化原因
     */
    public void recordTupleDecision(RelationTuple tuple, boolean allowed, EvaluationExplainReason reason) {
        recordTupleNode(tuple, allowed, reason, null);
    }

    /**
     * 记录一个条件求值节点。
     *
     * <p>条件节点仍以 tuple 叶子节点的形式写入，但会额外附带
     * {@link ConditionExplainDetail}，用于说明该 tuple 绑定的条件是否通过。
     *
     * @param tuple   绑定条件的 tuple
     * @param allowed 条件是否通过
     * @param reason  结构化原因
     */
    public void recordConditionDecision(RelationTuple tuple, boolean allowed, EvaluationExplainReason reason) {
        recordTupleNode(tuple, allowed, reason, ConditionExplainDetail.of(tuple, allowed));
    }

    /**
     * 标记当前节点结果。
     *
     * <p>该方法用于没有单独叶子节点的原因，例如 relation 不存在、memo 命中、
     * 循环检测、深度超限或 trace 截断。
     *
     * @param allowed 当前节点是否成立
     * @param reason  结构化原因
     */
    public void markCurrent(boolean allowed, EvaluationExplainReason reason) {
        treeBuilder.markCurrent(allowed, reason);
    }

    /**
     * 离开当前节点。
     *
     * <p>离开节点时会完成当前节点结果。如果节点此前已经写入更具体的失败或成功原因，
     * tree builder 会保留更具体的原因，避免被通用的 NODE_ALLOWED/NODE_DENIED 覆盖。
     *
     * @param allowed 当前节点最终是否成立
     * @param reason  如果节点尚无更具体原因时使用的默认原因
     */
    public void leaveNode(boolean allowed, EvaluationExplainReason reason) {
        treeBuilder.leave(allowed, reason);
    }

    /**
     * 构造不可变 trace。
     *
     * <p>该方法是 collector 生命周期的收口点。调用后上层应该只使用返回的
     * {@link EvaluationTrace}，不要继续复用 collector 追加节点。
     *
     * @param allowed 最终授权结果
     * @return 不可变 explain trace
     */
    public EvaluationTrace toTrace(boolean allowed) {
        return new EvaluationTrace(allowed, requestedZookie, "", StaleSnapshotDiagnosis.NOT_REQUESTED,
                treeBuilder.buildRoot(allowed), truncated);
    }

    /**
     * 追加 tuple 类叶子节点。
     *
     * <p>tuple 命中、tuple 过滤和 condition 结果最终都会落到该方法。
     * 如果当前没有打开的父节点，说明 evaluator 尚未进入可承载叶子的 relation/rewrite 节点，
     * 此时直接忽略该事件，避免生成悬挂节点。
     *
     * @param tuple     参与判断的 tuple
     * @param allowed   该 tuple 或条件是否通过
     * @param reason    结构化原因
     * @param condition 条件明细；非条件 tuple 节点为空
     */
    private void recordTupleNode(RelationTuple tuple, boolean allowed, EvaluationExplainReason reason,
                                 ConditionExplainDetail condition) {
        if (!treeBuilder.hasOpenParent() || cannotRecordMoreNodes()) {
            return;
        }
        treeBuilder.appendLeaf(nodeFactory.tuple(tuple, allowed, reason, condition));
        recordedNodeCount++;
    }

    /**
     * 判断本次 explain 是否还能继续记录节点。
     *
     * <p>当节点数量达到上限时，只会在当前节点标记一次 {@link  EvaluationExplainReason.BusinessEvidenceReason#TRACE_TRUNCATED}，
     * 后续事件直接忽略，避免异常授权模型生成过大的 resolution 树。
     *
     * @return 如果不能继续记录新节点则返回 {@code true}
     */
    private boolean cannotRecordMoreNodes() {
        if (truncated) {
            return true;
        }
        if (recordedNodeCount < maxNodes) {
            return false;
        }
        truncated = true;
        markCurrent(false, EvaluationExplainReason.BusinessEvidenceReason.TRACE_TRUNCATED);
        return true;
    }
}
