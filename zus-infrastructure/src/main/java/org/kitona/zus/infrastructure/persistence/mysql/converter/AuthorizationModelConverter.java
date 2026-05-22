package org.kitona.zus.infrastructure.persistence.mysql.converter;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelSnapshot;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;

/**
 * AuthorizationModel PO 与领域聚合根 AuthorizationModelAggregate 转换器
 *
 * @author kitona
 */
public final class AuthorizationModelConverter {

    /**
     * 创建 AuthorizationModelConverter 工具类私有构造方法，防止外部实例化。
     */
    private AuthorizationModelConverter() {
    }

    /**
     * PO 转换为聚合根（通过 reconstitute 重建，不绕过领域创建规则）
     */
    public static AuthorizationModelAggregate toAggregate(AuthModelPO po) {
        if (po == null) {
            return null;
        }
        ModelPublishStatus status = ModelPublishStatus.fromStatus(po.getStatus());
        AuthorizationModelSnapshot snapshot = new AuthorizationModelSnapshot(
                po.getStoreId(),
                po.getModelId(),
                po.getSchemaVersion(),
                po.getDslText(),
                status,
                po.getDescription(),
                po.getCreateTime()
        );
        return AuthorizationModelAggregate.reconstitute(snapshot);
    }

    /**
     * 聚合根转换为 PO
     */
    public static AuthModelPO toPO(AuthorizationModelAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        AuthModelPO po = new AuthModelPO();
        po.setStoreId(aggregate.getStoreId());
        po.setModelId(aggregate.getModelId());
        po.setSchemaVersion(aggregate.getSchemaVersion());
        po.setDslText(aggregate.getDslText());
        po.setStatus(aggregate.getStatusValue());
        po.setDescription(aggregate.getDescription());
        po.setCreateTime(aggregate.getCreateTime());
        return po;
    }

    /**
     * PO 转换为读侧视图
     */
    public static AuthorizationModelView toView(AuthModelPO po) {
        if (po == null) {
            return null;
        }
        return new AuthorizationModelView(
                po.getStoreId(),
                po.getModelId(),
                po.getSchemaVersion(),
                po.getDslText(),
                po.getStatus(),
                po.getDescription(),
                po.getCreateTime()
        );
    }
}
