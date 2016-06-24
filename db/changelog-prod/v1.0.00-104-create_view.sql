--liquibase formatted sql
--changeset opentone:v1.0.00-104-create_view

CREATE OR REPLACE VIEW v_sys_users AS
  SELECT
    u.cpny_id         AS cpny_id,
    u.emp_no          AS emp_no,
    u.emp_last_nm     AS emp_last_nm,
    u.emp_nm_e        AS emp_nm_e,
    u.emp_nm_j        AS emp_nm_j,
    u.emp_email_addr  AS emp_email_addr,
    u.sys_adm_flg     AS sys_adm_flg,
    u.id_created_dt   AS id_created_dt
  FROM
    sys_users u
;

CREATE OR REPLACE VIEW v_project AS
  SELECT
    pj.pj_id               AS project_id,
    pj.cl_nm_e             AS client_name_e,
    pj.cl_nm_j             AS client_name_j,
    pj.pj_nm_e             AS name_e,
    pj.pj_nm_j             AS name_j
  FROM
    sys_pj pj
;

CREATE OR REPLACE VIEW v_user AS
  SELECT
    u.emp_no              AS emp_no,
    u.emp_last_nm         AS last_name,
    u.emp_nm_e            AS name_e,
    u.emp_nm_j            AS name_j,
    u.sys_adm_flg         AS sys_admin_flg,
    u.id_created_dt       AS emp_created_at,
    u.emp_email_addr      AS email_address,
    --
    -- user profile
    up.id                 AS user_profile_id,
    up.default_project_id AS default_project_id,
    pj.name_e             AS default_project_name_e,
    up.last_logged_in_at  AS last_logged_in_at,
    up.feed_key           AS feed_key,
    up.version_no         AS user_profile_version_no
  FROM
    v_sys_users u
      LEFT OUTER JOIN user_profile up
        ON u.emp_no = up.emp_no
       AND up.delete_no = 0
      LEFT OUTER JOIN v_project pj
        ON up.default_project_id = pj.project_id
;

CREATE OR REPLACE VIEW v_system_project AS
  SELECT
    pj.pj_id               AS project_id,
    pj.cl_nm_e             AS client_name_e,
    pj.cl_nm_j             AS client_name_j,
    pj.pj_nm_e             AS name_e,
    pj.pj_nm_j             AS name_j
  FROM sys_pj pj
    INNER JOIN sys_pj_auth pa
      ON pa.pj_id = pj.pj_id
     AND pa.apl_sys_aprv_flg = 'X'
;

CREATE OR REPLACE VIEW v_project_user AS
  SELECT
    p.project_id                  AS project_id,
    u.emp_no                      AS emp_no,
    u.emp_last_nm                 AS emp_last_name,
    u.emp_nm_e                    AS emp_name_e,
    pu.security_lvl               AS security_level,
    pu.pj_adm_flg                 AS project_admin_flg,
    pc.id                         AS project_company_id,
    --
    -- company
    c.company_cd                  AS company_cd,
    c.name                        AS company_name,
    --
    -- project-user profile
    up.id                         AS project_user_profile_id,
    up.role                       AS role,
    up.default_correspon_group_id AS default_correspon_group_id,
    au.name                       AS default_correspon_group_name
  FROM 
    v_system_project p
     INNER JOIN sys_pj_users pu
       ON p.project_id = pu.pj_id
     INNER JOIN v_sys_users u
       ON pu.emp_no = u.emp_no
     LEFT OUTER JOIN company_user cu
       ON u.emp_no = cu.emp_no
      AND p.project_id = cu.project_id
      AND cu.delete_no = 0
     LEFT OUTER JOIN project_company pc
       ON cu.project_company_id = pc.id
      AND pc.delete_no = 0
     LEFT OUTER JOIN company c
       ON pc.company_id = c.id
      AND c.delete_no = 0
     LEFT OUTER JOIN project_user_profile up
       ON p.project_id = up.project_id
      AND u.emp_no = up.emp_no
      AND up.delete_no = 0
     LEFT OUTER JOIN correspon_group au
       ON up.default_correspon_group_id = au.id
      AND au.delete_no = 0
 ;

