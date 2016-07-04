--liquibase formatted sql
--changeset opentone:v1.0.00-107-insert_sample_user

--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00001'  , 'Tsunoda'  , 'Akemi Tsunoda'  , 'Akemi Tsunoda'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00001', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00002'  , 'Tonoike'  , 'Yoshio Tonoike'  , 'Yoshio Tonoike'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00002', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00003'  , 'Yamano'  , 'Hidenori Yamano'  , 'Hidenori Yamano'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00003', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00004'  , 'Wakabayashi'  , 'Atsushi Wakabayashi'  , 'Atsushi Wakabayashi'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00004', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00005'  , 'Koyano'  , 'Tadashi Koyano'  , 'Tadashi Koyano'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00005', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00006'  , 'Chikada'  , 'Masashi Chikada'  , 'Masashi Chikada'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00006', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00007'  , 'Yamamoto'  , 'Koji Yamamoto'  , 'Koji Yamamoto'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00007', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00008'  , 'Fujiwara'  , 'Yoko Fujiwara'  , 'Yoko Fujiwara'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00008', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00009'  , 'Katase'  , 'Umi Katase'  , 'Umi Katase'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00009', NULL);
--
INSERT INTO sys_users(cpny_id  , emp_no  , emp_last_nm  , emp_nm_e  , emp_nm_j  , sys_adm_flg  , user_regist_aprv_flg) VALUES ('OT'  , '00010'  , 'Yui'  , 'Haruko Yui'  , 'Haruko Yui'  , null  , 'X');
INSERT INTO sys_pj_users(pj_id, emp_no, pj_adm_flg) VALUES ('A0000000001', '00010', NULL);

