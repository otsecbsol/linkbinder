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

import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 会社情報入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CompanyEditPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6458471404051455580L;

    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "会社新規登録";

    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "会社更新";

    /**
     * 会社情報サービス.
     */
    @Resource
    private CompanyService service;

    /**
     * プロジェクトID.
     * <p>
     * sessionにセットされたプロジェクトID. マスタ管理ならばnull
     * </p>
     */
    @Transfer
    private String projectId;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 表示タイトル.
     */
    @Transfer
    private String title;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
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
//    @SkipValidation("#{!companyEditPage.nextAction}")
    @Transfer
    @Required
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String code;

    /**
     * 会社名.
     */
//    @SkipValidation("#{!companyEditPage.nextAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 役割.
     */
//    @SkipValidation("#{!companyEditPage.nextAction}")
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 50)
    //CHECKSTYLE:ON
    private String role;

    /**
     * 確認画面から戻ってきた場合はtrue.
     */
    private boolean back;
    /**
     * 検索条件. 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyEditPage() {
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:next")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 会社情報入力確認画面へ遷移 （validateチェック）.
     * @return 遷移先画面ID
     */
    public String next() {
        if (handler.handleAction(new ValidateAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl("companyConfirmation", isLoginProject());
        }
        return null;
    }

    /**
     * 一覧画面へ遷移する.
     * @return 一覧画面
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        return toUrl("companyIndex", isLoginProject());
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
     * 現在のリクエストがnextアクションによって発生した場合はtrue.
     * @return nextアクションの場合true
     */
    public boolean isNextAction() {
        return isActionInvoked("form:next");
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
        private static final long serialVersionUID = 422383166433844382L;
        /** アクション発生元ページ. */
        private CompanyEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CompanyEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.initialDisplaySuccess = false;
            page.previousCondition = page.getPreviousSearchCondition(SearchCompanyCondition.class);
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            if (!page.isBack()) {
                clearCompany();
                if (page.id == null) {
                    // 新規登録
                    page.title = NEW;
                    // ProjectAdminで登録はできない
                    if (page.projectId != null) {
                        throw new ServiceAbortException(MessageCode.E_INVALID_PARAMETER);
                    }
                } else {
                    // 更新
                    page.title = UPDATE;
                    page.company = page.service.find(page.id);
                    page.code = page.company.getCompanyCd();
                    page.name = page.company.getName();
                    page.role = page.company.getRole();
                }
            }
            page.initialDisplaySuccess = true;
        }

        private void clearCompany() {
            page.company = null;
            page.code = null;
            page.name = null;
            page.role = null;
        }
    }

    /**
     * 入力値検証アクション.
     * @author opentone
     */
    static class ValidateAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1426272425041232518L;
        /** アクション発生元ページ. */
        private CompanyEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ValidateAction(CompanyEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setCompanyInfo();
            if (page.service.validate(page.company)) {
                page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
            }
        }

        /**
         * 会社情報を作成する.
         */
        private void setCompanyInfo() {
            if (page.company == null) {
                page.company = new Company();
            }
            page.company.setCompanyCd(page.code);
            page.company.setName(page.name);
            if (page.projectId != null) {
                page.company.setProjectId(page.projectId);
                page.company.setRole(page.role);
            }
        }
    }
}
