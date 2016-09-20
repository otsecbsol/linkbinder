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
package jp.co.opentone.bsol.linkbinder.view.correspon.module;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.web.view.action.ServiceActionHandler;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUserMapping;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.IssueToLearningProjectsResult;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowIndex;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.event.CorresponDeleted;
import jp.co.opentone.bsol.linkbinder.event.CorresponIssued;
import jp.co.opentone.bsol.linkbinder.event.CorresponUpdated;
import jp.co.opentone.bsol.linkbinder.event.CorresponWorkflowStatusChanged;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.CorresponGroupService;
import jp.co.opentone.bsol.linkbinder.service.admin.UserService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponPage.CorresponPageController;
import jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentDownloadAction;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.AddressModel;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.AddressUserModel;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponDataSource;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * コレポン文書表示画面通常表示用モジュールクラス.
 * @author opentone
 */
@Component
@Scope("request")
public class DefaultModule implements Serializable {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1425691804555043361L;

    /** Logger. */
    private static Logger log = LoggerFactory.getLogger(DefaultModule.class);

    /**
     * コレポンページ.
     */
    //CHECKSTYLE:OFF
    protected CorresponPage page;
    //CHECKSTYLE:ON

    /**
     * ページの各アクションを起動するオブジェクト.
     * <p>
     * Service層を呼び出す全てのアクションはこのオブジェクト経由で起動すること!
     * </p>
     */
    //CHECKSTYLE:OFF
    protected ServiceActionHandler serviceActionHandler;
    //CHECKSTYLE:ON

    /**
     * viewの共通処理を集めたオブジェクト.
     */
    //CHECKSTYLE:OFF
    protected ViewHelper viewHelper;
    //CHECKSTYLE:ON

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * コレポン文書既読・未読サービス.
     */
    @Resource
    private CorresponReadStatusService corresponReadStatusService;

    /**
     * 活動単位サービス.
     */
    @Resource
    private CorresponGroupService corresponGroupService;

    /**
     * ユーザーサービス.
     */
    @Resource
    private UserService userService;

    /**
     * アクションが発生した宛先-グループ.
     */
    private AddressModel detectAddressModel;

    /**
     * アクションが発生した宛先-ユーザー.
     */
    private AddressUserModel detectAddressUser;

    /**
     * 選択されたPerson in Chargeのリスト.
     */
    private List<PersonInCharge> selectedPersonInCharges;

    /**
     *
     */
    private boolean skipInitCorresponIds;

    /**
     * 空のインスタンスを生成する.
     */
    public DefaultModule() {
    }

    public void setCorresponPage(CorresponPage corresponPage) {
        this.page = corresponPage;
    }

    public CorresponPage getCorresponPage() {
        return this.page;
    }

    public void setServiceActionHandler(ServiceActionHandler serviceActionHandler) {
        this.serviceActionHandler = serviceActionHandler;
    }

    public ServiceActionHandler getServiceActionHandler() {
        return this.serviceActionHandler;
    }

    public void setViewHelper(ViewHelper viewHelper) {
        this.viewHelper = viewHelper;
    }

    public ViewHelper getViewHelper() {
        return this.viewHelper;
    }

    public void setDetectAddressModel(AddressModel detectAddressModel) {
        this.detectAddressModel = detectAddressModel;
    }

    public AddressModel getDetectAddressModel() {
        return this.detectAddressModel;
    }

    public void setDetectAddressUser(AddressUserModel detectAddressUser) {
        this.detectAddressUser = detectAddressUser;
    }

    public AddressUserModel getDetectAddressUser() {
        return this.detectAddressUser;
    }

    public void setSelectedPersonInCharges(List<PersonInCharge> selectedPersonInCharges) {
        this.selectedPersonInCharges = selectedPersonInCharges;
    }

