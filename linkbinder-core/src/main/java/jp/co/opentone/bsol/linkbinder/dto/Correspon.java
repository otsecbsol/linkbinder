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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;

/**
 * テーブル [v_correspon] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class Correspon extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8623788859494120374L;

    /**
     * このクラスのオブジェクトの文字列表現から除外するフィールド名.
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<String>();
        fields.add("body");

        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    /**
     * Id.
     * <p>
     * [v_correspon.id]
     * </p>
     */
    private Long id;

    /**
     * Correspon no.
     * <p>
     * [v_correspon.correspon_no]
     * </p>
     */
    private String corresponNo;

    /**
     * Parent correspon.
     * <p>
     * [v_correspon.parent_correspon_id]
     * </p>
     */
    private Long parentCorresponId;

    /**
     * Parent correspon no.
     * <p>
     * [v_correspon.parent_correspon_no]
     * </p>
     */
    private String parentCorresponNo;

    /**
     * Project.
     * <p>
     * [v_correspon.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Project name e.
     * <p>
     * [v_correspon.project_name_e]
     * </p>
     */
    private String projectNameE;

    /**
     * From correspon group.
     */
    private CorresponGroup fromCorresponGroup;

    /**
     * Previous Revision correspon.
     * <p>
     * [v_correspon.parent_correspon_id]
     * </p>
     */
    private Long previousRevCorresponId;

    /**
     * Previous Revision correspon no.
     * <p>
     * [v_correspon.parent_correspon_no]
     * </p>
     */
    private String previousRevCorresponNo;

    /**
     * Correspon Type.
     */
    private CorresponType corresponType;

    /**
     * Subject.
     * <p>
     * [v_correspon.subject]
     * </p>
     */
    private String subject;

    /**
     * Body.
     * <p>
     * [v_correspon.body]
     * </p>
     */
    private String body;

    /**
     * Issued by.
     * <p>
     * </p>
     */
    private User issuedBy;

    /**
     * Issued at.
     * <p>
     * [v_correspon.issued_at]
     * </p>
     */
    private Date issuedAt;

    /**
     * Correspon status.
     * <p>
     * [v_correspon.correspon_status]
     * </p>
     */
    private CorresponStatus corresponStatus;

    /**
     * Reply required.
     * <p>
     * [v_correspon.reply_required]
     * </p>
     */
    private ReplyRequired replyRequired;

    /**
     * Deadline for reply.
     * <p>
     * [v_correspon.deadline_for_reply]
     * </p>
     */
    private Date deadlineForReply;

    /**
     * Requested approval at.
     * <p>
     * [v_correspon.requested_approval_at]
     * </p>
     */
    private Date requestedApprovalAt;

    /**
     * Workflow status.
     * <p>
     * [v_correspon.workflow_status]
     * </p>
     */
    private WorkflowStatus workflowStatus;

    /**
     * For Learning.
     * <p>
     * [v_correspon.for_learning]
     * </p>
     */
    private String forLearning;

    /**
     * Custom field1.
     * <p>
     * [v_correspon.custom_field1_id]
     * </p>
     */
    private Long customField1Id;

    /**
     * Custom field1 label.
     * <p>
     * [v_correspon.custom_field1_label]
     * </p>
     */
    private String customField1Label;

    /**
     * Custom field1 value.
     * <p>
     * [v_correspon.custom_field1_value]
     * </p>
     */
    private String customField1Value;

    /**
     * Custom field2.
     * <p>
     * [v_correspon.custom_field2_id]
     * </p>
     */
    private Long customField2Id;

    /**
     * Custom field2 label.
     * <p>
     * [v_correspon.custom_field2_label]
     * </p>
     */
    private String customField2Label;

    /**
     * Custom field2 value.
     * <p>
     * [v_correspon.custom_field2_value]
     * </p>
     */
    private String customField2Value;

    /**
     * Custom field3.
     * <p>
     * [v_correspon.custom_field3_id]
     * </p>
     */
    private Long customField3Id;

    /**
     * Custom field3 label.
     * <p>
     * [v_correspon.custom_field3_label]
     * </p>
     */
    private String customField3Label;

    /**
     * Custom field3 value.
     * <p>
     * [v_correspon.custom_field3_value]
     * </p>
     */
    private String customField3Value;

    /**
     * Custom field4.
     * <p>
     * [v_correspon.custom_field4_id]
     * </p>
     */
    private Long customField4Id;

    /**
     * Custom field4 label.
     * <p>
     * [v_correspon.custom_field4_label]
     * </p>
     */
    private String customField4Label;

    /**
     * Custom field4 value.
     * <p>
     * [v_correspon.custom_field4_value]
     * </p>
     */
    private String customField4Value;

    /**
     * Custom field5.
     * <p>
     * [v_correspon.custom_field5_id]
     * </p>
     */
    private Long customField5Id;

    /**
     * Custom field5 label.
     * <p>
     * [v_correspon.custom_field5_label]
     * </p>
     */
    private String customField5Label;

    /**
     * Custom field5 value.
     * <p>
     * [v_correspon.custom_field5_value]
     * </p>
     */
    private String customField5Value;

    /**
     * Custom field6.
     * <p>
     * [v_correspon.custom_field6_id]
     * </p>
     */
    private Long customField6Id;

    /**
     * Custom field6 label.
     * <p>
     * [v_correspon.custom_field6_label]
     * </p>
     */
    private String customField6Label;

    /**
     * Custom field6 value.
     * <p>
     * [v_correspon.custom_field6_value]
     * </p>
     */
    private String customField6Value;

    /**
     * Custom field7.
     * <p>
     * [v_correspon.custom_field7_id]
     * </p>
     */
    private Long customField7Id;

    /**
     * Custom field7 label.
     * <p>
     * [v_correspon.custom_field7_label]
     * </p>
     */
    private String customField7Label;

    /**
     * Custom field7 value.
     * <p>
     * [v_correspon.custom_field7_value]
     * </p>
     */
    private String customField7Value;

    /**
     * Custom field8.
     * <p>
     * [v_correspon.custom_field8_id]
     * </p>
     */
    private Long customField8Id;

    /**
     * Custom field8 label.
     * <p>
     * [v_correspon.custom_field8_label]
     * </p>
     */
    private String customField8Label;

    /**
     * Custom field8 value.
     * <p>
     * [v_correspon.custom_field8_value]
     * </p>
     */
    private String customField8Value;

    /**
     * Custom field9.
     * <p>
     * [v_correspon.custom_field9_id]
     * </p>
     */
    private Long customField9Id;

    /**
     * Custom field9 label.
     * <p>
     * [v_correspon.custom_field9_label]
     * </p>
     */
    private String customField9Label;

    /**
     * Custom field9 value.
     * <p>
     * [v_correspon.custom_field9_value]
     * </p>
     */
    private String customField9Value;

    /**
     * Custom field10.
     * <p>
     * [v_correspon.custom_field10_id]
     * </p>
     */
    private Long customField10Id;

    /**
     * Custom field10 label.
     * <p>
     * [v_correspon.custom_field10_label]
     * </p>
     */
    private String customField10Label;

    /**
     * Custom field10 value.
     * <p>
     * [v_correspon.custom_field10_value]
     * </p>
     */
    private String customField10Value;

    /**
     * File1.
     * <p>
     * [v_correspon.file1_id]
     * </p>
     */
    private Long file1Id;

    /**
     * File1 file.
     * <p>
     * [v_correspon.file1_file_id]
     * </p>
     */
    private String file1FileId;

    /**
     * File1 file name.
     * <p>
     * [v_correspon.file1_file_name]
     * </p>
     */
    private String file1FileName;

    /**
     * File2.
     * <p>
     * [v_correspon.file2_id]
     * </p>
     */
    private Long file2Id;

    /**
     * File2 file.
     * <p>
     * [v_correspon.file2_file_id]
     * </p>
     */
    private String file2FileId;

    /**
     * File2 file name.
     * <p>
     * [v_correspon.file2_file_name]
     * </p>
     */
    private String file2FileName;

    /**
     * File3.
     * <p>
     * [v_correspon.file3_id]
     * </p>
     */
    private Long file3Id;

    /**
     * File3 file.
     * <p>
     * [v_correspon.file3_file_id]
     * </p>
     */
    private String file3FileId;

    /**
     * File3 file name.
     * <p>
     * [v_correspon.file3_file_name]
     * </p>
     */
    private String file3FileName;

    /**
     * File4.
     * <p>
     * [v_correspon.file4_id]
     * </p>
     */
    private Long file4Id;

    /**
     * File4 file.
     * <p>
     * [v_correspon.file4_file_id]
     * </p>
     */
    private String file4FileId;

    /**
     * File4 file name.
     * <p>
     * [v_correspon.file4_file_name]
     * </p>
     */
    private String file4FileName;

    /**
     * File5.
     * <p>
     * [v_correspon.file5_id]
     * </p>
     */
    private Long file5Id;

    /**
     * File5 file.
     * <p>
     * [v_correspon.file5_file_id]
     * </p>
     */
    private String file5FileId;

    /**
     * File5 file name.
     * <p>
     * [v_correspon.file5_file_name]
     * </p>
     */
    private String file5FileName;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_correspon.created_at]
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
     * [v_correspon.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_correspon.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_correspon.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * コレポン文書に設定されたワークフロー. /**
     */
    private List<Workflow> workflows;

    /**
     * 宛先(To/Cc) 活動単位.
     */
    private List<AddressCorresponGroup> addressCorresponGroups;

    /**
     * 既読・未読ステータス情報.
     */
    private CorresponReadStatus corresponReadStatus;

    /**
     * 一覧用：宛先（To）代表活動単位.
     */
    private CorresponGroup toCorresponGroup;

    /**
     * 一覧用：宛先(To)の件数.
     */
    private Long toCorresponGroupCount;

    /**
     * 返信元の宛先-ユーザーID.
     * <p>
     * このコレポン文書が返信文書かつ新規登録されるコレポン文書の場合、
     * 返信を行う宛先-ユーザーIDが設定されている.
     * この値をコレポン文書階層テーブルの返信元宛先-ユーザーIDにセットすること.
     * </p>
     */
    private Long replyAddressUserId;

    /**
     * 登録・更新・削除対象の添付ファイル.
     */
    private List<Attachment> updateAttachments;

    /**
     * 空のインスタンスを生成する.
     */
    public Correspon() {
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dto.AbstractDto#isToStringIgnoreField(java.lang.String)
     */
    @Override
    public boolean isToStringIgnoreField(String fieldName) {
        return super.isToStringIgnoreField(fieldName)
                || TO_STRING_IGNORE_FIELDS.contains(fieldName);
    }

    /**
     * id の値を返す.
     * <p>
     * [v_correspon.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_correspon.id]
     * </p>
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * corresponNo の値を返す.
     * <p>
     * [v_correspon.correspon_no]
     * </p>
     * @return corresponNo
     */
    public String getCorresponNo() {
        return corresponNo;
    }

    /**
     * corresponNo の値を設定する.
     * <p>
     * [v_correspon.correspon_no]
     * </p>
     * @param corresponNo corresponNo
     */
    public void setCorresponNo(String corresponNo) {
        this.corresponNo = corresponNo;
    }

    /**
     * parentCorresponId の値を返す.
     * <p>
     * [v_correspon.parent_correspon_id]
     * </p>
     * @return parentCorresponId
     */
    public Long getParentCorresponId() {
        return parentCorresponId;
    }

    /**
     * parentCorresponId の値を設定する.
     * <p>
     * [v_correspon.parent_correspon_id]
     * </p>
     * @param parentCorresponId parentCorresponId
     */
    public void setParentCorresponId(Long parentCorresponId) {
        this.parentCorresponId = parentCorresponId;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_correspon.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_correspon.project_id]
     * </p>
     * @param projectId projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * projectNameE の値を返す.
     * <p>
     * [v_correspon.project_name_e]
     * </p>
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * <p>
     * [v_correspon.project_name_e]
     * </p>
     * @param projectNameE projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    /**
     * correspon_typeの値を返す.
     * <p>
     * [v_correspon.correspon_type]
     * </p>
     * @return corresponType
     */
    public CorresponType getCorresponType() {
        return corresponType;
    }

    /**
     * correspon_typeの値を設定する.
     * <p>
     * [v_correspon.correspon_type]
     * </p>
     * @param corresponType corresponType
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * subject の値を設定する.
     * <p>
     * [v_correspon.subject]
     * </p>
     * @param subject subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * subjectの値を返す.
     * <p>
     * [v_correspon.subject]
     * </p>
     * @return subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * body の値を返す.
     * <p>
     * [v_correspon.body]
     * </p>
     * @return body
     */
    public String getBody() {
        return body;
    }

    /**
     * body の値を設定する.
     * <p>
     * [v_correspon.body]
     * </p>
     * @param body body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * issuedByを設定します.
     * @param issuedBy the issuedBy to set
     */
    public void setIssuedBy(User issuedBy) {
        this.issuedBy = issuedBy;
    }

    /**
     * issuedByを取得します.
     * @return the issuedBy
     */
    public User getIssuedBy() {
        return issuedBy;
    }

    /**
     * issuedAt の値を返す.
     * <p>
     * [v_correspon.issued_at]
     * </p>
     * @return issuedAt
     */
    public Date getIssuedAt() {
        return CloneUtil.cloneDate(issuedAt);
    }

    /**
     * issuedAt の値を設定する.
     * <p>
     * [v_correspon.issued_at]
     * </p>
     * @param issuedAt issuedAt
     */
    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = CloneUtil.cloneDate(issuedAt);
    }

    /**
     * corresponStatus の値を返す.
     * <p>
     * [v_correspon.correspon_status]
     * </p>
     * @return corresponStatus
     */
    public CorresponStatus getCorresponStatus() {
        return corresponStatus;
    }

    /**
     * corresponStatus の値を設定する.
     * <p>
     * [v_correspon.correspon_status]
     * </p>
     * @param corresponStatus corresponStatus
     */
    public void setCorresponStatus(CorresponStatus corresponStatus) {
        this.corresponStatus = corresponStatus;
    }

    /**
     * deadlineForReply の値を返す.
     * <p>
     * [v_correspon.deadline_for_reply]
     * </p>
     * @return deadlineForReply
     */
    public Date getDeadlineForReply() {
        return CloneUtil.cloneDate(deadlineForReply);
    }

    /**
     * deadlineForReply の値を設定する.
     * <p>
     * [v_correspon.deadline_for_reply]
     * </p>
     * @param deadlineForReply deadlineForReply
     */
    public void setDeadlineForReply(Date deadlineForReply) {
        this.deadlineForReply = CloneUtil.cloneDate(deadlineForReply);
    }

    /**
     * requestedApprovalAt の値を返す.
     * <p>
     * [v_correspon.requested_approval_at]
     * </p>
     * @return requestedApprovalAt
     */
    public Date getRequestedApprovalAt() {
        return CloneUtil.cloneDate(requestedApprovalAt);
    }

    /**
     * requestedApprovalAt の値を設定する.
     * <p>
     * [v_correspon.requested_approval_at]
     * </p>
     * @param requestedApprovalAt requestedApprovalAt
     */
    public void setRequestedApprovalAt(Date requestedApprovalAt) {
        this.requestedApprovalAt = CloneUtil.cloneDate(requestedApprovalAt);
    }


    /**
     * workflowStatus の値を返す.
     * <p>
     * [v_correspon.workflow_status]
     * </p>
     * @return workflowStatus
     */
    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * workflowStatus の値を設定する.
     * <p>
     * [v_correspon.workflow_status]
     * </p>
     * @param workflowStatus workflowStatus
     */
    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    /**
     * forLearning の値を返す.
     * <p>
     * [v_correspon.for_learning]
     * </p>
     * @return forLearning
     */
    public String getForLearning() { return forLearning; }

    /**
     * forLearning の値を設定する.
     * <p>
     * [v_correspon.for_learning]
     * </p>
     *
     */
    public void setForLearning(String forLearning) { this.forLearning = forLearning; }

    /**
     * customField1Id の値を返す.
     * <p>
     * [v_correspon.custom_field1_id]
     * </p>
     * @return customField1Id
     */
    public Long getCustomField1Id() {
        return customField1Id;
    }

    /**
     * customField1Id の値を設定する.
     * <p>
     * [v_correspon.custom_field1_id]
     * </p>
     * @param customField1Id customField1Id
     */
    public void setCustomField1Id(Long customField1Id) {
        this.customField1Id = customField1Id;
    }

    /**
     * customField1Label の値を返す.
     * <p>
     * [v_correspon.custom_field1_label]
     * </p>
     * @return customField1Label
     */
    public String getCustomField1Label() {
        return customField1Label;
    }

    /**
     * customField1Label の値を設定する.
     * <p>
     * [v_correspon.custom_field1_label]
     * </p>
     * @param customField1Label customField1Label
     */
    public void setCustomField1Label(String customField1Label) {
        this.customField1Label = customField1Label;
    }

    /**
     * customField1Value の値を返す.
     * <p>
     * [v_correspon.custom_field1_value]
     * </p>
     * @return customField1Value
     */
    public String getCustomField1Value() {
        return customField1Value;
    }

    /**
     * customField1Value の値を設定する.
     * <p>
     * [v_correspon.custom_field1_value]
     * </p>
     * @param customField1Value customField1Value
     */
    public void setCustomField1Value(String customField1Value) {
        this.customField1Value = customField1Value;
    }

    /**
     * customField2Id の値を返す.
     * <p>
     * [v_correspon.custom_field2_id]
     * </p>
     * @return customField2Id
     */
    public Long getCustomField2Id() {
        return customField2Id;
    }

    /**
     * customField2Id の値を設定する.
     * <p>
     * [v_correspon.custom_field2_id]
     * </p>
     * @param customField2Id customField2Id
     */
    public void setCustomField2Id(Long customField2Id) {
        this.customField2Id = customField2Id;
    }

    /**
     * customField2Label の値を返す.
     * <p>
     * [v_correspon.custom_field2_label]
     * </p>
     * @return customField2Label
     */
    public String getCustomField2Label() {
        return customField2Label;
    }

    /**
     * customField2Label の値を設定する.
     * <p>
     * [v_correspon.custom_field2_label]
     * </p>
     * @param customField2Label customField2Label
     */
    public void setCustomField2Label(String customField2Label) {
        this.customField2Label = customField2Label;
    }

    /**
     * customField2Value の値を返す.
     * <p>
     * [v_correspon.custom_field2_value]
     * </p>
     * @return customField2Value
     */
    public String getCustomField2Value() {
        return customField2Value;
    }

    /**
     * customField2Value の値を設定する.
     * <p>
     * [v_correspon.custom_field2_value]
     * </p>
     * @param customField2Value customField2Value
     */
    public void setCustomField2Value(String customField2Value) {
        this.customField2Value = customField2Value;
    }

    /**
     * customField3Id の値を返す.
     * <p>
     * [v_correspon.custom_field3_id]
     * </p>
     * @return customField3Id
     */
    public Long getCustomField3Id() {
        return customField3Id;
    }

    /**
     * customField3Id の値を設定する.
     * <p>
     * [v_correspon.custom_field3_id]
     * </p>
     * @param customField3Id customField3Id
     */
    public void setCustomField3Id(Long customField3Id) {
        this.customField3Id = customField3Id;
    }

    /**
     * customField3Label の値を返す.
     * <p>
     * [v_correspon.custom_field3_label]
     * </p>
     * @return customField3Label
     */
    public String getCustomField3Label() {
        return customField3Label;
    }

    /**
     * customField3Label の値を設定する.
     * <p>
     * [v_correspon.custom_field3_label]
     * </p>
     * @param customField3Label customField3Label
     */
    public void setCustomField3Label(String customField3Label) {
        this.customField3Label = customField3Label;
    }

    /**
     * customField3Value の値を返す.
     * <p>
     * [v_correspon.custom_field3_value]
     * </p>
     * @return customField3Value
     */
    public String getCustomField3Value() {
        return customField3Value;
    }

    /**
     * customField3Value の値を設定する.
     * <p>
     * [v_correspon.custom_field3_value]
     * </p>
     * @param customField3Value customField3Value
     */
    public void setCustomField3Value(String customField3Value) {
        this.customField3Value = customField3Value;
    }

    /**
     * customField4Id の値を返す.
     * <p>
     * [v_correspon.custom_field4_id]
     * </p>
     * @return customField4Id
     */
    public Long getCustomField4Id() {
        return customField4Id;
    }

    /**
     * customField4Id の値を設定する.
     * <p>
     * [v_correspon.custom_field4_id]
     * </p>
     * @param customField4Id customField4Id
     */
    public void setCustomField4Id(Long customField4Id) {
        this.customField4Id = customField4Id;
    }

    /**
     * customField4Label の値を返す.
     * <p>
     * [v_correspon.custom_field4_label]
     * </p>
     * @return customField4Label
     */
    public String getCustomField4Label() {
        return customField4Label;
    }

    /**
     * customField4Label の値を設定する.
     * <p>
     * [v_correspon.custom_field4_label]
     * </p>
     * @param customField4Label customField4Label
     */
    public void setCustomField4Label(String customField4Label) {
        this.customField4Label = customField4Label;
    }

    /**
     * customField4Value の値を返す.
     * <p>
     * [v_correspon.custom_field4_value]
     * </p>
     * @return customField4Value
     */
    public String getCustomField4Value() {
        return customField4Value;
    }

    /**
     * customField4Value の値を設定する.
     * <p>
     * [v_correspon.custom_field4_value]
     * </p>
     * @param customField4Value customField4Value
     */
    public void setCustomField4Value(String customField4Value) {
        this.customField4Value = customField4Value;
    }

    /**
     * customField5Id の値を返す.
     * <p>
     * [v_correspon.custom_field5_id]
     * </p>
     * @return customField5Id
     */
    public Long getCustomField5Id() {
        return customField5Id;
    }

    /**
     * customField5Id の値を設定する.
     * <p>
     * [v_correspon.custom_field5_id]
     * </p>
     * @param customField5Id customField5Id
     */
    public void setCustomField5Id(Long customField5Id) {
        this.customField5Id = customField5Id;
    }

    /**
     * customField5Label の値を返す.
     * <p>
     * [v_correspon.custom_field5_label]
     * </p>
     * @return customField5Label
     */
    public String getCustomField5Label() {
        return customField5Label;
    }

    /**
     * customField5Label の値を設定する.
     * <p>
     * [v_correspon.custom_field5_label]
     * </p>
     * @param customField5Label customField5Label
     */
    public void setCustomField5Label(String customField5Label) {
        this.customField5Label = customField5Label;
    }

    /**
     * customField5Value の値を返す.
     * <p>
     * [v_correspon.custom_field5_value]
     * </p>
     * @return customField5Value
     */
    public String getCustomField5Value() {
        return customField5Value;
    }

    /**
     * customField5Value の値を設定する.
     * <p>
     * [v_correspon.custom_field5_value]
     * </p>
     * @param customField5Value customField5Value
     */
    public void setCustomField5Value(String customField5Value) {
        this.customField5Value = customField5Value;
    }

    /**
     * customField6Id の値を返す.
     * <p>
     * [v_correspon.custom_field6_id]
     * </p>
     * @return customField6Id
     */
    public Long getCustomField6Id() {
        return customField6Id;
    }

    /**
     * customField6Id の値を設定する.
     * <p>
     * [v_correspon.custom_field6_id]
     * </p>
     * @param customField6Id customField6Id
     */
    public void setCustomField6Id(Long customField6Id) {
        this.customField6Id = customField6Id;
    }

    /**
     * customField6Label の値を返す.
     * <p>
     * [v_correspon.custom_field6_label]
     * </p>
     * @return customField6Label
     */
    public String getCustomField6Label() {
        return customField6Label;
    }

    /**
     * customField6Label の値を設定する.
     * <p>
     * [v_correspon.custom_field6_label]
     * </p>
     * @param customField6Label customField6Label
     */
    public void setCustomField6Label(String customField6Label) {
        this.customField6Label = customField6Label;
    }

    /**
     * customField6Value の値を返す.
     * <p>
     * [v_correspon.custom_field6_value]
     * </p>
     * @return customField6Value
     */
    public String getCustomField6Value() {
        return customField6Value;
    }

    /**
     * customField6Value の値を設定する.
     * <p>
     * [v_correspon.custom_field6_value]
     * </p>
     * @param customField6Value customField6Value
     */
    public void setCustomField6Value(String customField6Value) {
        this.customField6Value = customField6Value;
    }

    /**
     * customField7Id の値を返す.
     * <p>
     * [v_correspon.custom_field7_id]
     * </p>
     * @return customField7Id
     */
    public Long getCustomField7Id() {
        return customField7Id;
    }

    /**
     * customField7Id の値を設定する.
     * <p>
     * [v_correspon.custom_field7_id]
     * </p>
     * @param customField7Id customField7Id
     */
    public void setCustomField7Id(Long customField7Id) {
        this.customField7Id = customField7Id;
    }

    /**
     * customField7Label の値を返す.
     * <p>
     * [v_correspon.custom_field7_label]
     * </p>
     * @return customField7Label
     */
    public String getCustomField7Label() {
        return customField7Label;
    }

    /**
     * customField7Label の値を設定する.
     * <p>
     * [v_correspon.custom_field7_label]
     * </p>
     * @param customField7Label customField7Label
     */
    public void setCustomField7Label(String customField7Label) {
        this.customField7Label = customField7Label;
    }

    /**
     * customField7Value の値を返す.
     * <p>
     * [v_correspon.custom_field7_value]
     * </p>
     * @return customField7Value
     */
    public String getCustomField7Value() {
        return customField7Value;
    }

    /**
     * customField7Value の値を設定する.
     * <p>
     * [v_correspon.custom_field7_value]
     * </p>
     * @param customField7Value customField7Value
     */
    public void setCustomField7Value(String customField7Value) {
        this.customField7Value = customField7Value;
    }

    /**
     * customField8Id の値を返す.
     * <p>
     * [v_correspon.custom_field8_id]
     * </p>
     * @return customField8Id
     */
    public Long getCustomField8Id() {
        return customField8Id;
    }

    /**
     * customField8Id の値を設定する.
     * <p>
     * [v_correspon.custom_field8_id]
     * </p>
     * @param customField8Id customField8Id
     */
    public void setCustomField8Id(Long customField8Id) {
        this.customField8Id = customField8Id;
    }

    /**
     * customField8Label の値を返す.
     * <p>
     * [v_correspon.custom_field8_label]
     * </p>
     * @return customField8Label
     */
    public String getCustomField8Label() {
        return customField8Label;
    }

    /**
     * customField8Label の値を設定する.
     * <p>
     * [v_correspon.custom_field8_label]
     * </p>
     * @param customField8Label customField8Label
     */
    public void setCustomField8Label(String customField8Label) {
        this.customField8Label = customField8Label;
    }

    /**
     * customField8Value の値を返す.
     * <p>
     * [v_correspon.custom_field8_value]
     * </p>
     * @return customField8Value
     */
    public String getCustomField8Value() {
        return customField8Value;
    }

    /**
     * customField8Value の値を設定する.
     * <p>
     * [v_correspon.custom_field8_value]
     * </p>
     * @param customField8Value customField8Value
     */
    public void setCustomField8Value(String customField8Value) {
        this.customField8Value = customField8Value;
    }

    /**
     * customField9Id の値を返す.
     * <p>
     * [v_correspon.custom_field9_id]
     * </p>
     * @return customField9Id
     */
    public Long getCustomField9Id() {
        return customField9Id;
    }

    /**
     * customField9Id の値を設定する.
     * <p>
     * [v_correspon.custom_field9_id]
     * </p>
     * @param customField9Id customField9Id
     */
    public void setCustomField9Id(Long customField9Id) {
        this.customField9Id = customField9Id;
    }

    /**
     * customField9Label の値を返す.
     * <p>
     * [v_correspon.custom_field9_label]
     * </p>
     * @return customField9Label
     */
    public String getCustomField9Label() {
        return customField9Label;
    }

    /**
     * customField9Label の値を設定する.
     * <p>
     * [v_correspon.custom_field9_label]
     * </p>
     * @param customField9Label customField9Label
     */
    public void setCustomField9Label(String customField9Label) {
        this.customField9Label = customField9Label;
    }

    /**
     * customField9Value の値を返す.
     * <p>
     * [v_correspon.custom_field9_value]
     * </p>
     * @return customField9Value
     */
    public String getCustomField9Value() {
        return customField9Value;
    }

    /**
     * customField9Value の値を設定する.
     * <p>
     * [v_correspon.custom_field9_value]
     * </p>
     * @param customField9Value customField9Value
     */
    public void setCustomField9Value(String customField9Value) {
        this.customField9Value = customField9Value;
    }

    /**
     * customField10Id の値を返す.
     * <p>
     * [v_correspon.custom_field10_id]
     * </p>
     * @return customField10Id
     */
    public Long getCustomField10Id() {
        return customField10Id;
    }

    /**
     * customField10Id の値を設定する.
     * <p>
     * [v_correspon.custom_field10_id]
     * </p>
     * @param customField10Id customField10Id
     */
    public void setCustomField10Id(Long customField10Id) {
        this.customField10Id = customField10Id;
    }

    /**
     * customField10Label の値を返す.
     * <p>
     * [v_correspon.custom_field10_label]
     * </p>
     * @return customField10Label
     */
    public String getCustomField10Label() {
        return customField10Label;
    }

    /**
     * customField10Label の値を設定する.
     * <p>
     * [v_correspon.custom_field10_label]
     * </p>
     * @param customField10Label customField10Label
     */
    public void setCustomField10Label(String customField10Label) {
        this.customField10Label = customField10Label;
    }

    /**
     * customField10Value の値を返す.
     * <p>
     * [v_correspon.custom_field10_value]
     * </p>
     * @return customField10Value
     */
    public String getCustomField10Value() {
        return customField10Value;
    }

    /**
     * customField10Value の値を設定する.
     * <p>
     * [v_correspon.custom_field10_value]
     * </p>
     * @param customField10Value customField10Value
     */
    public void setCustomField10Value(String customField10Value) {
        this.customField10Value = customField10Value;
    }

    /**
     * file1Id の値を返す.
     * <p>
     * [v_correspon.file1_id]
     * </p>
     * @return file1Id
     */
    public Long getFile1Id() {
        return file1Id;
    }

    /**
     * file1Id の値を設定する.
     * <p>
     * [v_correspon.file1_id]
     * </p>
     * @param file1Id file1Id
     */
    public void setFile1Id(Long file1Id) {
        this.file1Id = file1Id;
    }

    /**
     * file1FileId の値を返す.
     * <p>
     * [v_correspon.file1_file_id]
     * </p>
     * @return file1FileId
     */
    public String getFile1FileId() {
        return file1FileId;
    }

    /**
     * file1FileId の値を設定する.
     * <p>
     * [v_correspon.file1_file_id]
     * </p>
     * @param file1FileId file1FileId
     */
    public void setFile1FileId(String file1FileId) {
        this.file1FileId = file1FileId;
    }

    /**
     * file1FileName の値を返す.
     * <p>
     * [v_correspon.file1_file_name]
     * </p>
     * @return file1FileName
     */
    public String getFile1FileName() {
        return file1FileName;
    }

    /**
     * file1FileName の値を設定する.
     * <p>
     * [v_correspon.file1_file_name]
     * </p>
     * @param file1FileName file1FileName
     */
    public void setFile1FileName(String file1FileName) {
        this.file1FileName = file1FileName;
    }

    /**
     * file2Id の値を返す.
     * <p>
     * [v_correspon.file2_id]
     * </p>
     * @return file2Id
     */
    public Long getFile2Id() {
        return file2Id;
    }

    /**
     * file2Id の値を設定する.
     * <p>
     * [v_correspon.file2_id]
     * </p>
     * @param file2Id file2Id
     */
    public void setFile2Id(Long file2Id) {
        this.file2Id = file2Id;
    }

    /**
     * file2FileId の値を返す.
     * <p>
     * [v_correspon.file2_file_id]
     * </p>
     * @return file2FileId
     */
    public String getFile2FileId() {
        return file2FileId;
    }

    /**
     * file2FileId の値を設定する.
     * <p>
     * [v_correspon.file2_file_id]
     * </p>
     * @param file2FileId file2FileId
     */
    public void setFile2FileId(String file2FileId) {
        this.file2FileId = file2FileId;
    }

    /**
     * file2FileName の値を返す.
     * <p>
     * [v_correspon.file2_file_name]
     * </p>
     * @return file2FileName
     */
    public String getFile2FileName() {
        return file2FileName;
    }

    /**
     * file2FileName の値を設定する.
     * <p>
     * [v_correspon.file2_file_name]
     * </p>
     * @param file2FileName file2FileName
     */
    public void setFile2FileName(String file2FileName) {
        this.file2FileName = file2FileName;
    }

    /**
     * file3Id の値を返す.
     * <p>
     * [v_correspon.file3_id]
     * </p>
     * @return file3Id
     */
    public Long getFile3Id() {
        return file3Id;
    }

    /**
     * file3Id の値を設定する.
     * <p>
     * [v_correspon.file3_id]
     * </p>
     * @param file3Id file3Id
     */
    public void setFile3Id(Long file3Id) {
        this.file3Id = file3Id;
    }

    /**
     * file3FileId の値を返す.
     * <p>
     * [v_correspon.file3_file_id]
     * </p>
     * @return file3FileId
     */
    public String getFile3FileId() {
        return file3FileId;
    }

    /**
     * file3FileId の値を設定する.
     * <p>
     * [v_correspon.file3_file_id]
     * </p>
     * @param file3FileId file3FileId
     */
    public void setFile3FileId(String file3FileId) {
        this.file3FileId = file3FileId;
    }

    /**
     * file3FileName の値を返す.
     * <p>
     * [v_correspon.file3_file_name]
     * </p>
     * @return file3FileName
     */
    public String getFile3FileName() {
        return file3FileName;
    }

    /**
     * file3FileName の値を設定する.
     * <p>
     * [v_correspon.file3_file_name]
     * </p>
     * @param file3FileName file3FileName
     */
    public void setFile3FileName(String file3FileName) {
        this.file3FileName = file3FileName;
    }

    /**
     * file4Id の値を返す.
     * <p>
     * [v_correspon.file4_id]
     * </p>
     * @return file4Id
     */
    public Long getFile4Id() {
        return file4Id;
    }

    /**
     * file4Id の値を設定する.
     * <p>
     * [v_correspon.file4_id]
     * </p>
     * @param file4Id file4Id
     */
    public void setFile4Id(Long file4Id) {
        this.file4Id = file4Id;
    }

    /**
     * file4FileId の値を返す.
     * <p>
     * [v_correspon.file4_file_id]
     * </p>
     * @return file4FileId
     */
    public String getFile4FileId() {
        return file4FileId;
    }

    /**
     * file4FileId の値を設定する.
     * <p>
     * [v_correspon.file4_file_id]
     * </p>
     * @param file4FileId file4FileId
     */
    public void setFile4FileId(String file4FileId) {
        this.file4FileId = file4FileId;
    }

    /**
     * file4FileName の値を返す.
     * <p>
     * [v_correspon.file4_file_name]
     * </p>
     * @return file4FileName
     */
    public String getFile4FileName() {
        return file4FileName;
    }

    /**
     * file4FileName の値を設定する.
     * <p>
     * [v_correspon.file4_file_name]
     * </p>
     * @param file4FileName file4FileName
     */
    public void setFile4FileName(String file4FileName) {
        this.file4FileName = file4FileName;
    }

    /**
     * file5Id の値を返す.
     * <p>
     * [v_correspon.file5_id]
     * </p>
     * @return file5Id
     */
    public Long getFile5Id() {
        return file5Id;
    }

    /**
     * file5Id の値を設定する.
     * <p>
     * [v_correspon.file5_id]
     * </p>
     * @param file5Id file5Id
     */
    public void setFile5Id(Long file5Id) {
        this.file5Id = file5Id;
    }

    /**
     * file5FileId の値を返す.
     * <p>
     * [v_correspon.file5_file_id]
     * </p>
     * @return file5FileId
     */
    public String getFile5FileId() {
        return file5FileId;
    }

    /**
     * file5FileId の値を設定する.
     * <p>
     * [v_correspon.file5_file_id]
     * </p>
     * @param file5FileId file5FileId
     */
    public void setFile5FileId(String file5FileId) {
        this.file5FileId = file5FileId;
    }

    /**
     * file5FileName の値を返す.
     * <p>
     * [v_correspon.file5_file_name]
     * </p>
     * @return file5FileName
     */
    public String getFile5FileName() {
        return file5FileName;
    }

    /**
     * file5FileName の値を設定する.
     * <p>
     * [v_correspon.file5_file_name]
     * </p>
     * @param file5FileName file5FileName
     */
    public void setFile5FileName(String file5FileName) {
        this.file5FileName = file5FileName;
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
     * @param createdBy 作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * <p>
     * [v_correspon.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_correspon.created_at]
     * </p>
     * @param createdAt createdAt
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
     * @param updatedBy 更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * <p>
     * [v_correspon.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_correspon.updated_at]
     * </p>
     * @param updatedAt updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * versionNo の値を返す.
     * <p>
     * [v_correspon.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_correspon.version_no]
     * </p>
     * @param versionNo versionNo
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [v_correspon.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_correspon.delete_no]
     * </p>
     * @param deleteNo deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * workflows の値を設定する.
     * @param workflows the workflows to set
     */
    public void setWorkflows(List<Workflow> workflows) {
        this.workflows = workflows;
    }

    /**
     * workflows の値を返す.
     * @return the workflows
     */
    public List<Workflow> getWorkflows() {
        return workflows;
    }

    /**
     * addressCorresponGroupsの値を設定する.
     * @param addressCorresponGroups the addressCorresponGroups to set
     */
    public void setAddressCorresponGroups(List<AddressCorresponGroup> addressCorresponGroups) {
        this.addressCorresponGroups = addressCorresponGroups;
    }

    /**
     * addressCorresponGroupsの値を返す.
     * @return the addressCorresponGroups
     */
    public List<AddressCorresponGroup> getAddressCorresponGroups() {
        return addressCorresponGroups;
    }

    /**
     * fromCorresponGroupの値を設定する.
     * @param fromCorresponGroup the fromCorresponGroup to set
     */
    public void setFromCorresponGroup(CorresponGroup fromCorresponGroup) {
        this.fromCorresponGroup = fromCorresponGroup;
    }

    /**
     * fromCorresponGroupの値を返す.
     * @return the fromCorresponGroup
     */
    public CorresponGroup getFromCorresponGroup() {
        return fromCorresponGroup;
    }

    /**
     * replyRequiredの値を設定する.
     * @param replyRequired the replyRequired to set
     */
    public void setReplyRequired(ReplyRequired replyRequired) {
        this.replyRequired = replyRequired;
    }

    /**
     * replyRequiredの値を返す.
     * @return the replyRequired
     */
    public ReplyRequired getReplyRequired() {
        return replyRequired;
    }

    /**
     * previousRevCorresponIdの値を設定する.
     * @param previousRevCorresponId the previousRevCorresponId to set
     */
    public void setPreviousRevCorresponId(Long previousRevCorresponId) {
        this.previousRevCorresponId = previousRevCorresponId;
    }

    /**
     * previousRevCorresponIdの値を返す.
     * @return the previousRevCorresponId
     */
    public Long getPreviousRevCorresponId() {
        return previousRevCorresponId;
    }

    /**
     * corresponReadStatusを設定します.
     * @param corresponReadStatus the corresponReadStatus to set
     */
    public void setCorresponReadStatus(CorresponReadStatus corresponReadStatus) {
        this.corresponReadStatus = corresponReadStatus;
    }

    /**
     * corresponReadStatusを取得します.
     * @return the corresponReadStatus
     */
    public CorresponReadStatus getCorresponReadStatus() {
        return corresponReadStatus;
    }

    /**
     * parentCorresponNoの値を設定する.
     * @param parentCorresponNo the parentCorresponNo to set
     */
    public void setParentCorresponNo(String parentCorresponNo) {
        this.parentCorresponNo = parentCorresponNo;
    }

    /**
     * parentCorresponNoの値を返す.
     * @return the parentCorresponNo
     */
    public String getParentCorresponNo() {
        return parentCorresponNo;
    }

    /**
     * previousRevCorresponNoの値を設定する.
     * @param previousRevCorresponNo the previousRevCorresponNo to set
     */
    public void setPreviousRevCorresponNo(String previousRevCorresponNo) {
        this.previousRevCorresponNo = previousRevCorresponNo;
    }

    /**
     * previousRevCorresponNoの値を返す.
     * @return the previousRevCorresponNo
     */
    public String getPreviousRevCorresponNo() {
        return previousRevCorresponNo;
    }

    /**
     * toCorresponGroupを設定します.
     * @param toCorresponGroup the toCorresponGroup to set
     */
    public void setToCorresponGroup(CorresponGroup toCorresponGroup) {
        this.toCorresponGroup = toCorresponGroup;
    }

    /**
     * toCorresponGroupを取得します.
     * @return the toCorresponGroup
     */
    public CorresponGroup getToCorresponGroup() {
        return toCorresponGroup;
    }

    /**
     * @param replyAddressUserId the replyAddressUserId to set
     */
    public void setReplyAddressUserId(Long replyAddressUserId) {
        this.replyAddressUserId = replyAddressUserId;
    }

    /**
     * @return the replyAddressUserId
     */
    public Long getReplyAddressUserId() {
        return replyAddressUserId;
    }

    /**
     * このオブジェクトが保持する宛先のうち、Toだけを抽出して返す.
     * 編集モードが削除(DELETE)であるものは除外する.
     * @see UpdateMode
     * @return 宛先(To)
     */
    public List<AddressCorresponGroup> getToAddressCorresponGroups() {
        List<AddressCorresponGroup> result = new ArrayList<AddressCorresponGroup>();
        List<AddressCorresponGroup> addresses = getAddressCorresponGroups();
        if (addresses == null) {
            return result;
        }

        for (AddressCorresponGroup ag : addresses) {
            if (ag.getAddressType() == AddressType.TO
                && ag.getMode() != UpdateMode.DELETE) {
                result.add(ag);
            }
        }
        return result;
    }

    /**
     * このオブジェクトが保持する宛先のうち、Ccだけを抽出して返す.
     * 編集モードが削除(DELETE)であるものは除外する.
     * @see UpdateMode
     * @return 宛先(Cc)
     */
    public List<AddressCorresponGroup> getCcAddressCorresponGroups() {
        List<AddressCorresponGroup> result = new ArrayList<AddressCorresponGroup>();
        List<AddressCorresponGroup> addresses = getAddressCorresponGroups();
        if (addresses == null) {
            return result;
        }

        for (AddressCorresponGroup ag : addresses) {
            if (ag.getAddressType() == AddressType.CC
                && ag.getMode() != UpdateMode.DELETE) {
                result.add(ag);
            }
        }
        return result;
    }

    /**
     * @param updateAttachments the updateAttachments to set
     */
    public void setUpdateAttachments(List<Attachment> updateAttachments) {
        this.updateAttachments = updateAttachments;
    }

    /**
     * @return the updateAttachments
     */
    public List<Attachment> getUpdateAttachments() {
        return updateAttachments;
    }

    /**
     * このオブジェクトが保持する添付ファイル情報を、Attachmentオブジェクトのリストに変換して返す.
     * @return 添付ファイル情報のリスト
     */
    public List<Attachment> getAttachments() {
        List<Attachment> result = new ArrayList<Attachment>();
        if (getFile1Id() != null) {
            addAttachmentTo(result, getFile1Id(), getFile1FileId(), getFile1FileName());
        }
        if (getFile2Id() != null) {
            addAttachmentTo(result, getFile2Id(), getFile2FileId(), getFile2FileName());
        }
        if (getFile3Id() != null) {
            addAttachmentTo(result, getFile3Id(), getFile3FileId(), getFile3FileName());
        }
        if (getFile4Id() != null) {
            addAttachmentTo(result, getFile4Id(), getFile4FileId(), getFile4FileName());
        }
        if (getFile5Id() != null) {
            addAttachmentTo(result, getFile5Id(), getFile5FileId(), getFile5FileName());
        }
        return result;
    }

    private void addAttachmentTo(List<Attachment> attachments,
                                //CHECKSTYLE:OFF フィールドと同名の警告が出るが、この名前が最も適切
                                Long id,
                                //CHECKSTYLE:ON
                                String fileId,
                                String fileName) {
        Attachment a = new Attachment();
        a.setId(id);
        a.setCorresponId(getId());
        a.setFileId(fileId);
        a.setFileName(fileName);

        attachments.add(a);
    }

    /**
     * @param toCorresponGroupCount the toCorresponGroupCount to set
     */
    public void setToCorresponGroupCount(Long toCorresponGroupCount) {
        this.toCorresponGroupCount = toCorresponGroupCount;
    }

    /**
     * @return the toCorresponGroupCount
     */
    public Long getToCorresponGroupCount() {
        return toCorresponGroupCount;
    }

    /**
     * 宛先を、一覧表示形式の名称に変換して返す.
     * @return 宛先名称
     */
    public String getToGroupName() {
        CorresponGroup g = getToCorresponGroup();
        if (g == null) {
            return null;
        }

        String name = g.getName();
        Long count = getToCorresponGroupCount();
        if (count != null && getToCorresponGroupCount() > 1) {
            name = name + "...";
        }
        return name;
    }

    /**
     * 新規文書の場合はtrueを返す.
     * @return 新規文書の場合はtrue
     */
    public boolean isNew() {
        return (getId() == null || getId() == 0);
    }

    /**
     *発行済みの判定.
     * @return boolean true:発行済み
     */
    public boolean isIssued() {
        return (WorkflowStatus.ISSUED == getWorkflowStatus());
    }
}
