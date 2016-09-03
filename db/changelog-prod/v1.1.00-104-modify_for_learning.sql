--liquibase formatted sql
--changeset opentone:v1.1.00-104-modify_for_learning

-- add columns
ALTER TABLE correspon ADD for_learning NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE rep_v_correspon ADD for_learning NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE project_custom_setting ADD use_learning NUMBER(1,0) DEFAULT 1 NOT NULL;
ALTER TABLE sys_pj ADD for_learning NUMBER(1,0) DEFAULT 0 NOT NULL;
ALTER TABLE user_profile ADD use_learning NUMBER(1,0) DEFAULT 1 NOT NULL;
-- replace vies
CREATE OR REPLACE VIEW v_correspon AS
SELECT
    c.id                            AS id,
    c.correspon_no                  AS correspon_no,
    ch.parent_correspon_id          AS parent_correspon_id,
    pc.correspon_no                 AS parent_correspon_no,
    c.project_id                    AS project_id,
    p.name_e                        AS project_name_e,
    c.from_correspon_group_id       AS from_correspon_group_id,
    c.previous_rev_correspon_id     AS previous_rev_correspon_id,
    pr.correspon_no                 AS previous_rev_correspon_no,
    c.from_correspon_group_name     AS from_correspon_group_name,
    c.correspon_type_id             AS correspon_type_id,
    c.correspon_type                AS correspon_type,
    c.correspon_type_name           AS correspon_type_name,
    c.allow_approver_to_browse      AS allow_approver_to_browse,
    c.force_to_use_workflow         AS force_to_use_workflow,
    c.correspon_access_control_flags AS correspon_access_control_flags,
    c.workflow_pattern_id           AS workflow_pattern_id,
    c.workflow_cd                   AS workflow_cd,
    c.workflow_pattern_name         AS workflow_pattern_name,
    c.for_learning                  AS for_learning,
    c.subject                       AS subject,
    c.body                          AS body,
    c.issued_at                     AS issued_at,
    c.issued_by                     AS issued_by,
    u3.emp_nm_e                     AS issued_by_name,
    c.correspon_status              AS correspon_status,
    c.reply_required                AS reply_required,
    c.deadline_for_reply            AS deadline_for_reply,
    c.requested_approval_at         AS requested_approval_at,
    c.workflow_status               AS workflow_status,
    c.custom_field1_id              AS custom_field1_id,
    c.custom_field1_label           AS custom_field1_label,
    c.custom_field1_value           AS custom_field1_value,
    c.custom_field2_id              AS custom_field2_id,
    c.custom_field2_label           AS custom_field2_label,
    c.custom_field2_value           AS custom_field2_value,
    c.custom_field3_id              AS custom_field3_id,
    c.custom_field3_label           AS custom_field3_label,
    c.custom_field3_value           AS custom_field3_value,
    c.custom_field4_id              AS custom_field4_id,
    c.custom_field4_label           AS custom_field4_label,
    c.custom_field4_value           AS custom_field4_value,
    c.custom_field5_id              AS custom_field5_id,
    c.custom_field5_label           AS custom_field5_label,
    c.custom_field5_value           AS custom_field5_value,
    c.custom_field6_id              AS custom_field6_id,
    c.custom_field6_label           AS custom_field6_label,
    c.custom_field6_value           AS custom_field6_value,
    c.custom_field7_id              AS custom_field7_id,
    c.custom_field7_label           AS custom_field7_label,
    c.custom_field7_value           AS custom_field7_value,
    c.custom_field8_id              AS custom_field8_id,
    c.custom_field8_label           AS custom_field8_label,
    c.custom_field8_value           AS custom_field8_value,
    c.custom_field9_id              AS custom_field9_id,
    c.custom_field9_label           AS custom_field9_label,
    c.custom_field9_value           AS custom_field9_value,
    c.custom_field10_id             AS custom_field10_id,
    c.custom_field10_label          AS custom_field10_label,
    c.custom_field10_value          AS custom_field10_value,
    c.created_by                    AS created_by,
    u1.emp_nm_e                     AS created_by_name,
    c.created_at                    AS created_at,
    c.updated_by                    AS updated_by,
    u2.emp_nm_e                     AS updated_by_name,
    c.updated_at                    AS updated_at,
    c.version_no                    AS version_no,
    c.delete_no                     AS delete_no
