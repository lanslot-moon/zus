package org.kitona.zus.domain.authorization.store;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.domain.enums.StoreStatus;

import java.util.Objects;

/**
 * 存储空间聚合根
 *
 * StoreAggregate 是权限数据的逻辑隔离单元，是 FGA 系统的顶层聚合根之一。
 * 它负责维护 store 级别的元数据、当前生效模型指针以及生命周期状态。
 *
 * <p>创建方式（符合 DDD）：
 * <ul>
 *   <li>业务创建：使用 {@link #create(String, String, String)}</li>
 *   <li>持久化重建：使用 {@link #reconstitute(StoreSnapshot)}</li>
 * </ul>
 *
 * 聚合边界：
 * - Store 管理 store 自身的标识、名称、描述与状态
 * - Store 持有当前生效模型的引用（currentModelId）
 *
 * <p>说明：
 * tuple 写入、变更日志记录、zookie 序列生成以及一致性令牌查询
 * 由跨聚合协调流程或读侧查询处理，不作为 Store 聚合内部实体或行为的一部分。
 *
 * 不变量（Invariant）：
 * - storeId 在系统内唯一
 * - currentModelId 必须引用一个可用于鉴权的模型版本
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@ToString
@EqualsAndHashCode
@Getter
public class StoreAggregate {

    /**
     * 数据库主键ID
     */
    private Long id;

    /**
     * 存储空间唯一标识
     */
    private String storeId;

    /**
     * 存储空间名称
     */
    private String name;

    /**
     * 存储空间描述
     */
    private String description;

    /**
     * 当前使用的授权模型ID
     */
    private String currentModelId;

    /**
     * 存储空间状态
     */
    private StoreStatus status;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 私有构造器，禁止外部直接 new，保证只能通过 create() 或 reconstitute() 创建。
     */
    private StoreAggregate() {
    }

    /**
     * 创建新的存储空间（唯一业务创建入口）
     *
     * @param storeId     存储空间ID
     * @param name        名称
     * @param description 描述
     * @return StoreAggregate 实例
     */
    public static StoreAggregate create(String storeId, String name, String description) {
        StoreAggregate store = new StoreAggregate();
        store.storeId = Objects.requireNonNull(storeId, "storeId must not be null");
        store.name = Objects.requireNonNull(name, "name must not be null");
        store.description = description;
        store.status = StoreStatus.NORMAL;
        store.createTime = System.currentTimeMillis();
        return store;
    }

    /**
     * 创建新的存储空间，自动生成存储空间ID
     *
     * @param name        名称
     * @param description 描述
     * @return StoreAggregate 实例
     */
    public static StoreAggregate createWithGeneratedId(String name, String description) {
        String storeId = generateStoreId();
        return create(storeId, name, description);
    }

    /**
     * 从持久化快照重建聚合根，非业务创建入口。
     * 使用 {@link StoreSnapshot} 参数对象，避免方法参数超过 7 个。
     *
     * @param snapshot 持久化快照
     * @return 重建后的聚合根
     */
    public static StoreAggregate reconstitute(StoreSnapshot snapshot) {
        if (snapshot == null) {
            return null;
        }
        StoreAggregate store = new StoreAggregate();
        store.id = snapshot.id();
        store.storeId = snapshot.storeId();
        store.name = snapshot.name();
        store.description = snapshot.description();
        store.currentModelId = snapshot.currentModelId();
        store.status = snapshot.status() != null ? snapshot.status() : StoreStatus.NORMAL;
        store.tenantId = snapshot.tenantId();
        store.createTime = snapshot.createTime();
        return store;
    }


    /**
     * 判断是否处于正常状态
     *
     * @return 正常返回 true
     */
    public boolean isActive() {
        return status == StoreStatus.NORMAL;
    }

    /**
     * 判断是否已配置授权模型
     *
     * @return 已配置返回 true
     */
    public boolean hasModel() {
        return currentModelId != null && !currentModelId.isEmpty();
    }

    /**
     * 更新当前授权模型引用
     *
     * <p>模型是否允许被激活由上层用例先完成校验，
     * 聚合根只负责维护当前指针状态。
     *
     * @param modelId 新的模型ID
     */
    public void updateCurrentModel(String modelId) {
        Objects.requireNonNull(modelId, "modelId must not be null");
        this.currentModelId = modelId;
    }

    /**
     * 禁用存储空间
     *
     * <p>状态转换：NORMAL → DISABLE
     *
     * @throws IllegalStateException 如果当前状态不是 NORMAL
     */
    public void disable() {
        if (status == StoreStatus.DISABLE) {
            return;
        }
        if (status != StoreStatus.NORMAL) {
            throw new SystemException(
                    "Cannot disable store in status: " + status + ", only NORMAL status can be disabled",
                    IError.DATA_STATUS_ERROR.getCode());
        }
        this.status = StoreStatus.DISABLE;
    }

    /**
     * 启用存储空间
     *
     * <p>状态转换：DISABLE → NORMAL
     *
     * @throws IllegalStateException 如果当前状态不是 DISABLE
     */
    public void enable() {
        if (status == StoreStatus.NORMAL) {
            return;
        }
        if (status != StoreStatus.DISABLE) {
            throw new SystemException(
                    "Cannot enable store in status: " + status + ", only DISABLE status can be enabled",
                    IError.DATA_STATUS_ERROR.getCode());
        }
        this.status = StoreStatus.NORMAL;
    }

    /**
     * 检查是否可以删除
     *
     * <p>只有 DISABLE 状态的 Store 才能被删除，确保删除前用户已确认停用。
     *
     * @throws IllegalStateException 如果当前状态不是 DISABLE
     */
    public void checkDeletable() {
        if (status != StoreStatus.DISABLE) {
            throw new SystemException(
                    "Cannot delete store in status: " + status + ", only DISABLE status can be deleted",
                    IError.DATA_STATUS_ERROR.getCode());
        }
    }

    /**
     * 获取状态码（用于持久化等场景）
     *
     * @return 状态码
     */
    public Integer getStatusCode() {
        return status != null ? status.getCode() : null;
    }

    // ========== ID 生成方法 ==========

    /**
     * 生成存储空间ID（领域层 ID 生成策略）
     *
     * <p>格式：store-{uuid32}，如 store-a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6
     *
     * @return 存储空间ID
     */
    private static String generateStoreId() {
        String uuid = java.util.UUID.randomUUID().toString().replace("-", "");
        return "store-" + uuid;
    }
}
