package org.kitona.zus.api.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.kitona.zus.api.audit.FgaAuditContext;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.tuple.FgaDeleteRequest;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.TupleResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * FGA 元组相关的 API 与 Service 转换器。
 *
 * <p>条件定义 ID 不在 API 层解析：写入时只携带 conditionName 与 conditionContext 快照，
 * 由 Service 层根据当前模型解析 conditionDefinitionId，避免 API 层跨越聚合边界访问模型结构。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Mapper(componentModel = "spring")
public interface FgaTupleConverter {

    FgaTupleConverter INSTANCE = Mappers.getMapper(FgaTupleConverter.class);

    TypeReference<Map<String, Object>> CONDITION_CONTEXT_TYPE = new TypeReference<>() {
    };

    /**
     * 将写入请求转换为写入命令列表。
     *
     * @param request API 写请求
     * @return 写入命令列表
     */
    default List<WriteTupleCommand> toWriteCommands(FgaWriteRequest request) {
        if (request == null || request.getWrites() == null || request.getWrites().isEmpty()) {
            return Collections.emptyList();
        }
        return request.getWrites().stream()
                .map(this::toWriteCommand)
                .toList();
    }

    /**
     * 将删除请求中的 tupleKey 列表转换为删除命令列表。
     *
     * @param request API 删除请求
     * @return 删除命令列表
     */
    default List<WriteTupleCommand> toDeleteCommands(FgaDeleteRequest request) {
        if (request == null || request.getDeletes() == null || request.getDeletes().isEmpty()) {
            return Collections.emptyList();
        }
        return request.getDeletes().stream()
                .map(this::toDeleteCommand)
                .toList();
    }

    /**
     * 将单个写入项转换为写入命令。
     *
     * @param item 写入项
     * @return 写入命令
     */
    @Mapping(target = "objectType", source = "item.tupleKey.object.type")
    @Mapping(target = "objectId", source = "item.tupleKey.object.id")
    @Mapping(target = "relation", source = "item.tupleKey.relation")
    @Mapping(target = "subjectType", source = "item.tupleKey.subject.type")
    @Mapping(target = "subjectId", source = "item.tupleKey.subject.id")
    @Mapping(target = "subjectRelation", source = "item.tupleKey.subject.relation")
    @Mapping(target = "conditionName", source = "item.condition.name")
    @Mapping(target = "conditionDefinitionId", ignore = true)
    @Mapping(target = "conditionContext", source = "item.condition.context", qualifiedByName = "toJsonContext")
    @Mapping(target = "expiresAt", source = "item.expiresAt")
    @Mapping(target = "auditMetadata", expression = "java(toAuditMetadata(org.kitona.zus.api.audit.FgaAuditContext.current()))")
    WriteTupleCommand toWriteCommand(FgaTupleWriteItem item);

    /**
     * 将删除 tupleKey 转换为应用层写命令。
     *
     * @param key tupleKey
     * @return 删除命令
     */
    @Mapping(target = "objectType", source = "object.type")
    @Mapping(target = "objectId", source = "object.id")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = "subjectType", source = "subject.type")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectRelation", source = "subject.relation")
    @Mapping(target = "conditionName", ignore = true)
    @Mapping(target = "conditionDefinitionId", ignore = true)
    @Mapping(target = "conditionContext", ignore = true)
    @Mapping(target = "expiresAt", ignore = true)
    @Mapping(target = "auditMetadata", expression = "java(toAuditMetadata(org.kitona.zus.api.audit.FgaAuditContext.current()))")
    WriteTupleCommand toDeleteCommand(FgaTupleKeyRequest key);

    /**
     * 将 tuple read 请求转换为应用层查询对象。
     *
     * @param request API read 请求
     * @return 应用层查询对象
     */
    @Mapping(target = "objectType", source = "tupleKey.object.type")
    @Mapping(target = "objectId", source = "tupleKey.object.id")
    @Mapping(target = "relation", source = "tupleKey.relation")
    @Mapping(target = "subjectType", source = "tupleKey.subject.type")
    @Mapping(target = "subjectId", source = "tupleKey.subject.id")
    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "pageToken", source = "pageToken")
    TupleReadQuery toReadQuery(FgaReadRequest request);

    /**
     * 将应用层 tuple DTO 转换为 API tuple VO。
     *
     * @param dto 应用层 tuple DTO
     * @return API tuple VO
     */
    @Mapping(target = "objectType", source = "objectType")
    @Mapping(target = "objectId", source = "objectId")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = "subjectType", source = "subjectType")
    @Mapping(target = "subjectId", source = "subjectId")
    @Mapping(target = "subjectRelation", source = "subjectRelation")
    @Mapping(target = "conditionName", source = "conditionName")
    @Mapping(target = "conditionContext", source = "conditionContext", qualifiedByName = "parseJsonContext")
    @Mapping(target = "expiresAt", source = "expiresAt")
    @Mapping(target = "zookie", source = "zookie")
    FgaTupleVO toVO(TupleResultDTO dto);

    /**
     * 批量转换 tuple DTO。
     *
     * @param dtoList 应用层 tuple DTO 列表
     * @return API tuple VO 列表
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaTupleVO> toVOList(List<TupleResultDTO> dtoList);

    @Mapping(target = "operatorId", source = "operatorId")
    @Mapping(target = "requestId", source = "requestId")
    @Mapping(target = "source", source = "source")
    WriteTupleCommand.AuditMetadataInput toAuditMetadata(FgaAuditContext audit);

    @Named("toJsonContext")
    default String toJsonContext(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        return JacksonUtil.toJSONString(context);
    }

    @Named("parseJsonContext")
    default Map<String, Object> parseJsonContext(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return JacksonUtil.parseJSONArray(json, CONDITION_CONTEXT_TYPE);
        } catch (Exception ex) {
            return null;
        }
    }
}
