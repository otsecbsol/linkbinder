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
import java.util.EnumSet;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponTypeAdmittee;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * テーブル [v_project_correspon_type] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CorresponType extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6263229288862593313L;

    /**
     * Id.
     * <p>
     * [v_project_correspon_type.id]
     * </p>
     */
    private Long id;

    /**
     * Project correspon type.
     * <p>
     * [v_project_correspon_type.project_correspon_type_id]
     * </p>
     */
    private Long projectCorresponTypeId;

    /**
     * Project.
     * <p>
     * [v_project_correspon_type.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Project name e.
     * <p>
     * [v_project_correspon_type.project_name_e]
     * </p>
     */
    private String projectNameE;

    /**
     * Correspon type.
     * <p>
     * [v_project_correspon_type.correspon_type]
     * </p>
     */
    private String corresponType;

    /**
     * Name.
     * <p>
     * [v_project_correspon_type.name]
     * </p>
     */
    private String name;

    /**
     * Workflow pattern.
     */
    private WorkflowPattern workflowPattern;

    /**
     * Allow approver to browse.
     * <p>
     * [v_project_correspon_type.allow_approver_to_browse]
     * </p>
     */
    private AllowApproverToBrowse allowApproverToBrowse = null;

    /**
     * Force to use workflow.
     * <p>
     * [v_project_correspon_type.force_to_use_workflow]
     * </p>
     */
    private ForceToUseWorkflow forceToUseWorkflow = null;

    /**
     * Use whole.
     * <p>
     * [v_project_correspon_type.use_whole]
     * </p>
     */
    private UseWhole useWhole = null;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_project_correspon_type.created_at]
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
     * [v_project_correspon_type.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_project_correspon_type.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_project_correspon_type.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * この CorresponType を利用可能なユーザー種別.
     * <p>
     * [v_project_correspon_type.correspon_access_control_flags]
     * </p>
     */
    private EnumSet<CorresponTypeAdmittee> corresponAccessControlFlags;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponType() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_project_correspon_type.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_project_correspon_type.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * projectCorresponTypeId の値を返す.
     * <p>
     * [v_project_correspon_type.project_correspon_type_id]
     * </p>
     * @return projectCorresponTypeId
     */
    public Long getProjectCorresponTypeId() {
        return projectCorresponTypeId;
    }

    /**
     * projectCorresponTypeId の値を設定する.
     * <p>
     * [v_project_correspon_type.project_correspon_type_id]
     * </p>
     * @param projectCorresponTypeId
     *            projectCorresponTypeId
     */
    public void setProjectCorresponTypeId(Long projectCorresponTypeId) {
        this.projectCorresponTypeId = projectCorresponTypeId;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_project_correspon_type.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_project_correspon_type.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * projectNameE の値を返す.
     * <p>
     * [v_project_correspon_type.project_name_e]
     * </p>
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * <p>
     * [v_project_correspon_type.project_name_e]
     * </p>
     * @param projectNameE
     *            projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    public void setCorresponType(String corresponType) {
        this.corresponType = corresponType;
    }

    public String getCorresponType() {
        return corresponType;
    }

    /**
     * name の値を返す.
     * <p>
     * [v_project_correspon_type.name]
     * </p>
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name の値を設定する.
     * <p>
     * [v_project_correspon_type.name]
     * </p>
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * allowApproverToBrowse の値を返す.
     * <p>
     * [v_project_correspon_type.allow_approver_to_browse]
     * </p>
     * @return allowApproverToBrowse
     */
    public AllowApproverToBrowse getAllowApproverToBrowse() {
        return allowApproverToBrowse;
    }

    /**
     * allowApproverToBrowse の値を設定する.
     * <p>
     * [v_project_correspon_type.allow_approver_to_browse]
     * </p>
     * @param allowApproverToBrowse
     *            allowApproverToBrowse
     */
    public void setAllowApproverToBrowse(AllowApproverToBrowse allowApproverToBrowse) {
        this.allowApproverToBrowse = allowApproverToBrowse;
    }

    /**
     * forceToUseWorkflow の値を返す.
     * <p>
     * [v_project_correspon_type.force_to_use_workflow]
     * </p>
     * @return forceToUseWorkflow
     */
    public ForceToUseWorkflow getForceToUseWorkflow() {
        return forceToUseWorkflow;
    }

    /**
     * forceToUseWorkflow の値を設定する.
     * <p>
     * [v_project_correspon_type.force_to_use_workflow]
     * </p>
     * @param forceToUseWorkflow
     *            forceToUseWorkflow
     */
    public void setForceToUseWorkflow(ForceToUseWorkflow forceToUseWorkflow) {
        this.forceToUseWorkflow = forceToUseWorkflow;
    }

    /**
     * useWhole の値を返す.
     * <p>
     * [v_project_correspon_type.use_whole]
     * </p>
     * @return useWhole
     */
    public UseWhole getUseWhole() {
        return useWhole;
    }

    /**
     * useWhole の値を設定する.
     * <p>
     * [v_project_correspon_type.use_whole]
     * </p>
     * @param useWhole
     *            useWhole
     */
    public void setUseWhole(UseWhole useWhole) {
        this.useWhole = useWhole;
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
     * [v_project_correspon_type.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_project_correspon_type.created_at]
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
     * [v_project_correspon_type.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_project_correspon_type.updated_at]
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
     * [v_project_correspon_type.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_project_correspon_type.version_no]
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
     * [v_project_correspon_type.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_project_correspon_type.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param workflowPattern
     *            the workflowPattern to set
     */
    public void setWorkflowPattern(WorkflowPattern workflowPattern) {
        this.workflowPattern = workflowPattern;
    }

    /**
     * @return the workflowPattern
     */
    public WorkflowPattern getWorkflowPattern() {
        return workflowPattern;
    }

    /**
     * 表示項目：allowApproverを返却する.
     * @return 表示項目：allowApprover
     */
    public String getAllowApprover() {
        return ValueFormatter.formatLabel(allowApproverToBrowse, AllowApproverToBrowse.VISIBLE);
    }

    /**
     * 表示項目：userWorkflowを返却する.
     * @return 表示項目：userWorkflow
     */
    public String getUseWorkflow() {
        return ValueFormatter.formatLabel(forceToUseWorkflow, ForceToUseWorkflow.REQUIRED);
    }

    /**
     * 全体利用フラグがEachか判定する.
     * @return Eachならtrue / Each以外ならfalse
     */
    public boolean isUseWholeEach() {
        return useWhole != null && useWhole == UseWhole.EACH;
    }

    /**
     * Approver閲覧許可フラグが立っているか判定する.
     * @return フラグが立っているtrue / 立っていないfalse
     */
    public boolean isAllowApproverVisible() {
        return allowApproverToBrowse != null
            && allowApproverToBrowse == AllowApproverToBrowse.VISIBLE;
    }

    /**
     * 承認パターン適用フラグが立っているか判定する.
     * @return フラグが立っているtrue / 立っていないfalse
     */
    public boolean isForceToUseWorkflowRequired() {
        return forceToUseWorkflow != null && forceToUseWorkflow == ForceToUseWorkflow.REQUIRED;
    }

    /**
     * 表示用ラベルを返す.
     * @return 表示用ラベル
     */
    public String getLabel() {
        return ValueFormatter.formatCodeAndName(corresponType, name);
    }

    /**
     * この CorresponType を利用可能なユーザー種別を取得する.
     * @return 利用可能なユーザー種別 (CorresponTypeAdmittee) 列挙子の値を OR した 0 から
     *  255 の整数
     */
    public Integer getCorresponAccessControlFlags() {
        if (corresponAccessControlFlags == null) {
            return 0;
        }
        return CorresponTypeAdmittee.getIntValueFromEnumSet(
                    corresponAccessControlFlags);
    }

    /**
     * この CorresponType を利用可能なユーザー種別を設定する.
     * @param value 利用可能なユーザー種別 (CorresponTypeAdmittee) 列挙子の値を OR した
     *  0 から 255 の整数
     */
    public void setCorresponAccessControlFlags(Integer value) {
        if (value == null) {
            this.corresponAccessControlFlags = null;
        } else {
            this.corresponAccessControlFlags =
                CorresponTypeAdmittee.getEnumSetFromDbValue(value);
        }
    }

    /**
     * 登録か、更新か判定する.
     * @return 登録の場合true / 更新の場合false
     */
    public boolean isNew() {
        return getId() == null || getId() == 0;
    }

}
