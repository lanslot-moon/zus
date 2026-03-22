package org.kitona.zus.infrastructure.persistence.mysql.converter;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.valueobject.TupleCondition;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tuple PO 与领域实体 RelationTupleEntity 转换器
 *
 * @author kitona
 */
public final class TupleConverter {

    private TupleConverter() {
    }

    /**
     * PO 转换为领域实体（持久化重建）
     */
    public static RelationTupleEntity toEntity(TuplePO po) {
        if (po == null) {
            return null;
        }
        TupleKey tupleKey = TupleKey.of(
                po.getObjectType(),
                po.getObjectId(),
                po.getRelation(),
                po.getSubjectType(),
                po.getSubjectId(),
                po.getSubjectRelation()
        );
        return RelationTupleEntity.reconstitute(
                po.getId(),
                po.getStoreId(),
                tupleKey,
                po.getZookie() != null ? Zookie.of(po.getZookie()) : null,
                TupleCondition.of(po.getConditionName(), po.getConditionContext()),
                po.getCreateTime()
        );
    }

    /**
     * 领域实体转换为 PO
     */
    public static TuplePO toPO(RelationTupleEntity entity) {
        if (entity == null) {
            return null;
        }
        TuplePO po = new TuplePO();
        po.setId(entity.getId() != null ? entity.getId() : IdWorker.getId());
        po.setCreateTime(entity.getCreateTime());
        po.setStoreId(entity.getStoreId());
        po.setObjectType(entity.getObjectType());
        po.setObjectId(entity.getObjectId());
        po.setRelation(entity.getRelation());
        po.setSubjectType(entity.getSubjectType());
        po.setSubjectId(entity.getSubjectId());
        po.setSubjectRelation(entity.getSubjectRelation());
        po.setZookie(entity.getZookie() != null ? entity.getZookie().getVersion() : null);
        po.setConditionName(entity.getConditionName());
        po.setConditionContext(entity.getConditionContext());
        return po;
    }

    public static List<RelationTupleEntity> toEntityList(List<TuplePO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(TupleConverter::toEntity).collect(Collectors.toList());
    }

    public static List<TuplePO> toPOList(List<RelationTupleEntity> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(TupleConverter::toPO).collect(Collectors.toList());
    }
}
