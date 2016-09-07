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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.SQLConvertUtil;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowTemplateUserDao;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplate;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchWorkflowTemplateUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.UserRoleHelper;
import jp.co.opentone.bsol.linkbinder.service.common.CorresponSequenceService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService;
import jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService;

/**
 * このサービスでは承認フローに関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponWorkflowServiceImpl extends AbstractService implements
    CorresponWorkflowService {
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(CorresponServiceImpl.class);
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -2795456058133877087L;

    /**
     * 指定可能Checkerの最大数取得キー.
     */
    private static final String KEY_MAX_CHECKER_SIZE = "workflow.maxCheckerSize";
    /**
     * 指定可能Approverの最大数取得キー.
     */
    private static final String KEY_MAX_APPROVER_SIZE = "workflow.maxApproverSize";

    /**
     * 設定可能なCheckerの最大数(デフォルト値).
     */
    private static final int DEFAULT_MAX_CHECKER_SIZE = 10;

    /**
     * 設定可能なApproverの最大数(デフォルト値).
     */
    private static final int DEFAULT_MAX_APPROVER_SIZE = 1;

    /**
     * 承認パターン1の取得キー.
     */
    private static final String KEY_PATTERN_1 = "workflow.pattern.1";
    /**
     * 承認パターン2の取得キー.
     */
    private static final String KEY_PATTERN_2 = "workflow.pattern.2";
    /**
     * 承認パターン3の取得キー.
     */
    private static final String KEY_PATTERN_3 = "workflow.pattern.3";

    /**
     * 承認フローサービス.
     */
    @Resource
    private CorresponSequenceService corresponSequenceService;

    /**
     * E-mail通知関連サービス.
     */
    @Resource
    private EmailNoticeService emailNotificationService;

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * Role判定クラス.
     */
    @Resource
    private UserRoleHelper helper;

    /**
     * 指定可能なCheckerの最大数.
     */
    private int maxCheckerSize;
    /**
     * 指定可能なApproverの最大数.
     */
    private int maxApproverSize;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponWorkflowServiceImpl() {
        maxCheckerSize = getPropertyValue(KEY_MAX_CHECKER_SIZE, DEFAULT_MAX_CHECKER_SIZE);
        maxApproverSize = getPropertyValue(KEY_MAX_APPROVER_SIZE, DEFAULT_MAX_APPROVER_SIZE);
    }

    /**
     * システム設定情報から、指定されたキーの値を取得する。取得できない場合はdefaultValueを返す.
     * @param key 取得情報のキー
     * @param defaultValue 取得できなかった場合に返すデフォルト値
     * @return 値
     */
    private int getPropertyValue(String key, int defaultValue) {
        String val = SystemConfig.getValue(key);
        if (StringUtils.isEmpty(val)) {
            return defaultValue;
        }

        int result;
        try {
            result = Integer.parseInt(val);
        } catch (NumberFormatException e) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Checker数のチェック.
     * @param workflow 承認フロー
     * @throws ServiceAbortException Checkの数が正しくない
     */
    private void validateNumverOfChecker(List<Workflow> workflow) throws ServiceAbortException {
        // 指定可能なCheckerの数を超えた場合はエラー（現在MAX10)
        if (isOverMaxChecker(workflow)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_CHECKERS, maxCheckerSize);
        }
    }

    /**
     * Approver数のチェック.
     * @param workflow 承認フロー
     * @throws ServiceAbortException Approverの数が正しくない
     */
    private void validateNumverOfApprover(List<Workflow> workflow) throws ServiceAbortException {
        // 指定可能なApproverの数は1人以上の場合はエラー
        if (isOverMaxApprover(workflow)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.EXCEED_MAXIMUM_NUMBER_OF_APPROVERS, maxApproverSize);
        }
    }

    /**
     * Workflowの指定順チェック.
     * @param workflow 承認フロー
     * @throws ServiceAbortException 指定順が正しくない
     */
    private void validateWorkflowOrder(List<Workflow> workflow) throws ServiceAbortException {
        // 指定順チェック Approver下のCheckerが存在する場合はエラー
        if (isApproverUnderChecker(workflow)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_SEQUENCE_INVALID);
        }
    }

    /**
     * Approverが作業中か判定し、作業中の場合は承認フロー-の差分をチェック.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateApproverUnderConsideration(Correspon correspon, List<Workflow> workflow)
            throws ServiceAbortException {
        // Approverが作業中か判定し、作業中の場合は承認フローの差分を判定する
        if (isApproverUnderConsideration(correspon, workflow)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID);
        }
    }

    /**
     * 現在検証中のChecker/Approverの最初のワークフローNoを返す.
     * <p>
     * 先頭から順に検証を行う、承認フローパターン1にのみ対応
     * </p>
     * @param workflow 承認フロー
     */
    private Long getProcessingWorkflowNo(List<Workflow> workflow) {
        Long workflowNo = null;
        for (Workflow wf : workflow) {
            WorkflowProcessStatus s = wf.getWorkflowProcessStatus();
            if (s == WorkflowProcessStatus.REQUEST_FOR_CHECK
                || s == WorkflowProcessStatus.UNDER_CONSIDERATION
                || s == WorkflowProcessStatus.REQUEST_FOR_APPROVAL) {
                workflowNo = wf.getWorkflowNo();
                break;
            }
        }
        return workflowNo;
    }

    /**
     * Checker/Approverの重複チェックをする.
     * @param workflows ワークフロー
     * @throws ServiceAbortException 重複している
     */
    private void checkDuplicatedUser(List<Workflow> workflows) throws ServiceAbortException {
        // Listに対象情報セット
        HashMap<String, String> workflowHash = new HashMap<String, String>();
        for (Workflow w : workflows) {
            if (!(workflowHash.size() == 0)) {
                if (workflowHash.containsKey(w.getUser().getEmpNo())) {
                    if (WorkflowType.CHECKER.equals(w.getWorkflowType())) {
                        throw new ServiceAbortException(
                            ApplicationMessageCode.DUPLICATED_CHECKER, (Object) w.getUser()
                                .getEmpNo());
                    } else {
                        throw new ServiceAbortException(
                            ApplicationMessageCode.DUPLICATED_CHECKER_APPROVER, (Object) w
                                .getUser().getEmpNo());
                    }
                }
            }
            workflowHash.put(w.getUser().getEmpNo(), String.valueOf(w.getWorkflowType()));
        }
    }

    /**
     * ワークフローデータを更新する.
     * @param correspon コレポン文書
     * @param workflows ワークフロー
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflow(Correspon correspon, List<Workflow> workflows,
            List<Workflow> workflowDb) throws ServiceAbortException {
        //  コレポン文書を更新する(排他制御のため)
        //  正常に更新できれば以降の処理に続く
        updateCorrespon(correspon);

        Long corresponId = correspon.getId();
        // 承認フローパターン１で自身がCheckerの時は、自身以降のChecker/Approverのデータを更新する。
        if (isWorkflowPattern1(correspon)
            && helper.isWorkflowChecker(workflows, getCurrentUser())
            && !workflowDb.isEmpty()) {

            Long selfWorkflowNo = getProcessingWorkflowNo(workflows);
            //  Draftの時は全削除/全作成
            if (correspon.getWorkflowStatus() == WorkflowStatus.DRAFT) {
                selfWorkflowNo = 0L;
            }

            // レコードを全て削除する
            deleteByCorresponIdWorkflowNo(corresponId, selfWorkflowNo);
            for (Workflow wf : workflows) {
                // 承認フロー登録処理
                if (selfWorkflowNo.compareTo(wf.getWorkflowNo()) < 0) {
                    insertWorkflow(wf);
                }
            }
        } else if (isKeyPattern2AndWorkflowProcessStatus(correspon, workflows, workflowDb)) {
            // Approverを取得
            Workflow approver = workflows.get(workflows.size() - 1);
            // Approverのレコードのみ削除する
            deleteByCorresponIdWorkflowNo(corresponId, approver.getWorkflowNo() - 1);
            insertWorkflow(approver);
        } else {
            // レコードを全て削除する
            deleteByCorresponId(corresponId);
            for (Workflow wf : workflows) {
                // 承認フロー登録処理
                insertWorkflow(wf);
            }
        }
    }

    /**
     * 承認フローパターンが2で承認ステータスがRequest_for_CheckまたはUnder_Considerationか判定する.
     * @param correspon コレポン文書
     * @param workflows 承認フロー
     * @param workflowDb DBから取得した承認フロー
     * @return 承認パターンが2かつ承認ステータスがRequest_for_CheckまたはUnder_Considerationの場合true
     *         上記以外false
     */
    private boolean isKeyPattern2AndWorkflowProcessStatus(
            Correspon correspon, List<Workflow> workflows, List<Workflow> workflowDb) {
        return isWorkflowPattern2(correspon)
            && !workflowDb.isEmpty()
            && (correspon.getWorkflowStatus() == WorkflowStatus.REQUEST_FOR_CHECK
                || correspon.getWorkflowStatus() == WorkflowStatus.UNDER_CONSIDERATION);
    }

    /**
     * コレポン文書IDと承認フローナンバーを指定して承認フローを削除する.
     * @param corresponId コレポン文書ID
     * @param workflowNo 承認フローナンバー
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteByCorresponIdWorkflowNo(Long corresponId, Long workflowNo)
            throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            return dao.deleteByCorresponIdWorkflowNo(corresponId, workflowNo, getCurrentUser());
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * コレポン文書IDを指定して承認フローを削除する.
     * @param corresponId コレポン文書ID
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteByCorresponId(Long corresponId) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            return dao.deleteByCorresponId(corresponId, getCurrentUser());
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 承認フローを登録する.
     * @param workflow 承認フロー
     * @return 登録した承認フローID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertWorkflow(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            return dao.create(workflow);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#save(
     *      jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public void save(Correspon correspon, List<Workflow> workflows) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(workflows);
        validateProjectId(correspon.getProjectId());
        // 比較元ワークフロー取得
        List<Workflow> workflowDb = findByCorresponId(correspon.getId());
        // 承認状態共通チェック1
        validateWorkflowCommonToCheckFirst(correspon, workflows);
        // 承認状態共通チェック2
        validateWorkflowCommonToCheckSecond(correspon, workflows);
        // Checker/Approver数のチェック
        validateNumverOfChecker(workflows);
        validateNumverOfApprover(workflows);
        // Workflowの指定順チェック
        validateWorkflowOrder(workflows);
        // 承認フローパターン1時のチェック
        validateSavePattern1Or2(correspon, workflows, workflowDb);
        // Approverが作業中かチェック
        validateApproverUnderConsideration(correspon, workflows);
        // 重複チェック
        checkDuplicatedUser(workflows);
        // ワークフローを更新する.
        updateWorkflow(correspon, workflows, workflowDb);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#check(
     * jp.co.opentone.bsol.linkbinder.dto.Correspon, jp.co.opentone.bsol.linkbinder.dto.Workflow)
     */
    public void check(Correspon correspon, Workflow workflow) throws ServiceAbortException {
        // 引数のnullチェック
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(workflow);
        // プロジェクトのチェック
        validateProjectId(correspon.getProjectId());
        // 権限チェック
        validateForCheck(correspon, workflow);
        // Checker/Approverの重複チェック
        checkDuplicatedUser(correspon.getWorkflows());
        // 承認状態の更新
        updateCorresponForCheck(correspon, workflow);
        // 承認フローの適用パターンに準じて承認作業状態を更新
        updateWorkflowForCheck(correspon, workflow);
        // メール通知を行う
        sendWorkflowNotice(correspon, EmailNoticeEventCd.CHECKED);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#approve(
     * jp.co.opentone.bsol.linkbinder.dto.Correspon, jp.co.opentone.bsol.linkbinder.dto.Workflow)
     */
    public void approve(Correspon correspon, Workflow workflow) throws ServiceAbortException {
        // 引数のnullチェック
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(workflow);
        // プロジェクトのチェック
        validateProjectId(correspon.getProjectId());
        // 権限チェック
        validateForApprove(correspon, workflow);
        // Checker/Approverの重複チェック
        checkDuplicatedUser(correspon.getWorkflows());
        // 承認状態の更新
        updateCorresponForApprove(correspon);
        // 承認作業状態を更新
        updateWorkflowForApprove(workflow);
        // 学習用プロジェクトへ文書をコピーする
        copyLearningCorrespon(correspon);
        // メール通知を行う for approve
        sendWorkflowNotice(correspon, EmailNoticeEventCd.APPROVED);
        // メール通知を行う for issue
        sendIssuedNotice(correspon);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#approve(
     * jp.co.opentone.bsol.linkbinder.dto.Correspon, jp.co.opentone.bsol.linkbinder.dto.Workflow)
     */
    public void deny(Correspon correspon, Workflow workflow) throws ServiceAbortException {
        // 引数のnullチェック
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(workflow);
        // プロジェクトのチェック
        validateProjectId(correspon.getProjectId());
        // 権限チェック
        validateForDeny(correspon, workflow);
        // Checker/Approverの重複チェック
        checkDuplicatedUser(correspon.getWorkflows());
        // コレポン文書を否認する
        updateCorresponForDeny(correspon);
        // 承認フローを否認する
        updateWorkflowForDeny(workflow);
        // メール通知を行う
        sendWorkflowNotice(correspon, EmailNoticeEventCd.DENIED);
    }

    /*
     * (non-Javadoc)
     * @seejp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#
     * searchWorkflowTemplateUser()
     */
    @Transactional(readOnly = true)
    public List<WorkflowTemplateUser> searchWorkflowTemplateUser() {
        // 承認フローテンプレートの一覧を取得する
        return findWorkflowTemplateUser();
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#apply(java
     * .lang.Long)
     */
    @Transactional(readOnly = true)
    public List<WorkflowTemplate> apply(Long workflowTemplateUserId) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(workflowTemplateUserId);
        // 指定のテンプレートが存在しない場合はエラー
        findByTemplateUserId(workflowTemplateUserId);
        return findWorkflowTemplateByWorkflowTemplateUserId(workflowTemplateUserId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#deleteTemplate
     * (java.lang.Long)
     */
    public void deleteTemplate(Long workflowTemplateUserId) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(workflowTemplateUserId);
        // 指定のテンプレートが存在しない場合はエラー
        WorkflowTemplateUser workflowTemplateUser = findByTemplateUserId(workflowTemplateUserId);

        // 承認フローテンプレートを削除する
        deleteWorkflowTemplateByWorkflowTemplateUserId(
            createDeleteWorkflowTemplate(workflowTemplateUserId));
        // 承認フローテンプレートユーザーを削除する
        deleteWorkflowTemplateUser(createDeleteWorkflowTemplateUser(workflowTemplateUser));
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponWorkflowService#saveTemplate
     * (jp.co.opentone.bsol.linkbinder.dto.WorkflowTemplateUser,
     * jp.co.opentone.bsol.linkbinder.dto.Workflow)
     */
    public void saveTemplate(String name, List<Workflow> workflow, Correspon correspon)
            throws ServiceAbortException {
        ArgumentValidator.validateNotEmpty(name);
        ArgumentValidator.validateNotNull(workflow);
        ArgumentValidator.validateNotNull(correspon);
        // 指定可能なCheckerの数を越えた場合はエラー。(現在Max10)
        validateNumverOfChecker(workflow);
        // 指定可能なApproverの数は１人以上の指定はエラー
        validateNumverOfApprover(workflow);

        // 指定順チェック Approver下のCheckerが存在する際はエラー
        validateWorkflowOrder(workflow);

        // Checkerが重複した場合、エラー
        // CheckerとApproverが重複した場合、エラー
        checkDuplicatedUser(workflow);

        // 指定可能なApprover、Checkerは、当該コレポン文書に指定されたプロジェクトに参加しているユーザー以外はエラー
        // プロジェクトユーザー取得
        List<ProjectUser> projectUser =
                findProjectUser(createGetProjectUserSearchUserCondition(correspon));
        validateCorresponProjectDiffUserProject(workflow, projectUser);

        // プロジェクト内で同ユーザーが作成したテンプレートのテンプレート名が重複している際はエラー
        validateExistTemplateName(name);

        // 承認フローテンプレートユーザーを登録する
        Long newId = insertWorkflowTemplateUser(createInsertWorkflowTemplateUser(name));
        // 承認フローテンプレートを登録する
        insertAllWorkflowTemplate(createWorkflowTemplateList(newId, workflow));
    }

    /**
     * コレポン文書IDを指定して承認フローを取得する.
     * @param corresponId コレポン文書ID
     * @return 承認フローリスト
     */
    private List<Workflow> findByCorresponId(Long corresponId) {
        WorkflowDao dao = getDao(WorkflowDao.class);
        return dao.findByCorresponId(corresponId);
    }

    /**
     * 指定した承認フローテンプレートを登録する.
     * @param workflowTemplate 承認フローテンプレート
     * @throws ServiceAbortException 登録失敗
     */
    private void insertWorkflowTemplate(WorkflowTemplate workflowTemplate)
            throws ServiceAbortException {
        try {
            WorkflowTemplateDao dao = getDao(WorkflowTemplateDao.class);
            dao.create(workflowTemplate);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * 指定した全ての承認フローテンプレートを登録する.
     * @param workflowTemplateList 承認フローテンプレートリスト
     * @throws ServiceAbortException 登録失敗
     */
    private void insertAllWorkflowTemplate(List<WorkflowTemplate> workflowTemplateList)
            throws ServiceAbortException {
        for (WorkflowTemplate wt : workflowTemplateList) {
            insertWorkflowTemplate(wt);
        }
    }

    /**
     * 登録用の承認フローテンプレートを作成する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @param workflow 承認フロー
     * @return 承認フローテンプレートリスト
     */
    private List<WorkflowTemplate> createWorkflowTemplateList(Long workflowTemplateUserId,
            List<Workflow> workflow) {
        List<WorkflowTemplate> workflowTemplateList = new ArrayList<WorkflowTemplate>();

        for (Workflow w : workflow) {
            WorkflowTemplate workflowTemplate = new WorkflowTemplate();
            workflowTemplate.setWorkflowTemplateUserId(workflowTemplateUserId);
            workflowTemplate.setUser(w.getUser());
            workflowTemplate.setWorkflowType(w.getWorkflowType());
            workflowTemplate.setWorkflowNo(w.getWorkflowNo());
            User loginUser = getCurrentUser();
            workflowTemplate.setCreatedBy(loginUser);
            workflowTemplate.setUpdatedBy(loginUser);
            workflowTemplateList.add(workflowTemplate);
        }
        return workflowTemplateList;
    }

    /**
     * 登録用の承認フローテンプレートユーザーオブジェクトを作成する.
     * @param name テンプレート名
     * @return 承認フローテンプレートユーザー
     */
    private WorkflowTemplateUser createInsertWorkflowTemplateUser(String name) {
        WorkflowTemplateUser workflowTemplateUser = new WorkflowTemplateUser();
        workflowTemplateUser.setProjectId(getCurrentProjectId());
        workflowTemplateUser.setName(name);
        User loginUser = getCurrentUser();
        workflowTemplateUser.setCreatedBy(loginUser);
        workflowTemplateUser.setUpdatedBy(loginUser);
        workflowTemplateUser.setUser(loginUser);
        return workflowTemplateUser;
    }

    /**
     * 指定した承認フローテンプレートユーザーを登録する.
     * @param workflowTemplateUser 承認フローテンプレートユーザー
     * @return 登録したID
     * @throws ServiceAbortException 登録失敗
     */
    private Long insertWorkflowTemplateUser(WorkflowTemplateUser workflowTemplateUser)
            throws ServiceAbortException {
        try {
            WorkflowTemplateUserDao dao = getDao(WorkflowTemplateUserDao.class);
            return dao.create(workflowTemplateUser);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * プロジェクト内で同ユーザーが作成したテンプレートのテンプレート名が重複している際はエラー.
     * @param name テンプレート名
     * @throws ServiceAbortException テンプレート名が重複
     */
    private void validateExistTemplateName(String name) throws ServiceAbortException {
        if (isExistTempalateName(name)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_TEMPLATE_NAME_ALREADY_EXISTS,
                (Object) name);
        }
    }

    /**
     * プロジェクト内で同ユーザーが作成したテンプレート名が既に存在するか判定する.
     * @param name テンプレート名
     * @return 存在するtrue / 存在しないfalse
     */
    private boolean isExistTempalateName(String name) {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setName(name);
        condition.setProjectId(getCurrentProjectId());
        condition.setUser(getCurrentUser());
        return countTemplateUserCheck(condition) > 0;
    }

    /**
     * 条件を指定して承認フローテンプレートユーザー件数を取得する.
     * @param condition 検索条件
     * @return 承認フローテンプレート件数
     */
    private int countTemplateUserCheck(SearchWorkflowTemplateUserCondition condition) {
        WorkflowTemplateUserDao dao = getDao(WorkflowTemplateUserDao.class);
        return dao.countTemplateUserCheck(condition);
    }

    /**
     * 承認状態共通チェック1.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException 共通チェックエラー
     */
    private void validateWorkflowCommonToCheckFirst(Correspon correspon, List<Workflow> workflow)
            throws ServiceAbortException {
        // ログインユーザーがSystemAdmin又はPreparer
        if (isSystemAdmin(getCurrentUser())
            || correspon.getCreatedBy().getEmpNo().equals(getCurrentUser().getEmpNo())) {
            // プロジェクトユーザーを取得する
            List<ProjectUser> pu =
                    findProjectUser(createGetProjectUserSearchUserCondition(correspon));
            // 指定可能なApprover,Checkerは当該コレポン文書に指定されたプロジェクトに参加しているユーザー以外はエラー
            validateCorresponProjectDiffUserProject(workflow, pu);
        }
    }

    /**
     * プロジェクトユーザー取得条件を削除する.
     * @param correspon コレポン文書
     * @return 検索条件
     */
    private SearchUserCondition createGetProjectUserSearchUserCondition(Correspon correspon) {
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(correspon.getProjectId());
        return condition;
    }

    /**
     * 条件を指定して、プロジェクトユーザーを取得する.
     * @param condition 検索条件
     * @return プロジェクトユーザー
     */
    private List<ProjectUser> findProjectUser(SearchUserCondition condition) {
        UserDao dao = getDao(UserDao.class);
        return dao.findProjectUser(condition);
    }

    /**
     * 指定可能なApprover、Checkerは当該コレポン文書に指定されたプロジェクトに参加しているユーザー以外はエラー.
     * @param workflow 承認フロー
     * @param projectUser プロジェクトユーザー
     * @throws ServiceAbortException プロジェクトに参加していない
     */
    private void validateCorresponProjectDiffUserProject(List<Workflow> workflow,
            List<ProjectUser> projectUser) throws ServiceAbortException {
        String empNo = getNotExistProjectUser(workflow, projectUser);
        if (StringUtils.isNotEmpty(empNo)) {
            throw new ServiceAbortException(ApplicationMessageCode.INVALID_USER, (Object) empNo);
        }
    }

    /**
     * 承認状態共通チェック2.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException 承認状態共通チェックに引っかかった場合
     */
    private void validateWorkflowCommonToCheckSecond(Correspon correspon, List<Workflow> workflows)
            throws ServiceAbortException {
        // ログインユーザー取得
        User loginUser = getCurrentUser();
        // ログインユーザーがCheckerか判定
        boolean isChecker = helper.isWorkflowChecker(workflows, getCurrentUser());

        // SystemAdmin,ProjectAdmin,DiscipilineAdmin,Checkerの場合、承認作業状態をチェックする
        if (isSystemAdmin(loginUser) || isProjectAdmin(loginUser, getCurrentProjectId())
            || isChecker || (isGroupAdmin(loginUser, correspon.getFromCorresponGroup().getId()))) {

            validateWorkflowStatus(correspon, loginUser);
        } else if (correspon.getCreatedBy().getEmpNo().equals(loginUser.getEmpNo())) {
            // Prepererの場合
            if (correspon.getWorkflowStatus() != WorkflowStatus.DRAFT) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
        } else {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * SystemAdmin,ProjectAdmin,DiscipilineAdmin,Checkerの場合、承認作業状態をチェックする.
     * @param correspon コレポン文書
     * @param loginUser ログインユーザー
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateWorkflowStatus(Correspon correspon, User loginUser)
            throws ServiceAbortException {
        WorkflowStatus status = correspon.getWorkflowStatus();
        switch (status) {
        case DRAFT:
            // Preparer、System Admin以外はアクセスできない
            if (!isSystemAdmin(loginUser)
                && (!correspon.getCreatedBy().getEmpNo().equals(loginUser.getEmpNo()))) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID);
            }
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
            // コレポン文書の承認フローパターンがパターン1またはパターン2以外はエラー
            if (!isWorkflowPattern1(correspon) && !isWorkflowPattern2(correspon)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID);
            }
            break;
        default:
            // REQUEST_FOR_CHECK、UNDER_CONSIDERATION以外は全てエラーにする
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * Checker承認作業の権限チェック.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException 権限がない
     */
    private void validateForCheck(Correspon correspon, Workflow workflow)
            throws ServiceAbortException {
        // SystemAdmin、ProjectAdmin、GroupAdmin、Checker以外はエラー
        User currentUser = getCurrentUser();
        if (log.isDebugEnabled()) {
            log.debug("isSystemAdmin[" + isSystemAdmin(currentUser) + "]");
            log.debug("isProjectAdmin[" + isProjectAdmin(currentUser, getCurrentProjectId()) + "]");
            log.debug("isGroupAdmin["
                + isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId()) + "]");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.getUser().getEmpNo[" + workflow.getUser().getEmpNo() + "]");
            log.debug("currentUser.getEmpNo[" + currentUser.getEmpNo() + "]");
        }
        if (isSystemAdmin(currentUser)
            || isProjectAdmin(currentUser, getCurrentProjectId())
            || isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
            || (workflow.isChecker()
                    && currentUser.getEmpNo().equals(workflow.getUser().getEmpNo()))) {
            // 承認作業状態のチェック
            invalidCheckerWorkflowStatus(workflow);
        } else {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * Approver承認作業の権限チェック.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException 権限がない
     */
    private void validateForApprove(Correspon correspon, Workflow workflow)
            throws ServiceAbortException {
        // SystemAdmin、ProjectAdmin、GroupAdmin、Approver以外はエラー
        User currentUser = getCurrentUser();
        if (log.isDebugEnabled()) {
            log.debug("isSystemAdmin[" + isSystemAdmin(currentUser) + "]");
            log.debug("isProjectAdmin[" + isProjectAdmin(currentUser, getCurrentProjectId()) + "]");
            log.debug("isGroupAdmin["
                + isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId()) + "]");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug("workflow.getUser().getEmpNo[" + workflow.getUser().getEmpNo() + "]");
            log.debug("currentUser.getEmpNo[" + currentUser.getEmpNo() + "]");
        }
        if (isSystemAdmin(currentUser)
            || isProjectAdmin(currentUser, getCurrentProjectId())
            || isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
            || (workflow.isApprover()
                    && currentUser.getEmpNo().equals(workflow.getUser().getEmpNo()))) {
            // 承認作業状態のチェック
            invalidApproverWorkflowStatus(workflow);
        } else {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * Deny承認作業の権限チェック.
     * @param correspon コレポン文書
     * @param workflow 承認フロー
     * @throws ServiceAbortException 権限がない
     */
    private void validateForDeny(Correspon correspon, Workflow workflow)
            throws ServiceAbortException {
        // SystemAdmin、ProjectAdmin、GroupAdmin、Approver以外はエラー
        User currentUser = getCurrentUser();
        if (log.isDebugEnabled()) {
            log.debug("isSystemAdmin[" + isSystemAdmin(currentUser) + "]");
            log.debug("isProjectAdmin[" + isProjectAdmin(currentUser, getCurrentProjectId()) + "]");
            log.debug("isGroupAdmin["
                + isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId()) + "]");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug("workflow.getUser().getEmpNo[" + workflow.getUser().getEmpNo() + "]");
            log.debug("currentUser.getEmpNo[" + currentUser.getEmpNo() + "]");
        }
        if (isSystemAdmin(currentUser)
            || isProjectAdmin(currentUser, getCurrentProjectId())
            || isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
            || (currentUser.getEmpNo().equals(workflow.getUser().getEmpNo())
                    && (workflow.isChecker() || workflow.isApprover()))) {
            // OK
            log.debug("validateForDeny OK");
        } else {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }

        if (workflow.isChecker()) {
            invalidCheckerWorkflowStatus(workflow);
        } else if (workflow.isApprover()) {
            invalidApproverWorkflowStatus(workflow);
        }
    }

    /**
     * Checkerの承認作業状態をチェックする.
     * @param workflow 承認フロー
     * @return boolean 正常ならtrue / 異常ならfalse
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void invalidCheckerWorkflowStatus(Workflow workflow) throws ServiceAbortException {
        if (log.isDebugEnabled()) {
            log.trace("********** invalidCheckerWorkflowStatus **********");
            log.debug("workflow.isChecker[" + workflow.isChecker() + "]");
            log.debug("workflow.getWorkflowProcessStatus["
                + workflow.getWorkflowProcessStatus() + "]");
        }
        if (!workflow.isChecker()
            || !(WorkflowProcessStatus.REQUEST_FOR_CHECK
                .equals(workflow.getWorkflowProcessStatus())
                    || WorkflowProcessStatus.UNDER_CONSIDERATION
                .equals(workflow.getWorkflowProcessStatus()))) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_CHECKER);
        }
    }

    /**
     * Approverの承認作業状態をチェックする.
     * @param workflow 承認フロー
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void invalidApproverWorkflowStatus(Workflow workflow) throws ServiceAbortException {
        if (log.isDebugEnabled()) {
            log.trace("********** invalidApproverWorkflowStatus **********");
            log.debug("workflow.isApprover[" + workflow.isApprover() + "]");
            log.debug("workflow.getWorkflowProcessStatus["
                + workflow.getWorkflowProcessStatus() + "]");
        }
        if (!workflow.isApprover()
            || !(WorkflowProcessStatus.REQUEST_FOR_APPROVAL.equals(workflow
                .getWorkflowProcessStatus()) || WorkflowProcessStatus.UNDER_CONSIDERATION
                .equals(workflow.getWorkflowProcessStatus()))) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID_FOR_APPROVER);
        }
    }

    /**
     * 承認フローに設定したCheckerの数が最大数を超えているか判定する.
     * @param workflow コレポン文書に指定された承認フローリスト
     * @return boolean true:最大数を超えていない / false:最大数を超えている
     */
    private boolean isOverMaxChecker(List<Workflow> workflow) {
        // Checkerの数が最大数を超えているか判定する
        int count = 0;
        for (Workflow w : workflow) {
            if (w.isChecker()) {
                count++;
            }
        }
        return maxCheckerSize < count;
    }

    /**
     * 承認フローに設定したApproverの数が最大数を超えているか判定する.
     * @param workflow コレポン文書に指定された承認フローリスト
     * @return boolean true:最大数を超えていない / false:最大数を超えている
     */
    private boolean isOverMaxApprover(List<Workflow> workflow) {
        // Approverの数が最大数を超えているか判定する
        int count = 0;
        for (Workflow w : workflow) {
            if (w.isApprover()) {
                count++;
            }
        }
        // 指定したApproverの数が最大数以上はエラー
        return maxApproverSize < count || count == 0;
    }

    /**
     * Approver下のCheckerが存在しているか判定する.
     * @param workflow コレポン文書に指定された承認フローリスト
     * @return boolean true:CheckerがApprover下に存在する /
     *         false:CheckerがApprover下に存在しない
     */
    private boolean isApproverUnderChecker(List<Workflow> workflow) {
        boolean isApproverUnderChecker = false;
        boolean isApprover = false;

        for (Workflow w : workflow) {
            if (isApprover) {
                // Checkerが存在するか判定
                if (w.isChecker()) {
                    // エラーにする
                    isApproverUnderChecker = true;
                    break;
                }
            }
            if (w.isApprover()) {
                isApprover = true;
            }
        }
        return isApproverUnderChecker;
    }

    /**
     * 指定したChecker,Approverがコレポン文書に指定されたプロジェクトに参加しているか判定する.
     * @param workflow コレポン文書に指定された承認フローリスト
     * @param pu プロジェクトユーザーリスト
     * @return プロジェクトに参加していない従業員番号 参加している場合は空
     */
    private String getNotExistProjectUser(List<Workflow> workflow, List<ProjectUser> pu) {
        String notExistProjectUser = "";
        for (Workflow w : workflow) {
            boolean found = false;
            String empNo = w.getUser().getEmpNo();
            for (ProjectUser p : pu) {
                String pempNo = p.getUser().getEmpNo();
                // 承認フローに設定されているユーザーがプロジェクトに参加しているか判定
                if (StringUtils.isNotEmpty(empNo) && empNo.equals(pempNo)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // プロジェクトに参加していない従業員番号
                notExistProjectUser = empNo;
                break;
            }
        }
        return notExistProjectUser;
    }

    /**
     * Approverが作業中か判定し、作業中の場合は承認フローの差分を判定する.
     * @param correspon コレポン文書
     * @param workflow 承認フローリスト
     * @return boolean true:異常 / false;正常
     * @throws ServiceAbortException エラー
     */
    private boolean isApproverUnderConsideration(Correspon correspon, List<Workflow> workflow)
            throws ServiceAbortException {
        // DBから承認フローを取得する
        long id = correspon.getId();
        boolean isApproverUnderConsideration = false;

        // DBから承認フローリストを取得する
        WorkflowDao dao = getDao(WorkflowDao.class);
        List<Workflow> workflowDb = dao.findByCorresponId(id);
        if (workflowDb.isEmpty()) {
            return false;
        }
        Workflow wf = workflowDb.get(workflowDb.size() - 1);
        WorkflowProcessStatus wfp = wf.getWorkflowProcessStatus();

        if (wf.isApprover() && WorkflowProcessStatus.NONE == wfp) {
            // 承認フロー差分チェック
            isApproverUnderConsideration = isInvalidWorkflowDiffCheck(workflow, workflowDb);
        } else {
            isApproverUnderConsideration = true;
        }
        return isApproverUnderConsideration;
    }

    /**
     * DBが保持している承認フローリストと渡ってきた承認フローリストの差分をチェックする.
     * @param workflow コレポン文書から取得した承認フローリスト
     * @param workflowDb DBから取得した承認フローリスト
     * @return boolean true:異常 / false:正常
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private boolean isInvalidWorkflowDiffCheck(List<Workflow> workflow, List<Workflow> workflowDb)
            throws ServiceAbortException {
        // コレポン文書から取得した承認フローリストとDBから取得した承認フローリストの差分を見る
        if (isDeletedWorkflow(workflowDb, workflow)) {
            return true;
        }
        int count = 0;
        for (Workflow w : workflow) {
            Long workflowId = w.getId();
            // 追加データがあるかチェック
            if (workflowId == 0) {
                count++;
            }
        }
        // 追加したデータがある場合
        if (0 < count) {
            return isAddWorkflow(workflow);
        }
        return false;
    }

    /**
     * コレポン文書から取得した承認フローリストとDBから取得した承認フローリストの差分をチェックする.
     * @param workflowIdDb DBから取得した承認フローID
     * @return 承認フローリストに存在しなければtrue、それ以外はfalse
     */
    private boolean isDeletedWorkflow(List<Workflow> workflowDb, List<Workflow> workflows) {
        for (Workflow wd : workflowDb) {
            long workflowIdDb = wd.getId();
            WorkflowProcessStatus processStatusDb = wd.getWorkflowProcessStatus();
            // 削除されたデータがあるかチェックする
            if (WorkflowProcessStatus.NONE != processStatusDb) {
                if (isInvalidWorkflowDbDiff(workflowIdDb, processStatusDb, workflows)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 承認フロー設定が正しい順序で作成されているかチェックする.
     * @param workflowIdDb コレポン文書から取得した承認フローリスト
     * @return true:異常 false:正常
     */
    private boolean isAddWorkflow(List<Workflow> workflow) {
        int processCount = 0;
        // 承認フロー設定が正しい順序で作成されているかチェックする
        // 各承認フローの承認作業状態の順序が正しいか判定する
        for (int i = workflow.size() - 1; 0 <= i; i--) {
            WorkflowProcessStatus processStatus = workflow.get(i).getWorkflowProcessStatus();
            if (0 < processCount) {
                // 承認作業状態『None』が存在するか判定
                if (WorkflowProcessStatus.NONE == processStatus) {
                    // エラーにする
                    return true;
                }
            }
            // 承認作業状態が『None』以外を判定
            if (WorkflowProcessStatus.NONE != processStatus) {
                processCount++;
            }
        }
        return false;
    }

    /**
     * DBが保持している承認フローリストと渡ってきた承認フローリストの差分をチェックする.
     * @param workflowIdDb DBから取得した承認フローID
     * @param processStatusDb DBから取得した承認フロー作業状態
     * @param workflow コレポン文書から取得した承認フローリスト
     * @return 承認フローリストに存在しなければtrue、それ以外はfalse
     */
    private boolean isInvalidWorkflowDbDiff(long workflowIdDb,
        WorkflowProcessStatus processStatusDb, List<Workflow> workflow) {
        for (Workflow w : workflow) {
            long workflowId = w.getId();
            if (workflowIdDb == workflowId) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checkerの承認で、コレポン文書の承認状態を更新する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    private void updateCorresponForCheck(Correspon correspon, Workflow workflow)
            throws ServiceAbortException {
        // コレポン文書を承認/否認していないCheckerが存在しない場合、[Request for Approval]
        WorkflowStatus workflowStatus = WorkflowStatus.REQUEST_FOR_APPROVAL;

        // コレポン文書を承認/否認していないCheckerが存在する場合、 [Under Consideration]
        List<Workflow> list = correspon.getWorkflows();
        for (int i = 0; i < list.size(); i++) {
            Workflow otherWorkflow = list.get(i);
            if (otherWorkflow.isChecker()
                    && !workflow.getId().equals(otherWorkflow.getId()) // 対象のワークフローは除く
                && !WorkflowProcessStatus.CHECKED.equals(
                    otherWorkflow.getWorkflowProcessStatus())) {
                workflowStatus = WorkflowStatus.UNDER_CONSIDERATION;
            }
        }
        // コレポン文書の更新
        updateCorrespon(createNewCorrespon(correspon, workflowStatus));
    }

    /**
     * Approverの承認で、コレポン文書の承認状態を更新する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException 承認失敗
     */
    private void updateCorresponForApprove(Correspon correspon) throws ServiceAbortException {
        Correspon newCorrespon = createNewCorrespon(correspon, WorkflowStatus.ISSUED);
        // コレポン文書番号を生成
        newCorrespon.setCorresponNo(corresponSequenceService.getCorresponNo(correspon));
        newCorrespon.setIssuedAt(DateUtil.getNow());
        newCorrespon.setIssuedBy(getCurrentUser());

        // コレポン文書の更新
        updateCorrespon(newCorrespon);
    }

    /**
     * コレポン文書を否認する.
     * @param correspon コレポン文書
     * @throws ServiceAbortException * 否認失敗
     */
    private void updateCorresponForDeny(Correspon correspon) throws ServiceAbortException {
        Correspon newCorrespon = createNewCorrespon(correspon, WorkflowStatus.DENIED);
        // コレポン文書の更新
        updateCorrespon(newCorrespon);
    }

    /**
     * 更新用のコレポン文書Dtoを生成する.
     * @param oldCorrespon コレポン文書
     * @param workflowStatus 承認状態
     * @return 更新用コレポン文書
     */
    private Correspon createNewCorrespon(Correspon oldCorrespon, WorkflowStatus workflowStatus) {
        Correspon newCorrespon = new Correspon();
        newCorrespon.setId(oldCorrespon.getId());
        newCorrespon.setUpdatedBy(getCurrentUser());
        newCorrespon.setWorkflowStatus(workflowStatus);
        newCorrespon.setVersionNo(oldCorrespon.getVersionNo());

        return newCorrespon;
    }

    /**
     * コレポン文書の承認状態を更新する.
     * @param correspon コレポン文書
     * @param workflowStatus 承認状態
     * @throws ServiceAbortException 更新失敗
     */
    private void updateCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.update(correspon);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * Checkerの承認で、ワークフローの承認作業状態を更新する.
     * @param correspon コレポン文書
     * @param workflow ワークフロー
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflowForCheck(Correspon correspon, Workflow workflow)
            throws ServiceAbortException {
        // 承認作業状態の更新
        Workflow newWorkflow = createNewWorkFlow(workflow, WorkflowProcessStatus.CHECKED);
        newWorkflow.setCommentOn(SQLConvertUtil.parseNull(workflow.getCommentOn()));
        newWorkflow.setFinishedAt(DateUtil.getNow());
        newWorkflow.setFinishedBy(getCurrentUser());

        updateWorkflow(newWorkflow);

        // 承認フローの適用パターンに準じてChecker、Approverの承認作業状態を更新する
        List<Workflow> workflowList = correspon.getWorkflows();
        // 承認パターン1
        if (isWorkflowPattern1(correspon)) {
            updateWorkflowPattern1(workflowList, workflow);
            // 承認パターン2
        } else if (isWorkflowPattern2(correspon)) {
            updateWorkflowPattern2(workflowList, workflow);
        } else if (isWorkflowPattern3(correspon)) {
            updateWorkflowPattern3(workflowList, workflow);
        }
        // 承認パターン3の時は何もしない
    }

    /**
     * ワークフロー中で承認作業状態が[None]である最初のワークフローユーザーを返す.
     * @param workflowList ワークフローリスト
     * @param ignore 無視するワークフロー
     * @return 承認作業状態がNoneである最初のワークフロー
     */
    protected Workflow getFirstNotProcessedWorkflow(List<Workflow> workflowList, Workflow ignore) {
        Workflow result = null;
        for (Workflow w : workflowList) {
            if (w.getWorkflowNo().equals(ignore.getWorkflowNo())) {
                continue;
            }
            if (WorkflowProcessStatus.NONE == w.getWorkflowProcessStatus()) {
                result = w;
                break;
            }
        }
        return result;
    }

    protected boolean isNextProcessStatusUpdatable(
        List<Workflow> workflowList, Workflow next, Workflow ignore) {
        for (Workflow w : workflowList) {
            if (w.isApprover()) {
                return false;
            }
            if (w.getWorkflowNo() >= next.getWorkflowNo()) {
                break;
            }
            if (w.getWorkflowNo().equals(ignore.getWorkflowNo())) {
                continue;
            }
            if (WorkflowProcessStatus.CHECKED != w.getWorkflowProcessStatus()) {
                return false;
            }
        }
        return true;
    }

    /**
     * パターン1のワークフローを更新する.
     * @param workflowList ワークフローリスト
     * @param workflow 自分のワークフロー
     * @throws ServiceAbortException 更新時エラー
     */
    private void updateWorkflowPattern1(List<Workflow> workflowList, Workflow workflow)
            throws ServiceAbortException {
        //  まだ未処理のワークフローのうち、先頭の承認作業状態を更新
        //  通常は自身の直近のワークフローであるが、検証・承認中にワークフローパターンが変更された場合への対処のため
        //  このようにしている
        //  但し未処理でも、前のCheckerが全てCheckedでなければ承認作業状態は更新しない
        Workflow next = getFirstNotProcessedWorkflow(workflowList, workflow);
        if (next != null && isNextProcessStatusUpdatable(workflowList, next, workflow)) {
            Workflow wf = createNewWorkFlow(next, WorkflowProcessStatus.REQUEST_FOR_CHECK);
            updateWorkflowProcessStatusById(wf, WorkflowProcessStatus.NONE);
        }

        //  検証・承認依頼後に承認フローパターンが変更された場合への対応
        //    ・CheckerがすべてCheck済みの時、Approverを更新
        updateApproverWorkflowProcessStatus(workflowList, workflow);
    }

    /**
     * パターン2のワークフローを更新する.
     * @param workflowList ワークフローリスト
     * @param workflow 自分のワークフロー
     * @throws ServiceAbortException 更新時エラー
     */
    private void updateWorkflowPattern2(List<Workflow> workflowList, Workflow workflow)
            throws ServiceAbortException {
        //  検証・承認依頼後に承認フローパターンが変更された場合への対応
        //    ・検証・承認依頼されていないCheckerの承認作業状態をRequest for Checkに更新する
        Workflow checkers = new Workflow();
        //  更新値
        checkers.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        checkers.setUpdatedBy(workflow.getUpdatedBy());
        //  更新条件
        checkers.setCorresponId(workflow.getCorresponId());
        checkers.setWorkflowType(WorkflowType.CHECKER);

        updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
                checkers,
                WorkflowProcessStatus.NONE);

        // CheckerがすべてCheck済みの時、Approverを更新
        updateApproverWorkflowProcessStatus(workflowList, workflow);
    }

    /**
     * 全てのCheckerの承認作業状態がCheckedの場合、Approverの承認作業状態をRequest for Approvalに更新する.
     * @param workflowList ワークフローリスト
     * @param workflow 自分のワークフロー
     * @throws ServiceAbortException 更新時エラー
     */
    private void updateApproverWorkflowProcessStatus(
        List<Workflow> workflowList, Workflow workflow)
            throws ServiceAbortException {

        // CheckerがすべてCheck済みの時、Approverを更新
        boolean isUpdate = true;
        Workflow approver = null;
        for (int i = 0; i < workflowList.size(); i++) {
            Workflow nextWorkflow = workflowList.get(i);
            if (nextWorkflow.getWorkflowNo().equals(workflow.getWorkflowNo())) {
                continue;
            }

            if (nextWorkflow.isChecker()
                && !WorkflowProcessStatus.CHECKED.equals(nextWorkflow.getWorkflowProcessStatus())) {
                isUpdate = false;
                break;
            } else if (nextWorkflow.isApprover()) {
                approver = nextWorkflow;
            }
        }

        if (isUpdate) {
            Workflow wf = createNewWorkFlow(approver, WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
            updateWorkflowProcessStatusById(wf, WorkflowProcessStatus.NONE);
        }
    }

    /**
     * パターン3のワークフローを更新する.
     * @param workflowList ワークフローリスト
     * @param workflow 自分のワークフロー
     * @throws ServiceAbortException 更新時エラー
     */
    private void updateWorkflowPattern3(List<Workflow> workflowList, Workflow workflow)
            throws ServiceAbortException {
        //  検証・承認依頼後に承認フローパターンが変更された場合への対応
        //    ・検証・承認依頼されていないCheckerの承認作業状態をRequest for Checkに更新する
        //    ・検証・承認依頼されていないApproverの承認作業状態をRequest for Approvalに更新する
        Workflow checkers = new Workflow();
        //  更新値
        checkers.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
        checkers.setUpdatedBy(workflow.getUpdatedBy());
        //  更新条件
        checkers.setCorresponId(workflow.getCorresponId());
        checkers.setWorkflowType(WorkflowType.CHECKER);

        updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
                checkers,
                WorkflowProcessStatus.NONE);

        Workflow approver = new Workflow();
        //  更新値
        approver.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
        approver.setUpdatedBy(workflow.getUpdatedBy());
        //  更新条件
        approver.setCorresponId(workflow.getCorresponId());
        approver.setWorkflowType(WorkflowType.APPROVER);

        updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
                approver,
                WorkflowProcessStatus.NONE);
    }

    /**
     * Approverの承認で、ワークフローの承認作業状態を更新する.
     * @param workflow ワークフロー
     * @throws ServiceAbortException 承認失敗
     */
    private void updateWorkflowForApprove(Workflow workflow) throws ServiceAbortException {
        Workflow newWorkflow = createNewWorkFlow(workflow, WorkflowProcessStatus.APPROVED);
        newWorkflow.setCommentOn(SQLConvertUtil.parseNull(workflow.getCommentOn()));
        newWorkflow.setFinishedAt(DateUtil.getNow());
        newWorkflow.setFinishedBy(getCurrentUser());

        updateWorkflow(newWorkflow);
    }

    private void copyLearningCorrespon(Correspon correspon) throws ServiceAbortException {
        Correspon originalCorrespon = corresponService.findCorrespon(correspon.getId());
        corresponService.copyCorresponForLearning(originalCorrespon);
    }

    /**
     * 承認フローを否認する.
     * @param workflow ワークフロー
     * @throws ServiceAbortException 否認失敗
     */
    private void updateWorkflowForDeny(Workflow workflow) throws ServiceAbortException {
        Workflow newWorkflow = createNewWorkFlow(workflow, WorkflowProcessStatus.DENIED);
        newWorkflow.setCommentOn(SQLConvertUtil.parseNull(workflow.getCommentOn()));
        newWorkflow.setFinishedAt(DateUtil.getNow());
        newWorkflow.setFinishedBy(getCurrentUser());

        updateWorkflow(newWorkflow);
    }

    /**
     * 更新用のワークフローDtoを生成する.
     * @param oldWorkflow ワークフロー
     * @param workflowProcessStatus ワークフローステータス
     * @return 更新用ワークフロー
     */
    private Workflow createNewWorkFlow(Workflow oldWorkflow,
        WorkflowProcessStatus workflowProcessStatus) {
        Workflow newWorkflow = new Workflow();
        newWorkflow.setId(oldWorkflow.getId());
        newWorkflow.setUpdatedBy(getCurrentUser());
        newWorkflow.setWorkflowProcessStatus(workflowProcessStatus);
        newWorkflow.setVersionNo(oldWorkflow.getVersionNo());

        return newWorkflow;
    }

    /**
     * ワークフローの承認作業状態を更新する.
     * @param workflow ワークフロー
     * @param workflowProcessStatus 承認作業状態
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflow(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.update(workflow);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * ワークフローの承認作業状態を更新する.
     * @param workflow ワークフロー
     * @param workflowProcessStatus 承認作業状態
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflowProcessStatusById(
        Workflow workflow, WorkflowProcessStatus currentStatus)
            throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.updateWorkflowProcessStatusById(workflow, currentStatus);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * ワークフローの承認作業状態を更新する.
     * @param workflow ワークフロー
     * @param workflowProcessStatus 承認作業状態
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(
        Workflow workflow, WorkflowProcessStatus currentStatus)
            throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.updateWorkflowProcessStatusesByCorresponIdAndWorkflowType(workflow, currentStatus);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 承認フローパターン1の際にCheckerが自分の前者を変更した際はエラー.
     * 承認フローパターン2の際に検証者は、承認依頼後(承認作業状態=NONE以外) の承認者を変更した際はエラー
     * @param correspon コレポン文書
     * @param workflows 承認フロー
     * @param workflowsDb 変更前承認フロー
     * @throws ServiceAbortException チェックに引っかかった場合
     */
    private void validateSavePattern1Or2(Correspon correspon, List<Workflow> workflows,
            List<Workflow> workflowsDb) throws ServiceAbortException {
        log.debug("validateSavePattern1Or2 start");
        //  Draftの場合はまだ検証・承認が始まっていないのでこのチェックを行う必要はない
        if (correspon.getWorkflowStatus() == WorkflowStatus.DRAFT) {
            return;
        }

        if (isWorkflowPattern1(correspon) && isWorkflowUpdatableUser(correspon, workflowsDb)) {
            Long selfWorkflowNo = getProcessingWorkflowNo(workflowsDb);
            log.debug("check for pattern1");
            if (selfWorkflowNo == null) {
                //  検証・承認依頼中に、作業中のユーザーが一人もいないことは殆どあり得ない
                //  あるとすれば、既に誰かが否認した/全員承認したのいずれか。
                //  その場合は以降の処理で排他エラーになるはずなので、ここでは先頭の承認フローNoをセットしておき
                //  NullPointerExceptionが発生しないようにする
                selfWorkflowNo = 0L;
            }

            // 自身の前者を変更しているか判定する
            for (int i = 0; i < selfWorkflowNo; i++) {
                if (i > workflows.size()
                    || !workflows.get(i).getId().equals(workflowsDb.get(i).getId())) {
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID);
                }
            }
        } else if (isWorkflowPattern2(correspon)
                   && isWorkflowUpdatableUser(correspon, workflowsDb)) {
            log.debug("check for pattern2");
            //  承認フローパターン2では、新たなCheckerを追加することはできない
            //  Checkerが追加されていればエラー
            if (isCheckerAdded(workflows)) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_NOT_ALLOW_CHANGE_CHECKER);
            }
            for (Workflow w : workflowsDb) {
                log.debug(" {}. {} {}/{}", new Object[]{w.getWorkflowNo(),
                                            w.getUser().getEmpNo(),
                                            w.getWorkflowType(),
                                            w.getWorkflowProcessStatus()});
                WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
                WorkflowType type = w.getWorkflowType();
                if (type == WorkflowType.APPROVER) {
                    log.debug(" approver!");
                    if (processStatus != WorkflowProcessStatus.NONE) {
                        log.debug(" in process!");
                        throw new ServiceAbortException(
                            ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_PATTERN_INVALID);
                    }
                }
            }
        }
    }

    /**
     * workflowsに新たに追加されるCheckerが含まれている場合はtrueを返す.
     * @param workflows
     *   検証対象の承認フローリスト.データベースの現状のリストではなく、このServiceへのインプットとなった登録対象のリスト.
     * @return Checkerが追加されている場合はtrue
     */
    private boolean isCheckerAdded(List<Workflow> workflows) {
        final Long newId = Long.valueOf(0L);
        for (Workflow w : workflows) {
            log.debug("  {}.{}-{} {}", new Object[] {w.getWorkflowNo(),
                                            w.getId(),
                                            w.getUser().getEmpNo(),
                                            w.getWorkflowType()});
            //  id未設定のCheckerを新規追加とみなす
            if (WorkflowType.CHECKER == w.getWorkflowType()
                    && (w.getId() == null || newId.equals(w.getId()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 検証・承認依頼中に承認フローを更新可能なユーザーである場合はtrueを返す.
     * @param correspon コレポン文書
     * @param workflowsDb 登録済の承認フロー. (入力値ではなく現在データベースに存在する承認フロー)
     * @return 更新可能な場合はtrue
     */
    private boolean isWorkflowUpdatableUser(Correspon correspon, List<Workflow> workflowsDb) {
        User u = getCurrentUser();
        return (helper.isWorkflowChecker(workflowsDb, u)
                || isSystemAdmin(u)
                || isProjectAdmin(u, getCurrentProjectId()))
                || isGroupAdmin(u, correspon.getFromCorresponGroup().getId());
    }

    /**
     * 承認フローテンプレートユーザーリストを取得する.
     * @return 承認フローテンプレートユーザー
     */
    private List<WorkflowTemplateUser> findWorkflowTemplateUser() {
        SearchWorkflowTemplateUserCondition condition = new SearchWorkflowTemplateUserCondition();
        condition.setProjectId(getCurrentProjectId());
        condition.setUser(getCurrentUser());

        WorkflowTemplateUserDao dao = getDao(WorkflowTemplateUserDao.class);
        return dao.find(condition);
    }

    /**
     * 承認フローテンプレートユーザーIDを指定して承認フローテンプレートを取得する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @return 承認フローテンプレート
     */
    private List<WorkflowTemplate> findWorkflowTemplateByWorkflowTemplateUserId(
        Long workflowTemplateUserId) {
        WorkflowTemplateDao dao = getDao(WorkflowTemplateDao.class);
        return dao.findByWorkflowTemplateUserId(workflowTemplateUserId);
    }

    /**
     * IDを指定して承認フローテンプレートユーザーを取得する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @return 承認フローテンプレートユーザー
     * @throws ServiceAbortException 取得失敗
     */
    private WorkflowTemplateUser findByTemplateUserId(Long workflowTemplateUserId)
            throws ServiceAbortException {
        try {
            WorkflowTemplateUserDao dao = getDao(WorkflowTemplateUserDao.class);
            return dao.findById(workflowTemplateUserId);
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_TEMPLATE_NOT_EXIST);
        }
    }

    /**
     * 削除用の承認フローテンプレートオブジェクトを作成する.
     * @param workflowTemplateUserId 承認フローテンプレートユーザーID
     * @return 承認フローテンプレート
     */
    private WorkflowTemplate createDeleteWorkflowTemplate(Long workflowTemplateUserId) {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.setWorkflowTemplateUserId(workflowTemplateUserId);
        workflowTemplate.setUpdatedBy(getCurrentUser());
        return workflowTemplate;
    }

    /**
     * 削除用の承認フローテンプレートユーザーオブジェクトを作成する.
     * @param workflowTemplateUser 承認フローテンプレートユーザー
     * @return 承認フローテンプレートユーザー
     */
    private WorkflowTemplateUser createDeleteWorkflowTemplateUser(
        WorkflowTemplateUser workflowTemplateUser) {
        WorkflowTemplateUser deleteTemplateUser = new WorkflowTemplateUser();
        deleteTemplateUser.setId(workflowTemplateUser.getId());
        deleteTemplateUser.setUpdatedBy(getCurrentUser());
        deleteTemplateUser.setVersionNo(workflowTemplateUser.getVersionNo());
        return deleteTemplateUser;
    }

    /**
     * 指定した承認フローテンプレートユーザーを削除する.
     * @param templateUser 承認フローテンプレートユーザー
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteWorkflowTemplateUser(WorkflowTemplateUser templateUser)
            throws ServiceAbortException {
        try {
            WorkflowTemplateUserDao dao = getDao(WorkflowTemplateUserDao.class);
            return dao.delete(templateUser);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * 指定した承認フローテンプレートを削除する.
     * @param template 承認フローテンプレート
     * @return 削除した件数
     * @throws ServiceAbortException 削除失敗
     */
    private Integer deleteWorkflowTemplateByWorkflowTemplateUserId(WorkflowTemplate template)
            throws ServiceAbortException {
        try {
            WorkflowTemplateDao dao = getDao(WorkflowTemplateDao.class);
            return dao.deleteByWorkflowTemplateUserId(template);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(e);
        }
    }

    private boolean isWorkflowPattern1(Correspon c) {
        String p = SystemConfig.getValue(KEY_PATTERN_1);
        return p.equals(getWorkflowPatternCd(c));
    }

    private boolean isWorkflowPattern2(Correspon c) {
        String p = SystemConfig.getValue(KEY_PATTERN_2);
        return p.equals(getWorkflowPatternCd(c));
    }

    private boolean isWorkflowPattern3(Correspon c) {
        String p = SystemConfig.getValue(KEY_PATTERN_3);
        return p.equals(getWorkflowPatternCd(c));
    }

    private String getWorkflowPatternCd(Correspon c) {
        if (c.getCorresponType() == null
            || c.getCorresponType().getWorkflowPattern() == null) {
            return null;
        }

        return StringUtils.trim(c.getCorresponType().getWorkflowPattern().getWorkflowCd());
    }

    private void sendWorkflowNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd)
            throws ServiceAbortException {
        Correspon updatedCorrespon = corresponService.find(correspon.getId());
        emailNotificationService.sendWorkflowNotice(updatedCorrespon, emailNoticeEventCd);
    }

    private void sendIssuedNotice(Correspon correspon) throws ServiceAbortException {
        Correspon updatedCorrespon = corresponService.find(correspon.getId());
        emailNotificationService
                .sendIssuedNotice(updatedCorrespon, EmailNoticeEventCd.ISSUED, null);
    }
}
