package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.service.application.IReadApplicationService;
import org.kitona.zus.service.assembler.TupleAssembler;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.query.ReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListUsersResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Read / ListObjects / ListUsers 应用服务
 *
 * <p>通过 ITupleQueryRepository 实现元组读取与列表查询。
 * <p>职责：编排查询流程，将结果转换为 DTO 返回。
 *
 * <h3>CQRS 读模型说明</h3>
 * <p>本服务遵循 CQRS（命令查询职责分离）模式，专注于<b>查询职责</b>。
 * 与写操作不同，查询服务直接通过查询仓储获取结果，原因如下：
 * <ul>
 *   <li>查询操作是只读的，不涉及状态变更，无需聚合根保证一致性</li>
 *   <li>查询通常需要跨聚合的数据组合，绕过聚合根可避免不必要的对象加载</li>
 *   <li>性能考量：直接查询 Repository 避免了加载完整聚合的开销</li>
 * </ul>
 *
 * <p>RelationTupleEntity 的列表、过滤与反向查询能力由查询仓储承接，
 * 避免命令仓储继续膨胀为通用 DAO。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 * @see org.kitona.zus.domain.entity.RelationTupleEntity
 */
@Service
public class ReadApplicationService implements IReadApplicationService {

    @Resource
    private ITupleQueryRepository tupleQueryRepository;

    @Override
    public PageResultDTO<TupleResultDTO> read(String storeId, ReadQuery query) {
        if (StringUtils.isBlank(storeId)) {
            return PageResultDTO.empty();
        }

        ReadQuery effectiveQuery = (query != null) ? query : new ReadQuery();
        int pageSize = effectiveQuery.getEffectivePageSize();

        List<RelationTupleEntity> tuples = tupleQueryRepository.listTuplesWithFilter(
                storeId,
                effectiveQuery.getObjectType(),
                effectiveQuery.getObjectId(),
                effectiveQuery.getRelation(),
                effectiveQuery.getSubjectType(),
                effectiveQuery.getSubjectId(),
                null,
                pageSize,
                effectiveQuery.parsePageTokenAsLong()
        );

        List<TupleResultDTO> tupleDTOs = TupleAssembler.toDTOList(tuples);
        String nextToken = buildNextPageToken(tuples, pageSize);
        return PageResultDTO.of(tupleDTOs, nextToken);
    }

    @Override
    public ListObjectsResultDTO listObjects(ListObjectsQuery query) {
        ValidationUtil.validate(query);

        List<RelationTupleEntity> tuples = tupleQueryRepository.findBySubject(
                query.getStoreId(),
                query.getSubjectType(),
                query.getSubjectId(),
                query.getSubjectRelation(),
                query.getObjectType(),
                query.getRelation(),
                null
        );

        List<String> objects = TupleAssembler.toDistinctObjectRefs(tuples);
        return ListObjectsResultDTO.builder().objects(objects).build();
    }

    @Override
    public ListUsersResultDTO listUsers(ListUsersQuery query) {
        ValidationUtil.validate(query);

        List<RelationTupleEntity> tuples = tupleQueryRepository.findByObject(
                query.getStoreId(),
                query.getObjectType(),
                query.getObjectId(),
                query.getRelation(),
                null
        );

        List<ListUsersResultDTO.UserDTO> users = TupleAssembler.toDistinctUserDTOList(tuples);
        return ListUsersResultDTO.builder().users(users).build();
    }



    private String buildNextPageToken(List<RelationTupleEntity> tuples, int pageSize) {
        if (tuples.isEmpty() || tuples.size() < pageSize) {
            return null;
        }
        return String.valueOf(tuples.get(tuples.size() - 1).getId());
    }
}
