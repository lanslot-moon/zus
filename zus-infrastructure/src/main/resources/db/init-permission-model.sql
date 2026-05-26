-- ============================================================
-- ZUS 复杂权限模型测试数据
-- ============================================================
--
-- 覆盖模型：
--   1. ACL   ：资源级直接授权、group#member userset、wildcard、过期授权、删除日志
--   2. ABAC  ：条件定义、tuple 条件绑定、动态上下文、通配符条件、过期条件授权
--   3. RBAC  ：角色对象、角色分配、角色到组织/项目权限传播、group#member 角色成员
--   4. ReBAC ：组织/分组/文件夹/文档关系图、TTU、computed userset、intersection、exclusion
--
-- 使用方式：
--   mysql -u root -p zus < init-permission-model.sql
--
-- 说明：
--   1. 本脚本是测试种子数据，不是生产初始化脚本。
--   2. 脚本按 store_id 清理后重建，可重复执行。
--   3. 所有 subject_relation 的 direct subject 均使用空字符串，和 table_schema.sql 设计保持一致。
--   4. expires_at 使用固定时间戳：
--      - 4102444800000 表示 2100-01-01，作为未过期样例。
--      - 1700000000000 表示 2023-11-14 左右，作为已过期样例。
--
-- ============================================================

USE zus;

SET NAMES utf8mb4;
SET @FUTURE_EXPIRES_AT := 4102444800000;
SET @PAST_EXPIRES_AT := 1700000000000;

-- ------------------------------------------------------------
-- 可重复执行清理
-- ------------------------------------------------------------
DELETE FROM fga_tuple_changelog
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');

DELETE FROM fga_relation_tuple
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');

DELETE FROM fga_condition_definition
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');

DELETE FROM fga_type_restriction
WHERE relation_definition_id BETWEEN 910200 AND 949999;

DELETE FROM fga_relation_definition
WHERE id BETWEEN 910200 AND 949999;

DELETE FROM fga_type_definition
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');

DELETE FROM fga_auth_model
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');

DELETE FROM fga_store
WHERE store_id IN ('store_acl_complex', 'store_abac_complex', 'store_rbac_complex', 'store_rebac_complex');


-- ============================================================
-- 1. ACL：资源级访问控制模型
-- ============================================================
-- 测试点：
--   - object#relation@user 直接授权
--   - object#relation@group#member userset 授权
--   - user:* wildcard 公开访问
--   - expires_at 过期过滤
--   - DELETE changelog
-- ============================================================

INSERT INTO fga_store
    (id, store_id, name, description, current_model_id, current_zookie, status, create_time, update_time, is_deleted)
VALUES
    (910001, 'store_acl_complex', 'ACL Complex Store', '复杂 ACL 测试数据：直接授权、用户组、通配符、过期授权', 'model_acl_complex_v1', 9, 0, 1779000000000, 1779000000000, 0);

INSERT INTO fga_auth_model
    (id, store_id, model_id, schema_version, dsl_text, status, description, create_time, update_time, publish_time, is_deleted)
VALUES
    (910010, 'store_acl_complex', 'model_acl_complex_v1', '1.1',
     'model schema 1.1\n\ntype user\n\ntype group\n  relations\n    define member as self\n\ntype document\n  relations\n    define owner as self\n    define editor as self or owner\n    define viewer as self or editor or owner\n    define commenter as self\n    define public_viewer as self',
     1, 'ACL 模型：每个资源维护直接访问控制列表，同时允许 group#member 和 user:*', 1779000000000, 1779000000000, 1779000000000, 0);

INSERT INTO fga_type_definition
    (id, store_id, model_id, type, sort_order, create_time, update_time, is_deleted)
VALUES
    (910101, 'store_acl_complex', 'model_acl_complex_v1', 'user', 0, 1779000000000, 1779000000000, 0),
    (910102, 'store_acl_complex', 'model_acl_complex_v1', 'group', 1, 1779000000000, 1779000000000, 0),
    (910103, 'store_acl_complex', 'model_acl_complex_v1', 'document', 2, 1779000000000, 1779000000000, 0);

INSERT INTO fga_relation_definition
    (id, type_definition_id, relation_name, rewrite_expression, relation_type, create_time, update_time, is_deleted)
VALUES
    (910201, 910102, 'member', 'self', 0, 1779000000000, 1779000000000, 0),
    (910211, 910103, 'owner', 'self', 0, 1779000000000, 1779000000000, 0),
    (910212, 910103, 'editor', 'self or owner', 3, 1779000000000, 1779000000000, 0),
    (910213, 910103, 'viewer', 'self or editor or owner', 3, 1779000000000, 1779000000000, 0),
    (910214, 910103, 'commenter', 'self', 0, 1779000000000, 1779000000000, 0),
    (910215, 910103, 'public_viewer', 'self', 0, 1779000000000, 1779000000000, 0);

INSERT INTO fga_type_restriction
    (id, relation_definition_id, allowed_type, allowed_subject_relation, create_time, update_time, is_deleted)
VALUES
    (910301, 910201, 'user', '', 1779000000000, 1779000000000, 0),
    (910311, 910211, 'user', '', 1779000000000, 1779000000000, 0),
    (910312, 910211, 'group', 'member', 1779000000000, 1779000000000, 0),
    (910313, 910212, 'user', '', 1779000000000, 1779000000000, 0),
    (910314, 910212, 'group', 'member', 1779000000000, 1779000000000, 0),
    (910315, 910213, 'user', '', 1779000000000, 1779000000000, 0),
    (910316, 910213, 'group', 'member', 1779000000000, 1779000000000, 0),
    (910317, 910214, 'user', '', 1779000000000, 1779000000000, 0),
    (910318, 910215, 'user', '', 1779000000000, 1779000000000, 0);

INSERT INTO fga_relation_tuple
    (id, store_id, object_type, object_id, relation, subject_type, subject_id, subject_relation, is_wildcard,
     condition_definition_id, condition_name, condition_context, expires_at, zookie, create_time, update_time, is_deleted)
