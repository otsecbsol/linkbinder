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
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 会社情報画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CompanyPage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6187869532903020213L;

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
    private String projectId;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 会社情報.
     */
    @Transfer
    private Company company;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public CompanyPage() {
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
     * 一覧画面に遷移する.
     * @return 一覧画面
     */
    public String goIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl("companyIndex", isLoginProject());
    }

    /**
     * 会社 - メンバー設定画面に遷移する.
     * @return 会社 - メンバー設定画面
     */
    public String editMember() {
        setNextSearchCondition(previousCondition);
        return toUrl(String.format("companyEditMember?id=%s&backPage=company", id),
                isLoginProject());
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
     * 初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -2956121900002555966L;
        /** アクション発生元ページ. */
        private CompanyPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CompanyPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition = page.getPreviousSearchCondition(SearchCompanyCondition.class);
            // このページの起動元からコレポン文書のIDが指定されていなければならない
            if (page.id == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();

            // 権限チェック
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            page.company = page.service.find(page.id);
        }
    }
}
