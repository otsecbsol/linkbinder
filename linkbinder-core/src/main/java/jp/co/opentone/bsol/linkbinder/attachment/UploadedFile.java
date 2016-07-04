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

import java.io.Serializable;

/**
 * アップロードされたファイル情報.
 * @author opentone
 */
public class UploadedFile implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4922161827327007573L;

    /**
     * ファイル名が制限を超えた際に設定されるキーの値.
     */
    public static final String KEY_FILENAME_OVER = "NAME";

    /**
     * ファイルサイズがオーバーした際に設定されるキーの値.
     */
    public static final String KEY_SIZE_OVER = "OVER";

    /**
     * ファイルサイズが0の場合に設定されるキーの値.
     */
    public static final String KEY_SIZE_ZERO = "ZERO";

    /**
     * 一時ファイル名に使用されるランダムな一意のキー.
     */
    private String key;

    /**
     * 実ファイル名.
     */
    private String filename;

    /**
     * ファイルサイズ.
     */
    private Long fileSize;

    @Override
    public UploadedFile clone() {
        UploadedFile clone = new UploadedFile();
        clone.setKey(key);
        clone.setFilename(filename);
        clone.setFileSize(fileSize == null ? fileSize : new Long(fileSize));
        return clone;
    }

    /**
     * キーを取得する.
     * @return キー
     */
    public String getKey() {
        return key;
    }

    /**
     * キーをセットする.
     * @param key キー
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 実ファイル名を取得する.
     * @return 実ファイル名
     */
    public String getFilename() {
        return filename;
    }

    /**
     * 実ファイル名をセットする.
     * @param filename 実ファイル名
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Long getFileSize() {
        return fileSize;
    }

    /**
     * エラーが発生したファイルかどうかを返す.
     * @return エラーが発生している場合true
     */
    public boolean isErrorFile() {
        return KEY_FILENAME_OVER.equals(key)
                || KEY_SIZE_ZERO.equals(key)
                || KEY_SIZE_OVER.equals(key);
    }

    /**
     * ファイル名が上限を超えているファイルかどうかを返す.
     * @return ファイル名が上限を超えている場合true
     */
    public boolean isFilenameOverFile() {
        return KEY_FILENAME_OVER.equals(key);
    }

    /**
     * サイズが0のファイルかどうかを返す.
     * @return サイズが0の場合true
     */
    public boolean isSizeZeroFile() {
        return KEY_SIZE_ZERO.equals(key);
    }

    /**
     * サイズオーバーが発生したファイルかどうかを返す.
     * @return サイズオーバーが発生している場合true
     */
    public boolean isSizeOverFile() {
        return KEY_SIZE_OVER.equals(key);
    }
}
