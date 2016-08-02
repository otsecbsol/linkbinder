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
package jp.co.opentone.bsol.linkbinder.view.correspon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.FullTextSearchCorresponsResult;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.util.help.HelpContent;
import jp.co.opentone.bsol.linkbinder.view.util.help.HelpContentLoader;

/**
 * コレポン文書全文検索画面.
 *
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponSearchPage extends AbstractPage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2701463890465075767L;

    /**
     * 全文検索条件式についてのヘルプ文書名.
     */
    private static final String HELP_NAME = "fullTextSearchRule";

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "corresponsearch.pagerow";
    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "corresponsearch.pageindex";
    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;
    /**
     * コレポン文書全文検索サービス.
     */
    @Resource
    private CorresponFullTextSearchService corresponFullTextSearchService;
    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;
    /**
     * コレポン文書全文検索結果リスト.
     */
    @Transfer
    private List<FullTextSearchCorresponsResult> fullTextSearchCorresponsResultList;
    /**
     * 現在のページ№.
     */
    @Transfer
    private int pageNo;
    /**
     * ダウンロード対象ファイルのID.
     */
    private Long attachmentId;
    /**
     * ダウンロード対象ファイルが添付されているコレポン文書ID.
     */
    private Long id;
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
     * 検索対象選択肢.
     */
    @Transfer
    private List<SelectItem> fullTextSearchModeSelectList = new ArrayList<SelectItem>();
    /**
     * 検索対象.
     */
    @Transfer
    private FullTextSearchMode[] fullTextSearchModeList = null;
    /**
     * 検索キーワード.
     */
    @Transfer
    private String keyword;
    /**
     * 選択された検索対象.
     */
    @Transfer
    private Integer fullTextSearchMode = null;

    /**
     * 画像を検索対象に含むか否か.
     */
    @Transfer
    private boolean includeImage;
    /**
     * 検索条件.
     */
    @Transfer
    private SearchFullTextSearchCorresponCondition condition = null;
    /**
     * コレポン文書詳細画面へ遷移する際の権限チェックに使用するフィールド.
     * <p>
     * ユーザーが閲覧可能なコレポン文書の場合はtrueがセットされる.
     * </p>
     */
    private boolean visible;

    /**
     * 画面初期化(initialize)時、
     * セッションに保持されているページ番号を使用するかを判断するための値.
     * trueだと使用する、falseだと使用しない.
     * クエリパラメータにてsessionPageNoに値が設定されると同時に値が書き換わる.
     */
    private boolean useSessionPageNo = false;

    /**
     * リロード時にRecordNotFoundエラーを無視するために使用する.
     */
    private boolean afterAction;

    /**
     * 全文検索条件式の説明内容.
     */
    @Transfer
    private String helpContent;

    /**
     * コンストラクタ.
     */
    public CorresponSearchPage() {
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * fullTextSearchCorresponsResultListを取得します.
     *
     * @return the fullTextSearchCorresponsResultList
     */
    public List<FullTextSearchCorresponsResult> getFullTextSearchCorresponsResultList() {
        return fullTextSearchCorresponsResultList;
    }

    /**
     * fullTextSearchCorresponsResultListを設定します.
     *
     * @param fullTextSearchCorresponsResultList the fullTextSearchCorresponsResultList to set
     */
    public void setFullTextSearchCorresponsResultList(
        List<FullTextSearchCorresponsResult> fullTextSearchCorresponsResultList) {
        this.fullTextSearchCorresponsResultList = fullTextSearchCorresponsResultList;
    }

    /**
     * 検索キーワードを設定する.
     *
     * @param keyword
     *            キーワード
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 検索キーワードを返却する.
     *
     * @return keyword
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * 選択された検索対象を設定する.
     * @return 選択された検索対象
     */
    public Integer getFullTextSearchMode() {
        return fullTextSearchMode;
    }

    /**
     * 選択された検索対象を返却する.
     * @param fullTextSearchMode 選択された検索対象
     */
    public void setFullTextSearchMode(Integer fullTextSearchMode) {
        this.fullTextSearchMode = fullTextSearchMode;
    }

    /**
     * 画像を検索対象に含む場合はtrueを返す.
     * @return 結果
     */
    public boolean isIncludeImage() {
        return includeImage;
    }

    /**
     * 画像を検索対象に含むか否かを設定する
     * @param includeImage 設定値
     */
    public void setIncludeImage(boolean includeImage) {
        this.includeImage = includeImage;
    }

    /**
     * 検索対象選択肢を返却する.
     * @return 検索対象選択肢
     */
    public List<SelectItem> getFullTextSearchModeSelectList() {
        if (this.fullTextSearchModeSelectList != null
                && !this.fullTextSearchModeSelectList.isEmpty()) {
            return this.fullTextSearchModeSelectList;
        }

        this.fullTextSearchModeSelectList = viewHelper.createSelectItem(
            fullTextSearchModeList);
        return fullTextSearchModeSelectList;
    }

    /**
     * 検索対象選択肢を設定する.
     * @param fullTextSearchModeSelectList 検索対象選択肢
     */
    public void setFullTextSearchModeSelectList(List<SelectItem> fullTextSearchModeSelectList) {
        this.fullTextSearchModeSelectList = fullTextSearchModeSelectList;
    }

    /**
     * 検索対象リストを設定する.
     * @return 検索対象リスト
     */
    public FullTextSearchMode[] getFullTextSearchModeList() {
        return CloneUtil.cloneArray(FullTextSearchMode.class, fullTextSearchModeList);
    }

    /**
     * 検索対象リストを返却する.
     * @param fullTextSearchModeList 検索対象リスト
     */
    public void setFullTextSearchModeList(FullTextSearchMode[] fullTextSearchModeList) {
        this.fullTextSearchModeList =
            CloneUtil.cloneArray(FullTextSearchMode.class, fullTextSearchModeList);
    }

    /**
     * 選択された検索対象の条件を取得する.
     * @return 検索対象の検索条件
     */
    public FullTextSearchMode getSelectedFullTextSearchMode() {
        for (FullTextSearchMode mode : fullTextSearchModeList) {
            if (mode.getValue().equals(fullTextSearchMode)) {
                return mode;
            }
        }
        return FullTextSearchMode.ALL;
    }

    /**
     * 検索条件を設定する.
     * @param condition 検索条件
     */
    public void setCondition(SearchFullTextSearchCorresponCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を返却する.
     * @return 検索条件
     */
    public SearchFullTextSearchCorresponCondition getCondition() {
        return condition;
    }

    /**
     * データ件数を設定する.
     *
     * @param dataCount
     *             データ件数
     */
    public void setDataCount(int dataCount) {
        this.dataCount = dataCount;
    }
    /**
     * データ件数を返却する.
     *
     * @return dataCount
     */
    public int getDataCount() {
        return dataCount;
    }
    /**
     * 添付ファイルIDを取得する.
     *
     * @return attachmentId
     */
    public Long getAttachmentId() {
        return attachmentId;
    }
    /**
     * 添付ファイルIDを設定する.
     *
     * @param attachmentId
     *              添付ファイルID
     */
    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
    /**
     * コレポン文書IDを取得する.
     *
     * @return id
     */
    public Long getId() {
        return id;
    }
    /**
     * コレポン文書IDを設定する.
     *
     * @param id
     *           コレポン文書ID
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * ページ番号を設定する.
     *
     * @param pageNo
     *            ページ番号
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * ページ番号を取得する.
     *
     * @return pageNo
     */
    public int getPageNo() {
        return pageNo;
    }

    /**
     * 画面表示件数を設定する.
     *
     * @param pageRowNum 画面表示件数
     */
    public void setPageRowNum(int pageRowNum) {
        this.pageRowNum = pageRowNum;
    }

    /**
     * 画面表示件数を返却する.
     *
     * @return 画面表示件数
     */
    public int getPageRowNum() {
        return pageRowNum;
    }

    /**
     * ページリンク数を設定する.
     *
     * @return ページリンク数
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * ページリンク数を返却する.
     *
     * @param pageIndex ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @param sessionPageNo the sessionPageNo to set
     */
    public void setSessionPageNo(String sessionPageNo) {
        // 値が設定されていればセッション中のPageNoを利用する
        if (StringUtils.isNotEmpty(sessionPageNo)) {
            useSessionPageNo = true;
        } else {
            useSessionPageNo = false;
        }
    }
    /**
     * @return the useSessionPageNo
     */
    public boolean isUseSessionPageNo() {
        return useSessionPageNo;
    }

    /**
     * @param useSessionPageNo the useSessionPageNo to set
     */
    public void setUseSessionPageNo(boolean useSessionPageNo) {
        this.useSessionPageNo = useSessionPageNo;
    }

    /**
     * 「前へ」を表示するかどうかの判定.
     *
     * @return true = 「前へ」を表示する
     */
    public boolean isPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示するかどうかの判定.
     *
     * @return true = 「次へ」を表示する
     */
    public boolean isNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * １つ前のページを表示する.
     *
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        loadFullTextSearchCorresponsResult();
        return null;
    }

    /**
     * １つ後のページを表示する.
     *
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        loadFullTextSearchCorresponsResult();
        return null;
    }

    /**
     * 選択したページを表示する.
     *
     * @return null
     */
    public String changePage() {
        loadFullTextSearchCorresponsResult();
        return null;
    }

    /**
     * 画面表示件数の文字列を取得する.
     *
     * @return 画面表示件数
     */
    public String getPageDisplayNo() {
        return PagingUtil.getPageDisplayNo(pageNo, pageRowNum, dataCount);
    }

    /**
     * ページリンクの文字列を取得する.
     *
     * @return ページリンク用配列
     */
    public String[] getPagingNo() {
        return PagingUtil.getPagingNo(pageIndex, pageNo, dataCount, pageRowNum);
    }

    /**
     * @return the afterAction
     */
    public boolean isAfterAction() {
        return afterAction;
    }

    /**
     * @param afterAction the afterAction to set
     */
    public void setAfterAction(boolean afterAction) {
        this.afterAction = afterAction;
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
            loadFullTextSearchCorresponsResult();
        }
    }

    /**
     * 検索を実行する.
     *
     * @return 次遷移画面
     */
    public String search() {
        pageNo = 1;
        setSearchCondition();
        loadFullTextSearchCorresponsResult();
        return null;
    }

    /**
     * 添付ファイルをダウンロードする.
     *
     * @return 次遷移画面
     */
    public String download() {
        handler.handleAction(new DownloadAction(this));
        return null;
    }

    /**
     * コレポン文書表示画面へ遷移する.
     *
     * @return 次遷移画面
     */
    public String show() {
        if (!handler.handleAction(new ShowAction(this))) {
            return null;
        }
        if (isVisible()) {
            return String.format(
                    "correspon?id=%s&projectId=%s&backPage=corresponSearch&fromIndex=1", id,
                    getCurrentProjectId());
        } else {
            return null;
        }
    }

    /**
     * 検索条件を設定する.
     */
    private void setSearchCondition() {
        condition.setKeyword(keyword);
        condition.setFullTextSearchMode(getSelectedFullTextSearchMode());
        condition.setIncludeImage(includeImage);
    }

    /**
     * コレポン文書全文検索の結果を表示する.
     */
    private void loadFullTextSearchCorresponsResult() {
        // 初期化
        dataCount = 0;
        handler.handleAction(new SearchAction(this));
    }

   /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param helpContent the helpContent to set
     */
    public void setHelpContent(String helpContent) {
        this.helpContent = helpContent;
    }

    /**
     * @return the helpContent
     */
    public String getHelpContent() {
        return helpContent;
    }

    /**
     * 画面初期化アクション.
     *
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 6807964787076666977L;

        /** アクション発生元ページ. */
        private CorresponSearchPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponSearchPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // プロジェクト権限チェック
            String projectId = page.getCurrentProjectId();
            if (StringUtils.isEmpty(projectId)) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            //初期化
            page.pageNo = 1;
            page.dataCount = 0;
            page.keyword = null;
            page.fullTextSearchMode = FullTextSearchMode.ALL.getValue();
            page.fullTextSearchCorresponsResultList = null;
            String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
            if (StringUtils.isNotEmpty(strPageRow)) {
                page.pageRowNum = Integer.parseInt(strPageRow);
            }
            String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
            if (StringUtils.isNotEmpty(strPageIndex)) {
                page.pageIndex = Integer.parseInt(strPageIndex);
            }
            setSelectList();
            setConditionFromSession();
            loadHelpContent();
        }

        /**
         * 選択肢を設定する.
         */
        private void setSelectList() {
            page.fullTextSearchModeList = FullTextSearchMode.values();
        }

        private void loadHelpContent() {
            page.helpContent = null;
            HelpContentLoader loader = new HelpContentLoader();
            try {
                HelpContent c = loader.load(HELP_NAME);
                if (c != null) {
                    page.helpContent = c.getContent();
                }
            } catch (IOException e) {
                throw new ApplicationFatalRuntimeException(e);
            }
        }

        /**
         * セッションに保存されているコンディションがあればpageの検索条件として設定する.
         * また、セッションに保持されているpageNoを使用する場合にはそれらをpageに設定する.<br>
         * (loadFullTextSearchCorresponsResult()を呼び出してコレポン文書一覧のデータを検索・
         * 設定する際、pageNoは、page.conditionに設定されているものではなくpageに設定されている
         * 値が使用される).
         */
        private void setConditionFromSession() {
            // セッションに保存されているコンディションがあれば使用
            page.condition = page.getCurrentSearchFullTextSearchCorresponCondition();
            if (page.condition != null) {
                // コンディション中のpageNoを使用するか否かを確認
                if (page.useSessionPageNo
                        && page.condition.getPageNo() > 0) {
                    page.pageNo = page.condition.getPageNo();
                }
                page.keyword = page.condition.getKeyword();
                FullTextSearchMode mode = page.condition.getFullTextSearchMode();
                if (mode != null) {
                    page.fullTextSearchMode = mode.getValue();
                } else {
                    page.fullTextSearchMode = FullTextSearchMode.ALL.getValue();
                }
                page.includeImage = page.condition.isIncludeImage();
            } else {
                page.condition = new SearchFullTextSearchCorresponCondition();
            }
        }
    }

    /**
     * ファイルのダウンロード.
     *
     * @author opentone
     */
    static class DownloadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 2528475221656908028L;

        /** 表示画面. */
        private CorresponSearchPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DownloadAction(CorresponSearchPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            if (page.attachmentId == null) {
                throw new ServiceAbortException("File ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            // IDからコレポン文書情報を取得することで、操作可能なコレポン文書(添付ファイル)か確認する
            Attachment downloading = null;
            try {
                page.corresponService.find(page.getId());
                downloading = page.corresponService.findAttachment(page.id, page.attachmentId);
            } catch (ServiceAbortException e) {
                if (ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT.equals(
                    e.getMessageCode())) {
                    //  参照不可を表す新たな例外を投げる
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON);
                } else {
                    throw e;
                }
            }

            if (downloading.getContent() != null) {
                try {
                    page.viewHelper.download(downloading.getFileName(),
                                             downloading.getContent(),
                                             true);
                } catch (IOException e) {
                    throw new ServiceAbortException("Download failed.", e,
                                                    MessageCode.E_DOWNLOAD_FAILED);
                }
            }
        }
    }

    /**
     * コレポン文書全文検索アクション.
     *
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7459282509358567535L;

        /** アクション発生元ページ. */
        private CorresponSearchPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public SearchAction(CorresponSearchPage page) {
            super(page);
            this.page = page;
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.condition.setPageRowNum(page.pageRowNum);
            page.condition.setPageNo(page.pageNo);

            if (StringUtils.isNotEmpty(page.condition.getKeyword())) {
                try {
                    List<FullTextSearchCorresponsResult> searchResult =
                        page.corresponFullTextSearchService.search(page.condition);

                    page.dataCount = searchResult.size();
                    page.setFullTextSearchCorresponsResultList(
                                                       createSearchResultForView(searchResult));
                } catch (ServiceAbortException e) {
                    if (ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                        if (!page.isAfterAction()) {
                            throw e;
                        }
                    } else {
                        throw e;
                    }
                }
                page.setCurrentSearchFullTextSearchCorresponCondition(page.condition);
            }
        }

        /**
         * １ページ表示分に分割する.
         *
         * @param searchResult
         *            全文検索結果リスト
         * @return FullTextSearchCorresponsResultリスト
         *            全文検索結果リスト
         * @throws ServiceAbortException
         */
        private List<FullTextSearchCorresponsResult> createSearchResultForView(
            List<FullTextSearchCorresponsResult> searchResult) throws ServiceAbortException {

            // 該当データ0件の場合
            if (searchResult.size() == 0) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }

            List<FullTextSearchCorresponsResult> returnList =
                new ArrayList<FullTextSearchCorresponsResult>();
            List<Long> indexIdList = new ArrayList<Long>(100);

            int minDataNum = (page.pageNo - 1) * page.pageRowNum;
            int maxDataNum = page.pageNo * page.pageRowNum;

            if (searchResult.size() < maxDataNum) {
                maxDataNum = searchResult.size();
            }

            // 表示するページの補正
            // 表示しようとしたページ番号までデータが無い場合、最終ページを表示するようにする
            if (minDataNum > maxDataNum) {
                int totalPageCount = searchResult.size() / page.pageRowNum;
                if ((searchResult.size() % page.pageRowNum) > 0) {
                    totalPageCount++;
                }
                minDataNum = (totalPageCount - 1) * page.pageRowNum;
                page.pageNo = totalPageCount;
            }
            page.condition.setPageNo(page.pageNo);

            for (int i = minDataNum; i < maxDataNum; i++) {
                returnList.add(searchResult.get(i));
                // コレポン文書のIDリストの作成
                indexIdList.add(searchResult.get(i).getId());
            }
            // ページ内のコレポン文書IDリストとコレポン文書全件数をSessionに保存する
            page.getViewHelper().setSessionValue(Constants.KEY_CORRESPON_IDS, indexIdList);
            page.getViewHelper().setSessionValue(Constants.KEY_CORRESPON_DATA_COUNT, searchResult.size());

            return returnList;
        }
    }
    /**
     * コレポン文書表示アクション.
     *
     * @author opentone
     */
    static class ShowAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 283365434985558556L;

        /** アクション発生元ページ. */
        private CorresponSearchPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ShowAction(CorresponSearchPage page) {
            super(page);
            this.page = page;
        }
        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.visible = true;
            // IDからコレポン文書情報を取得することで、操作可能なコレポン文書(添付ファイル)か確認する
            try {
                page.corresponService.find(page.getId());
            } catch (ServiceAbortException e) {
                page.visible = false;
                if (ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT.equals(
                    e.getMessageCode())) {

                    //  参照不可を表す新たな例外を投げる
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON);
                } else {
                    throw e;
                }
            }
        }
    }
}
