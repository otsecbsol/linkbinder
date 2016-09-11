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

import jp.co.opentone.bsol.framework.core.dao.RecordNotFoundException;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.dao.CorresponDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponGroupDao;
import jp.co.opentone.bsol.linkbinder.dao.CorresponTypeDao;
import jp.co.opentone.bsol.linkbinder.dao.CustomFieldDao;
import jp.co.opentone.bsol.linkbinder.dao.ProjectDao;
import jp.co.opentone.bsol.linkbinder.dao.UserDao;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.AddressUser;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponType;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.Project;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressUserType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchUserCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.AbstractService;
import jp.co.opentone.bsol.linkbinder.service.CorresponServiceHelper;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponValidateService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
/**
 * このサービスではコレポン文書の検証に関する処理を提供する.
 * @author opentone
 */
@Service
public class CorresponValidateServiceImpl extends AbstractService
implements CorresponValidateService {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 4194830475836740691L;
    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(CorresponValidateServiceImpl.class);

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
     * 空のインスタンスを生成する.
     */
    public CorresponValidateServiceImpl() {
    }

    /**
     * 宛先(To)の存在チェックを行う.
     * @param toAddressValues 宛先設定状況
     * @return true = 検証成功-宛先あり
     * @throws ServiceAbortException 検証失敗
     */
    public boolean validateToAddress(String toAddressValues) throws ServiceAbortException {
        if (toAddressValues == null) {
            throw new ServiceAbortException(ApplicationMessageCode.E_INVALID_INPUT);
        }
        return true;
    }

    /**
     * コレポン文書を検証する.
     * @param correspon
     *            コレポン文書
     * @throws ServiceAbortException
     *             コレポン文書の認証に失敗
     * @return true = コレポン文書の検証に成功
     */
    public boolean validate(Correspon correspon) throws ServiceAbortException {
        if (log.isDebugEnabled()) {
            log.debug("----- validate parameter & status -----");
            log.debug("getCurrentUser[" + getCurrentUser() + "]");
            log.debug("getCurrentProjectId[" + getCurrentProjectId() + "]");
            log.debug("correspon[" + correspon + "]");
        }
        ArgumentValidator.validateNotNull(correspon);
        // 活動単位ユーザーチェック(親クラスのメソッド使用)
        try {
            log.trace("★活動単位ユーザーチェック");
            if (log.isDebugEnabled()) {
                log.debug("getCurrentUser().getEmpNo[" + getCurrentUser().getEmpNo() + "]");
                log.debug("getCurrentProjectId[" + getCurrentProjectId() + "]");
                log.debug("isSystemAdmin(getCurrentUser())["
                    + isSystemAdmin(getCurrentUser()) + "]");
                log.debug("getAccessibleProjects(getCurrentUser())["
                    + getAccessibleProjects(getCurrentUser()) + "]");
            }
            validateProjectId(getCurrentProjectId());
        } catch (ServiceAbortException e) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF
                + " " + getCurrentUser()
                + " getCurrentProjectId[" + getCurrentProjectId() + "]" + " " + correspon);
            throw new ServiceAbortException(ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
        try {
            // コレポン文書Validateチェック(共通処理)
            commonValidateCorrespon(correspon);
            if (correspon.isNew()) {
                // コレポン文書を新規登録する・コレポン文書をDraft保存する の場合
                validateCorresponNew(correspon);
            } else {
                // コレポン文書を更新する・コレポン文書を修正する の場合
                validateCorresponUpdated(correspon);
            }
            // 添付ファイルチェック、排他チェックはサービス呼び元で行う
        } catch (ServiceAbortException e) {
            log.warn(e.getMessageCode() + " " + getCurrentUser()
                + " getCurrentProjectId[" + getCurrentProjectId() + "]" + " " + correspon);
            throw e;
        }

        if (log.isDebugEnabled()) {
            log.debug("----- validate end -----");
        }
        return true;
    }

    /**
     * コレポン文書を新規作成する・コレポン文書をDraft保存する のチェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    private void validateCorresponNew(Correspon correspon) throws ServiceAbortException {
        log.trace("●文書作成チェック");
        if (log.isDebugEnabled()) {
            log.debug("correspon.getParentCorresponI[" + correspon.getParentCorresponId() + "]");
            log.debug("correspon.getPreviousRevCorresponId["
                + correspon.getPreviousRevCorresponId() + "]");
        }
        if (correspon.getPreviousRevCorresponId() != null) {
            // 改訂の際、改訂文書作成チェック。
            commonValidateCorresponNewRevision(correspon);
        } else if (correspon.getParentCorresponId() != null) {
            // 返信の際、返信文書作成チェック
            commonValidateCorresponNewReply(correspon);
        }
    }

    /**
     * 返信の際、返信文書作成チェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    private void commonValidateCorresponNewReply(Correspon correspon) throws ServiceAbortException {
        Correspon sourceCorrespon;
        // 返信元のコレポンが存在しない際はエラー
        log.trace("★返信①");
        try {
            if (log.isDebugEnabled()) {
                log.debug("correspon.getParentCorresponI["
                    + correspon.getParentCorresponId() + "]");
            }
            sourceCorrespon = findCorrespon(correspon.getParentCorresponId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + "correspon.getParentCorresponId["
                + correspon.getParentCorresponId() + "]");
            throw new ServiceAbortException(ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REPLY_NOT_EXIST);
        }
        // 返信元のコレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
        log.trace("★返信②");
        try {
            validateCorresponProjectId(sourceCorrespon);
        } catch (ServiceAbortException e) {
            log.warn(e.getMessageCode() + " " + sourceCorrespon);
            throw new ServiceAbortException(ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                (Object) sourceCorrespon.getCorresponNo());
        }
        // 返信元のコレポン文書の承認状態が[5：Issued]]以外の場合、エラー
        log.trace("★返信③");
        if (log.isDebugEnabled()) {
            log.debug(
                "sourceCorrespon.getWorkflowStatus[" + sourceCorrespon.getWorkflowStatus() + "]");
        }
        if (!WorkflowStatus.ISSUED.equals(sourceCorrespon.getWorkflowStatus())) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID
                + " " + sourceCorrespon);
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID);
        }
        // 返信元のコレポン文書の文書状態がCancelならばエラー(追加要件分析概要_Rev006.docより)
        log.trace("★返信④(チェック定義書には記載なし)");
        if (log.isDebugEnabled()) {
            log.debug(
                "sourceCorrespon.getCorresponStatus[" + sourceCorrespon.getCorresponStatus() + "]");
        }
        if (CorresponStatus.CANCELED.equals(sourceCorrespon.getCorresponStatus())) {
            log.warn(ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON
                + " " + sourceCorrespon);
            throw new ServiceAbortException(
                ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON);
        }
    }

    /**
     * 改訂の際、改訂文書作成チェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    private void commonValidateCorresponNewRevision(Correspon correspon)
    throws ServiceAbortException {
        Correspon sourceCorrespon;
        // 改訂元のコレポンが存在しない際はエラー
        try {
            log.trace("★改訂①");
            if (log.isDebugEnabled()) {
                log.debug("correspon.getPreviousRevCorresponId["
                    + correspon.getPreviousRevCorresponId() + "]");
            }
            sourceCorrespon = findCorrespon(correspon.getPreviousRevCorresponId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + " correspon.getPreviousRevCorresponId["
                + correspon.getPreviousRevCorresponId() + "]");
            throw new ServiceAbortException(ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_FOR_REVISION_NOT_EXIST);
        }
        // 改訂元のコレポン文書の文書状態が[2：Canceled]ではない際はエラー
        log.trace("★改訂②");
        if (log.isDebugEnabled()) {
            log.debug(
                "sourceCorrespon.getCorresponStatus[" + sourceCorrespon.getCorresponStatus() + "]");
        }
        if (!CorresponStatus.CANCELED.equals(sourceCorrespon.getCorresponStatus())) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_CANCELED
                + " " + sourceCorrespon);
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_NOT_CANCELED);
        }
    }

    /**
     * コレポン文書を更新する・コレポン文書を修正する のチェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    private void validateCorresponUpdated(Correspon correspon) throws ServiceAbortException {
        // 指定のコレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
        validateCorresponProjectId(correspon);

        // コレポン文書の承認状態が変更されている際はエラー
        validateCorresponWorkflowStatus(correspon);

        // ワークフロー(Prepare/Checker/Approver)に応じてチェック
        validateCorresponByWorkflow(correspon);

        // コレポン文書のバージョンNoが異なる際はエラー(これはdaoで行うのでServiceでは行わなくてOK)
    }


    /**
     * 指定のコレポン文書のプロジェクトが現在選択中のプロジェクトかをチェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 指定のコレポン文書のプロジェクトが現在選択中のプロジェクト以外
     */
    private void validateCorresponProjectId(Correspon correspon) throws ServiceAbortException {
        log.trace("★プロジェクトチェック");
        if (log.isDebugEnabled()) {
            log.debug("correspon.getProjectId[" + correspon.getProjectId() + "]");
            log.debug("getCurrentProjectId[" + getCurrentProjectId() + "]");
        }
        if (!StringUtils.equals(correspon.getProjectId(), getCurrentProjectId())) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_PROJECT_DIFF);
        }
    }

    /**
     * コレポン文書の承認状態の変更有無をチェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException
     *            コレポン文書の承認状態が変更されている、または指定のコレポン文書が存在しない
     */
    private void validateCorresponWorkflowStatus(Correspon correspon) throws ServiceAbortException {
        log.trace("★承認状態チェック");
        // コレポン文書の承認状態が、以前のものと比較して変更されている際はエラー
        Correspon oldCorrespon;
        try {
            oldCorrespon = findCorrespon(correspon.getId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + " correspon.getId[" + correspon.getId() + "]");
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }

        if (log.isDebugEnabled()) {
            log.debug("correspon.getWorkflowStatus[" + correspon.getWorkflowStatus() + "]");
            log.debug("oldCorrespon.getWorkflowStatus[" + oldCorrespon.getWorkflowStatus() + "]");
        }
        if (!oldCorrespon.getWorkflowStatus().equals(correspon.getWorkflowStatus())) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED
                + " " + oldCorrespon);
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_ALREADY_UPDATED);
        }
    }

    /**
     * ワークフロー(Admin/Preparer/Checker/Approver)に応じて文書更新をチェック.
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException
     *            ワークフローに対して承認状態がエラー
     */
    private void validateCorresponByWorkflow(Correspon correspon) throws ServiceAbortException {
        log.trace("●文書更新チェック");
        if (log.isDebugEnabled()) {
            log.debug("isSystemAdmin[" + isSystemAdmin(getCurrentUser()) + "]");
            log.debug(
                "isProjectAdmin[" + isProjectAdmin(getCurrentUser(), getCurrentProjectId())  + "]");
            log.debug("isGroupAdmin["
                + isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
                + "]");
        }

        // 承認状態/承認作業状態制御オブジェクトに処理を委譲
        corresponStatusControl.validate(correspon);
    }


    /**
     * コレポン文書Validateチェック 共通処理.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException チェック定義書の一連のチェックでエラー
     */
    private void commonValidateCorrespon(Correspon correspon) throws ServiceAbortException {
        log.trace("●コレポン文書Validateチェック");
        // 「更新の際、」のチェック
        if (!correspon.isNew()) {
            commonValidateCorresponUpdate(correspon);
        }

        // 文書状態(Open/Closed/Canceled)チェック
        serviceHelper.commonValidateCorresponStatus(correspon);

        // From①②のチェック
        doValidateCorresponGroup(correspon.getProjectId(), correspon.getFromCorresponGroup());
        // To①②③④のチェック.
        doValidateCorresponTo(correspon);
        // Cc①②③④のチェック.
        doValidateCorresponCc(correspon);
        // Correspondence Type
        doValidateCorresponType(correspon);
        // Attachments
        doValidateAttachments(correspon);
        // Field1～10
        doValidateCustomField(correspon);
    }

    /**
     * コレポン文書Validateチェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「更新の際①②」記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException チェック定義書の一連のチェックでエラー
     */
    private void commonValidateCorresponUpdate(Correspon correspon) throws ServiceAbortException {
        // ①指定のコレポン文書情報が存在しない場合はエラー
        log.trace("★更新の際①");
        if (log.isDebugEnabled()) {
            log.debug("correspon.getId[" + correspon.getId() + "]");
        }
        try {
            findCorrespon(correspon.getId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + " correspon.getId[" + correspon.getId() + "]");
            throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
        }

        // ②System Admin, Project Admin, Group Admin以外で、承認状態が[5: Issued]のコレポン文書はエラー
        log.trace("★更新の際②");
        if (log.isDebugEnabled()) {
            log.debug("isSystemAdmin[" + isSystemAdmin(getCurrentUser()) + "]");
            log.debug("isProjectAdmin["
                + isProjectAdmin(getCurrentUser(), getCurrentProjectId())  + "]");
            log.debug("isGroupAdmin[" + isGroupAdmin(getCurrentUser(),
                correspon.getFromCorresponGroup().getId()) + "]");
            log.debug("correspon.getWorkflowStatus[" + correspon.getWorkflowStatus() + "]");
        }
        if (!isSystemAdmin(getCurrentUser())
                && !isProjectAdmin(getCurrentUser(), getCurrentProjectId())
                && !isGroupAdmin(getCurrentUser(),
                    correspon.getFromCorresponGroup().getId())
                    && WorkflowStatus.ISSUED.equals(correspon.getWorkflowStatus())) {
            log.warn(ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED
                + " isSystemAdmin[" + isSystemAdmin(getCurrentUser()) + "]"
                + " isProjectAdmin["
                + isProjectAdmin(getCurrentUser(), getCurrentProjectId()) + "]"
                + " isGroupAdmin["
                + isGroupAdmin(getCurrentUser(), correspon.getFromCorresponGroup().getId())
                + "]");
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED);
        }

        // 「③文章状態が[2:Canceled]の際はエラー」は、後の文書状態(Open/Closed/Canceled)チェックで実施しているので省略
    }

    /**
     * To①②③④チェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「To①②③④」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     *
     * @throws ServiceAbortException 検証エラー
     */
    private void doValidateCorresponTo(Correspon correspon) throws ServiceAbortException {
        log.trace("●To");
        // 重複エラーチェックリスト
        List<Long> corresponGroupList = new ArrayList<>();
        List<AddressCorresponGroup> addressCorresponGroupList
            = correspon.getAddressCorresponGroups();

        List<String> empNos;
        List<ProjectUser> projectUsers;
        try {
            empNos = findEmpNo();
        } catch (RecordNotFoundException e) {
            // マスタからユーザーが取得できない
            throw new ApplicationFatalRuntimeException(e.getMessage());
        }

        try {
            projectUsers = findUserByProjectId(correspon.getProjectId());
        } catch (RecordNotFoundException e) {
            // プロジェクトに紐づくユーザーが取得できない
            throw new ApplicationFatalRuntimeException(e.getMessage());
        }

        for (AddressCorresponGroup addressCorresponGroup : addressCorresponGroupList) {
            if (log.isDebugEnabled()) {
                log.debug("> addressCorresponGroup.getAddressType["
                    + addressCorresponGroup.getAddressType() + "]");
            }
            // 削除されているものは検証しない
            if (addressCorresponGroup.getMode() == UpdateMode.DELETE) {
                continue;
            }

            // 宛先種別がToのみ
            if (AddressType.TO.equals(addressCorresponGroup.getAddressType())) {
                // 活動単位が重複している場合はエラー #683
                if (corresponGroupList.contains(
                        addressCorresponGroup.getCorresponGroup().getId())) {
                    log.warn(ApplicationMessageCode.DUPLICATED_TO_GROUP + " "
                        + addressCorresponGroup.getCorresponGroup().getName());
                    throw new ServiceAbortException(ApplicationMessageCode.DUPLICATED_TO_GROUP,
                        (Object) addressCorresponGroup.getCorresponGroup().getName());
                }
                corresponGroupList.add(addressCorresponGroup.getCorresponGroup().getId());

                // To①②のチェック
                doValidateCorresponGroup(
                    correspon.getProjectId(), addressCorresponGroup.getCorresponGroup());

                List<AddressUser> addressUserList = addressCorresponGroup.getUsers();
                if (addressUserList != null) {
                    for (AddressUser au : addressUserList) {
                        if (log.isDebugEnabled()) {
                            log.debug(">> getAddressUserType[" + au.getAddressUserType() + "]");
                        }
                        // 宛先-ユーザ種別がAttentionのみ
                        if (AddressUserType.ATTENTION.equals(au.getAddressUserType())) {
                            validateCorresponToAttention(au, empNos, projectUsers);
                        }
                    }
                }
            }
        }
    }

    /**
     * Cc①②③④(ユーザの存在有無、プロジェクト所属)チェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「Cc①②③④」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     *
     * @throws ServiceAbortException 検証エラー
     */
    private void doValidateCorresponCc(Correspon correspon) throws ServiceAbortException {
        log.trace("●Cc");
        // 重複エラーチェックリスト
        List<Long> corresponGroupList = new ArrayList<>();
        List<AddressCorresponGroup> addressCorresponGroupList
            = correspon.getAddressCorresponGroups();

        List<String> empNos;
        List<ProjectUser> projectUsers;
        try {
            empNos = findEmpNo();
        } catch (RecordNotFoundException e) {
            // マスタからユーザーが取得できない
            throw new ApplicationFatalRuntimeException(e.getMessage());
        }

        try {
            projectUsers = findUserByProjectId(correspon.getProjectId());
        } catch (RecordNotFoundException e) {
            // プロジェクトに紐づくユーザーが取得できない
            throw new ApplicationFatalRuntimeException(e.getMessage());
        }

        for (AddressCorresponGroup addressCorresponGroup : addressCorresponGroupList) {
            if (log.isDebugEnabled()) {
                log.debug("> addressCorresponGroup.getAddressType["
                    + addressCorresponGroup.getAddressType() + "]");
            }
            // 値が削除されているものは検証しない
            if (addressCorresponGroup.getMode() == UpdateMode.DELETE) {
                continue;
            }

            // 宛先種別がCcのみ
            if (AddressType.CC.equals(addressCorresponGroup.getAddressType())) {
                // 活動単位が重複している場合はエラー #683
                if (corresponGroupList.contains(
                        addressCorresponGroup.getCorresponGroup().getId())) {
                    log.warn(ApplicationMessageCode.DUPLICATED_CC_GROUP + " "
                        + addressCorresponGroup.getCorresponGroup().getName());
                    throw new ServiceAbortException(ApplicationMessageCode.DUPLICATED_CC_GROUP,
                        (Object) addressCorresponGroup.getCorresponGroup().getName());
                }
                corresponGroupList.add(addressCorresponGroup.getCorresponGroup().getId());

                // Cc①②のチェック
                doValidateCorresponGroup(
                    correspon.getProjectId(), addressCorresponGroup.getCorresponGroup());

                List<AddressUser> addressUserList = addressCorresponGroup.getUsers();
                if (addressUserList != null) {
                    for (AddressUser au : addressUserList) {
                        if (log.isDebugEnabled()) {
                            log.debug(">> getAddressUserType[" + au.getAddressUserType() + "]");
                        }
                        // 宛先-ユーザ種別がUserのみ
                        if (AddressUserType.NORMAL_USER.equals(au.getAddressUserType())) {
                            validateCorresponCcUser(au, empNos, projectUsers);
                        }
                    }
                }
            }
        }
    }

    /**
     * To①②/Cc①②/From①②チェック.
     *
     * @param corresponProjectId
     *            コレポン文書のprojectId
     * @param corresponGroup
     *            チェックしたい活動単位コレポン文書のprojectId
     * @throws ServiceAbortException 検証エラー
     */
    private void doValidateCorresponGroup(
        String corresponProjectId, CorresponGroup corresponGroup) throws ServiceAbortException {
        // To/Cc/From①選択された活動単位がマスタに存在しない際はエラー
        log.trace("★ToCcFrom①");
        CorresponGroup sourceCorresponGroup;
        if (log.isDebugEnabled()) {
            log.debug("corresponGroup.getId["
                + corresponGroup.getId() + "]");
        }
        try {
            sourceCorresponGroup = findCorresponGroup(corresponGroup.getId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + " " + corresponGroup);
            throw new ServiceAbortException(ApplicationMessageCode
                .CANNOT_PERFORM_BECAUSE_GROUP_NOT_EXIST,
                (Object) corresponGroup.getName());
        }

        // To/Cc/From②選択された活動単位が同一プロジェクトに属していない際はエラー
        log.trace("★ToCcFrom②");
        if (log.isDebugEnabled()) {
            log.debug("correspon.getProjectId[" + corresponProjectId + "]");
            log.debug("sourceCorresponGroup.getProjectId["
                + sourceCorresponGroup.getProjectId() + "]");
        }
        if (!StringUtils.equals(
            sourceCorresponGroup.getProjectId(), corresponProjectId)) {
            throw new ServiceAbortException(ApplicationMessageCode.INVALID_GROUP,
                (Object) corresponGroup.getName());
        }
    }

    /**
     * To③④(ユーザの存在有無、プロジェクト所属)チェック 共通処理.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「To③④」に記述された一連のチェックのうち<br />
     * 共通の処理を行う
     * </p>
     * @throws ServiceAbortException ユーザがコレポンと同一プロジェクトに存在していない
     */
    private void validateCorresponToAttention(AddressUser au, List<String> empNos,
            List<ProjectUser> projectUsers) throws ServiceAbortException {
        if (!empNos.contains(au.getUser().getEmpNo())) {
            // マスタに存在しないためエラーとする
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ATTENTION_NOT_EXIST,
                (Object) au.getUser().getLabel());
        }

        if (isSystemAdmin(au.getUser())) {
            return;
        }

        boolean find = false;
        for (ProjectUser pu : projectUsers) {
            if (pu.getUser().getEmpNo().equals(au.getUser().getEmpNo())) {
                find = true;
                break;
            }
        }

        // プロジェクトに所属しないためエラーとする
        if (!find) {
            log.warn(">> AddressUser[" + au.getUser().getEmpNo() + "]");
            throw new ServiceAbortException(
                ApplicationMessageCode.INVALID_ATTENTION, (Object) au.getUser().getLabel());
        }
    }

    /**
     * Cc③④(ユーザの存在有無、プロジェクト所属)チェック 共通処理.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「Cc③④」に記述された一連のチェックのうち<br />
     * 共通の処理を行う
     * </p>
     * @throws ServiceAbortException ユーザがコレポンと同一プロジェクトに存在していない
     */
    private void validateCorresponCcUser(AddressUser au, List<String> empNos,
            List<ProjectUser> projectUsers) throws ServiceAbortException {
        if (!empNos.contains(au.getUser().getEmpNo())) {
            // マスタに存在しないためエラーとする
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_USER_NOT_EXIST,
                (Object) au.getUser().getLabel());
        }

        if (isSystemAdmin(au.getUser())) {
            return;
        }

        boolean find = false;
        for (ProjectUser pu : projectUsers) {
            if (pu.getUser().getEmpNo().equals(au.getUser().getEmpNo())) {
                find = true;
                break;
            }
        }

        // プロジェクトに所属しないためエラーとする
        if (!find) {
            throw new ServiceAbortException(
                ApplicationMessageCode.INVALID_USER, (Object) au.getUser().getLabel());
        }
    }

    /**
     * コレポン文書タイプのチェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「Correspondence Type①②」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     *
     * @throws ServiceAbortException 検証エラー
     */
    private void doValidateCorresponType(Correspon correspon) throws ServiceAbortException {
        // ①コレポン文書種別マスタに存在しない際はエラー
        log.trace("★Correspondence Type①");
        CorresponType corresponType = correspon.getCorresponType();
        if (log.isDebugEnabled()) {
            log.debug("corresponType.getId[" + corresponType.getId() + "]");
        }
        try {
            findCorresponType(corresponType.getId());
        } catch (RecordNotFoundException e) {
            log.warn(e.getMessageCode() + " corresponType.getId["
                + corresponType.getId() + "]");
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORRESPON_TYPE_NOT_EXIST);
        }

        // ②選択されたコレポン文書種別が同一プロジェクトに属していない際はエラー
        log.trace("★Correspondence Type②");
        if (log.isDebugEnabled()) {
            log.debug("corresponType.getProjectId[" + corresponType.getProjectId() + "]");
        }
        try {
            findCorresponTypeByIdProjectId(corresponType.getId(), correspon.getProjectId());
        } catch (RecordNotFoundException e) {
            log.warn(ApplicationMessageCode.INVALID_CORRESPON_TYPE
                + " corresponType[" + corresponType + "]"
                + " corresponType.getId[" + corresponType.getId() + "]"
                + " correspon.getProjectId[" + correspon.getProjectId() + "]");
            throw new ServiceAbortException(ApplicationMessageCode.INVALID_CORRESPON_TYPE
                , (Object) corresponType.getCorresponType());
        }
    }

    /**
     * 同じ添付ファイルが選択されているかのチェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「Attachments」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 同じ添付ファイルが選択されている
     */
    private void doValidateAttachments(Correspon correspon) throws ServiceAbortException {
        if (correspon.getUpdateAttachments() == null) {
            return;
        }
        log.trace("★Attachment");
        //  新規登録・削除・保存済の添付ファイルを走査し、
        //  ファイル名に重複がないか検証する
        Set<String> fileNames = new HashSet<>();
        for (Attachment a : correspon.getUpdateAttachments()) {
            switch (a.getMode()) {
            case DELETE:
                //  削除されるファイルは無視
                break;
            case NONE:
            case NEW:
                //  保存済ファイル・新規登録ファイルで名前が重複していればエラー
                if (fileNames.contains(a.getFileName())) {
                    log.warn(ApplicationMessageCode.FILE_ALREADY_ATTACHED
                        + " " + correspon.getUpdateAttachments());
                    throw new ServiceAbortException(
                        ApplicationMessageCode.FILE_ALREADY_ATTACHED,
                        (Object) a.getFileName());
                }
                //  次のチェックのため名前を保存
                fileNames.add(a.getFileName());
                break;
            case UPDATE:
            default:
                //  添付ファイルの更新はあり得ないので、ここでは何もしない
                break;
            }
        }
    }

    /**
     * カスタムフィールドのチェック.
     * <p>
     * チェック定義書の「コレポン文書Validateチェック」の「Field①②」に記述された一連のチェックを行う
     * </p>
     *
     * @param correspon
     *            コレポン文書情報
     * @throws ServiceAbortException 検証エラー
     */
    private void doValidateCustomField(Correspon correspon) throws ServiceAbortException {
        log.trace("●Field(フィールド数だけ)");
        List<CustomField> fields = findProjectCustomFields(correspon.getProjectId());
        validateCustomField(fields,
                            correspon.getCustomField1Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField2Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField3Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField4Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField5Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField6Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField7Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField8Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField9Id()
        );
        validateCustomField(fields,
                            correspon.getCustomField10Id()
        );

//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_1,
//            correspon.getCustomField1Id(), correspon.getCustomField1Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_2,
//            correspon.getCustomField2Id(), correspon.getCustomField2Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_3,
//            correspon.getCustomField3Id(), correspon.getCustomField3Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_4,
//            correspon.getCustomField4Id(), correspon.getCustomField4Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_5,
//            correspon.getCustomField5Id(), correspon.getCustomField5Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_6,
//            correspon.getCustomField6Id(), correspon.getCustomField6Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_7,
//            correspon.getCustomField7Id(), correspon.getCustomField7Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_8,
//            correspon.getCustomField8Id(), correspon.getCustomField8Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_9,
//            correspon.getCustomField9Id(), correspon.getCustomField9Value());
//        validateCustomFieldData(correspon, CUSTOM_FIELD_NO_10,
//            correspon.getCustomField10Id(), correspon.getCustomField10Value());
    }

    private List<CustomField> findProjectCustomFields(String projectId) {
        SearchCustomFieldCondition c = new SearchCustomFieldCondition();
        c.setProjectId(projectId);

        CustomFieldDao dao = getDao(CustomFieldDao.class);
        return dao.findByProjectId(c);
    }

    /**
     * カスタムフィールドがマスタに存在するかチェックする.
     * @param master 現時点でのカスタムフィールドマスタレコード
     * @param projectCustomFieldId 入力されたカスタムフィールドのID(project_custom_field.id)
     * @throws ServiceAbortException 検証エラー
     */
    private void validateCustomField(
            List<CustomField> master,
            Long projectCustomFieldId)
            throws ServiceAbortException {

        if (projectCustomFieldId == null) {
            log.trace("★Field コレポンにカスタムフィールドが設定されていないので処理無し");
            return;
        }

        boolean found = false;
        for (CustomField f : master) {
            if (f.getProjectCustomFieldId().equals(projectCustomFieldId)) {
                found = true;
                break;
            }
        }
        //  マスタに見つからなかった
        if (!found) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CUSTOM_FIELD_NOT_EXIST);
        }
    }

    /**
     * 指定されたユーザーが参照可能なプロジェクトを返す.
     * @return 指定されたユーザーが参照可能なプロジェクトの一覧
     */
    private List<Project> getAccessibleProjects(User user) {
        ProjectDao dao = getDao(ProjectDao.class);
        return dao.findByEmpNo(user.getEmpNo());
    }

    /**
     * コレポン文書を返す.
     * @param id コレポンid
     * @return コレポン文書
     * @throws RecordNotFoundException レコードが見つからない
     */
    private Correspon findCorrespon(Long id) throws RecordNotFoundException {
        CorresponDao dao = getDao(CorresponDao.class);
        return dao.findById(id);
    }

    /**
     * マスタから取得した活動単位(CorresponGroup)を返す.
     * @param id id
     * @return 活動単位
     * @throws RecordNotFoundException 指定IDの活動単位がマスタに存在しない
     */
    private CorresponGroup findCorresponGroup(Long id)
    throws RecordNotFoundException {
        CorresponGroupDao dao = getDao(CorresponGroupDao.class);
        return dao.findById(id);
    }

    /**
     * コレポン文書種別(CorresponType)を返す.
     * @param id id
     * @return コレポン文書種別
     * @throws RecordNotFoundException レコードが見つからない
     */
    private CorresponType findCorresponType(Long id)
    throws RecordNotFoundException {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        return dao.findById(id);
    }

    /**
     * コレポン文書種別(CorresponType)を返す.
     * @param id id
     * @param projectId projectId
     * @return コレポン文書種別
     * @throws RecordNotFoundException レコードが見つからない
     */
    private CorresponType findCorresponTypeByIdProjectId(Long id, String projectId)
    throws RecordNotFoundException {
        CorresponTypeDao dao = getDao(CorresponTypeDao.class);
        return dao.findByIdProjectId(id, projectId);
    }

    /**
     * 全ユーザーの従業員番号を返す.
     * @return 全ユーザーの従業員番号
     * @throws RecordNotFoundException レコードが見つからない
     */
    private List<String> findEmpNo() throws RecordNotFoundException {
        UserDao dao = getDao(UserDao.class);
        return dao.findEmpNo();
    }

    /**
     * プロジェクトに所属するユーザーを返す.
     * @return ユーザー
     * @throws RecordNotFoundException レコードが見つからない
     */
    private List<ProjectUser> findUserByProjectId(String projectId)
    throws RecordNotFoundException {
        UserDao dao = getDao(UserDao.class);
        SearchUserCondition condition = new SearchUserCondition();
        condition.setProjectId(projectId);
        return dao.findProjectUser(condition);
    }
}
