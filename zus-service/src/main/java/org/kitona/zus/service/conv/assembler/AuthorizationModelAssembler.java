package org.kitona.zus.service.conv.assembler;

import org.kitona.zus.domain.authorization.model.AuthorizationModelAggregate;
import org.kitona.zus.domain.authorization.model.ConditionDefinition;
import org.kitona.zus.domain.authorization.model.TypeDefinition;
import org.kitona.zus.domain.read.view.AuthorizationModelView;
import org.kitona.zus.service.dto.response.AuthorizationModelResultDTO;
import org.kitona.zus.service.dto.response.AuthorizationTypeDefinitionResultDTO;
import org.kitona.zus.service.dto.response.ConditionDefinitionResultDTO;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权模型聚合根 Assembler
 *
 * <p>负责 {@link AuthorizationModelAggregate} 与 {@link AuthorizationModelResultDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，专注于字段映射，不包含序列化逻辑。
 * 序列化逻辑由 DTO 层自行处理（如 Jackson 注解或 getter 方法）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class AuthorizationModelAssembler {

    /**
     * 创建 AuthorizationModelAssembler 工具类私有构造方法，防止外部实例化。
     */
    private AuthorizationModelAssembler() {
    }

    /**
     * 将聚合根转换为 DTO
     *
     * @param aggregate 授权模型聚合根
     * @return 模型结果 DTO，aggregate 为 null 时返回 null
     */
    public static AuthorizationModelResultDTO toDTO(AuthorizationModelAggregate aggregate) {
        return toDTO(aggregate, null);
    }

    /**
     * 将聚合根转换为 DTO，并标记是否为当前生效模型
     *
     * @param aggregate      授权模型聚合根
     * @param currentModelId Store 当前生效的模型ID，用于判断 isCurrent
     * @return 模型结果 DTO，aggregate 为 null 时返回 null
     */
    public static AuthorizationModelResultDTO toDTO(AuthorizationModelAggregate aggregate, String currentModelId) {
        if (aggregate == null) {
            return null;
        }
        boolean isCurrent = currentModelId != null && currentModelId.equals(aggregate.getModelId());
        return AuthorizationModelResultDTO.builder()
                .modelId(aggregate.getModelId())
                .schemaVersion(aggregate.getSchemaVersion())
                .dslText(aggregate.getDslText())
                .status(aggregate.getStatusValue())
                .description(aggregate.getDescription())
                .typeDefinitions(convertTypeDefinitions(aggregate.getTypeDefinitions()))
                .conditionDefinitions(convertConditionDefinitions(aggregate.getConditionDefinitions()))
                .createTime(aggregate.getCreateTime())
                .isCurrent(isCurrent)
                .build();
    }

    /**
     * 将读侧视图转换为 DTO，并标记是否为当前生效模型
     *
     * @param view           授权模型读侧视图
     * @param currentModelId Store 当前生效的模型ID，用于判断 isCurrent
     * @return 模型结果 DTO
     */
    public static AuthorizationModelResultDTO toDTO(AuthorizationModelView view, String currentModelId) {
        if (view == null) {
            return null;
        }
        boolean isCurrent = currentModelId != null && currentModelId.equals(view.modelId());
        return AuthorizationModelResultDTO.builder()
                .modelId(view.modelId())
                .schemaVersion(view.schemaVersion())
                .dslText(view.dslText())
                .status(view.status())
                .description(view.description())
                .typeDefinitions(Collections.emptyMap())
                .conditionDefinitions(Collections.emptyMap())
                .createTime(view.createTime())
                .isCurrent(isCurrent)
                .build();
    }

    /**
     * 批量将聚合根转换为 DTO
     *
     * @param aggregates 授权模型聚合根列表
     * @return DTO 列表，aggregates 为 null 或空时返回空列表
     */
    public static List<AuthorizationModelResultDTO> toDTOList(List<AuthorizationModelAggregate> aggregates) {
        return toDTOList(aggregates, null);
    }

    /**
     * 批量将聚合根转换为 DTO，并标记是否为当前生效模型
     *
     * @param aggregates     授权模型聚合根列表
     * @param currentModelId Store 当前生效的模型ID，用于判断 isCurrent
     * @return DTO 列表，aggregates 为 null 或空时返回空列表
     */
    public static List<AuthorizationModelResultDTO> toDTOList(List<AuthorizationModelAggregate> aggregates, String currentModelId) {
        if (aggregates == null || aggregates.isEmpty()) {
            return Collections.emptyList();
        }
        return aggregates.stream()
                .map(agg -> toDTO(agg, currentModelId))
                .toList();
    }

    /**
     * 批量将读侧视图转换为 DTO，并标记是否为当前生效模型
     *
     * @param views          授权模型读侧视图列表
     * @param currentModelId Store 当前生效的模型ID
     * @return DTO 列表
     */
    public static List<AuthorizationModelResultDTO> toViewDTOList(List<AuthorizationModelView> views, String currentModelId) {
        if (views == null || views.isEmpty()) {
            return Collections.emptyList();
        }
        return views.stream()
                .map(view -> toDTO(view, currentModelId))
                .toList();
    }

    /**
     * 转换类型定义映射
     *
     * @param typeDefinitions 类型定义实体列表
     * @return 类型定义 DTO 映射
     */
    private static Map<String, AuthorizationTypeDefinitionResultDTO> convertTypeDefinitions(
            List<TypeDefinition> typeDefinitions) {
        if (typeDefinitions == null || typeDefinitions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, AuthorizationTypeDefinitionResultDTO> result = new LinkedHashMap<>();
        for (TypeDefinition typeDefinition : typeDefinitions) {
            result.put(typeDefinition.getSubjectType(), AuthorizationTypeDefinitionAssembler.toDTO(typeDefinition));
        }
        return result;
    }

    /**
     * 转换条件定义映射
     */
    private static Map<String, ConditionDefinitionResultDTO> convertConditionDefinitions(
            List<ConditionDefinition> conditionDefinitions) {
        if (conditionDefinitions == null || conditionDefinitions.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, ConditionDefinitionResultDTO> result = new LinkedHashMap<>();
        for (ConditionDefinition def : conditionDefinitions) {
            result.put(def.getConditionName(), ConditionDefinitionResultDTO.builder()
                        .id(def.getId())
                        .name(def.getConditionName())
                        .expression(def.getExpression())
                        .parameterSchema(def.getParameterSchema())
                        .description(def.getDescription())
                        .build());
        }
        return result;
    }
}
