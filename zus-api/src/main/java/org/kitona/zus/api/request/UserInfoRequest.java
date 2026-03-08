package org.kitona.zus.api.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.api.verify.AddGroup;
import org.kitona.zus.api.verify.UpdateGroup;


import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息 API 请求
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserInfoRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID（更新时必填）
     */
    @NotBlank(message = "用户Id不能为空", groups = {UpdateGroup.class})
    private String id;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = {AddGroup.class})
    private String username;

    /**
     * 用户昵称
     */
    @NotBlank(message = "用户昵称不能为空", groups = {AddGroup.class})
    private String nickname;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空", groups = {AddGroup.class})
    private String email;

    /**
     * 头像 URL
     */
    @NotBlank(message = "头像Url不能为空", groups = {AddGroup.class})
    private String avatarUrl;
}
