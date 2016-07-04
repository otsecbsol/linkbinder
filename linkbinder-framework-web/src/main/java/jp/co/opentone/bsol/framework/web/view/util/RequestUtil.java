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

import javax.servlet.http.HttpServletRequest;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * {@link HttpServletRequest}に関する共通処理.
 * @author opentone
 */
public class RequestUtil {

    /**
     * 絶対パス用フォーマット.
     */
    public static final String BASE_PATH = "%s://%s%s%s";

    /** サーバ名. */
    public static final String KEY_SERVER_NAME = "server.name";

    /**
     * アプリケーションのベースURLを返す.
     * <p>
     * 返されるのは次のような文字列.
     * </p>
     * <p>
     * <code>http://hostname/context/</code>
     * </p>
     * <p>
     * ポート番号が80、443以外の場合は<code>hostname:portNum</code>となる.
     * </p>
     *
     * @param request リクエスト
     * @return 基準となるURLを表す文字列
     */
    public static String getBaseURL(HttpServletRequest request) {
        return String.format(BASE_PATH,
                        request.getScheme(),
                        SystemConfig.getValue(KEY_SERVER_NAME),
                        getPort(request),
                        request.getContextPath());
    }

    private static String getPort(HttpServletRequest request) {
        final int PORT_HTTP = 80;
        final int PORT_HTTPS = 443;
        switch (request.getServerPort()) {
        case PORT_HTTP:
        case PORT_HTTPS:
            return "";
        default:
            return String.format(":%s", request.getServerPort());
        }
    }

}
