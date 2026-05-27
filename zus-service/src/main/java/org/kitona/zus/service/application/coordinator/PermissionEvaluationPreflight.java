package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainReason.BusinessEvidenceReason;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Zookie;
import org.springframework.stereotype.Component;

/**
 * 权限评估前置检查组件。
 *
 * <p>该组件属于应用编排层，负责在进入 evaluator 前做低成本快速失败。
 * 它不参与权限证明算法，也不检查模型 type/relation 是否存在，只判断目标 object
 * 在授权事实层是否出现过。模型语义问题必须交给 evaluator 解释，避免 preflight
 * 抢占领域执行链路并造成 Check/Explain 语义漂移。在 ZUS 语义中，授权事实层出现过即可视为资源存在；
 * 业务删除资源后未清理 tuple 属于接入方的数据治理问题。
 */
@Component
public class PermissionEvaluationPreflight {

    /**
     * tuple 读侧查询端口，用于判断目标对象是否已经出现在授权事实层。
     */
    @Resource
    private ITupleQueryPort tupleQueryPort;

    /**
     * 执行权限评估前置检查。
     *
     * @param storeId  Store 标识
     * @param object   授权对象
     * @param zookie   一致性 token
     * @return 前置检查失败原因；返回 {@code null} 表示通过
     */
    EvaluationExplainReason inspect(String storeId, ObjectRef object, Zookie zookie) {
        if (!tupleQueryPort.existsObjectFact(storeId, object, zookie.getVersion())) {
            return BusinessEvidenceReason.OBJECT_FACT_NOT_FOUND;
        }
        return null;
    }
}
