--liquibase formatted sql
--changeset opentone:v1.1.00-108-modify_trigger_for_add_column_for_learning endDelimiter:\n/\s*\n|\n/\s*$
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
                for_learning,
                for_learning_src_id,
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
                :new.for_learning,
                :new.for_learning_src_id,
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
                    for_learning = :new.for_learning,
                    for_learning_src_id = :new.for_learning_src_id,
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
--rollback DROP TRIGGER upd_correspon;

