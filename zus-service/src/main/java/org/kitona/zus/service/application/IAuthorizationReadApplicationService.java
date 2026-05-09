package org.kitona.zus.service.application;

import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;

/**
 * Read / ListObjects / ListSubjects 应用服务接口
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public interface IAuthorizationReadApplicationService {

    /**
     * 读取元组（分页，支持过滤）
     *
     * @param storeId 存储空间ID
     * @param query   查询条件
     * @return 分页结果
     */
    PageResultDTO<TupleResultDTO> read(String storeId, TupleReadQuery query);

    /**
     * 列出主体可访问的对象（type:id 列表）
     *
     * @param query 查询条件
     * @return 对象列表
     */
    ListObjectsResultDTO listObjects(ListObjectsQuery query);

    /**
     * 列出对对象关系具备权限的主体。
     *
     * @param query 查询条件
     * @return 主体列表
     */
    ListSubjectsResultDTO listSubjects(ListSubjectsQuery query);
}
