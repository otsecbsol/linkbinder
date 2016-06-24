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
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;
import jp.co.opentone.bsol.linkbinder.view.correspon.util.CorresponEditMode;

/**
 * コレポン文書入力画面に初期表示するコレポン文書オブジェクトを生成する.
 * 編集モード @{link {@link CorresponEditMode#COPY}に対応する.
 * @author opentone
 */
public class CopyCorresponSetupStrategy extends CorresponSetupStrategy {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5583414188530769658L;

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.util.setup.CorresponSetup#setup()
     */
    @Override
    public void setup() throws ServiceAbortException {
        //  コピー元コレポン文書を取得し、条件に利用したIDをクリア
        Correspon correspon = page.getCorresponService().find(page.getId());

        //  コピーなので元コレポン文書から不要な情報をクリア
        clearValues(correspon);

        // ワークフローステータス初期値設定
        correspon.setWorkflowStatus(WorkflowStatus.DRAFT);
        //  宛先の編集モードを新規登録に設定
        UpdateMode.setUpdateMode(correspon.getAddressCorresponGroups(), UpdateMode.NEW);

        //  添付ファイルの内容をページに反映
        applyOriginalAttachmentsToPage(correspon);

        //  新規登録となるのでIDはクリア
        correspon.setId(null);
        page.setId(null);

        page.setCorrespon(correspon);
    }

    private void clearValues(Correspon correspon) {
        correspon.setCorresponNo(null);
        correspon.setParentCorresponId(null);
        correspon.setParentCorresponNo(null);
        correspon.setPreviousRevCorresponId(null);
        correspon.setPreviousRevCorresponNo(null);

        clearRepliedInformationFromAddresses(correspon);
        clearAttachments(correspon);
    }

    private void clearAttachments(Correspon correspon) {
        if (!page.isAttachment1Transfer()) {
            correspon.setFile1Id(null);
            correspon.setFile1FileId(null);
            correspon.setFile1FileName(null);
        }
        if (!page.isAttachment2Transfer()) {
            correspon.setFile2Id(null);
            correspon.setFile2FileId(null);
            correspon.setFile2FileName(null);
        }
        if (!page.isAttachment3Transfer()) {
            correspon.setFile3Id(null);
            correspon.setFile3FileId(null);
            correspon.setFile3FileName(null);
        }
        if (!page.isAttachment4Transfer()) {
            correspon.setFile4Id(null);
            correspon.setFile4FileId(null);
            correspon.setFile4FileName(null);
        }
        if (!page.isAttachment5Transfer()) {
            correspon.setFile5Id(null);
            correspon.setFile5FileId(null);
            correspon.setFile5FileName(null);
        }
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
}
