package org.kitona.zus.domain.valueobject;

import lombok.Getter;

import java.util.Objects;

/**
 * Zookie 值对象。一致性读取快照令牌，不可变。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@Getter
public final class Zookie implements Comparable<Zookie> {

    public static final Zookie EMPTY = new Zookie(null);

    private final Long version;

    private Zookie(Long version) {
        this.version = version;
    }

    public static Zookie of(Long version) {
        if (version == null || version <= 0) {
            return EMPTY;
        }
        return new Zookie(version);
    }

    public static Zookie parse(String token) {
        if (token == null || token.isEmpty()) {
            return EMPTY;
        }
        try {
            return of(Long.parseLong(token));
        } catch (NumberFormatException e) {
            return EMPTY;
        }
    }

    public boolean isEmpty() {
        return version == null;
    }

    public boolean isValid() {
        return version != null && version > 0;
    }

    public String toToken() {
        return version != null ? version.toString() : "";
    }

    @Override
    public String toString() {
        return version != null ? "Zookie(" + version + ")" : "Zookie(empty)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zookie zookie = (Zookie) o;
        return Objects.equals(version, zookie.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version);
    }

    @Override
    public int compareTo(Zookie other) {
        if (this.version == null && other.version == null) {
            return 0;
        }
        if (this.version == null) {
            return -1;
        }
        if (other.version == null) {
            return 1;
        }
        return this.version.compareTo(other.version);
    }
}
