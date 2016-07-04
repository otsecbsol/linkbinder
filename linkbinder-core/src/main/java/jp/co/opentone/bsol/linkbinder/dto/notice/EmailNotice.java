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
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeAddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeStatus;

/**
 * メール通知情報.
 *
 * @author opentone
 *
 */
public class EmailNotice extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 755056344848513054L;

    /** ID. */
    private Long id;
    /** プロジェクトID. */
    private String projectId;
    /** メール通知分類. */
    private EmailNoticeCategory emailNoticeCategory;
    /** メール通知イベントコード. */
    private EmailNoticeEventCd emailNoticeEventCd;
    /** メール通知宛先種別. */
    private EmailNoticeAddressType emailNoticeAddressType;
    /** メール通知状態. */
    private EmailNoticeStatus emailNoticeStatus;
    /** メール通知日時. */
    private Date notifiedAt;
    /** 通知ヘッダーSubject. */
    private String mhSubject;
    /** 通知ヘッダーTo. */
    private String mhTo;
    /** 通知ヘッダーFrom. */
    private String mhFrom;
    /** 通知ヘッダーFrom. */
    private String mhErrorsTo;
    /** コレポン文書ID. */
    private Long corresponId;
    /** 通知本文. */
    private String mailBody;
    /** 作成者. */
    private User createdBy;
    /** 作成日時. */
    private Date createdAt;
    /** 更新者. */
    private User updatedBy;
    /** 更新日時. */
    private Date updatedAt;
    /** 削除番号. */
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
     * @return the emailNoticeCategory
     */
    public EmailNoticeCategory getEmailNoticeCategory() {
        return emailNoticeCategory;
    }

    /**
     * @param emailNoticeCategory the emailNoticeCategory to set
     */
    public void setEmailNoticeCategory(EmailNoticeCategory emailNoticeCategory) {
        this.emailNoticeCategory = emailNoticeCategory;
    }

    /**
     * @return the emailNoticeEventCd
     */
    public EmailNoticeEventCd getEmailNoticeEventCd() {
        return emailNoticeEventCd;
    }

    /**
     * @param emailNoticeEventCd the emailNoticeEventCd to set
     */
    public void setEmailNoticeEventCd(EmailNoticeEventCd emailNoticeEventCd) {
        this.emailNoticeEventCd = emailNoticeEventCd;
    }

    /**
     * @return the emailNoticeAddressType
     */
    public EmailNoticeAddressType getEmailNoticeAddressType() {
        return emailNoticeAddressType;
    }

    /**
     * @param emailNoticeAddressType the emailNoticeAddressType to set
     */
    public void setEmailNoticeAddressType(EmailNoticeAddressType emailNoticeAddressType) {
        this.emailNoticeAddressType = emailNoticeAddressType;
    }

    /**
     * @return the emailNoticeStatus
     */
    public EmailNoticeStatus getEmailNoticeStatus() {
        return emailNoticeStatus;
    }

    /**
     * @param emailNoticeStatus the emailNoticeStatus to set
     */
    public void setEmailNoticeStatus(EmailNoticeStatus emailNoticeStatus) {
        this.emailNoticeStatus = emailNoticeStatus;
    }

    /**
     * @return the notifiedAt
     */
    public Date getNotifiedAt() {
        return notifiedAt;
    }

    /**
     * @param notifiedAt the notifiedAt to set
     */
    public void setNotifiedAt(Date notifiedAt) {
        this.notifiedAt = notifiedAt;
    }

    /**
     * @return the mhSubject
     */
    public String getMhSubject() {
        return mhSubject;
    }

    /**
     * @param mhSubject the mhSubject to set
     */
    public void setMhSubject(String mhSubject) {
        this.mhSubject = mhSubject;
    }

    /**
     * @return the mhTo
     */
    public String getMhTo() {
        return mhTo;
    }

    /**
     * @param mhTo the mhTo to set
     */
    public void setMhTo(String mhTo) {
        this.mhTo = mhTo;
    }

    /**
     * @return the mhFrom
     */
    public String getMhFrom() {
        return mhFrom;
    }

    /**
     * @param mhFrom the mhFrom to set
     */
    public void setMhFrom(String mhFrom) {
        this.mhFrom = mhFrom;
    }

    /**
     * @return the mhErrorsTo
     */
    public String getMhErrorsTo() {
        return mhErrorsTo;
    }

    /**
     * @param mhErrorsTo the mhErrorsTo to set
     */
    public void setMhErrorsTo(String mhErrorsTo) {
        this.mhErrorsTo = mhErrorsTo;
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

    /**
     * @return the mailBody
     */
    public String getMailBody() {
        return mailBody;
    }

    /**
     * @param mailBody the mailBody to set
     */
    public void setMailBody(String mailBody) {
        this.mailBody = mailBody;
    }

    /**
     * @return the corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * @param corresponId the corresponId to set
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }
}
