package org.kitona.zus.business.entity.antlr4;

import org.kitona.zus.business.entity.bo.AuthorizationModel;
import org.kitona.zus.business.entity.bo.TypeDefinition;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TTU (Tuple To Userset) 辅助类
 * 负责从授权模型中 Type Restrictions 提取 TTU 目标类型。
 */
public class TtuHelper {

    private final Map<String, TypeDefinition> typeMap;

    public TtuHelper(AuthorizationModel model) {
        this.typeMap = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));
    }

    /**
     * OpenFGA 标准逻辑：根据 Type Restrictions 规则，推断 TTU 依赖的目标资源类型。
     *
     * @param tupleKeyRelation    元组关系名 (e.g., parentFolder)。
     * @param currentResourceType 当前资源类型 (e.g., document)。
     * @return TTU 依赖的父级资源类型 (e.g., folder)。
     */
    public String extractTargetType(String tupleKeyRelation, String currentResourceType) {
        TypeDefinition currentTypeDefinition = typeMap.get(currentResourceType);
        if (currentTypeDefinition == null) {
            return null;
        }

        // 1. 获取 tupleKeyRelation (parentFolder) 上的所有 Type Restrictions
        Set<String> restrictions = currentTypeDefinition.getRestrictionsForRelation(tupleKeyRelation);

        // 2. 遍历 Type Restrictions，提取 Type
        for (String restriction : restrictions) {
            // Restriction 可以是 "folder" 或 "group#member"
            if (restriction.contains("#")) {
                // 如果是 type#relation 格式 (e.g., group#member)，返回 type 部分
                return restriction.split("#")[0];
            } else {
                // 如果是 type 格式 (e.g., folder)，直接返回
                return restriction;
            }
        }

        return null;
    }
}