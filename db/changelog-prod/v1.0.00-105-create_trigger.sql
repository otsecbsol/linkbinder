--liquibase formatted sql
--changeset opentone:v1.0.00-105.1-create_trigger endDelimiter:\n/\s*\n|\n/\s*$
--
--------------------------------------------
-- create triggers
--------------------------------------------
CREATE OR REPLACE TRIGGER upd_correspon
    AFTER INSERT OR UPDATE OR DELETE ON correspon
FOR EACH ROW
DECLARE
    val_f_correspon_group_name      VARCHAR2(100);
    val_correspon_type              VARCHAR2(10);
    val_correspon_type_name         VARCHAR2(100);
    val_allow_approver_to_browse    NUMBER(1);
    val_force_to_use_workflow       NUMBER(1);
    val_correspon_access_flags      NUMBER(3);
    val_workflow_pattern_id         NUMBER(10);
    val_workflow_cd                 VARCHAR2(10);
    val_workflow_pattern_name       VARCHAR2(100);
BEGIN
    IF INSERTING OR UPDATING THEN
        SELECT name INTO val_f_correspon_group_name
            FROM correspon_group
                WHERE id = :new.from_correspon_group_id AND delete_no = 0;
        SELECT
            correspon_type,
            name,
            allow_approver_to_browse,
            force_to_use_workflow,
            correspon_access_control_flags,
            workflow_pattern_id,
            workflow_cd,
            workflow_pattern_name
        INTO
            val_correspon_type,
            val_correspon_type_name,
            val_allow_approver_to_browse,
            val_force_to_use_workflow,
            val_correspon_access_flags,
            val_workflow_pattern_id,
            val_workflow_cd,
            val_workflow_pattern_name
            FROM v_project_correspon_type
                WHERE project_correspon_type_id = :new.project_correspon_type_id;

        IF INSERTING THEN
            INSERT INTO rep_v_correspon(
                id,
                correspon_no,
                project_id,
                from_correspon_group_id,
                previous_rev_correspon_id,
                from_correspon_group_name,
                correspon_type_id,
                correspon_type,
                correspon_type_name,
                allow_approver_to_browse,
                force_to_use_workflow,
                correspon_access_control_flags,
                workflow_pattern_id,
                workflow_cd,
                workflow_pattern_name,
                subject,
                body,
                issued_at,
                issued_by,
                correspon_status,
                reply_required,
                deadline_for_reply,
                requested_approval_at,
                workflow_status,
                created_by,
                created_at,
                updated_by,
                updated_at,
                version_no,
                delete_no
            ) VALUES(
                :new.id,
                :new.correspon_no,
                :new.project_id,
                :new.from_correspon_group_id,
                :new.previous_rev_correspon_id,
                val_f_correspon_group_name,
                :new.project_correspon_type_id,
                val_correspon_type,
                val_correspon_type_name,
                val_allow_approver_to_browse,
                val_force_to_use_workflow,
                val_correspon_access_flags,
                val_workflow_pattern_id,
                val_workflow_cd,
                val_workflow_pattern_name,
                :new.subject,
                :new.body,
                :new.issued_at,
                :new.issued_by,
                :new.correspon_status,
                :new.reply_required,
                :new.deadline_for_reply,
                :new.requested_approval_at,
                :new.workflow_status,
                :new.created_by,
                :new.created_at,
                :new.updated_by,
                :new.updated_at,
                :new.version_no,
                :new.delete_no
            );
        END IF;
        IF UPDATING THEN
            UPDATE rep_v_correspon
                SET correspon_no = :new.correspon_no,
                    project_id = :new.project_id,
                    from_correspon_group_id = :new.from_correspon_group_id,
                    previous_rev_correspon_id = :new.previous_rev_correspon_id,
                    from_correspon_group_name = val_f_correspon_group_name,
                    correspon_type_id = :new.project_correspon_type_id,
                    correspon_type = val_correspon_type,
                    correspon_type_name = val_correspon_type_name,
                    allow_approver_to_browse = val_allow_approver_to_browse,
                    force_to_use_workflow = val_force_to_use_workflow,
                    correspon_access_control_flags = val_correspon_access_flags,
                    workflow_pattern_id = val_workflow_pattern_id,
                    workflow_cd = val_workflow_cd,
                    workflow_pattern_name = val_workflow_pattern_name,
                    subject = :new.subject,
                    body = :new.body,
                    issued_at = :new.issued_at,
                    issued_by = :new.issued_by,
                    correspon_status = :new.correspon_status,
                    reply_required = :new.reply_required,
                    deadline_for_reply = :new.deadline_for_reply,
                    requested_approval_at = :new.requested_approval_at,
                    workflow_status = :new.workflow_status,
                    created_by = :new.created_by,
                    created_at = :new.created_at,
                    updated_by = :new.updated_by,
                    updated_at = :new.updated_at,
                    version_no = :new.version_no,
                    delete_no = :new.delete_no
                WHERE id = :new.id;
        END IF;
    END IF;
    IF DELETING THEN
        DELETE FROM rep_v_correspon WHERE id = :old.id;
    END IF;
END;
/

--changeset opentone:v1.0.00-105.2-create_trigger endDelimiter:\n/\s*\n|\n/\s*$
CREATE OR REPLACE TRIGGER upd_correspon_custom_field
    AFTER INSERT OR UPDATE OR DELETE ON correspon_custom_field
FOR EACH ROW
DECLARE
    val_project_id          VARCHAR2(11);
    val_custom_field_no     NUMBER;
    var_custom_field_label  VARCHAR2(100);
