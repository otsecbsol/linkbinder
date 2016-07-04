--liquibase formatted sql
--changeset opentone:v1.0.00-103-create_auth_table
-- system
CREATE TABLE sys_systems
(
    apl_sys_cd                     VARCHAR2(6) NOT NULL,
    apl_sys_node_cd                VARCHAR2(6) NOT NULL,
    CONSTRAINT pk_sys_systems PRIMARY KEY (apl_sys_node_cd, apl_sys_cd) USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        TABLESPACE ${idx_tablespace}
);
-- project
CREATE TABLE sys_pj
(
    pj_id                          VARCHAR2(11) NOT NULL,
    cl_nm_e                        VARCHAR2(30),
    cl_nm_j                        VARCHAR2(30 CHAR),
    pj_nm_e                        VARCHAR2(40),
    pj_nm_j                        VARCHAR2(40 CHAR),
    CONSTRAINT pk_sys_pj PRIMARY KEY (pj_id) USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        TABLESPACE ${idx_tablespace}
);
CREATE TABLE sys_pj_auth
(
    pj_id                          VARCHAR2(11) NOT NULL,
    apl_sys_aprv_flg               VARCHAR2(1),
    CONSTRAINT pk_sys_pj_auth PRIMARY KEY (pj_id) USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        TABLESPACE ${idx_tablespace}
    );
-- user
CREATE TABLE sys_users
(
    cpny_id                        VARCHAR2(4),
    emp_no                         VARCHAR2(5) NOT NULL,
    emp_email_addr                 VARCHAR2(60),
    emp_last_nm                    VARCHAR2(15 CHAR),
    emp_nm_e                       VARCHAR2(40 CHAR) NOT NULL,
    emp_nm_j                       VARCHAR2(20 CHAR),
    emp_passwd                     VARCHAR2(4000),
    emp_passwd_upd_dt              DATE DEFAULT SYSDATE,
    sys_adm_flg                    VARCHAR2(1),
    user_regist_aprv_flg           VARCHAR2(1),
    security_lvl                   VARCHAR2(2), 
    user_id_valid_dt               DATE,
    id_created_dt                  DATE DEFAULT SYSDATE,
    CONSTRAINT pk_sys_users PRIMARY KEY (emp_no) USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        TABLESPACE ${idx_tablespace}
);
CREATE INDEX ix_sys_users_1
    ON sys_users(emp_last_nm)
    PCTFREE 10
    INITRANS 2
    MAXTRANS 255
    TABLESPACE ${idx_tablespace};
-- project user
CREATE TABLE sys_pj_users
(
    pj_id                          VARCHAR2(11) NOT NULL,
    emp_no                         VARCHAR2(5) NOT NULL,
    pj_adm_flg                     VARCHAR2(1),
    security_lvl                   VARCHAR2(2) DEFAULT '40',
    CONSTRAINT pk_sys_pj_users PRIMARY KEY (pj_id, emp_no) USING INDEX
        PCTFREE 10
        INITRANS 2
        MAXTRANS 255
        TABLESPACE ${idx_tablespace}
);
