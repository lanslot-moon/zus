package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.repository.IChangelogQueryRepository;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.application.coordinator.PermissionCheckCoordinator;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 权限检查应用服务
 *
 * <p>对外暴露权限检查用例，负责：
 * <ul>
 *   <li>请求参数转换与校验</li>
 *   <li>调用 PermissionCheckCoordinator 执行检查</li>
 *   <li>组装返回结果</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Service
public class PermissionCheckApplicationService implements IPermissionCheckApplicationService {

    @Resource
    private PermissionCheckCoordinator permissionCheckCoordinator;

    @Resource
    private IChangelogQueryRepository changelogQueryRepository;

    @Override
    public PermissionCheckResultDTO check(CheckCommand request) {
        ValidationUtil.validate(request);
        long startTime = System.currentTimeMillis();
        Zookie zookie = Zookie.parse(request.getConsistencyToken());
        ObjectRef object = ObjectRef.of(request.getObjectType(), request.getObjectId());
        Subject subject = buildSubject(request);

        PermissionCheckResult result = permissionCheckCoordinator.execute(request.getStoreId(), object, request.getRelation(), subject, zookie, request.getContext());

        String zookieToken = resolveZookieToken(request.getStoreId(), result);
        long duration = System.currentTimeMillis() - startTime;
        PermissionCheckResultDTO response = toPermissionCheckResultDTO(result, zookieToken, duration);

        logCheckResult(request, result, duration);
        return response;
    }

    @Override
    public CompletableFuture<PermissionCheckResultDTO> checkAsync(CheckCommand request) {
        return CompletableFuture.supplyAsync(() -> check(request));
    }

    private Subject buildSubject(CheckCommand request) {
        if (StringUtils.isBlank(request.getSubjectRelation())) {
            return Subject.user(request.getSubjectType(), request.getSubjectId());
        }
        return Subject.userset(request.getSubjectType(), request.getSubjectId(), request.getSubjectRelation());
    }

    private String resolveZookieToken(String storeId, PermissionCheckResult result) {
        if (PermissionCheckStatus.STORE_NOT_FOUND == result.status()) {
            return "";
        }
        Long version = changelogQueryRepository.getMaxZookie(storeId);
        return Zookie.of(version).toToken();
    }

    private PermissionCheckResultDTO toPermissionCheckResultDTO(PermissionCheckResult result, String zookieToken,
                                                                long duration) {
        if (result.isAllowed()) {
            return PermissionCheckResultDTO.allowed(zookieToken, duration);
        }
        if (result.isDenied()) {
            return PermissionCheckResultDTO.denied(zookieToken, duration);
        }
        return PermissionCheckResultDTO.error(result.status().name(), buildErrorMessage(result.status()),
                zookieToken, duration);
    }

    private String buildErrorMessage(PermissionCheckStatus status) {
        return switch (status) {
            case STORE_NOT_FOUND -> "store 不存在";
            case MODEL_NOT_BOUND -> "store 未绑定授权模型";
            case MODEL_NOT_FOUND -> "当前授权模型不存在";
            case MODEL_INVALID -> "当前授权模型无效";
            case ALLOWED, DENIED -> "";
        };
    }

    private void logCheckResult(CheckCommand request, PermissionCheckResult result, long duration) {
        if (Objects.equals(PermissionCheckStatus.ALLOWED, result.status())
                || Objects.equals(PermissionCheckStatus.DENIED, result.status())) {
            log.info("权限检查完成: params:{}, result={}, duration={}ms",
                    JacksonUtil.toJSONString(request), result.status(), duration);
            return;
        }
        log.warn("权限检查未能产出权限判定: params:{}, result={}, duration={}ms",
                JacksonUtil.toJSONString(request), result.status(), duration);
    }
}
