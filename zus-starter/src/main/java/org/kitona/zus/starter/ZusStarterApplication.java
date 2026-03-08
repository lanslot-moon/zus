package org.kitona.zus.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Zus 应用启动类
 *
 * <p>组件扫描配置在 spring-context.xml 中定义，
 * 便于集中管理各模块的包扫描路径。
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EnableAsync
@MapperScan("org.kitona.zus.infrastructure.**.mapper")
@ImportResource("classpath:spring-context.xml")
@SpringBootApplication
public class ZusStarterApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZusStarterApplication.class, args);
    }

}
