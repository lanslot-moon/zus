package org.kitona.zus.starter.controller.support;

import org.junit.jupiter.api.BeforeEach;
import org.kitona.zus.api.sse.SseConnectionManager;
import org.kitona.zus.service.application.IAuthorizationModelApplicationService;
import org.kitona.zus.service.application.IAuthorizationReadApplicationService;
import org.kitona.zus.service.application.IPermissionCheckApplicationService;
import org.kitona.zus.service.application.IStoreApplicationService;
import org.kitona.zus.service.application.ITupleMutationApplicationService;
import org.kitona.zus.service.application.ITupleWatchApplicationService;
import org.kitona.zus.starter.ZusStarterApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

/**
 * Controller 层端到端真实链路测试基类。
 *
 * <p>设计目标：
 * <ul>
 *   <li>启动完整 Spring 上下文，controller / application service / domain / infrastructure 全部使用真实 Bean；</li>
 *   <li>持久层走 classpath 下的 H2（MySQL 兼容模式），{@code schema.sql} 自动建表；</li>
 *   <li>缓存走 {@code fga.cache.type=local} 的本地实现，避免真实 Redis 依赖；</li>
 *   <li>每个测试用例运行前将所有业务表清空，保证测试隔离，测试数据由每个测试自行准备。</li>
 * </ul>
 *
 * <p>子类通过 {@link Autowired} 注入需要的 controller 接口及 Mapper/JdbcTemplate 来验证真实落库结果。
 *
 * @author kitona
 * @since 2026-04-18
 */
@SpringBootTest(classes = ZusStarterApplication.class)
@ActiveProfiles("test")
public abstract class AbstractControllerTest {

    /**
     * 所有业务表列表（清库顺序不敏感，因为 H2 未配置外键）。
     */
    private static final List<String> TABLES = List.of(
            "fga_store",
            "fga_auth_model",
            "fga_type_definition",
            "fga_relation_definition",
            "fga_type_restriction",
            "fga_condition_definition",
            "fga_relation_tuple",
            "fga_tuple_changelog"
    );

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected IStoreApplicationService storeApplicationService;

    @Autowired
    protected IAuthorizationModelApplicationService authorizationModelApplicationService;

    @Autowired
    protected IPermissionCheckApplicationService permissionCheckApplicationService;

    @Autowired
    protected IAuthorizationReadApplicationService authorizationReadApplicationService;

    @Autowired
    protected ITupleMutationApplicationService tupleMutationApplicationService;

    @Autowired
    protected ITupleWatchApplicationService tupleWatchApplicationService;

    @Autowired
    protected SseConnectionManager sseConnectionManager;

    @BeforeEach
    void cleanDatabase() {
        for (String table : TABLES) {
            jdbcTemplate.execute("DELETE FROM " + table);
        }
    }
}
