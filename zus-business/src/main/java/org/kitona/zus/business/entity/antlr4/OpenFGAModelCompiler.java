package org.kitona.zus.business.entity.antlr4;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.kitona.zus.business.entity.graph.AuthorizationModelGraphBuilder;
import org.kitona.zus.business.entity.graph.GraphNode;
import org.kitona.zus.business.entity.graph.NodeType;
import org.kitona.zus.business.entity.model.AuthorizationModel;

public class OpenFGAModelCompiler extends OpenFGAModelBaseVisitor<Void> {

    private final AuthorizationModelGraphBuilder builder;
    private final TtuHelper ttuHelper;

    // 编译上下文
    private String currentResourceType;
    private GraphNode currentParentNode; // 当前正在定义的关系节点 (e.g., document#writer)

    public OpenFGAModelCompiler(AuthorizationModelGraphBuilder builder, AuthorizationModel model) {
        this.builder = builder;
        this.ttuHelper = new TtuHelper(model);
    }

    // =========================================================================
    // 权限表达式节点编译 (AST Visitor 核心)
    // =========================================================================

    // Union (or): 逻辑不变
    @Override
    public Void visitUnion(OpenFGAModelParser.UnionContext ctx) {
        return visitChildren(ctx);
    }

    // Intersection (and) 和 Exclusion (but not): 逻辑不变
    @Override
    public Void visitIntersection(OpenFGAModelParser.IntersectionContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public Void visitExclusion(OpenFGAModelParser.ExclusionContext ctx) {
        return visitChildren(ctx);
    }

    // 3. Primary (基本单元)：图边生成的关键点
    @Override
    public Void visitPrimary(OpenFGAModelParser.PrimaryContext ctx) {
        if (ctx.SELF() != null) {
            // self -> 自循环边
            builder.addEdge(currentParentNode, currentParentNode);
        } else if (ctx.relationName() != null) {
            // 简单关系 (e.g., 'owner') -> 内部依赖边
            String targetRel = ctx.relationName().getText();
            String targetId = currentResourceType + "#" + targetRel;
            GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
            builder.addEdge(currentParentNode, targetNode);
        } else if (ctx.computedUserset() != null) {
            // CUS 关系 (e.g., 'parent#editor' 或 'group:admin#member')
            visitComputedUserset(ctx.computedUserset());
        } else if (ctx.tupleToUserset() != null) {
            // TTU 关系 (e.g., 'editor from parentFolder')
            visitTupleToUserset(ctx.tupleToUserset());
        } else if (ctx.rewrite() != null) {
            // 括号内的表达式
            ctx.rewrite().accept(this);
        }
        return null;
    }

    // 4. Computed Userset (CUS) 修正：支持可选的类型前缀 (e.g., group:admin#member)
    // 语法规则: (relationName COLON)? relationName HASH relationName
    @Override
    public Void visitComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx) {
        String targetId;

        // CUS 规则中，relationName(0) 是第一个关系，relationName(1) 是第二个关系，relationName(2) 是第三个关系

        if (ctx.COLON() != null) {
            // 结构: type:relation#targetRelation (使用 relationName(0), relationName(2))
            // 目标依赖ID: type#targetRelation
            String typePrefix = ctx.relationName(0).getText();       // e.g., group
            String targetRelation = ctx.relationName(2).getText();   // e.g., member
            targetId = typePrefix + "#" + targetRelation;
        } else {
            // 结构: relation#targetRelation (使用 relationName(1))
            // 目标依赖ID: currentResourceType#targetRelation
            String targetRelation = ctx.relationName(1).getText();   // e.g., editor
            targetId = currentResourceType + "#" + targetRelation;
        }

        GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
        builder.addEdge(currentParentNode, targetNode);
        return null;
    }

    // 5. TupleToUserset (TTU) 修正：解析标准的 TTU 引用 (e.g., editor from parentFolder)
    // 语法规则: relationName FROM relationName
    @Override
    public Void visitTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx) {
        // 1. 获取 TTU 结构的关键部分
        String targetRel = ctx.relationName(0).getText();        // 目标关系，e.g., "editor"
        String tupleKeyRelation = ctx.relationName(1).getText(); // 元组关系，e.g., "parentFolder"

        // 2. 使用 TtuHelper 推断目标类型 (e.g., document, parentFolder -> folder)
        String targetType = ttuHelper.extractTargetType(tupleKeyRelation, currentResourceType);

        if (targetType != null) {
            // 3. 构造依赖终点 ID (e.g., folder#editor)
            String targetId = targetType + "#" + targetRel;

            // 4. 确保节点存在，并添加依赖边
            GraphNode targetNode = builder.getOrAddNode(targetId, targetId, NodeType.SPECIFIC_TYPE_AND_RELATION);
            // ** 关键：添加 TTU 依赖边，例如 document#writer -> folder#editor **
            builder.addEdge(currentParentNode, targetNode);
        }
        // ⚠️ 如果 targetType 为 null，则不生成边，这是导致图缺失的原因
        return null;
    }

    // ----------------------------------------------------------------------
    // 编译控制方法：取代 AuthorizationModelGraph.fromModel 中的解析部分
    // ----------------------------------------------------------------------

    public void compileRelation(String resourceType, String relationName, String rewriteExpression) {
        this.currentResourceType = resourceType;
        String uniqueId = resourceType + "#" + relationName;

        // 1. 创建父节点
        // 确保 currentParentNode 在 TTU 关系中能够被正确设置类型
        this.currentParentNode = builder.getOrAddNode(uniqueId, uniqueId, NodeType.SPECIFIC_TYPE_AND_RELATION);

        // 2. 将 rewriteExpression 转换为 AST 并开始遍历

        // 2.1. 词法分析 (Lexer): 将字符串转换为 Token 流
        OpenFGAModelLexer lexer = new OpenFGAModelLexer(CharStreams.fromString(rewriteExpression));

        // 2.2. 语法分析 (Parser): 将 Token 流转换为 AST
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OpenFGAModelParser parser = new OpenFGAModelParser(tokens);

        // 2.3. 获取 AST 根节点 (对应 .g4 文件中的 'rewrite' 规则)
        OpenFGAModelParser.RewriteContext rewriteContext = parser.rewrite();

        // 2.4. 启动 AST 遍历器：Visitor 开始遍历 AST，执行图生成逻辑
        // this 就是 OpenFGAGraphCompiler，它继承自 OpenFGAModelBaseVisitor
        rewriteContext.accept(this);
    }
}