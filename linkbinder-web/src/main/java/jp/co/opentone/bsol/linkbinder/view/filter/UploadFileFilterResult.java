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
package jp.co.opentone.bsol.linkbinder.view.filter;

/**
 * マルチパートリクエスト処理結果.
 * @author opentone
 */
public class UploadFileFilterResult {
    /**
     * リクエスト処理成功.
     */
    public static final String RESULT_OK = "OK";

    /**
     * リクエスト処理失敗.
     */
    public static final String RESULT_NG = "NG";

    /**
     * リクエスト処理結果.
     */
    private String result;

    /**
     * アップロードされたファイルのキー.
     */
    private String[] keys;

    /**
     * アップロードされたファイル名.
     */
    private String[] names;

    /**
     * アップロードされたファイルが入力されたフォームフィールド名.
     */
    private String[] fieldNames;

    /**
     * アップロードされたファイルサイズ.
     */
    private Long[] fileSize;
    /**
     * リクエスト処理結果を返す.
     * @return リクエスト処理結果
     */
    public String getResult() {
        return result;
    }

    /**
     * リクエスト処理結果をセットする.
     * @param result リクエスト処理結果
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * アップロードされたファイルのキーを返す.
     * @return キー配列
     */
    public String[] getKeys() {
        return keys;
    }

    /**
     * アップロードされたファイルのキーをセットする.
     * @param keys キー配列
     */
    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    /**
     * アップロードされたファイル名を返す.
     * @return ファイル名配列
     */
    public String[] getNames() {
        return names;
    }

    /**
     * アップロードされたファイル名をセットする.
     * @param names ファイル名配列
     */
    public void setNames(String[] names) {
        this.names = names;
    }

    /**
     * アップロードされたファイルが入力されたフォームフィールド名を返す.
     * @return フォームフィールド名配列
     */
    public String[] getFieldNames() {
        return fieldNames;
    }

    /**
     * アップロードされたファイルが入力されたフォームフィールド名をセットする.
     * @param fieldNames フォームフィールド名配列
     */
    public void setFieldNames(String[] fieldNames) {
        this.fieldNames = fieldNames;
    }

    public void setFileSize(Long[] fileSize) {
        this.fileSize = fileSize;
    }

    public Long[] getFileSize() {
        return fileSize;
    }
}
