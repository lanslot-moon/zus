package org.kitona.zus.domain.enums;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 授权模型发布状态（领域枚举）
 *
 * @author kitona
 */
@AllArgsConstructor
@Getter
public enum ModelPublishStatus {

    DRAFT(0, "草稿"),
    PUBLISHED(1, "已发布"),
    ABANDONED(2, "已废弃");

    private final Integer status;
    private final String desc;

    public static ModelPublishStatus fromStatus(Integer status) {
        for (ModelPublishStatus value : ModelPublishStatus.values()) {
            if (Objects.equals(value.getStatus(), status)) {
                return value;
            }
        }
        return null;
    }
}
