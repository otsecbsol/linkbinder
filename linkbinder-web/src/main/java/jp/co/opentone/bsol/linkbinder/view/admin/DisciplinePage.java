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

import jp.co.opentone.bsol.framework.core.message.MessageCode;
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
public class DisciplinePage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8710449544916087169L;

    /**
     * 部門情報サービス.
     */
    @Resource
    private DisciplineService service;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
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
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public DisciplinePage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 部門一覧画面に遷移する.
     * @return 拠点一覧画面
     */
    public String goIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl("disciplineIndex");
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
     * previousConditionを設定します.
     * @param previousCondition the previousCondition to set
     */
    public void setPreviousCondition(AbstractCondition previousCondition) {
        this.previousCondition = previousCondition;
    }

    /**
     * previousConditionを取得します.
     * @return the previousCondition
     */
    public AbstractCondition getPreviousCondition() {
        return previousCondition;
    }


    /**
     * 保存アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -8566357631735572678L;
        /** アクション発生元ページ. */
        private DisciplinePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(DisciplinePage page) {
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
            page.checkProjectSelected();
            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            // このページの起動元から部門情報のIDが指定されていなければならない
            if (page.id == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }

            page.discipline = page.service.find(page.id);
        }
    }
}