CREATE OR REPLACE VIEW v_custom_field AS
  SELECT
    c.id                      AS id,
    c.no                      AS no,
    c.label                   AS label,
    c.order_no                AS order_no,
    c.use_whole               AS use_whole,
    c.created_by              AS created_by,
    c.created_by_name         AS created_by_name,
    c.created_at              AS created_at,
    c.updated_by              AS updated_by,
    c.updated_by_name         AS updated_by_name,
    c.updated_at              AS updated_at,
    c.version_no              AS version_no,
    c.delete_no               AS delete_no
  FROM (
      SELECT
        c.id                             AS id,
        c.order_no                       AS no,
        c.label                          AS label,
        c.order_no                       AS order_no,
        c.use_whole                      AS use_whole,
        c.created_by                     AS created_by,
        u1.emp_nm_e                      AS created_by_name,
        c.created_at                     AS created_at,
        c.updated_by                     AS updated_by,
        u2.emp_nm_e                      AS updated_by_name,
        c.updated_at                     AS updated_at,
        c.version_no                     AS version_no,
        c.delete_no                      AS delete_no
      FROM
        custom_field c
         LEFT OUTER JOIN v_sys_users u1
           ON c.created_by = u1.emp_no
         LEFT OUTER JOIN v_sys_users u2
           ON c.updated_by = u2.emp_no
      WHERE c.delete_no = 0
      ) c
;

CREATE OR REPLACE VIEW v_project_custom_field AS
  SELECT
    c.id                      AS id,
    c.no                      AS no,
    c.project_custom_field_id AS project_custom_field_id,
    c.project_id              AS project_id,
    c.project_name_e          AS project_name_e,
    c.label                   AS label,
    c.order_no                AS order_no,
    c.use_whole               AS use_whole,
    c.created_by              AS created_by,
    c.created_by_name         AS created_by_name,
    c.created_at              AS created_at,
    c.updated_by              AS updated_by,
    c.updated_by_name         AS updated_by_name,
    c.updated_at              AS updated_at,
    c.pc_created_by           AS pc_created_by,
    c.pc_created_by_name      AS pc_created_by_name,
    c.pc_created_at           AS pc_created_at,
    c.pc_updated_by           AS pc_updated_by,
    c.pc_updated_by_name      AS pc_updated_by_name,
    c.pc_updated_at           AS pc_updated_at,
    c.version_no              AS version_no,
    c.delete_no               AS delete_no
  FROM (
      SELECT
        c.id                             AS id,
        ROW_NUMBER()
          OVER (PARTITION BY pj_c.project_id
                ORDER BY NVL(pj_c.order_no,  c.order_no), NVL(pj_c.id,  c.id))
                                         AS no,
        pj_c.id                          AS project_custom_field_id,
        pj_c.project_id                  AS project_id,
        pj.name_e                        AS project_name_e,
        NVL(pj_c.label, c.label)         AS label,
        NVL(pj_c.order_no,  c.order_no)  AS order_no,
        c.use_whole                      AS use_whole,
        c.created_by                     AS created_by,
        u1.emp_nm_e                      AS created_by_name,
        c.created_at                     AS created_at,
        c.updated_by                     AS updated_by,
        u2.emp_nm_e                      AS updated_by_name,
        c.updated_at                     AS updated_at,
        --
        pj_c.created_by                  AS pc_created_by,
        u3.emp_nm_e                      AS pc_created_by_name,
        pj_c.created_at                  AS pc_created_at,
        pj_c.updated_by                  AS pc_updated_by,
        u4.emp_nm_e                      AS pc_updated_by_name,
        pj_c.updated_at                  AS pc_updated_at,
        --
        c.version_no                     AS version_no,
        c.delete_no                      AS delete_no
      FROM
        custom_field c
         LEFT OUTER JOIN project_custom_field pj_c
           ON c.id = pj_c.custom_field_id
          AND pj_c.delete_no = 0
         LEFT OUTER JOIN v_project pj
           ON pj_c.project_id = pj.project_id
         LEFT OUTER JOIN v_sys_users u1
           ON c.created_by = u1.emp_no
         LEFT OUTER JOIN v_sys_users u2
           ON c.updated_by = u2.emp_no
         LEFT OUTER JOIN v_sys_users u3
           ON pj_c.created_by = u3.emp_no
         LEFT OUTER JOIN v_sys_users u4
           ON pj_c.updated_by = u4.emp_no
      WHERE c.delete_no = 0
      ) c
;

CREATE OR REPLACE VIEW v_discipline AS
  SELECT
    d.id            AS id,
    d.project_id    AS project_id,
    p.name_e        AS project_name_e,
    d.discipline_cd AS discipline_cd,
    d.name          AS name,
    d.created_by    AS created_by,
    u1.emp_nm_e     AS created_by_name,
    d.created_at    AS created_at,
    d.updated_by    AS updated_by,
    u2.emp_nm_e     AS updated_by_name,
    d.updated_at    AS updated_at,
    d.version_no    AS version_no,
    d.delete_no     AS delete_no
  FROM 
    discipline d
      LEFT OUTER JOIN v_project p
        ON d.project_id = p.project_id
     LEFT OUTER JOIN v_sys_users u1
       ON d.created_by = u1.emp_no
     LEFT OUTER JOIN v_sys_users u2
       ON d.updated_by = u2.emp_no
  WHERE
    d.delete_no = 0
