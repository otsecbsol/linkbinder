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

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.PagingUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.Alphanumeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.DateString;
import jp.co.opentone.bsol.framework.core.validation.constraints.Numeric;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndex;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.SearchCorresponResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponTypeCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.event.CorresponDeleted;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponTypeService;
import jp.co.opentone.bsol.linkbinder.service.admin.CustomFieldService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import jp.co.opentone.bsol.linkbinder.view.util.help.HelpContent;
import jp.co.opentone.bsol.linkbinder.view.util.help.HelpContentLoader;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * コレポン文書一覧画面.
 * @author opentone
 */
// 行数オーバーの警告を抑止
//CHECKSTYLE:OFF
@ManagedBean
@Scope("view")
public class CorresponIndexPage extends AbstractPage {
//CHECKSTYLE:ON

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7518301865798112286L;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(CorresponIndexPage.class);

    /**
     * 検索条件のALL（コレポン文書種別）.
     */
    private static final Long ALL_TYPE = -1L;

    /**
     * 検索条件のALL（ワークフロー）.
     */
    private static final Integer ALL_WORKFLOW = -1;

    /**
     * 検索条件のALL（既読／未読状態）.
     */
    private static final Integer ALL_READ_STATUS = -1;
    /**
     * 検索条件のALL（学習用プロジェクト）.
     */
    private static final Integer ALL_FOR_LEARNING = -1;

    /**
     * 高度検索のリストボックスのデフォルト値.
     */
    private static final Integer DEFAULT_SELECT_ITEM_VALUE = -1;

    /**
     * 1ページあたりの表示件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_ROW = "corresponindex.pagerow";

    /**
     * 1ページあたりの表示件数を保存しておくCookieの名前.
     */
    private static final String PAGE_ROW_COOKIE_NAME = "CORRESPONINDEXPAGEROW";

    /**
     * 1ページあたりの表示件数として設定可能な値.
     */
    private static final int[] PAGE_ROW_SELECTABLE_VALUES = {10, 20, 30, 50, 100};

    /**
     * ページリンクの件数を取得するためのKEY.
     */
    private static final String KEY_PAGE_INDEX = "corresponindex.pageindex";

    /**
     * CorresponNo のプレースホルダテキストキー.
     */
    private static final String KEY_CORRESPON_NO_PH_TXT  = "correspon.corresponIndex.corresponNo";

    /**
     * 検索項目が削除されていた場合に代替表示する文字列のキー.
     */
    private static final String KEY_UNKNOWN_TXT = "correspon.corresponIndex.unknown";

    /**
     * 検索結果を1画面に表示する件数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_ROW_NUMBER = 10;
    /**
     * 検索結果をページ遷移するためのリンクを表示する数のデフォルト値.
     */
    public static final int DEFAULT_PAGE_INDEX_NUMBER = 10;

    /**
     * 全文検索条件式についてのヘルプ文書名.
     */
    // TODO Helpは全文検索画面と同じなので、リファクタリングが必要
    private static final String HELP_NAME = "fullTextSearchRule";

    /**
     * 画面表示カラムの非表示設定.
     */
    public static final String NON_DISPLAY = "false";

    /**
     * 表示.
     * <p>
     * 詳細表示エリアの表示状態.
     * </p>
     */
    public static final String STYLE_SHOW = "block";

    /**
     * 非表示.
     * <p>
     * 詳細表示エリアの表示状態.
     * </p>
     */
    public static final String STYLE_HIDE = "none";

    /**
     * TRタグ制御Classの区切り文字.
     */
    public static final String DELIM_CLASS = ",";

    /**
     * TRタグ制御Class：無効.
     */
    public static final String CANCELED = "canceled";

    /**
     * TRタグ制御Class：返信期限切れ.
     */
    public static final String DEADLINE = "deadline";

    /**
     * TRタグ制御Class：奇数行.
     */
    public static final String ODD = "odd";

    /**
     * TRタグ制御Class：偶数行.
     */
    public static final String EVEN = "even";

    /**
     * 選択肢の空行（Enum型のコード）.
     */
    public static final Integer RESET_LINE_CODE = -1;

    /**
     * 選択肢の空行（ID）.
     */
    public static final Long RESET_LINE_OBJECT = -1L;

    /**
     * 表示用の検索条件の区切り文字.
     */
    public static final String DELIM_CONDITION = ",";

    /**
     * 表示用の検索条件の表示最大数.
     */
    public static final int MAX_CONDITION = 3;

    /**
     * 表示用の検索条件が最大値を超えたときに付与される文字列.
     */
    public static final String TEXT_MORE_CONDITION = "...";

    /**
     * Correspondence Noのプレースホルダーに表示するテキスト.
     */
    private String corresponNoPhTxt = SystemConfig.getValue(KEY_CORRESPON_NO_PH_TXT);

    /**
     * コレポン文書検索サービス.
     */
    @Resource
    private CorresponSearchService corresponSearchService;

    /**
     * コレポン文書種別サービス.
     */
    @Resource
    private CorresponTypeService corresponTypeService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * カスタムフィールドサービス.
     */
    @Resource
    private CustomFieldService customFieldService;

    /**
     * お気に入りサービス.
     */
    @Resource
    private FavoriteFilterService favoriteFilterService;

    /**
     * 検索条件.
     */
    @Transfer
    private SearchCorresponCondition condition = null;

    /**
     * シンプルな検索フラグ.
     */
    @Transfer
    private boolean simpleSearch;

    /**
     * コレポン文書種別リスト.
     */
    @Transfer
    private List<CorresponType> typeList = null;

    /**
     * 承認状態リスト.
     */
    @Transfer
    private WorkflowStatus[] workflowList = null;

    /**
     * 未読／既読状態リスト.
     */
    @Transfer
    private ReadStatus[] readStatusList = null;

    /**
     * 学習用文書可否リスト.
     */
    @Transfer
    private ForLearning[] forLearningList = null;

    /**
     * 文書状態リスト.
     */
    @Transfer
    private CorresponStatus[] statusList = null;

    /**
     * 承認フロー状態リスト.
     */
    @Transfer
    private WorkflowProcessStatus[] workflowProcessesList = null;

    /**
     * ユーザーリスト.
     */
    @Transfer
    private List<ProjectUser> userList = null;

    /**
     * 活動単位リスト.
     */
    @Transfer
    private List<CorresponGroup> groupList = null;

    /**
     * カスタムフィールドリスト.
     */
    @Transfer
    private List<CustomField> customFieldList = null;

    /**
     * シンプルな検索：コレポン文書種別.
     */
    @Transfer
    private Long type = null;

    /**
     * シンプルな検索：承認状態.
     */
    @Transfer
    private Integer workflow = null;

    /**
     * シンプルな検索：未読／既読状態.
     */
    @Transfer
    private Integer readStatus = null;

    /**
     * シンプルな検索：学習用文書.
     */
    @Transfer
    private Integer forLearning = null;

    /**
     * 学習用コンテンツ検索：ラベル
     */
    @Transfer
    private String learningLabel = null;

    /**
     * 学習用コンテンツ検索：タグ
     */
    @Transfer
    private String learningTag = null;

    /**
     * 学習用コンテンツ検索：キーワード
     */
    private String learningKeyword = null;

    /**
     * 高度な検索：コレポン文書SequenceNo.
     */
    @Transfer
    @Numeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String sequenceNo;

    /**
     * 高度な検索：コレポン文書識別No（From).
     */
    @Transfer
    @Numeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String fromNo;

    /**
     * 高度な検索：コレポン文書識別No（To).
     */
    @Transfer
    @Numeric
    //CHECKSTYLE:OFF
    @Length(max = 10)
    //CHECKSTYLE:ON
    private String toNo;

    /**
     * 高度な検索：コレポン文書No.
     */
    @Transfer
    @Alphanumeric
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String corresponNo;

    /**
     * 高度な検索：コレポン文書Noの改訂後のコレポン文書も対象とする.
     */
    @Transfer
    private boolean includingRevision;

    /**
     * 高度な検索：コレポン文書種別選択肢.
     */
    @Transfer
    private List<SelectItem> typeSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：承認状態選択肢.
     */
    @Transfer
    private List<SelectItem> workflowStatusSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：既読／未読状態選択肢.
     */
    @Transfer
    private List<SelectItem> readStatusSelectList = new ArrayList<SelectItem>();
    /**
     * 高度な検索：文書状態選択肢.
     */
    @Transfer
    private List<SelectItem> statusSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：ユーザー選択肢.
     */
    @Transfer
    private List<SelectItem> userSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：検索対象選択肢.
     */
    @Transfer
    private List<SelectItem> fullTextSearchModeSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：検索対象.
     */
    @Transfer
    private FullTextSearchMode[] fullTextSearchModeList = null;

    /**
     * 高度な検索：検索キーワード.
     */
    @Transfer
    private String keyword;

    /**
     * 高度な検索：選択された検索対象.
     */
    @Transfer
    private Integer fullTextSearchMode = null;

    /**
     * 高度な検索：Preparer.
     */
    @Transfer
    private boolean userPreparer;

    /**
     * 高度な検索：Checker.
     */
    @Transfer
    private boolean userChecker;

    /**
     * 高度な検索：Approver.
     */
    @Transfer
    private boolean userApprover;

    /**
     * 高度な検索：Attention.
     */
    @Transfer
    private boolean userAttention;

    /**
     * 高度な検索：Cc.
     */
    @Transfer
    private boolean userCc;

    /**
     * 高度な検索：Person in Charge.
     */
    @Transfer
    private boolean userPic;

    /**
     * 高度な検索：Unreplied(User).
     */
    @Transfer
    private boolean userUnreplied;

    /**
     * 高度な検索：承認フロー状態選択肢.
     */
    @Transfer
    private List<SelectItem> workflowProcessSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：活動単位選択肢.
     */
    @Transfer
    private List<SelectItem> groupSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：活動単位宛先（Cc）.
     */
    @Transfer
    private boolean groupCc;

    /**
     * 高度な検索：活動単位宛先（To）.
     */
    @Transfer
    private boolean groupTo;

    /**
     * 高度な検索：活動単位宛先未返信.
     */
    @Transfer
    private boolean groupUnreplied;

    /**
     * 高度な検索：作成日（From）.
     */
    @Transfer
    @DateString
    private String fromCreated;

    /**
     * 高度な検索：作成日（to）.
     */
    @Transfer
    @DateString
    private String toCreated;

    /**
     * 高度な検索：発行日（From）.
     */
    @Transfer
    @DateString
    private String fromIssued;

    /**
     * 高度な検索：発行日（to）.
     */
    @Transfer
    @DateString
    private String toIssued;

    /**
     * 高度な検索：返信期限（From）.
     */
    @Transfer
    @DateString
    private String fromReply;

    /**
     * 高度な検索：返信期限（to）.
     */
    @Transfer
    @DateString
    private String toReply;

    /**
     * 高度な検索：Custom Field選択肢.
     */
    @Transfer
    private List<SelectItem> customFieldSelectList = new ArrayList<SelectItem>();

    /**
     * 高度な検索：選択されたCustom Field No.
     */
    @Transfer
    private Long customFieldNo;

    /**
     * 高度な検索：Custom Field値.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String customFieldValue;

    /**
     * 高度な検索：選択されたコレポン文書種別.
     */
    @Transfer
    private Long[] types = null;

    /**
     * 高度な検索：選択された承認状態.
     */
    @Transfer
    private Integer[] workflowStatuses = null;

    /**
     * 高度な検索：選択された既読／未読状態.
     */
    @Transfer
    private Integer[] readStatuses = null;

    /**
     * 高度な検索：選択された文書状態.
     */
    @Transfer
    private Integer[] statuses = null;

    /**
     * 高度な検索：選択された送信元、宛先、担当者ユーザー.
     */
    @Transfer
    private String[] fromUsers = null;

    /**
     * 高度な検索：選択された送信元、宛先、担当者ユーザー.
     */
    @Transfer
    private String[] toUsers = null;

    /**
     * 高度な検索：選択された承認フロー状態.
     */
    @Transfer
    private Integer[] workflowProcesses = null;

    /**
     * 高度な検索：選択された活動単位.
     */
    @Transfer
    private Long[] fromGroups = null;

    /**
     * 高度な検索：選択された活動単位.
     */
    @Transfer
    private Long[] toGroups = null;

    /**
     * 高度な検索の表示状態.
     */
    @Transfer
    private boolean advancedSearchDisplayed;

    /**
     * 高度な検索の表示を行ったかどうか.
     */
    private boolean advancedSearchRequested;

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
     * コレポン文書一覧のヘッダ.
     */
    @Transfer
    private CorresponIndexHeader header = null;

    /**
     * コレポン文書のデータ.
     */
    @Transfer
    private List<Correspon> corresponList = null;