FROM rep_v_correspon c
   LEFT OUTER JOIN v_project p
       ON c.project_id = p.project_id
   LEFT OUTER JOIN correspon_hierarchy ch
       ON c.id = ch.child_correspon_id
           AND ch.delete_no = 0
   LEFT OUTER JOIN correspon pc
       ON ch.parent_correspon_id = pc.id
           AND pc.delete_no = 0
   LEFT OUTER JOIN correspon pr
       ON c.previous_rev_correspon_id = pr.id
           AND pr.delete_no = 0
   LEFT OUTER JOIN v_sys_users u1
       ON c.created_by = u1.emp_no
   LEFT OUTER JOIN v_sys_users u2
       ON c.updated_by = u2.emp_no
   LEFT OUTER JOIN v_sys_users u3
       ON c.issued_by = u3.emp_no
WHERE c.delete_no = 0
;
CREATE OR REPLACE VIEW v_correspon_detail AS
SELECT
    c.id                            AS id,
    c.correspon_no                  AS correspon_no,
    ch.parent_correspon_id          AS parent_correspon_id,
    pc.correspon_no                 AS parent_correspon_no,
    c.project_id                    AS project_id,
    p.name_e                        AS project_name_e,
    c.from_correspon_group_id       AS from_correspon_group_id,
    c.previous_rev_correspon_id     AS previous_rev_correspon_id,
    pr.correspon_no                 AS previous_rev_correspon_no,
    c.from_correspon_group_name     AS from_correspon_group_name,
    c.correspon_type_id             AS correspon_type_id,
    c.correspon_type                AS correspon_type,
    c.correspon_type_name           AS correspon_type_name,
    c.allow_approver_to_browse      AS allow_approver_to_browse,
    c.force_to_use_workflow         AS force_to_use_workflow,
    c.correspon_access_control_flags AS correspon_access_control_flags,
    c.workflow_pattern_id           AS workflow_pattern_id,
    c.workflow_cd                   AS workflow_cd,
    c.workflow_pattern_name         AS workflow_pattern_name,
    c.for_learning                  AS for_learning,
    c.subject                       AS subject,
    c.body                          AS body,
    c.issued_at                     AS issued_at,
    c.issued_by                     AS issued_by,
    u3.emp_nm_e                     AS issued_by_name,
    c.correspon_status              AS correspon_status,
    c.reply_required                AS reply_required,
    c.deadline_for_reply            AS deadline_for_reply,
    c.requested_approval_at         AS requested_approval_at,
    c.workflow_status               AS workflow_status,
    c.custom_field1_id              AS custom_field1_id,
    c.custom_field1_label           AS custom_field1_label,
    c.custom_field1_value           AS custom_field1_value,
    c.custom_field2_id              AS custom_field2_id,
    c.custom_field2_label           AS custom_field2_label,
    c.custom_field2_value           AS custom_field2_value,
    c.custom_field3_id              AS custom_field3_id,
    c.custom_field3_label           AS custom_field3_label,
    c.custom_field3_value           AS custom_field3_value,
    c.custom_field4_id              AS custom_field4_id,
    c.custom_field4_label           AS custom_field4_label,
    c.custom_field4_value           AS custom_field4_value,
    c.custom_field5_id              AS custom_field5_id,
    c.custom_field5_label           AS custom_field5_label,
    c.custom_field5_value           AS custom_field5_value,
    c.custom_field6_id              AS custom_field6_id,
    c.custom_field6_label           AS custom_field6_label,
    c.custom_field6_value           AS custom_field6_value,
    c.custom_field7_id              AS custom_field7_id,
    c.custom_field7_label           AS custom_field7_label,
    c.custom_field7_value           AS custom_field7_value,
    c.custom_field8_id              AS custom_field8_id,
    c.custom_field8_label           AS custom_field8_label,
    c.custom_field8_value           AS custom_field8_value,
    c.custom_field9_id              AS custom_field9_id,
    c.custom_field9_label           AS custom_field9_label,
    c.custom_field9_value           AS custom_field9_value,
    c.custom_field10_id             AS custom_field10_id,
    c.custom_field10_label          AS custom_field10_label,
    c.custom_field10_value          AS custom_field10_value,
    at1.id                          AS file1_id,
    at1.file_id                     AS file1_file_id,
    at1.file_name                   AS file1_file_name,
    at1.file_type                   AS file1_file_type,
    at1.org_extracted_text          AS file1_org_extracted_text,
    at1.extracted_text              AS file1_extracted_text,
    at2.id                          AS file2_id,
    at2.file_id                     AS file2_file_id,
    at2.file_name                   AS file2_file_name,
    at2.file_type                   AS file2_file_type,
    at2.org_extracted_text          AS file2_org_extracted_text,
    at2.extracted_text              AS file2_extracted_text,
    at3.id                          AS file3_id,
    at3.file_id                     AS file3_file_id,
    at3.file_name                   AS file3_file_name,
    at3.file_type                   AS file3_file_type,
    at3.org_extracted_text          AS file3_org_extracted_text,
    at3.extracted_text              AS file3_extracted_text,
    at4.id                          AS file4_id,
    at4.file_id                     AS file4_file_id,
    at4.file_name                   AS file4_file_name,
    at4.file_type                   AS file4_file_type,
    at4.org_extracted_text          AS file4_org_extracted_text,
    at4.extracted_text              AS file4_extracted_text,
    at5.id                          AS file5_id,
    at5.file_id                     AS file5_file_id,
    at5.file_name                   AS file5_file_name,
    at5.file_type                   AS file5_file_type,
    at5.org_extracted_text          AS file5_org_extracted_text,
    at5.extracted_text              AS file5_extracted_text,
    c.created_by                    AS created_by,
    u1.emp_nm_e                     AS created_by_name,
    c.created_at                    AS created_at,
    c.updated_by                    AS updated_by,
    u2.emp_nm_e                     AS updated_by_name,
    c.updated_at                    AS updated_at,
    c.version_no                    AS version_no,
    c.delete_no                     AS delete_no