;

CREATE OR REPLACE VIEW v_site AS
  SELECT
    s.id            AS id,
    s.project_id    AS project_id,
    p.name_e        AS project_name_e,
    s.site_cd       AS site_cd,
    s.name          AS name,
    s.created_by    AS created_by,
    u1.emp_nm_e     AS created_by_name,
    s.created_at    AS created_at,
    s.updated_by    AS updated_by,
    u2.emp_nm_e     AS updated_by_name,
    s.updated_at    AS updated_at,
    s.version_no    AS version_no,
    s.delete_no     AS delete_no
  FROM 
    site s
      LEFT OUTER JOIN v_project p
        ON s.project_id = p.project_id
     LEFT OUTER JOIN v_sys_users u1
       ON s.created_by = u1.emp_no
     LEFT OUTER JOIN v_sys_users u2
       ON s.updated_by = u2.emp_no
  WHERE
    s.delete_no = 0
;

CREATE OR REPLACE VIEW v_correspon_group AS
  SELECT
    a.id            AS id,
    s.project_id    AS project_id,
    p.name_e        AS project_name_e,
    a.site_id       AS site_id,
    s.site_cd       AS site_cd,
    s.name          AS site_name,
    a.discipline_id AS discipline_id,
    d.discipline_cd AS discipline_cd,
    d.name          AS discipline_name,
    a.name          AS name,
    a.created_by    AS created_by,
    a.created_at    AS created_at,
    a.updated_by    AS updated_by,
    a.updated_at    AS updated_at,
    a.version_no    AS version_no,
    a.delete_no     AS delete_no
  FROM 
    correspon_group a
      LEFT OUTER JOIN v_site s
        ON a.site_id = s.id
      LEFT OUTER JOIN v_discipline d
        ON a.discipline_id = d.id
      LEFT OUTER JOIN v_project p
        ON s.project_id = p.project_id
  WHERE
    a.delete_no = 0
;

CREATE OR REPLACE VIEW v_correspon_group_user AS
  SELECT
    au.id                 AS id,
    a.project_id          AS project_id,
    au.correspon_group_id AS correspon_group_id,
    a.name                AS correspon_group_name,
    au.emp_no             AS emp_no,
    u.emp_last_nm         AS emp_last_name,
    u.emp_nm_e            AS emp_name_e,
    up.role               AS role,
    au.security_level     AS security_level,
    au.created_by         AS created_by,
    au.created_at         AS created_at,
    au.updated_by         AS updated_by,
    au.updated_at         AS updated_at,
    au.delete_no          AS delete_no
  FROM 
    correspon_group_user au
      LEFT OUTER JOIN v_correspon_group a
        ON au.correspon_group_id = a.id
      LEFT OUTER JOIN v_sys_users u
        ON au.emp_no = u.emp_no
      LEFT OUTER JOIN project_user_profile up
        ON a.project_id = up.project_id
        AND au.emp_no = up.emp_no
        AND up.delete_no = 0
  WHERE
    au.delete_no = 0
;

CREATE OR REPLACE VIEW v_correspon_type AS
  SELECT
    ct.id                       AS id,
    ct.correspon_type           AS correspon_type,
    ct.name                     AS name,
    ct.workflow_pattern_id      AS workflow_pattern_id,
    wp.workflow_cd              AS workflow_cd,
    wp.name                     AS workflow_pattern_name,
    ct.allow_approver_to_browse AS allow_approver_to_browse,
    ct.force_to_use_workflow    AS force_to_use_workflow,
    -- add 2012/07  --
    ct.correspon_access_control_flags AS correspon_access_control_flags,
    ------------------
    ct.use_whole                AS use_whole,
    ct.created_by               AS created_by,
    u1.emp_nm_e                 AS created_by_name,
    ct.created_at               AS created_at,
    ct.updated_by               AS updated_by,
    u2.emp_nm_e                 AS updated_by_name,
    ct.updated_at               AS updated_at,
    ct.version_no               AS version_no,
    ct.delete_no                AS delete_no
  FROM
    correspon_type ct
     JOIN workflow_pattern wp
       ON ct.workflow_pattern_id = wp.id
     LEFT OUTER JOIN v_sys_users u1
       ON ct.created_by = u1.emp_no
     LEFT OUTER JOIN v_sys_users u2
       ON ct.updated_by = u2.emp_no
  WHERE
        ct.delete_no = 0
