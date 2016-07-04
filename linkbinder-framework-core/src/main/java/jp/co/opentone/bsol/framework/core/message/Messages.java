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
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

/**
 * メッセージを管理するクラス.
 * <p>
 * SpringのMessageSourceが提供するメッセージを利用する.
 * </p>
 * @author opentone
 */
public final class Messages implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4963133339731078548L;

    /**
     * logger.
     */
    private static final Logger log = LoggerFactory.getLogger(Messages.class);

    /**
     * SpringのMessageSource.
     * <p>
     * {@link MessageInitializer}により、アプリケーション初期化時にセットされる.
     * </p>
     * @see MessageInitializer
     */
    private static MessageSource messageSource;

    /**
     * staticメソッドのみなので外部からのインスタンス化禁止.
     */
    private Messages() {
    }

    /**
     * MessageSourceを設定する.
     * @param messageSource SpringのMessageSource
     */
    protected static void setMessageSource(MessageSource messageSource) {
        Messages.messageSource = messageSource;
    }

    /**
     * メッセージを生成して文字列として返す.
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数を置換する値
     * @return メッセージ
     * @throws NoSuchMessageException
     *             メッセージが見つからない場合
     */
    public static String getMessageAsString(String messageCode, Object... vars)
        throws NoSuchMessageException {
        return getMessageAsString(Locale.getDefault(), messageCode, vars);
    }

    /**
     * メッセージを生成して文字列として返す.
     * @param locale
     *            locale
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            メッセージ内の変数を置換する値
     * @return メッセージ
     * @throws NoSuchMessageException
     *             メッセージが見つからない場合
     */
    public static String getMessageAsString(Locale locale, String messageCode, Object... vars)
        throws NoSuchMessageException {
        return messageSource.getMessage(messageCode, vars, locale);
    }

    /**
     * メッセージの要約を文字列として返す.
     * @param messageCode
     *            メッセージコード
     * @return メッセージ
     * @throws NoSuchMessageException
     *             メッセージが見つからない場合
     */
    public static String getSummaryAsString(String messageCode) throws NoSuchMessageException {
        return getSummaryAsString(Locale.getDefault(), messageCode);
    }

    /**
     * メッセージの要約を文字列として返す.
     * @param locale
     *            locale
     * @param messageCode
     *            メッセージコード
     * @return メッセージ
     * @throws NoSuchMessageException
     *             メッセージが見つからない場合
     */
    public static String getSummaryAsString(Locale locale, String messageCode)
        throws NoSuchMessageException {
        String code = String.format("%s.summary", messageCode);
        return messageSource.getMessage(code, null, locale);
    }

    /**
     * 指定されたメッセージコードのメッセージを生成して返す.
     * @param messageCode
     *            メッセージコード
     * @param vars
     *            引数
     * @return メッセージ
     * @throws NoSuchMessageException
     *             メッセージが見つからない場合
     */
    public static Message getMessage(String messageCode, Object... vars)
        throws NoSuchMessageException {
        String message = getMessageAsString(messageCode, vars);

        // 要約があれば取得・設定する
        String summary = null;
        try {
            summary = getSummaryAsString(messageCode);
        } catch (NoSuchMessageException ignore) {
            log.info("Summary not found. [{}]", messageCode);
        }

        Message m = new Message(messageCode, summary, message);
        String format = "{}: {}";
        switch (m.getType()) {
        case ERROR :
            log.error(format, m.getMessageCode(), m.getMessage());
            break;
        case WARN :
            log.warn(format, m.getMessageCode(), m.getMessage());
            break;
        case INFO :
            log.info(format, m.getMessageCode(), m.getMessage());
            break;
         default :
            log.debug(format, m.getMessageCode(), m.getMessage());
            break;
        }
        return m;
    }
}