FROM rep_v_correspon c
   LEFT OUTER JOIN v_project p
       ON c.project_id = p.project_id
   LEFT OUTER JOIN correspon_hierarchy ch
       ON c.id = ch.child_correspon_id
           AND ch.delete_no = 0
   LEFT OUTER JOIN correspon pc
       ON ch.parent_correspon_id = pc.id
           AND pc.delete_no = 0
   LEFT OUTER JOIN correspon pr
       ON c.previous_rev_correspon_id = pr.id
           AND pr.delete_no = 0
   LEFT OUTER JOIN v_sys_users u1
       ON c.created_by = u1.emp_no
   LEFT OUTER JOIN v_sys_users u2
       ON c.updated_by = u2.emp_no
   LEFT OUTER JOIN v_sys_users u3
       ON c.issued_by = u3.emp_no
   LEFT OUTER JOIN v_attachment at1
       ON c.id = at1.correspon_id AND at1.no = 1
   LEFT OUTER JOIN v_attachment at2
       ON c.id = at2.correspon_id AND at2.no = 2
   LEFT OUTER JOIN v_attachment at3
       ON c.id = at3.correspon_id AND at3.no = 3
   LEFT OUTER JOIN v_attachment at4
       ON c.id = at4.correspon_id AND at4.no = 4
   LEFT OUTER JOIN v_attachment at5
       ON c.id = at5.correspon_id AND at5.no = 5
WHERE c.delete_no = 0
;
CREATE OR REPLACE VIEW v_system_project AS
  SELECT
    pj.pj_id               AS project_id,
    pj.cl_nm_e             AS client_name_e,
    pj.cl_nm_j             AS client_name_j,
    pj.pj_nm_e             AS name_e,
    pj.pj_nm_j             AS name_j,
    pj.for_learning        AS for_learning
  FROM sys_pj pj
    INNER JOIN sys_pj_auth pa
      ON pa.pj_id = pj.pj_id
     AND pa.apl_sys_aprv_flg = 'X'
;
CREATE OR REPLACE VIEW v_project AS
  SELECT
    pj.pj_id               AS project_id,
    pj.cl_nm_e             AS client_name_e,
    pj.cl_nm_j             AS client_name_j,
    pj.pj_nm_e             AS name_e,
    pj.pj_nm_j             AS name_j,
    pj.for_learning        AS for_learning
  FROM
    sys_pj pj
;
CREATE OR REPLACE VIEW v_user AS
  SELECT
    u.emp_no                AS emp_no,
    u.emp_last_nm           AS last_name,
    u.emp_nm_e              AS name_e,
    u.emp_nm_j              AS name_j,
    u.sys_adm_flg           AS sys_admin_flg,
    u.id_created_dt         AS emp_created_at,
    u.emp_email_addr        AS email_address,
    --
    -- user profile
    up.id                   AS user_profile_id,
    up.default_project_id   AS default_project_id,
    pj.name_e               AS default_project_name_e,
    up.last_logged_in_at    AS last_logged_in_at,
    up.feed_key             AS feed_key,
    NVL(up.use_learning, 0) AS use_learning,
    up.version_no           AS user_profile_version_no
  FROM
    v_sys_users u
      LEFT OUTER JOIN user_profile up
        ON u.emp_no = up.emp_no
       AND up.delete_no = 0
      LEFT OUTER JOIN v_project pj
        ON up.default_project_id = pj.project_id
;

