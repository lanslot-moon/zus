package org.kitona.zus.starter.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.common.exception.RestException;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.service.exception.ApplicationException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.kitona.zus.api.response.RestResult;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获参数绑定异常（如 @RequestBody 校验失败）
     */
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

    /**
     * 捕获 JSR303 校验异常（如 Service 层 @Validated 校验失败）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public RestResult<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数校验失败");
        return RestResult.error(400, errorMessage);
    }

    /**
     * 捕获基础 RestException 异常
     */
    @ExceptionHandler(RestException.class)
    public RestResult<String> handleRestException(RestException ex) {
        log.warn("GlobalExceptionHandler handleRestException 捕获到业务异常 code={}, msg={}", ex.getCode(), ex.getMessage());
        return RestResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕获系统/核心异常（Domain 层或核心组件抛出的自定义异常）
     */
    @ExceptionHandler(SystemException.class)
    public RestResult<String> handleSystemException(SystemException ex) {
        log.error("GlobalExceptionHandler handleSystemException 捕获到系统异常 code={}, msg={}", ex.getCode(), ex.getMessage(), ex);
        return RestResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕获应用层异常（DDD 规范：应用层异常统一转换为 REST 响应）
     */
    @ExceptionHandler(ApplicationException.class)
    public RestResult<String> handleApplicationException(ApplicationException ex) {
        log.error("GlobalExceptionHandler handleApplicationException 捕获到应用层异常 code={}, msg={}", ex.getCode(), ex.getMessage(), ex);
        return RestResult.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 捕获兜底的其他所有未知异常
     */
    @ExceptionHandler(Exception.class)
    public RestResult<String> handleException(Exception ex) {
        log.error("GlobalExceptionHandler handleException 捕获到未知异常", ex);
        return RestResult.error(500, "系统内部错误: " + ex.getMessage());
    }
}
