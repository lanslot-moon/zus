// Generated from /Users/kitona/IdeaProjects/zus/zus-business/src/main/java/org/kitona/zus/business/entity/antlr4/FGARewriter.g4 by ANTLR 4.13.2
package org.kitona.zus.business.entity.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link FGARewriterParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface FGARewriterVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#parse}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParse(FGARewriterParser.ParseContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(FGARewriterParser.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#orExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrExpr(FGARewriterParser.OrExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#andExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAndExpr(FGARewriterParser.AndExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#primaryExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpr(FGARewriterParser.PrimaryExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#tupleToUsersetExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTupleToUsersetExpr(FGARewriterParser.TupleToUsersetExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#kvPairs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKvPairs(FGARewriterParser.KvPairsContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#kvPair}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKvPair(FGARewriterParser.KvPairContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#fromExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFromExpr(FGARewriterParser.FromExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#listExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListExpr(FGARewriterParser.ListExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#listItems}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListItems(FGARewriterParser.ListItemsContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#relationExpr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationExpr(FGARewriterParser.RelationExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link FGARewriterParser#value}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValue(FGARewriterParser.ValueContext ctx);
}