package org.kitona.zus.domain.valueobject;

import lombok.Getter;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;

import java.util.Objects;

/**
 * 资源对象引用值对象
 *
 * 表示权限检查中的资源对象，格式为 "type:id"。
 * 这是一个不可变的值对象，用于标识被授权的资源。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public final class ObjectRef {

    public static final String SEPARATOR = ":";

    private final String type;

    private final String id;

    private ObjectRef(String type, String id) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.id = Objects.requireNonNull(id, "id must not be null");

        if (type.isEmpty()) {
            throw new SystemException("type must not be empty", IError.PARAMS_EXIST_ERROR.getCode());
        }
        if (id.isEmpty()) {
            throw new SystemException("id must not be empty", IError.PARAMS_EXIST_ERROR.getCode());
        }
    }

    public static ObjectRef of(String type, String id) {
        return new ObjectRef(type, id);
    }

    public static ObjectRef parse(String objectString) {
        if (objectString == null || objectString.isEmpty()) {
            throw new SystemException("objectString must not be null or empty", IError.PARAMS_EXIST_ERROR.getCode());
        }

        int separatorIndex = objectString.indexOf(SEPARATOR);
        if (separatorIndex <= 0 || separatorIndex >= objectString.length() - 1) {
            throw new SystemException("Invalid object format: " + objectString + ". Expected format: type:id", IError.PARAMS_EXIST_ERROR.getCode());
        }

        String type = objectString.substring(0, separatorIndex);
        String id = objectString.substring(separatorIndex + 1);
        return new ObjectRef(type, id);
    }

    @Override
    public String toString() {
        return type + SEPARATOR + id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectRef objectRef = (ObjectRef) o;
        return type.equals(objectRef.type) && id.equals(objectRef.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
