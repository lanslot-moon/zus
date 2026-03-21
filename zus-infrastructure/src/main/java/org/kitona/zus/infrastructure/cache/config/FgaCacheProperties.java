package org.kitona.zus.infrastructure.cache.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * FGA 缓存配置属性
 *
 * @author kitona
 */
@Data
@Component
@ConfigurationProperties(prefix = "fga.cache")
public class FgaCacheProperties {

    /**
     * 缓存类型
     * - redis: Redis + Caffeine (默认)
     * - local: 仅 Caffeine
     */
    private String type = "redis";

}
