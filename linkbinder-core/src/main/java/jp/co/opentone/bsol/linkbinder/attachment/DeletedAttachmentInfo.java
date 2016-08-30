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

/**
 * システムに登録されていたが、削除された添付ファイル.
 * @author opentone
 */
public class DeletedAttachmentInfo extends AttachmentInfo {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 924497660263446928L;

    /** 削除された添付ファイル. */
    private Attachment attachment;

    /**
     * 削除された添付ファイルを指定してインスタンス化する.
     * @param attachment 添付ファイル
     */
    public DeletedAttachmentInfo(Attachment attachment) {
        populate(attachment);
        this.attachment = attachment;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#getContent()
     */
    @Override
    public byte[] getContent() throws ServiceAbortException {
        //  削除するファイルの内容を取得しても意味無いのでnullを返す
        return null;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#toAttachment()
     */
    @Override
    public Attachment toAttachment() throws ServiceAbortException {
        attachment.setMode(UpdateMode.DELETE);
        return attachment;
    }
}
