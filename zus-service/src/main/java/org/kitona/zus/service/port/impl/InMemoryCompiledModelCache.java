package org.kitona.zus.service.port.impl;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 应用层默认编译模型缓存实现。
 *
 * <p>缓存端口属于应用层用例优化，不应让基础设施模块反向依赖 service。
 * 该实现只提供进程内缓存语义，后续如果需要 Redis/Caffeine 等技术实现，
 * 应在组合根中通过适配器显式替换，而不是把技术细节倒灌到领域层。
 */
@Component
public class InMemoryCompiledModelCache implements ICompiledModelCache {

    /**
     * 按 storeId:modelId 保存编译后的授权模型。
     */
    private final ConcurrentMap<String, CompiledAuthorizationModel> cache = new ConcurrentHashMap<>();

    /**
     * 读取编译模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 查询结果
     */
    @Override
    public Optional<CompiledAuthorizationModel> get(String storeId, String modelId) {
        return Optional.ofNullable(cache.get(cacheKey(storeId, modelId)));
    }

    /**
     * 写入put。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @param model 授权模型聚合
     */
    @Override
    public void put(String storeId, String modelId, CompiledAuthorizationModel model) {
        if (model == null) {
            return;
        }
        cache.put(cacheKey(storeId, modelId), model);
    }

    /**
     * 失效编译模型缓存。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     */
    @Override
    public void invalidate(String storeId, String modelId) {
        cache.remove(cacheKey(storeId, modelId));
    }

    /**
     * 构建编译模型缓存键。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 返回结果
     */
    private String cacheKey(String storeId, String modelId) {
        return Objects.toString(storeId, "") + ":" + Objects.toString(modelId, "");
    }
}