BEGIN
    IF INSERTING THEN
        SELECT project_id INTO val_project_id
            FROM correspon WHERE id = :new.correspon_id;
        SELECT no, label INTO val_custom_field_no, var_custom_field_label
            FROM v_project_custom_field
                WHERE project_custom_field_id = :new.project_custom_field_id
                    AND project_id = val_project_id
                    AND delete_no = 0;
        IF val_custom_field_no = 1 THEN
            UPDATE rep_v_correspon
            SET custom_field1_id = :new.project_custom_field_id,
                custom_field1_label = var_custom_field_label,
                custom_field1_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 2 THEN
            UPDATE rep_v_correspon
            SET custom_field2_id = :new.project_custom_field_id,
                custom_field2_label = var_custom_field_label,
                custom_field2_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 3 THEN
            UPDATE rep_v_correspon
            SET custom_field3_id = :new.project_custom_field_id,
                custom_field3_label = var_custom_field_label,
                custom_field3_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 4 THEN
            UPDATE rep_v_correspon
            SET custom_field4_id = :new.project_custom_field_id,
                custom_field4_label = var_custom_field_label,
                custom_field4_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 5 THEN
            UPDATE rep_v_correspon
            SET custom_field5_id = :new.project_custom_field_id,
                custom_field5_label = var_custom_field_label,
                custom_field5_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 6 THEN
            UPDATE rep_v_correspon
            SET custom_field6_id = :new.project_custom_field_id,
                custom_field6_label = var_custom_field_label,
                custom_field6_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 7 THEN
            UPDATE rep_v_correspon
            SET custom_field7_id = :new.project_custom_field_id,
                custom_field7_label = var_custom_field_label,
                custom_field7_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 8 THEN
            UPDATE rep_v_correspon
            SET custom_field8_id = :new.project_custom_field_id,
                custom_field8_label = var_custom_field_label,
                custom_field8_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 9 THEN
            UPDATE rep_v_correspon
            SET custom_field9_id = :new.project_custom_field_id,
                custom_field9_label = var_custom_field_label,
                custom_field9_value = :new.value
            WHERE id = :new.correspon_id;
        ELSIF val_custom_field_no = 10 THEN
            UPDATE rep_v_correspon
            SET custom_field10_id = :new.project_custom_field_id,
                custom_field10_label = var_custom_field_label,
                custom_field10_value = :new.value
            WHERE id = :new.correspon_id;
        END IF;
    END IF;
    IF UPDATING THEN
        UPDATE rep_v_correspon
        SET custom_field1_id = NULL,
            custom_field1_label = NULL,
            custom_field1_value = NULL,
            custom_field2_id = NULL,
            custom_field2_label = NULL,
            custom_field2_value = NULL,
            custom_field3_id = NULL,
            custom_field3_label = NULL,
            custom_field3_value = NULL,
            custom_field4_id = NULL,
            custom_field4_label = NULL,
            custom_field4_value = NULL,
            custom_field5_id = NULL,
            custom_field5_label = NULL,
            custom_field5_value = NULL,
            custom_field6_id = NULL,
            custom_field6_label = NULL,
            custom_field6_value = NULL,
            custom_field7_id = NULL,
            custom_field7_label = NULL,
            custom_field7_value = NULL,
            custom_field8_id = NULL,
            custom_field8_label = NULL,
            custom_field8_value = NULL,
            custom_field9_id = NULL,
            custom_field9_label = NULL,
            custom_field9_value = NULL,
            custom_field10_id = NULL,
            custom_field10_label = NULL,
            custom_field10_value = NULL
        WHERE id = :new.correspon_id;
    END IF;
    IF DELETING THEN
        SELECT project_id INTO val_project_id
            FROM correspon WHERE id = :old.correspon_id;
        SELECT no INTO val_custom_field_no
            FROM v_project_custom_field
                WHERE project_custom_field_id = :old.project_custom_field_id
                    AND project_id = val_project_id
                    AND delete_no = 0;
        IF val_custom_field_no = 1 THEN
            UPDATE rep_v_correspon
            SET custom_field1_id = NULL,
                custom_field1_label = NULL,
                custom_field1_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 2 THEN
            UPDATE rep_v_correspon
            SET custom_field2_id = NULL,
                custom_field2_label = NULL,
                custom_field2_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 3 THEN
            UPDATE rep_v_correspon
            SET custom_field3_id = NULL,
                custom_field3_label = NULL,
                custom_field3_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 4 THEN
            UPDATE rep_v_correspon
            SET custom_field4_id = NULL,
                custom_field4_label = NULL,
                custom_field4_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 5 THEN
            UPDATE rep_v_correspon
            SET custom_field5_id = NULL,
                custom_field5_label = NULL,
                custom_field5_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 6 THEN
            UPDATE rep_v_correspon
            SET custom_field6_id = NULL,
                custom_field6_label = NULL,
                custom_field6_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 7 THEN
            UPDATE rep_v_correspon
            SET custom_field7_id = NULL,
                custom_field7_label = NULL,
                custom_field7_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 8 THEN
            UPDATE rep_v_correspon
            SET custom_field8_id = NULL,
                custom_field8_label = NULL,
                custom_field8_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 9 THEN
            UPDATE rep_v_correspon
            SET custom_field9_id = NULL,
                custom_field9_label = NULL,
                custom_field9_value = NULL
            WHERE id = :old.correspon_id;
        ELSIF val_custom_field_no = 10 THEN
            UPDATE rep_v_correspon
            SET custom_field10_id = NULL,
                custom_field10_label = NULL,
                custom_field10_value = NULL
            WHERE id = :old.correspon_id;
        END IF;
    END IF;
END;
/
--rollback DROP TRIGGER upd_correspon_custom_field;
--rollback DROP TRIGGER upd_correspon;

