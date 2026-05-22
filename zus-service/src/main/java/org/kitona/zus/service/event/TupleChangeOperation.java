package org.kitona.zus.service.event;

/**
 * Watch 元组变更操作类型。
 *
 * <p>该枚举用于应用层事件推送和 DTO 装配，避免在监听器中散落 WRITE / DELETE
 * 字符串常量。领域层 changelog 仍然维护自身操作语义，应用层只在对外发布 Watch
 * 事件时使用该枚举表达输出操作类型。
 */
public enum TupleChangeOperation {

    /**
     * 元组写入操作。
     */
    WRITE("WRITE"),

    /**
     * 元组删除操作。
     */
    DELETE("DELETE");

    /**
     * 对外输出的操作类型编码。
     */
    private final String code;

    TupleChangeOperation(String code) {
        this.code = code;
    }

    /**
     * 返回对外输出的操作类型编码。
     *
     * @return 操作类型编码
     */
    public String getCode() {
        return code;
    }
}