VALUES
    (910401, 'store_acl_complex', 'group', 'engineering', 'member', 'user', 'bob', '', 0, NULL, NULL, NULL, NULL, 1, 1779000001000, 1779000001000, 0),
    (910402, 'store_acl_complex', 'group', 'auditors', 'member', 'user', 'carol', '', 0, NULL, NULL, NULL, NULL, 2, 1779000002000, 1779000002000, 0),
    (910403, 'store_acl_complex', 'document', 'design-spec', 'owner', 'user', 'alice', '', 0, NULL, NULL, NULL, NULL, 3, 1779000003000, 1779000003000, 0),
    (910404, 'store_acl_complex', 'document', 'design-spec', 'editor', 'group', 'engineering', 'member', 0, NULL, NULL, NULL, NULL, 4, 1779000004000, 1779000004000, 0),
    (910405, 'store_acl_complex', 'document', 'design-spec', 'viewer', 'user', 'dave', '', 0, NULL, NULL, NULL, @FUTURE_EXPIRES_AT, 5, 1779000005000, 1779000005000, 0),
    (910406, 'store_acl_complex', 'document', 'public-roadmap', 'public_viewer', 'user', '*', '', 1, NULL, NULL, NULL, NULL, 6, 1779000006000, 1779000006000, 0),
    (910407, 'store_acl_complex', 'document', 'audit-plan', 'viewer', 'group', 'auditors', 'member', 0, NULL, NULL, NULL, NULL, 7, 1779000007000, 1779000007000, 0),
    (910408, 'store_acl_complex', 'document', 'legacy-plan', 'viewer', 'user', 'erin', '', 0, NULL, NULL, NULL, @PAST_EXPIRES_AT, 8, 1779000008000, 1779000008000, 0),
    (910409, 'store_acl_complex', 'document', 'deleted-draft', 'viewer', 'user', 'mallory', '', 0, NULL, NULL, NULL, NULL, 9, 1779000009000, 1779000010000, 1);

INSERT INTO fga_tuple_changelog
    (id, store_id, zookie, operation, object_type, object_id, relation, subject_type, subject_id, subject_relation,
     operator_id, request_id, source, operation_time, create_time, update_time, is_deleted)
VALUES
    (910501, 'store_acl_complex', 1, 'WRITE', 'group', 'engineering', 'member', 'user', 'bob', '', 'seed-admin', 'seed-acl-001', 'MIGRATION', 1779000001000, 1779000001000, 1779000001000, 0),
    (910502, 'store_acl_complex', 2, 'WRITE', 'group', 'auditors', 'member', 'user', 'carol', '', 'seed-admin', 'seed-acl-002', 'MIGRATION', 1779000002000, 1779000002000, 1779000002000, 0),
    (910503, 'store_acl_complex', 3, 'WRITE', 'document', 'design-spec', 'owner', 'user', 'alice', '', 'seed-admin', 'seed-acl-003', 'MIGRATION', 1779000003000, 1779000003000, 1779000003000, 0),
    (910504, 'store_acl_complex', 4, 'WRITE', 'document', 'design-spec', 'editor', 'group', 'engineering', 'member', 'seed-admin', 'seed-acl-004', 'MIGRATION', 1779000004000, 1779000004000, 1779000004000, 0),
    (910505, 'store_acl_complex', 5, 'WRITE', 'document', 'design-spec', 'viewer', 'user', 'dave', '', 'seed-admin', 'seed-acl-005', 'MIGRATION', 1779000005000, 1779000005000, 1779000005000, 0),
    (910506, 'store_acl_complex', 6, 'WRITE', 'document', 'public-roadmap', 'public_viewer', 'user', '*', '', 'seed-admin', 'seed-acl-006', 'MIGRATION', 1779000006000, 1779000006000, 1779000006000, 0),
    (910507, 'store_acl_complex', 7, 'WRITE', 'document', 'audit-plan', 'viewer', 'group', 'auditors', 'member', 'seed-admin', 'seed-acl-007', 'MIGRATION', 1779000007000, 1779000007000, 1779000007000, 0),
    (910508, 'store_acl_complex', 8, 'WRITE', 'document', 'legacy-plan', 'viewer', 'user', 'erin', '', 'seed-admin', 'seed-acl-008', 'MIGRATION', 1779000008000, 1779000008000, 1779000008000, 0),
    (910509, 'store_acl_complex', 9, 'DELETE', 'document', 'deleted-draft', 'viewer', 'user', 'mallory', '', 'seed-admin', 'seed-acl-009', 'MIGRATION', 1779000010000, 1779000010000, 1779000010000, 0);


-- ============================================================
-- 2. ABAC：条件化授权模型
-- ============================================================
-- 测试点：
--   - fga_condition_definition 条件定义
--   - tuple.condition_definition_id + condition_context
--   - input.context / input.params / input.subject / input.object / input.tuple
--   - 条件失败、条件缺失、过期条件、通配符条件
-- ============================================================

INSERT INTO fga_store
    (id, store_id, name, description, current_model_id, current_zookie, status, create_time, update_time, is_deleted)
VALUES
    (920001, 'store_abac_complex', 'ABAC Complex Store', '复杂 ABAC 测试数据：CEL 条件、动态上下文、临时授权', 'model_abac_complex_v1', 10, 0, 1779010000000, 1779010000000, 0);

INSERT INTO fga_auth_model
    (id, store_id, model_id, schema_version, dsl_text, status, description, create_time, update_time, publish_time, is_deleted)
VALUES
    (920010, 'store_abac_complex', 'model_abac_complex_v1', '1.1',
     'model schema 1.1\n\ntype user\n\ntype document\n  relations\n    define owner as self\n    define viewer as self or owner\n\ntype api_endpoint\n  relations\n    define caller as self\n\nconditions\n  condition working_hours(start_hour: int, end_hour: int) { input.context.current_hour >= input.params.start_hour && input.context.current_hour < input.params.end_hour }\n  condition ip_match(allowed_ip: string) { input.context.client_ip == input.params.allowed_ip }\n  condition department_match(department: string) { input.context.department == input.params.department }\n  condition min_clearance(min_clearance: int) { input.context.clearance >= input.params.min_clearance }\n  condition low_risk(max_risk_score: int) { input.context.risk_score <= input.params.max_risk_score }',
     1, 'ABAC 模型：ReBAC tuple 绑定 CEL 条件定义，Check 时结合动态上下文求值', 1779010000000, 1779010000000, 1779010000000, 0);

