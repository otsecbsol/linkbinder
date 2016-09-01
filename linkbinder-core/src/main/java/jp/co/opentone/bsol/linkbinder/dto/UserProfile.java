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

import jp.co.opentone.bsol.framework.core.dao.VersioningEntity;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.UseLearning;

import java.util.Date;


/**
 * テーブル [user_profile] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class UserProfile extends AbstractDto implements VersioningEntity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7383816847841745110L;

    /**
     * Id.
     * <p>
     * [user_profile.id]
     * </p>
     */
    private Long id;

    /**
     * User.
     */
    private User user;

    /**
     * Last logged in at.
     * <p>
     * [user_profile.last_logged_in_at]
     * </p>
     */
    private Date lastLoggedInAt;

    /**
     * Default project.
     * <p>
     * [user_profile.default_project_id]
     * </p>
     */
    private String defaultProjectId;

    /**
     * Correspon invisible fields.
     * <p>
     * [user_profile.correspon_invisible_fields]
     * </p>
     */
    private String corresponInvisibleFields;

    private UseLearning useLearning;
    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [user_profile.created_at]
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
     * [user_profile.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Version no.
     * <p>
     * [user_profile.version_no]
     * </p>
     */
    private Long versionNo;

    /**
     * Delete no.
     * <p>
     * [user_profile.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * feed key.
     * <p>
     * [user_profile.feed_key]
     * </p>
     */
    private String feedKey;

    /**
     * 空のインスタンスを生成する.
     */
    public UserProfile() {
    }

    /**
     * id の値を返す.
     * <p>
     * [user_profile.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [user_profile.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * lastLoggedInAt の値を返す.
     * <p>
     * [user_profile.last_logged_in_at]
     * </p>
     * @return lastLoggedInAt
     */
    public Date getLastLoggedInAt() {
        return CloneUtil.cloneDate(lastLoggedInAt);
    }

    /**
     * lastLoggedInAt の値を設定する.
     * <p>
     * [user_profile.last_logged_in_at]
     * </p>
     * @param lastLoggedInAt
     *            lastLoggedInAt
     */
    public void setLastLoggedInAt(Date lastLoggedInAt) {
        this.lastLoggedInAt = CloneUtil.cloneDate(lastLoggedInAt);
    }

    /**
     * defaultProjectId の値を返す.
     * <p>
     * [user_profile.default_project_id]
     * </p>
     * @return defaultProjectId
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * defaultProjectId の値を設定する.
     * <p>
     * [user_profile.default_project_id]
     * </p>
     * @param defaultProjectId
     *            defaultProjectId
     */
    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    /**
     * corresponInvisibleFields の値を返す.
     * <p>
     * [user_profile.correspon_invisible_fields]
     * </p>
     * @return corresponInvisibleFields
     */
    public String getCorresponInvisibleFields() {
        return corresponInvisibleFields;
    }

    /**
     * corresponInvisibleFields の値を設定する.
     * <p>
     * [user_profile.correspon_invisible_fields]
     * </p>
     * @param corresponInvisibleFields
     *            corresponInvisibleFields
     */
    public void setCorresponInvisibleFields(String corresponInvisibleFields) {
        this.corresponInvisibleFields = corresponInvisibleFields;
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
     * [user_profile.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [user_profile.created_at]
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
     * [user_profile.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [user_profile.updated_at]
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
     * [user_profile.version_no]
     * </p>
     * @return versionNo
     */
    public Long getVersionNo() {
        return versionNo;
    }

    /**
     * versionNo の値を設定する.
     * <p>
     * [user_profile.version_no]
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
     * [user_profile.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [user_profile.delete_no]
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
     * @param feedKey the feedKey to set
     */
    public void setFeedKey(String feedKey) {
        this.feedKey = feedKey;
    }

    /**
     * @return the feedKey
     */
    public String getFeedKey() {
        return feedKey;
    }

    public boolean isNew() {
        return getId() == null || getId() == 0;
    }

    public UseLearning getUseLearning() {
        return useLearning;
    }

    public void setUseLearning(UseLearning useLearning) {
        this.useLearning = useLearning;
    }
}
