package org.kitona.zus.api.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.api.enums.FgaRelationType;
import org.kitona.zus.api.request.model.FgaConditionDefinitionInput;
import org.kitona.zus.api.request.model.FgaRelationDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.response.FgaConditionVO;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaRelationVO;
import org.kitona.zus.api.response.FgaTypeDefinitionVO;
import org.kitona.zus.api.response.FgaTypeRestrictionVO;
import org.kitona.zus.common.utils.JacksonUtil;
import org.kitona.zus.domain.enums.ModelPublishStatus;
import org.kitona.zus.service.dto.command.CreateModelCommand;
import org.kitona.zus.service.dto.query.ListModelsQuery;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;
import org.kitona.zus.service.dto.response.ConditionDefinitionResultDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 授权模型 API ↔ Service 转换器。
 *
 * <p>负责 {@link FgaWriteAuthorizationModelRequest} ↔ {@link CreateModelCommand}、
 * {@link AuthorizationModelResultDTO} ↔ {@link FgaModelVO} 的双向转换。
 *
 * <p>关键映射决策：
 * <ul>
 *   <li><b>restrictions</b>：API 的结构化 {@link FgaTypeRestrictionInput} 编码为
 *       OpenFGA DSL 字符串（{@code user} / {@code group#member} / {@code user:*} /
 *       {@code user with is_working_hours}），以匹配
 *       {@link CreateModelCommand.RelationInput#getAllowedSubjectTypes()} 的
 *       {@code List<String>} 约束；反向时解析字符串还原结构化字段。</li>
 *   <li><b>parameterSchema</b>：API 使用 {@code Map<String,String>}；服务层/领域层
 *       使用原始 JSON 文本。这里统一在 API 层做 JSON ↔ Map 序列化。</li>
 *   <li><b>relationType</b>：仅在响应侧（{@link FgaRelationVO}）填充，根据
 *       rewrite 表达式粗判（和数据库 {@code relation_type} 一致的编码）。</li>
 * </ul>
 *
 * @author kitona
 * @since 2026-04-18
 */
public final class FgaModelConverter {

    private static final String DEFAULT_SCHEMA_VERSION = "1.1";
    private static final String UNKNOWN_STATUS_DESC = "UNKNOWN";
    private static final String RESTRICTION_CONDITION_SEPARATOR = " with ";
    private static final String TYPE_WILDCARD_SUFFIX = ":*";
    private static final String USERSET_RELATION_SEPARATOR = "#";

    private static final TypeReference<Map<String, String>> PARAMETER_SCHEMA_TYPE =
            new TypeReference<>() {
            };

    private FgaModelConverter() {
    }

    // =========================== API → Service ===========================

    /**
     * 将 API 写模型请求转为创建模型命令。
     *
     * <p>当前仅支持 Schema 模式（typeDefinitions + conditions）。DSL 模式由上层
     * 在调用此方法前拒绝（目前尚未实现 DSL 解析器）。
     */
    public static CreateModelCommand toCreateModelCommand(String storeId,
                                                          FgaWriteAuthorizationModelRequest request) {
        return CreateModelCommand.builder()
                .storeId(storeId)
                .schemaVersion(resolveSchemaVersion(request))
                .description(request != null ? request.getDescription() : null)
                .typeDefinitions(toTypeDefinitionInputs(request != null ? request.getTypeDefinitions() : null))
                .conditions(toConditionDefinitionInputs(request != null ? request.getConditions() : null))
                .build();
    }

    public static ListModelsQuery toListModelsQuery(String storeId, Integer status,
                                                    Integer pageSize, String pageToken) {
        return ListModelsQuery.builder()
                .storeId(storeId)
                .status(status)
                .pageSize(pageSize)
                .pageToken(pageToken)
                .build();
    }

    private static String resolveSchemaVersion(FgaWriteAuthorizationModelRequest request) {
        if (request == null || StringUtils.isBlank(request.getSchemaVersion())) {
            return DEFAULT_SCHEMA_VERSION;
        }
        return request.getSchemaVersion();
    }

    private static List<CreateModelCommand.TypeDefinitionInput> toTypeDefinitionInputs(
            List<FgaTypeDefinitionInput> apiInputs) {
        if (CollectionUtils.isEmpty(apiInputs)) {
            return Collections.emptyList();
        }
        return apiInputs.stream()
                .map(FgaModelConverter::toTypeDefinitionInput)
                .toList();
    }

    private static CreateModelCommand.TypeDefinitionInput toTypeDefinitionInput(FgaTypeDefinitionInput api) {
        return CreateModelCommand.TypeDefinitionInput.builder()
                .type(api.getType())
                .relations(toRelationInputs(api.getRelations()))
                .build();
    }

    private static List<CreateModelCommand.RelationInput> toRelationInputs(List<FgaRelationDefinitionInput> apiInputs) {
        if (CollectionUtils.isEmpty(apiInputs)) {
            return Collections.emptyList();
        }
        return apiInputs.stream()
                .map(FgaModelConverter::toRelationInput)
                .toList();
    }

    private static CreateModelCommand.RelationInput toRelationInput(FgaRelationDefinitionInput api) {
        return CreateModelCommand.RelationInput.builder()
                .relationName(api.getName())
                .rewriteExpression(api.getRewriteExpression())
                .allowedSubjectTypes(encodeRestrictions(api.getRestrictions()))
                .build();
    }

    /**
     * 将 API 的结构化限制编码为字符串列表，便于服务层 / 领域层存储。
     */
    private static List<String> encodeRestrictions(List<FgaTypeRestrictionInput> apiRestrictions) {
        if (CollectionUtils.isEmpty(apiRestrictions)) {
            return Collections.emptyList();
        }
        List<String> encoded = new ArrayList<>(apiRestrictions.size());
        for (FgaTypeRestrictionInput r : apiRestrictions) {
            String s = encodeRestriction(r);
            if (StringUtils.isNotBlank(s)) {
                encoded.add(s);
            }
        }
        return encoded;
    }

    private static String encodeRestriction(FgaTypeRestrictionInput r) {
        if (r == null || StringUtils.isBlank(r.getType())) {
            return null;
        }
        String base;
        if (Boolean.TRUE.equals(r.getWildcard())) {
            base = r.getType() + TYPE_WILDCARD_SUFFIX;
        } else if (StringUtils.isNotBlank(r.getRelation())) {
            base = r.getType() + USERSET_RELATION_SEPARATOR + r.getRelation();
        } else {
            base = r.getType();
        }
        if (StringUtils.isNotBlank(r.getCondition())) {
            return base + RESTRICTION_CONDITION_SEPARATOR + r.getCondition();
        }
        return base;
    }

    private static List<CreateModelCommand.ConditionDefinitionInput> toConditionDefinitionInputs(
            List<FgaConditionDefinitionInput> apiInputs) {
        if (CollectionUtils.isEmpty(apiInputs)) {
            return Collections.emptyList();
        }
        return apiInputs.stream()
                .map(FgaModelConverter::toConditionDefinitionInput)
                .toList();
    }

    private static CreateModelCommand.ConditionDefinitionInput toConditionDefinitionInput(FgaConditionDefinitionInput api) {
        String schemaJson = null;
        if (api.getParameterSchema() != null && !api.getParameterSchema().isEmpty()) {
            schemaJson = JacksonUtil.toJSONString(api.getParameterSchema());
        }
        return CreateModelCommand.ConditionDefinitionInput.builder()
                .name(api.getName())
                .expression(api.getExpression())
                .parameterSchema(schemaJson)
                .description(api.getDescription())
                .build();
    }

    // =========================== Service → API ===========================

    public static FgaModelVO toVO(AuthorizationModelResultDTO dto) {
        if (dto == null) {
            return null;
        }
        FgaModelVO vo = new FgaModelVO();
        vo.setModelId(dto.getModelId());
        vo.setSchemaVersion(dto.getSchemaVersion());
        vo.setDslText(dto.getDslText());
        vo.setStatus(dto.getStatus());
        vo.setStatusDesc(statusDesc(dto.getStatus()));
        vo.setDescription(dto.getDescription());
        vo.setTypes(toTypeDefinitionVOList(dto.getTypeDefinitions()));
        vo.setConditions(toConditionVOList(dto.getConditionDefinitions()));
        vo.setCreateTime(dto.getCreateTime());
        vo.setIsCurrent(dto.getIsCurrent());
        return vo;
    }

    public static List<FgaModelVO> toVOList(List<AuthorizationModelResultDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(FgaModelConverter::toVO).toList();
    }

    private static List<FgaTypeDefinitionVO> toTypeDefinitionVOList(List<AuthorizationTypeDefinitionResultDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<FgaTypeDefinitionVO> result = new ArrayList<>(list.size());
        for (AuthorizationTypeDefinitionResultDTO dto : list) {
            FgaTypeDefinitionVO vo = new FgaTypeDefinitionVO();
            vo.setType(dto.getType());
            vo.setRelations(toRelationVOList(dto));
            result.add(vo);
        }
        return result;
    }

    private static List<FgaRelationVO> toRelationVOList(AuthorizationTypeDefinitionResultDTO dto) {
        Map<String, String> relations = dto.getRelations();
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, List<String>> restrictions = dto.getRelationRestrictions();
        List<FgaRelationVO> list = new ArrayList<>(relations.size());
        for (Map.Entry<String, String> e : relations.entrySet()) {
            FgaRelationVO vo = new FgaRelationVO();
            vo.setName(e.getKey());
            vo.setRewriteExpression(e.getValue());
            vo.setRelationType(FgaRelationType.deriveCode(e.getValue()));
            if (restrictions != null) {
                vo.setRestrictions(decodeRestrictions(restrictions.get(e.getKey())));
            }
            list.add(vo);
        }
        return list;
    }

    private static List<FgaTypeRestrictionVO> decodeRestrictions(List<String> encoded) {
        if (CollectionUtils.isEmpty(encoded)) {
            return Collections.emptyList();
        }
        List<FgaTypeRestrictionVO> out = new ArrayList<>(encoded.size());
        for (String s : encoded) {
            FgaTypeRestrictionVO vo = decodeRestriction(s);
            if (vo != null) {
                out.add(vo);
            }
        }
        return out;
    }

    /**
     * 解析 OpenFGA DSL 限制字符串为结构化 VO。
     * <p>支持：{@code user} / {@code user:*} / {@code group#member} / {@code user with cond}。
     */
    private static FgaTypeRestrictionVO decodeRestriction(String raw) {
        if (StringUtils.isBlank(raw)) {
            return null;
        }
        String core = raw;
        String condition = null;
        int withIdx = raw.indexOf(RESTRICTION_CONDITION_SEPARATOR);
        if (withIdx > 0) {
            core = raw.substring(0, withIdx).trim();
            condition = raw.substring(withIdx + RESTRICTION_CONDITION_SEPARATOR.length()).trim();
        }
        FgaTypeRestrictionVO.FgaTypeRestrictionVOBuilder builder = FgaTypeRestrictionVO.builder()
                .condition(StringUtils.isBlank(condition) ? null : condition);
        if (core.endsWith(TYPE_WILDCARD_SUFFIX)) {
            builder.type(core.substring(0, core.length() - TYPE_WILDCARD_SUFFIX.length())).wildcard(Boolean.TRUE);
        } else {
            int idx = core.indexOf(USERSET_RELATION_SEPARATOR);
            if (idx > 0) {
                builder.type(core.substring(0, idx)).relation(core.substring(idx + 1));
            } else {
                builder.type(core);
            }
        }
        return builder.build();
    }

    private static List<FgaConditionVO> toConditionVOList(List<ConditionDefinitionResultDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        List<FgaConditionVO> out = new ArrayList<>(list.size());
        for (ConditionDefinitionResultDTO dto : list) {
            FgaConditionVO vo = new FgaConditionVO();
            vo.setName(dto.getName());
            vo.setExpression(dto.getExpression());
            vo.setParameterSchema(parseParameterSchema(dto.getParameterSchema()));
            vo.setDescription(dto.getDescription());
            out.add(vo);
        }
        return out;
    }

    private static Map<String, String> parseParameterSchema(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return JacksonUtil.parseJSONArray(json, PARAMETER_SCHEMA_TYPE);
        } catch (Exception ex) {
            return null;
        }
    }

    private static String statusDesc(Integer status) {
        if (status == null) {
            return null;
        }
        ModelPublishStatus publishStatus = ModelPublishStatus.fromStatus(status);
        return publishStatus != null ? publishStatus.name() : UNKNOWN_STATUS_DESC;
    }
}