INSERT INTO fga_type_definition
    (id, store_id, model_id, type, sort_order, create_time, update_time, is_deleted)
VALUES
    (920101, 'store_abac_complex', 'model_abac_complex_v1', 'user', 0, 1779010000000, 1779010000000, 0),
    (920102, 'store_abac_complex', 'model_abac_complex_v1', 'document', 1, 1779010000000, 1779010000000, 0),
    (920103, 'store_abac_complex', 'model_abac_complex_v1', 'api_endpoint', 2, 1779010000000, 1779010000000, 0);

INSERT INTO fga_relation_definition
    (id, type_definition_id, relation_name, rewrite_expression, relation_type, create_time, update_time, is_deleted)
VALUES
    (920201, 920102, 'owner', 'self', 0, 1779010000000, 1779010000000, 0),
    (920202, 920102, 'viewer', 'self or owner', 3, 1779010000000, 1779010000000, 0),
    (920211, 920103, 'caller', 'self', 0, 1779010000000, 1779010000000, 0);

INSERT INTO fga_type_restriction
    (id, relation_definition_id, allowed_type, allowed_subject_relation, create_time, update_time, is_deleted)
VALUES
    (920301, 920201, 'user', '', 1779010000000, 1779010000000, 0),
    (920302, 920202, 'user', '', 1779010000000, 1779010000000, 0),
    (920303, 920211, 'user', '', 1779010000000, 1779010000000, 0);

INSERT INTO fga_condition_definition
    (id, store_id, model_id, condition_name, expression, parameter_schema, description, create_time, update_time, is_deleted)
VALUES
    (920601, 'store_abac_complex', 'model_abac_complex_v1', 'working_hours',
     'input.context.current_hour >= input.params.start_hour && input.context.current_hour < input.params.end_hour',
     '{"start_hour":"int","end_hour":"int"}', '仅允许在指定工作时间访问', 1779010000000, 1779010000000, 0),
    (920602, 'store_abac_complex', 'model_abac_complex_v1', 'ip_match',
     'input.context.client_ip == input.params.allowed_ip',
     '{"allowed_ip":"string"}', '仅允许指定来源 IP 访问', 1779010000000, 1779010000000, 0),
    (920603, 'store_abac_complex', 'model_abac_complex_v1', 'department_match',
     'input.context.department == input.params.department',
     '{"department":"string"}', '仅允许同部门访问', 1779010000000, 1779010000000, 0),
    (920604, 'store_abac_complex', 'model_abac_complex_v1', 'min_clearance',
     'input.context.clearance >= input.params.min_clearance',
     '{"min_clearance":"int"}', '访问者安全等级必须达到阈值', 1779010000000, 1779010000000, 0),
    (920605, 'store_abac_complex', 'model_abac_complex_v1', 'low_risk',
     'input.context.risk_score <= input.params.max_risk_score',
     '{"max_risk_score":"int"}', '访问风险分必须低于阈值', 1779010000000, 1779010000000, 0);

INSERT INTO fga_relation_tuple
    (id, store_id, object_type, object_id, relation, subject_type, subject_id, subject_relation, is_wildcard,
     condition_definition_id, condition_name, condition_context, expires_at, zookie, create_time, update_time, is_deleted)
VALUES
    (920401, 'store_abac_complex', 'document', 'payroll-q1', 'owner', 'user', 'finance-lead', '', 0, NULL, NULL, NULL, NULL, 1, 1779010001000, 1779010001000, 0),
    (920402, 'store_abac_complex', 'document', 'payroll-q1', 'viewer', 'user', 'alice', '', 0, 920601, 'working_hours', '{"start_hour":9,"end_hour":18}', NULL, 2, 1779010002000, 1779010002000, 0),
    (920403, 'store_abac_complex', 'document', 'payroll-q1', 'viewer', 'user', 'bob', '', 0, 920604, 'min_clearance', '{"min_clearance":4}', NULL, 3, 1779010003000, 1779010003000, 0),
    (920404, 'store_abac_complex', 'document', 'engineering-plan', 'viewer', 'user', 'carol', '', 0, 920603, 'department_match', '{"department":"engineering"}', NULL, 4, 1779010004000, 1779010004000, 0),
    (920405, 'store_abac_complex', 'api_endpoint', 'deploy-prod', 'caller', 'user', 'dave', '', 0, 920602, 'ip_match', '{"allowed_ip":"10.0.1.25"}', NULL, 5, 1779010005000, 1779010005000, 0),
    (920406, 'store_abac_complex', 'api_endpoint', 'risk-review', 'caller', 'user', 'erin', '', 0, 920605, 'low_risk', '{"max_risk_score":30}', NULL, 6, 1779010006000, 1779010006000, 0),
    (920407, 'store_abac_complex', 'document', 'campaign-brief', 'viewer', 'user', '*', '', 1, 920601, 'working_hours', '{"start_hour":10,"end_hour":17}', NULL, 7, 1779010007000, 1779010007000, 0),
    (920408, 'store_abac_complex', 'document', 'expired-vendor-share', 'viewer', 'user', 'vendor-a', '', 0, 920602, 'ip_match', '{"allowed_ip":"172.16.9.9"}', @PAST_EXPIRES_AT, 8, 1779010008000, 1779010008000, 0),
    (920409, 'store_abac_complex', 'document', 'temporary-audit', 'viewer', 'user', 'auditor-temp', '', 0, 920601, 'working_hours', '{"start_hour":8,"end_hour":20}', @FUTURE_EXPIRES_AT, 9, 1779010009000, 1779010009000, 0),
    (920410, 'store_abac_complex', 'api_endpoint', 'sensitive-export', 'caller', 'user', 'mallory', '', 0, 920605, 'low_risk', '{"max_risk_score":5}', NULL, 10, 1779010010000, 1779010010000, 0);

INSERT INTO fga_tuple_changelog
    (id, store_id, zookie, operation, object_type, object_id, relation, subject_type, subject_id, subject_relation,
     operator_id, request_id, source, operation_time, create_time, update_time, is_deleted)
