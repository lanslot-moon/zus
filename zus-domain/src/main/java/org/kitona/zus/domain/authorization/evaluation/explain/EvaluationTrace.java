package org.kitona.zus.domain.authorization.evaluation.explain;

/**
 * 一次授权 explain 的完整结果。
 *
 * @param allowed                 最终是否允许
 * @param requestedZookie         请求侧一致性令牌
 * @param currentZookie           当前 store 最新 zookie
 * @param staleSnapshotDiagnosis  旧快照诊断结果
 * @param root                    解释树根节点
 * @param truncated               解释树是否被截断
 */
public record EvaluationTrace(boolean allowed,
                              String requestedZookie,
                              String currentZookie,
                              StaleSnapshotDiagnosis staleSnapshotDiagnosis,
                              EvaluationExplainNode root,
                              boolean truncated) {

    /**
     * 补充当前最新 zookie。
     */
    public EvaluationTrace withCurrentZookie(String nextCurrentZookie) {
        return new EvaluationTrace(allowed, requestedZookie, nextCurrentZookie, staleSnapshotDiagnosis, root, truncated);
    }

    /**
     * 补充旧快照诊断结果。
     */
    public EvaluationTrace withStaleSnapshotDiagnosis(StaleSnapshotDiagnosis diagnosis) {
        return new EvaluationTrace(allowed, requestedZookie, currentZookie, diagnosis, root, truncated);
    }
}
