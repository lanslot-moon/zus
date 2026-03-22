package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * FGA 关系类型限制持久化对象
 *
 * 每个关系可允许多个主体类型，一对多。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("fga_relation_restriction")
@Data
@Accessors(chain = true)
public class RelationRestrictionPO extends BaseSoftDeletePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 关系定义ID（fga_model_relation.id） */
    private Long relationDefinitionId;

    /** 允许的主体类型，如 user、folder */
    private String allowedType;

    /** 允许的主体关系，如 group#member 中的 member */
    private String allowedSubjectRelation;
}
