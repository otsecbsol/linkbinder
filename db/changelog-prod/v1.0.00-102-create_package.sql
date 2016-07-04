--liquibase formatted sql

--changeset opentone:v1.0.00-102.1-create_package endDelimiter:\n/\s*\n|\n/\s*$
--
-- a type of correspon history record.
--
CREATE
TYPE r_correspon_history AS OBJECT (
  id                         NUMBER(10),
  correspon_no               VARCHAR2(100),
  issued_at                  TIMESTAMP,
  from_correspon_group_id    NUMBER(10),
  from_correspon_group_name  VARCHAR2(100),
  subject                    VARCHAR2(100),
  correspon_status           NUMBER(1),
  workflow_status            NUMBER(1),
  deadline_for_reply         TIMESTAMP,
  address_correspon_group_id NUMBER(10),
  to_correspon_group_id      NUMBER(10),
  to_correspon_group_name    VARCHAR2(100),
  lvl                        NUMBER,
  correspon_url              VARCHAR2(1000)
);
/

--changeset opentone:v1.0.00-102.2-create_package endDelimiter:\n/\s*\n|\n/\s*$
--
-- a type of r_correspon_history's record set .
--
CREATE
TYPE rs_correspon_history AS TABLE OF r_correspon_history;
/

--changeset opentone:v1.0.00-102.3-create_package endDelimiter:\n/\s*\n|\n/\s*$
--
-- Package
--
CREATE OR REPLACE
PACKAGE CORRESPON_ACTION AS
  /** cursor type of correspn history */
  TYPE cur_type_correspon_history IS REF CURSOR;

  /**
   * Return correspon history contains correspon owns specified id.
   * @param id correspon_id
   * @return  cursor of correspon history
   */
  FUNCTION get_correspon_history(id IN NUMBER) RETURN cur_type_correspon_history;

  /** Constants of Correspon URL */
  CORRESPON_URL CONSTANT VARCHAR2(1024) := '${corresponUrl}';
END CORRESPON_ACTION;
/

