package org.kitona.zus.infrastructure.persistence.mysql.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 授权模型持久化对象
 * 
 * Authorization Model 定义了系统中有哪些资源类型、每种类型有哪些关系、
 * 关系之间如何继承。它是 ReBAC 权限检查的基础。
 * 
 * 授权模型包含：
 * - Type（资源类型）: user, document, folder, sensor 等
 * - Relation（关系）: viewer, editor, owner, read_data 等
 * - Type Restriction（类型限制）: 定义关系的主体类型
 * - Rewrite Expression（重写表达式）: 定义关系的计算规则
 * 
 * 一个 Store 可以有多个版本的授权模型，但同一时间只有一个生效。
 * 该 PO 既可用于完整聚合重建，也可用于读侧列表视图投影。
 * 
 * @author kitona
 * @version 1.0.0
 * @since 2025-01-15
 */
@EqualsAndHashCode(callSuper = true)
@TableName("fga_auth_model")
@Data
@Accessors(chain = true)
public class AuthModelPO extends BasePO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 存储空间ID
     * 关联 fga_store 表
     */
    private String storeId;

    /**
     * 模型唯一标识
     * 由系统生成的 ULID
     * 示例: "01HVMCC2X5HQJT1P5PZ3XJWQ1M"
     */
    private String modelId;

    /**
     * 模型 Schema 版本
     * 用于标识模型格式版本，便于后续升级
     * 示例: "1.0", "1.1"
     */
    private String schemaVersion;

    /**
     * 原始 DSL 文本（可选）
     * 保存用户输入的原始 DSL 定义，便于查看和编辑
     */
    private String dslText;

    /**
     * 模型状态
     * 0: 草稿, 1: 已发布, 2: 已废弃
     */
    private Integer status;

    /**
     * 模型描述
     * 说明该版本模型的变更内容
     */
    private String description;
}
