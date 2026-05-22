package org.kitona.zus.domain.service;

import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.authorization.tuple.TupleMutationRequest;
import org.kitona.zus.domain.valueobject.Zookie;

import java.util.List;

/**
 * tuple 变更领域服务。
 *
 * <p>该服务只负责 tuple 变更相关的领域对象构造和变更事实表达。
 * zookie 生成、仓储持久化、事务边界和应用事件发布属于应用层用例编排职责。
 */
public class TupleMutationDomainService {

    /**
     * 构造待写入的关系元组。
     *
     * @param storeId  存储空间标识
     * @param requests tuple 变更请求
     * @param zookie   本批变更使用的一致性版本
     * @return 待写入关系元组
     */
    public List<RelationTuple> buildWriteTuples(String storeId, List<TupleMutationRequest> requests, Zookie zookie) {
        if (CollectionUtils.isEmpty(requests)) {
            return List.of();
        }
        return requests.stream()
                .map(request -> RelationTuple.create(storeId, request.tupleKey(), zookie,
                        request.condition(), request.expiresAt()))
                .toList();
    }

    /**
     * 构造一批 tuple 变更日志。
     *
     * @param storeId       存储空间标识
     * @param keys          发生变更的 tuple key
     * @param zookie        本批变更使用的一致性版本
     * @param operation     变更操作
     * @param auditMetadata 审计元数据
     * @return 变更日志实体列表
     */
    public List<Changelog> buildChangelogs(String storeId, List<TupleKey> keys, Zookie zookie,
                                           String operation, AuditMetadata auditMetadata) {
        if (CollectionUtils.isEmpty(keys)) {
            return List.of();
        }
        Long zookieVersion = zookie != null && zookie.getVersion() != null ? zookie.getVersion() : 0L;
        return keys.stream()
                .map(key -> buildChangelogEntity(storeId, key, zookieVersion, operation, auditMetadata))
                .toList();
    }

    /**
     * 根据操作类型构造对应的领域审计对象。
     */
    private Changelog buildChangelogEntity(String storeId, TupleKey key, Long zookieVersion,
                                           String operation, AuditMetadata auditMetadata) {
        if (Changelog.OPERATION_WRITE.equals(operation)) {
            return Changelog.createWriteLog(storeId, key, zookieVersion, auditMetadata);
        }
        return Changelog.createDeleteLog(storeId, key, zookieVersion, auditMetadata);
    }
}
