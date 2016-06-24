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
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;


/**
 * テーブル [project_user_profile] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class ProjectUserProfile extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4408432078133203114L;

    /**
     * Id.
     * <p>
     * [project_user_profile.id]
     * </p>
     */
    private Long id;

    /**
     * Project.
     * <p>
     * [project_user_profile.project_id]
     * </p>
     */
    private String projectId;

    /**
     * User.
     */
    private User user;

    /**
     * Role.
     */
    private String role;

    /**
     * Default correspon group.
     */
    private CorresponGroup defaultCorresponGroup;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [project_user_profile.created_at]
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
     * [project_user_profile.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [project_user_profile.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * NULL値をセットするカラム.
     */
    private String nullColumn;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectUserProfile() {
    }

    /**
     * id の値を返す.
     * <p>
     * [project_user_profile.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [project_user_profile.id]
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
     * [project_user_profile.project_id]
     * </p>
     * @return projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectId の値を設定する.
     * <p>
     * [project_user_profile.project_id]
     * </p>
     * @param projectId
     *            projectId
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
     * [project_user_profile.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [project_user_profile.created_at]
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
     * [project_user_profile.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [project_user_profile.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = CloneUtil.cloneDate(updatedAt);
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [project_user_profile.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [project_user_profile.delete_no]
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
     * roleを取得します.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * roleを設定します.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param defaultCorresponGroup
     *            the defaultCorresponGroup to set
     */
    public void setDefaultCorresponGroup(CorresponGroup defaultCorresponGroup) {
        this.defaultCorresponGroup = defaultCorresponGroup;
    }

    /**
     * @return the defaultCorresponGroup
     */
    public CorresponGroup getDefaultCorresponGroup() {
        return defaultCorresponGroup;
    }

    /**
     * nullColumnを設定します.
     * @param nullColumn the nullColumn to set
     */
    public void setNullColumn(String nullColumn) {
        this.nullColumn = nullColumn;
    }

    /**
     * nullColumnを取得します.
     * @return the nullColumn
     */
    public String getNullColumn() {
        return SQLConvertUtil.encode(nullColumn);
    }
}
