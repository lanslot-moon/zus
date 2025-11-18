package org.kitona.zus.service.domain.tuple.entity;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 批量操作结果
 */
@Data
@Builder
public class BatchOperationResult {
    private int totalTuples;               // 总元组数
    private int successfulOperations;      // 成功操作数
    private int failedOperations;          // 失败操作数
    private List<String> errors;           // 错误列表
    private List<String> operationIds;     // 操作ID列表
    private long executionTime;            // 执行时间（毫秒）
    private List<RelationshipTuple> affectedTuples; // 受影响的元组
}
    