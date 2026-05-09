// Generated from /Subjects/kitona/IdeaProjects/zus/zus-infrastructure/src/main/java/org/kitona/zus/infrastructure/engine/parser/OpenFGAModel.g4 by ANTLR 4.13.2
package org.kitona.zus.infrastructure.engine.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link OpenFGAModelParser}.
 */
public interface OpenFGAModelListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#model}.
	 * @param ctx the parse tree
	 */
	void enterModel(OpenFGAModelParser.ModelContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#model}.
	 * @param ctx the parse tree
	 */
	void exitModel(OpenFGAModelParser.ModelContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#modelHeader}.
	 * @param ctx the parse tree
	 */
	void enterModelHeader(OpenFGAModelParser.ModelHeaderContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#modelHeader}.
	 * @param ctx the parse tree
	 */
	void exitModelHeader(OpenFGAModelParser.ModelHeaderContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#schemaVersion}.
	 * @param ctx the parse tree
	 */
	void enterSchemaVersion(OpenFGAModelParser.SchemaVersionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#schemaVersion}.
	 * @param ctx the parse tree
	 */
	void exitSchemaVersion(OpenFGAModelParser.SchemaVersionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#typeDefinition}.
	 * @param ctx the parse tree
	 */
	void enterTypeDefinition(OpenFGAModelParser.TypeDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#typeDefinition}.
	 * @param ctx the parse tree
	 */
	void exitTypeDefinition(OpenFGAModelParser.TypeDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#typeRestriction}.
	 * @param ctx the parse tree
	 */
	void enterTypeRestriction(OpenFGAModelParser.TypeRestrictionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#typeRestriction}.
	 * @param ctx the parse tree
	 */
	void exitTypeRestriction(OpenFGAModelParser.TypeRestrictionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#typeRestrictionList}.
	 * @param ctx the parse tree
	 */
	void enterTypeRestrictionList(OpenFGAModelParser.TypeRestrictionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#typeRestrictionList}.
	 * @param ctx the parse tree
	 */
	void exitTypeRestrictionList(OpenFGAModelParser.TypeRestrictionListContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#typeRestrictionItem}.
	 * @param ctx the parse tree
	 */
	void enterTypeRestrictionItem(OpenFGAModelParser.TypeRestrictionItemContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#typeRestrictionItem}.
	 * @param ctx the parse tree
	 */
	void exitTypeRestrictionItem(OpenFGAModelParser.TypeRestrictionItemContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#typeRestrictionBase}.
	 * @param ctx the parse tree
	 */
	void enterTypeRestrictionBase(OpenFGAModelParser.TypeRestrictionBaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#typeRestrictionBase}.
	 * @param ctx the parse tree
	 */
	void exitTypeRestrictionBase(OpenFGAModelParser.TypeRestrictionBaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#relationBlock}.
	 * @param ctx the parse tree
	 */
	void enterRelationBlock(OpenFGAModelParser.RelationBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#relationBlock}.
	 * @param ctx the parse tree
	 */
	void exitRelationBlock(OpenFGAModelParser.RelationBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#defineStatement}.
	 * @param ctx the parse tree
	 */
	void enterDefineStatement(OpenFGAModelParser.DefineStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#defineStatement}.
	 * @param ctx the parse tree
	 */
	void exitDefineStatement(OpenFGAModelParser.DefineStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionBlock}.
	 * @param ctx the parse tree
	 */
	void enterConditionBlock(OpenFGAModelParser.ConditionBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionBlock}.
	 * @param ctx the parse tree
	 */
	void exitConditionBlock(OpenFGAModelParser.ConditionBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionDefinition}.
	 * @param ctx the parse tree
	 */
	void enterConditionDefinition(OpenFGAModelParser.ConditionDefinitionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionDefinition}.
	 * @param ctx the parse tree
	 */
	void exitConditionDefinition(OpenFGAModelParser.ConditionDefinitionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionParameterList}.
	 * @param ctx the parse tree
	 */
	void enterConditionParameterList(OpenFGAModelParser.ConditionParameterListContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionParameterList}.
	 * @param ctx the parse tree
	 */
	void exitConditionParameterList(OpenFGAModelParser.ConditionParameterListContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionParameter}.
	 * @param ctx the parse tree
	 */
	void enterConditionParameter(OpenFGAModelParser.ConditionParameterContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionParameter}.
	 * @param ctx the parse tree
	 */
	void exitConditionParameter(OpenFGAModelParser.ConditionParameterContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionType}.
	 * @param ctx the parse tree
	 */
	void enterConditionType(OpenFGAModelParser.ConditionTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionType}.
	 * @param ctx the parse tree
	 */
	void exitConditionType(OpenFGAModelParser.ConditionTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#conditionExpression}.
	 * @param ctx the parse tree
	 */
	void enterConditionExpression(OpenFGAModelParser.ConditionExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#conditionExpression}.
	 * @param ctx the parse tree
	 */
	void exitConditionExpression(OpenFGAModelParser.ConditionExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#rewrite}.
	 * @param ctx the parse tree
	 */
	void enterRewrite(OpenFGAModelParser.RewriteContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#rewrite}.
	 * @param ctx the parse tree
	 */
	void exitRewrite(OpenFGAModelParser.RewriteContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#union}.
	 * @param ctx the parse tree
	 */
	void enterUnion(OpenFGAModelParser.UnionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#union}.
	 * @param ctx the parse tree
	 */
	void exitUnion(OpenFGAModelParser.UnionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#intersection}.
	 * @param ctx the parse tree
	 */
	void enterIntersection(OpenFGAModelParser.IntersectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#intersection}.
	 * @param ctx the parse tree
	 */
	void exitIntersection(OpenFGAModelParser.IntersectionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#exclusion}.
	 * @param ctx the parse tree
	 */
	void enterExclusion(OpenFGAModelParser.ExclusionContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#exclusion}.
	 * @param ctx the parse tree
	 */
	void exitExclusion(OpenFGAModelParser.ExclusionContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#primary}.
	 * @param ctx the parse tree
	 */
	void enterPrimary(OpenFGAModelParser.PrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#primary}.
	 * @param ctx the parse tree
	 */
	void exitPrimary(OpenFGAModelParser.PrimaryContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#relationName}.
	 * @param ctx the parse tree
	 */
	void enterRelationName(OpenFGAModelParser.RelationNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#relationName}.
	 * @param ctx the parse tree
	 */
	void exitRelationName(OpenFGAModelParser.RelationNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void enterQualifiedName(OpenFGAModelParser.QualifiedNameContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#qualifiedName}.
	 * @param ctx the parse tree
	 */
	void exitQualifiedName(OpenFGAModelParser.QualifiedNameContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#computedUserset}.
	 * @param ctx the parse tree
	 */
	void enterComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#computedUserset}.
	 * @param ctx the parse tree
	 */
	void exitComputedUserset(OpenFGAModelParser.ComputedUsersetContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#tupleToUserset}.
	 * @param ctx the parse tree
	 */
	void enterTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#tupleToUserset}.
	 * @param ctx the parse tree
	 */
	void exitTupleToUserset(OpenFGAModelParser.TupleToUsersetContext ctx);
	/**
	 * Enter a parse tree produced by {@link OpenFGAModelParser#usersetRestrictionList}.
	 * @param ctx the parse tree
	 */
	void enterUsersetRestrictionList(OpenFGAModelParser.UsersetRestrictionListContext ctx);
	/**
	 * Exit a parse tree produced by {@link OpenFGAModelParser#usersetRestrictionList}.
	 * @param ctx the parse tree
	 */
	void exitUsersetRestrictionList(OpenFGAModelParser.UsersetRestrictionListContext ctx);
}