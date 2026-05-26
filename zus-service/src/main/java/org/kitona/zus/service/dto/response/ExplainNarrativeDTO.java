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
 * summary 用于一句话说明结论，keySteps 用于突出关键证据，timeline 用于按 evaluator
 * 执行顺序展示完整调用流转。
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
     * 从完整调用时序中提取出的关键证明步骤。
     */
    private List<ExplainTimelineStepDTO> keySteps;

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
                .keySteps(ExplainNarrativeAssembler.keySteps(root))
                .timeline(ExplainNarrativeAssembler.timeline(root))
                .build();
    }
}
