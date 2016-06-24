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
package jp.co.opentone.bsol.linkbinder.view.correspon.module;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowIndex;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.event.CorresponIssued;
import jp.co.opentone.bsol.linkbinder.event.CorresponWorkflowStatusChanged;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.UserRoleHelper;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;

/**
 * コレポン文書表示画面／承認・検証ダイアログ用モジュールクラス.
 * @author opentone
 */
@Component
@Scope("request")
public class VerificationModule extends DefaultModule {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1644381607325552729L;

    /**
     * 承認フローサービス.
     */
    @Resource
    private CorresponWorkflowService corresponWorkflowService;

    /**
     * Role判定クラス.
     */
    @Resource
    private UserRoleHelper helper;

    /**
     * 空のインスタンスを生成する.
     */
    public VerificationModule() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     * @return null
     */
    @Override
    public String initialize() {
        if (serviceActionHandler.handleAction(new InitializeAction(page, this))) {
            page.setVerificationDisplay(true);
        }
        return null;
    }

    /**
     * Checkerによる承認を行う.
     * @return null
     */
    public String check() {
        if (!serviceActionHandler.handleAction(new CheckAction(page, this))) {
            return null;
        }
        page.setVerificationDisplay(false);
        String backPage = page.getBackPage();
        return String.format("%s?afterAction=true&sessionSort=1&sessionPageNo=1",
            StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
    }

    /**
     * Approverによる承認を行う.
     * @return null
     */
    public String approve() {
        if (!serviceActionHandler.handleAction(new ApproveAction(page, this))) {
            return null;
        }
        page.setVerificationDisplay(false);
        String backPage = page.getBackPage();
        return String.format("%s?afterAction=true&sessionSort=1&sessionPageNo=1",
            StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
    }

    /**
     * 否認する.
     * @return null
     */
    public String deny() {
        if (!serviceActionHandler.handleAction(new DenyAction(page, this))) {
            return null;
        }
        page.setVerificationDisplay(false);
        String backPage = page.getBackPage();
        return String.format("%s?afterAction=true&sessionSort=1&sessionPageNo=1",
            StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
    }

    /**
     * 承認・検証をキャンセルする.
     * @return 実行結果
     */
    @Override
    public String cancel() {
        page.setVerificationDisplay(false);
        page.setScrollPosition(null);
        page.setComment(null);
        clearWorkflowComment();
        return null;
    }

    /**
     * 検証・承認ダイアログにて入力されたコメントを削除する.
     */
    private void clearWorkflowComment() {
        List<WorkflowIndex> workflowIndexes = page.getWorkflowIndex();
        for (WorkflowIndex workflowIndex : workflowIndexes) {
            if (workflowIndex.isVerification() && (page.isCheck() || page.isApprove())) {
                workflowIndex.getWorkflow().setCommentOn(workflowIndex.getComment());
            }
        }
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6835875820554409691L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private VerificationModule module;
        /**
         * リストTRタグ クラス制御 odd.
         */
        private static final String ODD = "odd";
        /**
         * リストTRタグ クラス制御 even.
         */
        private static final String EVEN = "even";
        /**
         * リストTRタグ クラス制御 role.
         */
        private static final String ROLE = "role";

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponPage page, VerificationModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // このページの起動元からコレポン文書のIDが指定されていなければならない
            if (page.getId() == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            // 表示用承認フローにPrepererをセットする
            page.setWorkflow(page.createDisplayWorkflowList(page.getCorrespon()));
            // 管理者権限を持つユーザーの場合は全ボタン押下可能
            if ((page.isSystemAdmin()
                        || page.isProjectAdmin(page.getCurrentProject().getProjectId())
                        || page.isGroupAdmin(page.getCorrespon().getFromCorresponGroup().getId()))
                    && !(WorkflowStatus.DENIED.equals(page.getCorrespon().getWorkflowStatus())
                            || WorkflowStatus.ISSUED.equals(
                                    page.getCorrespon().getWorkflowStatus()))) {
                // 画面表示用のオブジェクトに変換する
                page.setWorkflowIndex(createWorkflowIndexList(true));
                setAdminButton();
            } else {
                // 画面表示用のオブジェクトに変換する
                page.setWorkflowIndex(createWorkflowIndexList(false));
                setRoleButton();
            }
        }

        /**
         * 役割単位にコメント、ボタンの制御を行う.
         */
        private void setRoleButton() {
            if (module.helper.isWorkflowChecker(page.getCorrespon(), page.getCurrentUser())) {
                setCheckerButton();
            } else if (module.helper.isWorkflowApprover(
                    page.getCorrespon(), page.getCurrentUser())) {
                setApproverButton();
            } else {
                setProhibitButton();
            }
        }

        /**
         * 画面表示用のワークフロー一覧リストを作成する.
         * @param admin
         *            Admin権限有無
         * @return ワークフロー一覧リスト
         */
        private List<WorkflowIndex> createWorkflowIndexList(boolean admin) {
            List<Workflow> workflow = page.getWorkflow();
            List<WorkflowIndex> list = new ArrayList<WorkflowIndex>();
            String[] styleClasses = new String[workflow.size()];
            for (int i = 0; i < workflow.size(); i++) {
                Workflow w = workflow.get(i);
                WorkflowIndex index =
                        new WorkflowIndex(w,
                                          page.getCorrespon().getWorkflowStatus(),
                                          page.getCorrespon().getCorresponType(),
                                          admin, page.getCurrentUser().getEmpNo());

                styleClasses[i] = getStyleClass(i, w);
                list.add(index);
            }

            page.setTrClassId(StringUtils.join(styleClasses, ','));
            return list;
        }

        private String getStyleClass(int index, Workflow w) {
            //  Preparer
            if (index == 0) {
                return getStyleClass(index);
            }

            //  承認状態が検証中かつ、このワークフローユーザーの承認作業状態が検証中であれば
            //  行ハイライトのスタイルシートクラスを返す
            switch (page.getCorrespon().getWorkflowStatus()) {
            case DRAFT:
            case DENIED:
            case ISSUED:
                return getStyleClass(index);
            default:
                switch (w.getWorkflowProcessStatus()) {
                case REQUEST_FOR_CHECK:
                case UNDER_CONSIDERATION:
                case REQUEST_FOR_APPROVAL:
                    return ROLE;
                default:
                    return getStyleClass(index);
                }
            }
        }

        private String getStyleClass(int index) {
            int no = index + 1;
            return (no % 2 == 0) ? EVEN : ODD;
        }

        /**
         * ログインユーザがCheckerになっているワークフローを取得する.
         * @return ワークフロー
         */
        private Workflow searchUserCheck() {
            List<Workflow> workflow = page.getWorkflow();
            int size = workflow.size();
            for (int i = 0; i < size; i++) {
                Workflow wf = (Workflow) workflow.get(i);
                if (wf.isChecker()
                        && wf.getUser().getUserId().equals(page.getCurrentUser().getUserId())) {
                    return wf;
                }
            }
            return null;
        }

        /**
         * ログインユーザがApproverになっているワークフローを取得する.
         * @return ワークフロー
         */
        private Workflow searchUserApprove() {
            List<Workflow> workflow = page.getWorkflow();
            int size = workflow.size();
            for (int i = 0; i < size; i++) {
                Workflow wf = (Workflow) workflow.get(i);
                if (wf.isApprover()
                        && wf.getUser().getUserId().equals(page.getCurrentUser().getUserId())) {
                    return wf;
                }
            }
            return null;
        }

        /**
         * Checker用のボタン押下設定をする.
         */
        private void setCheckerButton() {
            page.setUsersWorkflow(searchUserCheck());
            WorkflowProcessStatus status = page.getUsersWorkflow().getWorkflowProcessStatus();

            if (WorkflowProcessStatus.REQUEST_FOR_CHECK.equals(status)
                    || WorkflowProcessStatus.UNDER_CONSIDERATION.equals(status)) {
                page.setCheck(true);
                page.setApprove(false);
                page.setDeny(true);
            } else {
                page.setCheck(false);
                page.setApprove(false);
                page.setDeny(false);
            }
        }

        /**
         * Approver用のボタン押下設定をする.
         */
        private void setApproverButton() {
            page.setUsersWorkflow(searchUserApprove());
            WorkflowProcessStatus status = page.getUsersWorkflow().getWorkflowProcessStatus();

            if (WorkflowProcessStatus.REQUEST_FOR_APPROVAL.equals(status)
                    || WorkflowProcessStatus.UNDER_CONSIDERATION.equals(status)) {
                page.setCheck(false);
                page.setApprove(true);
                page.setDeny(true);
            } else {
                page.setCheck(false);
                page.setApprove(false);
                page.setDeny(false);
            }
        }

        /**
         * 処理不可のボタン押下設定をする.
         */
        private void setProhibitButton() {
            page.setCheck(false);
            page.setApprove(false);
            page.setDeny(false);
        }

        /**
         * 管理者用のボタン押下設定をする.
         */
        private void setAdminButton() {
            page.setCheck(true);
            page.setApprove(true);
            page.setDeny(true);
        }
    }

    /**
     * Checker承認アクション.
     * @author opentone
     */
    static class CheckAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7146678477137176616L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private VerificationModule module;

        public CheckAction(CorresponPage page, VerificationModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            WorkflowStatus before = page.getCorrespon().getWorkflowStatus();
            Workflow workflow = page.getWorkflow().get(page.getCurrentDataModel().getRowIndex());
            module.corresponWorkflowService.check(page.getCorrespon(), workflow);

            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_CHECKED);

            WorkflowStatus after = page.getCorrespon().getWorkflowStatus();
            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponWorkflowStatusChanged(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId(),
                                        before,
                                        after));
        }
    }

    /**
     * Approver承認アクション.
     * @author opentone
     */
    static class ApproveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 2168123225357476629L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private VerificationModule module;

        public ApproveAction(CorresponPage page, VerificationModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Workflow workflow = page.getWorkflow().get(page.getCurrentDataModel().getRowIndex());
            module.corresponWorkflowService.approve(page.getCorrespon(), workflow);

            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_APPROVED);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponIssued(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
        }
    }

    /**
     * 否認アクション.
     * @author opentone
     */
    static class DenyAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4676214365666027713L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private VerificationModule module;

        public DenyAction(CorresponPage page, VerificationModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            WorkflowStatus before = page.getCorrespon().getWorkflowStatus();
            Workflow workflow = page.getWorkflow().get(page.getCurrentDataModel().getRowIndex());
            module.corresponWorkflowService.deny(page.getCorrespon(), workflow);

            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_DENIED);

            WorkflowStatus after = page.getCorrespon().getWorkflowStatus();
            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponWorkflowStatusChanged(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId(),
                                        before,
                                        after));
        }
    }
}
