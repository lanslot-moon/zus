package org.kitona.zus.service.port.impl;

import org.apache.commons.lang3.StringUtils;
import org.kitona.zus.domain.read.port.IZookieWatermarkQueryPort;
import org.kitona.zus.service.port.IConsistencyTokenReader;
import org.springframework.stereotype.Component;

/**
 * 基于变更日志读侧水位的一致性 token 读取器。
 *
 * <p>该类位于应用层，是对 read port 的轻量组合：基础设施只实现
 * {@link IZookieWatermarkQueryPort}，不需要反向依赖 service 端口。
 */
@Component
public class ChangelogConsistencyTokenReader implements IConsistencyTokenReader {

    /**
     * Zookie 读侧水位查询端口。
     */
    private final IZookieWatermarkQueryPort zookieWatermarkQueryPort;

    /**
     * 创建 ChangelogConsistencyTokenReader 实例。
     *
     * @param zookieWatermarkQueryPort zookieWatermarkQueryPort 参数
     */
    public ChangelogConsistencyTokenReader(IZookieWatermarkQueryPort zookieWatermarkQueryPort) {
        this.zookieWatermarkQueryPort = zookieWatermarkQueryPort;
    }

    /**
     * 读取current max zookie。
     *
     * @param storeId Store 标识
     * @return 返回结果
     */
    @Override
    public Long currentMaxZookie(String storeId) {
        if (StringUtils.isBlank(storeId)) {
            return 0L;
        }
        Long maxZookie = zookieWatermarkQueryPort.currentMaxZookie(storeId);
        return maxZookie == null ? 0L : maxZookie;
    }
}
