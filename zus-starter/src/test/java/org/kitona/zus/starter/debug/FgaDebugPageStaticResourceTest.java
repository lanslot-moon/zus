package org.kitona.zus.starter.debug;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    @DisplayName("index.html 存在并引用 rebac-web 构建产物")
    void debugPage_existsAndContainsCoreDebugActions() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/index.html");

        assertThat(resource.exists()).isTrue();
        String html = resource.getContentAsString(StandardCharsets.UTF_8);

        assertThat(html)
                .contains("ZUS Storage ReBAC")
                .contains("id=\"root\"")
                .contains("type=\"module\"")
                .contains("/assets/index-");

        ClassPathResource script = new ClassPathResource("static/" + findAssetPath(html, "js"));
        assertThat(script.exists()).isTrue();
        String javascript = script.getContentAsString(StandardCharsets.UTF_8);

        assertThat(javascript)
                .contains("存储空间")
                .contains("模型版本")
                .contains("关系建模")
                .contains("授权管理")
                .contains("/fga/stores");

        ClassPathResource stylesheet = new ClassPathResource("static/" + findAssetPath(html, "css"));
        assertThat(stylesheet.exists()).isTrue();
    }

    /**
     * 从 Vite 入口 HTML 中解析构建后的资源路径。
     *
     * @param html      入口 HTML
     * @param extension 资源扩展名
     * @return classpath static 下的资源相对路径
     */
    private static String findAssetPath(String html, String extension) {
        Pattern pattern = Pattern.compile("/(assets/index-[^\"]+\\." + extension + ")");
        Matcher matcher = pattern.matcher(html);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }
}
