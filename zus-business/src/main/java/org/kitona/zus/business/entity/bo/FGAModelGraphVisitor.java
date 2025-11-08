//package org.kitona.zus.business.entity.bo;
//
///*
// * Author: 登林
// * Email: wangli.liu@kitona.org
// * Date: 2025/11/8 23:27
// * Version: V1.0
// * Description: Xxxx
// */
//
//import com.google.common.graph.Graph;
//import org.kitona.zus.business.entity.antlr4.OpenFGAModelBaseVisitor;
//import org.kitona.zus.business.entity.antlr4.OpenFGAModelParser;
//
//// ================================================================
//// 自定义Visitor，用于遍历AST并构建图
//// ================================================================
//class FGAModelGraphVisitor extends OpenFGAModelBaseVisitor<Void> {
//    // 使用 Guava 的 MutableGraph<String> 来存储节点（字符串）
//    private MutableGraph<String> graph = GraphBuilder.directed().build(); // 创建一个有向图
//
//    public MutableGraph<String> getGraph() { // 返回类型也改为 MutableGraph
//        return graph;
//    }
//
//    @Override
//    public Void visitRelationDefinition(OpenFGAModelParser.RelationDefinitionContext ctx) {
//        String typeName = ctx.ID().getText();
//        currentType = typeName;
//        graph.addNode("type:" + typeName); // 为类型添加节点
//        return super.visitRelationDefinition(ctx);
//    }
//
//    @Override
//    public Void visitRelationEntry(OpenFGAModelParser.RelationEntryContext ctx) {
//        String relationName = ctx.ID().getText();
//        String fullRelationId = currentType + "#" + relationName;
//        graph.addNode(fullRelationId); // 为关系添加节点
//        graph.putEdge("type:" + currentType, fullRelationId); // 类型指向其关系 (使用putEdge)
//
//        // 递归访问关系表达式
//        visit(ctx.expression());
//
//        return null;
//    }
//
//    @Override
//    public Void visitExpression(OpenFGAModelParser.ExpressionContext ctx) {
//        String currentRelationId = currentType + "#" + ((OpenFGAModelParser.RelationEntryContext)ctx.getParent()).ID().getText();
//
//        for (int i = 0; i < ctx.term().size(); i++) {
//            OpenFGAModelParser.TermContext termCtx = ctx.term(i);
//            String termNodeId = getTermNodeId(termCtx);
//            if (termNodeId != null && !termNodeId.isEmpty()) {
//                graph.addNode(termNodeId);
//                graph.putEdge(currentRelationId, termNodeId); // 当前关系指向其包含的term (使用putEdge)
//            }
//        }
//        return super.visitExpression(ctx);
//    }
//
//    // 辅助方法：根据term上下文获取对应的节点ID (保持不变)
//    private String getTermNodeId(OpenFGAModelParser.TermContext ctx) {
//        if (ctx.targetIdentifier() != null) {
//            OpenFGAModelParser.TargetIdentifierContext targetCtx = ctx.targetIdentifier();
//            StringBuilder targetNodeId = new StringBuilder(targetCtx.ID(0).getText());
//            if (targetCtx.ID().size() > 1) { // group#member
//                targetNodeId.append("#").append(targetCtx.ID(1).getText());
//            } else if (targetCtx.getChild(1) != null && targetCtx.getChild(1).getText().equals(":")) { // user:*
//                targetNodeId.append(":*");
//            }
//            return targetNodeId.toString();
//        } else if (ctx.ID() != null) { // For 'this', 'self', '_'
//            return ctx.ID().getText().toUpperCase(); // 'THIS', 'SELF', 'PUBLIC' (mapping '_')
//        } else if (ctx.expression() != null) { // ( expression )
//            return "sub_expression_" + ctx.hashCode(); // 简单标识
//        } else if (ctx.getText().equals("*")) { // For '*' which means user:* for some contexts
//            return "ANY_USER"; // 简化处理
//        }
//        return null;
//    }
//}
