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
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.AddressType;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#REPLY}に対応する.
 * @author opentone
 */
public class ReplyWithPreviousCorresponSetupStrategy extends ReplyCorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7392260577497403507L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        Correspon reply = new Correspon();
        setupBase(reply);

        // 返信元文書を取得し、返信文書に内容を引き継ぐ
        Correspon request = getReferencedCorrespon(page.getId());
        // 検索条件として使用したので値をクリア
        page.setId(null);

        // 引用する返信済文書を取得し、返信文書に内容を引き継ぐ
        Correspon replied = getReferencedCorrespon(page.getRepliedId());
        // 検索条件として使用したので値をクリア
        page.setRepliedId(null);

        transferReply(request, replied, reply);
        applyOriginalAttachmentsToPage(replied);
        reply.setCorresponStatus(page.getDefaultStatus());
        page.setCorrespon(reply);
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
     * 依頼文書の値を返信文書に引き継ぐ.
     * @param request 依頼文書
     * @param replied 引用する返信済文書
     * @param reply 返信文書
     */
    private void transferReply(Correspon request, Correspon replied, Correspon reply)
            throws ServiceAbortException {
        transferReplyTo(request, reply);
        transferAddress(request, replied, reply);
        transferReplyCustomField(replied, reply);
        transferValues(replied, reply);
    }

    /**
     * 引用する返信済文書の各種値を返信文書に設定する.
     * <p>
     *  通常の返信との違い
     *  <ul>
     *  <li>コレポン文書種状態を返信済文書から設定(ただし返信済文書がCANCELEDの場合は、OPENを設定)</li>
     *  <li>コレポン文書種別を返信済文書から設定</li>
     *  <li>件名、本文に引用記号を付与せず、返信済文書からそのまま設定</li>
     *  <li>返信要否・返信期限を返信済文書から設定</li>
     *  </ul>
     * </p>
     * @param replied 引用する返信済文書
     * @param reply 返信文書
     */
    private void transferValues(Correspon replied, Correspon reply) {
        if (CorresponStatus.CANCELED == replied.getCorresponStatus()) {
            reply.setCorresponStatus(CorresponStatus.OPEN);
        } else {
            reply.setCorresponStatus(replied.getCorresponStatus());
        }
        reply.setCorresponType(replied.getCorresponType());
        reply.setSubject(replied.getSubject());
        reply.setBody(replied.getBody());
        reply.setReplyRequired(replied.getReplyRequired());
        reply.setDeadlineForReply(replied.getDeadlineForReply());
    }

    /**
     * 返信文書の宛先を設定する.
     * <p>
     *  通常の返信との違い
     *  <ul>
     *  <li>Ccを返信済文書からそのまま設定</li>
     *  </ul>
     * </p>
     * @param request 依頼文書
     * @param replied 引用する返信済文書
     * @param reply 返信文書
     * @throws ServiceAbortException 設定に失敗
     */
    private void transferAddress(Correspon request, Correspon replied, Correspon reply)
            throws ServiceAbortException {
        List<AddressCorresponGroup> addresses = new ArrayList<AddressCorresponGroup>();
        addresses.add(getTo(request, reply));
        addresses.addAll(getCc(replied, reply));

        //  編集モードを新規に設定したうえでコレポン文書に設定
        UpdateMode.setUpdateMode(addresses, UpdateMode.NEW);
        reply.setAddressCorresponGroups(addresses);
        clearRepliedInformationFromAddresses(reply);
    }

    /**
     * 返信文書のCcに設定する、引用対象の返信文書のCcを返す.
     * @param replied 引用対象の返信済文書
     * @param reply 返信文書
     * @return 返信文書のCc
     * @throws ServiceAbortException 設定に失敗
     */
    private List<AddressCorresponGroup> getCc(Correspon replied, Correspon reply)
            throws ServiceAbortException {
        List<AddressCorresponGroup> cc = new ArrayList<AddressCorresponGroup>();
        for (AddressCorresponGroup ag : replied.getAddressCorresponGroups()) {
            if (ag.getAddressType() != AddressType.CC) {
                continue;
            }

            cc.add(ag);
        }
        return cc;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        return CorresponEditPage.NEW;
    }

    /**
     * 引用対象の返信文書が引用返信を許容する状態であることをチェックする.
     * <p>
     *  通常の返信との違い
     *  <ul>
     *  <li>返信済文書がCanceledでも引用返信可能</li>
     *  </ul>
     * </p>
     * @param org 返信文書
     * @throws ServiceAbortException 引用返信不可の状態
     */
    @Override
    protected void validateReplyOriginalCorrespon(Correspon org) throws ServiceAbortException {
        // 返信元コレポン文書のプロジェクトが現在選択中のプロジェクト以外はエラー
        if (!StringUtils.equals(page.getCurrentProjectId(), org.getProjectId())) {
            throw new ServiceAbortException(
                "Project ID is invalid.",
                ApplicationMessageCode.INVALID_ORIGINAL_CORRESPON,
                org.getCorresponNo());
        }
        // 返信元コレポン文書の承認状態が
        // [5：Issued]以外に関してはエラー
        if (org.getWorkflowStatus() != WorkflowStatus.ISSUED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_ORIGINAL_WORKFLOW_STATUS_INVALID);
        }
    }
}
