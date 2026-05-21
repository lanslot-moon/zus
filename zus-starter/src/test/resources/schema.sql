-- ==========================================================================
-- 测试环境 H2 schema（对齐 zus-infrastructure PO @TableName / 字段）
--
-- 与生产 table_schema.sql 的差异：
--   1) 按 PO 层真实 @TableName 命名：
--      fga_auth_model / fga_relation_definition / fga_type_restriction
--   2) 去掉 MySQL 特有的 ENGINE / CHARSET / COLLATE / COMMENT 等 DDL 选项
--   3) JSON 列替换为 VARCHAR（PO 侧本就存字符串）
--   4) TINYINT(1) 替换为 TINYINT，Boolean 字段用 0/1
--   5) 保留核心业务唯一约束，确保 H2 端到端测试能覆盖 MySQL 的唯一键行为。
--
-- 作用：让 @SpringBootTest 启动 DataSource 后可以执行真实 Mapper/Repository。
-- ==========================================================================

DROP TABLE IF EXISTS fga_store;
CREATE TABLE fga_store (
    id               BIGINT       NOT NULL,
    store_id         VARCHAR(64)  NOT NULL,
    name             VARCHAR(128) NOT NULL,
    description      VARCHAR(512) DEFAULT NULL,
    current_model_id VARCHAR(64)  DEFAULT NULL,
    current_zookie   BIGINT       NOT NULL DEFAULT 0,
    status           TINYINT      NOT NULL DEFAULT 0,
    create_time      BIGINT       DEFAULT NULL,
    update_time      BIGINT       DEFAULT NULL,
    is_deleted       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_auth_model;
CREATE TABLE fga_auth_model (
    id             BIGINT       NOT NULL,
    store_id       VARCHAR(64)  NOT NULL,
    model_id       VARCHAR(64)  NOT NULL,
    schema_version VARCHAR(16)  NOT NULL DEFAULT '1.1',
    dsl_text       CLOB         DEFAULT NULL,
    status         TINYINT      NOT NULL DEFAULT 0,
    description    VARCHAR(512) DEFAULT NULL,
    create_time    BIGINT       DEFAULT NULL,
    update_time    BIGINT       DEFAULT NULL,
    is_deleted     TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_type_definition;
CREATE TABLE fga_type_definition (
    id          BIGINT      NOT NULL,
    store_id    VARCHAR(64) NOT NULL,
    model_id    VARCHAR(64) NOT NULL,
    type        VARCHAR(64) NOT NULL,
    sort_order  INT         NOT NULL DEFAULT 0,
    create_time BIGINT      DEFAULT NULL,
    update_time BIGINT      DEFAULT NULL,
    is_deleted  TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_model_type (store_id, model_id, type)
);

DROP TABLE IF EXISTS fga_relation_definition;
CREATE TABLE fga_relation_definition (
    id                 BIGINT       NOT NULL,
    type_definition_id BIGINT       NOT NULL,
    subject_type       VARCHAR(64)  DEFAULT NULL,
    relation_name      VARCHAR(64)  NOT NULL,
    rewrite_expression VARCHAR(512) NOT NULL,
    create_time        BIGINT       DEFAULT NULL,
    update_time        BIGINT       DEFAULT NULL,
    is_deleted         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_type_restriction;
CREATE TABLE fga_type_restriction (
    id                       BIGINT      NOT NULL,
    relation_definition_id   BIGINT      NOT NULL,
    allowed_type             VARCHAR(64) NOT NULL,
    allowed_subject_relation VARCHAR(64) NOT NULL DEFAULT '',
    create_time              BIGINT      DEFAULT NULL,
    update_time              BIGINT      DEFAULT NULL,
    is_deleted               TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_condition_definition;
CREATE TABLE fga_condition_definition (
    id               BIGINT        NOT NULL,
    store_id         VARCHAR(64)   NOT NULL,
    model_id         VARCHAR(64)   NOT NULL,
    condition_name   VARCHAR(64)   NOT NULL,
    expression       VARCHAR(1024) NOT NULL,
    parameter_schema VARCHAR(2048) DEFAULT NULL,
    description      VARCHAR(256)  DEFAULT NULL,
    create_time      BIGINT        DEFAULT NULL,
    update_time      BIGINT        DEFAULT NULL,
    is_deleted       TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uk_condition (store_id, model_id, condition_name)
);

DROP TABLE IF EXISTS fga_relation_tuple;
CREATE TABLE fga_relation_tuple (
    id                      BIGINT       NOT NULL,
    store_id                VARCHAR(64)  NOT NULL,
    object_type             VARCHAR(64)  NOT NULL,
    object_id               VARCHAR(128) NOT NULL,
    relation                VARCHAR(64)  NOT NULL,
    subject_type            VARCHAR(64)  NOT NULL,
    subject_id              VARCHAR(128) NOT NULL,
    subject_relation        VARCHAR(64)  DEFAULT '',
    is_wildcard             TINYINT      DEFAULT 0,
    condition_definition_id BIGINT       DEFAULT NULL,
    condition_name          VARCHAR(64)  DEFAULT NULL,
    condition_context       VARCHAR(2048) DEFAULT NULL,
    expires_at              BIGINT       DEFAULT NULL,
    zookie                  BIGINT       NOT NULL,
    create_time             BIGINT       DEFAULT NULL,
    update_time             BIGINT       DEFAULT NULL,
    is_deleted              TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_tuple_changelog;
CREATE TABLE fga_tuple_changelog (
    id               BIGINT       NOT NULL,
    store_id         VARCHAR(64)  NOT NULL,
    zookie           BIGINT       NOT NULL,
    operation        VARCHAR(16)  NOT NULL,
    object_type      VARCHAR(64)  NOT NULL,
    object_id        VARCHAR(128) NOT NULL,
    relation         VARCHAR(64)  NOT NULL,
    subject_type     VARCHAR(64)  NOT NULL,
    subject_id       VARCHAR(128) NOT NULL,
    subject_relation VARCHAR(64)  DEFAULT '',
    operator_id      VARCHAR(128) DEFAULT NULL,
    request_id       VARCHAR(64)  DEFAULT NULL,
    source           VARCHAR(32)  DEFAULT NULL,
    operation_time   BIGINT       NOT NULL,
    create_time      BIGINT       DEFAULT NULL,
    update_time      BIGINT       DEFAULT NULL,
    is_deleted       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
