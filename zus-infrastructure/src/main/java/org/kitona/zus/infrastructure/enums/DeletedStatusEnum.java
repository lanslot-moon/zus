package org.kitona.zus.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeletedStatusEnum {

    DELETED(true, "已删除"),

    NOT_DELETED(false, "未删除");

    private final Boolean code;

    private final String desc;
    }
