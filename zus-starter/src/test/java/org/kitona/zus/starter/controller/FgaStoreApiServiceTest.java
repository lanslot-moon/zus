package org.kitona.zus.starter.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kitona.zus.api.controller.IFgaStoreApiService;
import org.kitona.zus.api.request.FgaCreateStoreRequest;
import org.kitona.zus.api.response.FgaStoreVO;
import org.kitona.zus.api.response.PageResponseVO;
import org.kitona.zus.api.response.RestResult;
import org.kitona.zus.common.exception.SystemException;
import org.kitona.zus.starter.controller.support.AbstractControllerTest;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * {@link IFgaStoreApiService} 端到端真实链路测试。
 *
 * <p>所有测试都在真实 {@code StoreApplicationService + H2} 基础上执行，
 * 不再 Mock 应用服务；断言通过接口结果 + JdbcTemplate 直查 {@code fga_store} 双向验证。
 */
@DisplayName("FGA Store API 端到端测试")
class FgaStoreApiServiceTest extends AbstractControllerTest {

    @Autowired
    private IFgaStoreApiService storeApi;

    // ========== createStore ==========

    @Test
    @DisplayName("createStore 创建 Store 写入 H2，接口返回 VO 可被再次 getStore 命中")
    void createStore_persistsAndReturnsVO() {
        FgaCreateStoreRequest request = new FgaCreateStoreRequest();
        request.setName("demo-store");
        request.setDescription("demo desc");

        RestResult<FgaStoreVO> result = storeApi.createStore(request);

        assertThat(result.getCode()).isEqualTo(200);
        FgaStoreVO vo = result.getData();
        assertThat(vo).isNotNull();
        assertThat(vo.getStoreId()).isNotBlank();
        assertThat(vo.getName()).isEqualTo("demo-store");
        assertThat(vo.getDescription()).isEqualTo("demo desc");
        assertThat(vo.getStatus()).isEqualTo(0);

        Integer rows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_store WHERE store_id = ? AND is_deleted = 0",
                Integer.class, vo.getStoreId());
        assertThat(rows).isEqualTo(1);

        FgaStoreVO found = storeApi.getStore(vo.getStoreId()).getData();
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("demo-store");
    }

    // ========== getStore ==========

    @Test
    @DisplayName("getStore Store 不存在 -> 返回 200 + data=null")
    void getStore_notFound() {
        RestResult<FgaStoreVO> result = storeApi.getStore("missing-store-id");

        assertThat(result.getCode()).isEqualTo(200);
        assertThat(result.getData()).isNull();
    }

    // ========== listStores ==========

    @Test
    @DisplayName("listStores 返回所有已创建的 Store")
    void listStores_returnsCreatedStores() {
        FgaStoreVO s1 = createStore("list-s1", null);
        FgaStoreVO s2 = createStore("list-s2", null);

        RestResult<PageResponseVO<FgaStoreVO>> result = storeApi.listStores(20, null);

        assertThat(result.getCode()).isEqualTo(200);
        PageResponseVO<FgaStoreVO> page = result.getData();
        assertThat(page.getData())
                .extracting(FgaStoreVO::getStoreId)
                .contains(s1.getStoreId(), s2.getStoreId());
    }

    // ========== enable / disable ==========

    @Test
    @DisplayName("disableStore 将 status 置 1，enableStore 复位到 0")
    void disable_then_enable_transitions() {
        FgaStoreVO store = createStore("disable-test", null);

        assertThat(storeApi.disableStore(store.getStoreId()).getCode()).isEqualTo(200);
        assertThat(queryStatus(store.getStoreId())).isEqualTo(1);

        assertThat(storeApi.enableStore(store.getStoreId()).getCode()).isEqualTo(200);
        assertThat(queryStatus(store.getStoreId())).isEqualTo(0);
    }

    @Test
    @DisplayName("enable/disable 对不存在的 Store -> 返回 500 + 失败原因")
    void enable_disable_unknownStore() {
        RestResult<Void> enableMiss = storeApi.enableStore("nope");
        assertThat(enableMiss.getCode()).isEqualTo(500);
        assertThat(enableMiss.getMessage()).contains("启用失败");

        RestResult<Void> disableMiss = storeApi.disableStore("nope");
        assertThat(disableMiss.getCode()).isEqualTo(500);
        assertThat(disableMiss.getMessage()).contains("禁用失败");
    }

    // ========== deleteStore ==========

    @Test
    @DisplayName("deleteStore 先禁用再删除，逻辑删除后 is_deleted=1")
    void deleteStore_afterDisable() {
        FgaStoreVO store = createStore("delete-test", null);
        storeApi.disableStore(store.getStoreId());

        RestResult<Void> deleted = storeApi.deleteStore(store.getStoreId());
        assertThat(deleted.getCode()).isEqualTo(200);

        Integer aliveRows = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM fga_store WHERE store_id = ? AND is_deleted = 0",
                Integer.class, store.getStoreId());
        assertThat(aliveRows).isEqualTo(0);

        assertThat(storeApi.getStore(store.getStoreId()).getData()).isNull();
    }

    @Test
    @DisplayName("deleteStore 对未知 storeId -> 返回 500")
    void deleteStore_unknownStore_returns500() {
        RestResult<Void> missing = storeApi.deleteStore("unknown-id");

        assertThat(missing.getCode()).isEqualTo(500);
        assertThat(missing.getMessage()).contains("删除失败");
    }

    @Test
    @DisplayName("deleteStore 对未禁用 Store 直接抛 SystemException（由聚合不变量守护）")
    void deleteStore_activeStoreThrows() {
        FgaStoreVO active = createStore("active-store", null);

        assertThatThrownBy(() -> storeApi.deleteStore(active.getStoreId()))
                .isInstanceOf(SystemException.class)
                .hasMessageContaining("only DISABLE status can be deleted");
    }

    // ========== helpers ==========

    private FgaStoreVO createStore(String name, String description) {
        FgaCreateStoreRequest request = new FgaCreateStoreRequest();
        request.setName(name);
        request.setDescription(description);
        return storeApi.createStore(request).getData();
    }

    private Integer queryStatus(String storeId) {
        return jdbcTemplate.queryForObject(
                "SELECT status FROM fga_store WHERE store_id = ?", Integer.class, storeId);
    }
}
