package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleKeyQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.TupleSubjectQuery;
import org.kitona.zus.infrastructure.persistence.mysql.entity.query.WildcardTupleExistsQuery;
import org.kitona.zus.infrastructure.persistence.mysql.mapper.ITupleMapper;
import org.kitona.zus.infrastructure.persistence.mysql.repository.ITuplePersistenceRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 关系元组持久化仓储实现（基础设施层）
 *
 * <p>
 * 简单查询使用 MyBatis Plus LambdaQueryWrapper，
 * 复杂查询（如带条件过滤、批量操作等）使用 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Repository
public class TuplePersistenceRepository extends BaseRepository<RelationTuplePO> implements ITuplePersistenceRepository {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;
    private static final String LIMIT_CLAUSE_PREFIX = "LIMIT ";

    @Resource
    private ITupleMapper tupleMapper;

    @Resource
    private FgaCacheManager cacheManager;

    /**
     * 判断精确关系元组是否存在。
     *
     * @param query 查询条件
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean existsTuple(TupleExistsQuery query) {
        if (query == null) {
            return false;
        }
        // 由于 tuple 支持 expiresAt 动态到期，持久层存在性查询不走缓存，避免时间推移带来的脏读。
        long count = this.count(buildLambdaQueryWrapper(query));
        return count > 0;
    }

    /**
     * 构建 MyBatis Plus 查询条件。
     *
     * @param query 查询条件
     * @return 构建结果
     */
    private LambdaQueryWrapper<RelationTuplePO> buildLambdaQueryWrapper(TupleExistsQuery query) {
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper();
        wrapper.eq(RelationTuplePO::getStoreId, query.getStoreId())
                .eq(RelationTuplePO::getObjectType, query.getObjectType())
                .eq(RelationTuplePO::getObjectId, query.getObjectId())
                .eq(RelationTuplePO::getRelation, query.getRelation())
                .eq(RelationTuplePO::getSubjectType, query.getSubjectType())
                .eq(RelationTuplePO::getSubjectId, query.getSubjectId())
                .le(Objects.nonNull(query.getMaxZookie()), RelationTuplePO::getZookie, query.getMaxZookie());
        applyExactSubjectRelationCondition(wrapper, query.getSubjectRelation());
        applyActiveTupleCondition(wrapper, currentTimestamp());
        return wrapper;
    }

