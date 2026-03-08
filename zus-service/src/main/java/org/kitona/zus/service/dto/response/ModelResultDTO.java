package org.kitona.zus.service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.common.utils.JacksonUtil;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 授权模型结果 DTO
 *
 * 对应数据库表 fga_authorization_model
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelResultDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型唯一标识（ULID）
     */
    private String modelId;

    /**
     * Schema 版本，如 1.1
     */
    private String schemaVersion;

    /**
     * 原始 DSL 文本
     */
    private String dslText;

    /**
     * 模型状态: 0-草稿, 1-已发布, 2-已废弃
     */
    private Integer status;

    /**
     * 模型描述
     */
    private String description;

    /**
     * 类型定义列表（内部使用，不参与 JSON 序列化）
     */
    @JsonIgnore
    private transient List<TypeDefinitionResultDTO> typeDefinitions;

    /**
     * 类型定义 JSON，API 层可解析为 List&lt;FgaTypeDefinitionVO&gt;
     * <p>序列化时从 typeDefinitions 自动生成
     */
    public String getTypeDefinitionsJson() {
        if (typeDefinitions == null || typeDefinitions.isEmpty()) {
            return null;
        }
        return JacksonUtil.toJSONString(typeDefinitions);
    }


    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;

    /**
     * 是否为当前生效模型
     * <p>true 表示该模型是 Store 当前使用的模型（currentModelId 指向的模型）
     */
    private Boolean isCurrent;
}
