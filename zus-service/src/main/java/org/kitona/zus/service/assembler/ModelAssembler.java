package org.kitona.zus.service.assembler;

import org.kitona.zus.domain.aggregate.AuthorizationModelAggregate;
import org.kitona.zus.domain.entity.TypeDefinitionEntity;
import org.kitona.zus.service.dto.response.ModelResultDTO;
import org.kitona.zus.service.dto.response.TypeDefinitionResultDTO;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 授权模型聚合根 Assembler
 *
 * <p>负责 {@link AuthorizationModelAggregate} 与 {@link ModelResultDTO} 之间的转换。
 * <p>DDD 规范：Assembler 位于应用服务层，专注于字段映射，不包含序列化逻辑。
 * 序列化逻辑由 DTO 层自行处理（如 Jackson 注解或 getter 方法）。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-26
 */
public final class ModelAssembler {

    private ModelAssembler() {
    }

    /**
     * 将聚合根转换为 DTO
     *
     * @param aggregate 授权模型聚合根
     * @return 模型结果 DTO，aggregate 为 null 时返回 null
     */
    public static ModelResultDTO toDTO(AuthorizationModelAggregate aggregate) {
        return toDTO(aggregate, null);
    }

    /**
     * 将聚合根转换为 DTO，并标记是否为当前生效模型
     *
     * @param aggregate      授权模型聚合根
     * @param currentModelId Store 当前生效的模型ID，用于判断 isCurrent
     * @return 模型结果 DTO，aggregate 为 null 时返回 null
     */
    public static ModelResultDTO toDTO(AuthorizationModelAggregate aggregate, String currentModelId) {
        if (aggregate == null) {
            return null;
        }
        boolean isCurrent = currentModelId != null && currentModelId.equals(aggregate.getModelId());
        return ModelResultDTO.builder()
                .modelId(aggregate.getModelId())
                .schemaVersion(aggregate.getSchemaVersion())
                .dslText(aggregate.getDslText())
                .status(aggregate.getStatusValue())
                .description(aggregate.getDescription())
                .typeDefinitions(convertTypeDefinitions(aggregate.getTypeDefinitions()))
                .createTime(aggregate.getCreateTime())
                .isCurrent(isCurrent)
                .build();
    }

    /**
     * 批量将聚合根转换为 DTO
     *
     * @param aggregates 授权模型聚合根列表
     * @return DTO 列表，aggregates 为 null 或空时返回空列表
     */
    public static List<ModelResultDTO> toDTOList(List<AuthorizationModelAggregate> aggregates) {
        return toDTOList(aggregates, null);
    }

    /**
     * 批量将聚合根转换为 DTO，并标记是否为当前生效模型
     *
     * @param aggregates     授权模型聚合根列表
     * @param currentModelId Store 当前生效的模型ID，用于判断 isCurrent
     * @return DTO 列表，aggregates 为 null 或空时返回空列表
     */
    public static List<ModelResultDTO> toDTOList(List<AuthorizationModelAggregate> aggregates, String currentModelId) {
        if (aggregates == null || aggregates.isEmpty()) {
            return Collections.emptyList();
        }
        return aggregates.stream()
                .map(agg -> toDTO(agg, currentModelId))
                .collect(Collectors.toList());
    }

    /**
     * 转换类型定义列表
     *
     * @param typeDefinitions 类型定义实体列表
     * @return 类型定义 DTO 列表
     */
    private static List<TypeDefinitionResultDTO> convertTypeDefinitions(List<TypeDefinitionEntity> typeDefinitions) {
        if (typeDefinitions == null || typeDefinitions.isEmpty()) {
            return Collections.emptyList();
        }
        return TypeDefinitionAssembler.toDTOList(typeDefinitions);
    }
}
