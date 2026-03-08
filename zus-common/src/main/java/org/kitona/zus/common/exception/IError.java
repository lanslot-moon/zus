package org.kitona.zus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum IError {

    SYSTEM_ERROR(500, "SYSTEM_ERROR", "系统异常"),

    USER_NOT_EXIST(1001, "USER_NOT_EXIST", "用户不存在"),

    USER_INFO_FIND_ERROR(1002, "user info find has error", "用户信息查询出现异常"),



    DATA_NOT_EXIST(4001, "data not exist", "数据不存在"),

    DATA_CONV_ERROR(4002, "", "数据转化异常"),

    PARAMS_EXIST_ERROR(4003, "", "参数存在异常"),

    DATA_EXIST_ERROR(4004, "data already exist", "数据已存在"),

    DATA_STATUS_ERROR(4005, "data status error", "数据状态异常"),
    ;

    private final Integer code;

    private final String enMsg;

    private final String message;
}
