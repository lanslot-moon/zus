package org.kitona.zus.infrastructure.persistence.mysql.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.domain.authorization.audit.Changelog;
import org.kitona.zus.domain.authorization.tuple.TupleKey;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Changelog 持久化转换器测试。
 */
class ChangelogConverterTest {

    @Test
    @DisplayName("直接主体转换为变更日志 PO 时 subjectRelation 存为空字符串")
    void toPO_normalizesDirectSubjectRelationToEmptyString() {
        Changelog changelog = Changelog.createWriteLog(
                "store-1",
                TupleKey.of("document", "doc-1", "viewer", "user", "alice"),
                1L
        );

        TupleChangelogPO po = ChangelogConverter.toPO(changelog);

        assertThat(po.getSubjectRelation()).isEqualTo("");
    }
}
