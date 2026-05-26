package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationTrace;

import java.io.Serial;
import java.io.Serializable;

/**
 * Explain 结果总览 DTO。
 *
 * <p>该对象只表达一次 Explain 响应的顶层结构。完整领域证明树放在 root，
 * 面向调试者的摘要、关键步骤和调用时序放在 narrative，避免把多种展示结构
 * 全部堆进同一个 DTO 类。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainResolutionDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 8177550699637708506L;

    /**
     * 本次 explain 对应的最终授权结果。
     */
    private boolean allowed;

    /**
     * 调用方请求中携带的一致性 zookie。
     */
    private String requestedZookie;

    /**
     * Store 当前最新的一致性 zookie。
     */
    private String currentZookie;

    /**
     * 当请求基于旧快照被拒绝时的诊断结果。
     */
    private String staleSnapshotDiagnosis;

    /**
     * explain 树是否因为节点数量上限被截断。
     */
    private boolean truncated;

    /**
     * 面向调试页面展示的叙事化投影。
     */
    private ExplainNarrativeDTO narrative;

    /**
     * 完整机器可读 explain 树根节点。
     */
    private ExplainResolutionNodeDTO root;

    /**
     * 从领域 trace 构造 DTO。
     *
     * @param trace 领域 explain trace
     * @return Explain 响应 DTO
     */
    public static ExplainResolutionDTO from(EvaluationTrace trace) {
        if (trace == null) {
            return null;
        }
        return ExplainResolutionDTO.builder()
                .allowed(trace.allowed())
                .requestedZookie(trace.requestedZookie())
                .currentZookie(trace.currentZookie())
                .staleSnapshotDiagnosis(trace.staleSnapshotDiagnosis().name())
                .truncated(trace.truncated())
                .narrative(ExplainNarrativeDTO.from(trace.root(), trace.allowed()))
                .root(ExplainResolutionNodeDTO.from(trace.root()))
                .build();
    }
}
