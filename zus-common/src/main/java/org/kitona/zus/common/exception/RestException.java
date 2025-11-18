package org.kitona.zus.common.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class RestException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -6844221534316308750L;

    private final int code;

    public RestException(String message) {
        super(message);
        this.code = 500;
    }

    public RestException(String message, Integer code) {
        super(message);
        this.code = code;
    }

    public RestException(IError error) {
        super(error.getMessage());
        this.code = error.getCode();
    }
}
