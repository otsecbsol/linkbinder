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
package jp.co.opentone.bsol.linkbinder.view.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * フィルターの親クラス.
 * @author opentone
 */
public abstract class AbstractFilter implements Filter {

    /**
     * ログイン状態チェックの対象外とするページを init-param に指定するためのキー.
     */
    public static final String PARAM_IGNORE_PAGES = "ignorePages";
    /**
     * ダイレクトログイン対象をinit-paramに指定するキーを追加.
     */
    public static final String PARAM_DIRECTLOGIN_PAGES = "directLoginPages";
    /**
     * ログインしていない時に遷移するページを init-param に指定するためのキー.
     */
    public static final String PARAM_REDIRECT_PAGE = "redirectPage";
    /**
     * セッションタイムアウト時に遷移するページを init-param に指定するためのキー.
     */
    public static final String PARAM_TIMEOUT_PAGE = "sessionTimeoutPage";
    /**
     * エラーページを init-param に指定するためのキー.
     */
    public static final String PARAM_ERROR_PAGE = "errorPage";

    /**
     * ログインしていない時に遷移するページのデフォルト値.
     */
    public static final String DEFAULT_REDIRECT_PAGE = "/login.jsf";
    /**
     * タイムアウト時に遷移するページのデフォルト値.
     */
    public static final String DEFAULT_TIMEOUT_PAGE = "/login.jsf?timeout=1";
    /**
     * タイムアウト時に遷移するページのデフォルト値.
     */
    public static final String DEFAULT_ERROR_PAGE = "/error.jsf";

    /**
     * ログイン状態チェックの対象外とするページ名.
     */
    protected Set<String> ignorePages;
    /**
     * ダイレクトログインの判定.
     */
    protected Set<String> directLoginPages;
    /**
     * ログインしていない時に遷移するページ.
     */
    protected String redirectPage = DEFAULT_REDIRECT_PAGE;
    /**
     * セッションタイムアウト時に遷移するページ.
     */
    protected String timeoutPage  = DEFAULT_TIMEOUT_PAGE;
    /**
     * エラーページ名.
     */
    protected String errorPage = DEFAULT_ERROR_PAGE;

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        ServletContext context = filterConfig.getServletContext();
        initLoginParameters(context);
        initTimeoutParameters(context);
        initErrorParameters(context);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }

    private void initLoginParameters(ServletContext context) {
        String redirect = context.getInitParameter(PARAM_REDIRECT_PAGE);
        if (StringUtils.isNotEmpty(redirect)) {
            redirectPage = redirect;
        }

        ignorePages = new HashSet<String>();
        String pages = context.getInitParameter(PARAM_IGNORE_PAGES);
        if (StringUtils.isNotEmpty(pages)) {
            for (String page : pages.split("\\s+")) {
                ignorePages.add(page);
            }
        }

        directLoginPages = new HashSet<String>();
        pages = context.getInitParameter(PARAM_DIRECTLOGIN_PAGES);
        if (StringUtils.isNotEmpty(pages)) {
            for (String page : pages.split("\\s+")) {
                directLoginPages.add(page);
            }
        }
    }

    private void initTimeoutParameters(ServletContext context) {
        String page = context.getInitParameter(PARAM_TIMEOUT_PAGE);
        if (StringUtils.isNotEmpty(page)) {
            timeoutPage = page;
        }
    }

    private void initErrorParameters(ServletContext context) {
        String page = context.getInitParameter(PARAM_ERROR_PAGE);
        if (StringUtils.isNotEmpty(page)) {
            errorPage = page;
        }
    }

    /**
     * ページのリダイレクトを行う.
     * @param request リクエスト
     * @param response レスポンス
     * @param page リダイレクト先ページ
     * @throws IOException 例外
     */
    protected void redirectTo(
        HttpServletRequest request, HttpServletResponse response, String page)
        throws IOException {
        response.sendRedirect(
            String.format("%s%s", request.getContextPath(), page));
    }

    /**
     * ログイン状態チェック対象外ページか否かを返す.
     * @param uri リクエストされたURI
     * @return ログイン状態チェック対象の場合true
     */
    protected boolean isIgnore(String uri) {
        for (String page : ignorePages) {
            if (uri.endsWith(page)) {
                return true;
            }
        }
        return false;
    }

    /**
     * リダイレクト対象ページか否かを返す.
     * @param uri リクエストされたURI
     * @return リダイレクト対象ページの場合true
     */
    protected boolean isDirectLogin(String uri) {
        for (String page : directLoginPages) {
            if (uri.endsWith(page)) {
                return true;
            }
        }
        return false;
    }
}
