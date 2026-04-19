package org.kitona.zus.api.request.common;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * FGA 关系元组过滤键 —— 用于 Read / Expand 等模糊查询
 *
 * <p>与 {@link FgaTupleKeyRequest} 的区别：所有字段均可空，空字段视为“任意匹配”。
 *
 * <p>典型用例：
 * <pre>
 *   只给 object → 查询该资源上的所有元组
 *   给 object + relation → 查询该资源某个关系的所有授权
 *   给 subject → 反向查询某主体的全部权限
 * </pre>
 *
 * <p>注意：查询至少需要 {@code storeId} + {@code object.type} / {@code subject.type} 之一，
 * 具体校验由应用服务层执行以获得更友好的错误信息。
 *
 * @author kitona
 * @since 2026-04-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FgaTupleKeyFilterRequest {

    /**
     * 资源过滤条件（任意字段可空）
     */
    private FgaReferenceFilter object;

    /**
     * 关系名过滤
     */
    @Size(max = 64, message = "relation 长度不能超过 64")
    private String relation;

    /**
     * 主体过滤条件（任意字段可空）
     */
    private FgaReferenceFilter subject;

    /**
     * 引用过滤器：所有字段可空
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FgaReferenceFilter {
        @Size(max = 64, message = "type 长度不能超过 64")
        private String type;

        @Size(max = 128, message = "id 长度不能超过 128")
        private String id;

        /** Subject 过滤时可指定 userset 关系，Object 侧不应填。 */
        @Size(max = 64, message = "relation 长度不能超过 64")
        private String relation;
    }
}
