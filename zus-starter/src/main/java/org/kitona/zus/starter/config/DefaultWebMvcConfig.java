package org.kitona.zus.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class DefaultWebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 注册请求链路拦截器。
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MdcHandlerInterceptor());
    }

    /**
     * 配置跨域策略，便于本地调试页面和外部调用方访问 API。
     */
    @Override
    protected void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 覆盖父类的默认 {@link LocaleResolver}，改为基于 HTTP Accept-Language 头。
     *
     * <p>新增语言只需在 {@code i18n/} 目录下放入对应的
     * {@code messages_<locale>.properties} 文件。
     * 未指定时回退到简体中文。
     */
    @Override
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        return resolver;
    }
}
