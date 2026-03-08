package org.kitona.zus.service.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 添加关系定义命令
 *
 * <p>用于向类型定义添加新的关系（如 viewer、editor、owner 等）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddRelationCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    private String storeId;

    /**
     * 模型ID
     */
    private String modelId;

    /**
     * 类型名
     */
    private String type;

    /**
     * 关系名称，如 viewer、editor、owner
     */
    private String relationName;

    /**
     * 重写表达式，如 self、self or owner
     */
    private String rewriteExpression;

    /**
     * 允许的主体类型列表（可选）
     */
    private List<String> allowedTypes;
}
