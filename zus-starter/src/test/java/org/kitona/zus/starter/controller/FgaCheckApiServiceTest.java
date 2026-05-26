package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.controller.IFgaCheckApiService;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.request.authorization.FgaBatchCheckRequest;
import org.kitona.zus.api.request.authorization.FgaCheckRequest;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.model.FgaRelationSchemaInput;
import org.kitona.zus.api.request.model.FgaTypeSchemaInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaBatchCheckResultVO;
import org.kitona.zus.api.response.FgaCheckResultVO;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link IFgaCheckApiService} 端到端真实链路测试。
 *
 * <p>流程：创建 Store → 激活 document(viewer: user) 模型 → 写入一条真实元组 →
 * 调用 {@code /check} 与 {@code /batch-check} 观察 allow / deny。
 */
@DisplayName("FGA Check API 端到端测试")
class FgaCheckApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaCheckApiService checkApi;

    @Autowired
    private IFgaStoreApiService storeApi;

    @Autowired
    private IFgaAuthModelApiService modelApi;

    @Autowired
    private IFgaTupleApiService tupleApi;

    @Test
    @DisplayName("check 命中直接授权：allowed=true；未授权主体 allowed=false")
    void check_allowedAndDenied() {
        String storeId = prepareStoreWithTuple();

        FgaCheckResultVO allowed = checkApi.check(storeId, checkReq("document", "doc-1", "viewer", "user", "alice"))
                .getData();
        assertThat(allowed.isAllowed()).isTrue();

        FgaCheckResultVO denied = checkApi.check(storeId, checkReq("document", "doc-1", "viewer", "user", "bob"))
                .getData();
        assertThat(denied.isAllowed()).isFalse();
    }

    @Test
    @DisplayName("check 支持 userset subject 递归展开")
    void check_usersetSubject_recursivelyMatchesMember() {
        FgaCreateStoreRequest createReq = new FgaCreateStoreRequest();
        createReq.setName("check-userset-subject-" + System.nanoTime());
        FgaStoreVO store = storeApi.createStore(createReq).getData();
        String storeId = store.getStoreId();

        FgaModelVO model = modelApi.writeModel(storeId, usersetSubjectModel()).getData();
        modelApi.publishModel(storeId, model.getModelId());
        modelApi.activateModel(storeId, model.getModelId());

        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(
                        FgaTupleWriteItem.builder()
                                .tupleKey(tupleKey("group", "platform", "member", "user", "erin"))
                                .build(),
                        FgaTupleWriteItem.builder()
                                .tupleKey(tupleKey("document", "roadmap", "viewer", "group", "platform", "member"))
                                .build()
                )).build());

        FgaCheckResultVO allowed = checkApi.check(storeId, checkReq("document", "roadmap", "viewer", "user", "erin"))
                .getData();

        assertThat(allowed.isAllowed()).isTrue();
    }

    @Test
    @DisplayName("batchCheck 一次请求返回多个 correlationId 对应的判定结果")
    void batchCheck_returnsResultsByCorrelationId() {
        String storeId = prepareStoreWithTuple();

        FgaBatchCheckRequest req = FgaBatchCheckRequest.builder()
                .checks(List.of(
                        FgaBatchCheckRequest.CheckItem.builder()
                                .correlationId("c1")
                                .tupleKey(tupleKey("document", "doc-1", "viewer", "user", "alice")).build(),
                        FgaBatchCheckRequest.CheckItem.builder()
                                .correlationId("c2")
                                .tupleKey(tupleKey("document", "doc-1", "viewer", "user", "bob")).build()
                )).build();

        RestResult<FgaBatchCheckResultVO> result = checkApi.batchCheck(storeId, req);
        assertThat(result.getCode()).isEqualTo(200);

        FgaBatchCheckResultVO data = result.getData();
        assertThat(data.getResults()).containsOnlyKeys("c1", "c2");
        assertThat(data.getResults().get("c1").isAllowed()).isTrue();
        assertThat(data.getResults().get("c2").isAllowed()).isFalse();
    }

    @Test
    @DisplayName("batchCheck 空 checks 返回空结果 map")
    void batchCheck_emptyChecks_returnsEmptyResults() {
        String storeId = prepareStoreWithTuple();

        FgaBatchCheckRequest req = FgaBatchCheckRequest.builder().checks(List.of()).build();
        FgaBatchCheckResultVO data = checkApi.batchCheck(storeId, req).getData();
        assertThat(data.getResults()).isEmpty();
    }

    // ========== helpers ==========

    private String prepareStoreWithTuple() {
        FgaCreateStoreRequest createReq = new FgaCreateStoreRequest();
        createReq.setName("check-test-" + System.nanoTime());
        FgaStoreVO store = storeApi.createStore(createReq).getData();
        String storeId = store.getStoreId();

        FgaModelVO model = modelApi.writeModel(storeId, simpleModel()).getData();
        modelApi.publishModel(storeId, model.getModelId());
        modelApi.activateModel(storeId, model.getModelId());

        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(FgaTupleWriteItem.builder()
                        .tupleKey(tupleKey("document", "doc-1", "viewer", "user", "alice"))
                        .build())).build());

        return storeId;
    }

    private static FgaCheckRequest checkReq(String objectType, String objectId, String relation,
                                            String subjectType, String subjectId) {
        return FgaCheckRequest.builder()
                .tupleKey(tupleKey(objectType, objectId, relation, subjectType, subjectId))
                .build();
    }

    private static FgaTupleKeyRequest tupleKey(String objectType, String objectId, String relation,
                                               String subjectType, String subjectId) {
        return tupleKey(objectType, objectId, relation, subjectType, subjectId, null);
    }

    private static FgaTupleKeyRequest tupleKey(String objectType, String objectId, String relation,
                                               String subjectType, String subjectId, String subjectRelation) {
        return FgaTupleKeyRequest.builder()
                .object(FgaReferenceRequest.builder().type(objectType).id(objectId).build())
                .relation(relation)
                .subject(FgaReferenceRequest.builder().type(subjectType).id(subjectId).relation(subjectRelation).build())
                .build();
    }

    private static FgaWriteAuthorizationModelRequest simpleModel() {
        return FgaWriteAuthorizationModelRequest.builder()
                .schemaVersion("1.1")
                .types(Map.of(
                        "document", FgaTypeSchemaInput.builder()
                                .relations(Map.of("viewer", FgaRelationSchemaInput.builder()
                                        .rewrite("self")
                                        .allowedSubjectTypes(List.of(FgaTypeRestrictionInput.builder().type("user").build()))
                                        .build()))
                                .build(),
                        "user", FgaTypeSchemaInput.builder()
                                .relations(Map.of("self", FgaRelationSchemaInput.builder().rewrite("self").build()))
                                .build()
                ))
                .build();
    }

    private static FgaWriteAuthorizationModelRequest usersetSubjectModel() {
        return FgaWriteAuthorizationModelRequest.builder()
                .schemaVersion("1.1")
                .types(Map.of(
                        "document", FgaTypeSchemaInput.builder()
                                .relations(Map.of("viewer", FgaRelationSchemaInput.builder()
                                        .rewrite("self")
                                        .allowedSubjectTypes(List.of(
                                                FgaTypeRestrictionInput.builder().type("user").build(),
                                                FgaTypeRestrictionInput.builder().type("group").relation("member").build()
                                        ))
                                        .build()))
                                .build(),
                        "group", FgaTypeSchemaInput.builder()
                                .relations(Map.of("member", FgaRelationSchemaInput.builder()
                                        .rewrite("self")
                                        .allowedSubjectTypes(List.of(FgaTypeRestrictionInput.builder().type("user").build()))
                                        .build()))
                                .build(),
                        "user", FgaTypeSchemaInput.builder()
                                .relations(Map.of("self", FgaRelationSchemaInput.builder().rewrite("self").build()))
                                .build()
                ))
                .build();
    }
}
