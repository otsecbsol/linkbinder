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
package jp.co.opentone.bsol.linkbinder.attachment;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;

/**
 * 既にシステムに保存済の添付ファイル.
 * @author opentone
 */
public class SavedAttachmentInfo extends AttachmentInfo {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 6841678972933036879L;

    /**
     * 添付ファイル.
     */
    private Attachment attachment;
    /**
     * コレポン文書サービス.
     */
    private CorresponService service;

    /**
     * システムに保存済の添付ファイルオブジェクトを指定してインスタンス化する.
     * @param attachment 添付ファイル
     * @param service コレポン文書サービス
     */
    public SavedAttachmentInfo(Attachment attachment, CorresponService service) {
        populate(attachment);
        this.attachment = attachment;
        this.service = service;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#getContent()
     */
    @Override
    public byte[] getContent() throws ServiceAbortException {
        if (attachment.getContent() == null) {
            Attachment downloading =
                service.findAttachment(attachment.getCorresponId(), attachment.getId());
            attachment.setContent(downloading.getContent());
        }
        return attachment.getContent();
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#toAttachment()
     */
    @Override
    public Attachment toAttachment() throws ServiceAbortException {
        if (attachment.getContent() == null) {
            attachment.setContent(getContent());
        }
        attachment.setMode(UpdateMode.NONE);

        return attachment;
    }
}
