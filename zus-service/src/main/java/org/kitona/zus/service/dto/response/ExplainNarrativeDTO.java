package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.EvaluationExplainNode;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * Explain 面向调试者的叙事投影 DTO。
 *
 * <p>该对象不是领域证明事实，而是从 {@link EvaluationExplainNode} 派生出来的展示结构。
 * summary 用于一句话说明结论，timeline 用于按 evaluator 执行顺序展示完整调用流转。
 * 关键步骤由 timeline 中每个步骤的 keyStep 标记表达，避免额外返回重复列表。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainNarrativeDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 5025267322920512523L;

    /**
     * 面向调试者的一句话解释摘要。
     */
    private String summary;

    /**
     * evaluator 执行过程的前序时序展开。
     */
    private List<ExplainTimelineStepDTO> timeline;

    /**
     * 从领域 explain 树生成叙事投影。
     *
     * @param root    explain 根节点
     * @param allowed 最终授权结果
     * @return 叙事投影 DTO
     */
    static ExplainNarrativeDTO from(EvaluationExplainNode root, boolean allowed) {
        return ExplainNarrativeDTO.builder()
                .summary(ExplainNarrativeAssembler.summarize(root, allowed))
                .timeline(ExplainNarrativeAssembler.timeline(root))
                .build();
    }
}
