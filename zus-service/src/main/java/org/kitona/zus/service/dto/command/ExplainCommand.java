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

    @Serial
    private static final long serialVersionUID = 2304718261723355988L;

    @NotBlank(message = "storeId 不能为空")
    private String storeId;

    @NotBlank(message = "objectType 不能为空")
    private String objectType;

    @NotBlank(message = "objectId 不能为空")
    private String objectId;

    @NotBlank(message = "relation 不能为空")
    private String relation;

    @NotBlank(message = "subjectType 不能为空")
    private String subjectType;

    @NotBlank(message = "subjectId 不能为空")
    private String subjectId;

    private String subjectRelation;

    private String consistencyToken;

    private Map<String, Object> context;
}
