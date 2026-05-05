package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;

/**
 * 权限解释应用服务。
 */
public interface IPermissionExplainApplicationService {

    /**
     * 执行独立 explain 用例。
     */
    PermissionExplainResultDTO explain(ExplainCommand command);
}