VALUES
    (920501, 'store_abac_complex', 1, 'WRITE', 'document', 'payroll-q1', 'owner', 'user', 'finance-lead', '', 'seed-admin', 'seed-abac-001', 'MIGRATION', 1779010001000, 1779010001000, 1779010001000, 0),
    (920502, 'store_abac_complex', 2, 'WRITE', 'document', 'payroll-q1', 'viewer', 'user', 'alice', '', 'seed-admin', 'seed-abac-002', 'MIGRATION', 1779010002000, 1779010002000, 1779010002000, 0),
    (920503, 'store_abac_complex', 3, 'WRITE', 'document', 'payroll-q1', 'viewer', 'user', 'bob', '', 'seed-admin', 'seed-abac-003', 'MIGRATION', 1779010003000, 1779010003000, 1779010003000, 0),
    (920504, 'store_abac_complex', 4, 'WRITE', 'document', 'engineering-plan', 'viewer', 'user', 'carol', '', 'seed-admin', 'seed-abac-004', 'MIGRATION', 1779010004000, 1779010004000, 1779010004000, 0),
    (920505, 'store_abac_complex', 5, 'WRITE', 'api_endpoint', 'deploy-prod', 'caller', 'user', 'dave', '', 'seed-admin', 'seed-abac-005', 'MIGRATION', 1779010005000, 1779010005000, 1779010005000, 0),
    (920506, 'store_abac_complex', 6, 'WRITE', 'api_endpoint', 'risk-review', 'caller', 'user', 'erin', '', 'seed-admin', 'seed-abac-006', 'MIGRATION', 1779010006000, 1779010006000, 1779010006000, 0),
    (920507, 'store_abac_complex', 7, 'WRITE', 'document', 'campaign-brief', 'viewer', 'user', '*', '', 'seed-admin', 'seed-abac-007', 'MIGRATION', 1779010007000, 1779010007000, 1779010007000, 0),
    (920508, 'store_abac_complex', 8, 'WRITE', 'document', 'expired-vendor-share', 'viewer', 'user', 'vendor-a', '', 'seed-admin', 'seed-abac-008', 'MIGRATION', 1779010008000, 1779010008000, 1779010008000, 0),
    (920509, 'store_abac_complex', 9, 'WRITE', 'document', 'temporary-audit', 'viewer', 'user', 'auditor-temp', '', 'seed-admin', 'seed-abac-009', 'MIGRATION', 1779010009000, 1779010009000, 1779010009000, 0),
    (920510, 'store_abac_complex', 10, 'WRITE', 'api_endpoint', 'sensitive-export', 'caller', 'user', 'mallory', '', 'seed-admin', 'seed-abac-010', 'MIGRATION', 1779010010000, 1779010010000, 1779010010000, 0);


-- ============================================================
-- 3. RBAC：角色权限模型
-- ============================================================
-- 测试点：
--   - role 作为一等对象
--   - organization/project 持有 role relation
--   - assignee from xxx_role 实现角色权限传播
--   - group#member 作为角色成员
--   - 项目权限从组织 parent 继承
-- ============================================================

INSERT INTO fga_store
    (id, store_id, name, description, current_model_id, current_zookie, status, create_time, update_time, is_deleted)
VALUES
    (930001, 'store_rbac_complex', 'RBAC Complex Store', '复杂 RBAC 测试数据：角色对象、角色成员、组织/项目权限传播', 'model_rbac_complex_v1', 13, 0, 1779020000000, 1779020000000, 0);

INSERT INTO fga_auth_model
    (id, store_id, model_id, schema_version, dsl_text, status, description, create_time, update_time, publish_time, is_deleted)
VALUES
    (930010, 'store_rbac_complex', 'model_rbac_complex_v1', '1.1',
     'model schema 1.1\n\ntype user\n\ntype group\n  relations\n    define member as self\n\ntype role\n  relations\n    define assignee as self\n\ntype organization\n  relations\n    define member as self\n    define admin_role as self\n    define audit_role as self\n    define finance_role as self\n    define can_manage as assignee from admin_role\n    define can_audit as assignee from audit_role or assignee from admin_role\n    define can_pay as assignee from finance_role\n\ntype project\n  relations\n    define parent as self\n    define deploy_role as self\n    define viewer_role as self\n    define can_deploy as assignee from deploy_role\n    define can_view as assignee from viewer_role or can_manage from parent',
     1, 'RBAC 模型：角色是对象，用户/用户组分配到 role#assignee，再由组织和项目引用角色', 1779020000000, 1779020000000, 1779020000000, 0);

INSERT INTO fga_type_definition
    (id, store_id, model_id, type, sort_order, create_time, update_time, is_deleted)
VALUES
    (930101, 'store_rbac_complex', 'model_rbac_complex_v1', 'user', 0, 1779020000000, 1779020000000, 0),
    (930102, 'store_rbac_complex', 'model_rbac_complex_v1', 'group', 1, 1779020000000, 1779020000000, 0),
    (930103, 'store_rbac_complex', 'model_rbac_complex_v1', 'role', 2, 1779020000000, 1779020000000, 0),
    (930104, 'store_rbac_complex', 'model_rbac_complex_v1', 'organization', 3, 1779020000000, 1779020000000, 0),
    (930105, 'store_rbac_complex', 'model_rbac_complex_v1', 'project', 4, 1779020000000, 1779020000000, 0);

INSERT INTO fga_relation_definition
    (id, type_definition_id, relation_name, rewrite_expression, relation_type, create_time, update_time, is_deleted)
VALUES
    (930201, 930102, 'member', 'self', 0, 1779020000000, 1779020000000, 0),
    (930211, 930103, 'assignee', 'self', 0, 1779020000000, 1779020000000, 0),
    (930221, 930104, 'member', 'self', 0, 1779020000000, 1779020000000, 0),
    (930222, 930104, 'admin_role', 'self', 0, 1779020000000, 1779020000000, 0),
    (930223, 930104, 'audit_role', 'self', 0, 1779020000000, 1779020000000, 0),
    (930224, 930104, 'finance_role', 'self', 0, 1779020000000, 1779020000000, 0),
    (930225, 930104, 'can_manage', 'assignee from admin_role', 2, 1779020000000, 1779020000000, 0),
    (930226, 930104, 'can_audit', 'assignee from audit_role or assignee from admin_role', 3, 1779020000000, 1779020000000, 0),
    (930227, 930104, 'can_pay', 'assignee from finance_role', 2, 1779020000000, 1779020000000, 0),
    (930231, 930105, 'parent', 'self', 0, 1779020000000, 1779020000000, 0),
    (930232, 930105, 'deploy_role', 'self', 0, 1779020000000, 1779020000000, 0),
    (930233, 930105, 'viewer_role', 'self', 0, 1779020000000, 1779020000000, 0),
    (930234, 930105, 'can_deploy', 'assignee from deploy_role', 2, 1779020000000, 1779020000000, 0),
    (930235, 930105, 'can_view', 'assignee from viewer_role or can_manage from parent', 3, 1779020000000, 1779020000000, 0);

