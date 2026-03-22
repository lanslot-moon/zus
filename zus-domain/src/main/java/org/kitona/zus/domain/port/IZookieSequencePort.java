package org.kitona.zus.domain.port;

/**
 * Zookie 序列端口
 *
 * <p>负责生成 store 级别的一致性版本号。
 */
public interface IZookieSequencePort {

    /**
     * 生成下一个 Zookie 版本号
     *
     * @param storeId 存储空间ID
     * @return 新的 Zookie 版本号
     */
    Long nextZookie(String storeId);
}
