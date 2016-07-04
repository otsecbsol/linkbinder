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
package jp.co.opentone.bsol.framework.core.service;

import jp.co.opentone.bsol.framework.core.message.MessageHolder;
import jp.co.opentone.bsol.framework.core.util.CloneUtil;


/**
 * Serviceが途中で中断したことを表す例外.
 * @author opentone
 */
public class ServiceAbortException extends Exception implements MessageHolder {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 7689354633853376944L;

    /**
     * メッセージコード.
     */
    private final String messageCode;

    /**
     * メッセージ内の変数を置換する値.
     */
    private final Object[] vars;

    /**
     * メッセージコードと例外メッセージをセットして例外を生成する.
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数部を置換する値
     */
    public ServiceAbortException(String messageCode, Object... vars) {
        super(messageCode);
        this.messageCode = messageCode;
        this.vars = vars;
    }

    /**
     * メッセージコードと例外メッセージをセットして例外を生成する.
     * @param message
     *            例外の内容を表すメッセージ
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数部を置換する値
     */
    public ServiceAbortException(String message, String messageCode, Object... vars) {
        super(message);
        this.messageCode = messageCode;
        this.vars = vars;
    }

    /**
     * メッセージと元になった例外をセットして例外を生成する.
     * @param message
     *            例外の内容を表すメッセージ
     * @param messageHolder
     *            メッセージ情報
     */
    public ServiceAbortException(String message, MessageHolder messageHolder) {
        super(message);
        this.messageCode = messageHolder.getMessageCode();
        this.vars = messageHolder.getMessageVars();

        if (messageHolder instanceof Throwable) {
            initCause((Throwable) messageHolder);
        }
    }

    /**
     * メッセージと元になった例外をセットして例外を生成する.
     * @param messageHolder
     *            メッセージ情報
     */
    public ServiceAbortException(MessageHolder messageHolder) {
        this.messageCode = messageHolder.getMessageCode();
        this.vars = messageHolder.getMessageVars();

        if (messageHolder instanceof Throwable) {
            initCause((Throwable) messageHolder);
        }
    }

    /**
     * メッセージと元になった例外をセットして例外を生成する.
     * @param messgae
     *            例外の内容を表すメッセージ
     * @param cause
     *            元の例外
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数部を置換する値
     */
    public ServiceAbortException(String messgae, Throwable cause, String messageCode,
                                 Object... vars) {
        super(messgae, cause);
        this.messageCode = messageCode;
        this.vars = vars;
    }

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.framework.message.MessageHolder#getMessageCode()
     */
    @Override
    public String getMessageCode() {
        return messageCode;
    }

    /*
     * (非 Javadoc)
     * @see jp.co.opentone.bsol.framework.message.MessageHolder#getMessageVars()
     */
    @Override
    public Object[] getMessageVars() {
        return CloneUtil.cloneArray(Object.class, vars);
    }
}
