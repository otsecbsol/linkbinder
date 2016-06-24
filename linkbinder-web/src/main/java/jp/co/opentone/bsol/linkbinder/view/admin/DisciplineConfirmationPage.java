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
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 部門情報入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class DisciplineConfirmationPage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8186671266016330700L;

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
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 部門情報.
     */
    @Transfer
    private Discipline discipline;

    /**
     * 部門コード.
     */
    @Transfer
    private String code;

    /**
     * 部門名.
     */
    @Transfer
    private String name;

    /**
     * 前画面に、この画面から戻ったことを通知するフラグ.
     */
    @Transfer
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
    public DisciplineConfirmationPage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 部門情報画面へ遷移.
     * @return 遷移先画面ID
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl(String.format("discipline?id=%s", id), isLoginProject());
        }
        return null;
    }

    /**
     * 部門情報入力画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        back = true;
        setTransferNext(true);
        return toUrl("disciplineEdit");
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
        private static final long serialVersionUID = -3425482306420885209L;
        /** アクション発生元ページ. */
        private DisciplineConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(DisciplineConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchDisciplineCondition.class);
            initializeCheck();
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        /**
         * 初期化アクション時のチェックを行う.
         * @throws ServiceAbortException 初期画面表示失敗
         */
        private void initializeCheck() throws ServiceAbortException {
            page.checkProjectSelected();
            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 不正アクセス防止
            if (page.discipline == null) {
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
        private static final long serialVersionUID = -6206742302850111676L;
        /** アクション発生元ページ. */
        private DisciplineConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(DisciplineConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.id = page.service.save(page.discipline);

            page.setNextPageMessage(ApplicationMessageCode.DISCIPLINE_SAVED);
        }
    }
}
