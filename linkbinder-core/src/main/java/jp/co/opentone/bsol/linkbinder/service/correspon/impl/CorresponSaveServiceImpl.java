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

import jp.co.opentone.bsol.framework.core.dao.KeyDuplicateException;
import jp.co.opentone.bsol.framework.core.dao.StaleRecordException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreClient;
import jp.co.opentone.bsol.framework.core.filestore.FileStoreException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.AddressUserDao;
import jp.co.opentone.bsol.linkbinder.dao.AttachmentDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponCustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponHierarchyDao;
import jp.co.opentone.bsol.linkbinder.dao.PersonInChargeDao;
import jp.co.opentone.bsol.linkbinder.dao.WorkflowDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponCustomField;
import jp.co.opentone.bsol.linkbinder.dto.CorresponHierarchy;
import jp.co.opentone.bsol.linkbinder.dto.PersonInCharge;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.Workflow;
import jp.co.opentone.bsol.linkbinder.dto.code.EmailNoticeEventCd;
import jp.co.opentone.bsol.linkbinder.dto.code.ForLearning;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowProcessStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponSaveService;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.service.notice.EmailNoticeService;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
/**
 * このサービスではコレポン文書に関する処理を提供する.
 * @author opentone
 */
@Service
@Transactional(rollbackFor = { ServiceAbortException.class })
public class CorresponSaveServiceImpl extends AbstractService implements CorresponSaveService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4004774537778286277L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(CorresponSaveServiceImpl.class);

    /**
     * 承認状態、承認作業状態を制御するクラス.
     */
    @Resource
    private CorresponStatusControl corresponStatusControl;

    /**
     * 通知関連サービス.
     */
    @Resource
    private EmailNoticeService emailNoticeService;

    /**
     * コレポン文書サービス.
     */
    @Resource
    private CorresponService corresponService;

    /**
     * 空のインスタンスを生成する.
     */
    public CorresponSaveServiceImpl() {
    }

    /**
     * 承認フローを更新する.
     * @param workflow
     *            承認フロー
     * @throws ServiceAbortException
     */
    private void updateWorkflow(Workflow workflow) throws ServiceAbortException {
        try {
            // 排他制御を通過した場合はワークフローの更新処理へ
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
     * 承認フローを更新する.
     * @param workflow
     *            承認フロー
     * @throws ServiceAbortException
     */
    private void updateWorkflowByCorresponId(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.updateByCorresponId(workflow);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 更新用フローの作成
     * Update(Deniedの編集).
     *
     * @param correspon コレポン文書
     *
     * @return workflow ワークフロー
     *
     * @throws ServiceAbortException
     */
    private Workflow setUpWorkflowCaseDenied(
        Correspon correspon,
        WorkflowProcessStatus wfProcessStatus
    )
    throws ServiceAbortException {

        Workflow workflow = new Workflow();
        workflow.setCorresponId(correspon.getId());
        workflow.setWorkflowProcessStatus(wfProcessStatus);
        User user = new User();
        user.setEmpNo(DBValue.STRING_NULL);
        workflow.setFinishedBy(user);
        workflow.setFinishedAt(DBValue.DATE_NULL);
        workflow.setUpdatedBy(getCurrentUser());

        return workflow;

    }

    /**
     * 更新用フローの作成.
     *
     * @param correspon コレポン文書
     *
     * @return workflow ワークフロー
     *
     * @throws ServiceAbortException
     */
    private Workflow setUpWorkflow(Correspon correspon, WorkflowProcessStatus wfProcessStatus)
    throws ServiceAbortException {

        Workflow workflowForUpdate = new Workflow();

        List<Workflow> workflows = correspon.getWorkflows();
        for (Workflow w : workflows) {
            if (w.getUser().getEmpNo().equals(getCurrentUser().getEmpNo())) {
                workflowForUpdate.setId(w.getId());
                workflowForUpdate.setWorkflowProcessStatus(wfProcessStatus);
                workflowForUpdate.setUpdatedBy(getCurrentUser());
                workflowForUpdate.setVersionNo(w.getVersionNo());
            }
        }
        return workflowForUpdate;
    }

    /**
     * コレポン文書を作成する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     */
    private Long createCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("①コレポン文書(correspon)");
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + correspon);
            }
            return dao.create(correspon);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * コレポン文書を更新する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     */
    private void updateCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("①コレポン文書(correspon)");
        try {
            CorresponDao dao = getDao(CorresponDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + correspon);
            }
            dao.update(correspon);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 添付ファイルを更新する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException
     */
    private void updateAttachment(Attachment attachment) throws ServiceAbortException {
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            dao.update(attachment);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 添付ファイルを削除する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException
     */
    private void deleteAttachment(Attachment attachment) throws ServiceAbortException {
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            dao.delete(attachment);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * 添付ファイルを作成する.
     * @param attachment
     *            添付ファイル
     * @throws ServiceAbortException
     */
    private void createAttachment(Attachment attachment) throws ServiceAbortException {
        log.trace("②添付ファイル(attachment)");
        try {
            AttachmentDao dao = getDao(AttachmentDao.class);
            if (log.isDebugEnabled()) {
                log.debug("" + attachment);
            }
            dao.create(attachment);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#save(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public Long save(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        // コレポンID
        Long corresponId = null;
        if (correspon.isNew()) {
            // 新規登録
            corresponId = doCreateCorrespon(correspon);
        } else {
            // 通知用に更新前の情報取得 (発行済みの場合のみ)
            List<AddressCorresponGroup> oldAddressCorresponGroupList = null;
            if (correspon.isIssued()) {
                oldAddressCorresponGroupList =
                        findAddressCorresponGroups(correspon.getId());
            }

            //更新
            corresponId = doUpdateCorrespon(correspon);

            // 追加のAddresUserに対しメール通知 (発行済みの場合のみ)
            if (correspon.isIssued() && correspon.getForLearning() != ForLearning.LEARNING) {
                sendIssuedNoticeToAddtionalAddressUser(correspon,
                        oldAddressCorresponGroupList,
                        EmailNoticeEventCd.ATTENTION_CC_ADDED);
            }
        }
        return corresponId;
    }

    @Override
    public void saveAttachmentInfo(Correspon correspon, Attachment attachment) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        ArgumentValidator.validateNotNull(attachment);

        attachment.setCorresponId(correspon.getId());
        attachment.setFileType(Attachment.detectFileTypeByFileName(attachment.getFileName()));
        attachment.setCreatedBy(getCurrentUser());
        attachment.setUpdatedBy(getCurrentUser());

        updateAttachment(attachment);
    }

    /**
     * 指定されたコレポン文書を保存する(新規登録).
     *
     * @param correspon コレポン文書関連情報
     * @throws ServiceAbortException コレポン文書の保存に失敗
     */
    private Long doCreateCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("★新規登録");
        Correspon clone = copyCorresponProperties(correspon);
        // コレポン情報設定
        clone.setCreatedBy(getCurrentUser());
        // コレポン文書ID
        clone.setId(createCorrespon(setUpCorrespon(clone)));
        // 添付ファイル保存
        saveAttachments(clone);
        // 1～10のカスタム情報をCorresponCustomField設定
        createCorresponCustomField(clone);
        // 宛先-活動単位情報設定
        saveAddressCorresponGroup(clone);
        // 宛先-ユーザー情報設定
        createAddressUser(clone);
        // 返信の時のみ
        // 返信情報設定
        if (clone.getParentCorresponId() != null) {
            // コレポン文書階層設定
            createHierarchy(clone);
        }

        return clone.getId();
    }

    /**
     * プロパティコピーをしたコレポン文書オブジェクトを作成する.
     *
     * @param correspon
     *            コレポン文書情報
     * @return correspon
     *            コレポン文書情報
     * @throws ServiceAbortException
     *            コレポン文書のコピーに失敗
     */
    private Correspon copyCorresponProperties(Correspon correspon) throws ServiceAbortException {
        Correspon clone = new Correspon();
        try {
            PropertyUtils.copyProperties(clone, correspon);
        } catch (IllegalAccessException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new ServiceAbortException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new ServiceAbortException(e.getMessage());
        }
        return clone;
    }

    /**
     * 指定されたコレポン文書を保存する(更新).
     *
     * @param correspon コレポン文書関連情報
     * @throws ServiceAbortException コレポン文書の保存に失敗
     */
    private Long doUpdateCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("★更新");
        Correspon clone = copyCorresponProperties(correspon);
        // 状態を取得する
        corresponStatusControl.setup(clone);
        // 承認状態をセットする
        clone.setWorkflowStatus(corresponStatusControl.getWorkflowStatus());
        // 承認作業状態を取得する
        WorkflowProcessStatus wfProcessStatus = corresponStatusControl.getWorkflowProcessStatus();
        //①－4－3．入力内容を指定のコレポン文書に更新する。
        updateCorrespon(setUpCorrespon(clone));                // コレポン文書
        if (wfProcessStatus != null) {
            updateWorkflow(correspon, wfProcessStatus);          // ワークフロー
        }
        //  添付ファイル保存
        saveAttachments(clone);

        updateCorresponCustomField(clone);                   // コレポン文書 カスタムフィールド
        updateAddressCorresponGroup(clone);                  // 宛先 活動単位
        updateAddressUser(clone);                            // 宛先 ユーザー


        return clone.getId();
    }

    /**
     * コレポン入力情報を設定.
     * @param correspon
     *            コレポン文書
     * @return Correspon情報
     */
    private Correspon setUpCorrespon(Correspon correspon) {
        Correspon result = new Correspon();
        // 新規かどうかで分岐
        if (correspon.isNew()) {
            result.setProjectId(correspon.getProjectId());
            result.setProjectNameE(correspon.getProjectNameE());
            result.setWorkflowStatus(WorkflowStatus.DRAFT);
            result.setCreatedBy(getCurrentUser());
        } else {
            result.setId(correspon.getId());
            result.setWorkflowStatus(correspon.getWorkflowStatus());
        }
        result.setFromCorresponGroup(correspon.getFromCorresponGroup());
        result.setPreviousRevCorresponId(correspon.getPreviousRevCorresponId());
        result.setCorresponType(correspon.getCorresponType());
        result.setSubject(correspon.getSubject());
        result.setBody(correspon.getBody());
        result.setForLearning(correspon.getForLearning());
        result.setCorresponStatus(correspon.getCorresponStatus());
        result.setReplyRequired(correspon.getReplyRequired());
        result.setDeadlineForReply(correspon.getDeadlineForReply());
        result.setUpdatedBy(getCurrentUser());
        result.setVersionNo(correspon.getVersionNo());
        result.setReplyAddressUserId(correspon.getReplyAddressUserId());
        return result;
    }

    /**
     * コレポン入力情報CorresponCustomFieldをListに設定.
     *
     * @param correspon
     *            コレポン文書情報
     * @return CorresponCustomField情報
     * @throws ServiceAbortException
     */
    private List<CorresponCustomField> setUpCorresponCustomFieldData(Correspon correspon)
                    throws ServiceAbortException {
        List<CorresponCustomField> result = new ArrayList<CorresponCustomField>();
        setUpCorresponCustomField(result,
                                  correspon.getCustomField1Id(),
                                  correspon.getCustomField1Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField2Id(),
                                  correspon.getCustomField2Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField3Id(),
                                  correspon.getCustomField3Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField4Id(),
                                  correspon.getCustomField4Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField5Id(),
                                  correspon.getCustomField5Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField6Id(),
                                  correspon.getCustomField6Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField7Id(),
                                  correspon.getCustomField7Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField8Id(),
                                  correspon.getCustomField8Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField9Id(),
                                  correspon.getCustomField9Value());
        setUpCorresponCustomField(result,
                                  correspon.getCustomField10Id(),
                                  correspon.getCustomField10Value());
        return result;
    }

    private void setUpCorresponCustomField(
                List<CorresponCustomField> fields,
                Long projectCustomFieldId,
                String value) {

        if (projectCustomFieldId != null) {
            CorresponCustomField f = new CorresponCustomField();
            f.setProjectCustomFieldId(projectCustomFieldId);
            f.setValue(value);
            fields.add(f);
        }
    }

    /**
     * コレポン入力情報Hierarchyを設定.
     * @param correspon
     * @return CorresponHierarchy情報
     */
    private CorresponHierarchy setUpCorresponHierarchyData(Correspon correspon) {
        CorresponHierarchy corresponHierarchy = new CorresponHierarchy();
        corresponHierarchy.setParentCorresponId(correspon.getParentCorresponId());
        return corresponHierarchy;
    }

    /**
     * 添付ファイルをFileStoreシステムに保存する.
     * @param attachment 添付ファイル
     * @return FileStoreシステムが発番したオブジェクトID
     * @throws ServiceAbortException 保存に失敗
     */
    private String saveAttachmentContent(Attachment attachment)
            throws ServiceAbortException {

        FileStoreClient fileStore = getFileStoreClient();
        try {
            String result = fileStore.createFile(attachment.getSourcePath());
            if (log.isDebugEnabled()) {
                log.debug("Save an attachment to FileStore. {}, {}", attachment.getFileName(), result);
            }
            return result;
        } catch (FileStoreException e) {
            throw new ServiceAbortException(e.getMessage(),
                                            e,
                                            ApplicationMessageCode.ERROR_SAVING_FILE);
        }
    }

    /**
     * ワークフローの更新処理.
     * ワークフロー
     *
     * @param original 更新前のコレポン文書
     * @param wfProcessStatus 更新する値
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void updateWorkflow(Correspon original, WorkflowProcessStatus wfProcessStatus)
            throws ServiceAbortException {
        switch (original.getWorkflowStatus()) {
        case DENIED:
            //  否認されたコレポン文書を更新する場合は
            //  承認フローに設定された、全てのChecker/Approverの承認作業状態を更新する
            //  (初期値に戻す)
            updateWorkflowByCorresponId(setUpWorkflowCaseDenied(original, wfProcessStatus));
            break;
        case REQUEST_FOR_CHECK:
        case UNDER_CONSIDERATION:
        case REQUEST_FOR_APPROVAL:
            //  検証・承認依頼中のコレポン文書をChecker/Approverが更新する場合は
            //  自身の承認作業ステータスを作業中に更新する
            if (isChecker(original) || isApprover(original)) {
                updateWorkflow(setUpWorkflow(original, wfProcessStatus));
            }
            break;
        default:
            //  その他の承認作業状態の場合は何もしない
            break;
        }
    }

    private void saveAttachments(Correspon correspon) throws ServiceAbortException {
        List<Attachment> attachments = correspon.getUpdateAttachments();
        if (attachments == null) {
            return;
        }

        for (Attachment a : attachments) {
            a.setCorresponId(correspon.getId());
            a.setFileType(Attachment.detectFileTypeByFileName(a.getFileName()));
            a.setCreatedBy(getCurrentUser());
            a.setUpdatedBy(getCurrentUser());

            processSaveAttachment(a);
        }
    }

    private void processSaveAttachment(Attachment attachment) throws ServiceAbortException {
        switch (attachment.getMode()) {
        case NEW:
            String fileStoreFileId = saveAttachmentContent(attachment);
            attachment.setFileId(fileStoreFileId);
            createAttachment(attachment);
            break;
        case UPDATE:
            updateAttachment(attachment);
            break;
        case DELETE :
            deleteAttachment(attachment);
            break;
        default:
            // 添付ファイルはNEW、DELETEのいずれかしかあり得ない (UPDATEは無し)
            break;
        }
    }

    /**
     * forLearningの値をDBへ記録する値へと変換する.
     *
     * @param learning 学習用コンテンツか否か（boolean）
     * @return 変換結果（true:"X", false:null）
     */
    private String convertLearningValue(String learning) {
        if(learning.equals("true")) {
            return "X";
        } else {
            return null;
        }
    }

    /**
     * List情報に変更があった場合の更新処理.
     * コレポン文書-カスタムフィールド
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void updateCorresponCustomField(Correspon correspon) throws ServiceAbortException {
        //削除処理
        deleteCorresponCustomField(correspon);
        //登録処理
        createCorresponCustomField(correspon);
    }

    /**
     * List情報に変更があった場合の削除処理.
     * コレポン文書-カスタムフィールド
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void deleteCorresponCustomField(Correspon correspon) throws ServiceAbortException {
        //コレポン文書-カスタムフィールド
        CorresponCustomFieldDao corresponCustomFieldDao = getDao(CorresponCustomFieldDao.class);
        CorresponCustomField corresponCustomField = new CorresponCustomField();
        corresponCustomField.setCorresponId(correspon.getId());
        corresponCustomField.setUpdatedBy(getCurrentUser());
        try {
            log.trace("③コレポン文書-カスタムフィールド削除(correspon_custom_field)");
            corresponCustomFieldDao.deleteByCorresponId(corresponCustomField);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * コレポン文書-カスタムフィールドの登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     */
    private void createCorresponCustomField(Correspon correspon) throws ServiceAbortException {
        //コレポン文書-カスタムフィールド
        CorresponCustomFieldDao corresponCustomFieldDao = getDao(CorresponCustomFieldDao.class);
        List<CorresponCustomField> corresponCustomFields = setUpCorresponCustomFieldData(correspon);
        for (CorresponCustomField corresponCustomField : corresponCustomFields) {
            if (corresponCustomField == null) {
                continue;
            }

            corresponCustomField.setCorresponId(correspon.getId());
            corresponCustomField.setCreatedBy(correspon.getCreatedBy());
            corresponCustomField.setUpdatedBy(getCurrentUser());
            log.trace("③コレポン文書-カスタムフィールド(correspon_custom_field)");
            try {
                if (log.isDebugEnabled()) {
                    log.debug("" + corresponCustomField);
                }
                corresponCustomFieldDao.create(corresponCustomField);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            }
        }
    }

    /**
     * List情報に変更があった場合の更新処理.
     * 宛先ユーザー
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void updateAddressUser(Correspon correspon) throws ServiceAbortException {
        //削除処理
        deleteAddressUser(correspon);
        //登録処理
        createAddressUser(correspon);
    }

    /**
     * List情報に変更があった場合の削除処理.
     * 宛先ユーザー
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void deleteAddressUser(Correspon correspon) throws ServiceAbortException {
        AddressUserDao addressUserDao = getDao(AddressUserDao.class);
        List<AddressCorresponGroup> addressCorresponGroupList
        = correspon.getAddressCorresponGroups();
        if (addressCorresponGroupList == null) {
            return;
        }
        for (AddressCorresponGroup addressCorresponGroup
                : addressCorresponGroupList) {
            if (log.isDebugEnabled()) {
                log.debug(addressCorresponGroup + "");
            }
            if (addressCorresponGroup.getMode() != UpdateMode.NEW
                && addressCorresponGroup.getMode() != UpdateMode.UPDATE) {
                continue;
            }

            // PIC削除
            deletePersonInCharge(addressCorresponGroup.getUsers());

            AddressUser addressUser = new AddressUser();
            addressUser.setAddressCorresponGroupId(addressCorresponGroup.getId());
            addressUser.setUpdatedBy(getCurrentUser());
            try {
                addressUserDao.deleteByAddressCorresponGroupId(addressUser);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            } catch (StaleRecordException e) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
            }
        }
    }

    /**
     * List情報に変更があった場合の削除処理.
     * 宛先ユーザーのPIC
     *
     * @param addressUsers 宛先ユーザーリスト
     * @throws ServiceAbortException
     */
    private void deletePersonInCharge(List<AddressUser> addressUsers) throws ServiceAbortException {
        PersonInChargeDao personInChargeDao = getDao(PersonInChargeDao.class);
        for (AddressUser addressUser : addressUsers) {
            if (addressUser.getId() == null) {
                continue;
            }

            addressUser.setPersonInCharges(
                personInChargeDao.findByAddressUserId(addressUser.getId()));

            if (addressUser.getPersonInCharges() != null) {
                PersonInCharge personInCharge = new PersonInCharge();
                personInCharge.setAddressUserId(addressUser.getId());
                personInCharge.setUpdatedBy(getCurrentUser());
                try {
                    personInChargeDao.deleteByAddressUserId(personInCharge);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                } catch (StaleRecordException e) {
                    throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
                }
            }
        }
    }

    /**
     * 宛先ユーザーの登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     */
    private void createAddressUser(Correspon correspon) throws ServiceAbortException {
        AddressUserDao addressuserDao = getDao(AddressUserDao.class);
        List<AddressCorresponGroup> addressCorresponGroupList
        = correspon.getAddressCorresponGroups();
        if (addressCorresponGroupList == null) {
            return;
        }
        for (AddressCorresponGroup addressCorresponGroup : addressCorresponGroupList) {
            List<AddressUser> addressUsers = addressCorresponGroup.getUsers();
            log.trace("⑤★");
            if (addressUsers == null) {
                return;
            }
            if (addressCorresponGroup.getMode() != UpdateMode.NEW
                && addressCorresponGroup.getMode() != UpdateMode.UPDATE) {
                continue;
            }

            for (AddressUser addressUser : addressUsers) {
                try {
                    addressUser.setAddressCorresponGroupId(addressCorresponGroup.getId());
                    addressUser.setCreatedBy(correspon.getCreatedBy());
                    addressUser.setUpdatedBy(getCurrentUser());
                    log.trace("⑤宛先-ユーザー(address_user)");
                    if (log.isDebugEnabled()) {
                        log.debug("" + addressUser);
                    }
                    Long id = addressuserDao.create(addressUser);
                    // PIC登録用にidをセットする
                    addressUser.setId(id);

                    // PIC登録
                    createPersonInCharge(addressUser);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                }
            }
        }
    }

    /**
     * 宛先ユーザーに設定されたPICの登録処理.
     *
     * @param addressUser 宛先ユーザー
     * @throws ServiceAbortException
     */
    private void createPersonInCharge(AddressUser addressUser) throws ServiceAbortException {
        PersonInChargeDao personInChargeDao = getDao(PersonInChargeDao.class);
        List<PersonInCharge> personInCharges = addressUser.getPersonInCharges();
        if (personInCharges != null) {
            for (PersonInCharge pic : personInCharges) {
                try {
                    pic.setAddressUserId(addressUser.getId());
                    personInChargeDao.create(pic);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                }
            }
        }
    }

    /**
     * List情報に変更があった場合の更新処理.
     * 宛先-活動単位
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void updateAddressCorresponGroup(Correspon correspon) throws ServiceAbortException {
        //削除処理
        deleteAddressCorresponGroup(correspon);
        //登録処理
        saveAddressCorresponGroup(correspon);
    }

    /**
     * List情報に変更があった場合の削除処理.
     * 宛先-活動単位
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     * @throws StaleRecordException
     */
    private void deleteAddressCorresponGroup(Correspon correspon) throws ServiceAbortException {
        AddressCorresponGroupDao addressCorresponGroupDao = getDao(AddressCorresponGroupDao.class);
        for (AddressCorresponGroup ag : correspon.getAddressCorresponGroups()) {
            if (ag.getMode() != UpdateMode.DELETE) {
                continue;
            }
            ag.setUpdatedBy(getCurrentUser());
            try {
                addressCorresponGroupDao.delete(ag);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            } catch (StaleRecordException e) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
            }
        }
    }

    /**
     * 宛先-活動単位の登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     */
    private void saveAddressCorresponGroup(Correspon correspon) throws ServiceAbortException {
        AddressCorresponGroupDao addressCorresponGroupDao = getDao(AddressCorresponGroupDao.class);
        List<AddressCorresponGroup> addressCorresponGroups = correspon.getAddressCorresponGroups();
        if (addressCorresponGroups == null) {
            return;
        }
        try {
            for (AddressCorresponGroup addressCorresponGroup : addressCorresponGroups) {
                log.trace("④宛先-活動単位(address_correspon_group)");
                if (log.isDebugEnabled()) {
                    log.debug("" + addressCorresponGroup);
                }
                UpdateMode mode = addressCorresponGroup.getMode();
                if (mode == UpdateMode.NEW) {
                    addressCorresponGroup.setCorresponId(correspon.getId());
                    addressCorresponGroup.setCreatedBy(correspon.getCreatedBy());
                    addressCorresponGroup.setUpdatedBy(getCurrentUser());

                    Long id = addressCorresponGroupDao.create(addressCorresponGroup);
                    addressCorresponGroup.setId(id);
                } else  if (mode == UpdateMode.UPDATE) {
                    addressCorresponGroup.setCorresponId(correspon.getId());
                    addressCorresponGroup.setUpdatedBy(getCurrentUser());

                    addressCorresponGroupDao.update(addressCorresponGroup);
                }
            }

        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * コレポン文書階層の登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException
     */
    private void createHierarchy(Correspon correspon) throws ServiceAbortException {
        log.trace("⑥コレポン文書階層");
        CorresponHierarchyDao corresponHierarchyDao = getDao(CorresponHierarchyDao.class);
        CorresponHierarchy corresponHierarchy = setUpCorresponHierarchyData(correspon);
        corresponHierarchy.setChildCorresponId(correspon.getId());
        corresponHierarchy.setReplyAddressUserId(correspon.getReplyAddressUserId());
        corresponHierarchy.setCreatedBy(correspon.getCreatedBy());
        corresponHierarchy.setUpdatedBy(getCurrentUser());
        try {
            if (log.isDebugEnabled()) {
                log.debug("" + corresponHierarchy);
            }
            corresponHierarchyDao.create(corresponHierarchy);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }

    }

    private List<AddressCorresponGroup> findAddressCorresponGroups(Long corresponId) {
        AddressCorresponGroupDao dao = getDao(AddressCorresponGroupDao.class);
        return dao.findByCorresponId(corresponId);
    }

    private void sendIssuedNoticeToAddtionalAddressUser(Correspon correspon,
            List<AddressCorresponGroup> oldAddressCorresponGroupList,
            EmailNoticeEventCd emailNoticeEventCd) throws ServiceAbortException {
        Correspon updatedCorrespon = corresponService.find(correspon.getId());
        emailNoticeService.sendIssuedNoticeToAddtionalAddressUser(updatedCorrespon,
            emailNoticeEventCd, oldAddressCorresponGroupList);
    }
}
