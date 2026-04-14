package org.kitona.zus.infrastructure.engine.compiler;

import org.kitona.zus.domain.authorization.evaluation.nodes.ComputedUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.DirectRelationReferenceNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.ExclusionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.IntersectionNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.SelfNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.TupleToUsersetNode;
import org.kitona.zus.domain.authorization.evaluation.nodes.UnionNode;

/**
 * Rewrite AST 访问器。
 *
 * <p>把 parser 树节点转换成领域侧 rewrite 节点，保持编译器主流程只负责“编排”。
 */
final class RewriteNodeAstVisitor extends OpenFGAModelBaseVisitor<RewriteNode> {

    private final String currentResourceType;

    RewriteNodeAstVisitor(String currentResourceType) {
        this.currentResourceType = currentResourceType;
    }

    /**
     * union 形态：a or b or c。
     */
    @Override
    public RewriteNode visitUnion(OpenFGAModelParser.UnionContext ctx) {
        if (ctx.intersection().size() == 1) {
            return visit(ctx.intersection(0));
        }
        return new UnionNode(ctx.intersection().stream().map(this::visit).toList());
    }

    /**
     * intersection 形态：a and b and c。
     */
    @Override
    public RewriteNode visitIntersection(OpenFGAModelParser.IntersectionContext ctx) {
        if (ctx.exclusion().size() == 1) {
            return visit(ctx.exclusion(0));
        }
        return new IntersectionNode(ctx.exclusion().stream().map(this::visit).toList());
    }

    /**
     * exclusion 形态：a but not b [but not c ...]，按左结合构建。
     */
    @Override
    public RewriteNode visitExclusion(OpenFGAModelParser.ExclusionContext ctx) {
        RewriteNode left = visit(ctx.primary(0));
        for (int index = 1; index < ctx.primary().size(); index++) {
            left = new ExclusionNode(left, visit(ctx.primary(index)));
        }
        return left;
    }

    @Override
    public RewriteNode visitPrimary(OpenFGAModelParser.PrimaryContext ctx) {
        if (ctx.SELF() != null || ctx.THIS() != null) {
            return new SelfNode();
        }
        if (ctx.relationName() != null) {
            return new DirectRelationReferenceNode(ctx.relationName().getText());
        }
        if (ctx.computedUserset() != null) {
            return visitComputedUserset(ctx.computedUserset());
        }
        if (ctx.tupleToUserset() != null) {
            return visitTupleToUserset(ctx.tupleToUserset());
        }
        // [user, group#member] 这类主体限制在关系定义中由 restrictions 表达，
        // rewrite AST 里按 direct/self 处理。
        if (ctx.usersetRestrictionList() != null) {
            return new SelfNode();
        }
        if (ctx.rewrite() != null) {
            return visit(ctx.rewrite());
        }
        throw new IllegalStateException("Unsupported primary rewrite node: " + ctx.getText());
    }

    /**
     * computedUserset 允许显式跨资源前缀，也允许省略前缀（默认当前资源类型）。
     */
    @Override
    public RewriteNode visitComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx) {
        return new ComputedUsersetNode(resolveComputedUsersetResourceType(ctx), resolveComputedUsersetRelation(ctx));
    }

    /**
     * tupleToUserset 形态：from <tupleset> compute <computedRelation>。
     */
    @Override
    public RewriteNode visitTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx) {
        return new TupleToUsersetNode(ctx.relationName(1).getText(), ctx.relationName(0).getText());
    }

    private String resolveComputedUsersetResourceType(OpenFGAModelParser.ComputedUsersetContext ctx) {
        if (ctx.COLON() != null) {
            return ctx.relationName(0).getText();
        }
        return currentResourceType;
    }

    private String resolveComputedUsersetRelation(OpenFGAModelParser.ComputedUsersetContext ctx) {
        if (ctx.COLON() != null) {
            return ctx.relationName(2).getText();
        }
        return ctx.relationName(1).getText();
    }
}
