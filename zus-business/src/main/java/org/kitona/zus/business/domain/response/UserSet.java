package org.kitona.zus.business.domain.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 扩展响应中的用户集合树节点
 */
@Data
@Builder
public class UserSet {
    private String type;               // 节点类型: "leaf" 或 "computed"
    private String name;               // 节点名称
    private List<String> users;        // 叶子节点的用户列表
    private List<UserSet> computed;    // 计算得出的子集
    private UserSet exclude;           // 排除的子集
}