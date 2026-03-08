package org.kitona.zus.infrastructure.persistence.mysql.repository.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.common.utils.MapstructUtil;
import org.kitona.zus.infrastructure.cache.FgaCacheManager;
import org.kitona.zus.infrastructure.cache.query.TupleExistsCacheQuery;
import org.kitona.zus.infrastructure.enums.DeletedStatusEnum;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TuplePO;
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
 * <p>简单查询使用 MyBatis Plus LambdaQueryWrapper，
 * 复杂查询（如带条件过滤、批量操作等）使用 XML 映射。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Repository
public class TuplePersistenceRepository extends BaseRepository<TuplePO> implements ITuplePersistenceRepository {

    @Resource
    private ITupleMapper tupleMapper;

    @Resource
    private FgaCacheManager cacheManager;

    @Override
    public boolean existsTuple(TupleExistsQuery query) {
        if (query == null) {
            return false;
        }
        Long maxZookie = query.getMaxZookie();
        TupleExistsCacheQuery existsCacheQuery = MapstructUtil.convert(query, TupleExistsCacheQuery.class);
        if (maxZookie != null) {
            long count = this.count(buildLambdaQueryWrapper(query));
            return count > 0;
        }
        // 无 Zookie 限制时先查缓存
        Boolean cached = cacheManager.getCheckResult(existsCacheQuery);
        if (cached != null) {
            return cached;
        }
        long count = this.count(buildLambdaQueryWrapper(query));
        cacheManager.setCheckResult(existsCacheQuery, count > 0);
        return count > 0;
    }

    private LambdaQueryWrapper<TuplePO> buildLambdaQueryWrapper(TupleExistsQuery query) {
        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper();
        wrapper.eq(TuplePO::getStoreId, query.getStoreId())
                .eq(TuplePO::getObjectType, query.getObjectType())
                .eq(TuplePO::getObjectId, query.getObjectId())
                .eq(TuplePO::getRelation, query.getRelation())
                .eq(TuplePO::getSubjectType, query.getSubjectType())
                .eq(TuplePO::getSubjectId, query.getSubjectId())
                .eq(StringUtils.isBlank(query.getSubjectRelation()), TuplePO::getSubjectRelation, query.getSubjectRelation())
                .le(Objects.nonNull(query.getMaxZookie()), TuplePO::getZookie, query.getMaxZookie());
        return wrapper;
    }

    @Override
    public boolean existsWildcardTuple(WildcardTupleExistsQuery query) {
        TupleExistsQuery existsQuery = MapstructUtil.convert(query, TupleExistsQuery.class);
        if (existsQuery == null) {
            return false;
        }
        existsQuery.setSubjectId("*");
        existsQuery.setSubjectRelation(null);
        return existsTuple(existsQuery);
    }

    @Override
    public List<TuplePO> findByObjectAndRelation(String storeId, String objectType,
                                                 String objectId, String relation, Long maxZookie) {
        List<TuplePO> tuples;
        if (maxZookie != null) {
            tuples = tupleMapper.selectByObjectAndRelation(storeId, objectType, objectId, relation, maxZookie);
            return CollectionUtils.isEmpty(tuples) ? Collections.emptyList() : tuples;
        }

        // 无 Zookie 限制时先查缓存
        tuples = cacheManager.getTupleQuery(storeId, objectType, objectId, relation);
        if (tuples != null) {
            return tuples;
        }

        tuples = tupleMapper.selectByObjectAndRelation(storeId, objectType, objectId, relation, null);
        if (tuples != null) {
            cacheManager.setTupleQuery(storeId, objectType, objectId, relation, tuples);
        }
        return tuples != null ? tuples : Collections.emptyList();
    }

    @Override
    public List<TuplePO> findBySubject(TupleSubjectQuery query) {
        if (query == null) {
            return Collections.emptyList();
        }
        List<TuplePO> tuples = this.list(buildLambdaQueryWrapper(query));
        return CollectionUtils.isEmpty(tuples) ? Collections.emptyList() : tuples;
    }

    private LambdaQueryWrapper<TuplePO> buildLambdaQueryWrapper(TupleSubjectQuery query) {
        return getLambdaQueryWrapper()
                .eq(TuplePO::getStoreId, query.getStoreId())
                .eq(TuplePO::getObjectType, query.getObjectType())
                .eq(TuplePO::getRelation, query.getRelation())
                .eq(TuplePO::getSubjectType, query.getSubjectType())
                .eq(TuplePO::getSubjectId, query.getSubjectId())
                .eq(StringUtils.isBlank(query.getSubjectRelation()), TuplePO::getSubjectRelation, query.getSubjectRelation())
                .le(Objects.nonNull(query.getMaxZookie()), TuplePO::getZookie, query.getMaxZookie());
    }

