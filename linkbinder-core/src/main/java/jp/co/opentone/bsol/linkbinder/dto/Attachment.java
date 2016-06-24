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
package jp.co.opentone.bsol.linkbinder.dto;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jp.co.opentone.bsol.framework.core.dao.Entity;
import jp.co.opentone.bsol.linkbinder.dto.UpdateMode.ModeHolder;

/**
 * テーブル [v_attachment] の1レコードを表すDto.
 * <p>
 * generated at Tue Mar 31 18:38:27 +0900 2009.
 * </p>
 * @author db2sgen
 * <p>
 * $Date: 2011-05-17 15:46:33 +0900 (火, 17  5 2011) $
 * $Rev: 3923 $
 * $Author: nemoto $
 */
public class Attachment extends AbstractDto implements Entity, ModeHolder {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8601859953723184513L;
    /**
     * このクラスのオブジェクトの文字列表現から除外するフィールド名.
     */
    private static final Set<String> TO_STRING_IGNORE_FIELDS;
    static {
        Set<String> fields = new HashSet<String>();
        fields.add("content");

        TO_STRING_IGNORE_FIELDS = Collections.unmodifiableSet(fields);
    }

    /** 編集モード. */
    private UpdateMode mode;

    /**
     * Id.
     * <p>
     * [v_attachment.id]
     * </p>
     */
    private Long id;

    /**
     * No.
     * <p>
     * [v_attachment.no]
     * </p>
     */
    private Long no;

    /**
     * Correspon.
     * <p>
     * [v_attachment.correspon_id]
     * </p>
     */
    private Long corresponId;

    /**
     * File.
     * <p>
     * [v_attachment.file_id]
     * </p>
     */
    private String fileId;

    /**
     * File name.
     * <p>
     * [v_attachment.file_name]
     * </p>
     */
    private String fileName;

    /**
     * Created by.
     * <p>
     * </p>
     */
    private User createdBy;

    /**
     * Created at.
     * <p>
     * [v_attachment.created_at]
     * </p>
     */
    private Date createdAt;

    /**
     * Updated by.
     * <p>
     * </p>
     */
    private User updatedBy;

    /**
     * Updated at.
     * <p>
     * [v_attachment.updated_at]
     * </p>
     */
    private Date updatedAt;

    /**
     * Delete no.
     * <p>
     * [v_attachment.delete_no]
     * </p>
     */
    private Long deleteNo;

    /**
     * ファイルの中身.
     */
    private byte[] content;

    /**
     * 保存対象ファイルの格納先.
     */
    private String sourcePath;

    /**
     * 空のインスタンスを生成する.
     */
    public Attachment() {
    }

    /**
     * id の値を返す.
     * <p>
     * [v_attachment.id]
     * </p>
     * @return id
     */
    public Long getId() {
        return id;
    }

    /**
     * id の値を設定する.
     * <p>
     * [v_attachment.id]
     * </p>
     * @param id
     *            id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * no の値を返す.
     * <p>
     * [v_attachment.no]
     * </p>
     * @return no
     */
    public Long getNo() {
        return no;
    }

    /**
     * no の値を設定する.
     * <p>
     * [v_attachment.no]
     * </p>
     * @param no
     *            no
     */
    public void setNo(Long no) {
        this.no = no;
    }

    /**
     * corresponId の値を返す.
     * <p>
     * [v_attachment.correspon_id]
     * </p>
     * @return corresponId
     */
    public Long getCorresponId() {
        return corresponId;
    }

    /**
     * corresponId の値を設定する.
     * <p>
     * [v_attachment.correspon_id]
     * </p>
     * @param corresponId
     *            corresponId
     */
    public void setCorresponId(Long corresponId) {
        this.corresponId = corresponId;
    }

    /**
     * fileId の値を返す.
     * <p>
     * [v_attachment.file_id]
     * </p>
     * @return fileId
     */
    public String getFileId() {
        return fileId;
    }

    /**
     * fileId の値を設定する.
     * <p>
     * [v_attachment.file_id]
     * </p>
     * @param fileId
     *            fileId
     */
    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    /**
     * fileName の値を返す.
     * <p>
     * [v_attachment.file_name]
     * </p>
     * @return fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * fileName の値を設定する.
     * <p>
     * [v_attachment.file_name]
     * </p>
     * @param fileName
     *            fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 作成者を返す.
     * <p>
     * </p>
     * @return createdBy
     */
    public User getCreatedBy() {
        return createdBy;
    }

    /**
     * 作成者を設定する.
     * <p>
     * </p>
     * @param createdBy
     *            作成者
     */
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * createdAt の値を返す.
     * <p>
     * [v_attachment.created_at]
     * </p>
     * @return createdAt
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * createdAt の値を設定する.
     * <p>
     * [v_attachment.created_at]
     * </p>
     * @param createdAt
     *            createdAt
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 更新者を返す.
     * <p>
     * </p>
     * @return updatedBy
     */
    public User getUpdatedBy() {
        return updatedBy;
    }

    /**
     * 更新者を設定する.
     * <p>
     * </p>
     * @param updatedBy
     *            更新者
     */
    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * updatedAt の値を返す.
     * <p>
     * [v_attachment.updated_at]
     * </p>
     * @return updatedAt
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * updatedAt の値を設定する.
     * <p>
     * [v_attachment.updated_at]
     * </p>
     * @param updatedAt
     *            updatedAt
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * deleteNo の値を返す.
     * <p>
     * [v_attachment.delete_no]
     * </p>
     * @return deleteNo
     */
    public Long getDeleteNo() {
        return deleteNo;
    }

    /**
     * deleteNo の値を設定する.
     * <p>
     * [v_attachment.delete_no]
     * </p>
     * @param deleteNo
     *            deleteNo
     */
    public void setDeleteNo(Long deleteNo) {
        this.deleteNo = deleteNo;
    }

    /**
     * ファイルの中身を設定する.
     * @param content
     *            ファイルの中身
     */
    public void setContent(byte[] content) {
        this.content = content;
    }

    /**
     * ファイルの中身を取得する.
     * @return ファイルの中身
     */
    public byte[] getContent() {
        return content;
    }

    /**
     * @param mode the mode to set
     */
    public void setMode(UpdateMode mode) {
        this.mode = mode;
    }

    /**
     * @return the mode
     */
    public UpdateMode getMode() {
        return mode;
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

    /* (non-Javadoc)
     * @see jp.co.opentone.bsol.linkbinder.dto.AbstractDto#isToStringIgnoreField(java.lang.String)
     */
    @Override
    public boolean isToStringIgnoreField(String fieldName) {
        return super.isToStringIgnoreField(fieldName)
                || TO_STRING_IGNORE_FIELDS.contains(fieldName);
    }

}
