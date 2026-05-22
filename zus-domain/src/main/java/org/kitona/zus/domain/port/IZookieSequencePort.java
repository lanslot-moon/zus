package org.kitona.zus.domain.port;

/**
 * Store 级 Zookie 序列端口。
 *
 * <p>Zookie 是 RelationTuple 写入事实的一致性版本，属于授权领域语言。
 * 该端口只表达“为某个 Store 生成下一版本”的领域能力，不关心数据库自增、
 * Redis 计数器或其他技术实现。
 *
 * <p>当前调用方是应用层 tuple 写入用例协调器，而不是领域服务本身。
 * 这样可以保持领域服务只构造变更事实，事务、持久化和版本分配由应用层编排。
 */
public interface IZookieSequencePort {

    /**
     * 生成下一个 Zookie 版本号。
     *
     * @param storeId 存储空间 ID
     * @return 新的 Zookie 版本号
     */
    Long nextZookie(String storeId);
}
