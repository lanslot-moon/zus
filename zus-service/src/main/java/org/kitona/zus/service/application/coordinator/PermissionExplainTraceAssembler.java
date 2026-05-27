package org.kitona.zus.service.application.coordinator;

import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;
import org.kitona.zus.domain.authorization.evaluation.explain.StaleSnapshotDiagnosis;
import org.kitona.zus.domain.enums.EvaluationNodeType;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限 Explain Trace 装配器。
 *
 * <p>该组件负责将应用层前置失败上下文转换为领域 explain trace。
 * Coordinator 只消费装配结果，不直接拼装 {@link EvaluationExplainNode}。
 */
@Component
public class PermissionExplainTraceAssembler {

    /**
     * 构建前置失败对应的 Explain Trace。
     *
     * @param context       权限评估上下文
     * @param subject       授权主体
     * @param object        授权对象
     * @param relation      目标关系
     * @param requestZookie 请求侧 zookie
     * @return Explain Trace
     */
    EvaluationTrace preflightDeniedTrace(PermissionEvaluationContext context, Subject subject,
                                         ObjectRef object, String relation, Zookie requestZookie) {
        EvaluationExplainNode root = new EvaluationExplainNode(
                EvaluationNodeType.RELATION,
                object + "#" + relation,
                subject.toString(),
                relation,
                false,
                context.preflightFailureReason(),
                null,
                null,
                List.of()
        );
        return new EvaluationTrace(false, requestZookie.toToken(),
                Zookie.of(context.storeView().currentZookie()).toToken(),
                StaleSnapshotDiagnosis.NOT_APPLICABLE, root, false);
    }
}