    /**
     * コレポン文書一覧のデータ.
     */
    @Transfer
    private List<CorresponIndex> indexList = null;

    /**
     * ソートカラム.
     */
    @Transfer
    private String sort;

    /**
     * ソートの昇順／降順.
     */
    @Transfer
    private boolean ascending;

    /**
     * 学習用プロジェクトID
     */
    @Transfer
    private String learningPjId;

    /**
     * 学習用コンテンツプロジェクトか否か.
     */
    @Transfer
    private boolean learningPj;

    /**
     * リロード時にRecordNotFoundエラーを無視するために使用する.
     */
    private boolean afterAction;

    /**
     * TRタグ制御Class指定.
     */
    @Transfer
    private String rowClasses;

    /**
     * Simple SearchでTypeを選択したことを示す値.
     */
    private static final int SIMPLESEARCH_SELECTED_TYPE = 1;

    /**
     * Simple SearchでWorkflowStatusを選択したことを示す値.
     */
    private static final int SIMPLESEARCH_SELECTED_WORKFLOWSTATUS = 2;

    /**
     * Simple SearchでReadStatusを選択したことを示す値.
     */
    private static final int SIMPLESEARCH_SELECTED_READSTATUS = 3;

    /**
     * Simple SearchでForLearningを選択したことを示す値.
     */
    private static final int SIMPLESEARCH_SELECTED_FORLEARNING = 4;
    /**
     * Simple Searchで何も選択していないことを示す値.
     */
    private static final int SIMPLESEARCH_SELECTED_NONE = 0;

    /**
     * Simple SearchでType, WorkflowStatus, ReadStatus, ForLearningの
     * どれを選択したのか判別するために使用する.
     * corresponIndex.jspにより値が設定される.
     */
    private int simpleSearchSelectedItem = SIMPLESEARCH_SELECTED_NONE;

    /**
     * 画面初期化(initialize)時、
     * セッションに保持されているソートアイテムとソート順を使用するかを判断するための値.
     * trueだと使用する、falseだと使用しない.
     * クエリパラメータにてsessionSortに値が設定されると同時に値が書き換わる.
     */
    private boolean useSessionSort = false;

    /**
     * 画面初期化(initialize)時、
     * セッションに保持されているページ番号を使用するかを判断するための値.
     * trueだと使用する、falseだと使用しない.
     * クエリパラメータにてsessionPageNoに値が設定されると同時に値が書き換わる.
     */
    private boolean useSessionPageNo = false;

    /**
     * 1ページの表示件数を変更した検索か否かを判断するために使用する.
     */
    private boolean pageRowChangeSearch = false;

    /**
     * お気に入りID.
     */
    private Long favoriteFilterId;

    /**
     * お気に入り名.
     */
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String favoriteFilterName;

    /**
     * ダイアログ表示フラグ.
     */
    @Transfer
    private boolean favoriteFilterDisplay;

    /**
     * 全文検索条件式の説明内容.
     */
    @Transfer
    private String helpContent;

    /**
     * @return the useSessionSort
     */
    public boolean isUseSessionSort() {
        return useSessionSort;
    }

    /**
     * @param useSessionSort the useSessionSort to set
     */
    public void setUseSessionSort(boolean useSessionSort) {
        this.useSessionSort = useSessionSort;
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
     * コンストラクタ.
     */
    public CorresponIndexPage() {
    }

    /**
     * Correspondence Noのプレースホルダーに表示するテキストを取得する.
     * @return the corresponNoPhTxt
     */
    public String getCorresponNoPhTxt() {
        return corresponNoPhTxt;
    }

    /**
     * @param corresponNoPhTxt the corresponNoPhTxt to set
     */
    public void setCorresponNoPhTxt(String corresponNoPhTxt) {
        this.corresponNoPhTxt = corresponNoPhTxt;
    }

    /**
     * 入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:search")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    public String getValidationFavoriteGroups() {
        if (isActionInvoked("form:addFavoriteFilter")) {
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
            loadCorresponIndex();
            loadHelpContent();
            hideAdvancedSearch();
        }

        if(isLearningProject()) {
            setLearningPj(true);
        } else {
            setLearningPj(false);
        }
    }

    /**
     * 検索を実行する.
     * @return null
     */
    public String search() {
        pageNo = 1;
        setSearchCondition();
        loadCorresponIndex();
        hideAdvancedSearch();
        return null;
    }

    /**
     * 1ページの表示件数を変更して検索を再実行する.
     * @return null
     */
    public String changePageRowNum() {
        pageRowChangeSearch = true;
        loadCorresponIndex();
        hideAdvancedSearch();
        return null;
    }

    /**
     * 一覧画面の印刷用ページを表示する.
     * @return null
     */
    public String printHTML() {
        handler.handleAction(new HtmlPrintAction(this));
        return null;
    }

    /**
     * 一覧画面をCSVファイルでダウンロードする.
     * @return null
     */
    public String downloadCsv() {
        handler.handleAction(new CsvDownloadAction(this));
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
     * 印刷用の画面をZIP形式でダウンロードする.
     * @return null
     */
    public String downloadZip() {
        handler.handleAction(new ZipDownloadAction(this));
        return null;
    }

    /**
     * 選択したコレポン文書を既読にする.
     * @return null
     */
    public String read() {
        if (handler.handleAction(new UpdateReadAction(this, true))) {
            loadCorresponIndex();
        }
        hideAdvancedSearch();
        return null;
    }

    /**
     * 選択したコレポン文書を未読にする.
     * @return null
     */
    public String unread() {
        if (handler.handleAction(new UpdateReadAction(this, false))) {
            loadCorresponIndex();
        }
        hideAdvancedSearch();
        return null;
    }

    /**
     * 一覧のソート.
     * @return null
     * @throws ServiceAbortException ソートエラー
     */
    public String sortIndex() throws ServiceAbortException {
        // ソート（再検索）
        if (condition != null && pageNo != 0) {
            loadCorresponIndex();
            hideAdvancedSearch();
        }
        return null;
    }

    /**
     * 選択したコレポン文書をOpenにする.
     * @return null
     */
    public String open() {
        if (handler.handleAction(new UpdateStatusAction(this, true))) {
            loadCorresponIndex();
        }
        hideAdvancedSearch();
        return null;
    }

    /**
     * 選択したコレポン文書をClosedにする.
     * @return null
     */
    public String close() {
        if (handler.handleAction(new UpdateStatusAction(this, false))) {
            loadCorresponIndex();
        }
        hideAdvancedSearch();
        return null;
    }

    /**
     * 選択したコレポン文書を削除する.
     * @return null
     */
    public String delete() {
        if (handler.handleAction(new DeleteAction(this))) {
            loadCorresponIndex();
        }
        hideAdvancedSearch();
        return null;
    }

    /**
     * １つ前のページを表示する.
     * @return null
     */
    public String movePrevious() {
        this.pageNo--;
        loadCorresponIndex();
        hideAdvancedSearch();
        return null;
    }

    /**
     * １つ後のページを表示する.
     * @return null
     */
    public String moveNext() {
        this.pageNo++;
        loadCorresponIndex();
        hideAdvancedSearch();
        return null;
    }

    /**
     * 選択したページを表示する.
     * @return null
     */
    public String changePage() {
        loadCorresponIndex();
        hideAdvancedSearch();
        return null;
    }

    /**
     * @param sessionSort the sessionSort to set
     */
    public void setSessionSort(String sessionSort) {
        // 値が設定されていればセッション中のsortを利用する
        if (StringUtils.isNotEmpty(sessionSort)) {
            useSessionSort = true;
        } else {
            useSessionSort = false;
        }
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
     * お気に入り追加処理.
     * @return null
     */
    public String addFavoriteFilter() {
        if (handler.handleAction(new AddFavoriteFilterAction(this))) {
            favoriteFilterDisplay = false;
        }
        return null;
    }

    /**
     * 検索条件を設定する.
     * @param condition 検索条件
     */
    public void setCondition(SearchCorresponCondition condition) {
        this.condition = condition;
    }

    /**
     * 検索条件を返却する.
     * @return 検索条件
     */
    public SearchCorresponCondition getCondition() {
        return condition;
    }

    /**
     * シンプルな検索フラグを返却する.
     * @return シンプルな検索フラグ
     */
    public boolean isSimpleSearch() {
        return simpleSearch;
    }

    /**
     * シンプルな検索フラグを設定する.
     * @param simpleSearch シンプルな検索フラグ
     */
    public void setSimpleSearch(boolean simpleSearch) {
        this.simpleSearch = simpleSearch;
    }

    /**
     * コレポン文書種別リストを設定する.
     * @return コレポン文書種別リスト
     */
    public List<CorresponType> getTypeList() {
        return typeList;
    }

    /**
     * コレポン文書種別リストを返却する.
     * @param typeList コレポン文書種別リスト
     */
    public void setTypeList(List<CorresponType> typeList) {
        this.typeList = typeList;
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
     * 承認状態リストを設定する.
     * @return 承認状態リスト
     */
    public WorkflowStatus[] getWorkflowList() {
        return CloneUtil.cloneArray(WorkflowStatus.class, workflowList);
    }

    /**
     * 承認状態リストを返却する.
     * @param workflowList 承認状態リスト
     */
    public void setWorkflowList(WorkflowStatus[] workflowList) {
        this.workflowList = CloneUtil.cloneArray(WorkflowStatus.class, workflowList);
    }

    /**
     * 未読／既読状態リストを設定する.
     * @return 未読／既読状態リスト
     */
    public ReadStatus[] getReadStatusList() {
        return CloneUtil.cloneArray(ReadStatus.class, readStatusList);
    }

    /**
     * 未読／既読状態リストを返却する.
     * @param readStatusList 未読／既読状態リスト
     */
    public void setReadStatusList(ReadStatus[] readStatusList) {
        this.readStatusList = CloneUtil.cloneArray(ReadStatus.class, readStatusList);
    }

    /**
     * 学習用文書可否リストを返却する.
     * @return 未読／既読状態リスト
     */
    public ForLearning[] getForLearningList() {
        return CloneUtil.cloneArray(ForLearning.class, forLearningList);
    }

    /**
     * 学習用文書可否リストを設定する.
     * @param forLearningList 学習用文書可否リスト
     */
    public void setForLearningList(ForLearning[] forLearningList) {
        this.forLearningList = CloneUtil.cloneArray(ForLearning.class, forLearningList);
    }

    /**
     * 文書状態リストを設定する.
     * @return 文書状態リスト
     */
    public CorresponStatus[] getStatusList() {
        return CloneUtil.cloneArray(CorresponStatus.class, statusList);
    }

    /**
     * 文書状態リストを返却する.
     * @param statusList 文書状態リスト
     */
    public void setStatusList(CorresponStatus[] statusList) {
        this.statusList = CloneUtil.cloneArray(CorresponStatus.class, statusList);
    }

    /**
     * 承認フロー状態リストを設定する.
     * @return 承認フロー状態リスト
     */
    public WorkflowProcessStatus[] getWorkflowProcessesList() {
        return CloneUtil.cloneArray(WorkflowProcessStatus.class, workflowProcessesList);
    }

    /**
     * 承認フロー状態リストを返却する.
     * @param workflowProcessesList 承認フロー状態リスト
     */
    public void setWorkflowProcessesList(WorkflowProcessStatus[] workflowProcessesList) {
        this.workflowProcessesList = CloneUtil.cloneArray(WorkflowProcessStatus.class,
                                                          workflowProcessesList);
    }

    /**
     * ユーザーリストを設定する.
     * @return ユーザーリスト
     */
    public List<ProjectUser> getUserList() {
        return userList;
    }

    /**
     * ユーザーリストを返却する.
     * @param userList ユーザーリスト
     */
    public void setUserList(List<ProjectUser> userList) {
        this.userList = userList;
    }

    /**
     * 活動単位リストを設定する.
     * @return 活動単位リスト
     */
    public List<CorresponGroup> getGroupList() {
        return groupList;
    }

    /**
     * 活動単位リストを返却する.
     * @param groupList 活動単位リスト
     */
    public void setGroupList(List<CorresponGroup> groupList) {
        this.groupList = groupList;
    }

    /**
     * カスタムフィールドリストを設定する.
     * @return カスタムフィールドリスト
     */
    public List<CustomField> getCustomFieldList() {
        return customFieldList;
    }

    /**
     * カスタムフィールドリストを返却する.
     * @param customFieldList カスタムフィールドリスト
     */
    public void setCustomFieldList(List<CustomField> customFieldList) {
        this.customFieldList = customFieldList;
    }

    /**
     * シンプルな検索：コレポン文書種別を設定する.
     * @return シンプルな検索：コレポン文書種別
     */
    public Long getType() {
        return type;
    }

    /**
     * シンプルな検索：コレポン文書種別を返却する.
     * @param type シンプルな検索：コレポン文書種別
     */
    public void setType(Long type) {
        this.type = type;
    }

    /**
     * シンプルな検索：承認状態を設定する.
     * @return シンプルな検索：承認状態
     */
    public Integer getWorkflow() {
        return workflow;
    }

    /**
     * シンプルな検索：承認状態を返却する.
     * @param workflow シンプルな検索：承認状態
     */
    public void setWorkflow(Integer workflow) {
        this.workflow = workflow;
    }

    /**
     * シンプルな検索：未読／既読状態を設定する.
     * @return シンプルな検索：未読／既読状態
     */
    public Integer getReadStatus() {
        return readStatus;
    }

    /**
     * シンプルな検索：未読／既読状態を返却する.
     * @param readStatus シンプルな検索：未読／既読状態
     */
    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }
    /**
     * シンプルな検索：学習用文書種類を返却する.
     * @return シンプルな検索：学習用文書種類
     */
    public Integer getForLearning() {
        return forLearning;
    }

    /**
     * シンプルな検索：学習用文書種類を設定する.
     * @param forLearning シンプルな検索：学習用文書種類
     */
    public void setForLearning(Integer forLearning) {
        this.forLearning = forLearning;
    }

    /**
     * 学習用コンテンツ検索：ラベルを取得する.
     * @return ラベル.
     */
    public String getLearningLabel() {
        return learningLabel;
    }

    /**
     * 学習用コンテンツ検索：ラベルを設定する.
     */
    public void setLearningLabel(String label) {
        this.learningLabel = label;
    }

    /**
     * 学習用コンテンツ検索：タグを取得する.
     * @return タグ
     */
    public String getLearningTag() {
        return learningTag;
    }

    /**
     * 学習用コンテンツ検索：タグを設定する.
     */
    public void setLearningTag(String tag) {
        this.learningTag = tag;
    }

    /**
     * 学習用コンテンツ検索：キーワードを取得する.
     * @return キーワード
     */
    public String getLearningKeyword() {
        return this.learningKeyword;
    }

    /**
     * 学習用コンテンツ検索：キーワードを設定する.
     */
    public void setLearningKeyword(String keyword) {
        this.learningKeyword = keyword;
    }

    /**
     * 高度な検索：コレポン文書識別No（From)を設定する.
     * @return 高度な検索：コレポン文書識別No（From)
     */
    public String getFromNo() {
        return fromNo;
    }

    /**
     * 高度な検索：コレポン文書識別No（From)を返却する.
     * @param fromNo 高度な検索：コレポン文書識別No（From)
     */
    public void setFromNo(String fromNo) {
        this.fromNo = fromNo;
    }

    /**
     * 高度な検索：コレポン文書識別No（To)を設定する.
     * @return 高度な検索：コレポン文書識別No（To)
     */
    public String getToNo() {
        return toNo;
    }

    /**
     * 高度な検索：コレポン文書識別No（To)を返却する.
     * @param toNo 高度な検索：コレポン文書識別No（To)
     */
    public void setToNo(String toNo) {
        this.toNo = toNo;
    }

    /**
     * 高度な検索：コレポン文書Noを設定する.
     * @return 高度な検索：コレポン文書No
     */
    public String getCorresponNo() {
        return corresponNo;
    }

    /**
     * 高度な検索：コレポン文書Noを返却する.
     * @param corresponNo 高度な検索：コレポン文書No
     */
    public void setCorresponNo(String corresponNo) {
        this.corresponNo = corresponNo;
    }

    /**
     * includingRevisionを取得します.
     * @return the includingRevision
     */
    public boolean isIncludingRevision() {
        return includingRevision;
    }

    /**
     * includingRevisionを設定します.
     * @param includingRevision the includingRevision to set
     */
    public void setIncludingRevision(boolean includingRevision) {
        this.includingRevision = includingRevision;
    }

    /**
     * 高度な検索：検索対象選択肢を返却する.
     * @return 検索対象選択肢
     */
    public List<SelectItem> getFullTextSearchModeSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        this.fullTextSearchModeSelectList = viewHelper.createSelectItem(
            fullTextSearchModeList);
        return fullTextSearchModeSelectList;
    }

    /**
     * 高度な検索：検索対象選択肢を設定する.
     * @param fullTextSearchModeSelectList 検索対象選択肢
     */
    public void setFullTextSearchModeSelectList(List<SelectItem> fullTextSearchModeSelectList) {
        this.fullTextSearchModeSelectList = fullTextSearchModeSelectList;
    }

    /**
     * 高度な検索：コレポン文書種別選択肢を設定する.
     * @return 高度な検索：コレポン文書種別選択肢
     */
    public List<SelectItem> getTypeSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.typeSelectList != null && !this.typeSelectList.isEmpty()) {
            return this.typeSelectList;
        }

        this.typeSelectList = viewHelper.createSelectItem(
            typeList, "projectCorresponTypeId", "corresponType");
        return typeSelectList;
    }

