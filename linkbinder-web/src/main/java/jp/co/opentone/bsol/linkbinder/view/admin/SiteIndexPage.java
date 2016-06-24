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
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 拠点一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class SiteIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7808707908147212340L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "siteindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "siteindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * 拠点情報サービス.
     */
    @Resource
    private SiteService siteService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchSiteCondition condition = null;

    /**
     * 検索条件：拠点コード.
     */
    @Transfer
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String code;

    /**
     * 検索条件：拠点名.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

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
     * 拠点のデータ.
     */
    @Transfer
    private List<Site> siteList;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * 拠点リストから選択されたレコードのID.
     */
    private Long siteId;

    /**
     * 拠点リストから選択されたレコードのProject ID.
     */
    private String siteProjId;

    /**
     * 拠点リストから選択されたレコードのVersion No.
     */
    private Long siteVerNo;

    /**
     * 画面要素の制御オブジェクト.
     */
    @Transfer
    private boolean permitMasterEdit;

    /**
     * 削除アクションフラグ. リロード時にRecordNotFoundエラーを無視するために使用する.
     */
    private boolean isDeleteAction;

    /**
     * 空のインスタンスを生成する.
     */
    public SiteIndexPage() {
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
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される.
     * ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     */
    @Initialize
    public void initialize() {
        String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
        if (StringUtils.isNotEmpty(strPageRow)) {
            pageRowNum = Integer.parseInt(strPageRow);
        }
        String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
        if (StringUtils.isNotEmpty(strPageIndex)) {
            pageIndex = Integer.parseInt(strPageIndex);
        }
        permitMasterEdit = isSystemAdmin() || isProjectAdmin(getCurrentProjectId());

        condition = (SearchSiteCondition) getPreviousSearchCondition(SearchSiteCondition.class);
        if (condition != null) {
            code = condition.getSiteCd();
            name = condition.getName();
        }

        search();
    }

    /**
     * 入力された検索条件で検索する.
     * @return null
     */
    public String search() {
        pageNo = 1;
        handler.handleAction(new SearchAction(this, true));
        return null;
    }

    /**
     * 一覧画面をEXCELファイルでダウンロードする.
     * @return null
     */
    public String downloadExcel() {
        handler.handleAction(new ExcelDownloadAction(this));
        return null;
    }

    /**
     * 新規作成を行う.
     * @return 編集画面
     */
    public String create() {
        setNextSearchCondition(condition);
        return toUrl("siteEdit");
    }

    /**
     * 更新を行う.
     * @return 編集画面
     */
    public String update() {
        setNextSearchCondition(condition);
        return toUrl(String.format("siteEdit?id=%s", getSiteId()));
    }

    /**
     * 活動単位一覧に遷移する.
     * @return 活動単位一覧画面
     */
    public String goGroupIndex() {
        setNextSearchCondition(condition);
        return toUrl(String.format("groupIndex?id=%s&backPage=siteIndex", getSiteId()));
    }

    /**
     * 拠点情報を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            reloadSiteIndex();
        }
        return null;
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        reloadSiteIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadSiteIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     * @throws ServiceAbortException
     *             再検索時にエラー
     */
    public String changePage() throws ServiceAbortException {
        reloadSiteIndex();
        return null;
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
     * 前ページリンクを表示するか判定する.
     * @return 前ページリンク
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 次ページリンクを表示するか判定する.
     * @return 次ページリンク
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * 検索条件：拠点コードを設定する.
     * @param code
     *            検索条件：拠点コード
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 検索条件：拠点コードを取得する.
     * @return 検索条件：拠点コード
     */
    public String getCode() {
        return code;
    }

    /**
     * 検索条件：拠点名を設定する.
     * @param name
     *            検索条件：拠点名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 検索条件：拠点名を取得する.
     * @return 検索条件：拠点名
     */
    public String getName() {
        return name;
    }

    /**
     * @return the scCode
     */
    public String getScCode() {
        return scCode;
    }

    /**
     * @param scCode the scCode to set
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
     * 検索条件を設定する.
     * @param condition
     *            検索条件
     */
    public void setCondition(SearchSiteCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を取得する.
     * @return 検索条件
     */
    public SearchSiteCondition getCondition() {
        return condition;
    }

    /**
     * 現在のページ№.を設定する.
     * @param pageNo
     *            現在のページ№.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 現在のページ№.を取得する.
     * @return 現在のページ№.
     */
    public int getPageNo() {
        return pageNo;
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
     * 総レコード数を取得する.
     * @return 総レコード数
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
     * 画面表示件数を取得する.
     * @return 画面表示件数
     */
    public int getPageRowNum() {
        return pageRowNum;
    }

    /**
     * ページリンク数を設定する.
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
     * 拠点のデータを設定する.
     * @param siteList
     *            拠点のデータ
     */
    public void setSiteList(List<Site> siteList) {
        this.siteList = siteList;
    }

    /**
     * 拠点のデータを取得する.
     * @return 拠点のデータ
     */
    public List<Site> getSiteList() {
        return siteList;
    }

    /**
     * 拠点データのDataModelを設定する.
     * @param dataModel
     *            拠点データのDataModel
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * 拠点データのDataModelを取得する.
     * @return 拠点データのDataModel
     */
    public DataModel<?> getDataModel() {
        if (siteList != null) {
            dataModel = new ListDataModel<Site>(siteList);
        }

        return dataModel;
    }

    /**
     * @return the siteId
     */
    public Long getSiteId() {
        return siteId;
    }

    /**
     * @param siteId the siteId to set
     */
    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the siteProjId
     */
    public String getSiteProjId() {
        return siteProjId;
    }

    /**
     * @param siteProjId the siteProjId to set
     */
    public void setSiteProjId(String siteProjId) {
        this.siteProjId = siteProjId;
    }

    /**
     * @return the siteVerNo
     */
    public Long getSiteVerNo() {
        return siteVerNo;
    }

    /**
     * @param siteVerNo the siteVerNo to set
     */
    public void setSiteVerNo(Long siteVerNo) {
        this.siteVerNo = siteVerNo;
    }

    /**
     * 画面要素の制御オブジェクトを設定する.
     * @param permitMasterEdit
     *            画面要素の制御オブジェクト
     */
    public void setPermitMasterEdit(boolean permitMasterEdit) {
        this.permitMasterEdit = permitMasterEdit;
    }

    /**
     * 画面要素の制御オブジェクトを取得する.
     * @return 画面要素の制御オブジェクト
     */
    public boolean isPermitMasterEdit() {
        return permitMasterEdit;
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * 拠点データ一覧を更新する.
     */
    private void reloadSiteIndex() {
        if (handler.handleAction(new SearchAction(this, false))) {
            dataModel = new ListDataModel<Site>(siteList);
        }
    }

    /**
     * 検索条件にGroupAdminの情報をセットする.
     */
    private void setConditionGroupAdminUser() {
        User searchUser = new User();
        searchUser.setEmpNo(getCurrentUser().getEmpNo());
        searchUser.setSecurityLevel(SystemConfig
            .getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN));
        condition.setSearchUser(searchUser);
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -642210818544114125L;

        /** アクション発生元ページ. */
        private SiteIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchAction(SiteIndexPage page, boolean parameterSearch) {
            super(page);
            this.page = page;
            this.parameterSearch = parameterSearch;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.checkProjectSelected();
            if (page.condition == null) {
                page.condition = new SearchSiteCondition();
            }
            // 検索条件をセットする
            setCondition();

            // 初期化
            if (page.siteList != null) {
                page.siteList.clear();
            }
            page.dataCount = 0;

            if (page.isDeleteAction) {
                searchAfterDelete();
            } else {
                search();
            }
        }

        /**
         * 検索条件をセットする.
         */
        private void setCondition() {
            if (parameterSearch) {
                page.condition.setName(page.getName());
                page.condition.setSiteCd(page.getCode());

                page.setScCode(page.getCode());
                page.setScName(page.getName());
            } else {
                page.condition.setSiteCd(page.getScCode());
                page.condition.setName(page.getScName());
            }
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
            page.condition.setProjectId(page.getCurrentProjectId());
            page.condition.setSystemAdmin(page.isSystemAdmin());
            if (StringUtils.isNotEmpty(page.getCurrentProjectId())) {
                page.condition.setProjectAdmin(page.isProjectAdmin(page.getCurrentProjectId()));

                if (page.isAnyGroupAdmin(page.getCurrentProjectId())
                        && !page.isSystemAdmin()
                        && !page.isProjectAdmin(page.getCurrentProjectId())) {
                    page.condition.setGroupAdmin(true);
                    page.setConditionGroupAdminUser();
                }
            } else {
                page.condition.setProjectAdmin(false);
                page.condition.setGroupAdmin(false);
            }
        }

        /**
         * 検索を行う.
         * @throws ServiceAbortException 検索時にエラー
         */
        private void search() throws ServiceAbortException {
            SearchSiteResult result = page.siteService.search(page.condition);
            page.siteList = result.getSiteList();
            page.dataCount = result.getCount();
        }

        /**
         * 削除アクションの後専用：検索を行う. 削除の結果0件になってもエラーメッセージを表示しない.
         * @throws ServiceAbortException
         *             NO_DATA_FOUND以外のエラー
         */
        private void searchAfterDelete() throws ServiceAbortException {
            try {
                search();
            } catch (ServiceAbortException e) {
                // 削除の結果該当ページにレコードがなくなった場合、前ページを表示する
                if (ApplicationMessageCode.NO_PAGE_FOUND.equals(e.getMessageCode())) {
                    page.pageNo--;
                    page.condition.setPageNo(page.pageNo);
                    searchAfterDelete();
                    // 削除の結果0件になってもエラーメッセージを表示しない
                } else if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                    // 処理が終わったらフラグを戻す
                    page.isDeleteAction = false;
                    throw e;
                }
            }
            // 処理が終わったらフラグを戻す
            page.isDeleteAction = false;
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
        private static final long serialVersionUID = 2312098665932036496L;
        /** アクション発生元ページ. */
        private SiteIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(SiteIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setSiteCd(page.getScCode());
                page.condition.setName(page.getScName());

                String fileName = page.createFileName() + ".xls";
                // 全件指定
                SearchSiteCondition allRowCondition
                    = (SearchSiteCondition) page.cloneToAllRowCondition(page.condition);
                SearchSiteResult result = page.siteService.search(allRowCondition);
                List<Site> list = result.getSiteList();

                byte[] data = page.siteService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
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
        private static final long serialVersionUID = 6033585630680038817L;
        /** アクション発生元ページ. */
        private SiteIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DeleteAction(SiteIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Site site = new Site();
            site.setId(page.getSiteId());
            site.setProjectId(page.getSiteProjId());
            site.setVersionNo(page.getSiteVerNo());

            page.siteService.delete(site);

            page.isDeleteAction = true;

            page.setPageMessage(ApplicationMessageCode.SITE_DELETED);
        }
    }
}
