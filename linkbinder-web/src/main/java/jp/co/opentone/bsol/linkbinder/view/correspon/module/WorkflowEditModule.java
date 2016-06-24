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
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import jp.co.opentone.bsol.linkbinder.view.util.WorkflowListUtil;

/**
 * コレポン文書表示画面／承認フロー編集ダイアログ用モジュールクラス.
 * @author opentone
 */
@Component
@Scope("request")
public class WorkflowEditModule extends DefaultModule {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3250980307442790803L;

    /**
     * 活動単位「ALL」を表す値.
     */
    private static final Long ALL = -1L;

    /**
     * ワークフローに設定可能なユーザーの人数.
     * <p>
     * Preparer(1) + Checker(10) + Approver(1)
     */
    //CHECKSTYLE:OFF
    private static final int WORKFLOW_NO_SIZE = 1 + 10 + 1;
    //CHECKSTYLE:ON

    /**
     * ワークフローNo.
     */
    private static final Long[] WORKFLOW_NOS;

    static {
        WORKFLOW_NOS = new Long[WORKFLOW_NO_SIZE];
        for (int i = 0; i < WORKFLOW_NOS.length; i++) {
            WORKFLOW_NOS[i] = Long.valueOf(i + 1);
        }
    }

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * 承認フローサービス.
     */
    @Resource
    private CorresponWorkflowService corresponWorkflowService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * 空のインスタンスを生成する.
     */
    public WorkflowEditModule() {
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
            page.setWorkflowEditDisplay(true);
        }
        return null;
    }

    /**
     * 活動単位(グループ)選択時の処理.
     * @return null
     */
    public String changeGroup() {
        serviceActionHandler.handleAction(new ChangeGroupAction(page, this));
        return null;
    }

    /**
     * 承認フローを保存する.
     * @return null
     */
    @Override
    public String save() {
        if (serviceActionHandler.handleAction(new SaveAction(page, this))) {
            page.setWorkflowEditDisplay(false);
            page.setScrollPosition(null);
        }
        return null;
    }

    /**
     * 承認フロー編集をキャンセルする.
     * @return 実行結果
     */
    @Override
    public String cancel() {
        page.setWorkflowEditDisplay(false);
        page.setScrollPosition(null);
        return null;
    }

    /**
     * 承認フローにChecker/Approverを追加する.
     * @return null
     */
    public String addWorkflow() {
        serviceActionHandler.handleAction(new AddAction(page, this));
        return null;
    }

    /**
     * 選択したレコードを承認フローから削除する.
     * @return null
     */
    @Override
    public String delete() {
        // ワークフローリストから選択したワークフローを削除する
        List<Workflow> workflowForEditView = page.getWorkflowForEditView();
        workflowForEditView.remove(page.getCurrentWorkflowModel().getRowIndex());

        List<Workflow> workflow = page.getWorkflow();
        workflow.remove(page.getCurrentWorkflowModel().getRowIndex() - 1);

        // ワークフローナンバーを振り直す
        WorkflowListUtil.renumberWorkflowNo(workflowForEditView);
        WorkflowListUtil.renumberWorkflowNo(workflow);
        return null;
    }

    /**
     * 設定してある承認フローを全て削除.
     * @return 自画面遷移の為null
     */
    public String allDelete() {
        List<Workflow> workflowForEditView = page.getWorkflowForEditView();
        if (workflowForEditView != null) {
            workflowForEditView.clear();
            workflowForEditView.add(getPreparer());
            WorkflowListUtil.renumberWorkflowNo(workflowForEditView);
        }

        List<Workflow> workflow = page.getWorkflow();
        if (workflow != null) {
            workflow.clear();
            WorkflowListUtil.renumberWorkflowNo(workflow);
        }
        return null;
    }

    /**
     * 承認フローリスト表示用のPreparerを作成する.
     * @return the Workflow
     */
    private Workflow getPreparer() {
        Workflow w = new Workflow();
        w.setUser(page.getCorrespon().getCreatedBy());
        return w;
    }

    /**
     * 登録されている承認フローテンプレート情報を自動設定する.
     * @return null
     */
    public String apply() {
        serviceActionHandler.handleAction(new ApplyAction(page, this));
        return null;
    }

    /**
     * 指定の承認フローテンプレートを削除する.
     * @return null
     */
    public String deleteTemplate() {
        serviceActionHandler.handleAction(new DeleteTemplateAction(page, this));
        return null;
    }

    /**
     * 承認フローテンプレートを保存する.
     * @return null
     */
    public String saveTemplate() {
        serviceActionHandler.handleAction(new SaveTemplateAction(page, this));
        return null;
    }

    /**
     * ワークフローのコピーを作成する.
     * @param workflows コピー元ワークフロー
     * @throws ServiceAbortException コピー失敗
     */
    private List<Workflow> cloneWorkflow(List<Workflow> workflows) throws ServiceAbortException {
        List<Workflow> cloneWorkflows = new ArrayList<Workflow>();
        for (Workflow w : workflows) {
            cloneWorkflows.add((Workflow) page.cloneToObject(w));
        }
        return cloneWorkflows;
    }

    /**
     * グループ検索条件を作成する.
     * @param projectId プロジェクトID
     * @return SearchCorresponGroupCondition グループ検索条件
     */
    private SearchCorresponGroupCondition createGroupCondition(String projectId) {
        // グループ検索条件をセット
        SearchCorresponGroupCondition scgc = new SearchCorresponGroupCondition();
        scgc.setProjectId(projectId);
        return scgc;
    }

    /**
     * デフォルト活動単位をセットする.
     * @param projectUser プロジェクトユーザー
     */
    private void setDefaultCorresponGroup(ProjectUser projectUser) {
        if (projectUser != null) {
            CorresponGroup coGroup = projectUser.getDefaultCorresponGroup();
            if (coGroup != null) {
                page.setGroupId(coGroup.getId());
            }
        }
        // デフォルト活動単位が取得できない場合
        if (page.getGroupId() == null) {
            // ALLを指定
            page.setGroupId(ALL);
        }
    }

    /**
     * 承認フローの活動単位名を取得する.
     * @return 活動単位名
     * @throws ServiceAbortException 取得失敗
     */
    private CorresponGroup getPrimaryCorresponGroup(User u) throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(page.getCurrentProjectId());
        // プロジェクトユーザーリストを取得する
        List<ProjectUser> projectUserList = userService.search(condition);
        for (ProjectUser pu : projectUserList) {
            if (u.getEmpNo().equals(pu.getUser().getEmpNo())) {
                if (pu.getDefaultCorresponGroup() != null) {
                    return pu.getDefaultCorresponGroup();
                }
            }
        }
        // 活動単位ユーザーリストを取得する
        CorresponGroup g = null;
        List<CorresponGroupUser> listCgu =
                userService.searchCorrseponGroup(page.getCurrentProjectId(), u.getEmpNo());
        if (listCgu != null && !listCgu.isEmpty()) {
             g = listCgu.get(0).getCorresponGroup();
        }
        return g;
    }

    /**
     * 承認フローテンプレートを表示するSelectItemを作成する.
     * @param selectList 承認フローテンプレートユーザーリスト
     */
    private void createSelectTemplate() {
        page.setSelectTemplate(
            viewHelper.createSelectItem(page.getWorkflowTemplateUserList(), "id", "name"));
    }

    /**
     * ワークフローナンバーを表示するSelectItemを作成する.
     */
    private void createSelectWorkflowNo() {
        List<SelectItem> selectWorkflowNo = new ArrayList<SelectItem>();
        for (int i = 1; i < WORKFLOW_NOS.length; i++) {
            selectWorkflowNo.add(new SelectItem(WORKFLOW_NOS[i], String.valueOf(WORKFLOW_NOS[i])));
        }
        page.setSelectWorkflowNo(selectWorkflowNo);
    }

    /**
     * 検証者種別を表示するSelectItemを作成する.
     */
    public void createSelectWorkflowType() {
        page.setSelectWorkflowType(viewHelper.createSelectItem(WorkflowType.values()));
    }

    /**
     * 活動単位を表示するSelectItemを作成する.
     */
    private void createSelectGroup() {
        page.setSelectGroup(viewHelper.createSelectItem(page.getCorresponGroup(), "id", "name"));
        page.getSelectGroup().add(0, new SelectItem(ALL, "*"));
    }

    /**
     * ユーザーを表示するSelectItemを作成する.
     */
    private void createSelectUser() {
        page.setSelectUser(viewHelper.createSelectItem(page.getUser(), "empNo", "labelWithRole"));
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 2426692980632306463L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            checkParameter();
            // コレポン文書から取得したワークフローをいじるのではなく
            // 同じデータをコピーしてつかうように変更する。
            page.setWorkflow(module.cloneWorkflow(page.getCorrespon().getWorkflows()));
            String projectId = page.getCorrespon().getProjectId();
            // 活動単位を取得する
            page.setCorresponGroup(
                    module.corresponGroupService.search(module.createGroupCondition(projectId)));
            // デフォルト活動単位をセット
            module.setDefaultCorresponGroup(page.getCurrentProjectUser());
            // コレポン文書のコピーを作成する
            Correspon cloneCorrespon = (Correspon) page.cloneToObject(page.getCorrespon());
            // ワークフローのコピーをコピーしたコレポン文書に格納する
            cloneCorrespon.setWorkflows(module.cloneWorkflow(page.getWorkflow()));
            // 表示用承認フローにPrepererをセットする
            page.setWorkflowForEditView(page.createDisplayWorkflowList(cloneCorrespon));
            setPrepererRole();
            // デフォルト活動単位に紐付くユーザーを取得する
            getDefaultCorresponGroupUser();
            // 承認フローテンプレート取得
            page.setWorkflowTemplateUserList(
                    module.corresponWorkflowService.searchWorkflowTemplateUser());
            // 承認フローテンプレートを取得する.
            module.createSelectTemplate();
            page.setInitialDisplaySuccess(true);

            if (page.getWorkflowForEditView() != null) {
                WorkflowListUtil.renumberWorkflowNo(page.getWorkflowForEditView());
            }
            if (page.getWorkflow() != null) {
                WorkflowListUtil.renumberWorkflowNo(page.getWorkflow());
            }
        }

        /**
         * PrepererのユーザーRoleをセットする.
         * @throws ServiceAbortException 検索失敗
         */
        private void setPrepererRole() throws ServiceAbortException {
            // Preperer
            User preperer = page.getWorkflowForEditView().get(0).getUser();
            SearchUserCondition condition = new SearchUserCondition();
            condition.setProjectId(page.getCurrentProjectId());
            condition.setEmpNo(preperer.getEmpNo());
            List<ProjectUser> pjUserList = module.userService.search(condition);
            if (pjUserList.size() == 1
                    && pjUserList.get(0).getUser().getEmpNo().equals(preperer.getEmpNo())) {
                    preperer.setRole(
                        pjUserList.get(0).getUser().getRole() == null
                            ? "" : pjUserList.get(0).getUser().getRole());
            }
        }

        /**
         * 必須パラメータのチェックを行う.
         * @throws ServiceAbortException 必須パラメータがない
         */
        private void checkParameter() throws ServiceAbortException {
            if (page.getId() == null) {
                page.setInitialDisplaySuccess(false);
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
        }

        /**
         * デフォルト活動単位に紐付くユーザーを取得する.
         */
        private void getDefaultCorresponGroupUser() {
            module.changeGroup();
            module.createSelectWorkflowNo();
            module.createSelectGroup();
            module.createSelectUser();
            module.createSelectWorkflowType();
        }
    }

    /**
     * 活動単位選択変更アクション.
     * @author opentone
     */
    static class ChangeGroupAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1696724160668219L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ChangeGroupAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Long selectedGroup = page.getGroupId();
            List<User> users = new ArrayList<User>();
            if (selectedGroup.longValue() == WorkflowEditModule.ALL.longValue()) {
                SearchUserCondition c = new SearchUserCondition();
                c.setProjectId(page.getCurrentProjectId());
                List<ProjectUser> projectUsers = module.userService.search(c);
                for (ProjectUser pu : projectUsers) {
                    users.add(pu.getUser());
                }
            } else {
                List<CorresponGroupUser> gusers =
                        module.corresponGroupService.findMembers(selectedGroup);
                for (CorresponGroupUser gu : gusers) {
                    users.add(gu.getUser());
                }
            }
            page.setUser(users);
            module.createSelectUser();
        }

    }

    static class AddAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 3709999354718123851L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public AddAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            User u = new User();
            u.setEmpNo(page.getUserId());
            for (User us : page.getUser()) {
                if (page.getUserId().equals(us.getEmpNo())) {
                    u.setNameE(us.getNameE());
                    u.setRole(us.getRole());
                }
            }
            CorresponGroup g = module.getPrimaryCorresponGroup(u);

            // 役割をセットする
            Workflow w = new Workflow();
            Workflow newWorkflow = new Workflow();
            w.setId(0L);
            w.setWorkflowType(getWorkflowType());
            w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            w.setCorresponId(page.getCorrespon().getId());
            w.setUser(u);
            w.setCorresponGroup(g);

            newWorkflow.setId(0L);
            newWorkflow.setWorkflowType(getWorkflowType());
            newWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
            newWorkflow.setCorresponId(page.getCorrespon().getId());
            newWorkflow.setUser(u);
            newWorkflow.setCorresponGroup(g);

            User loginUser = page.getCurrentUser();
            w.setCreatedBy(loginUser);
            w.setUpdatedBy(loginUser);
            addWorkflowAndWorkflowIndex(w, newWorkflow);

            WorkflowListUtil.renumberWorkflowNo(page.getWorkflowForEditView());
            WorkflowListUtil.renumberWorkflowNo(page.getWorkflow());
        }

        /**
         * 役割を返す.
         * @return 役割
         */
        private WorkflowType getWorkflowType() {
            if (WorkflowType.CHECKER.getValue().equals(page.getRole())) {
                return WorkflowType.CHECKER;
            } else {
                return WorkflowType.APPROVER;
            }
        }

        /**
         * DB保存用、画面表示用の承認フローに追加ユーザーを追加する.
         * @param addWorkflow DB保存用ワークフロー
         * @param addWorkflowIndex 画面表示用ワークフロー
         */
        private void addWorkflowAndWorkflowIndex(Workflow addWorkflow, Workflow addWorkflowIndex) {
            int element = page.getWorkflowNo().intValue() - 1;
            if (page.getWorkflow().size() <= element - 1) {
                page.getWorkflow().add(addWorkflow);
            } else {
                page.getWorkflow().add(element - 1, addWorkflow);
            }
            if (page.getWorkflowForEditView().size() <= element) {
                page.getWorkflowForEditView().add(addWorkflowIndex);
            } else {
                page.getWorkflowForEditView().add(element, addWorkflowIndex);
            }
        }
    }

    /**
     * 承認フロー保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6840600748605191977L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            module.corresponWorkflowService.save(page.getCorrespon(), page.getWorkflow());
            page.reload();
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }
    }

    /**
     * 承認フローテンプレート自動設定アクション.
     * @author opentone
     */
    static class ApplyAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1705425329744059400L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ApplyAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // テンプレートを設定
            page.setWorkflowTemplateList(module.corresponWorkflowService.apply(page.getTemplate()));

            List<Workflow> workflows = new ArrayList<Workflow>();
            for (WorkflowTemplate template : page.getWorkflowTemplateList()) {
                Workflow w = new Workflow();
                w.setId(0L);
                w.setWorkflowProcessStatus(WorkflowProcessStatus.NONE);
                w.setCorresponId(page.getCorrespon().getId());
                w.setUser(template.getUser());
                w.setWorkflowType(template.getWorkflowType());
                w.setWorkflowNo(template.getWorkflowNo());
                User loginUser = page.getCurrentUser();
                w.setCreatedBy(loginUser);
                w.setUpdatedBy(loginUser);
                workflows.add(w);
            }
            // クローン作成
            List<Workflow> cloneWorkflows = module.cloneWorkflow(workflows);

            page.setWorkflow(workflows);
            page.setWorkflowForEditView(
                page.createDisplayWorkflowList(page.getCorrespon().getCreatedBy(),
                    cloneWorkflows));
        }
    }

    /**
     * 承認フローテンプレート削除アクション.
     * @author opentone
     */
    static class DeleteTemplateAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1065628034316342829L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DeleteTemplateAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // テンプレートを削除
            module.corresponWorkflowService.deleteTemplate(page.getTemplate());
            page.setWorkflowTemplateUserList(
                    module.corresponWorkflowService.searchWorkflowTemplateUser());
            module.createSelectTemplate();
            page.setPageMessage(ApplicationMessageCode.WORKFLOW_TEMPLATE_DELETED);

        }
    }

    /**
     * 承認フローテンプレート削除アクション.
     * @author opentone
     */
    static class SaveTemplateAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7846906956039183568L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private WorkflowEditModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public SaveTemplateAction(CorresponPage page, WorkflowEditModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // テンプレートを保存
            module.corresponWorkflowService.saveTemplate(page.getTemplateName(),
                page.getWorkflow(),
                page.getCorrespon());
            page.setWorkflowTemplateUserList(
                    module.corresponWorkflowService.searchWorkflowTemplateUser());
            module.createSelectTemplate();
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }
    }
}
