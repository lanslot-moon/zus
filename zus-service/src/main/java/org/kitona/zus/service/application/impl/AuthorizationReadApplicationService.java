package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.EvaluationRequest;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.assembler.TupleAssembler;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListUsersQuery;
import org.kitona.zus.service.dto.query.TupleReadQuery;
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
 * <p>RelationTuple 的列表、过滤与反向查询能力由查询仓储承接，
 * 避免命令仓储继续膨胀为通用 DAO。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 * @see org.kitona.zus.domain.authorization.tuple.RelationTuple
 */
@Service
public class AuthorizationReadApplicationService implements IAuthorizationReadApplicationService {

    @Resource
    private ITupleQueryRepository tupleQueryRepository;

    @Resource
    private IStoreQueryRepository storeQueryRepository;

    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    @Resource
    private ICompiledModelCompiler compiledModelCompiler;

    @Resource
    private ICompiledModelCache compiledModelCache;

    @Resource
    private PermissionEvaluator permissionEvaluator;

    @Override
    public PageResultDTO<TupleResultDTO> read(String storeId, TupleReadQuery query) {
        if (StringUtils.isBlank(storeId)) {
            return PageResultDTO.empty();
        }

        TupleReadQuery effectiveQuery = (query != null) ? query : new TupleReadQuery();
        int pageSize = effectiveQuery.getEffectivePageSize();

        List<RelationTuple> tuples = tupleQueryRepository.list(
                TupleQueryCriteria.forPage(
                        storeId,
                        effectiveQuery.getObjectType(),
                        effectiveQuery.getObjectId(),
                        effectiveQuery.getRelation(),
                        effectiveQuery.getSubjectType(),
                        effectiveQuery.getSubjectId(),
                        null,
                        pageSize,
                        effectiveQuery.parsePageTokenAsLong()
                )
        );

        List<TupleResultDTO> tupleDTOs = TupleAssembler.toDTOList(tuples);
        String nextToken = buildNextPageToken(tuples, pageSize);
        return PageResultDTO.of(tupleDTOs, nextToken);
    }

    @Override
    public ListObjectsResultDTO listObjects(ListObjectsQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId());
        EvaluationRequest request = EvaluationRequest.of(
                query.getStoreId(),
                buildSubject(query.getSubjectType(), query.getSubjectId(), query.getSubjectRelation()),
                ObjectRef.of(StringUtils.defaultIfBlank(query.getObjectType(), "object"), "*"),
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken()),
                query.getContext()
        );
        List<String> objects = permissionEvaluator.listObjects(compiledModel, request, query.getObjectType());
        return ListObjectsResultDTO.builder().objects(objects).build();
    }

    @Override
    public ListUsersResultDTO listUsers(ListUsersQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId());
        EvaluationRequest request = EvaluationRequest.of(
                query.getStoreId(),
                Subject.user(StringUtils.defaultIfBlank(query.getSubjectType(), "subject"), "*"),
                ObjectRef.of(query.getObjectType(), query.getObjectId()),
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken()),
                query.getContext()
        );
        List<ListUsersResultDTO.UserDTO> users = permissionEvaluator.listUsers(compiledModel, request).stream()
                .filter(subject -> StringUtils.isBlank(query.getSubjectType())
                        || StringUtils.equals(query.getSubjectType(), subject.getType()))
                .filter(subject -> StringUtils.isBlank(query.getSubjectRelation())
                        || StringUtils.equals(query.getSubjectRelation(), subject.getRelation()))
                .map(subject -> ListUsersResultDTO.UserDTO.builder()
                        .type(subject.getType())
                        .id(subject.getId())
                        .relation(subject.getRelation())
                        .build())
                .toList();
        return ListUsersResultDTO.builder().users(users).build();
    }

    private CompiledAuthorizationModel loadCompiledModel(String storeId) {
        StoreView storeView = storeQueryRepository.findViewByStoreId(storeId).orElse(null);
        if (storeView == null || StringUtils.isBlank(storeView.currentModelId())) {
            throw new IllegalStateException("store 未绑定当前模型: " + storeId);
        }
        return compiledModelCache.get(storeId, storeView.currentModelId())
                .orElseGet(() -> {
                    CompiledAuthorizationModel model = compiledModelCompiler.compile(
                            modelRepository.findByModelId(storeId, storeView.currentModelId())
                                    .orElseThrow(() -> new IllegalStateException("模型不存在: " + storeView.currentModelId())));
                    compiledModelCache.put(storeId, storeView.currentModelId(), model);
                    return model;
                });
    }

    private Subject buildSubject(String subjectType, String subjectId, String subjectRelation) {
        if (StringUtils.isBlank(subjectRelation)) {
            if (Subject.WILDCARD.equals(subjectId)) {
                return Subject.wildcard(subjectType);
            }
            return Subject.user(subjectType, subjectId);
        }
        return Subject.userset(subjectType, subjectId, subjectRelation);
    }

    private String buildNextPageToken(List<RelationTuple> tuples, int pageSize) {
        if (tuples.isEmpty() || tuples.size() < pageSize) {
            return null;
        }
        return String.valueOf(tuples.get(tuples.size() - 1).getId());
    }
}
