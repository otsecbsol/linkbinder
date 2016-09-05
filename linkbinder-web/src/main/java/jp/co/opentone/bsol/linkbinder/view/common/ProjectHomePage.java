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
package jp.co.opentone.bsol.linkbinder.view.common;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.validation.constraints.Required;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Initialize;
import jp.co.opentone.bsol.framework.web.extension.jsf.annotation.Transfer;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupSummary;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.FavoriteFilter;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectDetailsSummary;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchFavoriteFilterCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.common.FavoriteFilterService;
import jp.co.opentone.bsol.linkbinder.service.common.HomeService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponFullTextSearchService;
import jp.co.opentone.bsol.linkbinder.validation.groups.ValidationGroupBuilder;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.util.ArrayList;
import java.util.List;

/**
 * プロジェクトホーム画面.
 * @author opentone
 */
@ManagedBean
@Scope("view")
public class ProjectHomePage extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2024353815940391207L;

    /**
     * 承認作業中を意味する.
     */
    private static final String UNDER_REVIEW = "UNDER_REVIEW";

    /**
     * ホームサービス.
     */
    @Resource
    private HomeService homeService;

    /**
     * FavoriteFilter サービス.
     */
    @Resource
    private FavoriteFilterService favoriteFilterService;

    /**
     * 全文検索 サービス.
     */
    @Resource
    private CorresponFullTextSearchService fullTextSearchService;

    /**
     * サマリ情報.
     */
    @Transfer
    private ProjectDetailsSummary projectDetailsSummary;

    /**
     * コレポン文書種別と活動単位でのサマリ情報のデータモデル.
     */
    private DataModel<?> groupSummaryDataModel;

    /**
     * Correspondence trackingの宛先（TO）を選択したかどうかの判断材料.
     */
    @Transfer
    private boolean selectedTo;

    /**
     * Correspondence trackingの宛先（CC）を選択したかどうかの判断材料.
     */
    @Transfer
    private boolean selectedCc;

    /**
     * My correspondenceの承認状態を選択したかどうかの判断材料.
     * 選択している場合、WorkflowStatusの値が入る.
     */
    @Transfer
    private String selectedWorkflowStatus;

    /**
     * My correspondenceの承認作業状態を選択したかどうかの判断材料.
     * 選択している場合、WorkflowProcessStatusの値が入る.
     */
    @Transfer
    private String selectedWorkflowProcessStatus;

    /**
     * My correspondenceの文書既読状態を選択したかどうかの判断材料.
     * 選択している場合、ReadStatusの値が入る.
     */
    @Transfer
    private String selectedReadStatus;

    /**
     * My correspondenceの文書状態を選択したかどうかの判断材料.
     * 選択している場合、CorresponStatusの値が入る.
     */
    @Transfer
    private String selectedCorresponStatus;

    /**
     * My correspondenceのAttentionを選択したかどうかの判断材料.
     */
    @Transfer
    private boolean selectedAttention;

    /**
     * My correspondenceのPerson in Chargeを選択したかどうかの判断材料.
     */
    @Transfer
    private boolean selectedPIC;

    /**
     * Favorite Filter のデータモデル.
     */
    @Transfer
    private DataModel<FavoriteFilter> favoriteFilterDataModel;

    /**
     * FavoriteFilter DTO.
     */
    private FavoriteFilter favoriteFilter;

    /**
     * favorite filter の処理対象ID.
     */
    @Transfer
    private Long targetFavoriteFilterId;

    /**
     * favorite filter の更新名称.
     */
    @Transfer
    @Required
    //CHECKSTYLE:OFF
    @Length(max = 100)
    //CHECKSTYLE:ON
    private String targetFavoriteFilterName;

    /**
     * Favorite filter rename ダイアログ表示フラグ.
     * <p>
     * 当該ダイアログ表示時にtrueに設定される
     * </p>
     */
    @Transfer
    private boolean favoriteFilterDisplay;

    /**
     * 空のインスタンスを生成する.
     */
    public ProjectHomePage() {
    }

    /**
     * projectDetailsSummaryを取得します.
     * @return the projectDetailsSummary
     */
    public ProjectDetailsSummary getProjectDetailsSummary() {
        return projectDetailsSummary;
    }

    /**
     * projectDetailsSummaryを設定します.
     * @param projectDetailsSummary the projectDetailsSummary to set
     */
    public void setProjectDetailsSummary(ProjectDetailsSummary projectDetailsSummary) {
        this.projectDetailsSummary = projectDetailsSummary;
    }

    /**
     * groupSummaryDataModelを取得します.
     * @return the groupSummaryDataModel
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public DataModel<?> getGroupSummaryDataModel() {
        if (projectDetailsSummary != null
                && projectDetailsSummary.getCorresponGroupSummary() != null) {
            groupSummaryDataModel
                = new ListDataModel(projectDetailsSummary.getCorresponGroupSummary());
        }
        return groupSummaryDataModel;
    }

    /**
     * groupSummaryDataModelを設定します.
     * @param groupSummaryDataModel the groupSummaryDataModel to set
     */
    public void setGroupSummaryDataModel(DataModel<?> groupSummaryDataModel) {
        this.groupSummaryDataModel = groupSummaryDataModel;
    }

    /**
     * selectedToを取得します.
     * @return the selectedTo
     */
    public boolean isSelectedTo() {
        return selectedTo;
    }

    /**
     * selectedToを設定します.
     * @param selectedTo the selectedTo to set
     */
    public void setSelectedTo(boolean selectedTo) {
        this.selectedTo = selectedTo;
    }

    /**
     * selectedCcを取得します.
     * @return the selectedCc
     */
    public boolean isSelectedCc() {
        return selectedCc;
    }

    /**
     * selectedCcを設定します.
     * @param selectedCc the selectedCc to set
     */
    public void setSelectedCc(boolean selectedCc) {
        this.selectedCc = selectedCc;
    }

    /**
     * selectedWorkflowStatusを取得します.
     * @return the selectedWorkflowStatus
     */
    public String getSelectedWorkflowStatus() {
        return selectedWorkflowStatus;
    }

    /**
     * selectedWorkflowStatusを設定します.
     * @param selectedWorkflowStatus the selectedWorkflowStatus to set
     */
    public void setSelectedWorkflowStatus(String selectedWorkflowStatus) {
        this.selectedWorkflowStatus = selectedWorkflowStatus;
    }

    /**
     * selectedWorkflowProcessStatusを取得します.
     * @return the selectedWorkflowProcessStatus
     */
    public String getSelectedWorkflowProcessStatus() {
        return selectedWorkflowProcessStatus;
    }

    /**
     * selectedWorkflowProcessStatusを設定します.
     * @param selectedWorkflowProcessStatus the selectedWorkflowProcessStatus to set
     */
    public void setSelectedWorkflowProcessStatus(String selectedWorkflowProcessStatus) {
        this.selectedWorkflowProcessStatus = selectedWorkflowProcessStatus;
    }

    /**
     * selectedReadStatusを取得します.
     * @return the selectedReadStatus
     */
    public String getSelectedReadStatus() {
        return selectedReadStatus;
    }

    /**
     * selectedReadStatusを設定します.
     * @param selectedReadStatus the selectedReadStatus to set
     */
    public void setSelectedReadStatus(String selectedReadStatus) {
        this.selectedReadStatus = selectedReadStatus;
    }

    /**
     * selectedCorresponStatusを取得します.
     * @return the selectedCorresponStatus
     */
    public String getSelectedCorresponStatus() {
        return selectedCorresponStatus;
    }

    /**
     * selectedCorresponStatusを設定します.
     * @param selectedCorresponStatus the selectedCorresponStatus to set
     */
    public void setSelectedCorresponStatus(String selectedCorresponStatus) {
        this.selectedCorresponStatus = selectedCorresponStatus;
    }

    /**
     * selectedAttentionを取得します.
     * @return the selectedAttention
     */
    public boolean isSelectedAttention() {
        return selectedAttention;
    }

    /**
     * selectedAttentionを設定します.
     * @param selectedAttention the selectedAttention to set
     */
    public void setSelectedAttention(boolean selectedAttention) {
        this.selectedAttention = selectedAttention;
    }

    /**
     * selectedPICを取得します.
     * @return the selectedPIC
     */
    public boolean isSelectedPIC() {
        return selectedPIC;
    }

    /**
     * selectedPICを設定します.
     * @param selectedPIC the selectedPIC to set
     */
    public void setSelectedPIC(boolean selectedPIC) {
        this.selectedPIC = selectedPIC;
    }

    /**
     * 承認状態：作成中を取得する.
     * @return 承認状態：作成中
     */
    public String getDraft() {
        return WorkflowStatus.DRAFT.name();
    }

    /**
     * 承認作業状態：検証依頼中を取得する.
     * @return 承認作業状態：検証依頼中
     */
    public String getRequestForCheck() {
        return WorkflowProcessStatus.REQUEST_FOR_CHECK.name();
    }

    /**
     * 承認作業状態：承認依頼中を取得する.
     * @return 承認作業状態：承認依頼中
     */
    public String getRequestForApproval() {
        return WorkflowProcessStatus.REQUEST_FOR_APPROVAL.name();
    }

    /**
     * 承認作業状態:検証依頼中、承認依頼中、コレポン文書を更新を意味する
     * 値を取得する.
     * @return 承認作業状態:検証依頼中、承認依頼中、コレポン文書を更新を意味する値
     */
    public String getUnderReview() {
        return UNDER_REVIEW;
    }

    /**
     * 承認状態：否認済を取得する.
     * @return 承認状態：否認済
     */
    public String getDenied() {
        return WorkflowStatus.DENIED.name();
    }

    /**
     * 承認状態：発行済を取得する.
     * @return 承認状態：発行済
     */
    public String getIssued() {
        return WorkflowStatus.ISSUED.name();
    }

    /**
     * コレポン文書状態：Openを取得する.
     * @return コレポン文書状態：Open
     */
    public String getOpen() {
        return CorresponStatus.OPEN.name();
    }

    /**
     * コレポン文書状態：Closedを取得する.
     * @return コレポン文書状態：Closed
     */
    public String getClosed() {
        return CorresponStatus.CLOSED.name();
    }

    /**
     * コレポン文書状態：Canceledを取得する.
     * @return コレポン文書状態：Canceled
     */
    public String getCanceled() {
        return CorresponStatus.CANCELED.name();
    }

    /**
     * 既読・未読ステータス：未読を取得する.
     * @return 既読・未読ステータス：未読
     */
    public String getUnread() {
        return ReadStatus.NEW.name();
    }

    /**
     * 既読・未読ステータス：既読を取得する.
     * @return 既読・未読ステータス：既読
     */
    public String getRead() {
        return ReadStatus.READ.name();
    }

    /**
     * favoriteFilterDataModelを取得します.
     * @return the favoriteFilterDataModel
     */
    public DataModel<FavoriteFilter> getFavoriteFilterDataModel() {
        return favoriteFilterDataModel;
    }

    /**
     * favoriteFilterDataModelを設定します.
     * @param favoriteFilterDataModel the favoriteFilterDataModel to set
     */
    public void setFavoriteFilterDataModel(DataModel<FavoriteFilter> favoriteFilterDataModel) {
        this.favoriteFilterDataModel = favoriteFilterDataModel;
    }

    /**
     * favoriteFilter を取得します.
     * @return the favoriteFilter dto.
     */
    public FavoriteFilter getFavoriteFilter() {
        return favoriteFilter;
    }

    /**
     * favoriteFilter を設定します.
     * @param favoriteFilter the favoriteFilter dto.
     */
    public void setFavoriteFilter(FavoriteFilter favoriteFilter) {
        this.favoriteFilter = favoriteFilter;
    }
    /**
     * favoriteFilter の Id を取得します.
     * @return the favoriteFilter id.
     */
    public Long getTargetFavoriteFilterId() {
        return targetFavoriteFilterId;
    }

    /**
     * favoriteFilter の Id を設定します.
     * @param id the target favoriteFilter id.
     */
    public void setTargetFavoriteFilterId(Long id) {
        this.targetFavoriteFilterId = id;
    }
    /**
     * favoriteFilter の (renamed) Name を取得します.
     * @return the favoriteFilter name.
     */
    public String getTargetFavoriteFilterName() {
        return targetFavoriteFilterName;
    }
    /**
     * favoriteFilter の (renamed) Name を設定します.
     * @param name the target favoriteFilter name.
     */
    public void setTargetFavoriteFilterName(String name) {
        this.targetFavoriteFilterName = name;
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


    /**
     * 画面を初期化する.
     * <p>
     * 他画面から遷移した時に起動される. ページの初期化に必要な情報を準備する.
     * </p>
     */
    @Initialize
    public void initialize() {
        if (handler.handleAction(new InitializeAction(this))) {
            loadFavoriteFilter();
        }
        // ダイアログ表示初期化
        favoriteFilterDisplay = false;
    }

    private String getCorresponIndexUrl() {
        return toUrl("correspon/corresponIndex");
    }

    /**
     * コレポン文書種別を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String getGoCorresponIndexTracking() {
        setTrackingCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    public String goCorresponIndexTracking() {
        return this.getGoCorresponIndexTracking();
    }

    /**
     * コレポン文書種別を選択せずにコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexTrackingAll() {
        setTrackingAllCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 学習用コンテンツを指定してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexLearningContents() {
        setLearningCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 承認状態を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexWorkflowStatus() {
        setWorkflowStatusCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 承認作業状態を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexWorkflowProcessStatus() {
        setWorkflowProcessStatusCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 既読・未読状態を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexReadStatus() {
        setReadStatusCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 返信要条件を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexAction() {
        setActionCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * 宛先（CC）を選択してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexCc() {
        setCcCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * ユーザー関連の情報を選択せずにコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexRelatedUserAll() {
        setCurrentSearchCorresponCondition(createCorresponSearchCondition());
        return getCorresponIndexUrl();
    }

    /**
     * Favorite Filter を適用してコレポン文書一覧に遷移する.
     * @return コレポン文書一覧画面
     */
    public String goCorresponIndexByFavoriteFilter() {
        FavoriteFilter filter = getFavoriteFilterFromId(this.targetFavoriteFilterId);
        return toUrl(String.format("correspon/corresponIndex?favoriteFilterId=%s", filter.getId()));
    }

    /**
     * FavoriteFilterダイアログ画面の表示.
     * @return null.
     */
    public String showFavoriteFilterDialog() {
        favoriteFilterDisplay = true;
        return null;
    }
    /**
     * FavoriteFilterダイアログ画面のクローズ.
     * @return null.
     */
    public String closeFavoriteFilterDialog() {
        favoriteFilterDisplay = false;
        return null;
    }

    /**
     * 選択したコレポン文書を登録する.
     * @return null
     */
    public String updateFavoriteFilter() {
        if (handler.handleAction(new RenameAction(this))) {
            loadFavoriteFilter();
            favoriteFilterDisplay = false;
        }
        return null;
    }

    /**
     * 選択したコレポン文書を削除する.
     * @return null
     */
    public String deleteFavoriteFilter() {
        if (handler.handleAction(new DeleteAction(this))) {
            loadFavoriteFilter();
            favoriteFilterDisplay = false;
        }
        return null;
    }

    /**
     * Favorite Filter を取得し表示する.
     */
    private void loadFavoriteFilter() {
        handler.handleAction(new GetFavoriteFilterAction(this));
    }

    /**
     * 共通の条件を設定したコレポン文書検索条件を作成する.
     * @return コレポン文書検索条件
     */
    private SearchCorresponCondition createCorresponSearchCondition() {
        SearchCorresponCondition condition = new SearchCorresponCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setUserId(getCurrentUser().getUserId());
        condition.setSystemAdmin(isSystemAdmin());
        condition.setProjectAdmin(isProjectAdmin(getCurrentProjectId()));

        return condition;
    }

    /**
     * Correspondence trackingの検索条件を設定する.
     */
    private void setTrackingCondition(SearchCorresponCondition condition) {
        // 暗黙の条件として、発行状態であること.
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.ISSUED};
        condition.setWorkflowStatuses(workflowStatuses);

        CorresponType[] corresponTypes
          = {((CorresponGroupSummary) groupSummaryDataModel.getRowData()).getCorresponType()};
        condition.setCorresponTypes(corresponTypes);

        condition.setToGroups(projectDetailsSummary.getUserRelatedCorresponGroups());
        condition.setGroupTo(selectedTo);
        condition.setGroupCc(selectedCc);

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * Correspondence trackingの検索条件を設定する.
     */
    private void setTrackingAllCondition(SearchCorresponCondition condition) {
        // 暗黙の条件として、発行状態であること.
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.ISSUED};
        condition.setWorkflowStatuses(workflowStatuses);

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * 学習用文書の検索条件を設定する.
     */
    private void setLearningCondition(SearchCorresponCondition condition) {
        condition.setForLearnings(new ForLearning[]{ ForLearning.LEARNING });
        setCurrentSearchCorresponCondition(condition);
    }
    /**
     * My correspondence の承認状態の検索条件を設定する.
     */
    private void setWorkflowStatusCondition(SearchCorresponCondition condition) {
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.valueOf(selectedWorkflowStatus)};
        condition.setWorkflowStatuses(workflowStatuses);
        // 自分が作成した文書であること.
        User[] fromUsers = {getCurrentUser()};
        condition.setFromUsers(fromUsers);
        condition.setUserPreparer(true);

        if (selectedCorresponStatus != null) {
            CorresponStatus[] corresponStatuses
                = {CorresponStatus.valueOf(selectedCorresponStatus)};
            condition.setCorresponStatuses(corresponStatuses);
        }

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * My correspondence の自分宛の検索条件を設定する.
     */
    private void setReadStatusCondition(SearchCorresponCondition condition) {
        if (selectedReadStatus != null) {
            ReadStatus[] readStatuses = {ReadStatus.valueOf(selectedReadStatus)};
            condition.setReadStatuses(readStatuses);
        }

        // 暗黙の条件として、発行状態であること.
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.ISSUED};
        condition.setWorkflowStatuses(workflowStatuses);

        // 自分宛(Attention/Cc/Person In Charge)の文書であること.
        User[] toUsers = {getCurrentUser()};
        condition.setToUsers(toUsers);
        condition.setUserCc(true);
        condition.setUserAttention(true);
        condition.setUserPic(isUsePersonInCharge());
        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * My correspondence の承認作業状態の検索条件を設定する.
     */
    private void setWorkflowProcessStatusCondition(SearchCorresponCondition condition) {
        if (UNDER_REVIEW.equals(selectedWorkflowProcessStatus)) {
            condition.setWorkflowStatuses(WorkflowStatus.getRequesting());
            condition.setUserId(getCurrentUser().getUserId());
            // 自分が作成した文書であること.
            User[] fromUsers = {getCurrentUser()};
            condition.setFromUsers(fromUsers);
            condition.setUserPreparer(true);
        } else {
            ArrayList<WorkflowProcessStatus> workflowProcessStatuseList
                    = new ArrayList<WorkflowProcessStatus>();

            workflowProcessStatuseList.add(
                    WorkflowProcessStatus.valueOf(selectedWorkflowProcessStatus));

            //  検証・承認依頼中のコレポン文書のみ
            if (workflowProcessStatuseList.size() > 0
                && (workflowProcessStatuseList.get(0) == WorkflowProcessStatus.REQUEST_FOR_CHECK
                    || workflowProcessStatuseList.get(0)
                        == WorkflowProcessStatus.REQUEST_FOR_APPROVAL)) {
                condition.setWorkflowStatuses(WorkflowStatus.getRequesting());

                // Under Considerationを追加
                workflowProcessStatuseList.add(WorkflowProcessStatus.UNDER_CONSIDERATION);
            }

            WorkflowProcessStatus[] workflowProcessStatuses
                    = new WorkflowProcessStatus[workflowProcessStatuseList.size()];
            workflowProcessStatuseList.toArray(workflowProcessStatuses);
            condition.setWorkflowProcessStatuses(workflowProcessStatuses);

            User[] toUsers = {getCurrentUser()};
            condition.setFromUsers(toUsers);
        }

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * My correspondence の返信要の検索条件を設定する.
     */
    private void setActionCondition(SearchCorresponCondition condition) {
        // Open文書
        CorresponStatus[] corresponStatuses = {CorresponStatus.OPEN};
        condition.setCorresponStatuses(corresponStatuses);

        //  発行済
        condition.setWorkflowStatuses(new WorkflowStatus[] {WorkflowStatus.ISSUED});

        User[] toUsers = {getCurrentUser()};
        condition.setToUsers(toUsers);
        condition.setUserUnreplied(true);
        condition.setUserAttention(selectedAttention);
        condition.setUserPic(selectedPIC);

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     * My correspondence の宛先（CC）の検索条件を設定する.
     */
    private void setCcCondition(SearchCorresponCondition condition) {
        // Open文書
        CorresponStatus[] corresponStatuses = {CorresponStatus.OPEN};
        condition.setCorresponStatuses(corresponStatuses);

        //  発行済
        condition.setWorkflowStatuses(new WorkflowStatus[] {WorkflowStatus.ISSUED});

        User[] toUsers = {getCurrentUser()};
        condition.setToUsers(toUsers);
        condition.setUserCc(true);

        setCurrentSearchCorresponCondition(condition);
    }

    /**
     *
     * @return validation group.
     */
    public String getValidationGroups() {
        if (isActionInvoked("form:nameInputOkLink")) {
            return new ValidationGroupBuilder().defaultGroup().toString();
        } else {
            return new ValidationGroupBuilder().skipValidationGroup().toString();
        }
    }
    /**
    *
    * @return validation group.
    */
   public String getSkipValidationGroups() {
       return new ValidationGroupBuilder().skipValidationGroup().toString();
   }

    /**
     * 指定した FavoriteFilter を取得する.
     * @param id FavoriteFilter を特定するID
     * @return Favorite Filter
     */
    FavoriteFilter getFavoriteFilterFromId(Long id) {
        if (favoriteFilterDataModel == null
                || favoriteFilterDataModel.getRowCount() <= 0) {
            return null;
        }
        for (FavoriteFilter item : favoriteFilterDataModel) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 画面初期化アクション.
     * @author opentone
     */
    static class InitializeAction extends AbstractAction {

        /**
         * SerialVersionUID.
         */
        private static final long serialVersionUID = 7937379217842873948L;

        /** アクション発生元ページ. */
        private ProjectHomePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page
         *            ページ
         */
        public InitializeAction(ProjectHomePage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            this.page.setFavoriteFilterDisplay(false);
            String projectId = page.getCurrentProjectId();
            if (StringUtils.isEmpty(projectId)) {
                throw new ServiceAbortException(
                    "Project ID is not specified.", MessageCode.E_INVALID_PARAMETER);
            }

            page.projectDetailsSummary
                = page.homeService.findProjectDetails(projectId, page.isUsePersonInCharge());
        }
    }

    /**
     * Favorite Filter 取得アクション.
     * @author opentone
     */
    static class GetFavoriteFilterAction extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 5089917157155657157L;
        /**
         * 呼出し元ページ.
         */
        private ProjectHomePage page;

        /**
         * インスタンスを生成する .
         * @param page 呼出し元のページ
         */
        public GetFavoriteFilterAction(ProjectHomePage page) {
            super(page);
            this.page = page;
        }

        /* (non-Javadoc)
         * @see Action#execute()
         */
        @Override
        public void execute() throws ServiceAbortException {
            Project project = page.getCurrentProject();
            User user = page.getCurrentUser();
            SearchFavoriteFilterCondition condition
                = new SearchFavoriteFilterCondition(project, user);
            List<FavoriteFilter> favoriteFilterList = page.favoriteFilterService.search(condition);
            page.favoriteFilterDataModel = new ListDataModel<FavoriteFilter>(favoriteFilterList);
        }
    }

    /**
     * リネームアクション.
     * @author opentone
     */
    static class RenameAction extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 4620559469086069902L;
        /**
         * Serial Id.
         */

        /** アクション発生元ページ. */
        private ProjectHomePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public RenameAction(ProjectHomePage page) {
            super(page);
            this.page = page;
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // prepare the target
            Long id = page.targetFavoriteFilterId;
            this.page.favoriteFilter = new FavoriteFilter();
            this.page.favoriteFilter.setId(id);
            this.page.favoriteFilter.setVersionNo(page.getFavoriteFilterFromId(id).getVersionNo());
            this.page.favoriteFilter.setFavoriteName(page.targetFavoriteFilterName);
            // execute delete process
            page.favoriteFilterService.save(page.favoriteFilter);
            page.setPageMessage(ApplicationMessageCode.FAVORITE_FILTER_SAVED);
        }

    }
    /**
     * 削除アクション.
     * @author opentone
     */
    static class DeleteAction extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -6701449639312707278L;

        /** アクション発生元ページ. */
        private ProjectHomePage page;

        /**
         * このアクションの発生元ページを指定してインスタンス化する.
         * @param page 発生元ページ
         */
        public DeleteAction(ProjectHomePage page) {
            super(page);
            this.page = page;
            this.page.setFavoriteFilterDisplay(false);
        }

        /*
         * (non-Javadoc)
         * @see jp.co.opentone.bsol.framework.action.Action#execute()
         */
        public void execute() throws ServiceAbortException {
            // prepare the target
            Long id = page.targetFavoriteFilterId;
            this.page.favoriteFilter = new FavoriteFilter();
            this.page.favoriteFilter.setId(id);
            this.page.favoriteFilter.setVersionNo(page.getFavoriteFilterFromId(id).getVersionNo());
            // execute delete process
            page.favoriteFilterService.delete(page.favoriteFilter);
            page.setPageMessage(ApplicationMessageCode.FAVORITE_FILTER_DELETED);
        }

    }

}
