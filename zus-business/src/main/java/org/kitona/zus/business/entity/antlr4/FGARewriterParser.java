// Generated from /Users/kitona/IdeaProjects/zus/zus-business/src/main/java/org/kitona/zus/business/entity/antlr4/FGARewriter.g4 by ANTLR 4.13.2
package org.kitona.zus.business.entity.antlr4;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class FGARewriterParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		OR=1, AND=2, FROM=3, SELF=4, TUPLE_TO_USERSET=5, EQ=6, COLON=7, SEMI=8, 
		COMMA=9, HASH=10, LPAREN=11, RPAREN=12, LBRACK=13, RBRACK=14, ID=15, WS=16;
	public static final int
		RULE_parse = 0, RULE_expression = 1, RULE_orExpr = 2, RULE_andExpr = 3, 
		RULE_primaryExpr = 4, RULE_tupleToUsersetExpr = 5, RULE_kvPairs = 6, RULE_kvPair = 7, 
		RULE_fromExpr = 8, RULE_listExpr = 9, RULE_listItems = 10, RULE_relationExpr = 11, 
		RULE_value = 12;
	private static String[] makeRuleNames() {
		return new String[] {
			"parse", "expression", "orExpr", "andExpr", "primaryExpr", "tupleToUsersetExpr", 
			"kvPairs", "kvPair", "fromExpr", "listExpr", "listItems", "relationExpr", 
			"value"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, null, null, null, null, null, "'='", "':'", "';'", "','", "'#'", 
			"'('", "')'", "'['", "']'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "OR", "AND", "FROM", "SELF", "TUPLE_TO_USERSET", "EQ", "COLON", 
			"SEMI", "COMMA", "HASH", "LPAREN", "RPAREN", "LBRACK", "RBRACK", "ID", 
			"WS"
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
	public String getGrammarFileName() { return "FGARewriter.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public FGARewriterParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParseContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode EOF() { return getToken(FGARewriterParser.EOF, 0); }
		public ParseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_parse; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterParse(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitParse(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitParse(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ParseContext parse() throws RecognitionException {
		ParseContext _localctx = new ParseContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_parse);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26);
			expression();
			setState(27);
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
	public static class ExpressionContext extends ParserRuleContext {
		public OrExprContext orExpr() {
			return getRuleContext(OrExprContext.class,0);
		}
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterExpression(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitExpression(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitExpression(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		ExpressionContext _localctx = new ExpressionContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_expression);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			orExpr();
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
	public static class OrExprContext extends ParserRuleContext {
		public List<AndExprContext> andExpr() {
			return getRuleContexts(AndExprContext.class);
		}
		public AndExprContext andExpr(int i) {
			return getRuleContext(AndExprContext.class,i);
		}
		public List<TerminalNode> OR() { return getTokens(FGARewriterParser.OR); }
		public TerminalNode OR(int i) {
			return getToken(FGARewriterParser.OR, i);
		}
		public OrExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterOrExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitOrExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitOrExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrExprContext orExpr() throws RecognitionException {
		OrExprContext _localctx = new OrExprContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_orExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(31);
			andExpr();
			setState(36);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==OR) {
				{
				{
				setState(32);
				match(OR);
				setState(33);
				andExpr();
				}
				}
				setState(38);
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
	public static class AndExprContext extends ParserRuleContext {
		public List<PrimaryExprContext> primaryExpr() {
			return getRuleContexts(PrimaryExprContext.class);
		}
		public PrimaryExprContext primaryExpr(int i) {
			return getRuleContext(PrimaryExprContext.class,i);
		}
		public List<TerminalNode> AND() { return getTokens(FGARewriterParser.AND); }
		public TerminalNode AND(int i) {
			return getToken(FGARewriterParser.AND, i);
		}
		public AndExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_andExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterAndExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitAndExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitAndExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AndExprContext andExpr() throws RecognitionException {
		AndExprContext _localctx = new AndExprContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_andExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(39);
			primaryExpr();
			setState(44);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==AND) {
				{
				{
				setState(40);
				match(AND);
				setState(41);
				primaryExpr();
				}
				}
				setState(46);
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
	public static class PrimaryExprContext extends ParserRuleContext {
		public TupleToUsersetExprContext tupleToUsersetExpr() {
			return getRuleContext(TupleToUsersetExprContext.class,0);
		}
		public FromExprContext fromExpr() {
			return getRuleContext(FromExprContext.class,0);
		}
		public ListExprContext listExpr() {
			return getRuleContext(ListExprContext.class,0);
		}
		public RelationExprContext relationExpr() {
			return getRuleContext(RelationExprContext.class,0);
		}
		public TerminalNode SELF() { return getToken(FGARewriterParser.SELF, 0); }
		public TerminalNode LPAREN() { return getToken(FGARewriterParser.LPAREN, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode RPAREN() { return getToken(FGARewriterParser.RPAREN, 0); }
		public PrimaryExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primaryExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterPrimaryExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitPrimaryExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitPrimaryExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PrimaryExprContext primaryExpr() throws RecognitionException {
		PrimaryExprContext _localctx = new PrimaryExprContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_primaryExpr);
		try {
			setState(56);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(47);
				tupleToUsersetExpr();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(48);
				fromExpr();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(49);
				listExpr();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(50);
				relationExpr();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(51);
				match(SELF);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(52);
				match(LPAREN);
				setState(53);
				expression();
				setState(54);
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
	public static class TupleToUsersetExprContext extends ParserRuleContext {
		public TerminalNode TUPLE_TO_USERSET() { return getToken(FGARewriterParser.TUPLE_TO_USERSET, 0); }
		public TerminalNode COLON() { return getToken(FGARewriterParser.COLON, 0); }
		public KvPairsContext kvPairs() {
			return getRuleContext(KvPairsContext.class,0);
		}
		public TupleToUsersetExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_tupleToUsersetExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterTupleToUsersetExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitTupleToUsersetExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitTupleToUsersetExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TupleToUsersetExprContext tupleToUsersetExpr() throws RecognitionException {
		TupleToUsersetExprContext _localctx = new TupleToUsersetExprContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_tupleToUsersetExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(58);
			match(TUPLE_TO_USERSET);
			setState(59);
			match(COLON);
			setState(60);
			kvPairs();
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
	public static class KvPairsContext extends ParserRuleContext {
		public List<KvPairContext> kvPair() {
			return getRuleContexts(KvPairContext.class);
		}
		public KvPairContext kvPair(int i) {
			return getRuleContext(KvPairContext.class,i);
		}
		public List<TerminalNode> SEMI() { return getTokens(FGARewriterParser.SEMI); }
		public TerminalNode SEMI(int i) {
			return getToken(FGARewriterParser.SEMI, i);
		}
		public KvPairsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kvPairs; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterKvPairs(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitKvPairs(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitKvPairs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KvPairsContext kvPairs() throws RecognitionException {
		KvPairsContext _localctx = new KvPairsContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_kvPairs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62);
			kvPair();
			setState(67);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==SEMI) {
				{
				{
				setState(63);
				match(SEMI);
				setState(64);
				kvPair();
				}
				}
				setState(69);
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
	public static class KvPairContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(FGARewriterParser.ID, 0); }
		public TerminalNode EQ() { return getToken(FGARewriterParser.EQ, 0); }
		public ValueContext value() {
			return getRuleContext(ValueContext.class,0);
		}
		public KvPairContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_kvPair; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterKvPair(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitKvPair(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitKvPair(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KvPairContext kvPair() throws RecognitionException {
		KvPairContext _localctx = new KvPairContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_kvPair);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70);
			match(ID);
			setState(71);
			match(EQ);
			setState(72);
			value();
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
	public static class FromExprContext extends ParserRuleContext {
		public RelationExprContext relationExpr() {
			return getRuleContext(RelationExprContext.class,0);
		}
		public TerminalNode FROM() { return getToken(FGARewriterParser.FROM, 0); }
		public TerminalNode ID() { return getToken(FGARewriterParser.ID, 0); }
		public FromExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fromExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterFromExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitFromExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitFromExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FromExprContext fromExpr() throws RecognitionException {
		FromExprContext _localctx = new FromExprContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_fromExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(74);
			relationExpr();
			setState(75);
			match(FROM);
			setState(76);
			match(ID);
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
	public static class ListExprContext extends ParserRuleContext {
		public TerminalNode LBRACK() { return getToken(FGARewriterParser.LBRACK, 0); }
		public ListItemsContext listItems() {
			return getRuleContext(ListItemsContext.class,0);
		}
		public TerminalNode RBRACK() { return getToken(FGARewriterParser.RBRACK, 0); }
		public ListExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterListExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitListExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitListExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListExprContext listExpr() throws RecognitionException {
		ListExprContext _localctx = new ListExprContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_listExpr);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			match(LBRACK);
			setState(79);
			listItems();
			setState(80);
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

	@SuppressWarnings("CheckReturnValue")
	public static class ListItemsContext extends ParserRuleContext {
		public List<RelationExprContext> relationExpr() {
			return getRuleContexts(RelationExprContext.class);
		}
		public RelationExprContext relationExpr(int i) {
			return getRuleContext(RelationExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(FGARewriterParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(FGARewriterParser.COMMA, i);
		}
		public ListItemsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listItems; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterListItems(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitListItems(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitListItems(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListItemsContext listItems() throws RecognitionException {
		ListItemsContext _localctx = new ListItemsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_listItems);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(82);
			relationExpr();
			setState(87);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(83);
				match(COMMA);
				setState(84);
				relationExpr();
				}
				}
				setState(89);
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
	public static class RelationExprContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(FGARewriterParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(FGARewriterParser.ID, i);
		}
		public TerminalNode HASH() { return getToken(FGARewriterParser.HASH, 0); }
		public RelationExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_relationExpr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterRelationExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitRelationExpr(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitRelationExpr(this);
			else return visitor.visitChildren(this);
		}
	}

	public final RelationExprContext relationExpr() throws RecognitionException {
		RelationExprContext _localctx = new RelationExprContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_relationExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(90);
			match(ID);
			setState(93);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HASH) {
				{
				setState(91);
				match(HASH);
				setState(92);
				match(ID);
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
	public static class ValueContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(FGARewriterParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(FGARewriterParser.ID, i);
		}
		public TerminalNode HASH() { return getToken(FGARewriterParser.HASH, 0); }
		public ValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_value; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).enterValue(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof FGARewriterListener ) ((FGARewriterListener)listener).exitValue(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof FGARewriterVisitor ) return ((FGARewriterVisitor<? extends T>)visitor).visitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ValueContext value() throws RecognitionException {
		ValueContext _localctx = new ValueContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_value);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(95);
			match(ID);
			setState(98);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==HASH) {
				{
				setState(96);
				match(HASH);
				setState(97);
				match(ID);
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

	public static final String _serializedATN =
		"\u0004\u0001\u0010e\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002#\b\u0002\n\u0002\f\u0002"+
		"&\t\u0002\u0001\u0003\u0001\u0003\u0001\u0003\u0005\u0003+\b\u0003\n\u0003"+
		"\f\u0003.\t\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001"+
		"\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u00049\b"+
		"\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001"+
		"\u0006\u0001\u0006\u0005\u0006B\b\u0006\n\u0006\f\u0006E\t\u0006\u0001"+
		"\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001\n\u0001\n\u0005\nV\b"+
		"\n\n\n\f\nY\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000b^\b\u000b"+
		"\u0001\f\u0001\f\u0001\f\u0003\fc\b\f\u0001\f\u0000\u0000\r\u0000\u0002"+
		"\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u0000\u0000b\u0000"+
		"\u001a\u0001\u0000\u0000\u0000\u0002\u001d\u0001\u0000\u0000\u0000\u0004"+
		"\u001f\u0001\u0000\u0000\u0000\u0006\'\u0001\u0000\u0000\u0000\b8\u0001"+
		"\u0000\u0000\u0000\n:\u0001\u0000\u0000\u0000\f>\u0001\u0000\u0000\u0000"+
		"\u000eF\u0001\u0000\u0000\u0000\u0010J\u0001\u0000\u0000\u0000\u0012N"+
		"\u0001\u0000\u0000\u0000\u0014R\u0001\u0000\u0000\u0000\u0016Z\u0001\u0000"+
		"\u0000\u0000\u0018_\u0001\u0000\u0000\u0000\u001a\u001b\u0003\u0002\u0001"+
		"\u0000\u001b\u001c\u0005\u0000\u0000\u0001\u001c\u0001\u0001\u0000\u0000"+
		"\u0000\u001d\u001e\u0003\u0004\u0002\u0000\u001e\u0003\u0001\u0000\u0000"+
		"\u0000\u001f$\u0003\u0006\u0003\u0000 !\u0005\u0001\u0000\u0000!#\u0003"+
		"\u0006\u0003\u0000\" \u0001\u0000\u0000\u0000#&\u0001\u0000\u0000\u0000"+
		"$\"\u0001\u0000\u0000\u0000$%\u0001\u0000\u0000\u0000%\u0005\u0001\u0000"+
		"\u0000\u0000&$\u0001\u0000\u0000\u0000\',\u0003\b\u0004\u0000()\u0005"+
		"\u0002\u0000\u0000)+\u0003\b\u0004\u0000*(\u0001\u0000\u0000\u0000+.\u0001"+
		"\u0000\u0000\u0000,*\u0001\u0000\u0000\u0000,-\u0001\u0000\u0000\u0000"+
		"-\u0007\u0001\u0000\u0000\u0000.,\u0001\u0000\u0000\u0000/9\u0003\n\u0005"+
		"\u000009\u0003\u0010\b\u000019\u0003\u0012\t\u000029\u0003\u0016\u000b"+
		"\u000039\u0005\u0004\u0000\u000045\u0005\u000b\u0000\u000056\u0003\u0002"+
		"\u0001\u000067\u0005\f\u0000\u000079\u0001\u0000\u0000\u00008/\u0001\u0000"+
		"\u0000\u000080\u0001\u0000\u0000\u000081\u0001\u0000\u0000\u000082\u0001"+
		"\u0000\u0000\u000083\u0001\u0000\u0000\u000084\u0001\u0000\u0000\u0000"+
		"9\t\u0001\u0000\u0000\u0000:;\u0005\u0005\u0000\u0000;<\u0005\u0007\u0000"+
		"\u0000<=\u0003\f\u0006\u0000=\u000b\u0001\u0000\u0000\u0000>C\u0003\u000e"+
		"\u0007\u0000?@\u0005\b\u0000\u0000@B\u0003\u000e\u0007\u0000A?\u0001\u0000"+
		"\u0000\u0000BE\u0001\u0000\u0000\u0000CA\u0001\u0000\u0000\u0000CD\u0001"+
		"\u0000\u0000\u0000D\r\u0001\u0000\u0000\u0000EC\u0001\u0000\u0000\u0000"+
		"FG\u0005\u000f\u0000\u0000GH\u0005\u0006\u0000\u0000HI\u0003\u0018\f\u0000"+
		"I\u000f\u0001\u0000\u0000\u0000JK\u0003\u0016\u000b\u0000KL\u0005\u0003"+
		"\u0000\u0000LM\u0005\u000f\u0000\u0000M\u0011\u0001\u0000\u0000\u0000"+
		"NO\u0005\r\u0000\u0000OP\u0003\u0014\n\u0000PQ\u0005\u000e\u0000\u0000"+
		"Q\u0013\u0001\u0000\u0000\u0000RW\u0003\u0016\u000b\u0000ST\u0005\t\u0000"+
		"\u0000TV\u0003\u0016\u000b\u0000US\u0001\u0000\u0000\u0000VY\u0001\u0000"+
		"\u0000\u0000WU\u0001\u0000\u0000\u0000WX\u0001\u0000\u0000\u0000X\u0015"+
		"\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000Z]\u0005\u000f\u0000"+
		"\u0000[\\\u0005\n\u0000\u0000\\^\u0005\u000f\u0000\u0000][\u0001\u0000"+
		"\u0000\u0000]^\u0001\u0000\u0000\u0000^\u0017\u0001\u0000\u0000\u0000"+
		"_b\u0005\u000f\u0000\u0000`a\u0005\n\u0000\u0000ac\u0005\u000f\u0000\u0000"+
		"b`\u0001\u0000\u0000\u0000bc\u0001\u0000\u0000\u0000c\u0019\u0001\u0000"+
		"\u0000\u0000\u0007$,8CW]b";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}