;

CREATE OR REPLACE VIEW v_project_correspon_type AS
  SELECT
    ct.id                       AS id,
    pct.id                      AS project_correspon_type_id,
    pct.project_id              AS project_id,
    pj.name_e                   AS project_name_e,
    ct.correspon_type           AS correspon_type,
    ct.name                     AS name,
    ct.workflow_pattern_id      AS workflow_pattern_id,
    wp.workflow_cd              AS workflow_cd,
    wp.name                     AS workflow_pattern_name,
    ct.allow_approver_to_browse AS allow_approver_to_browse,
    ct.force_to_use_workflow    AS force_to_use_workflow,
    -- add 2012/07  --
    pct.correspon_access_control_flags AS correspon_access_control_flags,
    ------------------
    ct.use_whole                AS use_whole,
    ct.created_by               AS created_by,
    u1.emp_nm_e                 AS created_by_name,
    ct.created_at               AS created_at,
    ct.updated_by               AS updated_by,
    u2.emp_nm_e                 AS updated_by_name,
    ct.updated_at               AS updated_at,
    ct.version_no               AS version_no,
    ct.delete_no                AS delete_no
  FROM
    correspon_type ct
     LEFT OUTER JOIN project_correspon_type pct
       ON ct.id = pct.correspon_type_id
      AND pct.delete_no = 0
     LEFT OUTER JOIN v_project pj
       ON pct.project_id = pj.project_id
     JOIN workflow_pattern wp
       ON ct.workflow_pattern_id = wp.id
     LEFT OUTER JOIN v_sys_users u1
       ON ct.created_by = u1.emp_no
     LEFT OUTER JOIN v_sys_users u2
       ON ct.updated_by = u2.emp_no
  WHERE
        ct.delete_no = 0
;

CREATE OR REPLACE VIEW v_project_company AS
  SELECT
    co.id            AS id,
    pc.id            AS project_company_id,
    pc.project_id    AS project_id,
    pj.name_e        AS project_name_e,
    co.company_cd    AS company_cd,
    co.name          AS name,
    pc.role          AS role,
    co.created_by    AS created_by,
    u1.emp_nm_e      AS created_by_name,
    co.created_at    AS created_at,
    co.updated_by    AS updated_by,
    u2.emp_nm_e      AS updated_by_name,
    co.updated_at    AS updated_at,
    --
    pc.created_by    AS pc_created_by,
    u3.emp_nm_e      AS pc_created_by_name,
    pc.created_at    AS pc_created_at,
    pc.updated_by    AS pc_updated_by,
    u4.emp_nm_e      AS pc_updated_by_name,
    pc.updated_at    AS pc_updated_at,
    --
    co.version_no    AS version_no,
    co.delete_no     AS delete_no
  FROM 
    company co
      LEFT JOIN project_company pc
       ON co.id = pc.company_id
      AND pc.delete_no = 0
      LEFT OUTER JOIN v_project pj
       ON pc.project_id = pj.project_id
     LEFT OUTER JOIN v_sys_users u1
       ON co.created_by = u1.emp_no
     LEFT OUTER JOIN v_sys_users u2
       ON co.updated_by = u2.emp_no
     LEFT OUTER JOIN v_sys_users u3
       ON pc.created_by = u3.emp_no
     LEFT OUTER JOIN v_sys_users u4
       ON pc.updated_by = u4.emp_no
  WHERE
    co.delete_no = 0
;

CREATE OR REPLACE VIEW v_correspon_custom_field AS
  SELECT
    c.id                      AS id,
    c.no                      AS no,
    c.correspon_id            AS correspon_id,
    c.project_custom_field_id AS project_custom_field_id,
    c.value                   AS value,
    c.created_by              AS created_by,
    c.created_at              AS created_at,
    c.updated_by              AS updated_by,
    c.updated_at              AS updated_at,
    c.delete_no               AS delete_no
  FROM (
      SELECT
        cc.id                              AS id,
        ROW_NUMBER() 
           OVER (PARTITION BY cc.correspon_id 
                 ORDER BY pj_c.order_no)   AS no,
        cc.correspon_id                    AS correspon_id,
        cc.project_custom_field_id         AS project_custom_field_id,
        cc.value                           AS value,
        cc.created_by                      AS created_by,
        cc.created_at                      AS created_at,
        cc.updated_by                      AS updated_by,
        cc.updated_at                      AS updated_at,
        cc.delete_no                       AS delete_no
      FROM 
        correspon_custom_field cc
          LEFT OUTER JOIN project_custom_field pj_c
            ON cc.project_custom_field_id = pj_c.id
           AND pj_c.delete_no = 0
      WHERE cc.delete_no = 0
      ) c