INSERT INTO fga_type_restriction
    (id, relation_definition_id, allowed_type, allowed_subject_relation, create_time, update_time, is_deleted)
VALUES
    (930301, 930201, 'user', '', 1779020000000, 1779020000000, 0),
    (930311, 930211, 'user', '', 1779020000000, 1779020000000, 0),
    (930312, 930211, 'group', 'member', 1779020000000, 1779020000000, 0),
    (930321, 930221, 'user', '', 1779020000000, 1779020000000, 0),
    (930322, 930222, 'role', '', 1779020000000, 1779020000000, 0),
    (930323, 930223, 'role', '', 1779020000000, 1779020000000, 0),
    (930324, 930224, 'role', '', 1779020000000, 1779020000000, 0),
    (930331, 930231, 'organization', '', 1779020000000, 1779020000000, 0),
    (930332, 930232, 'role', '', 1779020000000, 1779020000000, 0),
    (930333, 930233, 'role', '', 1779020000000, 1779020000000, 0);

INSERT INTO fga_relation_tuple
    (id, store_id, object_type, object_id, relation, subject_type, subject_id, subject_relation, is_wildcard,
     condition_definition_id, condition_name, condition_context, expires_at, zookie, create_time, update_time, is_deleted)
VALUES
    (930401, 'store_rbac_complex', 'group', 'security-team', 'member', 'user', 'bob', '', 0, NULL, NULL, NULL, NULL, 1, 1779020001000, 1779020001000, 0),
    (930402, 'store_rbac_complex', 'group', 'finance-team', 'member', 'user', 'carol', '', 0, NULL, NULL, NULL, NULL, 2, 1779020002000, 1779020002000, 0),
    (930403, 'store_rbac_complex', 'role', 'platform_admin', 'assignee', 'user', 'alice', '', 0, NULL, NULL, NULL, NULL, 3, 1779020003000, 1779020003000, 0),
    (930404, 'store_rbac_complex', 'role', 'security_auditor', 'assignee', 'group', 'security-team', 'member', 0, NULL, NULL, NULL, NULL, 4, 1779020004000, 1779020004000, 0),
    (930405, 'store_rbac_complex', 'role', 'finance_operator', 'assignee', 'group', 'finance-team', 'member', 0, NULL, NULL, NULL, NULL, 5, 1779020005000, 1779020005000, 0),
    (930406, 'store_rbac_complex', 'role', 'release_engineer', 'assignee', 'user', 'diana', '', 0, NULL, NULL, NULL, NULL, 6, 1779020006000, 1779020006000, 0),
    (930407, 'store_rbac_complex', 'organization', 'acme', 'admin_role', 'role', 'platform_admin', '', 0, NULL, NULL, NULL, NULL, 7, 1779020007000, 1779020007000, 0),
    (930408, 'store_rbac_complex', 'organization', 'acme', 'audit_role', 'role', 'security_auditor', '', 0, NULL, NULL, NULL, NULL, 8, 1779020008000, 1779020008000, 0),
    (930409, 'store_rbac_complex', 'organization', 'acme', 'finance_role', 'role', 'finance_operator', '', 0, NULL, NULL, NULL, NULL, 9, 1779020009000, 1779020009000, 0),
    (930410, 'store_rbac_complex', 'project', 'zus-api', 'parent', 'organization', 'acme', '', 0, NULL, NULL, NULL, NULL, 10, 1779020010000, 1779020010000, 0),
    (930411, 'store_rbac_complex', 'project', 'zus-api', 'deploy_role', 'role', 'release_engineer', '', 0, NULL, NULL, NULL, NULL, 11, 1779020011000, 1779020011000, 0),
    (930412, 'store_rbac_complex', 'role', 'project_viewer', 'assignee', 'user', 'erin', '', 0, NULL, NULL, NULL, NULL, 12, 1779020012000, 1779020012000, 0),
    (930413, 'store_rbac_complex', 'project', 'zus-api', 'viewer_role', 'role', 'project_viewer', '', 0, NULL, NULL, NULL, NULL, 13, 1779020013000, 1779020013000, 0);

INSERT INTO fga_tuple_changelog
    (id, store_id, zookie, operation, object_type, object_id, relation, subject_type, subject_id, subject_relation,
     operator_id, request_id, source, operation_time, create_time, update_time, is_deleted)
