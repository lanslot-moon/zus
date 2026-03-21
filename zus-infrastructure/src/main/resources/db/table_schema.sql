-- ============================================================
-- FGA 权限库表结构 DDL（工业级增强版：对齐 OpenFGA / Zanzibar）
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
--   3) 写入关系定义：fga_model_relation（每个 type 下 the relation 及 rewrite_expression）
--   4) 写入关系限制：fga_relation_restriction（每个 relation 允许的 user 类型）
--   5) 将 Store 的 current_model_id 设为该模型，后续 Check 使用此模型
--
-- 【三、元组写入规则】
--   - 写元组：按业务事件（如共享、授权、加入组织）调用 Write API，向 fga_relation_tuple 插入一条 (user, relation, object)
--   - 删元组：收回权限或资源删除时调用 Delete API，对 fga_relation_tuple 做逻辑删除并同步 fga_changelog
--   - user、object 仅以 type:id 形式存在于元组中，无需在本库预注册主体或资源
--
-- 【四、业务边界】
--   权限系统职责：提供授权模型管理与 Check/ListObjects 等鉴权 API；不管理资源实体本身。
--   业务服务职责：管理资源生命周期；在资源变更时同步写/删本库元组；鉴权时统一调用本系统 API。
--
-- ============================================================

-- -----------------------------------------------------------
-- 1. 存储空间表 (fga_store)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_store` (
                                           `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法）',
                                           `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间唯一标识',
                                           `name` VARCHAR(128) NOT NULL COMMENT '存储空间名称',
                                           `description` VARCHAR(512) DEFAULT NULL COMMENT '存储空间描述',
                                           `current_model_id` VARCHAR(64) DEFAULT NULL COMMENT '当前使用的授权模型ID(model_id)。原因：模型版本锁定，防止规则变更导致旧元组瞬间失效，实现无损发布或快照回滚',
                                           `current_zookie` BIGINT NOT NULL DEFAULT 0 COMMENT '当前最新的Zookie版本号。原因：解决分布式环境下的一致性延迟，确保权限删除即刻生效',
                                           `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-禁用, 2-删除中',
                                           `tenant_id` VARCHAR(64) DEFAULT NULL COMMENT '租户ID',
                                           `create_time` BIGINT DEFAULT NULL COMMENT '创建时间',
                                           `update_time` BIGINT DEFAULT NULL COMMENT '更新时间',
                                           `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否, 1-是',
                                           PRIMARY KEY (`id`),
                                           UNIQUE KEY `uk_store_id` (`store_id`),
                                           KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA存储空间表';


-- -----------------------------------------------------------
-- 2. 授权模型表 (fga_authorization_model)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_authorization_model` (
                                                         `id` BIGINT NOT NULL COMMENT '主键ID',
                                                         `store_id` VARCHAR(64) NOT NULL COMMENT '存储空间ID',
                                                         `model_id` VARCHAR(64) NOT NULL COMMENT '模型唯一标识（ULID/UUID）',
                                                         `schema_version` VARCHAR(16) NOT NULL DEFAULT '1.1' COMMENT '模型Schema版本',
                                                         `dsl_text` TEXT DEFAULT NULL COMMENT '原始DSL文本',
                                                         `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已废弃',
                                                         `description` VARCHAR(512) DEFAULT NULL,
                                                         `tenant_id` VARCHAR(64) DEFAULT NULL,
                                                         `create_time` BIGINT DEFAULT NULL,
                                                         `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
                                                         PRIMARY KEY (`id`),
                                                         UNIQUE KEY `uk_store_model` (`store_id`, `model_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA授权模型表';


-- -----------------------------------------------------------
-- 3. 类型 definition 表 (fga_type_definition)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_type_definition` (
                                                     `id` BIGINT NOT NULL COMMENT '主键ID',
                                                     `store_id` VARCHAR(64) NOT NULL,
                                                     `model_id` VARCHAR(64) NOT NULL,
                                                     `type` VARCHAR(64) NOT NULL COMMENT '类型名（如: document, folder, user）',
                                                     `sort_order` INT NOT NULL DEFAULT 0,
                                                     `tenant_id` VARCHAR(64) DEFAULT NULL,
                                                     `create_time` BIGINT DEFAULT NULL,
                                                     `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
                                                     PRIMARY KEY (`id`),
                                                     KEY `idx_model_type` (`store_id`, `model_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA类型定义表';


-- -----------------------------------------------------------
-- 4. 关系 definition 表 (fga_model_relation)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_model_relation` (
                                                    `id` BIGINT NOT NULL COMMENT '主键ID',
                                                    `type_definition_id` BIGINT NOT NULL COMMENT '类型定义ID',
                                                    `relation_name` VARCHAR(64) NOT NULL COMMENT '关系名（如: viewer, editor, owner）',
                                                    `rewrite_expression` VARCHAR(512) NOT NULL COMMENT '重写表达式（如: self, self or owner）。定义了权限如何进行逻辑计算与继承',
                                                    `create_time` BIGINT DEFAULT NULL,
                                                    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
                                                    PRIMARY KEY (`id`),
                                                    UNIQUE KEY `uk_type_rel` (`type_definition_id`, `relation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系定义表';


-- -----------------------------------------------------------
-- 5. 关系类型限制表 (fga_relation_restriction)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_restriction` (
                                                          `id` BIGINT NOT NULL COMMENT '主键ID',
                                                          `relation_definition_id` BIGINT NOT NULL COMMENT '关系定义ID',
                                                          `allowed_type` VARCHAR(64) NOT NULL COMMENT '允许的主体类型（如: user, group）',
                                                          `allowed_subject_relation` VARCHAR(64) DEFAULT NULL COMMENT '允许的主体关系。原因：支撑 Userset(集主体)，如允许 group#member。支持前端“主体类型#关系”的输入，实现层级继承',
                                                          `create_time` BIGINT DEFAULT NULL,
                                                          `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
                                                          PRIMARY KEY (`id`),
                                                          UNIQUE KEY `uk_rel_type_subject` (`relation_definition_id`, `allowed_type`, `allowed_subject_relation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系类型限制表';


-- -----------------------------------------------------------
-- 6. 关系元组表 (fga_relation_tuple)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_tuple` (
                                                    `id` BIGINT NOT NULL COMMENT '主键ID（雪花算法，天然具备时间序，利于作为 zookie 基础）',
                                                    `store_id` VARCHAR(64) NOT NULL,
                                                    `object_type` VARCHAR(64) NOT NULL COMMENT 'object 类型',
                                                    `object_id` VARCHAR(128) NOT NULL COMMENT 'object 标识',
                                                    `relation` VARCHAR(64) NOT NULL COMMENT '关系名称',
                                                    `subject_type` VARCHAR(64) NOT NULL COMMENT 'user 类型',
                                                    `subject_id` VARCHAR(128) NOT NULL COMMENT 'user 标识',
                                                    `subject_relation` VARCHAR(64) DEFAULT NULL COMMENT 'userset 时使用，如 member',
                                                    `condition_name` VARCHAR(64) DEFAULT NULL COMMENT '关联的 ABAC 条件名称。原因：支撑关系模型下的环境/属性约束',
                                                    `condition_context` JSON DEFAULT NULL COMMENT '条件的上下文数据。原因：实现动态授权逻辑（如 IP 白名单、时间限制），由鉴权引擎动态计算',
                                                    `zookie` BIGINT NOT NULL COMMENT 'Zookie版本号。用于解决分布式环境下主从同步延迟导致的安全风险',
                                                    `tenant_id` VARCHAR(64) DEFAULT NULL,
                                                    `create_time` BIGINT DEFAULT NULL,
                                                    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0,
                                                    PRIMARY KEY (`id`),
                                                    UNIQUE KEY `uk_tuple` (`store_id`, `object_type`, `object_id`, `relation`, `subject_type`, `subject_id`, `subject_relation`),
                                                    KEY `idx_object_relation` (`store_id`, `object_type`, `object_id`, `relation`),
                                                    KEY `idx_subject` (`store_id`, `subject_type`, `subject_id`, `subject_relation`),
                                                    KEY `idx_zookie` (`store_id`, `zookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA关系元组表';


-- -----------------------------------------------------------
-- 7. 变更日志表 (fga_changelog)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_changelog` (
                                               `id` BIGINT NOT NULL COMMENT '主键ID',
                                               `store_id` VARCHAR(64) NOT NULL,
                                               `zookie` BIGINT NOT NULL COMMENT '版本令牌。审计与 Watch API 使用',
                                               `operation` VARCHAR(16) NOT NULL COMMENT '操作类型: WRITE, DELETE',
                                               `object_type` VARCHAR(64) NOT NULL,
                                               `object_id` VARCHAR(128) NOT NULL,
                                               `relation` VARCHAR(64) NOT NULL,
                                               `subject_type` VARCHAR(64) NOT NULL,
                                               `subject_id` VARCHAR(128) NOT NULL,
                                               `subject_relation` VARCHAR(64) DEFAULT NULL,
                                               `operation_time` BIGINT NOT NULL COMMENT '毫秒级时间戳',
                                               PRIMARY KEY (`id`),
                                               KEY `idx_store_zookie` (`store_id`, `zookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='FGA变更日志表';


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

-- ==================================================================
-- 补充说明详细原因及业务价值
-- ==================================================================
--
-- A. allowed_subject_relation (表 5) —— 支撑层级继承与 Userset
-- 原因：在 ReBAC 中，权限往往授予给“一组人”。例如 document:viewer 的主体不是具体的 user:alice，而是 folder:A#viewer。
-- 业务价值：这允许你实现 “父子继承”。如果没有这个字段，你只能在表 5 定义“允许 folder 类型”，但无法指定“允许 folder 的 viewer 关系”。
--          增加此字段后，你的前端界面中的“主体类型”就能支持 类型#关系 的输入。
-- 逻辑对应：对应 OpenFGA DSL 中的 define viewer: [user, group#member]。
--
-- B. condition_name & condition_context (表 6) —— 支撑 ABAC 混合模式
-- 原因：纯关系模型无法处理“环境/属性约束”。
-- 业务价值：例如：editor 权限仅在 request_client_ip 属于公司内网时生效。
-- 实现逻辑：写入元组时，你可以绑定一个预定义的 condition（如 is_internal_network），并存入该元组特有的上下文数据（如 IP 白名单）。
--          鉴权时，引擎会动态计算该 JSON 表达式。
--
-- C. zookie (表 1 & 表 6 & 表 7) —— 解决“新瓶装旧酒”安全问题
-- 已存在于你的 DDL 中，但需注意其逻辑用途：
-- 原因：分布式环境下，数据库主从同步可能有几百毫秒延迟。
-- 业务价值：如果管理员刚删除了 A 的权限（产生 zookie: 100），A 立即发起访问。请求带上最新的 zookie，
--          鉴权引擎会强制检查缓存/从库的版本是否 >= 100。如果不是，则等待或读主库，确保 “权限删除即刻生效”，防止越权。
--
-- D. current_model_id (表 1) —— 模型版本锁定
-- 原因：权限模型（DSL）是元组的解析引擎。
-- 业务价值：如果你在 viewer 的定义里删除了 self 逻辑，那么所有现存的 viewer 元组都会瞬间失效。
--          通过在 Store 层面锁定 current_model_id，你可以先发布新模型，测试无误后再通过修改 Store 表一键切换，实现 “无损发布” 或 “快速回滚”。
--
-- ==================================================================