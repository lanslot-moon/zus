package org.kitona.zus.infrastructure.engine.compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.kitona.zus.domain.authorization.evaluation.nodes.RewriteNode;
import org.kitona.zus.infrastructure.engine.parser.OpenFGAModelLexer;
import org.kitona.zus.infrastructure.engine.parser.OpenFGAModelParser;

import java.util.Objects;

/**
 * Rewrite 表达式解析适配器。
 *
 * <p>集中处理 ANTLR 细节、语法失败语义与 AST visitor 装配。
 */
final class RewriteExpressionParserAdapter {

    /**
     * 将 rewrite 表达式解析为领域 AST 节点。
     *
     * @param resourceType 当前关系所属资源类型
     * @param expression   rewrite 表达式
     * @return rewrite AST 根节点
     */
    RewriteNode parse(String resourceType, String expression) {
        Objects.requireNonNull(resourceType, "resourceType must not be null");
        Objects.requireNonNull(expression, "expression must not be null");

        try {
            OpenFGAModelLexer lexer = new OpenFGAModelLexer(CharStreams.fromString(expression));
            lexer.removeErrorListeners();
            lexer.addErrorListener(FailFastErrorListener.INSTANCE);

            OpenFGAModelParser parser = new OpenFGAModelParser(new CommonTokenStream(lexer));
            parser.removeErrorListeners();
            parser.addErrorListener(FailFastErrorListener.INSTANCE);
            parser.setErrorHandler(new BailErrorStrategy());

            return new RewriteNodeAstVisitor(resourceType).visit(parser.rewrite());
        } catch (IllegalStateException ex) {
            throw new IllegalArgumentException("Invalid rewrite expression for resource type '" + resourceType + "': " + expression, ex);
        }
    }

    /**
     * 语法错误即失败，避免默认恢复导致"看似成功但语义错误"的编译结果。
     */
    private static final class FailFastErrorListener extends BaseErrorListener {

        /**
         * 单例实例，用于全局共享同一个错误监听器。
         */
        private static final FailFastErrorListener INSTANCE = new FailFastErrorListener();

        /**
         * 当语法错误发生时被调用。
         *
         * @param recognizer         解析器实例，提供对解析状态的访问
         * @param offendingSymbol    导致错误的符号
         * @param line               错误发生的行号
         * @param charPositionInLine 错误发生的列位置
         * @param msg                错误信息
         * @param e                  识别异常实例，可能为null
         * @throws ParseCancellationException 总是抛出此异常以终止解析过程
         */
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
                                String msg, RecognitionException e) {
            throw new ParseCancellationException("line " + line + ":" + charPositionInLine + " " + msg, e);
        }
    }
}
