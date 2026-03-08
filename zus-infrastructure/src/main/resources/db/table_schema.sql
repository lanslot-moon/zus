-- ============================================================
-- FGA 权限库表结构 DDL（对齐 OpenFGA / Zanzibar）
-- ============================================================
--
-- 本库支撑权限系统的两类能力：
-- 【权限管理】写/删关系元组（授予、收回权限）、写/更新授权模型
-- 【权限验证】Check、ListObjects、ListUsers（基于当前模型 + 元组计算）
--
-- 约定：无主体表/资源表，user 与 object 仅以 type:id 形式存在于元组中；
-- 资源生命周期由业务服务管理，业务在资源变更时同步写/删本库元组。
--
-- 执行顺序：按表序号 1～7 依次执行即可（表间无外键依赖）。
-- ============================================================

-- -----------------------------------------------------------
-- 1. 存储空间表 (fga_store)
-- 管理：创建/更新 Store，设置 current_model_id
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_store` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间唯一标识',
    `name` VARCHAR(128) NOT NULL COMMENT '存储空间名称',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '存储空间描述',
    `current_model_id` VARCHAR(64) DEFAULT NULL COMMENT '当前使用的授权模型ID',
    `current_zookie` BIGINT NOT NULL DEFAULT 0 COMMENT '当前最新的Zookie版本号',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-禁用, 2-删除中',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `updated_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_store_id` (`store_id`),
    KEY `idx_tenant_id` (`tenant_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA存储空间表';


-- -----------------------------------------------------------
-- 2. 授权模型表 (fga_authorization_model)
-- 管理：创建/发布/废弃模型；类型定义由 3～5 表组装（Service 层）
-- 验证：Check/ListObjects 等依赖当前模型计算关系
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_authorization_model` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间ID',
    `model_id` VARCHAR(64) NOT NULL COMMENT '模型唯一标识（ULID）',
    `schema_version` VARCHAR(16) NOT NULL DEFAULT '1.1' COMMENT '模型Schema版本',
    `dsl_text` TEXT DEFAULT NULL COMMENT '原始DSL文本',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已废弃',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '模型描述',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `updated_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_store_model` (`store_id`, `model_id`),
    KEY `idx_store_id` (`store_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA授权模型表';


