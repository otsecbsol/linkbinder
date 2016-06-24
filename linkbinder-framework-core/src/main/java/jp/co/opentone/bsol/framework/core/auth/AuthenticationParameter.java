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
package jp.co.opentone.bsol.framework.core.auth;

import java.io.Serializable;

/**
 * ユーザー認証に必要な情報を格納するためのクラス.
 * @author opentone
 */
public class AuthenticationParameter implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1279929516138348995L;

    /**
     * ユーザーID.
     */
    private String userId;
    /**
     * 暗号化前のパスワード.
     */
    private String password;
    /**
     * sys_users.emp_nm_e.
     */
    private String nameE;
    /**
     * sys_users.sys_adm_flg.
     */
    private String sysAdminFlg;
    /**
     * パスワードがnullであるかを判定するフラグ.
     */
    private String passwordNullFlg;
    /**
     * パスワードが最後に更新された日付.
     */
    private Long   passwordUpdatedAt;
    /**
     * ID作成日
     */
    private String createdAt;

    /**
     * 空のインスタンスを生成する.
     */
    public AuthenticationParameter() {
    }

    /**
     * @param userId セットする userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param password セットする password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param nameE セットする nameE
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * @return nameE
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * @param sysAdminFlg セットする sysAdminFlg
     */
    public void setSysAdminFlg(String sysAdminFlg) {
        this.sysAdminFlg = sysAdminFlg;
    }

    /**
     * @return sysAdminFlg
     */
    public String getSysAdminFlg() {
        return sysAdminFlg;
    }

    /**
     * @param passwordNullFlg セットする passwordNullFlg
     */
    public void setPasswordNullFlg(String passwordNullFlg) {
        this.passwordNullFlg = passwordNullFlg;
    }

    /**
     * @return passwordNullFlg
     */
    public String getPasswordNullFlg() {
        return passwordNullFlg;
    }

    /**
     * @param passwordUpdatedAt セットする passwordUpdatedAt
     */
    public void setPasswordUpdatedAt(Long passwordUpdatedAt) {
        this.passwordUpdatedAt = passwordUpdatedAt;
    }

    /**
     * @return passwordUpdatedAt
     */
    public Long getPasswordUpdatedAt() {
        return passwordUpdatedAt;
    }

    /**
     * @param createdAt セットする createdIdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * @return createdAt
     */
    public String getCreatedIdAt() {
        return createdAt;
    }
}
