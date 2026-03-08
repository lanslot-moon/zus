package org.kitona.zus.infrastructure.persistence.mysql.entity.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 元组键查询参数（按完整元组键查单条，超过 5 个参数封装为对象）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleKeyQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间 ID
     */
    private String storeId;
    /**
     * 资源类型
     */
    private String objectType;
    /**
     * 资源 ID
     */
    private String objectId;
    /**
     * 关系名
     */
    private String relation;
    /**
     * 主体类型
     */
    private String subjectType;
    /**
     * 主体 ID
     */
    private String subjectId;
    /**
     * 主体关系，非用户集可传 null 或空串
     */
    private String subjectRelation;
}
