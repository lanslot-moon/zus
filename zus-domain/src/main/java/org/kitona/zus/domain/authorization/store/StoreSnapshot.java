package org.kitona.zus.domain.authorization.store;

import org.kitona.zus.domain.enums.StoreStatus;

/**
 * 存储空间持久化快照（参数对象）
 * <p>
 * 用于 {@link StoreAggregate#reconstitute(StoreSnapshot)} 从持久化重建聚合根，
 * 将超过 7 个参数封装为单一对象，避免长参数列表。
 *
 * @param id              数据库主键
 * @param storeId         存储空间ID
 * @param name            名称
 * @param description     描述
 * @param currentModelId  当前模型ID
 * @param status          状态
 * @param createTime      创建时间
 * @author kitona
 */
public record StoreSnapshot(
        Long id,
        String storeId,
        String name,
        String description,
        String currentModelId,
        StoreStatus status,
        Long createTime
) {
}
