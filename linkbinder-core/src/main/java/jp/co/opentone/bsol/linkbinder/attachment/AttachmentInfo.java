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
import jp.co.opentone.bsol.linkbinder.dto.code.AttachmentFileType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.Serializable;

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
     * ファイル種別.
     */
    private AttachmentFileType fileType;
    /**
     * システムがファイルから抽出したテキスト.
     */
    private String orgExtractedText;
    /**
     * ファイルから抽出したテキスト.
     */
    private String extractedText;
    /**
     * 空のインスタンスを生成する.
     */
    public AttachmentInfo() {
    }

    /**
     * 添付ファイル情報をこのオブジェクトに適用する.
     * @param attachment 添付ファイル情報
     */
    protected void populate(Attachment attachment) {
        this.fileName = attachment.getFileName();
        this.fileId = attachment.getId();
        this.fileType = attachment.getFileType();
        this.orgExtractedText = attachment.getOrgExtractedText();
        this.extractedText = attachment.getExtractedText();
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

    /**
     * 画像ファイルの場合trueを返す.
     * @return 判定結果
     */
    public boolean isImageFile() {
        return fileType != null && AttachmentFileType.IMAGE == fileType;
    }

    /**
     * 抽出テキストが変更されている場合はtrueを返す.
     * @return 判定結果
     */
    public boolean isExtractedTextChanged() {
        return !StringUtils.equals(orgExtractedText, extractedText);
    }

    public AttachmentFileType getFileType() {
        return fileType;
    }

    public void setFileType(AttachmentFileType fileType) {
        this.fileType = fileType;
    }

    public String getOrgExtractedText() {
        return orgExtractedText;
    }

    public void setOrgExtractedText(String orgExtractedText) {
        this.orgExtractedText = orgExtractedText;
    }

    public String getExtractedText() {
        return extractedText;
    }

    public void setExtractedText(String extractedText) {
        this.extractedText = extractedText;
    }
}
