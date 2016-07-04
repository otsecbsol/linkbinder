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
package jp.co.opentone.bsol.linkbinder.view.admin.attachment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.action.AbstractAction;
import jp.co.opentone.bsol.linkbinder.attachment.AttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.NewAttachmentInfo;
import jp.co.opentone.bsol.linkbinder.attachment.UploadedFile;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.util.AttachmentUtil;
import jp.co.opentone.bsol.linkbinder.view.admin.module.dataimport.MasterDataImportablePage;

/**
 * インポートファイルをアップロードするアクション.
 * @author opentone
 */
public class MasterSettingFileUploadAction extends AbstractAction {
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = -654425982165433120L;

    /**
     * このアクションが発生したリクエストでアップロードされたファイルが格納されたプロパティ名.
     */
    public static final String PROP_UPLOADED = "importFile";

    /** アクションが発生したページ. */
    private MasterDataImportablePage page;

    /**
     * ページを指定してインスタンス化する.
     * @param page アップロードアクションが発生したページ
     */
    public MasterSettingFileUploadAction(MasterDataImportablePage page) {
        super(page);
        this.page = page;
    }

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.framework.action.Action#execute()
     */
    public void execute() throws ServiceAbortException {
        // アップロード処理
        processUpload();
    }

    private void processUpload() throws ServiceAbortException {
        UploadedFile file = getUploadedFile();
        if (file != null) {
            //  新しいインポートファイルがアップロードされた
            try {
                processUploadedFile(file);
            } catch (IOException e) {
                throw new ServiceAbortException(
                        e.getMessage(), e, ApplicationMessageCode.ERROR_UPLOADING_IMPORT_FILE);
            }
        } else {
            throw new ServiceAbortException(ApplicationMessageCode.ERROR_IMPORT_FILE_NONE);
        }
    }

    private void processUploadedFile(UploadedFile file)
            throws IOException, ServiceAbortException {
        if (!UploadedFile.KEY_SIZE_ZERO.equals(file.getKey())) {
            String temp = AttachmentUtil.createTempporaryFile(file.getKey());
            AttachmentInfo importAttach = new NewAttachmentInfo(file.getFilename(), temp);
            page.setImportFileInfo(importAttach);
        }
    }

    private UploadedFile getUploadedFile() {
        try {
            UploadedFile f = (UploadedFile) PropertyUtils.getProperty(page, PROP_UPLOADED);
            return (f != null && f.getFileSize() != null) ? f : null;
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }
}
