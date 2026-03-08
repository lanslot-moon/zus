package org.kitona.zus.domain.aggregate;

import org.kitona.zus.domain.enums.ModelPublishStatus;

/**
 * 授权模型持久化快照（参数对象）
 * <p>
 * 用于 {@link AuthorizationModelAggregate#reconstitute(AuthorizationModelSnapshot)} 从持久化重建聚合根，
 * 将多个参数封装为单一对象，避免长参数列表。
 *
 * @param storeId       存储空间ID
 * @param modelId       模型ID
 * @param schemaVersion Schema 版本
 * @param dslText       原始 DSL 文本
 * @param status        模型状态
 * @param description   描述
 * @param createTime    创建时间
 * @author kitona
 */
public record AuthorizationModelSnapshot(
        String storeId,
        String modelId,
        String schemaVersion,
        String dslText,
        ModelPublishStatus status,
        String description,
        Long createTime
) {
}
