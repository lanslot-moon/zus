package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 存储空间持久化对象
 * 
 * Store 是权限数据的逻辑隔离单元，类似于数据库的 Schema 或租户概念。
 * 该 PO 是持久化与读侧查询载体，不等同于领域聚合本身。
 * 
 * 使用场景：
 * - 多租户隔离：每个租户一个 Store
 * - 环境隔离：dev/staging/prod 各一个 Store
 * - 业务隔离：不同业务系统各一个 Store
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_store")
@Data
@Accessors(chain = true)
public class StorePO extends BaseTenantTrackableSoftDeletePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间唯一标识
     * 由系统生成的 ULID 或用户指定的业务标识
     * 示例: "store-company-abc", "01HVMCC2X5HQJT1P5PZ3XJWQ1M"
     */
    private String storeId;

    /**
     * 存储空间名称
     * 用于显示的友好名称
     * 示例: "公司ABC的权限空间"
     */
    private String name;

    /**
     * 存储空间描述
     * 详细说明该存储空间的用途
     */
    private String description;

    /**
     * 当前使用的授权模型ID
     * 关联 fga_authorization_model 表的 model_id
     * 为空表示尚未配置授权模型
     */
    private String currentModelId;

    /**
     * 当前最新的 Zookie 版本号
     * 作为持久化与读侧查询字段保存，用于一致性令牌读取。
     * 该字段不再映射为 Store 聚合内部状态。
     */
    private Long currentZookie;

    /**
     * 存储空间状态
     * 0: 正常, 1: 禁用, 2: 删除中
     */
    private Integer status;
}
