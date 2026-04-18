package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 类型定义 VO
 *
 * <p>表示授权模型中的一种资源类型及其关系定义。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
public class FgaTypeDefinitionVO {

    /**
     * 资源类型名，如 document、folder、user
     */
    private String type;

    private List<FgaRelationVO> relations;
}
