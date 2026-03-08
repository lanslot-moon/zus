package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.query.ReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;

/**
 * Read / ListObjects / ListUsers 应用服务接口
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IReadApplicationService {

    /**
     * 读取元组（分页，支持过滤）
     *
     * @param storeId 存储空间ID
     * @param query   查询条件
     * @return 分页结果
     */
    PageResultDTO<TupleResultDTO> read(String storeId, ReadQuery query);

    /**
     * 列出主体可访问的对象（type:id 列表）
     *
     * @param query 查询条件
     * @return 对象列表
     */
    ListObjectsResultDTO listObjects(ListObjectsQuery query);

    /**
     * 列出对资源有权限的用户
     *
     * @param query 查询条件
     * @return 用户列表
     */
    ListUsersResultDTO listUsers(ListUsersQuery query);
}
