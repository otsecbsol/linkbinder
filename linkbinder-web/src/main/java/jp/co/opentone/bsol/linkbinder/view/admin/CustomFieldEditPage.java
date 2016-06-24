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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Numeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.CustomFieldValue;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.validator.CustomFieldValueValidator;

/**
 * カスタムフィールド入力画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CustomFieldEditPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -9118521812910293845L;

    /**
     * 表題（新規登録）ラベル.
     */
    private static final String NEW = "その他項目新規作成";

    /**
     * 表題（更新）ラベル.
     */
    private static final String UPDATE = "その他項目更新";

    /**
     * タブ.
     */
    private static final String TAB = "\t";

    /**
     * 会社情報サービス.
     */
    @Resource
    private CustomFieldService service;

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
     * Project Custom Field ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long projectCustomFieldId;

    /**
     * ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. 新規登録ならばnull
     * </p>
     */
    @Transfer
    private Long id;

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
//    @SkipValidation("#{!customFieldEditPage.nextAction}")
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String label;

    /**
     * 表示順.
     */
//    @SkipValidation("#{!customFieldEditPage.nextAction}")
    @Transfer
    @Required
    @Numeric
    //CHECKSTYLE:OFF
    @Length(max = 5)
    //CHECKSTYLE:ON
    private String orderNo;

    /**
     * カスタムフィールド値.
     */
    @Transfer
    private List<CustomFieldValue> customFieldValues;

    /**
     * カスタムフィールド値.
     */
    private String valueList;

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
    public CustomFieldEditPage() {
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
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、カスタムフィールドを表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * カスタムフィールド入力確認画面へ遷移 （validateチェック）.
     * @return 遷移先画面ID
     */
    public String next() {
        if (handler.handleAction(new ValidateAction(this))) {
            setNextSearchCondition(previousCondition);
            setTransferNext(true);
            return toUrl("customFieldConfirmation", isLoginProject());
        }
        return null;
    }

    /**
     * 一覧画面へ遷移する.
     * @return 一覧画面
     */
    public String back() {
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
     * 表示タイトルを返す.
     * @return 表示タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * 表示タイトルを設定する.
     * @param title 表示タイトル
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
     * @param id ID
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
     * @param label カスタムフィールドラベル
     */
    public void setLabel(String label) {
        this.label = label;
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
     * 表示順を返す.
     * @return 表示順
     */
    public String getOrderNo() {
        return orderNo;
    }

    /**
     * 表示順を設定する.
     * @param orderNo 表示順
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
     * @param customFieldValues カスタムフィールド値
     */
    public void setCustomFieldValues(List<CustomFieldValue> customFieldValues) {
        this.customFieldValues = customFieldValues;
    }

    /**
     * 初期画面表示判定値を返す.
     * @return 初期画面表示判定値
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 初期画面表示判定値を設定する.
     * @param initialDisplaySuccess 初期画面表示判定値
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * カスタムフィールド値を設定する.
     * @param valueList カスタムフィールド値
     */
    public void setValueList(String valueList) {
        this.valueList = valueList;
    }

    /**
     * カスタムフィールド値を返す.
     * @return カスタムフィールド値
     */
    public String getValueList() {
        return valueList;
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
     * 画面表示用に取得したカスタムフィールド設定値を加工する.
     * @param customFieldValueList カスタムフィールド設定値
     * @return カスタムフィールド設定値
     */
    private String createValueList(List<CustomFieldValue> customFieldValueList) {
        StringBuilder values = new StringBuilder();
        for (int i = 0; i < customFieldValueList.size(); i++) {
            values.append(customFieldValueList.get(i).getValue());
            if (i != customFieldValueList.size() - 1) {
                values.append(TAB);
            }
        }
        return values.toString();
    }

    /**
     * を検証する.
     * <p>
     * このメソッドはJSPのValidationフェーズで呼び出される.
     * </p>
     * @param context FacesContext
     * @param component 入力コンポーネント
     * @param value 入力値
     */
    public void validateValues(FacesContext context, UIComponent component, Object value) {
        if (!isNextAction()) {
            return;
        }
        CustomFieldValueValidator validator = new CustomFieldValueValidator(TAB);
        validator.validate(context, component, value);
    }

    private List<CustomFieldValue> createValueList() {
        List<CustomFieldValue> values = new ArrayList<CustomFieldValue>();
        if (StringUtils.isNotEmpty(valueList)) {
            String[] list = valueList.split(TAB);
            for (String value : list) {
                // 空文字は無視
                if (StringUtils.isNotEmpty(value)) {
                    CustomFieldValue cf = new CustomFieldValue();
                    cf.setValue(value);
                    values.add(cf);
                }
            }
        }
        return values;
    }

    /**
     * 現在のリクエストがnextアクションによって発生した場合はtrue.
     * @return nextアクションの場合true
     */
    public boolean isNextAction() {
        return isActionInvoked("form:next");
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
        private static final long serialVersionUID = -1734942988282487293L;
        /** アクション発生元ページ. */
        private CustomFieldEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CustomFieldEditPage page) {
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
                    page.getPreviousSearchCondition(SearchCustomFieldCondition.class);
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();
            // 権限チェック
            if (!page.isSystemAdmin() && !page.isProjectAdmin(page.projectId)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            // 初期表示項目を設定
            setUpInitializeValues();
            // 初期画面表示成功
            page.initialDisplaySuccess = true;
        }

        /**
         * 初期表示項目を設定する.
         * @throws ServiceAbortException 初期表示設定失敗
         */
        private void setUpInitializeValues() throws ServiceAbortException {
            if (!page.isBack()) {
                clearCustomField();
                if (page.id == null) {
                    // 新規登録
                    page.title = NEW;
                } else {
                    // 更新
                    page.title = UPDATE;
                    page.customField = page.service.find(page.id);
                    page.customFieldValues = page.service.findCustomFieldValue(page.id);
                    page.label = page.customField.getLabel();
                    if (page.customField.getOrderNo() != null) {
                        page.orderNo = page.customField.getOrderNo().toString();
                    } else {
                        page.orderNo = null;
                    }
                }
            }
            if (page.customFieldValues != null) {
                page.valueList = page.createValueList(page.customFieldValues);
            }
        }

        private void clearCustomField() {
            page.customField = null;
            page.customFieldValues = null;
            page.valueList = null;
            page.label = null;
            page.orderNo = null;
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
        private static final long serialVersionUID = 8526952656171395014L;
        /** アクション発生元ページ. */
        private CustomFieldEditPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ValidateAction(CustomFieldEditPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setCustomFieldInfo();
            if (page.service.validate(page.customField)) {
                page.setNextPageMessage(ApplicationMessageCode.CONTENT_WILL_BE_SAVED);
            }
        }

        /**
         * カスタムフィールドを作成する.
         */
        private void setCustomFieldInfo() {
            if (page.customField == null) {
                page.customField = new CustomField();
            }

            List<CustomFieldValue> customFieldValueNext = page.createValueList();
            page.customField.setLabel(page.label);
            if (page.orderNo != null) {
                page.customField.setOrderNo(Long.valueOf(page.orderNo));
            } else {
                page.customField.setOrderNo(null);
            }
            page.customField.setProjectId(page.projectId);
            page.customField.setCustomFieldValues(customFieldValueNext);
        }
    }
}
