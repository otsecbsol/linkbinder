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
package jp.co.opentone.bsol.linkbinder.view.action.control;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;

/**
 * コレポン文書表示画面のリンク/ボタン制御クラス.
 *
 * @author opentone
 */
public class CorresponPageElementControl implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -196012266982313261L;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(CorresponPageElementControl.class);

    /**
     * 承認パターン1の取得キー.
     */
    private static final String KEY_PATTERN_1 = "workflow.pattern.1";
    /**
     * 承認パターン2の取得キー.
     */
    private static final String KEY_PATTERN_2 = "workflow.pattern.2";
    /**
     * コレポン文書表示画面.
     */
    private CorresponPage page = null;

    /**
     * コレポン文書.
     */
    private Correspon correspon = null;
    /**
     * Copyリンクの表示/非表示.
     */
    private boolean copyLink;
    /**
     * 学習用文書再登録の表示/非表示.
     */
    private boolean recreateLearningContents;
    /**
     * Printリンクの表示/非表示.
     */
    private boolean printLink;
    /**
     * Updateリンクの表示/非表示.
     */
    private boolean updateLink;
    /**
     * Downloadリンクの表示/非表示.
     */
    private boolean downloadLink;
    /**
     * Workflowリンクの表示/非表示.
     */
    private boolean workFlowLink;
    /**
     * Verificationボタンの表示/非表示.
     */
    private boolean verificationButton;
    /**
     * Request for Approvalボタンの表示/非表示.
     */
    private boolean requestForApprovalButton;
    /**
     * Issueボタンの表示/非表示.
     */
    private boolean issueButton;
    /**
     * Deleteボタンの表示/非表示.
     */
    private boolean deleteButton;

    /**
     * 設定済の情報を全てクリアする.
     */
    public void clear() {
        page = null;
        correspon = null;

        copyLink = false;
        printLink = false;
        updateLink = false;
        downloadLink = false;
        workFlowLink = false;
        verificationButton = false;
        requestForApprovalButton = false;
        issueButton = false;
        deleteButton = false;
    }

    /**
     * @param correspon
     *            the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }
    /**
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }
    /**
     * @param copyLink
     *            the copyLink to set
     */
    public void setCopyLink(boolean copyLink) {
        this.copyLink = copyLink;
    }
    /**
     *
     */
    public boolean isRecreateLearningContentsButton() {
        return isRecreateLearningContents();
    }
    /**
     * @return the copyLink
     */
    public boolean isCopyLink() {
        return copyLink;
    }
    /**
     * @return the replyLink
     */
    public boolean isReplyLink() {
        return isReply();
    }
    /**
     * @return the forwardLink
     */
    public boolean isForwardLink() {
        return isForward();
    }
    /**
     * @param printLink
     *            the printLink to set
     */
    public void setPrintLink(boolean printLink) {
        this.printLink = printLink;
    }
    /**
     * @return the printLink
     */
    public boolean isPrintLink() {
        return printLink;
    }
    /**
     * @param updateLink
     *            the updateLink to set
     */
    public void setUpdateLink(boolean updateLink) {
        this.updateLink = updateLink;
    }
    /**
     * @return the updateLink
     */
    public boolean isUpdateLink() {
        return updateLink;
    }
    /**
     * @param downloadLink
     *            the downloadLink to set
     */
    public void setDownloadLink(boolean downloadLink) {
        this.downloadLink = downloadLink;
    }
    /**
     * @return the downloadLink
     */
    public boolean isDownloadLink() {
        return downloadLink;
    }
    /**
     * @param workFlowLink
     *            the workFlowLink to set
     */
    public void setWorkFlowLink(boolean workFlowLink) {
        this.workFlowLink = workFlowLink;
    }
    /**
     * @return the workFlowLink
     */
    public boolean isWorkFlowLink() {
        return workFlowLink;
    }
    /**
     * @param verificationButton
     *            the verificationButton to set
     */
    public void setVerificationButton(boolean verificationButton) {
        this.verificationButton = verificationButton;
    }
    /**
     * @return the verificationLink
     */
    public boolean isVerificationButton() {
        return verificationButton;
    }
    /**
     * @param issueButton
     *            the issueButton to set
     */
    public void setIssueButton(boolean issueButton) {
        this.issueButton = issueButton;
    }
    /**
     * @return the verificationLink
     */
    public boolean isIssueButton() {
        return issueButton;
    }
    /**
     * @param requestForApprovalButton
     *            the requestForApprovalButton to set
     */
    public void setRequestForApprovalButton(boolean requestForApprovalButton) {
        this.requestForApprovalButton = requestForApprovalButton;
    }
    /**
     * @return the requestForApprovalButton
     */
    public boolean isRequestForApprovalButton() {
        return requestForApprovalButton;
    }
    /**
     * @param deleteButton
     *            the deleteButton to set
     */
    public void setDeleteButton(boolean deleteButton) {
        this.deleteButton = deleteButton;
    }
    /**
     * @return the deleteButton
     */
    public boolean isDeleteButton() {
        return deleteButton;
    }

    /**
     * ログインユーザーが、System Admin/Project Admin/Group Adminのいずれかであればtrue.
     * @param corresponGroupId Adminのチェック対象となる活動単位ID
     * @return 管理者の場合はtrue
     */
    public boolean isAdmin(Long corresponGroupId) {
        return isSystemAdmin() || isProjectAdmin() || isGroupAdmin(corresponGroupId);
    }

    /**
     * ログインユーザーがSystem Adminの場合はtrue.
     * @return System Adminの場合はtrue
     */
    public boolean isSystemAdmin() {
        return page.isSystemAdmin();
    }

    /**
     * ログインユーザーがProject Adminの場合はtrue.
     * @return Project Adminの場合はtrue
     */
    public boolean isProjectAdmin() {
        return page.isProjectAdmin(page.getCurrentProjectId());
    }

    /**
     * ログインユーザーがコレポン文書に対してGroup Adminの権限を持つ場合はtrue.
     * @return Group Adminの場合はtrue
     */
    public boolean isCorresponGroupAdmin() {
        return page.isAnyGroupAdmin(page.getCorrespon());
    }

    /**
     * ログインユーザーが指定された活動単位に対してGroup Adminの権限を持つ場合はtrue.
     * @param corresponGroupId 活動単位ID
     * @return Group Adminの場合はtrue
     */
    public boolean isGroupAdmin(Long corresponGroupId) {
        return page.isGroupAdmin(corresponGroupId);
    }

    /**
     * ログインユーザーがコレポン文書のPreparerである場合はtrue.
     * @return Preparerの場合true
     */
    public boolean isPreparer() {
        return page.getUserRoleHelper()
            .isWorkflowPreparer(page.getCorrespon(),
                               page.getCurrentUser());
    }

    /**
     * ログインユーザーがコレポン文書のCheckerである場合はtrue.
     * @return Checkerの場合true
     */
    public boolean isChecker() {
        return page.getUserRoleHelper()
            .isWorkflowChecker(page.getCorrespon(),
                               page.getCurrentUser());
    }

    /**
     * ログインユーザーがコレポン文書のApproverである場合はtrue.
     * @return Approverの場合true
     */
    public boolean isApprover() {
        return page.getUserRoleHelper()
            .isWorkflowApprover(page.getCorrespon(),
                               page.getCurrentUser());
    }

    /**
     * 対象のページを指定し、このオブジェクトを初期化する.
     * @param setPage 対象のページ
     */
    public void setUp(CorresponPage setPage) {
        ArgumentValidator.validateNotNull(setPage);
        this.page = setPage;
        this.correspon = setPage.getCorrespon();

        // 権限により分岐
        if (isSystemAdmin()) {
            setSystemAdminCondition();
        } else if (isProjectAdmin()
                   || page.isGroupAdmin(page.getCorrespon().getFromCorresponGroup().getId())) {
            setProjectGroupAdminCondition();
        } else {
            if (isChecker()) {
                setCheckerCondition();
            } else if (isApprover()) {
                setApproverCondition();
            } else if (isPreparer()) {
                setPreparerCondition();
            } else {
                setNormalCondition();
            }
        }

        // Request For Approvalボタンの制御(要望対応)
        setRequestForApprovalButton();
    }

    /**
     * Request For Approvalボタンの制御(要望対応).
     * ボタンを押下可能なのは、承認状態がDraftかつ承認フローにApproverが設定されている場合のみ。
     * 権限チェックは行わない(Draft文書を見ることができる権限がある　ならば ボタンを押下できる)。
     */
    private void setRequestForApprovalButton() {
        requestForApprovalButton = false;

        WorkflowStatus workflowStatus = correspon.getWorkflowStatus();
        if (log.isDebugEnabled()) {
            log.debug("workflowStatus[" + workflowStatus + "]");
        }
        if (WorkflowStatus.DRAFT.equals(workflowStatus)) {
            // Draftの場合には、APPROVERが設定されているかをチェック
            List<Workflow> workflow = correspon.getWorkflows();
            if (workflow != null) {
                for (Workflow w : workflow) {
                    if (log.isDebugEnabled()) {
                        log.debug("w.getWorkflowType[" + w.getWorkflowType() + "]");
                    }
                    if (WorkflowType.APPROVER.equals(w.getWorkflowType())) {
                        requestForApprovalButton = true;
                        break;
                    }
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("requestForApprovalButton[" + requestForApprovalButton + "]");
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        switch (status) {
        case DRAFT:
            setSystemAdminDraft();
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            setSystemAdminRequesting();
            break;
        // 上記以外のステータスの場合(Denied,Issue)
        default:
            setSystemAdminAfterApproved(status);
            break;
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminDraft() {
        setSystemAdminDraftFixed();
        setSystemAdminDraftForceToUseWorkflow();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <p>
     * 下記の条件で表示/非表示が固定な要素.
     * </p>
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminDraftFixed() {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        workFlowLink = true;
        verificationButton = false;
        requestForApprovalButton = true;
        deleteButton = true;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：システム管理者</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminDraftForceToUseWorkflow() {
        setIssueButton();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminRequesting() {
        setSystemAdminRequestingFixed();
        setSystemAdminRequestingWorkflowPattern();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <p>
     * 下記の条件で表示/非表示が固定な要素.
     * </p>
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminRequestingFixed() {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        verificationButton = true;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminRequestingWorkflowPattern() {
        // 承認フローパターン1で承認作業ステータスが「None」のChecker/Approverを変更できる
        workFlowLink = false;
        List<Workflow> workflow = correspon.getWorkflows();
        if (SystemConfig.getValue(KEY_PATTERN_1).equals(
                StringUtils.trim(
                    correspon.getCorresponType().getWorkflowPattern().getWorkflowCd()))) {
            for (Workflow w : workflow) {
                WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
                if (WorkflowProcessStatus.NONE == processStatus) {
                    workFlowLink = true;
                }
            }
        } else if (SystemConfig.getValue(KEY_PATTERN_2).equals(StringUtils.trim(
                    correspon.getCorresponType().getWorkflowPattern().getWorkflowCd()))) {
            workFlowLink = isApproverProcessStatusNone(workflow);
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：システム管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DENIED.ISSUED</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setSystemAdminAfterApproved(WorkflowStatus status) {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        workFlowLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DENIED.ISSUED</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupAdminCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        switch (status) {
        case DRAFT:
            setProjectGroupDraft();
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            setProjectGroupRequesting();
            break;
        // 上記以外のステータスの場合(Denied,Issue)
        default:
            setProjectGroupAfterApproved(status);
            break;
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraft() {
        setProjectGroupDraftFixed();
        setProjectGroupDraftRole();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <p>
     * 下記の条件で表示/非表示が固定な要素.
     * </p>
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftFixed() {
        copyLink = true;
        verificationButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftRole() {
        if (isPreparer()) {
            // Preparer時
            setProjectGroupDraftPreparer();
        } else {
            setProjectGroupDraftNotPreparer();
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftPreparer() {
        setProjectGroupDraftPreparerIssueButton();
        setProjectGroupDraftPreparerNotIssueButton();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftPreparerIssueButton() {
        setIssueButton();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftPreparerNotIssueButton() {
        printLink = true;
        updateLink = true;
        downloadLink = true;
        workFlowLink = true;
        requestForApprovalButton = true;
        deleteButton = true;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Preparer以外</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupDraftNotPreparer() {
        updateLink = false;
        printLink = false;
        downloadLink = false;
        workFlowLink = false;
        requestForApprovalButton = false;
        deleteButton = false;
        issueButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupRequesting() {
        setProjectGroupRequestingFixed();
        setProjectGroupRequestingRole();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <p>
     * 下記の条件で表示/非表示が固定な要素.
     * </p>
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupRequestingFixed() {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        verificationButton = true;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupRequestingRole() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        if (log.isDebugEnabled()) {
            log.trace("********** setProjectGroupRequestingRole **********");
            log.debug("WorkflowStatus[" + status + "]");
        }
        if (((status.equals(WorkflowStatus.REQUEST_FOR_CHECK))
                || (status.equals(WorkflowStatus.UNDER_CONSIDERATION)))) {
            setWorkflowLinkForChecker(true);
        } else {
            setWorkflowLinkForNotChecker();
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Checker</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setProjectGroupAfterApproved(WorkflowStatus status) {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        workFlowLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：プロジェクト管理者.グループ管理者</li>
     * <li>ロール：Checker以外</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setWorkflowLinkForNotChecker() {
        workFlowLink = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        switch (status) {
        case DRAFT:
            setPreparerDraft();
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            setPreparerRequesting();
            break;
        // 上記以外のステータスの場合(Denied,Issue)
        default:
            setPreparerAfterDecision(status);
            break;
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerDraft() {
        setPreparerDraftFixed();
        setPreparerDraftForceToUseWorkflow();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <p>
     * 下記の条件で表示/非表示が固定な要素.
     * </p>
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerDraftFixed() {
        copyLink = true;
        updateLink = true;
        printLink = true;
        downloadLink = true;
        workFlowLink = true;
        verificationButton = false;
        requestForApprovalButton = true;
        deleteButton = true;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerDraftForceToUseWorkflow() {
        setIssueButton();
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：REQUEST_FOR_CHECK.UNDER_CONSIDERATION.REQUEST_FOR_APPROVAL</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerRequesting() {
        copyLink = true;
        updateLink = false;
        printLink = true;
        downloadLink = true;
        workFlowLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Preparer</li>
     * <li>承認状態：DENIED.ISSUED</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setPreparerAfterDecision(WorkflowStatus status) {
        copyLink = true;
        updateLink = !status.equals(WorkflowStatus.ISSUED);
        printLink = true;
        downloadLink = true;
        workFlowLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Checker</li>
     * <li>承認状態：</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setCheckerCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        boolean preparer = isPreparer();
        switch (status) {
        case DRAFT:
            setCheckerConditionDraft(preparer);
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
            setCheckerConditionNotWorkflowLink(preparer);
            setWorkflowLinkForChecker(false);
            break;
        // 上記以外のステータスの場合(REQUEST_FOR_APPROVAL,DENIED,ISSUED)
        default:
            setCheckerConditionAfterChecked(status);
            break;
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Checker</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     * @param preparer Preparer
     */
    private void setCheckerConditionDraft(boolean preparer) {
        copyLink = true;
        updateLink = preparer;
        printLink = preparer;
        downloadLink = preparer;
        workFlowLink = preparer;
        verificationButton = false;
        issueButton = false;
        if (preparer) {
            setIssueButton();
        }
        requestForApprovalButton = preparer;
        deleteButton = preparer;
    }

    /**
     * 承認作業状態により画面遷移構成要素が制御される.
     * @param preparer Preparer
     */
    private void setCheckerConditionNotWorkflowLink(boolean preparer) {
        copyLink = true;
//        printLink = preparer;
//        downloadLink = preparer;
        printLink = true;
        downloadLink = true;
        updateLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
        // 自身の承認作業ステータスが、Request for Check または Under Consideration
        List<Workflow> workflow = correspon.getWorkflows();
        for (Workflow w : workflow) {
            WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
            if ((WorkflowProcessStatus.REQUEST_FOR_CHECK == processStatus)
                    || (WorkflowProcessStatus.UNDER_CONSIDERATION == processStatus)) {

                String empNo = w.getUser().getEmpNo();
                if (empNo.equals(page.getCurrentUser().getEmpNo())) {
                    printLink = true;
                    downloadLink = true;
                    updateLink = true;
                    verificationButton = true;
                    break;
                }
            }
        }
    }

    /**
     * 承認フローパターンと承認作業状態によりWorkflowリンクが制御される.
     * ①承認フローパターン1が適用されている自身の承認作業ステータスが、Request for Check
     *   または Under Considerationの場合は、後続のChecker/Approverを追加・変更できる.
     * ②検証者は、 承認依頼前(承認作業状態=NONE) の承認者を変更することができる。
     *   承認者は承認ワークフローを変更することができない。
     */
    //CHECKSTYLE:OFF
    private void setWorkflowLinkForChecker(boolean isAdmin) {
        if (log.isDebugEnabled()) {
            log.trace("********** setWorkflowLinkForChecker **********");
            log.debug("isAdmin[" + isAdmin + "]");
            log.debug("workflowCd["
                + correspon.getCorresponType().getWorkflowPattern().getWorkflowCd() + "]");
        }
        boolean concludedFlag = false;
        workFlowLink = false;
        List<Workflow> workflow = correspon.getWorkflows();
        if (SystemConfig.getValue(KEY_PATTERN_1).equals(
                StringUtils.trim(
                    correspon.getCorresponType().getWorkflowPattern().getWorkflowCd()))) {
            log.trace("KEY_PATTERN_1");
            for (Workflow w : workflow) {
                if (log.isDebugEnabled()) {
                    log.debug("concludedFlag[" + concludedFlag + "]");
                    log.debug("WorkflowProcessStatus[" + w.getWorkflowProcessStatus() + "]");
                }
                if (concludedFlag) {
                    workFlowLink = true;
                    break;
                }

                WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
                if ((WorkflowProcessStatus.REQUEST_FOR_CHECK == processStatus)
                        || (WorkflowProcessStatus.UNDER_CONSIDERATION == processStatus)) {
                    String empNo = w.getUser().getEmpNo();
                    if (log.isDebugEnabled()) {
                        log.debug("w.getUser().getEmpNo[" + empNo + "]");
                        log.debug("page.getCurrentUser().getEmpNo["
                            + page.getCurrentUser().getEmpNo() + "]");
                    }
                    if (empNo.equals(page.getCurrentUser().getEmpNo()) || isAdmin) {
                        concludedFlag = true;
                    }
                }
            }
        } else if (SystemConfig.getValue(KEY_PATTERN_2).equals(StringUtils.trim(
                    correspon.getCorresponType().getWorkflowPattern().getWorkflowCd()))) {
            log.trace("KEY_PATTERN_2");

            // 自身がChecker&&Check済の場合にはworkflowは弄れない
            String currentUserEmpNo = page.getCurrentUser().getEmpNo();
            if (log.isDebugEnabled()) {
                log.debug("currentUserEmpNo[" + currentUserEmpNo + "]");
            }
            for (Workflow w : workflow) {
                WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
                String empNo = w.getUser().getEmpNo();
                if (log.isDebugEnabled()) {
                    log.debug("WorkflowProcessStatus[" + processStatus + "]");
                    log.debug("empNo[" + empNo + "]");
                }
                if (!isAdmin
                     && WorkflowProcessStatus.CHECKED.equals(processStatus)
                     && currentUserEmpNo.equals(empNo)) {
                    workFlowLink = false;
                    return;
                }
            }

            workFlowLink = isApproverProcessStatusNone(workflow);
        }
    }
    //CHECKSTYLE:ON

    /**
     * Approverの承認作業状態を判定する.
     * @param workflow 承認フローリスト
     * @return 承認作業状態がNONEの場合はtrue / 以外はfalse
     */
    private boolean isApproverProcessStatusNone(List<Workflow> workflow) {
        boolean approverProjecessStatus = false;
        for (Workflow w : workflow) {
            WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
            WorkflowType type = w.getWorkflowType();
            if (type == WorkflowType.APPROVER) {
                approverProjecessStatus = processStatus == WorkflowProcessStatus.NONE;
                break;
            }
        }
        return approverProjecessStatus;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Checker</li>
     * <li>承認状態：REQUEST_FOR_APPROVAL,DENIED,ISSUED</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setCheckerConditionAfterChecked(WorkflowStatus status) {
        copyLink = true;
        if (status.equals(WorkflowStatus.ISSUED)) {
            updateLink = false;
        } else {
            updateLink = false;
        }
        printLink = true;
        downloadLink = true;
        workFlowLink = false;
        verificationButton = false;
        issueButton = false;
        requestForApprovalButton = false;
        deleteButton = false;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Approver</li>
     * <li>承認状態：</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setApproverCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        boolean preparer = isPreparer();
        switch (status) {
        case DRAFT:
            setApproverConditionDraft(preparer);
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
            setApproverConditionBeforeChecked(preparer);
            break;
        // 上記以外のステータスの場合(REQUEST_FOR_APPROVAL,DENIED,ISSUED)
        default:
            setApproverConditionAfterChecked(status);
            break;
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Approver</li>
     * <li>承認状態：DRAFT</li>
     * <li>承認作業状態：</li>
     * </ul>
     * @param preparer Preparer
     */
    private void setApproverConditionDraft(boolean preparer) {
        copyLink = true;
        updateLink = preparer;
        printLink = preparer;
        downloadLink = preparer;
        verificationButton = false;
        issueButton = false;
        if (preparer) {
            setIssueButton();
        }
        requestForApprovalButton = preparer;
        workFlowLink = preparer;
        deleteButton = preparer;
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Approver</li>
     * <li>承認状態：REQUEST_FOR_CHECK,UNDER_CONSIDERATION</li>
     * <li>承認作業状態：</li>
     * </ul>
     * @param preparer Preparer
     */
    private void setApproverConditionBeforeChecked(boolean preparer) {
        issueButton = false;
        requestForApprovalButton = false;
        workFlowLink = false;
        deleteButton = false;
        copyLink = true;
        // 承認フローパターン3 または 承認フローパターン4が適用されている
        if ((SystemConfig.getValue("workflow.pattern.3").equals(StringUtils.trim(
                   correspon.getCorresponType().getWorkflowPattern().getWorkflowCd())))
                || ((AllowApproverToBrowse.VISIBLE).equals(
                      correspon.getCorresponType().getAllowApproverToBrowse()))) {
            printLink = true;
            downloadLink = true;
        } else {
//            printLink = preparer;
//            downloadLink = preparer;
            printLink = true;
            downloadLink = true;
        }
        // 自身の承認作業ステータスが、Request for Approval または Under Consideration
        updateLink = false;
        verificationButton = false;
        List<Workflow> workflow = correspon.getWorkflows();
        for (Workflow w : workflow) {
            WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
            if ((WorkflowProcessStatus.REQUEST_FOR_APPROVAL == processStatus)
                    || (WorkflowProcessStatus.UNDER_CONSIDERATION == processStatus)) {
                if (StringUtils.equals(w.getUser().getEmpNo(), page.getCurrentUser().getEmpNo())) {
                    updateLink = true;
                    verificationButton = true;
                    printLink = true;
                    downloadLink = true;
                    break;
                }
            }
        }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：Approver</li>
     * <li>承認状態：REQUEST_FOR_APPROVAL,DENIED,ISSUED</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setApproverConditionAfterChecked(WorkflowStatus status) {
        issueButton = false;
        requestForApprovalButton = false;
        workFlowLink = false;
        deleteButton = false;
        printLink = true;
        downloadLink = true;
        copyLink = true;
        if (status.equals(WorkflowStatus.REQUEST_FOR_APPROVAL)) {
            updateLink = true;
            verificationButton = true;
        } else {
            verificationButton = false;
            if (status.equals(WorkflowStatus.ISSUED)) {
                updateLink = false;
            } else {
                updateLink = false;
            }
        }
    }

    /**
     * 承認フロー適用フラグにより発行ボタンが制御される.
     */
    private void setIssueButton() {
        if (correspon.getCorresponType().getForceToUseWorkflow().equals(
                          ForceToUseWorkflow.OPTIONAL)) {
             // 承認フロー設定が「任意」
             issueButton = true;
         } else {
             issueButton = false;
         }
    }

    /**
     * 画面遷移構成要素の制御を行う.
     * <ul>
     * <li>権限：一般</li>
     * <li>ロール：閲覧者</li>
     * <li>承認状態：</li>
     * <li>承認作業状態：</li>
     * </ul>
     */
    private void setNormalCondition() {
        WorkflowStatus status = correspon.getWorkflowStatus();
        switch (status) {
        case ISSUED:
            copyLink = true;
            updateLink = false;
            printLink = true;
            downloadLink = true;
            workFlowLink = false;
            verificationButton = false;
            issueButton = false;
            requestForApprovalButton = false;
            deleteButton = false;
            break;
        // 上記以外のステータスの場合
        default:
            copyLink = true;
            updateLink = false;
            printLink = false;
            downloadLink = false;
            workFlowLink = false;
            verificationButton = false;
            issueButton = false;
            requestForApprovalButton = false;
            deleteButton = false;
            break;
        }
    }

    /**
     * 改訂可能であればture.
     * @return 改訂可能な場合はtrue
     */
    public boolean isReviseLink() {
        if (correspon == null) {
            return false;
        }

        // Cancel済のもの
        return correspon.getCorresponStatus() == CorresponStatus.CANCELED;
    }

    /**
     * コレポン文書状態を編集可能であればtrueを返す.
     * @return 編集可能であればtrue
     */
    public boolean isCorresponStatusEditable() {
        //  妥当な承認状態であるか
        if (!isIssued(correspon)) {
            return false;
        }

        //  妥当なユーザーであるか
        Long corresponGroupId = correspon.getFromCorresponGroup().getId();
        if (isAdmin(corresponGroupId)
            || isPreparer() || isChecker() || isApprover()) {
            //  妥当な文書状態であるか
            return correspon.getCorresponStatus() != CorresponStatus.CANCELED;
        } else {
            return false;
        }
    }

    /**
     * コレポン文書既読・未読状態を編集可能であればtrueを返す.
     * @return 編集可能であればtrue
     */
    public boolean isCorresponReadStatusEditable() {
        return (correspon != null);
    }

    /**
     * コレポン文書が、返信文書である場合はtrueを返す.
     * @return 返信文書の場合true
     */
    public boolean isReplyCorrespon() {
        return (correspon != null && correspon.getParentCorresponId() != null);
    }

    /**
     * 文書状態更新(Open)ボタンが押下可能であればtrueを返す.
     * @return 押下可能であればtrue
     */
    public boolean isOpenButton() {
        return isCorresponStatusEditable();
    }

    /**
     * 文書状態更新(Close)ボタンが押下可能であればtrueを返す.
     * @return 押下可能であればtrue
     */
    public boolean isCloseButton() {
        return isCorresponStatusEditable();
    }

    /**
     * 文書状態更新(Cancel)ボタンが押下可能であればtrueを返す.
     * @return 押下可能であればtrue
     */
    public boolean isCancelButton() {
        // 編集可能な文書状態であるか
        return isCorresponStatusEditable();
    }

    /**
     * 既読・未読状態更新(Read)ボタンが押下可能であればtrueを返す.
     * @return 押下可能であればtrue
     */
    public boolean isReadButton() {
        // 編集可能な文書状態であるか
        return isCorresponReadStatusEditable();
    }

    /**
     * 既読・未読状態更新(Unread)ボタンが押下可能であればtrueを返す.
     * @return 押下可能であればtrue
     */
    public boolean isUnreadButton() {
        // 編集可能な文書状態であるか
        return isCorresponReadStatusEditable();
    }

    /**
     * 返信要否・返信期限が編集可能であればtrueを返す.
     * @return 編集可能であればtrue
     */
    public boolean isReplyRequiredEditable() {
        //  妥当な承認状態であるか
        if (!isIssued(correspon)) {
            return false;
        }

        //  妥当なユーザーであるか
        Long corresponGroupId = correspon.getFromCorresponGroup().getId();
        if (isAdmin(corresponGroupId)
            || isPreparer() || isChecker() || isApprover()) {
            //  妥当な文書状態であるか
            return correspon.getCorresponStatus() != CorresponStatus.CANCELED;
        } else {
            return false;
        }
    }

    /**
     * 返信済文書を引用して返信作可能であればtrueを返す.
     * @return 操作可能であればtrue
     */
    public boolean isReplyWithPreviousCorresponLink() {
        if (page == null) {
            return false;
        }
        Correspon parent = page.getCorrespon();

        //  妥当なコレポン文書であるか
        if (!isRepliableCorrespon(parent)) {
            return false;
        }
        return true;
    }

    private boolean isRepliableCorrespon(Correspon c) {
        //  妥当な承認状態であるか
        if (!isIssued(c)) {
            return false;
        }

        //  妥当な文書状態であるか
        if (c.getCorresponStatus() == CorresponStatus.CANCELED) {
            return false;
        }
        return true;
    }

    private boolean isEditablePersonInChargeUser(
                AddressCorresponGroup addressGroup, AddressUser addressUser) {
        Long corresponGroupId = addressGroup.getCorresponGroup().getId();
        if (isAdmin(corresponGroupId)) {
            return true;
        }

        //  Attention/Ccであるか
        return isAddressUser(addressUser);
    }

    private boolean isAddressUser(AddressUser addressUser) {
        if (addressUser == null) {
            return false;
        }
        return addressUser.getUser().getEmpNo().equals(page.getCurrentUser().getEmpNo());
    }

    /**
     * ログインユーザーがPerson in Chargeを編集可能であればtrueを返す.
     * @param addressGroup 対象の宛先-活動単位
     * @param addressUser 対象の宛先-ユーザー
     * @return 編集可能であればtrue
     */
    public boolean isEditPersonInCharge(
                AddressCorresponGroup addressGroup, AddressUser addressUser) {
        return isRepliableCorrespon(page.getCorrespon())
                && isEditablePersonInChargeUser(addressGroup, addressUser);
    }

    /**
     * ログインユーザーがPerson in Chargeを削除可能であればtrueを返す.
     * @param addressGroup 対象の宛先-活動単位
     * @param addressUser 対象の宛先-ユーザー
     * @return 編集可能であればtrue
     */
    public boolean isDeletePersonInCharge(
                AddressCorresponGroup addressGroup, AddressUser addressUser) {
        return isRepliableCorrespon(page.getCorrespon())
                && isEditablePersonInChargeUser(addressGroup, addressUser);
    }

    /**
     * ログインユーザーがPerson in Chargeを割り当て可能であればtrueを返す.
     * @param addressGroup 対象の宛先-活動単位
     * @param addressUser 対象の宛先-ユーザー
     * @return 編集可能であればtrue
     */
    public boolean isAssignPersonInCharge(
                AddressCorresponGroup addressGroup, AddressUser addressUser) {
        return isRepliableCorrespon(page.getCorrespon())
                && isEditablePersonInChargeUser(addressGroup, addressUser);
    }

    /**
     * ログインユーザーが返信可能であればtrueを返す.
     * @return 返信可能であればtrue
     */
    public boolean isReply() {
        if (page == null) {
            return false;
        }
        return isRepliableCorrespon(page.getCorrespon());
    }

    /**
     * ログインユーザーが転送可能であればtrueを返す.
     * @return 転送可能であればtrue
     */
    public boolean isForward() {
        if (page == null) {
            return false;
        }
        return isIssued(page.getCorrespon());
    }

    /**
     * ZIPリンクが操作可能であればtrueを返す.
     * @return コレポン文書が存在する場合trueを返す.
     */
    public boolean isZipLink() {
        return correspon != null;
    }

    /**
     * 承認作業状態がISSUEDであればtrueを返す.
     * @param c コレポン文書
     * @return 承認作業状態がISSUEDであればtrueを返す.
     */
    public boolean isIssued(Correspon c) {
        return ((c != null) && c.isIssued());
    }

    /**
     * ログインユーザが学習用コンテンツ再作成可能であればtrueを返す.
     */
    public boolean isRecreateLearningContents() {
        if (page == null) {
            return false;
        }
        return isRepliableCorrespon(page.getCorrespon());
    }
}
