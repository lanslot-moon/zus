package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.ConditionExplainDetail;

import java.io.Serial;
import java.io.Serializable;

/**
 * Explain 中展示的条件求值明细 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainConditionDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 6128922511098284153L;

    /**
     * tuple 绑定的条件定义 ID。
     */
    private Long conditionDefinitionId;

    /**
     * tuple 绑定的条件名称。
     */
    private String conditionName;

    /**
     * 条件表达式在本次请求上下文下是否求值通过。
     */
    private boolean passed;

    /**
     * 从领域 condition explain 明细构造 DTO。
     *
     * @param detail condition explain 明细
     * @return condition DTO
     */
    static ExplainConditionDTO from(ConditionExplainDetail detail) {
        if (detail == null) {
            return null;
        }
        return ExplainConditionDTO.builder()
                .conditionDefinitionId(detail.conditionDefinitionId())
                .conditionName(detail.conditionName())
                .passed(detail.passed())
                .build();
    }
}
