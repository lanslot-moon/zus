package org.kitona.zus.service.bean;

import org.kitona.zus.domain.port.IConditionEvaluator;
import org.kitona.zus.domain.port.IDirectTupleReader;
import org.kitona.zus.domain.port.IObjectSubjectCandidateReader;
import org.kitona.zus.domain.port.ISubjectObjectCandidateReader;
import org.kitona.zus.domain.port.ITupleLinkReader;
import org.kitona.zus.domain.service.PermissionEvaluator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 权限求值器配置。
 */
@Configuration
public class PermissionEvaluatorConfiguration {

    @Bean
    public PermissionEvaluator permissionEvaluator(IDirectTupleReader directTupleReader,
                                                   ITupleLinkReader tupleLinkReader,
                                                   ISubjectObjectCandidateReader subjectObjectCandidateReader,
                                                   IObjectSubjectCandidateReader objectSubjectCandidateReader,
                                                   IConditionEvaluator conditionEvaluator) {
        return new PermissionEvaluator(
                directTupleReader,
                tupleLinkReader,
                subjectObjectCandidateReader,
                objectSubjectCandidateReader,
                conditionEvaluator
        );
    }
}
