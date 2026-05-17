package org.kitona.zus.infrastructure.persistence.mysql.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.domain.authorization.tuple.RelationTuple;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.domain.valueobject.Zookie;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tuple 持久化转换器测试。
 */
class TupleConverterTest {

    @Test
    @DisplayName("直接主体转换为 PO 时 subjectRelation 存为空字符串")
    void toPO_normalizesDirectSubjectRelationToEmptyString() {
        RelationTuple tuple = RelationTuple.create(
                "store-1",
                TupleKey.of("document", "doc-1", "viewer", "user", "alice"),
                Zookie.of(1L)
        );

        RelationTuplePO po = TupleConverter.toPO(tuple);

        assertThat(po.getSubjectRelation()).isEqualTo("");
    }
}
