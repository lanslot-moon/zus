package org.kitona.zus.infrastructure.engine.compiler;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kitona.zus.domain.port.IModelCompiler;
import org.kitona.zus.domain.service.internal.GraphBuilder;
import org.kitona.zus.domain.service.internal.GraphNode;
import org.kitona.zus.domain.service.internal.NodeType;
import org.kitona.zus.domain.valueobject.AuthorizationModel;
import org.kitona.zus.infrastructure.engine.parser.OpenFGAModelBaseVisitor;
import org.kitona.zus.infrastructure.engine.parser.OpenFGAModelLexer;
import org.kitona.zus.infrastructure.engine.parser.OpenFGAModelParser;
import org.kitona.zus.infrastructure.engine.parser.TtuHelper;
import org.springframework.stereotype.Component;

/**
 * 基于 ANTLR 的 OpenFGA 关系重写表达式编译器
 * <p>
 * 将 rewrite 字符串转为图边，实现领域层定义的 {@link IModelCompiler} 接口。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Component
public class OpenFGAModelCompiler extends OpenFGAModelBaseVisitor<Void> implements IModelCompiler {

    private GraphBuilder builder;
    private TtuHelper ttuHelper;

    private String currentResourceType;
    private GraphNode currentParentNode;

    @Override
    public void initGraphBuilder(GraphBuilder graphBuilder, AuthorizationModel model) {
        this.builder = graphBuilder;
        this.ttuHelper = new TtuHelper(model);
    }

    @Override
    public Void visitUnion(OpenFGAModelParser.UnionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Void visitIntersection(OpenFGAModelParser.IntersectionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Void visitExclusion(OpenFGAModelParser.ExclusionContext ctx) {
        return visitChildren(ctx);
    }

    /** Primary：self 为自循环边；简单关系为内部依赖边；computedUserset / tupleToUserset 生成对应边 */
    @Override
    public Void visitPrimary(OpenFGAModelParser.PrimaryContext ctx) {
        if (ctx.SELF() != null) {
            builder.addEdge(currentParentNode, currentParentNode);
        } else if (ctx.relationName() != null) {
            String targetRel = ctx.relationName().getText();
            String targetId = currentResourceType + "#" + targetRel;
            GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
            builder.addEdge(currentParentNode, targetNode);
        } else if (ctx.computedUserset() != null) {
            visitComputedUserset(ctx.computedUserset());
        } else if (ctx.tupleToUserset() != null) {
            visitTupleToUserset(ctx.tupleToUserset());
        } else if (ctx.rewrite() != null) {
            ctx.rewrite().accept(this);
        }
        return null;
    }

    /** Computed Userset：支持 type:relation#targetRelation 或 relation#targetRelation */
    @Override
    public Void visitComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx) {
        String targetId;
        if (ctx.COLON() != null) {
            String typePrefix = ctx.relationName(0).getText();
            String targetRelation = ctx.relationName(2).getText();
            targetId = typePrefix + "#" + targetRelation;
        } else {
            String targetRelation = ctx.relationName(1).getText();
            targetId = currentResourceType + "#" + targetRelation;
        }
        GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
        builder.addEdge(currentParentNode, targetNode);
        return null;
    }

    /** Tuple To Userset：例如 editor from parentFolder，用 TtuHelper 推断目标类型后添加 TTU 依赖边 */
    @Override
    public Void visitTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx) {
        String targetRel = ctx.relationName(0).getText();
        String tupleKeyRelation = ctx.relationName(1).getText();
        String targetType = ttuHelper.extractTargetType(tupleKeyRelation, currentResourceType);
        if (targetType != null) {
            String targetId = targetType + "#" + targetRel;
            GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
            builder.addEdge(currentParentNode, targetNode);
        }
        return null;
    }

    /**
     * 编译单条关系的重写表达式：创建关系节点，将 rewrite 字符串做词法/语法分析得到 AST，再遍历生成图边。
     */
    @Override
    public void compileRelation(String resourceType, String relationName, String rewriteExpression) {
        this.currentResourceType = resourceType;
        String uniqueId = resourceType + "#" + relationName;
        this.currentParentNode = builder.getOrAddNode(uniqueId, uniqueId, NodeType.SPECIFIC_TYPE_AND_RELATION);

        OpenFGAModelLexer lexer = new OpenFGAModelLexer(CharStreams.fromString(rewriteExpression));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OpenFGAModelParser parser = new OpenFGAModelParser(tokens);
        OpenFGAModelParser.RewriteContext rewriteContext = parser.rewrite();
        rewriteContext.accept(this);
    }
}
