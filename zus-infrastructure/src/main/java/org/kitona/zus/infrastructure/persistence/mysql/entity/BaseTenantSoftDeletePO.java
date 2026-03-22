package org.kitona.zus.infrastructure.persistence.mysql.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 带租户字段和逻辑删除标记的持久化基类。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseTenantSoftDeletePO extends BaseSoftDeletePO {

    private String tenantId;
}
