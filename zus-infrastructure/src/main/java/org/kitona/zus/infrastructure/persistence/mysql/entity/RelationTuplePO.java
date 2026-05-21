package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 关系元组持久化对象
 * 
 * Tuple 是 ReBAC 的核心数据单元，表示一条具体的权限关系。
 * 结构为 (subject, relation, object)，即"谁对什么资源有什么关系"。
 * 
 * 元组示例：
 * - (user:alice, viewer, document:1) - Alice 是文档1的查看者
 * - (group:engineers#member, editor, folder:src) - 工程师组的成员是src文件夹的编辑者
 * - (folder:projects, parent, document:1) - projects文件夹是文档1的父级
 * 
 * Subject 支持三种格式：
 * 1. 直接用户: user:alice
 * 2. 用户集: group:engineers#member
 * 3. 通配符: user:*
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_relation_tuple")
@Data
@Accessors(chain = true)
public class RelationTuplePO extends BasePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     * 用于数据隔离，关联 fga_store 表
     */
    private String storeId;

    /**
     * 资源对象类型
     * 示例: "document", "folder", "sensor", "order"
     */
    private String objectType;

    /**
     * 资源对象ID
     * 示例: "1", "readme", "temp-001"
     */
    private String objectId;

    /**
     * 关系名称
     * 示例: "viewer", "editor", "owner", "read_data", "manage"
     */
    private String relation;

    /**
     * 主体类型
     * 示例: "user", "group", "operator", "device_group"
     */
    private String subjectType;

    /**
     * 主体ID
     * 示例: "alice", "engineers", "zhang"
     * 通配符时为 "*"
     */
    private String subjectId;

    /**
     * 主体关系（用于用户集）
     * 当 subject 是用户集时使用，如 group:engineers#member 中的 "member"
     * 直接用户时为空
     */
    private String subjectRelation;

    /**
     * 是否通配符主体。
     */
    private Boolean isWildcard;

    /**
     * Zookie 版本号
     * 记录该元组写入时的版本，用于一致性读取
     */
    private Long zookie;

    /**
     * 条件定义ID（可选）
     */
    private Long conditionDefinitionId;

    /**
     * 条件名称快照（可选）
     * 例如 is_internal_network、within_business_hours。
     */
    private String conditionName;

    /**
     * 条件上下文（可选）
     * JSON 字符串形式存储，与 condition_definition_id 配合使用。
     */
    private String conditionContext;

    /**
     * 过期时间（毫秒），null 表示永不过期。
     */
    private Long expiresAt;
}
