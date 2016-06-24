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
package jp.co.opentone.bsol.linkbinder.view.correspon.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.CopiedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.AddressCorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroupUser;
import jp.co.opentone.bsol.linkbinder.dto.ProjectUser;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponGroupCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#REVISE}に対応する.
 * @author opentone
 *
 * <p>
 * $Date: 2011-06-10 18:38:27 +0900 (金, 10  6 2011) $
 * $Rev: 4074 $
 * $Author: nemoto $
 */
public class ReviseCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5245364070604309566L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        return CorresponEditPage.NEW;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        Correspon revise = new Correspon();
        revise.setProjectId(page.getCurrentProjectId());
        revise.setProjectNameE(page.getCurrentProject().getNameE());
        revise.setWorkflowStatus(WorkflowStatus.DRAFT);

        // 改訂元文書を取得し、改訂文書に内容を引き継ぐ
        Correspon org = getOriginalCorrespon();
        transferRevise(org, revise);
        applyOriginalAttachmentsToPage(org);

        //  新規登録なのでIDをクリア
        page.setId(null);
        revise.setCorresponStatus(page.getDefaultStatus());
        page.setCorrespon(revise);
    }

    private void applyOriginalAttachmentsToPage(Correspon org) {
        List<Attachment> attachments = org.getAttachments();
        page.clearAttachments();
        for (Attachment a : attachments) {
            AttachmentInfo info = new CopiedAttachmentInfo(a, page.getCorresponService());
            page.addAttachment(info);
        }
    }

    /**
     * 改訂元文書の値を改訂文書に引き継ぐ.
     * @param org 改訂元文書
     * @param revise 改訂文書
     */
    private void transferRevise(Correspon org, Correspon revise) throws ServiceAbortException {
        transferRevisionNo(org, revise);
        transferFrom(org, revise);
        transferValues(org, revise);
        transferAttachemtn(org, revise);
        transferReplyCustomField(org, revise);
    }

    private void transferRevisionNo(Correspon org, Correspon revise) {
        revise.setPreviousRevCorresponId(org.getId());
        revise.setPreviousRevCorresponNo(org.getCorresponNo());
    }

    private void transferFrom(Correspon org, Correspon revise) throws ServiceAbortException {
        //  ユーザーのデフォルト活動単位か、
        //  ユーザーが所属する活動単位のうち一番最初のもの
        ProjectUser pu = page.getCurrentProjectUser();
        if (pu != null && pu.getDefaultCorresponGroup() != null) {
            revise.setFromCorresponGroup(pu.getDefaultCorresponGroup());
        } else {
            CorresponGroup selected = getUserPrimaryCorresponGroup();
            if (selected != null) {
                revise.setFromCorresponGroup(selected);
            } else {
                //  見つからない場合は改訂元の活動単位をセットしておく
                revise.setFromCorresponGroup(org.getFromCorresponGroup());
            }
        }
    }

    private CorresponGroup getUserPrimaryCorresponGroup() throws ServiceAbortException {
        // 送信元活動単位を取得、選択リスト生成
        List<CorresponGroup> groups = null;
        if (page.isSystemAdmin()) {
            groups = findProjectCorresponGroups();
        } else {
            groups = findUserCorresponGroups();
        }
        return (groups.size() > 0) ? groups.get(0) : null;
    }

    private List<CorresponGroup> findProjectCorresponGroups() throws ServiceAbortException {
        SearchCorresponGroupCondition condition = new SearchCorresponGroupCondition();
        condition.setProjectId(page.getCurrentProjectId());
        return page.getCorresponGroupService().search(condition);
    }

    private List<CorresponGroup> findUserCorresponGroups() throws ServiceAbortException {
        String projectId = page.getCurrentProjectId();
        String empNo = page.getCurrentUser().getEmpNo();

        List<CorresponGroupUser> corresponGroupUsers =
            page.getUserService().searchCorrseponGroup(projectId, empNo);

        return findLoginUserGroups(corresponGroupUsers);
    }

    /**
     * ログインユーザーが所属する活動単位取得.
     * @param addressCorresponGroup
     * @return CorresponGroup List
     */
    private List<CorresponGroup> findLoginUserGroups(
        List<CorresponGroupUser> corresponGroupUsers) {
        List<CorresponGroup> groupLists = new ArrayList<CorresponGroup>();
        if (corresponGroupUsers == null) {
            return groupLists;
        }
        for (CorresponGroupUser correponGroupUser : corresponGroupUsers) {
            CorresponGroup group = new CorresponGroup();
            group.setId(correponGroupUser.getCorresponGroup().getId());
            group.setName(correponGroupUser.getCorresponGroup().getName());

            groupLists.add(group);
        }
        return groupLists;
    }

    private void transferValues(Correspon org, Correspon revise) {
        List<AddressCorresponGroup> addresses = org.getAddressCorresponGroups();
        UpdateMode.setUpdateMode(addresses, UpdateMode.NEW);
        clearRepliedInformationFromAddresses(org);

        revise.setAddressCorresponGroups(addresses);

        revise.setParentCorresponId(org.getParentCorresponId());
        revise.setParentCorresponNo(org.getParentCorresponNo());
        revise.setCorresponType(org.getCorresponType());
        revise.setReplyRequired(org.getReplyRequired());
        revise.setDeadlineForReply(org.getDeadlineForReply());
        revise.setSubject(org.getSubject());
        revise.setBody(org.getBody());
    }

    private void transferAttachemtn(Correspon org, Correspon revise) {
        revise.setFile1Id(org.getFile1Id());
        revise.setFile1FileId(org.getFile1FileId());
        revise.setFile1FileName(org.getFile1FileName());
        revise.setFile2Id(org.getFile2Id());
        revise.setFile2FileId(org.getFile2FileId());
        revise.setFile2FileName(org.getFile2FileName());
        revise.setFile3Id(org.getFile3Id());
        revise.setFile3FileId(org.getFile3FileId());
        revise.setFile3FileName(org.getFile3FileName());
        revise.setFile4Id(org.getFile4Id());
        revise.setFile4FileId(org.getFile4FileId());
        revise.setFile4FileName(org.getFile4FileName());
        revise.setFile5Id(org.getFile5Id());
        revise.setFile5FileId(org.getFile5FileId());
        revise.setFile5FileName(org.getFile5FileName());
    }

    //カスタムフィールドの項目数が多いため行数オーバーの警告が出るが、仕方が無いので警告を抑制する
    //CHECKSTYLE:OFF
    private void transferReplyCustomField(Correspon org, Correspon revise) {
    //CHECKSTYLE:ON
        revise.setCustomField1Id(org.getCustomField1Id());
        revise.setCustomField1Label(org.getCustomField1Label());
        revise.setCustomField1Value(org.getCustomField1Value());
        revise.setCustomField2Id(org.getCustomField2Id());
        revise.setCustomField2Label(org.getCustomField2Label());
        revise.setCustomField2Value(org.getCustomField2Value());
        revise.setCustomField3Id(org.getCustomField3Id());
        revise.setCustomField3Label(org.getCustomField3Label());
        revise.setCustomField3Value(org.getCustomField3Value());
        revise.setCustomField4Id(org.getCustomField4Id());
        revise.setCustomField4Label(org.getCustomField4Label());
        revise.setCustomField4Value(org.getCustomField4Value());
        revise.setCustomField5Id(org.getCustomField5Id());
        revise.setCustomField5Label(org.getCustomField5Label());
        revise.setCustomField5Value(org.getCustomField5Value());
        revise.setCustomField6Id(org.getCustomField6Id());
        revise.setCustomField6Label(org.getCustomField6Label());
        revise.setCustomField6Value(org.getCustomField6Value());
        revise.setCustomField7Id(org.getCustomField7Id());
        revise.setCustomField7Label(org.getCustomField7Label());
        revise.setCustomField7Value(org.getCustomField7Value());
        revise.setCustomField8Id(org.getCustomField8Id());
        revise.setCustomField8Label(org.getCustomField8Label());
        revise.setCustomField8Value(org.getCustomField8Value());
        revise.setCustomField9Id(org.getCustomField9Id());
        revise.setCustomField9Label(org.getCustomField9Label());
        revise.setCustomField9Value(org.getCustomField9Value());
        revise.setCustomField10Id(org.getCustomField10Id());
        revise.setCustomField10Label(org.getCustomField10Label());
        revise.setCustomField10Value(org.getCustomField10Value());
    }

    private Correspon getOriginalCorrespon() throws ServiceAbortException {
        Correspon org = findOriginalCorrespon();
        validateOriginalCorrespon(org);
        return org;
    }

    private Correspon findOriginalCorrespon() throws ServiceAbortException {
        try {
            return page.getCorresponService().find(page.getId());
        } catch (ServiceAbortException e) {
            if (ApplicationMessageCode.NO_DATA_FOUND.equals(e.getMessageCode())) {
                throw new ServiceAbortException(
                    ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_CORRESPON_NOT_EXIST);
            } else {
                throw e;
            }
        }
    }

    private void validateOriginalCorrespon(Correspon org) throws ServiceAbortException {
        // 改訂元コレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
        if (!StringUtils.equals(page.getCurrentProjectId(), org.getProjectId())) {
            throw new ServiceAbortException(
                "Project ID is invalid.",
                ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                org.getCorresponNo());
        }
        // 改訂元コレポン文書の文書状態がCanceledでなければエラー
        if (org.getCorresponStatus() != CorresponStatus.CANCELED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID);
        }
    }
}
