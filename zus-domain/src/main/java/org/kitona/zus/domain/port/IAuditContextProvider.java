package org.kitona.zus.domain.port;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;

/**
 * 审计上下文提供者。
 */
public interface IAuditContextProvider {

    AuditMetadata current();
}
