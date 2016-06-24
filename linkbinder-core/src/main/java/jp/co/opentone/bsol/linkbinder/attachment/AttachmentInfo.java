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

import java.io.File;
import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.linkbinder.dto.Attachment;

/**
 * 添付ファイル情報.
 * @author opentone
 */
public abstract class AttachmentInfo implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8313870328263264878L;
    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(AttachmentInfo.class);

    /**
     * クライアントにダウンロードするファイルの名前.
     */
    private String fileName;

    /**
     * ファイルID.未登録ファイルの場合は空.
     */
    private Long fileId;

    /**
     * 保存対象ファイルの一時保存先.
     */
    private String sourcePath;

    /**
     * 空のインスタンスを生成する.
     */
    public AttachmentInfo() {
    }

    /**
     * ダウンロードするファイルの内容を返す.
     * @return ファイルの内容
     * @throws ServiceAbortException ファイルの内容取得に失敗
     */
    public abstract byte[] getContent() throws ServiceAbortException;

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileId the fileId to set
     */
    public void setFileId(Long fileId) {
        this.fileId = fileId;
    }

    /**
     * @return the fileId
     */
    public Long getFileId() {
        return fileId;
    }

    /**
     * @param sourcePath the sourcePath to set
     */
    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    /**
     * @return the sourcePath
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * このオブジェクトが保持する情報をデータベース登録用の添付ファイル情報に変換する.
     * @return データベース登録用添付ファイル
     * @throws ServiceAbortException 変換に失敗
     */
    public abstract Attachment toAttachment() throws ServiceAbortException;

    /**
     * 作成済の一時ファイルがあれば削除する.
     */
    public void delete() {
        if (StringUtils.isEmpty(getSourcePath())) {
            return;
        }

        File tmp = new File(getSourcePath());
        if (tmp.exists()) {
            if (!tmp.delete()) {
                log.warn("A Temporary file {} not deleted.", getSourcePath());
            }
        }
    }
}
