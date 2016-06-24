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
 * 部門情報の検索条件を保持する. テーブル [v_discipline]
 *
 * @author opentone
 */
public class SearchSiteCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5237649692872697615L;

    /**
     * Id.
     * <p>
     * [v_site.id]
     * </p>
     */
    private Long id;

    /**
     * Project.
     * <p>
     * [v_site.project_id]
     * </p>
     */
    private String projectId;

    /**
     * Project name e.
     * <p>
     * [v_site.project_name_e]
     * </p>
     */
    private String projectNameE;

    /**
     * Correspon type.
     * <p>
     * [v_site.site_cd]
     * </p>
     */
    private String siteCd;

    /**
     * Name.
     * <p>
     * [v_site.name]
     * </p>
     */
    private String name;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_site.created_at]
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
     * [v_site.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [v_site.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [v_site.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * GroupAdminフラグ.
     */
    private boolean groupAdmin;

    /**
     * 検索するユーザー.
     */
    private User searchUser;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchSiteCondition() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_site.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_site.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * projectId の値を返す.
     * <p>
     * [v_site.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [v_site.project_id]
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
     * [v_site.project_name_e]
     * </p>
     * @return projectNameE
     */
    public String getProjectNameE() {
        return projectNameE;
    }

    /**
     * projectNameE の値を設定する.
     * <p>
     * [v_site.project_name_e]
     * </p>
     * @param projectNameE
     *            projectNameE
     */
    public void setProjectNameE(String projectNameE) {
        this.projectNameE = projectNameE;
    }

    public String getSiteCd() {
        return siteCd;
    }

    public void setSiteCd(String siteCd) {
        this.siteCd = siteCd;
    }

    /**
     * name の値を返す.
     * <p>
     * [v_site.name]
     * </p>
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * name の値を設定する.
     * <p>
     * [v_site.name]
     * </p>
     * @param name
     *            name
     */
    public void setName(String name) {
        this.name = name;
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
     * [v_site.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_site.created_at]
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
     * [v_site.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_site.updated_at]
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
     * [v_site.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [v_site.version_no]
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
     * [v_site.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_site.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * SystemAdminフラグを取得する.
     * @return SystemAdminならtrue / SystemAdminではないfalse
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * SystemAdminフラグをセットする.
     * @param systemAdmin SystemAdminフラグ
     */
    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    /**
     * ProjectAdminフラグを取得する.
     * @return ProjectAdminならtrue / ProjectAdminではないfalse
     */
    public boolean isProjectAdmin() {
        return projectAdmin;
    }

    /**
     * ProjectAdminフラグをセットする.
     * @param projectAdmin ProjectAdminフラグ
     */
    public void setProjectAdmin(boolean projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * GroupAdminフラグを取得する.
     * @return GroupAdminならtrue / GroupAdminではないfalse
     */
    public boolean isGroupAdmin() {
        return groupAdmin;
    }

    /**
     * GroupAdminフラグをセットする.
     * @param groupAdmin GroupAdminフラグ
     */
    public void setGroupAdmin(boolean groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    /**
     * 検索するユーザーを取得する.
     * @return 検索を行うユーザー
     */
    public User getSearchUser() {
        return searchUser;
    }

    /**
     * 検索するユーザーをセットする.
     * @param searchUser 検索を行うユーザー
     */
    public void setSearchUser(User searchUser) {
        this.searchUser = searchUser;
    }
}
