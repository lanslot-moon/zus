package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationDecision;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Explain 用例编排器。
 *
 * <p>该组件只提供独立调试入口所需的编排逻辑，真正的授权判断仍复用
 * {@link PermissionCheckEvaluator}，避免出现独立 explain 鉴权链路。
 */
@Slf4j
@Component
public class PermissionExplainCoordinator {

    /**
     * 权限评估上下文加载器，用于复用 Store/Model/CompiledModel/Preflight 准备流程。
     */
    @Resource
    private PermissionEvaluationContextLoader evaluationContextLoader;

    /**
     * 单点权限检查器，Explain 入口通过它复用真实 Check 执行内核。
     */
    @Resource
    private PermissionCheckEvaluator permissionCheckEvaluator;

    /**
     * 执行一次可解释的权限检查。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param relation 目标关系
     * @param subject  授权主体
     * @param zookie   一致性 token
     * @param context  条件求值上下文
     * @return Explain 执行结果
     */
    public PermissionExplainOutcome explain(String storeId, ObjectRef object, String relation,
                                            Subject subject, Zookie zookie, Map<String, Object> context) {
        PermissionEvaluationContext evaluationContext = evaluationContextLoader.load(storeId, object, zookie);
        if (evaluationContext.isAbnormal()) {
            return PermissionExplainOutcome.abnormal(evaluationContext.abnormalStatus());
        }

        Long currentedZookie = evaluationContext.storeView().currentZookie();
        if (evaluationContext.isPreflightDenied()) {
            EvaluationTrace trace = buildPreflightDeniedTrace(evaluationContext.preflightFailureReason(), subject, object, relation, zookie, currentedZookie);
            return PermissionExplainOutcome.denied(trace);
        }

        EvaluationRequest request = EvaluationRequest.of(storeId, subject, object, relation, zookie, context);
        EvaluationDecision decision = permissionCheckEvaluator.checkWithExplain(evaluationContext.compiledModel(), request);
        EvaluationTrace trace = enrichTrace(decision, evaluationContext.compiledModel(), request, currentedZookie);

        log.debug("Explain 权限检查完成: storeId={}, object={}, relation={}, subject={}, allowed={}",
                storeId, object, relation, subject, decision.allowed());
        return decision.allowed() ? PermissionExplainOutcome.allowed(trace) : PermissionExplainOutcome.denied(trace);
    }

    /**
     * 构建前置检查失败时的 Explain Trace。
     *
     * @param reason        前置检查失败原因
     * @param subject       授权主体
     * @param object        授权对象
     * @param relation      目标关系
     * @param requestZookie 请求侧 zookie
     * @param currentZookie Store 当前 zookie
     * @return Explain Trace
     */
    private EvaluationTrace buildPreflightDeniedTrace(EvaluationExplainReason reason, Subject subject,
                                                      ObjectRef object, String relation,
                                                      Zookie requestZookie, Long currentZookie) {
        EvaluationExplainNode root = new EvaluationExplainNode(
                EvaluationNodeType.RELATION,
                object + "#" + relation,
                subject.toString(),
                relation,
                false,
                reason,
                null,
                null,
                List.of()
        );
        return new EvaluationTrace(false, requestZookie.toToken(), Zookie.of(currentZookie).toToken(),
                StaleSnapshotDiagnosis.NOT_APPLICABLE, root, false);
    }

    /**
     * 补充当前 zookie 与旧快照诊断信息。
     *
     * @param decision      评估决策对象，包含原始 trace
     * @param compiledModel 已编译的授权模型
     * @param request       评估请求对象
     * @param currentZookie 当前 store 最新 zookie
     * @return 包含当前 zookie 和旧快照诊断的 trace
     */
    private EvaluationTrace enrichTrace(EvaluationDecision decision, CompiledAuthorizationModel compiledModel,
                                        EvaluationRequest request, Long currentZookie) {
        String currentToken = Zookie.of(currentZookie).toToken();
        EvaluationTrace trace = decision.trace().withCurrentZookie(currentToken);
        StaleSnapshotDiagnosis diagnosis = diagnoseStaleSnapshot(decision, compiledModel, request, currentZookie);
        return trace.withStaleSnapshotDiagnosis(diagnosis);
    }

    /**
     * 诊断旧快照是否导致本次拒绝。
     *
     * <p>只有请求携带的 zookie 小于当前 zookie 且本次结果为拒绝时，
     * 才使用当前最新 zookie 额外执行一次普通 Check 来判断是否由旧快照导致。
     *
     * @param decision      评估决策对象
     * @param compiledModel 已编译的授权模型
     * @param request       评估请求对象
     * @param currentZookie 当前 store 最新 zookie
     * @return 旧快照诊断结果
     */
    private StaleSnapshotDiagnosis diagnoseStaleSnapshot(EvaluationDecision decision,
                                                         CompiledAuthorizationModel compiledModel,
                                                         EvaluationRequest request,
                                                         Long currentZookie) {
        if (decision.allowed() || request.zookie().isEmpty() || currentZookie == null) {
            return StaleSnapshotDiagnosis.NOT_APPLICABLE;
        }
        Long requestedVersion = request.zookie().getVersion();
        if (requestedVersion == null || requestedVersion >= currentZookie) {
            return StaleSnapshotDiagnosis.NOT_APPLICABLE;
        }
        boolean latestAllowed = permissionCheckEvaluator.check(compiledModel, request.withZookie(Zookie.of(currentZookie)));
        return latestAllowed
                ? StaleSnapshotDiagnosis.STALE_SNAPSHOT_CAUSED
                : StaleSnapshotDiagnosis.STALE_SNAPSHOT_NOT_CAUSED;
    }
}
