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

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;

/**
 * テーブル [v_correspon_group_user] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class CorresponGroupUser extends AbstractDto implements Entity {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5545000272331421859L;

    /**
     * Id.
     * <p>
     * [v_correspon_group_user.id]
     * </p>
     */
    private Long id;

    /**
     * Correspon group.
     */
    private CorresponGroup corresponGroup;

    /**
     * User.
     */
    private User user;

    /**
     * Security level.
     * <p>
     * [v_correspon_group_user.security_level]
     * </p>
     */
    private String securityLevel;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_correspon_group_user.created_at]
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
     * [v_correspon_group_user.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [v_correspon_group_user.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * Role.
     */
    private String role;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponGroupUser() {
    }

    /**
     * このユーザーがDicipline Adminの権限を持つ場合はtrue.
     * @return Group Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isGroupAdmin() {

        String groupAdmin =
                SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN);

        if (StringUtils.isEmpty(groupAdmin)) {
            throw new ApplicationFatalRuntimeException(
                "Security level (Group Admin) not defined.");
        }

        return groupAdmin.equals(getSecurityLevel());
    }

    /**
     * id の値を返す.
     * <p>
     * [v_correspon_group_user.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_correspon_group_user.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * securityLevel の値を返す.
     * <p>
     * [v_correspon_group_user.security_level]
     * </p>
     * @return securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * securityLevel の値を設定する.
     * <p>
     * [v_correspon_group_user.security_level]
     * </p>
     * @param securityLevel
     *            securityLevel
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
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
     * [v_correspon_group_user.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return CloneUtil.cloneDate(createdAt);
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_correspon_group_user.created_at]
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
     * [v_correspon_group_user.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return CloneUtil.cloneDate(updatedAt);
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_correspon_group_user.updated_at]
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
     * [v_correspon_group_user.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_correspon_group_user.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * @param corresponGroup
     *            the corresponGroup to set
     */
    public void setCorresponGroup(CorresponGroup corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * @return the corresponGroup
     */
    public CorresponGroup getCorresponGroup() {
        return corresponGroup;
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
     * 表示用ラベルを返す.
     * @param idParam コレポングループID
     * @return このユーザーを表すラベル
     */
    public String getLabelGroupUser(Long idParam) {
        return ValueFormatter.formatUserNameAndEmpNoAndGroup(this, idParam);
    }

    /**
     * 役職をつけた表示用ラベルを返す.
     * @param idParam コレポングループID
     * @return このユーザーを表すラベル
     */
    public String getLabelWithRole(Long idParam) {
        return ValueFormatter.formatUserNameAndEmpNoAndRoleAndGroup(this, idParam);
    }
}
