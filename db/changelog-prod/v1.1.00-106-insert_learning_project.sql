--liquibase formatted sql
--changeset opentone:v1.1.00-106-insert_learning_project
--
INSERT INTO sys_pj (pj_id, cl_nm_e, cl_nm_j, pj_nm_e, pj_nm_j, for_learning)
  VALUES ('L9999999999', null, null, 'FOR LEARNING', '学習用プロジェクト', 1);

