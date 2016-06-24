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


import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * コレポン文書のユーザー情報でのサマリ情報のための検索条件.
 *
 * @author opentone
 *
 */
public class SearchCorresponUserSummaryCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7048630909385605485L;

    /**
     * 権限：部門管理者を取得するKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * 従業員番号.
     */
    private String userId;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * Person In Charge使用フラグ.
     */
    private boolean usePersonInCharge;

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
     * userIdを取得します.
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * userIdを設定します.
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * systemAdminを取得します.
     * @return the systemAdmin
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * systemAdminを設定します.
     * @param systemAdmin the systemAdmin to set
     */
    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    /**
     * projectAdminを取得します.
     * @return the projectAdmin
     */
    public boolean isProjectAdmin() {
        return projectAdmin;
    }

    /**
     * projectAdminを設定します.
     * @param projectAdmin the projectAdmin to set
     */
    public void setProjectAdmin(boolean projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * usePersonInChargeを設定します.
     * @param usePersonInCharge the usePersonInCharge to set
     */
    public void setUsePersonInCharge(boolean usePersonInCharge) {
        this.usePersonInCharge = usePersonInCharge;
    }

    /**
     * usePersonInChargeを取得します.
     * @return the usePersonInCharge
     */
    public boolean isUsePersonInCharge() {
        return usePersonInCharge;
    }

    /**
     * 承認状態：作成中を取得します.
     * @return 承認状態：作成中
     */
    public WorkflowStatus getDraft() {
        return WorkflowStatus.DRAFT;
    }

    /**
     * 承認種別：検証を取得します.
     * @return 承認種別：検証
     */
    public WorkflowType getChecker() {
        return WorkflowType.CHECKER;
    }

    /**
     * 承認種別：検証を取得します.
     * @return 承認種別：検証
     */
    public WorkflowType getApprover() {
        return WorkflowType.APPROVER;
    }

    /**
     * 承認作業状態：検証依頼中を取得します.
     * @return 承認作業状態：検証依頼中
     */
    public WorkflowProcessStatus getRequestForCheck() {
        return WorkflowProcessStatus.REQUEST_FOR_CHECK;
    }

    /**
     * 承認作業状態：承認依頼中を取得します.
     * @return 承認作業状態：承認依頼中
     */
    public WorkflowProcessStatus getRequestForApproval() {
        return WorkflowProcessStatus.REQUEST_FOR_APPROVAL;
    }

    /**
     * 承認作業状態：コレポン文書更新済を取得します.
     * @return 承認作業状態：コレポン文書更新済
     */
    public WorkflowProcessStatus getUnderConsideration() {
        return WorkflowProcessStatus.UNDER_CONSIDERATION;
    }

    /**
     * 承認状態：否認済を取得します.
     * @return 承認状態：否認済
     */
    public WorkflowStatus getDenied() {
        return WorkflowStatus.DENIED;
    }

    /**
     * 承認状態：発行済を取得します.
     * @return 承認状態：発行済
     */
    public WorkflowStatus getIssued() {
        return WorkflowStatus.ISSUED;
    }

    /**
     * コレポン文書状態：Openを取得します.
     * @return コレポン文書状態：Open
     */
    public CorresponStatus getOpen() {
        return CorresponStatus.OPEN;
    }

    /**
     * コレポン文書状態：Closedを取得します.
     * @return コレポン文書状態：Closed
     */
    public CorresponStatus getClosed() {
        return CorresponStatus.CLOSED;
    }

    /**
     * コレポン文書状態：Canceledを取得します.
     * @return コレポン文書状態：Canceled
     */
    public CorresponStatus getCanceled() {
        return CorresponStatus.CANCELED;
    }

    /**
     * 宛先：TOを取得します.
     * @return 宛先：TO
     */
    public AddressType getTo() {
        return AddressType.TO;
    }

    /**
     * 宛先：CCを取得します.
     * @return 宛先：CC
     */
    public AddressType getCc() {
        return AddressType.CC;
    }

    /**
     * 宛先ユーザー種別：Attentionを取得します.
     * @return 宛先ユーザー種別：Attention
     */
    public AddressUserType getAttention() {
        return AddressUserType.ATTENTION;
    }

    /**
     * 宛先ユーザー種別：NormalUserを取得します.
     * @return 宛先ユーザー種別：NormalUser
     */
    public AddressUserType getNormalUser() {
        return AddressUserType.NORMAL_USER;
    }

    /**
     * 返信要否：要を取得します.
     * @return 返信要否：要
     */
    public ReplyRequired getYes() {
        return ReplyRequired.YES;
    }

    /**
     * WorkflowProccessStatusのnoneを返却する.
     * @return NONE
     */
    public WorkflowProcessStatus getNone() {
        return WorkflowProcessStatus.NONE;
    }

    /**
     * AllowApproverToBrowseのvisibleを返却する.
     * @return VISIBLE
     */
    public AllowApproverToBrowse getVisible() {
        return AllowApproverToBrowse.VISIBLE;
    }

    /**
     * ReadStatusのreadを返却する.
     * @return READ
     */
    public ReadStatus getRead() {
        return ReadStatus.READ;
    }

    /**
     * SecurityLevelのgroupAdminを返却する.
     * @return groupAdmin
     */
    public String getGroupAdmin() {
        return SystemConfig.getValue(KEY_GROUP_ADMIN);
    }

    /**
     * 検証・承認依頼中を表す全ての承認状態を返す.
     * @return 検証・承認依頼中の承認状態
     */
    public WorkflowStatus[] getRequesting() {
        return WorkflowStatus.getRequesting();
    }
}
