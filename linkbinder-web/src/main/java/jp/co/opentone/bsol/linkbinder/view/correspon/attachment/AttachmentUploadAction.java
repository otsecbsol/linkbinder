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
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.DeletedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.NewAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.SavedAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.util.AttachmentUtil;
import jp.co.opentone.bsol.linkbinder.view.correspon.CorresponEditPage;

/**
 * コレポン文書の添付ファイルをアップロードするアクション.
 * @author opentone
 */
public class AttachmentUploadAction extends AbstractAction {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -8007158517065713146L;

    /**
     * このアクションが発生したリクエストでアップロードされたファイルが格納されたプロパティ名.
     */
    public static final String PROP_UPLOADED = "attachment%s";
    /**
     * このアクションが発生したリクエストで添付ファイルが削除されたことを表すプロパティ名.
     */
    public static final String PROP_ATTCHMENT_DELETED = "attachmentDeleted%s";

    /** アクションが発生したページ. */
    private CorresponEditPage page;

    /**
     * ページを指定してインスタンス化する.
     * @param page アップロードアクションが発生したページ
     */
    public AttachmentUploadAction(CorresponEditPage page) {
        super(page);
        this.page = page;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.action.Action#execute()
     */
    public void execute() throws ServiceAbortException {
        for (int i = 0; i < page.getMaxAttachmentsSize(); i++) {
            processUpload(i);
        }

        for (int i = page.getMaxAttachmentsSize(); i > 0; i--) {
            //  ユーザーの操作によりアップロード済ファイルが削除された
            if (isAttachmentDeletedAt(i - 1).booleanValue()) {
                removeAttachmentAt(i - 1);
            }
        }
    }

    private void processUpload(int index) throws ServiceAbortException {
        UploadedFile file = getUploadedFileAt(index);
        if (file != null) {
            //  新しい添付ファイルがアップロードされた
            try {
                processUploadedFile(file, index);
            } catch (IOException e) {
                throw new ServiceAbortException(
                        e.getMessage(), e, ApplicationMessageCode.ERROR_UPLOADING_FILE);
            }
        }
    }

    private void processUploadedFile(UploadedFile file, int index)
            throws IOException, ServiceAbortException {
        if (StringUtils.isNotEmpty(file.getKey())
                && !UploadedFile.KEY_SIZE_ZERO.equals(file.getKey())) {
            String temp = AttachmentUtil.createTempporaryFile(file.getKey());
            AttachmentInfo newAttachment
                = new NewAttachmentInfo(file.getFilename(), temp);

            page.addAttachment(newAttachment);
//            if (page.getAttachments().size() > index) {
//                removeAttachmentAt(index);
//                page.addAttachmentAt(index, newAttachment);
//            } else {
//                page.addAttachment(newAttachment);
//            }
        }
    }

    private void removeAttachmentAt(int index) throws ServiceAbortException {
        if (page.getMaxAttachmentsSize() > index) {
            AttachmentInfo current = page.getAttachmentAt(index);
            if (current instanceof SavedAttachmentInfo) {
                DeletedAttachmentInfo deleted =
                        new DeletedAttachmentInfo(current.toAttachment());
                page.addRemovedAttachment(deleted);
            }
            page.removeAttachmentAt(index);
        }
    }

    private UploadedFile getUploadedFileAt(int index) {
        try {
            UploadedFile f = (UploadedFile) PropertyUtils.getProperty(page,
                    String.format(PROP_UPLOADED, index + 1));

            return (f != null && f.getFileSize() != null) ? f : null;

        } catch (IllegalAccessException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private Boolean isAttachmentDeletedAt(int index) {
        List<Boolean> attachmentDeleted = page.getAttachmentDeletedList();
        if (attachmentDeleted != null && attachmentDeleted.size() > index) {
            // attachmentDeletedで返されるClassがStringな為、一旦Objectクラスに変換
            Object rt = attachmentDeleted.get(index);
            return Boolean.valueOf(rt.toString());
        }
        return Boolean.FALSE;
    }
}
