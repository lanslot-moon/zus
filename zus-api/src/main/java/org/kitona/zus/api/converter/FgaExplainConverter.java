package org.kitona.zus.api.converter;

import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.response.FgaExplainResolutionVO;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.ExplainResolutionDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Explain API 与应用层 DTO 转换器。
 *
 * <p>Explain 响应包含递归 resolution tree，字段虽然同名但需要逐层转换，因此这里采用
 * MapStruct mapper interface 承载入口，树结构转换保留为 default 方法。
 */
@Mapper(componentModel = "spring")
public interface FgaExplainConverter {

    FgaExplainConverter INSTANCE = Mappers.getMapper(FgaExplainConverter.class);

    /**
     * 将 Explain API 请求转换为应用层命令。
     *
     * @param storeId URL 路径中的 storeId
     * @param request Explain 请求
     * @return 应用层 Explain 命令
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "objectType", source = "request.tupleKey.object.type")
    @Mapping(target = "objectId", source = "request.tupleKey.object.id")
    @Mapping(target = "relation", source = "request.tupleKey.relation")
    @Mapping(target = "subjectType", source = "request.tupleKey.subject.type")
    @Mapping(target = "subjectId", source = "request.tupleKey.subject.id")
    @Mapping(target = "subjectRelation", source = "request.tupleKey.subject.relation")
    @Mapping(target = "consistencyToken", source = "request.consistency", qualifiedByName = "resolveConsistencyToken")
    @Mapping(target = "context", source = "request.context")
    ExplainCommand toExplainCommand(String storeId, FgaExplainRequest request);

    /**
     * 将应用层 Explain 结果转换为 API 响应 VO。
     *
     * @param dto 应用层 Explain 结果
     * @return API 响应 VO
     */
    @Mapping(target = "allowed", source = "allowed")
    @Mapping(target = "durationMs", source = "durationMs")
    @Mapping(target = "zookieToken", source = "zookieToken")
    @Mapping(target = "decision", source = "decision")
    @Mapping(target = "errorMessage", source = "errorMessage")
    @Mapping(target = "resolution", source = "resolution")
    FgaExplainResultVO toVO(PermissionExplainResultDTO dto);

    @Named("resolveConsistencyToken")
    default String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null || options.getPreference() != FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return null;
        }
        return options.getAtRevision();
    }

    @Mapping(target = "allowed", source = "allowed")
    @Mapping(target = "requestedZookie", source = "requestedZookie")
    @Mapping(target = "currentZookie", source = "currentZookie")
    @Mapping(target = "staleSnapshotDiagnosis", source = "staleSnapshotDiagnosis")
    @Mapping(target = "truncated", source = "truncated")
    @Mapping(target = "root", source = "root")
    FgaExplainResolutionVO toResolutionVO(ExplainResolutionDTO dto);

    @Mapping(target = "nodeType", source = "nodeType")
    @Mapping(target = "target", source = "target")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = "allowed", source = "allowed")
    @Mapping(target = "reason", source = "reason")
    @Mapping(target = "tuple", source = "tuple")
    @Mapping(target = "condition", source = "condition")
    @Mapping(target = "children", source = "children")
    FgaExplainResolutionVO.Node toNodeVO(ExplainResolutionDTO.NodeDTO node);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaExplainResolutionVO.Node> toNodeVOList(List<ExplainResolutionDTO.NodeDTO> children);

    @Mapping(target = "object", source = "object")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = "subject", source = "subject")
    @Mapping(target = "wildcard", source = "wildcard")
    @Mapping(target = "zookie", source = "zookie")
    @Mapping(target = "expiresAt", source = "expiresAt")
    @Mapping(target = "conditionDefinitionId", source = "conditionDefinitionId")
    @Mapping(target = "conditionName", source = "conditionName")
    FgaExplainResolutionVO.Tuple toTupleVO(ExplainResolutionDTO.TupleDTO tuple);

    @Mapping(target = "conditionDefinitionId", source = "conditionDefinitionId")
    @Mapping(target = "conditionName", source = "conditionName")
    @Mapping(target = "passed", source = "passed")
    FgaExplainResolutionVO.Condition toConditionVO(ExplainResolutionDTO.ConditionDTO condition);
}
