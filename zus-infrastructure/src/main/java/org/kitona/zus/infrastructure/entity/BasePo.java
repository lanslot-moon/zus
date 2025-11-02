package org.kitona.zus.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * Title: BaseDo
 * Email: wangli.liu@kitona.org
 * Author  Kitona
 * Date  2025/10/22 17:58
 * Description: xxx
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class BasePo {

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
     * 是否被删除 0-未删除 1-已删除
     */
    private Boolean isDeleted;

    /**
     * 租户Id
     */
    private String tenantId;
}
