package org.kitona.zus.domain.authorization.model;

import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * 授权模型聚合根标识。
 *
 * <p>授权模型在业务上隶属于某个 Store，因此完整身份由 {@code storeId + modelId} 共同组成。
 * 使用显式值对象可以避免领域仓储接口暴露散乱字符串参数，也让调用方更清楚地表达
 * “按聚合身份查找授权模型”这一领域语义。
 *
 * @param storeId Store 标识
 * @param modelId 授权模型标识
 */
public record AuthorizationModelId(String storeId, String modelId) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建授权模型聚合根标识。
     *
     * @param storeId Store 标识
     * @param modelId 授权模型标识
     * @return 授权模型聚合根标识
     */
    public static AuthorizationModelId of(String storeId, String modelId) {
        if (StringUtils.isAnyBlank(storeId, modelId)) {
            throw new IllegalArgumentException("storeId 和 modelId 不能为空");
        }
        return new AuthorizationModelId(storeId, modelId);
    }
}
