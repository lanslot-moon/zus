// Generated from OpenFGAModel.g4 by ANTLR 4.13.2
package org.kitona.zus.infrastructure.engine.parser;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link OpenFGAModelParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface OpenFGAModelVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#model}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModel(OpenFGAModelParser.ModelContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#typeDefinition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeDefinition(OpenFGAModelParser.TypeDefinitionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#typeRestriction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeRestriction(OpenFGAModelParser.TypeRestrictionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#typeRestrictionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeRestrictionList(OpenFGAModelParser.TypeRestrictionListContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#typeRestrictionItem}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTypeRestrictionItem(OpenFGAModelParser.TypeRestrictionItemContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#relationBlock}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationBlock(OpenFGAModelParser.RelationBlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#defineStatement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDefineStatement(OpenFGAModelParser.DefineStatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#rewrite}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRewrite(OpenFGAModelParser.RewriteContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#union}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnion(OpenFGAModelParser.UnionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#intersection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIntersection(OpenFGAModelParser.IntersectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#exclusion}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExclusion(OpenFGAModelParser.ExclusionContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#primary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimary(OpenFGAModelParser.PrimaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#relationName}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRelationName(OpenFGAModelParser.RelationNameContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#computedUserset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#tupleToUserset}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx);
	/**
	 * Visit a parse tree produced by {@link OpenFGAModelParser#usersetRestrictionList}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUsersetRestrictionList(OpenFGAModelParser.UsersetRestrictionListContext ctx);
}