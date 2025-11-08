package org.kitona.zus.business.entity.bo;

import lombok.extern.slf4j.Slf4j;
import org.kitona.zus.business.entity.antlr4.OpenFGAGraphCompiler;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权模型图 (AuthorizationModelGraph)
 */
@Slf4j
public record AuthorizationModelGraph(DirectedGraph graph, AuthorizationModel model) { // 保持 model 引用以便使用

    /**
     * 【核心修正方法】：使用 OpenFGAGraphCompiler 编译授权模型。
     */
    public static AuthorizationModelGraph fromModel(AuthorizationModel model) {
        AuthorizationModelGraphBuilder builder = new AuthorizationModelGraphBuilder();

        // 1. 初始化辅助组件
        OpenFGAGraphCompiler compiler = new OpenFGAGraphCompiler(builder, model); // 传入 builder 和 helper

        Map<String, TypeDefinition> resTypeMapDef = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));

        // 2. 遍历所有 TypeDefinition，启动编译器
        for (var entry : resTypeMapDef.entrySet()) {
            String resourceType = entry.getKey();
            TypeDefinition typeDef = entry.getValue();
            
            // 2.1. 添加资源类型节点，例如 "document"
            builder.getOrAddNode(resourceType, resourceType, NodeType.SPECIFIC_TYPE);

            // 2.2. 遍历所有关系，并交给 Compiler 编译表达式
            for (var relationEntry : typeDef.relations().entrySet()) {
                String relationName = relationEntry.getKey();
                RelationDefinition relDef = relationEntry.getValue();
                String rewriteExpression = relDef.rewriteExpression();

                if (rewriteExpression != null) {
                    // 🌟 核心调用：编译器开始工作
                    compiler.compileRelation(resourceType, relationName, rewriteExpression);
                }
            }
        }

        return new AuthorizationModelGraph(builder.build(), model);
    }
}