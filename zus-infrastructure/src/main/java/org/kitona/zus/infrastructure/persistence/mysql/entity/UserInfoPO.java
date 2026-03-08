package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户信息持久化对象
 *
 * <p>系统内部缓存的用户基础信息，可与外部用户中心同步。
 * 表名：user_info（若未建表，可由应用自行执行 DDL 或通过外部适配器提供数据）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@EqualsAndHashCode(callSuper = true)
@TableName("user_info")
@Data
@Accessors(chain = true)
public class UserInfoPO extends BasePoMinimal implements Serializable {

    @Serial
    private static final long serialVersionUID = -8843592676316039867L;

    /** 用户唯一标识（与外部用户中心对应） */
    private String userId;
    /** 用户名称 */
    private String userName;
    /** 密码（加密存储，可选） */
    private String userPassword;
    /** 角色 */
    private String userRole;
    /** 状态 */
    private String userStatus;
    /** 手机号 */
    private String userPhone;
    /** 邮箱 */
    private String userEmail;
    /** 地址 */
    private String userAddress;
    /** 头像 URL */
    private String userAvatar;
    /** 用户创建时间（业务侧字符串或时间戳字符串） */
    private String userCreateTime;
    /** 用户更新时间 */
    private String userUpdateTime;
}
