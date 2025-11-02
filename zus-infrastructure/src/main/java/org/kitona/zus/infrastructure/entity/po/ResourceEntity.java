package org.kitona.zus.infrastructure.entity.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kitona.zus.infrastructure.entity.BasePo;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName("resource")
@Data
public class ResourceEntity extends BasePo implements Serializable {

    /**
     * 资源类型 FILE/FOLDER/PROJECT 等
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 父资源ID
     */
    private Long parentId;
}
