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
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

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
@Mapper(componentModel = "spring")
public interface FgaModelConverter {

    FgaModelConverter INSTANCE = Mappers.getMapper(FgaModelConverter.class);

    String DEFAULT_SCHEMA_VERSION = "1.1";
    String UNKNOWN_STATUS_DESC = "UNKNOWN";
    String RESTRICTION_CONDITION_SEPARATOR = " with ";
    String TYPE_WILDCARD_SUFFIX = ":*";
    String USERSET_RELATION_SEPARATOR = "#";

    TypeReference<Map<String, String>> PARAMETER_SCHEMA_TYPE =
            new TypeReference<>() {
            };

    // =========================== API → Service ===========================

    /**
     * 将 API 写模型请求转为创建模型命令。
     *
     * <p>当前仅支持 Schema 模式（typeDefinitions + conditions）。DSL 模式由上层
     * 在调用此方法前拒绝（目前尚未实现 DSL 解析器）。
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "schemaVersion", source = "request", qualifiedByName = "resolveSchemaVersion")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "typeDefinitions", source = "request.typeDefinitions")
    @Mapping(target = "conditions", source = "request.conditions")
    CreateModelCommand toCreateModelCommand(String storeId, FgaWriteAuthorizationModelRequest request);

    /**
     * 将旧版模型创建请求转为创建模型命令。
     *
     * <p>该方法用于兼容当前 {@code IFgaModelApiService#createModel} 的入参结构。
     * 转换语义仍集中在本 converter 中，避免 controller 承担模型组装细节。
     */
    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "schemaVersion", source = "request", qualifiedByName = "resolveLegacySchemaVersion")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "typeDefinitions", source = "request.types")
    @Mapping(target = "conditions", source = "request.conditions")
    CreateModelCommand toCreateModelCommand(String storeId,
                                            org.kitona.zus.api.request.FgaCreateModelRequest request);

    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "pageToken", source = "pageToken")
    ListModelsQuery toListModelsQuery(String storeId, Integer status, Integer pageSize, String pageToken);

    @Named("resolveSchemaVersion")
    default String resolveSchemaVersion(FgaWriteAuthorizationModelRequest request) {
        if (request == null || StringUtils.isBlank(request.getSchemaVersion())) {
            return DEFAULT_SCHEMA_VERSION;
        }
        return request.getSchemaVersion();
    }

    @Named("resolveLegacySchemaVersion")
    default String resolveSchemaVersion(org.kitona.zus.api.request.FgaCreateModelRequest request) {
        if (request == null || StringUtils.isBlank(request.getSchemaVersion())) {
            return DEFAULT_SCHEMA_VERSION;
        }
        return request.getSchemaVersion();
    }

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.TypeDefinitionInput> toTypeDefinitionInputs(List<FgaTypeDefinitionInput> apiInputs);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "relations", source = "relations")
    CreateModelCommand.TypeDefinitionInput toTypeDefinitionInput(FgaTypeDefinitionInput api);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.RelationInput> toRelationInputs(List<FgaRelationDefinitionInput> apiInputs);

    @Mapping(target = "relationName", source = "name")
    @Mapping(target = "rewriteExpression", source = "rewriteExpression")
    @Mapping(target = "allowedSubjectTypes", source = "restrictions", qualifiedByName = "encodeRestrictions")
    CreateModelCommand.RelationInput toRelationInput(FgaRelationDefinitionInput api);

    /**
     * 将 API 的结构化限制编码为字符串列表，便于服务层 / 领域层存储。
     */
    @Named("encodeRestrictions")
    default List<String> encodeRestrictions(List<FgaTypeRestrictionInput> apiRestrictions) {
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

    private String encodeRestriction(FgaTypeRestrictionInput r) {
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

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.ConditionDefinitionInput> toConditionDefinitionInputs(
            List<FgaConditionDefinitionInput> apiInputs);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "expression", source = "expression")
    @Mapping(target = "parameterSchema", source = "parameterSchema", qualifiedByName = "toJsonParameterSchema")
    @Mapping(target = "description", source = "description")
    CreateModelCommand.ConditionDefinitionInput toConditionDefinitionInput(FgaConditionDefinitionInput api);

    @Named("toJsonParameterSchema")
    default String toJsonParameterSchema(Map<String, String> parameterSchema) {
        if (parameterSchema == null || parameterSchema.isEmpty()) {
            return null;
        }
        return JacksonUtil.toJSONString(parameterSchema);
    }

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.TypeDefinitionInput> toLegacyTypeDefinitionInputs(
            List<org.kitona.zus.api.request.FgaTypeDefinitionInput> apiInputs);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "relations", source = "relations")
    CreateModelCommand.TypeDefinitionInput toLegacyTypeDefinitionInput(
            org.kitona.zus.api.request.FgaTypeDefinitionInput api);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.RelationInput> toLegacyRelationInputs(
            List<org.kitona.zus.api.request.FgaRelationDefinitionInput> apiInputs);

    @Mapping(target = "relationName", source = "name")
    @Mapping(target = "rewriteExpression", source = "rewriteExpression")
    @Mapping(target = "allowedSubjectTypes", source = "restrictions", qualifiedByName = "encodeLegacyRestrictions")
    CreateModelCommand.RelationInput toLegacyRelationInput(
            org.kitona.zus.api.request.FgaRelationDefinitionInput api);

    @Named("encodeLegacyRestrictions")
    default List<String> encodeLegacyRestrictions(
            List<org.kitona.zus.api.request.FgaTypeRestrictionInput> apiRestrictions) {
        if (CollectionUtils.isEmpty(apiRestrictions)) {
            return Collections.emptyList();
        }
        List<String> encoded = new ArrayList<>(apiRestrictions.size());
        for (org.kitona.zus.api.request.FgaTypeRestrictionInput restriction : apiRestrictions) {
            String value = encodeLegacyRestriction(restriction);
            if (StringUtils.isNotBlank(value)) {
                encoded.add(value);
            }
        }
        return encoded;
    }

    private String encodeLegacyRestriction(org.kitona.zus.api.request.FgaTypeRestrictionInput restriction) {
        if (restriction == null || StringUtils.isBlank(restriction.getType())) {
            return null;
        }
        if (StringUtils.isBlank(restriction.getRelation())) {
            return restriction.getType();
        }
        return restriction.getType() + USERSET_RELATION_SEPARATOR + restriction.getRelation();
    }

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<CreateModelCommand.ConditionDefinitionInput> toLegacyConditionDefinitionInputs(
            List<org.kitona.zus.api.request.FgaConditionDefinitionInput> apiInputs);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "expression", source = "expression")
    @Mapping(target = "parameterSchema", source = "parameterSchema", qualifiedByName = "toJsonParameterSchema")
    @Mapping(target = "description", source = "description")
    CreateModelCommand.ConditionDefinitionInput toLegacyConditionDefinitionInput(
            org.kitona.zus.api.request.FgaConditionDefinitionInput api);

    // =========================== Service → API ===========================

    @Mapping(target = "modelId", source = "modelId")
    @Mapping(target = "schemaVersion", source = "schemaVersion")
    @Mapping(target = "dslText", source = "dslText")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "statusDesc", source = "status", qualifiedByName = "statusDesc")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "types", source = "typeDefinitions")
    @Mapping(target = "conditions", source = "conditionDefinitions")
    @Mapping(target = "createTime", source = "createTime")
    @Mapping(target = "publishTime", ignore = true)
    @Mapping(target = "isCurrent", source = "isCurrent")
    FgaModelVO toVO(AuthorizationModelResultDTO dto);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaModelVO> toVOList(List<AuthorizationModelResultDTO> dtoList);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaTypeDefinitionVO> toTypeDefinitionVOList(List<AuthorizationTypeDefinitionResultDTO> list);

    @Mapping(target = "type", source = "type")
    @Mapping(target = "relations", source = ".", qualifiedByName = "relationVOList")
    FgaTypeDefinitionVO toTypeDefinitionVO(AuthorizationTypeDefinitionResultDTO dto);

    @Named("relationVOList")
    default List<FgaRelationVO> toRelationVOList(AuthorizationTypeDefinitionResultDTO dto) {
        Map<String, String> relations = dto.getRelations();
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, List<String>> restrictions = dto.getRelationRestrictions();
        return relations.entrySet().stream()
                .map(entry -> toRelationVO(entry.getKey(), entry.getValue(),
                        restrictions == null ? null : restrictions.get(entry.getKey())))
                .toList();
    }

    @Mapping(target = "name", source = "name")
    @Mapping(target = "rewriteExpression", source = "rewriteExpression")
    @Mapping(target = "relationType", source = "rewriteExpression", qualifiedByName = "relationType")
    @Mapping(target = "restrictions", source = "restrictions", qualifiedByName = "decodeRestrictions")
    FgaRelationVO toRelationVO(String name, String rewriteExpression, List<String> restrictions);

    @Named("decodeRestrictions")
    default List<FgaTypeRestrictionVO> decodeRestrictions(List<String> encoded) {
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
    private FgaTypeRestrictionVO decodeRestriction(String raw) {
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
        String type;
        String relation = null;
        Boolean wildcard = null;
        if (core.endsWith(TYPE_WILDCARD_SUFFIX)) {
            type = core.substring(0, core.length() - TYPE_WILDCARD_SUFFIX.length());
            wildcard = Boolean.TRUE;
        } else {
            int idx = core.indexOf(USERSET_RELATION_SEPARATOR);
            if (idx > 0) {
                type = core.substring(0, idx);
                relation = core.substring(idx + 1);
            } else {
                type = core;
            }
        }
        return toTypeRestrictionVO(new DecodedRestriction(type, relation, wildcard,
                StringUtils.isBlank(condition) ? null : condition));
    }

    @Mapping(target = "type", source = "type")
    @Mapping(target = "relation", source = "relation")
    @Mapping(target = "wildcard", source = "wildcard")
    @Mapping(target = "condition", source = "condition")
    FgaTypeRestrictionVO toTypeRestrictionVO(DecodedRestriction restriction);

    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    List<FgaConditionVO> toConditionVOList(List<ConditionDefinitionResultDTO> list);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "expression", source = "expression")
    @Mapping(target = "parameterSchema", source = "parameterSchema", qualifiedByName = "parseParameterSchema")
    @Mapping(target = "description", source = "description")
    FgaConditionVO toConditionVO(ConditionDefinitionResultDTO dto);

    @Named("parseParameterSchema")
    default Map<String, String> parseParameterSchema(String json) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            return JacksonUtil.parseJSONArray(json, PARAMETER_SCHEMA_TYPE);
        } catch (Exception ex) {
            return null;
        }
    }

    @Named("statusDesc")
    default String statusDesc(Integer status) {
        if (status == null) {
            return null;
        }
        ModelPublishStatus publishStatus = ModelPublishStatus.fromStatus(status);
        return publishStatus != null ? publishStatus.name() : UNKNOWN_STATUS_DESC;
    }

    @Named("relationType")
    default Integer relationType(String rewriteExpression) {
        return FgaRelationType.deriveCode(rewriteExpression);
    }

    /**
     * restriction 字符串解析后的中间结构，用于把解析职责和 VO 字段映射职责分开。
     *
     * @param type      主体类型
     * @param relation  userset relation
     * @param wildcard  是否为 wildcard subject
     * @param condition 条件名称
     */
    record DecodedRestriction(String type, String relation, Boolean wildcard, String condition) {
    }
}
