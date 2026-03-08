package org.kitona.zus.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

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
@ToString
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
            throw new IllegalArgumentException("type must not be empty");
        }
        if (id.isEmpty()) {
            throw new IllegalArgumentException("id must not be empty");
        }
    }

    public static Subject user(String type, String id) {
        return new Subject(type, id, null);
    }

    public static Subject userset(String type, String id, String relation) {
        if (relation == null || relation.isEmpty()) {
            throw new IllegalArgumentException("relation must not be null or empty for userset");
        }
        return new Subject(type, id, relation);
    }

    public static Subject wildcard(String type) {
        return new Subject(type, WILDCARD, null);
    }

    public static Subject parse(String subjectString) {
        if (subjectString == null || subjectString.isEmpty()) {
            throw new IllegalArgumentException("subjectString must not be null or empty");
        }

        int colonIndex = subjectString.indexOf(SEPARATOR);
        if (colonIndex <= 0 || colonIndex >= subjectString.length() - 1) {
            throw new IllegalArgumentException("Invalid subject format: " + subjectString);
        }

        String type = subjectString.substring(0, colonIndex);
        String rest = subjectString.substring(colonIndex + 1);

        int hashIndex = rest.indexOf(RELATION_SEPARATOR);
        if (hashIndex > 0) {
            String id = rest.substring(0, hashIndex);
            String relation = rest.substring(hashIndex + 1);
            if (relation.isEmpty()) {
                throw new IllegalArgumentException("Invalid userset format: " + subjectString);
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
}
