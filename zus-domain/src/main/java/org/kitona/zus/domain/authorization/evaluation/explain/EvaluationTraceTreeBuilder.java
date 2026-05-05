package org.kitona.zus.domain.authorization.evaluation.explain;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * explain 树构建器。
 *
 * <p>只负责维护当前递归栈、父子节点关系以及最终不可变树输出。
 */
final class EvaluationTraceTreeBuilder {

    /**
     * explain 节点工厂，用于创建兜底根节点。
     */
    private final EvaluationExplainNodeFactory nodeFactory;

    /**
     * 当前递归求值路径上的节点栈，栈顶表示当前正在处理的父节点。
     */
    private final Deque<TraceNodeBuilder> nodeStack = new ArrayDeque<>();

    /**
     * explain 树根节点构建器。
     */
    private TraceNodeBuilder rootNode;

    EvaluationTraceTreeBuilder(EvaluationExplainNodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    /**
     * 追加一个可进入的分支节点，并把它压入当前路径。
     *
     * @param node 分支节点构建器
     */
    void enter(TraceNodeBuilder node) {
        append(node);
        nodeStack.push(node);
    }

    /**
     * 追加一个叶子节点。
     *
     * @param node 叶子节点构建器
     */
    void appendLeaf(TraceNodeBuilder node) {
        if (!nodeStack.isEmpty()) {
            append(node);
        }
    }

    /**
     * 标记当前路径顶部节点。
     *
     * @param allowed 当前节点是否成立
     * @param reason  结构化原因
     */
    void markCurrent(boolean allowed, EvaluationExplainReason reason) {
        if (!nodeStack.isEmpty()) {
            nodeStack.peek().mark(allowed, reason);
        }
    }

    /**
     * 弹出当前路径顶部节点并写入最终结果。
     *
     * @param allowed 当前节点最终是否成立
     * @param reason  默认结构化原因
     */
    void leave(boolean allowed, EvaluationExplainReason reason) {
        if (!nodeStack.isEmpty()) {
            nodeStack.pop().complete(allowed, reason);
        }
    }

    /**
     * 判断当前是否存在可追加子节点的父节点。
     *
     * @return 存在父节点返回 true
     */
    boolean hasOpenParent() {
        return !nodeStack.isEmpty();
    }

    /**
     * 输出不可变 explain 根节点。
     *
     * @param allowed 最终授权结果
     * @return 不可变根节点
     */
    EvaluationExplainNode buildRoot(boolean allowed) {
        return rootNode == null ? nodeFactory.fallbackRoot(allowed) : rootNode.toImmutable();
    }

    private void append(TraceNodeBuilder node) {
        if (nodeStack.isEmpty()) {
            rootNode = node;
            return;
        }
        nodeStack.peek().addChild(node);
    }
}
