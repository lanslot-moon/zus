package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 条件定义持久化对象。
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_condition_definition")
@Data
@Accessors(chain = true)
public class ConditionDefinitionPO extends BasePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String storeId;

    private String modelId;

    private String conditionName;

    private String expression;

    private String parameterSchema;

    private String description;
}
