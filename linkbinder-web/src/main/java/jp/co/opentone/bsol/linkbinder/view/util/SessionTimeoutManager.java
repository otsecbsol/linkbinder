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
package jp.co.opentone.bsol.linkbinder.view.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * セッションタイムアウトの設定値を取得するクラス.
 * @author opentone
 */
public class SessionTimeoutManager {

    /** 設定ファイルからセッションタイムアウトを取得する為のキー. */
    private static final String KEY_LINKBINDER_SESSION_TIMEOUT = "linkbinder.session.timeout";

    /** 設定ファイルからLANか判定する文字列を取得する為のキー. */
    private static final String KEY_NOT_LINKBINDER_LAN = "not.linkbinder.lan";

    /** セッションタイムアウト値から減算する値を取得する為のキー. */
    private static final String KEY_LINKBINDER_SESSION_TIMEOUT_SUBTRACTION_VALUE =
        "linkbinder.session.timeout.subtraction.value";

    /** ヘッダーからViaを取得する為のキー. */
    private static final String KEY_HEADER_NAME_VIA = "via";

    /** 1分(ミリ秒). */
    private static final int MINUTE = 60 * 1000;

    /** 60. */
    private static final int NUMBER_SIXTY = 60;

    /** リクエスト発行回数. */
    private int requestCount = 0;

    /** リクエスト発行間隔. */
    private Long requestInterval = 0L;

    /**
     * リクエスト発行回数を取得する.
     * @return リクエスト発行回数
     */
    public int getRequestCount() {
        return requestCount;
    }

    /**
     * リクエスト発行間隔を取得する.
     * @return リクエスト発行間隔
     */
    public Long getRequestInterval() {
        return requestInterval;
    }

    /**
     * コンストラクタ、リクエスト発行回数、リクエスト発行間隔を設定する.
     * @param request HttpServletRequest
     */
    public SessionTimeoutManager(HttpServletRequest request) {
        // リクエスト発行回数、発行間隔を設定する
        setRequestCountAndInterval(request);
    }

    /**
     * リクエスト発行回数、リクエスト発行間隔を設定する.
     * @param session HttpSession
     */
    private void setRequestCountAndInterval(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (isNotLan(request) || session == null) {
            return;
        }
        // セッションタイムアウト値(分)を設定ファイルから取得
        String linkBinderSessionTimeout = SystemConfig.getValue(KEY_LINKBINDER_SESSION_TIMEOUT);
        // セッションタイムアウト値をミリ秒に変換
        int linkBinderTimeout = Integer.valueOf(linkBinderSessionTimeout) * MINUTE;

        // セッションタイムアウト値から減算する値を設定ファイルから取得(分)し、ミリ秒に変換
        int linkBinderSessionTimeoutSubtractionValue = Integer.valueOf(SystemConfig.getValue(
                KEY_LINKBINDER_SESSION_TIMEOUT_SUBTRACTION_VALUE)) * MINUTE;
        // アプリケーションサーバーのセッションタイムアウト値を取得しミリ秒に変換
        int sessionTimeout = session.getMaxInactiveInterval() / NUMBER_SIXTY * MINUTE;

        // セッションタイムアウト値から減算し、リクエスト発行間隔を取得(ミリ秒)
        int interval = sessionTimeout - linkBinderSessionTimeoutSubtractionValue;

        // 計算結果が減算値以下となる場合
        if (interval <= linkBinderSessionTimeoutSubtractionValue) {
            interval = linkBinderSessionTimeoutSubtractionValue;
        }
        // リクエスト発行間隔を設定(ミリ秒)
        this.requestInterval = Long.valueOf(interval);
        // リクエスト発行回数を設定
        this.requestCount = linkBinderTimeout / interval;
    }

    /**
     * LAN以外からのアクセスか判定する.
     * @param request リクエスト
     * @return LAN以外 true / LAN false
     */
    private boolean isNotLan(HttpServletRequest request) {
        String via = request.getHeader(KEY_HEADER_NAME_VIA);
        String notLan = SystemConfig.getValue(KEY_NOT_LINKBINDER_LAN);
        return via != null && via.indexOf(notLan) != -1;
    }
}
