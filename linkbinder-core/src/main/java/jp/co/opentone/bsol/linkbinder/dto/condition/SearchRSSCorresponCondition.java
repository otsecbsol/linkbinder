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

import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.RSSCategory;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;

/**
 * RSSコレポンの検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchRSSCorresponCondition extends AbstractCondition {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8400588115292744704L;

    /**
     * ユーザーID.
     */
    private String userId;

    /**
     * 対象コレポン下限更新日.
     */
    private Date dayPeriod;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchRSSCorresponCondition() {
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the dayPeriod
     */
    public Date getDayPeriod() {
        return dayPeriod;
    }

    /**
     * @param dayPeriod the dayPeriod to set
     */
    public void setDayPeriod(Date dayPeriod) {
        this.dayPeriod = dayPeriod;
    }

    /**
     * WorkflowProcessStatusのrequest for checkを返却する.
     * @return REQUEST_FOR_CHECK
     */
    public WorkflowProcessStatus getRequestForCheckProcess() {
        return WorkflowProcessStatus.REQUEST_FOR_CHECK;
    }

    /**
     * WorkflowProcessStatusのrequest for approvalを返却する.
     * @return REQUEST_FOR_CHECK
     */
    public WorkflowProcessStatus getRequestForApprovalProcess() {
        return WorkflowProcessStatus.REQUEST_FOR_APPROVAL;
    }

    /**
     * WorkflowStatusのrequest for checkを返却する.
     * @return REQUEST_FOR_CHECK
     */
    public WorkflowStatus getRequestForCheck() {
        return WorkflowStatus.REQUEST_FOR_CHECK;
    }

    /**
     * WorkflowStatusのrequest for approvalを返却する.
     * @return REQUEST_FOR_APPROVAL
     */
    public WorkflowStatus getRequestForApproval() {
        return WorkflowStatus.REQUEST_FOR_APPROVAL;
    }

    /**
     * WorkflowStatusのunder considerationを返却する.
     * @return UNDER_CONSIDERATION
     */
    public WorkflowStatus getUnderConsideration() {
        return WorkflowStatus.UNDER_CONSIDERATION;
    }

    /**
     * WorkflowStatusのdeniedを返却する.
     * @return DENIED
     */
    public WorkflowStatus getDenied() {
        return WorkflowStatus.DENIED;
    }

    /**
     * WorkflowStatusのissuedを返却する.
     * @return ISSUED
     */
    public WorkflowStatus getIssued() {
        return WorkflowStatus.ISSUED;
    }

    /**
     * CorresponStatusのopenを返却する.
     * @return OPEN
     */
    public CorresponStatus getOpen() {
        return CorresponStatus.OPEN;
    }

    /**
     * CorresponStatusのclosedを返却する.
     * @return CLOSED
     */
    public CorresponStatus getClosed() {
        return CorresponStatus.CLOSED;
    }

    /**
     * WorkflowTypeのcheckerを返却する.
     * @return CHECKER
     */
    public WorkflowType getChecker() {
        return WorkflowType.CHECKER;
    }

    /**
     * WorkflowTypeのapproverを返却する.
     * @return APPROVER
     */
    public WorkflowType getApprover() {
        return WorkflowType.APPROVER;
    }

    /**
     * AddressTypeのtoを返却する.
     * @return TO
     */
    public AddressType getTo() {
        return AddressType.TO;
    }

    /**
     * AddressTypeのCcを返却する.
     * @return CC
     */
    public AddressType getCc() {
        return AddressType.CC;
    }

    /**
     * AddressUserTypeのAttentionを返却する.
     * @return ATTENTION
     */
    public AddressUserType getAttention() {
        return AddressUserType.ATTENTION;
    }

    /**
     * AddressUserTypeのNormalUserを返却する.
     * @return NORMAL_USER
     */
    public AddressUserType getNormalUser() {
        return AddressUserType.NORMAL_USER;
    }

    /**
     * RSSCategoryのISSUED_NOTICE_ATTENTION(条件1)を返却する.
     * @return ISSUE_NOTICE_ATTENTION
     */
    public RSSCategory getCategory1() {
        return RSSCategory.ISSUE_NOTICE_ATTENTION;
    }
    /**
     * RSSCategoryのISSUED_NOTICE_CC(条件2)を返却する.
     * @return ISSUE_NOTICE_CC
     */
    public RSSCategory getCategory2() {
        return RSSCategory.ISSUE_NOTICE_CC;
    }
    /**
     * RSSCategoryのREQUEST_FOR_CHECK(条件3)を返却する.
     * @return REQUEST_FOR_CHECK
     */
    public RSSCategory getCategory3() {
        return RSSCategory.REQUEST_FOR_CHECK;
    }
    /**
     * RSSCategoryのREQUEST_FOR_APPROVAL(条件4)を返却する.
     * @return REQUEST_FOR_APPROVAL
     */
    public RSSCategory getCategory4() {
        return RSSCategory.REQUEST_FOR_APPROVAL;
    }
    /**
     * RSSCategoryのPERSON_IN_CHARGE(条件5)を返却する.
     * @return PERSON_IN_CHARGE
     */
    public RSSCategory getCategory5() {
        return RSSCategory.PERSON_IN_CHARGE;
    }
    /**
     * RSSCategoryのDENIED(条件6)を返却する.
     * @return DENIED
     */
    public RSSCategory getCategory6() {
        return RSSCategory.DENIED;
    }
}
