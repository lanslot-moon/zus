package org.kitona.zus.domain.read.view;

/**
 * Store 读侧视图
 *
 * <p>用于应用层查询返回，承载展示所需字段，
 * 不参与聚合不变量维护。
 */
public record StoreView(
        String storeId,
        String name,
        String description,
        String currentModelId,
        Long currentZookie,
        Integer status,
        Long createTime
) {
}
