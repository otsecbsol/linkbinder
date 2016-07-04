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
import java.util.LinkedList;
import java.util.Queue;

import jp.co.opentone.bsol.framework.core.message.Message;

/**
 * メッセージキューユーティリティです.
 * @author opentone
 */
public class MessageQueue implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 7059081103388354024L;
    /**
     * メッセージリスト.
     */
    private final Queue<Message> messageList = new LinkedList<Message>();

    /**
     * メッセージを1件最後尾へ追加します.
     * @param message 追加するメッセージ
     */
    public void add(Message message) {
        if (null != message) {
            messageList.add(message);
        }
    }

    /**
     * メッセージを複数件、末尾へ追加します.
     * @param messages 追加するメッセージ
     */
    public void add(Message[] messages) {
        if (null != messages) {
            for (Message message : messages) {
                add(message);
            }
        }
    }

    /**
     * メッセージを1件先頭から抽出します.
     * @return message 抽出したメッセージ. 1件も無い場合はnull
     */
    public Message poll() {
        return messageList.poll();
    }

    /**
     * メッセージをクリアする.
     */
    public void clear() {
        messageList.clear();
    }

    /**
     * 要素数を取得します.
     * @return 要素数
     */
    public int size() {
        return messageList.size();
    }

    /**
     * 要素をすべて取得して、すべての要素を削除します.
     * @return Message配列. 1件も無い場合はnull
     */
    public Message[] pollArray() {
        Message[] array = null;
        if (0 < size()) {
            array = messageList.toArray(new Message [0]);
            clear();
        }
        return array;
    }
}
