package org.kitona.zus.service.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 类型定义结果 DTO
 */
@Data
public class AuthorizationTypeDefinitionResultDTO {

    /**
     * 类型定义ID
     */
    private Long id;

    /**
     * 存储空间ID
     */
    private String storeId;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 资源类型名
     */
    private String type;

    /**
     * 关系定义映射，key 为 relation 名称。
     */
    private Map<String, RelationDefinitionResultDTO> relations;

    /**
     * 关系定义结果 DTO。
     */
    @Data
    public static class RelationDefinitionResultDTO {

        /**
         * rewrite 表达式。
         */
        private String rewriteExpression;

        /**
         * 允许的 subject 类型限制。
         */
        private List<String> restrictions;
    }
}
