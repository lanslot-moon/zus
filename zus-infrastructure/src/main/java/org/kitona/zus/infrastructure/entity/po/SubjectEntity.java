package org.kitona.zus.infrastructure.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.kitona.zus.infrastructure.entity.BasePo;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName("subject")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectEntity  extends BasePo implements Serializable {

    /**
     * 主体类型 USER/ROLE/GROUP
     */
    private String type;

    /**
     * 名称
     */
    private String name;
}
