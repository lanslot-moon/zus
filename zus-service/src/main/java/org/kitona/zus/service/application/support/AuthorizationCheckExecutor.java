package org.kitona.zus.service.application.support;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.aggregate.StoreAggregate;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.domain.factory.AuthorizationModelFactory;
import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.port.ITupleStore;
import org.kitona.zus.domain.port.ITupleStoreFactory;
import org.kitona.zus.domain.repository.IAuthorizationModelDomainRepository;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.service.AuthorizationChecker;
import org.kitona.zus.domain.service.AuthorizationModelGraph;
import org.kitona.zus.domain.valueobject.ObjectRef;
import org.kitona.zus.domain.valueobject.Subject;
import org.kitona.zus.domain.valueobject.TypeDefinition;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.service.assembler.TypeDefinitionAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 权限检查执行器
 *
 * <p>应用层内部支撑组件，负责编排领域服务完成权限检查的核心逻辑。
 * 封装了授权模型加载、类型定义转换、领域服务调用等复杂流程。
 *
 * <p>设计说明：
 * <ul>
 *   <li>作为应用层内部辅助组件，不对外暴露接口</li>
 *   <li>使用 @Component 而非 @Service，明确其支撑组件定位</li>
 *   <li>被 CheckApplicationService 调用，实现职责分离</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Slf4j
@Component
public class AuthorizationCheckExecutor {

    @Resource
    private IStoreDomainRepository storeDomainRepository;

    @Resource
    private IAuthorizationModelDomainRepository modelRepository;

    @Resource
    private ITupleStoreFactory tupleStoreFactory;

    @Resource
    private IModelCompiler modelCompiler;

    /**
     * 执行权限检查
     *
     * @param storeId  存储空间ID
     * @param object   资源对象
     * @param relation 关系名称
     * @param subject  主体
     * @param zookie   一致性令牌（可为 null）
     * @return 是否允许
     */
    public boolean execute(String storeId, ObjectRef object, String relation, Subject subject, Zookie zookie) {
        log.debug("开始权限检查: storeId={}, object={}, relation={}, subject={}", storeId, object, relation, subject);

        Optional<StoreAggregate> storeOpt = storeDomainRepository.findByStoreId(storeId);
        if (storeOpt.isEmpty()) {
            log.warn("store 不存在: {}", storeId);
            return false;
        }

        String currentModelId = storeOpt.get().getCurrentModelId();
        if (StringUtils.isBlank(currentModelId)) {
            log.warn("store 未绑定授权模型: {}", storeId);
            return false;
        }

        Optional<AuthorizationModelAggregate> modelOpt = modelRepository.findByModelId(storeId, currentModelId);
        if (modelOpt.isEmpty()) {
            log.warn("授权模型不存在: storeId={}, modelId={}", storeId, currentModelId);
            return false;
        }

        List<TypeDefinitionEntity> typeDefinitionEntityList = modelOpt.get().getTypeDefinitions();
        if (typeDefinitionEntityList.isEmpty()) {
            log.warn("授权模型无类型定义: storeId={}, modelId={}", storeId, currentModelId);
            return false;
        }

        List<TypeDefinition> typeDefinitions = TypeDefinitionAssembler.toValueObjectList(typeDefinitionEntityList);

        ITupleStore tupleStore = tupleStoreFactory.create(storeId, zookie);
        AuthorizationModelGraph graph = AuthorizationModelFactory.createGraph(modelCompiler, typeDefinitions);
        AuthorizationChecker checker = new AuthorizationChecker(graph, tupleStore);

        boolean result = checker.check(subject.toString(), object.toString(), relation);
        log.debug("权限检查结果: {}", result);
        return result;
    }
}
