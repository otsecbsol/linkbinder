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

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.SearchProjectResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * プロジェクト一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class ProjectIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7471974422043568818L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "projectindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "projectindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * プロジェクト情報サービス.
     */
    @Resource
    private ProjectService projectService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchProjectCondition condition = null;

    /**
     * 検索条件：プロジェクトID.
     */
    @Transfer
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 11)
    //CHECKSTYLE:ON
    private String projectId;

    /**
     * 検索条件：プロジェクト名.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 40)
    //CHECKSTYLE:ON
    private String name;

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
     * プロジェクトのデータ.
     */
    @Transfer
    private List<Project> projectList;

    /**
     * 検索条件-ProjectId.
     */
    @Transfer
    private String scProjectId;

    /**
     * 検索条件-NameE.
     */
    @Transfer
    private String scName;

    /**
     * @return the scProjectId
     */
    public String getScProjectId() {
        return scProjectId;
    }

    /**
     * @param scProjectId the scProjectId to set
     */
    public void setScProjectId(String scProjectId) {
        this.scProjectId = scProjectId;
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
     * 空のインスタンスを生成する.
     */
    public ProjectIndexPage() {
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
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
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
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        reloadProjectIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadProjectIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        reloadProjectIndex();
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
     * 検索条件：プロジェクトコードを設定する.
     * @param code
     *            検索条件：プロジェクトコード
     */
    public void setProjectId(String code) {
        this.projectId = code;
    }

    /**
     * 検索条件：プロジェクトコードを取得する.
     * @return 検索条件：プロジェクトコード
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * 検索条件：プロジェクト名を設定する.
     * @param name
     *            検索条件：プロジェクト名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 検索条件：プロジェクト名を取得する.
     * @return 検索条件：プロジェクト名
     */
    public String getName() {
        return name;
    }

    /**
     * 検索条件を設定する.
     * @param condition
     *            検索条件
     */
    public void setCondition(SearchProjectCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を取得する.
     * @return 検索条件
     */
    public SearchProjectCondition getCondition() {
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
     * プロジェクトのデータを設定する.
     * @param projectList
     *            プロジェクトのデータ
     */
    public void setProjectList(List<Project> projectList) {
        this.projectList = projectList;
    }

    /**
     * プロジェクトのデータを取得する.
     * @return プロジェクトのデータ
     */
    public List<Project> getProjectList() {
        return projectList;
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * プロジェクトデータ一覧を更新する.
     */
    private void reloadProjectIndex() {
        handler.handleAction(new SearchAction(this, false));
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4356472293309881132L;

        /** アクション発生元ページ. */
        private ProjectIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchAction(ProjectIndexPage page, boolean parameterSearch) {
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
                page.condition = new SearchProjectCondition();
            }
            // 検索条件をセットする
            setCondition();

            // 初期化
            if (page.projectList != null) {
                page.projectList.clear();
            }
            page.dataCount = 0;
            search();
        }

        /**
         * 検索条件をセットする.
         */
        private void setCondition() {
            if (parameterSearch) {
                page.condition.setProjectId(page.getProjectId());
                page.condition.setNameE(page.getName());

                page.setScProjectId(page.getProjectId());
                page.setScName(page.getName());
            } else {
                page.condition.setProjectId(page.getScProjectId());
                page.condition.setNameE(page.getScName());
            }
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
        }

        /**
         * 検索を行う.
         * @throws ServiceAbortException 検索時にエラー
         */
        private void search() throws ServiceAbortException {
            SearchProjectResult result = page.projectService.searchPagingList(page.condition);
            page.projectList = result.getProjectList();
            page.dataCount = result.getCount();
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
        private static final long serialVersionUID = 4579188208957402307L;
        /** アクション発生元ページ. */
        private ProjectIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(ProjectIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setProjectId(page.getScProjectId());
                page.condition.setNameE(page.getScName());

                String fileName = page.createFileName() + ".xls";
                // 全件指定
                SearchProjectCondition allRowCondition
                    = (SearchProjectCondition) page.cloneToAllRowCondition(page.condition);
                SearchProjectResult result = page.projectService.searchPagingList(allRowCondition);
                List<Project> list = result.getProjectList();

                byte[] data = page.projectService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }
}
