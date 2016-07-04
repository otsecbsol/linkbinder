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
package jp.co.opentone.bsol.framework.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.Messages;


/**
 * アプリケーションの各層間で受け渡しするデータを格納するクラス. 本クラスのオブジェクトに格納される値は、
 * 1回のリクエスト-レスポンス(1スレッド)間のみ有効
 * @author opentone
 */
public class ProcessContext implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 3134151286718382942L;

    /**
     * このクラスのインスタンスを、Thread毎に対応付けて格納するためのコンテナ.
     */
    private static final ThreadLocal<ProcessContext> CONTAINER =
                    new ThreadLocal<ProcessContext>();

    /**
     * 値格納コンテナ.
     */
    private final Map<String, Object> values = new HashMap<String, Object>();

    /**
     * メッセージ格納コンテナ.
     */
    private final List<Message> messages = new ArrayList<Message>();

    /**
     * このクラスのインスタンスを返す.
     * @return このクラスのインスタンス
     */
    public static ProcessContext getCurrentContext() {
        ProcessContext c = CONTAINER.get();
        if (c == null) {
            c = new ProcessContext();
            CONTAINER.set(c);
        }
        return c;
    }

    /**
     * このオブジェクトに値を格納する.
     * @param <T>
     *            戻り値の型
     * @param key
     *            値を取り出すためのキー
     * @return 値. keyに該当する値が無い場合はnull
     */
    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

    /**
     * このオブジェクトに値を格納する.
     * @param key
     *            値を格納するためのキー
     * @param value
     *            値
     */
    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    /**
     * ユーザーに通知するメッセージを設定する.
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数部を置換する値
     */
    public void addMessage(String messageCode, Object... vars) {
        messages.add(Messages.getMessage(messageCode, vars));
    }

    /**
     * ユーザーに通知するメッセージを設定する.
     * @param message
     *            メッセージ
     */
    public void addMessage(Message message) {
        messages.add(message);
    }

    /**
     * 設定済メッセージを全てクリアする.
     */
    public void clearMessages() {
        messages.clear();
    }

    /**
     * このオブジェクトに格納されたメッセージを列挙する.
     * @return メッセージに対するIterator
     */
    public Iterable<Message> messages() {
        return messages;
    }
}
