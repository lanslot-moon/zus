package org.kitona.zus.service.application.impl;

import org.apache.commons.lang3.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.ICheckApplicationService;
import org.kitona.zus.service.application.support.AuthorizationCheckExecutor;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.CheckResultDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * 权限检查应用服务
 *
 * <p>对外暴露权限检查用例，负责：
 * <ul>
 *   <li>请求参数转换与校验</li>
 *   <li>调用 AuthorizationCheckExecutor 执行检查</li>
 *   <li>组装返回结果</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class CheckApplicationService implements ICheckApplicationService {

    @Resource
    private AuthorizationCheckExecutor authorizationCheckExecutor;

    @Resource
    private IStoreDomainRepository storeRepository;

    @Override
    public CheckResultDTO check(CheckCommand request) {
        ValidationUtil.validate(request);
        long startTime = System.currentTimeMillis();

        try {
            Zookie zookie = Zookie.parse(request.getConsistencyToken());
            // 资源对象
            ObjectRef object = ObjectRef.of(request.getObjectType(), request.getObjectId());
            // 资源主体
            Subject subject = Subject.user(request.getSubjectType(), request.getSubjectId());

            if (StringUtils.isNotBlank(request.getSubjectRelation())) {
                subject = Subject.userset(request.getSubjectType(), request.getSubjectId(), request.getSubjectRelation());
            }

            boolean allowed = authorizationCheckExecutor.execute(request.getStoreId(), object, request.getRelation(), subject, zookie);

            // 通过仓储获取当前 Zookie
            Long version = storeRepository.getCurrentZookie(request.getStoreId());
            Zookie currentZookie = Zookie.of(version);
            String zookieToken = currentZookie != null ? currentZookie.toToken() : "";
            long duration = System.currentTimeMillis() - startTime;

            log.info("权限检查完成: params:{}, allowed={}, duration={}ms", JacksonUtil.toJSONString(request), allowed, duration);

            return allowed ? CheckResultDTO.allowed(zookieToken, duration) : CheckResultDTO.denied(zookieToken, duration);
        } catch (Exception e) {
            log.error("权限检查异常", e);
            return CheckResultDTO.error("权限检查失败: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<CheckResultDTO> checkAsync(CheckCommand request) {
        return CompletableFuture.supplyAsync(() -> check(request));
    }
}
