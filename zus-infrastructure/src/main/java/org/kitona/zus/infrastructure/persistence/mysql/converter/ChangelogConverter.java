package org.kitona.zus.infrastructure.persistence.mysql.converter;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;

import java.util.Collections;
import java.util.List;

/**
 * Changelog PO 与领域实体 Changelog 转换器
 *
 * @author kitona
 */
public final class ChangelogConverter {

    /**
     * 创建 ChangelogConverter 工具类私有构造方法，防止外部实例化。
     */
    private ChangelogConverter() {
    }

    /**
     * PO 转换为领域实体（持久化重建）
     */
    public static Changelog toEntity(TupleChangelogPO po) {
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
    public static TupleChangelogPO toPO(Changelog entity) {
        if (entity == null) {
            return null;
        }
        long operationTime = entity.getOperationTime() != null ? entity.getOperationTime() : System.currentTimeMillis();
        TupleChangelogPO po = new TupleChangelogPO();
        po.setId(IdWorker.getId());
        po.setStoreId(entity.getStoreId());
        po.setZookie(entity.getZookie());
        po.setOperation(entity.getOperation());
        po.setObjectType(entity.getObjectType());
        po.setObjectId(entity.getObjectId());
        po.setRelation(entity.getRelation());
        po.setSubjectType(entity.getSubjectType());
        po.setSubjectId(entity.getSubjectId());
        po.setSubjectRelation(normalizeSubjectRelation(entity.getSubjectRelation()));
        po.setOperatorId(entity.getOperatorId());
        po.setRequestId(entity.getRequestId());
        po.setSource(entity.getSource());
        po.setOperationTime(operationTime);
        po.setCreateTime(operationTime);
        po.setUpdateTime(operationTime);
        return po;
    }

    /**
     * 规范化 subject relation 为空字符串语义。
     *
     * @param subjectRelation 主体关系
     * @return 返回结果
     */
    private static String normalizeSubjectRelation(String subjectRelation) {
        return subjectRelation == null ? "" : subjectRelation;
    }

    /**
     * 批量转换为领域实体列表。
     *
     * @param list list 参数
     * @return 构建结果
     */
    public static List<Changelog> toEntityList(List<TupleChangelogPO> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ChangelogConverter::toEntity).toList();
    }

    /**
     * 批量转换为持久化对象列表。
     *
     * @param list list 参数
     * @return 构建结果
     */
    public static List<TupleChangelogPO> toPOList(List<Changelog> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return list.stream().map(ChangelogConverter::toPO).toList();
    }
}
