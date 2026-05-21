package org.kitona.zus.api.response;

import lombok.Data;

import java.util.Map;

/**
 * FGA 类型 Schema VO。
 *
 * <p>类型名称由外层 {@code types} Map 的 key 表达。
 */
@Data
public class FgaTypeSchemaVO {

    /**
     * 关系定义映射，key 为 relation name。
     */
    private Map<String, FgaRelationSchemaVO> relations;
}