--changeset opentone:v1.0.00-102.4-create_package endDelimiter:\n/\s*\n|\n/\s*$
--
-- Package Body
--
CREATE OR REPLACE
PACKAGE BODY CORRESPON_ACTION AS
  ----------------------------------------------------------------------------
  -- Definition block of private subroutines
  ----------------------------------------------------------------------------
  FUNCTION find_root_correspon_id(current_correspon_id correspon.id%TYPE)
  RETURN correspon.id%TYPE
  IS
    CURSOR cur_select_root(correspon_id NUMBER) IS
      SELECT
        min(ch.parent_correspon_id) root_id
      FROM
        correspon_hierarchy ch
        INNER JOIN correspon c
           ON ch.child_correspon_id = c.id
          AND c.delete_no = 0
      START WITH
          ch.child_correspon_id = correspon_id
      AND ch.delete_no = 0
      CONNECT BY
          prior ch.parent_correspon_id = ch.child_correspon_id
      AND prior ch.delete_no = 0;
      ret correspon.id%TYPE;
  BEGIN
    OPEN cur_select_root(current_correspon_id);
    FETCH cur_select_root INTO ret;
    CLOSE cur_select_root;
    IF ret IS NULL THEN
      ret := current_correspon_id;
    END IF;
    RETURN ret;
  END;

  FUNCTION find_correspon_history(root_correspon_id correspon.id%TYPE)
  RETURN rs_correspon_history
  IS
    correspon_url_base VARCHAR2(1000);
    ret rs_correspon_history;
    rec r_correspon_history;
    CURSOR cur_select_history(root_correspon_id NUMBER) IS
      SELECT
        c.id,
        c.correspon_no,
        c.project_id,
        c.issued_at,
        c.from_correspon_group_id,
        fcg.name as from_correspon_group_name,
        c.subject,
        c.correspon_status,
        c.workflow_status,
        c.deadline_for_reply,
        acg.id as address_correspon_group_id,
        cg.id as to_correspon_group_id,
        cg.name as to_correspon_group_name,
        0 lvl,
        0 rn
      FROM correspon c
        INNER JOIN correspon_group fcg
           ON c.from_correspon_group_id = fcg.id
        INNER JOIN address_correspon_group acg
           ON c.id = acg.correspon_id
        INNER JOIN correspon_group cg
           ON acg.correspon_group_id = cg.id
      WHERE c.id = root_correspon_id
        AND c.delete_no = 0
        AND fcg.delete_no = 0
        AND acg.address_type = 1
        AND acg.delete_no = 0
        AND cg.delete_no = 0
      UNION
      SELECT
        ch.id,
        ch.correspon_no,
        ch.project_id,
        ch.issued_at,
        ch.from_correspon_group_id,
        fcg.name as from_correspon_group_name,
        ch.subject,
        ch.correspon_status,
        ch.workflow_status,
        ch.deadline_for_reply,
        acg.id as address_correspon_group_id,
        cg.id as to_correspon_group_id,
        cg.name as to_correspon_group_name,
        ch.lvl,
        ch.rn
      FROM (
          SELECT c.id,
                 c.correspon_no,
                 c.project_id,
                 c.issued_at,
                 c.from_correspon_group_id,
                 c.subject,
                 c.correspon_status,
                 c.deadline_for_reply,
                 c.workflow_status,
                 level lvl,
                 rownum rn
          FROM correspon_hierarchy ch
          INNER JOIN correspon c
             ON ch.child_correspon_id = c.id
            AND c.delete_no = 0
          START WITH ch.parent_correspon_id = root_correspon_id
                 AND ch.delete_no = 0
          CONNECT BY prior ch.child_correspon_id = ch.parent_correspon_id
          ORDER SIBLINGS BY
            c.issued_at,
            c.created_at
        ) ch
        INNER JOIN correspon_group fcg
           ON ch.from_correspon_group_id = fcg.id
        INNER JOIN address_correspon_group acg
           ON ch.id = acg.correspon_id
        INNER JOIN correspon_group cg
           ON acg.correspon_group_id = cg.id
      WHERE fcg.delete_no = 0
        AND acg.address_type = 1
        AND acg.delete_no = 0
        AND cg.delete_no = 0
      ORDER BY
        rn;
  BEGIN
    ret := rs_correspon_history();
    correspon_url_base := CORRESPON_URL;
    FOR rec_select_root IN cur_select_history(root_correspon_id) LOOP

    rec :=r_correspon_history(
      rec_select_root.id,
      rec_select_root.correspon_no,
      rec_select_root.issued_at,
      rec_select_root.from_correspon_group_id,
      rec_select_root.from_correspon_group_name,
      rec_select_root.subject,
      rec_select_root.correspon_status,
      rec_select_root.workflow_status,
      rec_select_root.deadline_for_reply,
      rec_select_root.address_correspon_group_id,
      rec_select_root.to_correspon_group_id,
      rec_select_root.to_correspon_group_name,
      rec_select_root.lvl,
      correspon_url_base ||
        '?projectId='|| rec_select_root.project_id ||
        CHR(38)||'id=' || rec_select_root.id
      );

      ret.EXTEND;
      ret(ret.LAST) := rec;
    END LOOP;
    RETURN ret;
  END;

  PROCEDURE store_unified_history_rec(wk_rec IN OUT r_correspon_history, exist_some_correspon_grps IN BOOLEAN,
    ret IN OUT rs_correspon_history) IS
  BEGIN
    IF wk_rec.id > -1 THEN
      IF exist_some_correspon_grps THEN
        wk_rec.to_correspon_group_name := wk_rec.to_correspon_group_name || '...';
      END IF;
      ret.EXTEND;
      ret(ret.LAST) := wk_rec;
    END IF;
  END;

  FUNCTION unify_to_correspon_groups(history rs_correspon_history)
  RETURN rs_correspon_history
  IS
    ret rs_correspon_history;
    wk_rec r_correspon_history := r_correspon_history(NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL);
    exist_some_correspon_grps BOOLEAN := FALSE;
  BEGIN
    ret := rs_correspon_history();
    wk_rec.id := -1;
    FOR r IN 1..history.LAST LOOP
      IF wk_rec.id = history(r).id THEN
        exist_some_correspon_grps := TRUE;
        IF history(r).to_correspon_group_id < wk_rec.to_correspon_group_id THEN
          wk_rec := history(r);
        END IF;
      ELSE
        store_unified_history_rec(wk_rec, exist_some_correspon_grps, ret);
        wk_rec := history(r);
        exist_some_correspon_grps := FALSE;
      END IF;
    END LOOP;
    store_unified_history_rec(wk_rec, exist_some_correspon_grps, ret);

    RETURN ret;
  END;
  ----------------------------------------------------------------------------
  -- Definition block of public procedures
  ----------------------------------------------------------------------------
  FUNCTION get_correspon_history(id IN NUMBER)
  RETURN cur_type_correspon_history
  IS
    root_correspon_id NUMBER := NULL;
    ret rs_correspon_history;
    rec r_correspon_history;
    cur_index NUMBER;
    cur_ret cur_type_correspon_history;
  BEGIN
    -- Find the id of the root correspon
    root_correspon_id := find_root_correspon_id(id);

    -- Get a correspon history from root correspon
    ret := find_correspon_history(root_correspon_id);

    -- Unify same corresopn records having saome to_correspon_groups
    IF ret.COUNT > 0 THEN
      ret := unify_to_correspon_groups(ret);
    END IF;

    -- Create cursor of correspon history
    OPEN cur_ret FOR SELECT * FROM TABLE(ret);
    RETURN cur_ret;
  END;

END CORRESPON_ACTION;
/
--rollback DROP TYPE rs_correspon_history;
--rollback DROP TYPE r_correspon_history;
--rollback DROP PACKAGE CORRESPON_ACTION;

