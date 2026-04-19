package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 关系定义 VO —— 对应数据库表 {@code fga_relation_definition}
 */
@Data
public class FgaRelationVO {

    /**
     * 关系名称（如 viewer / editor / owner）
     */
    private String name;

    /**
     * 重写表达式
     */
    private String rewriteExpression;

    /**
     * 关系类型：0-direct_only / 1-computed_userset / 2-ttu / 3-composite
     */
    private Integer relationType;

    /**
     * 主体类型限制列表
     */
    private List<FgaTypeRestrictionVO> restrictions;
}
