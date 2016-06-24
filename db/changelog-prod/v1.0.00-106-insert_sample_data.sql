--liquibase formatted sql
--changeset opentone:v1.0.00-106-insert_sample_data

 INSERT INTO sys_systems (apl_sys_cd, apl_sys_node_cd) VALUES ('OCT', 'JPN');
 INSERT INTO sys_pj (pj_id  , cl_nm_e  , cl_nm_j  , pj_nm_e  , pj_nm_j) VALUES ('A0000000001'  , 'OpenTone'  , 'OpenTone'  , 'Sample Project'  , 'Sample Project');
 INSERT INTO sys_pj_auth(pj_id, apl_sys_aprv_flg) VALUES ('A0000000001', 'X');
 INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00000'  , 'Sample'  , 'Taro Sample'  , 'Taro Sample'  , 'X'  , 'X');
 INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00000', NULL);
 INSERT INTO workflow_pattern VALUES (1, '01', 'pattern1', 'test');
 INSERT INTO workflow_pattern VALUES (2, '02', 'pattern2', 'test');
