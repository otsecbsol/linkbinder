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



import org.hibernate.validator.constraints.Length;

import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.DateString;
import jp.co.opentone.bsol.framework.core.validation.constraints.MailAddress;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.linkbinder.dto.code.MasterDataImportResultStatus;

/**
 * テーブル [SYS_USERS]と関連する[SYS_PJ_USERS] の1レコードを表すDto.
 *
 * @author opentone
 *
 */
public class SysUsers extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 235420162351325820L;

    /**
     * 空のインスタンスを生成する.
     */
    public SysUsers() {
    }

    /**
     * Emp no.
     * <p>
     * [sys_users.emp_no]
     * </p>
     */
    @Required
    @Length(max = 5)
    @Alphanumeric
    private String empNo;

    /**
     * userIdAt.
     * <p>
     * [sys_users.USER_ID_VALID_DT]
     * </p>
     */
    @Required
    @DateString
    private String userIdAt;

    /**
     * createdAt.
     * <p>
     * [sys_users.ID_CREATED_DT]
     * </p>
     */
    private String createdAt;


    /**
     * Last name.
     * <p>
     * [sys_users.last_name]
     * </p>
     */
    @Length(max = 15)
    private String lastName;

    /**
     * Name e.
     * <p>
     * [sys_users.name_e]
     * </p>
     */
    @Length(max = 40)
    private String nameE;

    /**
     * Name j.
     * <p>
     * [sys_users.name_j]
     * </p>
     */
    @Length(max = 20)
    private String nameJ;



    /**
     * password.
     * <p>
     * [sys_users.emp_passwd]
     * </p>
     */
    @Length(max = 31)
    @Alphanumeric
    private String password;


    /**
     * passwordUpdatedAt.
     * <p>
     * [sys_users.emp_passwd_upd]
     * </p>
     */
    private String passwordUpdatedAt;

    /**
     * sysAdmFlg.
     * <p>
     * [sys_users.sys_adm_flg]
     * </p>
     */
    @Length(max = 1)
    private String sysAdmFlg;


    /**
     * userRegistAprvFlg.
     * <p>
     * [sys_users.user_regist_aprv_flg]
     * </p>
     */
    @Length(max = 1)
    @Alphanumeric
    private String userRegistAprvFlg;

    /**
     * securityLevel.
     * <p>
     * [sys_users.security_level]
     * </p>
     */
    @Length(max = 2)
    private String securityLevel;

    /**
     * Name j.
     * <p>
     * [sys_pj_users.pj_id]
     * </p>
     */
    @Length(max = 11)
    @Alphanumeric
    private String pjId;

    /**
     * Name j.
     * <p>
     * [sys_pj_users.pj_adm_flg]
     * </p>
     */
    @Length(max = 1)
    @Alphanumeric
    private String pjAdmFlg;

    /**
     * mailAddress
     *
     * [sys_users.EMP_EMAIL_ADDR]
     *
     */
    @Length(max = 60)
    @MailAddress
    private String mailAddress;

    /**
     * 取込結果.
     */
    private MasterDataImportResultStatus importResultStatus = MasterDataImportResultStatus.NONE;

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
     * userIdAt の値を返す.
     * <p>
     * [v_user.userIdAt]
     * </p>
     * @return userIdAt
     */
    public String getUserIdAt() {
        return userIdAt;
    }

    /**
     * userIdAt の値を設定する.
     * <p>
     * [v_user.userIdAt]
     * </p>
     * @param userIdAt
     *            userIdAt
     */
    public void setUserIdAt(String userIdAt) {
        this.userIdAt = userIdAt;
    }


    /**
     * empNo の値を返す.
     * <p>
     * [v_user.emp_no]
     * </p>
     * @return empNo
     */
    public String getCreatedIdAt() {
        return createdAt;
    }

    /**
     * empNo の値を設定する.
     * <p>
     * [v_user.emp_no]
     * </p>
     * @param empNo
     *            empNo
     */
    public void setCreatedIdAt(String createdAt) {
        this.createdAt = createdAt;
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
     * password の値を返す.
     * <p>
     * [sys_users.emp_passwd]
     * </p>
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * password の値を設定する.
     * <p>
     * [sys_users.emp_passwd]
     * </p>
     * @param password
     *
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * passwordUpdatedAt の値を返す.
     * <p>
     * [sys_users.emp_passwd_upd]
     * </p>
     * @return passwordUpdatedAt
     */
    public String getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    /**
     * passwordUpdatedAt の値を設定する.
     * <p>
     * [sys_users.emp_passwd_upd]
     * </p>
     * @param passwordUpdatedAt
     *
     */
    public void setPasswordUpdatedAt(String passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }


    /**
     * sysAdmFlg の値を返す.
     * <p>
     * [sys_users.sys_adm_flg]
     * </p>
     * @return
     */
    public String getSysAdmFlg() {
        return sysAdmFlg;
    }

    /**
     * sysAdmFlg の値を設定する.
     * <p>
     * [sys_users.sys_adm_flg]
     * </p>
     * @param sysAdmFlg
     *
     */
    public void setSysAdmFlg(String sysAdmFlg) {
        this.sysAdmFlg = sysAdmFlg;
    }

    /**
     * userRegistAprvFlg の値を返す.
     * <p>
     * [sys_users.user_regist_aprv_flg]
     * </p>
     * @return
     */
    public String getUserRegistAprvFlg() {
        return userRegistAprvFlg;
    }

    /**
     * userRegistAprvFlg の値を設定する.
     * <p>
     * [sys_users.user_regist_aprv_flg]
     * </p>
     * @param userRegistAprvFlg
     *
     */
    public void setUserRegistAprvFlg(String userRegistAprvFlg) {
        this.userRegistAprvFlg = userRegistAprvFlg;
    }

    /**
     *  の値を返す.
     * <p>
     * [sys_users.security_level]
     * </p>
     * @return
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     *  の値を設定する.
     * <p>
     * [sys_users.security_level]
     * </p>
     * @param
     *
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * pjAdmFlg の値を返す.
     * <p>
     * [sys_pj_users.pj_adm_flg]
     * </p>
     * @return pjAdmFlg
     */
    public String getPjAdmFlg() {
        return pjAdmFlg;
    }

    /**
     * pjAdmFlg の値を設定する.
     * <p>
     * [sys_pj_users.pj_adm_flg]
     * </p>
     * @param pjAdmFlg
     *
     */
    public void setPjAdmFlg(String pjAdmFlg) {
        this.pjAdmFlg = pjAdmFlg;
    }

    /**
     * mailAddress の値を返す.
     * <p>
     * [sys_pj_users.pj_adm_flg]
     * </p>
     * @return pjAdmFlg
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * mailAddress の値を設定する.
     * <p>
     * [sys_pj_users.pj_adm_flg]
     * </p>
     * @param pjAdmFlg
     *
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * pjId の値を返す.
     * <p>
     * [sys_pj_users.pj_id]
     * </p>
     * @return pjId
     */
    public String getPjId() {
        return pjId;
    }

    /**
     * pjId の値を設定する.
     * <p>
     * [sys_pj_users.pj_id]
     * </p>
     * @param pjId
     *
     */
    public void setPjId(String pjId) {
        this.pjId = pjId;
    }

    public MasterDataImportResultStatus getImportResultStatus() {
        return importResultStatus;
    }

    public void setImportResultStatus(MasterDataImportResultStatus importResultStatus) {
        this.importResultStatus = importResultStatus;
    }


}
