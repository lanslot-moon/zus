package org.kitona.zus.starter.converter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.converter.FgaRelationQueryConverter;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListSubjectsRequest;
import org.kitona.zus.api.request.common.FgaConsistencyOptions;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.service.dto.query.ListObjectsQuery;
import org.kitona.zus.service.dto.query.ListSubjectsQuery;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 关系查询 API 转换器测试。
 *
 * <p>项目约定 API 模块不承载测试代码，因此这里在 starter 组合层验证 API 请求到应用层查询对象
 * 的转换语义，避免可选参数在跨层传递时丢失。
 */
@DisplayName("FGA Relation Query Converter 测试")
class FgaRelationQueryConverterTest {

    @Test
    @DisplayName("listObjects 转换时保留所有可选参数")
    void shouldKeepListObjectsOptionalFields() {
        FgaListObjectsRequest request = FgaListObjectsRequest.builder()
                .subject(FgaReferenceRequest.builder()
                        .type("group")
                        .id("eng")
                        .relation("member")
                        .build())
                .relation("viewer")
                .objectType("document")
                .authorizationModelId("model-explicit")
                .consistency(FgaConsistencyOptions.builder()
                        .preference(FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH)
                        .atRevision("42")
                        .build())
                .context(Map.of("ip", "127.0.0.1"))
                .build();

        ListObjectsQuery query = FgaRelationQueryConverter.toListObjectsQuery("store-1", request);

        assertThat(query.getStoreId()).isEqualTo("store-1");
        assertThat(query.getSubjectType()).isEqualTo("group");
        assertThat(query.getSubjectId()).isEqualTo("eng");
        assertThat(query.getSubjectRelation()).isEqualTo("member");
        assertThat(query.getRelation()).isEqualTo("viewer");
        assertThat(query.getObjectType()).isEqualTo("document");
        assertThat(query.getAuthorizationModelId()).isEqualTo("model-explicit");
        assertThat(query.getConsistencyToken()).isEqualTo("42");
        assertThat(query.getContext()).containsEntry("ip", "127.0.0.1");
    }

    @Test
    @DisplayName("listSubjects 转换时保留所有可选参数")
    void shouldKeepListSubjectsOptionalFields() {
        FgaListSubjectsRequest request = FgaListSubjectsRequest.builder()
                .object(FgaReferenceRequest.builder()
                        .type("document")
                        .id("doc-1")
                        .build())
                .relation("viewer")
                .subjectFilters(List.of(FgaListSubjectsRequest.SubjectFilter.builder()
                        .type("group")
                        .relation("member")
                        .build()))
                .authorizationModelId("model-explicit")
                .consistency(FgaConsistencyOptions.builder()
                        .preference(FgaConsistencyOptions.Preference.AT_LEAST_AS_FRESH)
                        .atRevision("43")
                        .build())
                .context(Map.of("region", "cn"))
                .build();

        ListSubjectsQuery query = FgaRelationQueryConverter.toListSubjectsQuery("store-1", request);

        assertThat(query.getStoreId()).isEqualTo("store-1");
        assertThat(query.getObjectType()).isEqualTo("document");
        assertThat(query.getObjectId()).isEqualTo("doc-1");
        assertThat(query.getRelation()).isEqualTo("viewer");
        assertThat(query.getSubjectType()).isEqualTo("group");
        assertThat(query.getSubjectRelation()).isEqualTo("member");
        assertThat(query.getAuthorizationModelId()).isEqualTo("model-explicit");
        assertThat(query.getConsistencyToken()).isEqualTo("43");
        assertThat(query.getContext()).containsEntry("region", "cn");
    }
}
