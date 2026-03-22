package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带更新时间和逻辑删除标记的持久化基类。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTrackableSoftDeletePO extends BaseSoftDeletePO {

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateTime;
}