;

CREATE OR REPLACE VIEW v_attachment AS
  SELECT
    a.id           AS id,
    a.no           AS no,
    a.correspon_id AS correspon_id,
    a.file_id      AS file_id,
    a.file_name    AS file_name,
    a.created_by   AS created_by,
    a.created_at   AS created_at,
    a.updated_by   AS updated_by,
    a.updated_at   AS updated_at,
    a.delete_no    AS delete_no
  FROM (
      SELECT
        id,
        ROW_NUMBER() OVER (PARTITION BY correspon_id ORDER BY id) no,
        correspon_id,
        file_id,
        file_name,
        created_by,
        created_at,
        updated_by,
        updated_at,
        delete_no
      FROM attachment
      WHERE delete_no = 0
   ) a
;

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
    -- add 2012/07 --
    c.correspon_access_control_flags AS correspon_access_control_flags,
    ----------------
    c.workflow_pattern_id           AS workflow_pattern_id,
    c.workflow_cd                   AS workflow_cd,
    c.workflow_pattern_name         AS workflow_pattern_name,
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
--    at1.id                          AS file1_id,
--    at1.file_id                     AS file1_file_id,
--    at1.file_name                   AS file1_file_name,
--    at2.id                          AS file2_id,
--    at2.file_id                     AS file2_file_id,
--    at2.file_name                   AS file2_file_name,
--    at3.id                          AS file3_id,
--    at3.file_id                     AS file3_file_id,
--    at3.file_name                   AS file3_file_name,
--    at4.id                          AS file4_id,
--    at4.file_id                     AS file4_file_id,
--    at4.file_name                   AS file4_file_name,
--    at5.id                          AS file5_id,
--    at5.file_id                     AS file5_file_id,
--    at5.file_name                   AS file5_file_name,
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
--   LEFT OUTER JOIN v_attachment at1
--       ON c.id = at1.correspon_id AND at1.no = 1
--   LEFT OUTER JOIN v_attachment at2
--       ON c.id = at2.correspon_id AND at2.no = 2
--   LEFT OUTER JOIN v_attachment at3
--       ON c.id = at3.correspon_id AND at3.no = 3
--   LEFT OUTER JOIN v_attachment at4
--       ON c.id = at4.correspon_id AND at4.no = 4
--   LEFT OUTER JOIN v_attachment at5
--       ON c.id = at5.correspon_id AND at5.no = 5
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
    -- add 2012/07  --
    c.correspon_access_control_flags AS correspon_access_control_flags,
    ------------------
    c.workflow_pattern_id           AS workflow_pattern_id,
    c.workflow_cd                   AS workflow_cd,
    c.workflow_pattern_name         AS workflow_pattern_name,
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
    at2.id                          AS file2_id,
    at2.file_id                     AS file2_file_id,
    at2.file_name                   AS file2_file_name,
    at3.id                          AS file3_id,
    at3.file_id                     AS file3_file_id,
    at3.file_name                   AS file3_file_name,
    at4.id                          AS file4_id,
    at4.file_id                     AS file4_file_id,
    at4.file_name                   AS file4_file_name,
    at5.id                          AS file5_id,
    at5.file_id                     AS file5_file_id,
    at5.file_name                   AS file5_file_name,
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

CREATE OR REPLACE VIEW v_workflow AS
  SELECT
    w.id                      AS id,
    w.correspon_id            AS correspon_id,
    w.emp_no                  AS emp_no,
    u1.emp_nm_e               AS emp_name_e,
    up.role                   AS role,
    w.workflow_type           AS workflow_type,
    w.workflow_no             AS workflow_no,
    w.workflow_process_status AS workflow_process_status,
    w.comment_on              AS comment_on,
    w.finished_by             AS finished_by,
    u2.emp_nm_e               AS finished_by_name,
    w.finished_at             AS finished_at,
    w.created_by              AS created_by,
    u3.emp_nm_e               AS created_by_name,
    w.created_at              AS created_at,
    w.updated_by              AS updated_by,
    u4.emp_nm_e               AS updated_by_name,
    w.updated_at              AS updated_at,
    w.version_no              AS version_no,
    w.delete_no               AS delete_no
  FROM 
    workflow w
      LEFT OUTER JOIN correspon c
        ON w.correspon_id = c.id
       AND c.delete_no = 0
      LEFT OUTER JOIN project_user_profile up
        ON c.project_id = up.project_id
       AND w.emp_no = up.emp_no
       AND up.delete_no = 0
      LEFT OUTER JOIN v_sys_users u1
        ON w.emp_no = u1.emp_no
      LEFT OUTER JOIN v_sys_users u2
        ON w.finished_by = u2.emp_no
      LEFT OUTER JOIN v_sys_users u3
        ON w.created_by = u3.emp_no
      LEFT OUTER JOIN v_sys_users u4
        ON w.updated_by = u4.emp_no
  WHERE
    w.delete_no = 0
