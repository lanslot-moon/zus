package org.kitona.zus.infrastructure.cache;

import jakarta.annotation.Resource;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * 编译模型缓存适配器。
 */
@Component
public class CompiledModelCacheAdapter implements ICompiledModelCache {

    @Resource
    private FgaCacheManager cacheManager;

    @Override
    public Optional<CompiledAuthorizationModel> get(String storeId, String modelId) {
        Object cached = cacheManager.getModel(storeId, modelId);
        if (cached instanceof CompiledAuthorizationModel compiledAuthorizationModel) {
            return Optional.of(compiledAuthorizationModel);
        }
        return Optional.empty();
    }

    @Override
    public void put(String storeId, String modelId, CompiledAuthorizationModel model) {
        cacheManager.setModel(storeId, modelId, model);
    }

    @Override
    public void invalidate(String storeId, String modelId) {
        cacheManager.invalidateModel(storeId, modelId);
    }
}
