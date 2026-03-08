package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.CheckCommand;
import org.kitona.zus.service.dto.response.CheckResultDTO;

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
public interface ICheckApplicationService {

    /**
     * 执行权限检查
     *
     * @param request 检查请求
     * @return 检查响应
     */
    CheckResultDTO check(CheckCommand request);

    /**
     * 异步执行权限检查
     *
     * @param request 检查请求
     * @return CompletableFuture 包装的检查响应
     */
    CompletableFuture<CheckResultDTO> checkAsync(CheckCommand request);
}
