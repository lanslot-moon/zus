// Generated from OpenFGAModel.g4 by ANTLR 4.13.2
package org.kitona.zus.infrastructure.engine.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class OpenFGAModelLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		MODEL=1, TYPE=2, RELATIONS=3, DEFINE=4, AS=5, UNION=6, INTERSECTION=7, 
		EXCLUSION=8, SELF=9, FROM=10, HASH=11, LPAREN=12, RPAREN=13, COLON=14, 
		COMMA=15, LBRACK=16, RBRACK=17, NEWLINE=18, IDENTIFIER=19, WS=20, COMMENT=21, 
		UNRECOGNIZED_CHAR=22;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	private static String[] makeRuleNames() {
		return new String[] {
			"MODEL", "TYPE", "RELATIONS", "DEFINE", "AS", "UNION", "INTERSECTION", 
			"EXCLUSION", "SELF", "FROM", "HASH", "LPAREN", "RPAREN", "COLON", "COMMA", 
			"LBRACK", "RBRACK", "NEWLINE", "IDENTIFIER", "LETTER", "DIGIT", "WS", 
			"COMMENT", "UNRECOGNIZED_CHAR"
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


	public OpenFGAModelLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "OpenFGAModel.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\u0004\u0000\u0016\u009d\u0006\uffff\uffff\u0002\u0000\u0007\u0000\u0002"+
		"\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002"+
		"\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002"+
		"\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002"+
		"\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e"+
		"\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011"+
		"\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014"+
		"\u0002\u0015\u0007\u0015\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017"+
		"\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0000"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003\u0001\u0003\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006"+
		"\u0001\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007"+
		"\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\n\u0001"+
		"\n\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\r\u0001\r\u0001\u000e"+
		"\u0001\u000e\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0011"+
		"\u0003\u0011y\b\u0011\u0001\u0011\u0001\u0011\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0012\u0005\u0012\u0081\b\u0012\n\u0012\f\u0012\u0084"+
		"\t\u0012\u0001\u0013\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0015\u0004"+
		"\u0015\u008b\b\u0015\u000b\u0015\f\u0015\u008c\u0001\u0015\u0001\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0016\u0005\u0016\u0095\b\u0016"+
		"\n\u0016\f\u0016\u0098\t\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001"+
		"\u0017\u0000\u0000\u0018\u0001\u0001\u0003\u0002\u0005\u0003\u0007\u0004"+
		"\t\u0005\u000b\u0006\r\u0007\u000f\b\u0011\t\u0013\n\u0015\u000b\u0017"+
		"\f\u0019\r\u001b\u000e\u001d\u000f\u001f\u0010!\u0011#\u0012%\u0013\'"+
		"\u0000)\u0000+\u0014-\u0015/\u0016\u0001\u0000\u0004\u0002\u0000AZaz\u0001"+
		"\u000009\u0002\u0000\t\t  \u0002\u0000\n\n\r\r\u00a0\u0000\u0001\u0001"+
		"\u0000\u0000\u0000\u0000\u0003\u0001\u0000\u0000\u0000\u0000\u0005\u0001"+
		"\u0000\u0000\u0000\u0000\u0007\u0001\u0000\u0000\u0000\u0000\t\u0001\u0000"+
		"\u0000\u0000\u0000\u000b\u0001\u0000\u0000\u0000\u0000\r\u0001\u0000\u0000"+
		"\u0000\u0000\u000f\u0001\u0000\u0000\u0000\u0000\u0011\u0001\u0000\u0000"+
		"\u0000\u0000\u0013\u0001\u0000\u0000\u0000\u0000\u0015\u0001\u0000\u0000"+
		"\u0000\u0000\u0017\u0001\u0000\u0000\u0000\u0000\u0019\u0001\u0000\u0000"+
		"\u0000\u0000\u001b\u0001\u0000\u0000\u0000\u0000\u001d\u0001\u0000\u0000"+
		"\u0000\u0000\u001f\u0001\u0000\u0000\u0000\u0000!\u0001\u0000\u0000\u0000"+
		"\u0000#\u0001\u0000\u0000\u0000\u0000%\u0001\u0000\u0000\u0000\u0000+"+
		"\u0001\u0000\u0000\u0000\u0000-\u0001\u0000\u0000\u0000\u0000/\u0001\u0000"+
		"\u0000\u0000\u00011\u0001\u0000\u0000\u0000\u00037\u0001\u0000\u0000\u0000"+
		"\u0005<\u0001\u0000\u0000\u0000\u0007F\u0001\u0000\u0000\u0000\tM\u0001"+
		"\u0000\u0000\u0000\u000bP\u0001\u0000\u0000\u0000\rS\u0001\u0000\u0000"+
		"\u0000\u000fW\u0001\u0000\u0000\u0000\u0011_\u0001\u0000\u0000\u0000\u0013"+
		"d\u0001\u0000\u0000\u0000\u0015i\u0001\u0000\u0000\u0000\u0017k\u0001"+
		"\u0000\u0000\u0000\u0019m\u0001\u0000\u0000\u0000\u001bo\u0001\u0000\u0000"+
		"\u0000\u001dq\u0001\u0000\u0000\u0000\u001fs\u0001\u0000\u0000\u0000!"+
		"u\u0001\u0000\u0000\u0000#x\u0001\u0000\u0000\u0000%|\u0001\u0000\u0000"+
		"\u0000\'\u0085\u0001\u0000\u0000\u0000)\u0087\u0001\u0000\u0000\u0000"+
		"+\u008a\u0001\u0000\u0000\u0000-\u0090\u0001\u0000\u0000\u0000/\u009b"+
		"\u0001\u0000\u0000\u000012\u0005m\u0000\u000023\u0005o\u0000\u000034\u0005"+
		"d\u0000\u000045\u0005e\u0000\u000056\u0005l\u0000\u00006\u0002\u0001\u0000"+
		"\u0000\u000078\u0005t\u0000\u000089\u0005y\u0000\u00009:\u0005p\u0000"+
		"\u0000:;\u0005e\u0000\u0000;\u0004\u0001\u0000\u0000\u0000<=\u0005r\u0000"+
		"\u0000=>\u0005e\u0000\u0000>?\u0005l\u0000\u0000?@\u0005a\u0000\u0000"+
		"@A\u0005t\u0000\u0000AB\u0005i\u0000\u0000BC\u0005o\u0000\u0000CD\u0005"+
		"n\u0000\u0000DE\u0005s\u0000\u0000E\u0006\u0001\u0000\u0000\u0000FG\u0005"+
		"d\u0000\u0000GH\u0005e\u0000\u0000HI\u0005f\u0000\u0000IJ\u0005i\u0000"+
		"\u0000JK\u0005n\u0000\u0000KL\u0005e\u0000\u0000L\b\u0001\u0000\u0000"+
		"\u0000MN\u0005a\u0000\u0000NO\u0005s\u0000\u0000O\n\u0001\u0000\u0000"+
		"\u0000PQ\u0005o\u0000\u0000QR\u0005r\u0000\u0000R\f\u0001\u0000\u0000"+
		"\u0000ST\u0005a\u0000\u0000TU\u0005n\u0000\u0000UV\u0005d\u0000\u0000"+
		"V\u000e\u0001\u0000\u0000\u0000WX\u0005b\u0000\u0000XY\u0005u\u0000\u0000"+
		"YZ\u0005t\u0000\u0000Z[\u0005 \u0000\u0000[\\\u0005n\u0000\u0000\\]\u0005"+
		"o\u0000\u0000]^\u0005t\u0000\u0000^\u0010\u0001\u0000\u0000\u0000_`\u0005"+
		"s\u0000\u0000`a\u0005e\u0000\u0000ab\u0005l\u0000\u0000bc\u0005f\u0000"+
		"\u0000c\u0012\u0001\u0000\u0000\u0000de\u0005f\u0000\u0000ef\u0005r\u0000"+
		"\u0000fg\u0005o\u0000\u0000gh\u0005m\u0000\u0000h\u0014\u0001\u0000\u0000"+
		"\u0000ij\u0005#\u0000\u0000j\u0016\u0001\u0000\u0000\u0000kl\u0005(\u0000"+
		"\u0000l\u0018\u0001\u0000\u0000\u0000mn\u0005)\u0000\u0000n\u001a\u0001"+
		"\u0000\u0000\u0000op\u0005:\u0000\u0000p\u001c\u0001\u0000\u0000\u0000"+
		"qr\u0005,\u0000\u0000r\u001e\u0001\u0000\u0000\u0000st\u0005[\u0000\u0000"+
		"t \u0001\u0000\u0000\u0000uv\u0005]\u0000\u0000v\"\u0001\u0000\u0000\u0000"+
		"wy\u0005\r\u0000\u0000xw\u0001\u0000\u0000\u0000xy\u0001\u0000\u0000\u0000"+
		"yz\u0001\u0000\u0000\u0000z{\u0005\n\u0000\u0000{$\u0001\u0000\u0000\u0000"+
		"|\u0082\u0003\'\u0013\u0000}\u0081\u0003\'\u0013\u0000~\u0081\u0003)\u0014"+
		"\u0000\u007f\u0081\u0005_\u0000\u0000\u0080}\u0001\u0000\u0000\u0000\u0080"+
		"~\u0001\u0000\u0000\u0000\u0080\u007f\u0001\u0000\u0000\u0000\u0081\u0084"+
		"\u0001\u0000\u0000\u0000\u0082\u0080\u0001\u0000\u0000\u0000\u0082\u0083"+
		"\u0001\u0000\u0000\u0000\u0083&\u0001\u0000\u0000\u0000\u0084\u0082\u0001"+
		"\u0000\u0000\u0000\u0085\u0086\u0007\u0000\u0000\u0000\u0086(\u0001\u0000"+
		"\u0000\u0000\u0087\u0088\u0007\u0001\u0000\u0000\u0088*\u0001\u0000\u0000"+
		"\u0000\u0089\u008b\u0007\u0002\u0000\u0000\u008a\u0089\u0001\u0000\u0000"+
		"\u0000\u008b\u008c\u0001\u0000\u0000\u0000\u008c\u008a\u0001\u0000\u0000"+
		"\u0000\u008c\u008d\u0001\u0000\u0000\u0000\u008d\u008e\u0001\u0000\u0000"+
		"\u0000\u008e\u008f\u0006\u0015\u0000\u0000\u008f,\u0001\u0000\u0000\u0000"+
		"\u0090\u0091\u0005/\u0000\u0000\u0091\u0092\u0005/\u0000\u0000\u0092\u0096"+
		"\u0001\u0000\u0000\u0000\u0093\u0095\b\u0003\u0000\u0000\u0094\u0093\u0001"+
		"\u0000\u0000\u0000\u0095\u0098\u0001\u0000\u0000\u0000\u0096\u0094\u0001"+
		"\u0000\u0000\u0000\u0096\u0097\u0001\u0000\u0000\u0000\u0097\u0099\u0001"+
		"\u0000\u0000\u0000\u0098\u0096\u0001\u0000\u0000\u0000\u0099\u009a\u0006"+
		"\u0016\u0000\u0000\u009a.\u0001\u0000\u0000\u0000\u009b\u009c\t\u0000"+
		"\u0000\u0000\u009c0\u0001\u0000\u0000\u0000\u0006\u0000x\u0080\u0082\u008c"+
		"\u0096\u0001\u0006\u0000\u0000";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}