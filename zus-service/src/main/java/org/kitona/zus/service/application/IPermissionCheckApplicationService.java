package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.PermissionCheckResultDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;

import java.util.concurrent.CompletableFuture;

/**
 * Check 应用服务接口
 *
 * 提供权限检查用例的应用层契约。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IPermissionCheckApplicationService {

    /**
     * 执行权限检查
     *
     * @param request 检查请求
     * @return 检查响应
     */
    PermissionCheckResultDTO check(CheckCommand request);

    /**
     * 执行独立 explain 用例。
     */
    PermissionExplainResultDTO explain(ExplainCommand command);

    /**
     * 异步执行权限检查
     *
     * @param request 检查请求
     * @return CompletableFuture 包装的检查响应
     */
    CompletableFuture<PermissionCheckResultDTO> checkAsync(CheckCommand request);
}
