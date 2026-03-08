// Generated from OpenFGAModel.g4 by ANTLR 4.13.2
package org.kitona.zus.infrastructure.engine.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class OpenFGAModelParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MODEL=1, TYPE=2, RELATIONS=3, DEFINE=4, AS=5, UNION=6, INTERSECTION=7, 
		EXCLUSION=8, SELF=9, FROM=10, HASH=11, LPAREN=12, RPAREN=13, COLON=14, 
		COMMA=15, LBRACK=16, RBRACK=17, NEWLINE=18, IDENTIFIER=19, WS=20, COMMENT=21, 
		UNRECOGNIZED_CHAR=22;
	public static final int
		RULE_model = 0, RULE_typeDefinition = 1, RULE_typeRestriction = 2, RULE_typeRestrictionList = 3, 
		RULE_typeRestrictionItem = 4, RULE_relationBlock = 5, RULE_defineStatement = 6, 
		RULE_rewrite = 7, RULE_union = 8, RULE_intersection = 9, RULE_exclusion = 10, 
		RULE_primary = 11, RULE_relationName = 12, RULE_computedUserset = 13, 
		RULE_tupleToUserset = 14, RULE_usersetRestrictionList = 15;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "typeDefinition", "typeRestriction", "typeRestrictionList", 
			"typeRestrictionItem", "relationBlock", "defineStatement", "rewrite", 
			"union", "intersection", "exclusion", "primary", "relationName", "computedUserset", 
			"tupleToUserset", "usersetRestrictionList"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'model'", "'type'", "'relations'", "'define'", "'as'", "'or'", 
			"'and'", "'but not'", "'self'", "'from'", "'#'", "'('", "')'", "':'", 
			"','", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MODEL", "TYPE", "RELATIONS", "DEFINE", "AS", "UNION", "INTERSECTION", 
			"EXCLUSION", "SELF", "FROM", "HASH", "LPAREN", "RPAREN", "COLON", "COMMA", 
			"LBRACK", "RBRACK", "NEWLINE", "IDENTIFIER", "WS", "COMMENT", "UNRECOGNIZED_CHAR"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "OpenFGAModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public OpenFGAModelParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ModelContext extends ParserRuleContext {
		public TerminalNode MODEL() { return getToken(OpenFGAModelParser.MODEL, 0); }
		public TerminalNode IDENTIFIER() { return getToken(OpenFGAModelParser.IDENTIFIER, 0); }
		public TerminalNode EOF() { return getToken(OpenFGAModelParser.EOF, 0); }
		public List<TypeDefinitionContext> typeDefinition() {
			return getRuleContexts(TypeDefinitionContext.class);
		}
		public TypeDefinitionContext typeDefinition(int i) {
			return getRuleContext(TypeDefinitionContext.class,i);
		}
		public ModelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_model; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterModel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitModel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitModel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelContext model() throws RecognitionException {
		ModelContext _localctx = new ModelContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_model);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(32);
			match(MODEL);
			setState(33);
			match(IDENTIFIER);
			setState(35); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(34);
				typeDefinition();
				}
				}
				setState(37); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==TYPE );
			setState(39);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeDefinitionContext extends ParserRuleContext {
		public TerminalNode TYPE() { return getToken(OpenFGAModelParser.TYPE, 0); }
		public TerminalNode IDENTIFIER() { return getToken(OpenFGAModelParser.IDENTIFIER, 0); }
		public TypeRestrictionContext typeRestriction() {
			return getRuleContext(TypeRestrictionContext.class,0);
		}
		public RelationBlockContext relationBlock() {
			return getRuleContext(RelationBlockContext.class,0);
		}
		public TypeDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTypeDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTypeDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTypeDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeDefinitionContext typeDefinition() throws RecognitionException {
		TypeDefinitionContext _localctx = new TypeDefinitionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_typeDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(41);
			match(TYPE);
			setState(42);
			match(IDENTIFIER);
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(43);
				typeRestriction();
				}
			}

			setState(47);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RELATIONS) {
				{
				setState(46);
				relationBlock();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeRestrictionContext extends ParserRuleContext {
		public TerminalNode FROM() { return getToken(OpenFGAModelParser.FROM, 0); }
		public TerminalNode LPAREN() { return getToken(OpenFGAModelParser.LPAREN, 0); }
		public TypeRestrictionListContext typeRestrictionList() {
			return getRuleContext(TypeRestrictionListContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(OpenFGAModelParser.RPAREN, 0); }
		public TypeRestrictionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeRestriction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTypeRestriction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTypeRestriction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTypeRestriction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeRestrictionContext typeRestriction() throws RecognitionException {
		TypeRestrictionContext _localctx = new TypeRestrictionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_typeRestriction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(49);
			match(FROM);
			setState(50);
			match(LPAREN);
			setState(51);
			typeRestrictionList();
			setState(52);
			match(RPAREN);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeRestrictionListContext extends ParserRuleContext {
		public List<TypeRestrictionItemContext> typeRestrictionItem() {
			return getRuleContexts(TypeRestrictionItemContext.class);
		}
		public TypeRestrictionItemContext typeRestrictionItem(int i) {
			return getRuleContext(TypeRestrictionItemContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(OpenFGAModelParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OpenFGAModelParser.COMMA, i);
		}
		public TypeRestrictionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeRestrictionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTypeRestrictionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTypeRestrictionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTypeRestrictionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeRestrictionListContext typeRestrictionList() throws RecognitionException {
		TypeRestrictionListContext _localctx = new TypeRestrictionListContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_typeRestrictionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			typeRestrictionItem();
			setState(59);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(55);
				match(COMMA);
				setState(56);
				typeRestrictionItem();
				}
				}
				setState(61);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeRestrictionItemContext extends ParserRuleContext {
		public List<RelationNameContext> relationName() {
			return getRuleContexts(RelationNameContext.class);
		}
		public RelationNameContext relationName(int i) {
			return getRuleContext(RelationNameContext.class,i);
		}
		public TerminalNode HASH() { return getToken(OpenFGAModelParser.HASH, 0); }
		public TypeRestrictionItemContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeRestrictionItem; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTypeRestrictionItem(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTypeRestrictionItem(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTypeRestrictionItem(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeRestrictionItemContext typeRestrictionItem() throws RecognitionException {
		TypeRestrictionItemContext _localctx = new TypeRestrictionItemContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_typeRestrictionItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			relationName();
			setState(65);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HASH) {
				{
				setState(63);
				match(HASH);
				setState(64);
				relationName();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationBlockContext extends ParserRuleContext {
		public TerminalNode RELATIONS() { return getToken(OpenFGAModelParser.RELATIONS, 0); }
		public List<DefineStatementContext> defineStatement() {
			return getRuleContexts(DefineStatementContext.class);
		}
		public DefineStatementContext defineStatement(int i) {
			return getRuleContext(DefineStatementContext.class,i);
		}
		public RelationBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterRelationBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitRelationBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitRelationBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationBlockContext relationBlock() throws RecognitionException {
		RelationBlockContext _localctx = new RelationBlockContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_relationBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67);
			match(RELATIONS);
			setState(69); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(68);
				defineStatement();
				}
				}
				setState(71); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==DEFINE );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DefineStatementContext extends ParserRuleContext {
		public TerminalNode DEFINE() { return getToken(OpenFGAModelParser.DEFINE, 0); }
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
		public TerminalNode AS() { return getToken(OpenFGAModelParser.AS, 0); }
		public RewriteContext rewrite() {
			return getRuleContext(RewriteContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(OpenFGAModelParser.NEWLINE, 0); }
		public DefineStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_defineStatement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterDefineStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitDefineStatement(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitDefineStatement(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DefineStatementContext defineStatement() throws RecognitionException {
		DefineStatementContext _localctx = new DefineStatementContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_defineStatement);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(73);
			match(DEFINE);
			setState(74);
			relationName();
			setState(75);
			match(AS);
			setState(76);
			rewrite();
			setState(77);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RewriteContext extends ParserRuleContext {
		public UnionContext union() {
			return getRuleContext(UnionContext.class,0);
		}
		public RewriteContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rewrite; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterRewrite(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitRewrite(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitRewrite(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RewriteContext rewrite() throws RecognitionException {
		RewriteContext _localctx = new RewriteContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_rewrite);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			union();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UnionContext extends ParserRuleContext {
		public List<IntersectionContext> intersection() {
			return getRuleContexts(IntersectionContext.class);
		}
		public IntersectionContext intersection(int i) {
			return getRuleContext(IntersectionContext.class,i);
		}
		public List<TerminalNode> UNION() { return getTokens(OpenFGAModelParser.UNION); }
		public TerminalNode UNION(int i) {
			return getToken(OpenFGAModelParser.UNION, i);
		}
		public UnionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_union; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterUnion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitUnion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitUnion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UnionContext union() throws RecognitionException {
		UnionContext _localctx = new UnionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_union);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(81);
			intersection();
			setState(86);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UNION) {
				{
				{
				setState(82);
				match(UNION);
				setState(83);
				intersection();
				}
				}
				setState(88);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IntersectionContext extends ParserRuleContext {
		public List<ExclusionContext> exclusion() {
			return getRuleContexts(ExclusionContext.class);
		}
		public ExclusionContext exclusion(int i) {
			return getRuleContext(ExclusionContext.class,i);
		}
		public List<TerminalNode> INTERSECTION() { return getTokens(OpenFGAModelParser.INTERSECTION); }
		public TerminalNode INTERSECTION(int i) {
			return getToken(OpenFGAModelParser.INTERSECTION, i);
		}
		public IntersectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_intersection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterIntersection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitIntersection(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitIntersection(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IntersectionContext intersection() throws RecognitionException {
		IntersectionContext _localctx = new IntersectionContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_intersection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			exclusion();
			setState(94);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECTION) {
				{
				{
				setState(90);
				match(INTERSECTION);
				setState(91);
				exclusion();
				}
				}
				setState(96);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExclusionContext extends ParserRuleContext {
		public List<PrimaryContext> primary() {
			return getRuleContexts(PrimaryContext.class);
		}
		public PrimaryContext primary(int i) {
			return getRuleContext(PrimaryContext.class,i);
		}
		public List<TerminalNode> EXCLUSION() { return getTokens(OpenFGAModelParser.EXCLUSION); }
		public TerminalNode EXCLUSION(int i) {
			return getToken(OpenFGAModelParser.EXCLUSION, i);
		}
		public ExclusionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exclusion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterExclusion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitExclusion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitExclusion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExclusionContext exclusion() throws RecognitionException {
		ExclusionContext _localctx = new ExclusionContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_exclusion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			primary();
			setState(102);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==EXCLUSION) {
				{
				{
				setState(98);
				match(EXCLUSION);
				setState(99);
				primary();
				}
				}
				setState(104);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PrimaryContext extends ParserRuleContext {
		public TerminalNode SELF() { return getToken(OpenFGAModelParser.SELF, 0); }
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
		public ComputedUsersetContext computedUserset() {
			return getRuleContext(ComputedUsersetContext.class,0);
		}
		public TupleToUsersetContext tupleToUserset() {
			return getRuleContext(TupleToUsersetContext.class,0);
		}
		public UsersetRestrictionListContext usersetRestrictionList() {
			return getRuleContext(UsersetRestrictionListContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(OpenFGAModelParser.LPAREN, 0); }
		public RewriteContext rewrite() {
			return getRuleContext(RewriteContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(OpenFGAModelParser.RPAREN, 0); }
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitPrimary(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitPrimary(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_primary);
		try {
			setState(114);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,9,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(105);
				match(SELF);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(106);
				relationName();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(107);
				computedUserset();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(108);
				tupleToUserset();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(109);
				usersetRestrictionList();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(110);
				match(LPAREN);
				setState(111);
				rewrite();
				setState(112);
				match(RPAREN);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class RelationNameContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(OpenFGAModelParser.IDENTIFIER, 0); }
		public RelationNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterRelationName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitRelationName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitRelationName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationNameContext relationName() throws RecognitionException {
		RelationNameContext _localctx = new RelationNameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_relationName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(116);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ComputedUsersetContext extends ParserRuleContext {
		public List<RelationNameContext> relationName() {
			return getRuleContexts(RelationNameContext.class);
		}
		public RelationNameContext relationName(int i) {
			return getRuleContext(RelationNameContext.class,i);
		}
		public TerminalNode HASH() { return getToken(OpenFGAModelParser.HASH, 0); }
		public TerminalNode COLON() { return getToken(OpenFGAModelParser.COLON, 0); }
		public ComputedUsersetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_computedUserset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterComputedUserset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitComputedUserset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitComputedUserset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ComputedUsersetContext computedUserset() throws RecognitionException {
		ComputedUsersetContext _localctx = new ComputedUsersetContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_computedUserset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(121);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,10,_ctx) ) {
			case 1:
				{
				setState(118);
				relationName();
				setState(119);
				match(COLON);
				}
				break;
			}
			setState(123);
			relationName();
			setState(124);
			match(HASH);
			setState(125);
			relationName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TupleToUsersetContext extends ParserRuleContext {
		public List<RelationNameContext> relationName() {
			return getRuleContexts(RelationNameContext.class);
		}
		public RelationNameContext relationName(int i) {
			return getRuleContext(RelationNameContext.class,i);
		}
		public TerminalNode FROM() { return getToken(OpenFGAModelParser.FROM, 0); }
		public TupleToUsersetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tupleToUserset; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTupleToUserset(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTupleToUserset(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTupleToUserset(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleToUsersetContext tupleToUserset() throws RecognitionException {
		TupleToUsersetContext _localctx = new TupleToUsersetContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_tupleToUserset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127);
			relationName();
			setState(128);
			match(FROM);
			setState(129);
			relationName();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class UsersetRestrictionListContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(OpenFGAModelParser.LBRACK, 0); }
		public List<TypeRestrictionItemContext> typeRestrictionItem() {
			return getRuleContexts(TypeRestrictionItemContext.class);
		}
		public TypeRestrictionItemContext typeRestrictionItem(int i) {
			return getRuleContext(TypeRestrictionItemContext.class,i);
		}
		public TerminalNode RBRACK() { return getToken(OpenFGAModelParser.RBRACK, 0); }
		public List<TerminalNode> COMMA() { return getTokens(OpenFGAModelParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OpenFGAModelParser.COMMA, i);
		}
		public UsersetRestrictionListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_usersetRestrictionList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterUsersetRestrictionList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitUsersetRestrictionList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitUsersetRestrictionList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final UsersetRestrictionListContext usersetRestrictionList() throws RecognitionException {
		UsersetRestrictionListContext _localctx = new UsersetRestrictionListContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_usersetRestrictionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(LBRACK);
			setState(132);
			typeRestrictionItem();
			setState(137);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(133);
				match(COMMA);
				setState(134);
				typeRestrictionItem();
				}
				}
				setState(139);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(140);
			match(RBRACK);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\u0004\u0001\u0016\u008f\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001"+
		"\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004"+
		"\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007"+
		"\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b"+
		"\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007"+
		"\u000f\u0001\u0000\u0001\u0000\u0001\u0000\u0004\u0000$\b\u0000\u000b"+
		"\u0000\f\u0000%\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0003\u0001-\b\u0001\u0001\u0001\u0003\u00010\b\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0005\u0003:\b\u0003\n\u0003\f\u0003=\t\u0003\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0003\u0004B\b\u0004\u0001\u0005\u0001\u0005"+
		"\u0004\u0005F\b\u0005\u000b\u0005\f\u0005G\u0001\u0006\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0005\bU\b\b\n\b\f\bX\t\b\u0001\t\u0001\t\u0001\t\u0005"+
		"\t]\b\t\n\t\f\t`\t\t\u0001\n\u0001\n\u0001\n\u0005\ne\b\n\n\n\f\nh\t\n"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b"+
		"\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000bs\b\u000b\u0001\f\u0001"+
		"\f\u0001\r\u0001\r\u0001\r\u0003\rz\b\r\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0005\u000f\u0088\b\u000f\n\u000f\f\u000f\u008b"+
		"\t\u000f\u0001\u000f\u0001\u000f\u0001\u000f\u0000\u0000\u0010\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e"+
		"\u0000\u0000\u008e\u0000 \u0001\u0000\u0000\u0000\u0002)\u0001\u0000\u0000"+
		"\u0000\u00041\u0001\u0000\u0000\u0000\u00066\u0001\u0000\u0000\u0000\b"+
		">\u0001\u0000\u0000\u0000\nC\u0001\u0000\u0000\u0000\fI\u0001\u0000\u0000"+
		"\u0000\u000eO\u0001\u0000\u0000\u0000\u0010Q\u0001\u0000\u0000\u0000\u0012"+
		"Y\u0001\u0000\u0000\u0000\u0014a\u0001\u0000\u0000\u0000\u0016r\u0001"+
		"\u0000\u0000\u0000\u0018t\u0001\u0000\u0000\u0000\u001ay\u0001\u0000\u0000"+
		"\u0000\u001c\u007f\u0001\u0000\u0000\u0000\u001e\u0083\u0001\u0000\u0000"+
		"\u0000 !\u0005\u0001\u0000\u0000!#\u0005\u0013\u0000\u0000\"$\u0003\u0002"+
		"\u0001\u0000#\"\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%#\u0001"+
		"\u0000\u0000\u0000%&\u0001\u0000\u0000\u0000&\'\u0001\u0000\u0000\u0000"+
		"\'(\u0005\u0000\u0000\u0001(\u0001\u0001\u0000\u0000\u0000)*\u0005\u0002"+
		"\u0000\u0000*,\u0005\u0013\u0000\u0000+-\u0003\u0004\u0002\u0000,+\u0001"+
		"\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000-/\u0001\u0000\u0000\u0000"+
		".0\u0003\n\u0005\u0000/.\u0001\u0000\u0000\u0000/0\u0001\u0000\u0000\u0000"+
		"0\u0003\u0001\u0000\u0000\u000012\u0005\n\u0000\u000023\u0005\f\u0000"+
		"\u000034\u0003\u0006\u0003\u000045\u0005\r\u0000\u00005\u0005\u0001\u0000"+
		"\u0000\u00006;\u0003\b\u0004\u000078\u0005\u000f\u0000\u00008:\u0003\b"+
		"\u0004\u000097\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000\u0000;9\u0001"+
		"\u0000\u0000\u0000;<\u0001\u0000\u0000\u0000<\u0007\u0001\u0000\u0000"+
		"\u0000=;\u0001\u0000\u0000\u0000>A\u0003\u0018\f\u0000?@\u0005\u000b\u0000"+
		"\u0000@B\u0003\u0018\f\u0000A?\u0001\u0000\u0000\u0000AB\u0001\u0000\u0000"+
		"\u0000B\t\u0001\u0000\u0000\u0000CE\u0005\u0003\u0000\u0000DF\u0003\f"+
		"\u0006\u0000ED\u0001\u0000\u0000\u0000FG\u0001\u0000\u0000\u0000GE\u0001"+
		"\u0000\u0000\u0000GH\u0001\u0000\u0000\u0000H\u000b\u0001\u0000\u0000"+
		"\u0000IJ\u0005\u0004\u0000\u0000JK\u0003\u0018\f\u0000KL\u0005\u0005\u0000"+
		"\u0000LM\u0003\u000e\u0007\u0000MN\u0005\u0012\u0000\u0000N\r\u0001\u0000"+
		"\u0000\u0000OP\u0003\u0010\b\u0000P\u000f\u0001\u0000\u0000\u0000QV\u0003"+
		"\u0012\t\u0000RS\u0005\u0006\u0000\u0000SU\u0003\u0012\t\u0000TR\u0001"+
		"\u0000\u0000\u0000UX\u0001\u0000\u0000\u0000VT\u0001\u0000\u0000\u0000"+
		"VW\u0001\u0000\u0000\u0000W\u0011\u0001\u0000\u0000\u0000XV\u0001\u0000"+
		"\u0000\u0000Y^\u0003\u0014\n\u0000Z[\u0005\u0007\u0000\u0000[]\u0003\u0014"+
		"\n\u0000\\Z\u0001\u0000\u0000\u0000]`\u0001\u0000\u0000\u0000^\\\u0001"+
		"\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_\u0013\u0001\u0000\u0000"+
		"\u0000`^\u0001\u0000\u0000\u0000af\u0003\u0016\u000b\u0000bc\u0005\b\u0000"+
		"\u0000ce\u0003\u0016\u000b\u0000db\u0001\u0000\u0000\u0000eh\u0001\u0000"+
		"\u0000\u0000fd\u0001\u0000\u0000\u0000fg\u0001\u0000\u0000\u0000g\u0015"+
		"\u0001\u0000\u0000\u0000hf\u0001\u0000\u0000\u0000is\u0005\t\u0000\u0000"+
		"js\u0003\u0018\f\u0000ks\u0003\u001a\r\u0000ls\u0003\u001c\u000e\u0000"+
		"ms\u0003\u001e\u000f\u0000no\u0005\f\u0000\u0000op\u0003\u000e\u0007\u0000"+
		"pq\u0005\r\u0000\u0000qs\u0001\u0000\u0000\u0000ri\u0001\u0000\u0000\u0000"+
		"rj\u0001\u0000\u0000\u0000rk\u0001\u0000\u0000\u0000rl\u0001\u0000\u0000"+
		"\u0000rm\u0001\u0000\u0000\u0000rn\u0001\u0000\u0000\u0000s\u0017\u0001"+
		"\u0000\u0000\u0000tu\u0005\u0013\u0000\u0000u\u0019\u0001\u0000\u0000"+
		"\u0000vw\u0003\u0018\f\u0000wx\u0005\u000e\u0000\u0000xz\u0001\u0000\u0000"+
		"\u0000yv\u0001\u0000\u0000\u0000yz\u0001\u0000\u0000\u0000z{\u0001\u0000"+
		"\u0000\u0000{|\u0003\u0018\f\u0000|}\u0005\u000b\u0000\u0000}~\u0003\u0018"+
		"\f\u0000~\u001b\u0001\u0000\u0000\u0000\u007f\u0080\u0003\u0018\f\u0000"+
		"\u0080\u0081\u0005\n\u0000\u0000\u0081\u0082\u0003\u0018\f\u0000\u0082"+
		"\u001d\u0001\u0000\u0000\u0000\u0083\u0084\u0005\u0010\u0000\u0000\u0084"+
		"\u0089\u0003\b\u0004\u0000\u0085\u0086\u0005\u000f\u0000\u0000\u0086\u0088"+
		"\u0003\b\u0004\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0088\u008b\u0001"+
		"\u0000\u0000\u0000\u0089\u0087\u0001\u0000\u0000\u0000\u0089\u008a\u0001"+
		"\u0000\u0000\u0000\u008a\u008c\u0001\u0000\u0000\u0000\u008b\u0089\u0001"+
		"\u0000\u0000\u0000\u008c\u008d\u0005\u0011\u0000\u0000\u008d\u001f\u0001"+
		"\u0000\u0000\u0000\f%,/;AGV^fry\u0089";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}