    /**
     * 高度な検索：コレポン文書種別選択肢を返却する.
     * @param typeSelectList 高度な検索：コレポン文書種別選択肢
     */
    public void setTypeSelectList(List<SelectItem> typeSelectList) {
        this.typeSelectList = typeSelectList;
    }

    /**
     * 高度な検索：承認状態選択肢を設定する.
     * @return 高度な検索：承認状態選択肢
     */
    public List<SelectItem> getWorkflowStatusSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.workflowStatusSelectList != null && !this.workflowStatusSelectList.isEmpty()) {
            return this.workflowStatusSelectList;
        }

        workflowStatusSelectList = viewHelper.createSelectItem(workflowList);
        return workflowStatusSelectList;
    }

    /**
     * 高度な検索：承認状態選択肢を返却する.
     * @param workflowStatusSelectList 高度な検索：承認状態選択肢
     */
    public void setWorkflowStatusSelectList(List<SelectItem> workflowStatusSelectList) {
        this.workflowStatusSelectList = workflowStatusSelectList;
    }

    /**
     * 高度な検索：既読／未読状態選択肢を設定する.
     * @return 高度な検索：既読／未読状態選択肢
     */
    public List<SelectItem> getReadStatusSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.readStatusSelectList != null && !this.readStatusSelectList.isEmpty()) {
            return this.readStatusSelectList;
        }

        readStatusSelectList = viewHelper.createSelectItem(readStatusList);
        return readStatusSelectList;
    }

    /**
     * 高度な検索：既読／未読状態選択肢を返却する.
     * @param readStatusSelectList 高度な検索：既読／未読状態選択肢
     */
    public void setReadStatusSelectList(List<SelectItem> readStatusSelectList) {
        this.readStatusSelectList = readStatusSelectList;
    }

    /**
     * 高度な検索：文書状態選択肢を設定する.
     * @return 高度な検索：文書状態選択肢
     */
    public List<SelectItem> getStatusSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.statusSelectList != null && !this.statusSelectList.isEmpty()) {
            return this.statusSelectList;
        }

        statusSelectList = viewHelper.createSelectItem(statusList);
        return statusSelectList;
    }

    /**
     * 高度な検索：文書状態選択肢を返却する.
     * @param statusSelectList 高度な検索：文書状態選択肢
     */
    public void setStatusSelectList(List<SelectItem> statusSelectList) {
        this.statusSelectList = statusSelectList;
    }

    /**
     * 高度な検索：ユーザー選択肢を設定する.
     * @return 高度な検索：ユーザー選択肢
     */
    public List<SelectItem> getUserSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.userSelectList != null && !this.userSelectList.isEmpty()) {
            return this.userSelectList;
        }

        if (userList != null) {
            userSelectList = new ArrayList<SelectItem>();
            for (ProjectUser pUser : userList) {
                User user = pUser.getUser();
                SelectItem item = new SelectItem(user.getEmpNo(),
                                                 user.getLabelWithRole());
                userSelectList.add(item);
            }
        }
        return userSelectList;
    }

    /**
     * 高度な検索：ユーザー選択肢を返却する.
     * @param addressUserSelectList 高度な検索：ユーザー選択肢
     */
    public void setUserSelectList(List<SelectItem> addressUserSelectList) {
        this.userSelectList = addressUserSelectList;
    }

    /**
     * 高度な検索：承認フロー状態選択肢を設定する.
     * @return 高度な検索：承認フロー状態選択肢
     */
    public List<SelectItem> getWorkflowProcessSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.workflowProcessSelectList != null && !this.workflowProcessSelectList.isEmpty()) {
            return this.workflowProcessSelectList;
        }

        workflowProcessSelectList = viewHelper.createSelectItem(workflowProcessesList);
        return workflowProcessSelectList;
    }

    /**
     * 高度な検索：承認フロー状態選択肢を返却する.
     * @param workflowProcessSelectList 高度な検索：承認フロー状態選択肢
     */
    public void setWorkflowProcessSelectList(List<SelectItem> workflowProcessSelectList) {
        this.workflowProcessSelectList = workflowProcessSelectList;
    }

    /**
     * 高度な検索：活動単位選択肢を設定する.
     * @return 高度な検索：活動単位選択肢
     */
    public List<SelectItem> getGroupSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.groupSelectList != null && !this.groupSelectList.isEmpty()) {
            return this.groupSelectList;
        }

        groupSelectList = viewHelper.createSelectItem(groupList, "id", "name");
        return groupSelectList;
    }

    /**
     * 高度な検索：活動単位選択肢を返却する.
     * @param groupSelectList 高度な検索：活動単位選択肢
     */
    public void setGroupSelectList(List<SelectItem> groupSelectList) {
        this.groupSelectList = groupSelectList;
    }

    /**
     * groupCcを取得します.
     * @return the groupCc
     */
    public boolean isGroupCc() {
        return groupCc;
    }

    /**
     * groupCcを設定します.
     * @param groupCc the groupCc to set
     */
    public void setGroupCc(boolean groupCc) {
        this.groupCc = groupCc;
    }

    /**
     * groupToを取得します.
     * @return the groupTo
     */
    public boolean isGroupTo() {
        return groupTo;
    }

    /**
     * groupToを設定します.
     * @param groupTo the groupTo to set
     */
    public void setGroupTo(boolean groupTo) {
        this.groupTo = groupTo;
    }

    /**
     * groupUnrepliedを取得します.
     * @return the groupUnreplied
     */
    public boolean isGroupUnreplied() {
        return groupUnreplied;
    }

    /**
     * groupUnrepliedを設定します.
     * @param groupUnreplied the groupUnreplied to set
     */
    public void setGroupUnreplied(boolean groupUnreplied) {
        this.groupUnreplied = groupUnreplied;
    }

    /**
     * 高度な検索：作成日（From）を設定する.
     * @return 高度な検索：作成日（From）
     */
    public String getFromCreated() {
        return fromCreated;
    }

    /**
     * 高度な検索：作成日（From）を返却する.
     * @param fromCreated 高度な検索：作成日（From）
     */
    public void setFromCreated(String fromCreated) {
        this.fromCreated = fromCreated;
    }

    /**
     * 高度な検索：作成日（to）を設定する.
     * @return 高度な検索：作成日（to）
     */
    public String getToCreated() {
        return toCreated;
    }

    /**
     * 高度な検索：作成日（to）を返却する.
     * @param toCreated 高度な検索：作成日（to）
     */
    public void setToCreated(String toCreated) {
        this.toCreated = toCreated;
    }

    /**
     * 高度な検索：発効日（From）を設定する.
     * @return 高度な検索：発効日（From）
     */
    public String getFromIssued() {
        return fromIssued;
    }

    /**
     * 高度な検索：発効日（From）を返却する.
     * @param fromIssued 高度な検索：発効日（From）
     */
    public void setFromIssued(String fromIssued) {
        this.fromIssued = fromIssued;
    }

    /**
     * 高度な検索：発効日（to）を設定する.
     * @return 高度な検索：発効日（to）
     */
    public String getToIssued() {
        return toIssued;
    }

    /**
     * 高度な検索：発効日（to）を返却する.
     * @param toIssued 高度な検索：発効日（to）
     */
    public void setToIssued(String toIssued) {
        this.toIssued = toIssued;
    }

    /**
     * 高度な検索：返信期限（From）を設定する.
     * @return 高度な検索：返信期限（From）
     */
    public String getFromReply() {
        return fromReply;
    }

    /**
     * 高度な検索：返信期限（From）を返却する.
     * @param fromReply 高度な検索：返信期限（From）
     */
    public void setFromReply(String fromReply) {
        this.fromReply = fromReply;
    }

    /**
     * 高度な検索：返信期限（to）を設定する.
     * @return 高度な検索：返信期限（to）
     */
    public String getToReply() {
        return toReply;
    }

    /**
     * 高度な検索：返信期限（to）を返却する.
     * @param toReply 高度な検索：返信期限（to）
     */
    public void setToReply(String toReply) {
        this.toReply = toReply;
    }

    /**
     * 高度な検索：Custom Field選択肢を設定する.
     * @return 高度な検索：Custom Field選択肢
     */
    public List<SelectItem> getCustomFieldSelectList() {
        if (!this.advancedSearchDisplayed) {
            return new ArrayList<SelectItem>();
        }
        if (this.customFieldSelectList != null && !this.customFieldSelectList.isEmpty()) {
            return this.customFieldSelectList;
        }

        customFieldSelectList =
            viewHelper.createSelectItem(customFieldList, "projectCustomFieldId", "label");
        return customFieldSelectList;
    }

    /**
     * 高度な検索：Custom Field選択肢を返却する.
     * @param customFieldSelectList 高度な検索：Custom Field選択肢
     */
    public void setCustomFieldSelectList(List<SelectItem> customFieldSelectList) {
        this.customFieldSelectList = customFieldSelectList;
    }

    /**
     * 高度な検索：選択されたCustom Field Noを設定する.
     * @return 高度な検索：選択されたCustom Field No
     */
    public Long getCustomFieldNo() {
        return customFieldNo;
    }

    /**
     * 高度な検索：選択されたCustom Field Noを返却する.
     * @param customField 高度な検索：選択されたCustom Field No
     */
    public void setCustomFieldNo(Long customField) {
        this.customFieldNo = customField;
    }

    /**
     * 高度な検索：Custom Field値を設定する.
     * @return 高度な検索：Custom Field値
     */
    public String getCustomFieldValue() {
        return customFieldValue;
    }

    /**
     * 高度な検索：Custom Field値を返却する.
     * @param customFieldValue 高度な検索：Custom Field値
     */
    public void setCustomFieldValue(String customFieldValue) {
        this.customFieldValue = customFieldValue;
    }

    /**
     * 高度な検索：選択されたコレポン文書種別を設定する.
     * @return 高度な検索：選択されたコレポン文書種別
     */
    public Long[] getTypes() {
        return types == null ? null : types.clone();
    }

    /**
     * 高度な検索：選択されたコレポン文書種別を返却する.
     * @param corresponTypes 高度な検索：選択されたコレポン文書種別
     */
    public void setTypes(Long[] corresponTypes) {
        if (corresponTypes != null) {
            this.types = corresponTypes.clone();
        } else {
            this.types = null;
        }
    }

    /**
     * 高度な検索：選択された承認状態を設定する.
     * @return 高度な検索：選択された承認状態
     */
    public Integer[] getWorkflowStatuses() {
        return workflowStatuses == null ? null : workflowStatuses.clone();
    }

    /**
     * 高度な検索：選択された承認状態を返却する.
     * @param workflowStatuses 高度な検索：選択された承認状態
     */
    public void setWorkflowStatuses(Integer[] workflowStatuses) {
        if (workflowStatuses != null) {
            this.workflowStatuses = workflowStatuses.clone();
        } else {
            this.workflowStatuses = null;
        }
    }

    /**
     * 高度な検索：選択された既読／未読状態を設定する.
     * @return 高度な検索：選択された既読／未読状態
     */
    public Integer[] getReadStatuses() {
        return readStatuses == null ? null : readStatuses.clone();
    }

    /**
     * 高度な検索：選択された既読／未読状態を返却する.
     * @param readStatuses 高度な検索：選択された既読／未読状態
     */
    public void setReadStatuses(Integer[] readStatuses) {
        if (readStatuses != null) {
            this.readStatuses = readStatuses.clone();
        } else {
            this.readStatuses = null;
        }
    }

    /**
     * 高度な検索：選択された文書状態を設定する.
     * @return 高度な検索：選択された文書状態
     */
    public Integer[] getStatuses() {
        return statuses == null ? null : statuses.clone();
    }

    /**
     * 高度な検索：選択された文書状態を返却する.
     * @param statuses 高度な検索：選択された文書状態
     */
    public void setStatuses(Integer[] statuses) {
        if (statuses != null) {
            this.statuses = statuses.clone();
        } else {
            this.statuses = null;
        }
    }

    /**
     * 高度な検索：選択された承認フロー状態を設定する.
     * @return 高度な検索：選択された承認フロー状態
     */
    public Integer[] getWorkflowProcesses() {
        return workflowProcesses == null ? null : workflowProcesses.clone();
    }

    /**
     * 高度な検索：選択された承認フロー状態を返却する.
     * @param workflowProcesses 高度な検索：選択された承認フロー状態
     */
    public void setWorkflowProcesses(Integer[] workflowProcesses) {
        if (workflowProcesses != null) {
            this.workflowProcesses = workflowProcesses.clone();
        } else {
            this.workflowProcesses = null;
        }
    }

    /**
     * 高度な検索の表示状態を設定する.
     * @param advancedSearchDisplayed 表示状態
     */
    public void setAdvancedSearchDisplayed(boolean advancedSearchDisplayed) {
        this.advancedSearchDisplayed = advancedSearchDisplayed;
    }

    /**
     * 高度な検索の表示状態を返却する.
     * @return 表示状態(表示している場合はtrue)
     */
    public boolean isAdvancedSearchDisplayed() {
        return advancedSearchDisplayed;
    }

    /**
     * 高度な検索を行ったか否かを設定する.
     * @param advancedSearchRequested 高度な検索を行っていればtrue
     */
    public void setAdvancedSearchRequested(boolean advancedSearchRequested) {
        this.advancedSearchRequested = advancedSearchRequested;
    }

    /**
     * 高度な検索の表示を行ったか否かを返却する.
     * @return 一度でも表示している場合はtrue
     */
    public boolean isAdvancedSearchRequested() {
        return advancedSearchRequested;
    }

    /**
     * 高度な検索の表示／非表示を制御するための、display属性に設定される値を返す.
     * @return 表示状態ならblock、非表示状態ならnone
     */
    public String getAdvancedSearchDisplay() {
        if (this.advancedSearchDisplayed) {
            return STYLE_SHOW;
        }
        return STYLE_HIDE;
    }

    /**
     * 現在のページ№を設定する.
     * @param pageNo 現在のページ№
     */
    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    /**
     * 現在のページ№を返却する.
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
     * 総レコード数を返却する.
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
     * 画面表示件数を返却する.
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
     * ページリンク数を返却する.
     * @param pageIndex ページリンク数
     */
    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * @return the simpleSearchSelectedItem
     */
    public int getSimpleSearchSelectedItem() {
        return simpleSearchSelectedItem;
    }

    /**
     * @param simpleSearchSelectedItem the simpleSearchSelectedItem to set
     */
    public void setSimpleSearchSelectedItem(int simpleSearchSelectedItem) {
        this.simpleSearchSelectedItem = simpleSearchSelectedItem;
    }

    /**
     * 「前へ」を表示するかどうかの判定.
     * @return true = 「前へ」を表示する
     */
    public boolean getPrevious() {
        return pageNo > 1;
    }

    /**
     * 「次へ」を表示するかどうかの判定.
     * @return true = 次前へ」を表示する
     */
    public boolean getNext() {
        return pageNo < PagingUtil.getAllPage(dataCount, pageRowNum);
    }

    /**
     * コレポン文書一覧のヘッダを設定する.
     * @param header コレポン文書一覧のヘッダ
     */
    public void setHeader(CorresponIndexHeader header) {
        this.header = header;
    }

    /**
     * コレポン文書一覧のヘッダを返却する.
     * @return コレポン文書一覧のヘッダ
     */
    public CorresponIndexHeader getHeader() {
        return header;
    }

    /**
     * コレポン文書のデータを設定する.
     * @param coresponList コレポン文書のデータ
     */
    public void setIndexList(List<CorresponIndex> coresponList) {
        this.indexList = coresponList;
    }

    /**
     * コレポン文書のデータを返却する.
     * @return コレポン文書のデータ
     */
    public List<CorresponIndex> getIndexList() {
        return indexList;
    }

    /**
     * コレポン文書一覧のデータを設定する.
     * @param corresponList コレポン文書一覧のデータ
     */
    public void setCorresponList(List<Correspon> corresponList) {
        this.corresponList = corresponList;
    }

    /**
     * コレポン文書一覧のデータを返却する.
     * @return コレポン文書一覧のデータ
     */
    public List<Correspon> getCorresponList() {
        return corresponList;
    }

    /**
     * ソートカラムを設定する.
     * @param sort ソートカラム
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * ソートカラムを返却する.
     * @return ソートカラム
     */
    public String getSort() {
        return sort;
    }

    /**
     * ソートの昇順／降順を設定する.
     * @param ascending ソートの昇順／降順
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    /**
     * ソートの昇順／降順を返却する.
     * @return true = 昇順
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * rowClassesを設定します.
     * @param rowClasses the rowClasses to set
     */
    public void setRowClasses(String rowClasses) {
        this.rowClasses = rowClasses;
    }

    /**
     * rowClassesを取得します.
     * @return the rowClasses
     */
    public String getRowClasses() {
        return rowClasses;
    }

    /**
     * シンプルな検索：コレポン種別のAllを表す値.
     * @return コレポン種別のAll
     */
    public Long getTypeAll() {
        return ALL_TYPE;
    }

    /**
     * シンプルな検索：承認状態のAllを表す値.
     * @return 承認状態のAll
     */
    public Integer getWorkflowAll() {
        return ALL_WORKFLOW;
    }

    /**
     * シンプルな検索：既読／未読状態のAllを表す値.
     * @return 既読／未読状態のAll
     */
    public Integer getReadStatusAll() {
        return ALL_READ_STATUS;
    }

    /**
     * シンプルな検索：学習用文書を含むか否かのAllを表す値.
     * @return 既読／未読状態のAll
     */
    public Integer getForLearningAll() {
        return ALL_FOR_LEARNING;
    }

    public Integer getDefaultSelectItemValue() {
        return DEFAULT_SELECT_ITEM_VALUE;
    }

    /**
     * 未読状態を表す値.
     * @return 未読状態l
     */
    public ReadStatus getUnread() {
        return ReadStatus.NEW;
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
     * 検索条件：コレポン文書種別の文字列表現を取得する.
     * @return 検索条件：コレポン文書種別の文字列表現
     */
    public String getTypeConditionText() {
        if (condition != null) {
            CorresponType[] typeCondition = condition.getCorresponTypes();
            if (typeCondition != null && typeCondition.length > 0) {
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < typeCondition.length; i++) {
                    sb.append(getViewCorresponType(typeCondition[i].getProjectCorresponTypeId()));
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < typeCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (typeCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：承認状態の文字列表現を取得する.
     * @return 検索条件：承認状態の文字列表現
     */
    public String getWorkflowConditionText() {
        if (condition != null) {
            WorkflowStatus[] workflowCondition = condition.getWorkflowStatuses();
            if (workflowCondition != null && workflowCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < workflowCondition.length; i++) {
                    sb.append(workflowCondition[i].getLabel());
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < workflowCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (workflowCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：既読／未読状態の文字列表現を取得する.
     * @return 検索条件：既読／未読状態の文字列表現
     */
    public String getReadStatusConditionText() {
        if (condition != null) {
            ReadStatus[] readStatusCondition = condition.getReadStatuses();
            if (readStatusCondition != null && readStatusCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < readStatusCondition.length; i++) {
                    sb.append(readStatusCondition[i].getLabel());
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < readStatusCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (readStatusCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：コレポン文書状態の文字列表現を取得する.
     * @return 検索条件：コレポン文書状態の文字列表現
     */
    public String getStatusConditionText() {
        if (condition != null) {
            CorresponStatus[] corresponStatusCondition = condition.getCorresponStatuses();
            if (corresponStatusCondition != null && corresponStatusCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < corresponStatusCondition.length; i++) {
                    sb.append(corresponStatusCondition[i].getLabel());
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < corresponStatusCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (corresponStatusCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：承認フロー状態の文字列表現を取得する.
     * @return 検索条件：承認フロー状態の文字列表現
     */
    public String getWorkflowProcessConditionText() {
        if (condition != null) {
            WorkflowProcessStatus[] workflowProcessStatusCondition
                = condition.getWorkflowProcessStatuses();
            if (workflowProcessStatusCondition != null
                    && workflowProcessStatusCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < workflowProcessStatusCondition.length; i++) {
                    sb.append(workflowProcessStatusCondition[i].getLabel());
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < workflowProcessStatusCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (workflowProcessStatusCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：From:Groupの文字列表現を取得する.
     * @return 検索条件：From:Groupの文字列表現
     */
    public String getFromGroupsConditionText() {
        if (condition != null) {
            CorresponGroup[] groupCondition = condition.getFromGroups();
            if (groupCondition != null && groupCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < groupCondition.length; i++) {
                    sb.append(getViewGroupName(groupCondition[i].getId()));
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < groupCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (groupCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：To/Cc:Groupの文字列表現を取得する.
     * @return 検索条件：To/Cc:Groupの文字列表現
     */
    public String getToGroupsConditionText() {
        if (condition != null) {
            CorresponGroup[] groupCondition = condition.getToGroups();
            if (groupCondition != null && groupCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < groupCondition.length; i++) {
                    sb.append(getViewGroupName(groupCondition[i].getId()));
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < groupCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (groupCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：From:Userの文字列表現を取得する.
     * @return 検索条件：From:Userの文字列表現
     */
    public String getFromUsersConditionText() {
        if (condition != null) {
            User[] userCondition = condition.getFromUsers();
            if (userCondition != null && userCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < userCondition.length; i++) {
                    sb.append(getViewUserName(userCondition[i].getEmpNo()));
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < userCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (userCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：To/Cc:Userの文字列表現を取得する.
     * @return 検索条件：To/Cc:Userの文字列表現
     */
    public String getToUsersConditionText() {
        if (condition != null) {
            User[] userCondition = condition.getToUsers();
            if (userCondition != null && userCondition.length > 0) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < userCondition.length; i++) {
                    sb.append(getViewUserName(userCondition[i].getEmpNo()));
                    if (i + 1 < MAX_CONDITION
                            && i + 1 < userCondition.length) {
                        sb.append(DELIM_CONDITION);
                    } else if (userCondition.length > MAX_CONDITION) {
                        sb.append(TEXT_MORE_CONDITION);
                        break;
                    }
                }
                return sb.toString();
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * 検索条件：検索対象の文字列表現を取得する.
     * @return 検索条件：検索対象の文字列表現
     */
    public String getFullTextSearchModeConditionText() {
        if (condition != null) {
            FullTextSearchMode mode = condition.getFullTextSearchMode();
            if (mode == null) {
                return null;
            }
            return mode.getLabel();
        }
        return null;
    }

    /**
     * 検索条件：カスタムフィールドの文字列表現を取得する.
     * @return 検索条件：カスタムフィールドの文字列表現
     */
    public String getCustomFieldText() {
        if (condition != null) {
            Long customFieldCondition = condition.getCustomFieldNo();
            if (customFieldCondition != null
                    && customFieldCondition > 0
                    && customFieldList.size() > 0) {
                for (CustomField cf : customFieldList) {
                    if (customFieldCondition.equals(cf.getProjectCustomFieldId())) {
                        return cf.getLabel();
                    }
                }
                return SystemConfig.getValue(KEY_UNKNOWN_TXT);
            }
        }
        return null; // ラベルを表示させないためにNULLで返却する
    }

    /**
     * Group表示内容を取得する.
     * @param id コレポンうグループID
     * @return Group表示内容
     */
    private String getViewGroupName(Long id) {
        if (id != null) {
            for (CorresponGroup g : groupList) {
                if (id.equals(g.getId())) {
                    return g.getName();
                }
            }
            return SystemConfig.getValue(KEY_UNKNOWN_TXT);
        }
        return null;
    }

    /**
     * User表示内容を取得する.
     * @param empNo 従業員No
     * @return User表示内容
     */
    private String getViewUserName(String empNo) {
        if (empNo != null) {
            for (ProjectUser pu : userList) {
                if (pu.getUser() != null && empNo.equals(pu.getUser().getEmpNo())) {
                    return pu.getUser().getLabel();
                }
            }
            return SystemConfig.getValue(KEY_UNKNOWN_TXT) + "/" + empNo;
        }
        return null;
    }

    /**
     * CorresponType表示内容を取得する.
     * @param id CorresponTypeId
     * @return CorresponType表示内容
     */
    private String getViewCorresponType(Long id) {
        if (id != null) {
            for (CorresponType c : typeList) {
                if (id.equals(c.getProjectCorresponTypeId())) {
                    return c.getCorresponType();
                }
            }
            return SystemConfig.getValue(KEY_UNKNOWN_TXT);
        }
        return null;
    }

    /**
     * 検索条件を設定する.
     */
    private void setSearchCondition() {
        condition.setSimpleSearch(simpleSearch);
        if (simpleSearch) {
            setSimpleSearchCondition();
        } else {
            clearSimpleSearchParameter();
            setAdvancedSearchCondition();
        }
        setSimpleConditionValues();
        setAdvancedConditionValues();
    }

    /**
     * コレポン文書一覧のデータを検索・設定する.
     */
    private void loadCorresponIndex() {
        dataCount = 0;
        indexList = new ArrayList<CorresponIndex>();

        handler.handleAction(new SearchAction(this));
    }

    /**
     * 全文検索ヘルプを設定する.
     */
    private void loadHelpContent() {
        setHelpContent(null);
        HelpContentLoader loader = new HelpContentLoader();
        try {
            HelpContent c = loader.load(HELP_NAME);
            if (c != null) {
                setHelpContent(c.getContent());
            }
        } catch (IOException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    /**
     * 高度な検索の検索条件入力フォームを表示する.
     * @return null
     */
    public String showAdvancedSearch() {
        setAdvancedConditionValues();
        advancedSearchDisplayed = true;
        return null;
    }

    /**
     * Advanced Search表示領域を隠す.
     * <p>
     * Simple Searchが表示されている場合のみ、Advanced Search表示領域を隠す.<br />
     * Advanced Searchが表示された状態であれば何も行わない.
     * </p>
     */
    private void hideAdvancedSearch() {
        if (simpleSearch) {
            advancedSearchDisplayed = false;
        }
    }

    /**
     * コレポン文書一覧の表示列の設定をする.
     * @return コレポン文書一覧表示列
     */
    private CorresponIndexHeader getCookieHeader() {
        CorresponIndexHeader head = new CorresponIndexHeader();
        // null（設定なし）の場合とtrueの場合に表示
        head.setNo(checkDisplay(CorresponIndexHeader.NUM_NO));
        head.setCorresponNo(checkDisplay(CorresponIndexHeader.NUM_CORRESPON_NO));
        head.setPreviousRevision(checkDisplay(CorresponIndexHeader.NUM_PREVIOUS_REVISION));
        head.setFrom(checkDisplay(CorresponIndexHeader.NUM_FROM));
        head.setTo(checkDisplay(CorresponIndexHeader.NUM_TO));
        head.setType(checkDisplay(CorresponIndexHeader.NUM_TYPE));
        head.setSubject(checkDisplay(CorresponIndexHeader.NUM_SUBJECT));
        head.setWorkflow(checkDisplay(CorresponIndexHeader.NUM_WORKFLOW));
        head.setCreatedOn(checkDisplay(CorresponIndexHeader.NUM_CREATED_ON));
        head.setIssuedOn(checkDisplay(CorresponIndexHeader.NUM_ISSUED_ON));
        head.setDeadline(checkDisplay(CorresponIndexHeader.NUM_DEADLINE));
        head.setUpdatedOn(checkDisplay(CorresponIndexHeader.NUM_UPDATED_ON));
        head.setCreatedBy(checkDisplay(CorresponIndexHeader.NUM_CREATED_BY));
        head.setIssuedBy(checkDisplay(CorresponIndexHeader.NUM_ISSUED_BY));
        head.setUpdatedBy(checkDisplay(CorresponIndexHeader.NUM_UPDATED_BY));
        head.setReplyRequired(checkDisplay(CorresponIndexHeader.NUM_REPLY_REQUIRED));
        head.setStatus(checkDisplay(CorresponIndexHeader.NUM_STATUS));

        return head;
    }

    /**
     * 表示列の表示判定をする.
     * @param key 表示列のKEY
     * @return true：表示
     */
    private boolean checkDisplay(String key) {
        return !NON_DISPLAY.equals(viewHelper.getCookieValue(key));
    }

    /**
     * シンプルな検索の検索条件を設定する.
     * (シンプルな検索にて選択された検索条件のみ更新。何も選択されていないばあには全て更新).
     */
    private void setSimpleSearchCondition() {
        switch(simpleSearchSelectedItem) {
        case SIMPLESEARCH_SELECTED_TYPE:
            condition.setCorresponTypes(getSelectType());
            break;
        case SIMPLESEARCH_SELECTED_WORKFLOWSTATUS:
            condition.setWorkflowStatuses(getSelectWorkflow());
            break;
        case SIMPLESEARCH_SELECTED_READSTATUS:
            condition.setReadStatuses(getSelectReadStatus());
            break;
            case SIMPLESEARCH_SELECTED_FORLEARNING:
                condition.setForLearnings(getSelectForLearning());
                break;
        case SIMPLESEARCH_SELECTED_NONE:
        default:
            condition.setCorresponTypes(getSelectType());
            condition.setWorkflowStatuses(getSelectWorkflow());
            condition.setReadStatuses(getSelectReadStatus());
        }
    }

    /**
     * シンプルな検索：選択されたコレポン文書種別の条件を取得する.
     * @return コレポン文書種別の検索条件
     */
    private CorresponType[] getSelectType() {
        CorresponType[] selected = null;
        if (type == null) {
            type = ALL_TYPE;
        }
        if (typeList == null) {
            return selected;
        }
        if (!type.equals(ALL_TYPE)) {
            for (CorresponType cType : typeList) {
                if (cType.getProjectCorresponTypeId().equals(type)) {
                    selected = new CorresponType[1];
                    selected[0] = cType;
                    break;
                }
            }
        }
        return selected;
    }

    /**
     * シンプルな検索：選択された承認状態の条件を取得する.
     * @return 承認状態の検索条件
     */
    private WorkflowStatus[] getSelectWorkflow() {
        WorkflowStatus[] selected = null;
        if (workflow == null) {
            workflow = ALL_WORKFLOW;
        }

        if (!workflow.equals(ALL_WORKFLOW)) {
            for (WorkflowStatus status : workflowList) {
                if (status.getValue().equals(workflow)) {
                    selected = new WorkflowStatus[1];
                    selected[0] = status;
                    break;
                }
            }
        }
        return selected;
    }

    /**
     * シンプルな検索：選択された既読／未読状態の条件を取得する.
     * @return 既読／未読状態の検索条件
     */
    private ReadStatus[] getSelectReadStatus() {
        ReadStatus[] selected = null;
        if (readStatus == null) {
            readStatus = ReadStatus.NEW.getValue();
        }

        if (!readStatus.equals(ALL_READ_STATUS)) {
            for (ReadStatus status : readStatusList) {
                if (status.getValue().equals(readStatus)) {
                    selected = new ReadStatus[1];
                    selected[0] = status;
                    break;
                }
            }
        }

        return selected;
    }

    /**
     * シンプルな検索：選択された学習用文書であるか否かの条件を取得する.
     * @return 既読／未読状態の検索条件
     */
    private ForLearning[] getSelectForLearning() {
        ForLearning[] selected = null;
        if (forLearning == null) {
            forLearning = ForLearning.ALL.getValue();
        }

        if (!forLearning.equals(ALL_FOR_LEARNING)) {
            for (ForLearning status : forLearningList) {
                if (status.getValue().equals(forLearning)) {
                    selected = new ForLearning[1];
                    selected[0] = status;
                    break;
                }
            }
        }

        return selected;
    }

    /**
     * 高度な検索実行時、シンプルな検索の検索条件をクリアする.
     */
    private void clearSimpleSearchParameter() {
        if (condition != null) {
            condition.setCorresponTypes(null);
            condition.setWorkflowStatuses(null);
            condition.setReadStatuses(null);
            condition.setForLearnings(null);
        }
    }

    /**
     * 高度な検索の検索条件を取得する.
     */
    //CHECKSTYLE:OFF
    private void setAdvancedSearchCondition() {
        condition.setSequenceNo(sequenceNo == null ? null : Long.valueOf(sequenceNo));
        condition.setCorresponNo(corresponNo);
        condition.setIncludingRevision(includingRevision);

        condition.setKeyword(keyword);
        condition.setFullTextSearchMode(getSelectedFullTextSearchMode());

        condition.setCorresponTypes(getSelectedTypes());
        condition.setWorkflowStatuses(getSelectedWorkflows());
        condition.setReadStatuses(getSelectedReadStatuses());
        condition.setCorresponStatuses(getSelectedStatuses());

        condition.setFromUsers(getSelectedFromUsers());
        condition.setToUsers(getSelectedToUsers());
        condition.setUserAttention(userAttention);
        condition.setUserCc(userCc);
        condition.setUserPic(userPic);
        condition.setUserUnreplied(userUnreplied);
        condition.setUserPreparer(userPreparer);
        condition.setUserChecker(userChecker);
        condition.setUserApprover(userApprover);
        condition.setWorkflowProcessStatuses(getSelectedWorkflowProcesses());
        condition.setFromGroups(getSelectedFromGroups());
        condition.setToGroups(getSelectedToGroups());
        condition.setGroupTo(groupTo);
        condition.setGroupCc(groupCc);
        condition.setGroupUnreplied(groupUnreplied);
        condition.setFromCreatedOn(DateUtil.convertStringToDate(fromCreated));
        condition.setToCreatedOn(DateUtil.convertStringToDate(toCreated));
        condition.setFromIssuedOn(DateUtil.convertStringToDate(fromIssued));
        condition.setToIssuedOn(DateUtil.convertStringToDate(toIssued));
        condition.setFromDeadlineForReply(DateUtil.convertStringToDate(fromReply));
        condition.setToDeadlineForReply(DateUtil.convertStringToDate(toReply));
        condition.setCustomFieldNo(customFieldNo);
        condition.setCustomFieldValue(customFieldValue);
    }
    //CHECKSTYLE:ON

    /**
     * 高度な検索：選択されたコレポン文書種別の条件を取得する.
     * @return コレポン文書種別の検索条件
     */
    public CorresponType[] getSelectedTypes() {
        int length = (types == null ? 0 : types.length);
        CorresponType[] selected = new CorresponType[length];
        for (int i = 0; i < length; i++) {
            for (CorresponType cType : typeList) {
                if (types[i].equals(cType.getProjectCorresponTypeId())) {
                    selected[i] = cType;
                }
            }
        }
        return selected;
    }

    /**
     * 高度な検索：選択された承認状態の条件を取得する.
     * @return 承認状態の検索条件
     */
    public WorkflowStatus[] getSelectedWorkflows() {
        int length = (workflowStatuses == null ? 0 : workflowStatuses.length);
        WorkflowStatus[] selected = new WorkflowStatus[length];
        for (int i = 0; i < length; i++) {
            for (WorkflowStatus status : workflowList) {
                if (workflowStatuses[i].equals(status.getValue())) {
                    selected[i] = status;
                }
            }
        }
        return selected;
    }

    /**
     * 高度な検索：選択された既読／未読状態の条件を取得する.
     * @return 既読／未読状態の検索条件
     */
    public ReadStatus[] getSelectedReadStatuses() {
        int length = (readStatuses == null ? 0 : readStatuses.length);
        ReadStatus[] selected = new ReadStatus[length];
        for (int i = 0; i < length; i++) {
            for (ReadStatus status : readStatusList) {
                if (readStatuses[i].equals(status.getValue())) {
                    selected[i] = status;
                }
            }
        }
        return selected;
    }

    /**
     * 高度な検索：選択された文書状態の条件を取得する.
     * @return 文書状態の検索条件
     */
    public CorresponStatus[] getSelectedStatuses() {
        int length = (statuses == null ? 0 : statuses.length);
        CorresponStatus[] selected = new CorresponStatus[length];
        for (int i = 0; i < length; i++) {
            for (CorresponStatus status : statusList) {
                if (statuses[i].equals(status.getValue())) {
                    selected[i] = status;
                }
            }
        }
        return selected;
    }

    /**
     * 高度な検索：選択されたFrom:Group一覧を取得する.
     * @return 選択されたFrom:Group一覧
     */
    public CorresponGroup[] getSelectedFromGroups() {
        int length = (fromGroups == null ? 0 : fromGroups.length);
        List<CorresponGroup> selected = new ArrayList<CorresponGroup>();
        for (int i = 0; i < length; i++) {
            for (CorresponGroup cGroup : groupList) {
                if (fromGroups[i].equals(cGroup.getId())) {
                    selected.add(cGroup);
                }
            }
        }
        if (selected.size() == 0) {
            return null;
        } else {
            CorresponGroup[] wt = new CorresponGroup[selected.size()];
            selected.toArray(wt);
            return wt;
        }
    }

    /**
     * 高度な検索：選択されたTo/Cc:Group一覧を取得する.
     * @return 選択されたTo/Cc:Group一覧
     */
    public CorresponGroup[] getSelectedToGroups() {
        int length = (toGroups == null ? 0 : toGroups.length);
        List<CorresponGroup> selected = new ArrayList<CorresponGroup>();
        for (int i = 0; i < length; i++) {
            for (CorresponGroup cGroup : groupList) {
                if (toGroups[i].equals(cGroup.getId())) {
                    selected.add(cGroup);
                }
            }
        }
        if (selected.size() == 0) {
            return null;
        } else {
            CorresponGroup[] wt = new CorresponGroup[selected.size()];
            selected.toArray(wt);
            return wt;
        }
    }

    /**
     * 高度な検索：選択されたFrom:User一覧を取得する.
     * @return 選択されたFrom:User一覧
     */
    public User[] getSelectedFromUsers() {
        int length = (fromUsers == null ? 0 : fromUsers.length);
        List<User> selected = new ArrayList<User>();
        for (int i = 0; i < length; i++) {
            for (ProjectUser pUser : userList) {
                User user = pUser.getUser();
                if (fromUsers[i].equals(user.getEmpNo())) {
                    selected.add(user);
                }
            }
        }
        if (selected.size() == 0) {
            return null;
        } else {
            User[] wt = new User[selected.size()];
            selected.toArray(wt);
            return wt;
        }
    }

    /**
     * 高度な検索：選択されたTo/Cc:User一覧を取得する.
     * @return 選択されたTo/Cc:User一覧
     */
    public User[] getSelectedToUsers() {
        int length = (toUsers == null ? 0 : toUsers.length);
        List<User> selected = new ArrayList<User>();
        for (int i = 0; i < length; i++) {
            for (ProjectUser pUser : userList) {
                User user = pUser.getUser();
                if (toUsers[i].equals(user.getEmpNo())) {
                    selected.add(user);
                }
            }
        }
        if (selected.size() == 0) {
            return null;
        } else {
            User[] wt = new User[selected.size()];
            selected.toArray(wt);
            return wt;
        }
    }

    /**
     * 高度な検索：選択された承認フロー状態の条件を取得する.
     * @return 承認フロー状態の検索条件
     */
    public WorkflowProcessStatus[] getSelectedWorkflowProcesses() {
        int length = (workflowProcesses == null ? 0 : workflowProcesses.length);
        WorkflowProcessStatus[] selected = new WorkflowProcessStatus[length];
        for (int i = 0; i < length; i++) {
            for (WorkflowProcessStatus status : workflowProcessesList) {
                if (workflowProcesses[i].equals(status.getValue())) {
                    selected[i] = status;
                    break;
                }
            }
        }
        return selected;
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
     * 画面表示用：高度な検索：選択されたカスタムフィールドのラベルを取得する.
     * @return 選択されたカスタムフィールドのラベル
     */
    public String getSelectedCustomField() {
        if (customFieldNo == null) {
            return null;
        }
        for (int i = 0; i < customFieldList.size(); i++) {
            if (customFieldNo.equals(customFieldList.get(i).getNo())) {
                return customFieldList.get(i).getLabel();
            }
        }
        return null;
    }

    /**
     * 現在のリクエストがsearchアクションによって発生した場合はtrue.
     * @return searchアクションの場合true
     */
    public boolean isSearchAction() {
        return isActionInvoked("form:search");
    }

    /**
     * シンプルな検索の条件を反映する.
     */
    private void setSimpleConditionValues() {
        // Advanced Searchで複数選択されていた場合は先頭の選択値のみ反映
        if (condition.getCorresponTypes().length > 0) {
            if (getTypeSelectList() != null
                && (getTypeSelectList().size() == 1
                    || getTypeSelectList().size() != condition.getCorresponTypes().length)) {
                CorresponType cType = condition.getCorresponTypes()[0];
                setType(cType.getProjectCorresponTypeId());
            } else {
                setType(-1L);
            }
        } else {
            setType(-1L);
        }
        if (condition.getWorkflowStatuses().length > 0) {
            if (getWorkflowStatusSelectList() != null
                && getWorkflowStatusSelectList().size() != condition.getWorkflowStatuses().length) {
                WorkflowStatus wStatus = condition.getWorkflowStatuses()[0];
                setWorkflow(wStatus.getValue());
            } else {
                setWorkflow(Integer.valueOf(-1));
            }
        } else {
            setWorkflow(Integer.valueOf(-1));
        }
        if (condition.getReadStatuses().length > 0) {
            if (getReadStatusSelectList() != null
                    && getReadStatusSelectList().size() != condition.getReadStatuses().length) {
                ReadStatus rStatus = condition.getReadStatuses()[0];
                setReadStatus(rStatus.getValue());
            } else {
                setReadStatus(Integer.valueOf(-1));
            }
        } else {
            setReadStatus(Integer.valueOf(-1));
        }
        if (condition.getForLearnings().length > 0) {
            setForLearning(condition.getForLearnings()[0].getValue());
        } else {
            setForLearning(Integer.valueOf(-1));
        }
    }

    /**
     * 高度な検索の条件を反映する.
     */
    //CHECKSTYLE:OFF
    private void setAdvancedConditionValues() {
        setSequenceNo(condition.getSequenceNo() == null
                ? null : condition.getSequenceNo().toString());

        setCorresponNo(condition.getCorresponNo());
        setIncludingRevision(condition.isIncludingRevision());

        setKeyword(condition.getKeyword());
        setFullTextSearchMode(getFullTextSearchModeValue());

        setTypes(getTypeValues());
        setWorkflowStatuses(getWorkflowValues());
        setReadStatuses(getReadStatusValues());
        setStatuses(getStatusValues());

        setFromUsers(getFromUserValues());
        setToUsers(getToUserValues());
        setUserAttention(condition.isUserAttention());
        setUserCc(condition.isUserCc());
        setUserPic(condition.isUserPic());
        setUserUnreplied(condition.isUserUnreplied());
        setUserPreparer(condition.isUserPreparer());
        setUserChecker(condition.isUserChecker());
        setUserApprover(condition.isUserApprover());
        setWorkflowProcesses(getWorkflowProcessValues());

        setFromGroups(getFromGroupValues());
        setToGroups(getToGroupValues());
        setGroupTo(condition.isGroupTo());
        setGroupCc(condition.isGroupCc());
        setGroupUnreplied(condition.isGroupUnreplied());

        setFromCreated(DateUtil.convertDateToString(condition.getFromCreatedOn()));
        setToCreated(DateUtil.convertDateToString(condition.getToCreatedOn()));
        setFromIssued(DateUtil.convertDateToString(condition.getFromIssuedOn()));
        setToIssued(DateUtil.convertDateToString(condition.getToIssuedOn()));
        setFromReply(DateUtil.convertDateToString(condition.getFromDeadlineForReply()));
        setToReply(DateUtil.convertDateToString(condition.getToDeadlineForReply()));
        setCustomFieldNo(condition.getCustomFieldNo());
        setCustomFieldValue(condition.getCustomFieldValue());
    }
    //CHECKSTYLE:ON

    /**
     * 既読／未読状態の検索条件を取得する.
     * @return 既読／未読状態の検索条件
     */
    private Integer getFullTextSearchModeValue() {
        FullTextSearchMode mode = condition.getFullTextSearchMode();
        return mode == null ? null : mode.getValue();
    }

    /**
     * コレポン文書種別の検索条件を取得する.
     * @return コレポン文書種別の検索条件
     */
    private Long[] getTypeValues() {
        CorresponType[] array = condition.getCorresponTypes();
        if (array == null) {
            return new Long[0];
        }
        Long[] values = new Long[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getProjectCorresponTypeId();
        }
        return values;
    }

    /**
     * 承認状態の検索条件を取得する.
     * @return 承認状態の検索条件
     */
    private Integer[] getWorkflowValues() {
        WorkflowStatus[] array = condition.getWorkflowStatuses();
        if (array == null) {
            return new Integer[0];
        }
        Integer[] values = new Integer[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getValue();
        }
        return values;
    }

    /**
     * 既読／未読状態の検索条件を取得する.
     * @return 既読／未読状態の検索条件
     */
    private Integer[] getReadStatusValues() {
        ReadStatus[] array = condition.getReadStatuses();
        if (array == null) {
            return new Integer[0];
        }
        Integer[] values = new Integer[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getValue();
        }
        return values;
    }

    /**
     * 文書状態の検索条件を取得する.
     * @return 文書状態の検索条件
     */
    private Integer[] getStatusValues() {
        CorresponStatus[] array = condition.getCorresponStatuses();
        if (array == null) {
            return new Integer[0];
        }
        Integer[] values = new Integer[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getValue();
        }
        return values;
    }

    /**
     * From:Groupの検索条件を取得する.
     * @return From:Groupの検索条件
     */
    private Long[] getFromGroupValues() {
        CorresponGroup[] array = condition.getFromGroups();
        if (array == null) {
            return new Long[0];
        }
        Long[] values = new Long[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getId();
        }
        return values;
    }

    /**
     * To/Cc:Groupの検索条件を取得する.
     * @return To/Cc:Groupの検索条件
     */
    private Long[] getToGroupValues() {
        CorresponGroup[] array = condition.getToGroups();
        if (array == null) {
            return new Long[0];
        }
        Long[] values = new Long[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getId();
        }
        return values;
    }

    /**
     * From:Userの検索条件を取得する.
     * @return From:Userの検索条件
     */
    private String[] getFromUserValues() {
        User[] array = condition.getFromUsers();
        if (array == null) {
            return new String[0];
        }
        String[] values = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getEmpNo();
        }
        return values;
    }

    /**
     * To/Cc:Userの検索条件を取得する.
     * @return To/Cc:Userの検索条件
     */
    private String[] getToUserValues() {
        User[] array = condition.getToUsers();
        if (array == null) {
            return new String[0];
        }
        String[] values = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getEmpNo();
        }
        return values;
    }

    /**
     * 承認フロー状態の検索条件を取得する.
     * @return 承認フロー状態
     */
    private Integer[] getWorkflowProcessValues() {
        WorkflowProcessStatus[] array = condition.getWorkflowProcessStatuses();
        if (array == null) {
            return new Integer[0];
        }
        Integer[] values = new Integer[array.length];

        for (int i = 0; i < array.length; i++) {
            values[i] = array[i].getValue();
        }
        return values;
    }

    /**
     * @param sequenceNo the sequenceNo to set
     */
    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    /**
     * @return the sequenceNo
     */
    public String getSequenceNo() {
        return sequenceNo;
    }

    /**
     * @param fromGroups the fromGroups to set
     */
    public void setFromGroups(Long[] fromGroups) {
        if (fromGroups != null) {
            this.fromGroups = fromGroups.clone();
        } else {
            this.fromGroups = null;
        }
    }

    /**
     * @return the fromGroups
     */
    public Long[] getFromGroups() {
        return fromGroups == null ? null : fromGroups.clone();
    }

    /**
     * @param toGroups the toGroups to set
     */
    public void setToGroups(Long[] toGroups) {
        if (toGroups != null) {
            this.toGroups = toGroups.clone();
        } else {
            this.toGroups = null;
        }
    }

    /**
     * @return the toGroups
     */
    public Long[] getToGroups() {
        return toGroups == null ? null : toGroups.clone();
    }

    /**
     * @param fromUsers the fromUsers to set
     */
    public void setFromUsers(String[] fromUsers) {
        if (fromUsers != null) {
            this.fromUsers = fromUsers.clone();
        } else {
            this.fromUsers = null;
        }
    }

    /**
     * @return the fromUsers
     */
    public String[] getFromUsers() {
        return fromUsers == null ? null : fromUsers.clone();
    }

    /**
     * @param toUsers the toUsers to set
     */
    public void setToUsers(String[] toUsers) {
        if (toUsers != null) {
            this.toUsers = toUsers.clone();
        } else {
            this.toUsers = null;
        }
    }

    /**
     * @return the toUsers
     */
    public String[] getToUsers() {
        return toUsers == null ? null : toUsers.clone();
    }

    /**
     * @param userPreparer the userPreparer to set
     */
    public void setUserPreparer(boolean userPreparer) {
        this.userPreparer = userPreparer;
    }

    /**
     * @return the userPreparer
     */
    public boolean isUserPreparer() {
        return userPreparer;
    }

    /**
     * @param userChecker the userChecker to set
     */
    public void setUserChecker(boolean userChecker) {
        this.userChecker = userChecker;
    }

    /**
     * @return the userChecker
     */
    public boolean isUserChecker() {
        return userChecker;
    }

    /**
     * @param userApprover the userApprover to set
     */
    public void setUserApprover(boolean userApprover) {
        this.userApprover = userApprover;
    }

    /**
     * @return the userApprover
     */
    public boolean isUserApprover() {
        return userApprover;
    }

    /**
     * @param userAttention the userAttention to set
     */
    public void setUserAttention(boolean userAttention) {
        this.userAttention = userAttention;
    }

    /**
     * @return the userAttention
     */
    public boolean isUserAttention() {
        return userAttention;
    }

    /**
     * @param userCc the userCc to set
     */
    public void setUserCc(boolean userCc) {
        this.userCc = userCc;
    }

    /**
     * @return the userCc
     */
    public boolean isUserCc() {
        return userCc;
    }

    /**
     * @param userPic the userPic to set
     */
    public void setUserPic(boolean userPic) {
        this.userPic = userPic;
    }

    /**
     * @return the userPic
     */
    public boolean isUserPic() {
        return userPic;
    }

    /**
     * @param userUnreplied the userUnreplied to set
     */
    public void setUserUnreplied(boolean userUnreplied) {
        this.userUnreplied = userUnreplied;
    }

    /**
     * @return the userUnreplied
     */
    public boolean isUserUnreplied() {
        return userUnreplied;
    }

    /**
     * @param keyword the keyword to set
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * @return the keyword
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
     * @param favoriteFilterId the favoriteFilterId to set
     */
    public void setFavoriteFilterId(Long favoriteFilterId) {
        this.favoriteFilterId = favoriteFilterId;
    }

    /**
     * @return the favoriteFilterId
     */
    public Long getFavoriteFilterId() {
        return favoriteFilterId;
    }

    /**
     * @param favoriteFilterName the favoriteFilterName to set
     */
    public void setFavoriteFilterName(String favoriteFilterName) {
        this.favoriteFilterName = favoriteFilterName;
    }

    /**
     * @return the favoriteFilterName
     */
    public String getFavoriteFilterName() {
        return favoriteFilterName;
    }

    /**
     * @param favoriteFilterDisplay the favoriteFilterDisplay to set
     */
    public void setFavoriteFilterDisplay(boolean favoriteFilterDisplay) {
        this.favoriteFilterDisplay = favoriteFilterDisplay;
    }

    /**
     * @return the favoriteFilterDisplay
     */
    public boolean isFavoriteFilterDisplay() {
        return favoriteFilterDisplay;
    }

    public boolean isDefaultDisplay() {
        return (!favoriteFilterDisplay);
    }

    public String showFavoriteDialog() {
        favoriteFilterName = null;
        favoriteFilterDisplay = true;
        return null;
    }

    public String closeFavoriteDialog() {
        favoriteFilterDisplay = false;
        return null;
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
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -3158779863476493493L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            String projectId = page.getCurrentProjectId();
            if (StringUtils.isEmpty(projectId)) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }
            String strPageRow = SystemConfig.getValue(KEY_PAGE_ROW);
            if (StringUtils.isNotEmpty(strPageRow)) {
                page.pageRowNum = Integer.parseInt(strPageRow);
            }
            String strPageIndex = SystemConfig.getValue(KEY_PAGE_INDEX);
            if (StringUtils.isNotEmpty(strPageIndex)) {
                page.pageIndex = Integer.parseInt(strPageIndex);
            }
            page.header = page.getCookieHeader();

            // 検索条件表示用
            setSelectLists();

            page.pageNo = 1;
            page.ascending = false;
            page.sort = "issued_at";

            // セッションに保存されているコンディションがあれば設定する
            setConditionFromSession();

            page.setSimpleConditionValues();
            page.setAdvancedConditionValues();

            //  初期表示時は常にSimple Searchを表示させる
            page.setSimpleSearch(true);

            page.condition.setPageRowNum(page.pageRowNum);
        }

        /**
         * favoriteFilterIdがURLパラメタに設定されている場合、お気に入り検索条件を取得する.
         * セッションに保存されているコンディションがあればpageの検索条件として設定する.
         * また、セッションに保持されているpageNo・ascending・sortを
         * 使用する場合にはそれらをpageに設定する.<br>
         * (loadCorresponIndex()を呼び出してコレポン文書一覧のデータを検索・設定する際、
         * pageNo・ascending・sortの3つは、page.conditionに設定されているものではなく
         * pageに設定されている値が使用される).
         * <p>
         * PersonInChargeが無効の場合には、意図的にPersonInChargeの検索条件を無効とする<br>
         * これは、プロジェクトカスタム設定画面からの遷移に対応したもの
         * @throws ServiceAbortException 権限エラー
         */
        private void setConditionFromSession() throws ServiceAbortException {
            if (page.getFavoriteFilterId() == null) {
                // セッションに保存されているコンディションがあれば使用
                page.condition = page.getCurrentSearchCorresponCondition();
            } else {
                // favoriteFilterIdの指定があればお気に入り検索条件を使用
                page.condition = getSearchCorresponConditionByFavoriteFilter(
                        page.getFavoriteFilterId());
                // favoriteFilterの検索条件を使用する場合は意図的にsessionのソートアイテムをtureに更新する
                page.useSessionSort = true;
            }
            if (page.condition != null) {
                // コンディション中のsort, ascending, pageNoを使用するか否かを確認
                if (page.useSessionSort
                        && StringUtils.isNotEmpty(page.condition.getSort())) {
                    page.sort = page.condition.getSort();
                    page.ascending = page.condition.isAscending();
                }
                if (page.useSessionPageNo
                        && page.condition.getPageNo() > 0) {
                    page.pageNo = page.condition.getPageNo();
                }
                // Person In Chargeが無効の場合には、PICの条件を除く
                if (!page.isUsePersonInCharge()) {
                    page.condition.setUserPic(false);
                }
            } else {
                page.condition = getDefaultCondition();
            }
        }

        /**
         * Favorite Filterとして登録済みの検索条件を取得する.
         * @param id
         * @return 検索条件
         * @throws ServiceAbortException 権限エラー
         */
        private SearchCorresponCondition getSearchCorresponConditionByFavoriteFilter(Long id)
                throws ServiceAbortException {
            if (id == null) {
                return null;
            }
            try {
                FavoriteFilter ff = page.favoriteFilterService.find(id);
                if (ff == null) {
                    return null;
                }
                SearchCorresponCondition rt = ff.getSearchConditionsAsObject();
                rt.setProjectId(page.getCurrentProjectId());
                return rt;
            } catch (ServiceAbortException e) {
                if (ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT.equals(
                        e.getMessageCode())) {
                    throw e;
                }
            }
            return null;
        }

        /**
         * 選択肢を設定する.
         * @throws ServiceAbortException 選択肢取得エラー
         */
        private void setSelectLists() throws ServiceAbortException {
            SearchCorresponTypeCondition typeCondition = new SearchCorresponTypeCondition();
            typeCondition.setProjectId(page.getCurrentProjectId());
            page.typeList = page.corresponTypeService.search(typeCondition);

            page.workflowList = WorkflowStatus.values();
            page.readStatusList = ReadStatus.values();
            page.forLearningList = Arrays.stream(ForLearning.values())
                        .filter(e -> {return ForLearning.ALL != e;})
                        .collect(Collectors.toList())
                        .toArray(new ForLearning[0]);
            page.statusList = CorresponStatus.values();
            page.workflowProcessesList = WorkflowProcessStatus.values();
            page.fullTextSearchModeList = FullTextSearchMode.values();

            SearchUserCondition userCondition = new SearchUserCondition();
            userCondition.setProjectId(page.getCurrentProjectId());
            page.userList = page.userService.search(userCondition);

            SearchCorresponGroupCondition groupCondition = new SearchCorresponGroupCondition();
            groupCondition.setProjectId(page.getCurrentProjectId());
            page.groupList = page.corresponGroupService.search(groupCondition);

            SearchCustomFieldCondition customFieldCondition = new SearchCustomFieldCondition();
            customFieldCondition.setProjectId(page.getCurrentProjectId());
            page.customFieldList = page.customFieldService.search(customFieldCondition);
        }

        /**
         * 初期状態の検索条件を返却する.
         * @return 検索条件
         */
        private SearchCorresponCondition getDefaultCondition() {
            SearchCorresponCondition condition = new SearchCorresponCondition();
            // 初期化の時のみプロジェクトID、ユーザID、SystemAdmin、ProjectAdminをセット
            condition.setProjectId(page.getCurrentProjectId());
            condition.setUserId(page.getCurrentUser().getUserId());
            condition.setSystemAdmin(page.isSystemAdmin());
            condition.setProjectAdmin(page.isProjectAdmin(page.getCurrentProjectId()));

            ReadStatus[] readStatus = {ReadStatus.NEW};
            condition.setReadStatuses(readStatus);

            page.simpleSearch = true;
            condition.setSimpleSearch(true);

            return condition;
        }

    }

    static class AddFavoriteFilterAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1627506968905051426L;
        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * @param page
         */
        public AddFavoriteFilterAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            FavoriteFilter ff = new FavoriteFilter();
            ff.setFavoriteName(page.getFavoriteFilterName());
            ff.setUser(page.getCurrentUser());
            ff.setProjectId(page.getCurrentProjectId());
            ff.setSearchConditionsToJson(page.getCurrentSearchCorresponCondition());
            page.favoriteFilterService.save(ff);
            page.setPageMessage(ApplicationMessageCode.FAVORITE_FILTER_SAVED);
        }
    }

    /**
     * コレポン一覧検索アクション.
     * @author opentone
     */
    static class SearchAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4386301817388464850L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public SearchAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            setPageRowNumFromCookie();
            page.condition.setPageRowNum(page.pageRowNum);
            page.condition.setPageNo(page.pageNo);
            page.condition.setSort(page.sort);
            page.condition.setAscending(page.ascending);

            if (log.isDebugEnabled()) {
                log.debug("pageRowChangeSearch["
                            + page.pageRowChangeSearch + "]");
                log.debug("afterAction[" + page.afterAction + "]");
                log.debug("useSessionPageNo["
                    + page.useSessionPageNo + "]");
                log.debug("pageRowNum[" + page.pageRowNum + "]");
                log.debug("pageNo[" + page.pageNo + "]");
            }

            // ifの順番に注意。afterActionがuseSessionPageNoよりも優先。
            if (page.pageRowChangeSearch) {
                searchPageRowChange();
            } else if (page.afterAction) {
                searchAfterAction();
            } else if (page.useSessionPageNo) {
                searchPageNo();
            } else {
                search();
            }
        }

        /**
         * 検索.
         * 検索結果０件の場合Exceptionが発生し、条件を保存出来ないので
         * 検索実行の前に、検索条件を保持するようにする
         * @throws ServiceAbortException
         */
        private void search() throws ServiceAbortException {
            // 検索条件を保存
            page.setCurrentSearchCorresponCondition(page.condition);

            SearchCorresponResult result = page.corresponSearchService.search(page.condition);
            page.corresponList = result.getCorresponList();
            page.dataCount = result.getCount();

            createCorresponIndexList();
        }

        /**
         * アクションの後専用：検索を行う.
         * アクションの結果0件になってもエラーメッセージを表示しない.
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
                    page.setAfterAction(false);
                    throw e;
                }
            }
            // 処理が終わったらフラグを戻す
            page.setAfterAction(false);
        }

        /**
         * ページが設定された場合専用：検索を行う.
         * 検索の結果ページが無くなっていてもエラーメッセージを表示しない.
         * @throws ServiceAbortException NO_PAGE_FOUND以外のエラー
         */
        private void searchPageNo() throws ServiceAbortException {
            try {
                search();
            } catch (ServiceAbortException e) {
                // 検索の結果該当ページにレコードがない場合、前ページを表示する
                if (ApplicationMessageCode.NO_PAGE_FOUND.equals(e.getMessageCode())) {
                    page.pageNo--;
                    page.condition.setPageNo(page.pageNo);
                    searchPageNo();
                } else {
                    throw e;
                }
            }
        }

        /**
         * 1ページの表示件数が変更された場合専用：検索を行う.
         * 検索の結果ページが無くなっていてもエラーメッセージを表示しない.
         * @throws ServiceAbortException NO_PAGE_FOUND以外のエラー
         */
        private void searchPageRowChange() throws ServiceAbortException {
            try {
                search();
            } catch (ServiceAbortException e) {
                // 検索の結果該当ページにレコードがない場合、１ページを表示する
                if (ApplicationMessageCode.NO_PAGE_FOUND.equals(e.getMessageCode())) {
                    page.pageNo = 1;
                    page.condition.setPageNo(page.pageNo);
                    searchPageRowChange();
                } else {
                    throw e;
                }
            }
        }

        /**
         * 表示用の一覧オブジェクトを作成し、設定する.
         */
//        private void createCorresponIndexList() {
//            page.indexList = new ArrayList<CorresponIndex>();
//            List<Long> indexIdList = new ArrayList<Long>(100);
//
//            StringBuilder sb = new StringBuilder();
//            for (int i = 0; i < page.corresponList.size(); i++) {
//                Correspon correspon = page.corresponList.get(i);
//                CorresponIndex index = new CorresponIndex();
//                index.setCorrespon(correspon);
//
//                page.indexList.add(index);
//                setClassId(sb, i, correspon);
//
//                // コレポン文書のIDリストの作成
//                indexIdList.add(correspon.getId());
//            }
//            page.rowClasses = sb.toString();
//        }

        /**
         * 表示用の一覧オブジェクトを作成し、設定する.
         */
        private void createCorresponIndexList() {
            page.indexList = new ArrayList<CorresponIndex>();
            List<Long> indexIdList = new ArrayList<Long>(100);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < page.corresponList.size(); i++) {
                Correspon correspon = page.corresponList.get(i);
                CorresponIndex index = new CorresponIndex();
                index.setCorrespon(correspon);

                page.indexList.add(index);
                setClassId(sb, i, correspon);

                // コレポン文書のIDリストの作成
                indexIdList.add(correspon.getId());
            }
            page.rowClasses = sb.toString();
            // ページ内のコレポン文書IDリストとコレポン文書全件数をSessionに保存する
            page.getViewHelper().setSessionValue(Constants.KEY_CORRESPON_IDS, indexIdList);
            page.getViewHelper().setSessionValue(Constants.KEY_CORRESPON_DATA_COUNT, page.dataCount);
        }

        /**
         * TRタグ制御Classを設定する.
         * @param sb StringBuilder
         * @param rowNo Listの行数（0～）
         * @param correspon コレポン文書
         */
        private void setClassId(StringBuilder sb, int rowNo, Correspon correspon) {
            if (rowNo != 0) {
                sb.append(DELIM_CLASS);
            }

            boolean oddRow = (rowNo % 2 == 0); // 0行目=1行目=奇数行
            if (CorresponStatus.CANCELED.equals(correspon.getCorresponStatus())) {
                sb.append(CANCELED);
            } else if (DateUtil.isExpire(correspon.getDeadlineForReply())
                    && CorresponStatus.OPEN.equals(correspon.getCorresponStatus())) {
                sb.append(DEADLINE);
            } else if (oddRow) {
                sb.append(ODD);
            } else {
                sb.append(EVEN);
            }
        }

        /**
         * 1ページあたりの表示件数をクッキーから取得し設定する.
         */
        private void setPageRowNumFromCookie() {
            String cookieValue =
                page.viewHelper.getCookieValue(PAGE_ROW_COOKIE_NAME);
            if (log.isDebugEnabled()) {
                log.debug("cookieValue[" + cookieValue + "]");
            }

            try {
                if (StringUtils.isNotEmpty(cookieValue)) {
                    int pageRowNum = Integer.valueOf(cookieValue);
                    // PAGE_ROW_SELECTABLE_VALUESに記述してある値の場合のみ設定する
                    for (int i = 0; i < PAGE_ROW_SELECTABLE_VALUES.length; i++) {
                        if (pageRowNum == PAGE_ROW_SELECTABLE_VALUES[i]) {
                            if (log.isDebugEnabled()) {
                                log.debug("page.pageRowNum = " + pageRowNum);
                            }
                            page.pageRowNum = pageRowNum;
                            break;
                        }
                    }
                }
            } catch (NumberFormatException e) {
                log.info("cookieValue[" + cookieValue + "] is not a number");
            }
        }
    }

    /**
     * HTML表示アクション.
     * @author opentone
     */
    static class HtmlPrintAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4561318835323252801L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public HtmlPrintAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                // 全件指定
                SearchCorresponCondition allRowCondition
                    = (SearchCorresponCondition) page.cloneToAllRowCondition(page.condition);
                SearchCorresponResult result
                    = page.corresponSearchService.search(allRowCondition);
                byte[] data =
                        page.corresponSearchService.generateHTML(result.getCorresponList(),
                                                                 page.getCookieHeader());
                String charset = SystemConfig.getValue(Constants.KEY_HTML_ENCODING);
                page.viewHelper.requestResponse(data, charset);
            } catch (IOException e) {
                throw new ServiceAbortException("Html Request failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    /**
     * CSVダウンロードアクション.
     * @author opentone
     */
    static class CsvDownloadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 3170056482936612319L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public CsvDownloadAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                String fileName = page.createFileName() + ".csv";
                // 全件指定
                SearchCorresponCondition allRowCondition
                    = (SearchCorresponCondition) page.cloneToAllRowCondition(page.condition);
                SearchCorresponResult result
                    = page.corresponSearchService.search(allRowCondition);
                byte[] data = page.corresponSearchService.generateCsv(result.getCorresponList());
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException("Csv Download failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
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
        private static final long serialVersionUID = -3069601221480591700L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ExcelDownloadAction(CorresponIndexPage page) {
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
                SearchCorresponCondition allRowCondition
                    = (SearchCorresponCondition) page.cloneToAllRowCondition(page.condition);
                SearchCorresponResult result
                    = page.corresponSearchService.search(allRowCondition);
                byte[] data = page.corresponSearchService.generateExcel(result.getCorresponList());
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException("Excel Download failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
            }
        }

    }

    /**
     * Zipダウンロードアクション.
     * @author opentone
     */
    static class ZipDownloadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 4159409033113514937L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ZipDownloadAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                String fileName = page.createFileName() + ".zip";

                byte[] data = page.corresponSearchService.generateZip(
                    getSelectedCorresponList(), page.isUsePersonInCharge());
                page.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException("Zip Download failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
            }
        }

        /**
         * 画面で選択されたコレポン文書を取得する.
         * @return コレポン文書リスト
         */
        private List<Correspon> getSelectedCorresponList() {
            List<Correspon> list = new ArrayList<Correspon>();
            for (CorresponIndex index : page.indexList) {
                if (index.isChecked()) {
                    list.add(index.getCorrespon());
                }
            }
            return list;
        }
    }

    /**
     * 既読／未読状態更新アクション.
     * @author opentone
     */
    static class UpdateReadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 7417535001209445278L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;
        /** 既読／未読ステータス. */
        private boolean read;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public UpdateReadAction(CorresponIndexPage page, boolean read) {
            super(page);
            this.page = page;
            this.read = read;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponSearchService.updateCorresponsReadStatus(getSelectedCorresponList());

            page.afterAction = true;
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }

        /**
         * 画面で選択されたコレポン文書を取得する.
         * @return コレポン文書リスト
         */
        private List<Correspon> getSelectedCorresponList() {
            List<Correspon> list = new ArrayList<Correspon>();
            for (CorresponIndex index : page.indexList) {
                if (index.isChecked()) {
                    Correspon correspon = index.getCorrespon();
                    if (read) {
                        correspon.getCorresponReadStatus().setReadStatus(ReadStatus.READ);
                    } else {
                        correspon.getCorresponReadStatus().setReadStatus(ReadStatus.NEW);
                    }
                    list.add(correspon);
                }
            }
            return list;
        }
    }

    /**
     * 文書状態更新アクション.
     * @author opentone
     */
    static class UpdateStatusAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -3461297850821602601L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;
        /** 既読／未読ステータス. */
        private boolean open;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public UpdateStatusAction(CorresponIndexPage page, boolean open) {
            super(page);
            this.page = page;
            this.open = open;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            page.corresponSearchService.updateCorresponsStatus(getSelectedCorresponList());

            page.afterAction = true;
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }

        /**
         * 画面で選択されたコレポン文書を取得する.
         * @return コレポン文書リスト
         */
        private List<Correspon> getSelectedCorresponList() {
            List<Correspon> list = new ArrayList<Correspon>();
            for (CorresponIndex index : page.indexList) {
                if (index.isChecked()) {
                    Correspon correspon = (Correspon) page.cloneToObject(index.getCorrespon());
                    if (open) {
                        correspon.setCorresponStatus(CorresponStatus.OPEN);
                    } else {
                        correspon.setCorresponStatus(CorresponStatus.CLOSED);
                    }
                    list.add(correspon);
                }
            }
            return list;
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
        private static final long serialVersionUID = 7768186685056518870L;

        /** アクション発生元ページ. */
        private CorresponIndexPage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public DeleteAction(CorresponIndexPage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            List<Correspon> selected = getSelectedCorresponList();
            page.corresponSearchService.deleteCorrespons(selected);


            page.afterAction = true;
            page.setPageMessage(ApplicationMessageCode.CORRESPONS_DELETED);

            //イベント発火
            selected.stream().forEach(c -> page.eventBus
                    .raiseEvent(new CorresponDeleted(c.getId(), c.getProjectId())));
        }

        /**
         * 画面で選択されたコレポン文書を取得する.
         * @return コレポン文書リスト
         */
        private List<Correspon> getSelectedCorresponList() {
            List<Correspon> list = new ArrayList<Correspon>();
            for (CorresponIndex index : page.indexList) {
                if (index.isChecked()) {
                    list.add(index.getCorrespon());
                }
            }
            return list;
        }
    }


    public String getLearningPjId() {
        return this.learningPjId;
    }

    /**
     * 学習用プロジェクトか否かを返却する.
     * @return 学習用プロジェクトであるか否か
     */
    public boolean isLearningPj() {
        return this.learningPj;
    }

    /**
     * 学習用プロジェクトか否かを設定する.
     */
    public void setLearningPj(boolean learningPj) {
        this.learningPj = learningPj;
    }

}
