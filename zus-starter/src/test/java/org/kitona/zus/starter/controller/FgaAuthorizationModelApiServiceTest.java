package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaAuthModelApiService;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.request.model.FgaConditionSchemaInput;
import org.kitona.zus.api.request.model.FgaRelationSchemaInput;
import org.kitona.zus.api.request.model.FgaTypeSchemaInput;
import org.kitona.zus.api.request.model.FgaTypeRestrictionInput;
import org.kitona.zus.api.request.model.FgaWriteAuthorizationModelRequest;
import org.kitona.zus.api.response.FgaModelVO;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.service.exception.ApplicationException;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link IFgaAuthModelApiService} 端到端真实链路测试。
 *
 * <p>真实写库：创建 Store → writeModel 草稿 → publish → activate → deprecate / delete，
 * 走完 {@code fga_auth_model / fga_type_definition / fga_relation_definition / fga_type_restriction}
 * 的所有关联表 CRUD。断言通过接口返回 VO 与 JdbcTemplate 直查双向验证。
 */
@DisplayName("FGA Authorization Model API 端到端测试")
class FgaAuthorizationModelApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaAuthModelApiService modelApi;

    @Autowired
    private IFgaStoreApiService storeApi;

    // ========== writeModel ==========

    @Test
    @DisplayName("writeModel Schema 模式：草稿落库，types / relations / restrictions 多表一致")
    void writeModel_schemaMode_persistsAllTables() {
        String storeId = newStore();

        RestResult<FgaModelVO> result = modelApi.writeModel(storeId, simpleDocumentModel("viewer", "editor"));

        assertThat(result.getCode()).isEqualTo(200);
        FgaModelVO vo = result.getData();
        assertThat(vo).isNotNull();
        assertThat(vo.getModelId()).isNotBlank();
        assertThat(vo.getStatus()).isEqualTo(0);

        Integer modelRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_auth_model WHERE store_id = ? AND model_id = ? AND status = 0",
                Integer.class, storeId, vo.getModelId());
        assertThat(modelRows).isEqualTo(1);

        Integer typeRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_type_definition WHERE store_id = ? AND model_id = ?",
                Integer.class, storeId, vo.getModelId());
        assertThat(typeRows).isGreaterThanOrEqualTo(2);

        Integer relationRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_relation_definition WHERE is_deleted = 0",
                Integer.class);
        assertThat(relationRows).isGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("writeModel DSL 模式：直接 501，不落库")
    void writeModel_dslMode_returns501() {
        String storeId = newStore();

        FgaWriteAuthorizationModelRequest req = FgaWriteAuthorizationModelRequest.builder()
                .dslText("model\n  schema 1.1").build();

        RestResult<FgaModelVO> result = modelApi.writeModel(storeId, req);

        assertThat(result.getCode()).isEqualTo(501);
        Integer rows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_auth_model WHERE store_id = ?",
                Integer.class, storeId);
        assertThat(rows).isEqualTo(0);
    }

    @Test
    @DisplayName("writeModel 对不存在的 Store 抛 ApplicationException（DATA_NOT_EXIST）")
    void writeModel_unknownStore_throws() {
        assertThatThrownBy(() -> modelApi.writeModel("no-such-store", simpleDocumentModel("viewer")))
                .isInstanceOf(ApplicationException.class);
    }

    // ========== publish / activate / deprecate / delete ==========

    @Test
    @DisplayName("完整生命周期：草稿 → 发布 → 激活 → 废弃")
    void fullLifecycle_draftPublishActivateDeprecate() {
        String storeId = newStore();
        String modelId = modelApi.writeModel(storeId, simpleDocumentModelWithCondition("viewer")).getData().getModelId();

        assertThat(modelApi.publishModel(storeId, modelId).getCode()).isEqualTo(200);
        assertThat(queryModelStatus(storeId, modelId)).isEqualTo(1);

        assertThat(modelApi.activateModel(storeId, modelId).getCode()).isEqualTo(200);
        assertThat(queryCurrentModelId(storeId)).isEqualTo(modelId);

        assertThat(modelApi.deprecateModel(storeId, modelId).getCode()).isEqualTo(200);
        assertThat(queryModelStatus(storeId, modelId)).isEqualTo(2);
    }

    @Test
    @DisplayName("publish 再次保存：全量替换结构，不产生重复行或唯一键冲突")
    void publishModel_replacesStructureWithoutDuplicateRows() {
        String storeId = newStore();
        String modelId = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();

        assertThat(modelApi.publishModel(storeId, modelId).getCode()).isEqualTo(200);

        Integer activeTypeRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_type_definition WHERE store_id = ? AND model_id = ? AND is_deleted = 0",
                Integer.class, storeId, modelId);
        Integer documentRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_type_definition WHERE store_id = ? AND model_id = ? AND type = 'document' AND is_deleted = 0",
                Integer.class, storeId, modelId);
        Integer viewerRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_relation_definition r "
                        + "JOIN fga_type_definition t ON r.type_definition_id = t.id "
                        + "WHERE t.store_id = ? AND t.model_id = ? AND t.type = 'document' "
                        + "AND r.relation_name = 'viewer' AND t.is_deleted = 0 AND r.is_deleted = 0",
                Integer.class, storeId, modelId);

        assertThat(activeTypeRows).isEqualTo(2);
        assertThat(documentRows).isEqualTo(1);
        assertThat(viewerRows).isEqualTo(1);
    }

    @Test
    @DisplayName("deleteModel 可以删除草稿，已发布模型会抛 ApplicationException")
    void deleteModel_draftOnly() {
        String storeId = newStore();

        String draftId = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();
        assertThat(modelApi.deleteModel(storeId, draftId).getCode()).isEqualTo(200);

        String publishedId = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();
        modelApi.publishModel(storeId, publishedId);

        assertThatThrownBy(() -> modelApi.deleteModel(storeId, publishedId))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("activateModel 未发布模型抛 ApplicationException")
    void activateModel_draftThrows() {
        String storeId = newStore();
        String modelId = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();

        assertThatThrownBy(() -> modelApi.activateModel(storeId, modelId))
                .isInstanceOf(ApplicationException.class);
    }

    // ========== 读取 ==========

    @Test
    @DisplayName("getCurrentModel：激活后命中当前模型；未激活 store 返回 null")
    void getCurrentModel_flow() {
        String storeId = newStore();
        assertThat(modelApi.getCurrentModel(storeId, "FULL").getData()).isNull();

        String modelId = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();
        modelApi.publishModel(storeId, modelId);
        modelApi.activateModel(storeId, modelId);

        FgaModelVO current = modelApi.getCurrentModel(storeId, "FULL").getData();
        assertThat(current).isNotNull();
        assertThat(current.getModelId()).isEqualTo(modelId);
        assertThat(current.getIsCurrent()).isTrue();
    }

    @Test
    @DisplayName("getModel 返回 types 结构；不存在时抛 ApplicationException")
    void getModel_detailsAndMissing() {
        String storeId = newStore();
        String modelId = modelApi.writeModel(storeId, simpleDocumentModel("viewer", "editor")).getData().getModelId();

        FgaModelVO vo = modelApi.getModel(storeId, modelId, "FULL").getData();
        assertThat(vo.getModelId()).isEqualTo(modelId);
        assertThat(vo.getTypes()).isNotNull();
        assertThat(vo.getTypes()).containsKeys("document", "user");

        assertThatThrownBy(() -> modelApi.getModel(storeId, "missing-id", "FULL"))
                .isInstanceOf(ApplicationException.class);
    }

    @Test
    @DisplayName("listModels 按状态过滤返回所有草稿")
    void listModels_filterByStatus() {
        String storeId = newStore();
        String m1 = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();
        String m2 = modelApi.writeModel(storeId, simpleDocumentModel("viewer")).getData().getModelId();
        modelApi.publishModel(storeId, m2);

        RestResult<PageResponseVO<FgaModelVO>> draftsResult = modelApi.listModels(storeId, 20, null, 0);
        List<FgaModelVO> drafts = draftsResult.getData().getData();
        assertThat(drafts).extracting(FgaModelVO::getModelId).contains(m1).doesNotContain(m2);

        RestResult<PageResponseVO<FgaModelVO>> publishedResult = modelApi.listModels(storeId, 20, null, 1);
        List<FgaModelVO> published = publishedResult.getData().getData();
        assertThat(published).extracting(FgaModelVO::getModelId).contains(m2).doesNotContain(m1);
    }

    // ========== helpers ==========

    private String newStore() {
        FgaCreateStoreRequest req = new FgaCreateStoreRequest();
        req.setName("model-test-" + System.nanoTime());
        FgaStoreVO vo = storeApi.createStore(req).getData();
        return vo.getStoreId();
    }

    private static FgaWriteAuthorizationModelRequest simpleDocumentModel(String... relations) {
        Map<String, FgaRelationSchemaInput> documentRelations = new LinkedHashMap<>();
        List.of(relations).forEach(relation -> documentRelations.put(relation, FgaRelationSchemaInput.builder()
                .rewrite("self")
                .allowedSubjectTypes(List.of(FgaTypeRestrictionInput.builder().type("user").build()))
                .build()));

        Map<String, FgaTypeSchemaInput> types = new LinkedHashMap<>();
        types.put("document", FgaTypeSchemaInput.builder().relations(documentRelations).build());
        types.put("user", FgaTypeSchemaInput.builder()
                .relations(Map.of("self", FgaRelationSchemaInput.builder().rewrite("self").build()))
                .build());

        return FgaWriteAuthorizationModelRequest.builder()
                .schemaVersion("1.1")
                .description("e2e test model")
                .types(types)
                .build();
    }

    private static FgaWriteAuthorizationModelRequest simpleDocumentModelWithCondition(String... relations) {
        FgaWriteAuthorizationModelRequest request = simpleDocumentModel(relations);
        request.setConditions(Map.of("is_working_hours", FgaConditionSchemaInput.builder()
                .expression("request.hour >= params.start_hour && request.hour < params.end_hour")
                .parameterSchema(Map.of("start_hour", "int", "end_hour", "int"))
                .description("工作时间访问")
                .build()));
        return request;
    }

    private Integer queryModelStatus(String storeId, String modelId) {
        return jdbcTemplate.queryForObject(
                "SELECT status FROM fga_auth_model WHERE store_id = ? AND model_id = ?",
                Integer.class, storeId, modelId);
    }

    private String queryCurrentModelId(String storeId) {
        return jdbcTemplate.queryForObject(
                "SELECT current_model_id FROM fga_store WHERE store_id = ?",
                String.class, storeId);
    }
}
