package org.kitona.zus.domain.authorization.evaluation.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 递归保护器。
 *
 * <p>负责维护单次请求内的访问路径和 memo 缓存，
 * 用于防止循环依赖导致无限递归，并避免重复计算同一个求值目标。
 */
public final class RecursionGuard {

    private final int maxDepth;
    private final Set<EvaluationMemoKey> visiting;
    private final Map<EvaluationMemoKey, Boolean> memo;

    /**
     * 创建一个新的递归保护器。
     */
    public RecursionGuard(int maxDepth) {
        this.maxDepth = maxDepth;
        this.visiting = new HashSet<>();
        this.memo = new HashMap<>();
    }

    /**
     * 返回允许的最大递归深度。
     */
    public int maxDepth() {
        return maxDepth;
    }

    /**
     * 判断某个求值目标是否已经在当前递归路径上。
     */
    public boolean isVisiting(EvaluationMemoKey key) {
        return visiting.contains(key);
    }

    /**
     * 标记进入某个求值目标。
     */
    public void enter(EvaluationMemoKey key) {
        visiting.add(key);
    }

    /**
     * 标记退出某个求值目标。
     */
    public void exit(EvaluationMemoKey key) {
        visiting.remove(key);
    }

    /**
     * 读取 memo 缓存中的既有结果。
     */
    public Boolean getMemo(EvaluationMemoKey key) {
        return memo.get(key);
    }

    /**
     * 缓存某个求值目标的最终结果。
     */
    public void putMemo(EvaluationMemoKey key, boolean value) {
        memo.put(key, value);
    }
}
