package org.kitona.zus.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.kitona.zus.common.exception.IError;
import org.kitona.zus.common.exception.SystemException;

import java.util.Objects;

/**
 * 主体值对象
 *
 * 表示权限关系中的主体，即"谁"拥有权限。支持直接用户、用户集、通配符。不可变值对象。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode
@Getter
public final class Subject {

    public static final String SEPARATOR = ":";
    public static final String RELATION_SEPARATOR = "#";
    public static final String WILDCARD = "*";

    private final String type;

    private final String id;

    private final String relation;

    private Subject(String type, String id, String relation) {
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.relation = relation;

        if (type.isEmpty()) {
            throw new SystemException("type must not be empty", IError.PARAMS_EXIST_ERROR.getCode());
        }
        if (id.isEmpty()) {
            throw new SystemException("id must not be empty", IError.PARAMS_EXIST_ERROR.getCode());
        }
    }

    public static Subject user(String type, String id) {
        return new Subject(type, id, null);
    }

    public static Subject userset(String type, String id, String relation) {
        if (relation == null || relation.isEmpty()) {
            throw new SystemException("relation must not be null or empty for userset", IError.PARAMS_EXIST_ERROR.getCode());
        }
        return new Subject(type, id, relation);
    }

    public static Subject wildcard(String type) {
        return new Subject(type, WILDCARD, null);
    }

    public static Subject parse(String subjectString) {
        if (subjectString == null || subjectString.isEmpty()) {
            throw new SystemException("subjectString must not be null or empty", IError.PARAMS_EXIST_ERROR.getCode());
        }

        int colonIndex = subjectString.indexOf(SEPARATOR);
        if (colonIndex <= 0 || colonIndex >= subjectString.length() - 1) {
            throw new SystemException("Invalid subject format: " + subjectString, IError.PARAMS_EXIST_ERROR.getCode());
        }

        String type = subjectString.substring(0, colonIndex);
        String rest = subjectString.substring(colonIndex + 1);

        int hashIndex = rest.indexOf(RELATION_SEPARATOR);
        if (hashIndex > 0) {
            String id = rest.substring(0, hashIndex);
            String relation = rest.substring(hashIndex + 1);
            if (relation.isEmpty()) {
                throw new SystemException("Invalid userset format: " + subjectString, IError.PARAMS_EXIST_ERROR.getCode());
            }
            return userset(type, id, relation);
        }

        return new Subject(type, rest, null);
    }

    public boolean isUserset() {
        return relation != null && !relation.isEmpty();
    }

    public boolean isWildcard() {
        return WILDCARD.equals(id);
    }

    public boolean isDirectUser() {
        return !isUserset() && !isWildcard();
    }

    @Override
    public String toString() {
        if (isUserset()) {
            return type + SEPARATOR + id + RELATION_SEPARATOR + relation;
        }
        return type + SEPARATOR + id;
    }
}
