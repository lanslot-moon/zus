package org.kitona.zus.starter.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 项目启动成功后的日志打印
 *
 * @author kitona
 * @version 1.0.0
 * @since 2025-02-06
 */
@Slf4j
@Component
public class ProjectStartupRunner implements CommandLineRunner {

    private final Environment env;

    public ProjectStartupRunner(Environment env) {
        this.env = env;
    }

    @Override
    public void run(String... args) {
        log.info(">>>>>>-------------------- Zus Application started successfully!!! ({} mode) --------------------<<<<<<",
                env.getProperty("spring.profiles.active", "default"));
    }
}
