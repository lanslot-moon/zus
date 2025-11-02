package org.kitona.zus.infrastructure.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.kitona.zus.infrastructure.entity.BasePo;

import java.io.Serializable;

/**
 * 关系元组表
 */
@EqualsAndHashCode(callSuper = true)
@TableName("relation_tuple")
@Data
public class Tuple extends BasePo implements Serializable {

    /**
     * 主体类型
     * User
     */
    private String subjectType;

    /**
     * 主体Id
     * 1000
     */
    private Long subjectId;

    /**
     * 关系主体类型
     * File
     */
    private String resourceType;

    /**
     * 关系主体Id
     * 2294
     */
    private Long resourceId;


    /**
     * 关系,例如 "viewer"
     */
    private String relation;
}