-- -----------------------------------------------------------
-- 3. 类型定义表 (fga_type_definition)
-- 管理：写授权模型时写入；type 如 document、folder、user
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_type_definition` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间ID',
    `model_id` VARCHAR(64) NOT NULL COMMENT '授权模型ID',
    `type` VARCHAR(64) NOT NULL COMMENT '类型名（如: document, folder, user）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_model` (`store_id`, `model_id`),
    KEY `idx_model_type` (`store_id`, `model_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA类型定义表';


-- -----------------------------------------------------------
-- 4. 关系定义表 (fga_model_relation)
-- 管理：写授权模型时写入；每个 type 下的 relation 及重写表达式
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_model_relation` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `type_definition_id` BIGINT NOT NULL COMMENT '类型定义ID',
    `relation_name` VARCHAR(64) NOT NULL COMMENT '关系名（如: viewer, editor, owner）',
    `rewrite_expression` VARCHAR(512) NOT NULL COMMENT '重写表达式（如: self, self or owner）',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_rel` (`type_definition_id`, `relation_name`),
    KEY `idx_type_definition_id` (`type_definition_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系定义表';


-- -----------------------------------------------------------
-- 5. 关系类型限制表 (fga_relation_restriction)
-- 管理：写授权模型时写入；每个 relation 允许的 user 类型
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_restriction` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `relation_definition_id` BIGINT NOT NULL COMMENT '关系定义ID（fga_model_relation.id）',
    `allowed_type` VARCHAR(64) NOT NULL COMMENT '允许的主体类型（如: user, folder）',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_relation_def` (`relation_definition_id`),
    UNIQUE KEY `uk_rel_type` (`relation_definition_id`, `allowed_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系类型限制表';


-- -----------------------------------------------------------
-- 6. 关系元组表 (fga_relation_tuple)
-- 管理：Write/Delete API 写、删元组（授予/收回权限）
-- 验证：Check/ListObjects/ListUsers 读取并参与计算
-- user = subject_type:subject_id（可选 #subject_relation）；object = object_type:object_id
-- 资源在业务侧删除或变更时，由业务调用 Delete/Write 同步本表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_tuple` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间ID',
    `object_type` VARCHAR(64) NOT NULL COMMENT 'object 类型，如 document、folder',
    `object_id` VARCHAR(128) NOT NULL COMMENT 'object 标识，object = object_type:object_id',
    `relation` VARCHAR(64) NOT NULL COMMENT '关系名称（如: viewer, editor, owner）',
    `subject_type` VARCHAR(64) NOT NULL COMMENT 'user 类型，如 user、group',
    `subject_id` VARCHAR(128) NOT NULL COMMENT 'user 标识（通配符时为 *），user = subject_type:subject_id',
    `subject_relation` VARCHAR(64) DEFAULT NULL COMMENT 'userset 时使用，如 member，即 subject_type:subject_id#subject_relation',
    `zookie` BIGINT NOT NULL COMMENT 'Zookie版本号',
    `condition_expression` JSON DEFAULT NULL COMMENT '条件表达式（可选）',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `updated_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_tuple` (`store_id`, `object_type`, `object_id`, `relation`, `subject_type`, `subject_id`, `subject_relation`),
    KEY `idx_object_relation` (`store_id`, `object_type`, `object_id`, `relation`),
    KEY `idx_subject` (`store_id`, `subject_type`, `subject_id`, `subject_relation`),
    KEY `idx_zookie` (`store_id`, `zookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系元组表';


-- -----------------------------------------------------------
-- 7. 变更日志表 (fga_changelog)
-- 管理：Write/Delete 元组时写入；用于 Watch、一致性读取与审计
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_changelog` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间ID',
    `zookie` BIGINT NOT NULL COMMENT 'Zookie版本号',
    `operation` VARCHAR(16) NOT NULL COMMENT '操作类型: WRITE, DELETE',
    `object_type` VARCHAR(64) NOT NULL COMMENT '资源对象类型',
    `object_id` VARCHAR(128) NOT NULL COMMENT '资源对象ID',
    `relation` VARCHAR(64) NOT NULL COMMENT '关系名称',
    `subject_type` VARCHAR(64) NOT NULL COMMENT '主体类型',
    `subject_id` VARCHAR(128) NOT NULL COMMENT '主体ID',
    `subject_relation` VARCHAR(64) DEFAULT NULL COMMENT '主体关系',
    `operation_time` BIGINT NOT NULL COMMENT '操作时间戳（毫秒）',
    `operator_id` VARCHAR(64) DEFAULT NULL COMMENT '操作人ID',
    `trace_id` VARCHAR(64) DEFAULT NULL COMMENT '请求追踪ID',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `updated_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    KEY `idx_store_zookie` (`store_id`, `zookie`),
    KEY `idx_operation_time` (`store_id`, `operation_time`),
    KEY `idx_trace_id` (`trace_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA变更日志表';


-- -----------------------------------------------------------
-- 8. 用户信息表 (user_info)（可选）
-- 系统内部缓存的用户基础信息，可与外部用户中心同步；若仅用外部接口可不必建表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user_info` (
    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
    `user_id` VARCHAR(64) NOT NULL COMMENT '用户唯一标识（与外部用户中心对应）',
    `user_name` VARCHAR(128) DEFAULT NULL COMMENT '用户名称',
    `user_password` VARCHAR(256) DEFAULT NULL COMMENT '密码（加密存储）',
    `user_role` VARCHAR(64) DEFAULT NULL COMMENT '角色',
    `user_status` VARCHAR(32) DEFAULT NULL COMMENT '状态',
    `user_phone` VARCHAR(32) DEFAULT NULL COMMENT '手机号',
    `user_email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    `user_address` VARCHAR(256) DEFAULT NULL COMMENT '地址',
    `user_avatar` VARCHAR(512) DEFAULT NULL COMMENT '头像URL',
    `user_create_time` VARCHAR(32) DEFAULT NULL COMMENT '用户创建时间',
    `user_update_time` VARCHAR(32) DEFAULT NULL COMMENT '用户更新时间',
    `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
    `created_by` VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
    `updated_by` VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
    `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息缓存表';


-- ============================================================
-- 规则与业务边界
-- ============================================================
--
-- 【一、模型与元组的语义】
--   - 模型表（fga_authorization_model + fga_type_definition + fga_model_relation + fga_relation_restriction）
--     描述的是「授权模型的关系图」：有哪些类型、每种类型有哪些关系、关系如何计算、允许谁与谁建立关系。
--     相当于图的「结构/规则」，不存储具体权限数据。
--   - 元组表（fga_relation_tuple）存储的是「关系实例」：具体哪个人对哪个对象具有哪条关系。
--     一条元组即关系图上的一条边；Check/ListObjects 时用模型规则对元组进行推导计算。
--
-- 【二、模型搭建顺序（仅建库/建模型时遵守）】
--   1) 创建 Store (fga_store)，并可选先占位一条 fga_authorization_model 记录（得到 model_id）
--   2) 写入类型：fga_type_definition（如 document、folder、user）
--   3) 写入关系定义：fga_model_relation（每个 type 下的 relation 及 rewrite_expression）
--   4) 写入关系限制：fga_relation_restriction（每个 relation 允许的 user 类型）
--   5) 将 Store 的 current_model_id 设为该模型，后续 Check 使用此模型
--   元组与模型独立：不是把上述表「组合」后写入元组；元组按业务事件逐条写入。
--
-- 【三、元组写入规则】
--   - 写元组：按业务事件（如共享、授权、加入组织）调用 Write API，向 fga_relation_tuple 插入一条 (user, relation, object)
--   - 删元组：收回权限或资源删除时调用 Delete API，对 fga_relation_tuple 做逻辑删除并同步 fga_changelog
--   - user、object 仅以 type:id 形式存在于元组中，无需在本库预注册主体或资源
--
-- 【四、业务边界】
--   权限系统职责：
--     - 提供授权模型与元组的写入/删除/查询 API（权限管理）
--     - 提供 Check、ListObjects、ListUsers 等鉴权 API（权限验证）
--     - 不管理资源实体本身（无资源表）；不要求主体/资源预注册
--   业务服务职责：
--     - 资源的创建、更新、删除、元数据由业务服务管理
--     - 资源生命周期变化时（如删除、转让），由业务调用本系统 Write/Delete 同步元组
--     - 鉴权时统一调用本系统 Check（或 ListObjects/ListUsers），不在业务侧重复实现权限计算
--
-- ============================================================
-- 数据流说明
-- ============================================================
--
-- 【权限管理】由本系统提供 API，业务或管控端调用：
--   1) 创建/更新 Store (fga_store)，设置 current_model_id
--   2) 写入/更新授权模型：fga_authorization_model + fga_type_definition
--      + fga_model_relation + fga_relation_restriction
--   3) 写元组 (Write)：向 fga_relation_tuple 插入，同步写 fga_changelog
--   4) 删元组 (Delete)：fga_relation_tuple 逻辑删除，同步写 fga_changelog
--
-- 【权限验证】由本系统提供 API，业务在鉴权时调用：
--   - Check：当前模型 + fga_relation_tuple 计算 user 是否对 object 有 relation
--   - ListObjects：当前模型 + 元组，查 user 对某 type 有某 relation 的 object 列表
--   - ListUsers：当前模型 + 元组，查对某 object 有某 relation 的 user 列表
--
-- 【业务侧职责】
--   - 资源管理在业务服务；资源创建/共享/删除时调用本系统 Write/Delete 同步元组
--   - 鉴权时调用本系统 Check（或 ListObjects/ListUsers），不自行计算权限
--
