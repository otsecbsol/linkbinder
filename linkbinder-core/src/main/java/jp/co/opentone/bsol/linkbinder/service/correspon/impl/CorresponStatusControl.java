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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import org.springframework.stereotype.Service;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;

/**
 * このサービスでは承認状態・承認作業状態に関する処理を提供する.
 *
 * @author opentone
 */
@Service
public class CorresponStatusControl extends AbstractService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -17868043754859751L;
    /**
     * 承認状態.
     */
    private WorkflowStatus workflowStatus;
    /**
     * 承認作業状態.
     */
    private WorkflowProcessStatus workflowProcessStatus;
    /**
     * 空のインスタンスを生成する.
     */
    public CorresponStatusControl() {
    }

    /**
     * ログインユーザーの権限/ロール、引数のコレポン文書情報から承認状態と承認作業状態を設定する.
     * <p>
     * このメソッド実行後、実行結果が当オブジェクトに格納され、各Getterにより取得することができる.
     * なお、現在のコレポン文書の承認状態/承認作業状態から変化が無い場合は
     * Getterからnullが返される.
     *
     * @see #getWorkflowStatus()
     * @see #getWorkflowProcessStatus()
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新可能な状態でないとき
     */
    public void setup(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        validate(correspon);

        this.workflowStatus = setupWorkflowStatus(correspon);
        this.workflowProcessStatus = setupWorkflowProcessStatus(correspon);
    }

    /**
     * ログインユーザーが指定されたコレポン文書を更新可能な状態であるかを検証する.
     * @param c コレポン文書
     * @throws ServiceAbortException 更新できない状態の場合
     */
    public void validate(Correspon c) throws ServiceAbortException {
        //  System Adminは承認状態の値にかかわらず何時でも更新できる
        if (isSystemAdmin()) {
            return;
        }
        //  Project Admin/Group Adminの場合は基本的に
        //  参照可能なコレポン文書は全て更新できる
        //  但し他人の作成したコレポン文書の承認状態がDraft/Deniedの場合は更新できない
        if (isProjectAdmin(c) || isGroupAdmin(c)) {
            if ((c.getWorkflowStatus() == WorkflowStatus.DRAFT
                   || c.getWorkflowStatus() == WorkflowStatus.DENIED)
                && !isPreparer(c)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            return;
        }

        //  ユーザーの役割と承認状態・承認作業状態で更新可能な状態であるか検証
        validateWorkflowStatus(c);
    }

    /**
     * @return the workflowStatus
     */
    public WorkflowStatus getWorkflowStatus() {
        return workflowStatus;
    }
    /**
     * @return the workflowProcessStatus
     */
    public WorkflowProcessStatus getWorkflowProcessStatus() {
        return workflowProcessStatus;
    }

    /**
     * コレポン文書のChecker/Approverに設定する承認作業状態を返す.
     * @param c コレポン文書
     * @return 承認作業状態
     */
    private WorkflowProcessStatus setupWorkflowProcessStatus(Correspon c) {
        WorkflowProcessStatus result = null;
        switch (c.getWorkflowStatus()) {
        case DRAFT:
            result = getWorkflowProcessStatusDraft(c);
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            result = getWorkflowProcessStatusVerifying(c);
            break;
        case ISSUED:
            result = getWorkflowProcessStatusIssued(c);
            break;
        case DENIED:
            result = getWorkflowProcessStatusDenied(c);
            break;
        default:
            //  通常は起こり得ない
            throw new ApplicationFatalRuntimeException(
                    String.format("Invalid workflow status: %s", c.getWorkflowStatus()));
        }
        return result;
    }

    private WorkflowProcessStatus getWorkflowProcessStatusDraft(Correspon c) {
        //  Draftの場合は何も更新しない
        return null;
    }

    private WorkflowProcessStatus getWorkflowProcessStatusVerifying(Correspon c) {
        //  Checker/Approverで検証・承認可能であれば、Under Consideration更新される
        WorkflowProcessStatus result = null;
        Workflow w = findMyWorkflow(c);
        if (w != null) {
            WorkflowProcessStatus current = w.getWorkflowProcessStatus();
            switch (current) {
            case REQUEST_FOR_CHECK:
            case UNDER_CONSIDERATION:
            case REQUEST_FOR_APPROVAL:
                result = WorkflowProcessStatus.UNDER_CONSIDERATION;
                break;
            default:
                break;
            }
        }
        return result;
    }

    private WorkflowProcessStatus getWorkflowProcessStatusIssued(Correspon c) {
        //  Issuedの場合は何も更新しない
        return null;
    }

    private WorkflowProcessStatus getWorkflowProcessStatusDenied(Correspon c) {
        //  Deniedのコレポン文書を更新したタイミングで、全てのChecker/Approverの
        //  承認作業状態が更新される
        //  ここではその更新値を返す
        return WorkflowProcessStatus.NONE;
    }

    /**
     * コレポン文書に設定する承認状態を返す.
     * @param c コレポン文書
     */
    private WorkflowStatus setupWorkflowStatus(Correspon c) {
        WorkflowStatus result = null;
        switch (c.getWorkflowStatus()) {
        case DRAFT:
            result = getWorkflowStatusDraft(c);
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            result = getWorkflowStatusVerifying(c);
            break;
        case ISSUED:
            result = getWorkflowStatusIssued(c);
            break;
        case DENIED:
            result = getWorkflowStatusDenied(c);
            break;
        default:
            //  通常は起こり得ない
            throw new ApplicationFatalRuntimeException(
                    String.format("Invalid workflow status: %s", c.getWorkflowStatus()));
        }

        return result;
    }

    private WorkflowStatus getWorkflowStatusDraft(Correspon c) {
        //  Draftは更新してもDraftのまま
        return null;
    }

    private WorkflowStatus getWorkflowStatusVerifying(Correspon c) {
       // 検証中はUnder Considerationへ遷移
        return WorkflowStatus.UNDER_CONSIDERATION;
    }

    private WorkflowStatus getWorkflowStatusIssued(Correspon c) {
        //  Issuedは更新してもIssuedのまま
        return null;
    }

    private WorkflowStatus getWorkflowStatusDenied(Correspon c) {
        //  Deniedの文書はDraftへ遷移
        return WorkflowStatus.DRAFT;
    }

    private void validateWorkflowStatus(Correspon c) throws ServiceAbortException {
        switch (c.getWorkflowStatus()) {
        case DRAFT:
            validateDraft(c);
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            validateVerifying(c);
            break;
        case ISSUED:
            validateIssued(c);
            break;
        case DENIED:
            validateDenied(c);
            break;
        default:
            //  通常は起こり得ない
            throw new ApplicationFatalRuntimeException(
                    String.format("Invalid workflow status: %s", c.getWorkflowStatus()));
        }
    }

    private void validateDraft(Correspon c) throws ServiceAbortException {
        if (!isPreparer(c)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    private void validateVerifying(Correspon c) throws ServiceAbortException {
        if (isChecker(c) && c.getWorkflowStatus() == WorkflowStatus.REQUEST_FOR_APPROVAL) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        }

        boolean verifying = false;
        Workflow w = findMyWorkflow(c);
        if (w != null) {
            //  Checker/Approverが検証中であるか判定
            verifying = (isChecker(c) && isCheckerVerifying(w))
                        || (isApprover(c) && isApproverVerifying(w));
        }
        if (!verifying) {
            throwVerifyingException(c);
        }
    }

    private boolean isCheckerVerifying(Workflow w) {
        WorkflowProcessStatus s = w.getWorkflowProcessStatus();
        return s == WorkflowProcessStatus.REQUEST_FOR_CHECK
               || s == WorkflowProcessStatus.UNDER_CONSIDERATION;
    }

    private boolean isApproverVerifying(Workflow w) {
        WorkflowProcessStatus s = w.getWorkflowProcessStatus();
        return s == WorkflowProcessStatus.REQUEST_FOR_APPROVAL
               || s == WorkflowProcessStatus.UNDER_CONSIDERATION;
    }

    private void throwVerifyingException(Correspon c) throws ServiceAbortException {
        //  if文の順番重要!!!
        //  Checker/Approverを兼ねることはできないが、
        //  PreparerとChecker/Approverを兼ねることはできるため
        //  先にChecker/Approverの判定をしなければならない
        if (isChecker(c)) {
            throw new ServiceAbortException(
                ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER);
        } else if (isApprover(c)) {
            throw new ServiceAbortException(
                ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER);
        } else if (isPreparer(c)) {
            //  Preparerは検証中のコレポン文書を更新できない
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        } else {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    private void validateIssued(Correspon c) throws ServiceAbortException {
        //  発行済文書は管理者権限が無いと更新できない
        //  つまりこのメソッドでは常にエラーとなる
        throw new ServiceAbortException(
            ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
    }

    private void validateDenied(Correspon c) throws ServiceAbortException {
        if (!isPreparer(c)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        }
    }

    /**
     * コレポン文書に設定されている承認フロー中に ログインユーザーが設定されていればその情報を返す.
     * @param c
     *            コレポン文書
     * @return 承認フロー情報. ログインユーザーが承認フローに設定されていない場合はnull
     */
    private Workflow findMyWorkflow(Correspon c) {
        if (c.getWorkflows() == null) {
            return null;
        }

        User me = getCurrentUser();
        for (Workflow wf : c.getWorkflows()) {
            if (me.getEmpNo().equals(wf.getUser().getEmpNo())) {
                return wf;
            }
        }
        return null;
    }

    // 以下、権限/役割判定用ユーティリティメソッド

    private boolean isSystemAdmin() {
        return isSystemAdmin(getCurrentUser());
    }

    private boolean isProjectAdmin(Correspon c) {
        return isProjectAdmin(getCurrentUser(), c.getProjectId());
    }
    private boolean isGroupAdmin(Correspon c) {
        return isGroupAdmin(getCurrentUser(), c.getFromCorresponGroup().getId());
    }

    private boolean isPreparer(Correspon correspon) {
        return isPreparer(correspon.getCreatedBy().getEmpNo());
    }
}
