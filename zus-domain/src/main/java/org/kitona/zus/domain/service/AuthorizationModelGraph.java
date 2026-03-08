package org.kitona.zus.domain.service;

import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.service.internal.DirectedGraph;
import org.kitona.zus.domain.service.internal.GraphBuilder;
import org.kitona.zus.domain.service.internal.NodeType;
import org.kitona.zus.domain.valueobject.AuthorizationModel;
import org.kitona.zus.domain.valueobject.RelationDefinition;
import org.kitona.zus.domain.valueobject.TypeDefinition;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权模型图（AuthorizationModelGraph）
 * <p>
 * 使用 IModelCompiler 编译授权模型得到有向图。
 * 具体的编译器实现（如基于 ANTLR）由基础设施层提供。
 * <p>
 * 这是领域层对外暴露的图结构入口，内部实现细节封装在 engine 包中。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
public record AuthorizationModelGraph(DirectedGraph graph, AuthorizationModel model) {

    /**
     * 从授权模型构建图：遍历 TypeDefinition，为每个关系的 rewrite 表达式做编译并生成图边。
     *
     * @param model    授权模型
     * @param compiler 模型编译器（由基础设施层提供实现）
     * @return 授权模型图
     */
    public static AuthorizationModelGraph fromModel(AuthorizationModel model, IModelCompiler compiler) {
        GraphBuilder builder = new GraphBuilder();

        compiler.initGraphBuilder(builder, model);

        Map<String, TypeDefinition> resTypeMapDef = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));

        for (var entry : resTypeMapDef.entrySet()) {
            String resourceType = entry.getKey();
            TypeDefinition typeDef = entry.getValue();

            builder.getOrAddNode(resourceType, resourceType, NodeType.SPECIFIC_TYPE);

            for (var relationEntry : typeDef.relations().entrySet()) {
                String relationName = relationEntry.getKey();
                RelationDefinition relDef = relationEntry.getValue();
                String rewriteExpression = relDef.rewriteExpression();

                if (rewriteExpression != null) {
                    compiler.compileRelation(resourceType, relationName, rewriteExpression);
                }
            }
        }

        return new AuthorizationModelGraph(builder.build(), model);
    }
}
