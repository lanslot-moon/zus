package org.kitona.zus.domain.query;

/**
 * 授权模型读侧视图
 *
 * <p>用于列表/分页等查询场景，只承载展示所需字段。
 */
public record AuthorizationModelView(
        String storeId,
        String modelId,
        String schemaVersion,
        String dslText,
        Integer status,
        String description,
        Long createTime
) {
}