VALUES
    (930501, 'store_rbac_complex', 1, 'WRITE', 'group', 'security-team', 'member', 'user', 'bob', '', 'seed-admin', 'seed-rbac-001', 'MIGRATION', 1779020001000, 1779020001000, 1779020001000, 0),
    (930502, 'store_rbac_complex', 2, 'WRITE', 'group', 'finance-team', 'member', 'user', 'carol', '', 'seed-admin', 'seed-rbac-002', 'MIGRATION', 1779020002000, 1779020002000, 1779020002000, 0),
    (930503, 'store_rbac_complex', 3, 'WRITE', 'role', 'platform_admin', 'assignee', 'user', 'alice', '', 'seed-admin', 'seed-rbac-003', 'MIGRATION', 1779020003000, 1779020003000, 1779020003000, 0),
    (930504, 'store_rbac_complex', 4, 'WRITE', 'role', 'security_auditor', 'assignee', 'group', 'security-team', 'member', 'seed-admin', 'seed-rbac-004', 'MIGRATION', 1779020004000, 1779020004000, 1779020004000, 0),
    (930505, 'store_rbac_complex', 5, 'WRITE', 'role', 'finance_operator', 'assignee', 'group', 'finance-team', 'member', 'seed-admin', 'seed-rbac-005', 'MIGRATION', 1779020005000, 1779020005000, 1779020005000, 0),
    (930506, 'store_rbac_complex', 6, 'WRITE', 'role', 'release_engineer', 'assignee', 'user', 'diana', '', 'seed-admin', 'seed-rbac-006', 'MIGRATION', 1779020006000, 1779020006000, 1779020006000, 0),
    (930507, 'store_rbac_complex', 7, 'WRITE', 'organization', 'acme', 'admin_role', 'role', 'platform_admin', '', 'seed-admin', 'seed-rbac-007', 'MIGRATION', 1779020007000, 1779020007000, 1779020007000, 0),
    (930508, 'store_rbac_complex', 8, 'WRITE', 'organization', 'acme', 'audit_role', 'role', 'security_auditor', '', 'seed-admin', 'seed-rbac-008', 'MIGRATION', 1779020008000, 1779020008000, 1779020008000, 0),
    (930509, 'store_rbac_complex', 9, 'WRITE', 'organization', 'acme', 'finance_role', 'role', 'finance_operator', '', 'seed-admin', 'seed-rbac-009', 'MIGRATION', 1779020009000, 1779020009000, 1779020009000, 0),
    (930510, 'store_rbac_complex', 10, 'WRITE', 'project', 'zus-api', 'parent', 'organization', 'acme', '', 'seed-admin', 'seed-rbac-010', 'MIGRATION', 1779020010000, 1779020010000, 1779020010000, 0),
    (930511, 'store_rbac_complex', 11, 'WRITE', 'project', 'zus-api', 'deploy_role', 'role', 'release_engineer', '', 'seed-admin', 'seed-rbac-011', 'MIGRATION', 1779020011000, 1779020011000, 1779020011000, 0),
    (930512, 'store_rbac_complex', 12, 'WRITE', 'role', 'project_viewer', 'assignee', 'user', 'erin', '', 'seed-admin', 'seed-rbac-012', 'MIGRATION', 1779020012000, 1779020012000, 1779020012000, 0),
    (930513, 'store_rbac_complex', 13, 'WRITE', 'project', 'zus-api', 'viewer_role', 'role', 'project_viewer', '', 'seed-admin', 'seed-rbac-013', 'MIGRATION', 1779020013000, 1779020013000, 1779020013000, 0);


-- ============================================================
-- 4. ReBAC：关系图授权模型
-- ============================================================
-- 测试点：
--   - group 层级 member from parent
--   - folder/document parent 链路传播
--   - computed userset：viewer 包含 editor / owner
--   - TTU：viewer from parent
--   - exclusion：restricted_viewer = viewer but not blocked
--   - intersection：approver = reviewer and owner
--   - wildcard：shared_public
-- ============================================================

INSERT INTO fga_store
    (id, store_id, name, description, current_model_id, current_zookie, status, create_time, update_time, is_deleted)
VALUES
    (940001, 'store_rebac_complex', 'ReBAC Complex Store', '复杂 ReBAC 测试数据：组织、分组、层级资源、传播、排除与交集', 'model_rebac_complex_v1', 16, 0, 1779030000000, 1779030000000, 0);

INSERT INTO fga_auth_model
    (id, store_id, model_id, schema_version, dsl_text, status, description, create_time, update_time, publish_time, is_deleted)
VALUES
    (940010, 'store_rebac_complex', 'model_rebac_complex_v1', '1.1',
     'model schema 1.1\n\ntype user\n\ntype group\n  relations\n    define parent as self\n    define member as self or member from parent\n\ntype organization\n  relations\n    define admin as self\n    define member as self\n\ntype folder\n  relations\n    define parent as self\n    define owner as self\n    define editor as self or owner\n    define viewer as self or editor or viewer from parent\n\ntype document\n  relations\n    define parent as self\n    define owner as self\n    define editor as self or owner\n    define viewer as self or editor or viewer from parent\n    define blocked as self\n    define restricted_viewer as viewer but not blocked\n    define reviewer as self\n    define approver as reviewer and owner\n    define shared_public as self',
     1, 'ReBAC 模型：资源层级、用户组层级、TTU、computed userset、排除和交集', 1779030000000, 1779030000000, 1779030000000, 0);

INSERT INTO fga_type_definition
    (id, store_id, model_id, type, sort_order, create_time, update_time, is_deleted)
VALUES
    (940101, 'store_rebac_complex', 'model_rebac_complex_v1', 'user', 0, 1779030000000, 1779030000000, 0),
    (940102, 'store_rebac_complex', 'model_rebac_complex_v1', 'group', 1, 1779030000000, 1779030000000, 0),
    (940103, 'store_rebac_complex', 'model_rebac_complex_v1', 'organization', 2, 1779030000000, 1779030000000, 0),
    (940104, 'store_rebac_complex', 'model_rebac_complex_v1', 'folder', 3, 1779030000000, 1779030000000, 0),
    (940105, 'store_rebac_complex', 'model_rebac_complex_v1', 'document', 4, 1779030000000, 1779030000000, 0);

INSERT INTO fga_relation_definition
    (id, type_definition_id, relation_name, rewrite_expression, relation_type, create_time, update_time, is_deleted)
VALUES
    (940201, 940102, 'parent', 'self', 0, 1779030000000, 1779030000000, 0),
    (940202, 940102, 'member', 'self or member from parent', 3, 1779030000000, 1779030000000, 0),
    (940211, 940103, 'admin', 'self', 0, 1779030000000, 1779030000000, 0),
    (940212, 940103, 'member', 'self', 0, 1779030000000, 1779030000000, 0),
    (940221, 940104, 'parent', 'self', 0, 1779030000000, 1779030000000, 0),
    (940222, 940104, 'owner', 'self', 0, 1779030000000, 1779030000000, 0),
    (940223, 940104, 'editor', 'self or owner', 3, 1779030000000, 1779030000000, 0),
    (940224, 940104, 'viewer', 'self or editor or viewer from parent', 3, 1779030000000, 1779030000000, 0),
    (940231, 940105, 'parent', 'self', 0, 1779030000000, 1779030000000, 0),
    (940232, 940105, 'owner', 'self', 0, 1779030000000, 1779030000000, 0),
    (940233, 940105, 'editor', 'self or owner', 3, 1779030000000, 1779030000000, 0),
    (940234, 940105, 'viewer', 'self or editor or viewer from parent', 3, 1779030000000, 1779030000000, 0),
    (940235, 940105, 'blocked', 'self', 0, 1779030000000, 1779030000000, 0),
    (940236, 940105, 'restricted_viewer', 'viewer but not blocked', 3, 1779030000000, 1779030000000, 0),
    (940237, 940105, 'reviewer', 'self', 0, 1779030000000, 1779030000000, 0),
    (940238, 940105, 'approver', 'reviewer and owner', 3, 1779030000000, 1779030000000, 0),
    (940239, 940105, 'shared_public', 'self', 0, 1779030000000, 1779030000000, 0);

