package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationDecision;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Explain 用例编排器。
 *
 * <p>该组件只提供独立调试入口所需的编排逻辑，真正的授权判断仍复用
 * {@link PermissionEvaluator}，避免出现独立 explain 鉴权链路。
 */
@Slf4j
@Component
public class PermissionExplainCoordinator {

    /**
     * Store 查询仓储，用于加载当前 store 视图、当前模型指针和当前 zookie。
     */
    @Resource
    private IStoreQueryRepository storeQueryRepository;

    /**
     * 授权模型仓储，用于加载 explain 所需的当前激活模型聚合。
     */
    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    /**
     * 编译模型端口，用于把结构化模型聚合编译为 evaluator 可执行模型。
     */
    @Resource
    private ICompiledModelCompiler compiledModelCompiler;

    /**
     * 编译模型缓存，用于复用同一 store/model 下的编译产物。
     */
    @Resource
    private ICompiledModelCache compiledModelCache;

    /**
     * 权限求值领域服务，Explain 入口通过它复用真实 Check 执行内核。
     */
    @Resource
    private PermissionEvaluator permissionEvaluator;

    /**
     * 执行一次可解释的权限检查。
     */
    public PermissionExplainOutcome explain(String storeId, ObjectRef object, String relation,
                                            Subject subject, Zookie zookie, Map<String, Object> context) {
        Optional<StoreView> storeOpt = storeQueryRepository.findViewByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            return PermissionExplainOutcome.storeNotFound();
        }

        StoreView storeView = storeOpt.get();
        if (StringUtils.isBlank(storeView.currentModelId())) {
            return PermissionExplainOutcome.modelNotBound();
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelRepository.findByModelId(storeId, storeView.currentModelId());
        if (modelOpt.isEmpty()) {
            return PermissionExplainOutcome.modelNotFound();
        }

        AuthorizationModelAggregate aggregate = modelOpt.get();
        if (aggregate.getTypeDefinitions().isEmpty()) {
            return PermissionExplainOutcome.modelInvalid();
        }

        CompiledAuthorizationModel compiledModel;
        String currentModelId = storeView.currentModelId();
        try {
            compiledModel = compiledModelCache.get(storeId, currentModelId)
                    .orElseGet(() -> {
                        CompiledAuthorizationModel model = compiledModelCompiler.compile(aggregate);
                        compiledModelCache.put(storeId, currentModelId, model);
                        return model;
                    });
        } catch (RuntimeException ex) {
            log.warn("授权模型结构无效，无法构建鉴权图: storeId={}, modelId={}", storeId, currentModelId, ex);
            return PermissionExplainOutcome.modelInvalid();
        }
        EvaluationRequest request = EvaluationRequest.of(storeId, subject, object, relation, zookie, context);
        EvaluationDecision decision = permissionEvaluator.checkWithExplain(compiledModel, request);
        EvaluationTrace trace = enrichTrace(decision, compiledModel, request, storeView.currentZookie());

        log.debug("Explain 权限检查完成: storeId={}, object={}, relation={}, subject={}, allowed={}",
                storeId, object, relation, subject, decision.allowed());
        return decision.allowed() ? PermissionExplainOutcome.allowed(trace) : PermissionExplainOutcome.denied(trace);
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
        boolean latestAllowed = permissionEvaluator.check(compiledModel, request.withZookie(Zookie.of(currentZookie)));
        return latestAllowed
                ? StaleSnapshotDiagnosis.STALE_SNAPSHOT_CAUSED
                : StaleSnapshotDiagnosis.STALE_SNAPSHOT_NOT_CAUSED;
    }
}