    /**
     * 判断通配符关系元组是否存在。
     *
     * @param query 查询条件
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    public boolean existsWildcardTuple(WildcardTupleExistsQuery query) {
        TupleExistsQuery existsQuery = new TupleExistsQuery();
        existsQuery.setStoreId(query.getStoreId());
        existsQuery.setObjectType(query.getObjectType());
        existsQuery.setObjectId(query.getObjectId());
        existsQuery.setRelation(query.getRelation());
        existsQuery.setSubjectType(query.getSubjectType());
        existsQuery.setMaxZookie(query.getMaxZookie());
        existsQuery.setSubjectId("*");
        existsQuery.setSubjectRelation(null);
        return existsTuple(existsQuery);
    }

    /**
     * 按对象和关系查询有效 tuple。
     *
     * @param storeId    Store 标识
     * @param objectType 对象类型
     * @param objectId   对象标识
     * @param relation   关系名
     * @param maxZookie  最大 zookie 版本
     * @return tuple 持久化记录列表
     */
    @Override
    public List<RelationTuplePO> findByObjectAndRelation(String storeId, String objectType,
            String objectId, String relation, Long maxZookie) {
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, storeId)
                .eq(RelationTuplePO::getObjectType, objectType)
                .eq(RelationTuplePO::getObjectId, objectId)
                .eq(RelationTuplePO::getRelation, relation)
                .le(Objects.nonNull(maxZookie), RelationTuplePO::getZookie, maxZookie);
        applyActiveTupleCondition(wrapper, currentTimestamp());
        List<RelationTuplePO> tuples = this.list(wrapper);
        return tuples != null ? tuples : Collections.emptyList();
    }

    /**
     * 按主体查询关系元组。
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public List<RelationTuplePO> findBySubject(TupleSubjectQuery query) {
        if (query == null) {
            return Collections.emptyList();
        }
        List<RelationTuplePO> tuples = this.list(buildLambdaQueryWrapper(query));
        return CollectionUtils.isEmpty(tuples) ? Collections.emptyList() : tuples;
    }

    /**
     * 构建 MyBatis Plus 查询条件。
     *
     * @param query 查询条件
     * @return 构建结果
     */
    private LambdaQueryWrapper<RelationTuplePO> buildLambdaQueryWrapper(TupleSubjectQuery query) {
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, query.getStoreId())
                .eq(RelationTuplePO::getObjectType, query.getObjectType())
                .eq(RelationTuplePO::getRelation, query.getRelation())
                .eq(RelationTuplePO::getSubjectType, query.getSubjectType())
                .eq(RelationTuplePO::getSubjectId, query.getSubjectId())
                .le(Objects.nonNull(query.getMaxZookie()), RelationTuplePO::getZookie, query.getMaxZookie());
        applyExactSubjectRelationCondition(wrapper, query.getSubjectRelation());
        applyActiveTupleCondition(wrapper, currentTimestamp());
        return wrapper;
    }

    /**
     * 按对象条件查询有效 tuple。
     *
     * @param storeId    Store 标识
     * @param objectType 对象类型
     * @param objectId   对象标识
     * @param relation   关系名
     * @param maxZookie  最大 zookie 版本
     * @return tuple 持久化记录列表
     */
    @Override
    public List<RelationTuplePO> findByObject(String storeId, String objectType, String objectId,
            String relation, Long maxZookie) {

        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), RelationTuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), RelationTuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(objectId), RelationTuplePO::getObjectId, objectId)
                .eq(StringUtils.isNotBlank(relation), RelationTuplePO::getRelation, relation)
                .le(Objects.nonNull(maxZookie), RelationTuplePO::getZookie, maxZookie);
        applyActiveTupleCondition(wrapper, currentTimestamp());

        List<RelationTuplePO> tuples = super.list(wrapper);

        return CollectionUtils.isEmpty(tuples) ? Collections.emptyList() : tuples;
    }

    /**
     * 按 tuple key 查询关系元组。
     *
     * @param query 查询条件
     * @return 查询结果
     */
    @Override
    public Optional<RelationTuplePO> findByTupleKey(TupleKeyQuery query) {
        if (query == null) {
            return Optional.empty();
        }
        // 简单精确查询使用 MyBatis Plus
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, query.getStoreId())
                .eq(RelationTuplePO::getObjectType, query.getObjectType())
                .eq(RelationTuplePO::getObjectId, query.getObjectId())
                .eq(RelationTuplePO::getRelation, query.getRelation())
                .eq(RelationTuplePO::getSubjectType, query.getSubjectType())
                .eq(RelationTuplePO::getSubjectId, query.getSubjectId());
        applyExactSubjectRelationCondition(wrapper, query.getSubjectRelation());
        applyActiveTupleCondition(wrapper, currentTimestamp());

        return Optional.ofNullable(this.getOne(wrapper));
    }

    /**
     * 批量创建持久化记录。
     *
     * @param tuples 关系元组列表
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreate(List<RelationTuplePO> tuples) {
        if (tuples == null || tuples.isEmpty()) {
            return false;
        }

        boolean result = tupleMapper.batchInsert(tuples) > 0;
        if (!result) {
            return false;
        }

        tuples.forEach(tuple -> cacheManager.invalidateTupleCache(tuple.getStoreId(), tuple.getObjectType(),
                tuple.getObjectId()));
        cacheManager.invalidateCheckCache(tuples.get(0).getStoreId());
        return true;
    }

    /**
     * 批量逻辑删除持久化记录。
     *
     * @param storeId Store 标识
     * @param ids ids 参数
     * @return 满足条件返回 true，否则返回 false
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(String storeId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        LambdaUpdateWrapper<RelationTuplePO> wrapper = new LambdaUpdateWrapper<RelationTuplePO>()
                .set(RelationTuplePO::getIsDeleted, DeletedStatusEnum.DELETED.getCode())
                .in(RelationTuplePO::getId, ids).eq(RelationTuplePO::getStoreId, storeId);

        boolean result = super.update(wrapper);
        if (!result) {
            return false;
        }

        cacheManager.invalidateCheckCache(storeId);
        return true;
    }

    /**
     * 分页查询 tuple 持久化记录。
     *
     * @param storeId    Store 标识
     * @param objectType 对象类型
     * @param relation   关系名
     * @param pageSize   分页大小
     * @param pageToken  分页游标
     * @return tuple 持久化记录列表
     */
    @Override
    public List<RelationTuplePO> listTuples(String storeId, String objectType, String relation,
            int pageSize, Long pageToken) {
        // 分页查询使用 MyBatis Plus
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), RelationTuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(relation), RelationTuplePO::getRelation, relation)
                .gt(pageToken != null, RelationTuplePO::getId, pageToken)
                .orderByAsc(RelationTuplePO::getId)
                .last(LIMIT_CLAUSE_PREFIX + resolvePageSize(pageSize));
        applyActiveTupleCondition(wrapper, currentTimestamp());

        return this.list(wrapper);
    }

    /**
     * 批量按 tuple key 查询关系元组。
     *
     * @param storeId Store 标识
     * @param tupleKeyQueries tupleKeyQueries 参数
     * @return 查询结果
     */
    @Override
    public List<RelationTuplePO> findByTupleKeys(String storeId, List<TupleKeyQuery> tupleKeyQueries) {
        if (tupleKeyQueries == null || tupleKeyQueries.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用 OR 条件组合多个 TupleKey 查询，避免 N+1 问题
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, storeId);
        applyActiveTupleCondition(wrapper, currentTimestamp());

        wrapper.and(w -> {
            for (int i = 0; i < tupleKeyQueries.size(); i++) {
                TupleKeyQuery query = tupleKeyQueries.get(i);
                if (i == 0) {
                    w.and(inner -> buildTupleKeyCondition(inner, query));
                } else {
                    w.or(inner -> buildTupleKeyCondition(inner, query));
                }
            }
        });

        return this.list(wrapper);
    }

    /**
     * 构建 tuple key 查询条件。
     *
     * @param wrapper wrapper 参数
     * @param query 查询条件
     */
    private void buildTupleKeyCondition(LambdaQueryWrapper<RelationTuplePO> wrapper, TupleKeyQuery query) {
        wrapper.eq(RelationTuplePO::getObjectType, query.getObjectType())
                .eq(RelationTuplePO::getObjectId, query.getObjectId())
                .eq(RelationTuplePO::getRelation, query.getRelation())
                .eq(RelationTuplePO::getSubjectType, query.getSubjectType())
                .eq(RelationTuplePO::getSubjectId, query.getSubjectId());
        applyExactSubjectRelationCondition(wrapper, query.getSubjectRelation());
    }

    /**
     * 按完整过滤条件分页查询 tuple 持久化记录。
     *
     * @param storeId         Store 标识
     * @param objectType      对象类型
     * @param objectId        对象标识
     * @param relation        关系名
     * @param subjectType     主体类型
     * @param subjectId       主体标识
     * @param subjectRelation 主体关系
     * @param pageSize        分页大小
     * @param pageToken       分页游标
     * @param maxZookie       最大 zookie 版本
     * @return tuple 持久化记录列表
     */
    @Override
    public List<RelationTuplePO> listTuplesWithFilter(String storeId, String objectType, String objectId,
            String relation, String subjectType, String subjectId,
            String subjectRelation, int pageSize, Long pageToken, Long maxZookie) {
        LambdaQueryWrapper<RelationTuplePO> wrapper = getLambdaQueryWrapper()
                .eq(RelationTuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), RelationTuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(objectId), RelationTuplePO::getObjectId, objectId)
                .eq(StringUtils.isNotBlank(relation), RelationTuplePO::getRelation, relation)
                .eq(StringUtils.isNotBlank(subjectType), RelationTuplePO::getSubjectType, subjectType)
                .eq(StringUtils.isNotBlank(subjectId), RelationTuplePO::getSubjectId, subjectId)
                .eq(StringUtils.isNotBlank(subjectRelation), RelationTuplePO::getSubjectRelation, subjectRelation)
                .le(Objects.nonNull(maxZookie), RelationTuplePO::getZookie, maxZookie)
                .gt(pageToken != null, RelationTuplePO::getId, pageToken)
                .orderByAsc(RelationTuplePO::getId)
                .last(LIMIT_CLAUSE_PREFIX + resolvePageSize(pageSize));
        applyActiveTupleCondition(wrapper, currentTimestamp());

        return this.list(wrapper);
    }

    /**
     * 应用精确 subject relation 查询条件。
     *
     * @param wrapper wrapper 参数
     * @param subjectRelation 主体关系
     */
    private void applyExactSubjectRelationCondition(LambdaQueryWrapper<RelationTuplePO> wrapper, String subjectRelation) {
        if (StringUtils.isNotBlank(subjectRelation)) {
            wrapper.eq(RelationTuplePO::getSubjectRelation, subjectRelation);
            return;
        }
        wrapper.eq(RelationTuplePO::getSubjectRelation, "");
    }

    /**
     * 应用未过期 tuple 查询条件。
     *
     * @param wrapper wrapper 参数
     * @param currentTimestamp currentTimestamp 参数
     */
    private void applyActiveTupleCondition(LambdaQueryWrapper<RelationTuplePO> wrapper, long currentTimestamp) {
        wrapper.and(condition -> condition.isNull(RelationTuplePO::getExpiresAt)
                .or()
                .gt(RelationTuplePO::getExpiresAt, currentTimestamp));
    }

    /**
     * 读取current timestamp。
     * @return 返回结果
     */
    private long currentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 解析安全分页大小。
     *
     * @param pageSize 分页大小
     * @return 构建结果
     */
    private int resolvePageSize(int pageSize) {
        if (pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
