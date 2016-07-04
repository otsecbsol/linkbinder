--liquibase formatted sql
--changeset opentone:v1.0.00-101-create_table

---------------------------
-- company
---------------------------
CREATE TABLE company (
  id         NUMBER(10)         NOT NULL,
  company_cd VARCHAR2(10)       NOT NULL,
  name       VARCHAR2(100 CHAR) NOT NULL,
  created_by VARCHAR2(5)        NOT NULL,
  created_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by VARCHAR2(5)        NOT NULL,
  updated_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  version_no NUMBER(10)         DEFAULT 1 NOT NULL,
  delete_no  NUMBER(10)         DEFAULT 0 NOT NULL,
  CONSTRAINT pk_company
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_company_01 ON company(company_cd, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_company_02 ON company(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_company_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_company_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- project_company
---------------------------
CREATE TABLE project_company (
  id         NUMBER(10)   NOT NULL,
  project_id VARCHAR2(11) NOT NULL,
  company_id NUMBER(10)   NOT NULL,
  role       VARCHAR2(50) ,
  created_by VARCHAR2(5)  NOT NULL,
  created_at TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by VARCHAR2(5)  NOT NULL,
  updated_at TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no  NUMBER(10)   DEFAULT 0 NOT NULL,
  CONSTRAINT pk_project_company
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_project_company_01 ON project_company(project_id, company_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_company_02 ON project_company(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_company_03 ON project_company(company_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_company_04 ON project_company(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_project_company_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_project_company_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- company_user
---------------------------
CREATE TABLE company_user (
  id                 NUMBER(10)   NOT NULL,
  project_company_id NUMBER(10)   NOT NULL,
  project_id         VARCHAR2(11) ,
  emp_no             VARCHAR2(5)  NOT NULL,
  created_by         VARCHAR2(5)  NOT NULL,
  created_at         TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by         VARCHAR2(5)  NOT NULL,
  updated_at         TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no          NUMBER(10)   DEFAULT 0 NOT NULL,
  CONSTRAINT pk_company_user
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_company_user_01 ON company_user(emp_no, delete_no, project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_company_user_02 ON company_user(project_company_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_company_user_03 ON company_user(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_company_user_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_company_user_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon
---------------------------
CREATE TABLE correspon (
  id                        NUMBER(10)          NOT NULL,
  correspon_no              VARCHAR2(100)       ,
  project_id                VARCHAR2(11)        NOT NULL,
  from_correspon_group_id   NUMBER(10)          NOT NULL,
  previous_rev_correspon_id NUMBER(10)          ,
  project_correspon_type_id NUMBER(10)          NOT NULL,
  subject                   VARCHAR2(300 CHAR)  NOT NULL,
  body                      CLOB                NOT NULL,
  issued_by                 VARCHAR2(5)         ,
  issued_at                 TIMESTAMP           ,
  correspon_status          NUMBER(1)           DEFAULT 0 NOT NULL,
  reply_required            NUMBER(1)           NOT NULL,
  deadline_for_reply        DATE                ,
  requested_approval_at     TIMESTAMP           ,
  workflow_status           NUMBER(2)           DEFAULT 0 NOT NULL,
  created_by                VARCHAR2(5)         NOT NULL,
  created_at                TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                VARCHAR2(5)         NOT NULL,
  updated_at                TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  version_no                NUMBER(10)          DEFAULT 1 NOT NULL,
  delete_no                 NUMBER(10)          DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE  INDEX ix_correspon_01 ON correspon(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_02 ON correspon(from_correspon_group_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_03 ON correspon(project_correspon_type_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_04 ON correspon(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_05 ON correspon(workflow_status)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_06 ON correspon(created_by)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_07 ON correspon(updated_at)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- discipline
---------------------------
CREATE TABLE discipline (
  id            NUMBER(10)          NOT NULL,
  project_id    VARCHAR2(11)        NOT NULL,
  discipline_cd VARCHAR2(10)        NOT NULL,
  name          VARCHAR2(100 CHAR)  NOT NULL,
  created_by    VARCHAR2(5)         NOT NULL,
  created_at    TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)         NOT NULL,
  updated_at    TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)          DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)          DEFAULT 0 NOT NULL,
  CONSTRAINT pk_discipline
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_discipline_01 ON discipline(project_id, discipline_cd, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_discipline_02 ON discipline(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_discipline_03 ON discipline(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_discipline_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_discipline_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- site
---------------------------
CREATE TABLE site (
  id         NUMBER(10)         NOT NULL,
  project_id VARCHAR2(11)       NOT NULL,
  site_cd    VARCHAR2(10)       NOT NULL,
  name       VARCHAR2(100 CHAR) NOT NULL,
  created_by VARCHAR2(5)        NOT NULL,
  created_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by VARCHAR2(5)        NOT NULL,
  updated_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  version_no NUMBER(10)         DEFAULT 1 NOT NULL,
  delete_no  NUMBER(10)         DEFAULT 0 NOT NULL,
  CONSTRAINT pk_site
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_site_01 ON site(site_cd, project_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_site_02 ON site(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_site_03 ON site(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_site_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_site_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_group
---------------------------
CREATE TABLE correspon_group (
  id            NUMBER(10)          NOT NULL,
  site_id       NUMBER(10)          NOT NULL,
  discipline_id NUMBER(10)          NOT NULL,
  name          VARCHAR2(100 CHAR)  NOT NULL,
  created_by    VARCHAR2(5)         NOT NULL,
  created_at    TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)         NOT NULL,
  updated_at    TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)          DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)          DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_group
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_correspon_group_01 ON correspon_group(site_id, discipline_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_02 ON correspon_group(site_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_03 ON correspon_group(discipline_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_04 ON correspon_group(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_group_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_group_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_type
---------------------------
CREATE TABLE correspon_type (
  id                       NUMBER(10)           NOT NULL,
  correspon_type           VARCHAR2(10)         NOT NULL,
  name                     VARCHAR2(100 CHAR)   NOT NULL,
  workflow_pattern_id      NUMBER(10)           NOT NULL,
  allow_approver_to_browse NUMBER(1)            DEFAULT 0 NOT NULL,
  force_to_use_workflow    NUMBER(1)            DEFAULT 1 NOT NULL,
  correspon_access_control_flags NUMBER(3)      DEFAULT 255 NOT NULL,
  use_whole                NUMBER(1)            DEFAULT 0 NOT NULL,
  created_by               VARCHAR2(5)          NOT NULL,
  created_at               TIMESTAMP            DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by               VARCHAR2(5)          NOT NULL,
  updated_at               TIMESTAMP            DEFAULT SYSTIMESTAMP NOT NULL,
  version_no               NUMBER(10)           DEFAULT 1 NOT NULL,
  delete_no                NUMBER(10)           DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_type
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE INDEX ix_correspon_type_01 ON correspon_type(correspon_type, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_type_02 ON correspon_type(workflow_pattern_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_type_03 ON correspon_type(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_type_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_type_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- custom_field
---------------------------
CREATE TABLE custom_field (
  id         NUMBER(10)         NOT NULL,
  label      VARCHAR2(100 CHAR) NOT NULL,
  order_no   NUMBER(5)          DEFAULT 0 NOT NULL,
  use_whole  NUMBER(1)          DEFAULT 0 NOT NULL,
  created_by VARCHAR2(5)        NOT NULL,
  created_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by VARCHAR2(5)        NOT NULL,
  updated_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  version_no NUMBER(10)         DEFAULT 1 NOT NULL,
  delete_no  NUMBER(10)         DEFAULT 0 NOT NULL,
  CONSTRAINT pk_custom_field
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE INDEX ix_custom_field_01 ON custom_field(label, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_custom_field_02 ON custom_field(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_custom_field_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_custom_field_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- custom_field_value
---------------------------
CREATE TABLE custom_field_value (
  id              NUMBER(10)            NOT NULL,
  custom_field_id NUMBER(10)            ,
  value           VARCHAR2(100 CHAR)    NOT NULL,
  order_no        NUMBER(5)             DEFAULT 0 NOT NULL,
  created_by      VARCHAR2(5)           NOT NULL,
  created_at      TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by      VARCHAR2(5)           NOT NULL,
  updated_at      TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no       NUMBER(10)            DEFAULT 0 NOT NULL,
  CONSTRAINT pk_custom_field_value
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_custom_field_value_01 ON custom_field_value(value, custom_field_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_custom_field_value_02 ON custom_field_value(custom_field_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_custom_field_value_03 ON custom_field_value(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_custom_field_value_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_custom_field_value_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- attachment
---------------------------
CREATE TABLE attachment (
  id           NUMBER(10)    NOT NULL,
  correspon_id NUMBER(10)    NOT NULL,
  file_id      VARCHAR2(256) NOT NULL,
  file_name    VARCHAR2(300) NOT NULL,
  created_by   VARCHAR2(5)   NOT NULL,
  created_at   TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by   VARCHAR2(5)   NOT NULL,
  updated_at   TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no    NUMBER(10)    DEFAULT 0 NOT NULL,
  CONSTRAINT pk_attachment
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_attachment_01 ON attachment(correspon_id, delete_no, file_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_attachment_02 ON attachment(correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_attachment_03 ON attachment(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_attachment_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_attachment_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_group_user
---------------------------
CREATE TABLE correspon_group_user (
  id                 NUMBER(10)  NOT NULL,
  correspon_group_id NUMBER(10)  NOT NULL,
  emp_no             VARCHAR2(5) NOT NULL,
  security_level     CHAR(2)     NOT NULL,
  created_by         VARCHAR2(5) NOT NULL,
  created_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by         VARCHAR2(5) NOT NULL,
  updated_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no          NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_group_user
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_correspon_group_user_01 ON correspon_group_user(emp_no, correspon_group_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_user_02 ON correspon_group_user(correspon_group_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_user_03 ON correspon_group_user(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_group_user_04 ON correspon_group_user(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_group_user_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_group_user_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_read_status
---------------------------
CREATE TABLE correspon_read_status (
  id           NUMBER(10)  NOT NULL,
  emp_no       VARCHAR2(5) NOT NULL,
  correspon_id NUMBER(10)  NOT NULL,
  read_status  NUMBER(1)   DEFAULT 0 NOT NULL,
  created_by   VARCHAR2(5) NOT NULL,
  created_at   TIMESTAMP   DEFAULT SYSTIMESTAMP,
  updated_by   VARCHAR2(5) NOT NULL,
  updated_at   TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  version_no   NUMBER(10)  DEFAULT 1 NOT NULL,
  delete_no    NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_read_status
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_correspon_read_status_01 ON correspon_read_status(emp_no, correspon_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_read_status_02 ON correspon_read_status(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_read_status_03 ON correspon_read_status(correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_read_status_04 ON correspon_read_status(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_read_status_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_read_status_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_custom_field
---------------------------
CREATE TABLE correspon_custom_field (
  id                      NUMBER(10)        NOT NULL,
  correspon_id            NUMBER(10)        NOT NULL,
  project_custom_field_id NUMBER(10)        NOT NULL,
  value                   VARCHAR2(100 CHAR) ,
  created_by              VARCHAR2(5)       NOT NULL,
  created_at              TIMESTAMP         DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by              VARCHAR2(5)       NOT NULL,
  updated_at              TIMESTAMP         DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no               NUMBER(10)        DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_custom_field
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_correspon_custom_field_01 ON correspon_custom_field(correspon_id, project_custom_field_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_custom_field_02 ON correspon_custom_field(correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_custom_field_03 ON correspon_custom_field(project_custom_field_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_custom_field_04 ON correspon_custom_field(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_custom_field_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_custom_field_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- address_correspon_group
---------------------------
CREATE TABLE address_correspon_group (
  id                 NUMBER(10)  NOT NULL,
  correspon_id       NUMBER(10)  NOT NULL,
  correspon_group_id NUMBER(10)  NOT NULL,
  address_type       NUMBER(1)   NOT NULL,
  created_by         VARCHAR2(5) NOT NULL,
  created_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by         VARCHAR2(5) NOT NULL,
  updated_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no          NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_address_correspon_group
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE  INDEX ix_address_correspon_group_01 ON address_correspon_group(correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_address_correspon_group_02 ON address_correspon_group(correspon_group_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_address_correspon_group_03 ON address_correspon_group(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE UNIQUE INDEX ix_address_correspon_group_04 ON address_correspon_group(correspon_id, correspon_group_id, address_type, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_address_correspon_group_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_address_correspon_group_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- address_user
---------------------------
CREATE TABLE address_user (
  id                         NUMBER(10)  NOT NULL,
  address_correspon_group_id NUMBER(10)  NOT NULL,
  emp_no                     VARCHAR2(5) NOT NULL,
  address_user_type          NUMBER(1)   DEFAULT 0 NOT NULL,
  created_by                 VARCHAR2(5) NOT NULL,
  created_at                 TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                 VARCHAR2(5) NOT NULL,
  updated_at                 TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                  NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_address_user
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE  INDEX ix_address_user_01 ON address_user(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_address_user_02 ON address_user(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE UNIQUE INDEX ix_address_user_03 ON address_user(address_correspon_group_id, emp_no, address_user_type, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_address_user_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_address_user_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- correspon_hierarchy
---------------------------
CREATE TABLE correspon_hierarchy (
  id                     NUMBER(10)  NOT NULL,
  parent_correspon_id    NUMBER(10)  NOT NULL,
  child_correspon_id     NUMBER(10)  NOT NULL,
  reply_address_user_id  NUMBER(10),
  created_by             VARCHAR2(5) NOT NULL,
  created_at             TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by             VARCHAR2(5) NOT NULL,
  updated_at             TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no              NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_hierarchy
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_correspon_hierarchy_01 ON correspon_hierarchy(delete_no, parent_correspon_id, child_correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_hierarchy_02 ON correspon_hierarchy(parent_correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_hierarchy_03 ON correspon_hierarchy(child_correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_hierarchy_04 ON correspon_hierarchy(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_correspon_hierarchy_05 ON correspon_hierarchy(reply_address_user_id)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_correspon_hierarchy_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_correspon_hierarchy_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- user_profile
---------------------------
CREATE TABLE user_profile (
  id                         NUMBER(10)    NOT NULL,
  emp_no                     VARCHAR2(5)   NOT NULL,
  last_logged_in_at          TIMESTAMP     ,
  default_project_id         VARCHAR2(11)  ,
  correspon_invisible_fields VARCHAR2(200) ,
  feed_key                   VARCHAR2(100) ,
  created_by                 VARCHAR2(5)   NOT NULL,
  created_at                 TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                 VARCHAR2(5)   NOT NULL,
  updated_at                 TIMESTAMP     DEFAULT SYSTIMESTAMP NOT NULL,
  version_no                 NUMBER(10)    DEFAULT 1 NOT NULL,
  delete_no                  NUMBER(10)    DEFAULT 0 NOT NULL,
  CONSTRAINT pk_user_profile
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_user_profile_01 ON user_profile(emp_no, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_user_profile_02 ON user_profile(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_user_profile_03 ON user_profile(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_user_profile_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_user_profile_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- workflow
---------------------------
CREATE TABLE workflow (
  id                      NUMBER(10)        NOT NULL,
  correspon_id            NUMBER(10)        NOT NULL,
  emp_no                  VARCHAR2(5)       NOT NULL,
  workflow_type           NUMBER(2)         NOT NULL,
  workflow_no             NUMBER(10)        NOT NULL,
  workflow_process_status NUMBER(2)         DEFAULT 0 NOT NULL,
  comment_on              VARCHAR2(500 CHAR) ,
  finished_by             VARCHAR2(5)       ,
  finished_at             TIMESTAMP         ,
  created_by              VARCHAR2(5)       NOT NULL,
  created_at              TIMESTAMP         DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by              VARCHAR2(5)       NOT NULL,
  updated_at              TIMESTAMP         DEFAULT SYSTIMESTAMP NOT NULL,
  version_no              NUMBER(10)        DEFAULT 1 NOT NULL,
  delete_no               NUMBER(10)        DEFAULT 0 NOT NULL,
  CONSTRAINT pk_workflow
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_workflow_01 ON workflow(correspon_id, emp_no, workflow_type, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_workflow_02 ON workflow(correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_workflow_03 ON workflow(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_workflow_04 ON workflow(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_workflow_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_workflow_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- project_user_profile
---------------------------
CREATE TABLE project_user_profile (
  id                         NUMBER(10)         NOT NULL,
  project_id                 VARCHAR2(11)       NOT NULL,
  emp_no                     VARCHAR2(5)        NOT NULL,
  role                       VARCHAR2(50 CHAR)  ,
  default_correspon_group_id NUMBER(10)         ,
  created_by                 VARCHAR2(5)        NOT NULL,
  created_at                 TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                 VARCHAR2(5)        NOT NULL,
  updated_at                 TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                  NUMBER(10)         DEFAULT 0 NOT NULL,
  CONSTRAINT pk_project_user_profile
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_project_user_profile_01 ON project_user_profile(project_id, emp_no, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_user_profile_02 ON project_user_profile(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_user_profile_03 ON project_user_profile(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_user_profile_04 ON project_user_profile(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_project_user_profile_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_project_user_profile_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- project_custom_field
---------------------------
CREATE TABLE project_custom_field (
  id              NUMBER(10)            NOT NULL,
  project_id      VARCHAR2(11)          NOT NULL,
  custom_field_id NUMBER(10)            NOT NULL,
  label           VARCHAR2(100 CHAR)    NOT NULL,
  order_no        NUMBER(5)             DEFAULT 0 NOT NULL,
  created_by      VARCHAR2(5)           NOT NULL,
  created_at      TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by      VARCHAR2(5)           NOT NULL,
  updated_at      TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no       NUMBER(10)            DEFAULT 0 NOT NULL,
  CONSTRAINT pk_project_custom_field
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_project_custom_field_01 ON project_custom_field(project_id, custom_field_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_custom_field_02 ON project_custom_field(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_custom_field_03 ON project_custom_field(custom_field_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_custom_field_04 ON project_custom_field(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_project_custom_field_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_project_custom_field_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- project_correspon_type
---------------------------
CREATE TABLE project_correspon_type (
  id                NUMBER(10)   NOT NULL,
  project_id        VARCHAR2(11) ,
  correspon_type_id NUMBER(10)   NOT NULL,
  created_by        VARCHAR2(5)  NOT NULL,
  created_at        TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by        VARCHAR2(5)  NOT NULL,
  updated_at        TIMESTAMP    DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no         NUMBER(10)   DEFAULT 0 NOT NULL,
  correspon_access_control_flags NUMBER(3) DEFAULT 255 NOT NULL,
  -----------------
  CONSTRAINT pk_project_correspon_type
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_project_correspon_type_01 ON project_correspon_type(project_id, correspon_type_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_correspon_type_02 ON project_correspon_type(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_correspon_type_03 ON project_correspon_type(correspon_type_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_project_correspon_type_04 ON project_correspon_type(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_project_correspon_type_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_project_correspon_type_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- workflow_pattern
---------------------------
CREATE TABLE workflow_pattern (
  id          NUMBER(10)            NOT NULL,
  workflow_cd VARCHAR2(10)          NOT NULL,
  name        VARCHAR2(100 CHAR)    NOT NULL,
  description VARCHAR2(500 CHAR)    ,
  CONSTRAINT pk_workflow_pattern
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_workflow_pattern_01 ON workflow_pattern(workflow_cd)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_workflow_pattern_id
  START WITH 1
  NOCACHE
;
--
---------------------------
-- person_in_charge
---------------------------
CREATE TABLE person_in_charge (
  id              NUMBER(10)  NOT NULL,
  address_user_id NUMBER(10)  NOT NULL,
  emp_no          VARCHAR2(5) ,
  created_by      VARCHAR2(5) ,
  created_at      TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by      VARCHAR2(5) ,
  updated_at      TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no       NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_person_in_charge
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE  INDEX ix_person_in_charge_01 ON person_in_charge(emp_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_person_in_charge_02 ON person_in_charge(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE UNIQUE INDEX ix_person_in_charge_03 ON person_in_charge(address_user_id, emp_no, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_person_in_charge_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_person_in_charge_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- parent_correspon_no_seq
---------------------------
CREATE TABLE parent_correspon_no_seq (
  id                 NUMBER(10)  NOT NULL,
  site_id            NUMBER(10)  NOT NULL,
  discipline_id      NUMBER(10)  NOT NULL,
  no                 NUMBER(5)   DEFAULT 1 NOT NULL,
  created_by         VARCHAR2(5) NOT NULL,
  created_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by         VARCHAR2(5) NOT NULL,
  updated_at         TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  version_no         NUMBER(10)  DEFAULT 1 NOT NULL,
  delete_no          NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_parent_correspon_no_seq
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_parent_correspon_no_seq_01 ON parent_correspon_no_seq(site_id, discipline_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_parent_correspon_no_seq_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_parent_correspon_no_seq_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- reply_correspon_no_seq
---------------------------
CREATE TABLE reply_correspon_no_seq (
  id                  NUMBER(10)  NOT NULL,
  parent_correspon_id NUMBER(10)  NOT NULL,
  no                  NUMBER(3)   DEFAULT 1 NOT NULL,
  created_by          VARCHAR2(5) NOT NULL,
  created_at          TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by          VARCHAR2(5) NOT NULL,
  updated_at          TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  version_no          NUMBER(10)  DEFAULT 1 NOT NULL,
  delete_no           NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_reply_correspon_no_seq
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_reply_correspon_no_seq_01 ON reply_correspon_no_seq(parent_correspon_id, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_reply_correspon_no_seq_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_reply_correspon_no_seq_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- workflow_template_user
---------------------------
CREATE TABLE workflow_template_user (
  id         NUMBER(10)         NOT NULL,
  project_id VARCHAR2(11)       NOT NULL,
  emp_no     VARCHAR2(5)        NOT NULL,
  name       VARCHAR2(100 CHAR) NOT NULL,
  created_by VARCHAR2(5)        NOT NULL,
  created_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by VARCHAR2(5)        NOT NULL,
  updated_at TIMESTAMP          DEFAULT SYSTIMESTAMP NOT NULL,
  version_no NUMBER(10)         DEFAULT 1 NOT NULL,
  delete_no  NUMBER(10)         DEFAULT 0 NOT NULL,
  CONSTRAINT pk_workflow_template_user
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_workflow_template_user_01 ON workflow_template_user(project_id, emp_no, name, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_workflow_template_user_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_workflow_template_user_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- workflow_template
---------------------------
CREATE TABLE workflow_template (
  id                        NUMBER(10)  NOT NULL,
  workflow_template_user_id NUMBER(10)  NOT NULL,
  emp_no                    VARCHAR2(5) NOT NULL,
  workflow_type             NUMBER(2)   NOT NULL,
  workflow_no               NUMBER(10)  NOT NULL,
  created_by                VARCHAR2(5) NOT NULL,
  created_at                TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                VARCHAR2(5) NOT NULL,
  updated_at                TIMESTAMP   DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                 NUMBER(10)  DEFAULT 0 NOT NULL,
  CONSTRAINT pk_workflow_template
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE UNIQUE INDEX ix_workflow_template_01 ON workflow_template(workflow_template_user_id, emp_no, workflow_type, delete_no)
  TABLESPACE ${idx_tablespace};
CREATE SEQUENCE s_workflow_template_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_workflow_template_dno
  START WITH 1
  NOCACHE
;
--
---------------------------
-- rep_v_correspon
---------------------------
CREATE TABLE rep_v_correspon (
    id                          NUMBER(10)          NOT NULL,
    correspon_no                VARCHAR2(100 CHAR)  ,
    project_id                  VARCHAR2(11)        NOT NULL,
    from_correspon_group_id     NUMBER(10)          NOT NULL,
    previous_rev_correspon_id   NUMBER(10)          ,
    from_correspon_group_name   VARCHAR2(100 CHAR)  ,
    correspon_type_id           NUMBER(10)          NOT NULL,
    correspon_type              VARCHAR2(10)        ,
    correspon_type_name         VARCHAR2(100 CHAR)  ,
    allow_approver_to_browse    NUMBER(1)           ,
    force_to_use_workflow       NUMBER(1)           ,
    workflow_pattern_id         NUMBER(10)          ,
    workflow_cd                 VARCHAR2(10)        ,
    workflow_pattern_name       VARCHAR2(100 CHAR)  ,
    subject                     VARCHAR2(300 CHAR)  NOT NULL,
    body                        CLOB                NOT NULL,
    issued_by                   VARCHAR(5)          ,
    issued_at                   TIMESTAMP           ,
    correspon_status            NUMBER(1)           DEFAULT 0 NOT NULL,
    reply_required              NUMBER(1)           NOT NULL,
    deadline_for_reply          DATE                ,
    requested_approval_at       TIMESTAMP           ,
    workflow_status             NUMBER(2)           DEFAULT 0 NOT NULL,
    --
    -- custom_field
    custom_field1_id            NUMBER(10)         ,
    custom_field1_label         VARCHAR2(100 CHAR) ,
    custom_field1_value         VARCHAR2(100 CHAR) ,
    custom_field2_id            NUMBER(10)         ,
    custom_field2_label         VARCHAR2(100 CHAR) ,
    custom_field2_value         VARCHAR2(100 CHAR) ,
    custom_field3_id            NUMBER(10)         ,
    custom_field3_label         VARCHAR2(100 CHAR) ,
    custom_field3_value         VARCHAR2(100 CHAR) ,
    custom_field4_id            NUMBER(10)         ,
    custom_field4_label         VARCHAR2(100 CHAR) ,
    custom_field4_value         VARCHAR2(100 CHAR) ,
    custom_field5_id            NUMBER(10)         ,
    custom_field5_label         VARCHAR2(100 CHAR) ,
    custom_field5_value         VARCHAR2(100 CHAR) ,
    custom_field6_id            NUMBER(10)         ,
    custom_field6_label         VARCHAR2(100 CHAR) ,
    custom_field6_value         VARCHAR2(100 CHAR) ,
    custom_field7_id            NUMBER(10)         ,
    custom_field7_label         VARCHAR2(100 CHAR) ,
    custom_field7_value         VARCHAR2(100 CHAR) ,
    custom_field8_id            NUMBER(10)         ,
    custom_field8_label         VARCHAR2(100 CHAR) ,
    custom_field8_value         VARCHAR2(100 CHAR) ,
    custom_field9_id            NUMBER(10)         ,
    custom_field9_label         VARCHAR2(100 CHAR) ,
    custom_field9_value         VARCHAR2(100 CHAR) ,
    custom_field10_id           NUMBER(10)         ,
    custom_field10_label        VARCHAR2(100 CHAR) ,
    custom_field10_value        VARCHAR2(100 CHAR) ,
    --
    created_by                  VARCHAR2(5)         NOT NULL,
    created_at                  TIMESTAMP           NOT NULL,
    updated_by                  VARCHAR2(5)         NOT NULL,
    updated_at                  TIMESTAMP           NOT NULL,
    version_no                  NUMBER(10)          DEFAULT 1 NOT NULL,
    delete_no                   NUMBER(10)          DEFAULT 0 NOT NULL,
    correspon_access_control_flags NUMBER(3)        DEFAULT 255 NOT NULL,

    -- PRIMARY KEY
    CONSTRAINT pk_rep_v_correspon PRIMARY KEY(id)
      USING INDEX TABLESPACE ${idx_tablespace}
);
CREATE  INDEX ix_rep_v_correspon_01 ON rep_v_correspon(project_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_02 ON rep_v_correspon(from_correspon_group_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_03 ON rep_v_correspon(correspon_type_id)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_04 ON rep_v_correspon(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_05 ON rep_v_correspon(workflow_status)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_06 ON rep_v_correspon(created_by)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_rep_v_correspon_07 ON rep_v_correspon(updated_at)
  TABLESPACE ${idx_tablespace};
--
---------------------------
-- project_custom_setting
---------------------------
CREATE TABLE project_custom_setting (
    id                          NUMBER(10) NOT NULL,
    project_id                  VARCHAR2(11) NOT NULL,
    default_status              NUMBER(1) DEFAULT 0 NOT NULL,
    use_person_in_charge        NUMBER(1) DEFAULT 1 NOT NULL,
    created_by                  VARCHAR2(5) NOT NULL,
    created_at                  TIMESTAMP(6) DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by                  VARCHAR2(5) NOT NULL,
    updated_at                  TIMESTAMP(6) DEFAULT SYSTIMESTAMP NOT NULL,
    version_no                  NUMBER(10) DEFAULT 1 NOT NULL,
    delete_no                   NUMBER(10) DEFAULT 0 NOT NULL,
    use_correspon_access_control NUMBER(1) DEFAULT 0 NOT NULL,

    CONSTRAINT pk_project_custom_setting PRIMARY KEY (id) USING INDEX TABLESPACE ${idx_tablespace},
    CONSTRAINT ix_project_custom_setting_01 UNIQUE (project_id, delete_no) USING INDEX TABLESPACE ${idx_tablespace}
);

-- INDEX
CREATE INDEX ix_project_custom_setting_02 ON project_custom_setting(project_id) TABLESPACE ${idx_tablespace};
CREATE INDEX ix_project_custom_setting_03 ON project_custom_setting(delete_no) TABLESPACE ${idx_tablespace};

-- SEQUENCE
CREATE SEQUENCE s_project_custom_setting_id START WITH 1 NOCACHE;
CREATE SEQUENCE s_project_custom_setting_dno START WITH 1 NOCACHE;
--
---------------------------
-- favorite_filter
---------------------------
CREATE TABLE favorite_filter(
    id                    NUMBER(10, 0)         NOT NULL,
    project_id            VARCHAR2(11)          NOT NULL,
    emp_no                VARCHAR2(5)           NOT NULL,
    favorite_name         VARCHAR2(100 CHAR)    NOT NULL,
    search_conditions     CLOB                  NOT NULL,
    created_by            VARCHAR2(5)           NOT NULL,
    created_at            TIMESTAMP(0)          DEFAULT SYSTIMESTAMP NOT NULL,
    updated_by            VARCHAR2(5)           NOT NULL,
    updated_at            TIMESTAMP(0)          DEFAULT SYSTIMESTAMP NOT NULL,
    version_no            NUMBER(10, 0)         DEFAULT 1 NOT NULL,
    delete_no             NUMBER(10, 0)         DEFAULT 0 NOT NULL,
    CONSTRAINT pk_favorite_filter PRIMARY KEY (id)
    USING INDEX
TABLESPACE ${idx_tablespace}
)
;

-- INDEX
CREATE INDEX ix_favorite_filter_01 ON favorite_filter(project_id, emp_no, delete_no) TABLESPACE ${idx_tablespace};

-- SEQUENCE
CREATE SEQUENCE s_favorite_filter_id START WITH 1 NOCACHE;
CREATE SEQUENCE s_favorite_filter_dno START WITH 1 NOCACHE;

---------------------------
-- email_notice
---------------------------
CREATE TABLE email_notice (
  id                         NUMBER(10)             NOT NULL,
  project_id                 VARCHAR2(11)           NOT NULL,
  notice_category            NUMBER(1)              NOT NULL,
  event_cd                   NUMBER(1)              NOT NULL,
  notice_address_type        NUMBER(1)              NOT NULL,
  notice_status              NUMBER(1)              NOT NULL,
  notified_at                TIMESTAMP              ,
  mh_subject                 VARCHAR2(2000 CHAR)    NOT NULL,
  mh_to                      VARCHAR2(3000 CHAR)    NOT NULL,
  mh_from                    VARCHAR2(512 CHAR)     NOT NULL,
  mh_errors_to               VARCHAR2(5)            NOT NULL,
  correspon_id               NUMBER(10)             NOT NULL,
  mail_body                  VARCHAR2(4000 CHAR)    NOT NULL,
  created_by                 VARCHAR2(5)            NOT NULL,
  created_at                 TIMESTAMP              DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                 VARCHAR2(5)            NOT NULL,
  updated_at                 TIMESTAMP              DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                  NUMBER (10)            DEFAULT 0 NOT NULL,
  CONSTRAINT pk_email_notice
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);

-- INDEX
CREATE  INDEX ix_email_notice_01 ON email_notice(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_email_notice_02 ON email_notice(notice_status)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_email_notice_03 ON email_notice(correspon_id)
  TABLESPACE ${idx_tablespace};

-- SEQUENCE
CREATE SEQUENCE s_email_notice_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_email_notice_dno
  START WITH 1
  NOCACHE
;

---------------------------
-- email_notice_recv_setting
---------------------------
CREATE TABLE email_notice_recv_setting (
  id                           NUMBER(10)       NOT NULL,
  project_id                   VARCHAR2(11)     NOT NULL,
  emp_no                       VARCHAR2(5)      NOT NULL,
  receive_workflow             NUMBER(1)        NOT NULL,
  recv_distribution_attention  NUMBER(1)        NOT NULL,
  recv_distribution_cc         NUMBER(1)        NOT NULL,
  created_by                   VARCHAR2(5)      NOT NULL,
  created_at                   TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                   VARCHAR2(5)      NOT NULL,
  updated_at                   TIMESTAMP        DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                    NUMBER (10)      DEFAULT 0 NOT NULL,
  CONSTRAINT pk_email_notice_recv_setting
    PRIMARY KEY(id)
    USING INDEX TABLESPACE ${idx_tablespace}
);

-- INDEX
CREATE  INDEX ix_email_notice_rcv_setting_01 ON email_notice_recv_setting(delete_no)
  TABLESPACE ${idx_tablespace};
CREATE  INDEX ix_email_notice_rcv_setting_02 ON email_notice_recv_setting(project_id, emp_no, delete_no)
  TABLESPACE ${idx_tablespace};

-- SEQUENCE
CREATE SEQUENCE s_email_notice_recv_setting_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_email_notice_rcv_setting_dno
  START WITH 1
  NOCACHE
;

----------------------------------------
-- dist_template_header
----------------------------------------
CREATE TABLE dist_template_header (
  id                                  NUMBER(10)          NOT NULL,
  project_id                          VARCHAR2(11)        NOT NULL,
  emp_no                              VARCHAR2(5)         NOT NULL,
  template_cd                         VARCHAR2(10)        NOT NULL,
  name                                VARCHAR2(100 CHAR)  NOT NULL,
  option1                             VARCHAR2(100 CHAR)  ,
  option2                             VARCHAR2(100 CHAR)  ,
  option3                             VARCHAR2(100 CHAR)  ,
  created_by                          VARCHAR2(5)         NOT NULL,
  created_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                          VARCHAR2(5)         NOT NULL,
  updated_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  version_no                          NUMBER(10)          DEFAULT 1 NOT NULL,
  delete_no                           NUMBER(10)          DEFAULT 0 NOT NULL
)
PCTFREE 10
;
ALTER TABLE dist_template_header
  ADD CONSTRAINT pk_dist_template_header PRIMARY KEY (id) USING INDEX TABLESPACE ${idx_tablespace};
ALTER TABLE dist_template_header
  ADD CONSTRAINT uq_dist_template_header_01
  UNIQUE (project_id, emp_no, template_cd, delete_no) USING INDEX TABLESPACE ${idx_tablespace};

----------------------------------------
-- dist_template_group
----------------------------------------
CREATE TABLE dist_template_group (
  id                                  NUMBER(10)          NOT NULL,
  dist_template_header_id             NUMBER(10)          NOT NULL,
  distribution_type                   NUMBER(1)           NOT NULL,
  order_no                            NUMBER(2)           DEFAULT 0 NOT NULL,
  group_id                            NUMBER(10)          NOT NULL,
  created_by                          VARCHAR2(5)         NOT NULL,
  created_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                          VARCHAR2(5)         NOT NULL,
  updated_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                           NUMBER(10)          DEFAULT 0 NOT NULL
)
PCTFREE 5
;
ALTER TABLE dist_template_group
  ADD CONSTRAINT pk_dist_template_group PRIMARY KEY (id) USING INDEX TABLESPACE ${idx_tablespace};
ALTER TABLE dist_template_group
  ADD CONSTRAINT uq_dist_template_group_01
  UNIQUE (dist_template_header_id, distribution_type, order_no, delete_no) USING INDEX TABLESPACE ${idx_tablespace};
ALTER TABLE dist_template_group
  ADD CONSTRAINT fk_dist_template_group_01
  FOREIGN KEY (dist_template_header_id) REFERENCES dist_template_header(id);

----------------------------------------
-- dist_template_user
----------------------------------------
CREATE TABLE dist_template_user (
  id                                  NUMBER(10)          NOT NULL,
  dist_template_group_id              NUMBER(10)          NOT NULL,
  order_no                            NUMBER(2)           DEFAULT 0 NOT NULL,
  emp_no                              VARCHAR2(5)         NOT NULL,
  created_by                          VARCHAR2(5)         NOT NULL,
  created_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by                          VARCHAR2(5)         NOT NULL,
  updated_at                          TIMESTAMP           DEFAULT SYSTIMESTAMP NOT NULL,
  delete_no                           NUMBER(10)          DEFAULT 0 NOT NULL
)
PCTFREE 5
;
ALTER TABLE dist_template_user
  ADD CONSTRAINT pk_dist_template_user PRIMARY KEY (id) USING INDEX TABLESPACE ${idx_tablespace};
ALTER TABLE dist_template_user
  ADD CONSTRAINT uq_dist_template_user_01
  UNIQUE (dist_template_group_id, order_no, delete_no) USING INDEX TABLESPACE ${idx_tablespace};
ALTER TABLE dist_template_user
  ADD CONSTRAINT fk_dist_template_user_01
  FOREIGN KEY (dist_template_group_id) REFERENCES dist_template_group(id);

CREATE SEQUENCE s_dist_template_header_id START WITH 1 NOCACHE;
CREATE SEQUENCE s_dist_template_header_dno NOCACHE;

CREATE SEQUENCE s_dist_template_group_id START WITH 1 NOCACHE;
CREATE SEQUENCE s_dist_template_group_dno NOCACHE;

CREATE SEQUENCE s_dist_template_user_id START WITH 1 NOCACHE;
CREATE SEQUENCE s_dist_template_user_dno NOCACHE;

--
---------------------------
-- create foreign keys
---------------------------
ALTER TABLE project_company ADD CONSTRAINT fk_project_company_01
  FOREIGN KEY(company_id) REFERENCES company(id);
ALTER TABLE company_user ADD CONSTRAINT fk_company_user_01
  FOREIGN KEY(project_company_id) REFERENCES project_company(id);
ALTER TABLE correspon ADD CONSTRAINT fk_correspon_01
  FOREIGN KEY(from_correspon_group_id) REFERENCES correspon_group(id);
ALTER TABLE correspon ADD CONSTRAINT fk_correspon_02
  FOREIGN KEY(project_correspon_type_id) REFERENCES project_correspon_type(id);
ALTER TABLE correspon_group ADD CONSTRAINT fk_correspon_group_01
  FOREIGN KEY(site_id) REFERENCES site(id);
ALTER TABLE correspon_group ADD CONSTRAINT fk_correspon_group_02
  FOREIGN KEY(discipline_id) REFERENCES discipline(id);
ALTER TABLE correspon_type ADD CONSTRAINT fk_correspon_type_01
  FOREIGN KEY(workflow_pattern_id) REFERENCES workflow_pattern(id);
ALTER TABLE custom_field_value ADD CONSTRAINT fk_custom_field_value_01
  FOREIGN KEY(custom_field_id) REFERENCES custom_field(id);
ALTER TABLE attachment ADD CONSTRAINT fk_attachment_01
  FOREIGN KEY(correspon_id) REFERENCES correspon(id);
ALTER TABLE correspon_group_user ADD CONSTRAINT fk_correspon_group_user_01
  FOREIGN KEY(correspon_group_id) REFERENCES correspon_group(id);
ALTER TABLE correspon_read_status ADD CONSTRAINT fk_correspon_read_status_01
  FOREIGN KEY(correspon_id) REFERENCES correspon(id);
ALTER TABLE correspon_custom_field ADD CONSTRAINT fk_correspon_custom_field_01
  FOREIGN KEY(correspon_id) REFERENCES correspon(id);
ALTER TABLE correspon_custom_field ADD CONSTRAINT fk_correspon_custom_field_02
  FOREIGN KEY(project_custom_field_id) REFERENCES project_custom_field(id);
ALTER TABLE address_correspon_group ADD CONSTRAINT fk_address_correspon_group_01
  FOREIGN KEY(correspon_id) REFERENCES correspon(id);
ALTER TABLE address_correspon_group ADD CONSTRAINT fk_address_correspon_group_02
  FOREIGN KEY(correspon_group_id) REFERENCES correspon_group(id);
ALTER TABLE address_user ADD CONSTRAINT fk_address_user_01
  FOREIGN KEY(address_correspon_group_id) REFERENCES address_correspon_group(id);
ALTER TABLE correspon_hierarchy ADD CONSTRAINT fk_correspon_hierarchy_01
  FOREIGN KEY(parent_correspon_id) REFERENCES correspon(id);
ALTER TABLE correspon_hierarchy ADD CONSTRAINT fk_correspon_hierarchy_02
  FOREIGN KEY(child_correspon_id) REFERENCES correspon(id);
ALTER TABLE workflow ADD CONSTRAINT fk_workflow_01
  FOREIGN KEY(correspon_id) REFERENCES correspon(id);
ALTER TABLE project_custom_field ADD CONSTRAINT fk_project_custom_field_01
  FOREIGN KEY(custom_field_id) REFERENCES custom_field(id);
ALTER TABLE project_correspon_type ADD CONSTRAINT fk_project_correspon_type_01
  FOREIGN KEY(correspon_type_id) REFERENCES correspon_type(id);
ALTER TABLE person_in_charge ADD CONSTRAINT fk_person_in_charge_01
  FOREIGN KEY(address_user_id) REFERENCES address_user(id);
ALTER TABLE workflow_template ADD CONSTRAINT fk_workflow_template_01
  FOREIGN KEY(workflow_template_user_id) REFERENCES workflow_template_user(id);
ALTER TABLE rep_v_correspon ADD CONSTRAINT fk_rep_v_correspon_01
    FOREIGN KEY(id) REFERENCES correspon(id);
