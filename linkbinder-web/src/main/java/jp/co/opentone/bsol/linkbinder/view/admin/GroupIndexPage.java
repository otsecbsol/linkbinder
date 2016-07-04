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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponGroupResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.AbstractCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.SiteService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 活動単位一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class GroupIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7808707908147212340L;

    /**
     * ログ出力オブジェクト.
     */
    private static final Logger log = LoggerFactory.getLogger(GroupIndexPage.class);

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "correspongroupindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "correspongroupindex.pageindex";

    /**
     * 拠点表示画面名.
     */
    private static final String PAGE_SITE = "site";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * 拠点サービス.
     */
    @Resource
    private SiteService siteService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchCorresponGroupCondition condition = null;

    /**
     * backで遷移するページ名.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private String backPage;

    /**
     * 拠点ID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;

    /**
     * 検索条件.
     * 起動元画面からのFlashパラメータを設定する.
     */
    @Transfer
    private AbstractCondition previousCondition;

    /**
     * 拠点.
     */
    @Transfer
    private Site site;

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
     * 活動単位のデータ.
     */
    @Transfer
    private List<CorresponGroup> corresponGroupList;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * 活動単位に登録する部門.
     */
//    @SkipValidation("#{!groupIndexPage.addAction}")
    @Required
    @Transfer
    private Long selectDiscipline;

    /**
     * 活動単位に登録する部門のデータ.
     */
    @Transfer
    private List<Discipline> disciplineList = null;

    /**
     * 活動単位に登録する部門の選択肢.
     */
    @Transfer
    private List<SelectItem> selectDisciplineList = new ArrayList<SelectItem>();

    /**
     * アクション後の検索フラグ.
     * リロード時にNoDataFoundとNoPageFoundエラーを無視するために使用する.
     */
    private boolean isAfterActionSearch;

    /*
     * 空のインスタンスを生成する.
     */
    public GroupIndexPage() {
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     */
    @Initialize
    public void initialize() {
        if (handler.handleAction(new InitializeAction(this))) {
            loadGroupIndex();
        }
    }

    /**
     * 入力検証グループ名を返す.
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:add")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 拠点に部門を新しい活動単位として追加する.
     * @return null
     */
    public String add() {
        if (handler.handleAction(new AddAction(this))) {
            loadGroupIndex();
        }
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
     * 指定された情報を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            loadGroupIndex();
        }
        return null;
    }

    /**
     * 活動単位設定画面に遷移する.
     * @return 活動単位設定
     */
    public String editMember() {
        setNextSearchCondition(previousCondition);
        return toUrl(String.format("groupEdit?id=%s&backPage=%s&siteId=%s",
                             ((CorresponGroup) dataModel.getRowData()).getId(),
                             backPage,
                             id));
    }

    /**
     * 起動元画面に遷移する.
     * @return 起動元画面名
     */
    public String back() {
        setNextSearchCondition(previousCondition);
        // 拠点表示画面の場合はIDが必要
        if (PAGE_SITE.equals(backPage)) {
            backPage = String.format("site?id=%s", id);
        }
        return toUrl(backPage);
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        loadGroupIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        loadGroupIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        loadGroupIndex();
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
     * ページ番号をセットする.
     * @param pageNo
     *            ページ番号
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * ページ番号を取得する.
     * @return ページ番号
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 一覧に表示するデータの総数をセットする.
     * @param dataCount
     *            一覧に表示するデータの総数
     */
    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }

    /**
     * 一覧に表示するデータの総数を取得する.
     * @return 一覧に表示するデータの総数
     */
    public int getDataCount() {
        return dataCount;
    }

    /**
     * 1ページあたりの行数をセットする.
     * @param pageRowNum
     *            1ページあたりの行数
     */
    public void setPageRowNum(int pageRowNum) {
        this.pageRowNum = pageRowNum;
    }

    /**
     * 1ページあたりの行数を取得する.
     * @return 1ページあたりの行数
     */
    public int getPageRowNum() {
        return pageRowNum;
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
     * @param pageIndex
     *            ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * 活動単位検索条件を取得する.
     * @return 活動単位検索条件
     */
    public SearchCorresponGroupCondition getCondition() {
        return condition;
    }

    /**
     * 活動単位検索条件をセットする.
     * @param condition
     *            活動単位検索条件
     */
    public void setCondition(SearchCorresponGroupCondition condition) {
        this.condition = condition;
    }

    /**
     * 起動元画面を取得する.
     * @return 起動元画面
     */
    public String getBackPage() {
        return backPage;
    }

    /**
     * 起動元画面をセットする.
     * @param backPage
     *            起動元画面
     */
    public void setBackPage(String backPage) {
        this.backPage = backPage;
    }

    /**
     * 活動単位IDを取得する.
     * @return 活動単位ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 活動単位IDをセットする.
     * @param id
     *            活動単位ID
     */
    public void setId(Long id) {
        this.id = id;
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
     * 拠点情報をセットする.
     * @param site
     *            拠点情報
     */
    public void setSite(Site site) {
        this.site = site;
    }

    /**
     * 拠点情報を取得する.
     * @return 拠点情報
     */
    public Site getSite() {
        return site;
    }

    /**
     * 一覧に表示する活動単位リストをセットする.
     * @param corresponGroupList
     *            一覧に表示する活動単位リスト
     */
    public void setCorresponGroupList(List<CorresponGroup> corresponGroupList) {
        this.corresponGroupList = corresponGroupList;
    }

    /**
     * 一覧に表示する活動単位リストを取得する.
     * @return 一覧に表示する活動単位リスト
     */
    public List<CorresponGroup> getCorresponGroupList() {
        return corresponGroupList;
    }

    /**
     * 一覧に表示する活動単位のDataModelをセットする.
     * @param dataModel
     *            活動単位のDataModel
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * 一覧に表示する活動単位のDataModelを取得する.
     * @return 活動単位のDataModel
     */
    public DataModel<?> getDataModel() {
        if (corresponGroupList != null) {
            dataModel = new ListDataModel<CorresponGroup>(corresponGroupList);
        }
        return dataModel;
    }

    /**
     * 活動単位に属していない部門情報をセットする..
     * @param selectDiscipline
     *            活動単位に属していない部門情報
     */
    public void setSelectDiscipline(Long selectDiscipline) {
        this.selectDiscipline = selectDiscipline;
    }

    /**
     * 活動単位に属していない部門情報を取得する.
     * @return 活動単位に属していない部門情報
     */
    public Long getSelectDiscipline() {
        return selectDiscipline;
    }

    /**
     * 拠点情報を取得する.
     * @return 拠点情報
     */
    public List<Discipline> getDisciplineList() {
        return disciplineList;
    }

    /**
     * 拠点情報をセットする.
     * @param disciplineList
     *            拠点情報
     */
    public void setDisciplineList(List<Discipline> disciplineList) {
        this.disciplineList = disciplineList;
    }

    /**
     * 活動単位に属していない部門情報リストをセットする.
     * @param selectDisciplineList
     *            活動単位に属していない部門情報リスト
     */
    public void setSelectDisciplineList(List<SelectItem> selectDisciplineList) {
        this.selectDisciplineList = selectDisciplineList;
    }

    /**
     * 活動単位に属していない部門情報リストを取得する.
     * @return 活動単位に属していない部門情報リスト
     */
    public List<SelectItem> getSelectDisciplineList() {
        return selectDisciplineList;
    }

    /**
     * 活動単位一覧画面を表示する.
     */
    private void loadGroupIndex() {
        condition.setPageNo(pageNo);
        if (handler.handleAction(new SearchCorresponGroupAction(this))) {
            dataModel = new ListDataModel<CorresponGroup>(corresponGroupList);
        }
    }

    /**
     * 現在のリクエストがaddアクションによって発生した場合はtrue.
     * @return addアクションの場合true
     */
    public boolean isAddAction() {
        return isActionInvoked("form:add");
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
     * 検索条件にログインユーザーの権限を設定する.
     */
    private void setConditionPermission() {
        if (StringUtils.isNotEmpty(getCurrentProjectId())) {
            condition.setProjectAdmin(isProjectAdmin(getCurrentProjectId()));
            if (isAnyGroupAdmin(getCurrentProjectId()) && !isSystemAdmin()
                && !isProjectAdmin(getCurrentProjectId())) {
                condition.setGroupAdmin(true);
                setConditionGroupAdminUser();
            }
        } else {
            condition.setProjectAdmin(false);
            condition.setGroupAdmin(false);
        }
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1535014384956420021L;
        /** アクション発生元ページ. */
        private GroupIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(GroupIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.checkProjectSelected();
            checkPermission();

            // 必須パラメータチェック
            if (page.id == null) {
                throw new ServiceAbortException(
                    "ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            } else if (StringUtils.isEmpty(page.backPage)) {
                throw new ServiceAbortException(
                    "Back Page is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            page.previousCondition =
                    page.getPreviousSearchCondition(SearchCorresponGroupCondition.class);
            // 表示データを取得し、各詳細情報の表示状態の初期値を設定する
            page.pageNo = 1;
            String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
            if (StringUtils.isNotEmpty(strPageRow)) {
                page.pageRowNum = Integer.parseInt(strPageRow);
            }
            String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
            if (StringUtils.isNotEmpty(strPageIndex)) {
                page.pageIndex = Integer.parseInt(strPageIndex);
            }

            page.setSite(page.siteService.find(page.id));

            page.condition = getDefaultCondition();
        }

        /**
         * 権限チェックを行う.
         * @param 編集対象の活動単位ID
         * @throws ServiceAbortException 権限エラー
         */
        private void checkPermission() throws ServiceAbortException {
            if (!page.isSystemAdmin()
                    && !page.isProjectAdmin(page.getCurrentProjectId())
                    && !page.isAnyGroupAdmin(page.getCurrentProjectId())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        }

        /**
         * デフォルトの検索条件を作成する.
         * @return デフォルト検索条件
         */
        private SearchCorresponGroupCondition getDefaultCondition() {
            SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
            condition.setProjectId(page.getCurrentProjectId());
            condition.setSiteId(page.getSite().getId());
            condition.setPageNo(page.getPageNo());
            condition.setPageRowNum(page.getPageRowNum());

            return condition;
        }
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class SearchCorresponGroupAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4682289155973819645L;
        /** アクション発生元ページ. */
        private GroupIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchCorresponGroupAction(GroupIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 初期化
            if (page.corresponGroupList != null) {
                page.corresponGroupList.clear();
            }

            if (page.disciplineList != null) {
                page.disciplineList.clear();
            }
            page.dataCount = 0;

            if (page.isAfterActionSearch) {
                searchAfterAction();
            } else {
                search();
            }
        }

        private void search() throws ServiceAbortException {
            if (log.isDebugEnabled()) {
                log.trace("********** search **********");
                log.debug("siteId[" + page.getId() + "]");
            }
            page.disciplineList = page.corresponGroupService.searchNotAdd(page.getId());
            page.selectDisciplineList =
                page.viewHelper.createSelectItem(page.disciplineList, "id", "codeAndName");

            // ログインユーザーの情報を検索条件にセットする
            page.setConditionPermission();

            SearchCorresponGroupResult result =
                page.corresponGroupService.searchPagingList(page.condition);
            page.corresponGroupList = result.getCorresponGroupList();
            page.dataCount = result.getCount();

        }

        /**
         * 削除・追加アクションの後専用：検索を行う.
         * 再検索の結果0件になってもエラーメッセージを表示しない.
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
                    throw e;
                }
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
        private static final long serialVersionUID = 1470552657640507350L;
        /** アクション発生元ページ. */
        private GroupIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DeleteAction(GroupIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponGroupService.delete((CorresponGroup) page.dataModel.getRowData());

            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.CORRESPON_GROUP_DELETED);
        }
    }

    /**
     * 追加アクション.
     * @author opentone
     */
    static class AddAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -712058175617157012L;
        /** アクション発生元ページ. */
        private GroupIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public AddAction(GroupIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponGroupService.add(page.site, getDiscipline());


            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.DISCIPLINE_ADDED);
        }

        /**
         * 選択された部門を取得する.
         */
        private Discipline getDiscipline() {
            Discipline retDiscipline = null;
            for (Discipline disciplne : page.disciplineList) {
                if (page.selectDiscipline.equals((disciplne.getId()))) {
                    retDiscipline = disciplne;
                    break;
                }
            }
            return retDiscipline;
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
        private static final long serialVersionUID = 7636313357266033802L;
        /** アクション発生元ページ. */
        private GroupIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(GroupIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                String fileName = page.createFileName() + ".xls";
                // 全件指定
                SearchCorresponGroupCondition allRowCondition
                    = (SearchCorresponGroupCondition) page.cloneToAllRowCondition(page.condition);
                SearchCorresponGroupResult result =
                        page.corresponGroupService.searchPagingList(allRowCondition);
                List<CorresponGroup> list = result.getCorresponGroupList();

                byte[] data = page.corresponGroupService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }
}
