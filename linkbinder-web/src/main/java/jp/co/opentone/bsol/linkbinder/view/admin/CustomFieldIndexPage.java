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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * カスタムフィールド一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CustomFieldIndexPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -7007426138315063754L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "customfieldindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "customfieldindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * プロジェクトID.
     */
    @Transfer
    private String projectId;

    /**
     * 検索条件-Label.
     */
    @Transfer
    private String scLabel;

    /**
     * CustomFieldService.
     */
    @Resource
    private CustomFieldService customFieldService;

    /**
     * 一覧画面表示用オブジェクト.
     */
    @Transfer
    private SearchCustomFieldResult result;

    /**
     * 検索条件（Label）.
     */
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String label;

    /**
     * カスタムフィールドDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * カスタムフィールド一覧から選択されたレコードのID.
     */
    private Long customFieldId;

    /**
     * カスタムフィールド一覧から選択されたレコードのprojectCustomFieldId.
     */
    private Long customFieldProjId;

    /**
     * カスタムフィールド一覧から選択されたレコードのversionNo.
     */
    private Long customFieldVerNo;

    /**
     * 現在のページ№.
     */
    @Transfer
    private int pageNo;

    /**
     * 総レコード数.
     */
    @Transfer
    private int dataCount;

    /**
     * 画面表示件数. default = 10.
     */
    @Transfer
    private int pageRowNum = DEFAULT_PAGE_ROW_NUMBER;

    /**
     * ページリンク数. default = 10.
     */
    @Transfer
    private int pageIndex = DEFAULT_PAGE_INDEX_NUMBER;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchCustomFieldCondition condition = null;

    /**
     * カスタムフィールド情報リスト.
     */
    @Transfer
    private List<CustomField> customFieldList = null;

    /**
     * Projectに登録するカスタムフィールドID.
     */
    @Required
    @Transfer
    private Long selectCustomField;

    /**
     * Projectに登録するカスタムフィールドの選択肢.
     */
    @Transfer
    private List<SelectItem> selectCustomFieldList = new ArrayList<SelectItem>();

    /**
     * アクション後の検索フラグ. リロード時にNoDataFoundとNoPageFoundエラーを無視するために使用する.
     */
    private boolean isAfterActionSearch;

    /**
     * プロジェクトに追加するカスタムフィールド情報.
     */
    @Transfer
    private List<CustomField> assignableCustomFieldList;

    /**
     * プロジェクトIDを取得する.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * プロジェクトIDをセットする.
     * @param projectId プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * @return the scLabel
     */
    public String getScLabel() {
        return scLabel;
    }

    /**
     * @param scLabel the scLabel to set
     */
    public void setScLabel(String scLabel) {
        this.scLabel = scLabel;
    }

    /**
     * 一覧画面表示用オブジェクトを取得する.
     * @return 一覧画面表示用オブジェクト
     */
    public SearchCustomFieldResult getResult() {
        return result;
    }

    /**
     * 一覧画面表示用オブジェクトをセットする.
     * @param result 一覧画面表示用オブジェクト
     */
    public void setResult(SearchCustomFieldResult result) {
        this.result = result;
    }

    /**
     * Labelを取得する.
     * @return Label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Labelをセットする.
     * @param label Label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * DataModelを取得する.
     * @return DataModel
     */
    public DataModel<?> getDataModel() {
        if (customFieldList != null) {
            dataModel = new ListDataModel<CustomField>(customFieldList);
        }
        return dataModel;
    }

    /**
     * DataModelをセットする.
     * @param dataModel DataModel
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the customFieldId
     */
    public Long getCustomFieldId() {
        return customFieldId;
    }

    /**
     * @param customFieldId the customFieldId to set
     */
    public void setCustomFieldId(Long customFieldId) {
        this.customFieldId = customFieldId;
    }

    /**
     * @return the customFieldProjId
     */
    public Long getCustomFieldProjId() {
        return customFieldProjId;
    }

    /**
     * @param customFieldProjId the customFieldProjId to set
     */
    public void setCustomFieldProjId(Long customFieldProjId) {
        this.customFieldProjId = customFieldProjId;
    }

    /**
     * @return the customFieldVerNo
     */
    public Long getCustomFieldVerNo() {
        return customFieldVerNo;
    }

    /**
     * @param customFieldVerNo the customFieldVerNo to set
     */
    public void setCustomFieldVerNo(Long customFieldVerNo) {
        this.customFieldVerNo = customFieldVerNo;
    }

    /**
     * ページナンバーを取得する.
     * @return ページナンバー
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * ページナンバーをセットする.
     * @param pageNo ページナンバー
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * データ総件数を取得する.
     * @return データ総件数
     */
    public int getDataCount() {
        return dataCount;
    }

    /**
     * データ総件数をセットする.
     * @param dataCount データ総件数
     */
    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    /**
     * 1ページあたりに表示する行数を取得する.
     * @return 1ページあたりに表示する行数
     */
    public int getPageRowNum() {
        return pageRowNum;
    }

    /**
     * 1ページあたりに表示する行数をセットする.
     * @param pageRowNum 1ページあたりに表示する行数
     */
    public void setPageRowNum(int pageRowNum) {
        this.pageRowNum = pageRowNum;
    }

    /**
     * ページリンク数を取得する.
     * @return ページリンク数
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * ページリンク数をセットする.
     * @param pageIndex ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * カスタムフィールド検索条件を取得する.
     * @return カスタムフィールド検索条件
     */
    public SearchCustomFieldCondition getCondition() {
        return condition;
    }

    /**
     * カスタムフィールド検索条件をセットする.
     * @param condition カスタムフィールド検索条件
     */
    public void setCondition(SearchCustomFieldCondition condition) {
        this.condition = condition;
    }

    /**
     * カスタムフィールドを取得する.
     * @return カスタムフィールド
     */
    public List<CustomField> getCustomFieldList() {
        return customFieldList;
    }

    /**
     * カスタムフィールドをセットする.
     * @param customFieldList カスタムフィールド
     */
    public void setCustomFieldList(List<CustomField> customFieldList) {
        this.customFieldList = customFieldList;
    }

    /**
     * 選択した追加するカスタムフィールドを取得する.
     * @return 選択した追加するカスタムフィールド
     */
    public Long getSelectCustomField() {
        return selectCustomField;
    }

    /**
     * 選択した追加するカスタムフィールドをセットする.
     * @param selectCustomField 選択した追加するカスタムフィールド
     */
    public void setSelectCustomField(Long selectCustomField) {
        this.selectCustomField = selectCustomField;
    }

    /**
     * 追加するカスタムフィールドリストを取得する.
     * @return 追加するカスタムフィールドリスト
     */
    public List<SelectItem> getSelectCustomFieldList() {
        return selectCustomFieldList;
    }

    /**
     * 追加するカスタムフィールドリストをセットする.
     * @param selectCustomFieldList 追加するカスタムフィールドリスト
     */
    public void setSelectCustomFieldList(List<SelectItem> selectCustomFieldList) {
        this.selectCustomFieldList = selectCustomFieldList;
    }

    /**
     * プロジェクトに属していないカスタムフィールドを取得する.
     * @return プロジェクトに属していないカスタムフィールド
     */
    public List<CustomField> getAssignableCustomFieldList() {
        return assignableCustomFieldList;
    }

    /**
     * プロジェクトに属していないカスタムフィールドをセットする.
     * @param assignableCustomFieldList プロジェクトに属していないカスタムフィールド
     */
    public void setAssignableCustomFieldList(List<CustomField> assignableCustomFieldList) {
        this.assignableCustomFieldList = assignableCustomFieldList;
    }

    /**
     * 「前へ」を表示する判定をする.
     * @return 表示するtrue / 表示しないfalse
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示する判定をする.
     * @return 表示するtrue / 表示しないfalse
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        reloadCustomFieldIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadCustomFieldIndex();
        return null;
    }

    /**
     * 一覧画面をリロードする.
     */
    private void reloadCustomFieldIndex() {
        handler.handleAction(new SearchAction(this, false));
    }

    /**
     * 画面表示件数の文字列を取得する.
     * @return 画面表示件数
     */
    public String getPageDisplayNo() {
        return PagingUtil.getPageDisplayNo(pageNo, pageRowNum, dataCount);
    }

    /**
     * ページリンクの文字列を取得する.
     * @return ページリンク用配列
     */
    public String[] getPagingNo() {
        return PagingUtil.getPagingNo(pageIndex, pageNo, dataCount, pageRowNum);
    }

    /**
     * 空のインスタンスを生成する.
     */
    public CustomFieldIndexPage() {
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:search")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * assignの入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getAssignToValidationGroups() {
        if (isActionInvoked("form:assign")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される.
     * </p>
     */
    @Initialize
    public void initialize() {
        projectId = getCurrentProjectId();
        String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
        if (StringUtils.isNotEmpty(strPageRow)) {
            pageRowNum = Integer.parseInt(strPageRow);
        }
        String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
        if (StringUtils.isNotEmpty(strPageIndex)) {
            pageIndex = Integer.parseInt(strPageIndex);
        }

        condition =
            (SearchCustomFieldCondition)
                getPreviousSearchCondition(SearchCustomFieldCondition.class);
        if (condition != null) {
            label = condition.getLabel();
        }

        search();
    }

    /**
     * 検索処理.
     * @return null
     */
    public String search() {
        pageNo = 1;
        handler.handleAction(new SearchAction(this, true));
        return null;
    }

    /**
     * カスタムフィールドを登録する.
     * @return 編集画面
     */
    public String create() {
        setNextSearchCondition(condition);
        return toUrl("customFieldEdit", isLoginProject());
    }

    /**
     * カスタムフィールドを更新する.
     * @return 編集画面
     */
    public String update() {
        setNextSearchCondition(condition);
        return toUrl(String.format("customFieldEdit?id=%s&projectCustomFieldId=%s",
                             getCustomFieldId(),
                             getCustomFieldProjId()),
                             isLoginProject());
    }

    /**
     * カスタムフィールドを削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            reloadCustomFieldIndex();
        }
        return null;
    }

    /**
     * カスタムフィールド一覧をExcelに出力する.
     */
    public void downloadExcel() {
        handler.handleAction(new ExcelDownloadAction(this));
    }

    /**
     * カスタムフィールドをプロジェクトに登録する.
     */
    public void assignTo() {
        if (handler.handleAction(new AssignAction(this))) {
            reloadCustomFieldIndex();
        }
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        reloadCustomFieldIndex();
        return null;
    }

    /**
     * 現在のリクエストがassignアクションによって発生した場合はtrue.
     * @return assignアクションの場合true
     */
    public boolean isAssignToAction() {
        return isActionInvoked("form:assign");
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * 検索処理アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -3946082550335542778L;

        /** アクション発生元ページ. */
        private CustomFieldIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public SearchAction(CustomFieldIndexPage page, boolean parameterSearch) {
            super(page);
            this.page = page;
            this.parameterSearch = parameterSearch;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (page.condition == null) {
                page.condition = new SearchCustomFieldCondition();
            }
            page.projectId = page.getCurrentProjectId();

            setConditionParameter();

            // 初期化
            if (page.customFieldList != null) {
                page.customFieldList.clear();
            }
            page.dataCount = 0;

            if (page.isAfterActionSearch) {
                searchAfterAction();
            } else {
                search();
            }
        }

        private void setConditionParameter() {
            if (parameterSearch) {
                page.condition.setLabel(page.getLabel());

                page.setScLabel(page.getLabel());
            } else {
                page.condition.setLabel(page.getScLabel());
            }
            page.condition.setProjectId(page.getCurrentProjectId());
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
            if (StringUtils.isEmpty(page.getCurrentProjectId())) {
                page.condition.setUseWhole(UseWhole.ALL);
            }

        }
        /**
         * 検索を行う.
         * @throws ServiceAbortException 検索エラー
         */
        private void search() throws ServiceAbortException {
            if (StringUtils.isNotEmpty(page.projectId)) {
                page.assignableCustomFieldList =
                        page.customFieldService.searchNotAssigned();
                page.selectCustomFieldList =
                        page.viewHelper.createSelectItem(
                            page.assignableCustomFieldList, "id", "label");
            }
            page.result = page.customFieldService.searchPagingList(page.condition);
            page.customFieldList = page.result.getCustomFieldList();
            page.dataCount = page.result.getCount();

        }

        /**
         * 削除アクションの後専用：検索を行う. 削除の結果0件になってもエラーメッセージを表示しない.
         * @throws ServiceAbortException NO_DATA_FOUND以外のエラー
         */
        private void searchAfterAction() throws ServiceAbortException {
            try {
                search();
            } catch (ServiceAbortException e) {
                // 削除の結果該当ページにレコードがなくなった場合、前ページを表示する
                if (ApplicationMessageCode.NO_PAGE_FOUND.equals(e.getMessageCode())) {
                    page.pageNo--;
                    page.condition.setPageNo(page.pageNo);
                    searchAfterAction();
                    // 削除の結果0件になってもエラーメッセージを表示しない
                } else if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                    // 処理が終わったらフラグを戻す
                    page.isAfterActionSearch = false;
                    throw e;
                }
            }
            // 処理が終わったらフラグを戻す
            page.isAfterActionSearch = false;
        }
    }

    /**
     * Excelダウンロードアクション.
     * @author opentone
     */
    static class ExcelDownloadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4725841700795107032L;
        /** アクション発生元ページ. */
        private CustomFieldIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(CustomFieldIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setLabel(page.getScLabel());

                String fileName = page.createFileName() + ".xls";
                // 全件指定用検索条件に変換
                SearchCustomFieldCondition allRowCondition =
                        (SearchCustomFieldCondition) page.cloneToAllRowCondition(page.condition);
                SearchCustomFieldResult result =
                        page.customFieldService.searchPagingList(allRowCondition);
                List<CustomField> list = result.getCustomFieldList();

                byte[] data = page.customFieldService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    /**
     * カスタムフィールドをプロジェクトに登録するアクション.
     * @author opentone
     */
    static class AssignAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -2373721082760020635L;
        /** アクション発生元ページ. */
        private CustomFieldIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public AssignAction(CustomFieldIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            CustomField entryCustomField = null;

            for (CustomField customField : page.assignableCustomFieldList) {
                if (page.selectCustomField.equals(customField.getId())) {
                    entryCustomField = customField;
                    entryCustomField.setProjectId(page.projectId);
                    break;
                }
            }

            page.customFieldService.assignTo(entryCustomField);

            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.CUSTOM_FIELD_ASSIGNED);
        }
    }

    /**
     * 削除アクション.
     * @author opentone
     */
    static class DeleteAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 6803281473014526110L;
        /** アクション発生元ページ. */
        private CustomFieldIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public DeleteAction(CustomFieldIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            CustomField customField = new CustomField();
            customField.setId(page.getCustomFieldId());
            customField.setProjectCustomFieldId(page.getCustomFieldProjId());
            customField.setVersionNo(page.getCustomFieldVerNo());
            customField.setProjectId(page.getProjectId());

            page.customFieldService.delete(customField);

            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.CUSTOM_FIELD_DELETED);
        }
    }

}
