package org.kitona.zus.service.bean;

import org.kitona.zus.domain.port.*;
import org.kitona.zus.domain.service.PermissionCheckEvaluator;
import org.kitona.zus.domain.service.PermissionSearchEvaluator;
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
     * 创建单点权限检查器 Bean。
     *
     * @param directTupleReader  直接元组读取器
     * @param conditionEvaluator 条件评估器
     * @return 单点权限检查器
     */
    @Bean
    public PermissionCheckEvaluator buildPermissionCheckEvaluator(IDirectTupleReader directTupleReader,
                                                                  IConditionEvaluator conditionEvaluator) {
        return new PermissionCheckEvaluator(directTupleReader, conditionEvaluator);
    }

    /**
     * 创建权限搜索评估器 Bean。
     *
     * @param permissionCheckEvaluator     单点权限证明器
     * @param subjectObjectCandidateReader object 候选读取器
     * @param objectSubjectCandidateReader subject 候选读取器
     * @return 权限搜索评估器
     */
    @Bean
    public PermissionSearchEvaluator buildPermissionSearchEvaluator(PermissionCheckEvaluator permissionCheckEvaluator,
                                                                    ISubjectObjectCandidateReader subjectObjectCandidateReader,
                                                                    IObjectSubjectCandidateReader objectSubjectCandidateReader) {
        return new PermissionSearchEvaluator(permissionCheckEvaluator, subjectObjectCandidateReader, objectSubjectCandidateReader);
    }


    /**
     * 创建元组变更领域服务Bean，将此方法返回的对象作为Bean注册到Spring容器中
     *
     * @return 返回元组变更领域服务实例
     */
    @Bean
    public TupleMutationDomainService buildTupleMutationDomainService() {
        return new TupleMutationDomainService();
    }
}
