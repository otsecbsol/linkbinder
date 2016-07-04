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


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;

/**
 * アプリケーションの実行環境を表すクラス.
 * 実行環境とは次のいずれかのこと.
 * <ul>
 * <li>production (本番環境)</li>
 * <li>demo (デモ環境)</li>
 * <li>test (テスト環境)</li>
 * </ul>
 *
 * ログインユーザーのIDを判定して、現在の実行環境を識別する.
 *
 * @author opentone
 */
public enum Environment {

    /** 本番環境. */
    PRODUCTION("production"),
    /** デモ環境. */
    DEMO("demo"),
    /** テスト環境. */
    TEST("test");

    /** logger. */
    private static Logger log = LoggerFactory.getLogger(Environment.class);
    /**
     * ユーザーIDのプレフィックスの長さ.
     */
    private static final int PREFIX_LENGTH = 3;
    /**
     * プレフィックス：デモ環境ユーザー.
     */
    private static final String PREFIX_DEMO = "ZZB";
    /**
     * プレフィックス：テスト環境ユーザー.
     */
    private static final String PREFIX_TEST = "ZZA";

    /**
     * 現在の環境を表す名前.
     */
    private String name;
    /**
     * 名前を指定してインスタンス化する.
     * @param 名前
     */
    private Environment(String name) {
        this.name = name;
    }

    /**
     * 現在の実行環境を返す.
     * @return 現在の実行環境
     */
    public static Environment getEnvironment() {
        Environment env = null;
        String userId = getUserId();
        log.info("userId = {}", userId);

        if (isProductionUser(userId)) {
            env = PRODUCTION;
        } else if (isDemoUser(userId)) {
            env = DEMO;
        } else if (isTestUser(userId)) {
            env = TEST;
        } else {
            //  ユーザーIDが未設定の場合以外に、ここに到達することは無い
            throw new ApplicationFatalRuntimeException(
                    String.format("Environment was not selected. userId = %s", userId));
        }

        log.info("environment: [{}]", env.toString());
        return env;
    }

    /**
     * 実行環境を表す文字列をこのクラスのインスタンスに変換する.
     * @param env 実行環境を表す文字列
     * @return 変換結果
     */
    public static Environment parse(String env) {
        for (Environment e : values()) {
            if (e.name.equals(env)) {
                return e;
            }
        }
        throw new ApplicationFatalRuntimeException(
                    String.format("invalid environment: %s", env));
    }


    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    private static String getUserId() {
        String userId = ProcessContext.getCurrentContext().getValue(SystemConfig.KEY_USER_ID);
        if (StringUtils.isEmpty(userId)) {
            log.warn("userId was not found in ProcessContext.");
            return null;
        }
        return userId;
    }

    private static boolean isProductionUser(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return false;
        }
        return !isTestUser(userId) && !isDemoUser(userId);
    }

    private static boolean isDemoUser(String userId) {
        if (StringUtils.isEmpty(userId) || userId.length() < PREFIX_LENGTH) {
            return false;
        }
        return userId.startsWith(PREFIX_DEMO);
    }

    private static boolean isTestUser(String userId) {
        if (StringUtils.isEmpty(userId) || userId.length() < PREFIX_LENGTH) {
            return false;
        }
        return userId.startsWith(PREFIX_TEST);
    }
}
