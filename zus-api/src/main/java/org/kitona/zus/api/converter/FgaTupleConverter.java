package org.kitona.zus.api.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.kitona.zus.api.audit.FgaAuditContext;
import org.kitona.zus.api.request.common.FgaConditionRequest;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyFilterRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.service.dto.command.WriteTupleCommand;
import org.kitona.zus.service.dto.query.TupleReadQuery;
import org.kitona.zus.service.dto.response.TupleResultDTO;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * FGA 元组相关的 API ↔ Service 转换器。
 *
 * <p>DDD 规范：Converter 位于用户接口层（API），负责把 API 层的 Request/VO 与 Service 层的
 * Command/Query/DTO 做双向转换；不承担业务规则。
 *
 * <p>条件定义 ID 不在此层解析：写入时只携带 {@code conditionName} 与 {@code conditionContext}
 * 快照，由 Service 层通过 {@code conditionDefinitionId} 二次解析（Service 层能感知当前模型）。
 * 这避免 API 层跨越聚合边界访问条件定义仓储。
 *
 * @author kitona
 * @since 2026-04-18
 */
public final class FgaTupleConverter {

    private static final TypeReference<Map<String, Object>> CONDITION_CONTEXT_TYPE =
            new TypeReference<>() {
            };

    private FgaTupleConverter() {
    }

    // ============ API → Service：Write ============

    /**
     * 将写入请求拆分为「写入命令列表」。删除项由 {@link #toDeleteCommands} 单独转换。
     *
     * @param request                 API 请求
     * @param conditionIdResolver     条件名 → {@code conditionDefinitionId} 解析器（允许返回 null）
     */
    public static List<WriteTupleCommand> toWriteCommands(FgaWriteRequest request,
                                                          Function<String, Long> conditionIdResolver) {
        if (request == null || request.getWrites() == null || request.getWrites().isEmpty()) {
            return Collections.emptyList();
        }
        return request.getWrites().stream()
                .map(item -> toWriteCommand(item, conditionIdResolver))
                .toList();
    }

    /**
     * 将删除请求的元组键列表转换为命令列表。
     */
    public static List<WriteTupleCommand> toDeleteCommands(FgaWriteRequest request) {
        if (request == null || request.getDeletes() == null || request.getDeletes().isEmpty()) {
            return Collections.emptyList();
        }
        return request.getDeletes().stream()
                .map(key -> toCommand(key, null, null, null))
                .toList();
    }

    /**
     * 单个写入项转命令。
     */
    public static WriteTupleCommand toWriteCommand(FgaTupleWriteItem item,
                                                   Function<String, Long> conditionIdResolver) {
        if (item == null) {
            return null;
        }
        return toCommand(item.getTupleKey(), item.getCondition(), item.getExpiresAt(), conditionIdResolver);
    }

    private static WriteTupleCommand toCommand(FgaTupleKeyRequest key,
                                               FgaConditionRequest condition,
                                               Long expiresAt,
                                               Function<String, Long> conditionIdResolver) {
        if (key == null) {
            return null;
        }
        FgaReferenceRequest object = key.getObject();
        FgaReferenceRequest subject = key.getSubject();

        WriteTupleCommand.WriteTupleCommandBuilder builder = WriteTupleCommand.builder()
                .objectType(object != null ? object.getType() : null)
                .objectId(object != null ? object.getId() : null)
                .relation(key.getRelation())
                .subjectType(subject != null ? subject.getType() : null)
                .subjectId(subject != null ? subject.getId() : null)
                .subjectRelation(subject != null ? subject.getRelation() : null)
                .expiresAt(expiresAt)
                .auditMetadata(buildAuditFromContext());

        if (condition != null && condition.getName() != null && !condition.getName().isBlank()) {
            builder.conditionName(condition.getName());
            builder.conditionContext(toJsonContext(condition.getContext()));
            if (conditionIdResolver != null) {
                builder.conditionDefinitionId(conditionIdResolver.apply(condition.getName()));
            }
        }
        return builder.build();
    }

    // ============ API → Service：Read ============

    public static TupleReadQuery toReadQuery(FgaReadRequest request) {
        TupleReadQuery.TupleReadQueryBuilder builder = TupleReadQuery.builder()
                .pageSize(request != null ? request.getPageSize() : null)
                .pageToken(request != null ? request.getPageToken() : null);

        FgaTupleKeyFilterRequest filter = request != null ? request.getTupleKey() : null;
        if (filter != null) {
            builder.relation(filter.getRelation());
            if (filter.getObject() != null) {
                builder.objectType(filter.getObject().getType());
                builder.objectId(filter.getObject().getId());
            }
            if (filter.getSubject() != null) {
                builder.subjectType(filter.getSubject().getType());
                builder.subjectId(filter.getSubject().getId());
            }
        }
        return builder.build();
    }

    // ============ Service → API：VO ============

    public static FgaTupleVO toVO(TupleResultDTO dto) {
        if (dto == null) {
            return null;
        }
        FgaTupleVO vo = new FgaTupleVO();
        vo.setObjectType(dto.getObjectType());
        vo.setObjectId(dto.getObjectId());
        vo.setRelation(dto.getRelation());
        vo.setSubjectType(dto.getSubjectType());
        vo.setSubjectId(dto.getSubjectId());
        vo.setSubjectRelation(dto.getSubjectRelation());
        vo.setZookie(dto.getZookie());
        vo.setConditionName(dto.getConditionName());
        vo.setConditionContext(parseJsonContext(dto.getConditionContext()));
        vo.setExpiresAt(dto.getExpiresAt());
        return vo;
    }

    public static List<FgaTupleVO> toVOList(List<TupleResultDTO> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(FgaTupleConverter::toVO).toList();
    }

    // ============ 内部工具 ============

    private static WriteTupleCommand.AuditMetadataInput buildAuditFromContext() {
        FgaAuditContext audit = FgaAuditContext.current();
        return WriteTupleCommand.AuditMetadataInput.builder()
                .operatorId(audit.getOperatorId())
                .requestId(audit.getRequestId())
                .source(audit.getSource())
                .build();
    }

    private static String toJsonContext(Map<String, Object> context) {
        if (context == null || context.isEmpty()) {
            return null;
        }
        return JacksonUtil.toJSONString(context);
    }

    private static Map<String, Object> parseJsonContext(String json) {
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
