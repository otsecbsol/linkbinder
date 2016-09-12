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
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.AddressCorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.AddressUserDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponCustomFieldDao;
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
import jp.co.opentone.bsol.linkbinder.service.CorresponServiceHelper;
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
     * サービスヘルパ.
     */
    @Resource
    private CorresponServiceHelper serviceHelper;

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
     * @throws ServiceAbortException 更新失敗
     */
    private void updateWorkflowByCorresponId(Workflow workflow) throws ServiceAbortException {
        try {
            WorkflowDao dao = getDao(WorkflowDao.class);
            dao.updateByCorresponId(workflow);
        } catch (StaleRecordException sre) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException kde) {
            throw new ServiceAbortException(kde);
        }
    }

    /**
     * 更新用フローの作成
     * Update(Deniedの編集).
     *
     * @param correspon コレポン文書
     * @param wfProcessStatus ワークフロー処理状態
     *
     * @return workflow ワークフロー
     */
    private Workflow setUpWorkflowCaseDenied(Correspon correspon, WorkflowProcessStatus wfProcessStatus) {
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
     * @param wfProcessStatus ワークフロー処理状態
     * @return workflow ワークフロー
     */
    private Workflow setUpWorkflow(Correspon correspon, WorkflowProcessStatus wfProcessStatus) {
        Workflow workflowForUpdate = new Workflow();

        List<Workflow> workflows = correspon.getWorkflows();
        workflows.stream()
                .filter(w -> w.getUser().getEmpNo().equals(getCurrentUser().getEmpNo()))
                .forEach(w -> {
            workflowForUpdate.setId(w.getId());
            workflowForUpdate.setWorkflowProcessStatus(wfProcessStatus);
            workflowForUpdate.setUpdatedBy(getCurrentUser());
            workflowForUpdate.setVersionNo(w.getVersionNo());
        });
        return workflowForUpdate;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService#save(jp.co.opentone.bsol.linkbinder.dto.Correspon)
     */
    public Long save(Correspon correspon) throws ServiceAbortException {
        ArgumentValidator.validateNotNull(correspon);
        // コレポンID
        Long corresponId;
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

        serviceHelper.updateAttachment(attachment);
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
        clone.setId(serviceHelper.createCorrespon(setUpCorrespon(clone)));
        // 添付ファイル保存
        serviceHelper.saveAttachments(clone);
        // 1～10のカスタム情報をCorresponCustomField設定
        serviceHelper.createCorresponCustomField(clone);
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
        serviceHelper.saveLearningLabel(clone);
        serviceHelper.saveLearningTag(clone);

        return clone.getId();
    }

    /**
     * プロパティコピーをしたコレポン文書オブジェクトを作成する.
     *
     * @param correspon
     *            コレポン文書情報
     * @return correspon
     *            コレポン文書情報
     */
    private Correspon copyCorresponProperties(Correspon correspon) {
        Correspon clone = new Correspon();
        try {
            PropertyUtils.copyProperties(clone, correspon);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ApplicationFatalRuntimeException(e);
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
        serviceHelper.updateCorrespon(setUpCorrespon(clone));                // コレポン文書
        if (wfProcessStatus != null) {
            updateWorkflow(correspon, wfProcessStatus);          // ワークフロー
        }
        //  添付ファイル保存
        serviceHelper.saveAttachments(clone);

        updateCorresponCustomField(clone);                   // コレポン文書 カスタムフィールド
        updateAddressCorresponGroup(clone);                  // 宛先 活動単位
        updateAddressUser(clone);                            // 宛先 ユーザー

        serviceHelper.saveLearningLabel(clone);
        serviceHelper.saveLearningTag(clone);

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
     * コレポン入力情報Hierarchyを設定.
     * @param correspon 文書
     * @return CorresponHierarchy情報
     */
    private CorresponHierarchy setUpCorresponHierarchyData(Correspon correspon) {
        CorresponHierarchy corresponHierarchy = new CorresponHierarchy();
        corresponHierarchy.setParentCorresponId(correspon.getParentCorresponId());
        return corresponHierarchy;
    }

    /**
     * ワークフローの更新処理.
     * ワークフロー
     *
     * @param original 更新前のコレポン文書
     * @param wfProcessStatus 更新する値
     * @throws ServiceAbortException 更新失敗
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
                serviceHelper.updateWorkflow(setUpWorkflow(original, wfProcessStatus));
            }
            break;
        default:
            //  その他の承認作業状態の場合は何もしない
            break;
        }
    }

    /**
     * List情報に変更があった場合の更新処理.
     * コレポン文書-カスタムフィールド
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    private void updateCorresponCustomField(Correspon correspon) throws ServiceAbortException {
        //削除処理
        deleteCorresponCustomField(correspon);
        //登録処理
        serviceHelper.createCorresponCustomField(correspon);
    }

    /**
     * List情報に変更があった場合の削除処理.
     * コレポン文書-カスタムフィールド
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
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
        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
        }
    }

    /**
     * List情報に変更があった場合の更新処理.
     * 宛先ユーザー
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
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
     * @throws ServiceAbortException 更新失敗
     */
    private void deleteAddressUser(Correspon correspon) throws ServiceAbortException {
        AddressUserDao addressUserDao = getDao(AddressUserDao.class);
        List<AddressCorresponGroup> addressCorresponGroupList = correspon.getAddressCorresponGroups();
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
            } catch (StaleRecordException e) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            }
        }
    }

    /**
     * List情報に変更があった場合の削除処理.
     * 宛先ユーザーのPIC
     *
     * @param addressUsers 宛先ユーザーリスト
     * @throws ServiceAbortException 更新失敗
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
                } catch (StaleRecordException e) {
                    throw new ServiceAbortException(
                            ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
                } catch (KeyDuplicateException e) {
                    throw new ServiceAbortException(e);
                }
            }
        }
    }

    /**
     * 宛先ユーザーの登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
     */
    private void createAddressUser(Correspon correspon) throws ServiceAbortException {
        AddressUserDao addressuserDao = getDao(AddressUserDao.class);
        List<AddressCorresponGroup> addressCorresponGroupList = correspon.getAddressCorresponGroups();
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
     * @throws ServiceAbortException 更新失敗
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
     * @throws ServiceAbortException 更新失敗
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
     * @throws ServiceAbortException 更新失敗
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
            } catch (StaleRecordException e) {
                throw new ServiceAbortException(
                        ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
            } catch (KeyDuplicateException e) {
                throw new ServiceAbortException(e);
            }
        }
    }

    /**
     * 宛先-活動単位の登録処理.
     *
     * @param correspon コレポン文書
     * @throws ServiceAbortException 更新失敗
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

        } catch (StaleRecordException e) {
            throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        } catch (KeyDuplicateException e) {
            throw new ServiceAbortException(e);
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
