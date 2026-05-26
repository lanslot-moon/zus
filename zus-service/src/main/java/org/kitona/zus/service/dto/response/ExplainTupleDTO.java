package org.kitona.zus.service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.domain.authorization.evaluation.explain.TupleExplainDetail;

import java.io.Serial;
import java.io.Serializable;

/**
 * Explain 中展示的 tuple 明细 DTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainTupleDTO implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 6359634895431287760L;

    /**
     * tuple 所属对象，格式通常为 type:id。
     */
    private String object;

    /**
     * tuple 绑定的对象关系。
     */
    private String relation;

    /**
     * tuple 指向的主体，可能是 direct subject 或 userset subject。
     */
    private String subject;

    /**
     * tuple 是否为 wildcard 主体。
     */
    private boolean wildcard;

    /**
     * tuple 写入时生成的一致性版本。
     */
    private String zookie;

    /**
     * tuple 过期时间戳，为空表示不过期。
     */
    private Long expiresAt;

    /**
     * tuple 绑定的条件定义 ID。
     */
    private Long conditionDefinitionId;

    /**
     * tuple 绑定的条件名称。
     */
    private String conditionName;

    /**
     * 从领域 tuple explain 明细构造 DTO。
     *
     * @param detail tuple explain 明细
     * @return tuple DTO
     */
    static ExplainTupleDTO from(TupleExplainDetail detail) {
        if (detail == null) {
            return null;
        }
        return ExplainTupleDTO.builder()
                .object(detail.object())
                .relation(detail.relation())
                .subject(detail.subject())
                .wildcard(detail.wildcard())
                .zookie(detail.zookie())
                .expiresAt(detail.expiresAt())
                .conditionDefinitionId(detail.conditionDefinitionId())
                .conditionName(detail.conditionName())
                .build();
    }
}
