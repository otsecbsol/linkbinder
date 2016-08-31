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
package jp.co.opentone.bsol.linkbinder.dto.condition;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.LearningLabel;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.code.FullTextSearchMode;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import org.apache.commons.lang.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * テーブル [v_correspon] の検索条件を表すDto.
 *
 * @author opentone
 *
 */
public class SearchCorresponCondition extends AbstractCondition {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4159955924231307239L;

    /**
     * 権限：部門管理者を取得するKEY.
     */
    private static final String KEY_GROUP_ADMIN = "securityLevel.groupAdmin";

    /**
     * ソートの昇順／降順の指定.
     */
    private static final Map<String, String> ASCENDING_VALUE;
    static {
        Map<String, String> map = new HashMap<String, String>();
        map.put("true", "ASC");
        map.put("false", "DESC NULLS LAST");
        ASCENDING_VALUE = Collections.unmodifiableMap(map);
    }

    /**
     * 検索条件：シンプルな検索フラグ.
     */
    private boolean simpleSearch;

    /**
     * No.
     */
    private Long sequenceNo;

    /**
     * IDの配列.
     * 全文検索とのマージで使用する。
     */
    private List<Ids> idList;

    /**
     * ID（From).
     */
    private Long fromId;

    /**
     * ID（To）.
     */
    private Long toId;

    /**
     * コレポン文書番号.
     */
    private String corresponNo;

    /**
     * 改訂後のコレポン文書も対象とする.
     */
    private boolean includingRevision;

    /**
     * コレポン文書種別.
     */
    private CorresponType[] corresponTypes;

    /**
     * 学習用ラベル.
     */
    private LearningLabel[] learningLabels;

    /**
     * 承認状態.
     */
    private WorkflowStatus[] workflowStatuses;

    /**
     * 未読／既読状態.
     */
    private ReadStatus[] readStatuses;

    /**
     * 学習用文書であるか否か.
     */
    private ForLearning[] forLearnings;

    /**
     * 文書状態.
     */
    private CorresponStatus[] corresponStatuses;

    /**
     * 検索キーワード.
     */
    private String keyword;

    /**
     * 検索対象.
     */
    private FullTextSearchMode fullTextSearchMode = null;

    /**
     * 差出グループ一覧.
     */
    private CorresponGroup[] fromGroups;

    /**
     * 宛先グループ一覧.
     */
    private CorresponGroup[] toGroups;

    /**
     * 差出ユーザー一覧.
     */
    private User[] fromUsers;

    /**
     * 差出ユーザー(Preparer).
     */
    private boolean userPreparer;

    /**
     * 差出ユーザー(Checker).
     */
    private boolean userChecker;

    /**
     * 差出ユーザー(Approver).
     */
    private boolean userApprover;

    /**
     * 差出ユーザーWorkflow Process Status.
     */
    private WorkflowProcessStatus[] workflowProcessStatuses;

    /**
     * 宛先ユーザー一覧.
     */
    private User[] toUsers;

    /**
     * 宛先ユーザー一覧(活動単位ごと).
     */
    private CorresponGroupUser[] toGroupUsers;

    /**
     * 宛先ユーザー-アドレスタイプ-Attention.
     */
    private boolean userAttention;

    /**
     * 宛先ユーザー-アドレスタイプ-Cc.
     */
    private boolean userCc;

    /**
     * 宛先ユーザー-アドレスタイプ-PIC(Person in Charge).
     */
    private boolean userPic;

    /**
     * 宛先ユーザー（未返信）.
     */
    private boolean userUnreplied;

    /**
     * 活動単位の宛先（To）.
     */
    private boolean groupTo;

    /**
     * 活動単位の宛先（Cc）.
     */
    private boolean groupCc;

    /**
     * 活動単位の宛先（未返信）.
     */
    private boolean groupUnreplied;

    /**
     * 作成日（From）.
     */
    private Date fromCreatedOn;

    /**
     * 作成日（To）.
     */
    private Date toCreatedOn;

    /**
     * 更新日（From）.
     */
    private Date fromIssuedOn;

    /**
     * 更新日（To）.
     */
    private Date toIssuedOn;

    /**
     * 返信期限（From）.
     */
    private Date fromDeadlineForReply;

