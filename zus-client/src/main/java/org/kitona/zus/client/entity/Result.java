package org.kitona.zus.client.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.kitona.zus.common.exception.IError;

import java.io.Serializable;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Result<T> implements Serializable {

    private transient T data;

    private Integer code;

    private String message;

    private boolean success;

    public static <T> Result<T> success(T data) {
        return new Result<>(data, 200, "success", true);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(null, code, message, false);
    }

    public static <T> Result<T> error(IError error) {
        return new Result<>(null, error.getCode(), error.getMessage(), false);
    }
}
