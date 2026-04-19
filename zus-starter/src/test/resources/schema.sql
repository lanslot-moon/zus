-- ==========================================================================
-- 测试环境 H2 schema（对齐 zus-infrastructure PO @TableName / 字段）
--
-- 与生产 table_schema.sql 的差异：
--   1) 按 PO 层真实 @TableName 命名：
--      fga_authorization_model / fga_model_relation / fga_relation_restriction
--   2) 去掉 MySQL 特有的 ENGINE / CHARSET / COLLATE / COMMENT 等 DDL 选项
--   3) JSON 列替换为 VARCHAR（PO 侧本就存字符串）
--   4) TINYINT(1) 替换为 TINYINT，Boolean 字段用 0/1
--   5) 软删除表不加业务唯一约束：仓储层采用"软删除 + 重新插入"模式，
--      生产 MySQL 下可接受（is_deleted=1 占位会阻塞同键写入，是已知实现约束），
--      但 H2 测试需要在一条事务里完成 publish/update 等"重写关联结构"流程，
--      因此这里仅保留 PRIMARY KEY，业务唯一性由应用层 + 领域约束保证。
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
    tenant_id        VARCHAR(64)  DEFAULT NULL,
    create_time      BIGINT       DEFAULT NULL,
    update_time      BIGINT       DEFAULT NULL,
    is_deleted       TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_authorization_model;
CREATE TABLE fga_authorization_model (
    id             BIGINT       NOT NULL,
    store_id       VARCHAR(64)  NOT NULL,
    model_id       VARCHAR(64)  NOT NULL,
    schema_version VARCHAR(16)  NOT NULL DEFAULT '1.1',
    dsl_text       CLOB         DEFAULT NULL,
    status         TINYINT      NOT NULL DEFAULT 0,
    description    VARCHAR(512) DEFAULT NULL,
    tenant_id      VARCHAR(64)  DEFAULT NULL,
    create_time    BIGINT       DEFAULT NULL,
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
    tenant_id   VARCHAR(64) DEFAULT NULL,
    create_time BIGINT      DEFAULT NULL,
    is_deleted  TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_model_relation;
CREATE TABLE fga_model_relation (
    id                 BIGINT       NOT NULL,
    type_definition_id BIGINT       NOT NULL,
    subject_type       VARCHAR(64)  DEFAULT NULL,
    relation_name      VARCHAR(64)  NOT NULL,
    rewrite_expression VARCHAR(512) NOT NULL,
    create_time        BIGINT       DEFAULT NULL,
    is_deleted         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

DROP TABLE IF EXISTS fga_relation_restriction;
CREATE TABLE fga_relation_restriction (
    id                       BIGINT      NOT NULL,
    relation_definition_id   BIGINT      NOT NULL,
    allowed_type             VARCHAR(64) NOT NULL,
    allowed_subject_relation VARCHAR(64) NOT NULL DEFAULT '',
    create_time              BIGINT      DEFAULT NULL,
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
    is_deleted       TINYINT       NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
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
    tenant_id               VARCHAR(64)  DEFAULT NULL,
    create_time             BIGINT       DEFAULT NULL,
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
    PRIMARY KEY (id)
);
