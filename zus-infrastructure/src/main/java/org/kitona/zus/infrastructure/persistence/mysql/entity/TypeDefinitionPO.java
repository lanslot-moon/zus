package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * FGA 类型定义持久化对象
 *
 * 对应 business.entity.bo.TypeDefinition，一个授权模型下可有多个资源类型。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_type_definition")
@Data
@Accessors(chain = true)
public class TypeDefinitionPO extends BasePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 存储空间ID */
    private String storeId;
    /** 授权模型ID */
    private String modelId;
    /** 资源类型名，如 document、folder、user */
    @TableField("type")
    private String subjectType;
    /** 排序序号 */
    private Integer sortOrder;
}
