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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.beanutils.PropertyUtils;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.correspon.CorresponService;
import jp.co.opentone.bsol.linkbinder.util.AttachmentUtil;

/**
 * 複写された添付ファイル.
 * @author opentone
 */
public class CopiedAttachmentInfo extends AttachmentInfo {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8719968412413724203L;

    /**
     * 一時ファイルのBASENAME.
     */
    private static final String TMPFILE_BASENAME = "copy.tmp";

    /**
     * 添付ファイル.
     */
    private Attachment attachment;
    /**
     * コレポン文書サービス.
     */
    private CorresponService service;

    /**
     * 複写元の添付ファイルを指定してインスタンス化する.
     * @param attachment 複写元の添付ファイル.
     * @param service コレポン文書サービス
     */
    public CopiedAttachmentInfo(Attachment attachment, CorresponService service) {
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
            Attachment org =
                service.findAttachment(attachment.getCorresponId(), attachment.getId());
            attachment.setContent(org.getContent());
            //  一時ファイルとして保存
            try {
                attachment.setSourcePath(createTemporaryFile(org));
                setSourcePath(attachment.getSourcePath());
            } catch (IOException e) {
                throw new ServiceAbortException(
                            e.getMessage(),
                            e,
                            ApplicationMessageCode.ERROR_GETTING_FILE);
            }
        }
        return attachment.getContent();
    }

    private String createTemporaryFile(
                //CHECKSTYLE:OFF フィールドと同名の警告が出るが、ここではこの名前が最も適切
                Attachment attachment)
                //CHECKSTYLE:ON
                    throws IOException {
        String randomId = String.valueOf(attachment.hashCode());
        InputStream in = new ByteArrayInputStream(attachment.getContent());

        return AttachmentUtil.createTempporaryFile(randomId, TMPFILE_BASENAME, in);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#toAttachment()
     */
    @Override
    public Attachment toAttachment() throws ServiceAbortException {
        Attachment a = new Attachment();
        try {
            PropertyUtils.copyProperties(a, this.attachment);
        } catch (Exception e) {
            throw new ApplicationFatalRuntimeException(e);
        }
        a.setMode(UpdateMode.NEW);
        if (a.getContent() == null) {
            a.setContent(getContent());
            a.setSourcePath(getSourcePath());
        }

        return a;
    }
}
