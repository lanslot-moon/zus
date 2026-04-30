package org.kitona.zus.service.bean;

import org.kitona.zus.domain.port.*;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.kitona.zus.domain.service.TupleMutationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * Author: 登林
 * Email: wangli.liu@kitona.org
 * Date: 2026/4/30 14:28
 * Version: V1.0
 * Description: Xxxx
 */
@Configuration
public class InitServiceBeanConfiguration {

    /**
     * 创建权限评估器Bean，将此方法返回的对象作为Bean注册到Spring容器中
     *
     * @param directTupleReader            直接元组读取器
     * @param tupleLinkReader              元组链接读取器
     * @param subjectObjectCandidateReader 主体对象候选读取器
     * @param objectSubjectCandidateReader 对象主体候选读取器
     * @param conditionEvaluator           条件评估器
     * @return 返回权限评估器实例
     */
    @Bean
    public PermissionEvaluator buildPermissionEvaluator(IDirectTupleReader directTupleReader,
                                                        ITupleLinkReader tupleLinkReader,
                                                        ISubjectObjectCandidateReader subjectObjectCandidateReader,
                                                        IObjectSubjectCandidateReader objectSubjectCandidateReader,
                                                        IConditionEvaluator conditionEvaluator) {
        return new PermissionEvaluator(directTupleReader, tupleLinkReader, subjectObjectCandidateReader, objectSubjectCandidateReader, conditionEvaluator);
    }


    /**
     * 创建元组变更领域服务Bean，将此方法返回的对象作为Bean注册到Spring容器中
     *
     * @param tupleRepository     元组领域仓库
     * @param changelogRepository 变更日志领域仓库
     * @param zookieSequencePort  Zookie序列端口
     * @return 返回元组变更领域服务实例
     */
    @Bean
    public TupleMutationDomainService buildTupleMutationDomainService(ITupleDomainRepository tupleRepository,
                                                                      IChangelogDomainRepository changelogRepository,
                                                                      IZookieSequencePort zookieSequencePort) {
        return new TupleMutationDomainService(tupleRepository, changelogRepository, zookieSequencePort);
    }
}
