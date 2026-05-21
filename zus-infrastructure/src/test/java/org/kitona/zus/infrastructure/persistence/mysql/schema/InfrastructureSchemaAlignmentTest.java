package org.kitona.zus.infrastructure.persistence.mysql.schema;

import com.baomidou.mybatisplus.annotation.TableName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.infrastructure.persistence.mysql.entity.AuthModelPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.ConditionDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.RelationTuplePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.StorePO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeDefinitionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TypeRestrictionPO;
import org.kitona.zus.infrastructure.persistence.mysql.entity.TupleChangelogPO;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 基础设施建表脚本与 MyBatis Plus PO 映射一致性测试。
 */
class InfrastructureSchemaAlignmentTest {

    private static final String SCHEMA_RESOURCE = "/db/table_schema.sql";

    @Test
    @DisplayName("table_schema.sql 包含授权模型核心 PO 映射的表名")
    void schemaContainsMappedModelTables() throws IOException {
        String schema = loadSchema();

        List<Class<?>> mappedTypes = List.of(
                AuthModelPO.class,
                TypeDefinitionPO.class,
                RelationDefinitionPO.class,
                TypeRestrictionPO.class,
                ConditionDefinitionPO.class
        );

        for (Class<?> mappedType : mappedTypes) {
            String tableName = mappedType.getAnnotation(TableName.class).value();
            assertThat(schema)
                    .as("%s 映射表 %s 必须存在于 table_schema.sql", mappedType.getSimpleName(), tableName)
                    .containsPattern(createTablePattern(tableName));
        }
    }

    @Test
    @DisplayName("关系限制表包含 userset restriction 需要的主体关系列")
    void relationRestrictionTableContainsAllowedSubjectRelation() throws IOException {
        String schema = loadSchema();
        String tableName = TypeRestrictionPO.class.getAnnotation(TableName.class).value();

        assertThat(extractCreateTable(schema, tableName))
                .contains("`relation_definition_id`")
                .contains("`allowed_type`")
                .contains("`allowed_subject_relation`");
    }

    @Test
    @DisplayName("旧库补丁包含条件定义表建表语句")
    void legacyPatchCreatesConditionDefinitionTable() throws IOException {
        String patch = loadResource("/db/patch/20260513_create_condition_definition.sql");
        String tableName = ConditionDefinitionPO.class.getAnnotation(TableName.class).value();

        assertThat(patch)
                .containsPattern(createTablePattern(tableName))
                .contains("`condition_name`")
                .contains("`parameter_schema`");
    }

    @Test
    @DisplayName("元组变更日志操作类型字段使用字符串枚举")
    void changelogOperationColumnUsesStringEnum() throws IOException {
        String schema = loadSchema();
        String tableName = TupleChangelogPO.class.getAnnotation(TableName.class).value();

        assertThat(extractCreateTable(schema, tableName))
                .contains("`operation`         varchar(16)")
                .contains("操作类型: write/delete");
    }

    @Test
    @DisplayName("核心表都包含创建时间和更新时间字段")
    void coreTablesContainCreateTimeAndUpdateTime() throws IOException {
        String schema = loadSchema();
        List<Class<?>> coreTypes = List.of(
                StorePO.class,
                AuthModelPO.class,
                TypeDefinitionPO.class,
                RelationDefinitionPO.class,
                TypeRestrictionPO.class,
                ConditionDefinitionPO.class,
                RelationTuplePO.class,
                TupleChangelogPO.class
        );

        for (Class<?> coreType : coreTypes) {
            String tableName = coreType.getAnnotation(TableName.class).value();
            assertThat(extractCreateTable(schema, tableName))
                    .as("%s 映射表 %s 必须包含 create_time 和 update_time", coreType.getSimpleName(), tableName)
                    .contains("`create_time`")
                    .contains("`update_time`");
        }
    }

    @Test
    @DisplayName("核心表都包含逻辑删除字段")
    void coreTablesContainSoftDeleteColumn() throws IOException {
        String schema = loadSchema();
        List<Class<?>> coreTypes = List.of(
                StorePO.class,
                AuthModelPO.class,
                TypeDefinitionPO.class,
                RelationDefinitionPO.class,
                TypeRestrictionPO.class,
                ConditionDefinitionPO.class,
                RelationTuplePO.class,
                TupleChangelogPO.class
        );

        for (Class<?> coreType : coreTypes) {
            String tableName = coreType.getAnnotation(TableName.class).value();
            assertThat(extractCreateTable(schema, tableName))
                    .as("%s 映射表 %s 必须包含 is_deleted", coreType.getSimpleName(), tableName)
                    .contains("`is_deleted`");
        }
    }

    @Test
    @DisplayName("旧库补丁包含变更日志 operation 字段类型修复")
    void legacyPatchMigratesChangelogOperationColumn() throws IOException {
        String patch = loadResource("/db/patch/20260521_fix_tuple_changelog_operation.sql");
        String tableName = TupleChangelogPO.class.getAnnotation(TableName.class).value();

        assertThat(patch)
                .contains(tableName)
                .contains("modify column operation varchar(16)");
    }

    private static String loadSchema() throws IOException {
        return loadResource(SCHEMA_RESOURCE);
    }

    private static String loadResource(String resource) throws IOException {
        try (InputStream input = InfrastructureSchemaAlignmentTest.class.getResourceAsStream(resource)) {
            assertThat(input).as("schema resource %s", resource).isNotNull();
            return new String(input.readAllBytes(), StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
        }
    }

    private static Pattern createTablePattern(String tableName) {
        return Pattern.compile("create\\s+table\\s+(?:if\\s+not\\s+exists\\s+)?`?"
                + Pattern.quote(tableName.toLowerCase(Locale.ROOT)) + "`?\\s*\\(");
    }

    private static String extractCreateTable(String schema, String tableName) {
        Pattern pattern = createTablePattern(tableName);
        java.util.regex.Matcher matcher = pattern.matcher(schema);
        assertThat(matcher.find()).as("建表语句必须存在: %s", tableName).isTrue();
        int start = matcher.start();
        int end = schema.indexOf(";", start);
        assertThat(end).as("建表语句必须以分号结束: %s", tableName).isGreaterThan(start);
        return schema.substring(start, end);
    }
}
