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
package jp.co.opentone.bsol.linkbinder.view.admin;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 会社情報入力確認画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CompanyConfirmationPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4536610132401986992L;

    /**
     * 会社情報サービス.
     */
    @Resource
    private CompanyService service;

    /**
     * プロジェクトID.
     * <p>
     * マスタ管理ならばnull
     * </p>
     */
    @Transfer
    private String projectId;

    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;

    /**
     * ID.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 会社情報.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Company company;

    /**
     * 会社コード.
     */
    @Transfer
    private String code;

    /**
     * 会社名.
     */
    @Transfer
    private String name;

    /**
     * 役割.
     */
    @Transfer
    private String role;

    /**
     * 前画面に、この画面から戻ったことを通知するフラグ.
     */
    @Transfer
    private boolean back;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyConfirmationPage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 会社情報画面へ遷移.
     * @return 遷移先画面ID
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            setNextSearchCondition(previousCondition);
            return toUrl(String.format("company?id=%s", id), isLoginProject());
        }
        return null;
    }

    /**
     * 会社情報入力画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        back = true;
        setTransferNext(true);
        return toUrl("companyEdit", isLoginProject());
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
     * titleを取得します.
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * titleを設定します.
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * idを取得します.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * idを設定します.
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * companyを取得します.
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * companyを設定します.
     * @param company the company to set
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * codeを取得します.
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * codeを設定します.
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * nameを取得します.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * roleを取得します.
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * roleを設定します.
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * initialDisplaySuccessを取得します.
     * @return the initialDisplaySuccess
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * initialDisplaySuccessを設定します.
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * previousConditionを取得します.
     * @return the previousCondition
     */
    public AbstractCondition getPreviousCondition() {
        return previousCondition;
    }

    /**
     * previousConditionを設定します.
     * @param previousCondition the previousCondition to set
     */
    public void setPreviousCondition(AbstractCondition previousCondition) {
        this.previousCondition = previousCondition;
    }


    /**
     * @param back the back to set
     */
    public void setBack(boolean back) {
        this.back = back;
    }

    /**
     * @return the back
     */
    public boolean isBack() {
        return back;
    }


    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -8416958604162613063L;
        /** アクション発生元ページ. */
        private CompanyConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CompanyConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition = page.getPreviousSearchCondition(SearchCompanyCondition.class);
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();
            initializeCheck();
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        /**
         * 初期画面表示チェックを行う.
         * @throws ServiceAbortException 初期画面表示失敗
         */
        private void initializeCheck() throws ServiceAbortException {
            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 不正アクセス防止
            if (page.id == null && page.company == null) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }
    }

    /**
     * 保存アクション.
     * @author opentone
     */
    static class SaveAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1818987559661793122L;
        /** アクション発生元ページ. */
        private CompanyConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CompanyConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Long newId = page.service.save(page.company);
            page.id = newId;
            page.setNextPageMessage(ApplicationMessageCode.COMPANY_SAVED);
        }
    }
}
