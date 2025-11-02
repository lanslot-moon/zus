package org.kitona.zus.infrastructure.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kitona.zus.infrastructure.entity.BasePo;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName("relation_definition")
@Data
public class RelationDefinitionEntity extends BasePo implements Serializable {

    /**
     * 资源类型
     */
    private String resourceType;

    /**
     * 关系
     */
    private String relation;

    /**
     * 派生规则，如 "owner", "parent.viewer"
     */
    private String derivedFrom;

    /**
     * 描述
     */
    private String description;
}
