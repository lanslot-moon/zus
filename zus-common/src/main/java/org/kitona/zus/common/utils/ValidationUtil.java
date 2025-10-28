package org.kitona.zus.common.utils;

import jakarta.validation.*;
import jakarta.validation.groups.Default;


import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 参数校验工具类
 */
public final class ValidationUtil {

    private ValidationUtil() {
        throw new IllegalStateException("Utility class");
    }

    private static final Validator validator;

    static {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    /**
     * 校验整个对象（默认组），失败时抛出 ConstraintViolationException
     */
    public static <T> void validate(T object) {
        validate(object, Default.class);
    }

    /**
     * 支持指定分组的校验，失败时抛出 ConstraintViolationException
     */
    public static <T> void validate(T object, Class<?>... groups) {
        try {
            ValidationGroupContext.setGroups(groups);
            Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("参数校验失败", violations);
            }
        } finally {
            ValidationGroupContext.clear();
        }
    }

    /**
     * 校验信息（默认组)
     */
    public static <T> void validateMessage(T object) {
        validateMessage(object, Default.class);
    }

    /**
     * 获取指定分组的校验信息，不抛异常
     */
    public static <T> void validateMessage(T object, Class<?>... groups) {
        try {
            ValidationGroupContext.setGroups(groups);
            Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
            if (!violations.isEmpty()) {
                String errorMsg = violations.stream().map(v -> v.getPropertyPath() + ": " + v.getMessage()).collect(Collectors.joining("; "));
                throw new IllegalArgumentException(errorMsg);
            }
        } finally {
            ValidationGroupContext.clear();
        }

    }

    /**
     * 校验某个字段值是否符合指定组的约束
     */
    public static <T> void validatePropertyValue(Class<T> clazz, String propertyName, Object value, Class<?>... groups) {
        try {
            ValidationGroupContext.setGroups(groups);
            Set<ConstraintViolation<T>> violations = validator.validateValue(clazz, propertyName, value, groups);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("字段值校验失败", violations);
            }
        } finally {
            ValidationGroupContext.clear();
        }
    }

    /**
     * 设置当前线程的校验组,留给动态校验组的拓展
     */
    public static class ValidationGroupContext implements AutoCloseable {
        private static final ThreadLocal<Set<String>> GROUPS = new ThreadLocal<>();

        public static void setGroups(Class<?>... groups) {
            if (groups == null || groups.length == 0) {
                return;
            }

            Set<String> groupNames = Arrays.stream(groups)
                    .filter(Objects::nonNull) // 过滤掉 null 的 group
                    .map(Class::getName)
                    .collect(Collectors.toSet());

            if (!groupNames.isEmpty()) {
                GROUPS.set(groupNames);
            }
        }

        public static Set<String> getCurrentGroups() {
            return GROUPS.get() != null ? GROUPS.get() : Collections.emptySet();
        }

        public static void clear() {
            GROUPS.remove();
        }

        @Override
        public void close() {
            clear();
        }
    }
}