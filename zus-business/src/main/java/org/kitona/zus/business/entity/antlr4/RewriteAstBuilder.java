package org.kitona.zus.business.entity.antlr4;

import org.kitona.zus.business.entity.bo.ExprNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 将 FGA Rewriter 表达式语法树转换为 AST（ExprNode）结构
 * 支持 AND/OR、tupleToUserset、from、list、self、relation 等节点。
 */
public final class RewriteAstBuilder extends FGARewriterBaseVisitor<ExprNode> {

    /**
     * 处理 OR 表达式
     *
     * @param ctx OR表达式的上下文
     * @return 返回表达式节点，如果只有一个子表达式则直接返回该子表达式
     */
    @Override
    public ExprNode visitOrExpr(FGARewriterParser.OrExprContext ctx) {
        List<ExprNode> andNodes = ctx.andExpr().stream().map(this::visit).collect(Collectors.toList());
        return andNodes.size() == 1 ? andNodes.get(0) : new ExprNode.OrNode(andNodes);
    }

    /**
     * 处理 AND 表达式
     *
     * @param ctx AND表达式的上下文
     * @return 返回表达式节点，如果只有一个子表达式则直接返回该子表达式
     */
    @Override
    public ExprNode visitAndExpr(FGARewriterParser.AndExprContext ctx) {
        List<ExprNode> primaryNodes = ctx.primaryExpr().stream().map(this::visit).collect(Collectors.toList());
        return primaryNodes.size() == 1 ? primaryNodes.get(0) : new ExprNode.AndNode(primaryNodes);
    }

    /**
     * 处理 tupleToUserset 表达式
     *
     * @param ctx tupleToUserset表达式的上下文
     * @return 返回 TupleToUsersetNode 节点
     */
    @Override
    public ExprNode visitTupleToUsersetExpr(FGARewriterParser.TupleToUsersetExprContext ctx) {
        // 将键值对转换为小写的Map
        Map<String, String> kvMap = ctx.kvPairs().kvPair().stream()
                .collect(Collectors.toMap(
                        kv -> kv.ID().getText().toLowerCase(Locale.ROOT),
                        kv -> kv.value().getText()
                ));

        return new ExprNode.TupleToUsersetNode(kvMap.get("from"), kvMap.get("to"));
    }

    /**
     * 处理 from 表达式
     *
     * @param ctx from表达式的上下文
     * @return 返回 FromNode 节点
     */
    @Override
    public ExprNode visitFromExpr(FGARewriterParser.FromExprContext ctx) {
        ExprNode source = visit(ctx.relationExpr());
        String fromType = ctx.ID().getText();
        return new ExprNode.FromNode(source, fromType);
    }

    /**
     * 处理 relation 表达式
     *
     * @param ctx relation表达式的上下文
     * @return 返回 RelationNode 节点
     * @throws IllegalStateException 如果relation表达式无效
     */
    @Override
    public ExprNode visitRelationExpr(FGARewriterParser.RelationExprContext ctx) {
        List<String> ids = ctx.ID().stream().map(Object::toString).toList();

        return switch (ids.size()) {
            case 1 -> new ExprNode.RelationNode(null, ids.get(0));  // 只有关系名的情况
            case 2 -> new ExprNode.RelationNode(ids.get(0), ids.get(1));  // 对象类型和关系名的情况
            default -> throw new IllegalStateException("Invalid relationExpr: " + ctx.getText());
        };
    }

    /**
     * 处理 list 表达式
     *
     * @param ctx list表达式的上下文
     * @return 返回 ListNode 节点
     */
    @Override
    public ExprNode visitListExpr(FGARewriterParser.ListExprContext ctx) {
        List<ExprNode> items = ctx.listItems().relationExpr().stream()
                .map(this::visit)
                .collect(Collectors.toList());
        return new ExprNode.ListNode(items);
    }

    /**
     * 处理 primary 表达式
     *
     * @param ctx primary表达式的上下文
     * @return 返回相应的表达式节点
     */
    @Override
    public ExprNode visitPrimaryExpr(FGARewriterParser.PrimaryExprContext ctx) {
        if (ctx.SELF() != null) {
            return new ExprNode.SelfNode();  // 处理SELF关键字
        }
        if (ctx.expression() != null) {
            return visit(ctx.expression()); // 括号表达式
        }
        return super.visitPrimaryExpr(ctx);
    }
}
