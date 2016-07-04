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

import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.RSSCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;

/**
 * RSS用コレポンの1レコードを表すDto(Read-Only).
 * <p>
 * $Date: 2011-05-17 15:46:33 +0900 (火, 17  5 2011) $
 * $Rev: 3923 $
 * $Author: nemoto $
 */
public class RSSCorrespon extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6330956750197210379L;

    /**
     * Id.
     */
    private Long id;

    /**
     * Correspon no.
     */
    private String corresponNo;

    /**
     * Project.
     */
    private String projectId;

    /**
     * Project name e.
     */
    private String projectNameE;

    /**
     * From correspon group.
     */
    private CorresponGroup fromCorresponGroup;

    /**
     * Correspon Type.
     */
    private CorresponType corresponType;

    /**
     * Subject.
     */
    private String subject;

    /**
     * Issued by.
     */
    private User issuedBy;

    /**
     * Issued at.
     */
    private Date issuedAt;

    /**
     * Correspon status.
     */
    private CorresponStatus corresponStatus;

    /**
     * Reply required.
     */
    private ReplyRequired replyRequired;

    /**
     * Deadline for reply.
     */
    private Date deadlineForReply;

    /**
     * Workflow status.
     */
    private WorkflowStatus workflowStatus;

    /**
     * Created by.
     */
    private User createdBy;

    /**
     * Created at.
     */
    private Date createdAt;

    /**
     * Updated by.
     */
    private User updatedBy;

    /**
     * Updated at.
     */
    private Date updatedAt;

    /**
     * 宛先（To）代表活動単位.
     */
    private CorresponGroup toCorresponGroup;

    /**
     * 宛先(To)の件数.
     */
    private Long toCorresponGroupCount;

    /**
     * 宛先（Cc）代表活動単位.
     */
    private CorresponGroup ccCorresponGroup;

    /**
     * 宛先(Cc)の件数.
     */
    private Long ccCorresponGroupCount;

    /**
     * Category.
     */
    private RSSCategory category;

    /**
     * 空のインスタンスを生成する.
     */
    public RSSCorrespon() {
    }

    /**
     * id の値を返す.
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * @param id id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * corresponNo の値を返す.
     * @return corresponNo
     */
    public String getCorresponNo() {
        return corresponNo;
    }

    /**
     * corresponNo の値を設定する.
     * @param corresponNo corresponNo
     */
    public void setCorresponNo(String corresponNo) {
        this.corresponNo = corresponNo;
    }

    /**
     * projectId の値を返す.
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * @param projectId projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * projectNameE の値を返す.
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * @param projectNameE projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    /**
     * correspon_typeの値を返す.
     * @return corresponType
     */
    public CorresponType getCorresponType() {
        return corresponType;
    }

    /**
     * correspon_typeの値を設定する.
     * @param corresponType corresponType
     */
    public void setCorresponType(CorresponType corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * subject の値を設定する.
     * @param subject subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * subjectの値を返す.
     * @return subject
     */
    public String getSubject() {
        return subject;
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
     * @return issuedAt
     */
    public Date getIssuedAt() {
        return CloneUtil.cloneDate(issuedAt);
    }

    /**
     * issuedAt の値を設定する.
     * @param issuedAt issuedAt
     */
    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = CloneUtil.cloneDate(issuedAt);
    }

    /**
     * corresponStatus の値を返す.
     * @return corresponStatus
     */
    public CorresponStatus getCorresponStatus() {
        return corresponStatus;
    }

    /**
     * corresponStatus の値を設定する.
     * @param corresponStatus corresponStatus
     */
    public void setCorresponStatus(CorresponStatus corresponStatus) {
        this.corresponStatus = corresponStatus;
    }

    /**
     * deadlineForReply の値を返す.
     * @return deadlineForReply
     */
    public Date getDeadlineForReply() {
        return CloneUtil.cloneDate(deadlineForReply);
    }

    /**
     * deadlineForReply の値を設定する.
     * @param deadlineForReply deadlineForReply
     */
    public void setDeadlineForReply(Date deadlineForReply) {
        this.deadlineForReply = CloneUtil.cloneDate(deadlineForReply);
    }

    /**
     * workflowStatus の値を返す.
     * @return workflowStatus
     */
    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * workflowStatus の値を設定する.
     * @param workflowStatus workflowStatus
     */
    public void setWorkflowStatus(WorkflowStatus workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    /**
     * 作成者を返す.
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 作成者を設定する.
     * @param createdBy 作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * @param createdAt createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 更新者を返す.
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 更新者を設定する.
     * @param updatedBy 更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * @param updatedAt updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
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
     * @return the ccCorresponGroup
     */
    public CorresponGroup getCcCorresponGroup() {
        return ccCorresponGroup;
    }

    /**
     * @param ccCorresponGroup the ccCorresponGroup to set
     */
    public void setCcCorresponGroup(CorresponGroup ccCorresponGroup) {
        this.ccCorresponGroup = ccCorresponGroup;
    }

    /**
     * @return the ccCorresponGroupCount
     */
    public Long getCcCorresponGroupCount() {
        return ccCorresponGroupCount;
    }

    /**
     * @param ccCorresponGroupCount the ccCorresponGroupCount to set
     */
    public void setCcCorresponGroupCount(Long ccCorresponGroupCount) {
        this.ccCorresponGroupCount = ccCorresponGroupCount;
    }

    /**
     * @param category the category to set
     */
    public void setCategory(RSSCategory category) {
        this.category = category;
    }

    /**
     * @return the category
     */
    public RSSCategory getCategory() {
        return category;
    }

    /**
     * 宛先(To)を一覧表示形式の名称に変換して返す.
     * @return 宛先名称
     */
    public String getToGroupName() {
        CorresponGroup g = getToCorresponGroup();
        if (g == null) {
            return null;
        }

        String name = g.getName();
        Long count = getToCorresponGroupCount();
        if (count != null && count > 1) {
            name = name + "...";
        }
        return name;
    }

    /**
     * 宛先(Cc)を一覧表示形式の名称に変換して返す.
     * @return 宛先名称
     */
    public String getCcGroupName() {
        CorresponGroup g = getCcCorresponGroup();
        if (g == null) {
            return null;
        }

        String name = g.getName();
        Long count = getCcCorresponGroupCount();
        if (count != null && count > 1) {
            name = name + "...";
        }
        return name;
    }
}
