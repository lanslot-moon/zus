package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListObjectsEvaluationRequest;
import org.kitona.zus.domain.authorization.evaluation.runtime.ListSubjectsEvaluationRequest;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.AuthorizationModelId;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.read.port.IStoreQueryPort;
import org.kitona.zus.domain.read.port.ITupleQueryPort;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.read.view.TupleView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.service.PermissionSearchEvaluator;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.conv.assembler.TupleAssembler;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.ListObjectsResultDTO;
import org.kitona.zus.service.dto.response.ListSubjectsResultDTO;
import org.kitona.zus.service.dto.response.PageResultDTO;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.kitona.zus.service.port.ICompiledModelCache;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Read / ListObjects / ListSubjects 应用服务
 *
 * <p>通过 ITupleQueryPort 实现元组读取与列表查询。
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
 * <p>TupleView 的列表、过滤与反向查询能力由读侧端口承接，
 * 避免命令仓储继续膨胀为通用 DAO。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Service
public class AuthorizationReadApplicationService implements IAuthorizationReadApplicationService {

    @Resource
    private ITupleQueryPort tupleQueryRepository;

    @Resource
    private IStoreQueryPort storeQueryRepository;

    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    @Resource
    private ICompiledModelCompiler compiledModelCompiler;

    @Resource
    private ICompiledModelCache compiledModelCache;

    @Resource
    private PermissionSearchEvaluator permissionSearchEvaluator;

    /**
     * 读取关系元组列表。
     *
     * @param storeId Store 标识
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public PageResultDTO<TupleResultDTO> read(String storeId, TupleReadQuery query) {
        if (StringUtils.isBlank(storeId)) {
            return PageResultDTO.empty();
        }

        TupleReadQuery effectiveQuery = (query != null) ? query : new TupleReadQuery();
        int pageSize = effectiveQuery.getEffectivePageSize();

        TupleQueryCriteria criteria = TupleQueryCriteria.forPage(
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

        List<TupleView> tuples = tupleQueryRepository.list(criteria);
        List<TupleResultDTO> tupleDTOs = TupleAssembler.toDTOList(tuples);
        String nextToken = buildNextPageToken(tuples, pageSize);
        return PageResultDTO.of(tupleDTOs, nextToken);
    }

    /**
     * 查询主体可访问的对象列表。
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public ListObjectsResultDTO listObjects(ListObjectsQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId(), query.getAuthorizationModelId());

        ListObjectsEvaluationRequest evaluationRequest = ListObjectsEvaluationRequest.of(
                query.getStoreId(),
                query.getSubjectType(),
                query.getSubjectId(),
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken())
        ).withContext(query.getContext()).withSubjectRelation(query.getSubjectRelation()).withObjectType(query.getObjectType());

        List<ObjectRef> objects = permissionSearchEvaluator.listObjects(compiledModel, evaluationRequest);
        List<String> stringList = objects.stream().map(ObjectRef::toString).toList();
        return ListObjectsResultDTO.builder().objects(stringList).build();
    }

    /**
     * 查询对对象关系具备权限的主体列表。
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public ListSubjectsResultDTO listSubjects(ListSubjectsQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId(), query.getAuthorizationModelId());
        ListSubjectsEvaluationRequest evaluationRequest = ListSubjectsEvaluationRequest.of(
                query.getStoreId(),
                ObjectRef.of(query.getObjectType(), query.getObjectId()),
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken())
        ).withContext(query.getContext()).withSubjectType(query.getSubjectType()).withSubjectRelation(query.getSubjectRelation());

        Function<Subject, ListSubjectsResultDTO.SubjectDTO> function = subject -> ListSubjectsResultDTO.SubjectDTO.builder()
                .type(subject.getType())
                .id(subject.getId())
                .relation(subject.getRelation())
                .build();

        List<Subject> listedSubjects = permissionSearchEvaluator.listSubjects(compiledModel, evaluationRequest);
        List<ListSubjectsResultDTO.SubjectDTO> subjects = listedSubjects.stream().map(function).toList();
        return ListSubjectsResultDTO.builder().subjects(subjects).build();
    }

    /**
     * 加载load compiled model。
     *
     * @param storeId Store 标识
     * @param authorizationModelId authorizationModelId 参数
     * @return 查询结果
     */
    private CompiledAuthorizationModel loadCompiledModel(String storeId, String authorizationModelId) {
        StoreView storeView = storeQueryRepository.findViewByStoreId(storeId).orElse(null);
        String modelId = resolveModelId(storeId, authorizationModelId, storeView);

        Optional<CompiledAuthorizationModel> modelOptional = compiledModelCache.get(storeId, modelId);
        if (modelOptional.isPresent()) {
            return modelOptional.get();
        }

        Optional<AuthorizationModelAggregate> optional = modelRepository.findById(AuthorizationModelId.of(storeId, modelId));
        if (optional.isEmpty()) {
            throw new IllegalStateException("模型不存在: " + modelId);
        }

        CompiledAuthorizationModel model = compiledModelCompiler.compile(optional.get());
        compiledModelCache.put(storeId, modelId, model);
        return model;
    }

    /**
     * 解析本次读取使用的授权模型 ID。
     *
     * @param storeId Store 标识
     * @param authorizationModelId authorizationModelId 参数
     * @param storeView storeView 参数
     * @return 构建结果
     */
    private String resolveModelId(String storeId, String authorizationModelId, StoreView storeView) {
        if (storeView == null) {
            throw new IllegalStateException("store 不存在: " + storeId);
        }
        if (StringUtils.isNotBlank(authorizationModelId)) {
            return authorizationModelId;
        }
        if (StringUtils.isBlank(storeView.currentModelId())) {
            throw new IllegalStateException("未指定授权模型且 store 未绑定当前模型: " + storeId);
        }
        return storeView.currentModelId();
    }

    /**
     * 根据当前页结果构建下一页游标。
     *
     * @param tuples 关系元组列表
     * @param pageSize 分页大小
     * @return 构建结果
     */
    private String buildNextPageToken(List<TupleView> tuples, int pageSize) {
        if (tuples.isEmpty() || tuples.size() < pageSize) {
            return null;
        }
        return String.valueOf(tuples.get(tuples.size() - 1).id());
    }
}
