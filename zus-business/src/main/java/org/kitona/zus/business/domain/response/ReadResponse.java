package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Read API响应对象
 */
@Data
@Builder
public class ReadResponse {
    private List<String[]> tuples;     // 关系元组 [object, relation, user]
    private Long requestId;
    private Long version;              // 当前版本号
    private Long timestamp;            // 时间戳
}