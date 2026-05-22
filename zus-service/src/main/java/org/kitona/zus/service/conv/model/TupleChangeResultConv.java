package org.kitona.zus.service.conv.model;

import org.kitona.zus.domain.authorization.audit.AuditMetadata;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.kitona.zus.service.dto.response.TupleChangeResultDTO;
import org.kitona.zus.service.event.TupleChangeOperation;

/**
 * Tuple 变更结果转换器。
 *
 * <p>负责将应用事件中的 tuple key、操作类型、zookie 和审计信息装配成 Watch
 * 推送使用的 {@link TupleChangeResultDTO}，避免监听器手写字段映射。
 */
@Mapper(componentModel = "spring")
public interface TupleChangeResultConv {

    /**
     * 静态转换器实例，供不注入 Spring Bean 的轻量场景使用。
     */
    TupleChangeResultConv INSTANCE = Mappers.getMapper(TupleChangeResultConv.class);

    /**
     * 将 tuple 变更事件上下文转换为 Watch 变更 DTO。
     *
     * @param tupleKey      tuple 键
     * @param operation     变更操作类型
     * @param zookie        zookie 字符串
     * @param auditMetadata 审计元数据
     * @return Watch 变更 DTO
     */
    @Mapping(target = "objectType", source = "tupleKey.objectType")
    @Mapping(target = "objectId", source = "tupleKey.objectId")
    @Mapping(target = "relation", source = "tupleKey.relation")
    @Mapping(target = "subjectType", source = "tupleKey.subjectType")
    @Mapping(target = "subjectId", source = "tupleKey.subjectId")
    @Mapping(target = "subjectRelation", source = "tupleKey.subjectRelation")
    @Mapping(target = "operation", source = "operation.code")
    @Mapping(target = "zookie", source = "zookie")
    @Mapping(target = "operatorId", source = "auditMetadata.operatorId")
    @Mapping(target = "requestId", source = "auditMetadata.requestId")
    @Mapping(target = "source", source = "auditMetadata.source")
    TupleChangeResultDTO toDTO(TupleKey tupleKey, TupleChangeOperation operation, String zookie,
                               AuditMetadata auditMetadata);
}
