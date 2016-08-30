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

import jp.co.opentone.bsol.framework.core.auth.UserHolder;
import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.LegacyEntity;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.code.UseLearning;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * テーブル [v_user] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class User extends AbstractDto implements LegacyEntity, UserHolder {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 235420162351325820L;

    /**
     * Emp no.
     * <p>
     * [v_user.emp_no]
     * </p>
     */
    private String empNo;

    /**
     * Security level.
     * <p>
     * [v_user.security_level]
     * </p>
     */
    private String securityLevel;

    /**
     * Last name.
     * <p>
     * [v_user.last_name]
     * </p>
     */
    private String lastName;

    /**
     * Name e.
     * <p>
     * [v_user.name_e]
     * </p>
     */
    private String nameE;

    /**
     * Name j.
     * <p>
     * [v_user.name_j]
     * </p>
     */
    private String nameJ;

    /**
     * Email address.
     * <p>
     * [v_user.email_address]
     * </p>
     */
    private String emailAddress;

    /**
     * Sys admin flg.
     * <p>
     * [v_user.sys_admin_flg]
     * </p>
     */
    private String sysAdminFlg;

    /**
     * User profile.
     * <p>
     * [v_user.user_profile_id]
     * </p>
     */
    private Long userProfileId;

    /**
     * Default project.
     * <p>
     * [v_user.default_project_id]
     * </p>
     */
    private String defaultProjectId;

    /**
     * Last logged in at.
     * <p>
     * [v_user.last_logged_in_at]
     * </p>
     */
    private Date lastLoggedInAt;

    /**
     * User profile version no.
     * <p>
     * [v_user.user_profile_version_no]
     * </p>
     */
    private Long userProfileVersionNo;

    /**
     * Default project name e.
     * <p>
     * [v_user.default_project_name_e]
     * </p>
     */
    private String defaultProjectNameE;

    /**
     * Remarks.
     * <p>
     * [v_user.remarks]
     * </p>
     */
    private String remarks;

    /**
     * createdAt.
     * <p>
     * [v_user.emp_create_at]
     * </p>
     */
    private String empCreatedAt;

    /**
     * Role.
     */
    private String role;

    /**
     * use_learning.
     */
    private UseLearning useLearning;

    /**
     * 空のインスタンスを生成する.
     */
    public User() {
    }

    /**
     * コンストラクタ.
     * @param empNo Emp no.
     * @param nameE Name e.
     */
    public User(String empNo, String nameE) {
        this.empNo = empNo;
        this.nameE = nameE;
    }

    /**
     * このユーザーがSystem Adminの権限を持つ場合はtrue.
     * @return System Adminの権限を持つ場合はtrue、それ以外はfalse
     */
    public boolean isSystemAdmin() {

        String flg = getSysAdminFlg();
        String systemAdmin = SystemConfig.getValue(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN);
        if (StringUtils.isEmpty(systemAdmin)) {
            throw new ApplicationFatalRuntimeException(
                "Security flg (System Admin) not defined.");
        }

        return systemAdmin.equals(flg);
    }

    /**
     * empNo の値を返す.
     * <p>
     * [v_user.emp_no]
     * </p>
     * @return empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * empNo の値を設定する.
     * <p>
     * [v_user.emp_no]
     * </p>
     * @param empNo
     *            empNo
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * securityLevel の値を返す.
     * <p>
     * [v_user.security_level]
     * </p>
     * @return securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * securityLevel の値を設定する.
     * <p>
     * [v_user.security_level]
     * </p>
     * @param securityLevel
     *            securityLevel
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * lastName の値を返す.
     * <p>
     * [v_user.last_name]
     * </p>
     * @return lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * lastName の値を設定する.
     * <p>
     * [v_user.last_name]
     * </p>
     * @param lastName
     *            lastName
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * nameE の値を返す.
     * <p>
     * [v_user.name_e]
     * </p>
     * @return nameE
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * nameE の値を設定する.
     * <p>
     * [v_user.name_e]
     * </p>
     * @param nameE
     *            nameE
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * nameJ の値を返す.
     * <p>
     * [v_user.name_j]
     * </p>
     * @return nameJ
     */
    public String getNameJ() {
        return nameJ;
    }

    /**
     * nameJ の値を設定する.
     * <p>
     * [v_user.name_j]
     * </p>
     * @param nameJ
     *            nameJ
     */
    public void setNameJ(String nameJ) {
        this.nameJ = nameJ;
    }

    /**
     * emailAddress の値を返す.
     * <p>
     * [v_user.email_address]
     * </p>
     * @return emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * emailAddress の値を設定する.
     * <p>
     * [v_user.email_address]
     * </p>
     * @param emailAddress
     *            emailAddress
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * sysAdminFlg の値を返す.
     * <p>
     * [v_user.sys_admin_flg]
     * </p>
     * @return sysAdminFlg
     */
    public String getSysAdminFlg() {
        return sysAdminFlg;
    }

    /**
     * sysAdminFlg の値を設定する.
     * <p>
     * [v_user.sys_admin_flg]
     * </p>
     * @param sysAdminFlg
     *            sysAdminFlg
     */
    public void setSysAdminFlg(String sysAdminFlg) {
        this.sysAdminFlg = sysAdminFlg;
    }

    /**
     * userProfileId の値を返す.
     * <p>
     * [v_user.user_profile_id]
     * </p>
     * @return userProfileId
     */
    public Long getUserProfileId() {
        return userProfileId;
    }

    /**
     * userProfileId の値を設定する.
     * <p>
     * [v_user.user_profile_id]
     * </p>
     * @param userProfileId
     *            userProfileId
     */
    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    /**
     * defaultProjectId の値を返す.
     * <p>
     * [v_user.default_project_id]
     * </p>
     * @return defaultProjectId
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * defaultProjectId の値を設定する.
     * <p>
     * [v_user.default_project_id]
     * </p>
     * @param defaultProjectId
     *            defaultProjectId
     */
    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    /**
     * lastLoggedInAt の値を返す.
     * <p>
     * [v_user.last_logged_in_at]
     * </p>
     * @return lastLoggedInAt
     */
    public Date getLastLoggedInAt() {
        return CloneUtil.cloneDate(lastLoggedInAt);
    }

    /**
     * lastLoggedInAt の値を設定する.
     * <p>
     * [v_user.last_logged_in_at]
     * </p>
     * @param lastLoggedInAt
     *            lastLoggedInAt
     */
    public void setLastLoggedInAt(Date lastLoggedInAt) {
        this.lastLoggedInAt = CloneUtil.cloneDate(lastLoggedInAt);
    }

    /**
     * userProfileVersionNo の値を返す.
     * <p>
     * [v_user.user_profile_version_no]
     * </p>
     * @return userProfileVersionNo
     */
    public Long getUserProfileVersionNo() {
        return userProfileVersionNo;
    }

    /**
     * userProfileVersionNo の値を設定する.
     * <p>
     * [v_user.user_profile_version_no]
     * </p>
     * @param userProfileVersionNo
     *            userProfileVersionNo
     */
    public void setUserProfileVersionNo(Long userProfileVersionNo) {
        this.userProfileVersionNo = userProfileVersionNo;
    }

    /**
     * defaultProjectNameE の値を返す.
     * <p>
     * [v_user.default_project_name_e]
     * </p>
     * @return defaultProjectNameE
     */
    public String getDefaultProjectNameE() {
        return defaultProjectNameE;
    }

    /**
     * defaultProjectNameE の値を設定する.
     * <p>
     * [v_user.default_project_name_e]
     * </p>
     * @param defaultProjectNameE
     *            defaultProjectNameE
     */
    public void setDefaultProjectNameE(String defaultProjectNameE) {
        this.defaultProjectNameE = defaultProjectNameE;
    }

    /**
     * remarks の値を返す.
     * <p>
     * [v_user.remarks]
     * </p>
     * @return remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * remarks の値を設定する.
     * <p>
     * [v_user.remarks]
     * </p>
     * @param remarks
     *            remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * emp_create_at の値を返す.
     * <p>
     * [v_user.emp_create_at]
     * </p>
     * @return remarks
     */
    public String getEmpCreatedAt() {
        return empCreatedAt;
    }

    /**
     * emp_create_at の値を設定する.
     * <p>
     * [v_user.emp_create_at]
     * </p>
     * @param empCreatedAt
     *            empCreatedAt
     */
    public void setEmpCreatedAt(String empCreatedAt) {
        this.empCreatedAt = empCreatedAt;
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

    public UseLearning getUseLearning() {
        return useLearning;
    }

    public void setUseLearning(UseLearning useLearning) {
        this.useLearning = useLearning;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.auth.UserHolder#getUserId()
     */
    public String getUserId() {
        return empNo;
    }

    /**
     * 表示用ラベルを返す.
     * @return このユーザーを表すラベル
     */
    public String getLabel() {
        return ValueFormatter.formatUserNameAndEmpNo(this);
    }

    /**
     * 役職をつけた表示用ラベルを返す.
     * @return このユーザーを表すラベル
     */
    public String getLabelWithRole() {
        return ValueFormatter.formatUserNameAndEmpNoAndRole(this);
    }

    /**
     * このオブエジェクトが保持する内容を全てクリアする.
     */
    public void clearProperties() {
        empNo = null;
        securityLevel = null;
        lastName = null;
        nameE = null;
        nameJ = null;
        emailAddress = null;
        sysAdminFlg = null;
        userProfileId = null;
        defaultProjectId = null;
        lastLoggedInAt = null;
        userProfileVersionNo = null;
        defaultProjectNameE = null;
        remarks = null;
        useLearning = null;
    }
}
