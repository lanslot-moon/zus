package org.kitona.zus.infrastructure.external.audit;

import org.kitona.zus.domain.port.IAuditContextProvider;
import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.springframework.stereotype.Component;

/**
 * 默认审计上下文提供者。
 */
@Component
public class NoopAuditContextProvider implements IAuditContextProvider {

    /**
     * 读取current。
     * @return 返回结果
     */
    @Override
    public AuditMetadata current() {
        return AuditMetadata.EMPTY;
    }
}
