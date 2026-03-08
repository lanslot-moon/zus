package org.kitona.zus.infrastructure.engine.parser;

import org.kitona.zus.domain.valueobject.AuthorizationModel;
import org.kitona.zus.domain.valueobject.TypeDefinition;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TTU (Tuple To Userset) 辅助类。负责从授权模型的 Type Restrictions 中提取 TTU 目标类型。
 */
public class TtuHelper {

    private final Map<String, TypeDefinition> typeMap;

    public TtuHelper(AuthorizationModel model) {
        this.typeMap = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));
    }

    /**
     * OpenFGA 标准：根据 Type Restrictions 规则，推断 TTU 依赖的目标资源类型。
     *
     * @param tupleKeyRelation    元组关系名，例如 parentFolder
     * @param currentResourceType 当前资源类型，例如 document
     * @return TTU 依赖的父级资源类型，例如 folder；找不到返回 null
     */
    public String extractTargetType(String tupleKeyRelation, String currentResourceType) {
        TypeDefinition currentTypeDefinition = typeMap.get(currentResourceType);
        if (currentTypeDefinition == null) {
            return null;
        }
        Set<String> restrictions = currentTypeDefinition.getRestrictionsForRelation(tupleKeyRelation);
        for (String restriction : restrictions) {
            if (restriction.contains("#")) {
                return restriction.split("#")[0];
            } else {
                return restriction;
            }
        }
        return null;
    }
}
