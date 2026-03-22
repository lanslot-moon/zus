package org.kitona.zus.domain.valueobject;

import java.util.Objects;

/**
 * tuple 变更请求
 *
 * <p>用于在应用层和领域层之间传递经过校验的 tuple 变更意图，
 * 将写入所需的关键业务信息收敛为领域可理解的数据结构。
 */
public record TupleMutationRequest(TupleKey tupleKey, TupleCondition condition) {

    public TupleMutationRequest(TupleKey tupleKey, TupleCondition condition) {
        this.tupleKey = Objects.requireNonNull(tupleKey, "tupleKey 不能为空");
        this.condition = condition != null ? condition : TupleCondition.EMPTY;
    }

    public static TupleMutationRequest of(TupleKey tupleKey, TupleCondition condition) {
        return new TupleMutationRequest(tupleKey, condition);
    }
}
