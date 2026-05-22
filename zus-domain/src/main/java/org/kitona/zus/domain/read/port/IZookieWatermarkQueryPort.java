package org.kitona.zus.domain.read.port;

/**
 * Zookie 读侧水位查询端口。
 *
 * <p>该端口只表达“当前 Store 已经提交到哪个一致性版本”这一读侧快照能力。
 * 它和 {@link IChangelogQueryPort} 分离，避免变更日志列表查询接口混入一致性 token
 * 诊断或响应组装所需的水位读取语义。
 */
public interface IZookieWatermarkQueryPort {

    /**
     * 查询当前 Store 下最大的 zookie。
     *
     * @param storeId 存储空间标识
     * @return 当前最大 zookie，不存在变更时可返回 0
     */
    Long currentMaxZookie(String storeId);
}
