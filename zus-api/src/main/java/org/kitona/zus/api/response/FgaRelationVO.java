package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 关系定义 VO
 */
@Data
public class FgaRelationVO {

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 类型定义ID
     */
    private Long typeDefinitionId;

    /**
     * 关系名称，如 viewer、editor、owner
     */
    private String relationName;

    /**
     * 重写表达式，如 self、self or owner
     */
    private String rewriteExpression;

    /**
     * 允许的主体类型列表
     */
    private List<String> allowedTypes;
}
