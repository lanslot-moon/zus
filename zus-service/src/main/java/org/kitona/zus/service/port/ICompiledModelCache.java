package org.kitona.zus.service.port;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;

import java.util.Optional;

/**
 * 应用层编译模型缓存端口。
 *
 * <p>编译模型缓存服务于 Check/List/Explain 用例编排，属于应用层性能优化能力，
 * 领域服务只接收已经准备好的 {@link CompiledAuthorizationModel}。
 */
public interface ICompiledModelCache {

    /**
     * 读取已编译模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 模型标识
     * @return 已编译模型，不存在返回 empty
     */
    Optional<CompiledAuthorizationModel> get(String storeId, String modelId);

    /**
     * 写入已编译模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 模型标识
     * @param model   已编译模型
     */
    void put(String storeId, String modelId, CompiledAuthorizationModel model);

    /**
     * 失效已编译模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 模型标识
     */
    void invalidate(String storeId, String modelId);
}
