package org.kitona.zus.domain.authorization.evaluation.runtime;

import org.kitona.zus.domain.authorization.tuple.RelationTuple;

/**
 * tuple 条件匹配上下文。
 *
 * @param request 当前请求
 * @param tuple   命中的 tuple
 */
public record TupleMatchContext(EvaluationRequest request, RelationTuple tuple) {
}
