package org.kitona.zus.business.entity.bo;

import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kitona.zus.business.entity.antlr4.FGARewriterLexer;
import org.kitona.zus.business.entity.antlr4.FGARewriterParser;
import org.kitona.zus.business.entity.antlr4.RewriteAstBuilder;
import org.kitona.zus.business.entity.antlr4.RewriteEvaluator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 授权模型图 - AST 版本
 */
@Slf4j
public record AuthorizationModelGraphV2(DirectedGraph graph, AuthorizationModel model) {


    public static AuthorizationModelGraph fromModel(AuthorizationModel model) {
        AuthorizationModelGraphBuilder builder = new AuthorizationModelGraphBuilder();

        Map<String, TypeDefinition> resTypeMapDef = model.typeDefinitions().stream()
                .collect(Collectors.toMap(TypeDefinition::resourceType, Function.identity()));

        for (var resourceType : resTypeMapDef.keySet()) {
            // 添加资源类型节点，例如 "document"
            builder.getOrAddNode(resourceType, resourceType, NodeType.SPECIFIC_TYPE);

            TypeDefinition typeDef = resTypeMapDef.get(resourceType);
            List<String> relations = new ArrayList<>(typeDef.relations().keySet());
            Collections.sort(relations);

            for (var relationName : relations) {
                String uniqueLabel = resourceType + "#" + relationName;
                GraphNode parentNode = builder.getOrAddNode(uniqueLabel, uniqueLabel, NodeType.SPECIFIC_TYPE_AND_RELATION);

                RelationDefinition relDef = typeDef.relations().get(relationName);
                parseRewriteExpression(builder, parentNode, relDef, resourceType);
            }
        }

        return new AuthorizationModelGraph(builder.build(), model);
    }

    private static void parseRewriteExpression(AuthorizationModelGraphBuilder builder,
                                               GraphNode parentNode,
                                               RelationDefinition relDef, String resourceType) {
        if (relDef.rewriteExpression() != null) {
            try {
                // 1️⃣ ANTLR 解析 rewriteExpression
                FGARewriterLexer lexer = new FGARewriterLexer(CharStreams.fromString(relDef.rewriteExpression()));
                FGARewriterParser parser = new FGARewriterParser(new CommonTokenStream(lexer));
                ExprNode ast = new RewriteAstBuilder().visit(parser.parse());

                // 2️⃣ Evaluator 生成依赖集合
                Set<String> dependencies = new RewriteEvaluator().evaluate(ast);

                // 3️⃣ 构建图节点和边
                for (String dep : dependencies) {
                    GraphNode depNode = builder.getOrAddNode(dep, dep, NodeType.SPECIFIC_TYPE_AND_RELATION);
                    builder.addEdge(parentNode, depNode);
                }

            } catch (Exception e) {
                log.error("Failed to parse rewrite expression for {}", resourceType, e);
            }
        }
    }
}