INSERT INTO fga_type_restriction
    (id, relation_definition_id, allowed_type, allowed_subject_relation, create_time, update_time, is_deleted)
VALUES
    (940301, 940201, 'group', '', 1779030000000, 1779030000000, 0),
    (940302, 940202, 'user', '', 1779030000000, 1779030000000, 0),
    (940303, 940211, 'user', '', 1779030000000, 1779030000000, 0),
    (940304, 940212, 'user', '', 1779030000000, 1779030000000, 0),
    (940311, 940221, 'folder', '', 1779030000000, 1779030000000, 0),
    (940312, 940222, 'user', '', 1779030000000, 1779030000000, 0),
    (940313, 940223, 'user', '', 1779030000000, 1779030000000, 0),
    (940314, 940223, 'group', 'member', 1779030000000, 1779030000000, 0),
    (940315, 940224, 'user', '', 1779030000000, 1779030000000, 0),
    (940316, 940224, 'group', 'member', 1779030000000, 1779030000000, 0),
    (940321, 940231, 'folder', '', 1779030000000, 1779030000000, 0),
    (940322, 940232, 'user', '', 1779030000000, 1779030000000, 0),
    (940323, 940233, 'user', '', 1779030000000, 1779030000000, 0),
    (940324, 940233, 'group', 'member', 1779030000000, 1779030000000, 0),
    (940325, 940234, 'user', '', 1779030000000, 1779030000000, 0),
    (940326, 940234, 'group', 'member', 1779030000000, 1779030000000, 0),
    (940327, 940235, 'user', '', 1779030000000, 1779030000000, 0),
    (940328, 940237, 'user', '', 1779030000000, 1779030000000, 0),
    (940329, 940239, 'user', '', 1779030000000, 1779030000000, 0);

INSERT INTO fga_relation_tuple
    (id, store_id, object_type, object_id, relation, subject_type, subject_id, subject_relation, is_wildcard,
     condition_definition_id, condition_name, condition_context, expires_at, zookie, create_time, update_time, is_deleted)
VALUES
    (940401, 'store_rebac_complex', 'group', 'platform', 'parent', 'group', 'engineering', '', 0, NULL, NULL, NULL, NULL, 1, 1779030001000, 1779030001000, 0),
    (940402, 'store_rebac_complex', 'group', 'engineering', 'member', 'user', 'erin', '', 0, NULL, NULL, NULL, NULL, 2, 1779030002000, 1779030002000, 0),
    (940403, 'store_rebac_complex', 'group', 'external', 'member', 'user', 'vendor-user', '', 0, NULL, NULL, NULL, NULL, 3, 1779030003000, 1779030003000, 0),
    (940404, 'store_rebac_complex', 'organization', 'acme', 'admin', 'user', 'alice', '', 0, NULL, NULL, NULL, NULL, 4, 1779030004000, 1779030004000, 0),
    (940405, 'store_rebac_complex', 'organization', 'acme', 'member', 'user', 'bob', '', 0, NULL, NULL, NULL, NULL, 5, 1779030005000, 1779030005000, 0),
    (940406, 'store_rebac_complex', 'folder', 'root', 'viewer', 'group', 'platform', 'member', 0, NULL, NULL, NULL, NULL, 6, 1779030006000, 1779030006000, 0),
    (940407, 'store_rebac_complex', 'folder', 'finance', 'parent', 'folder', 'root', '', 0, NULL, NULL, NULL, NULL, 7, 1779030007000, 1779030007000, 0),
    (940408, 'store_rebac_complex', 'folder', 'finance', 'editor', 'user', 'frank', '', 0, NULL, NULL, NULL, NULL, 8, 1779030008000, 1779030008000, 0),
    (940409, 'store_rebac_complex', 'document', 'roadmap', 'parent', 'folder', 'finance', '', 0, NULL, NULL, NULL, NULL, 9, 1779030009000, 1779030009000, 0),
    (940410, 'store_rebac_complex', 'document', 'roadmap', 'owner', 'user', 'grace', '', 0, NULL, NULL, NULL, NULL, 10, 1779030010000, 1779030010000, 0),
    (940411, 'store_rebac_complex', 'document', 'roadmap', 'reviewer', 'user', 'grace', '', 0, NULL, NULL, NULL, NULL, 11, 1779030011000, 1779030011000, 0),
    (940412, 'store_rebac_complex', 'document', 'roadmap', 'blocked', 'user', 'mallory', '', 0, NULL, NULL, NULL, NULL, 12, 1779030012000, 1779030012000, 0),
    (940413, 'store_rebac_complex', 'document', 'roadmap', 'viewer', 'group', 'external', 'member', 0, NULL, NULL, NULL, @FUTURE_EXPIRES_AT, 13, 1779030013000, 1779030013000, 0),
    (940414, 'store_rebac_complex', 'document', 'public-handbook', 'shared_public', 'user', '*', '', 1, NULL, NULL, NULL, NULL, 14, 1779030014000, 1779030014000, 0),
    (940415, 'store_rebac_complex', 'document', 'roadmap', 'viewer', 'user', 'mallory', '', 0, NULL, NULL, NULL, NULL, 15, 1779030015000, 1779030015000, 0),
    (940416, 'store_rebac_complex', 'document', 'legacy-brief', 'viewer', 'user', 'expired-user', '', 0, NULL, NULL, NULL, @PAST_EXPIRES_AT, 16, 1779030016000, 1779030016000, 0);

