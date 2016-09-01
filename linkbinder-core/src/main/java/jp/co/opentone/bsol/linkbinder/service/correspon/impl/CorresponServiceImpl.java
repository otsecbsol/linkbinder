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

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Resource;

import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchProjectCondition;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.record.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreClient;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreException;
import jp.co.opentone.bsol.framework.core.generator.GeneratorFailedException;
import jp.co.opentone.bsol.framework.core.generator.html.HTMLGenerator;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.core.util.PropertyGetUtil;
import jp.co.opentone.bsol.framework.core.util.zip.ZipArchiver;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponResponseHistory;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.WorkflowPattern;
import jp.co.opentone.bsol.linkbinder.dto.code.AllowApproverToBrowse;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.code.ForceToUseWorkflow;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowType;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.common.CorresponSequenceService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponReadStatusService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService;
import jp.co.opentone.bsol.linkbinder.util.AttachmentUtil;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponResponseHistoryModel;

/**
 * このサービスではコレポン文書に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponServiceImpl extends AbstractService implements CorresponService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4004774537778286277L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(CorresponServiceImpl.class);
    /**
     * HTMLテンプレート名.
     */
    private static final String TEMPLATE_KEY_HTML = "template.correspon.html";
    /**
     * HTMLテンプレート名（ZIPダウンロード）.
     */
    private static final String TEMPLATE_KEY_ZIP = "template.correspon.zip";
    /**
     * ファイル名用IDフォーマット形式.
     */
    private static final String FILENAME_ID_FORMAT = "%010d";
    /**
     * ファイル名フォーマット形式.
     */
    private static final String FILENAME_FORMAT = "%s_%s(%s).html";
    /**
     * 添付ファイル名フォーマット形式(ディレクトリ名付き).
     */
    private static final String ATTACHMENT_FILENAME_FORMAT = "Attachments-%s_%s(%s)\\%s";
    /**
     * ファイル名禁止文字をプロパティから取得するKEY.
     */
    private static final String FILENAME_KEY_REGEX = "file.name.regex";
    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REGEX = "[\\\\/:*?\"<>|]";
    /**
     * ファイル名置換文字をプロパティから取得するするKEY.
     */
    private static final String FILENAME_KEY_REPLACEMENT = "file.name.replacement";
    /**
     * ファイル名禁止文字がプロパティに設定されていない場合のデフォルト値.
     */
    private static final String FILENAME_DEFAULT_REPLACEMENT = "-";
    /**
     * スタイルシート名.
     */
    private static final String TEMPLATE_KEY_STYLESHEET = "template.stylesheet";

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponServiceImpl() {
    }

    /**
     * コレポン文書番号採番サービス.
     */
    @Resource
    private CorresponSequenceService corresponSequenceService;

    /** /**
     * コレポン文書既読・未読サービス.
     */
    @Resource
    private CorresponReadStatusService corresponReadStatusService;

    /**
     * E-mail通知関連サービス.
     */
    @Resource
    private EmailNoticeService emailNotificationService;

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#find(java.lang.Long)
     */
    @Transactional(readOnly = true)
    public Correspon find(Long id) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(id);
        try {
            Correspon c = findCorrespon(id);
            // 関連情報を取得
            loadAddressCorresponGroup(c);
            loadWorkflow(c);
            // アクセス可能なレコードであるか検証
            validateAccess(c);
            return c;
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }
    }

    private Correspon findCorrespon(Long id)
            throws RecordNotFoundException, ServiceAbortException {
        CorresponDao dao = getDao(CorresponDao.class);
        Correspon c = dao.findById(id);
        loadCorresponType(c);
        adjustCustomFields(c);

        return c;
    }

    private void loadCorresponType(Correspon c)  throws RecordNotFoundException {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        CorresponType ct =
            dao.findByProjectCorresponTypeId(
                c.getCorresponType().getProjectCorresponTypeId());
        c.setCorresponType(ct);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *     .CorresponService#adjustCustomFields(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public void adjustCustomFields(Correspon c) throws ServiceAbortException {
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setProjectId(c.getProjectId());
        CustomFieldDao dao = getDao(CustomFieldDao.class);
        List<CustomField> fields = dao.findByProjectId(condition);
        List<CorresponCustomField> currentValues = collectCurrentCorresponCustomField(c);

        final String idProperty = "customField%dId";
        final String labelProperty = "customField%dLabel";
        final String valueProperty = "customField%dValue";
        int maxCount =
            Integer.parseInt(SystemConfig.getValue(Constants.KEY_CUSTOM_FIELD_MAX_COUNT));
        int max = fields.size() > maxCount ? maxCount : fields.size();

        //  現在のカスタムフィールドマスタの並び順で並べ換える
        int i = 0;
        for (; i < max; i++) {
            CustomField f = fields.get(i);

            PropertyGetUtil.setProperty(
                c, String.format(idProperty, (i + 1)), f.getProjectCustomFieldId());
            PropertyGetUtil.setProperty(
                c, String.format(labelProperty, (i + 1)), f.getLabel());

            String value = null;
            for (CorresponCustomField v : currentValues) {
                if (f.getProjectCustomFieldId().equals(v.getProjectCustomFieldId())) {
                    value = v.getValue();
                    break;
                }
            }
            PropertyGetUtil.setProperty(
                c, String.format(valueProperty, (i + 1)), value);
        }
        for (; i < maxCount; i++) {
            PropertyGetUtil.setProperty(
                c, String.format(idProperty, (i + 1)), null);
            PropertyGetUtil.setProperty(
                c, String.format(labelProperty, (i + 1)), null);
            PropertyGetUtil.setProperty(
                c, String.format(valueProperty, (i + 1)), null);
        }
    }

    //  カスタムフィールド項目数が多いため行数オーバーする
    //CHECKSTYLE:OFF
    private List<CorresponCustomField> collectCurrentCorresponCustomField(Correspon c) {
        List<CorresponCustomField> fields = new ArrayList<CorresponCustomField>();
        CorresponCustomField f;
        if (c.getCustomField1Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField1Id());
            f.setValue(c.getCustomField1Value());
            fields.add(f);
        }
        if (c.getCustomField2Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField2Id());
            f.setValue(c.getCustomField2Value());
            fields.add(f);
        }
        if (c.getCustomField3Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField3Id());
            f.setValue(c.getCustomField3Value());
            fields.add(f);
        }
        if (c.getCustomField4Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField4Id());
            f.setValue(c.getCustomField4Value());
            fields.add(f);
        }
        if (c.getCustomField5Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField5Id());
            f.setValue(c.getCustomField5Value());
            fields.add(f);
        }
        if (c.getCustomField6Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField6Id());
            f.setValue(c.getCustomField6Value());
            fields.add(f);
        }
        if (c.getCustomField7Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField7Id());
            f.setValue(c.getCustomField7Value());
            fields.add(f);
        }
        if (c.getCustomField8Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField8Id());
            f.setValue(c.getCustomField8Value());
            fields.add(f);
        }
        if (c.getCustomField9Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField9Id());
            f.setValue(c.getCustomField9Value());
            fields.add(f);
        }
        if (c.getCustomField10Id() != null) {
            f = new CorresponCustomField();
            f.setProjectCustomFieldId(c.getCustomField10Id());
            f.setValue(c.getCustomField10Value());
            fields.add(f);
        }
        return fields;
    }
    //CHECKSTYLE:ON


    private void loadAddressCorresponGroup(Correspon c) {
        AddressCorresponGroupDao dao = getDao(AddressCorresponGroupDao.class);
        c.setAddressCorresponGroups(dao.findByCorresponId(c.getId()));
    }

    private void loadWorkflow(Correspon c) {
        WorkflowDao dao = getDao(WorkflowDao.class);
        List<Workflow> workflows = dao.findByCorresponId(c.getId());
        setupCorresponGroup(workflows);

        c.setWorkflows(workflows);
    }

    private void setupCorresponGroup(List<Workflow> workflows) {
        if (workflows == null) {
            return;
        }
        for (Workflow w : workflows) {
            w.setCorresponGroup(findPrimaryCorresponGroup(w.getUser()));
        }
    }

    /**
     * 指定されたコレポン文書に、現在ログイン中ユーザーがアクセス可能であるか検証する. 不正なアクセスの場合は
     * {@link ServiceAbortException}を発生させ処理を終了する.
     * @param c
     *            コレポン文書
     * @throws ServiceAbortException
     *             不正アクセス
     */
    private void validateAccess(Correspon c) throws ServiceAbortException {
        validateProjectId(c.getProjectId());
        // System Admin / Preparerであればチェックの必要無し
        if (!((isSystemAdmin(getCurrentUser()) || (isPreparer(c.getCreatedBy().getEmpNo()))))) {
            validateWorkflowStatus(c);
        }
    }

    /**
     * 指定されたコレポン文書が、ログインユーザーがアクセス可能な状態であるか検証する. 不正なアクセスの場合は
     * {@link ServiceAbortException}を発生させ処理を終了する. <h2>前提!!</h2>
     * <ul>
     * <li>ユーザーがアクセス可能なプロジェクトのコレポン文書である</li>
     * </ul>
     * @param c
     *            コレポン文書
     * @throws ServiceAbortException
     *             不正アクセス
     */
    private void validateWorkflowStatus(Correspon c) throws ServiceAbortException {
        WorkflowStatus status = c.getWorkflowStatus();
        doValidateWorkflowStatus(c, status);
    }

    /**
     * 承認状態のチェックを行う.
     * @param c
     *            コレポン文書
     * @param status
     *            承認状態
     * @throws ServiceAbortException
     *             アクセス不可
     */
    private void doValidateWorkflowStatus(Correspon c, WorkflowStatus status)
        throws ServiceAbortException {
        switch (status) {
        // 発行済文書は誰でも見られる(同一プロジェクト内の前提あり)
        case ISSUED:
            break;
        // 作成中文書は他人に見られてはいけない
        case DRAFT:
            if (!isPreparer(c.getCreatedBy().getEmpNo())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
            }
            break;
        default:
            // Project Admin / 送信元活動単位のGroup Adminであればチェックの必要無し
            if (!(isProjectAdmin(getCurrentUser(), c.getProjectId())
                  || isGroupAdmin(getCurrentUser(), c.getFromCorresponGroup().getId()))) {
                // workflowによるチェック
                validateWorkflow(c);
            }
        }
    }

    /**
     * 承認フローによるチェックを行う.
     * @param c
     *            コレポン文書
     * @throws ServiceAbortException
     *             アクセス不可
     */
    private void validateWorkflow(Correspon c) throws ServiceAbortException {
        // 承認フローに設定されているか否かを確認
        // 未設定であればアクセス不可、設定中であれば承認作業状態に応じて
        // アクセス可能であるか否かを判定
        Workflow workflow = findMyWorkflow(c);
        if (workflow == null) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
        if (workflow.isChecker()
            && workflow.getWorkflowProcessStatus() == WorkflowProcessStatus.NONE) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID);
        }
        if (workflow.isApprover()
            && c.getCorresponType().getAllowApproverToBrowse() == AllowApproverToBrowse.INVISIBLE
            && workflow.getWorkflowProcessStatus() == WorkflowProcessStatus.NONE) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_WORKFLOW_STATUS_INVALID);
        }
    }

    /**
     * コレポン文書に設定されている承認フロー中に ログインユーザーが設定されていればその情報を返す.
     * @param c
     *            コレポン文書
     * @return 承認フロー情報. ログインユーザーが承認フローに設定されていない場合はnull
     */
    private Workflow findMyWorkflow(Correspon c) {
        User me = getCurrentUser();
        for (Workflow wf : c.getWorkflows()) {
            if (me.getEmpNo().equals(wf.getUser().getEmpNo())) {
                return wf;
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#findAttachment(java
     * .lang.Long, java.lang.Long)
     */
    @Transactional(readOnly = true)
    public Attachment findAttachment(Long corresponId, Long attachmentId)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponId);
        ArgumentValidator.validateNotNull(attachmentId);
        String filePath = null;
        // アクセス権チェックのため、コレポン文書を取得
        // 例外が発生しなければアクセス可能とみなす
        find(corresponId);
        AttachmentDao dao = getDao(AttachmentDao.class);
        try {
            Attachment attachment = dao.findById(attachmentId);
            FileStoreClient client = getFileStoreClient();
            filePath = createTemporaryFileName();
            attachment.setContent(client.getFileContent(attachment.getFileId(), filePath));

            return attachment;
        } catch (RecordNotFoundException e) {
            throw new ServiceAbortException(e);
        } catch (FileStoreException e) {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPLOADING_FILE);
        } finally {
            // ファイルを削除する
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists()) {
                    if (!file.delete()) {
                        log.warn("failed in the deletion of the temporary attached file(" + filePath
                            + ").");
                    }
                }
            }
        }
    }

    private String createTemporaryFileName() {
        String id = String.valueOf(Thread.currentThread().getId());
        String result = AttachmentUtil.createFileName(id, "download.tmp");
        log.debug("tempfile : {}", result);
        return result;
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#findReplyCorrespons
     * (java.lang.Long)
     */
    public List<Correspon> findReplyCorrespons(Correspon parentCorrespon, Long groupId)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(parentCorrespon);
        ArgumentValidator.validateNotNull(groupId);

        // 親コレポン文書へのアクセス可否を検証後、返信文書を取得
        validateAccess(parentCorrespon);
        return findReplyCorrespons(parentCorrespon.getId(), groupId);
    }

    private List<Correspon> findReplyCorrespons(Long corresponId, Long groupId)
        throws ServiceAbortException {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findReplyCorresponByGroupId(corresponId, groupId);
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#requestForVerification
     * (jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    @Transactional(readOnly = true)
    public void requestForApproval(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);

        validateProjectId(correspon.getProjectId());
        validateForRequestForVerification(correspon);
        correspon.setRequestedApprovalAt(DateUtil.getNow());
        updateForRequestForApproval(correspon);
        sendWorkflowNotice(correspon, EmailNoticeEventCd.REQUESTED_FOR_APPROVAL);
    }

    /**
     * 検証・承認依頼のためのチェック.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             チェックで不正あり
     */
    private void validateForRequestForVerification(Correspon correspon)
        throws ServiceAbortException {
        // 承認状態がDRAFT以外はエラー
        if (WorkflowStatus.DRAFT != correspon.getWorkflowStatus()) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        }
        // 検証・承認依頼を実行できるのはPreparer/System Adminだけ
        User loginUser = getCurrentUser();
        if (!(isSystemAdmin(loginUser) || isPreparer(correspon.getCreatedBy().getEmpNo()))) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
        // Approverが設定されていない場合はエラー
        List<Workflow> workflow = correspon.getWorkflows();
        if (workflow == null || !existApprover(workflow)) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_APPROVER_SPECIFIED);
        }
    }

    /**
     * 検証・承認依頼のためレコードを更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             更新に失敗
     */
    private void updateForRequestForApproval(Correspon correspon) throws ServiceAbortException {
        Correspon corresponUpdate;
        List<Workflow> workflow = correspon.getWorkflows();
        corresponUpdate = setUpCorresponForRequestForApproval(correspon, workflow);
        updateCorrespon(corresponUpdate);
        // 承認フローを承認パターン別で更新する
        updateWorkflows(correspon);
    }

    /**
     * 承認フローにCheckerが存在するか判定する.
     * @param workflow
     *            承認フローリスト
     * @return 承認フローにCheckerが存在したらtrue/存在しなかったらfalse
     */
    private boolean existChecker(List<Workflow> workflow) {
        for (Workflow w : workflow) {
            if (WorkflowType.CHECKER == w.getWorkflowType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 承認フローにApproverが存在するか判定する.
     * @param workflow
     *            承認フローリスト
     * @return 承認フローにApproverが存在したらtrue/存在しなかったらfalse
     */
    private boolean existApprover(List<Workflow> workflow) {
        for (Workflow w : workflow) {
            if (WorkflowType.APPROVER == w.getWorkflowType()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 承認フローをパターン別に更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             更新に失敗
     */
    private void updateWorkflows(Correspon correspon) throws ServiceAbortException {
        // 承認フローのパターン取得
        WorkflowPattern pattern = correspon.getCorresponType().getWorkflowPattern();
        List<Workflow> workflow = correspon.getWorkflows();
        if (SystemConfig.getValue("workflow.pattern.1").equals(pattern.getWorkflowCd())) {
            // [承認パターン1]
            // Checkerが一人以上設定されている場合
            // 先頭のCheckerのみ、検証を依頼する
            // Checkerが設定されていない場合
            // Approverに承認を依頼する
            createWorkflowPattern1(workflow);
        } else if (SystemConfig.getValue("workflow.pattern.2").equals(pattern.getWorkflowCd())) {
            // [承認パターン2]
            // 全てのCheckerに検証を依頼する
            createWorkflowPattern2(workflow);
        } else if (SystemConfig.getValue("workflow.pattern.3").equals(pattern.getWorkflowCd())) {
            // [承認パターン3]
            // 全てのCheckerに検証を依頼する
            // Approverに承認を依頼する
            createWorkflowPattern3(workflow);
        } else {
            // 承認フローパターンが取得できない
            throw new ApplicationFatalRuntimeException("WorkflowPattern Invalid "
                + pattern.getWorkflowCd());
        }
    }

    /**
     * 承認フローを更新する(パターン1).
     * @param workflow
     *            承認フローリスト
     * @throws ServiceAbortException
     */
    private void createWorkflowPattern1(List<Workflow> workflow) throws ServiceAbortException {

        Workflow updateWorkflow;

        for (Workflow w : workflow) {
            WorkflowProcessStatus processStatus = w.getWorkflowProcessStatus();
            if (WorkflowProcessStatus.NONE == processStatus) {
                updateWorkflow = new Workflow();
                if (WorkflowType.CHECKER == w.getWorkflowType()) {
                    updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                    updateWorkflow
                        .setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
                    updateWorkflow(updateWorkflow);
                    break;
                } else {
                    updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                    updateWorkflow
                        .setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
                    updateWorkflow(updateWorkflow);
                    break;
                }
            }
        }
    }

    /**
     * 承認フローを更新する(パターン2).
     * @param workflow
     *            承認フローリスト
     * @throws ServiceAbortException
     */
    private void createWorkflowPattern2(List<Workflow> workflow) throws ServiceAbortException {

        Workflow updateWorkflow;

        boolean existChecker = false;
        for (Workflow w : workflow) {
            updateWorkflow = new Workflow();
            if (WorkflowType.CHECKER == w.getWorkflowType()) {
                existChecker = true;
                updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                updateWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
                updateWorkflow(updateWorkflow);
            }
            // Checkerが存在しない場合はApproverに承認依頼する
            if (!existChecker && WorkflowType.APPROVER == w.getWorkflowType()) {
                updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                updateWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
                updateWorkflow(updateWorkflow);
            }
        }
    }

    /**
     * 承認フローを更新する(パターン3).
     * @param workflow
     *            承認フローリスト
     * @throws ServiceAbortException
     */
    private void createWorkflowPattern3(List<Workflow> workflow) throws ServiceAbortException {

        Workflow updateWorkflow;

        for (Workflow w : workflow) {
            updateWorkflow = new Workflow();
            if (WorkflowType.CHECKER == w.getWorkflowType()) {
                updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                updateWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_CHECK);
                updateWorkflow(updateWorkflow);
            } else {
                updateWorkflow = setUpWorkflowForRequestForVerification(w, updateWorkflow);
                updateWorkflow.setWorkflowProcessStatus(WorkflowProcessStatus.REQUEST_FOR_APPROVAL);
                updateWorkflow(updateWorkflow);
            }
        }
    }

    /**
     * 承認フローを更新する.
     * @param workflow
     *            承認フロー
     * @throws ServiceAbortException
     */
    private void updateWorkflow(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.update(workflow);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * コレポン文書を更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     */
    private void updateCorrespon(Correspon correspon) throws ServiceAbortException {
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.update(correspon);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 検証・承認を依頼する処理に必要な値のみの承認フローを作成する.
     * @param workflow
     *            承認フロー
     * @param updateWorkflow
     *            更新用ワークフロー
     * @return 検証・承認を依頼する処理に必要な値のみの承認フロー
     */
    private Workflow setUpWorkflowForRequestForVerification(Workflow workflow,
        Workflow updateWorkflow) {
        updateWorkflow.setId(workflow.getId());
        updateWorkflow.setUpdatedBy(getCurrentUser());
        updateWorkflow.setWorkflowProcessStatus(workflow.getWorkflowProcessStatus());
        updateWorkflow.setVersionNo(workflow.getVersionNo());

        return updateWorkflow;
    }

    /**
     * 検証・承認を依頼する処理に必要な値のみのコレポン文書を作成する.
     * @param correspon
     *            コレポン文書
     * @param workflow
     *            承認フローリスト
     * @return 検証・承認を依頼する処理に必要な値のみのコレポン文書
     */
    private Correspon setUpCorresponForRequestForApproval(Correspon correspon,
        List<Workflow> workflow) {
        Correspon requestForApprovalCorrespon = new Correspon();
        // 承認フローにCheckerが１人以上設定されている場合、コレポン文書の承認状態を[Request for Check]で更新する。
        if (existChecker(workflow)) {
            requestForApprovalCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_CHECK);
        } else {
            requestForApprovalCorrespon.setWorkflowStatus(WorkflowStatus.REQUEST_FOR_APPROVAL);
        }
        requestForApprovalCorrespon.setId(correspon.getId());
        requestForApprovalCorrespon.setUpdatedBy(getCurrentUser());
        requestForApprovalCorrespon.setVersionNo(correspon.getVersionNo());
        requestForApprovalCorrespon.setRequestedApprovalAt(correspon.getRequestedApprovalAt());
        return requestForApprovalCorrespon;
    }

    /**
     * 存在チェック.
     * @param hashSet
     *            ハッシュ
     * @param target
     *            ターゲット
     * @return boolean
     */
    public boolean containHash(HashSet<String> hashSet, String target) {
        return hashSet.contains(target);
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #generateHTML(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public byte[] generateHTML(Correspon correspon,
        List<CorresponResponseHistoryModel> corresponResponseHistory) throws ServiceAbortException {
        return generateHTML(correspon, corresponResponseHistory, true);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #generateHTML(jp.co.opentone.bsol.linkbinder.dto.Correspon, java.util.List, boolean)
     */
    public byte[] generateHTML(Correspon correspon,
        List<CorresponResponseHistoryModel> corresponResponseHistory, boolean usePersonInCharge)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        try {
            HTMLGenerator generator =
                    new HTMLGenerator(SystemConfig.getValue(TEMPLATE_KEY_HTML), correspon);
            CorresponHTMLGeneratorUtil util = new CorresponServiceGeneratorUtil();
            util.setCorresponResponseHistoryModel(corresponResponseHistory);
            util.setBaseURL(getBaseURL());
            util.setContextURL(getContextURL());
            CorresponPageFormatter corresponPageFormatter = new CorresponPageFormatter();
            util.setIconName(corresponPageFormatter.getProjectLogoUrl(correspon.getProjectId()));
            util.setStylesheetName(SystemConfig.getValue(TEMPLATE_KEY_STYLESHEET));
            util.setUsePersonInCharge(usePersonInCharge);
            generator.setUtil(util);
            return generator.generate();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED, e
                .getMessage());
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#
     * delete(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public void delete(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);

        // TODO;CorresponSearchServiceImpl.javaに同処理のコードあり.共通化を考える
        // チェック定義書に定義された(CorresponService#delete)チェックを行う。
        validateDeleteIssuePermission(correspon);
        // 更新
        deleteCorrespon(createDeletedCorrespon(correspon));
    }

    /**
     * コレポン文書を削除/発行する権限をチェックする.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             権限エラー
     */
    private void validateDeleteIssuePermission(Correspon correspon) throws ServiceAbortException {

        if (!WorkflowStatus.DRAFT.equals(correspon.getWorkflowStatus())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        } else if (!isSystemAdmin(getCurrentUser())
            && !getCurrentUser().getEmpNo().equals(correspon.getCreatedBy().getEmpNo())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 文書状態削除用のオブジェクトを作成する.
     * @param old
     *            削除対象のコレポン文書
     * @return 削除用のオブジェクト
     */
    private Correspon createDeletedCorrespon(Correspon old) {

        Correspon correspon = new Correspon();
        correspon.setId(old.getId());
        correspon.setUpdatedBy(getCurrentUser());
        correspon.setVersionNo(old.getVersionNo());
        return correspon;
    }

    /**
     * コレポン文書を削除する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             削除に失敗
     */
    private void deleteCorrespon(Correspon correspon) throws ServiceAbortException {

        try {
            CorresponDao dao = getDao(CorresponDao.class);
            dao.delete(correspon);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#issue(
     * jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public void issue(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);

        // TODO;CorresponWorkflowServiceImpl.javaに同処理のコードあり.共通化を考える
        // (checkForceToUseWorkflow以外)
        // プロジェクトのチェック
        validateProjectId(correspon.getProjectId());
        // チェック定義書に定義された(CorresponService#issue)チェックを行う。
        if (!isOptional(correspon)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
        // チェック定義書に定義された(CorresponService#issue)チェックを行う。
        validateDeleteIssuePermission(correspon);
        // 承認状態の更新
        updateCorresponForIssue(correspon);

        if (correspon.getForLearning() == ForLearning.LEARNING) {
            Correspon clone = new Correspon();
            try {
                CorresponDao dao = getDao(CorresponDao.class);
                Correspon originalCorrespon = dao.findById(correspon.getId());
                PropertyUtils.copyProperties(clone, originalCorrespon);
            } catch(NoSuchMethodException e) {
                throw new ServiceAbortException(e.getMessage());
            } catch (InvocationTargetException e) {
                throw new ServiceAbortException(e.getMessage());
            } catch (IllegalAccessException e) {
                throw new ServiceAbortException(e.getMessage());
            } catch (RecordNotFoundException e) {

            }
            copyCorresponForLearning(clone);
        }
        // メール通知機能
        sendIssuedNotice(correspon, EmailNoticeEventCd.ISSUED);
    }

    /**
     * コレポン文書の承認パターン適用フラグをチェックする.
     * @param correspon
     *            コレポン文書
     * @return boolean true:任意 / false:必須
     * @throws ServiceAbortException
     *             チェックエラー
     */
    private boolean isOptional(Correspon correspon) throws ServiceAbortException {
        return ForceToUseWorkflow.OPTIONAL.equals(correspon.getCorresponType()
            .getForceToUseWorkflow());
    }

    /**
     * コレポン文書の承認状態を更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     */
    private void updateCorresponForIssue(Correspon correspon) throws ServiceAbortException {
        Correspon newCorrespon = createNewCorrespon(correspon, WorkflowStatus.ISSUED);
        // コレポン文書番号を生成
        newCorrespon.setCorresponNo(corresponSequenceService.getCorresponNo(correspon));
        newCorrespon.setIssuedAt(DateUtil.getNow());
        newCorrespon.setIssuedBy(getCurrentUser());

        // コレポン文書の更新
        updateCorrespon(newCorrespon);
    }

    /**
     * 学習用に設定された文書をコピーする.
     * @param correspon 「学習用コンテンツ」に指定された、対象の文書
     * @throws ServiceAbortException
     */
    private void copyCorresponForLearning(Correspon correspon) throws ServiceAbortException {
        //TODO: 「学習用プロジェクト」に指定されたProjectIDの数だけ文書を複製・登録する処理を追加する
        // ここでリストのIDに紐づく文書としてコピーする処理
        List<Project> learningProjectList = findLearningProject();
        for(Project learningProject : learningProjectList) {
            correspon.setProjectId(learningProject.getProjectId());
            try {
                saveLearningCorrespon(correspon);
            } catch (KeyDuplicateException e) {
                continue;
            }

        }
    }

   /**
     * 更新用のコレポン文書Dtoを生成する.
     * @param oldCorrespon
     *            コレポン文書
     * @param workflowStatus
     *            承認状態
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

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #assignPersonInCharge(jp.co.opentone.bsol.linkbinder.dto.Correspon,
     * jp.co.opentone.bsol.linkbinder.dto.AddressUser, java.util.List)
     */
    public void assignPersonInCharge(Correspon correspon, AddressUser addressUser,
        List<PersonInCharge> pics) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(addressUser);
        ArgumentValidator.validateNotNull(pics);

        // 渡されたAddressUser, PICを検証する
        log.trace("validate");
        validatePersonInCharge(correspon, addressUser, pics);

        // 通知用に現在のPICを取得
        List<PersonInCharge> oldPics = findPersonInChages(addressUser);

        // 登録されているPICを削除
        try {
            log.trace("delete");
            deletePersonInCharge(addressUser);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PERSON_IN_CHARGE_NOT_EXIST);
        }

        // PICを登録
        for (PersonInCharge pic : pics) {
            try {
                log.trace("create");
                createPersionInCharge(addressUser, pic);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            }
        }

        // 追加設定されたPICに対して担当者決定メール通知
        sendPICAssignedEmailNoticeToAdditionalPIC(correspon,
                EmailNoticeEventCd.ASSIGN_TO,
                oldPics,
                pics);
    }

    /**
     * 現在登録されているPICを取得する.
     * @param addressUser PIC登録対象となっているAddress User
     * @return 現在登録中のPIC
     */
    private List<PersonInCharge> findPersonInChages(AddressUser addressUser) {
        PersonInChargeDao dao = getDao(PersonInChargeDao.class);
        return dao.findByAddressUserId(addressUser.getId());
    }

    /**
     * AddressUser, PICを検証する.
     * @param correspon
     *            コレポン文書
     * @param addressUser
     *            AddressUser
     * @param pics
     *            PICのリスト
     * @throws ServiceAbortException
     */
    private void validatePersonInCharge(Correspon correspon, AddressUser addressUser,
        List<PersonInCharge> pics) throws ServiceAbortException {

        for (PersonInCharge pic : pics) {
            try {
                // PICに選択されたユーザが存在しない際はエラー
                findUserByEmpNo(pic.getUser().getEmpNo());
                // PICに選択されたユーザが同一プロジェクトに属していない際はエラー
                validateProjectId(correspon.getProjectId(), pic.getUser());
            } catch (RecordNotFoundException e) {
                log.warn(e.getMessageCode() + " " + pic.getUser());
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PERSON_IN_CHARGE_NOT_EXIST);
            } catch (ServiceAbortException e) {
                log.warn(e.getMessageCode() + " " + pic.getUser());
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_INVALID_PERSON_IN_CHARGE);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #findCorresponResponseHistory(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public List<CorresponResponseHistory> findCorresponResponseHistory(Correspon correspon)
        throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        CorresponDao dao = getDao(CorresponDao.class);

        Long corresponId = correspon.getId();
        Long currentCorresponId = corresponId;
        if (correspon.getParentCorresponId() != null) {
            // 親文書ありの場合、大本のコレポン文書を検索する
            corresponId = dao.findRootCorresponId(correspon.getId());
        }
        return dao.findCorresponResponseHistory(corresponId, currentCorresponId);
    }

    // TODO ここから下のメソッドはCorresopnValidateServiceに同じものがいくつかある.共通化を考える

    /**
     * 指定されたユーザーが参照可能なプロジェクトを返す.
     * @return 指定されたユーザーが参照可能なプロジェクトの一覧
     */
    private List<Project> getAccessibleProjects(User user) {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findByEmpNo(user.getEmpNo());
    }

    /**
     * 指定されたユーザーが指定されたプロジェクトIDを参照可能であるか検証する. (任意のprojectIdに対応)
     * @param projectId
     *            操作対象のプロジェクトID
     * @param user
     *            指定されたユーザー
     * @throws ServiceAbortException
     *             プロジェクトID参照不可
     */
    private void validateProjectId(String projectId, User user) throws ServiceAbortException {
        // System Adminの場合は全プロジェクトを参照可能なのでチェックしない
        if (isSystemAdmin(user)) {
            return;
        }

        boolean isAccessible = false;
        for (Project p : getAccessibleProjects(user)) {
            if (projectId.equals(p.getProjectId())) {
                isAccessible = true;
                break;
            }
        }
        if (!isAccessible) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * ユーザー(User)を返す.
     * @param empNo
     *            従業員番号
     * @return ユーザー
     * @throws ServiceAbortException
     */
    private User findUserByEmpNo(String empNo) throws RecordNotFoundException {
        UserDao dao = getDao(UserDao.class);
        return dao.findByEmpNo(empNo);
    }

    /**
     * 条件に一致するPerson In Chargeを削除する.
     * @param addressUser
     *            宛先-ユーザー
     * @return
     * @throws StaleRecordException
     * @throws KeyDuplicateException
     */
    private int deletePersonInCharge(AddressUser addressUser) throws KeyDuplicateException,
        StaleRecordException {
        PersonInCharge personInCharge = new PersonInCharge();
        personInCharge.setAddressUserId(addressUser.getId());
        personInCharge.setUpdatedBy(getCurrentUser());
        PersonInChargeDao dao = getDao(PersonInChargeDao.class);
        return dao.deleteByAddressUserId(personInCharge);
    }

    /**
     * Person In Chargeを登録する.
     * @param addressUser
     *            宛先-ユーザー
     * @param persionInCharge
     *            Person In Charge
     * @return
     * @throws KeyDuplicateException
     */
    private Long createPersionInCharge(AddressUser addressUser, PersonInCharge persionInCharge)
        throws KeyDuplicateException {
        if (log.isDebugEnabled()) {
            log.debug("addressUser.id[" + addressUser.getId() + "]");
            log.debug(persionInCharge.getUser() + "");
        }
        // 登録内容の準備
        PersonInCharge p = new PersonInCharge();
        p.setAddressUserId(addressUser.getId());
        p.setUser(persionInCharge.getUser());
        p.setCreatedBy(getCurrentUser());
        p.setUpdatedBy(getCurrentUser());

        // 登録
        PersonInChargeDao dao = getDao(PersonInChargeDao.class);
        return dao.create(p);
    }

    /**
     * 文書状態を更新する.
     * @param correspon
     *            コレポン文書
     * @param status
     *            コレポン文書の文書状態(これに更新する)
     * @throws ServiceAbortException
     *             文書状態更新で例外発生
     */
    public void updateCorresponStatus(Correspon correspon, CorresponStatus status)
        throws ServiceAbortException {
        log.trace("★文書状態を更新");

        // 文書状態チェック(チェック定義書)
        commonValidateCorresponStatus(correspon);

        // 文書状態チェック(画面仕様)
        // チェック定義書のチェックがあるためCanceledのコレポン文書はここまで到達しない
        log.trace("★権限チェック");
        if (!isSystemAdmin(getCurrentUser())
            && !isProjectAdmin(getCurrentUser(), correspon.getProjectId())
            && !isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
            && !isPreparer(correspon.getCreatedBy().getEmpNo()) && !isChecker(correspon)
            && !isApprover(correspon)) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT + " "
                + getCurrentUser() + " " + correspon);
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }

        // 値設定
        correspon.setCorresponStatus(status);
        correspon.setUpdatedBy(getCurrentUser());

        log.trace("★文書状態更新実行");
        // 実際に更新を行う
        updateCorrespon(correspon);

        // コレポン文書を更新した為、既読→未読に変更
        if (CorresponStatus.CANCELED == status) {
            corresponReadStatusService.updateReadStatusByCorresponId(
                        correspon.getId(), ReadStatus.NEW);
        }
    }

    /**
     * 文書状態(Open/Closed/Canceled)チェック.
     * <p>
     * チェック定義書の「文書状態(Open/Closed/Canceled)チェック」に記述された一連のチェックを行う<br />
     * 【新規追加】ただし、新規登録の際の文書状態チェックは「Canceledか否か」のチェックのみ行う
     * </p>
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException
     */
    // TODO CorresponValidateService#commonValidateCorresponStatusをそのままコピーした.
    // 要共通化.
    private void commonValidateCorresponStatus(Correspon correspon) throws ServiceAbortException {
        if (correspon.isNew()) { // コレポン文書の文書状態が[2:Canceled]の際はエラー
            log.trace("★文書状態(新規)");
            if (log.isDebugEnabled()) {
                log.debug("correspon.getCorresponStatus[" + correspon.getCorresponStatus() + "]");
            }
            validateCorresponStatusCanceled(correspon.getCorresponStatus());
        } else { // 更新前コレポン文書の文書状態が[2:Canceled]の際はエラー
            log.trace("★文書状態(更新)①");
            if (log.isDebugEnabled()) {
                log.debug("correspon.getId[" + correspon.getId() + "]");
            }
            Correspon oldCorrespon = null;
            try {
                oldCorrespon = findCorrespon(correspon.getId());
            } catch (RecordNotFoundException e) {
                log.warn(e.getMessageCode() + " correspon.getId[" + correspon.getId() + "]");
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            if (log.isDebugEnabled()) {
                log.debug("oldCorrespon.getCorresponStatus[" + oldCorrespon.getCorresponStatus()
                    + "]");
            }
            if (CorresponStatus.CANCELED.equals(oldCorrespon.getCorresponStatus())) {
                log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED + " "
                    + oldCorrespon);
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED);
            }
            // コレポン文書の承認状態が[5:Issued]以外で、文書状態を[2:Canceled]にする際はエラー
            log.trace("★文書状態(更新)②");
            if (log.isDebugEnabled()) {
                log.debug("oldCorrespon.getWorkflowStatus[" + oldCorrespon.getWorkflowStatus()
                    + "]");
                log.debug("correspon.getCorresponStatus[" + correspon.getCorresponStatus() + "]");
            }
            if (!WorkflowStatus.ISSUED.equals(oldCorrespon.getWorkflowStatus())
                && CorresponStatus.CANCELED.equals(correspon.getCorresponStatus())) {
                log
                    .warn(ApplicationMessageCode
                        .CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED
                        + " " + oldCorrespon);
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_ISSUED_AND_CANCELED);
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#savePartial(
     *      jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public void savePartial(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        // 更新できる一般ユーザーは対象コレポン文書Preperer,Checker,Approverに限る
        validateSavePartialPermission(correspon);
        // 発行状態がISSUEDじゃないと駄目
        validateSavePartialWorkflowStatus(correspon);
        // 文書状態がCanceledは駄目
        validateCorresponStatusCanceled(correspon.getCorresponStatus());
        // 更新する
        updateCorrespon(createSavePatialCorrespon(correspon));
        // コレポン文書を更新した為、既読→未読に変更
        corresponReadStatusService.updateReadStatusByCorresponId(
                    correspon.getId(), ReadStatus.NEW);
    }

    /**
     * 文書状態がCanceledかチェックする.
     * @param corresponStatus
     *            文書状態
     * @throws ServiceAbortException
     *             文書状態がCanceled
     */
    private void validateCorresponStatusCanceled(CorresponStatus corresponStatus)
        throws ServiceAbortException {
        if (CorresponStatus.CANCELED == corresponStatus) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_CANCELED);
        }
    }

    /**
     * 返信要否、期限のみ更新処理の発行状態をチェックする.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             発行状態が期待値ではない
     */
    private void validateSavePartialWorkflowStatus(Correspon correspon)
        throws ServiceAbortException {
        if (correspon.getWorkflowStatus() != WorkflowStatus.ISSUED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_STATUS_INVALID);
        }
    }

    /**
     * 返信要否、期限のみ更新処理の権限をチェックする.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             権限がない
     */
    private void validateSavePartialPermission(Correspon correspon) throws ServiceAbortException {
        User loginUser = getCurrentUser();
        String projectId = getCurrentProjectId();
        if (!isSystemAdmin(loginUser)
            && !isProjectAdmin(loginUser, projectId)
            && !isGroupAdmin(loginUser, correspon.getFromCorresponGroup().getId())
            && !isPreparer(correspon.getCreatedBy().getEmpNo())
            && !isChecker(correspon) && !isApprover(correspon)) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT);
        }
    }

    /**
     * 返信要否、期限のみ更新用のコレポン文書オブジェクトを作成する.
     * @param correspon
     *            コレポン文書
     * @return 更新用コレポン文書
     */
    private Correspon createSavePatialCorrespon(Correspon correspon) {
        Correspon savePatialCorrespon = new Correspon();
        // 返信要否、期限のみ更新する
        savePatialCorrespon.setReplyRequired(correspon.getReplyRequired());
        if (correspon.getDeadlineForReply() == null) {
            savePatialCorrespon.setDeadlineForReply(DBValue.DATE_NULL);
        } else {
            savePatialCorrespon.setDeadlineForReply(correspon.getDeadlineForReply());
        }
        savePatialCorrespon.setId(correspon.getId());
        savePatialCorrespon.setUpdatedBy(getCurrentUser());
        savePatialCorrespon.setVersionNo(correspon.getVersionNo());
        return savePatialCorrespon;
    }

    /*
     * (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #generateZip(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public byte[] generateZip(Correspon correspons) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspons);
        return generateZip(correspons, true);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService
     * #generateZip(jp.co.opentone.bsol.linkbinder.dto.Correspon, boolean)
     */
    public byte[] generateZip(Correspon correspons, boolean usePersonInCharge)
        throws ServiceAbortException {
        try {
            ZipArchiver zip = new ZipArchiver();
            // 指定のコレポン文書を取得 -- 存在しない場合はエラー
            Correspon correspon = findCorresponDetail(correspons.getId());

            // 応答履歴を取得し、表示用のビーンに詰める
            List<CorresponResponseHistory> histories =
                findCorresponResponseHistory(correspon);

            List<CorresponResponseHistoryModel> historyModels =
                new ArrayList<CorresponResponseHistoryModel>();

            for (CorresponResponseHistory history : histories) {
                CorresponResponseHistoryModel model = new CorresponResponseHistoryModel();
                model.setCorresponResponseHistory(history);
                historyModels.add(model);
            }

            // HTMLに変換を行う
            HTMLGenerator generator =
                new HTMLGenerator(SystemConfig.getValue(TEMPLATE_KEY_ZIP), correspon);
            CorresponHTMLGeneratorUtil util = new CorresponServiceGeneratorUtil(true);
            util.setCorresponResponseHistoryModel(historyModels);
            util.setBaseURL(getBaseURL());
            util.setContextURL(getContextURL());
            CorresponPageFormatter corresponPageFormatter = new CorresponPageFormatter();
            util.setIconName(
                corresponPageFormatter.getProjectLogoUrl(correspon.getProjectId()));
            util.setStylesheetName(SystemConfig.getValue(TEMPLATE_KEY_STYLESHEET));
            util.setUsePersonInCharge(usePersonInCharge);
            generator.setUtil(util);
            zip.add(createCorresponHtmlName(correspon), generator.generate());

            // コレポン文書に付随しているAttachMentを取得
            for (Attachment attachment : correspons.getAttachments()) {
                zip.add(
                    createAttachmentFileName(correspon, attachment.getFileName()),
                    new SavedAttachmentInfo(attachment, this).getContent());
            }

            return zip.toByte();
        } catch (GeneratorFailedException e) {
            throw new ServiceAbortException(e.getMessage(), e, MessageCode.E_GENERATION_FAILED,
                                            e.getMessage());
        }
    }

    /**
     * ZIPファイル内のHTMLファイルの名前を作成する.
     * <pre>
     * ファイル名：
     *     projectId_コレポン文書番号(コレポン文書ID).html
     *     ただし未発行の場合はprojectId_(コレポン文書ID).html
     *         ・コレポン文書IDは10桁前ゼロ埋め
     *         ・コレポン文書番号の「:」はWindowsのファイル名として使用できない文字なので、「-」に変換
     *         ・その他Windowsのファイル名として使用できない文字があれば「-」に変換
     * </pre>
     * @return HTMLファイル名
     */
    private String createCorresponHtmlName(Correspon correspon) {
        String corresponNo = correspon.getCorresponNo();
        if (corresponNo == null) { // NULLならば空文字に変換
            corresponNo = "";
        }
        String corresponId = String.format(FILENAME_ID_FORMAT, correspon.getId());

        String fileName = String.format(FILENAME_FORMAT,
                                        getCurrentProjectId(),
                                        corresponNo,
                                        corresponId);
        return convertFileName(fileName);
    }

    /**
     * ディレクトリ名を作成する.
     * @return ATTACHMENT名
     */
    private String createAttachmentFileName(Correspon correspon, String filename) {
        String corresponNo = correspon.getCorresponNo();
        if (corresponNo == null) { // NULLならば空文字に変換
            corresponNo = "";
        }
        String corresponId = String.format(FILENAME_ID_FORMAT, correspon.getId());

        String fileName = String.format(ATTACHMENT_FILENAME_FORMAT,
                                        getCurrentProjectId(),
                                        corresponNo,
                                        corresponId,
                                        filename);
        return convertFileName(fileName);
    }

    /**
     * Windowsのファイル名として使用できない文字を変換する.
     * @return ファイル名
     */
    private String convertFileName(String name) {
        String regex = SystemConfig.getValue(FILENAME_KEY_REGEX);
        if (StringUtils.isEmpty(regex)) {
            regex = FILENAME_DEFAULT_REGEX;
        }
        String replacement = SystemConfig.getValue(FILENAME_KEY_REPLACEMENT);
        if (replacement == null) { // 置換文字は空文字でも問題ない
            replacement = FILENAME_DEFAULT_REPLACEMENT;
        }
        return name.replaceAll(regex, replacement);
    }

    /**
     * コレポン文書の情報を関連する情報と一緒に取得する.
     * @throws ServiceAbortException 取得エラー
     */
    private Correspon findCorresponDetail(Long id) throws ServiceAbortException {
        try {
            Correspon clone = new Correspon();
            Correspon original = find(id);
            PropertyUtils.copyProperties(clone, original);
            clone.setWorkflows(createDisplayWorkflowList(original));
            return clone;
        } catch (ServiceAbortException e) {
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        } catch (IllegalAccessException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new ServiceAbortException(e.getMessage());
        }
    }

    private void sendWorkflowNotice(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd) throws ServiceAbortException {
        Correspon updatedCorrespon = find(correspon.getId());
        emailNotificationService.sendWorkflowNotice(updatedCorrespon,
                emailNoticeEventCd);
    }

    private void sendIssuedNotice(Correspon correspon, EmailNoticeEventCd emailNoticeEventCd)
            throws ServiceAbortException {
        Correspon updatedCorrespon = find(correspon.getId());
        emailNotificationService.sendIssuedNotice(updatedCorrespon, emailNoticeEventCd, null);
    }

    private void sendPICAssignedEmailNoticeToAdditionalPIC(Correspon correspon,
            EmailNoticeEventCd emailNoticeEventCd, List<PersonInCharge> oldPics,
            List<PersonInCharge> newPics)
            throws ServiceAbortException {
        Correspon updatedCorrespon = find(correspon.getId());
        emailNotificationService.sendPICAssignedEmailNoticeToAdditionalPIC(updatedCorrespon,
                emailNoticeEventCd,
                oldPics,
                newPics);
    }

    /* (非 Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon
     *      .CorresponService#findAttachments(java.lang.Long)
     */
    @Override
    public List<Attachment> findAttachments(Long corresponId) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(corresponId);
        AttachmentDao dao = getDao(AttachmentDao.class);
        try {
            List<Attachment> result = dao.findByCorresponId(corresponId);
            result.forEach(a -> {
                FileStoreClient client = getFileStoreClient();
                String filePath = createTemporaryFileName();
                try {
                    a.setContent(client.getFileContent(a.getFileId(), filePath));
                } finally {
                    new File(filePath).delete();
                }
            });

            return result;
        } catch (FileStoreException e) {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_UPLOADING_FILE);
        }
    }

    private List<Project> findLearningProject() {
        SearchProjectCondition condition = new SearchProjectCondition();
        condition.setForLearning(ForLearning.LEARNING);

        ProjectDao dao = getDao(ProjectDao.class);
        List<Project> learningPjList = dao.findLearningPj(condition);

        return learningPjList;
    }

    private void saveLearningCorrespon(Correspon correspon) throws KeyDuplicateException {
        CorresponDao dao = getDao(CorresponDao.class);
        Long copyLearningCorresponId = dao.create(correspon);
    }
}
