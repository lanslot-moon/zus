package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.controller.IFgaAuthViewApiService;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.request.authorization.FgaExpandRequest;
import org.kitona.zus.api.request.authorization.FgaListObjectsRequest;
import org.kitona.zus.api.request.authorization.FgaListUsersRequest;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.model.FgaRelationDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaExpandTreeVO;
import org.kitona.zus.api.response.FgaListObjectsResponseVO;
import org.kitona.zus.api.response.FgaListUsersResponseVO;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link IFgaAuthViewApiService} 端到端真实链路测试。
 *
 * <p>场景：document(viewer: user) 模型，写入多条真实元组后验证
 * list-objects / list-users / expand 的输出。
 */
@DisplayName("FGA Relation Query API 端到端测试")
class FgaRelationQueryApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaAuthViewApiService authViewApi;

    @Autowired
    private IFgaStoreApiService storeApi;

    @Autowired
    private IFgaAuthModelApiService modelApi;

    @Autowired
    private IFgaTupleApiService tupleApi;

    @Test
    @DisplayName("listObjects 列出主体有权的对象 id 集合")
    void listObjects_returnsAuthorizedObjectIds() {
        String storeId = prepareStoreWithTuples();

        FgaListObjectsRequest req = FgaListObjectsRequest.builder()
                .subject(FgaReferenceRequest.builder().type("user").id("alice").build())
                .relation("viewer")
                .objectType("document")
                .build();

        RestResult<FgaListObjectsResponseVO> result = authViewApi.listObjects(storeId, req);
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getObjects())
                .contains("document:doc-1", "document:doc-2")
                .doesNotContain("document:doc-3");
    }

    @Test
    @DisplayName("listUsers 列出对资源有权的主体（userFilters 过滤 user 类型）")
    void listUsers_returnsAuthorizedSubjects() {
        String storeId = prepareStoreWithTuples();

        FgaListUsersRequest req = FgaListUsersRequest.builder()
                .object(FgaReferenceRequest.builder().type("document").id("doc-1").build())
                .relation("viewer")
                .userFilters(List.of(FgaListUsersRequest.UserFilter.builder().type("user").build()))
                .build();

        RestResult<FgaListUsersResponseVO> result = authViewApi.listUsers(storeId, req);
        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData().getUsers())
                .isNotNull()
                .extracting(u -> u.getType() + ":" + u.getId())
                .contains("user:alice");
    }

    @Test
    @DisplayName("expand 暂未实现 → 返回 501 错误码")
    void expand_returns501() {
        String storeId = prepareStoreWithTuples();

        FgaExpandRequest req = FgaExpandRequest.builder()
                .object(FgaReferenceRequest.builder().type("document").id("doc-1").build())
                .relation("viewer")
                .build();

        RestResult<FgaExpandTreeVO> result = authViewApi.expand(storeId, req);
        assertThat(result.getCode()).isEqualTo(501);
    }

    // ========== helpers ==========

    private String prepareStoreWithTuples() {
        FgaCreateStoreRequest createReq = new FgaCreateStoreRequest();
        createReq.setName("relation-query-test-" + System.nanoTime());
        FgaStoreVO store = storeApi.createStore(createReq).getData();
        String storeId = store.getStoreId();

        FgaModelVO model = modelApi.writeModel(storeId, simpleModel()).getData();
        modelApi.publishModel(storeId, model.getModelId());
        modelApi.activateModel(storeId, model.getModelId());

        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(
                        writeItem("document", "doc-1", "viewer", "user", "alice"),
                        writeItem("document", "doc-2", "viewer", "user", "alice"),
                        writeItem("document", "doc-3", "viewer", "user", "bob")
                )).build());

        return storeId;
    }

    private static FgaTupleWriteItem writeItem(String objectType, String objectId, String relation,
                                               String subjectType, String subjectId) {
        return FgaTupleWriteItem.builder()
                .tupleKey(FgaTupleKeyRequest.builder()
                        .object(FgaReferenceRequest.builder().type(objectType).id(objectId).build())
                        .relation(relation)
                        .subject(FgaReferenceRequest.builder().type(subjectType).id(subjectId).build())
                        .build())
                .build();
    }

    private static FgaWriteAuthorizationModelRequest simpleModel() {
        FgaTypeDefinitionInput document = FgaTypeDefinitionInput.builder()
                .type("document")
                .relations(List.of(FgaRelationDefinitionInput.builder()
                        .name("viewer").rewriteExpression("self")
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
}
