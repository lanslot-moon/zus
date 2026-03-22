package org.kitona.zus.service.bean;

import org.kitona.zus.domain.port.IZookieSequencePort;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.service.TupleMutationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * tuple 变更领域服务装配配置
 *
 * <p>将无框架依赖的领域服务注册为 Spring Bean，
 * 避免在应用层编排器中手动 new，提升测试替换与装配一致性。
 */
@Configuration
public class TupleMutationDomainServiceConfiguration {

    @Bean
    public TupleMutationDomainService tupleMutationDomainService(ITupleDomainRepository tupleRepository,
                                                                 IChangelogDomainRepository changelogRepository,
                                                                 IZookieSequencePort zookieSequencePort) {
        return new TupleMutationDomainService(tupleRepository, changelogRepository, zookieSequencePort);
    }
}
