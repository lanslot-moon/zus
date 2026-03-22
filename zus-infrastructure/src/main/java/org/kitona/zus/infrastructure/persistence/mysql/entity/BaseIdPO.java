package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 仅包含主键的持久化基类。
 */
@Data
public class BaseIdPO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
}
