package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Explain 调用时序步骤 DTO。
 *
 * <p>该结构从 explain 树前序展开而来，便于调用方直接按执行顺序展示 evaluator 的流转。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainTimelineStepDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 2730361018625114575L;

    /**
     * 调用时序中的步骤序号。
     */
    private int step;

    /**
     * 当前步骤在 explain 树中的深度。
     */
    private int depth;

    /**
     * 当前步骤对应的节点类型编码。
     */
    private String nodeType;

    /**
     * 当前步骤对应的求值目标。
     */
    private String target;

    /**
     * 当前步骤正在证明的主体。
     */
    private String subject;

    /**
     * 当前步骤正在证明的关系。
     */
    private String relation;

    /**
     * 当前步骤的展示状态。
     */
    private String status;

    /**
     * 当前步骤是否证明成立。
     */
    private boolean allowed;

    /**
     * 当前步骤的结构化原因编码。
     */
    private String reason;

    /**
     * 当前步骤面向调试者的解释文案。
     */
    private String message;

    /**
     * 当前步骤命中的 tuple 摘要。
     */
    private String tuple;

    /**
     * 当前步骤是否属于关键步骤。
     */
    private boolean keyStep;
}
