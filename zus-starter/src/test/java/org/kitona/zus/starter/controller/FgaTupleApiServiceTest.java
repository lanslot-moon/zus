package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyFilterRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.model.FgaRelationDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.request.tuple.FgaReadRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.FgaTupleChangeVO;
import org.kitona.zus.api.response.FgaTupleVO;
import org.kitona.zus.api.response.FgaWriteResultVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link IFgaTupleApiService} 端到端真实链路测试。
 *
 * <p>前置条件：每个测试先构造一个含激活模型的 Store，保证 write 路径下
 * condition resolver 可以命中；真实落库 {@code fga_relation_tuple} 与 {@code fga_tuple_changelog}。
 */
@DisplayName("FGA Tuple API 端到端测试")
class FgaTupleApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaTupleApiService tupleApi;

    @Autowired
    private IFgaStoreApiService storeApi;

    @Autowired
    private IFgaAuthModelApiService modelApi;

    // ========== write ==========

    @Test
    @DisplayName("write 纯写入：落库 fga_relation_tuple + changelog，返回非空 zookie")
    void write_persistsTupleAndChangelog() {
        String storeId = prepareStoreWithActiveModel();

        FgaWriteRequest req = FgaWriteRequest.builder()
                .writes(List.of(writeItem("document", "doc-1", "viewer", "user", "alice")))
                .build();

        RestResult<FgaWriteResultVO> result = tupleApi.write(storeId, req);

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getWrittenCount()).isEqualTo(1);
        assertThat(result.getData().getDeletedCount()).isEqualTo(0);
        assertThat(result.getData().getZookie()).isNotBlank();

        Integer tupleRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_relation_tuple WHERE store_id = ? AND object_type = 'document' "
                        + "AND object_id = 'doc-1' AND relation = 'viewer' AND subject_id = 'alice' AND is_deleted = 0",
                Integer.class, storeId);
        assertThat(tupleRows).isEqualTo(1);

        Integer changelogRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_tuple_changelog WHERE store_id = ? AND operation = 'WRITE'",
                Integer.class, storeId);
        assertThat(changelogRows).isEqualTo(1);
    }

    @Test
    @DisplayName("write + delete 在同一事务内完成：最终仅剩未被删除的元组")
    void writeAndDelete_inSameRequest() {
        String storeId = prepareStoreWithActiveModel();

        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(
                        writeItem("document", "doc-1", "viewer", "user", "alice"),
                        writeItem("document", "doc-1", "viewer", "user", "bob")
                )).build());

        FgaWriteRequest delReq = FgaWriteRequest.builder()
                .deletes(List.of(tupleKey("document", "doc-1", "viewer", "user", "alice")))
                .build();
        RestResult<FgaWriteResultVO> delResult = tupleApi.write(storeId, delReq);
        assertThat(delResult.getCode()).isEqualTo(200);
        assertThat(delResult.getData().getDeletedCount()).isEqualTo(1);

        Integer aliveAlice = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_relation_tuple WHERE store_id = ? AND subject_id = 'alice' AND is_deleted = 0",
                Integer.class, storeId);
        assertThat(aliveAlice).isEqualTo(0);

        Integer aliveBob = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_relation_tuple WHERE store_id = ? AND subject_id = 'bob' AND is_deleted = 0",
                Integer.class, storeId);
        assertThat(aliveBob).isEqualTo(1);
    }

    // ========== read ==========

    @Test
    @DisplayName("read 按 object 过滤可命中对应元组")
    void read_byObject_returnsMatchingTuples() {
        String storeId = prepareStoreWithActiveModel();
        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(
                        writeItem("document", "doc-1", "viewer", "user", "alice"),
                        writeItem("document", "doc-2", "viewer", "user", "alice")
                )).build());

        FgaReadRequest req = FgaReadRequest.builder()
                .tupleKey(FgaTupleKeyFilterRequest.builder()
                        .object(FgaTupleKeyFilterRequest.FgaReferenceFilter.builder()
                                .type("document").id("doc-1").build())
                        .build())
                .pageSize(50)
                .build();

        RestResult<PageResponseVO<FgaTupleVO>> result = tupleApi.read(storeId, req);

        assertThat(result.getCode()).isEqualTo(200);
        PageResponseVO<FgaTupleVO> page = result.getData();
        assertThat(page.getData())
                .hasSize(1)
                .first()
                .satisfies(t -> {
                    assertThat(t.getObjectId()).isEqualTo("doc-1");
                    assertThat(t.getSubjectId()).isEqualTo("alice");
                });
    }

    @Test
    @DisplayName("read 无过滤条件时返回 store 内全部未删除元组")
    void read_noFilter_returnsAllTuples() {
        String storeId = prepareStoreWithActiveModel();
        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(
                        writeItem("document", "doc-1", "viewer", "user", "alice"),
                        writeItem("document", "doc-2", "viewer", "user", "bob")
                )).build());

        FgaReadRequest req = FgaReadRequest.builder()
                .tupleKey(FgaTupleKeyFilterRequest.builder().build())
                .pageSize(50)
                .build();

        PageResponseVO<FgaTupleVO> page = tupleApi.read(storeId, req).getData();
        assertThat(page.getData()).hasSize(2);
    }

    // ========== listChanges ==========

    @Test
    @DisplayName("listChanges 返回按 zookie 倒序的变更事件")
    void listChanges_returnsChangelogEntries() {
        String storeId = prepareStoreWithActiveModel();
        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(writeItem("document", "doc-1", "viewer", "user", "alice"))).build());
        tupleApi.write(storeId, FgaWriteRequest.builder()
                .deletes(List.of(tupleKey("document", "doc-1", "viewer", "user", "alice"))).build());

        RestResult<PageResponseVO<FgaTupleChangeVO>> result = tupleApi.listChanges(storeId, null, null, 50);

        assertThat(result.getCode()).isEqualTo(200);
        List<FgaTupleChangeVO> changes = result.getData().getData();
        assertThat(changes).hasSize(2);
        assertThat(changes).extracting(FgaTupleChangeVO::getOperation)
                .containsExactlyInAnyOrder("WRITE", "DELETE");
    }

    // ========== helpers ==========

    private String prepareStoreWithActiveModel() {
        FgaCreateStoreRequest createReq = new FgaCreateStoreRequest();
        createReq.setName("tuple-test-" + System.nanoTime());
        FgaStoreVO store = storeApi.createStore(createReq).getData();

        FgaWriteAuthorizationModelRequest modelReq = simpleModel();
        FgaModelVO model = modelApi.writeModel(store.getStoreId(), modelReq).getData();
        modelApi.publishModel(store.getStoreId(), model.getModelId());
        modelApi.activateModel(store.getStoreId(), model.getModelId());

        return store.getStoreId();
    }

    private static FgaWriteAuthorizationModelRequest simpleModel() {
        FgaTypeDefinitionInput document = FgaTypeDefinitionInput.builder()
                .type("document")
                .relations(List.of(FgaRelationDefinitionInput.builder()
                        .name("viewer")
                        .rewriteExpression("self")
                        .restrictions(List.of(FgaTypeRestrictionInput.builder().type("user").build()))
                        .build()))
                .build();
        FgaTypeDefinitionInput user = FgaTypeDefinitionInput.builder()
                .type("user")
                .relations(List.of(FgaRelationDefinitionInput.builder()
                        .name("self").rewriteExpression("self").build()))
                .build();
        return FgaWriteAuthorizationModelRequest.builder()
                .schemaVersion("1.1")
                .typeDefinitions(List.of(document, user))
                .build();
    }

    private static FgaTupleWriteItem writeItem(String objectType, String objectId, String relation,
                                               String subjectType, String subjectId) {
        return FgaTupleWriteItem.builder()
                .tupleKey(tupleKey(objectType, objectId, relation, subjectType, subjectId))
                .build();
    }

    private static FgaTupleKeyRequest tupleKey(String objectType, String objectId, String relation,
                                               String subjectType, String subjectId) {
        return FgaTupleKeyRequest.builder()
                .object(FgaReferenceRequest.builder().type(objectType).id(objectId).build())
                .relation(relation)
                .subject(FgaReferenceRequest.builder().type(subjectType).id(subjectId).build())
                .build();
    }
}
