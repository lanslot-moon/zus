package org.kitona.zus.service.dto.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 权限检查命令
 *
 * <p>用于 Check API 的请求参数传输。
 * <p>检查 subject 是否对 object 拥有指定的 relation。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckCommand implements Serializable {

    @Serial
    private static final long serialVersionUID = 5068573299682003948L;

    /**
     * 存储空间ID
     */
    private String storeId;

    /**
     * 资源类型，如 document、folder
     */
    private String objectType;

    /**
     * 资源ID
     */
    private String objectId;

    /**
     * 关系名称，如 viewer、editor、owner
     */
    private String relation;

    /**
     * 主体类型，如 user、group
     */
    private String subjectType;

    /**
     * 主体ID
     */
    private String subjectId;

    /**
     * 主体关系（用户集时使用，可选）
     */
    private String subjectRelation;

    /**
     * Zookie 令牌（用于一致性读取，可选）
     * <p>传入后将只查询该版本之前的元组，保证一致性读取
     */
    private String consistencyToken;
}
