package org.kitona.zus.infrastructure.persistence.mysql.converter;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Changelog PO 与领域实体 Changelog 转换器
 *
 * @author kitona
 */
public final class ChangelogConverter {

    private ChangelogConverter() {
    }

    /**
     * PO 转换为领域实体（持久化重建）
     */
    public static Changelog toEntity(ChangelogPO po) {
        if (po == null) {
            return null;
        }
        return Changelog.reconstitute(
                po.getStoreId(),
                po.getZookie(),
                po.getOperation(),
                po.getObjectType(),
                po.getObjectId(),
                po.getRelation(),
                po.getSubjectType(),
                po.getSubjectId(),
                po.getSubjectRelation(),
                po.getOperatorId(),
                po.getRequestId(),
                po.getSource(),
                po.getOperationTime()
        );
    }

    /**
     * 领域实体转换为 PO
     */
    public static ChangelogPO toPO(Changelog entity) {
        if (entity == null) {
            return null;
        }
        ChangelogPO po = new ChangelogPO();
        po.setId(IdWorker.getId());
        po.setStoreId(entity.getStoreId());
        po.setZookie(entity.getZookie());
        po.setOperation(entity.getOperation());
        po.setObjectType(entity.getObjectType());
        po.setObjectId(entity.getObjectId());
        po.setRelation(entity.getRelation());
        po.setSubjectType(entity.getSubjectType());
        po.setSubjectId(entity.getSubjectId());
        po.setSubjectRelation(entity.getSubjectRelation());
        po.setOperatorId(entity.getOperatorId());
        po.setRequestId(entity.getRequestId());
        po.setSource(entity.getSource());
        po.setOperationTime(entity.getOperationTime());
        return po;
    }

    public static List<Changelog> toEntityList(List<ChangelogPO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ChangelogConverter::toEntity).collect(Collectors.toList());
    }

    public static List<ChangelogPO> toPOList(List<Changelog> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ChangelogConverter::toPO).collect(Collectors.toList());
    }
}
