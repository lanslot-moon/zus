package org.kitona.zus.infrastructure.persistence.mysql.repository;

import com.baomidou.mybatisplus.extension.service.IService;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;

import java.util.List;

/**
 * 变更日志持久化仓储接口（基础设施层）
 *
 * <p>
 * 定义基于 PO 的持久化契约，供
 * {@link org.kitona.zus.domain.repository.IChangelogDomainRepository} 的适配器内部使用。
 *
 * @author kitona
 * @version 1.1.0
 * @since 2025-02-06
 */
public interface IChangelogPersistenceRepository extends IService<ChangelogPO> {

    /**
     * 按 Zookie 范围查询变更日志，用于 Watch 增量拉取
     *
     * @param storeId     存储空间 ID
     * @param startZookie 起始 Zookie（不包含）
     * @param endZookie   结束 Zookie（包含），传 null 表示到最新
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogPO> findByZookieRange(String storeId, Long startZookie, Long endZookie, Integer limit);

    /**
     * 查询指定 Zookie 之后的变更，用于 Watch 轮询
     *
     * @param storeId     存储空间 ID
     * @param afterZookie 起始 Zookie（不包含）
     * @param limit       最大返回条数
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogPO> findAfterZookie(String storeId, Long afterZookie, Integer limit);

    /**
     * 获取该存储空间当前最大 Zookie
     *
     * @param storeId 存储空间 ID
     * @return 最大 Zookie，无记录返回 0
     */
    Long getMaxZookie(String storeId);

    /**
     * 清理指定 Zookie 之前的变更日志，用于历史数据归档或限流
     *
     * @param storeId      存储空间 ID
     * @param beforeZookie 清理该 Zookie 之前的记录
     * @return 删除行数
     */
    int cleanupBeforeZookie(String storeId, Long beforeZookie);

    /**
     * 查询最近若干条变更，按 Zookie 降序语义（内部按范围查）
     *
     * @param storeId 存储空间 ID
     * @param limit   最大返回条数
     * @return 变更日志列表
     */
    List<ChangelogPO> findRecentChanges(String storeId, Integer limit);
}
