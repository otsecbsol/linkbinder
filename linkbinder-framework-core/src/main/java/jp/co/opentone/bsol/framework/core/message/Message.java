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
package jp.co.opentone.bsol.framework.core.message;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * 1件のメッセージを表すクラス.
 * @author opentone
 */
public class Message implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -5500137574795964775L;
    /**
     * メッセージ中にアクション名を埋め込むための変数を取得するためのキー.
     */
    public static final String KEY_REGEX_ACTION_NAME = "message.action.name.regex";
    /**
     * メッセージ中にアクション名を埋め込むための変数定義(デフォルト値).
     */
    private static final String DEFAULT_REGEX_ACTION_NAME = "\\$action\\$";
    /**
     * メッセージ中にアクション名を埋め込むための変数定義.
     */
    private String regexActionName = DEFAULT_REGEX_ACTION_NAME;

    /**
     * メッセージコード.
     */
    private String messageCode;
    /**
     * メッセージ.
     */
    private String message;

    /**
     * メッセージの要約.
     */
    private String summary;

    /**
     * メッセージの種類.
     */
    private MessageType type;

    /**
     * 空のメッセージオブジェクトを生成する.
     */
    public Message() {
    }

    /**
     * 新しいメッセージを生成する.
     * @param messageCode
     *            メッセージコード
     * @param message
     *            メッセージ
     */
    public Message(String messageCode, String message) {
        this(messageCode, null, message);
    }

    /**
     * 新しいメッセージを生成する.
     * @param messageCode
     *            メッセージコード
     * @param summary
     *            要約
     * @param message
     *            メッセージ
     */
    public Message(String messageCode, String summary, String message) {
        this.messageCode = messageCode;
        this.summary = summary;
        this.message = message;
        if (StringUtils.isNotEmpty(messageCode)) {
            switch (messageCode.charAt(0)) {
            case 'F':
                this.type = MessageType.ERROR;
                break;
            case 'E':
                this.type = MessageType.WARN;
                break;
            case 'I':
                this.type = MessageType.INFO;
                break;
            default:
                this.type = MessageType.UNDEFINED;
            }
        } else {
            this.type = MessageType.UNDEFINED;
        }
    }

    /**
     * 指定されたアクション名をこのメッセージに埋め込む.
     * <p>
     * メッセージ文言に、アクション名を埋め込むためのプレースホルダがあることが前提.
     * </p>
     * @param actionName アクション名
     */
    public void applyActionName(String actionName) {
        String m = message;
        m = m.replaceAll(getRegActionName(),
                String.format("%s",
                        StringUtils.isNotEmpty(actionName)
                            ? "操作：[" + actionName + "]"
                            : ""));
        message = m;
    }

    private String getRegActionName() {
        String val = SystemConfig.getValue(KEY_REGEX_ACTION_NAME);
        if (StringUtils.isNotEmpty(val)) {
            regexActionName = val;
        }
        return regexActionName;
    }

    /**
     * @return the messageCode
     */
    public String getMessageCode() {
        return messageCode;
    }

    /**
     * @param messageCode
     *            the messageCode to set
     */
    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @param summary
     *            the summary to set
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param type the type to set
     */
    public void setType(MessageType type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public MessageType getType() {
        return type;
    }

    /**
     * Messageクラスが表すメッセージの種類.
     * @author opentone
     */
    public static enum MessageType {
        /** 本来的なエラー. */
        ERROR,
        /** 入力検証エラーなどの業務的なエラー. */
        WARN,
        /** ユーザーへの通知等、情報. */
        INFO,
        /** 未定義. */
        UNDEFINED
    }
}