    public List<PersonInCharge> getSelectedPersonInCharges() {
        return this.selectedPersonInCharges;
    }

    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. クエリパラメータで指定されたIDに対応する、コレポン文書を表示する.
     * </p>
     * @return 実行結果
     */
    public String initialize() {
        if (page.getFromEdit() != null) {
            skipInitCorresponIds = true;
        }
        if (serviceActionHandler.handleAction(new InitializeAction(page, this))) {
            skipInitCorresponIds = false;
            if (serviceActionHandler.handleAction(new ResponseHistoryAction(page, this))) {
                page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);
                page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
            }
        }
        return null;
    }

    /**
     * 別コレポンを再読み込みする.
     * <p>
     * 返信先、改訂元、応答履歴のコレポン文書表示時に使用する.
     * </p>
     * @return 実行結果
     */
    public String changeCorrespon() {
        // コレポンを切り替える処理では、必ずコレポンIDリストの取得をスキップする
        skipInitCorresponIds = true;
        serviceActionHandler.handleAction(new InitializeAction(page, this));
        clearResponseHistory();
        skipInitCorresponIds = false;
        return null;
    }

    /**
     * 返信状況表示をトグルする.
     * @return null
     */
    public String replyStatus() {
        serviceActionHandler.handleAction(new ReplyStatusAction(page, this));
        // 前後移動、応答履歴でCcがセットされていなコレポンに移動することを考慮
        page.setTargetAddressType(AddressType.TO.getValue());
        return null;
    }

    /**
     * Attentionに対する担当者を任命する.
     * @return null
     */
    public String assignTo() {
        serviceActionHandler.handleAction(new AssignToAction(page, this));
        page.setDetectedAddressUserId(null);
        // 前後移動、応答履歴でCcがセットされていなコレポンに移動することを考慮
        page.setTargetAddressType(AddressType.TO.getValue());
        return null;
    }

    /**
     * 指定された添付ファイルをダウンロードする.
     * @return null
     */
    public String download() {
        serviceActionHandler.handleAction(new AttachmentDownloadAction(page));
        return null;
    }

    /**
     * HTML出力（印刷）を行う.
     * @return null
     */
    public String print() {
        serviceActionHandler.handleAction(new HtmlPrintAction(page, this));
        return null;
    }

    /**
     * 発行を行う.
     * @return 遷移先
     */
    public String issue() {
        if (!serviceActionHandler.handleAction(new IssueAction(page, this))) {
            return null;
        }
        String backPage = page.getBackPage();
        return String.format("%s?afterAction=true&sessionSort=1&sessionPageNo=1",
            StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
    }

    /**
     * 学習用プロジェクトへ公開する.
     * @return 遷移先
     */
    public String issueToLearningProjects() {
        serviceActionHandler.handleAction(new IssueToLearningProjectsAction(page, this));
        return null;
    }

    /**
     * コレポン文書を検証依頼状態にする.
     *
     * @return null
     */
    public String requestForApproval() {
        serviceActionHandler.handleAction(new RequestForApprovalAction(page, this));
        return null;
    }

    /**
     * 既読状態を更新する(Read).
     *
     * @return null
     */
    public String read() {
        serviceActionHandler.handleAction(new UpdateReadAction(page, ReadStatus.READ, this));
        return null;
    }

    /**
     * 未読状態を更新する(Unread).
     *
     * @return null
     */
    public String unread() {
        serviceActionHandler.handleAction(new UpdateReadAction(page, ReadStatus.NEW, this));
        return null;
    }

    /**
     * 文書状態を更新する(Open).
     *
     * @return null
     */
    public String open() {
        serviceActionHandler.handleAction(new UpdateStatusAction(page, CorresponStatus.OPEN, this));
        return null;
    }

    /**
     * 文書状態を更新する(Closed).
     *
     * @return null
     */
    public String close() {
        serviceActionHandler.handleAction(
            new UpdateStatusAction(page, CorresponStatus.CLOSED, this));
        return null;
    }

    /**
     * 文書状態を更新する(Canceled).
     *
     * @return null
     */
    public String cancel() {
        serviceActionHandler.handleAction(
            new UpdateStatusAction(page, CorresponStatus.CANCELED, this));
        return null;
    }

    /**
     * 返信済文書を取得する.
     * @return null
     */
    public String showReplied() {
        serviceActionHandler.handleAction(new FindRepliedAction(page, this));
        page.transferCorresponDisplayInfo();
        return null;
    }

    /**
     * 削除を行う.
     *
     * @return 実行結果
     */
    public String delete() {
        if (serviceActionHandler.handleAction(new DeleteAction(page, this))) {
            String backPage = page.getBackPage();
            return String.format("%s?afterAction=true&sessionSort=1&sessionPageNo=1",
                StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
        }
        return null;
    }

    /**
     * 入力可能な項目に限り、部分的な更新を行う.
     * @return null
     */
    public String save() {
        serviceActionHandler.handleAction(new SavePartialAction(page, this));
        return null;
    }

    /**
     * 応答履歴の表示を行う.
     * @return 実行結果
     */
    public String showResponseHistory() {
        if (serviceActionHandler.handleAction(new ResponseHistoryAction(page, this))) {
            page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);
            page.setResponseHistoryDetail(CorresponPage.LABEL_HIDE_DETAILS);
        }
        return null;
    }

    /**
     * コレポン文書のHTMLと全ての添付ファイルをZIP形式にしてダウンロードを行う.
     * @return 実行結果
     */
    public String downloadZip() {
        serviceActionHandler.handleAction(new ZipDownloadAction(page, this));
        return null;
    }

    /**
     * コレポン文書を改訂する.
     *
     * @return 遷移先
     */
    public String revise() {
        page.transferBackPage();
        return String.format("corresponEdit?id=%s&%s&newEdit=1",
                        page.getId(),
                        CorresponEditMode.REVISE.toQueryString());
    }

    private String getBaseCopyUrl(CorresponEditMode editMode) {
        return String.format(
                "corresponEdit?id=%s"
                + "&%s&newEdit=1"
                + "&attachment1Transfer=%s"
                + "&attachment2Transfer=%s"
                + "&attachment3Transfer=%s"
                + "&attachment4Transfer=%s"
                + "&attachment5Transfer=%s",
                page.getId(),
                editMode.toQueryString(),
                page.isAttachment1Checked(),
                page.isAttachment2Checked(),
                page.isAttachment3Checked(),
                page.isAttachment4Checked(),
                page.isAttachment5Checked()
        );
    }

    /**
     * コレポン文書を複写登録する.
     *
     * @return String
     */
    public String copy() {
        page.transferBackPage();
        return getBaseCopyUrl(CorresponEditMode.COPY);
    }

    /**
     * コレポン文書を転送登録する.
     *
     * @return String
     */
    public String forward() {
        page.transferBackPage();
        return getBaseCopyUrl(CorresponEditMode.FORWARD);
    }

    /**
     * コレポン文書を返信する.
     *
     * @return 遷移先
     */
    public String reply() {
        page.transferBackPage();
        return getBaseReplyUrl(CorresponEditMode.REPLY);
    }

    /**
     * 返信済文書をコピーしてコレポン文書を返信する.
     *
     * @return 遷移先
     */
    public String replyWithPreviousCorrespon() {
        page.transferBackPage();
        return String.format("%s&repliedId=%s",
                        getBaseReplyUrl(CorresponEditMode.REPLY_WITH_PREVIOUS_CORRESPON),
                        page.getDetectedRepliedId());
    }

    private String getBaseReplyUrl(CorresponEditMode editMode) {
        return String.format("corresponEdit?id=%s&%s&newEdit=1",
                        page.getId(),
                        editMode.toQueryString());
    }

    /**
     * コレポン文書を更新する.
     *
     * @return 遷移先
     */
    public String update() {
        page.transferBackPage();
        page.transferCorresponDisplayInfo();
        return String.format("corresponEdit?id=%s&%s",
                        page.getId(),
                        CorresponEditMode.UPDATE.toQueryString());
    }

    /**
     * コレポン文書一覧画面に遷移する.
     * 遷移後の画面では、ソートアイテムとソート順そしてページ番号を初期化しないで表示.
     * @return 遷移先
     */
    public String back() {
        String backPage = page.getBackPage();
        return String.format("%s?sessionSort=1&sessionPageNo=1",
            StringUtils.isNotEmpty(backPage) ? backPage : "corresponIndex");
    }

    /**
     * 1つ前のコレポンページを表示する.
     * @return null
     * @throws ServiceAbortException
     */
    public String movePrevious() throws ServiceAbortException {
        page.transferBackPage();
        page.setId(page.getCorresponPageController().getPreviousId());
        page.setDisplayIndex(page.getCorresponPageController().getLogicalIndexNo());
        return changeCorrespon();
    }

    /**
     * 1つ後のコレポンページを表示する.
     * @return null
     * @throws ServiceAbortException
     */
    public String moveNext() throws ServiceAbortException {
        page.transferBackPage();
        page.setId(page.getCorresponPageController().getNextId());
        page.setDisplayIndex(page.getCorresponPageController().getLogicalIndexNo());
        return changeCorrespon();
    }

    /**
     * responseHistory内に当該コレポン番号が存在していない場合、responseHistoryをクリアする.<br />
     * changeCorresponからコールされる.
     */
    private void clearResponseHistory() {
        List<CorresponResponseHistory> responseHistory = page.getCorresponResponseHistory();
        if (responseHistory != null) {
            for (CorresponResponseHistory history : responseHistory) {
                if (page.getId().equals(history.getCorrespon().getId())) {
                    return;
                }
            }

            // 応答履歴をクリアする
            page.setResponseHistoryDetail(CorresponPage.LABEL_SHOW_DETAILS);
            page.setResponseHistoryDisplay(CorresponPage.STYLE_HIDE);
            page.setCorresponResponseHistory(null);
        }
    }

    /**
     * コレポンID一覧を再取得する処理をスキップするか否かをセットする.
     * 一覧がない場合はこのフラグの値の影響を受けず、必ず取得する.
     * @param skipInitCorresponIds スキップするか否か
     */
    public void setSkipInitCorresponIds(boolean skipInitCorresponIds) {
        this.skipInitCorresponIds = skipInitCorresponIds;
    }

    /**
     * コレポンID一覧を再取得する処理をスキップするか否かを取得する.
     * 一覧がない場合はこのフラグの値の影響を受けず、必ず取得する.
     * @return skipInitCorresponIds
     */
    public boolean isSkipInitCorresponIds() {
        return skipInitCorresponIds;
    }

    /**
     * ダイアログで使用するJSON形式の情報を読み込み.
     * 実際にはフラグをtrueにしておき、非同期で再読み込みをさせる.
     * @return null
     * @throws ServiceAbortException
     */
    public String loadJsonValues() throws ServiceAbortException {
        // Person in Charge編集ダイアログのグループ・ユーザを検索する add s.sasaki
        page.setUsers(findProjectUsers());
        page.setJsonValuesLoaded(true);
        return null;
    }

    // Person in Charge編集ダイアログのグループ・ユーザを検索する
    private List<ProjectUser> findProjectUsers() throws ServiceAbortException {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(page.getCurrentProjectId());
        return userService.search(condition);
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -5472670885530676396L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        public void execute() throws ServiceAbortException {
            //  前回表示情報をクリア
            clear();

            //遷移元に応じたID一覧の取得
            if (page.getCorresponIds() == null || !module.isSkipInitCorresponIds()) {
                List<Long> corresponIds = getCorresponIds();
                int index = corresponIds.indexOf(page.getId());
                if (index < 0) {
                    // 検索対象外になっている場合は先頭に位置するようにする。
                    corresponIds.add(0, page.getId());
                    index = 0;
                }
                page.setCorresponIds(corresponIds);
                CorresponPageController controller = page.getCorresponPageController();
                if (controller != null) {
                    controller.setIndexNo(index);
                    page.setDisplayIndex(controller.getLogicalIndexNo());
                }
            }
            if (page.getCorresponIds() != null) {
                CorresponPageController controller = page.getCorresponPageController();
                if ((0 == controller.getTotalCount()) && (0 < page.getCorresponIds().size())) {
                    controller.setTotalCount(page.getCorresponIds().size());
                    // ページ番号の取得
                    SearchCorresponCondition condition = page.getCurrentSearchCorresponCondition();
                    if (condition != null) {
                        int pageNo = condition.getPageNo();
                        page.getCorresponPageController().setCurrentPageNo(pageNo);
                        // ページ単位行数の取得
                        int pageRowNum = condition.getPageRowNum();
                        page.getCorresponPageController().setPageRowNum(pageRowNum);
                    }
                }
            }
            // このページの起動元からコレポン文書のIDが指定されていなければならない
            if (page.getId() == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            String projectId = page.getCurrentProjectId();
            if (StringUtils.isEmpty(projectId)) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }

            setUpInputValues();
            setUpCorrespon();
            setUpInitialMessage();
        }

        private void clear() {
            page.setCorrespon(null);
            page.getElemControl().clear();
            page.setToAddressModel(null);
            page.setCcAddressModel(null);
            page.setDisplayBody(null);
            page.setAttachment1Checked(false);
            page.setAttachment2Checked(false);
            page.setAttachment3Checked(false);
            page.setAttachment4Checked(false);
            page.setAttachment5Checked(false);
            page.setWorkflowList(null);
            page.setToAddresses(null);
            page.setCcAddresses(null);
        }

        private List<Long> getCorresponIds() throws ServiceAbortException {
            List<Long> corresponIds = null;

            List<Long> list = page.getViewHelper().getSessionValue(Constants.KEY_CORRESPON_IDS);
            if (list != null) {
                corresponIds = new ArrayList<Long>(list);
            }
            // コレポン総件数の取得
            Integer totalCount = page.getViewHelper().getSessionValue(Constants.KEY_CORRESPON_DATA_COUNT);
            CorresponPageController controller = page.getCorresponPageController();
            if (controller != null && totalCount != null) {
                page.getCorresponPageController().setTotalCount(totalCount);
            }
            // ページ番号の取得
            SearchCorresponCondition condition = page.getCurrentSearchCorresponCondition();
            if (condition != null) {
                int pageNo = condition.getPageNo();
                page.getCorresponPageController().setCurrentPageNo(pageNo);
                // ページ単位行数の取得
                int pageRowNum = condition.getPageRowNum();
                page.getCorresponPageController().setPageRowNum(pageRowNum);
            }

            if (corresponIds == null) {
                corresponIds = new ArrayList<Long>();
                corresponIds.add(page.getId());
            }
            return corresponIds;
        }

        private void setUpCorrespon() throws ServiceAbortException {
            // 表示データを取得し、各詳細情報の表示状態の初期値を設定する
            try {
                page.setCorrespon(module.corresponService.find(page.getId()));
            } catch (ServiceAbortException e) {
                if (ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT.equals(
                    e.getMessageCode())) {
                    //  参照不可を表す新たな例外を投げる
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVISIBLE_CORRESPON);
                }
                throw e;
            }

            setUpActionElementCondition();
            setUpDisplayState();
            setUpBody();
            setUpAddress();
            setUpAttachments();
            setUpWorkflow();

            setUpInputPartial();

            updateReadStatus();
        }

        private void setUpDisplayState() throws ServiceAbortException {
            page.setTosDisplay(CorresponPage.STYLE_SHOW);
            page.setBodyDisplay(CorresponPage.STYLE_SHOW);
            page.setAttachmentsDisplay(CorresponPage.STYLE_SHOW);
            page.setCustomFieldsDisplay(CorresponPage.STYLE_SHOW);
            page.setWorkflowDisplay(CorresponPage.STYLE_SHOW);
            page.setCopyAttachmentDisplay(existAttachmentFile());

            if (page.getCorresponResponseHistory() != null) {
                page.setResponseHistoryDisplay(CorresponPage.STYLE_SHOW);
            } else {
                page.setResponseHistoryDisplay(CorresponPage.STYLE_HIDE);
            }
        }

        private void setUpBody() {
            page.setDisplayBody(page.getCorrespon().getBody());
        }

        private void setUpAddress() {
            page.setToAddressModel(AddressModel.createAddressModelList(
                                    page.getCorrespon().getAddressCorresponGroups(),
                                    true,
//                                    page.getUsers(),
                                    page.getElemControl()));
            page.setCcAddressModel(AddressModel.createAddressModelList(
                                    page.getCorrespon().getAddressCorresponGroups(),
                                    false,
//                                    page.getUsers(),
                                    page.getElemControl()));
        }

        private void setUpWorkflow() {
            // 表示用承認フローにPrepererをセットする
            page.setWorkflowList(page.createDisplayWorkflowList(page.getCorrespon()));
        }

        private void setUpAttachments() {
            page.setAttachment1Checked(
                StringUtils.isNotEmpty(page.getCorrespon().getFile1FileId()));
            page.setAttachment2Checked(
                StringUtils.isNotEmpty(page.getCorrespon().getFile2FileId()));
            page.setAttachment3Checked(
                StringUtils.isNotEmpty(page.getCorrespon().getFile3FileId()));
            page.setAttachment4Checked(
                StringUtils.isNotEmpty(page.getCorrespon().getFile4FileId()));
            page.setAttachment5Checked(
                StringUtils.isNotEmpty(page.getCorrespon().getFile5FileId()));
        }

        private void setUpInputValues() throws ServiceAbortException {
            page.setGroups(findProjectCorresponGroups());
// 初期表示時にPerson in Charge編集ダイアログのグループ・ユーザを検索しないようにする modified by s.sasaki
//            page.setUsers(findProjectUsers());
            page.setUsers(new ArrayList<ProjectUser>());
            page.setCorresponGroupUserMappings(findCorresponGroupUserMappings());
        }

        /**
         * 部分的に入力可能となる項目の初期化.
         */
        private void setUpInputPartial() {
            if (page.getCorrespon().getReplyRequired() != null) {
                page.setReplyRequired(page.getCorrespon().getReplyRequired().getValue());
            }
            page.setDeadlineForReply(
                DateUtil.convertDateToString(page.getCorrespon().getDeadlineForReply()));

            setupReplyRequiredList();
        }

        private void setupReplyRequiredList() {
            page.createSelectReplyRequired(ReplyRequired.values());
        }

        private List<CorresponGroup> findProjectCorresponGroups() throws ServiceAbortException {
            SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
            condition.setProjectId(page.getCurrentProjectId());
            List<CorresponGroup> groups =
                module.corresponGroupService.search(condition);

            //  「全て」を追加
            groups.add(0, CorresponDataSource.GROUP_ALL);

            return groups;
        }

        private List<CorresponGroupUserMapping> findCorresponGroupUserMappings()
            throws ServiceAbortException {
            return module.corresponGroupService.findCorresponGroupUserMappings();
        }

        /**
         * 既読・未読処理.
         *
         * @throws ServiceAbortException 既読・未読処理エラー
         */
        private void updateReadStatus() throws ServiceAbortException {
            // 確認画面よりSave後
            if (StringUtils.equals(page.getReadMode(), Constants.READ_STATUS_MODE)) {
                // コレポン文書を更新した為、既読→未読に変更
                // 新規の際は、未処理
                module.corresponReadStatusService.updateReadStatusByCorresponId(
                    page.getCorrespon().getId(), ReadStatus.NEW);
            // 検索画面よりLink後
            } else if (page.getReadMode() == null) {
                // コレポン文書を表示した為、未読→既読に変更
                module.corresponReadStatusService.updateReadStatusById(
                    page.getCorrespon().getId(), ReadStatus.READ);
            }
            page.setReadMode(null);
        }

        /**
         * 添付ファイルの有無.
         *
         * @return true:添付ファイル有 false:添付ファイル無
         * @throws ServiceAbortException 既読・未読処理エラー
         */
        private boolean existAttachmentFile() throws ServiceAbortException {
            // 添付ファイルが有るかどうか
            return ((StringUtils.isNotEmpty(page.getCorrespon().getFile1FileId()))
                    || (StringUtils.isNotEmpty(page.getCorrespon().getFile2FileId()))
                    || (StringUtils.isNotEmpty(page.getCorrespon().getFile3FileId()))
                    || (StringUtils.isNotEmpty(page.getCorrespon().getFile4FileId()))
                    || (StringUtils.isNotEmpty(page.getCorrespon().getFile5FileId())));
        }

        /**
         * 画面遷移構成要素の制御.
         */
        private void setUpActionElementCondition() {
            page.getElemControl().setUp(page);
            // Verifyボタン、Updateボタンを独自処理
            setUpdateVerifyButton(page);
        }

        /**
         * 初期表示メッセージの設定.
         */
        private void setUpInitialMessage() {
        }

        private boolean isCurrentWorkflowUser(Correspon correspon, User user) {
            List<Workflow> workflow = correspon.getWorkflows();
            if (workflow == null) {
                return false;
            }
            for (Workflow wf : workflow) {
                if (wf.getUser().getEmpNo().equals(user.getEmpNo())
                    && wf.getWorkflowProcessStatus() != null) {
                    switch (wf.getWorkflowProcessStatus()) {
                    case REQUEST_FOR_CHECK:
                    case REQUEST_FOR_APPROVAL:
                    case UNDER_CONSIDERATION:
                        return true;
                    default:
                        break;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 返信済文書取得アクション.
     * @author opentone
     */
    static class FindRepliedAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7933369037375540282L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public FindRepliedAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        public void execute() throws ServiceAbortException {
            page.setReplied(null);
            if (page.getDetectedRepliedId() == null) {
                throw new ServiceAbortException("ID is not specified.",
                                                MessageCode.E_INVALID_PARAMETER);
            }
            setUpCorrespon();

            // 応答履歴をデフォルト表示するために取得する
            if (page.getCorresponResponseHistory() == null) {
                module.showResponseHistory();
            }
        }

        private void setUpCorrespon() throws ServiceAbortException {
            // 表示データを取得し、各詳細情報の表示状態の初期値を設定する
            Correspon c = module.corresponService.find(page.getDetectedRepliedId());

            setUpBody(c);
            setUpAddress(c);
            setUpWorkflow(c);
            updateReadStatus(c);

            page.setReplied(c);
            page.setDetectedRepliedId(c.getId());
            page.setDisplayReplied(true);
            page.getElemControl().setUp(page);
            // Verifyボタン、Updateボタンを独自処理
            setUpdateVerifyButton(page);
        }

        private void setUpBody(Correspon c) {
            page.setRepliedDisplayBody(c.getBody());
        }

        private void setUpAddress(Correspon c) {
            page.setRepliedToAddressModel(AddressModel.createAddressModelList(
                        c.getAddressCorresponGroups(),
                        true,
//                        page.getUsers(),
                        page.getElemControl()));
            page.setRepliedCcAddressModel(AddressModel.createAddressModelList(
                        c.getAddressCorresponGroups(),
                        false,
//                        page.getUsers(),
                        page.getElemControl()));
        }

        private void setUpWorkflow(Correspon c) {
            // 表示用承認フローにPrepererをセットする
            page.setRepliedWorkflowList(page.createDisplayWorkflowList(c));
        }

        /**
         * 既読・未読処理.
         *
         * @throws ServiceAbortException 既読・未読処理エラー
         */
        private void updateReadStatus(Correspon c) throws ServiceAbortException {
            // コレポン文書を表示した為、未読→既読に変更
            module.corresponReadStatusService.updateReadStatusById(
                c.getId(), ReadStatus.READ);
        }
    }

    /**
     * コレポン文書表示画面のVerifyボタン、Updateボタンを制御する.
     * CorresponVerificationPage#InitializeAction#executeから一部コピー
     */
    //  行数オーバーの警告を抑制
    //CHECKSTYLE:OFF
    private static void setUpdateVerifyButton(CorresponPage page) {
    //CHECKSTYLE:ON
        log.trace("********** setUpdateVerifyButton **********");
        page.getElemControl().setUpdateLink(false);
        page.getElemControl().setVerificationButton(false);

        String empNo = page.getCurrentUser().getEmpNo();
        WorkflowStatus status = page.getCorrespon().getWorkflowStatus();
        boolean isPreparer = empNo.equals(page.getCorrespon().getCreatedBy().getEmpNo());
        List<Workflow> workflowList = page.getCorrespon().getWorkflows();

        // (SystemAdmin or ProjectAdmin or GroupAdmin)
        // && (Draft or Request for Check or Under Consideration or Request for Approval)
        // ならば isAdmin = true
        boolean isAdmin = false;
        if ((page.isSystemAdmin()
                || page.isProjectAdmin(page.getCurrentProject().getProjectId())
                || page.isGroupAdmin(page.getCorrespon().getFromCorresponGroup().getId()))
            && !(WorkflowStatus.DENIED.equals(status)
                    || WorkflowStatus.ISSUED.equals(status))) {
            isAdmin = true;
        }
        List<WorkflowIndex> workflowIndexList = new ArrayList<WorkflowIndex>();
        for (int i = 0; i < workflowList.size(); i++) {
            WorkflowIndex workflowIndex =
                    new WorkflowIndex(workflowList.get(i), status,
                                      page.getCorrespon().getCorresponType(),
                                      isAdmin, empNo);
            workflowIndexList.add(workflowIndex);
        }

        if (log.isDebugEnabled()) {
            log.debug("empNo[" + empNo + "]");
            log.debug("isSystemAdmin[" + page.isSystemAdmin() + "]");
            log.debug("isProjectAdmin["
                + page.isProjectAdmin(page.getCurrentProject().getProjectId()) + "]");
            log.debug("isGroupAdmin["
                + page.isGroupAdmin(page.getCorrespon().getFromCorresponGroup().getId()) + "]");
            log.debug("status[" + status + "]");
            log.debug("isPreparer[" + isPreparer + "]");
        }

        // Verificationボタン
        for (WorkflowIndex workflowIndex : workflowIndexList) {
            if (log.isDebugEnabled()) {
                log.debug("workflowIndex.isVerification[" + workflowIndex.isVerification() + "]");
                log.debug("workflowIndex.getWorkflow().getUser().getEmpNo["
                    + workflowIndex.getWorkflow().getUser().getEmpNo() + "]");
            }
            if (workflowIndex.isVerification()
                 && (isAdmin || empNo.equals(workflowIndex.getWorkflow().getUser().getEmpNo()))) {
                page.getElemControl().setVerificationButton(true);
                log.trace("******* verification is true *****");
                break;
             }
        }

        // Updateボタン
        if (page.isSystemAdmin()) {
            // SystemAdmin
            if (CorresponStatus.CANCELED != page.getCorrespon().getCorresponStatus()) {
                page.getElemControl().setUpdateLink(true);
                log.trace("******* update is true (systemAdmin) *****");
            }
        } else if (page.isProjectAdmin(page.getCurrentProject().getProjectId())
                    || page.isGroupAdmin(page.getCorrespon().getFromCorresponGroup().getId())) {
            // ProjectAdmin/GropuAdmin
            if (WorkflowStatus.REQUEST_FOR_CHECK.equals(status)
                    || WorkflowStatus.UNDER_CONSIDERATION.equals(status)
                    || WorkflowStatus.REQUEST_FOR_APPROVAL.equals(status)
                    || WorkflowStatus.ISSUED.equals(status)) {

                if (CorresponStatus.CANCELED != page.getCorrespon().getCorresponStatus()) {
                    page.getElemControl().setUpdateLink(true);
                    log.trace("******* update is true (project/group Admin) *****");
                }
            } else if (isPreparer
                        && (WorkflowStatus.DRAFT.equals(status)
                                || WorkflowStatus.DENIED.equals(status))) {
                page.getElemControl().setUpdateLink(true);
                log.trace(
                    "******* update is true (project/group Admin + preparer + draft/denied) *****");
            }
        } else {
            // Preparerはworkflowに設定されていないので特別扱い
            if (isPreparer
                 && (WorkflowStatus.DRAFT.equals(status)
                         || WorkflowStatus.DENIED.equals(status))) {
                page.getElemControl().setUpdateLink(true);
                log.trace("******* update is true (normal, preparer)*****");
            } else {
                for (WorkflowIndex workflowIndex : workflowIndexList) {
                    if (log.isDebugEnabled()) {
                        log.debug("workflowIndex.isUpdate[" + workflowIndex.isUpdate() + "]");
                        log.debug("workflowIndex.getWorkflow().getUser().getEmpNo["
                            + workflowIndex.getWorkflow().getUser().getEmpNo() + "]");
                        log.debug("isPreparer[" + isPreparer + "]");
                        log.debug("isChecker[" + workflowIndex.getWorkflow().isChecker() + "]");
                        log.debug("isApprover[" + workflowIndex.getWorkflow().isApprover() + "]");
                    }
                    if (workflowIndex.isUpdate()
                            && empNo.equals(workflowIndex.getWorkflow().getUser().getEmpNo())) {
                        // Normal UserはUpdateできないので除外。
                        if (isPreparer
                                || workflowIndex.getWorkflow().isChecker()
                                || workflowIndex.getWorkflow().isApprover()) {
                            page.getElemControl().setUpdateLink(true);
                            log.trace("******* update is true (normal, checker/approver)*****");
                        }
                    }
                }
            }
        }
    }

    /**
     * コレポン文書を検証依頼状態にする.
     * @author opentone
     */
    static class RequestForApprovalAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -6306324772448231270L;
        /** 表示画面. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        public RequestForApprovalAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        public void execute() throws ServiceAbortException {
            WorkflowStatus before = page.getCorrespon().getWorkflowStatus();

            // CorresponServiceのRequestForApprovalを呼び出す
            module.corresponService.requestForApproval(page.getCorrespon());
            // 画面の初期化
            module.setSkipInitCorresponIds(true);
            module.initialize();
            page.setPageMessage(ApplicationMessageCode.CORRESPON_REQUESTED);

            WorkflowStatus after = page.getCorrespon().getWorkflowStatus();
            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponWorkflowStatusChanged(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId(),
                                        before,
                                        after));
        }
    }

    /**
     * HTML表示（印刷）アクション.
     * @author opentone
     */
    static class HtmlPrintAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 2518515286959378792L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public HtmlPrintAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                Correspon clone = new Correspon();
                PropertyUtils.copyProperties(clone, page.getCorrespon());
                clone.setWorkflows(page.getWorkflowList());

                // 応答履歴未取得の場合、取得する。
                List<CorresponResponseHistory> histories = page.getCorresponResponseHistory();
                if (histories == null) {
                    histories = module.corresponService.findCorresponResponseHistory(clone);
                    page.setCorresponResponseHistory(histories);
                }

                List<CorresponResponseHistoryModel> cloneHistories =
                    new ArrayList<CorresponResponseHistoryModel>();

                for (CorresponResponseHistory history : histories) {
                    CorresponResponseHistory cloneHistory = new CorresponResponseHistory();
                    PropertyUtils.copyProperties(cloneHistory, history);
                    CorresponResponseHistoryModel model = new CorresponResponseHistoryModel();
                    model.setCorresponResponseHistory(cloneHistory);
                    cloneHistories.add(model);
                }

                byte[] data =
                        module.corresponService.generateHTML(
                                clone, cloneHistories, page.isUsePersonInCharge());
                String charset = SystemConfig.getValue(Constants.KEY_HTML_ENCODING);
                module.viewHelper.requestResponse(data, charset);
            } catch (IOException e) {
                throw new ServiceAbortException("Html Request failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
            } catch (IllegalAccessException e) {
                throw new ServiceAbortException(e.getMessage());
            } catch (InvocationTargetException e) {
                throw new ServiceAbortException(e.getMessage());
            } catch (NoSuchMethodException e) {
                throw new ServiceAbortException(e.getMessage());
            }
        }
    }

    /**
     * Deleteアクション.
     *
     * @author opentone
     */
    static class DeleteAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 6383240384204200219L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public DeleteAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            module.corresponService.delete(page.getCorrespon());
            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_DELETED);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponDeleted(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
        }
    }

    /**
     * Issueアクション.
     *
     * @author opentone
     */
    static class IssueAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -2245769339551640071L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public IssueAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            IssueToLearningProjectsResult result = module.corresponService.issue(page.getCorrespon());
            page.setNextPageMessage(ApplicationMessageCode.CORRESPON_ISSUED);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponIssued(page.getCorrespon().getId(),
                            page.getCorrespon().getProjectId()));

            // 学習用文書
            result.getIssuedCorresponList().forEach(c -> {
                page.getEventBus().raiseEvent(
                        new CorresponIssued(c.getId(), c.getProjectId())
                );
            });
            result.getDeletedCorresponList().forEach(c -> {
                page.getEventBus().raiseEvent(
                        new CorresponDeleted(c.getId(), c.getProjectId())
                );
            });
        }
    }

    /**
     * IssueToLearingProjectsアクション.
     *
     * @author opentone
     */
    static class IssueToLearningProjectsAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -2245769339551640071L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public IssueToLearningProjectsAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            module.corresponService.issueToLearningProjects(page.getCorrespon().getId());
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponIssued(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
        }
    }

    /**
     * 返信状況トグルアクション.
     * @author opentone
     */
    static class ReplyStatusAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -3518888352674090209L;
        /** アクション発生元. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ReplyStatusAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            //  アクションが発生した宛先-グループを特定
            AddressModel group = module.detectAddressModel;
            //  返信状況取得済であれば何もしない
            if (!group.isReplyCorresponEmpty()) {
                return;
            }

            //  この宛先-グループが返信したコレポン文書を取得して
            //  このオブジェクトに設定
            group.setReplyCorrespons(
                module.corresponService.findReplyCorrespons(
                        page.getCorrespon(),
                        group.getCorresponGroup().getId()));
        }
    }

    /**
     * Attentionに対するPerson in Chargeを任命するアクション.
     * @author opentone
     */
    static class AssignToAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4317759442512730162L;
        /** アクション発生元. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public AssignToAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            //  アクションが発生した宛先-ユーザーを特定
            AddressUserModel user = module.detectAddressUser;
            List<PersonInCharge> pics = module.selectedPersonInCharges;

            if (log.isDebugEnabled()) {
                StringBuilder sb = new StringBuilder();
                for (PersonInCharge pic : pics) {
                    sb.append(pic.toString());
                }
                log.debug(sb.toString());
            }
            module.corresponService.assignPersonInCharge(
                    page.getCorrespon(),
                    user.getAddressUser(),
                    pics);

            // 画面を再描画してメッセージを表示
            module.setSkipInitCorresponIds(true);
            module.initialize();
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponUpdated(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
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
        private static final long serialVersionUID = 2550531965531862073L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** 更新する文書状態の値. */
        private CorresponStatus status;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public UpdateStatusAction(CorresponPage page,
            CorresponStatus status, DefaultModule module) {
            super(page);
            this.page = page;
            this.status = status;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 文書状態の更新
            module.corresponService.updateCorresponStatus(page.getCorrespon(), status);
            // 画面の初期化
            module.setSkipInitCorresponIds(true);
            module.initialize();
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponUpdated(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
        }
    }

    /**
     * 既読・未読状態更新アクション.
     * @author opentone
     */
    static class UpdateReadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -4529576363406392254L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** 更新する文書状態の値. */
        private ReadStatus readStatus;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public UpdateReadAction(CorresponPage page,
            ReadStatus readStatus, DefaultModule module) {
            super(page);
            this.page = page;
            this.readStatus = readStatus;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // 既読・未読状態の更新
            module.corresponReadStatusService.updateReadStatusById(
                page.getCorrespon().getId(), readStatus);

            // 画面の初期化
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);
        }
    }

    /**
     * 更新アクション.
     * @author opentone
     */
    static class SavePartialAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -1453555255460686329L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public SavePartialAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            Correspon c = setUpCorrespon();
            // 部分的な値の更新
            module.corresponService.savePartial(c);

            // 画面を再描画してメッセージを表示
            module.setSkipInitCorresponIds(true);
            module.initialize();
            page.setPageMessage(ApplicationMessageCode.SAVE_SUCCESSFUL);

            //イベント発火
            page.getEventBus().raiseEvent(
                    new CorresponUpdated(page.getCorrespon().getId(),
                                        page.getCorrespon().getProjectId()));
        }

        private Correspon setUpCorrespon() {
            Correspon c = page.getCorrespon();
            setReplyRequiredTo(c);
            return c;
        }

        private void setReplyRequiredTo(Correspon c) {
            if (!CorresponDataSource.VALUE_NOT_SELECTED.equals(page.getReplyRequired())) {
                for (ReplyRequired v : ReplyRequired.values()) {
                    if (v.getValue().equals(page.getReplyRequired())) {
                        c.setReplyRequired(v);
                        break;
                    }
                }
            }
            c.setDeadlineForReply(DateUtil.convertStringToDate(page.getDeadlineForReply()));
        }
    }

    /**
     * 応答履歴表示アクション.
     * @author opentone
     */
    static class ResponseHistoryAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = -7847683806525046755L;
        /** アクション発生元. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ResponseHistoryAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /* (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            //  応答履歴を取得してページにセット
            page.setCorresponResponseHistory(
                module.corresponService.findCorresponResponseHistory(page.getCorrespon()));
        }
    }
    /**
     * ZIPダウンロードアクション.
     * @author opentone
     */
    static class ZipDownloadAction extends AbstractAction {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 2869783583694728883L;
        /** アクション発生元ページ. */
        private CorresponPage page;
        /** モジュール. */
        private DefaultModule module;
        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public ZipDownloadAction(CorresponPage page, DefaultModule module) {
            super(page);
            this.page = page;
            this.module = module;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            try {
                String fileName = page.createFileName() + ".zip";

                byte[] data = module.corresponService.generateZip(
                    page.getCorrespon(), page.isUsePersonInCharge());
                module.viewHelper.download(fileName, data);
            } catch (IOException e) {
                throw new ServiceAbortException("Zip Download failed.", e,
                                                MessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }
}
