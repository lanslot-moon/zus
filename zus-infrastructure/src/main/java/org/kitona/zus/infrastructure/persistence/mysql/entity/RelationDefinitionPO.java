package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * FGA 关系定义持久化对象
 *
 * 对应 business.entity.bo.RelationDefinition，每个类型下的关系及重写表达式。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_relation_definition")
@Data
@Accessors(chain = true)
public class RelationDefinitionPO extends BaseSoftDeletePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 类型定义ID */
    private Long typeDefinitionId;

    /**
     * 类型名称,冗余存储
     */
    @TableField(exist = false)
    private String subjectType;

    /** 关系名，如 viewer、editor、owner */
    private String relationName;

    /** 重写表达式，如 self、self or owner */
    private String rewriteExpression;
}
