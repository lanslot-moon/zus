package org.kitona.zus.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

/**
 * 元组键值对象。表示 (object, relation, subject) 组成的权限元组键。不可变。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@ToString
@EqualsAndHashCode
@Getter
public final class TupleKey {

    private final ObjectRef object;

    private final String relation;

    private final Subject subject;

    private TupleKey(ObjectRef object, String relation, Subject subject) {
        this.object = Objects.requireNonNull(object, "object must not be null");
        this.relation = Objects.requireNonNull(relation, "relation must not be null");
        this.subject = Objects.requireNonNull(subject, "subject must not be null");

        if (relation.isEmpty()) {
            throw new IllegalArgumentException("relation must not be empty");
        }
    }

    public static TupleKey of(ObjectRef object, String relation, Subject subject) {
        return new TupleKey(object, relation, subject);
    }

    public static TupleKey of(String objectType, String objectId, String relation,
                              String subjectType, String subjectId) {
        return new TupleKey(
                ObjectRef.of(objectType, objectId),
                relation,
                Subject.user(subjectType, subjectId)
        );
    }

    public static TupleKey of(String objectType, String objectId, String relation,
                              String subjectType, String subjectId, String subjectRelation) {
        Subject subject;
        if (subjectRelation != null && !subjectRelation.isEmpty()) {
            subject = Subject.userset(subjectType, subjectId, subjectRelation);
        } else {
            subject = Subject.user(subjectType, subjectId);
        }
        return new TupleKey(ObjectRef.of(objectType, objectId), relation, subject);
    }

    public String getObjectType() {
        return object.getType();
    }

    public String getObjectId() {
        return object.getId();
    }

    public String getSubjectType() {
        return subject.getType();
    }

    public String getSubjectId() {
        return subject.getId();
    }

    public String getSubjectRelation() {
        return subject.getRelation();
    }

    public String toCacheKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(object.getType()).append(":")
                .append(object.getId()).append(":")
                .append(relation).append(":")
                .append(subject.getType()).append(":")
                .append(subject.getId());
        if (subject.getRelation() != null) {
            sb.append(":").append(subject.getRelation());
        }
        return sb.toString();
    }
}
