package org.kitona.zus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IError {

    SYSTEM_ERROR(500, "SYSTEM_ERROR", "系统异常"),

    USER_NOT_EXIST(1001, "USER_NOT_EXIST", "用户不存在"),

    USER_INFO_FIND_ERROR(1002, "user info find has error", "用户信息查询出现异常"),
    ;

    private final Integer code;

    private final String enMsg;

    private final String message;
}
