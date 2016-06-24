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
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.SearchCompanyResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 会社情報一覧表示画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CompanyIndexPage extends AbstractPage {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 6910240426688308505L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "companyindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "companyindex.pageindex";

    /**
     * 画面表示用Role.
     */
    private static final String ROLE_DISPLAY = "役割";

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
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される. マスタ管理ならばnull
     * </p>
     */
    @Transfer
    private String projectId;

    /**
     * 会社情報サービス.
     */
    @Resource
    private CompanyService companyService;

    /**
     * 会社情報リスト.
     */
    @Transfer
    private SearchCompanyResult company;

    /**
     * 会社コード.
     */
    @Alphanumeric
    // CHECKSTYLE:OFF
    @Length(max = 10)
    // CHECKSTYLE:ON
    private String companyCd;

    /**
     * 会社名.
     */
    // CHECKSTYLE:OFF
    @Length(max = 100)
    // CHECKSTYLE:ON
    private String name;

    /**
     * 役割.
     */
    // CHECKSTYLE:OFF
    @Length(max = 50)
    // CHECKSTYLE:ON
    private String role;

    /**
     * 会社情報DataModel.
     */
    private DataModel<?> companyModel;

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
    private SearchCompanyCondition condition = null;

    /**
     * 検索条件-Code.
     */
    @Transfer
    private String scCode;

    /**
     * 検索条件-Name.
     */
    @Transfer
    private String scName;

    /**
     * 検索条件-Role.
     */
    @Transfer
    private String scRole;

    /**
     * 会社情報リストから選択されたレコードのID.
     */
    @Transfer
    private Long companyRowId;

    /**
     * 会社情報リストから選択されたレコードのVersionNo.
     */
    @Transfer
    private Long companyVerNo;

    /**
     * 会社情報リスト.
     */
    @Transfer
    private List<Company> companyList = null;

    /**
     * Projectに登録する会社ID.
     */
    @Required
    @Transfer
    private Long selectCompany;

    /**
     * Projectに登録する会社情報の選択肢.
     */
    @Transfer
    private List<SelectItem> selectCompanyList = new ArrayList<SelectItem>();

    /**
     * アクション後の検索フラグ. リロード時にNoDataFoundとNoPageFoundエラーを無視するために使用する.
     */
    private boolean isAfterActionSearch;

    /**
     * プロジェクトに追加する会社情報.
     */
    @Transfer
    private List<Company> assignableCompanyList;

    /**
     * 会社一覧のcolumnClassesに適用するclass.
     */
    private String tableStyleClasses;

    /**
     * 会社一覧のcolumnClassesに適用するclassを取得する.
     * @return the tableStyleClasses
     */
    public String getTableStyleClasses() {
        StringBuilder sb = new StringBuilder();
        // column 1
        sb.append("number,");
        // column 2
        sb.append("text,");
        // column 3
        sb.append("text width500,");

        // プロジェクトIDが存在する場合Role用のStyleClassを付与
        if (this.projectId != null) {
            // column 4
            sb.append("text,");
        }
        // column 5
        sb.append("list-row-action");

        tableStyleClasses = sb.toString();
        return tableStyleClasses;
    }

    /**
     * 会社一覧のcolumnClassesに適用するclassをセットする.
     * @param tableStyleClasses the tableStyleClasses to set
     */
    public void setTableStyleClasses(String tableStyleClasses) {
        this.tableStyleClasses = tableStyleClasses;
    }

    /**
     * @return the scCod
     */
    public String getScCode() {
        return scCode;
    }

    /**
     * @param scCode the scCod to set
     */
    public void setScCode(String scCode) {
        this.scCode = scCode;
    }

    /**
     * @return the scName
     */
    public String getScName() {
        return scName;
    }

    /**
     * @param scName the scName to set
     */
    public void setScName(String scName) {
        this.scName = scName;
    }

    /**
     * @return the scRole
     */
    public String getScRole() {
        return scRole;
    }

    /**
     * @param scRole the scRole to set
     */
    public void setScRole(String scRole) {
        this.scRole = scRole;
    }

    /**
     * @return the companyRowId
     */
    public Long getCompanyRowId() {
        return companyRowId;
    }

    /**
     * @param companyRowId the companyRowId to set
     */
    public void setCompanyRowId(Long companyRowId) {
        this.companyRowId = companyRowId;
    }

    /**
     * @return the companyVerNo
     */
    public Long getCompanyVerNo() {
        return companyVerNo;
    }

    /**
     * @param companyVerNo the companyVerNo to set
     */
    public void setCompanyVerNo(Long companyVerNo) {
        this.companyVerNo = companyVerNo;
    }

    /**
     * プロジェクトに追加する会社情報を取得する.
     * @return プロジェクトに追加する会社情報
     */
    public List<Company> getAssignableCompanyList() {
        return assignableCompanyList;
    }

    /**
     * プロジェクトに追加する会社情報をセットする.
     * @param assignableCompanyList
     *            プロジェクトに追加する会社情報
     */
    public void setAssignableCompanyList(List<Company> assignableCompanyList) {
        this.assignableCompanyList = assignableCompanyList;
    }

    /**
     * 一覧画面表示用会社情報を設定する.
     * @param company
     *            一覧画面表示用会社情報
     */
    public void setCompany(SearchCompanyResult company) {
        this.company = company;
    }

    /**
     * 一覧画面表示用会社情報を返す.
     * @return the company 一覧画面表示用会社情報
     */
    public SearchCompanyResult getCompany() {
        return company;
    }

    /**
     * 会社コードを設定する.
     * @param companyCd
     *            会社コード
     */
    public void setCompanyCd(String companyCd) {
        this.companyCd = companyCd;
    }

    /**
     * 会社コードを返す.
     * @return 会社コード
     */
    public String getCompanyCd() {
        return companyCd;
    }

    /**
     * 会社名を設定する.
     * @param name
     *            会社名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 会社名を返す.
     * @return 会社名
     */
    public String getName() {
        return name;
    }

    /**
     * 役割を設定する.
     * @param role
     *            役割
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * 役割を返す.
     * @return 役割
     */
    public String getRole() {
        return role;
    }

    /**
     * ページ番号を設定する.
     * @param pageNo
     *            ページ番号
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * ページ番号を返す.
     * @return ページ番号
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 「前へ」を表示するか判定する.
     * @return 表示するtrue / 表示しないfalse
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示するか判定する.
     * @return 表示するtrue / 表示しないfalse
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * 総レコード数を設定する.
     * @param dataCount
     *            総レコード数
     */
    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    /**
     * 総レコード数を返す.
     * @return 総レコード数t
     */
    public int getDataCount() {
        return dataCount;
    }

    /**
     * 画面表示件数を設定する.
     * @param pageRowNum
     *            画面表示件数
     */
    public void setPageRowNum(int pageRowNum) {
        this.pageRowNum = pageRowNum;
    }

    /**
     * 画面表示件数を返す.
     * @return 画面表示件数
     */
    public int getPageRowNum() {
        return pageRowNum;
    }

    /**
     * ページリンク数を返す.
     * @return ページリンク数
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * ページリンク数を設定する.
     * @param pageIndex
     *            ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
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
     * プロジェクトIDを返す.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 画面表示用Roleを返す.
     * @return 画面表示用Role
     */
    public String getRoleDisplay() {
        return ROLE_DISPLAY;
    }

    /**
     * 検索条件を設定する.
     * @param condition
     *            検索条件
     */
    public void setCondition(SearchCompanyCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を返す.
     * @return 検索条件
     */
    public SearchCompanyCondition getCondition() {
        return condition;
    }

    /**
     * 会社情報リストを設定する.
     * @param companyList
     *            会社情報リスト
     */
    public void setCompanyList(List<Company> companyList) {
        this.companyList = companyList;
    }

    /**
     * 会社情報リストを返す.
     * @return 会社情報リスト
     */
    public List<Company> getCompanyList() {
        return companyList;
    }

    /**
     * 追加する会社情報のIDを設定する.
     * @param selectCompany
     *            追加する会社情報ID
     */
    public void setSelectCompany(Long selectCompany) {
        this.selectCompany = selectCompany;
    }

    /**
     * 追加する会社情報IDを返す.
     * @return 追加する会社情報ID
     */
    public Long getSelectCompany() {
        return selectCompany;
    }

    /**
     * 追加できる会社情報リストを設定する.
     * @param selectCompanyList
     *            追加できる会社情報リスト
     */
    public void setSelectCompanyList(List<SelectItem> selectCompanyList) {
        this.selectCompanyList = selectCompanyList;
    }

    /**
     * 追加できる会社情報リストを返す.
     * @return 追加できる会社情報リスト
     */
    public List<SelectItem> getSelectCompanyList() {
        return selectCompanyList;
    }

    /**
     * 会社情報DataModelを返す.
     * @return 会社情報DataModel
     */
    public DataModel<?> getCompanyModel() {
        if (companyList != null) {
            companyModel = new ListDataModel<Company>(companyList);
        }
        return companyModel;
    }

    /**
     * 会社情報DataModelを設定する.
     * @param companyModel
     *            会社情報DataModel
     */
    public void setCompanyModel(DataModel<?> companyModel) {
        this.companyModel = companyModel;
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        reloadCompanyIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadCompanyIndex();
        return null;
    }

    private void reloadCompanyIndex() {
        projectId = getCurrentProjectId();
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
    public CompanyIndexPage() {
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
     * Assign to Projectの検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getAssignToValidationGroup() {
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
            (SearchCompanyCondition) getPreviousSearchCondition(SearchCompanyCondition.class);
        if (condition != null) {
            companyCd = condition.getCompanyCd();
            name = condition.getName();
            role = condition.getRole();
        }

        search();
    }

    /**
     * 検索処理.
     */
    public void search() {
        pageNo = 1;
        handler.handleAction(new SearchAction(this, true));
    }

    /**
     * 会社情報を作成する.
     * @return 編集画面
     */
    public String create() {
        setNextSearchCondition(condition);
        return toUrl("companyEdit", isLoginProject());
    }

    /**
     * 会社情報を更新する.
     * @return 編集画面
     */
    public String update() {
        setNextSearchCondition(condition);
        return toUrl(String.format("companyEdit?id=%s", getCompanyRowId()), isLoginProject());
    }

    /**
     * 会社 - ユーザー情報を更新する.
     * @return 会社 - ユーザー情報画面
     */
    public String editMember() {
        setNextSearchCondition(condition);
        return toUrl(String.format("companyEditMember?id=%s&backPage=companyIndex",
                getCompanyRowId()), isLoginProject());
    }

    /**
     * 会社情報を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            reloadCompanyIndex();
        }
        return null;
    }

    /**
     * 会社情報一覧をExcelに出力する.
     */
    public void downloadExcel() {
        handler.handleAction(new ExcelDownloadAction(this));
    }

    /**
     * 会社情報をプロジェクトに登録する.
     */
    public void assignTo() {
        if (handler.handleAction(new AssignAction(this))) {
            reloadCompanyIndex();
        }
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        reloadCompanyIndex();
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
     * 更新権限有無を判定する.
     * @return 更新権限ありの場合true
     */
    public boolean isUpdatableCompany() {
        return isSystemAdmin() || isProjectAdmin(projectId);
    }

    /**
     * 検索処理アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 794019128846761758L;

        /** アクション発生元ページ. */
        private CompanyIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchAction(CompanyIndexPage page, boolean parameterSearch) {
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
                page.condition = new SearchCompanyCondition();
            }
            page.projectId = page.getCurrentProjectId();

            setConditionParameter();

            // 初期化
            if (page.companyList != null) {
                page.companyList.clear();
            }
            page.dataCount = 0;

            if (page.isAfterActionSearch) {
                searchAfterAction();
            } else {
                search();
            }
        }

        /**
         * 検索条件を設定する.
         */
        private void setConditionParameter() {
            if (parameterSearch) {
                page.condition.setCompanyCd(page.getCompanyCd());
                page.condition.setName(page.getName());
                page.condition.setRole(page.role);

                page.setScCode(page.getCompanyCd());
                page.setScName(page.getName());
                page.setScRole(page.getRole());
            } else {
                page.condition.setName(page.getScName());
                page.condition.setCompanyCd(page.getScCode());
                page.condition.setRole(page.getScRole());
            }
            page.condition.setProjectId(page.getCurrentProjectId());
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
        }

        /**
         * 検索を行う.
         * @throws ServiceAbortException
         *             検索エラー
         */
        private void search() throws ServiceAbortException {
            if (StringUtils.isNotEmpty(page.projectId)) {
                page.assignableCompanyList = page.companyService.searchNotAssigned();
                page.selectCompanyList =
                        page.viewHelper.createSelectItem(page.assignableCompanyList,
                            "id",
                            "codeAndName");
            }
            page.company = page.companyService.search(page.condition);
            page.companyList = page.company.getCompanyList();
            page.dataCount = page.company.getCount();
        }

        /**
         * 削除アクションの後専用：検索を行う. 削除の結果0件になってもエラーメッセージを表示しない.
         * @throws ServiceAbortException
         *             NO_DATA_FOUND以外のエラー
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
        private static final long serialVersionUID = -241233572657016659L;
        /** アクション発生元ページ. */
        private CompanyIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(CompanyIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setCompanyCd(page.getScCode());
                page.condition.setName(page.getScName());
                page.condition.setRole(page.getScRole());

                String fileName = page.createFileName() + ".xls";
                // 全件指定用検索条件に変換
                SearchCompanyCondition allRowCondition =
                        (SearchCompanyCondition) page.cloneToAllRowCondition(page.condition);
                SearchCompanyResult result = page.companyService.search(allRowCondition);
                List<Company> list = result.getCompanyList();

                byte[] data = page.companyService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    /**
     * 会社情報をプロジェクトに登録するアクション.
     * @author opentone
     */
    static class AssignAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6044838491433286522L;
        /** アクション発生元ページ. */
        private CompanyIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public AssignAction(CompanyIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Company entryCompany = null;

            for (Company company : page.assignableCompanyList) {
                if (page.selectCompany.equals(company.getId())) {
                    entryCompany = company;
                    entryCompany.setProjectId(page.projectId);
                    break;
                }
            }
            page.companyService.assignTo(entryCompany);
            page.isAfterActionSearch = true;
            page.setPageMessage(ApplicationMessageCode.COMPANY_ASSIGNED);
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
        private static final long serialVersionUID = 2115025720912509708L;
        /** アクション発生元ページ. */
        private CompanyIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DeleteAction(CompanyIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Company company = page.companyService.find(page.getCompanyRowId());
            company.setVersionNo(page.getCompanyVerNo());

            page.companyService.delete(company);

            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.COMPANY_DELETED);
        }
    }
}
