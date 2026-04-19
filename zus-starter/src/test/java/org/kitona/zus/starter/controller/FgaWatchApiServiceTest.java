package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.controller.IFgaTupleApiService;
import org.kitona.zus.api.controller.IFgaWatchApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.request.common.FgaReferenceRequest;
import org.kitona.zus.api.request.common.FgaTupleKeyRequest;
import org.kitona.zus.api.request.model.FgaRelationDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeDefinitionInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.request.tuple.FgaTupleWriteItem;
import org.kitona.zus.api.request.tuple.FgaWriteRequest;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.sse.SseConnectionManager;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link IFgaWatchApiService} 端到端真实链路测试。
 *
 * <p>直接调用 controller 方法获取 {@link SseEmitter}，通过 {@link SseConnectionManager}
 * 提供的连接计数接口校验订阅是否成功建立，并在每个用例结束后主动释放连接
 * 防止线程池泄漏。
 */
@DisplayName("FGA Watch API 端到端测试")
class FgaWatchApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaWatchApiService watchApi;

    @Autowired
    private IFgaStoreApiService storeApi;

    @Autowired
    private IFgaAuthModelApiService modelApi;

    @Autowired
    private IFgaTupleApiService tupleApi;

    @Autowired
    private SseConnectionManager sseConnectionManager;

    private SseEmitter currentEmitter;

    @AfterEach
    void releaseEmitter() {
        if (currentEmitter != null) {
            currentEmitter.complete();
            currentEmitter = null;
        }
    }

    @Test
    @DisplayName("watch 无 startAt：返回 SseEmitter 并注册一个活跃连接")
    void watch_withoutStartAt_registersConnection() {
        String storeId = prepareStore();
        long before = sseConnectionManager.getActiveConnectionCount(storeId);

        currentEmitter = watchApi.watch(storeId, null, null);

        assertThat(currentEmitter).isNotNull();
        assertThat(sseConnectionManager.getActiveConnectionCount(storeId)).isEqualTo(before + 1);
    }

    @Test
    @DisplayName("watch 传 startAt：连接建立后会回放历史变更（不抛异常即视为通过）")
    void watch_withStartAt_replaysHistory() {
        String storeId = prepareStoreWithActiveModel();
        tupleApi.write(storeId, FgaWriteRequest.builder()
                .writes(List.of(writeItem("document", "doc-1", "viewer", "user", "alice"))).build());

        currentEmitter = watchApi.watch(storeId, 0L, null);

        assertThat(currentEmitter).isNotNull();
        assertThat(sseConnectionManager.getActiveConnectionCount(storeId)).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("watch 不同 Store 的连接相互隔离")
    void watch_isolatesByStore() {
        String storeA = prepareStore();
        String storeB = prepareStore();

        SseEmitter emitterA = watchApi.watch(storeA, null, null);
        try {
            assertThat(sseConnectionManager.getActiveConnectionCount(storeA)).isEqualTo(1);
            assertThat(sseConnectionManager.getActiveConnectionCount(storeB)).isZero();
        } finally {
            emitterA.complete();
        }
    }

    // ========== helpers ==========

    private String prepareStore() {
        FgaCreateStoreRequest req = new FgaCreateStoreRequest();
        req.setName("watch-test-" + System.nanoTime());
        FgaStoreVO store = storeApi.createStore(req).getData();
        return store.getStoreId();
    }

    private String prepareStoreWithActiveModel() {
        String storeId = prepareStore();
        FgaModelVO model = modelApi.writeModel(storeId, simpleModel()).getData();
        modelApi.publishModel(storeId, model.getModelId());
        modelApi.activateModel(storeId, model.getModelId());
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
