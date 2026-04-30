package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;

import java.util.Optional;

/**
 * 已编译模型缓存端口。
 * 该接口定义了编译后的授权模型缓存的基本操作，包括获取、存储和失效缓存项。
 */
public interface ICompiledModelCache {

    /**
     * 根据店铺ID和模型ID获取已编译的授权模型。
     *
     * @param storeId 店铺标识符
     * @param modelId 模型标识符
     * @return 包含已编译授权模型的Optional对象，如果不存在则返回空Optional
     */
    Optional<CompiledAuthorizationModel> get(String storeId, String modelId);

    /**
     * 将已编译的授权模型存入缓存。
     *
     * @param storeId 店铺标识符
     * @param modelId 模型标识符
     * @param model   已编译的授权模型对象
     */
    void put(String storeId, String modelId, CompiledAuthorizationModel model);

    /**
     * 使指定店铺和模型的缓存项失效。
     *
     * @param storeId 店铺标识符
     * @param modelId 模型标识符
     */
    void invalidate(String storeId, String modelId);
}
