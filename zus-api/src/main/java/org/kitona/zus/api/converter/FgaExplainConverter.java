package org.kitona.zus.api.converter;

import org.kitona.zus.api.request.authorization.FgaExplainRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.response.FgaExplainResolutionVO;
import org.kitona.zus.api.response.FgaExplainResultVO;
import org.kitona.zus.service.dto.command.ExplainCommand;
import org.kitona.zus.service.dto.response.ExplainResolutionDTO;
import org.kitona.zus.service.dto.response.PermissionExplainResultDTO;

import java.util.List;

/**
 * Explain API 与应用层 DTO 转换器。
 */
public final class FgaExplainConverter {

    private FgaExplainConverter() {
    }

    /**
     * 将 Explain API 请求转换为应用层命令。
     *
     * @param storeId URL 路径中的 storeId
     * @param request Explain 请求
     * @return 应用层 Explain 命令
     */
    public static ExplainCommand toExplainCommand(String storeId, FgaExplainRequest request) {
        if (request == null || request.getTupleKey() == null) {
            return null;
        }
        FgaTupleKeyRequest key = request.getTupleKey();
        FgaReferenceRequest object = key.getObject();
        FgaReferenceRequest subject = key.getSubject();
        return ExplainCommand.builder()
                .storeId(storeId)
                .objectType(object != null ? object.getType() : null)
                .objectId(object != null ? object.getId() : null)
                .relation(key.getRelation())
                .subjectType(subject != null ? subject.getType() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectRelation(subject != null ? subject.getRelation() : null)
                .consistencyToken(resolveConsistencyToken(request.getConsistency()))
                .context(request.getContext())
                .build();
    }

    /**
     * 将应用层 Explain 结果转换为 API 响应 VO。
     *
     * @param dto 应用层 Explain 结果
     * @return API 响应 VO
     */
    public static FgaExplainResultVO toVO(PermissionExplainResultDTO dto) {
        if (dto == null) {
            return null;
        }
        return FgaExplainResultVO.builder()
                .allowed(dto.isAllowed())
                .durationMs(dto.getDurationMs())
                .zookieToken(dto.getZookieToken())
                .decision(dto.getDecision())
                .errorMessage(dto.getErrorMessage())
                .resolution(toResolutionVO(dto.getResolution()))
                .build();
    }

    private static String resolveConsistencyToken(FgaConsistencyOptions options) {
        if (options == null || options.getPreference() != FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH) {
            return null;
        }
        return options.getAtRevision();
    }

    private static FgaExplainResolutionVO toResolutionVO(ExplainResolutionDTO dto) {
        if (dto == null) {
            return null;
        }
        return FgaExplainResolutionVO.builder()
                .allowed(dto.isAllowed())
                .requestedZookie(dto.getRequestedZookie())
                .currentZookie(dto.getCurrentZookie())
                .staleSnapshotDiagnosis(dto.getStaleSnapshotDiagnosis())
                .truncated(dto.isTruncated())
                .root(toNodeVO(dto.getRoot()))
                .build();
    }

    private static FgaExplainResolutionVO.Node toNodeVO(ExplainResolutionDTO.NodeDTO node) {
        if (node == null) {
            return null;
        }
        return FgaExplainResolutionVO.Node.builder()
                .nodeType(node.getNodeType())
                .target(node.getTarget())
                .subject(node.getSubject())
                .relation(node.getRelation())
                .allowed(node.isAllowed())
                .reason(node.getReason())
                .tuple(toTupleVO(node.getTuple()))
                .condition(toConditionVO(node.getCondition()))
                .children(toNodeVOList(node.getChildren()))
                .build();
    }

    private static List<FgaExplainResolutionVO.Node> toNodeVOList(List<ExplainResolutionDTO.NodeDTO> children) {
        if (children == null) {
            return List.of();
        }
        return children.stream().map(FgaExplainConverter::toNodeVO).toList();
    }

    private static FgaExplainResolutionVO.Tuple toTupleVO(ExplainResolutionDTO.TupleDTO tuple) {
        if (tuple == null) {
            return null;
        }
        return FgaExplainResolutionVO.Tuple.builder()
                .object(tuple.getObject())
                .relation(tuple.getRelation())
                .subject(tuple.getSubject())
                .wildcard(tuple.isWildcard())
                .zookie(tuple.getZookie())
                .expiresAt(tuple.getExpiresAt())
                .conditionDefinitionId(tuple.getConditionDefinitionId())
                .conditionName(tuple.getConditionName())
                .build();
    }

    private static FgaExplainResolutionVO.Condition toConditionVO(ExplainResolutionDTO.ConditionDTO condition) {
        if (condition == null) {
            return null;
        }
        return FgaExplainResolutionVO.Condition.builder()
                .conditionDefinitionId(condition.getConditionDefinitionId())
                .conditionName(condition.getConditionName())
                .passed(condition.isPassed())
                .build();
    }
}
