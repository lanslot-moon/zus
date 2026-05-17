package org.kitona.zus.infrastructure.persistence.mysql.converter;

import org.kitona.zus.domain.authorization.store.StoreAggregate;
import org.kitona.zus.domain.authorization.store.StoreSnapshot;
import org.kitona.zus.domain.enums.StoreStatus;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;

/**
 * Store PO 与领域聚合根 StoreAggregate 转换器
 *
 * @author kitona
 */
public final class StoreConverter {

    private StoreConverter() {
    }

    /**
     * PO 转换为聚合根（通过 reconstitute 重建，不绕过领域创建规则）
     */
    public static StoreAggregate toAggregate(StorePO po) {
        if (po == null) {
            return null;
        }
        StoreSnapshot snapshot = new StoreSnapshot(
                po.getId(),
                po.getStoreId(),
                po.getName(),
                po.getDescription(),
                po.getCurrentModelId(),
                StoreStatus.fromCode(po.getStatus()),
                po.getCreateTime()
        );
        return StoreAggregate.reconstitute(snapshot);
    }

    /**
     * 聚合根转换为 PO
     */
    public static StorePO toPO(StoreAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        StorePO po = new StorePO();
        po.setId(aggregate.getId());
        po.setStoreId(aggregate.getStoreId());
        po.setName(aggregate.getName());
        po.setDescription(aggregate.getDescription());
        po.setCurrentModelId(aggregate.getCurrentModelId());
        po.setStatus(aggregate.getStatusCode());
        po.setCreateTime(aggregate.getCreateTime());
        return po;
    }

    /**
     * PO 转换为读侧视图
     */
    public static StoreView toView(StorePO po) {
        if (po == null) {
            return null;
        }
        return new StoreView(
                po.getStoreId(),
                po.getName(),
                po.getDescription(),
                po.getCurrentModelId(),
                po.getCurrentZookie(),
                po.getStatus(),
                po.getCreateTime()
        );
    }
}
