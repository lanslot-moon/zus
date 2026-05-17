package org.kitona.zus.infrastructure.persistence.mysql.converter;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleCondition;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;

import java.util.Collections;
import java.util.List;

/**
 * Tuple PO 与领域实体 RelationTuple 转换器
 *
 * @author kitona
 */
public final class TupleConverter {

    private TupleConverter() {
    }

    /**
     * PO 转换为领域实体（持久化重建）
     */
    public static RelationTuple toEntity(RelationTuplePO po) {
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
        return RelationTuple.reconstitute(
                po.getId(),
                po.getStoreId(),
                tupleKey,
                po.getZookie() != null ? Zookie.of(po.getZookie()) : null,
                TupleCondition.of(po.getConditionDefinitionId(), po.getConditionName(), po.getConditionContext()),
                po.getExpiresAt(),
                Boolean.TRUE.equals(po.getIsWildcard()),
                po.getCreateTime()
        );
    }

    /**
     * 领域实体转换为 PO
     */
    public static RelationTuplePO toPO(RelationTuple entity) {
        if (entity == null) {
            return null;
        }
        RelationTuplePO po = new RelationTuplePO();
        po.setId(entity.getId() != null ? entity.getId() : IdWorker.getId());
        po.setCreateTime(entity.getCreateTime());
        po.setStoreId(entity.getStoreId());
        po.setObjectType(entity.getObjectType());
        po.setObjectId(entity.getObjectId());
        po.setRelation(entity.getRelation());
        po.setSubjectType(entity.getSubjectType());
        po.setSubjectId(entity.getSubjectId());
        po.setSubjectRelation(normalizeSubjectRelation(entity.getSubjectRelation()));
        po.setIsWildcard(entity.isWildcard());
        po.setZookie(entity.getZookie() != null ? entity.getZookie().getVersion() : null);
        po.setConditionDefinitionId(entity.getConditionDefinitionId());
        po.setConditionName(entity.getConditionName());
        po.setConditionContext(entity.getConditionContext());
        po.setExpiresAt(entity.getExpiresAt());
        return po;
    }

    private static String normalizeSubjectRelation(String subjectRelation) {
        return subjectRelation == null ? "" : subjectRelation;
    }

    public static List<RelationTuple> toEntityList(List<RelationTuplePO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(TupleConverter::toEntity).toList();
    }

    public static List<RelationTuplePO> toPOList(List<RelationTuple> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(TupleConverter::toPO).toList();
    }
}
