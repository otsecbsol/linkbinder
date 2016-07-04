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
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * カスタムフィールド項目表示画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CustomFieldPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7650446992293787767L;

    /**
     * カスタムフィールドサービス.
     */
    @Resource
    private CustomFieldService service;

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
     * カスタムフィールド.
     */
    @Transfer
    private CustomField customField;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldPage() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、カスタムフィールドを表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * カスタムフィールド一覧画面に遷移する.
     * @return カスタムフィールド一覧画面
     */
    public String goIndex() {
        setNextSearchCondition(previousCondition);
        return toUrl("customFieldIndex", isLoginProject());
    }

    /**
     * プロジェクトIDを返す.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * プロジェクトIDを設定する.
     * @param projectId プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * IDを返す.
     * @return ID
     */
    public Long getId() {
        return id;
    }

    /**
     * IDを設定する.
     * @param id ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * カスタムフィールドを設定する.
     * @param customField カスタムフィールド
     */
    public void setCustomField(CustomField customField) {
        this.customField = customField;
    }

    /**
     * カスタムフィールドを返す.
     * @return カスタムフィールド
     */
    public CustomField getCustomField() {
        return customField;
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
     * 初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 8395481352052528969L;
        /** アクション発生元ページ. */
        private CustomFieldPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CustomFieldPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchCustomFieldCondition.class);
            // このページの起動元からカスタムフィールドのIDが指定されていなければならない
            if (page.id == null) {
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();

            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            page.customField = page.service.find(page.id);
            page.customField.setCustomFieldValues(page.service.findCustomFieldValue(page.id));
        }
    }

}
