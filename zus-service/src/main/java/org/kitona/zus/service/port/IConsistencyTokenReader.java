package org.kitona.zus.service.port;

/**
 * 应用层一致性 token 读取端口。
 *
 * <p>该端口用于应用服务组装响应 token 和 Explain 旧快照诊断，不属于领域模型规则。
 */
public interface IConsistencyTokenReader {

    /**
     * 读取指定 Store 当前可见的最大 Zookie。
     *
     * @param storeId Store 标识
     * @return 当前最大 Zookie，未产生变更时可返回 null
     */
    Long currentMaxZookie(String storeId);
}
