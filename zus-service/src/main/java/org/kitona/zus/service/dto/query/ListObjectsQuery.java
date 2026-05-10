package org.kitona.zus.service.dto.query;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 列出对象查询
 *
 * <p>用于查询指定主体可访问的对象列表。
 * <p>查询语义：列出 subject 通过 relation 可访问的所有 object。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListObjectsQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    /**
     * 主体类型，如 user、group
     */
    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    /**
     * 主体ID
     */
    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    /**
     * 主体关系（用于 userset，可选）
     */
    private String subjectRelation;

    /**
     * 关系名称，如 viewer、editor
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 资源类型过滤（可选）
     */
    private String objectType;

    /**
     * 授权模型版本 ID。
     *
     * <p>为空时由应用服务使用 store 当前激活模型；非空时固定使用指定模型执行本次查询。
     */
    private String authorizationModelId;

    /**
     * 一致性令牌。
     */
    private String consistencyToken;

    /**
     * 请求上下文。
     */
    private java.util.Map<String, Object> context;
}
