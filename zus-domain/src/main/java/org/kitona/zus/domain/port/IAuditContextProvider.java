package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;

/**
 * 审计上下文提供者。
 */
public interface IAuditContextProvider {

    /**
     * 获取当前的审计元数据
     *
     * @return 返回当前的审计元数据对象
     */
    AuditMetadata current();
}
