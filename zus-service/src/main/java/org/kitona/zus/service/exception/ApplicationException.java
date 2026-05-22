package org.kitona.zus.service.exception;

import lombok.Getter;
import org.kitona.zus.common.exception.IError;

import java.io.Serial;

/**
 * 应用层专用异常
 *
 * <p>DDD 规范：应用层抛出应用层异常，由 API 层统一转换为 REST 异常。
 * 解耦应用层与 API 层的异常处理。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-27
 */
@Getter
public class ApplicationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;

    /**
     * 创建 ApplicationException 实例。
     *
     * @param message message 参数
     */
    public ApplicationException(String message) {
        super(message);
        this.code = 500;
    }

    /**
     * 创建 ApplicationException 实例。
     *
     * @param message message 参数
     * @param code code 参数
     */
    public ApplicationException(String message, int code) {
        super(message);
        this.code = code;
    }

    /**
     * 创建 ApplicationException 实例。
     *
     * @param error error 参数
     */
    public ApplicationException(IError error) {
        super(error.getMessage());
        this.code = error.getCode();
    }

    /**
     * 创建 ApplicationException 实例。
     *
     * @param error error 参数
     * @param detail detail 参数
     */
    public ApplicationException(IError error, String detail) {
        super(error.getMessage() + ": " + detail);
        this.code = error.getCode();
    }
}
