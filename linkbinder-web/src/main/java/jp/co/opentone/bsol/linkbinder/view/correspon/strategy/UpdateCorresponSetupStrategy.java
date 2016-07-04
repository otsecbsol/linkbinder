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

import java.util.List;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#UPDATE}に対応する.
 * @author opentone
 */
public class UpdateCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -1903527504303843016L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        Correspon correspon = page.getCorresponService().find(page.getId());
        // 初期表示チェック
        validateUpdate(correspon);
        page.setCorrespon(correspon);
        applyOriginalAttachmentsToPage(correspon);
    }

    private void validateUpdate(Correspon correspon) throws ServiceAbortException {
        if ((!page.isSystemAdmin() && !page.isProjectAdmin(page.getCurrentProjectId())
                && !page.isAnyGroupAdmin(correspon))
                    && correspon.getWorkflowStatus() == WorkflowStatus.ISSUED) {
            throw new ServiceAbortException(
                ApplicationMessageCode.CANNOT_PERFORM_BECAUSE_CORREPON_ALREADY_ISSUED);
        }
    }

    private void applyOriginalAttachmentsToPage(Correspon c) {
        List<Attachment> attachments = c.getAttachments();
        page.clearAttachments();
        for (Attachment a : attachments) {
            AttachmentInfo info = new SavedAttachmentInfo(a, page.getCorresponService());
            page.addAttachment(info);
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#getPageTitle()
     */
    @Override
    protected String getPageTitle() {
        return CorresponEditPage.UPDATE;
    }
}
