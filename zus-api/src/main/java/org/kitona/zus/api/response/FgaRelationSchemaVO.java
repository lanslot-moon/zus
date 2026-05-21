package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 关系 Schema VO。
 *
 * <p>关系名称由外层 {@code relations} Map 的 key 表达。
 */
@Data
public class FgaRelationSchemaVO {

    /**
     * 重写表达式。
     */
    private String rewrite;

    /**
     * 关系类型：0-direct_only / 1-computed_userset / 2-ttu / 3-composite。
     */
    private Integer relationType;

    /**
     * 主体类型限制列表。
     */
    private List<FgaTypeRestrictionVO> allowedSubjectTypes;
}
