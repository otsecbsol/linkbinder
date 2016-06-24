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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode;

/**
 * 新しく追加された添付ファイル.
 * @author opentone
 */
public class NewAttachmentInfo extends AttachmentInfo {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7936440001271091490L;

    /**
     * 新しい添付ファイルの情報を指定してインスタンス化する.
     * @param fileName ファイル名
     * @param sourcePath ファイルの一時保存先
     */
    public NewAttachmentInfo(String fileName, String sourcePath) {
        setFileName(fileName);
        setFileId(null);
        setSourcePath(sourcePath);
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#getContent()
     */
    @Override
    public byte[] getContent() throws ServiceAbortException {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(getSourcePath()));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            //CHECKSTYLE:OFF
            byte[] buf = new byte[1024 * 4];
            //CHECKSTYLE:ON
            int len;
            while ((len = in.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new ServiceAbortException(MessageCode.E_DOWNLOAD_FAILED);
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                throw new ServiceAbortException(MessageCode.E_DOWNLOAD_FAILED);
            }
        }
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.view.correspon.attachment.AttachmentInfo#toAttachment()
     */
    @Override
    public Attachment toAttachment() throws ServiceAbortException {
        Attachment a = new Attachment();
        a.setMode(UpdateMode.NEW);
        a.setFileId(null);
        a.setFileName(getFileName());
        a.setContent(getContent());
        a.setSourcePath(getSourcePath());

        return a;
    }
}
