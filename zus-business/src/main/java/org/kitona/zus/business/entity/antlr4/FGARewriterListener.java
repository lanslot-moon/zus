// Generated from /Users/kitona/IdeaProjects/zus/zus-business/src/main/java/org/kitona/zus/business/entity/antlr4/FGARewriter.g4 by ANTLR 4.13.2
package org.kitona.zus.business.entity.antlr4;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link FGARewriterParser}.
 */
public interface FGARewriterListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#parse}.
	 * @param ctx the parse tree
	 */
	void enterParse(FGARewriterParser.ParseContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#parse}.
	 * @param ctx the parse tree
	 */
	void exitParse(FGARewriterParser.ParseContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(FGARewriterParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(FGARewriterParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void enterOrExpr(FGARewriterParser.OrExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#orExpr}.
	 * @param ctx the parse tree
	 */
	void exitOrExpr(FGARewriterParser.OrExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void enterAndExpr(FGARewriterParser.AndExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#andExpr}.
	 * @param ctx the parse tree
	 */
	void exitAndExpr(FGARewriterParser.AndExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpr(FGARewriterParser.PrimaryExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#primaryExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpr(FGARewriterParser.PrimaryExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#tupleToUsersetExpr}.
	 * @param ctx the parse tree
	 */
	void enterTupleToUsersetExpr(FGARewriterParser.TupleToUsersetExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#tupleToUsersetExpr}.
	 * @param ctx the parse tree
	 */
	void exitTupleToUsersetExpr(FGARewriterParser.TupleToUsersetExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#kvPairs}.
	 * @param ctx the parse tree
	 */
	void enterKvPairs(FGARewriterParser.KvPairsContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#kvPairs}.
	 * @param ctx the parse tree
	 */
	void exitKvPairs(FGARewriterParser.KvPairsContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#kvPair}.
	 * @param ctx the parse tree
	 */
	void enterKvPair(FGARewriterParser.KvPairContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#kvPair}.
	 * @param ctx the parse tree
	 */
	void exitKvPair(FGARewriterParser.KvPairContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#fromExpr}.
	 * @param ctx the parse tree
	 */
	void enterFromExpr(FGARewriterParser.FromExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#fromExpr}.
	 * @param ctx the parse tree
	 */
	void exitFromExpr(FGARewriterParser.FromExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#listExpr}.
	 * @param ctx the parse tree
	 */
	void enterListExpr(FGARewriterParser.ListExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#listExpr}.
	 * @param ctx the parse tree
	 */
	void exitListExpr(FGARewriterParser.ListExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#listItems}.
	 * @param ctx the parse tree
	 */
	void enterListItems(FGARewriterParser.ListItemsContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#listItems}.
	 * @param ctx the parse tree
	 */
	void exitListItems(FGARewriterParser.ListItemsContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#relationExpr}.
	 * @param ctx the parse tree
	 */
	void enterRelationExpr(FGARewriterParser.RelationExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#relationExpr}.
	 * @param ctx the parse tree
	 */
	void exitRelationExpr(FGARewriterParser.RelationExprContext ctx);
	/**
	 * Enter a parse tree produced by {@link FGARewriterParser#value}.
	 * @param ctx the parse tree
	 */
	void enterValue(FGARewriterParser.ValueContext ctx);
	/**
	 * Exit a parse tree produced by {@link FGARewriterParser#value}.
	 * @param ctx the parse tree
	 */
	void exitValue(FGARewriterParser.ValueContext ctx);
}