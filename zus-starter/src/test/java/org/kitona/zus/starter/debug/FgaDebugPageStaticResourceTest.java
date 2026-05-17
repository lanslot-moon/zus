package org.kitona.zus.starter.debug;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FGA 调试台静态资源测试。
 *
 * <p>调试台以 Spring Boot 静态资源方式交付，不进入 API 模块测试目录。
 * 该测试用于确保 starter 打包后仍然能够暴露调试页面入口。
 */
@DisplayName("FGA 调试台静态资源测试")
class FgaDebugPageStaticResourceTest {

    /**
     * 验证调试台入口 HTML 存在，并包含核心联调能力入口。
     *
     * @throws IOException 读取 classpath 资源失败时抛出
     */
    @Test
    @DisplayName("fga-debug/index.html 存在并包含 Store、Model、Tuple、Check 入口")
    void debugPage_existsAndContainsCoreDebugActions() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/fga-debug/index.html");

        assertThat(resource.exists()).isTrue();
        String html = resource.getContentAsString(StandardCharsets.UTF_8);

        assertThat(html)
                .contains("ZUS FGA 调试台")
                .contains("创建 Store")
                .contains("构建 Model")
                .contains("写入关系 Tuple")
                .contains("Check 与 Explain")
                .contains("http://localhost:8091")
                .contains("function applyDefaultBaseUrl()")
                .contains("function normalizeBaseUrl(rawBaseUrl)")
                .contains("function writeModel()")
                .contains("function checkPermission()");
    }
}
