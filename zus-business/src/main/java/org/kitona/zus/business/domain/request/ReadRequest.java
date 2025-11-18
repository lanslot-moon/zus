package org.kitona.zus.business.domain.request;

import lombok.Builder;
import lombok.Data;

/**
 * Read API请求对象
 * 用于封装读取操作的请求参数
 */
@Data
@Builder
public class ReadRequest {
    private String namespace;  // 命名空间，用于标识不同的数据域
    private String object;    // 读取该对象的权限信息，指定需要读取的具体对象
    private String relation;  // 特定关系，可为空
    private Long requestId;
    private Long version;     // 版本号，用于一致性读取
}
