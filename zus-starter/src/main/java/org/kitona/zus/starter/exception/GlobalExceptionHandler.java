package org.kitona.zus.starter.exception;

import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.exception.RestException;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获所有运行时异常
    @ExceptionHandler(RuntimeException.class)
    public RestResult<String> handleRuntimeException(RuntimeException ex) {
        return RestResult.error(500, "系统内部错误：" + ex.getMessage());
    }

    // 捕获参数绑定异常（如 @RequestBody 校验失败）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public RestResult<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("参数校验失败");
        return RestResult.error(400, errorMessage);
    }

    // 捕获项目抛出的异常
    @ExceptionHandler(RestException.class)
    public RestResult<String> handleResourceNotFoundException(RestException ex) {
        return RestResult.error(ex.getCode(), ex.getMessage());
    }

    // 捕获应用层异常（DDD 规范：应用层异常统一转换为 REST 响应）
    @ExceptionHandler(ApplicationException.class)
    public RestResult<String> handleApplicationException(ApplicationException ex) {
        return RestResult.error(ex.getCode(), ex.getMessage());
    }
}
