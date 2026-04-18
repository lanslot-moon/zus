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
}
