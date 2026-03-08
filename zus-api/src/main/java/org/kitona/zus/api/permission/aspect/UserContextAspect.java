package org.kitona.zus.api.permission.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.kitona.zus.common.context.UserContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
/*
 * 根据具体的业务进行userId和tenantId的获取,使用TransmittableThreadLocal防止多线程上下文数据的丢失
 */
public class UserContextAspect {

    /**
     * 1️⃣ 匹配 org.kitona.zus.api 包下所有接口的方法（不含子包）
     * 2️⃣ 匹配实现了这些接口的类的方法
     */
    @Around("execution(public * org.kitona.zus.api.*.*(..)) || " +
            "execution(public * *(..)) && target(org.kitona.zus.api.*)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String userId = "";
        String tenantId = "";
        try (UserContextHolder ignored = UserContextHolder.with(userId, tenantId)) {
            return joinPoint.proceed();
        }
    }
}
