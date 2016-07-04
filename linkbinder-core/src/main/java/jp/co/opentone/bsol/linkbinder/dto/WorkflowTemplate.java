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

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * 承認フローテンプレート.
 *
 * @author opentone
 *s
 */
public class WorkflowTemplate extends AbstractDto implements Entity {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -5329524581993268613L;

    /**
     * ID.
     */
    private Long id;

    /**
     * 承認フローテンプレートユーザーID.
     */
    private Long workflowTemplateUserId;

    /**
     * ユーザー.
     */
    private User user;

    /**
     * 検証者種別(Checker/Approver).
     */
    private WorkflowType workflowType;

    /**
     * 承認フローナンバー.
     */
    private Long workflowNo;

    /**
     * 作成者.
     */
    private User createdBy;

    /**
     * 作成日.
     */
    private Date createdAt;

    /**
     * 更新者.
     */
    private User updatedBy;

    /**
     * 更新日.
     */
    private Date updatedAt;

    /**
     * 削除ナンバー.
     */
    private Long deleteNo;

    /**
     * 承認フローテンプレートユーザーIDを返す.
     * @return 承認フローテンプレートユーザーID
     */
    public Long getWorkflowTemplateUserId() {
        return workflowTemplateUserId;
    }

    /**
     * 承認フローテンプレートユーザーIDを設定する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     */
    public void setWorkflowTemplateUserId(Long workflowTemplateUserId) {
        this.workflowTemplateUserId = workflowTemplateUserId;
    }

    /**
     * ユーザーを返す.
     * @return ユーザー
     */
    public User getUser() {
        return user;
    }

    /**
     * ユーザーを設定する.
     * @param user ユーザー
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 検証者種別を返す.
     * @return 検証者種別
     */
    public WorkflowType getWorkflowType() {
        return workflowType;
    }

    /**
     * 検証者種別を設定する.
     * @param workflowType 検証者種別
     */
    public void setWorkflowType(WorkflowType workflowType) {
        this.workflowType = workflowType;
    }

    /**
     * 承認フローナンバーを返す.
     * @return 承認フローナンバー
     */
    public Long getWorkflowNo() {
        return workflowNo;
    }

    /**
     * 承認フローナンバーを設定する.
     * @param workflowNo 承認フローナンバー
     */
    public void setWorkflowNo(Long workflowNo) {
        this.workflowNo = workflowNo;
    }

    /**
     * IDを設定する.
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * IDを返す.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 作成者を返す.
     * @return 作成者
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
     * 作成日を返す.
     * @return 作成日
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * 作成日を設定する.
     * @param createdAt 作成日
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = CloneUtil.cloneDate(createdAt);
    }

    /**
     * 更新者を返す.
     * @return 更新者
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
     * 更新日を返す.
     * @return 更新日
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * 更新日を設定する.
     * @param updatedAt 更新日
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * 削除ナンバーを返す.
     * @return 削除ナンバー
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * 削除ナンバーを設定する.
     * @param deleteNo 削除ナンバー
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }


}
