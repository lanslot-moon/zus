package org.kitona.zus.common.exception;

import lombok.Getter;

import java.io.Serial;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/3/21 11:59
 * Version: V1.0
 * Description: Xxxx
 */
@Getter
public class SystemException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6844221534316308750L;

    private final int code;


    public SystemException(String message, int code) {
        super(message);
        this.code = code;
    }

    public SystemException(IError error) {
        super(error.getMessage());
        this.code = error.getCode();
    }

    public SystemException(String message) {
        super(message);
        this.code = 500;
    }
}