;

CREATE OR REPLACE VIEW v_person_in_charge AS
  SELECT
    p.id               AS id,
    p.address_user_id  AS address_user_id,
    p.emp_no           AS emp_no,
    u.emp_last_nm      AS emp_last_name,
    u.emp_nm_e         AS emp_name_e,
    pup.role           AS role,
    p.created_by       AS created_by,
    p.created_at       AS created_at,
    p.updated_by       AS updated_by,
    p.updated_at       AS updated_at,
    p.delete_no        AS delete_no
  FROM
    person_in_charge p
    INNER JOIN address_user a
      ON p.address_user_id = a.id AND
         a.delete_no = 0
    INNER JOIN address_correspon_group acg
      ON a.address_correspon_group_id = acg.id AND
         acg.delete_no = 0
    INNER JOIN correspon c
      ON acg.correspon_id = c.id
    LEFT OUTER JOIN project_user_profile pup
      ON a.emp_no = pup.emp_no AND
         c.project_id = pup.project_id AND
         pup.delete_no = 0
    LEFT OUTER JOIN v_sys_users u
      ON p.emp_no = u.emp_no
  WHERE
    p.delete_no = 0
;

CREATE OR REPLACE VIEW v_address_user AS
  SELECT
    a.id                         AS id,
    a.address_correspon_group_id AS address_correspon_group_id,
    a.emp_no                     AS emp_no,
    u.emp_last_nm                AS emp_last_name,
    u.emp_nm_e                   AS emp_name_e,
    a.address_user_type          AS address_user_type,
    pup.role                     AS role,
    a.created_by                 AS created_by,
    a.created_at                 AS created_at,
    a.updated_by                 AS updated_by,
    a.updated_at                 AS updated_at,
    a.delete_no                  AS delete_no
  FROM
    address_user a
      INNER JOIN address_correspon_group acg
        ON a.address_correspon_group_id = acg.id AND
           acg.delete_no = 0
      INNER JOIN correspon c
        ON acg.correspon_id = c.id
      LEFT OUTER JOIN project_user_profile pup
        ON a.emp_no = pup.emp_no AND
           c.project_id = pup.project_id AND
           pup.delete_no = 0
      LEFT OUTER JOIN v_sys_users u
        ON a.emp_no = u.emp_no
  WHERE
    a.delete_no = 0
;

CREATE OR REPLACE VIEW v_address_correspon_group AS
  SELECT
    a.id                 AS id,
    a.correspon_id       AS correspon_id,
    a.correspon_group_id AS correspon_group_id,
    u.name               AS correspon_group_name,
    a.address_type       AS address_type,
    a.created_by         AS created_by,
    a.created_at         AS created_at,
    a.updated_by         AS updated_by,
    a.updated_at         AS updated_at,
    a.delete_no          AS delete_no
  FROM 
    address_correspon_group a
      LEFT OUTER JOIN correspon_group u
        ON a.correspon_group_id = u.id
       AND u.delete_no = 0
  WHERE
    a.delete_no = 0
;

CREATE OR REPLACE VIEW v_address AS
  SELECT
    -- address_group
    g.id                   AS id,
    g.correspon_id         AS correspon_id,
    g.correspon_group_id   AS correspon_group_id,
    g.correspon_group_name AS correspon_group_name,
    g.address_type         AS address_type,
    -- address_user
    u.id                   AS address_user_id,
    u.emp_no               AS emp_no,
    u.emp_last_name        AS emp_last_name,
    u.emp_name_e           AS emp_name_e,
    u.address_user_type    AS address_user_type,
    u.role                 AS role,
    -- person_in_charge
    c.id                   AS charge_id,
    c.emp_no               AS charge_emp_no,
    c.emp_last_name        AS charge_last_name,
    c.emp_name_e           AS charge_name_e,
    c.role                 AS charge_role,
    --
    g.created_by           AS created_by,
    g.created_at           AS created_at,
    g.updated_by           AS updated_by,
    g.updated_at           AS updated_at,
    g.delete_no            AS delete_no
  FROM
    v_address_correspon_group g
      LEFT OUTER JOIN v_address_user u
        ON g.id = u.address_correspon_group_id
      LEFT OUTER JOIN v_person_in_charge c
        ON u.id = c.address_user_id
