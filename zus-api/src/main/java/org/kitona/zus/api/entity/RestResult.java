package org.kitona.zus.api.entity;

import lombok.Data;

@Data
public class RestResult<T> {

    /**
     * 状态码，如 200 成功，500 错误
     */
    private int code;
    /**
     * 描述信息
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;


    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(200, null, data);
    }

    public static <T> RestResult<T> error(int code, String message) {
        return new RestResult<>(code, message, null);
    }

    private RestResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
