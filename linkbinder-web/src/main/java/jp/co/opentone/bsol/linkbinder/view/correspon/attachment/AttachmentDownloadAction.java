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
package jp.co.opentone.bsol.linkbinder.view.correspon.attachment;

import java.io.IOException;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;

/**
 * コレポン文書の添付ファイルをダウンロードするアクション.
 * @author opentone
 */
public class AttachmentDownloadAction extends AbstractAction {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 5903241342060850166L;
    /** ダウンロードアクションを持つページ. */
    private AttachmentDownloadablePage page;
    /**
     * @param page アクションが発生したページ
     */
    public AttachmentDownloadAction(AttachmentDownloadablePage page) {
        super(page);
        this.page = page;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.action.Action#execute()
     */
    public void execute() throws ServiceAbortException {
        AttachmentInfo downloading = page.getDownloadingAttachmentInfo();
        if (downloading == null) {
            throw new ApplicationFatalRuntimeException("DownloadInfo is null.");
        }
        doDownload(downloading);
    }

    private void doDownload(AttachmentInfo downloading) throws ServiceAbortException {
        byte[] content = downloading.getContent();
        if (content != null) {
            try {
                page.getViewHelper().download(downloading.getFileName(), content, true);
            } catch (IOException e) {
                throw new ServiceAbortException("Download failed.", e,
                    MessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }
}
