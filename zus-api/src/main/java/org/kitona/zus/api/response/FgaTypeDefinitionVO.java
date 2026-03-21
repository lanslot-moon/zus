package org.kitona.zus.api.response;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.kitona.zus.service.dto.response.TypeDefinitionResultDTO;

import java.util.List;
import java.util.Map;

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
@AutoMapper(target = TypeDefinitionResultDTO.class)
public class FgaTypeDefinitionVO {

    /**
     * 资源类型名，如 document、folder、user
     */
    private String type;

    /**
     * 关系名到重写表达式的映射。
     * 键为关系名（如 viewer、editor、owner），值为表达式字符串（如 self、self or owner）。
     */
    private Map<String, String> relations;

    /**
     * 关系的类型限制（可选）。
     * 键为关系名，值为允许的主体类型列表，如 parentFolder -> ["folder"]。
     */
    private Map<String, List<String>> relationRestrictions;
}
