package org.kitona.zus.infrastructure.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kitona.zus.infrastructure.entity.BasePo;

import java.io.Serializable;

/**
 * 权限映射表
 */
@EqualsAndHashCode(callSuper = true)
@TableName("permission_mapping")
@Data
public class RelationEntity extends BasePo implements Serializable {
    /**
     * 资源类型
     * file/file
     */
    private String resourceType;

    /**
     * 关系名称
     * viewer/editor
     */
    private String relation;

    /**
     * 动作
     * read/write
     */
    private String action;
}
