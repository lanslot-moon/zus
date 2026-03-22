package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带创建时间的持久化基类。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseCreateTimePO extends BaseIdPO {

    @TableField(fill = FieldFill.INSERT)
    private Long createTime;
}
