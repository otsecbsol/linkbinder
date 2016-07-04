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
 * レコード更新時の楽観的排他制御チェックにより発生する例外.
 * @author opentone
 */
public class StaleRecordException extends Exception implements MessageHolder {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8228991608714228425L;

    /**
     * メッセージコード.
     */
    private String messageCode = MessageCode.E_STALE_RECORD;
    /**
     * メッセージ内の変数部を置換する値.
     */
    private Object[] vars = null;

    /**
     * 更新できなかったデータ.
     */
    private Entity record;

    /**
     * 空のインスタンスを生成する.
     */
    public StaleRecordException() {
    }

    /**
     * 更新できなかったレコードを設定してインスタンスを生成する.
     * @param record
     *            更新できなかったレコード
     */
    public StaleRecordException(Entity record) {
        this(null, record);
    }

    /**
     * メッセージを設定してインスタンスを生成する.
     * @param message
     *            メッセージ
     */
    public StaleRecordException(String message) {
        this(message, null);
    }

    /**
     * メッセージ、更新できなかったレコードを設定してインスタンスを生成する.
     * @param message
     *            メッセージ
     * @param record
     *            更新できなかったレコード
     */
    public StaleRecordException(String message, Entity record) {
        super(message);
        this.record = record;
    }

    /**
     * @return the record
     */
    public Entity getRecord() {
        return record;
    }

    /**
     * @param record
     *            the record to set
     */
    public void setRecord(Entity record) {
        this.record = record;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public Object[] getMessageVars() {
        return CloneUtil.cloneArray(Object.class, vars);
    }
}
