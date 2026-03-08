package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 最小化持久化基类（仅 id、create_time、update_time、is_deleted）
 * <p>
 * 用于 fga_type_definition、fga_model_relation、fga_relation_restriction 等
 * 无 created_by、updated_by、tenant_id 的表，与 table_schema 严格对齐。
 * </p>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePoMinimal {

    /**
     * 默认使用雪花算法进行插入
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String createdBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updatedBy;

    /**
     * 是否删除：0-否，1-是（与表 is_deleted TINYINT(1) 对应）
     */
    private Boolean isDeleted;

    /**
     * 租户ID（可选）
     * 用于多租户隔离
     */
    private String tenantId;
}
