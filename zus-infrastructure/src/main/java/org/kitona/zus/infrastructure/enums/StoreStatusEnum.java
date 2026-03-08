package org.kitona.zus.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StoreStatusEnum {
    /**
     * 存储空间状态
     * 0: 正常, 1: 禁用
     */

    NORMAL(0, "正常"),

    DISABLE(1, "禁用");

    private final Integer code;
    private final String name;
}