INSERT INTO fga_tuple_changelog
    (id, store_id, zookie, operation, object_type, object_id, relation, subject_type, subject_id, subject_relation,
     operator_id, request_id, source, operation_time, create_time, update_time, is_deleted)
VALUES
    (940501, 'store_rebac_complex', 1, 'WRITE', 'group', 'platform', 'parent', 'group', 'engineering', '', 'seed-admin', 'seed-rebac-001', 'MIGRATION', 1779030001000, 1779030001000, 1779030001000, 0),
    (940502, 'store_rebac_complex', 2, 'WRITE', 'group', 'engineering', 'member', 'user', 'erin', '', 'seed-admin', 'seed-rebac-002', 'MIGRATION', 1779030002000, 1779030002000, 1779030002000, 0),
    (940503, 'store_rebac_complex', 3, 'WRITE', 'group', 'external', 'member', 'user', 'vendor-user', '', 'seed-admin', 'seed-rebac-003', 'MIGRATION', 1779030003000, 1779030003000, 1779030003000, 0),
    (940504, 'store_rebac_complex', 4, 'WRITE', 'organization', 'acme', 'admin', 'user', 'alice', '', 'seed-admin', 'seed-rebac-004', 'MIGRATION', 1779030004000, 1779030004000, 1779030004000, 0),
    (940505, 'store_rebac_complex', 5, 'WRITE', 'organization', 'acme', 'member', 'user', 'bob', '', 'seed-admin', 'seed-rebac-005', 'MIGRATION', 1779030005000, 1779030005000, 1779030005000, 0),
    (940506, 'store_rebac_complex', 6, 'WRITE', 'folder', 'root', 'viewer', 'group', 'platform', 'member', 'seed-admin', 'seed-rebac-006', 'MIGRATION', 1779030006000, 1779030006000, 1779030006000, 0),
    (940507, 'store_rebac_complex', 7, 'WRITE', 'folder', 'finance', 'parent', 'folder', 'root', '', 'seed-admin', 'seed-rebac-007', 'MIGRATION', 1779030007000, 1779030007000, 1779030007000, 0),
    (940508, 'store_rebac_complex', 8, 'WRITE', 'folder', 'finance', 'editor', 'user', 'frank', '', 'seed-admin', 'seed-rebac-008', 'MIGRATION', 1779030008000, 1779030008000, 1779030008000, 0),
    (940509, 'store_rebac_complex', 9, 'WRITE', 'document', 'roadmap', 'parent', 'folder', 'finance', '', 'seed-admin', 'seed-rebac-009', 'MIGRATION', 1779030009000, 1779030009000, 1779030009000, 0),
    (940510, 'store_rebac_complex', 10, 'WRITE', 'document', 'roadmap', 'owner', 'user', 'grace', '', 'seed-admin', 'seed-rebac-010', 'MIGRATION', 1779030010000, 1779030010000, 1779030010000, 0),
    (940511, 'store_rebac_complex', 11, 'WRITE', 'document', 'roadmap', 'reviewer', 'user', 'grace', '', 'seed-admin', 'seed-rebac-011', 'MIGRATION', 1779030011000, 1779030011000, 1779030011000, 0),
    (940512, 'store_rebac_complex', 12, 'WRITE', 'document', 'roadmap', 'blocked', 'user', 'mallory', '', 'seed-admin', 'seed-rebac-012', 'MIGRATION', 1779030012000, 1779030012000, 1779030012000, 0),
    (940513, 'store_rebac_complex', 13, 'WRITE', 'document', 'roadmap', 'viewer', 'group', 'external', 'member', 'seed-admin', 'seed-rebac-013', 'MIGRATION', 1779030013000, 1779030013000, 1779030013000, 0),
    (940514, 'store_rebac_complex', 14, 'WRITE', 'document', 'public-handbook', 'shared_public', 'user', '*', '', 'seed-admin', 'seed-rebac-014', 'MIGRATION', 1779030014000, 1779030014000, 1779030014000, 0),
    (940515, 'store_rebac_complex', 15, 'WRITE', 'document', 'roadmap', 'viewer', 'user', 'mallory', '', 'seed-admin', 'seed-rebac-015', 'MIGRATION', 1779030015000, 1779030015000, 1779030015000, 0),
    (940516, 'store_rebac_complex', 16, 'WRITE', 'document', 'legacy-brief', 'viewer', 'user', 'expired-user', '', 'seed-admin', 'seed-rebac-016', 'MIGRATION', 1779030016000, 1779030016000, 1779030016000, 0);


-- ============================================================
-- 推荐手工验证样例
-- ============================================================
--
-- ACL:
--   Check user:alice document:design-spec#owner       -> true
--   Check user:bob   document:design-spec#viewer      -> true  (group#member -> editor -> viewer)
--   Check user:any   document:public-roadmap#public_viewer -> true (user:*)
--   Check user:erin  document:legacy-plan#viewer      -> false (expired)
--
-- ABAC:
--   Check user:alice document:payroll-q1#viewer with context {"current_hour":10} -> true
--   Check user:alice document:payroll-q1#viewer with context {"current_hour":22} -> false
--   Check user:bob   document:payroll-q1#viewer with context {"clearance":5} -> true
--   Check user:dave  api_endpoint:deploy-prod#caller with context {"client_ip":"10.0.1.25"} -> true
--   Check user:mallory api_endpoint:sensitive-export#caller with context {"risk_score":99} -> false
--
-- RBAC:
--   Check user:alice project:zus-api#can_view   -> true (org admin -> can_manage from parent)
--   Check user:bob   organization:acme#can_audit -> true (security-team#member -> security_auditor)
--   Check user:carol organization:acme#can_pay   -> true (finance-team#member -> finance_operator)
--   Check user:diana project:zus-api#can_deploy -> true (release_engineer role)
--
-- ReBAC:
--   Check user:erin  document:roadmap#viewer             -> true (group parent + folder parent propagation)
--   Check user:frank document:roadmap#viewer             -> true (folder editor -> folder viewer -> document viewer)
--   Check user:grace document:roadmap#approver           -> true (owner and reviewer)
--   Check user:mallory document:roadmap#restricted_viewer -> false (viewer but not blocked)
--   Check user:any document:public-handbook#shared_public -> true (user:*)
--
-- ============================================================
