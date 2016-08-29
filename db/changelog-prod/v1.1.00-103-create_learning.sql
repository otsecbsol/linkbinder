--liquibase formatted sql
--changeset opentone:v1.1.00-103-create_learning

-- --------------------------------
-- label
-- --------------------------------
CREATE TABLE learning_label (
  id            NUMBER(10)            NOT NULL,
  name          VARCHAR2(300 CHAR)    NOT NULL,
  created_by    VARCHAR2(5)           NOT NULL,
  created_at    TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)           NOT NULL,
  updated_at    TIMESTAMP             DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)            DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)            DEFAULT 0 NOT NULL,
  CONSTRAINT pk_learning_label
  PRIMARY KEY (id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
--
CREATE UNIQUE INDEX ix_learning_label_01 ON learning_label(name, delete_no)
  TABLESPACE ${idx_tablespace};
--
CREATE SEQUENCE s_learning_label_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_learning_label_dno
  START WITH 1
  NOCACHE
;
--
CREATE TABLE correspon_learning_label (
  id            NUMBER(10)       NOT NULL,
  correspon_id  NUMBER(10)       NOT NULL,
  label_id      NUMBER(10)       NOT NULL,
  created_by    VARCHAR2(5)      NOT NULL,
  created_at    TIMESTAMP(6)     DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)      NOT NULL,
  updated_at    TIMESTAMP(6)     DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)       DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)       DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_learning_label
  PRIMARY KEY (id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
--
ALTER TABLE correspon_learning_label
  ADD CONSTRAINT correspon_learning_label_fk1 FOREIGN KEY(correspon_id)
  REFERENCES correspon(id);
ALTER TABLE correspon_learning_label
  ADD CONSTRAINT correspon_learning_label_fk2 FOREIGN KEY(label_id)
  REFERENCES learning_label(id);
--
CREATE INDEX ix_cp_learning_label_01 ON correspon_learning_label (correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE INDEX ix_cp_learning_label_02 ON correspon_learning_label (label_id)
  TABLESPACE ${idx_tablespace};
CREATE UNIQUE INDEX ix_cp_learning_label_03 ON correspon_learning_label(label_id, correspon_id, delete_no)
  TABLESPACE ${idx_tablespace};
--
CREATE SEQUENCE s_correspon_learning_label_id
  START WITH 1
  NOCACHE;
CREATE SEQUENCE s_correspon_learning_label_dno
  START WITH 1
  NOCACHE;

-- --------------------------------
-- tag
-- --------------------------------
CREATE TABLE learning_tag (
  id            NUMBER(10)            NOT NULL,
  name          VARCHAR2(300 CHAR)    NOT NULL,
  created_by    VARCHAR2(5)           NOT NULL,
  created_at    TIMESTAMP(6)          DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)           NOT NULL,
  updated_at    TIMESTAMP(6)          DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)            DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)            DEFAULT 0 NOT NULL,
  CONSTRAINT pk_learning_tag
  PRIMARY KEY (id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
--
CREATE UNIQUE INDEX ix_learning_tag_01 ON learning_tag(name, delete_no)
  TABLESPACE ${idx_tablespace};
--
CREATE SEQUENCE s_learning_tag_id
  START WITH 1
  NOCACHE
;
CREATE SEQUENCE s_learning_tag_dno
  START WITH 1
  NOCACHE
;
--
CREATE TABLE correspon_learning_tag (
  id            NUMBER(10)       NOT NULL,
  correspon_id  NUMBER(10)       NOT NULL,
  tag_id        NUMBER(10)       NOT NULL,
  created_by    VARCHAR2(5)      NOT NULL,
  created_at    TIMESTAMP(6)     DEFAULT SYSTIMESTAMP NOT NULL,
  updated_by    VARCHAR2(5)      NOT NULL,
  updated_at    TIMESTAMP(6)     DEFAULT SYSTIMESTAMP NOT NULL,
  version_no    NUMBER(10)       DEFAULT 1 NOT NULL,
  delete_no     NUMBER(10)       DEFAULT 0 NOT NULL,
  CONSTRAINT pk_correspon_learning_tag
  PRIMARY KEY (id)
    USING INDEX TABLESPACE ${idx_tablespace}
);
--
ALTER TABLE correspon_learning_tag
  ADD CONSTRAINT correspon_learning_tag_fk1 FOREIGN KEY(correspon_id)
  REFERENCES correspon(id);
--
ALTER TABLE correspon_learning_tag
  ADD CONSTRAINT correspon_learning_tag_fk2 FOREIGN KEY(tag_id)
  REFERENCES learning_tag(id);
--
CREATE INDEX ix_cp_learning_tag_01 ON correspon_learning_tag (correspon_id)
  TABLESPACE ${idx_tablespace};
CREATE INDEX ix_cp_learning_tag_02 ON correspon_learning_tag (tag_id)
  TABLESPACE ${idx_tablespace};
CREATE UNIQUE INDEX ix_cp_learning_tag_03 ON correspon_learning_tag(tag_id, correspon_id, delete_no)
  TABLESPACE ${idx_tablespace};
--
CREATE SEQUENCE s_correspon_learning_tag_id
  START WITH 1
  NOCACHE;
CREATE SEQUENCE s_correspon_learning_tag_dno
  START WITH 1
  NOCACHE;

