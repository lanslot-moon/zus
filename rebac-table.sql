CREATE TABLE subject
(
    id   BIGINT PRIMARY KEY AUTO_INCREMENT,
    type VARCHAR(50)  NOT NULL,
    name VARCHAR(100) NOT NULL
) comment '主体表,主体，代表用户、角色、群组等';


CREATE TABLE resource
(
    id        BIGINT PRIMARY KEY AUTO_INCREMENT,
    type      VARCHAR(50)  NOT NULL,
    name      VARCHAR(100) NOT NULL,
    parent_id BIGINT       NULL,
    CONSTRAINT fk_resource_parent FOREIGN KEY (parent_id) REFERENCES resource (id)
) comment '资源表,被保护的对象（文件、项目、菜单等）';


CREATE TABLE relation_definition
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_type VARCHAR(50)  NOT NULL,
    relation_name VARCHAR(50)  NOT NULL,
    derived_from  JSON         NULL,
    description   VARCHAR(200) NULL
) comment '关系定义表,描述主体与资源之间的关系';


CREATE TABLE relation_tuple
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    subject_type  VARCHAR(50) NOT NULL,
    subject_id    BIGINT      NOT NULL,
    resource_type VARCHAR(50) NOT NULL,
    resource_id   BIGINT      NOT NULL,
    relation_name VARCHAR(50) NOT NULL,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
) comment '关系元组表,定义资源类型的关系模型（类似 FGA model）';


CREATE TABLE permission_mapping
(
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_type VARCHAR(50) NOT NULL,
    relation_name VARCHAR(50) NOT NULL,
    action        VARCHAR(50) NOT NULL
) comment '权限映射表,权限映射表（关系 → 操作）';




# +-------------+           +-----------------+          +------------------+
# |  subject    |           |  relation_tuple |          |  resource        |
# |-------------|           |-----------------|          |------------------|
# | id (PK)     |<--------->| subject_id      |<-------->| id (PK)          |
# | type        |           | subject_type    |          | type             |
# | name        |           | resource_type   |          | parent_id (FK)   |
# | ...         |           | resource_id     |          | name             |
# |-------------|           | relation        |          |------------------|
#                       ^                        |
#                       |                        |
#                       |                        v
#              +-----------------------+   +-----------------------+
#              | relation_definition   |   | permission_mapping    |
#              |-----------------------|   |-----------------------|
#              | resource_type         |   | resource_type         |
#              | relation              |   | relation              |
#              | derived_from (JSON)   |   | action                |
#              +-----------------------+   +-----------------------+

