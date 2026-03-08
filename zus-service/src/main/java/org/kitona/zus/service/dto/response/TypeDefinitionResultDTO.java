package org.kitona.zus.service.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 类型定义结果 DTO
 */
@Data
public class TypeDefinitionResultDTO {

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
     * 关系名到重写表达式的映射
     */
    private Map<String, String> relations;

    /**
     * 关系的类型限制
     */
    private Map<String, List<String>> relationRestrictions;
}
