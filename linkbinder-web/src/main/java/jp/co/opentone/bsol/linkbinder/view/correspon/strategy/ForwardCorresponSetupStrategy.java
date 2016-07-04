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

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.ReplyRequired;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.util.view.correspon.CorresponPageFormatter;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#FORWARD}に対応する.
 * @author opentone
 */
public class ForwardCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7447335550688898192L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        Correspon forward = new Correspon();
        setupBase(forward);

        //  転送元コレポン文書を取得
        Correspon correspon = page.getCorresponService().find(page.getId());

        //  添付ファイルの内容をページに反映
        applyOriginalAttachmentsToPage(correspon);

        // 検索条件として使用したので値をクリア
        page.setId(null);
        transferValues(correspon, forward);
        forward.setCorresponStatus(page.getDefaultStatus());
        page.setCorrespon(forward);
    }

    protected void setupBase(Correspon forward) {
        forward.setProjectId(page.getCurrentProjectId());
        forward.setProjectNameE(page.getCurrentProject().getNameE());
        forward.setWorkflowStatus(WorkflowStatus.DRAFT);
        forward.setReplyRequired(ReplyRequired.NO);
    }

    private void applyOriginalAttachmentsToPage(Correspon org) {
        if (page.isAttachment1Transfer()) {
            addAttachmentToPage(org, org.getFile1Id(), org.getFile1FileName());
        }
        if (page.isAttachment2Transfer()) {
            addAttachmentToPage(org, org.getFile2Id(), org.getFile2FileName());
        }
        if (page.isAttachment3Transfer()) {
            addAttachmentToPage(org, org.getFile3Id(), org.getFile3FileName());
        }
        if (page.isAttachment4Transfer()) {
            addAttachmentToPage(org, org.getFile4Id(), org.getFile4FileName());
        }
        if (page.isAttachment5Transfer()) {
            addAttachmentToPage(org, org.getFile5Id(), org.getFile5FileName());
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        return CorresponEditPage.NEW;
    }

    /**
     * 依頼文書の各種値を転送文書に設定する.
     * @param request 依頼文書
     * @param forward 転送文書
     */
    private void transferValues(Correspon request, Correspon forward) {
        CorresponPageFormatter f = new CorresponPageFormatter(request);
        forward.setSubject(f.getForwardSubject());
        forward.setBody(f.getForwardBody());
    }
}
