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


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;

/**
 * Workflow情報に、一覧表示用制御を追加したDto.
 *
 * @author opentone
 *
 */
public class WorkflowIndex extends AbstractDto {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -819314917010171032L;

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(WorkflowIndex.class);

    /**
     * ワークフロー.
     */
    private Workflow workflow;

    /**
     * 閲覧.
     */
    private boolean view;

    /**
     * 更新.
     */
    private boolean update;

    /**
     * 承認.
     */
    private boolean checkApprove;

    /**
     * 検証.
     */
    private boolean verification;

    /**
     * 変更前コメント.
     */
    private String comment;

    /**
     * 空のインスタンスを生成する. （引数ありの方を使用すること）
     */
    @SuppressWarnings("unused")
    private WorkflowIndex() {
    }

    /**
     * ワークフロー情報を元に、閲覧・更新・承認情報を設定する.
     * @param workflow
     *            ワークフロー
     * @param workflowStatus
     *            コレポン文書のワークフローステータス
     * @param corresponType
     *            コレポン種別
     * @param admin
     *            Admin権限有無
     * @param empNo
     *            ログインユーザーID
     */
    public WorkflowIndex(Workflow workflow, WorkflowStatus workflowStatus,
                         CorresponType corresponType, boolean admin, String empNo) {
        this.workflow = workflow;

        view = isViewStatus(workflowStatus, corresponType);
        update = isUpdateStatus(workflowStatus);
        checkApprove = isCheckApproveStatus(workflowStatus);
        verification = isVerificationStatus(checkApprove, admin, empNo);
        comment = workflow.getCommentOn();
        if (log.isDebugEnabled()) {
            log.trace("********** WorkflowIndex() result **********");
            log.debug("view[" + view + "]");
            log.debug("update[" + update + "]");
            log.debug("checkApprove[" + checkApprove + "]");
            log.debug("verification[" + verification + "]");
            log.trace("********************************************");
        }
    }

    /**
     * Verification制御情報を取得する.
     * ※コメント、ボタン等の制御
     *
     * @param admin
     * @param empNo
     * @return Verification制御情報
     */
    private boolean isVerificationStatus(boolean canCheckApprove, boolean admin, String empNo) {
        if (canCheckApprove) {
            return StringUtils.equals(empNo, workflow.getUser().getEmpNo()) || admin;
        }
        return false;
    }


