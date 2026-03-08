package org.kitona.zus.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储空间状态枚举
 *
 * @author kitona
 */
@AllArgsConstructor
@Getter
public enum StoreStatus {

    NORMAL(0, "正常"),

    DISABLE(1, "禁用");

    private final int code;

    private final String desc;

    /**
     * 根据状态码获取枚举值
     *
     * @param code 状态码
     * @return 对应的枚举值，未找到返回 null
     */
    public static StoreStatus fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StoreStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return null;
    }
}
