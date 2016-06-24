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

import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * カスタムフィールド項目確認画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CustomFieldConfirmationPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 979249362345585988L;

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
     * Project Custom Field ID.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long projectCustomFieldId;

    /**
     * カスタムフィールド.
     * <p>
     * 新規登録ならばnull
     * </p>
     */
    @Transfer
    private CustomField customField;

    /**
     * カスタムフィールドラベル.
     */
    @Transfer
    private String label;

    /**
     * 表示順.
     */
    @Transfer
    private String orderNo;

    /**
     * カスタムフィールド値.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValues;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * カスタムフィールド値表示判定値.
     */
    @Transfer
    private boolean initialValuesSuccess = false;

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
    public CustomFieldConfirmationPage() {
    }

    /**
     * 画面を初期化する.
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * カスタムフィールド画面へ遷移.
     * @return 遷移先画面ID
     */
    public String save() {
        if (handler.handleAction(new SaveAction(this))) {
            setNextSearchCondition(previousCondition);
            return toUrl(String.format("customField?id=%s", id), isLoginProject());
        }
        return null;
    }

    /**
     * カスタムフィールド入力画面へ遷移.
     * @return 遷移先画面ID
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        back = true;
        setTransferNext(true);
        return toUrl("customFieldEdit", isLoginProject());
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
     * @param projectId
     *            プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 画面表示タイトルを返す.
     * @return 画面表示タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * 画面表示タイトルを設定する.
     * @param title
     *            画面表示タイトル
     */
    public void setTitle(String title) {
        this.title = title;
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
     * @param id
     *            ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * カスタムフィールドラベルを返す.
     * @return カスタムフィールドラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * カスタムフィールドラベルを設定する.
     * @param label
     *            カスタムフィールドラベル
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * カスタムフィールドを設定する.
     * @param customField
     *            カスタムフィールド
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
     * 表示順を返す.
     * @return 表示順
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 表示順を設定する.
     * @param orderNo
     *            表示順
     */
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * カスタムフィールド値を返す.
     * @return カスタムフィールド値
     */
    public List<CustomFieldValue> getCustomFieldValues() {
        return customFieldValues;
    }

    /**
     * カスタムフィールド値を設定する.
     * @param customFieldValues
     *            カスタムフィールド値
     */
    public void setCustomFieldValues(List<CustomFieldValue> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

    /**
     * 初期画面表示判定値を返す.
     * @return 初期画面表示判定値
     */
    public boolean isInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期画面表示判定値を設定する.
     * @param initialDisplaySuccess
     *            初期画面表示判定値
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * initialValuesSuccessを設定します.
     * @param initialValuesSuccess the initialValuesSuccess to set
     */
    public void setInitialValuesSuccess(boolean initialValuesSuccess) {
        this.initialValuesSuccess = initialValuesSuccess;
    }

    /**
     * initialValuesSuccessを取得します.
     * @return the initialValuesSuccess
     */
    public boolean isInitialValuesSuccess() {
        return initialValuesSuccess;
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
     * @param projectCustomFieldId the projectCustomFieldId to set
     */
    public void setProjectCustomFieldId(Long projectCustomFieldId) {
        this.projectCustomFieldId = projectCustomFieldId;
    }

    /**
     * @return the projectCustomFieldId
     */
    public Long getProjectCustomFieldId() {
        return projectCustomFieldId;
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
        private static final long serialVersionUID = 5817886434375347787L;
        /** アクション発生元ページ. */
        private CustomFieldConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CustomFieldConfirmationPage page) {
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
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 不正アクセス防止
            if (page.customField == null) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }

            if (page.customField != null) {
                page.customFieldValues = page.customField.getCustomFieldValues();

                page.initialValuesSuccess =
                        page.customField.getCustomFieldValues() != null
                            && !page.customField.getCustomFieldValues().isEmpty();
            }
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
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
        private static final long serialVersionUID = 5455556693763097920L;
        /** アクション発生元ページ. */
        private CustomFieldConfirmationPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SaveAction(CustomFieldConfirmationPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Long newId = page.service.save(page.customField);
            page.id = newId;
            page.setNextPageMessage(ApplicationMessageCode.CUSTOM_FIELD_SAVED);
        }
    }

}
