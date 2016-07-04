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
package jp.co.opentone.bsol.linkbinder.dto.notice;

import java.util.Date;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.linkbinder.dto.AbstractDto;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;

/**
 * テーブル [v_email_notice_recv_setting] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class EmailNoticeRecvSetting extends AbstractDto implements Entity {

    /**
     *
     */
    private static final long serialVersionUID = -8331293546686309983L;

    /**
     * Id.
     * <p>
     * [v_email_notice_recv_setting.id]
     * </p>
     */
    private Long id;

    /**
     * projectId.
     * <p>
     * [v_email_notice_recv_setting.projectId]
     * </p>
     */
    private String projectId;

    /**
     * nameE.
     * <p>
     * [v_email_notice_recv_setting.nameE]
     * </p>
     */
    private String nameE;

    /**
     * empNo.
     * <p>
     * [v_email_notice_recv_setting.empNo]
     * </p>
     */
    private String empNo;

    /**
     * receiveWorkflow.
     * <p>
     * [v_email_notice_recv_setting.receiveWorkflow]
     * </p>
     */
    private EmailNoticeReceivable receiveWorkflow;

    /**
     * receiveWorkflow.
     * <p>
     * [v_email_notice_recv_setting.receiveWorkflow]
     * </p>
     */
    private boolean receiveWorkflowChk;

    /**
     * recvDistributionAttention.
     * <p>
     * [v_email_notice_recv_setting.recvDistributionAttention]
     * </p>
     */
    private EmailNoticeReceivable recvDistributionAttention;

    /**
     * recvDistributionAttention.
     * <p>
     * [v_email_notice_recv_setting.recvDistributionAttention]
     * </p>
     */
    private boolean recvDistributionAttentionChk;

    /**
     * recvDistributionCc.
     * <p>
     * [v_email_notice_recv_setting.recvDistributionCc]
     * </p>
     */
    private EmailNoticeReceivable recvDistributionCc;

    /**
     * recvDistributionCc.
     * <p>
     * [v_email_notice_recv_setting.recvDistributionCc]
     * </p>
     */
    private boolean recvDistributionCcChk;

    /**
     * createdBy.
     * <p>
     * [v_email_notice_recv_setting.createdBy]
     * </p>
     */
    private User createdBy;

    /**
     * createdAt.
     * <p>
     * [v_email_notice_recv_setting.createdAt]
     * </p>
     */
    private Date createdAt;

    /**
     * updateBy.
     * <p>
     * [v_email_notice_recv_setting.updateBy]
     * </p>
     */
    private User updatedBy;

    /**
     * updateAt.
     * <p>
     * [v_email_notice_recv_setting.updateAt]
     * </p>
     */
    private Date updatedAt;

    /**
     * deleteNo.
     * <p>
     * [v_email_notice_recv_setting.deleteNo]
     * </p>
     */
    private Long deleteNo;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the nameE
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * @param nameE the nameE to set
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * @return the empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * @param empNo the empNo to set
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * @return the receiveWorkflow
     */
    public EmailNoticeReceivable getReceiveWorkflow() {
        return receiveWorkflow;
    }

    /**
     * @param receiveWorkflow the receiveWorkflow to set
     */
    public void setReceiveWorkflow(EmailNoticeReceivable receiveWorkflow) {
        this.receiveWorkflow = receiveWorkflow;
    }

    /**
     * @return the receiveWorkflowChk
     */
    public boolean isReceiveWorkflowChk() {
        return receiveWorkflowChk;
    }

    /**
     * @param receiveWorkflowChk the receiveWorkflowChk to set
     */
    public void setReceiveWorkflowChk(boolean receiveWorkflowChk) {
        this.receiveWorkflowChk = receiveWorkflowChk;
    }

    /**
     * @return the recvDistributionAttention
     */
    public EmailNoticeReceivable getRecvDistributionAttention() {
        return recvDistributionAttention;
    }

    /**
     * @param recvDistributionAttention the recvDistributionAttention to set
     */
    public void setRecvDistributionAttention(EmailNoticeReceivable recvDistributionAttention) {
        this.recvDistributionAttention = recvDistributionAttention;
    }

    /**
     * @return the recvDistributionAttentionChk
     */
    public boolean isRecvDistributionAttentionChk() {
        return recvDistributionAttentionChk;
    }

    /**
     * @param recvDistributionAttentionChk the recvDistributionAttentionChk to set
     */
    public void setRecvDistributionAttentionChk(boolean recvDistributionAttentionChk) {
        this.recvDistributionAttentionChk = recvDistributionAttentionChk;
    }

    /**
     * @return the recvDistributionCc
     */
    public EmailNoticeReceivable getRecvDistributionCc() {
        return recvDistributionCc;
    }

    /**
     * @param recvDistributionCc the recvDistributionCc to set
     */
    public void setRecvDistributionCc(EmailNoticeReceivable recvDistributionCc) {
        this.recvDistributionCc = recvDistributionCc;
    }

    /**
     * @return the recvDistributionCcChk
     */
    public boolean isRecvDistributionCcChk() {
        return recvDistributionCcChk;
    }

    /**
     * @param recvDistributionCcChk the recvDistributionCcChk to set
     */
    public void setRecvDistributionCcChk(boolean recvDistributionCcChk) {
        this.recvDistributionCcChk = recvDistributionCcChk;
    }

    /**
     * @return the createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt the createdAt to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return the updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * @param updatedAt the updatedAt to set
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * @return the deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * @param deleteNo the deleteNo to set
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }
}
