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
package jp.co.opentone.bsol.framework.core.dao;

import jp.co.opentone.bsol.framework.core.message.MessageCode;
import jp.co.opentone.bsol.framework.core.message.MessageHolder;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;

/**
 * データベースにレコードが存在しないことを表す例外.
 * @author opentone
 */
public class RecordNotFoundException extends Exception implements MessageHolder {

    /**
     * SerialVersionID.
     */
    private static final long serialVersionUID = -5064099326560463012L;

    /**
     * メッセージコード.
     */
    private String messageCode = MessageCode.E_RECORD_NOT_FOUND;
    /**
     * メッセージ内の変数部を置換する値.
     */
    private Object[] vars = null;

    /**
     * 空のインスタンスを生成する.
     */
    public RecordNotFoundException() {
        super();
    }

    /**
     * メッセージを指定してインスタンスを生成する.
     * @param message
     *            メッセージ
     */
    public RecordNotFoundException(String message) {
        super(message);
    }

    /**
     * メッセージと原因となった例外を指定してインスタンスを生成する.
     * @param message
     *            メッセージ
     * @param cause
     *            元の例外オブジェクト
     */
    public RecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getMessageVars() {
        return CloneUtil.cloneArray(Object.class, vars);
    }
}
