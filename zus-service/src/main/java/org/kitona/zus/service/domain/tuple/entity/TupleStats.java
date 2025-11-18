package org.kitona.zus.service.domain.tuple.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 元组统计信息
 */
@Data
@Builder
public class TupleStats {
    private String namespace;              // 命名空间
    private long totalTuples;              // 总元组数
    private long activeTuples;             // 激活元组数
    private long inactiveTuples;           // 非激活元组数
    private long objectTypes;              // 对象类型数
    private long relationTypes;            // 关系类型数
    private Map<String, Long> relationCounts;  // 各关系计数
    private Map<String, Long> objectCounts;    // 各对象计数
    private Map<String, Long> userCounts;      // 各用户计数
    private long storageSize;              // 存储大小（字节）
    private Long lastModified;    // 最后修改时间
}