package org.kitona.zus.service.application.coordinator;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.domain.factory.AuthorizationModelFactory;
import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.port.ITupleStore;
import org.kitona.zus.domain.port.ITupleStoreFactory;
import org.kitona.zus.domain.query.StoreView;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreQueryRepository;
import org.kitona.zus.domain.service.AuthorizationChecker;
import org.kitona.zus.domain.service.AuthorizationModelGraph;
import org.kitona.zus.domain.valueobject.AuthorizationCheckResult;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.TypeDefinition;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.assembler.TypeDefinitionAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 权限检查编排器
 *
 * <p>应用层内部支撑组件，负责编排领域服务完成权限检查核心流程。
 */
@Slf4j
@Component
public class AuthorizationCheckOrchestrator {

    @Resource
    private IStoreQueryRepository storeQueryRepository;

    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    @Resource
    private ITupleStoreFactory tupleStoreFactory;

    @Resource
    private IModelCompiler modelCompiler;

    public AuthorizationCheckResult execute(String storeId, ObjectRef object, String relation, Subject subject, Zookie zookie) {
        log.debug("开始权限检查: storeId={}, object={}, relation={}, subject={}", storeId, object, relation, subject);

        Optional<StoreView> storeOpt = storeQueryRepository.findViewByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            log.warn("store 不存在: {}", storeId);
            return AuthorizationCheckResult.storeNotFound();
        }

        String currentModelId = storeOpt.get().currentModelId();
        if (StringUtils.isBlank(currentModelId)) {
            log.warn("store 未绑定授权模型: {}", storeId);
            return AuthorizationCheckResult.modelNotBound();
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelRepository.findByModelId(storeId, currentModelId);
        if (modelOpt.isEmpty()) {
            log.warn("授权模型不存在: storeId={}, modelId={}", storeId, currentModelId);
            return AuthorizationCheckResult.modelNotFound();
        }

        List<TypeDefinitionEntity> typeDefinitionEntityList = modelOpt.get().getTypeDefinitions();
        if (typeDefinitionEntityList.isEmpty()) {
            log.warn("授权模型无类型定义: storeId={}, modelId={}", storeId, currentModelId);
            return AuthorizationCheckResult.modelInvalid();
        }

        List<TypeDefinition> typeDefinitions = TypeDefinitionAssembler.toValueObjectList(typeDefinitionEntityList);
        ITupleStore tupleStore = tupleStoreFactory.create(storeId, zookie);
        AuthorizationModelGraph graph;
        try {
            graph = AuthorizationModelFactory.createGraph(modelCompiler, typeDefinitions);
        } catch (RuntimeException ex) {
            log.warn("授权模型结构无效，无法构建鉴权图: storeId={}, modelId={}", storeId, currentModelId, ex);
            return AuthorizationCheckResult.modelInvalid();
        }
        AuthorizationChecker checker = new AuthorizationChecker(graph, tupleStore);

        boolean result = checker.check(subject.toString(), object.toString(), relation);
        log.debug("权限检查结果: {}", result);
        return result ? AuthorizationCheckResult.allowed() : AuthorizationCheckResult.denied();
    }
}
