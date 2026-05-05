package org.kitona.zus.domain.authorization.evaluation.explain;

/**
 * 带解释信息的授权判定结果。
 *
 * @param allowed 是否允许
 * @param trace   本次求值产生的解释树
 */
public record EvaluationDecision(boolean allowed, EvaluationTrace trace) {
}
