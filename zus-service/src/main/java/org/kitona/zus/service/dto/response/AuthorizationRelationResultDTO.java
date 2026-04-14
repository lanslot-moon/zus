package org.kitona.zus.service.dto.response;

import lombok.Data;

import java.util.List;

/**
 * 关系定义结果 DTO
 */
@Data
public class AuthorizationRelationResultDTO {

    /**
     * 关系ID
     */
    private Long id;

    /**
     * 类型定义ID
     */
    private Long typeDefinitionId;

    /**
     * 关系名称
     */
    private String relationName;

    /**
     * 重写表达式
     */
    private String rewriteExpression;

    /**
     * 允许的主体类型列表
     */
    private List<String> allowedTypes;
}
