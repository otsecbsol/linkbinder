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

package jp.co.opentone.bsol.framework.web.view.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.message.Message;

/**
 * メッセージテーブルユーティリティです.
 * @author opentone
 */
public class MessageTable implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7059081103388354024L;

    /**
     * メッセージテーブル.
     */
    private Map<String, List<Message>> messageTable = new HashMap<String, List<Message>>();

    /**
     * 最後に追加したメッセージキー.
     */
    private String lastAddMessageKey;

    /**
     * コンストラクタ.
     */
    public MessageTable() {
    }

    /**
     * メッセージを1件追加します.
     * @param key メッセージ識別キー
     * @param message 追加するメッセージ
     */
    public void addMessage(String key, Message message) {
        if (StringUtils.isNotEmpty(key) && null != message) {
            List<Message> messageList = messageTable.get(key);
            if (null == messageList) {
                messageList = new ArrayList<Message>();
            }
            messageList.add(message);
            messageTable.put(key, messageList);
            lastAddMessageKey = key;
        }
    }

    /**
     * メッセージを取得します.
     * @param key メッセージ識別キー
     * @return メッセージ識別キーに対応するメッセージ. 登録が無い場合はnull.
     */
    public List<Message> getMessage(String key) {
        return getMessage(key, false);
    }

    /**
     * メッセージを取得します.
     * パラメータにより取得したメッセージをクリアすることができます.
     * @param key メッセージ識別キー
     * @param isRemoveAll すべてクリアする場合はtrue
     * @return メッセージ識別キーに対応するメッセージ. 登録が無い場合はnull.
     */
    public List<Message> getMessage(String key, boolean isRemoveAll) {
        List<Message> messageList = null;
        if (StringUtils.isNotEmpty(key)) {
            messageList =  messageTable.get(key);
        }
        if (isRemoveAll) {
            messageTable.remove(key);
        }
        return messageList;
    }

    /**
     * 現在設定されているすべてのメッセージをクリアします.
     */
    public void removeAll() {
        messageTable.clear();
        lastAddMessageKey = null;
    }

    /**
     * 最後に追加したメッセージキー.を取得します.
     * @return 最後に追加したメッセージキー.
     */
    public String getLastAddKey() {
        return lastAddMessageKey;
    }
}
