package org.kitona.zus.common.utils;

import lombok.extern.slf4j.Slf4j;


import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Kitona
 * Date: 2025/10/11 16:33
 * Desc: 用于获取Bean的字段名称的工具类
 * <a href="https://www.cnblogs.com/ludangxin/p/17775334.html"/a>
 */
@Slf4j
public class BeanFieldUtil {

    @FunctionalInterface
    public interface SFunction<T> extends Serializable {
        Object apply(T t);
    }


    private static final Map<SFunction<?>, Field> FUNCTION_CACHE = new ConcurrentHashMap<>();

    public static <T> String getFieldName(SFunction<T> function) {
        Field field = BeanFieldUtil.getField(function);
        return field.getName();
    }
 
    public static <T> Field getField(SFunction<T> function) {
        return FUNCTION_CACHE.computeIfAbsent(function, BeanFieldUtil::findField);
    }
 
    public static <T> Field findField(SFunction<T> function) {
        // 第1步 获取SerializedLambda
        final SerializedLambda serializedLambda = getSerializedLambda(function);
        // 第2步 implMethodName 即为Field对应的Getter方法名
        final String implClass = serializedLambda.getImplClass();
        final String implMethodName = serializedLambda.getImplMethodName();
        final String fieldName = convertToFieldName(implMethodName);
        // 第3步  Spring 中的反射工具类获取Class中定义的Field
        final Field field = getField(fieldName, serializedLambda);

        // 第4步 如果没有找到对应的字段应该抛出异常
        if (field == null) {
            throw new IllegalArgumentException("No such class 「"+ implClass +"」 field 「" + fieldName + "」.");
        }

        return field;
    }

    static Field getField(String fieldName, SerializedLambda serializedLambda) {
        try {
            // 获取的Class是字符串，并且包名是“/”分割，需要替换成“.”，才能获取到对应的Class对象
            String declaredClass = serializedLambda.getImplClass().replace("/", ".");
            Class<?>aClass = Class.forName(declaredClass, false, getDefaultClassLoader());
            return ReflectionUtil.findField(aClass, fieldName);
        }
        catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("get class field exception.", e);
        }
    }

    static String convertToFieldName(String getterMethodName) {
        // 获取方法名
        String prefix = null;
        if (getterMethodName.startsWith("get")) {
            prefix = "get";
        }
        else if (getterMethodName.startsWith("is")) {
            prefix = "is";
        }

        if (prefix == null) {
            throw new IllegalArgumentException("invalid getter method: " + getterMethodName);
        }

        // 截取get/is之后的字符串并转换首字母为小写
        return Introspector.decapitalize(getterMethodName.replace(prefix, ""));
    }

    static <T> SerializedLambda getSerializedLambda(SFunction<T> function) {
        try {
            Class<?> lambdaClass = function.getClass();
            // 在目标类中创建私有Lookup（绕过模块可见性问题）
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(lambdaClass, MethodHandles.lookup());
            MethodHandle writeReplace = lookup.findVirtual(lambdaClass, "writeReplace", MethodType.methodType(Object.class));
            return (SerializedLambda) writeReplace.invoke(function);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Lambda 未定义 writeReplace 方法。", e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("无法访问 Lambda 的 writeReplace 方法（模块未开放）。", e);
        } catch (Throwable e) {
            throw new IllegalArgumentException("获取 SerializedLambda 失败。", e);
        }
    }


    @SuppressWarnings("all")
    private static ClassLoader getDefaultClassLoader() {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        }
        catch (Throwable ex) {
            // Cannot access thread context ClassLoader - falling back...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = BeanFieldUtil.class.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                }
                catch (Throwable ex) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return cl;
    }
}
