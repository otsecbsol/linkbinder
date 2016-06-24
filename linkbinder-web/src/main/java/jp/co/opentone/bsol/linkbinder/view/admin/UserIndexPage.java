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
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.SearchUserResult;
import jp.co.opentone.bsol.linkbinder.dto.UserIndex;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CompanyService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;

/**
 * ユーザー一覧画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class UserIndexPage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4370789988294822483L;

    /**
     * SystemAdminの値（表示用）.
     */
    private static final String VIEW_SYSTEM_ADMIN = "System Admin";

    /**
     * ProjectAdminの値（表示用）.
     */
    private static final String VIEW_PROJECT_ADMIN = "プロジェクト管理者";

    /**
     * GroupAdminの値（表示用）.
     */
    private static final String VIEW_GROUP_ADMIN = "グループ管理者";

    /**
     * NormalUserの値（表示用）.
     */
    private static final String VIEW_NORMAL_USER = "一般ユーザー";

    /**
     * SystemAdminの値.
     */
    private static final String VALUE_SYSTEM_ADMIN_FLG
            = SystemConfig.getValue(Constants.KEY_SECURITY_FLG_SYSTEM_ADMIN);

    /**
     * ProjectAdminの値.
     */
    private static final String VALUE_PROJECT_ADMIN_FLG
            = SystemConfig.getValue(Constants.KEY_SECURITY_FLG_PROJECT_ADMIN);

    /**
     * GroupAdminの値.
     */
    private static final String VALUE_GROUP_ADMIN
            = SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_GROUP_ADMIN);

    /**
     * NormalUserの値.
     */
    private static final String VALUE_NORMAL_USER
            =  SystemConfig.getValue(Constants.KEY_SECURITY_LEVEL_NORMAL_USER);

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "userindex.pagerow";

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "userindex.pageindex";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * 会社サービス.
     */
    @Resource
    private CompanyService companyService;

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchUserCondition condition = null;

    /**
     * 検索条件：従業員番号.
     */
    @Transfer
    @Alphanumeric(allowAlphaNumericOnly = true)
    //CHECKSTYLE:OFF
    @Length(max = 5)
    //CHECKSTYLE:ON
    private String userId;

    /**
     * 検索条件：ユーザー名.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 50)
    //CHECKSTYLE:ON
    private String name;

    /**
     * 検索条件：会社ID.
     */
    @Transfer
    private Long companyId;

    /**
     * 検索条件：活動単位ID.
     */
    @Transfer
    private Long corresponGroupId;

    /**
     * 検索条件：権限レベル.
     */
    @Transfer
    private String securityLevel;

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
     * ユーザーのデータ.
     */
    @Transfer
    private List<UserIndex> userIndexList;

    /**
     * データのDataModel.
     */
    private DataModel<?> dataModel;

    /**
     * ユーザ一覧から選択したレコードのID.
     */
    private String empNo;

    /**
     * 検索条件-scUserId.
     */
    @Transfer
    private String scUserId;

    /**
     * 検索条件-scName.
     */
    @Transfer
    private String scName;

    /**
     * 検索条件-scCompanyId.
     */
    @Transfer
    private Long scCompanyId;

    /**
     * 検索条件-scSecurityLevel.
     */
    @Transfer
    private String scSecurityLevel;

    /**
     * 検索条件-scCorresponGroupId.
     */
    @Transfer
    private Long scCorresponGroupId;

    /**
     * 会社情報の選択肢.
     */
    @Transfer
    private List<SelectItem> companySelectItems;

    /**
     * 活動単位の選択肢.
     */
    @Transfer
    private List<SelectItem> groupSelectItems;

    /**
     * セキュリティレベルの選択肢.
     */
    private static final List<SelectItem> SECURITY_LEVEL_SELECT_ITEMS;
    static {
        SECURITY_LEVEL_SELECT_ITEMS = new ArrayList<SelectItem>();
        SECURITY_LEVEL_SELECT_ITEMS.add(new SelectItem(VIEW_PROJECT_ADMIN, VIEW_PROJECT_ADMIN));
        SECURITY_LEVEL_SELECT_ITEMS.add(new SelectItem(VIEW_GROUP_ADMIN, VIEW_GROUP_ADMIN));
        SECURITY_LEVEL_SELECT_ITEMS.add(new SelectItem(VIEW_NORMAL_USER, VIEW_NORMAL_USER));
    }

    /**
     * 空のインスタンスを生成する.
     */
    public UserIndexPage() {
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
        if (handler.handleAction(new InitializeAction(this))) {
            search();
        }
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
     * ユーザー設定画面に遷移する.
     * @return ユーザー設定画面
     */
    public String goUserSettings() {
        setNextSearchCondition(condition);
        return toUrl(String.format("../userSettings?id=%s&backPage=admin/userIndex",
                             getEmpNo()));
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
        reloadUserIndex();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        reloadUserIndex();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        reloadUserIndex();
        return null;
    }

    /**
     * conditionを取得します.
     * @return the condition
     */
    public SearchUserCondition getCondition() {
        return condition;
    }

    /**
     * conditionを設定します.
     * @param condition the condition to set
     */
    public void setCondition(SearchUserCondition condition) {
        this.condition = condition;
    }

    /**
     * userIdを取得します.
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * userIdを設定します.
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * nameを取得します.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * nameを設定します.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * companyIdを取得します.
     * @return the companyId
     */
    public Long getCompanyId() {
        return companyId;
    }

    /**
     * companyIdを設定します.
     * @param companyId the companyId to set
     */
    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    /**
     * corresponGroupIdを取得します.
     * @return the corresponGroupId
     */
    public Long getCorresponGroupId() {
        return corresponGroupId;
    }

    /**
     * corresponGroupIdを設定します.
     * @param corresponGroupId the corresponGroupId to set
     */
    public void setCorresponGroupId(Long corresponGroupId) {
        this.corresponGroupId = corresponGroupId;
    }

    /**
     * securityLevelを取得します.
     * @return the securityLevel
     */
    public String getSecurityLevel() {
        return securityLevel;
    }

    /**
     * securityLevelを設定します.
     * @param securityLevel the securityLevel to set
     */
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }

    /**
     * userIndexListを取得します.
     * @return the userIndexList
     */
    public List<UserIndex> getUserIndexList() {
        return userIndexList;
    }

    /**
     * userIndexListを設定します.
     * @param userIndexList the userIndexList to set
     */
    public void setUserIndexList(List<UserIndex> userIndexList) {
        this.userIndexList = userIndexList;
    }

    /**
     * dataModelを取得します.
     * @return the dataModel
     */
    public DataModel<?> getDataModel() {
        if (userIndexList != null) {
            dataModel = new ListDataModel<UserIndex>(userIndexList);
        }
        return dataModel;
    }

    /**
     * dataModelを設定します.
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the empNo
     */
    public String getEmpNo() {
        return empNo;
    }

    /**
     * @param empNo the empNo to set
     */
    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    /**
     * @return the scUserId
     */
    public String getScUserId() {
        return scUserId;
    }

    /**
     * @param scUserId the scUserId to set
     */
    public void setScUserId(String scUserId) {
        this.scUserId = scUserId;
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
     * @return the scCompanyId
     */
    public Long getScCompanyId() {
        return scCompanyId;
    }

    /**
     * @param scCompanyId the scCompanyId to set
     */
    public void setScCompanyId(Long scCompanyId) {
        this.scCompanyId = scCompanyId;
    }

    /**
     * @return the scSecurityLevel
     */
    public String getScSecurityLevel() {
        return scSecurityLevel;
    }

    /**
     * @param scSecurityLevel the scSecurityLevel to set
     */
    public void setScSecurityLevel(String scSecurityLevel) {
        this.scSecurityLevel = scSecurityLevel;
    }

    /**
     * @return the scCorresponGroupId
     */
    public Long getScCorresponGroupId() {
        return scCorresponGroupId;
    }

    /**
     * @param scCorresponGroupId the scCorresponGroupId to set
     */
    public void setScCorresponGroupId(Long scCorresponGroupId) {
        this.scCorresponGroupId = scCorresponGroupId;
    }

    /**
     * companySelectItemsを取得します.
     * @return the companySelectItems
     */
    public List<SelectItem> getCompanySelectItems() {
        return companySelectItems;
    }

    /**
     * companySelectItemsを設定します.
     * @param companySelectItems the companySelectItems to set
     */
    public void setCompanySelectItems(List<SelectItem> companySelectItems) {
        this.companySelectItems = companySelectItems;
    }

    /**
     * groupSelectItemsを取得します.
     * @return the groupSelectItems
     */
    public List<SelectItem> getGroupSelectItems() {
        return groupSelectItems;
    }

    /**
     * groupSelectItemsを設定します.
     * @param groupSelectItems the groupSelectItems to set
     */
    public void setGroupSelectItems(List<SelectItem> groupSelectItems) {
        this.groupSelectItems = groupSelectItems;
    }

    /**
     * SECURITY_LEVEL_SELECT_ITEMSを取得します.
     * @return the SECURITY_LEVEL_SELECT_ITEMS
     */
    public List<SelectItem> getSecurityLevelSelectItems() {
        return SECURITY_LEVEL_SELECT_ITEMS;
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
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * ユーザーデータ一覧を更新する.
     */
    private void reloadUserIndex() {
        if (handler.handleAction(new SearchAction(this, false))) {
            dataModel = new ListDataModel<UserIndex>(userIndexList);
        }
    }

    /**
     * 初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 359777802214876980L;
        /** アクション発生元ページ. */
        private UserIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(UserIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.checkProjectSelected();

            String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
            if (StringUtils.isNotEmpty(strPageRow)) {
                page.pageRowNum = Integer.parseInt(strPageRow);
            }
            String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
            if (StringUtils.isNotEmpty(strPageIndex)) {
                page.pageIndex = Integer.parseInt(strPageIndex);
            }

            page.condition =
                (SearchUserCondition) page.getPreviousSearchCondition(SearchUserCondition.class);
            if (page.condition != null) {
                setSearchParameter();
            }
        }

        /**
         * 検索条件をセットする.
         */
        private void setSearchParameter() {
            page.userId = page.condition.getEmpNo();
            page.name = page.condition.getNameE();
            page.companyId = page.condition.getCompanyId();
            page.corresponGroupId = page.condition.getCorresponGroupId();

            setSecurityLevelParameter();
        }

        /**
         * 検索条件：セキュリティレベルを設定する.
         */
        private void setSecurityLevelParameter() {
            if (VALUE_NORMAL_USER.equals(page.condition.getSecurityLevel())) {
                page.securityLevel = VIEW_NORMAL_USER;
            } else if (VALUE_PROJECT_ADMIN_FLG.equals(page.condition.getProjectAdmin())) {
                page.securityLevel = VIEW_PROJECT_ADMIN;
            } else if (VALUE_GROUP_ADMIN.equals(page.condition.getGroupAdmin())) {
                page.securityLevel = VIEW_GROUP_ADMIN;
            } else if (VALUE_SYSTEM_ADMIN_FLG.equals(page.condition.getSysAdminFlg())) {
                page.securityLevel = VIEW_SYSTEM_ADMIN;
            } else {
                page.securityLevel = null;
            }
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
        private static final long serialVersionUID = 322493185721558218L;

        /** アクション発生元ページ. */
        private UserIndexPage page;

        /** 検索条件反映フラグ. */
        private boolean parameterSearch;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public SearchAction(UserIndexPage page, boolean parameterSearch) {
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
                page.condition = new SearchUserCondition();
            }
            // 検索条件をセットする
            setConditionParameter();

            // 初期化
            if (page.userIndexList != null) {
                page.userIndexList.clear();
            }
            page.dataCount = 0;

            // 選択肢を作成
            createSelectItems();

            search();
        }

        /**
         * 検索条件をセットする.
         */
        private void setConditionParameter() {
            page.condition.setPageNo(page.getPageNo());
            page.condition.setPageRowNum(page.getPageRowNum());
            page.condition.setProjectId(page.getCurrentProjectId());

            if (parameterSearch) {
                page.condition.setEmpNo(page.getUserId());
                page.condition.setNameE(page.getName());
                page.condition.setCompanyId(getCompanyId());
                page.condition.setCorresponGroupId(getCorresponGroupId());

                page.setScUserId(page.getUserId());
                page.setScName(page.getName());
                page.setScCompanyId(getCompanyId());
                page.setScCorresponGroupId(getCorresponGroupId());
                page.setScSecurityLevel(page.getSecurityLevel());

                setSecurityLevelParameter();
            } else {
                page.condition.setEmpNo(page.getScUserId());
                page.condition.setNameE(page.getScName());
                page.condition.setCompanyId(page.getScCompanyId());
                page.condition.setCorresponGroupId(page.getScCorresponGroupId());

                // SecurityLevelを一時保存
                String tmpSecLv = page.getSecurityLevel();
                page.setSecurityLevel(page.getScSecurityLevel());
                setSecurityLevelParameter();
                // 画面表示用にSecurityLevelを元に戻す
                page.setSecurityLevel(tmpSecLv);
            }
        }

        /**
         * 検索条件：会社IDを取得する.
         * @return 会社ID
         */
        private Long getCompanyId() {
            if (page.getCompanyId() == null
                    || page.getCompanyId().longValue() < 0) {
                return null;
            }
            return page.getCompanyId();
        }

        /**
         * 検索条件：コレポングループIDを取得する.
         * @return コレポングループID
         */
        private Long getCorresponGroupId() {
            if (page.getCorresponGroupId() == null
                    || page.getCorresponGroupId().longValue() < 0) {
                return null;
            }
            return page.getCorresponGroupId();
        }

        /**
         * 検索条件：セキュリティレベルを設定する.
         */
        private void setSecurityLevelParameter() {
            if (VIEW_SYSTEM_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(VALUE_SYSTEM_ADMIN_FLG);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_PROJECT_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(VALUE_PROJECT_ADMIN_FLG);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_GROUP_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(VALUE_GROUP_ADMIN);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_NORMAL_USER.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(VALUE_SYSTEM_ADMIN_FLG);
                page.condition.setProjectAdmin(VALUE_PROJECT_ADMIN_FLG);
                page.condition.setGroupAdmin(VALUE_GROUP_ADMIN);
                page.condition.setSecurityLevel(VALUE_NORMAL_USER);
            } else {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            }
        }

        /**
         * 選択肢を作成する.
         * @throws ServiceAbortException 検索エラー
         */
        private void createSelectItems() throws ServiceAbortException {
            page.companySelectItems = createCompanySelectItems();
            page.groupSelectItems = createGroupSelectItems();
        }

        /**
         * 会社情報の選択肢を作成する.
         * @return 会社情報の選択肢
         */
        private List<SelectItem> createCompanySelectItems() {
            List<Company> companyList
                = page.companyService.searchRelatedToProject(page.getCurrentProjectId());

            return page.viewHelper.createSelectItem(companyList, "projectCompanyId", "codeAndName");
        }

        /**
         * 会社情報の選択肢を作成する.
         * @return 会社情報の選択肢
         * @throws ServiceAbortException 検索エラー
         */
        private List<SelectItem> createGroupSelectItems() throws ServiceAbortException {
            SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
            condition.setProjectId(page.getCurrentProjectId());

            List<CorresponGroup> groupList = page.corresponGroupService.search(condition);

            return page.viewHelper.createSelectItem(groupList, "id", "name");
        }

        /**
         * 検索を行う.
         * @throws ServiceAbortException 検索時にエラー
         */
        private void search() throws ServiceAbortException {
            try {
                SearchUserResult result = page.userService.searchPagingList(page.condition);
                page.userIndexList = result.getUserIndexes();
                page.dataCount = result.getCount();

            } catch (ServiceAbortException e) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
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
        private static final long serialVersionUID = -9127504858432289357L;
        /** アクション発生元ページ. */
        private UserIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public ExcelDownloadAction(UserIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                page.condition.setEmpNo(page.getScUserId());
                page.condition.setNameE(page.getScName());
                page.condition.setCompanyId(page.getScCompanyId());
                page.condition.setCorresponGroupId(page.getScCorresponGroupId());
                page.setSecurityLevel(page.getScSecurityLevel());
                setSecurityLevelParameter();

                String fileName = page.createFileName() + ".xls";
                // 全件指定
                SearchUserCondition allRowCondition
                    = (SearchUserCondition) page.cloneToAllRowCondition(page.condition);
                SearchUserResult result = page.userService.searchPagingList(allRowCondition);
                List<UserIndex> list = result.getUserIndexes();

                byte[] data = page.userService.generateExcel(list);
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException(
                    "Excel Download failed.", e, ApplicationMessageCode.E_DOWNLOAD_FAILED);
            }
        }

        /**
         * 検索条件：セキュリティレベルを設定する.
         */
        private void setSecurityLevelParameter() {
            if (VIEW_SYSTEM_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(VALUE_SYSTEM_ADMIN_FLG);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_PROJECT_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(VALUE_PROJECT_ADMIN_FLG);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_GROUP_ADMIN.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(VALUE_GROUP_ADMIN);
                page.condition.setSecurityLevel(null);
            } else if (VIEW_NORMAL_USER.equals(page.getSecurityLevel())) {
                page.condition.setSysAdminFlg(VALUE_SYSTEM_ADMIN_FLG);
                page.condition.setProjectAdmin(VALUE_PROJECT_ADMIN_FLG);
                page.condition.setGroupAdmin(VALUE_GROUP_ADMIN);
                page.condition.setSecurityLevel(VALUE_NORMAL_USER);
            } else {
                page.condition.setSysAdminFlg(null);
                page.condition.setProjectAdmin(null);
                page.condition.setGroupAdmin(null);
                page.condition.setSecurityLevel(null);
            }
        }
    }
}
