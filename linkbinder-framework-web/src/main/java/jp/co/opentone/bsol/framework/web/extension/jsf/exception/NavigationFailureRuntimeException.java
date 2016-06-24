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
package jp.co.opentone.bsol.framework.web.extension.jsf.exception;

/**
 * 当フレームワークがページ遷移に失敗したことを表す例外.
 * @author opentone
 */
public class NavigationFailureRuntimeException extends RuntimeException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2022358384235757947L;

    /**
     * 例外情報を生成する.
     */
    public NavigationFailureRuntimeException() {
        super();
    }

    /**
     * メッセージを指定して例外情報を生成する.
     * @param message
     *            メッセージ
     */
    public NavigationFailureRuntimeException(String message) {
        super(message);
    }

    /**
     * 原因となった例外を指定して例外情報を生成する.
     * @param cause
     *            原因
     */
    public NavigationFailureRuntimeException(Throwable cause) {
        super(cause);
    }
}
