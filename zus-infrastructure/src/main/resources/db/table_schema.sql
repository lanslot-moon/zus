-- ============================================================
-- ZUS 权限库表结构 DDL（8 张核心表，对齐 OpenFGA / Zanzibar 能力集）
-- ============================================================
--
-- 表名规范：fga_ 前缀（Fine-Grained Authorization）+ 领域概念
--   fga_store                → 存储空间（多租户隔离单元）
--   fga_auth_model           → 授权模型（DSL 版本化管理）
--   fga_type_definition      → 类型定义（model 内的 type）
--   fga_relation_definition  → 关系定义（type 内的 relation + rewrite rule）
--   fga_type_restriction     → 类型限制（relation 允许的 subject 类型）
--   fga_condition_definition → 条件定义（ABAC 混合模式的 CEL 表达式）
--   fga_relation_tuple       → 关系元组（核心数据 — 权限实例）
--   fga_tuple_changelog      → 元组变更日志（审计 + Watch API）
--
-- 【命名优化说明】
--   旧名 fga_authorization_model  → fga_auth_model（缩短，auth 在权限领域无歧义）
--   旧名 fga_model_relation       → fga_relation_definition（对齐领域概念 RelationDefinition）
--   旧名 fga_relation_restriction → fga_type_restriction（本质是限制 subject 类型，而非限制 relation）
--   旧名 fga_changelog            → fga_tuple_changelog（明确是元组变更日志，区分未来可能的模型变更日志）
--   新增 fga_condition_definition → ABAC 条件定义，使 tuple 可通过 condition_definition_id 稳定关联条件
--
-- ============================================================

