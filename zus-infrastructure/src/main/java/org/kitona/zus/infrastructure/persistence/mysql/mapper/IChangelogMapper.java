package org.kitona.zus.infrastructure.persistence.mysql.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ChangelogPO;

import java.util.List;

/**
 * 变更日志 Mapper 接口
 *
 * 提供 Changelog 表的数据访问操作。
 * 变更日志用于 Watch API、一致性读取和审计追踪。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Mapper
public interface IChangelogMapper extends BaseMapper<ChangelogPO> {

    int batchInsert(@Param("list") List<ChangelogPO> list);

    /**
     * 根据 Zookie 范围查询变更日志
     *
     * 用于 Watch API，支持增量获取变更。
     *
     * @param storeId     存储空间ID
     * @param startZookie 起始 Zookie（不包含）
     * @param endZookie   结束 Zookie（包含），传 null 表示查询到最新
     * @param limit       最大返回数量
     * @return 变更日志列表，按 Zookie 升序
     */
    List<ChangelogPO> selectByZookieRange(@Param("storeId") String storeId,
                                          @Param("startZookie") Long startZookie,
                                          @Param("endZookie") Long endZookie,
                                          @Param("limit") Integer limit);

    /**
     * 查询指定 Zookie 之后的所有变更
     *
     * 用于 Watch API 的实时监听。
     *
     * @param storeId     存储空间ID
     * @param afterZookie 起始 Zookie（不包含）
     * @param limit       最大返回数量
     * @return 变更日志列表
     */
    List<ChangelogPO> selectAfterZookie(@Param("storeId") String storeId,
                                        @Param("afterZookie") Long afterZookie,
                                        @Param("limit") Integer limit);

    /**
     * 查询存储空间的最新 Zookie
     *
     * @param storeId 存储空间ID
     * @return 最新 Zookie，没有变更记录返回 null
     */
    Long selectMaxZookie(@Param("storeId") String storeId);

    /**
     * 清理过期的变更日志
     *
     * 定期清理历史变更日志，避免数据膨胀。
     *
     * @param storeId      存储空间ID
     * @param beforeZookie 清理此 Zookie 之前的记录
     * @return 删除行数
     */
    int cleanupBeforeZookie(@Param("storeId") String storeId, @Param("beforeZookie") Long beforeZookie);
}
