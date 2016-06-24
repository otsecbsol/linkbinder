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
package jp.co.opentone.bsol.framework.core.config;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * システム定義情報.
 * @author opentone
 */
public class SystemConfig implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 1901377408616498211L;

    /**
     * SpringのMessageSource.
     * <p>
     * {@link SystemConfigInitializer}により、アプリケーション初期化時にセットされる.
     * </p>
     * @see SystemConfigInitializer
     */
    private static MessageSource config;

    /**
     * プロパティが見つからない場合のデフォルト値.
     */
    private static final String DEFAULT_VALUE = null;

    /**
     * 空の引数.
     */
    private static final Object[] BLANK_ARGS = new Object[0];

    /**
     * ユーザー情報を格納するためのキー.
     */
    public static final String KEY_USER = SystemConfig.class.getName() + "_USER";
    /**
     * ユーザーIDを格納するためのキー.
     */
    public static final String KEY_USER_ID = SystemConfig.class.getName() + "_USER_ID";
    /**
     * アクション実行時に必要な値を格納するためのキー.
     */
    public static final String KEY_ACTION_VALUES = SystemConfig.class.getName() + "_ACTION_VALUES";

    /**
     * デフォルトコンストラクタ. 外部からのインスタンス化はできない.
     */
    private SystemConfig() {
    }

    /**
     * MessageSourceを設定する.
     * @param config SpringのMessageSource
     */
    protected static void setConfig(MessageSource config) {
        SystemConfig.config = config;
    }

    /**
     * 指定された定義情報を返す.
     * @param key
     *            定義情報キー
     * @return 値.見つからない場合はnull
     */
    public static String getValue(String key) {
        return config.getMessage(key, BLANK_ARGS, DEFAULT_VALUE, Locale.getDefault());
    }
}
