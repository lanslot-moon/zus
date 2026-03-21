package org.kitona.zus.service.application.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.kitona.zus.common.utils.ValidationUtil;
import org.kitona.zus.domain.entity.ChangelogEntity;
import org.kitona.zus.domain.entity.RelationTupleEntity;
import org.kitona.zus.domain.event.TupleDeletedEvent;
import org.kitona.zus.domain.event.TupleWrittenEvent;
import org.kitona.zus.domain.valueobject.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.domain.repository.IChangelogDomainRepository;
import org.kitona.zus.domain.repository.IStoreDomainRepository;
import org.kitona.zus.domain.repository.ITupleDomainRepository;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.application.IWriteApplicationService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Write 应用服务
 *
 * <p>
 * 提供元组写入用例的应用层实现，编排写入、Zookie 与 Changelog。
 *
 * <h3>聚合根设计说明</h3>
 * <p>
 * {@link RelationTupleEntity} 是一个<b>独立聚合根</b>，不属于其他聚合的内部实体。
 * 因此本服务直接通过 {@link ITupleDomainRepository} 操作 RelationTupleEntity，
 * 这符合 DDD 的聚合根操作原则。
 *
 * <h3>DDD 规范</h3>
 * <ul>
 * <li>通过聚合根的工厂方法 {@link RelationTupleEntity#create} 创建实体，而非直接 new</li>
 * <li>通过 {@link ChangelogEntity#createWriteLog} 等工厂方法创建变更日志</li>
 * <li>不使用 MapStruct 直接映射到领域实体，保持领域对象的封装性</li>
 * <li>发布领域事件 {@link TupleWrittenEvent}、{@link TupleDeletedEvent} 通知其他限界上下文</li>
 * </ul>
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 * @see RelationTupleEntity
 * @see ChangelogEntity
 */
@Slf4j
@Service
public class WriteApplicationService implements IWriteApplicationService {

    private static final String OPERATION_WRITE = "WRITE";
    private static final String OPERATION_DELETE = "DELETE";

    @Resource
    private ITupleDomainRepository tupleRepository;

    @Resource
    private IStoreDomainRepository storeRepository;

    @Resource
    private IChangelogDomainRepository changelogRepository;

    @Resource
    private ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void write(String storeId, List<WriteTupleCommand> writeTuple) {
        if (CollectionUtils.isEmpty(writeTuple)) {
            return;
        }

        // 校验每个元组命令
        writeTuple.forEach(ValidationUtil::validate);

        // 获取新的 Zookie 版本
        Long version = storeRepository.nextZookie(storeId);
        Zookie newZookie = Zookie.of(version);

        // 通过领域实体的工厂方法创建实体
        List<RelationTupleEntity> tupleEntities = writeTuple.stream()
                .map(cmd -> createTupleEntity(storeId, cmd, newZookie))
                .toList();

        tupleRepository.saveBatch(tupleEntities);

        // 记录变更日志
        List<TupleKey> writtenKeys = tupleEntities.stream()
                .map(RelationTupleEntity::getTupleKey)
                .toList();

        if (CollectionUtils.isEmpty(writtenKeys)) {
            return;
        }

        List<ChangelogEntity> changelogs = writtenKeys.stream()
                .map(key -> buildChangelogEntity(storeId, key, newZookie, OPERATION_WRITE))
                .toList();
        changelogRepository.saveBatch(changelogs);

        log.debug("WriteApplicationService.write 写入变更日志(WRITE): storeId={}, count={}", storeId, writtenKeys.size());
        eventPublisher.publishEvent(new TupleWrittenEvent(storeId, writtenKeys, newZookie));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleted(String storeId, List<WriteTupleCommand> deleteTuple) {
        if (CollectionUtils.isEmpty(deleteTuple)) {
            return;
        }

        // 校验每个元组命令
        deleteTuple.forEach(ValidationUtil::validate);

        // 获取新的 Zookie 版本
        Long version = storeRepository.nextZookie(storeId);
        Zookie newZookie = Zookie.of(version);

        // 构建 TupleKey 列表用于查询
        List<TupleKey> deletedKeys = deleteTuple.stream()
                .map(this::buildTupleKey)
                .toList();

        // 批量查询存在的元组并获取 ID（优化 N+1 问题）
        List<RelationTupleEntity> existingTuples = tupleRepository.findByTupleKeys(storeId, deletedKeys);
        List<Long> ids = existingTuples.stream()
                .map(RelationTupleEntity::getId)
                .toList();

        if (!ids.isEmpty()) {
            tupleRepository.batchDelete(storeId, ids);
        }

        if (CollectionUtils.isEmpty(deletedKeys)) {
            return;
        }

        // 记录变更日志
        List<ChangelogEntity> changelogs = deletedKeys.stream()
                .map(key -> buildChangelogEntity(storeId, key, newZookie, OPERATION_DELETE))
                .toList();
        changelogRepository.saveBatch(changelogs);

        log.debug("WriteApplicationService.deleted 写入变更日志(DELETE): storeId={}, count={}", storeId, deletedKeys.size());
        eventPublisher.publishEvent(new TupleDeletedEvent(storeId, deletedKeys, newZookie));
    }

    /**
     * 通过命令创建 RelationTupleEntity（使用领域工厂方法）
     */
    private RelationTupleEntity createTupleEntity(String storeId, WriteTupleCommand cmd, Zookie zookie) {
        TupleKey tupleKey = buildTupleKey(cmd);
        return RelationTupleEntity.create(storeId, tupleKey, zookie);
    }

    /**
     * 从命令构建 TupleKey
     */
    private TupleKey buildTupleKey(WriteTupleCommand cmd) {
        return TupleKey.of(
                cmd.getObjectType(),
                cmd.getObjectId(),
                cmd.getRelation(),
                cmd.getSubjectType(),
                cmd.getSubjectId(),
                cmd.getSubjectRelation());
    }

    /**
     * 构建变更日志实体（使用领域工厂方法）
     */
    private static ChangelogEntity buildChangelogEntity(String storeId, TupleKey key, Zookie zookie, String operation) {
        Long zookieVersion = zookie != null && zookie.getVersion() != null ? zookie.getVersion() : 0L;
        if (OPERATION_WRITE.equals(operation)) {
            return ChangelogEntity.createWriteLog(storeId, key, zookieVersion);
        } else {
            return ChangelogEntity.createDeleteLog(storeId, key, zookieVersion);
        }
    }
}
