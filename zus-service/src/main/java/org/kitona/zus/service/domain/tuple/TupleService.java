package org.kitona.zus.service.domain.tuple;

import org.kitona.zus.service.domain.tuple.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 关系元组存储服务接口
 * 为每个客户端命名空间提供独立的数据库支持
 */
public interface TupleService {
    
    /**
     * 创建命名空间数据库
     */
    CompletableFuture<Boolean> createNamespaceDatabase(String namespace);
    
    /**
     * 删除命名空间数据库
     */
    CompletableFuture<Boolean> deleteNamespaceDatabase(String namespace);
    
    /**
     * 写入关系元组
     */
    CompletableFuture<RelationshipTuple> writeTuple(RelationshipTuple tuple);
    
    /**
     * 批量写入关系元组
     */
    CompletableFuture<BatchOperationResult> writeTuplesBatch(List<RelationshipTuple> tuples);
    
    /**
     * 删除关系元组
     */
    CompletableFuture<Boolean> deleteTuple(RelationshipTuple tuple);
    
    /**
     * 批量删除关系元组
     */
    CompletableFuture<BatchOperationResult> deleteTuplesBatch(List<RelationshipTuple> tuples);
    
    /**
     * 读取关系元组
     */
    CompletableFuture<List<RelationshipTuple>> readTuples(TupleQuery query);
    
    /**
     * 获取对象的所有关系
     */
    CompletableFuture<List<RelationshipTuple>> getObjectRelations(String namespace, String object);
    
    /**
     * 获取用户的权限列表
     */
    CompletableFuture<List<RelationshipTuple>> getUserPermissions(String namespace, String user);
    
    /**
     * 检查是否存在指定的关系
     */
    CompletableFuture<Boolean> checkRelationshipExists(String namespace, String object, String relation, String user);
    
    /**
     * 获取指定版本的关系数据（快照读取）
     */
    CompletableFuture<List<RelationshipTuple>> readTuplesAtVersion(VersionQuery query);
    
    /**
     * 获取命名空间统计信息
     */
    CompletableFuture<TupleStats> getTupleStats(String namespace);
    
    /**
     * 清理过期的非激活元组
     */
    CompletableFuture<Long> cleanupInactiveTuples(String namespace, LocalDateTime retentionTime);
    
    /**
     * 重建命名空间索引
     */
    CompletableFuture<Void> rebuildNamespaceIndex(String namespace);
    
    /**
     * 验证命名空间数据的完整性
     */
    CompletableFuture<TupleStats> validateNamespaceData(String namespace);
}