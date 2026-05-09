package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.authorization.evaluation.compiled.CompiledAuthorizationModel;
import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.port.ICompiledModelCache;
import org.kitona.zus.domain.port.ICompiledModelCompiler;
import org.kitona.zus.domain.read.criteria.TupleQueryCriteria;
import org.kitona.zus.domain.read.view.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.repository.ITupleQueryRepository;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Read / ListObjects / ListSubjects 应用服务
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
 * @see RelationTuple
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
    private PermissionSearchEvaluator permissionSearchEvaluator;

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

        List<RelationTuple> tuples = tupleQueryRepository.list(criteria);
        List<TupleResultDTO> tupleDTOs = TupleAssembler.toDTOList(tuples);
        String nextToken = buildNextPageToken(tuples, pageSize);
        return PageResultDTO.of(tupleDTOs, nextToken);
    }

    @Override
    public ListObjectsResultDTO listObjects(ListObjectsQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId());

        Subject subject = buildSubject(query.getSubjectType(), query.getSubjectId(), query.getSubjectRelation());
        List<String> objects = permissionSearchEvaluator.listObjects(
                compiledModel,
                query.getStoreId(),
                subject,
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken()),
                query.getContext(),
                query.getObjectType()
        );
        return ListObjectsResultDTO.builder().objects(objects).build();
    }

    @Override
    public ListSubjectsResultDTO listSubjects(ListSubjectsQuery query) {
        ValidationUtil.validate(query);
        CompiledAuthorizationModel compiledModel = loadCompiledModel(query.getStoreId());

        List<ListSubjectsResultDTO.SubjectDTO> subjects = permissionSearchEvaluator.listSubjects(
                compiledModel,
                query.getStoreId(),
                ObjectRef.of(query.getObjectType(), query.getObjectId()),
                query.getRelation(),
                Zookie.parse(query.getConsistencyToken()),
                query.getContext()
        ).stream()
                .filter(subject -> StringUtils.isBlank(query.getSubjectType())
                        || Objects.equals(query.getSubjectType(), subject.getType()))
                .filter(subject -> StringUtils.isBlank(query.getSubjectRelation())
                        || Objects.equals(query.getSubjectRelation(), subject.getRelation()))
                .map(subject -> ListSubjectsResultDTO.SubjectDTO.builder()
                        .type(subject.getType())
                        .id(subject.getId())
                        .relation(subject.getRelation())
                        .build())
                .toList();
        return ListSubjectsResultDTO.builder().subjects(subjects).build();
    }

    private CompiledAuthorizationModel loadCompiledModel(String storeId) {
        StoreView storeView = storeQueryRepository.findViewByStoreId(storeId).orElse(null);
        if (storeView == null || StringUtils.isBlank(storeView.currentModelId())) {
            throw new IllegalStateException("store 未绑定当前模型: " + storeId);
        }

        Optional<CompiledAuthorizationModel> modelOptional = compiledModelCache.get(storeId, storeView.currentModelId());
        if (modelOptional.isPresent()) {
            return modelOptional.get();
        }

        Optional<AuthorizationModelAggregate> optional = modelRepository.findByModelId(storeId, storeView.currentModelId());
        if (optional.isEmpty()) {
            throw new IllegalStateException("模型不存在: " + storeView.currentModelId());
        }

        CompiledAuthorizationModel model = compiledModelCompiler.compile(optional.get());
        compiledModelCache.put(storeId, storeView.currentModelId(), model);
        return model;
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
