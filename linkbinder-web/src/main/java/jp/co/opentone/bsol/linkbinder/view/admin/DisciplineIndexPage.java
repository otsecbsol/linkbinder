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
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Discipline;
import jp.co.opentone.bsol.linkbinder.dto.SearchDisciplineResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchDisciplineCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.DisciplineService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * 部門一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class DisciplineIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7808707908147212340L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "disciplineindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "disciplineindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * 部門情報サービス.
     */
    @Resource
    private DisciplineService disciplineService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchDisciplineCondition condition = null;

    /**
     * 検索条件：部門コード.
     */
    @Transfer
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String code;

    /**
     * 検索条件：部門名.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 検索条件-code.
     */
    @Transfer
    private String scCode;

    /**
     * 検索条件-name.
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
     * 部門情報のデータ.
     */
    @Transfer
    private List<Discipline> disciplineList;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * 部門情報一覧から選択されたレコードのID.
     */
    private Long disciplineRowId;

    /**
     * 部門情報一覧から選択されたレコードのProjectId.
     */
    private String disciplineRowProjId;

    /**
     * 部門情報一覧から選択されたレコードのversionNo.
     */
    private Long disciplineRowVerNo;

    /**
     * 削除アクションフラグ.
     * リロード時にRecordNotFoundエラーを無視するために使用する.
     */
    private boolean isDeleteAction;

    /**
     * コンストラクタ：空のインスタンスを生成する.
     */
    public DisciplineIndexPage() {
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

        condition =
            (SearchDisciplineCondition) getPreviousSearchCondition(SearchDisciplineCondition.class);
        if (condition != null) {
            code = condition.getDisciplineCd();
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
     * 部門情報を作成する.
     * @return 編集画面
     */
    public String create() {
        setNextSearchCondition(condition);
        return toUrl("disciplineEdit");
    }

    /**
     * 部門情報を更新する.
     * @return 編集画面
     */
    public String update() {
        setNextSearchCondition(condition);
        return toUrl(String.format("disciplineEdit?id=%s",
                             getDisciplineRowId()));
    }

    /**
     * 部門情報を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            reloadDisciplineIndex();
        }
        return null;
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        reloadDisciplineIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadDisciplineIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     * @throws ServiceAbortException 再検索時にエラー
     */
    public String changePage() throws ServiceAbortException {
        reloadDisciplineIndex();
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
     * 「前へ」を表示するかどうかの判定.
     * @return true : 「前へ」を表示する
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示するかどうかの判定.
     * @return true : 「次へ」を表示する
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * 検索条件：部門コードを設定する.
     * @param code 検索条件：部門コード
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 検索条件：部門コードを返却する.
     * @return 検索条件：部門コード
     */
    public String getCode() {
        return code;
    }

    /**
     * 検索条件：部門名を設定する.
     * @param name 検索条件：部門名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 検索条件：部門名を取得する.
     * @return 検索条件：部門名
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
     * @param condition 検索条件
     */
    public void setCondition(SearchDisciplineCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を取得する.
     * @return 検索条件
     */
    public SearchDisciplineCondition getCondition() {
        return condition;
    }

    /**
     * 現在のページ№を設定する.
     * @param pageNo 現在のページ№
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 現在のページ№を取得する.
     * @return 現在のページ№
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 総レコード数を設定する.
     * @param dataCount 総レコード数
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
     * @param pageRowNum 画面表示件数
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
     * ページリンク数を取得する.
     * @param pageIndex ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * 部門情報のデータを設定する.
     * @param disciplineList 部門情報のデータ
     */
    public void setDisciplineList(List<Discipline> disciplineList) {
        this.disciplineList = disciplineList;
    }

    /**
     * 部門情報のデータを取得する.
     * @return 部門情報のデータ
     */
    public List<Discipline> getDisciplineList() {
        return disciplineList;
    }

    /**
     * 部門情報のデータモデルを設定する.
     * @param dataModel 部門情報のデータモデル
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * 部門情報のデータモデルを返却する.
     * @return 部門情報のデータモデル
     */
    public DataModel<?> getDataModel() {
        if (disciplineList != null) {
            dataModel = new ListDataModel<Discipline>(disciplineList);
        }

        return dataModel;
    }

    /**
     * @return the disciplineRowId
     */
    public Long getDisciplineRowId() {
        return disciplineRowId;
    }

    /**
     * @param disciplineRowId the disciplineRowId to set
     */
    public void setDisciplineRowId(Long disciplineRowId) {
        this.disciplineRowId = disciplineRowId;
    }

    /**
     * @return the disciplineRowProjId
     */
    public String getDisciplineRowProjId() {
        return disciplineRowProjId;
    }

    /**
     * @param disciplineRowProjId the disciplineRowProjId to set
     */
    public void setDisciplineRowProjId(String disciplineRowProjId) {
        this.disciplineRowProjId = disciplineRowProjId;
    }

    /**
     * @return the disciplineRowVerNo
     */
    public Long getDisciplineRowVerNo() {
        return disciplineRowVerNo;
    }

    /**
     * @param disciplineRowVerNo the disciplineRowVerNo to set
     */
    public void setDisciplineRowVerNo(Long disciplineRowVerNo) {
        this.disciplineRowVerNo = disciplineRowVerNo;
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    private void reloadDisciplineIndex() {
        if (handler.handleAction(new SearchAction(this, false))) {
            dataModel = new ListDataModel<Discipline>(disciplineList);
        }
    }

    /**
     * 検索アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -3708288413436944344L;

        /** アクション発生元ページ. */
        private DisciplineIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * コンストラクタ.
         * @param page アクション発生元ページ
         */
        public SearchAction(DisciplineIndexPage page, boolean parameterSearch) {
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
                page.condition = new SearchDisciplineCondition();
            }

            setCondition();

            // 初期化
            if (page.disciplineList != null) {
                page.disciplineList.clear();
            }
            page.dataCount = 0;

            if (page.isDeleteAction) {
                searchAfterDelete();
            } else {
                search();
            }
        }

        private void setCondition() {
            if (parameterSearch) {
                page.condition.setName(page.getName());
                page.condition.setDisciplineCd(page.getCode());

                page.setScCode(page.getCode());
                page.setScName(page.getName());
            } else {
                page.condition.setDisciplineCd(page.getScCode());
                page.condition.setName(page.getScName());
            }
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
            page.condition.setProjectId(page.getCurrentProjectId());
        }

        private void search() throws ServiceAbortException {
            SearchDisciplineResult result = page.disciplineService.search(page.condition);

            page.disciplineList = result.getDisciplineList();
            page.dataCount = result.getCount();
            page.dataModel = new ListDataModel<Discipline>(page.disciplineList);
        }

        /**
         * 削除アクションの後専用：検索を行う.
         * 削除の結果0件になってもエラーメッセージを表示しない.
         * @throws ServiceAbortException NO_DATA_FOUND以外のエラー
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
        private static final long serialVersionUID = 4318171994705043790L;
        /** アクション発生元ページ. */
        private DisciplineIndexPage page;

        /**
         * コンストラクタ.
         * @param page アクション発生元ページ
         */
        public ExcelDownloadAction(DisciplineIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setDisciplineCd(page.getScCode());
                page.condition.setName(page.getScName());

                String fileName = page.createFileName() + ".xls";
                // 全件指定
                SearchDisciplineCondition allRowCondition
                    = (SearchDisciplineCondition) page.cloneToAllRowCondition(page.condition);
                SearchDisciplineResult result = page.disciplineService.search(allRowCondition);
                List<Discipline> list = result.getDisciplineList();

                byte[] data = page.disciplineService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException("Excel Download failed.", e,
                                                ApplicationMessageCode.E_DOWNLOAD_FAILED);
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
        private static final long serialVersionUID = 6512226876508547566L;
        /** アクション発生元ページ. */
        private DisciplineIndexPage page;

        /**
         * コンストラクタ.
         * @param page アクション発生元ページ
         */
        public DeleteAction(DisciplineIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Discipline discipline = new Discipline();
            discipline.setId(page.getDisciplineRowId());
            discipline.setProjectId(page.getDisciplineRowProjId());
            discipline.setVersionNo(page.getDisciplineRowVerNo());
            page.disciplineService.delete(discipline);

            page.isDeleteAction = true;

            page.setPageMessage(ApplicationMessageCode.DISCIPLINE_DELETED);
        }
    }
}
