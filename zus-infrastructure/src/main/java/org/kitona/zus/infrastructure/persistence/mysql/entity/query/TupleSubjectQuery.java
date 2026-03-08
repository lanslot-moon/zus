package org.kitona.zus.infrastructure.persistence.mysql.entity.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 按主体查询元组列表参数（超过 5 个参数封装为对象）
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TupleSubjectQuery implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间 ID
     */
    private String storeId;
    /**
     * 主体类型
     */
    private String subjectType;
    /**
     * 主体 ID
     */
    private String subjectId;
    /**
     * 主体关系，非用户集可传 null
     */
    private String subjectRelation;
    /**
     * 资源类型过滤，可选
     */
    private String objectType;
    /**
     * 关系名过滤，可选
     */
    private String relation;
    /**
     * 最大 Zookie，传 null 表示读最新
     */
    private Long maxZookie;
}