-- -----------------------------------------------------------
-- 1. 存储空间表 (fga_store)
-- -----------------------------------------------------------
-- 顶层隔离单元，每个 Store 拥有独立的授权模型和元组空间。
-- 对应领域聚合根：StoreAggregate
-- 状态机：NORMAL(0) ⇄ DISABLE(1)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_store` (
    `id`               BIGINT       NOT NULL COMMENT '主键ID（雪花算法）',
    `store_id`         VARCHAR(64)  NOT NULL COMMENT '存储空间唯一标识',
    `name`             VARCHAR(128) NOT NULL COMMENT '存储空间名称',
    `description`      VARCHAR(512) DEFAULT NULL COMMENT '存储空间描述',
    `current_model_id` VARCHAR(64)  DEFAULT NULL COMMENT '当前激活的授权模型ID，实现模型版本锁定与无损切换',
    `current_zookie`   BIGINT       NOT NULL DEFAULT 0 COMMENT '当前最新Zookie版本号，保障分布式一致性',
    `status`           TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-正常, 1-禁用',
    `tenant_id`        VARCHAR(64)  DEFAULT NULL COMMENT '租户ID',
    `create_time`      BIGINT       DEFAULT NULL COMMENT '创建时间(毫秒时间戳)',
    `update_time`      BIGINT       DEFAULT NULL COMMENT '更新时间(毫秒时间戳)',
    `is_deleted`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-否, 1-是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_store_id` (`store_id`),
    KEY `idx_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA存储空间表';


-- -----------------------------------------------------------
-- 2. 授权模型表 (fga_auth_model)
-- -----------------------------------------------------------
-- 存储 OpenFGA DSL 的版本化模型主体。
-- 对应领域聚合根：AuthorizationModelAggregate
-- 状态机：DRAFT(0) → PUBLISHED(1) → ABANDONED(2)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_auth_model` (
    `id`             BIGINT       NOT NULL COMMENT '主键ID',
    `store_id`       VARCHAR(64)  NOT NULL COMMENT '所属存储空间ID',
    `model_id`       VARCHAR(64)  NOT NULL COMMENT '模型唯一标识（ULID/UUID）',
    `schema_version` VARCHAR(16)  NOT NULL DEFAULT '1.1' COMMENT '模型Schema版本',
    `dsl_text`       TEXT         DEFAULT NULL COMMENT '原始DSL文本（完整保留，支持对比和回放）',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-草稿, 1-已发布, 2-已废弃',
    `description`    VARCHAR(512) DEFAULT NULL COMMENT '模型描述',
    `tenant_id`      VARCHAR(64)  DEFAULT NULL,
    `create_time`    BIGINT       DEFAULT NULL COMMENT '创建时间(毫秒时间戳)',
    `update_time`    BIGINT       DEFAULT NULL COMMENT '最后更新时间(毫秒时间戳)',
    `publish_time`   BIGINT       DEFAULT NULL COMMENT '发布时间(毫秒时间戳)，DRAFT→PUBLISHED 时写入',
    `is_deleted`     TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_store_model` (`store_id`, `model_id`),
    KEY `idx_store_status` (`store_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA授权模型表';


-- -----------------------------------------------------------
-- 3. 类型定义表 (fga_type_definition)
-- -----------------------------------------------------------
-- 对应 DSL 中的 type 声明（如 type document、type user）。
-- 对应领域实体：TypeDefinitionEntity
-- 领域不变量：同一模型下 type 名称唯一 → UNIQUE KEY 保障
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_type_definition` (
    `id`          BIGINT      NOT NULL COMMENT '主键ID',
    `store_id`    VARCHAR(64) NOT NULL,
    `model_id`    VARCHAR(64) NOT NULL,
    `type`        VARCHAR(64) NOT NULL COMMENT '类型名（如: document, folder, user）',
    `sort_order`  INT         NOT NULL DEFAULT 0 COMMENT '排序序号（控制DSL输出顺序）',
    `tenant_id`   VARCHAR(64) DEFAULT NULL,
    `create_time` BIGINT      DEFAULT NULL,
    `is_deleted`  TINYINT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_model_type` (`store_id`, `model_id`, `type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA类型定义表';


-- -----------------------------------------------------------
-- 4. 关系定义表 (fga_relation_definition)
-- -----------------------------------------------------------
-- 对应 DSL 中的 define relation 语句。
-- 对应领域值对象：RelationDefinition
-- 每条记录 = 一个 type 下的一个 relation 及其 rewrite 规则
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_definition` (
    `id`                   BIGINT       NOT NULL COMMENT '主键ID',
    `type_definition_id`   BIGINT       NOT NULL COMMENT '所属类型定义ID',
    `relation_name`        VARCHAR(64)  NOT NULL COMMENT '关系名（如: viewer, editor, owner）',
    `rewrite_expression`   VARCHAR(512) NOT NULL COMMENT '重写表达式（如: self, self or editor, viewer from parent）',
    `relation_type`        TINYINT      NOT NULL DEFAULT 0 COMMENT '关系类型: 0-direct_only, 1-computed_userset, 2-ttu, 3-composite(含多种)',
    `create_time`          BIGINT       DEFAULT NULL,
    `is_deleted`           TINYINT(1)   NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_type_relation` (`type_definition_id`, `relation_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA关系定义表';


-- -----------------------------------------------------------
-- 5. 类型限制表 (fga_type_restriction)
-- -----------------------------------------------------------
-- 对应 DSL 中的类型限制声明：define viewer: [user, group#member]
-- 限制哪些 subject 类型（及其关系）可以建立该关系
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_type_restriction` (
    `id`                       BIGINT      NOT NULL COMMENT '主键ID',
    `relation_definition_id`   BIGINT      NOT NULL COMMENT '所属关系定义ID',
    `allowed_type`             VARCHAR(64) NOT NULL COMMENT '允许的主体类型（如: user, group）',
    `allowed_subject_relation` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '允许的主体关系（如: member）；空字符串表示直接用户',
    `create_time`              BIGINT      DEFAULT NULL,
    `is_deleted`               TINYINT(1)  NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_restriction` (`relation_definition_id`, `allowed_type`, `allowed_subject_relation`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA类型限制表';


-- -----------------------------------------------------------
-- 6. 条件定义表 (fga_condition_definition)  【新增】
-- -----------------------------------------------------------
-- ReBAC + ABAC 混合模式的核心：存储条件表达式定义。
-- 元组上的 condition_definition_id 引用本表主键，condition_name 仅作快照展示。
-- Check 引擎在元组匹配成功后，加载条件表达式 + 元组上下文进行求值。
--
-- 示例：
--   condition_name = 'is_working_hours'
--   expression     = 'input.context.current_hour >= input.params.start_hour && input.context.current_hour < input.params.end_hour'
--   parameter_schema = {"start_hour": "int", "end_hour": "int"}
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_condition_definition` (
    `id`               BIGINT         NOT NULL COMMENT '主键ID',
    `store_id`         VARCHAR(64)    NOT NULL COMMENT '所属存储空间ID',
    `model_id`         VARCHAR(64)    NOT NULL COMMENT '所属模型ID',
    `condition_name`   VARCHAR(64)    NOT NULL COMMENT '条件名称（如: is_working_hours, ip_whitelist）',
    `expression`       VARCHAR(1024)  NOT NULL COMMENT '条件表达式（CEL语法）',
    `parameter_schema` JSON           DEFAULT NULL COMMENT '参数结构定义，描述 condition_context 的字段和类型',
    `description`      VARCHAR(256)   DEFAULT NULL COMMENT '条件描述',
    `create_time`      BIGINT         DEFAULT NULL,
    `is_deleted`       TINYINT(1)     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_condition` (`store_id`, `model_id`, `condition_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA条件定义表——ABAC混合模式';


-- -----------------------------------------------------------
-- 7. 关系元组表 (fga_relation_tuple)
-- -----------------------------------------------------------
-- 核心数据表：存储所有权限关系实例。
-- 一条元组 = 关系图上的一条边：subject --relation--> object
--
-- 关键设计决策：
--   a) subject_relation 使用 NOT NULL DEFAULT '' 而非 NULL，
--      解决 MySQL 唯一索引中 NULL != NULL 导致的重复元组问题
--   b) expires_at 支持临时授权（如7天审计员、24小时分享链接）
--   c) is_wildcard 标记通配符元组（subject_id='*'），
--      Check 时需同时匹配精确主体和通配符
--   d) idx_check_covering 覆盖索引避免 Check 核心路径回表
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_relation_tuple` (
    `id`                BIGINT       NOT NULL COMMENT '主键ID（雪花算法，天然时间序）',
    `store_id`          VARCHAR(64)  NOT NULL,
    `object_type`       VARCHAR(64)  NOT NULL COMMENT 'object 类型',
    `object_id`         VARCHAR(128) NOT NULL COMMENT 'object 标识',
    `relation`          VARCHAR(64)  NOT NULL COMMENT '关系名称',
    `subject_type`      VARCHAR(64)  NOT NULL COMMENT 'subject 类型',
    `subject_id`        VARCHAR(128) NOT NULL COMMENT 'subject 标识（通配符为 *）',
    `subject_relation`  VARCHAR(64)  NOT NULL DEFAULT '' COMMENT 'userset 关系（如 member）；直接用户为空字符串',
    `is_wildcard`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否通配符元组(subject_id=*)，标记后 Check 可快速匹配',
    `condition_definition_id` BIGINT DEFAULT NULL COMMENT 'ABAC 条件定义ID，引用 fga_condition_definition.id',
    `condition_name`    VARCHAR(64)  DEFAULT NULL COMMENT 'ABAC 条件名快照，仅用于展示/导出，真实关联以 condition_definition_id 为准',
    `condition_context` JSON         DEFAULT NULL COMMENT 'ABAC 上下文参数，由 Check 引擎结合条件表达式动态求值',
    `expires_at`        BIGINT       DEFAULT NULL COMMENT '元组过期时间(毫秒时间戳)，NULL表示永不过期',
    `zookie`            BIGINT       NOT NULL COMMENT 'Zookie版本号，保障分布式读写一致性',
    `tenant_id`         VARCHAR(64)  DEFAULT NULL,
    `create_time`       BIGINT       DEFAULT NULL,
    `is_deleted`        TINYINT(1)   NOT NULL DEFAULT 0,

    PRIMARY KEY (`id`),

    -- 元组唯一性约束：subject_relation 使用空字符串而非 NULL，确保唯一约束生效
    UNIQUE KEY `uk_tuple` (`store_id`, `object_type`, `object_id`, `relation`,
                           `subject_type`, `subject_id`, `subject_relation`),

    -- Check 核心路径覆盖索引：给定 object+relation，覆盖返回 subject 全部字段，避免回表
    KEY `idx_check_covering` (`store_id`, `object_type`, `object_id`, `relation`,
                              `is_deleted`, `subject_type`, `subject_id`, `subject_relation`),

    -- ListObjects / 反向查询索引：给定 subject，查找其所有关系
    KEY `idx_subject` (`store_id`, `subject_type`, `subject_id`, `subject_relation`, `is_deleted`),

    -- Zookie 一致性查询索引（Watch API 按 zookie 范围查变更）
    KEY `idx_zookie` (`store_id`, `zookie`),

    -- 条件定义定位索引
    KEY `idx_condition_definition` (`condition_definition_id`),

    -- 过期元组清理索引：定时任务批量扫描已过期元组
    KEY `idx_expires` (`is_deleted`, `expires_at`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA关系元组表';


-- -----------------------------------------------------------
-- 8. 元组变更日志表 (fga_tuple_changelog)
-- -----------------------------------------------------------
-- Append-only 审计日志，支撑：
--   a) Watch API 实时推送（按 zookie 范围查询增量变更）
--   b) 权限审计追溯（谁在什么时候通过什么操作改了什么权限）
--   c) 时间点快照回放（结合模型版本，重建任意时刻的权限状态）
--
-- 审计完整性：新增 operator_id / request_id / source 三字段，
-- 回答「谁(operator) → 通过什么渠道(source) → 在哪个请求中(request_id) → 做了什么」
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `fga_tuple_changelog` (
    `id`                BIGINT       NOT NULL COMMENT '主键ID',
    `store_id`          VARCHAR(64)  NOT NULL,
    `zookie`            BIGINT       NOT NULL COMMENT '版本令牌，Watch API 和审计使用',
    `operation`         TINYINT      NOT NULL DEFAULT 0 COMMENT '操作类型: 0=WRITE, 1=DELETE',
    `object_type`       VARCHAR(64)  NOT NULL,
    `object_id`         VARCHAR(128) NOT NULL,
    `relation`          VARCHAR(64)  NOT NULL,
    `subject_type`      VARCHAR(64)  NOT NULL,
    `subject_id`        VARCHAR(128) NOT NULL,
    `subject_relation`  VARCHAR(64)  NOT NULL DEFAULT '',
    `operator_id`       VARCHAR(128) DEFAULT NULL COMMENT '操作人标识（用户ID或服务账号）',
    `request_id`        VARCHAR(64)  DEFAULT NULL COMMENT '请求追踪ID，关联调用链（如 traceId）',
    `source`            VARCHAR(32)  DEFAULT NULL COMMENT '操作来源: API / SYNC / CLEANUP / MIGRATION',
    `operation_time`    BIGINT       NOT NULL COMMENT '操作时间(毫秒时间戳)',
    PRIMARY KEY (`id`),
    KEY `idx_store_zookie` (`store_id`, `zookie`),
    KEY `idx_store_time` (`store_id`, `operation_time`),
    KEY `idx_operator` (`operator_id`, `operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='FGA元组变更日志表';


-- ============================================================
-- 表关系总览（8 张表）
-- ============================================================
--
--   fga_store (存储空间)
--       │
--       ├──► fga_auth_model (授权模型) ──► current_model_id 回指 store
--       │        │
--       │        ├──► fga_type_definition (类型定义)
--       │        │        │
--       │        │        └──► fga_relation_definition (关系定义)
--       │        │                 │
--       │        │                 └──► fga_type_restriction (类型限制)
--       │        │
--       │        └──► fga_condition_definition (条件定义) 【新增】
--       │
--       ├──► fga_relation_tuple (关系元组)
--       │        └── condition_definition_id ──► 引用 fga_condition_definition.id
--       │
--       └──► fga_tuple_changelog (变更日志)
--
-- ============================================================


-- ============================================================
-- 能力覆盖清单（对齐 ReBAC 文章合集全部能力）
-- ============================================================
--
-- 【核心 ReBAC】
--   ✓ 关系元组存储          → fga_relation_tuple
--   ✓ Check / ListObjects   → uk_tuple + idx_check_covering
--   ✓ ListUsers / 反向查询  → idx_subject
--   ✓ 授权模型版本化        → fga_auth_model (status + publish_time)
--   ✓ 类型定义 / 关系定义   → fga_type_definition + fga_relation_definition
--   ✓ 类型限制（Userset）   → fga_type_restriction
--   ✓ 重写规则（Union/Intersection/Exclusion/TTU/ComputedUserset）
--                           → fga_relation_definition.rewrite_expression
--   ✓ 关系类型分类查询      → fga_relation_definition.relation_type
--
-- 【Zanzibar 一致性】
--   ✓ Zookie 一致性令牌     → fga_store.current_zookie + fga_relation_tuple.zookie
--   ✓ Watch API 增量推送    → fga_tuple_changelog + idx_store_zookie
--   ✓ 变更审计追溯          → fga_tuple_changelog (operator_id, request_id, source)
--
-- 【ABAC 混合模式】
--   ✓ 条件定义（CEL表达式） → fga_condition_definition 【新增表】
--   ✓ 元组条件绑定          → fga_relation_tuple.condition_definition_id + condition_context
--   ✓ 动态属性约束          → Check 引擎加载条件定义 + 元组上下文进行求值
--
-- 【临时授权】
--   ✓ 元组过期时间          → fga_relation_tuple.expires_at 【新增字段】
--   ✓ 过期清理索引          → idx_expires
--
-- 【通配符访问】
--   ✓ user:* 通配符元组     → fga_relation_tuple.is_wildcard 【新增字段】
--   ✓ Check 时匹配通配符    → WHERE ... OR is_wildcard = 1
--
-- 【多租户】
--   ✓ Store 级逻辑隔离      → store_id 作为所有表的前缀
--   ✓ 租户级物理隔离        → tenant_id 字段
--
-- 【模型生命周期】
--   ✓ DRAFT → PUBLISHED → ABANDONED → fga_auth_model.status
--   ✓ 发布时间记录          → fga_auth_model.publish_time 【新增字段】
--   ✓ 模型版本锁定与切换    → fga_store.current_model_id
--
-- ============================================================


-- ============================================================
-- 数据流说明
-- ============================================================
--
-- 【权限管理（本系统提供 API，业务或管控端调用）】
--   1) 创建/更新 Store → fga_store
--   2) 写入授权模型 → fga_auth_model + fga_type_definition
--      + fga_relation_definition + fga_type_restriction + fga_condition_definition
--   3) 写元组 (Write) → fga_relation_tuple 插入 + fga_tuple_changelog 记录
--   4) 删元组 (Delete) → fga_relation_tuple 逻辑删除 + fga_tuple_changelog 记录
--
-- 【权限验证（本系统提供 API，业务在鉴权时调用）】
--   - Check：加载当前模型 + 遍历 fga_relation_tuple 计算权限
--     → 匹配元组后若有 condition_definition_id，则加载 fga_condition_definition 求值
--     → 若元组有 expires_at，则检查是否过期
--     → 若存在 is_wildcard=1 的元组，则匹配通配符
--   - ListObjects：给定 subject + relation，查所有有权 object
--   - ListUsers：给定 object + relation，查所有有权 subject
--
-- 【业务侧职责】
--   - 资源管理在业务服务；资源创建/共享/删除时调用本系统 Write/Delete 同步元组
--   - 鉴权时调用本系统 Check（或 ListObjects/ListUsers），不自行计算权限
--
-- ============================================================
