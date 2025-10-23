package org.kitona.zus.infrastructure.entity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("user_info")
public class UserInfoDo extends BaseDo implements Serializable  {

    @Serial
    private static final long serialVersionUID = 2341386199742049283L;

    private Long id;
}
