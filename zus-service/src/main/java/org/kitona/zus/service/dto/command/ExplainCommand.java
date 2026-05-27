package org.kitona.zus.service.dto.command;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 权限解释命令。
 *
 * <p>用于独立 Explain API，不进入普通 Check 热路径。
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplainCommand implements Serializable {

    /**
     * 序列化版本号。
     */
    @Serial
    private static final long serialVersionUID = 2304718261723355988L;

    /**
     * Store 标识。
     */
    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    /**
     * 可选授权模型标识；为空时使用 Store 当前激活模型。
     */
    private String authorizationModelId;

    /**
     * 被检查对象的类型。
     */
    @NotBlank(message = "objectType 不能为空")
    private String objectType;

    /**
     * 被检查对象的业务 ID。
     */
    @NotBlank(message = "objectId 不能为空")
    private String objectId;

    /**
     * 需要证明的对象关系。
     */
    @NotBlank(message = "relation 不能为空")
    private String relation;

    /**
     * 请求主体的类型。
     */
    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    /**
     * 请求主体的业务 ID。
     */
    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    /**
     * 请求主体携带的 subject relation，为空表示 direct subject。
     */
    private String subjectRelation;

    /**
     * 请求侧一致性 token。
     */
    private String consistencyToken;

    /**
     * 条件求值所需的动态上下文。
     */
    private Map<String, Object> context;
}
