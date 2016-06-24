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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.StringUtils;

/**
 *　ページ遷移に関するユーティリティクラス.
 * @author opentone
 */
public class PageNavigationUtil {

    /**
     * Flashオブジェクトへのメッセージ格納キー.
     */
    public static final String FLASH_KEY_MESSAGE = "FLASH_KEY_MESSAGE";

    /**
     * コンストラクタ.<br/>
     * インスタンス生成は不要
     */
    private PageNavigationUtil() {
    }

    /**
     * 同じRequestContextPath内のページへredirectします.
     * @param context FacesContext
     * @param viewId redirect先のviewId
     * @throws AbortProcessingException 処理エラー
     */
    public static void redirectToViewId(FacesContext context, String viewId)
        throws AbortProcessingException {
        redirectToViewId(context, viewId, null);
    }

    /**
     * 同じRequestContextPath内のページへredirectします.
     * @param context FacesContext
     * @param viewId redirect先のviewId
     * @param paramMap パラメータmap
     * @throws AbortProcessingException 処理エラー
     */
    public static void redirectToViewId(
            FacesContext context, String viewId, Map<String, String> paramMap)
        throws AbortProcessingException {
        String contextPath = context.getExternalContext().getRequestContextPath();
        String enc  = context.getExternalContext().getRequestCharacterEncoding();
        String url = createRedirectUrlFromViewId(contextPath, viewId, paramMap, enc);
        redirect(context, url);
    }

    /**
     * ページをredirectします.
     * @param context FacesContext
     * @param url redirect先のURL
     * @throws AbortProcessingException 処理エラー
     */
    public static void redirect(FacesContext context, String url)
        throws AbortProcessingException {
        try {
            context.getExternalContext().redirect(url);
        } catch (IOException e) {
            throw new AbortProcessingException(e);
        }
    }

    /**
     * ページをforwardします.
     * @param context FacesContext
     * @param path forward先のパス
     */
    public static void forward(FacesContext context, String path) {
        ViewHandler viewHandler = context.getApplication().getViewHandler();
        UIViewRoot viewRoot = viewHandler.createView(context, path);
        context.setViewRoot(viewRoot);
        context.renderResponse();
    }

    /**
     * リダイレクト先のviewIdからurlを作成します.
     * @param contextPath リクエストのContextPath
     * @param viewId redirect先のviewId
     * @param paramMap パラメータmap
     * @param enc Character Encoding文字列
     * @return redirect先のurl
     */
    public static String createRedirectUrlFromViewId(
            String contextPath, String viewId, Map<String, String> paramMap, String enc) {
        StringBuffer url = new StringBuffer(contextPath);
        url.append(viewId.replace(".xhtml", ".jsf"));
        try {
            if (null != paramMap && 0 < paramMap.size()) {
                for (String key : paramMap.keySet()) {
                    if (-1 == url.indexOf("?")) {
                        url.append('?');
                    } else {
                        url.append('&');
                    }
                    String value = URLEncoder.encode(paramMap.get(key), enc);
                    url.append(String.format("%s=%s", key, value));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // 論理的にはプログラムエラー以外発生しないエラーと考えられる。
            throw new AbortProcessingException(e);
        }
        return url.toString();
    }

    /**
     * URL文字列にパラメータを追加します.
     * @param url URL
     * @param name パラメータ名
     * @param value パラメータ値
     * @return 追加した後のパラメータURL
     */
    public static String addUrlParameter(String url, String name, String value) {
        StringBuilder builder = new StringBuilder(url);
        if (StringUtils.isNotEmpty(name)) {
            String appendChar = (0 <= url.indexOf('?')) ? "&" : "?";
            String append = String.format("%s%s=%s", appendChar, name, value);
            builder.append(append);
        }
        return builder.toString();
    }
}
