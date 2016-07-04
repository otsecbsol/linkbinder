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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import java.util.Date;

import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * テーブル [v_workflow_template_user] の検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchWorkflowTemplateUserCondition extends AbstractCondition {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -4374745545827614575L;

    /**
     * ID.
     */
    private Long id;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * ユーザー.
     */
    private User user;

    /**
     * テンプレート名.
     */
    private String name;

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
     * バージョンナンバー.
     */
    private Long versionNo;

    /**
     * 削除ナンバー.
     */
    private Long deleteNo;

    /**
     * IDを返す.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * IDを設定する.
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * プロジェクトIDを返す.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * プロジェクトIDを設定する.
     * @param projectId プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
     * テンプレート名を返す.
     * @return テンプレート名
     */
    public String getName() {
        return name;
    }

    /**
     * テンプレート名を設定する.
     * @param name テンプレート名
     */
    public void setName(String name) {
        this.name = name;
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
     * バージョンナンバーを返す.
     * @return バージョンナンバー
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * パージョンナンバーを設定する.
     * @param versionNo バージョンナンバー
     */
    public void setVersionNo(Long versionNo) {
        this.versionNo = versionNo;
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
