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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 部門情報入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class DisciplineEditPage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8057609824840135445L;

    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "部門新規登録";

    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "部門更新";

    /**
     * 部門情報サービス.
     */
    @Resource
    private DisciplineService service;

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
     * 部門情報.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Discipline discipline;

    /**
     * 部門コード.
     */
//    @SkipValidation("#{!disciplineEditPage.nextAction}")
    @Transfer
    @Required
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String code;

    /**
     * 部門名.
     */
//    @SkipValidation("#{!disciplineEditPage.nextAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 確認画面から戻ってきた場合はtrue.
     */
    private boolean back;
    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public DisciplineEditPage() {
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
     * 部門情報入力確認画面へ遷移 （validateチェック）.
     * @return 遷移先画面ID
     */
    public String next() {
        if (handler.handleAction(new ValidateAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl("disciplineConfirmation");
        }
        return null;
    }

    /**
     * 一覧画面へ遷移する.
     * @return 一覧画面
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        return toUrl("disciplineIndex");
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
     * disciplineを取得します.
     * @return the discipline
     */
    public Discipline getDiscipline() {
        return discipline;
    }

    /**
     * disciplineを設定します.
     * @param discipline the discipline to set
     */
    public void setDiscipline(Discipline discipline) {
        this.discipline = discipline;
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
        private static final long serialVersionUID = -6562506023210887205L;
        /** アクション発生元ページ. */
        private DisciplineEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(DisciplineEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.initialDisplaySuccess = false;
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchDisciplineCondition.class);
            initializeCheck();

            if (!page.isBack()) {
                clearDiscipline();
                if (page.id == null) {
                    // 新規登録
                    page.title = NEW;
                } else {
                    // 更新
                    page.title = UPDATE;
                    page.discipline = page.service.find(page.id);
                    page.code = page.discipline.getDisciplineCd();
                    page.name = page.discipline.getName();
                }
            }
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        private void clearDiscipline() {
            page.discipline = null;
            page.code = null;
            page.name = null;
        }

        /**
         * 初期化アクション時のチェック処理を行う.
         * @throws ServiceAbortException 初期化失敗
         */
        private void initializeCheck() throws ServiceAbortException {
            page.checkProjectSelected();
            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
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
        private static final long serialVersionUID = 1781304314937001473L;
        /** アクション発生元ページ. */
        private DisciplineEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ValidateAction(DisciplineEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setDisciplineInfo();
            if (page.service.validate(page.discipline)) {
                page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
            }
        }

        /**
         * 部門情報を作成する.
         */
        private void setDisciplineInfo() {
            if (page.discipline == null) {
                page.discipline = new Discipline();
            }
            page.discipline.setDisciplineCd(page.code);
            page.discipline.setName(page.name);
            page.discipline.setProjectId(page.getCurrentProjectId());
        }
    }
}
