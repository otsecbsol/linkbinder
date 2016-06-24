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

import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeReceivable;

/**
 * ユーザーの検索条件を保持する.
 *
 * @author opentone
 *
 */
public class SearchUserCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8417959574643141078L;

    /**
     * プロジェクト-会社IDのカラム名.
     */
    private static final String COLUMN_PROJECT_COMPANY_ID = "project_company_id";

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * 従業員番号.
     */
    private String empNo;

    /**
     * セキュリティレベル.
     */
    private String securityLevel;

    /**
     * 従業員名.
     */
    private String lastName;

    /**
     * 従業員名（英語）.
     */
    private String nameE;

    /**
     * 従業員名（日本語）.
     */
    private String nameJ;

    /**
     * 最終ログイン日時.
     */
    private Long lastLoggedInAt;

    /**
     * 電子メールアドレス.
     */
    private String emailAddress;

    /**
     * 管理者フラグ.
     */
    private String sysAdminFlg;

    /**
     * バージョンNo.
     */
    private String userSettingsVersionNo;

    /**
     * デフォルトプロジェクトID.
     */
    private String defaultProjectId;

    /**
     * デフォルトプロジェクト名.
     */
    private String defaultProjectNameE;

    /**
     * 会社-メンバー設定用：IS NULLで検索するカラム名.
     */
    private String nullColumn;

    /**
     * 一覧用：会社ID.
     */
    private Long companyId;

    /**
     * 一覧用：活動単位ID.
     */
    private Long corresponGroupId;

    /**
     * 一覧用：ProjectAdmin.
     */
    private String projectAdmin;

    /**
     * 一覧用：GroupAdmin.
     */
    private String groupAdmin;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchUserCondition() {
    }

    /**
     * projectIdを取得します.
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectIdを設定します.
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * empNoを取得します.
     * @return the empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * empNoを設定します.
     * @param empNo the empNo to set
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * securityLevelを取得します.
     * @return the securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * securityLevelを設定します.
     * @param securityLevel the securityLevel to set
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * lastNameを取得します.
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * lastNameを設定します.
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * nameEを取得します.
     * @return the nameE
     */
    public String getNameE() {
        return nameE;
    }

    /**
     * nameEを設定します.
     * @param nameE the nameE to set
     */
    public void setNameE(String nameE) {
        this.nameE = nameE;
    }

    /**
     * nameJを取得します.
     * @return the nameJ
     */
    public String getNameJ() {
        return nameJ;
    }

    /**
     * nameJを設定します.
     * @param nameJ the nameJ to set
     */
    public void setNameJ(String nameJ) {
        this.nameJ = nameJ;
    }

    /**
     * lastLoggedInAtを取得します.
     * @return the lastLoggedInAt
     */
    public Long getLastLoggedInAt() {
        return lastLoggedInAt;
    }

    /**
     * lastLoggedInAtを設定します.
     * @param lastLoggedInAt the lastLoggedInAt to set
     */
    public void setLastLoggedInAt(Long lastLoggedInAt) {
        this.lastLoggedInAt = lastLoggedInAt;
    }

    /**
     * emailAddressを取得します.
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * emailAddressを設定します.
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * sysAdminFlgを取得します.
     * @return the sysAdminFlg
     */
    public String getSysAdminFlg() {
        return sysAdminFlg;
    }

    /**
     * sysAdminFlgを設定します.
     * @param sysAdminFlg the sysAdminFlg to set
     */
    public void setSysAdminFlg(String sysAdminFlg) {
        this.sysAdminFlg = sysAdminFlg;
    }

    /**
     * userSettingsVersionNoを取得します.
     * @return the userSettingsVersionNo
     */
    public String getUserSettingsVersionNo() {
        return userSettingsVersionNo;
    }

    /**
     * userSettingsVersionNoを設定します.
     * @param userSettingsVersionNo the userSettingsVersionNo to set
     */
    public void setUserSettingsVersionNo(String userSettingsVersionNo) {
        this.userSettingsVersionNo = userSettingsVersionNo;
    }

    /**
     * defaultProjectIdを取得します.
     * @return the defaultProjectId
     */
    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    /**
     * defaultProjectIdを設定します.
     * @param defaultProjectId the defaultProjectId to set
     */
    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }

    /**
     * defaultProjectNameEを取得します.
     * @return the defaultProjectNameE
     */
    public String getDefaultProjectNameE() {
        return defaultProjectNameE;
    }

    /**
     * defaultProjectNameEを設定します.
     * @param defaultProjectNameE the defaultProjectNameE to set
     */
    public void setDefaultProjectNameE(String defaultProjectNameE) {
        this.defaultProjectNameE = defaultProjectNameE;
    }

    /**
     * nullColumnを取得します.
     * @return the nullColumn
     */
    public String getNullColumn() {
        return SQLConvertUtil.encode(nullColumn);
    }

    /**
     * nullColumnを設定します.
     * @param nullColumn the nullColumn to set
     */
    public void setNullColumn(String nullColumn) {
        this.nullColumn = nullColumn;
    }

    /**
     * companyIdを取得します.
     * @return the companyId
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * companyIdを設定します.
     * @param companyId the companyId to set
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * corresponGroupIdを取得します.
     * @return the corresponGroupId
     */
    public Long getCorresponGroupId() {
        return corresponGroupId;
    }

    /**
     * corresponGroupIdを設定します.
     * @param corresponGroupId the corresponGroupId to set
     */
    public void setCorresponGroupId(Long corresponGroupId) {
        this.corresponGroupId = corresponGroupId;
    }

    /**
     * projectAdminを取得します.
     * @return the projectAdmin
     */
    public String getProjectAdmin() {
        return projectAdmin;
    }

    /**
     * projectAdminを設定します.
     * @param projectAdmin the projectAdmin to set
     */
    public void setProjectAdmin(String projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * groupAdminを取得します.
     * @return the groupAdmin
     */
    public String getGroupAdmin() {
        return groupAdmin;
    }

    /**
     * groupAdminを設定します.
     * @param groupAdmin the groupAdmin to set
     */
    public void setGroupAdmin(String groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    /**
     * プロジェクト - 会社に所属していない条件を設定する.
     */
    public void setNotRelatedCompanyCondition() {
        setNullColumn(COLUMN_PROJECT_COMPANY_ID);
    }

    /**
     * メール通知受信要否：Noを取得します.
     * @return メール通知受信要否：No
     */
    public EmailNoticeReceivable getEmailNoticeReceivableNo() {
        return EmailNoticeReceivable.NO;
    }
}