;

CREATE OR REPLACE VIEW v_workflow_template_user AS
  SELECT
    w.id          AS id,
    w.project_id  AS project_id,
    w.emp_no      AS emp_no,
    u.emp_last_nm AS emp_last_name,
    u.emp_nm_e    AS emp_name_e,
    w.name        AS name,
    w.created_by  AS created_by,
    w.created_at  AS created_at,
    w.updated_by  AS updated_by,
    w.updated_at  AS updated_at,
    w.version_no  AS version_no,
    w.delete_no   AS delete_no
  FROM
    workflow_template_user w
      LEFT OUTER JOIN v_sys_users u
        ON w.emp_no = u.emp_no
  WHERE
    w.delete_no = 0
;

CREATE OR REPLACE VIEW v_workflow_template AS
  SELECT
    w.id                        AS id,
    w.workflow_template_user_id AS workflow_template_user_id,
    w.emp_no                    AS emp_no,
    u1.emp_nm_e                 AS emp_name_e,
    up.role                     AS role,
    w.workflow_type             AS workflow_type,
    w.workflow_no               AS workflow_no,
    w.created_by                AS created_by,
    u2.emp_nm_e                 AS created_by_name,
    w.created_at                AS created_at,
    w.updated_by                AS updated_by,
    u3.emp_nm_e                 AS updated_by_name,
    w.updated_at                AS updated_at,
    w.delete_no                 AS delete_no
  FROM
    workflow_template w
      LEFT OUTER JOIN workflow_template_user t
        ON w.workflow_template_user_id = t.id
       AND t.delete_no = 0
      LEFT OUTER JOIN project_user_profile up
        ON t.project_id = up.project_id
       AND w.emp_no = up.emp_no
       AND up.delete_no = 0
      LEFT OUTER JOIN v_sys_users u1
        ON w.emp_no = u1.emp_no
      LEFT OUTER JOIN v_sys_users u2
        ON w.created_by = u2.emp_no
      LEFT OUTER JOIN v_sys_users u3
        ON w.updated_by = u3.emp_no
  WHERE
    w.delete_no = 0
;

CREATE OR REPLACE VIEW v_favorite_filter AS
  SELECT
    ff.id                AS id,
    ff.project_id        AS project_id,
    (SELECT u1.emp_nm_e
     FROM v_sys_users u1
     WHERE u1.emp_no = ff.emp_no)
                         AS emp_name_e,
    ff.emp_no            AS emp_no,
    ff.favorite_name     AS favorite_name,
    ff.search_conditions AS search_conditions,
    ff.created_by        AS created_by,
    --
    (SELECT u1.emp_nm_e
     FROM v_sys_users u1
     WHERE u1.emp_no = ff.created_by)
                         AS created_by_name,
    ff.created_at        AS created_at,
    ff.updated_by        AS updated_by,
    --
    (SELECT u2.emp_nm_e
     FROM v_sys_users u2
     WHERE u2.emp_no = ff.updated_by)
                         AS updated_by_name,
    ff.updated_at        AS updated_at,
    ff.version_no        AS version_no,
    ff.delete_no         AS delete_no
  FROM 
    favorite_filter ff
  WHERE ff.delete_no = 0
;

CREATE OR REPLACE VIEW v_email_notice AS
  SELECT
    en.id                      AS id,
    en.project_id              AS project_id,
    en.notice_category         AS notice_category,
    en.event_cd                AS event_cd,
    en.notice_address_type     AS notice_address_type,
    en.notice_status           AS notice_status ,
    en.notified_at             AS notified_at,
    en.mh_subject              AS mh_subject,
    en.mh_to                   AS mh_to,
    en.mh_from                 AS mh_from,
    en.mh_errors_to            AS mh_errors_to,
    en.correspon_id            AS correspon_id ,
    en.mail_body               AS mail_body,
    --en.mb_correspon_id         AS mb_correspon_id ,
    --en.mb_correspon_no         AS mb_correspon_no,
    --en.mb_from                 AS mb_from,
    --en.mb_to                   AS mb_to,
    --en.mb_cc                   AS mb_cc,
    --en.mb_correspon_type_id    AS mb_correspon_type_id,
    --en.mb_subject              AS mb_subject,
    --en.mb_workflow_status      AS mb_workflow_status,
    --en.mb_workflow_status_name AS mb_workflow_status_name,
    --en.mb_deadline_for_reply   AS mb_deadline_for_reply,
    --en.mb_reply_required       AS mb_reply_required,
    --en.mb_created_at           AS mb_created_at,
    --en.mb_created_by           AS mb_created_by,
    --u1.emp_nm_e                AS mb_created_by_name,
    --en.mb_issued_at            AS mb_issued_at,
    --en.mb_issued_by            AS mb_issued_by,
    --u2.emp_nm_e                AS mb_issued_by_name,
    --en.mb_coreespon_url        AS mb_coreespon_url,
    en.created_by              AS created_by,
    en.created_at              AS created_at,
    en.updated_by              AS updated_by,
    en.updated_at              AS updated_at,
    en.delete_no               AS delete_no
  FROM 
    email_notice en
    --   LEFT OUTER JOIN v_sys_users u1
    --     ON en.mb_created_at = u1.emp_no
    --   LEFT OUTER JOIN v_sys_users u2
    --     ON en.mb_issued_by = u2.emp_no
  WHERE
    en.delete_no = 0