    @Override
    public List<TuplePO> findByObject(String storeId, String objectType, String objectId,
                                      String relation, Long maxZookie) {

        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper()
                .eq(StringUtils.isNotBlank(storeId), TuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), TuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(objectId), TuplePO::getObjectId, objectId)
                .eq(StringUtils.isNotBlank(relation), TuplePO::getRelation, relation)
                .le(Objects.nonNull(maxZookie), TuplePO::getZookie, maxZookie);

        List<TuplePO> tuples = super.list(wrapper);

        return CollectionUtils.isEmpty(tuples) ? Collections.emptyList() : tuples;
    }

    @Override
    public Optional<TuplePO> findByTupleKey(TupleKeyQuery query) {
        if (query == null) {
            return Optional.empty();
        }
        // 简单精确查询使用 MyBatis Plus
        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper()
                .eq(TuplePO::getStoreId, query.getStoreId())
                .eq(TuplePO::getObjectType, query.getObjectType())
                .eq(TuplePO::getObjectId, query.getObjectId())
                .eq(TuplePO::getRelation, query.getRelation())
                .eq(TuplePO::getSubjectType, query.getSubjectType())
                .eq(TuplePO::getSubjectId, query.getSubjectId())
                .eq(StringUtils.isNotBlank(query.getSubjectRelation()), TuplePO::getSubjectRelation, query.getSubjectRelation());

        return Optional.ofNullable(this.getOne(wrapper));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchCreate(List<TuplePO> tuples) {
        if (tuples == null || tuples.isEmpty()) {
            return false;
        }

        boolean result = super.saveBatch(tuples);
        if (!result) {
            return false;
        }

        tuples.forEach(tuple -> cacheManager.invalidateTupleCache(tuple.getStoreId(), tuple.getObjectType(), tuple.getObjectId()));
        cacheManager.invalidateCheckCache(tuples.get(0).getStoreId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDelete(String storeId, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }

        LambdaUpdateWrapper<TuplePO> wrapper = new LambdaUpdateWrapper<TuplePO>()
                .set(TuplePO::getIsDeleted, DeletedStatusEnum.DELETED.getCode())
                .in(TuplePO::getId, ids).eq(TuplePO::getStoreId, storeId);

        boolean result = super.update(wrapper);
        if (!result) {
            return false;
        }

        cacheManager.invalidateCheckCache(storeId);
        return true;
    }

    @Override
    public List<TuplePO> listTuples(String storeId, String objectType, String relation,
                                    int pageSize, Long pageToken) {
        // 分页查询使用 MyBatis Plus
        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper()
                .eq(TuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), TuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(relation), TuplePO::getRelation, relation)
                .gt(pageToken != null, TuplePO::getId, pageToken)
                .orderByAsc(TuplePO::getId)
                .last("LIMIT " + pageSize);

        return this.list(wrapper);
    }

    @Override
    public List<TuplePO> findByTupleKeys(String storeId, List<TupleKeyQuery> tupleKeyQueries) {
        if (tupleKeyQueries == null || tupleKeyQueries.isEmpty()) {
            return Collections.emptyList();
        }

        // 使用 OR 条件组合多个 TupleKey 查询，避免 N+1 问题
        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper()
                .eq(TuplePO::getStoreId, storeId);

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

    private void buildTupleKeyCondition(LambdaQueryWrapper<TuplePO> wrapper, TupleKeyQuery query) {
        wrapper.eq(TuplePO::getObjectType, query.getObjectType())
                .eq(TuplePO::getObjectId, query.getObjectId())
                .eq(TuplePO::getRelation, query.getRelation())
                .eq(TuplePO::getSubjectType, query.getSubjectType())
                .eq(TuplePO::getSubjectId, query.getSubjectId())
                .eq(StringUtils.isNotBlank(query.getSubjectRelation()), 
                    TuplePO::getSubjectRelation, query.getSubjectRelation());
    }

    @Override
    public List<TuplePO> listTuplesWithFilter(String storeId, String objectType, String objectId,
                                              String relation, String subjectType, String subjectId,
                                              String subjectRelation, int pageSize, Long pageToken) {
        LambdaQueryWrapper<TuplePO> wrapper = getLambdaQueryWrapper()
                .eq(TuplePO::getStoreId, storeId)
                .eq(StringUtils.isNotBlank(objectType), TuplePO::getObjectType, objectType)
                .eq(StringUtils.isNotBlank(objectId), TuplePO::getObjectId, objectId)
                .eq(StringUtils.isNotBlank(relation), TuplePO::getRelation, relation)
                .eq(StringUtils.isNotBlank(subjectType), TuplePO::getSubjectType, subjectType)
                .eq(StringUtils.isNotBlank(subjectId), TuplePO::getSubjectId, subjectId)
                .eq(StringUtils.isNotBlank(subjectRelation), TuplePO::getSubjectRelation, subjectRelation)
                .gt(pageToken != null, TuplePO::getId, pageToken)
                .orderByAsc(TuplePO::getId)
                .last("LIMIT " + pageSize);

        return this.list(wrapper);
    }
}
