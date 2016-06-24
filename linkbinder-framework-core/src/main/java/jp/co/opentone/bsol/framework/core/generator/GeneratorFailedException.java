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
package jp.co.opentone.bsol.framework.core.generator;

/**
 * 何らかの生成処理に失敗したことを表す例外.
 * @author opentone
 */
public class GeneratorFailedException extends RuntimeException {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -7379981765185237257L;

    /**
     * 空のインスタンスを生成する.
     */
    public GeneratorFailedException() {
    }

    /**
     * 例外のメッセージを指定してインスタンスを生成する.
     * @param message
     *            例外メッセージ
     */
    public GeneratorFailedException(String message) {
        super(message);
    }

    /**
     * 例外のメッセージと、原因となった例外オブジェクトを指定してインスタンスを生成する.
     * @param message
     *            例外メッセージ
     * @param cause
     *            原因となった例外
     */
    public GeneratorFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 原因となった例外オブジェクトを指定してインスタンスを生成する.
     * @param cause
     *            原因となった例外
     */
    public GeneratorFailedException(Throwable cause) {
        super(cause);
    }
}
