package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Watch 元组数据 VO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchTupleDataVO {

    /**
     * 对象引用，格式：type:id
     */
    private String object;

    /**
     * 关系名
     */
    private String relation;

    /**
     * 主体引用，格式：type:id 或 type:id#relation
     */
    private String subject;
}
