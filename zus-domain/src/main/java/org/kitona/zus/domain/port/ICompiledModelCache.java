package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;

import java.util.Optional;

/**
 * 已编译模型缓存端口。
 */
public interface ICompiledModelCache {

    Optional<CompiledAuthorizationModel> get(String storeId, String modelId);

    void put(String storeId, String modelId, CompiledAuthorizationModel model);

    void invalidate(String storeId, String modelId);
}
