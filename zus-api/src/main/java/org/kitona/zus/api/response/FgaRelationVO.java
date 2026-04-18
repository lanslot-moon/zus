package org.kitona.zus.api.response;

import lombok.Data;

import java.util.List;

/**
 * FGA 关系定义 VO
 */
@Data
public class FgaRelationVO {

    /**
     * 关系名称，如 viewer、editor、owner
     */
    private String name;

    /**
     * 重写表达式，如 self、self or owner
     */
    private String rewriteExpression;

    /**
     * 关系类型限制列表。
     */
    private List<FgaTypeRestrictionVO> restrictions;
}
