package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 变更日志持久化对象
 * 
 * Changelog 记录所有元组的写入和删除操作，用于：
 * - Zookie 一致性读取：确保读取操作能看到指定版本之前的所有变更
 * - Watch API：支持实时监听权限变更
 * - 审计追踪：记录权限变更历史
 * - 数据同步：支持增量同步到其他系统
 * 
 * 每条变更日志包含：
 * - 操作类型：WRITE（写入）或 DELETE（删除）
 * - 元组键：object + relation + subject 的组合
 * - Zookie：该操作的版本号
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_changelog")
@Data
@Accessors(chain = true)
public class ChangelogPO extends BaseIdPO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     * 关联 fga_store 表
     */
    private String storeId;

    /**
     * Zookie 版本号
     * 该变更操作的版本号，全局递增
     * 用于实现一致性读取和 Watch 功能
     */
    private Long zookie;

    /**
     * 操作类型
     * WRITE: 写入元组
     * DELETE: 删除元组
     */
    private String operation;

    /**
     * 资源对象类型
     */
    private String objectType;

    /**
     * 资源对象ID
     */
    private String objectId;

    /**
     * 关系名称
     */
    private String relation;

    /**
     * 主体类型
     */
    private String subjectType;

    /**
     * 主体ID
     */
    private String subjectId;

    /**
     * 主体关系（用于用户集）
     */
    private String subjectRelation;

    /**
     * 操作时间戳（毫秒）
     * 精确记录操作发生的时间
     */
    private Long operationTime;

}
