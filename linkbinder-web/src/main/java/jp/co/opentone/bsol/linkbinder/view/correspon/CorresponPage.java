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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.core.validation.constraints.DateString;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.FacesHelper;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Prerender;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupJSON;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.UserJSON;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowIndex;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFullTextSearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSearchService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.util.ValueFormatter;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.action.control.CorresponPageElementControl;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadablePage;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.DefaultModule;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.VerificationModule;
import jp.co.opentone.bsol.linkbinder.view.correspon.module.WorkflowEditModule;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.AddressModel;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.AddressUserModel;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponDataSource;
import jp.co.opentone.bsol.linkbinder.view.util.RichTextUtil;

/**
 * コレポン文書表示画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class CorresponPage extends AbstractCorresponPage implements AttachmentDownloadablePage {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -6819171074955985797L;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(CorresponPage.class);

    /**
     * 詳細を表示.
     * <p>
     * 詳細表示/表示制御リンクのラベル.
     * </p>
     */
    public static final String LABEL_SHOW_DETAILS = "詳細を表示";
    /**
     * 詳細を隠す.
     * <p>
     * 詳細表示/表示制御リンクのラベル.
     * </p>
     */
    public static final String LABEL_HIDE_DETAILS = "詳細を隠す";
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
     * 承認パターン1の取得キー.
     */
    private static final String KEY_PATTERN_1 = "workflow.pattern.1";

    /**
     * 承認パターン2の取得キー.
     */
    private static final String KEY_PATTERN_2 = "workflow.pattern.2";

    /**
     * TRタグ制御Classの区切り文字.
     */
    public static final String DELIM_CLASS = ",";

    /**
     * TRタグ制御Class：強調表示.
     */
    public static final String HIGHLIGHT = "highlight";

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
     * コレポンID一覧情報をFlashに保持する時のキー名.
     * @author opentone
     */
    private static final String KEY_CORRESPON_PAGE_CONTROLLER = "keyCorresponPageController";

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * 全文検索サービス.
     */
    @Resource
    //CHECKSTYLE:OFF
    public CorresponFullTextSearchService corresponFullTextSearchService;
    //CHECKSTYLE:ON

    /**
     * コレポン一覧検索サービス.
     */
    @Resource
    //CHECKSTYLE:OFF
    public CorresponSearchService corresponSearchService;
    //CHECKSTYLE:ON

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * コレポン文書表示モジュール.
     */
    @Resource
    private DefaultModule defaultModule;

    /**
     * 承認フロー編集モジュール.
     */
    @Resource
    private WorkflowEditModule workflowEditModule;

    /**
     * 承認・検証モジュール.
     */
    @Resource
    private VerificationModule verificationModule;

    /**
     * 表示対象コレポン文書のID.
     * <p>
     * 起動元画面からのクエリパラメータが自動設定される.
     * </p>
     */
    @Transfer
    private Long id;
    /**
     * 表示対象のコレポン文書.
     */
    @Transfer
    private Correspon correspon;
    /**
     * コレポン文書の表示内容を整形するオブジェクト.
     */
    private CorresponPageFormatter formatter;
    /**
     * 動的な画面構成要素.
     */
    @Transfer
    private CorresponPageElementControl elemControl
                       = new CorresponPageElementControl();

    /**
     * 宛先(To)に含まれる情報を保持するオブジェクト.
     */
    @Transfer
    private List<AddressModel> toAddressModel;
    /**
     * 宛先(Cc)に含まれる情報を保持するオブジェクト.
     */
    @Transfer
    private List<AddressModel> ccAddressModel;
    /**
     * 宛先(To)のDataModel.
     */
    private transient DataModel<?> toAddresses;
    /**
     * 宛先(Cc)のDataModel.
     */
    private transient DataModel<?> ccAddresses;
    /**
     * 画面のアクションにより特定された宛先-ユーザーID.
     */
    private Long detectedAddressUserId;
    /**
     * 画面のアクションにより特定された宛先-グループーID.
     */
    private Long detectedAddressGroupId;

    /**
     * ワークフローリスト.
     */
    @Transfer
    private List<Workflow> workflowList;
    /**
     * 添付ファイル有無(添付１).
     */
    @Transfer
    private boolean attachment1Checked;
    /**
     * 添付ファイル有無(添付２).
     */
    @Transfer
    private boolean attachment2Checked;
    /**
     * 添付ファイル有無(添付３).
     */
    @Transfer
    private boolean attachment3Checked;
    /**
     * 添付ファイル有無(添付４).
     */
    @Transfer
    private boolean attachment4Checked;
    /**
     * 添付ファイル有無(添付５).
     */
    @Transfer
    private boolean attachment5Checked;
    /**
     * 宛先情報表示/非表示制御リンクのラベル.
     */
    private String tosDetail;
    /**
     * 宛先情報表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String tosDisplay;
    /**
     * 本文表示/非表示制御リンクのラベル.
     */
    private String bodyDetail;
    /**
     * 本文表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String bodyDisplay;
    /**
     * 添付ファイル情報表示/非表示制御リンクのラベル.
     */
    private String attachmentsDetail;
    /**
     * 添付ファイル情報表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String attachmentsDisplay;
    /**
     * カスタムフィールド情報表示/非表示制御リンクのラベル.
     */
    private String customFieldsDetail;

    /**
     * 応答履歴情報表示/非表示制御リンクのラベル.
     */
    @Transfer
    private String responseHistoryDetail;
    /**
     * カスタムフィールド情報表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String customFieldsDisplay;
    /**
     * 応答履歴情報表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String responseHistoryDisplay;
    /**
     * コピーダイアログ画面内の添付ファイル情報表示エリアの表示状態.
     */
    @Transfer
    private boolean copyAttachmentDisplay;
    /**
     * ワークフロー情報表示/非表示制御リンクのラベル.
     */
    private String workflowDetail;
    /**
     * ワークフロー情報表示エリアの表示状態.
     * <p>
     * CSSのdisplayに設定する値 (none/block)
     * </p>
     */
    @Transfer
    private String workflowDisplay;

    /**
     * 処理対象のコレポン文書ID.
     * <p>
     * ダウンロードや返信済文書表示に使用する.
     * </p>
     */
    private Long corresponId;
    /**
     * ダウンロード対象ファイルのID.
     */
    private Long fileId;

    /**
     * 表示対象displayBody.
     * <p>
     * body.
     * </p>
     */
    @Transfer
    private String displayBody;

    /**
     * Person in Charge選択リストで選択された活動単位.
     * <p>
     * このページでは特に使用しないが、JSFのタグ中に記載する必要があるため定義.
     * </p>
     */
    private Long group;

    /**
     * 現在のプロジェクト内に存在する全ての活動単位.
     */
    @Transfer
    private List<CorresponGroup> groups;

    /**
     * 現在のプロジェクトに所属する全てのユーザー.
     */
    @Transfer
    private List<ProjectUser> users;

    /**
     * 活動単位とユーザーのマッピングリスト.
     */
    @Transfer
    private List<CorresponGroupUserMapping> corresponGroupUserMappings;

    /**
     * 既読・未読制御.
     */
    private String readMode;

    /**
     * 入力値：返信要否.
     * <p>
     * Issue後、特定条件を満たすユーザーであれば
     * 入力項目として表示される.
     * </p>
     */
    @Transfer
    private Integer replyRequired;
    /**
     * 入力項目：返信要否.
     * <p>
     * Issue後、特定条件を満たすユーザーであれば
     * 入力項目として表示される.
     * </p>
     */
    @Transfer
    private List<SelectItem> selectReplyRequired;
    /**
     * 入力項目：返信期限.
     * <p>
     * Issue後、特定条件を満たすユーザーであれば
     * 入力項目として表示される.
     * </p>
     */
    @Transfer
    @DateString
    private String deadlineForReply;

    /**
     * 返信済文書表示フラグ.
     * <p>
     * 返信済文書の別画面表示時にtrueに設定される
     * </p>
     */
    private boolean displayReplied;

    /**
     * 返信済コレポン文書ID.
     * <p>
     * 返信済文書の別画面表示時に値が設定される
     * </p>
     */
    private Long detectedRepliedId;
    /**
     * 返信済コレポン文書.
     * <p>
     * 返信済文書の別画面表示時に値が設定される
     * </p>
     */
    private Correspon replied;
    /**
     * コレポン文書の表示内容を整形するオブジェクト(返信済文書用).
     */
    private CorresponPageFormatter repliedFormatter;
    /**
     * 返信済文書の本文.
     */
    private String repliedDisplayBody;
    /**
     * 返信済文書の宛先(To)に含まれる情報を保持するオブジェクト.
     */
    private List<AddressModel> repliedToAddressModel;
    /**
     * 返信済文書の宛先(Cc)に含まれる情報を保持するオブジェクト.
     */
    private List<AddressModel> repliedCcAddressModel;
    /**
     * 返信済文書の宛先(To)のDataModel.
     */
    private transient DataModel<?> repliedToAddresses;
    /**
     * 返信済文書の宛先(Cc)のDataModel.
     */
    private transient DataModel<?> repliedCcAddresses;
    /**
     * 返信済文書のワークフローリスト.
     */
    private List<Workflow> repliedWorkflowList;

    /**
     * 応答履歴リスト.
     */
    @Transfer
    private List<CorresponResponseHistory> corresponResponseHistory;
    /**
     * 応答履歴のDataModel.
     */
    private transient DataModel<?> corresponResponseHistories;

    /**
     * データ更新用ワークフローリスト.
     */
    @Transfer
    private List<Workflow> workflow;

    /**
     * 表示用のワークフローリスト(Verification).
     */
    @Transfer
    private List<WorkflowIndex> workflowIndex;

    /**
     * 表示用ワークフロー(EditWorkflow).
     */
    @Transfer
    private List<Workflow> workflowForEditView;

    /**
     * 自身のワークフロー.
     */
    @Transfer
    private Workflow usersWorkflow;

    /**
     * ワークフローDataModel.
     */
    private transient DataModel<?> dataModel;

    /**
     * コメント.
     */
    @Transfer
    //CHECKSTYLE:OFF
    @Length(max = 500)
    //CHECKSTYLE:ON
    private String comment;

    /**
     * Checkボタン押下の制御.
     */
    @Transfer
    private boolean check;

    /**
     * Aproveボタン押下の制御.
     */
    @Transfer
    private boolean approve;

    /**
     * Denyボタン押下の制御.
     */
    @Transfer
    private boolean deny;

    /**
     * TRタグ制御.
     */
    @Transfer
    private String trClassId;

    /**
     * 初期画面表示判定値.
     */
    @Transfer
    private boolean initialDisplaySuccess = false;

    /**
     * 承認フロー編集ダイアログ表示フラグ.
     * <p>
     * 承認フロー編集ダイアログ表示時にtrueに設定される
     * </p>
     */
    @Transfer
    private boolean workflowEditDisplay = false;

    /**
     * 承認・検証ダイアログ表示フラグ.
     * <p>
     * 承認・検証ダイアログ表示時にtrueに設定される
     * </p>
     */
    @Transfer
    private boolean verificationDisplay = false;

    /**
     * ワークフロー番号.
     */
    @Transfer
    private Long workflowNo = 1L;

    /**
     * 活動単位.
     */
    @Transfer
    private List<CorresponGroup> corresponGroup;

    /**
     * ユーザー.
     */
    @Transfer
    private List<User> user;

    /**
     * 入力項目：ワークフローNo.
     */
    @Transfer
    private List<SelectItem> selectWorkflowNo;

    /**
     * 入力項目：活動単位.
     */
    @Transfer
    private List<SelectItem> selectGroup;

    /**
     * 入力項目：ユーザー.
     */
    @Transfer
    private List<SelectItem> selectUser;

    /**
     * ワークフローDataModel.
     */
    private transient DataModel<?> workflowModel;

    /**
     * ユーザーID.
     */
    @Transfer
    @Required
    private String userId;

    /**
     * 入力項目：役割.
     */
    @Transfer
    private List<SelectItem> selectWorkflowType;

    /**
     * グループID.
     */
    @Transfer
    private Long groupId;

    /**
     * 役割.
     */
    private Integer role;

    /**
     * 選択した承認フローテンプレート.
     */
    @Transfer
    @Required
    private Long template;

    /**
     * 承認フローテンプレートリスト.
     */
    @Transfer
    private List<WorkflowTemplate> workflowTemplateList;

    /**
     * 承認フローテンプレートユーザーリスト.
     */
    @Transfer
    private List<WorkflowTemplateUser> workflowTemplateUserList;

    /**
     * 承認フローテンプレートDataModel.
     */
    @Transfer
    private List<SelectItem> selectTemplate;

    /**
     * 承認フローテンプレート名.
     */
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String templateName;

    /**
     * ワークフローダイアログのスクロール位置(初期表示時のみ設定).
     */
    private String scrollPosition;

    /**
     * 遷移元を判断する値(一覧).
     */
    private String fromIndex;

    /**
     * 遷移元を判断する値(編集).
     */
    private String fromEdit;

    /**
     * 操作対象の宛先種別.
     * デフォルトはTo
     */
    private Integer targetAddressType = AddressType.TO.getValue();

    /**
     * ダイアログで使用する各JSON形式の情報の読み込み済みかどうか.
     * <p>
     * 読み込み済みの場合はtrue.
     * </p>
     */
    private boolean jsonValuesLoaded;

    /**
     * 表示総数.
     */
    private String displayTotalCount;

    /**
     * 現在の処理がコレポン文書切り替えモードの場合はtrue.
     */
    @Transfer
    private boolean changeCorrespon;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponPage() {
    }

    /**
     * Viewのレンダリング直前に呼び出されるイベントハンドラ.
     */
    public void preRenderView() {
        FacesHelper h = new FacesHelper(FacesContext.getCurrentInstance());
        if (h.isGetRequest()) {
            //TODO Flashから状態を取得し、関連するコレポン文書の場合は状態を復元
            System.out.println("preRenderView called.");
        }
    }

    /**
     * commentの入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getCommentValidationGroups() {
        if (isCheckAction() || isApproveAction() || isDenyAction()) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * userIdの入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getUserIdValidationGroups() {
        if (isActionInvoked("form:add")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * templateの入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getTemplateValidationGroups() {
        if (isActionInvoked("form:apply") || isActionInvoked("form:deleteTemplate")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * templateNameの入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getTemplateNameValidationGroups() {
        if (isActionInvoked("form:saveTemplate")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * deadlineForReplyの入力検証グループ名を返す.
     *
     * @return 入力検証グループ名
     */
    public String getDeadlineForReplyValidationGroups() {
        if (isActionInvoked("form:savePartial")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }

    /**
     * 現在のリクエストがsavePartialアクションによって発生した場合はtrue.
     * @return savePartialアクションの場合true
     */
    public boolean isSavePartialAction() {
        return isActionInvoked("form:savePartial");
    }

    /**
     * 現在のリクエストがcancelアクションによって発生した場合はtrue.
     * @return cancelアクションの場合true
     */
    public boolean isCancel() {
        return isActionInvoked("form:cancel");
    }

    /**
     * 現在のリクエストがcheckアクションによって発生した場合はtrue.
     * @return checkアクションの場合true
     */
    public boolean isCheckAction() {
        return getRequestedActionName().equals("Check");
    }

    /**
     * 現在のリクエストがapproveアクションによって発生した場合はtrue.
     * @return approveアクションの場合true
     */
    public boolean isApproveAction() {
        return getRequestedActionName().equals("Approve");
    }

    /**
     * 現在のリクエストがdenyアクションによって発生した場合はtrue.
     * @return denyアクションの場合true
     */
    public boolean isDenyAction() {
        return getRequestedActionName().equals("Deny");
    }

    /**
     * 現在のリクエストがdeleteアクションによって発生した場合はtrue.
     * @return deleteアクションの場合true
     */
    public boolean isDeleteAction() {
        return isActionInvoked("form:delete");
    }

    /**
     * 現在のリクエストがaddアクションによって発生した場合はtrue.
     * @return addアクションの場合true
     */
    public boolean isAddAction() {
        return isActionInvoked("form:add");
    }

    /**
     * 現在のリクエストがapplyアクションによって発生した場合はtrue.
     * @return applyアクションの場合true
     */
    public boolean isApplyAction() {
        return isActionInvoked("form:apply");
    }

    /**
     * 現在のリクエストがdeleteTemplateアクションによって発生した場合はtrue.
     * @return deleteTemplateアクションの場合true
     */
    public boolean isDeleteTemplateAction() {
        return isActionInvoked("form:deleteTemplate");
    }

    /**
     * 現在のリクエストがsaveTemplateアクションによって発生した場合はtrue.
     * @return saveTemplateアクションの場合true
     */
    public boolean isSaveTemplateAction() {
        return isActionInvoked("form:saveTemplate");
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、
     * コレポン文書を表示する.
     * </p>
     */
    @Initialize
    public void initialize() {
        // ブラウザの戻るを使用された時用にダイアログ表示を無効にしておく
        workflowEditDisplay = false;
        verificationDisplay = false;

        setRequiredFields(defaultModule);
        if (isChangeCorrespon()) {
            receiveTransferValue();
            defaultModule.changeCorrespon();
        } else {
            // 画面初期化時は応答履歴をクリアする
            responseHistoryDetail = LABEL_SHOW_DETAILS;
            responseHistoryDisplay = STYLE_HIDE;
            corresponResponseHistory = null;

            // コレポン文書のページ単位制御クラスの初期化
            corresponPageController = new CorresponPageController();
            defaultModule.initialize();
        }
    }

    /**
     * 画面を再読み込みする.
     */
    public void reload() {
        setRequiredFields(defaultModule);
        defaultModule.setSkipInitCorresponIds(true);
        defaultModule.initialize();
    }

    /**
     * 承認フロー編集ダイアログを初期化する.
     * @return null
     */
    public String editWorkflow() {
        setRequiredFields(workflowEditModule);
        return workflowEditModule.initialize();
    }

    /**
     * 承認・検証ダイアログを初期化する.
     * @return null
     */
    public String verify() {
        setRequiredFields(verificationModule);
        return verificationModule.initialize();
    }

    /**
     * ページ描画前の処理.
     * <p>
     * ページ描画直前に必ず起動される.
     * </p>
     */
    @Prerender
    public void prerender() {
        setShowDetail();
    }

    /**
     * 詳細表示の状態を設定する.
     */
    private void setShowDetail() {
        setShowDetailTosDisplay();
        setShowDetailBodyDisplay();
        setShowDetailAttachmentsDisplay();
        setShowDetailCustomFieldsDisplay();
        setShowDetailResponseHistoryDisplay();
        setShowDetailWorkflowDisplay();
    }
    /**
     * 詳細表示の状態を設定する(宛先情報).
     */
    private void setShowDetailTosDisplay() {
        if (STYLE_SHOW.equals(tosDisplay)) {
            tosDetail = LABEL_HIDE_DETAILS;
        } else {
            tosDetail = LABEL_SHOW_DETAILS;
            tosDisplay = STYLE_HIDE;
        }
    }
    /**
     * 詳細表示の状態を設定する(本文).
     */
    private void setShowDetailBodyDisplay() {
        if (STYLE_SHOW.equals(bodyDisplay)) {
            bodyDetail = LABEL_HIDE_DETAILS;
        } else {
            bodyDetail = LABEL_SHOW_DETAILS;
            bodyDisplay = STYLE_HIDE;
        }
    }
    /**
     * 詳細表示の状態を設定する(添付ファイル情報).
     */
    private void setShowDetailAttachmentsDisplay() {
        if (STYLE_SHOW.equals(attachmentsDisplay)) {
            attachmentsDetail = LABEL_HIDE_DETAILS;
        } else {
            attachmentsDetail = LABEL_SHOW_DETAILS;
            attachmentsDisplay = STYLE_HIDE;
        }
    }
    /**
     * 詳細表示の状態を設定する(カスタムフィールド情報).
     */
    private void setShowDetailCustomFieldsDisplay() {
        if (STYLE_SHOW.equals(customFieldsDisplay)) {
            customFieldsDetail = LABEL_HIDE_DETAILS;
        } else {
            customFieldsDetail = LABEL_SHOW_DETAILS;
            customFieldsDisplay = STYLE_HIDE;
        }
    }
    /**
     * 詳細表示の状態を設定する(応答履歴情報).
     */
    private void setShowDetailResponseHistoryDisplay() {
        if (STYLE_SHOW.equals(responseHistoryDisplay)) {
            responseHistoryDetail = LABEL_HIDE_DETAILS;
        } else {
            responseHistoryDetail = LABEL_SHOW_DETAILS;
            responseHistoryDisplay = STYLE_HIDE;
        }
    }
    /**
     * 詳細表示の状態を設定する(ワークフロー情報).
     */
    private void setShowDetailWorkflowDisplay() {
        if (STYLE_SHOW.equals(workflowDisplay)) {
            workflowDetail = LABEL_HIDE_DETAILS;
        } else {
            workflowDetail = LABEL_SHOW_DETAILS;
            customFieldsDisplay = STYLE_HIDE;
        }
    }

    /**
     * @param defaultModule the defaultModule to set
     */
    public void setDefaultModule(DefaultModule defaultModule) {
        this.defaultModule = defaultModule;
    }

    /**
     * @return the defaultModule
     */
    public DefaultModule getDefaultModule() {
        setRequiredFields(defaultModule);
        return defaultModule;
    }

    /**
     * @param workflowEditModule the workflowEditModule to set
     */
    public void setWorkflowEditModule(WorkflowEditModule workflowEditModule) {
        this.workflowEditModule = workflowEditModule;
    }

    /**
     * @return the workflowEditModule
     */
    public WorkflowEditModule getWorkflowEditModule() {
        setRequiredFields(workflowEditModule);
        return workflowEditModule;
    }

    /**
     * @param verificationModule the verificationModule to set
     */
    public void setVerificationModule(VerificationModule verificationModule) {
        this.verificationModule = verificationModule;
    }

    /**
     * @return the verificationModule
     */
    public VerificationModule getVerificationModule() {
        setRequiredFields(verificationModule);
        return verificationModule;
    }

    /**
     * @return the module
     */
    public DefaultModule getModule() {
        DefaultModule module = null;
        if (isWorkflowEditDisplay()) {
            // 承認フロー編集ダイアログ表示中
            module = workflowEditModule;
        } else if (isVerificationDisplay()) {
            // 承認・検証ダイアログ表示中
            module = verificationModule;
        } else {
            // 通常
            module = defaultModule;
        }
        setRequiredFields(module);
        return module;
    }

    /**
     * コレポン文書を返信する.
     *
     * @return 遷移先
     */
    public String reply() {
        String result = this.getModule().reply();
        return toUrl(result);
    }

    /**
     * コレポン文書を改訂する.
     *
     * @return 遷移先
     */
    public String  revise() {
        String result = this.getModule().revise();
        return toUrl(result);
    }

    /**
     * コレポン文書を更新する.
     *
     * @return 遷移先
     */
    public String update() {
        String result = this.getModule().update();
        return toUrl(result);
    }

    /**
     * HTML出力（印刷）を行う.
     * @return null
     */
    public String print() {
        String retsult = this.getModule().print();
        return retsult;
    }

    /**
     * コレポン文書のHTMLと全ての添付ファイルをZIP形式にしてダウンロードを行う.
     * @return 実行結果
     */
    public String downloadZip() {
        String result = this.getModule().downloadZip();
        return result;
    }

    /**
     * 返信済文書をコピーしてコレポン文書を返信する.
     *
     * @return 遷移先
     */
    public String replyWithPreviousCorrespon() {
        String result = this.getModule().replyWithPreviousCorrespon();
        return toUrl(result);
    }

    /**
     * コレポン文書一覧画面に遷移する.
     * 遷移後の画面では、ソートアイテムとソート順そしてページ番号を初期化しないで表示.
     * @return 遷移先
     */
    public String back() {
        String result = this.getModule().back();
        return toUrl(result);
    }

    /**
     * 1つ前のコレポンページを表示する.
     * @return null
     * @throws ServiceAbortException
     */
    public String movePrevious() throws ServiceAbortException {
        String result = this.getModule().movePrevious();
        return result;
    }

    /**
     * 1つ後のコレポンページを表示する.
     * @return null
     * @throws ServiceAbortException
     */
    public String moveNext() throws ServiceAbortException {
        String result = this.getModule().moveNext();
        return result;
    }

    /**
     * 別コレポンを再読み込みする.
     * <p>
     * 返信先、改訂元、応答履歴のコレポン文書表示時に使用する.
     * </p>
     * @return 実行結果
     */
    public String changeCorrespon() {
        // 次ページ(実際はこのページ)に引き継ぐ情報を設定し、リダイレクト
        this.changeCorrespon = true;
        setTransferNext(true);
        transferCorresponDisplayInfo();
        transferBackPage();
        return toUrl(String.format("correspon?id=%s&projectId=%s",
                           getId(), getCurrentProjectId()));
    }

    /**
     * ダイアログで使用するJSON形式の情報を読み込み.
     * 実際にはフラグをtrueにしておき、非同期で再読み込みをさせる.
     * @return null
     * @throws ServiceAbortException
     */
    public String loadJsonValues() throws ServiceAbortException {
        String result = this.getModule().loadJsonValues();
        return result;
    }

    /**
     * 返信状況表示をトグルする.
     * @return null
     */
    public String replyStatus() {
        String result = this.getModule().replyStatus();
        return result;
    }

    /**
     * 返信済文書を取得する.
     * @return null
     */
    public String showReplied() {
//        String result = this.getModule().showReplied();
        return toUrl(String.format("corresponReplied?id=%s", getDetectedRepliedId()));
    }

    /**
     * 入力可能な項目に限り、部分的な更新を行う.
     * @return null
     */
    public String save() {
        String result = this.getModule().save();
        return result;
    }

    /**
     * 指定された添付ファイルをダウンロードする.
     * @return null
     */
    public String download() {
        String result = this.getModule().download();
        return result;
    }

    /**
     * 応答履歴の表示を行う.
     * @return 実行結果
     */
    public String showResponseHistory() {
        String result = this.getModule().showResponseHistory();
        return result;
    }

    /**
     * 発行を行う.
     * @return 遷移先
     */
    public String issue() {
        String result = this.getModule().issue();
        return toUrl(result);
    }

    /**
     * コレポン文書を検証依頼状態にする.
     *
     * @return null
     */
    public String requestForApproval() {
        String result = this.getModule().requestForApproval();
        return result;
    }

    /**
     * 既読状態を更新する(Read).
     *
     * @return null
     */
    public String read() {
        String result = this.getModule().read();
        return result;
    }

    /**
     * 未読状態を更新する(Unread).
     *
     * @return null
     */
    public String unread() {
        String result = this.getModule().unread();
        return result;
    }

    /**
     * 文書状態を更新する(Open).
     *
     * @return null
     */
    public String open() {
        String result = this.getModule().open();
        return result;
    }

    /**
     * 文書状態を更新する(Closed).
     *
     * @return null
     */
    public String close() {
        String result = this.getModule().close();
        return result;
    }

    /**
     * 文書状態を更新する(Canceled).
     *
     * @return null
     */
    public String cancel() {
        String result = this.getModule().cancel();
        return result;
    }

    /**
     * 削除を行う.
     *
     * @return URL
     */
    public String delete() {
        String result = this.getModule().delete();
        return toUrl(result);
    }

    /**
     * コレポン文書を転送登録する.
     *
     * @return URL
     */
    public String forward() {
        String result = this.getModule().forward();
        return toUrl(result);
    }

    /**
     * コレポン文書を複写登録する.
     *
     * @return URL
     */
    public String copy() {
        String result = this.getModule().copy();
        return toUrl(result);
    }

    /**
     * Person in Chargeを設定する.
     *
     * @return URL
     */
    public String assignTo() {
        String result = this.getModule().assignTo();
        return toUrl(result);
    }

    /**
     * モジュールを使用する際に必ず必要になるフィールドをセットする.
     * @param module
     */
    private void setRequiredFields(DefaultModule module) {
        module.setCorresponPage(this);
        module.setServiceActionHandler(handler);
        module.setViewHelper(viewHelper);
        if (((toAddresses != null) && toAddresses.isRowAvailable())
            || ((ccAddresses != null) && ccAddresses.isRowAvailable())) {
            module.setDetectAddressModel(detectAddressModel());
            if (getDetectedAddressUserId() != null) {
                module.setDetectAddressUser(detectAddressUser());
                module.setSelectedPersonInCharges(getSelectedPersonInCharges(detectAddressUser()));
            }
        }
    }

    /**
     * 返信要否を表示するSelectItemを作成する.
     * @param values SelectItemに設定する返信要否
     */
    public void createSelectReplyRequired(ReplyRequired[] values) {
        setSelectReplyRequired(viewHelper.createSelectItem(values));
    }

    /**
     * 返信期限を編集可能な、返信要否の値をJavaScriptの配列形式で返す.
     * @return 返信要否の値
     */
    public String getEditableReplyRequiredValues() {
        return StringUtils.join(CorresponDataSource.EDITABLE_REPLY_REQUIRED_VALUES, ',');
    }

    /**
     * プロジェクト内の全ての活動単位のリストをJSON形式に変換して返す.
     * @return プロジェクト内の全ての活動単位リストのJSON形式
     */
    public String getGroupJSONString() {
        List<CorresponGroup> list = getGroups();
        if (list != null) {
            return JSONUtil.encode(convertJSON(list));
        } else {
            return null;
        }
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setGroupJSONString(String value) {
        // 何もしない
    }

    /**
     * 活動単位とユーザーのマッピング情報をJSON形式に変換して返す.
     * @return 活動単位とユーザーのマッピング情報のJSON形式
     */
    public String getGroupUserMappingsJSONString() {
        Map<Long, List<String>> mappings = new HashMap<Long, List<String>>();
        List<CorresponGroupUserMapping> list = getCorresponGroupUserMappings();
        if (list == null) {
            return JSONUtil.encode(mappings);
        }
        for (CorresponGroupUserMapping gu : list) {
            Long corresponGroupId = gu.getCorresponGroupId();
            List<String> empNos = new ArrayList<String>();
            for (int i = 0; i < gu.getUsers().size(); i++) {
                User u = gu.getUsers().get(i);
                empNos.add(u.getEmpNo());
            }
//            for (User u : gu.getUsers()) {
//                empNos.add(u.getEmpNo());
//            }
            mappings.put(corresponGroupId, empNos);
        }
        return JSONUtil.encode(mappings);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setGroupUserMappingsJSONString(String value) {
        //  何もしない
    }

    /**
     * プロジェクト内の全てのユーザーのリストをJSON形式に変換して返す.
     * @return プロジェクト内の全てのユーザーリストのJSON形式
     */
    public String getUserJSONString() {
        Map<String, UserJSON> result = new HashMap<String, UserJSON>();
        List<ProjectUser> list = getUsers();
        if (list != null) {
            for (ProjectUser u : list) {
                result.put(u.getUser().getEmpNo(), UserJSON.newInstance(u.getUser()));
            }
        }
        return JSONUtil.encode(result);
    }

    /**
     * JSFの実装上必要なSetter.処理は何も行わない.
     * @param value 値
     */
    public void setUserJSONString(String value) {
        //  何もしない
    }

    /**
     * 活動単位リストをJSON変換用のDTOに変換する.
     * @param gs 活動単位リスト
     * @return JSON変換用活動単位
     */
    private List<CorresponGroupJSON> convertJSON(List<CorresponGroup> gs) {
        List<CorresponGroupJSON> jsonGroups = new ArrayList<CorresponGroupJSON>();
        for (CorresponGroup g : gs) {
            jsonGroups.add(CorresponGroupJSON.newInstance(g));
        }
        return jsonGroups;
    }


    /**
     * アクションが発生した宛先オブジェクトを判別する.
     * 返されるのは{@link #toAddresses}、{@link #ccAddresses}のいずれかから取得できる
     * {@link AddressModel}オブジェクト.
     * @return アクションが発生した宛先オブジェクト
     * @throws ApplicationFatalRuntimeException
     *             宛先で発生したアクション以外から呼び出された場合.通常これはバグである.
     */
    protected AddressModel detectAddressModel() throws ApplicationFatalRuntimeException {
        AddressModel address = null;
        if (toAddresses.isRowAvailable()
                && AddressType.TO.getValue().equals(targetAddressType)) {
            address = (AddressModel) toAddresses.getRowData();
        } else if (ccAddresses.isRowAvailable()
                && AddressType.CC.getValue().equals(targetAddressType)) {
            address = (AddressModel) ccAddresses.getRowData();
        } else {
            throw new ApplicationFatalRuntimeException("address not detected.");
        }
        return address;
    }

    /**
     * アクションが発生した宛先オブジェクトを返す.
     * @return アクションが発生した宛先オブジェクト
     */
    protected AddressUserModel detectAddressUser() {
        AddressModel address = detectAddressModel();
        AddressUserModel u = null;
        if (address != null) {
            u = address.getAddressUserModelById(detectedAddressUserId);
        }
        if (log.isDebugEnabled()) {
            if (address != null) {
                log.debug(
                    String.format(
                        "***** selected Group = %d, %s, %s%n",
                        address.getAddressGroup().getId(),
                        address.getAddressGroup().getAddressType(),
                        address.getCorresponGroup().getName()));
            }
            if (u != null) {
                log.debug(
                    String.format("***** selected User = %d, %s, %s%n",
                        u.getAddressUser().getId(),
                        u.getAddressUser().getAddressUserType(),
                        u.getUser().getEmpNo()));
            }
        }
        return u;
    }

    /**
     * 選択されたPerson in Chargeのリストを返す.
     * @param addressUserModel Attention/Cc
     * @return 選択されたPerson in Chargeのリスト
     */
    protected List<PersonInCharge> getSelectedPersonInCharges(AddressUserModel addressUserModel) {
        List<PersonInCharge> result = new ArrayList<PersonInCharge>();
        if (addressUserModel == null) {
            return result;
        }
        if (StringUtils.isEmpty(addressUserModel.getPersonInChargeValues())) {
            return result;
        }

        String[] values = addressUserModel.getPersonInChargeValues().split(",");
        for (String empNo : values) {
//            for (ProjectUser u : users) {
//                if (u.getUser().getEmpNo().equals(empNo)) {
//                    PersonInCharge pic = new PersonInCharge();
//                    pic.setUser(u.getUser());
//                    result.add(pic);
//                }
//            }

            // 先読みしたグループ・ユーザー情報からではなくマスタから読み込む modified by s.sasaki
            try {
                User user = userService.findByEmpNo(empNo);
                PersonInCharge pic = new PersonInCharge();
                pic.setUser(user);
                result.add(pic);
            } catch (ServiceAbortException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 現在の宛先(To)DataModelのインデックスカウンタを取得.
     * @return index
     */
    public int getToAddrIndex() {
        int index = toAddresses.getRowIndex();
        return index;
    }

    /**
     * @param toAddresses the toAddresses to set
     */
    public void setToAddresses(DataModel<?> toAddresses) {
        this.toAddresses = toAddresses;
    }

    /**
     * @return the toAddresses
     */
    public DataModel<?> getToAddresses() {
        if (toAddresses == null) {
            toAddresses = new ListDataModel<AddressModel>();
        }
        if (toAddressModel != null) {
            toAddresses.setWrappedData(toAddressModel);
        }
        return toAddresses;
    }

    /**
     * @param ccAddresses the ccAddresses to set
     */
    public void setCcAddresses(DataModel<?> ccAddresses) {
        this.ccAddresses = ccAddresses;
    }

    /**
     * @return the ccAddresses
     */
    public DataModel<?> getCcAddresses() {
        if (ccAddresses == null) {
            ccAddresses = new ListDataModel<AddressModel>();
        }
        if (ccAddressModel != null) {
            ccAddresses.setWrappedData(ccAddressModel);
        }
        return ccAddresses;
    }

    /**
     * 現在の宛先(Cc)のDataModelのカウンタインデックスを取得.
     * @return index
     */
    public int getCcAddrIndex() {
        int index = ccAddresses.getRowIndex();
        return index;
    }

    /**
     * コレポン文書の表示内容を整形するオブジェクトを返す.
     * @return コレポン文書整形オブジェクト
     */
    public CorresponPageFormatter getFormatter() {
        if (formatter == null) {
            formatter = new CorresponPageFormatter(correspon);
        }
        formatter.setCorrespon(correspon);
        return formatter;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment
     *   .AttachmentDownloadablePage#getDownloadingAttachmentInfo()
     */
    public AttachmentInfo getDownloadingAttachmentInfo() {
        Long downloadingFileId = getFileId();
        Attachment a = null;
        for (Attachment attachment : correspon.getAttachments()) {
            if (downloadingFileId.equals(attachment.getId())) {
                a = attachment;
                break;
            }
        }

        return new SavedAttachmentInfo(a, corresponService);
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }
    /**
     * @param correspon
     *            the correspon to set
     */
    public void setCorrespon(Correspon correspon) {
        this.correspon = correspon;
    }
    /**
     * @return the correspon
     */
    public Correspon getCorrespon() {
        return correspon;
    }
    /**
     * @param elemControl
     *            the elemControl to set
     */
    public void setElemControl(CorresponPageElementControl elemControl) {
        this.elemControl = elemControl;
    }
    /**
     * @return the elemControl
     */
    public CorresponPageElementControl getElemControl() {
        return elemControl;
    }
    /**
     * @param tosDetail
     *            the tosDetail to set
     */
    public void setTosDetail(String tosDetail) {
        this.tosDetail = tosDetail;
    }
    /**
     * @return the tosDetail
     */
    public String getTosDetail() {
        return tosDetail;
    }
    /**
     * @param tosDisplay
     *            the tosDisplay to set
     */
    public void setTosDisplay(String tosDisplay) {
        this.tosDisplay = tosDisplay;
    }
    /**
     * @return the tosDisplay
     */
    public String getTosDisplay() {
        return tosDisplay;
    }
    /**
     * @param attachmentsDetail
     *            the attachmentsDetail to set
     */
    public void setAttachmentsDetail(String attachmentsDetail) {
        this.attachmentsDetail = attachmentsDetail;
    }
    /**
     * @return the attachmentsDetail
     */
    public String getAttachmentsDetail() {
        return attachmentsDetail;
    }
    /**
     * @param attachmentsDisplay
     *            the attachmentsDisplay to set
     */
    public void setAttachmentsDisplay(String attachmentsDisplay) {
        this.attachmentsDisplay = attachmentsDisplay;
    }
    /**
     * @return the attachmentsDisplay
     */
    public String getAttachmentsDisplay() {
        return attachmentsDisplay;
    }
    /**
     * @param customFieldsDetail
     *            the customFieldsDetail to set
     */
    public void setCustomFieldsDetail(String customFieldsDetail) {
        this.customFieldsDetail = customFieldsDetail;
    }
    /**
     * @return the customFieldsDetail
     */
    public String getCustomFieldsDetail() {
        return customFieldsDetail;
    }
    /**
     * @param responseHistoryDetail
     *            the responseHistoryDetail to set
     */
    public void setResponseHistoryDetail(String responseHistoryDetail) {
        this.responseHistoryDetail = responseHistoryDetail;
    }
    /**
     * @return the responseHistoryDetail
     */
    public String getResponseHistoryDetail() {
        return responseHistoryDetail;
    }
    /**
     * @param customFieldsDisplay
     *            the customFieldsDisplay to set
     */
    public void setCustomFieldsDisplay(String customFieldsDisplay) {
        this.customFieldsDisplay = customFieldsDisplay;
    }
    /**
     * @return the customFieldsDisplay
     */
    public String getCustomFieldsDisplay() {
        return customFieldsDisplay;
    }
    /**
     * @param responseHistoryDisplay
     *            the responseHistoryDisplay to set
     */
    public void setResponseHistoryDisplay(String responseHistoryDisplay) {
        this.responseHistoryDisplay = responseHistoryDisplay;
    }
    /**
     * @return the responseHistoryDisplay
     */
    public String getResponseHistoryDisplay() {
        return responseHistoryDisplay;
    }
    /**
     * @param copyAttachmentDisplay
     *            the copyAttachmentDisplay to set
     */
    public void setCopyAttachmentDisplay(boolean copyAttachmentDisplay) {
        this.copyAttachmentDisplay = copyAttachmentDisplay;
    }
    /**
     * @return the copyAttachmentDisplay
     */
    public boolean getCopyAttachmentDisplay() {
        return copyAttachmentDisplay;
    }
    public Long getFileId() {
        return fileId;
    }
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }
    /**
     * @param bodyDetail
     *            the bodyDetail to set
     */
    public void setBodyDetail(String bodyDetail) {
        this.bodyDetail = bodyDetail;
    }
    /**
     * @return the bodyDetail
     */
    public String getBodyDetail() {
        return bodyDetail;
    }
    /**
     * @param bodyDisplay
     *            the bodyDisplay to set
     */
    public void setBodyDisplay(String bodyDisplay) {
        this.bodyDisplay = bodyDisplay;
    }
    /**
     * @return the bodyDisplay
     */
    public String getBodyDisplay() {
        return bodyDisplay;
    }
    /**
     * @param workflowDisplay
     *            the workflowDisplay to set
     */
    public void setWorkflowDisplay(String workflowDisplay) {
        this.workflowDisplay = workflowDisplay;
    }
    /**
     * @return the workflowDisplay
     */
    public String getWorkflowDisplay() {
        return workflowDisplay;
    }
    /**
     * @param workflowDetail
     *            the workflowDetail to set
     */
    public void setWorkflowDetail(String workflowDetail) {
        this.workflowDetail = workflowDetail;
    }
    /**
     * @return the workflowDetail
     */
    public String getWorkflowDetail() {
        return workflowDetail;
    }
    /**
     * @return boolean
     */
    public boolean isAttachment1Checked() {
        return attachment1Checked;
    }
    /**
     * @param attachment1Checked
     *            the attachment1Checked to set
     */
    public void setAttachment1Checked(boolean attachment1Checked) {
        this.attachment1Checked = attachment1Checked;
    }
    /**
     * @return boolean
     */
    public boolean isAttachment2Checked() {
        return attachment2Checked;
    }
    /**
     * @param attachment2Checked
     *            the attachment2Checked to set
     */
    public void setAttachment2Checked(boolean attachment2Checked) {
        this.attachment2Checked = attachment2Checked;
    }
    /**
     * @return boolean
     */
    public boolean isAttachment3Checked() {
        return attachment3Checked;
    }
    /**
     * @param attachment3Checked
     *            the attachment3Checked to set
     */
    public void setAttachment3Checked(boolean attachment3Checked) {
        this.attachment3Checked = attachment3Checked;
    }
    /**
     * @return boolean
     */
    public boolean isAttachment4Checked() {
        return attachment4Checked;
    }
    /**
     * @param attachment4Checked
     *            the attachment4Checked to set
     */
    public void setAttachment4Checked(boolean attachment4Checked) {
        this.attachment4Checked = attachment4Checked;
    }
    /**
     * @return boolean
     */
    public boolean isAttachment5Checked() {
        return attachment5Checked;
    }
    /**
     * @param attachment5Checked
     *            the attachment5Checked to set
     */
    public void setAttachment5Checked(boolean attachment5Checked) {
        this.attachment5Checked = attachment5Checked;
    }
    /**
     * @param workflowList
     *            the workflowList to set
     */
    public void setWorkflowList(List<Workflow> workflowList) {
        this.workflowList = workflowList;
    }
    /**
     * @return the workflowList
     */
    public List<Workflow> getWorkflowList() {
        return workflowList;
    }
    /**
     * @param displayBody
     *            the displayBody to set
     */
    public void setDisplayBody(String displayBody) {
        this.displayBody = displayBody;
    }
    /**
     * @return the displayBody
     */
    public String getDisplayBody() {
        return new RichTextUtil().createRichText(this.displayBody);
    }

    /**
     * @return the readMode
     */
    public String getReadMode() {
        return readMode;
    }
    /**
     * @param readMode
     *            the readMode to set
     */
    public void setReadMode(String readMode) {
        this.readMode = readMode;
    }


    /**
     * @param detectedAddressUserId the detectedAddressUserId to set
     */
    public void setDetectedAddressUserId(Long detectedAddressUserId) {
        this.detectedAddressUserId = detectedAddressUserId;
    }
    /**
     * @return the detectedAddressUserId
     */
    public Long getDetectedAddressUserId() {
        return detectedAddressUserId;
    }

    /**
     * @param detectedAddressGroupId the detectedAddressGroupId to set
     */
    public void setDetectedAddressGroupId(Long detectedAddressGroupId) {
        this.detectedAddressGroupId = detectedAddressGroupId;
    }
    /**
     * @return the detectedAddressGroupId
     */
    public Long getDetectedAddressGroupId() {
        return detectedAddressGroupId;
    }

    /**
     * @param toAddressModel the toAddressModel to set
     */
    public void setToAddressModel(List<AddressModel> toAddressModel) {
        this.toAddressModel = toAddressModel;
    }
    /**
     * @return the toAddressModel
     */
    public List<AddressModel> getToAddressModel() {
        return toAddressModel;
    }

    /**
     * @param ccAddressModel the ccAddressModel to set
     */
    public void setCcAddressModel(List<AddressModel> ccAddressModel) {
        this.ccAddressModel = ccAddressModel;
    }
    /**
     * @return the ccAddressModel
     */
    public List<AddressModel> getCcAddressModel() {
        return ccAddressModel;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(Long group) {
        this.group = group;
    }
    /**
     * @return the group
     */
    public Long getGroup() {
        return group;
    }

    /**
     * @param groups the groups to set
     */
    public void setGroups(List<CorresponGroup> groups) {
        this.groups = groups;
    }
    /**
     * @return the groups
     */
    public List<CorresponGroup> getGroups() {
        return groups;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<ProjectUser> users) {
        this.users = users;
    }

    /**
     * @return the users
     */
    public List<ProjectUser> getUsers() {
        return users;
    }

    /**
     * @param corresponGroupUserMappings the corresponGroupUserMappings to set
     */
    public void setCorresponGroupUserMappings(
            List<CorresponGroupUserMapping> corresponGroupUserMappings) {
        this.corresponGroupUserMappings = corresponGroupUserMappings;
    }

    /**
     * @return the corresponGroupUserMappings
     */
    public List<CorresponGroupUserMapping> getCorresponGroupUserMappings() {
        return corresponGroupUserMappings;
    }

    /**
     * @param replyRequired the replyRequired to set
     */
    public void setReplyRequired(Integer replyRequired) {
        this.replyRequired = replyRequired;
    }

    /**
     * @return the replyRequired
     */
    public Integer getReplyRequired() {
        return replyRequired;
    }

    /**
     * @param deadlineForReply the deadlineForReply to set
     */
    public void setDeadlineForReply(String deadlineForReply) {
        this.deadlineForReply = deadlineForReply;
    }

    /**
     * @return the deadlineForReply
     */
    public String getDeadlineForReply() {
        return deadlineForReply;
    }

    /**
     * @param selectReplyRequired the selectReplyRequired to set
     */
    public void setSelectReplyRequired(List<SelectItem> selectReplyRequired) {
        this.selectReplyRequired = selectReplyRequired;
    }

    /**
     * @return the selectReplyRequired
     */
    public List<SelectItem> getSelectReplyRequired() {
        return selectReplyRequired;
    }


    /**
     * @param displayReplied the displayReplied to set
     */
    public void setDisplayReplied(boolean displayReplied) {
        this.displayReplied = displayReplied;
    }

    /**
     * @return the displayReplied
     */
    public boolean isDisplayReplied() {
        return displayReplied;
    }


    /**
     * @param replied the replied to set
     */
    public void setReplied(Correspon replied) {
        this.replied = replied;
    }

    /**
     * @return the replied
     */
    public Correspon getReplied() {
        return replied;
    }

    /**
     * @param repliedFormatter the repliedFormatter to set
     */
    public void setRepliedFormatter(CorresponPageFormatter repliedFormatter) {
        this.repliedFormatter = repliedFormatter;
    }
    /**
     * @return the repliedFormatter
     */
    public CorresponPageFormatter getRepliedFormatter() {
        if (repliedFormatter == null) {
            repliedFormatter = new CorresponPageFormatter(replied);
        }
        repliedFormatter.setCorrespon(replied);
        return repliedFormatter;
    }

    /**
     * @param repliedDisplayBody the repliedDisplayBody to set
     */
    public void setRepliedDisplayBody(String repliedDisplayBody) {
        this.repliedDisplayBody = repliedDisplayBody;
    }
    /**
     * @return the repliedDisplayBody
     */
    public String getRepliedDisplayBody() {
        return new RichTextUtil().createRichText(this.repliedDisplayBody);
    }

    /**
     * @param detectedRepliedId the detectedRepliedId to set
     */
    public void setDetectedRepliedId(Long detectedRepliedId) {
        this.detectedRepliedId = detectedRepliedId;
    }
    /**
     * @return the detectedRepliedId
     */
    public Long getDetectedRepliedId() {
        return detectedRepliedId;
    }

    /**
     * @param repliedToAddressModel the repliedToAddressModel to set
     */
    public void setRepliedToAddressModel(List<AddressModel> repliedToAddressModel) {
        this.repliedToAddressModel = repliedToAddressModel;
    }
    /**
     * @return the repliedToAddressModel
     */
    public List<AddressModel> getRepliedToAddressModel() {
        return repliedToAddressModel;
    }

    /**
     * @param repliedCcAddressModel the repliedCcAddressModel to set
     */
    public void setRepliedCcAddressModel(List<AddressModel> repliedCcAddressModel) {
        this.repliedCcAddressModel = repliedCcAddressModel;
    }
    /**
     * @return the repliedCcAddressModel
     */
    public List<AddressModel> getRepliedCcAddressModel() {
        return repliedCcAddressModel;
    }

    /**
     * @param repliedToAddresses the repliedToAddresses to set
     */
    public void setRepliedToAddresses(DataModel<?> repliedToAddresses) {
        this.repliedToAddresses = repliedToAddresses;
    }

    /**
     * @return the repliedToAddresses
     */
    public DataModel<?> getRepliedToAddresses() {
        if (repliedToAddresses == null) {
            repliedToAddresses = new ListDataModel<AddressModel>();
        }
        if (repliedToAddressModel != null) {
            repliedToAddresses.setWrappedData(repliedToAddressModel);
        }
        return repliedToAddresses;
    }

    /**
     * 現在の返信済文書の宛先(To)のDataModelのインデックスカウンタを取得.
     * @return index
     */
    public int getRepliedToAddIndex() {
        int index = repliedToAddresses.getRowIndex();
        return index;
    }

    /**
     * @param repliedCcAddresses the repliedCcAddresses to set
     */
    public void setRepliedCcAddresses(DataModel<?> repliedCcAddresses) {
        this.repliedCcAddresses = repliedCcAddresses;
    }

    /**
     * @return the repliedCcAddresses
     */
    public DataModel<?> getRepliedCcAddresses() {
        if (repliedCcAddresses == null) {
            repliedCcAddresses = new ListDataModel<AddressModel>();
        }
        if (repliedCcAddressModel != null) {
            repliedCcAddresses.setWrappedData(repliedCcAddressModel);
        }
        return repliedCcAddresses;
    }

    /**
     * 現在の返信済文書の宛先(Cc)のDataModelのカウンタインデックスを取得.
     * @return index
     */
    public int getRepliedCcAddrIndex() {
        int index = repliedCcAddresses.getRowIndex();
        return index;
    }

    /**
     * @param repliedWorkflowList the repliedWorkflowList to set
     */
    public void setRepliedWorkflowList(List<Workflow> repliedWorkflowList) {
        this.repliedWorkflowList = repliedWorkflowList;
    }
    /**
     * @return the repliedWorkflowList
     */
    public List<Workflow> getRepliedWorkflowList() {
        return repliedWorkflowList;
    }

    /**
     * @param corresponId the corresponId to set
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }
    /**
     * @return the corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * @param initialDisplaySuccess the initialDisplaySuccess to set
     */
    public void setInitialDisplaySuccess(boolean initialDisplaySuccess) {
        this.initialDisplaySuccess = initialDisplaySuccess;
    }

    /**
     * @return the initialDisplaySuccess
     */
    public boolean getInitialDisplaySuccess() {
        return initialDisplaySuccess;
    }

    /**
     * 通常表示状態かどうかを返す.
     * <p>
     * 承認フロー編集ダイアログ表示状態、もしくは承認・検証ダイアログ表示状態の場合はfalse.<br />
     * ダイアログが表示されていない場合はtrueを返す.
     * </p>
     * @return ダイアログ表示中はfalse、ダイアログ非表示はtrue
     */
    public boolean isDefaultDisplay() {
        return (!isWorkflowEditDisplay() && !isVerificationDisplay());
    }

    /**
     * @param workflowEditDisplay the workflowEditDisplay to set
     */
    public void setWorkflowEditDisplay(boolean workflowEditDisplay) {
        this.workflowEditDisplay = workflowEditDisplay;
    }

    /**
     * @return the workflowEditDisplay
     */
    public boolean isWorkflowEditDisplay() {
        return workflowEditDisplay;
    }

    /**
     * @param verificationDisplay the verificationDisplay to set
     */
    public void setVerificationDisplay(boolean verificationDisplay) {
        this.verificationDisplay = verificationDisplay;
    }

    /**
     * @return the verificationDisplay
     */
    public boolean isVerificationDisplay() {
        return verificationDisplay;
    }

    /**
     * @param workflow
     *            the workflow to set
     */
    public void setWorkflow(List<Workflow> workflow) {
        this.workflow = workflow;
    }

    /**
     * @return the workflow
     */
    public List<Workflow> getWorkflow() {
        return workflow;
    }

    /**
     * @param workflowIndex
     *            the workflowIndex to set
     */
    public void setWorkflowIndex(List<WorkflowIndex> workflowIndex) {
        this.workflowIndex = workflowIndex;
    }

    /**
     * @return the workflowIndex
     */
    public List<WorkflowIndex> getWorkflowIndex() {
        return workflowIndex;
    }

    /**
     * @param usersWorkflow
     *            the usersWorkflow to set
     */
    public void setUsersWorkflow(Workflow usersWorkflow) {
        this.usersWorkflow = usersWorkflow;
    }

    /**
     * @return the usersWorkflow
     */
    public Workflow getUsersWorkflow() {
        return usersWorkflow;
    }

    /**
     * @param dataModel
     *            the deleteModel to set
     */
    public void setDataModel(DataModel<?> dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the deleteModel
     */
    public DataModel<?> getDataModel() {
        if (dataModel == null) {
            dataModel = new ListDataModel<WorkflowIndex>();
        }
        if (workflowIndex != null) {
            dataModel.setWrappedData(workflowIndex);
        }
        return dataModel;
    }

    /**
     * @return the deleteModel
     */
    public DataModel<?> getCurrentDataModel() {
        return dataModel;
    }

    /**
     * コメントを設定する.
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * コメントを取得する.
     * @return comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * 全て削除可能であればtrue.
     * @return 全て削除可能であればtrue
     */
    public boolean isAllDeletable() {
        return correspon.getWorkflowStatus() == WorkflowStatus.DRAFT;
    }

    /**
     * 削除可能な承認フローレコードの場合はtrue.
     * <p>
     * このメソッドはJSFのDataTableレンダリング時に呼び出されることを想定している.
     * @return 削除可能な場合はtrue
     */
    public boolean isDeletable() {
        Workflow w = (Workflow) workflowModel.getRowData();
        //  Preparer
        if (w.getWorkflowType() == null) {
            return false;
        }

        switch (correspon.getWorkflowStatus()) {
        case DRAFT:
            //  Draftの場合は削除可能
            return true;
        case DENIED:
        case ISSUED:
            //  Denied、Issuedの場合は削除不可
            return false;
        default:
            //  承認フローパターン、ユーザーの権限・役割により削除可否を判定
            return checkDeletable();
        }
    }

    private boolean checkDeletable() {
        WorkflowPattern p = correspon.getCorresponType().getWorkflowPattern();
        String p1 = SystemConfig.getValue(KEY_PATTERN_1);
        String p2 = SystemConfig.getValue(KEY_PATTERN_2);
        Workflow current = (Workflow) workflowModel.getRowData();

        //  保存できない新しいレコードを追加して「Save」に失敗した場合を想定して
        //  新しく追加したレコードは削除できるようにしておかなければならない
        if (current.getId() == null || current.getId().equals(0L)) {
            return true;
        }

        //  現在行がログインユーザー自身であるか、またはログインユーザーが管理者であるか
        boolean me    = isMe(current);
        boolean admin = isAdmin();

        boolean result;
        if (p1.equals(p.getWorkflowCd())) {
            //  ・承認フローパターン1は先頭から順番に検証・承認を行う
            //  ・CheckerとApproverを兼務はできない
            //  ・検証・承認未実施であれば変更可能
            //  つまり自分以外または管理者であれば、後続の検証・承認者ということ
            result = (!me || admin)
                     && current.getWorkflowProcessStatus() == WorkflowProcessStatus.NONE;

        } else if (p2.equals(p.getWorkflowCd())) {
            //  ・承認フローパターン2は承認；未実施のApproverのみ変更可能
            //  ・承認者は自身を変更することはできない
            //  ・管理者であれば自身を変更することもできる
            result = (!me || admin)
                     && current.getWorkflowType() == WorkflowType.APPROVER
                     && current.getWorkflowProcessStatus() == WorkflowProcessStatus.NONE;
        } else {
            result = false;
        }
        return result;
    }

    private boolean isMe(Workflow current) {
        return current.getUser().getEmpNo().equals(getCurrentUser().getEmpNo());
    }

    private boolean isAdmin() {
        return isSystemAdmin()
               || isProjectAdmin(getCurrentProjectId())
               || isAnyGroupAdmin(correspon);
    }

    /**
     * 検証を設定する.
     * @param check the check to set
     */
    public void setCheck(boolean check) {
        this.check = check;
    }

    /**
     * 検証を取得する.
     * @return check
     */
    public boolean isCheck() {
        return check;
    }

    /**
     * Checkerによる承認を行う.
     * @return null
     */
    public String check() {
         String result = ((VerificationModule) this.getModule()).check();
         result = toUrl(result);
         return result;
    }

    /**
     * Approverによる承認を行う.
     * @return null
     */
    public String approve() {
        String result = ((VerificationModule) this.getModule()).approve();
        return result == null ? result : toUrl(result);
    }

    /**
     * 否認する.
     * @return null
     */
    public String deny() {
        String result = ((VerificationModule) this.getModule()).deny();
        return result == null ? result : toUrl(result);
    }






    /**
     * 承認を設定する.
     * @param approve the approve to set
     */
    public void setApprove(boolean approve) {
        this.approve = approve;
    }

    /**
     * 承認を取得する.
     * @return approve
     */
    public boolean isApprove() {
        return approve;
    }

    /**
     * 否認を設定する.
     * @param deny the deny to set
     */
    public void setDeny(boolean deny) {
        this.deny = deny;
    }

    /**
     * 否認を取得する.
     * @return deny
     */
    public boolean isDeny() {
        return deny;
    }

    /**
     * TRタグ クラスIDを設定する.
     * @param trClassId ID
     */
    public void setTrClassId(String trClassId) {
        this.trClassId = trClassId;
    }

    /**
     * TRタグ クラスIDを取得する.
     * @return trClassId
     */
    public String getTrClassId() {
        return trClassId;
    }

    /**
     * @param workflowNo the workflowNo to set
     */
    public void setWorkflowNo(Long workflowNo) {
        this.workflowNo = workflowNo;
    }

    /**
     * @return the workflowNo
     */
    public Long getWorkflowNo() {
        return workflowNo;
    }

    /**
     * @return the corresponGroup
     */
    public List<CorresponGroup> getCorresponGroup() {
        return corresponGroup;
    }

    /**
     * @param corresponGroup the corresponGroup to set
     */
    public void setCorresponGroup(List<CorresponGroup> corresponGroup) {
        this.corresponGroup = corresponGroup;
    }

    /**
     * @return the List
     */
    public List<User> getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(List<User> user) {
        this.user = user;
    }

    /**
     * @param selectWorkflowNo the selectWorkflowNo to set
     */
    public void setSelectWorkflowNo(List<SelectItem> selectWorkflowNo) {
        this.selectWorkflowNo = selectWorkflowNo;
    }

    /**
     * @return the selectWorkflowNo
     */
    public List<SelectItem> getSelectWorkflowNo() {
        return selectWorkflowNo;
    }

    /**
     * @param selectGroup the selectGroup to set
     */
    public void setSelectGroup(List<SelectItem> selectGroup) {
        this.selectGroup = selectGroup;
    }

    /**
     * @return the selectGroup
     */
    public List<SelectItem> getSelectGroup() {
        return selectGroup;
    }

    /**
     * @param selectUser the selectUser to set
     */
    public void setSelectUser(List<SelectItem> selectUser) {
        this.selectUser = selectUser;
    }

    /**
     * @return the selectUser
     */
    public List<SelectItem> getSelectUser() {
        return selectUser;
    }

    /**
     * @param selectWorkflowType the selectWorkflowType to set
     */
    public void setSelectWorkflowType(List<SelectItem> selectWorkflowType) {
        this.selectWorkflowType = selectWorkflowType;
    }

    public List<SelectItem> getSelectWorkflowType() {
        return selectWorkflowType;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param workflowModel the workflowModel to set
     */
    public void setWorkflowModel(DataModel<?> workflowModel) {
        this.workflowModel = workflowModel;
    }

    /**
     * @return the workflowModel
     */
    public DataModel<?> getWorkflowModel() {
        if (workflowModel == null) {
            workflowModel = new ListDataModel<Workflow>();
        }
        if (workflowForEditView != null) {
            workflowModel.setWrappedData(workflowForEditView);
        }
        return workflowModel;
    }

    /**
     * @return the workflowModel
     */
    public DataModel<?> getCurrentWorkflowModel() {
        return workflowModel;
    }

    /**
     * @param groupId the groupId to set
     */
    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the groupId
     */
    public Long getGroupId() {
        return groupId;
    }

    /**
     * @param role the role to set
     */
    public void setRole(Integer role) {
        this.role = role;
    }

    /**
     * @return the role
     */
    public Integer getRole() {
        return role;
    }

    /**
     * 一覧画面表示用ワークフローを返す.
     * @return 一覧画面表示用ワークフロー
     */
    public List<Workflow> getWorkflowForEditView() {
        return workflowForEditView;
    }

    /**
     * 一覧画面表示用ワークフローを設定する.
     * @param workflowForEditView 一覧画面表示用ワークフロー
     */
    public void setWorkflowForEditView(List<Workflow> workflowForEditView) {
        this.workflowForEditView = workflowForEditView;
    }

    /**
     * 承認フローテンプレートリストを返す.
     * @return 承認フローテンプレートリスト
     */
    public List<WorkflowTemplate> getWorkflowTemplateList() {
        return workflowTemplateList;
    }

    /**
     * 承認フローテンプレートリストを設定する.
     * @param workflowTemplateList 承認フローテンプレートリスト
     */
    public void setWorkflowTemplateList(List<WorkflowTemplate> workflowTemplateList) {
        this.workflowTemplateList = workflowTemplateList;
    }

    /**
     * 承認フローテンプレートユーザーリストを返す.
     * @return 承認フローテンプレートユーザーリスト
     */
    public List<WorkflowTemplateUser> getWorkflowTemplateUserList() {
        return workflowTemplateUserList;
    }

    /**
     * 承認フローテンプレートユーザーリストを設定する.
     * @param workflowTemplateUserList 承認フローテンプレートユーザーリスト
     */
    public void setWorkflowTemplateUserList(List<WorkflowTemplateUser> workflowTemplateUserList) {
        this.workflowTemplateUserList = workflowTemplateUserList;
    }

    /**
     * 選択した承認フローテンプレートIDを設定する.
     * @param template 承認フローテンプレートID
     */
    public void setTemplate(Long template) {
        this.template = template;
    }

    /**
     * 選択した承認フローテンプレートIDを返す.
     * @return 承認フローテンプレートID
     */
    public Long getTemplate() {
        return template;
    }

    /**
     * 承認フローテンプレートDataModelを設定する.
     * @param selectTemplate 承認フローテンプレートDataModel
     */
    public void setSelectTemplate(List<SelectItem> selectTemplate) {
        this.selectTemplate = selectTemplate;
    }

    /**
     * 承認フローテンプレートDataModelを返す.
     * @return 承認フローテンプレートDataModel
     */
    public List<SelectItem> getSelectTemplate() {
        return selectTemplate;
    }

    /**
     * 承認フローテンプレートを設定する.
     * @param templateName 承認フローテンプレート名
     */
    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    /**
     * 承認フローテンプレート名を返す.
     * @return 承認フローテンプレート名
     */
    public String getTemplateName() {
        return templateName;
    }

    /**
     * ワークフローダイアログの表示位置を設定する.
     * @param scrollPosition ワークフローダイアログの表示位置
     */
    public void setScrollPosition(String scrollPosition) {
        this.scrollPosition = scrollPosition;
    }

    /**
     * ワークフローダイアログの表示位置を返す.
     * @return ワークフローダイアログの表示位置
     */
    public String getScrollPosition() {
        return scrollPosition;
    }

    /**
     * 遷移元が一覧かを判断する値をセットする.
     * @param fromIndex 遷移元が一覧かを判断する値
     */
    public void setFromIndex(String fromIndex) {
        this.fromIndex = fromIndex;
    }

    /**
     * 遷移元が一覧かを判断する値を返す.
     * 一覧から遷移した場合1
     * @return 遷移元が一覧かを判断する値
     */
    public String getFromIndex() {
        return fromIndex;
    }

    /**
     * 遷移元が編集かを判断する値をセットする.
     * @param fromEdit 遷移元が編集かを判断する値
     */
    public void setFromEdit(String fromEdit) {
        this.fromEdit = fromEdit;
    }

    /**
     * 遷移元が編集かを判断する値を返す.
     * コレポン編集確認の場合1
     * @return 遷移元が編集かを判断する値
     */
    public String getFromEdit() {
        return fromEdit;
    }

    /**
     * 宛先に対する操作がToとCcのどちらに対してのものかを表す値をセットする.
     * @param targetAddressType 操作対象に宛先種別
     */
    public void setTargetAddressType(Integer targetAddressType) {
        this.targetAddressType = targetAddressType;
    }

    /**
     * 宛先に対する操作がToとCcのどちらに対してのものかを表す値を返す.
     * @return 操作対象に宛先種別
     */
    public Integer getTargetAddressType() {
        return targetAddressType;
    }
    /**
     * @param corresponResponseHistory the corresponResponseHistory to set
     */
    public void setCorresponResponseHistory(
        List<CorresponResponseHistory> corresponResponseHistory) {
        this.corresponResponseHistory = corresponResponseHistory;
    }
    /**
     * @return the corresponResponseHistory
     */
    public List<CorresponResponseHistory> getCorresponResponseHistory() {
        return corresponResponseHistory;
    }

    /**
     * @param corresponResponseHistories the corresponResponseHistory to set
     */
    public void setCorresponResponseHistories(DataModel<?> corresponResponseHistories) {
        this.corresponResponseHistories = corresponResponseHistories;
    }

    /**
     * @return the corresponResponseHistories
     */
    public DataModel<?> getCorresponResponseHistories() {
        if (corresponResponseHistories == null) {
            corresponResponseHistories = new ListDataModel<CorresponResponseHistory>();
        }
        if (corresponResponseHistory != null) {
            corresponResponseHistories.setWrappedData(corresponResponseHistory);
        }
        return corresponResponseHistories;
    }

    /**
     * rowClassesを取得します.
     * @return the rowClasses
     */
    public String getRowClasses() {
        if (corresponResponseHistory == null) {
            return "";
        }

        if (displayReplied) {
            return createCorresponIndexList(replied.getId());
        } else {
            return createCorresponIndexList(correspon.getId());
        }
    }

    /**
     * 表示用の一覧オブジェクトを作成し、設定する.
     */
    private String createCorresponIndexList(Long corrId) {
        StringBuilder sb = new StringBuilder();
        int size = corresponResponseHistory.size();
        for (int i = 0; i < size; i++) {
            CorresponResponseHistory history = corresponResponseHistory.get(i);
            setClassId(sb, i, history, corrId);
        }
        return sb.toString();
    }

    /**
     * TRタグ制御Classを設定する.
     * @param sb StringBuilder
     * @param rowNo Listの行数（0～）
     * @param correspon コレポン文書
     * @param corrId 強調表示するコレポン文書ID
     */
    private void setClassId(StringBuilder sb,
            int rowNo, CorresponResponseHistory history, Long corrId) {
        if (rowNo != 0) {
            sb.append(DELIM_CLASS);
        }

        boolean oddRow = (rowNo % 2 == 0); // 0行目=1行目=奇数行
        if (history.getCorrespon().getId().equals(corrId)) {
            sb.append(HIGHLIGHT);
        } else if (CorresponStatus.CANCELED.equals(history.getCorrespon().getCorresponStatus())) {
            sb.append(CANCELED);
        } else if (DateUtil.isExpire(history.getCorrespon().getDeadlineForReply())
                && CorresponStatus.OPEN.equals(history.getCorrespon().getCorresponStatus())) {
            sb.append(DEADLINE);
        } else if (oddRow) {
            sb.append(ODD);
        } else {
            sb.append(EVEN);
        }
    }

    /**
     * HTMLに表示するアイコンを取得する.
     * @return アイコン
     */
    public String getIconPathName() {
        CorresponPageFormatter corresponPageFormatter = new CorresponPageFormatter();
        if (getCurrentProject() != null) {
            return corresponPageFormatter.getProjectLogoUrl(getCurrentProject().getProjectId());
        } else {
            return null;
        }
    }

    /**
     * 表示用にフォーマットした一覧の総件数.
     * @return 総件数
     */
    public String getDisplayTotalCount() {
        displayTotalCount = ValueFormatter.formatNumber(getTotalCount());
        return displayTotalCount;
    }

    /**
     * 表示中のコレポンが全体の何番目に位置するかを表す数値.
     * @return コレポンの一覧上での位置
     */
    public String getDisplayPositionNo() {
        return ValueFormatter.formatNumber(getDisplayIndex() + 1);
    }

    /**
     * @param jsonValuesLoaded the jsonValuesLoaded to set
     */
    public void setJsonValuesLoaded(boolean jsonValuesLoaded) {
        this.jsonValuesLoaded = jsonValuesLoaded;
    }

    /**
     * @return the jsonValuesLoaded
     */
    public boolean isJsonValuesLoaded() {
        return jsonValuesLoaded;
    }

    /**
     * 「Prev」が操作可能であればtrueを返す.
     * @return コレポン文書を移動可能ならtrue
     */
    public boolean isPreviousLink() {
        return getDisplayIndex() > 0;
    }

    /**
     * 「Next」が操作可能であればtrueを返す.
     * @return コレポン文書を移動可能ならtrue
     */
    public boolean isNextLink() {
        return getDisplayIndex() < getTotalCount() - 1;
    }

    /**
     * @param changeCorrespon the changeCorrespon to set
     */
    public void setChangeCorrespon(boolean changeCorrespon) {
        this.changeCorrespon = changeCorrespon;
    }

    /**
     * @return the changeCorrespon
     */
    public boolean isChangeCorrespon() {
        return changeCorrespon;
    }

    // コレポン文書の移動をページ単位内で行うようにする対応 (ページ内リストを超えたら再読み込みする) -------------------------------------

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.AbstractCorresponPage#getTotalCount()
     */
    @Override
    public int getTotalCount() {
        CorresponPageController controller = getCorresponPageController();
        if (controller != null) {
            return controller.getTotalCount();
        } else {
            return 0;
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.AbstractCorresponPage#receiveTransferValue()
     */
    @Override
    public void receiveTransferValue() {
        super.receiveTransferValue();
        Flash flash = new Flash();
        corresponPageController = flash.getValue(KEY_CORRESPON_PAGE_CONTROLLER);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.AbstractCorresponPage#transferCorresponDisplayInfo()
     */
    @Override
    public void transferCorresponDisplayInfo() {
        super.transferCorresponDisplayInfo();
        Flash flash = new Flash();
        flash.setValue(KEY_CORRESPON_PAGE_CONTROLLER, corresponPageController);
    }

    /**
     * コレポン文書のページ単位制御クラス.
     * @author opentone
     */
    public class CorresponPageController {
        // コレポン文書の全件数.
        private int totalCount;
        // 表示しているコレポン文書が属するページ番号
        private int currentPageNo;
        // １ページ単位での最大件数
        private int pageRowNum;

        // ページ内リストのインデックス番号 (物理番号)
        private int indexNo;

        /**
         * 全コレポン文書内での番号 (論理番号).
         * @return int 論理番号.
         */
        public int getLogicalIndexNo() {
            return (currentPageNo - 1) * pageRowNum + indexNo;
        }

        /**
         * 直前のコレポン文書IDを取得する.
         * @return Long コレポン文書ID.
         * @throws ServiceAbortException
         */
        public Long getPreviousId() throws ServiceAbortException {
            // 直前がページ内の場合
            if (indexNo > 0) {
                return getCorresponIds().get(--indexNo);
            }
            // ページ外で前ページがある場合は再読み込み
            if (currentPageNo > 0) {
                // ----------------------------------------------
                // 全文検索
                // ----------------------------------------------
                if (getBackPage() != null && getBackPage().equals("corresponSearch")) {
                    SearchFullTextSearchCorresponCondition searchFullTextSearchCorresponCondition = getCurrentSearchFullTextSearchCorresponCondition();
                    if (searchFullTextSearchCorresponCondition != null) {
                        SearchFullTextSearchCorresponCondition condition = (SearchFullTextSearchCorresponCondition)SerializationUtils.clone(searchFullTextSearchCorresponCondition);
                        condition.setPageNo(--currentPageNo);
                        try {
                            List<Long> corresponIds = corresponFullTextSearchService.searchId(condition);
                            setCorresponIds(createSearchResultForView(corresponIds));
                            indexNo = corresponIds.size() - 1;
                        } catch (ServiceAbortException e) {
                            // 件数のエラーは許容する
                            if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())
                                && !ApplicationMessageCode.RETURNED_MORE_THAN_RECORDS.equals(
                                        e.getMessageCode())) {
                                ++currentPageNo;
                                throw e;
                            }
                        }
                    }
                }
                // ----------------------------------------------
                // 一覧
                // ----------------------------------------------
                else {
                    SearchCorresponCondition searchCorresponCondition = getCurrentSearchCorresponCondition();
                    if (searchCorresponCondition != null) {
                        SearchCorresponCondition condition = (SearchCorresponCondition)SerializationUtils.clone(searchCorresponCondition);
                        condition.setPageNo(--currentPageNo);
                        try {
                            List<Long> corresponIds = corresponSearchService.searchIdInPage(condition);
                            setCorresponIds(corresponIds);
                            indexNo = corresponIds.size() - 1;
                        } catch (ServiceAbortException e) {
                            // 件数のエラーは許容する
                            if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())
                                && !ApplicationMessageCode.RETURNED_MORE_THAN_RECORDS.equals(
                                        e.getMessageCode())) {
                                ++currentPageNo;
                                throw e;
                            }
                        }
                    }
                }
            }
            // 画面上で制御しているので本来ならここには来ない (念のため現コレポンを再表示)
            return getCorresponIds().get(indexNo);
        }

        /**
         * 直後のコレポン文書IDを取得する.
         * @return Long コレポン文書ID.
         * @throws ServiceAbortException
         */
        public Long getNextId() throws ServiceAbortException {
            // 直後がページ内の場合
            if (indexNo < (getCorresponIds().size() - 1)) {
                return getCorresponIds().get(++indexNo);
            }
            // ページ外で次ページがある場合は再読み込み
            if (currentPageNo * pageRowNum < totalCount) {
                // ----------------------------------------------
                // 全文検索
                // ----------------------------------------------
                if (getBackPage() != null && getBackPage().equals("corresponSearch")) {
                    SearchFullTextSearchCorresponCondition searchFullTextSearchCorresponCondition = getCurrentSearchFullTextSearchCorresponCondition();
                    if (searchFullTextSearchCorresponCondition != null) {
                        SearchFullTextSearchCorresponCondition condition = (SearchFullTextSearchCorresponCondition)SerializationUtils.clone(searchFullTextSearchCorresponCondition);
                        condition.setPageNo(++currentPageNo);
                        try {
                            List<Long> corresponIds = corresponFullTextSearchService.searchId(condition);
                            setCorresponIds(createSearchResultForView(corresponIds));
                            indexNo = 0;
                        } catch (ServiceAbortException e) {
                            // 件数のエラーは許容する
                            if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())
                                && !ApplicationMessageCode.RETURNED_MORE_THAN_RECORDS.equals(
                                        e.getMessageCode())) {
                                --currentPageNo;
                                throw e;
                            }
                        }
                    }
                }
                // ----------------------------------------------
                // 一覧
                // ----------------------------------------------
                else {
                    SearchCorresponCondition searchCorresponCondition = getCurrentSearchCorresponCondition();
                    if (searchCorresponCondition != null) {
                        SearchCorresponCondition condition = (SearchCorresponCondition)SerializationUtils.clone(searchCorresponCondition);
                        condition.setPageNo(++currentPageNo);
                        try {
                            List<Long> corresponIds = corresponSearchService.searchIdInPage(condition);
                            setCorresponIds(corresponIds);
                            indexNo = 0;
                        } catch (ServiceAbortException e) {
                            // 件数のエラーは許容する
                            if (!ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())
                                && !ApplicationMessageCode.RETURNED_MORE_THAN_RECORDS.equals(
                                        e.getMessageCode())) {
                                --currentPageNo;
                                throw e;
                            }
                        }
                    }
                }
            }
            // 画面上で制御しているので本来ならここには来ない (念のため現コレポンを再表示)
            return getCorresponIds().get(indexNo);
        }

        // 全文検索の結果を１ページ表示分に分割する.
        private List<Long> createSearchResultForView(List<Long> searchResult) throws ServiceAbortException {

            // 該当データ0件の場合
            if (searchResult.size() == 0) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }

            List<Long> returnList = new ArrayList<Long>(100);

            int minDataNum = (currentPageNo - 1) * pageRowNum;
            int maxDataNum = currentPageNo * pageRowNum;

            if (searchResult.size() < maxDataNum) {
                maxDataNum = searchResult.size();
            }

            // 表示するページの補正
            // 表示しようとしたページ番号までデータが無い場合、最終ページを表示するようにする
            if (minDataNum > maxDataNum) {
                int totalPageCount = searchResult.size() / pageRowNum;
                if ((searchResult.size() % pageRowNum) > 0) {
                    totalPageCount++;
                }
                minDataNum = (totalPageCount - 1) * pageRowNum;
                currentPageNo = totalPageCount;
            }

            for (int i = minDataNum; i < maxDataNum; i++) {
                returnList.add(searchResult.get(i));
            }
            return returnList;
        }

        /**
         * @return the totalCount
         */
        public int getTotalCount() {
            return totalCount;
        }

        /**
         * @param totalCount the totalCount to set
         */
        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }

        /**
         * @return the currentPageNo
         */
        public int getCurrentPageNo() {
            return currentPageNo;
        }

        /**
         * @param currentPageNo the currentPageNo to set
         */
        public void setCurrentPageNo(int currentPageNo) {
            this.currentPageNo = currentPageNo;
        }

        /**
         * @return the pageRowNum
         */
        public int getPageRowNum() {
            return pageRowNum;
        }

        /**
         * @param pageRowNum the pageRowNum to set
         */
        public void setPageRowNum(int pageRowNum) {
            this.pageRowNum = pageRowNum;
        }

        /**
         * @return the indexNo
         */
        public int getIndexNo() {
            return indexNo;
        }

        /**
         * @param indexNo the indexNo to set
         */
        public void setIndexNo(int indexNo) {
            this.indexNo = indexNo;
        }
    }

    private CorresponPageController corresponPageController;

    /**
     * @return the corresponPageController
     */
    public CorresponPageController getCorresponPageController() {
        return corresponPageController;
    }

    public boolean isLearningContents() {
        if (correspon.getForLearning() == ForLearning.LEARNING) {
            return true;
        } else {
            return false;
        }
    }

    public void copyCorresponForLearning(Correspon correspon) {
        corresponService.copyCorresponForLearning(correspon);
    }
}
