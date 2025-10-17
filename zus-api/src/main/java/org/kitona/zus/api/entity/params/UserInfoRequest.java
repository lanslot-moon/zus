package org.kitona.zus.api.entity.params;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.verify.AddGroup;
import org.kitona.zus.api.verify.UpdateGroup;


import java.io.Serial;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户Id不能为空", groups = {UpdateGroup.class})
    private String id;

    @NotBlank(message = "用户名不能为空", groups = {AddGroup.class})
    private String username;

    @NotBlank(message = "用户昵称不能为空", groups = {AddGroup.class})
    private String nickname;

    @NotBlank(message = "邮箱不能为空", groups = {AddGroup.class})
    private String email;

    @NotBlank(message = "头像Url不能为空", groups = {AddGroup.class})
    private String avatarUrl;
}