    /**
     * CheckApprove制御情報を取得する.
     *
     * @param workflowStatus
     * @return CheckApprove制御情報
     *           承認状態とアクションの対応表_Rev002.xlsの通りにtrue/falseを返す。
     */
    private boolean isCheckApproveStatus(WorkflowStatus workflowStatus) {
        if (log.isDebugEnabled()) {
            log.trace("************** isCheckApproveStatus **************");
            log.debug("workflowStatus[" + workflowStatus + "]");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug(
                "workflow.getWorkflowProcessStatus[" + workflow.getWorkflowProcessStatus() + "]");
        }
        if (workflow.isChecker()) {
            return isWorkflowCheckerStatus()
               && (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                        || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus));
        } else if (workflow.isApprover()) {
            return WorkflowStatus.REQUEST_FOR_APPROVAL.equals(workflowStatus)
                    || (isWorkflowApproverStatus()
                         &&  (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                               || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus)));
        }

        return false;
    }

    /**
     * Update制御情報を取得する.
     *
     * @param workflowStatus
     * @return Update制御情報
     *           承認状態とアクションの対応表_Rev002.xlsの通りにtrue/falseを返す。
     */
    private boolean isUpdateStatus(WorkflowStatus workflowStatus) {
        if (log.isDebugEnabled()) {
            log.trace("************** isUpdateStatus **************");
            log.debug("workflowStatus[" + workflowStatus + "]");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug("workflow.getWorkflowType[" + workflow.getWorkflowType() + "]");
            log.debug(
                "workflow.getWorkflowProcessStatus[" + workflow.getWorkflowProcessStatus() + "]");
        }
        if (workflow.isChecker()) {
            // checker
            return isWorkflowCheckerStatus()
                && (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                        || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus));
        } else if (workflow.isApprover()) {
            // approver
            return WorkflowStatus.REQUEST_FOR_APPROVAL.equals(workflowStatus)
                    || (isWorkflowApproverStatus()
                           && (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                                    || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus)));
        } else if (workflow.getWorkflowType() == null) {
            // Preparer
            return WorkflowStatus.DRAFT.equals(workflowStatus)
                || WorkflowStatus.DENIED.equals(workflowStatus);
        }
        return false;
    }

    /**
     * View制御情報を取得する.
     *
     * @param allowApproverToBrowse
     * @return View制御情報
     *           承認状態とアクションの対応表_Rev002.xlsの通りにtrue/falseを返す。
     */
    private boolean isViewStatus(WorkflowStatus workflowStatus,
                                 CorresponType corresponType) {
        String workflowCd = null;
        if (corresponType.getWorkflowPattern() != null) {
            workflowCd = corresponType.getWorkflowPattern().getWorkflowCd();
        }
        if (log.isDebugEnabled()) {
            log.trace("************** isViewStatus **************");
            log.debug("workflowStatus[" + workflowStatus + "]");
            log.debug("corresponType.getWorkflowPattern().getWorkflowCd["
                + workflowCd + "]");
            log.debug("corresponType.getAllowApproverToBrowse["
                + corresponType.getAllowApproverToBrowse() + "]");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug("workflow.getWorkflowType[" + workflow.getWorkflowType() + "]");
            log.debug(
                "workflow.getWorkflowProcessStatus[" + workflow.getWorkflowProcessStatus() + "]");
        }
        if (workflow.isChecker()) {
            // NONEの場合は閲覧できない
            if (WorkflowProcessStatus.NONE == workflow.getWorkflowProcessStatus()) {
                return false;
            }

            //Checker
            return (isWorkflowCheckerStatus()
                        && (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                                || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus)))
                   || (WorkflowProcessStatus.CHECKED.equals(workflow.getWorkflowProcessStatus())
                       &&  WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus))
                   || WorkflowStatus.REQUEST_FOR_APPROVAL.equals(workflowStatus)
                   || WorkflowStatus.DENIED.equals(workflowStatus)
                   || WorkflowStatus.ISSUED.equals(workflowStatus);
        } else if (workflow.isApprover()) {
            // NONEの場合は閲覧できない
            // ただし承認フローパターン3か、Approver閲覧許可がある場合を除く
            if (WorkflowProcessStatus.NONE == workflow.getWorkflowProcessStatus()) {
                if (AllowApproverToBrowse.INVISIBLE == corresponType.getAllowApproverToBrowse()
                    && !SystemConfig.getValue("workflow.pattern.3").equals(workflowCd)) {
                    return false;
                }
            }

            // Approver
            return ((AllowApproverToBrowse.VISIBLE.equals(corresponType.getAllowApproverToBrowse())
                        || SystemConfig.getValue("workflow.pattern.3").equals(workflowCd))
                    && (WorkflowStatus.REQUEST_FOR_CHECK.equals(workflowStatus)
                                || WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus))
                    )
                   || (WorkflowProcessStatus.REQUEST_FOR_APPROVAL.equals(
                                               workflow.getWorkflowProcessStatus())
                       &&  WorkflowStatus.UNDER_CONSIDERATION.equals(workflowStatus))
                   || WorkflowStatus.REQUEST_FOR_APPROVAL.equals(workflowStatus)
                   || WorkflowStatus.DENIED.equals(workflowStatus)
                   || WorkflowStatus.ISSUED.equals(workflowStatus);
        } else if (workflow.getWorkflowType() == null) {
            // Preparer
            return true;
        }
        return false;
    }
    /**
     * Checkerワークフローステータス制御.
     * @return Checkerワークフローステータス制御
     *         ※①【Checkerに対して検証依頼中】か【Checkerに対して検証依頼中】の際は、true
     *           ②【①に該当しない】際は、false
     */
    private boolean isWorkflowCheckerStatus() {
        return WorkflowProcessStatus.REQUEST_FOR_CHECK.equals(workflow.getWorkflowProcessStatus())
                 || WorkflowProcessStatus.UNDER_CONSIDERATION.equals(workflow
                                                        .getWorkflowProcessStatus());
    }

    /**
     * Approverワークフローステータス制御.
     * @return Approverワークフローステータス制御
     *         ※①【Approverに対して承認依頼中】か【Checker/Approverがコレポン文書を更新】の際は、true
     *           ②【①に該当しない】際は、false
     */
   private boolean isWorkflowApproverStatus() {
       return WorkflowProcessStatus.REQUEST_FOR_APPROVAL.equals(workflow.getWorkflowProcessStatus())
                 || WorkflowProcessStatus.UNDER_CONSIDERATION.equals(workflow
                                                        .getWorkflowProcessStatus());
   }

    /**
     * 承認フロー情報を設定する.
     * @param workflow 承認フロー情報
     */
    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
     * 承認フロー情報を取得する.
     * @return workflow
     */
    public Workflow getWorkflow() {
        return workflow;
    }

    /**
     * 承認フロー表示制御条件を設定する.
     * @param view 表示制御
     */
    public void setView(boolean view) {
        this.view = view;
    }

    /**
     * 承認フロー表示制御条件を取得する.
     * @return view
     */
    public boolean isView() {
        return view;
    }

    /**
     * 承認フロー更新制御条件を設定する.
     * @param update 更新制御
     */
    public void setUpdate(boolean update) {
        this.update = update;
    }

    /**
     * 承認フロー更新制御条件を取得する.
     * @return update
     */
    public boolean isUpdate() {
        return update;
    }

    /**
     * 承認フローCheckApprove更新制御条件を設定する.
     * @param checkApprove CheckApprove更新制御
     */
    public void setCheckApprove(boolean checkApprove) {
        this.checkApprove = checkApprove;
    }

    /**
     * 承認フローCheckApprove更新制御条件を取得する.
     * @return checkApprove
     */
    public boolean isCheckApprove() {
        return checkApprove;
    }

    /**
     * 承認フロー コメント、ボタン制御条件を設定する.
     * @param verification コメント、ボタン制御
     */
    public void setVerification(boolean verification) {
        this.verification = verification;
    }

    /**
     * 承認フロー コメント、ボタン制御条件を取得する.
     * @return verification
     */
    public boolean isVerification() {
        return verification;
    }

    /**
     * 承認フロー 変更前コメントを設定する.
     * @param comment コメント
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * 承認フロー 変更前コメントを取得する.
     * @return comment
     */
    public String getComment() {
        return comment;
    }
}
