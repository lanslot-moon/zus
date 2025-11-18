package org.kitona.zus.api.entity.request;

import lombok.Builder;
import lombok.Data;

/**
 * Write API请求对象
 */
@Data
@Builder
public class WriteApiRequest {
    private String namespace;
    private String[][] writes;     // 要写入的关系元组 [object, relation, user]
    private String[][] deletes;    // 要删除的关系元组
    private String[][] conditions; // 条件写入
    private Long requestId;
    private String traceId;
    
    public int getWritesTupleCount() {
        return writes != null ? writes.length : 0;
    }
    
    public int getDeletesTupleCount() {
        return deletes != null ? deletes.length : 0;
    }
}