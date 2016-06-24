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
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;


/**
 * v_projectの検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchProjectCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 9046472255480726602L;

    /**
     * 権限：部門管理者を取得するKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * ユーザID.
     */
    private String empNo;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * ProjectAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * プロジェクト名.
     */
    private String nameE;

    /**
     * ORDER BY句に渡す並び順の条件.
     */
    private String orderBy;

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
     * nameを取得します.
     * @return the name
     */
    public String getNameE() {
        return nameE;
    }


    /**
     * nameを設定します.
     * @param name the name to set
     */
    public void setNameE(String name) {
        this.nameE = name;
    }


    /**
     * コレポン文書の宛先を表すAddressTypeを返却する.
     * @return To
     */
    public AddressType getToValue() {
        return AddressType.TO;
    }

    /**
     * コレポン文書のCCを表すAddressTypeを返却する.
     * @return Cc
     */
    public AddressType getCcValue() {
        return AddressType.CC;
    }

    /**
     * コレポン文書の未読を表すReadStatusを返却する.
     * @return Cc
     */
    public ReadStatus getUnreadValue() {
        return ReadStatus.NEW;
    }

    /**
     * コレポン文書のAttentionを表すAddressUserTypeを返却する.
     * @return Attention
     */
    public AddressUserType getAttentionValue() {
        return AddressUserType.ATTENTION;
    }

    /**
     * コレポン文書のNormalUserを表すAddressUserTypeを返却する.
     * @return Cc
     */
    public AddressUserType getNormalUserValue() {
        return AddressUserType.NORMAL_USER;
    }

    /**
     * コレポン文書の作成中を表すWorkflowStatusを返却する.
     * @return Draft
     */
    public WorkflowStatus getDraft() {
        return WorkflowStatus.DRAFT;
    }

    /**
     * コレポン文書の発行済を表すWorkflowStatusを返却する.
     * @return Issued
     */
    public WorkflowStatus getIssued() {
        return WorkflowStatus.ISSUED;
    }

    /**
     * コレポン文書のNoneを表すWorkflowProcessStatusを返却する.
     * @return Issued
     */
    public WorkflowProcessStatus getNone() {
        return WorkflowProcessStatus.NONE;
    }

    /**
     * WorkflowTypeのapproverを返却する.
     * @return APPROVER
     */
    public WorkflowType getApprover() {
        return WorkflowType.APPROVER;
    }

    /**
     * AllowApproverToBrowseのvisibleを返却する.
     * @return VISIBLE
     */
    public AllowApproverToBrowse getVisible() {
        return AllowApproverToBrowse.VISIBLE;
    }

    /**
     * SecurityLevelのgroupAdminを返却する.
     * @return groupAdmin
     */
    public String getGroupAdmin() {
        return SystemConfig.getValue(KEY_GROUP_ADMIN);
    }

    /**
     * @param orderBy the orderBy to set
     */
    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    /**
     * @return the orderBy
     */
    public String getOrderBy() {
        return orderBy;
    }

    /**
     * @return the sanitized orderBy
     */
    public String getSanitizedOrderBy() {
        return SQLConvertUtil.encode(orderBy);
    }
}