    /**
     * 返信期限（To）.
     */
    private Date toDeadlineForReply;

    /**
     * カスタムフィールドNo.
     */
    private Long customFieldNo;

    /**
     * カスタムフィールド値.
     */
    private String customFieldValue;

    /**
     * プロジェクトID.
     */
    private String projectId;

    /**
     * ユーザID.
     */
    private String userId;

    /**
     * SystemAdminフラグ.
     */
    private boolean systemAdmin;

    /**
     * SystemAdminフラグ.
     */
    private boolean projectAdmin;

    /**
     * ソートカラム.
     */
    private String sort;

    /**
     * ソートの昇順／降順.
     */
    private boolean ascending;

    /**
     * 空のインスタンスを生成する.
     */
    public SearchCorresponCondition() {
    }

    /**
     * simpleSearchを取得します.
     * @return the simpleSearch
     */
    public boolean isSimpleSearch() {
        return simpleSearch;
    }

    /**
     * simpleSearchを設定します.
     * @param simpleSearch the simpleSearch to set
     */
    public void setSimpleSearch(boolean simpleSearch) {
        this.simpleSearch = simpleSearch;
    }

    /**
     * @param sequenceNo the sequenceNo to set
     */
    public void setSequenceNo(Long sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    /**
     * @return the sequenceNo
     */
    public Long getSequenceNo() {
        return sequenceNo;
    }

    /**
     * @param idList the idList to set
     */
    public void setIdList(List<Ids> idList) {
        this.idList = idList;
    }

    /**
     * IDのリストクラス.
     * SQLマッピングで使用。
     *
     * @author opentone
     *
     */
    public static class Ids {
        /**
         * ids.
         */
        private List<Long> ids;

        /**
         * コンストラクタ.
         * <br/>
         * JSON文字列から復元するために、デフォルトコンストラクタが必要です.
         */
        public Ids() {
        }

        /**
         * コンストラクタ.
         * @param ids IDの配列
         */
        public Ids(List<Long> ids) {
            this.ids = ids;
        }
        /**
         * @return ids
         */
        public List<Long> getIds() {
            return this.ids;
        }
        /**
         * @param ids IDの配列
         */
        public void setIds(List<Long> ids) {
            this.ids = ids;
        }
    }

    /**
     * @return the idList
     */
    public List<Ids> getIdList() {
        return idList;
    }

    /**
     * fromIdを取得します.
     * @return the fromId
     */
    public Long getFromId() {
        return fromId;
    }

    /**
     * fromIdを設定します.
     * @param fromId the fromId to set
     */
    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    /**
     * toIdを取得します.
     * @return the toId
     */
    public Long getToId() {
        return toId;
    }

    /**
     * toIdを設定します.
     * @param toId the toId to set
     */
    public void setToId(Long toId) {
        this.toId = toId;
    }

    /**
     * corresponNoを取得します.
     * @return the corresponNo
     */
    public String getCorresponNo() {
        return corresponNo;
    }

    /**
     * corresponNoを設定します.
     * @param corresponNo the corresponNo to set
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
     * corresponTypesを取得します.
     * @return the corresponTypes
     */
    public CorresponType[] getCorresponTypes() {
        return CloneUtil.cloneArray(CorresponType.class, corresponTypes);
    }

    /**
     * corresponTypesを設定します.
     * @param corresponTypes the corresponTypes to set
     */
    public void setCorresponTypes(CorresponType[] corresponTypes) {
        this.corresponTypes = CloneUtil.cloneArray(CorresponType.class, corresponTypes);
    }

    /**
     * learningLabelsを取得します.
     * @return the learningLabels
     */
    public LearningLabel[] getLearningLabels() {
        return CloneUtil.cloneArray(LearningLabel.class, learningLabels);
    }

    /**
     * learingLabelsを設定します.
     * @param learningLabels the learningLabels to set
     */
    public void setLearningLabels(LearningLabel[] learningLabels) {
        this.learningLabels = CloneUtil.cloneArray(LearningLabel.class, learningLabels);
    }

    /**
     * workflowStatusesを取得します.
     * @return the workflowStatuses
     */
    public WorkflowStatus[] getWorkflowStatuses() {
        return CloneUtil.cloneArray(WorkflowStatus.class, workflowStatuses);
    }

    /**
     * workflowStatusesを設定します.
     * @param workflowStatuses the workflowStatuses to set
     */
    public void setWorkflowStatuses(WorkflowStatus[] workflowStatuses) {
        this.workflowStatuses = CloneUtil.cloneArray(WorkflowStatus.class, workflowStatuses);
    }

    /**
     * readStatusesを取得します.
     * @return the readStatuses
     */
    public ReadStatus[] getReadStatuses() {
        return CloneUtil.cloneArray(ReadStatus.class, readStatuses);
    }

    /**
     * readStatusesを設定します.
     * @param readStatuses the readStatuses to set
     */
    public void setReadStatuses(ReadStatus[] readStatuses) {
        this.readStatuses = CloneUtil.cloneArray(ReadStatus.class, readStatuses);
    }

    /**
     * forLearningsを取得します.
     * @return the forLearnings
     */
    public ForLearning[] getForLearnings() {
        return CloneUtil.cloneArray(ForLearning.class, forLearnings);
    }

    /**
     * forLearningssを設定します.
     * @param forLearnings the forLearnings to set
     */
    public void setForLearnings(ForLearning[] forLearnings) {
        this.forLearnings = CloneUtil.cloneArray(ForLearning.class, forLearnings);
    }

    /**
     * corresponStatusesを取得します.
     * @return the corresponStatuses
     */
    public CorresponStatus[] getCorresponStatuses() {
        return CloneUtil.cloneArray(CorresponStatus.class, corresponStatuses);
    }

    /**
     * corresponStatusesを設定します.
     * @param corresponStatuses the corresponStatuses to set
     */
    public void setCorresponStatuses(CorresponStatus[] corresponStatuses) {
        this.corresponStatuses = CloneUtil.cloneArray(CorresponStatus.class, corresponStatuses);
    }

    /**
     * workflowProcessStatusesを取得します.
     * @return the workflowProcessStatuses
     */
    public WorkflowProcessStatus[] getWorkflowProcessStatuses() {
        return CloneUtil.cloneArray(WorkflowProcessStatus.class, workflowProcessStatuses);
    }

    /**
     * workflowProcessStatusesを設定します.
     * @param workflowProcessStatuses the workflowProcessStatuses to set
     */
    public void setWorkflowProcessStatuses(WorkflowProcessStatus[] workflowProcessStatuses) {
        this.workflowProcessStatuses =
            CloneUtil.cloneArray(WorkflowProcessStatus.class, workflowProcessStatuses);
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
     * fromCreatedOnを取得します.
     * @return the fromCreatedOn
     */
    public Date getFromCreatedOn() {
        return CloneUtil.cloneDate(fromCreatedOn);
    }

    /**
     * fromCreatedOnを設定します.
     * @param fromCreatedOn the fromCreatedOn to set
     */
    public void setFromCreatedOn(Date fromCreatedOn) {
        if (fromCreatedOn != null) {
            this.fromCreatedOn = DateUtils.truncate(fromCreatedOn, Calendar.DATE);
        } else {
            this.fromCreatedOn = null;
        }
    }

    /**
     * toCreatedOnを取得します.
     * @return the toCreatedOn
     */
    public Date getToCreatedOn() {
        return CloneUtil.cloneDate(toCreatedOn);
    }

    /**
     * toCreatedOnを設定します.
     * @param toCreatedOn the toCreatedOn to set
     */
    public void setToCreatedOn(Date toCreatedOn) {
        if (toCreatedOn != null) {
            this.toCreatedOn = DateUtils.truncate(toCreatedOn, Calendar.DATE);
        } else {
            this.toCreatedOn = null;
        }
    }

    /**
     * fromIssuedOnを取得します.
     * @return the fromIssuedOn
     */
    public Date getFromIssuedOn() {
        return CloneUtil.cloneDate(fromIssuedOn);
    }

    /**
     * fromIssuedOnを設定します.
     * @param fromIssuedOn the fromIssuedOn to set
     */
    public void setFromIssuedOn(Date fromIssuedOn) {
        if (fromIssuedOn != null) {
            this.fromIssuedOn = DateUtils.truncate(fromIssuedOn, Calendar.DATE);
        } else {
            this.fromIssuedOn = null;
        }
    }

    /**
     * toIssuedOnを取得します.
     * @return the toIssuedOn
     */
    public Date getToIssuedOn() {
        return CloneUtil.cloneDate(toIssuedOn);
    }

    /**
     * toIssuedOnを設定します.
     * @param toIssuedOn the toIssuedOn to set
     */
    public void setToIssuedOn(Date toIssuedOn) {
        if (toIssuedOn != null) {
            this.toIssuedOn = DateUtils.truncate(toIssuedOn, Calendar.DATE);
        } else {
            this.toIssuedOn = null;
        }
    }

    /**
     * fromDeadlineForReplyを取得します.
     * @return the fromDeadlineForReply
     */
    public Date getFromDeadlineForReply() {
        return CloneUtil.cloneDate(fromDeadlineForReply);
    }

    /**
     * fromDeadlineForReplyを設定します.
     * @param fromDeadlineForReply the fromDeadlineForReply to set
     */
    public void setFromDeadlineForReply(Date fromDeadlineForReply) {
        if (fromDeadlineForReply != null) {
            this.fromDeadlineForReply = DateUtils.truncate(fromDeadlineForReply, Calendar.DATE);
        } else {
            this.fromDeadlineForReply = null;
        }
    }

    /**
     * toDeadlineForReplyを取得します.
     * @return the toDeadlineForReply
     */
    public Date getToDeadlineForReply() {
        return CloneUtil.cloneDate(toDeadlineForReply);
    }

    /**
     * toDeadlineForReplyを設定します.
     * @param toDeadlineForReply the toDeadlineForReply to set
     */
    public void setToDeadlineForReply(Date toDeadlineForReply) {
        if (toDeadlineForReply != null) {
            this.toDeadlineForReply = DateUtils.truncate(toDeadlineForReply, Calendar.DATE);
        } else {
            this.toDeadlineForReply = null;
        }
    }

    /**
     * customFieldNoを取得します.
     * @return the customFieldNo
     */
    public Long getCustomFieldNo() {
        return customFieldNo;
    }

    /**
     * customFieldNoを設定します.
     * @param customFieldNo the customFieldNo to set
     */
    public void setCustomFieldNo(Long customFieldNo) {
        this.customFieldNo = customFieldNo;
    }

    /**
     * customFieldValueを取得します.
     * @return the customFieldValue
     */
    public String getCustomFieldValue() {
        return customFieldValue;
    }

    /**
     * customFieldValueを設定します.
     * @param customFieldValue the customFieldValue to set
     */
    public void setCustomFieldValue(String customFieldValue) {
        this.customFieldValue = customFieldValue;
    }

    /**
     * projectIdを取得します.
     * @return the projectId
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * projectIdを設定します.
     * @param projectId the projectId to set
     */
    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
     * systemAdminを取得します.
     * @return the systemAdmin
     */
    public boolean isSystemAdmin() {
        return systemAdmin;
    }

    /**
     * systemAdminを設定します.
     * @param systemAdmin the systemAdmin to set
     */
    public void setSystemAdmin(boolean systemAdmin) {
        this.systemAdmin = systemAdmin;
    }

    /**
     * projectAdminを取得します.
     * @return the projectAdmin
     */
    public boolean isProjectAdmin() {
        return projectAdmin;
    }

    /**
     * projectAdminを設定します.
     * @param projectAdmin the projectAdmin to set
     */
    public void setProjectAdmin(boolean projectAdmin) {
        this.projectAdmin = projectAdmin;
    }

    /**
     * sortを取得します.
     * @return the sort
     */
    public String getSort() {
        return sort;
    }

    /**
     * sortを設定します.
     * @param sort the sort to set
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * ascendingを取得します.
     * @return the ascending
     */
    public boolean isAscending() {
        return ascending;
    }

    /**
     * ascendingを設定します.
     * @param ascending the ascending to set
     */
    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    /**
     * WorkflowStatusのDRAFTを返却する.
     * @return DRAFT
     */
    public WorkflowStatus getDraft() {
        return WorkflowStatus.DRAFT;
    }

    /**
     * WorkflowStatusのissuedを返却する.
     * @return ISSUED
     */
    public WorkflowStatus getIssued() {
        return WorkflowStatus.ISSUED;
    }

    /**
     * WorkflowProccessStatusのnoneを返却する.
     * @return NONE
     */
    public WorkflowProcessStatus getNone() {
        return WorkflowProcessStatus.NONE;
    }

    /**
     * WorkflowTypeのapproverを返却する.
     * @return APPROVER
     */
    public WorkflowType getApprover() {
        return WorkflowType.APPROVER;
    }

    /**
     * AllowApproverToBrowseのvisibleを返却する.
     * @return VISIBLE
     */
    public AllowApproverToBrowse getVisible() {
        return AllowApproverToBrowse.VISIBLE;
    }

    /**
     * SecurityLevelのgroupAdminを返却する.
     * @return groupAdmin
     */
    public String getGroupAdmin() {
        return SystemConfig.getValue(KEY_GROUP_ADMIN);
    }

    /**
     * AddressTypeのtoを返却する.
     * @return TO
     */
    public AddressType getTo() {
        return AddressType.TO;
    }

    /**
     * AddressTypeのCcを返却する.
     * @return CC
     */
    public AddressType getCc() {
        return AddressType.CC;
    }

    /**
     * AddressUserTypeのAttentionを返却する.
     * @return ATTENTION
     */
    public AddressUserType getAttention() {
        return AddressUserType.ATTENTION;
    }

    /**
     * AddressUserTypeのNormalUserを返却する.
     * @return NORMAL_USER
     */
    public AddressUserType getNormalUser() {
        return AddressUserType.NORMAL_USER;
    }

    /**
     * ReplyRequiredのYESを返却する.
     * @return YES
     */
    public ReplyRequired getYes() {
        return ReplyRequired.YES;
    }

    /**
     * 未読／既読ステータスが設定なしの条件を追加するかどうか判定する.
     * @return true:条件を追加する
     */
    public boolean isReadStatusNull() {
        boolean isNull = false;
        if (readStatuses != null
                && (readStatuses.length == ReadStatus.values().length
                    || (readStatuses.length == 1
                        && ReadStatus.NEW.equals(readStatuses[0])))) {
            isNull = true;
        }
        return isNull;
    }

    /**
     * ワークフローの検索を行うかどうか判定する.
     * @return true:検索を行う
     */
    public boolean isUseWorkflow() {
        if (workflowProcessStatuses != null && workflowProcessStatuses.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * SQL用に入力日付+1したものを返却する.
     * @return toCreatedOn + 1
     */
    public Date getToCreatedOnEnd() {
        if (toCreatedOn == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(toCreatedOn);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    /**
     * SQL用に入力日付+1したものを返却する.
     * @return toIssuedOn + 1
     */
    public Date getToIssuedOnEnd() {
        if (toIssuedOn == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(toIssuedOn);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    /**
     * SQL用に入力日付+1したものを返却する.
     * @return toDeadlineForReply + 1
     */
    public Date getToDeadlineForReplyEnd() {
        if (toDeadlineForReply == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDeadlineForReply);
        cal.add(Calendar.DATE, 1);
        return cal.getTime();
    }

    /**
     * ORDER BY句を生成する.
     * @return ORDER BY句
     */
    public String getOrderBy() {
        return SQLConvertUtil.encode(getSort() + " "
                + ASCENDING_VALUE.get(String.valueOf(isAscending())));
    }

    /**
     * @param fromGroups the groupsByFrom to set
     */
    public void setFromGroups(CorresponGroup[] fromGroups) {
        this.fromGroups = CloneUtil.cloneArray(CorresponGroup.class, fromGroups);
    }

    /**
     * @return the fromGroups
     */
    public CorresponGroup[] getFromGroups() {
        return CloneUtil.cloneArray(CorresponGroup.class, fromGroups);
    }

    /**
     * @param toGroups the groupsByToCc to set
     */
    public void setToGroups(CorresponGroup[] toGroups) {
        this.toGroups = CloneUtil.cloneArray(CorresponGroup.class, toGroups);
    }

    /**
     * @return the toGroups
     */
    public CorresponGroup[] getToGroups() {
        return CloneUtil.cloneArray(CorresponGroup.class, toGroups);
    }

    /**
     * @param userPreparer the roleOfWorkflowPreparer to set
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
     * @param userChecker the roleOfWorkflowChecker to set
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
     * @param userApprover the roleOfWorkflowApprover to set
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
     * @param fromUsers the usersByFrom to set
     */
    public void setFromUsers(User[] fromUsers) {
        this.fromUsers = CloneUtil.cloneArray(User.class, fromUsers);
    }

    /**
     * @return the fromUser
     */
    public User[] getFromUsers() {
        return CloneUtil.cloneArray(User.class, fromUsers);
    }

    /**
     * @param toUsers the usersByToCc to set
     */
    public void setToUsers(User[] toUsers) {
        this.toUsers = CloneUtil.cloneArray(User.class, toUsers);
    }

    /**
     * @return the toUsers
     */
    public User[] getToUsers() {
        return CloneUtil.cloneArray(User.class, toUsers);
    }

    /**
     * @return the toGroupUsers
     */
    public CorresponGroupUser[] getToGroupUsers() {
        return CloneUtil.cloneArray(CorresponGroupUser.class, toGroupUsers);
    }

    /**
     * @param userAttention the addressTypeAttentionByUser to set
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
     * @param userCc the addressTypeCcByUser to set
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
     * @param userPic the addressTypePicByUser to set
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
     * @param userUnreplied the unreplineByUser to set
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
     * @param fullTextSearchMode the fullTextSearchMode to set
     */
    public void setFullTextSearchMode(FullTextSearchMode fullTextSearchMode) {
        this.fullTextSearchMode = fullTextSearchMode;
    }

    /**
     * @return the fullTextSearchMode
     */
    public FullTextSearchMode getFullTextSearchMode() {
        return fullTextSearchMode;
    }

    /**
     * RoleOfWorkflowのCheckerまたはApproverが指定されているかを取得.
     * @return 条件と等しいならtrue
     */
    public boolean isUseRoleOfWorkflow() {
        return (userChecker || userApprover);
    }

    /**
     * workflowTypesを取得します.
     * @return the workflowTypes
     */
    public WorkflowType[] getWorkflowTypes() {
        List<WorkflowType> workflowTypeList = new ArrayList<WorkflowType>();
        if (userChecker) {
            workflowTypeList.add(WorkflowType.CHECKER);
        }
        if (userApprover) {
            workflowTypeList.add(WorkflowType.APPROVER);
        }

        if (workflowTypeList.size() == 0) {
            return null;
        } else {
            WorkflowType[] wt = new WorkflowType[workflowTypeList.size()];
            workflowTypeList.toArray(wt);
            return wt;
        }
    }

    /**
     * From:Groupの検索条件の利用有無を取得する.
     * 条件:
     * 1.Groupの何れかが選択されていること
     *
     * @return 有効であるならtrue
     */
    public boolean isUseFromGroups() {
        return (fromGroups != null && fromGroups.length > 0);
    }

    /**
     * To/Cc:Groupの検索条件の利用有無を取得する.
     * 条件:
     * 1.Groupの何れかが選択されていること
     * 2.AddressTypeの何れかが選択されていること(Unrepliedは除く)
     *
     * @return 条件と等しいならtrue
     */
    public boolean isUseToGroups() {
        return (toGroups != null && toGroups.length > 0
                && (groupTo || groupCc));
    }

    /**
     * From:Userの検索条件の利用有無を取得する.
     * 条件:
     * 1.Userの何れかが選択されていること
     * 2.RoleofWorkflowの何れかが選択されていること
     *
     * @return 条件と等しいならtrue
     */
    public boolean isUseFromUsers() {
        return (fromUsers != null && fromUsers.length > 0
                && (userPreparer || userChecker || userApprover));
    }

    /**
     * To/Cc:Userの検索条件の利用有無を取得する.
     * 条件:
     * 1.Userの何れかが選択されていること
     * 2.AddressTypeの何れかが選択されていること(Unrepliedは除く)
     *
     * @return 条件と等しいならtrue
     */
    public boolean isUseToUsers() {
        return (toUsers != null && toUsers.length > 0
                && (userAttention || userCc || userPic));
    }
}
