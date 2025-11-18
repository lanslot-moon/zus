package org.kitona.zus.service.domain.namespace;

import org.kitona.zus.service.domain.namespace.entity.NamespaceConfig;
import org.kitona.zus.service.domain.namespace.entity.NamespaceStats;
import org.kitona.zus.service.domain.spanner.SpannerAdapter;

import java.util.List;

/**
 * 命名空间配置管理
 */
public interface NamespaceService {

    /**
     * 创建命名空间
     */
    SpannerAdapter.DatabaseResult<NamespaceConfig> createNamespace(NamespaceConfig config);
    
    /**
     * 更新命名空间配置
     */
    SpannerAdapter.DatabaseResult<NamespaceConfig> updateNamespace(String name, NamespaceConfig config);
    
    /**
     * 获取命名空间配置
     */
    SpannerAdapter.DatabaseResult<NamespaceConfig> getNamespace(String name);
    
    /**
     * 删除命名空间
     */
    SpannerAdapter.DatabaseResult<Boolean> deleteNamespace(String name);
    
    /**
     * 获取所有命名空间
     */
    SpannerAdapter.DatabaseResult<List<NamespaceConfig>> getAllNamespaces();
    
    /**
     * 验证命名空间配置
     */
    SpannerAdapter.DatabaseResult<Boolean> validateNamespace(NamespaceConfig config);
    
    /**
     * 获取命名空间统计信息
     */
    SpannerAdapter.DatabaseResult<NamespaceStats> getNamespaceStats(String name);
    

}