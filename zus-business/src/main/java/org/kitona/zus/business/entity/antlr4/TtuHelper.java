package org.kitona.zus.business.entity.antlr4;

import org.kitona.zus.business.entity.bo.*;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * TTU (Tuple To Userset) 辅助类
 * 负责从原始授权模型中提取目标类型，用于编译跨对象依赖。
 */
public class TtuHelper {

    private final Map<String, TypeDefinition> typeMap;

    public TtuHelper(AuthorizationModel model) {
        this.typeMap = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));
    }

    /**
     * 根据编译后的别名（例如 "parentFolder#editor"）和当前资源类型，
     * 提取出最终的目标资源类型（例如 "folder"）。
     * * @param tupleKeyRelation 别名关系，例如 "parentFolder"
     * @param currentResourceType 当前资源类型，例如 "document"
     * @return 目标资源类型，例如 "folder"
     */
    public String extractTargetType(String tupleKeyRelation, String currentResourceType) {
        TypeDefinition currentTypeDefinition = typeMap.get(currentResourceType);
        if (currentTypeDefinition == null) return null;

        RelationDefinition relationDefinition = currentTypeDefinition.relations().get(tupleKeyRelation);
        if (relationDefinition == null) return null;

        String expr = relationDefinition.rewriteExpression();
        if (expr.startsWith("tupleToUserset:") && expr.contains("to=")) {
            int toIndex = expr.indexOf("to=");
            String targetPart = expr.substring(toIndex + 3).trim();
            // 目标格式为 folder#owner，我们只需要 folder
            return targetPart.split("#")[0];
        }
        return null;
    }
}