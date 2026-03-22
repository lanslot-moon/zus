package org.kitona.zus.domain.entity;

import lombok.Getter;
import org.kitona.zus.domain.valueobject.TupleKey;

import java.util.Objects;

/**
 * 变更日志实体
 *
 * <p>记录元组的写入和删除操作，用于：
 * <ul>
 *   <li>Zookie 一致性读取</li>
 *   <li>Watch API 实时监听</li>
 *   <li>审计追踪</li>
 * </ul>
 *
 * <p>创建方式（符合 DDD）：
 * <ul>
 *   <li>业务创建：使用 {@link #createWriteLog} 或 {@link #createDeleteLog}</li>
 *   <li>持久化重建：使用 {@link #reconstitute}</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public class ChangelogEntity {

    /**
     * 操作类型常量
     */
    public static final String OPERATION_WRITE = "WRITE";
    public static final String OPERATION_DELETE = "DELETE";

    /** 存储空间ID */
    private String storeId;

    /** Zookie 版本号 */
    private Long zookie;

    /** 操作类型：WRITE、DELETE */
    private String operation;

    /** 资源对象类型 */
    private String objectType;

    /** 资源对象ID */
    private String objectId;

    /** 关系名称 */
    private String relation;

    /** 主体类型 */
    private String subjectType;

    /** 主体ID */
    private String subjectId;

    /** 主体关系（用于用户集） */
    private String subjectRelation;

    /** 操作时间戳（毫秒） */
    private Long operationTime;

    /**
     * 私有构造函数
     */
    private ChangelogEntity() {
    }

    // ========== 工厂方法（业务创建） ==========

    /**
     * 创建写入操作日志
     *
     * @param storeId 存储空间ID
     * @param tupleKey 元组键
     * @param zookie Zookie 版本号
     * @return 变更日志实体
     */
    public static ChangelogEntity createWriteLog(String storeId, TupleKey tupleKey, Long zookie) {
        return createLog(storeId, tupleKey, zookie, OPERATION_WRITE);
    }

    /**
     * 创建删除操作日志
     *
     * @param storeId 存储空间ID
     * @param tupleKey 元组键
     * @param zookie Zookie 版本号
     * @return 变更日志实体
     */
    public static ChangelogEntity createDeleteLog(String storeId, TupleKey tupleKey, Long zookie) {
        return createLog(storeId, tupleKey, zookie, OPERATION_DELETE);
    }

    /**
     * 创建变更日志（内部方法）
     */
    private static ChangelogEntity createLog(String storeId, TupleKey tupleKey, Long zookie, String operation) {
        Objects.requireNonNull(storeId, "storeId 不能为空");
        Objects.requireNonNull(operation, "operation 不能为空");

        ChangelogEntity entity = new ChangelogEntity();
        entity.storeId = storeId;
        entity.zookie = zookie != null ? zookie : 0L;
        entity.operation = operation;
        entity.operationTime = System.currentTimeMillis();

        if (tupleKey != null) {
            entity.objectType = tupleKey.getObjectType();
            entity.objectId = tupleKey.getObjectId();
            entity.relation = tupleKey.getRelation();
            entity.subjectType = tupleKey.getSubjectType();
            entity.subjectId = tupleKey.getSubjectId();
            entity.subjectRelation = tupleKey.getSubjectRelation();
        }

        return entity;
    }

    // ========== 重建方法（持久化恢复） ==========

    /**
     * 从持久化数据重建实体（仅供基础设施层使用）
     *
     * @param storeId 存储空间ID
     * @param zookie Zookie 版本号
     * @param operation 操作类型
     * @param objectType 对象类型
     * @param objectId 对象ID
     * @param relation 关系名称
     * @param subjectType 主体类型
     * @param subjectId 主体ID
     * @param subjectRelation 主体关系
     * @param operationTime 操作时间
     * @return 重建后的实体
     */
    public static ChangelogEntity reconstitute(String storeId, Long zookie, String operation,
                                                String objectType, String objectId, String relation,
                                                String subjectType, String subjectId, String subjectRelation,
                                                Long operationTime) {
        ChangelogEntity entity = new ChangelogEntity();
        entity.storeId = storeId;
        entity.zookie = zookie;
        entity.operation = operation;
        entity.objectType = objectType;
        entity.objectId = objectId;
        entity.relation = relation;
        entity.subjectType = subjectType;
        entity.subjectId = subjectId;
        entity.subjectRelation = subjectRelation;
        entity.operationTime = operationTime;
        return entity;
    }

    // ========== 查询方法 ==========

    /**
     * 判断是否为写入操作
     */
    public boolean isWriteOperation() {
        return OPERATION_WRITE.equals(operation);
    }

    /**
     * 判断是否为删除操作
     */
    public boolean isDeleteOperation() {
        return OPERATION_DELETE.equals(operation);
    }
}
