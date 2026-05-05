package org.kitona.zus.domain.authorization.evaluation.explain;

/**
 * 一致性旧快照诊断结果。
 */
public enum StaleSnapshotDiagnosis {
    /**
     * 未执行旧快照诊断。
     */
    NOT_REQUESTED,

    /**
     * 当前结果不适用旧快照诊断，例如已经允许或没有请求 zookie。
     */
    NOT_APPLICABLE,

    /**
     * 使用请求 zookie 被拒绝，但使用当前最新 zookie 重新检查后允许。
     */
    STALE_SNAPSHOT_CAUSED,

    /**
     * 请求 zookie 较旧，但使用当前最新 zookie 重新检查后仍然拒绝。
     */
    STALE_SNAPSHOT_NOT_CAUSED
}
