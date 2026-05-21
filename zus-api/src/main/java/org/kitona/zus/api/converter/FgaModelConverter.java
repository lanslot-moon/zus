package org.kitona.zus.api.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.api.enums.FgaRelationType;
import org.kitona.zus.api.request.model.FgaConditionSchemaInput;
import org.kitona.zus.api.request.model.FgaRelationSchemaInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaTypeSchemaInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.response.FgaConditionSchemaVO;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaRelationSchemaVO;
import org.kitona.zus.api.response.FgaTypeRestrictionVO;
import org.kitona.zus.api.response.FgaTypeSchemaVO;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 授权模型 API 与应用层 DTO 转换器。
 *
 * <p>WriteAuthorizationModel 的标准入口采用 Map-keyed schema：type、relation、condition 的名称只来自
 * Map key，value 只描述该节点的结构，避免请求体内出现两个名称来源后产生语义漂移。
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
    Pattern MODEL_KEY_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9_]{0,63}");

    TypeReference<Map<String, String>> PARAMETER_SCHEMA_TYPE =
            new TypeReference<>() {
            };

    /**
     * 将写模型请求转换为创建模型命令。
     *
     * @param storeId store 标识
     * @param request API 写模型请求
     * @return 创建模型命令
     */
    default CreateModelCommand toCreateModelCommand(String storeId, FgaWriteAuthorizationModelRequest request) {
        if (request == null) {
            return CreateModelCommand.builder().storeId(storeId).schemaVersion(DEFAULT_SCHEMA_VERSION).build();
        }
        return CreateModelCommand.builder()
                .storeId(storeId)
                .schemaVersion(resolveSchemaVersion(request))
                .description(request.getDescription())
                .types(toTypeDefinitionInputs(request.getTypes()))
                .conditions(toConditionDefinitionInputs(request.getConditions()))
                .build();
    }

    @Mapping(target = "storeId", source = "storeId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "pageSize", source = "pageSize")
    @Mapping(target = "pageToken", source = "pageToken")
    ListModelsQuery toListModelsQuery(String storeId, Integer status, Integer pageSize, String pageToken);

    /**
     * 解析 schema version，未传时使用 OpenFGA 1.1 风格默认版本。
     *
     * @param request 写模型请求
     * @return schema version
     */
    @Named("resolveSchemaVersion")
    default String resolveSchemaVersion(FgaWriteAuthorizationModelRequest request) {
        if (request == null || StringUtils.isBlank(request.getSchemaVersion())) {
            return DEFAULT_SCHEMA_VERSION;
        }
        return request.getSchemaVersion();
    }

    /**
     * 转换 type 定义映射。
     *
     * @param apiInputs API type schema 映射
     * @return 应用层 type 输入映射
     */
    default Map<String, CreateModelCommand.TypeDefinitionInput> toTypeDefinitionInputs(
            Map<String, FgaTypeSchemaInput> apiInputs) {
        if (apiInputs == null || apiInputs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, CreateModelCommand.TypeDefinitionInput> result = new LinkedHashMap<>();
        apiInputs.forEach((typeName, input) -> {
            validateModelKey("type", typeName);
            result.put(typeName, CreateModelCommand.TypeDefinitionInput.builder()
                    .relations(toRelationInputs(input == null ? null : input.getRelations()))
                    .build());
        });
        return result;
    }

    /**
     * 转换 relation 定义映射。
     *
     * @param apiInputs API relation schema 映射
     * @return 应用层 relation 输入映射
     */
    default Map<String, CreateModelCommand.RelationInput> toRelationInputs(Map<String, FgaRelationSchemaInput> apiInputs) {
        if (apiInputs == null || apiInputs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, CreateModelCommand.RelationInput> result = new LinkedHashMap<>();
        apiInputs.forEach((relationName, input) -> {
            validateModelKey("relation", relationName);
            result.put(relationName, CreateModelCommand.RelationInput.builder()
                    .rewriteExpression(input == null ? null : input.getRewrite())
                    .allowedSubjectTypes(encodeRestrictions(input == null ? null : input.getAllowedSubjectTypes()))
                    .build());
        });
        return result;
    }

    /**
     * 转换 condition 定义映射。
     *
     * @param apiInputs API condition schema 映射
     * @return 应用层 condition 输入映射
     */
    default Map<String, CreateModelCommand.ConditionDefinitionInput> toConditionDefinitionInputs(
            Map<String, FgaConditionSchemaInput> apiInputs) {
        if (apiInputs == null || apiInputs.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, CreateModelCommand.ConditionDefinitionInput> result = new LinkedHashMap<>();
        apiInputs.forEach((conditionName, input) -> {
            validateModelKey("condition", conditionName);
            result.put(conditionName, CreateModelCommand.ConditionDefinitionInput.builder()
                    .expression(input == null ? null : input.getExpression())
                    .parameterSchema(toJsonParameterSchema(input == null ? null : input.getParameterSchema()))
                    .description(input == null ? null : input.getDescription())
                    .build());
        });
        return result;
    }

    /**
     * 将 API 的结构化限制编码为领域侧使用的 OpenFGA 限制字符串。
     *
     * @param apiRestrictions API 限制列表
     * @return 编码后的限制列表
     */
    @Named("encodeRestrictions")
    default List<String> encodeRestrictions(List<FgaTypeRestrictionInput> apiRestrictions) {
        if (CollectionUtils.isEmpty(apiRestrictions)) {
            return Collections.emptyList();
        }
        List<String> encoded = new ArrayList<>(apiRestrictions.size());
        for (FgaTypeRestrictionInput restriction : apiRestrictions) {
            String value = encodeRestriction(restriction);
            if (StringUtils.isNotBlank(value)) {
                encoded.add(value);
            }
        }
        return encoded;
    }

    /**
     * 将参数结构序列化为 JSON 文本。
     *
     * @param parameterSchema 参数结构
     * @return JSON 文本
     */
    @Named("toJsonParameterSchema")
    default String toJsonParameterSchema(Map<String, String> parameterSchema) {
        if (parameterSchema == null || parameterSchema.isEmpty()) {
            return null;
        }
        return JacksonUtil.toJSONString(parameterSchema);
    }

    /**
     * 将应用层模型结果转换为 API VO。
     *
     * @param dto 应用层模型结果
     * @return API 模型 VO
     */
    default FgaModelVO toVO(AuthorizationModelResultDTO dto) {
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
        vo.setTypes(toTypeSchemaVOMap(dto.getTypeDefinitions()));
        vo.setConditions(toConditionSchemaVOMap(dto.getConditionDefinitions()));
        vo.setCreateTime(dto.getCreateTime());
        vo.setIsCurrent(dto.getIsCurrent());
        return vo;
    }

    /**
     * 批量转换模型结果。
     *
     * @param dtoList 应用层模型结果列表
     * @return API 模型 VO 列表
     */
    @IterableMapping(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
    default List<FgaModelVO> toVOList(List<AuthorizationModelResultDTO> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return Collections.emptyList();
        }
        return dtoList.stream().map(this::toVO).toList();
    }

    /**
     * 转换 type schema 响应映射。
     *
     * @param typeDefinitions 应用层 type 定义映射
     * @return API type schema 映射
     */
    default Map<String, FgaTypeSchemaVO> toTypeSchemaVOMap(
            Map<String, AuthorizationTypeDefinitionResultDTO> typeDefinitions) {
        if (typeDefinitions == null || typeDefinitions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, FgaTypeSchemaVO> result = new LinkedHashMap<>();
        typeDefinitions.forEach((typeName, dto) -> {
            FgaTypeSchemaVO vo = new FgaTypeSchemaVO();
            vo.setRelations(toRelationSchemaVOMap(dto == null ? null : dto.getRelations()));
            result.put(typeName, vo);
        });
        return result;
    }

    /**
     * 转换 relation schema 响应映射。
     *
     * @param relations 应用层 relation 定义映射
     * @return API relation schema 映射
     */
    default Map<String, FgaRelationSchemaVO> toRelationSchemaVOMap(
            Map<String, AuthorizationTypeDefinitionResultDTO.RelationDefinitionResultDTO> relations) {
        if (relations == null || relations.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, FgaRelationSchemaVO> result = new LinkedHashMap<>();
        relations.forEach((relationName, dto) -> {
            FgaRelationSchemaVO vo = new FgaRelationSchemaVO();
            String rewriteExpression = dto == null ? null : dto.getRewriteExpression();
            vo.setRewrite(rewriteExpression);
            vo.setRelationType(relationType(rewriteExpression));
            vo.setAllowedSubjectTypes(decodeRestrictions(dto == null ? null : dto.getRestrictions()));
            result.put(relationName, vo);
        });
        return result;
    }

    /**
     * 转换 condition schema 响应映射。
     *
     * @param conditions 应用层 condition 定义映射
     * @return API condition schema 映射
     */
    default Map<String, FgaConditionSchemaVO> toConditionSchemaVOMap(
            Map<String, ConditionDefinitionResultDTO> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, FgaConditionSchemaVO> result = new LinkedHashMap<>();
        conditions.forEach((conditionName, dto) -> {
            FgaConditionSchemaVO vo = new FgaConditionSchemaVO();
            vo.setExpression(dto == null ? null : dto.getExpression());
            vo.setParameterSchema(parseParameterSchema(dto == null ? null : dto.getParameterSchema()));
            vo.setDescription(dto == null ? null : dto.getDescription());
            result.put(conditionName, vo);
        });
        return result;
    }

    /**
     * 将限制字符串解析为结构化 VO。
     *
     * @param encoded 编码后的限制字符串列表
     * @return API 限制 VO 列表
     */
    @Named("decodeRestrictions")
    default List<FgaTypeRestrictionVO> decodeRestrictions(List<String> encoded) {
        if (CollectionUtils.isEmpty(encoded)) {
            return Collections.emptyList();
        }
        List<FgaTypeRestrictionVO> out = new ArrayList<>(encoded.size());
        for (String raw : encoded) {
            FgaTypeRestrictionVO vo = decodeRestriction(raw);
            if (vo != null) {
                out.add(vo);
            }
        }
        return out;
    }

    /**
     * 解析参数结构 JSON。
     *
     * @param json JSON 文本
     * @return 参数结构
     */
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

    /**
     * 转换模型状态描述。
     *
     * @param status 模型状态码
     * @return 状态描述
     */
    @Named("statusDesc")
    default String statusDesc(Integer status) {
        if (status == null) {
            return null;
        }
        ModelPublishStatus publishStatus = ModelPublishStatus.fromStatus(status);
        return publishStatus != null ? publishStatus.name() : UNKNOWN_STATUS_DESC;
    }

    /**
     * 根据 rewrite 表达式推导 relation 类型。
     *
     * @param rewriteExpression rewrite 表达式
     * @return relation 类型编码
     */
    @Named("relationType")
    default Integer relationType(String rewriteExpression) {
        return FgaRelationType.deriveCode(rewriteExpression);
    }

    private String encodeRestriction(FgaTypeRestrictionInput restriction) {
        if (restriction == null || StringUtils.isBlank(restriction.getType())) {
            return null;
        }
        String base;
        if (Boolean.TRUE.equals(restriction.getWildcard())) {
            base = restriction.getType() + TYPE_WILDCARD_SUFFIX;
        } else if (StringUtils.isNotBlank(restriction.getRelation())) {
            base = restriction.getType() + USERSET_RELATION_SEPARATOR + restriction.getRelation();
        } else {
            base = restriction.getType();
        }
        if (StringUtils.isNotBlank(restriction.getCondition())) {
            return base + RESTRICTION_CONDITION_SEPARATOR + restriction.getCondition();
        }
        return base;
    }

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
        FgaTypeRestrictionVO vo = new FgaTypeRestrictionVO();
        if (core.endsWith(TYPE_WILDCARD_SUFFIX)) {
            vo.setType(core.substring(0, core.length() - TYPE_WILDCARD_SUFFIX.length()));
            vo.setWildcard(Boolean.TRUE);
        } else {
            int idx = core.indexOf(USERSET_RELATION_SEPARATOR);
            if (idx > 0) {
                vo.setType(core.substring(0, idx));
                vo.setRelation(core.substring(idx + 1));
            } else {
                vo.setType(core);
            }
        }
        vo.setCondition(StringUtils.isBlank(condition) ? null : condition);
        return vo;
    }

    private void validateModelKey(String kind, String key) {
        if (StringUtils.isBlank(key) || !MODEL_KEY_PATTERN.matcher(key).matches()) {
            throw new IllegalArgumentException(kind + " key must match " + MODEL_KEY_PATTERN.pattern());
        }
    }
}
