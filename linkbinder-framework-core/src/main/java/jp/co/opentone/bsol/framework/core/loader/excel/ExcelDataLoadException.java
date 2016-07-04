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
package jp.co.opentone.bsol.framework.core.loader.excel;

/**
 * Excelブックのデータ読み込みに失敗したことを表す例外.
 * @author opentone
 */
public class ExcelDataLoadException extends Exception {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -229020594735659836L;

    /** 失敗の原因. */
    private Reason reason;

    /**
     * 空のインスタンスを生成する.
     */
    public ExcelDataLoadException() {
        this(Reason.UNKNOWN);
    }

    /**
     * 失敗の原因を指定してインスタンス化する.
     * @param reason 失敗の原因
     */
    public ExcelDataLoadException(Reason reason) {
        this(null, null, reason);
    }

    /**
     * 失敗の原因を指定してインスタンス化する.
     * @param message エラーメッセージ
     * @param reason 失敗の原因
     */
    public ExcelDataLoadException(String message, Reason reason) {
        this(message, null, reason);
    }

    /**
     * 失敗の原因となった例外を指定してインスタンス化する.
     * @param cause 原因
     */
    public ExcelDataLoadException(Throwable cause) {
        this(cause, Reason.UNKNOWN);
    }

    /**
     * 失敗の原因となった例外を指定してインスタンス化する.
     * @param cause 原因
     * @param reason 失敗の原因
     */
    public ExcelDataLoadException(Throwable cause, Reason reason) {
        this(null, cause, reason);
    }

    /**
     * 失敗の原因となった例外を指定してインスタンス化する.
     * @param message エラーメッセージ
     * @param cause 原因
     */
    public ExcelDataLoadException(String message, Throwable cause) {
        this(message, cause, Reason.UNKNOWN);
    }

    /**
     * 失敗の原因となった例外を指定してインスタンス化する.
     * @param message エラーメッセージ
     * @param cause 原因
     * @param reason 失敗の原因
     */
    public ExcelDataLoadException(String message, Throwable cause, Reason reason) {
        super(message, cause);
        this.reason = reason;
    }

    /**
     * @param reason the reason to set
     */
    public void setReason(Reason reason) {
        this.reason = reason;
    }

    /**
     * @return the reason
     */
    public Reason getReason() {
        return reason;
    }

    /**
     * 読み込み失敗の原因.
     * @author opentone
     */
    public static enum Reason {
        /** ファイルが見つからない. */
        FILE_NOT_FOUND,
        /** ファイルの読み込みに失敗. */
        FAIL_TO_READ,
        /** 不明. */
        UNKNOWN;
    }
}
