package org.kitona.zus.domain.port;

import org.kitona.zus.domain.valueobject.Zookie;

/**
 * 元组存储工厂端口
 *
 * <p>用于创建 {@link ITupleStore} 实例，支持不同的存储实现。
 *
 * <p>工厂模式允许在运行时根据配置或上下文选择不同的元组存储实现：
 * <ul>
 *   <li>内存实现：用于单元测试或小规模数据</li>
 *   <li>仓储实现：用于生产环境，从数据库查询</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
public interface ITupleStoreFactory {

    /**
     * 创建元组存储实例
     *
     * @param storeId 存储空间ID
     * @param zookie  一致性令牌（可为 null）
     * @return 元组存储实例
     */
    ITupleStore create(String storeId, Zookie zookie);
}