;

CREATE OR REPLACE VIEW v_email_notice_recv_setting AS
  SELECT
    es.id                           AS id,
    es.project_id                   AS project_id,
    p.name_e                        AS project_name_e,
    es.emp_no                       AS emp_no,
    es.receive_workflow             AS receive_workflow,
    es.recv_distribution_attention  AS recv_distribution_attention,
    es.recv_distribution_cc         AS recv_distribution_cc,
    es.created_by                   AS created_by,
    es.created_at                   AS created_at,
    es.updated_by                   AS updated_by,
    es.updated_at                   AS updated_at,
    es.delete_no                    AS delete_no
  FROM 
    email_notice_recv_setting es
      LEFT OUTER JOIN v_project p
        ON es.project_id = p.project_id
  WHERE
    es.delete_no = 0
;

CREATE OR REPLACE VIEW v_dist_template_header AS
SELECT
    disttplh.id                             AS id,
    disttplh.project_id                     AS project_id,
    disttplh.emp_no                         AS emp_no,
    disttplh.template_cd                    AS template_cd,
    disttplh.name                           AS name,
    disttplh.option1                        AS option1,
    disttplh.option2                        AS option2,
    disttplh.option3                        AS option3,
    disttplh.created_by                     AS created_by,
    disttplh.created_at                     AS created_at,
    disttplh.updated_by                     AS updated_by,
    disttplh.updated_at                     AS updated_at,
    disttplh.version_no                     AS version_no
FROM dist_template_header disttplh
WHERE disttplh.delete_no = 0
;

CREATE OR REPLACE VIEW v_dist_template_group AS
SELECT
    disttplg.id                             AS id,
    disttplg.dist_template_header_id        AS dist_template_header_id,
    disttplg.distribution_type              AS distribution_type,
    disttplg.order_no                       AS order_no,
    disttplg.group_id                       AS group_id,
    vcorgrp.name                            AS group_name,
    disttplg.created_by                     AS created_by,
    disttplg.created_at                     AS created_at,
    disttplg.updated_by                     AS updated_by,
    disttplg.updated_at                     AS updated_at
FROM
    dist_template_group     disttplg,
    v_correspon_group       vcorgrp
WHERE disttplg.group_id = vcorgrp.id
    AND disttplg.delete_no = 0
;

CREATE OR REPLACE VIEW v_dist_template_user AS
SELECT
    disttplu.id                             AS id,
    disttplu.dist_template_group_id         AS dist_template_group_id,
    disttplu.order_no                       AS order_no,
    disttplu.emp_no                         AS emp_no,
    puser.emp_last_nm                       AS emp_last_nm,
    puser.emp_nm_e                          AS emp_nm_e,
    puser.emp_nm_j                          AS emp_nm_j,
    pjuser.role                             AS emp_role,
    disttplu.created_by                     AS created_by,
    disttplu.created_at                     AS created_at,
    disttplu.updated_by                     AS updated_by,
    disttplu.updated_at                     AS updated_at
FROM
    dist_template_user disttplu
    INNER JOIN v_sys_users puser
        ON disttplu.emp_no = puser.emp_no
    INNER JOIN v_dist_template_group distgrp
        ON disttplu.dist_template_group_id = distgrp.id
    INNER JOIN v_dist_template_header disthdr
        ON distgrp.dist_template_header_id = disthdr.id
    LEFT OUTER JOIN v_project_user pjuser
        ON disthdr.project_id = pjuser.project_id
            AND disttplu.emp_no = pjuser.emp_no
WHERE disttplu.delete_no = 0
;
