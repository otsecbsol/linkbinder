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
package jp.co.opentone.bsol.framework.core.message;

/**
 * フレームワーク共通のメッセージコード定数.
 * @author opentone
 */
public class MessageCode {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -3354938950041182045L;

    /**
     * コンストラクタ.
     * 外部からのインスタンス化禁止.
     */
    protected MessageCode() {
    }

    /**
     * キー重複.
     */
    public static final String E_KEY_DUPLICATE = "E901";
    /**
     * レコードが見つからない.
     */
    public static final String E_RECORD_NOT_FOUND = "E902";
    /**
     * 更新対象レコードの排他制御エラー.
     */
    public static final String E_STALE_RECORD = "E903";
    /**
     * 不正なパラメーター.
     */
    public static final String E_INVALID_PARAMETER = "E904";

    /**
     * ダウンロードに失敗.
     */
    public static final String E_DOWNLOAD_FAILED = "E905";

    /**
     * 生成処理に失敗.
     */
    public static final String E_GENERATION_FAILED = "E906";

    /**
     * 不正な入力(Validationエラー).
     */
    public static final String E_INVALID_INPUT = "E907";

    /**
     * HyperEstarierの処理で失敗.
     */
    public static final String E_ESTRAIER_FAILED = "E908";

    /**
     * アップロードに失敗.
     */
    public static final String E_UPLOAD_FAILED = "E909";
}
