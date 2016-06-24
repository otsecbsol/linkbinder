/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.linkbinder.dto;

import java.util.Date;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * テーブル [v_workflow] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class Workflow extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1314504891064737647L;

    /**
     * Id.
     * <p>
     * [v_workflow.id]
     * </p>
     */
    private Long id;

    /**
     * Correspon.
     * <p>
     * [v_workflow.correspon_id]
     * </p>
     */
    private Long corresponId;

    /**
     * User.
     */
    private User user;

    /**
     * Workflow type.
     * <p>
     * [v_workflow.workflow_type]
     * </p>
     */
    private WorkflowType workflowType;

    /**
     * Workflow no.
     * <p>
     * [v_workflow.workflow_no]
     * </p>
     */
    private Long workflowNo;

    /**
     * Workflow process status.
     * <p>
     * [v_workflow.workflow_process_status]
     * </p>
     */
    private WorkflowProcessStatus workflowProcessStatus;

    /**
     * CommentOn.
     * <p>
     * [v_workflow.comment_on]
     * </p>
     */
    private String commentOn;

    /**
     * Finished by.
     * <p>
     * [v_workflow.finished_by]
     * </p>
     */
    private User finishedBy;

    /**
     * Finished at.
     * <p>
     * [v_workflow.finished_at]
     * </p>
     */
    private Date finishedAt;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_workflow.created_at]
     * </p>
     */
    private Date createdAt;

    /**
     * Updated by.
     * <p>
     * </p>
     */
    private User updatedBy;

    /**
     * Updated at.
     * <p>
     * [v_workflow.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_workflow.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_workflow.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * このユーザーが所属する活動単位.
     */
    private CorresponGroup corresponGroup;

    /**
     * 空のインスタンスを生成する.
     */
    public Workflow() {
    }

    /**
     * このワークフローがCheckerであればtrueを返す.
     * @return Checkerの場合true
     */
    public boolean isChecker() {
        return workflowType == WorkflowType.CHECKER;
    }

    /**
     * このワークフローがApproverであればtrueを返す.
     * @return Approverの場合true
     */
    public boolean isApprover() {
        return workflowType == WorkflowType.APPROVER;
    }

    /**
     * id の値を返す.
     * <p>
     * [v_workflow.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_workflow.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * corresponId の値を返す.
     * <p>
     * [v_workflow.correspon_id]
     * </p>
     * @return corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * corresponId の値を設定する.
     * <p>
     * [v_workflow.correspon_id]
     * </p>
     * @param corresponId
     *            corresponId
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }

    /**
     * workflowType の値を返す.
     * <p>
     * [v_workflow.workflow_type]
     * </p>
     * @return workflowType
     */
    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    /**
     * workflowType の値を設定する.
     * <p>
     * [v_workflow.workflow_type]
     * </p>
     * @param workflowType
     *            workflowType
     */
    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    /**
     * workflowNo の値を返す.
     * <p>
     * [v_workflow.workflow_no]
     * </p>
     * @return workflowNo
     */
    public Long getWorkflowNo() {
        return workflowNo;
    }

    /**
     * workflowNo の値を設定する.
     * <p>
     * [v_workflow.workflow_no]
     * </p>
     * @param workflowNo
     *            workflowNo
     */
    public void setWorkflowNo(Long workflowNo) {
        this.workflowNo = workflowNo;
    }

    /**
     * workflowProcessStatus の値を返す.
     * <p>
     * [v_workflow.workflow_process_status]
     * </p>
     * @return workflowProcessStatus
     */
    public WorkflowProcessStatus getWorkflowProcessStatus() {
        return workflowProcessStatus;
    }

    /**
     * workflowProcessStatus の値を設定する.
     * <p>
     * [v_workflow.workflow_process_status]
     * </p>
     * @param workflowProcessStatus
     *            workflowProcessStatus
     */
    public void setWorkflowProcessStatus(WorkflowProcessStatus workflowProcessStatus) {
        this.workflowProcessStatus = workflowProcessStatus;
    }

    /**
     * commentOn の値を返す.
     * <p>
     * [v_workflow.commentOn]
     * </p>
     * @return commentOn
     */
    public String getCommentOn() {
        return commentOn;
    }

    /**
     * commentOn の値を設定する.
     * <p>
     * [v_workflow.commentOn]
     * </p>
     * @param commentOn
     *            commentOn
     */
    public void setCommentOn(String commentOn) {
        this.commentOn = commentOn;
    }

    /**
     * finishedAt の値を返す.
     * <p>
     * [v_workflow.finished_at]
     * </p>
     * @return finishedAt
     */
    public Date getFinishedAt() {
        return CloneUtil.cloneDate(finishedAt);
    }

    /**
     * finishedAt の値を設定する.
     * <p>
     * [v_workflow.finished_at]
     * </p>
     * @param finishedAt
     *            finishedAt
     */
    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = CloneUtil.cloneDate(finishedAt);
    }

    /**
     * 作成者を返す.
     * <p>
     * </p>
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 作成者を設定する.
     * <p>
     * </p>
     * @param createdBy
     *            作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * <p>
     * [v_workflow.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_workflow.created_at]
     * </p>
     * @param createdAt
     *            createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 更新者を返す.
     * <p>
     * </p>
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 更新者を設定する.
     * <p>
     * </p>
     * @param updatedBy
     *            更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * <p>
     * [v_workflow.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_workflow.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * versionNo の値を返す.
     * <p>
     * [v_workflow.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_workflow.version_no]
     * </p>
     * @param versionNo
     *            versionNo
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [v_workflow.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_workflow.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param finishedBy
     *            the finishedBy to set
     */
    public void setFinishedBy(User finishedBy) {
        this.finishedBy = finishedBy;
    }

    /**
     * @return the finishedBy
     */
    public User getFinishedBy() {
        return finishedBy;
    }

    /**
     * @param corresponGroup the corresponGroup to set
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * @return the corresponGroup
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
    }
}
