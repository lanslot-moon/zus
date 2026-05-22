package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.command.WriteTupleCommand;

import java.util.List;

/**
 * Write 应用服务接口
 *
 * 提供元组写入用例的应用层契约。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface ITupleMutationApplicationService {

    /**
     * 写入元组数据方法
     *
     * @param storeId              存储ID，用于标识特定的数据存储
     * @param authorizationModelId 可选模型版本，存在条件 tuple 时用于解析条件定义
     * @param writeTuple           包含要写入的元组数据的DTO列表
     */
    void write(String storeId, String authorizationModelId, List<WriteTupleCommand> writeTuple);

    /**
     * 删除元组数据方法
     *
     * @param storeId 存储ID，用于标识特定的数据存储
     * @param writeTuple 包含要删除的元组数据的DTO列表
     */
    void delete(String storeId, List<WriteTupleCommand> writeTuple);
}
