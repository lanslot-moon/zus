package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA ListObjects API 响应
 */
@Data
public class FgaListObjectsResponseVO {

    /**
     * 对象标识列表，格式为 type:id
     */
    private List<String> objects;
}
