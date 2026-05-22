package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.valueobject.PermissionCheckResult;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.application.coordinator.PermissionCheckCoordinator;
import org.kitona.zus.service.application.coordinator.PermissionExplainCoordinator;
import org.kitona.zus.service.application.coordinator.PermissionExplainOutcome;
import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.ExplainResolutionDTO;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.kitona.zus.service.port.IConsistencyTokenReader;
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
    private PermissionExplainCoordinator permissionExplainCoordinator;

    @Resource
    private IConsistencyTokenReader consistencyTokenReader;

    /**
     * 检查check。
     *
     * @param request 请求对象
     * @return 执行结果
     */
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

        if (Objects.equals(PermissionCheckStatus.ALLOWED, result.status()) || Objects.equals(PermissionCheckStatus.DENIED, result.status())) {
            log.info("权限检查完成: params:{}, result={}, duration={}ms", JacksonUtil.toJSONString(request), result.status(), duration);
            return response;
        }
        log.warn("权限检查未能产出权限判定: params:{}, result={}, duration={}ms", JacksonUtil.toJSONString(request), result.status(), duration);
        return response;
    }




    /**
     * 执行授权解释用例。
     *
     * @param command 应用命令
     * @return 返回结果
     */
    @Override
    public PermissionExplainResultDTO explain(ExplainCommand command) {
        ValidationUtil.validate(command);
        long startTime = System.currentTimeMillis();
        PermissionExplainOutcome outcome = permissionExplainCoordinator.explain(
                command.getStoreId(),
                ObjectRef.of(command.getObjectType(), command.getObjectId()),
                command.getRelation(),
                buildSubject(command),
                Zookie.parse(command.getConsistencyToken()),
                command.getContext()
        );
        long durationMs = System.currentTimeMillis() - startTime;
        return toResultDTO(outcome, durationMs);
    }

    /**
     * 构建授权主体值对象。
     *
     * @param command 应用命令
     * @return 构建结果
     */
    private Subject buildSubject(ExplainCommand command) {
        if (StringUtils.isBlank(command.getSubjectRelation())) {
            return Subject.user(command.getSubjectType(), command.getSubjectId());
        }
        return Subject.userset(command.getSubjectType(), command.getSubjectId(), command.getSubjectRelation());
    }

    /**
     * 转换授权解释结果 DTO。
     *
     * @param outcome outcome 参数
     * @param durationMs durationMs 参数
     * @return 构建结果
     */
    private PermissionExplainResultDTO toResultDTO(PermissionExplainOutcome outcome, long durationMs) {
        if (outcome.isAllowed() || outcome.isDenied()) {
            ExplainResolutionDTO resolution = ExplainResolutionDTO.from(outcome.trace());
            String zookieToken = resolution != null ? resolution.getCurrentZookie() : "";
            return PermissionExplainResultDTO.of(outcome.isAllowed(), outcome.status().name(), zookieToken,
                    durationMs, resolution);
        }
        return PermissionExplainResultDTO.error(outcome.status().name(), outcome.status().getDesc(), durationMs);
    }

    /**
     * 检查check async。
     *
     * @param request 请求对象
     * @return 执行结果
     */
    @Override
    public CompletableFuture<PermissionCheckResultDTO> checkAsync(CheckCommand request) {
        return CompletableFuture.supplyAsync(() -> check(request));
    }

    /**
     * 构建授权主体值对象。
     *
     * @param request 请求对象
     * @return 构建结果
     */
    private Subject buildSubject(CheckCommand request) {
        if (StringUtils.isBlank(request.getSubjectRelation())) {
            return Subject.user(request.getSubjectType(), request.getSubjectId());
        }
        return Subject.userset(request.getSubjectType(), request.getSubjectId(), request.getSubjectRelation());
    }


    /**
     * 解析响应使用的一致性 token。
     *
     * @param storeId Store 标识
     * @param result 结果对象
     * @return 构建结果
     */
    private String resolveZookieToken(String storeId, PermissionCheckResult result) {
        if (PermissionCheckStatus.STORE_NOT_FOUND == result.status()) {
            return "";
        }
        Long version = consistencyTokenReader.currentMaxZookie(storeId);
        return Zookie.of(version).toToken();
    }


    /**
     * 转换权限检查结果 DTO。
     *
     * @param result      领域权限检查结果
     * @param zookieToken 响应一致性 token
     * @param duration    检查耗时
     * @return 权限检查结果 DTO
     */
    private PermissionCheckResultDTO toPermissionCheckResultDTO(PermissionCheckResult result, String zookieToken,
                                                                long duration) {
        if (result.isAllowed()) {
            return PermissionCheckResultDTO.allowed(zookieToken, duration);
        }
        if (result.isDenied()) {
            return PermissionCheckResultDTO.denied(zookieToken, duration);
        }
        return PermissionCheckResultDTO.error(result.status().name(), result.status().getDesc(), zookieToken, duration);
    }
}
