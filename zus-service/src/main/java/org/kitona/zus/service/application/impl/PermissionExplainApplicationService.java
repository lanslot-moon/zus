package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.PermissionCheckStatus;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.IPermissionExplainApplicationService;
import org.kitona.zus.service.application.coordinator.PermissionExplainCoordinator;
import org.kitona.zus.service.application.coordinator.PermissionExplainOutcome;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.ExplainResolutionDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.springframework.stereotype.Service;

/**
 * 权限解释应用服务实现。
 *
 * <p>这是冷路径调试用例，负责参数校验、耗时统计和结果 DTO 组装。
 */
@Service
public class PermissionExplainApplicationService implements IPermissionExplainApplicationService {

    @Resource
    private PermissionExplainCoordinator permissionExplainCoordinator;

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

    private Subject buildSubject(ExplainCommand command) {
        if (StringUtils.isBlank(command.getSubjectRelation())) {
            return Subject.user(command.getSubjectType(), command.getSubjectId());
        }
        return Subject.userset(command.getSubjectType(), command.getSubjectId(), command.getSubjectRelation());
    }

    private PermissionExplainResultDTO toResultDTO(PermissionExplainOutcome outcome, long durationMs) {
        if (outcome.isAllowed() || outcome.isDenied()) {
            ExplainResolutionDTO resolution = ExplainResolutionDTO.from(outcome.trace());
            String zookieToken = resolution != null ? resolution.getCurrentZookie() : "";
            return PermissionExplainResultDTO.of(outcome.isAllowed(), outcome.status().name(), zookieToken,
                    durationMs, resolution);
        }
        return PermissionExplainResultDTO.error(outcome.status().name(), outcome.status().getDesc(), durationMs);
    }
}
