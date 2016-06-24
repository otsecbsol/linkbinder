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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect;

import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;


/**
 * リダイレクト処理例外クラス.
 *
 * @author opentone
 */
public class RedirectProcessException extends Exception {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3957918575190976707L;

    /**
     * エラーコード.
     */
    private ErrorCode errorCode;

    /**
     * 例外発生原因のサービス例外.
     */
    private ServiceAbortException cause;

    /**
     * コンストラクタ.
     * @param errorCode 例外発生理由のエラーコード
     */
    public RedirectProcessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * コンストラクタ.
     * @param errorCode 例外発生理由のエラーコード
     * @param cause 例外発生原因のサービス例外
     */
    public RedirectProcessException(ErrorCode errorCode, ServiceAbortException cause) {
        this.errorCode = errorCode;
        this.cause = cause;
    }

    /**
     * 例外発生理由を表すエラーコード.
     * @author opentone
     *
     * <p>
     * $Date: 2011-05-17 18:29:25 +0900 (火, 17  5 2011) $
     * $Rev: 3924 $
     * $Author: nemoto $
     */
    public static enum ErrorCode {
        /** 指定プロジェクトが見つからない.  */
        SPECIFIED_PROJECT_NOT_FOUND,
        /** セッション内のカレントプロジェクトが存在しない.  */
        CURRENT_PROJECT_IS_NULL,
        /** セッション内のカレントユーザ情報が存在しない.  */
        CURRENT_USER_IS_NULL,
        /** 指定プロジェクトのプロジェクトユーザが見つからない.  */
        SPECIFIED_PROJECT_USER_NOT_FOUND,
        /** その他理由.  */
        OTHER_REASON,
    }

    /**
     * エラーコードを返す.
     * @return 例外発生理由のエラーコード
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }


    /**
     * エラーコードを返す.
     * @return 例外発生理由のエラーコード
     */
    @Override
    public ServiceAbortException getCause() {
        return cause;
    }
}
