package org.kitona.zus.api.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Watch 变更事件 VO
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WatchChangeEventVO {

    /**
     * 变更版本标识
     */
    private String zookie;

    /**
     * 操作类型: WRITE / DELETE
     */
    private String operation;

    /**
     * 元组数据
     */
    private WatchTupleDataVO tuple;
}
