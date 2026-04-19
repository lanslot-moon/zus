// Generated from /Users/kitona/IdeaProjects/zus/zus-infrastructure/src/main/java/org/kitona/zus/infrastructure/engine/parser/OpenFGAModel.g4 by ANTLR 4.13.2
package org.kitona.zus.infrastructure.engine.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class OpenFGAModelParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MODEL=1, SCHEMA=2, TYPE=3, RELATIONS=4, DEFINE=5, CONDITIONS=6, CONDITION=7, 
		AS=8, WITH=9, UNION=10, INTERSECTION=11, BUT=12, NOT=13, SELF=14, THIS=15, 
		FROM=16, HASH=17, LPAREN=18, RPAREN=19, COLON=20, DOUBLE_COLON=21, COMMA=22, 
		STAR=23, LT=24, GT=25, LBRACK=26, RBRACK=27, LBRACE=28, RBRACE=29, NEWLINE=30, 
		IDENTIFIER=31, VERSION=32, WS=33, COMMENT=34;
	public static final int
		RULE_model = 0, RULE_modelHeader = 1, RULE_schemaVersion = 2, RULE_typeDefinition = 3, 
		RULE_typeRestriction = 4, RULE_typeRestrictionList = 5, RULE_typeRestrictionItem = 6, 
		RULE_typeRestrictionBase = 7, RULE_relationBlock = 8, RULE_defineStatement = 9, 
		RULE_conditionBlock = 10, RULE_conditionDefinition = 11, RULE_conditionParameterList = 12, 
		RULE_conditionParameter = 13, RULE_conditionType = 14, RULE_conditionExpression = 15, 
		RULE_rewrite = 16, RULE_union = 17, RULE_intersection = 18, RULE_exclusion = 19, 
		RULE_primary = 20, RULE_relationName = 21, RULE_qualifiedName = 22, RULE_computedUserset = 23, 
		RULE_tupleToUserset = 24, RULE_usersetRestrictionList = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"model", "modelHeader", "schemaVersion", "typeDefinition", "typeRestriction", 
			"typeRestrictionList", "typeRestrictionItem", "typeRestrictionBase", 
			"relationBlock", "defineStatement", "conditionBlock", "conditionDefinition", 
			"conditionParameterList", "conditionParameter", "conditionType", "conditionExpression", 
			"rewrite", "union", "intersection", "exclusion", "primary", "relationName", 
			"qualifiedName", "computedUserset", "tupleToUserset", "usersetRestrictionList"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'model'", "'schema'", "'type'", "'relations'", "'define'", "'conditions'", 
			"'condition'", "'as'", "'with'", "'or'", "'and'", "'but'", "'not'", "'self'", 
			"'this'", "'from'", "'#'", "'('", "')'", "':'", "'::'", "','", "'*'", 
			"'<'", "'>'", "'['", "']'", "'{'", "'}'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "MODEL", "SCHEMA", "TYPE", "RELATIONS", "DEFINE", "CONDITIONS", 
			"CONDITION", "AS", "WITH", "UNION", "INTERSECTION", "BUT", "NOT", "SELF", 
			"THIS", "FROM", "HASH", "LPAREN", "RPAREN", "COLON", "DOUBLE_COLON", 
			"COMMA", "STAR", "LT", "GT", "LBRACK", "RBRACK", "LBRACE", "RBRACE", 
			"NEWLINE", "IDENTIFIER", "VERSION", "WS", "COMMENT"
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
		public ModelHeaderContext modelHeader() {
			return getRuleContext(ModelHeaderContext.class,0);
		}
		public TerminalNode EOF() { return getToken(OpenFGAModelParser.EOF, 0); }
		public List<TypeDefinitionContext> typeDefinition() {
			return getRuleContexts(TypeDefinitionContext.class);
		}
		public TypeDefinitionContext typeDefinition(int i) {
			return getRuleContext(TypeDefinitionContext.class,i);
		}
		public ConditionBlockContext conditionBlock() {
			return getRuleContext(ConditionBlockContext.class,0);
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
			setState(52);
			match(MODEL);
			setState(53);
			modelHeader();
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==TYPE) {
				{
				{
				setState(54);
				typeDefinition();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(61);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==CONDITIONS) {
				{
				setState(60);
				conditionBlock();
				}
			}

			setState(63);
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
	public static class ModelHeaderContext extends ParserRuleContext {
		public TerminalNode SCHEMA() { return getToken(OpenFGAModelParser.SCHEMA, 0); }
		public SchemaVersionContext schemaVersion() {
			return getRuleContext(SchemaVersionContext.class,0);
		}
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public ModelHeaderContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modelHeader; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterModelHeader(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitModelHeader(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitModelHeader(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModelHeaderContext modelHeader() throws RecognitionException {
		ModelHeaderContext _localctx = new ModelHeaderContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_modelHeader);
		try {
			setState(68);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case SCHEMA:
				enterOuterAlt(_localctx, 1);
				{
				setState(65);
				match(SCHEMA);
				setState(66);
				schemaVersion();
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(67);
				qualifiedName();
				}
				break;
			default:
				throw new NoViableAltException(this);
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
	public static class SchemaVersionContext extends ParserRuleContext {
		public TerminalNode VERSION() { return getToken(OpenFGAModelParser.VERSION, 0); }
		public SchemaVersionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_schemaVersion; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterSchemaVersion(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitSchemaVersion(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitSchemaVersion(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SchemaVersionContext schemaVersion() throws RecognitionException {
		SchemaVersionContext _localctx = new SchemaVersionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_schemaVersion);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(VERSION);
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
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
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
		enterRule(_localctx, 6, RULE_typeDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			match(TYPE);
			setState(73);
			qualifiedName();
			setState(75);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==FROM) {
				{
				setState(74);
				typeRestriction();
				}
			}

			setState(78);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==RELATIONS) {
				{
				setState(77);
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
		enterRule(_localctx, 8, RULE_typeRestriction);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80);
			match(FROM);
			setState(81);
			match(LPAREN);
			setState(82);
			typeRestrictionList();
			setState(83);
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
		enterRule(_localctx, 10, RULE_typeRestrictionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(85);
			typeRestrictionItem();
			setState(90);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(86);
				match(COMMA);
				setState(87);
				typeRestrictionItem();
				}
				}
				setState(92);
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
		public TypeRestrictionBaseContext typeRestrictionBase() {
			return getRuleContext(TypeRestrictionBaseContext.class,0);
		}
		public TerminalNode WITH() { return getToken(OpenFGAModelParser.WITH, 0); }
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
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
		enterRule(_localctx, 12, RULE_typeRestrictionItem);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(93);
			typeRestrictionBase();
			setState(96);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==WITH) {
				{
				setState(94);
				match(WITH);
				setState(95);
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
	public static class TypeRestrictionBaseContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public TerminalNode HASH() { return getToken(OpenFGAModelParser.HASH, 0); }
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
		public TerminalNode COLON() { return getToken(OpenFGAModelParser.COLON, 0); }
		public TerminalNode STAR() { return getToken(OpenFGAModelParser.STAR, 0); }
		public TypeRestrictionBaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeRestrictionBase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterTypeRestrictionBase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitTypeRestrictionBase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitTypeRestrictionBase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeRestrictionBaseContext typeRestrictionBase() throws RecognitionException {
		TypeRestrictionBaseContext _localctx = new TypeRestrictionBaseContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_typeRestrictionBase);
		int _la;
		try {
			setState(107);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(98);
				qualifiedName();
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==HASH) {
					{
					setState(99);
					match(HASH);
					setState(100);
					relationName();
					}
				}

				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(103);
				qualifiedName();
				setState(104);
				match(COLON);
				setState(105);
				match(STAR);
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
		enterRule(_localctx, 16, RULE_relationBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(109);
			match(RELATIONS);
			setState(111); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(110);
				defineStatement();
				}
				}
				setState(113); 
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
		public RewriteContext rewrite() {
			return getRuleContext(RewriteContext.class,0);
		}
		public TerminalNode AS() { return getToken(OpenFGAModelParser.AS, 0); }
		public TerminalNode COLON() { return getToken(OpenFGAModelParser.COLON, 0); }
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
		enterRule(_localctx, 18, RULE_defineStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115);
			match(DEFINE);
			setState(116);
			relationName();
			setState(117);
			_la = _input.LA(1);
			if ( !(_la==AS || _la==COLON) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(118);
			rewrite();
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEWLINE) {
				{
				setState(119);
				match(NEWLINE);
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
	public static class ConditionBlockContext extends ParserRuleContext {
		public TerminalNode CONDITIONS() { return getToken(OpenFGAModelParser.CONDITIONS, 0); }
		public List<ConditionDefinitionContext> conditionDefinition() {
			return getRuleContexts(ConditionDefinitionContext.class);
		}
		public ConditionDefinitionContext conditionDefinition(int i) {
			return getRuleContext(ConditionDefinitionContext.class,i);
		}
		public ConditionBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionBlock; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionBlock(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionBlock(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionBlockContext conditionBlock() throws RecognitionException {
		ConditionBlockContext _localctx = new ConditionBlockContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_conditionBlock);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(CONDITIONS);
			setState(124); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(123);
				conditionDefinition();
				}
				}
				setState(126); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==CONDITION );
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
	public static class ConditionDefinitionContext extends ParserRuleContext {
		public TerminalNode CONDITION() { return getToken(OpenFGAModelParser.CONDITION, 0); }
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(OpenFGAModelParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(OpenFGAModelParser.RPAREN, 0); }
		public TerminalNode LBRACE() { return getToken(OpenFGAModelParser.LBRACE, 0); }
		public ConditionExpressionContext conditionExpression() {
			return getRuleContext(ConditionExpressionContext.class,0);
		}
		public TerminalNode RBRACE() { return getToken(OpenFGAModelParser.RBRACE, 0); }
		public ConditionParameterListContext conditionParameterList() {
			return getRuleContext(ConditionParameterListContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(OpenFGAModelParser.NEWLINE, 0); }
		public ConditionDefinitionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionDefinition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionDefinition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionDefinition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionDefinition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionDefinitionContext conditionDefinition() throws RecognitionException {
		ConditionDefinitionContext _localctx = new ConditionDefinitionContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_conditionDefinition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			match(CONDITION);
			setState(129);
			relationName();
			setState(130);
			match(LPAREN);
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==IDENTIFIER) {
				{
				setState(131);
				conditionParameterList();
				}
			}

			setState(134);
			match(RPAREN);
			setState(135);
			match(LBRACE);
			setState(136);
			conditionExpression();
			setState(137);
			match(RBRACE);
			setState(139);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NEWLINE) {
				{
				setState(138);
				match(NEWLINE);
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
	public static class ConditionParameterListContext extends ParserRuleContext {
		public List<ConditionParameterContext> conditionParameter() {
			return getRuleContexts(ConditionParameterContext.class);
		}
		public ConditionParameterContext conditionParameter(int i) {
			return getRuleContext(ConditionParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(OpenFGAModelParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OpenFGAModelParser.COMMA, i);
		}
		public ConditionParameterListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionParameterList; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionParameterList(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionParameterList(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionParameterList(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionParameterListContext conditionParameterList() throws RecognitionException {
		ConditionParameterListContext _localctx = new ConditionParameterListContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_conditionParameterList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(141);
			conditionParameter();
			setState(146);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(142);
				match(COMMA);
				setState(143);
				conditionParameter();
				}
				}
				setState(148);
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
	public static class ConditionParameterContext extends ParserRuleContext {
		public RelationNameContext relationName() {
			return getRuleContext(RelationNameContext.class,0);
		}
		public TerminalNode COLON() { return getToken(OpenFGAModelParser.COLON, 0); }
		public ConditionTypeContext conditionType() {
			return getRuleContext(ConditionTypeContext.class,0);
		}
		public ConditionParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionParameter; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionParameter(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionParameter(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionParameterContext conditionParameter() throws RecognitionException {
		ConditionParameterContext _localctx = new ConditionParameterContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_conditionParameter);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(149);
			relationName();
			setState(150);
			match(COLON);
			setState(151);
			conditionType();
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
	public static class ConditionTypeContext extends ParserRuleContext {
		public QualifiedNameContext qualifiedName() {
			return getRuleContext(QualifiedNameContext.class,0);
		}
		public TerminalNode LT() { return getToken(OpenFGAModelParser.LT, 0); }
		public List<ConditionTypeContext> conditionType() {
			return getRuleContexts(ConditionTypeContext.class);
		}
		public ConditionTypeContext conditionType(int i) {
			return getRuleContext(ConditionTypeContext.class,i);
		}
		public TerminalNode GT() { return getToken(OpenFGAModelParser.GT, 0); }
		public List<TerminalNode> COMMA() { return getTokens(OpenFGAModelParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(OpenFGAModelParser.COMMA, i);
		}
		public ConditionTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionType; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionTypeContext conditionType() throws RecognitionException {
		ConditionTypeContext _localctx = new ConditionTypeContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_conditionType);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153);
			qualifiedName();
			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LT) {
				{
				setState(154);
				match(LT);
				setState(155);
				conditionType();
				setState(160);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(156);
					match(COMMA);
					setState(157);
					conditionType();
					}
					}
					setState(162);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(163);
				match(GT);
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
	public static class ConditionExpressionContext extends ParserRuleContext {
		public ConditionExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionExpression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterConditionExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitConditionExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitConditionExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionExpressionContext conditionExpression() throws RecognitionException {
		ConditionExpressionContext _localctx = new ConditionExpressionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_conditionExpression);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(170);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
			while ( _alt!=1 && _alt!= ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(167);
					matchWildcard();
					}
					} 
				}
				setState(172);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,17,_ctx);
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
		enterRule(_localctx, 32, RULE_rewrite);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(173);
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
		enterRule(_localctx, 34, RULE_union);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175);
			intersection();
			setState(180);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==UNION) {
				{
				{
				setState(176);
				match(UNION);
				setState(177);
				intersection();
				}
				}
				setState(182);
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
		enterRule(_localctx, 36, RULE_intersection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(183);
			exclusion();
			setState(188);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INTERSECTION) {
				{
				{
				setState(184);
				match(INTERSECTION);
				setState(185);
				exclusion();
				}
				}
				setState(190);
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
		public List<TerminalNode> BUT() { return getTokens(OpenFGAModelParser.BUT); }
		public TerminalNode BUT(int i) {
			return getToken(OpenFGAModelParser.BUT, i);
		}
		public List<TerminalNode> NOT() { return getTokens(OpenFGAModelParser.NOT); }
		public TerminalNode NOT(int i) {
			return getToken(OpenFGAModelParser.NOT, i);
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
		enterRule(_localctx, 38, RULE_exclusion);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(191);
			primary();
			setState(197);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==BUT) {
				{
				{
				setState(192);
				match(BUT);
				setState(193);
				match(NOT);
				setState(194);
				primary();
				}
				}
				setState(199);
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
		public TerminalNode THIS() { return getToken(OpenFGAModelParser.THIS, 0); }
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
		enterRule(_localctx, 40, RULE_primary);
		try {
			setState(210);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(200);
				match(SELF);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(201);
				match(THIS);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(202);
				relationName();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(203);
				computedUserset();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(204);
				tupleToUserset();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(205);
				usersetRestrictionList();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(206);
				match(LPAREN);
				setState(207);
				rewrite();
				setState(208);
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
		enterRule(_localctx, 42, RULE_relationName);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(212);
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
	public static class QualifiedNameContext extends ParserRuleContext {
		public List<TerminalNode> IDENTIFIER() { return getTokens(OpenFGAModelParser.IDENTIFIER); }
		public TerminalNode IDENTIFIER(int i) {
			return getToken(OpenFGAModelParser.IDENTIFIER, i);
		}
		public List<TerminalNode> DOUBLE_COLON() { return getTokens(OpenFGAModelParser.DOUBLE_COLON); }
		public TerminalNode DOUBLE_COLON(int i) {
			return getToken(OpenFGAModelParser.DOUBLE_COLON, i);
		}
		public QualifiedNameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_qualifiedName; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).enterQualifiedName(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof OpenFGAModelListener ) ((OpenFGAModelListener)listener).exitQualifiedName(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof OpenFGAModelVisitor ) return ((OpenFGAModelVisitor<? extends T>)visitor).visitQualifiedName(this);
			else return visitor.visitChildren(this);
		}
	}

	public final QualifiedNameContext qualifiedName() throws RecognitionException {
		QualifiedNameContext _localctx = new QualifiedNameContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_qualifiedName);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			match(IDENTIFIER);
			setState(219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOUBLE_COLON) {
				{
				{
				setState(215);
				match(DOUBLE_COLON);
				setState(216);
				match(IDENTIFIER);
				}
				}
				setState(221);
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
		enterRule(_localctx, 46, RULE_computedUserset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(225);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,23,_ctx) ) {
			case 1:
				{
				setState(222);
				relationName();
				setState(223);
				match(COLON);
				}
				break;
			}
			setState(227);
			relationName();
			setState(228);
			match(HASH);
			setState(229);
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
		enterRule(_localctx, 48, RULE_tupleToUserset);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			relationName();
			setState(232);
			match(FROM);
			setState(233);
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
		enterRule(_localctx, 50, RULE_usersetRestrictionList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(235);
			match(LBRACK);
			setState(236);
			typeRestrictionItem();
			setState(241);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(237);
				match(COMMA);
				setState(238);
				typeRestrictionItem();
				}
				}
				setState(243);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(244);
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
		"\u0004\u0001\"\u00f7\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0001\u0000\u0001\u0000\u0001\u0000\u0005\u0000"+
		"8\b\u0000\n\u0000\f\u0000;\t\u0000\u0001\u0000\u0003\u0000>\b\u0000\u0001"+
		"\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0001\u0001\u0003\u0001E\b"+
		"\u0001\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0003"+
		"\u0003L\b\u0003\u0001\u0003\u0003\u0003O\b\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005"+
		"\u0005\u0005Y\b\u0005\n\u0005\f\u0005\\\t\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0003\u0006a\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u0007f\b\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0003\u0007l\b\u0007\u0001\b\u0001\b\u0004\bp\b\b\u000b\b\f\bq\u0001"+
		"\t\u0001\t\u0001\t\u0001\t\u0001\t\u0003\ty\b\t\u0001\n\u0001\n\u0004"+
		"\n}\b\n\u000b\n\f\n~\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003"+
		"\u000b\u0085\b\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\u000b\u0003\u000b\u008c\b\u000b\u0001\f\u0001\f\u0001\f\u0005\f\u0091"+
		"\b\f\n\f\f\f\u0094\t\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001"+
		"\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0005\u000e\u009f\b\u000e\n"+
		"\u000e\f\u000e\u00a2\t\u000e\u0001\u000e\u0001\u000e\u0003\u000e\u00a6"+
		"\b\u000e\u0001\u000f\u0005\u000f\u00a9\b\u000f\n\u000f\f\u000f\u00ac\t"+
		"\u000f\u0001\u0010\u0001\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0005"+
		"\u0011\u00b3\b\u0011\n\u0011\f\u0011\u00b6\t\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0005\u0012\u00bb\b\u0012\n\u0012\f\u0012\u00be\t\u0012\u0001"+
		"\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00c4\b\u0013\n"+
		"\u0013\f\u0013\u00c7\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014\u0001"+
		"\u0014\u0003\u0014\u00d3\b\u0014\u0001\u0015\u0001\u0015\u0001\u0016\u0001"+
		"\u0016\u0001\u0016\u0005\u0016\u00da\b\u0016\n\u0016\f\u0016\u00dd\t\u0016"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0003\u0017\u00e2\b\u0017\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0019\u0001\u0019\u0001\u0019\u0001\u0019\u0005\u0019"+
		"\u00f0\b\u0019\n\u0019\f\u0019\u00f3\t\u0019\u0001\u0019\u0001\u0019\u0001"+
		"\u0019\u0001\u00aa\u0000\u001a\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010"+
		"\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&(*,.02\u0000\u0001\u0002"+
		"\u0000\b\b\u0014\u0014\u00fa\u00004\u0001\u0000\u0000\u0000\u0002D\u0001"+
		"\u0000\u0000\u0000\u0004F\u0001\u0000\u0000\u0000\u0006H\u0001\u0000\u0000"+
		"\u0000\bP\u0001\u0000\u0000\u0000\nU\u0001\u0000\u0000\u0000\f]\u0001"+
		"\u0000\u0000\u0000\u000ek\u0001\u0000\u0000\u0000\u0010m\u0001\u0000\u0000"+
		"\u0000\u0012s\u0001\u0000\u0000\u0000\u0014z\u0001\u0000\u0000\u0000\u0016"+
		"\u0080\u0001\u0000\u0000\u0000\u0018\u008d\u0001\u0000\u0000\u0000\u001a"+
		"\u0095\u0001\u0000\u0000\u0000\u001c\u0099\u0001\u0000\u0000\u0000\u001e"+
		"\u00aa\u0001\u0000\u0000\u0000 \u00ad\u0001\u0000\u0000\u0000\"\u00af"+
		"\u0001\u0000\u0000\u0000$\u00b7\u0001\u0000\u0000\u0000&\u00bf\u0001\u0000"+
		"\u0000\u0000(\u00d2\u0001\u0000\u0000\u0000*\u00d4\u0001\u0000\u0000\u0000"+
		",\u00d6\u0001\u0000\u0000\u0000.\u00e1\u0001\u0000\u0000\u00000\u00e7"+
		"\u0001\u0000\u0000\u00002\u00eb\u0001\u0000\u0000\u000045\u0005\u0001"+
		"\u0000\u000059\u0003\u0002\u0001\u000068\u0003\u0006\u0003\u000076\u0001"+
		"\u0000\u0000\u00008;\u0001\u0000\u0000\u000097\u0001\u0000\u0000\u0000"+
		"9:\u0001\u0000\u0000\u0000:=\u0001\u0000\u0000\u0000;9\u0001\u0000\u0000"+
		"\u0000<>\u0003\u0014\n\u0000=<\u0001\u0000\u0000\u0000=>\u0001\u0000\u0000"+
		"\u0000>?\u0001\u0000\u0000\u0000?@\u0005\u0000\u0000\u0001@\u0001\u0001"+
		"\u0000\u0000\u0000AB\u0005\u0002\u0000\u0000BE\u0003\u0004\u0002\u0000"+
		"CE\u0003,\u0016\u0000DA\u0001\u0000\u0000\u0000DC\u0001\u0000\u0000\u0000"+
		"E\u0003\u0001\u0000\u0000\u0000FG\u0005 \u0000\u0000G\u0005\u0001\u0000"+
		"\u0000\u0000HI\u0005\u0003\u0000\u0000IK\u0003,\u0016\u0000JL\u0003\b"+
		"\u0004\u0000KJ\u0001\u0000\u0000\u0000KL\u0001\u0000\u0000\u0000LN\u0001"+
		"\u0000\u0000\u0000MO\u0003\u0010\b\u0000NM\u0001\u0000\u0000\u0000NO\u0001"+
		"\u0000\u0000\u0000O\u0007\u0001\u0000\u0000\u0000PQ\u0005\u0010\u0000"+
		"\u0000QR\u0005\u0012\u0000\u0000RS\u0003\n\u0005\u0000ST\u0005\u0013\u0000"+
		"\u0000T\t\u0001\u0000\u0000\u0000UZ\u0003\f\u0006\u0000VW\u0005\u0016"+
		"\u0000\u0000WY\u0003\f\u0006\u0000XV\u0001\u0000\u0000\u0000Y\\\u0001"+
		"\u0000\u0000\u0000ZX\u0001\u0000\u0000\u0000Z[\u0001\u0000\u0000\u0000"+
		"[\u000b\u0001\u0000\u0000\u0000\\Z\u0001\u0000\u0000\u0000]`\u0003\u000e"+
		"\u0007\u0000^_\u0005\t\u0000\u0000_a\u0003*\u0015\u0000`^\u0001\u0000"+
		"\u0000\u0000`a\u0001\u0000\u0000\u0000a\r\u0001\u0000\u0000\u0000be\u0003"+
		",\u0016\u0000cd\u0005\u0011\u0000\u0000df\u0003*\u0015\u0000ec\u0001\u0000"+
		"\u0000\u0000ef\u0001\u0000\u0000\u0000fl\u0001\u0000\u0000\u0000gh\u0003"+
		",\u0016\u0000hi\u0005\u0014\u0000\u0000ij\u0005\u0017\u0000\u0000jl\u0001"+
		"\u0000\u0000\u0000kb\u0001\u0000\u0000\u0000kg\u0001\u0000\u0000\u0000"+
		"l\u000f\u0001\u0000\u0000\u0000mo\u0005\u0004\u0000\u0000np\u0003\u0012"+
		"\t\u0000on\u0001\u0000\u0000\u0000pq\u0001\u0000\u0000\u0000qo\u0001\u0000"+
		"\u0000\u0000qr\u0001\u0000\u0000\u0000r\u0011\u0001\u0000\u0000\u0000"+
		"st\u0005\u0005\u0000\u0000tu\u0003*\u0015\u0000uv\u0007\u0000\u0000\u0000"+
		"vx\u0003 \u0010\u0000wy\u0005\u001e\u0000\u0000xw\u0001\u0000\u0000\u0000"+
		"xy\u0001\u0000\u0000\u0000y\u0013\u0001\u0000\u0000\u0000z|\u0005\u0006"+
		"\u0000\u0000{}\u0003\u0016\u000b\u0000|{\u0001\u0000\u0000\u0000}~\u0001"+
		"\u0000\u0000\u0000~|\u0001\u0000\u0000\u0000~\u007f\u0001\u0000\u0000"+
		"\u0000\u007f\u0015\u0001\u0000\u0000\u0000\u0080\u0081\u0005\u0007\u0000"+
		"\u0000\u0081\u0082\u0003*\u0015\u0000\u0082\u0084\u0005\u0012\u0000\u0000"+
		"\u0083\u0085\u0003\u0018\f\u0000\u0084\u0083\u0001\u0000\u0000\u0000\u0084"+
		"\u0085\u0001\u0000\u0000\u0000\u0085\u0086\u0001\u0000\u0000\u0000\u0086"+
		"\u0087\u0005\u0013\u0000\u0000\u0087\u0088\u0005\u001c\u0000\u0000\u0088"+
		"\u0089\u0003\u001e\u000f\u0000\u0089\u008b\u0005\u001d\u0000\u0000\u008a"+
		"\u008c\u0005\u001e\u0000\u0000\u008b\u008a\u0001\u0000\u0000\u0000\u008b"+
		"\u008c\u0001\u0000\u0000\u0000\u008c\u0017\u0001\u0000\u0000\u0000\u008d"+
		"\u0092\u0003\u001a\r\u0000\u008e\u008f\u0005\u0016\u0000\u0000\u008f\u0091"+
		"\u0003\u001a\r\u0000\u0090\u008e\u0001\u0000\u0000\u0000\u0091\u0094\u0001"+
		"\u0000\u0000\u0000\u0092\u0090\u0001\u0000\u0000\u0000\u0092\u0093\u0001"+
		"\u0000\u0000\u0000\u0093\u0019\u0001\u0000\u0000\u0000\u0094\u0092\u0001"+
		"\u0000\u0000\u0000\u0095\u0096\u0003*\u0015\u0000\u0096\u0097\u0005\u0014"+
		"\u0000\u0000\u0097\u0098\u0003\u001c\u000e\u0000\u0098\u001b\u0001\u0000"+
		"\u0000\u0000\u0099\u00a5\u0003,\u0016\u0000\u009a\u009b\u0005\u0018\u0000"+
		"\u0000\u009b\u00a0\u0003\u001c\u000e\u0000\u009c\u009d\u0005\u0016\u0000"+
		"\u0000\u009d\u009f\u0003\u001c\u000e\u0000\u009e\u009c\u0001\u0000\u0000"+
		"\u0000\u009f\u00a2\u0001\u0000\u0000\u0000\u00a0\u009e\u0001\u0000\u0000"+
		"\u0000\u00a0\u00a1\u0001\u0000\u0000\u0000\u00a1\u00a3\u0001\u0000\u0000"+
		"\u0000\u00a2\u00a0\u0001\u0000\u0000\u0000\u00a3\u00a4\u0005\u0019\u0000"+
		"\u0000\u00a4\u00a6\u0001\u0000\u0000\u0000\u00a5\u009a\u0001\u0000\u0000"+
		"\u0000\u00a5\u00a6\u0001\u0000\u0000\u0000\u00a6\u001d\u0001\u0000\u0000"+
		"\u0000\u00a7\u00a9\t\u0000\u0000\u0000\u00a8\u00a7\u0001\u0000\u0000\u0000"+
		"\u00a9\u00ac\u0001\u0000\u0000\u0000\u00aa\u00ab\u0001\u0000\u0000\u0000"+
		"\u00aa\u00a8\u0001\u0000\u0000\u0000\u00ab\u001f\u0001\u0000\u0000\u0000"+
		"\u00ac\u00aa\u0001\u0000\u0000\u0000\u00ad\u00ae\u0003\"\u0011\u0000\u00ae"+
		"!\u0001\u0000\u0000\u0000\u00af\u00b4\u0003$\u0012\u0000\u00b0\u00b1\u0005"+
		"\n\u0000\u0000\u00b1\u00b3\u0003$\u0012\u0000\u00b2\u00b0\u0001\u0000"+
		"\u0000\u0000\u00b3\u00b6\u0001\u0000\u0000\u0000\u00b4\u00b2\u0001\u0000"+
		"\u0000\u0000\u00b4\u00b5\u0001\u0000\u0000\u0000\u00b5#\u0001\u0000\u0000"+
		"\u0000\u00b6\u00b4\u0001\u0000\u0000\u0000\u00b7\u00bc\u0003&\u0013\u0000"+
		"\u00b8\u00b9\u0005\u000b\u0000\u0000\u00b9\u00bb\u0003&\u0013\u0000\u00ba"+
		"\u00b8\u0001\u0000\u0000\u0000\u00bb\u00be\u0001\u0000\u0000\u0000\u00bc"+
		"\u00ba\u0001\u0000\u0000\u0000\u00bc\u00bd\u0001\u0000\u0000\u0000\u00bd"+
		"%\u0001\u0000\u0000\u0000\u00be\u00bc\u0001\u0000\u0000\u0000\u00bf\u00c5"+
		"\u0003(\u0014\u0000\u00c0\u00c1\u0005\f\u0000\u0000\u00c1\u00c2\u0005"+
		"\r\u0000\u0000\u00c2\u00c4\u0003(\u0014\u0000\u00c3\u00c0\u0001\u0000"+
		"\u0000\u0000\u00c4\u00c7\u0001\u0000\u0000\u0000\u00c5\u00c3\u0001\u0000"+
		"\u0000\u0000\u00c5\u00c6\u0001\u0000\u0000\u0000\u00c6\'\u0001\u0000\u0000"+
		"\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000\u00c8\u00d3\u0005\u000e\u0000"+
		"\u0000\u00c9\u00d3\u0005\u000f\u0000\u0000\u00ca\u00d3\u0003*\u0015\u0000"+
		"\u00cb\u00d3\u0003.\u0017\u0000\u00cc\u00d3\u00030\u0018\u0000\u00cd\u00d3"+
		"\u00032\u0019\u0000\u00ce\u00cf\u0005\u0012\u0000\u0000\u00cf\u00d0\u0003"+
		" \u0010\u0000\u00d0\u00d1\u0005\u0013\u0000\u0000\u00d1\u00d3\u0001\u0000"+
		"\u0000\u0000\u00d2\u00c8\u0001\u0000\u0000\u0000\u00d2\u00c9\u0001\u0000"+
		"\u0000\u0000\u00d2\u00ca\u0001\u0000\u0000\u0000\u00d2\u00cb\u0001\u0000"+
		"\u0000\u0000\u00d2\u00cc\u0001\u0000\u0000\u0000\u00d2\u00cd\u0001\u0000"+
		"\u0000\u0000\u00d2\u00ce\u0001\u0000\u0000\u0000\u00d3)\u0001\u0000\u0000"+
		"\u0000\u00d4\u00d5\u0005\u001f\u0000\u0000\u00d5+\u0001\u0000\u0000\u0000"+
		"\u00d6\u00db\u0005\u001f\u0000\u0000\u00d7\u00d8\u0005\u0015\u0000\u0000"+
		"\u00d8\u00da\u0005\u001f\u0000\u0000\u00d9\u00d7\u0001\u0000\u0000\u0000"+
		"\u00da\u00dd\u0001\u0000\u0000\u0000\u00db\u00d9\u0001\u0000\u0000\u0000"+
		"\u00db\u00dc\u0001\u0000\u0000\u0000\u00dc-\u0001\u0000\u0000\u0000\u00dd"+
		"\u00db\u0001\u0000\u0000\u0000\u00de\u00df\u0003*\u0015\u0000\u00df\u00e0"+
		"\u0005\u0014\u0000\u0000\u00e0\u00e2\u0001\u0000\u0000\u0000\u00e1\u00de"+
		"\u0001\u0000\u0000\u0000\u00e1\u00e2\u0001\u0000\u0000\u0000\u00e2\u00e3"+
		"\u0001\u0000\u0000\u0000\u00e3\u00e4\u0003*\u0015\u0000\u00e4\u00e5\u0005"+
		"\u0011\u0000\u0000\u00e5\u00e6\u0003*\u0015\u0000\u00e6/\u0001\u0000\u0000"+
		"\u0000\u00e7\u00e8\u0003*\u0015\u0000\u00e8\u00e9\u0005\u0010\u0000\u0000"+
		"\u00e9\u00ea\u0003*\u0015\u0000\u00ea1\u0001\u0000\u0000\u0000\u00eb\u00ec"+
		"\u0005\u001a\u0000\u0000\u00ec\u00f1\u0003\f\u0006\u0000\u00ed\u00ee\u0005"+
		"\u0016\u0000\u0000\u00ee\u00f0\u0003\f\u0006\u0000\u00ef\u00ed\u0001\u0000"+
		"\u0000\u0000\u00f0\u00f3\u0001\u0000\u0000\u0000\u00f1\u00ef\u0001\u0000"+
		"\u0000\u0000\u00f1\u00f2\u0001\u0000\u0000\u0000\u00f2\u00f4\u0001\u0000"+
		"\u0000\u0000\u00f3\u00f1\u0001\u0000\u0000\u0000\u00f4\u00f5\u0005\u001b"+
		"\u0000\u0000\u00f53\u0001\u0000\u0000\u0000\u00199=DKNZ`ekqx~\u0084\u008b"+
		"\u0092\u00a0\u00a5\u00aa\u00b4\u00bc\u00c5\u00d2\u00db\u00e1\u00f1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}