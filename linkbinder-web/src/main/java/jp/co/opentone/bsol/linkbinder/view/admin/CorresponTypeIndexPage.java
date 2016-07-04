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
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.ProjectCustomSetting;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponTypeResult;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.admin.ProjectCustomSettingService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * コレポン文書種別一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponTypeIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7808707908147212340L;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "correspontypeindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "correspontypeindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService corresponTypeService;

    /**
     * プロジェクトカスタム設定画面サービス.
     */
    @Resource
    private ProjectCustomSettingService projectCustomSettingService;

    /**
     * プロジェクトID. SystemAdminの場合はnull.
     */
    @Transfer
    private String projectId;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchCorresponTypeCondition condition = null;

    /**
     * 検索条件：type.
     */
    @Transfer
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String corresponType;

    /**
     * 検索条件：name.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
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
     * 検索条件-Type.
     */
    @Transfer
    private String scType;

    /**
     * 検索条件-Name.
     */
    @Transfer
    private String scName;

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
     * コレポン文書種別のデータ.
     */
    @Transfer
    private List<CorresponType> corresponTypeList;

    /**
     * コレポン文書リストから選択されたレコードのID.
     */
    private Long corrTypeId;

    /**
     * コレポン文書リストから選択されたレコードのVersionNo.
     */
    private Long corrTypeVerNo;

    /**
     * コレポン文書リストから選択されたレコードのprojectCorresponTypeId.
     */
    private Long corrTypeProjId;

    /**
     * コレポン文書リストから選択されたレコードのuseWhole.
     */
    private UseWhole corrTypeUseWhole;
    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * Projectに登録するコレポン種別.
     */
    @Required
    @Transfer
    private Long selectType;

    /**
     * Projectに登録するコレポン種別の選択肢.
     */
    @Transfer
    private List<SelectItem> selectTypeList = new ArrayList<SelectItem>();

    /**
     * ログイン中のプロジェクトに属していないコレポン文書種別.
     */
    @Transfer
    private List<CorresponType> assignableCorresponTypeList;

    /**
     * アクション後の検索フラグ. リロード時にNoDataFoundとNoPageFoundエラーを無視するために使用する.
     */
    private boolean isAfterActionSearch;

    /**
     * プロジェクトカスタム設定情報.
     */
    @Transfer
    private boolean useAccessControl;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponTypeIndexPage() {
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
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備し、ユーザーがアクセス可能な状態であるか検証する.
     * </p>
     */
    @Initialize
    public void initialize() {
        handler.handleAction(new InitializeAction(this));
    }

    /**
     * 検索を行う.
     * @return null
     */
    public String search() {
        pageNo = 1;
        handler.handleAction(new SearchCorresponTypeAction(this, true));
        return null;
    }

    /**
     * 指定したコレポン文書種別をプロジェクトに追加する.
     */
    public void assignTo() {
        if (handler.handleAction(new AssignAction(this))) {
            reloadCorresponTypeIndex();
        }
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
     * コレポン文書種別を作成する.
     * @return 編集画面
     */
    public String create() {
        setNextSearchCondition(condition);
        return toUrl("corresponTypeEdit", isLoginProject());
    }

    /**
     * コレポン文書種別を更新する.
     * @return 編集画面
     */
    public String update() {
        setNextSearchCondition(condition);
      return toUrl(String.format("corresponTypeEdit?id=%s&projectCorresponTypeId=%s",
              getCorrTypeId(),
              getCorrTypeProjId()),
            isLoginProject());
    }

    /**
     * コレポン文書種別を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            reloadCorresponTypeIndex();
        }
        return null;
    }

    /**
     * １つ前のページを表示する.
     * @return null
     * @throws ServiceAbortException
     *             再検索時にエラー
     */
    public String movePrevious() throws ServiceAbortException {
        this.pageNo--;
        reloadCorresponTypeIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     * @throws ServiceAbortException
     *             再検索時にエラー
     */
    public String moveNext() throws ServiceAbortException {
        this.pageNo++;
        reloadCorresponTypeIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     * @throws ServiceAbortException
     *             再検索時にエラー
     */
    public String changePage() throws ServiceAbortException {
        reloadCorresponTypeIndex();
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
     * 「前へ」を表示するかどうか判定する.
     * @return true = 表示する
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示するかどうか判定する.
     * @return true = 表示する
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * プロジェクトIDを取得する.
     * @return プロジェクトID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * プロジェクトIDを取得する.
     * @param projectId プロジェクトID
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    /**
     * 選択条件のコレポン文書種別を設定する.
     * @param corresponType 選択条件のコレポン文書種別
     */
    public void setCorresponType(String corresponType) {
        this.corresponType = corresponType;
    }

    /**
     * 選択条件のコレポン文書種別を取得する.
     * @return 選択条件のコレポン文書種別
     */
    public String getCorresponType() {
        return corresponType;
    }

    /**
     * 選択条件のコレポン文書名を設定する.
     * @param name 選択条件のコレポン文書名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 選択条件のコレポン文書名を取得する.
     * @return 選択条件のコレポン文書名
     */
    public String getName() {
        return name;
    }

    /**
     * 検索条件を設定する.
     * @param condition 検索条件
     */
    public void setCondition(SearchCorresponTypeCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を取得する.
     * @return 検索条件
     */
    public SearchCorresponTypeCondition getCondition() {
        return condition;
    }

    /**
     * ページNoを設定する.
     * @param pageNo ページNo
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * ページNoを取得する.
     * @return ページNo
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
     * @return the scType
     */
    public String getScType() {
        return scType;
    }

    /**
     * @param scType the scType to set
     */
    public void setScType(String scType) {
        this.scType = scType;
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
     * ページリンク数を取得する.
     * @return ページリンク数
     */
    public int getPageIndex() {
        return pageIndex;
    }

    /**
     * ページリンク数を設定する.
     * @param pageIndex ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * コレポン文書種別のデータを設定する.
     * @param corresponTypeList コレポン文書種別のデータ
     */
    public void setCorresponTypeList(List<CorresponType> corresponTypeList) {
        this.corresponTypeList = corresponTypeList;
    }

    /**
     * コレポン文書種別のデータを取得する.
     * @return コレポン文書種別のデータ
     */
    public List<CorresponType> getCorresponTypeList() {
        return corresponTypeList;
    }

    /**
     * @return the corrTypeId
     */
    public Long getCorrTypeId() {
        return corrTypeId;
    }

    /**
     * @param corrTypeId the corrTypeId to set
     */
    public void setCorrTypeId(Long corrTypeId) {
        this.corrTypeId = corrTypeId;
    }

    /**
     * @return the corrTypeVerNo
     */
    public Long getCorrTypeVerNo() {
        return corrTypeVerNo;
    }

    /**
     * @param corrTypeVerNo the corrTypeVerNo to set
     */
    public void setCorrTypeVerNo(Long corrTypeVerNo) {
        this.corrTypeVerNo = corrTypeVerNo;
    }

    /**
     * @return the corrTypeProjId
     */
    public Long getCorrTypeProjId() {
        return corrTypeProjId;
    }

    /**
     * @param corrTypeProjId the corrTypeProjId to set
     */
    public void setCorrTypeProjId(Long corrTypeProjId) {
        this.corrTypeProjId = corrTypeProjId;
    }

    /**
     * @return the corrTypeUseWhole
     */
    public UseWhole getCorrTypeUseWhole() {
        return corrTypeUseWhole;
    }

    /**
     * @param corrTypeUseWhole the corrTypeUseWhole to set
     */
    public void setCorrTypeUseWhole(UseWhole corrTypeUseWhole) {
        this.corrTypeUseWhole = corrTypeUseWhole;
    }

    /**
     * DataModelを取得する.
     * @return DataModel データモデル
     */
    public DataModel<?> getDataModel() {
        if (corresponTypeList != null) {
            dataModel = new ListDataModel<CorresponType>(corresponTypeList);
        }
        return dataModel;
    }

    /**
     * DataModelを設定する.
     * @param dataModel データモデル
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * Projectに登録するコレポン種別を設定する.
     * @param selectType Projectに登録するコレポン種別
     */
    public void setSelectType(Long selectType) {
        this.selectType = selectType;
    }

    /**
     * Projectに登録するコレポン種別を取得する.
     * @return Projectに登録するコレポン種別
     */
    public Long getSelectType() {
        return selectType;
    }

    /**
     * Projectに登録するコレポン種別の選択肢を設定する.
     * @param selectTypeList Projectに登録するコレポン種別
     */
    public void setSelectTypeList(List<SelectItem> selectTypeList) {
        this.selectTypeList = selectTypeList;
    }

    /**
     * Projectに登録するコレポン種別の選択肢を取得する.
     * @return Projectに登録するコレポン種別の選択肢
     */
    public List<SelectItem> getSelectTypeList() {
        return selectTypeList;
    }

    /**
     * ログイン中のプロジェクトに属していないコレポン文書種別を返す.
     * @return コレポン文書種別
     */
    public List<CorresponType> getAssignableCorresponTypeList() {
        return assignableCorresponTypeList;
    }

    /**
     * ログイン中のプロジェクトに属していないコレポン文書種別を設定する.
     * @param assignableCorresponTypeList コレポン文書種別
     */
    public void setAssignableCorresponTypeList(List<CorresponType> assignableCorresponTypeList) {
        this.assignableCorresponTypeList = assignableCorresponTypeList;
    }

    /**
     * @return the useAccessControl
     */
    public boolean isUseAccessControl() {
        return useAccessControl;
    }

    /**
     * @param useAccessControl the useAccessControl to set
     */
    public void setUseAccessControl(boolean useAccessControl) {
        this.useAccessControl = useAccessControl;
    }

    /**
     * コレポン文書種別一覧を再検索する.
     * @throws ServiceAbortException
     */
    private void reloadCorresponTypeIndex() {
        handler.handleAction(new SearchCorresponTypeAction(this, false));
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
     * プロジェクトに登録されていないコレポン文書種別を設定する.
     */
    public void setNotAssignToList() {
        assignableCorresponTypeList =
            corresponTypeService.searchNotAssigned();
        selectTypeList = viewHelper.createSelectItem(assignableCorresponTypeList, "id", "label");
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5272835792028672319L;
        /** アクション発生元ページ. */
        private CorresponTypeIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponTypeIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
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
            // ProjectAdminだったらProjectIdが取得できる
            page.projectId = page.getCurrentProjectId();
            // プロジェクトマスタ管理の場合、プロジェクトに登録するコレポン文書種別を設定
            if (StringUtils.isNotEmpty(page.projectId)) {
                page.setNotAssignToList();
            }
            page.condition =
                (SearchCorresponTypeCondition)
                    page.getPreviousSearchCondition(SearchCorresponTypeCondition.class);
            if (page.condition != null) {
                page.corresponType = page.condition.getCorresponType();
                page.name = page.condition.getName();

                page.scType = page.corresponType;
                page.scName = page.name;
            }
            setDefaultCondition();
            SearchCorresponTypeResult result =
                    page.corresponTypeService.searchPagingList(page.condition);
            if (page.projectId != null) {
                ProjectCustomSetting pcs =
                    page.projectCustomSettingService.find(page.projectId);
                page.useAccessControl = pcs.isUseCorresponAccessControl();
            } else {
                page.useAccessControl = false;
            }
            page.corresponTypeList = result.getCorresponTypeList();
            page.dataCount = result.getCount();
        }

        /**
         * 初期画面表示時の検索条件を取得する.
         * @return 検索条件
         */
        private void setDefaultCondition() {
            if (page.condition == null) {
                page.condition = new SearchCorresponTypeCondition();

            }
            // ProjectIDが遷移元から渡ってきている＝ProjectAdmin
            if (StringUtils.isNotEmpty(page.projectId)) {
                page.condition.setProjectId(page.projectId);
                // SystemAdmin
            } else {
                page.condition.setUseWhole(UseWhole.ALL);
            }
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());

        }

    }

    /**
     * コレポン一覧種別検索アクション.
     * @author opentone
     */
    static class SearchCorresponTypeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 800422507237296120L;

        /** 処理対象. */
        private CorresponTypeIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page ページ
         */
        public SearchCorresponTypeAction(CorresponTypeIndexPage page, boolean parameterSearch) {
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
                page.condition = new SearchCorresponTypeCondition();
            }
            page.projectId = page.getCurrentProjectId();

            setConditionParameter();

            // 初期化
            if (page.corresponTypeList != null) {
                page.corresponTypeList.clear();
            }
            if (page.selectTypeList != null) {
                page.selectTypeList.clear();
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
                page.condition.setName(page.getName());
                page.condition.setCorresponType(page.getCorresponType());

                page.setScType(page.getCorresponType());
                page.setScName(page.getName());
            } else {
                page.condition.setCorresponType(page.getScType());
                page.condition.setName(page.getScName());
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
                page.setNotAssignToList();
            }
            SearchCorresponTypeResult result =
                    page.corresponTypeService.searchPagingList(page.condition);
            page.corresponTypeList = result.getCorresponTypeList();
            page.dataCount = result.getCount();

        }

        /**
         * 削除アクション、追加アクションの後：検索を行う. 削除の結果0件になってもエラーメッセージを表示しない.
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
        private static final long serialVersionUID = -6270951334162731386L;
        /** 処理対象. */
        private CorresponTypeIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(CorresponTypeIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setCorresponType(page.getScType());
                page.condition.setName(page.getScName());

                String fileName = page.createFileName() + ".xls";
                // 全件指定用検索条件に変換
                SearchCorresponTypeCondition allRowCondition =
                        (SearchCorresponTypeCondition) page.cloneToAllRowCondition(page.condition);
                SearchCorresponTypeResult result =
                        page.corresponTypeService.searchPagingList(allRowCondition);
                List<CorresponType> list = result.getCorresponTypeList();

                byte[] data = page.corresponTypeService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    /**
     * コレポン文書種別をプロジェクトに登録するアクション.
     * @author opentone
     */
    static class AssignAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 652413005508024769L;
        /** アクション発生元ページ. */
        private CorresponTypeIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public AssignAction(CorresponTypeIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            CorresponType entryCorresponType = null;

            for (CorresponType corresponType : page.assignableCorresponTypeList) {
                if (page.selectType.equals(corresponType.getId())) {
                    entryCorresponType = corresponType;
                    entryCorresponType.setProjectId(page.projectId);
                    break;
                }
            }
            page.corresponTypeService.assignTo(entryCorresponType);
            page.isAfterActionSearch = true;
            page.setPageMessage(ApplicationMessageCode.CORRESPON_TYPE_ASSIGNED);
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
        private static final long serialVersionUID = -6819591514464261314L;
        /** アクション発生元ページ. */
        private CorresponTypeIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public DeleteAction(CorresponTypeIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            CorresponType corrType = new CorresponType();
            corrType.setProjectId(page.getProjectId());
            corrType.setUseWhole(page.getCorrTypeUseWhole());
            corrType.setId(page.getCorrTypeId());
            corrType.setVersionNo(page.getCorrTypeVerNo());
            corrType.setProjectCorresponTypeId(page.getCorrTypeProjId());
            page.corresponTypeService.delete(corrType);

            page.isAfterActionSearch = true;

            page.setPageMessage(ApplicationMessageCode.CORRESPON_TYPE_DELETED);
        }
    }
}
