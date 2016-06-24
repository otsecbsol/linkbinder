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
package jp.co.opentone.bsol.framework.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.WebApplicationContextUtils;

import jp.co.opentone.bsol.framework.core.auth.AuthUser;
import jp.co.opentone.bsol.framework.core.auth.AuthenticateException;
import jp.co.opentone.bsol.framework.core.auth.FeedAuthenticator;
import jp.co.opentone.bsol.framework.core.auth.FeedAuthenticatorFactory;

/**
 * RSSフィードに対するリクエストが正当なユーザーからのリクエストであるかを認証する
 * Servlet Filter.
 * @author opentone
 */
public class FeedAuthenticationFilter implements Filter {

    /** logger. */
    private static final Logger log = LoggerFactory.getLogger(FeedAuthenticationFilter.class);
    /**
     * web.xmlに指定するBASIC認証オブジェクト生成ファクトリ名のキー.
     */
    public static final String AUTHENTICATOR_FACTORY = "authenticatorFactory";
    /**
     * 認証済のユーザー情報をセッションに格納するためのキー.
     */
    public static final String KEY_AUTH_USER
            = FeedAuthenticationFilter.class.getName() + "_AUTH_USER";

    /**
     * Authenticatorを生成するファクトリー.
     */
    private FeedAuthenticatorFactory factory;

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        //  何もしない
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter
     *      #doFilter(javax.servlet.ServletRequest,
     *              javax.servlet.ServletResponse,
     *              javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        AuthUser u = authenticate(req, res);
        if (u == null) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        storeAuthenticateUser(req, res, u);
        chain.doFilter(request, response);
    }

    private AuthUser authenticate(HttpServletRequest request, HttpServletResponse response) {
        String userId = request.getParameter("userId");
        String feedKey = request.getParameter("feedKey");
        try {
            FeedAuthenticator auth = factory.getAuthenticator();
            return auth.authenticate(userId, feedKey);
        } catch (AuthenticateException e) {
            log.debug(String.format("Authenticate failed. %s", userId), e);
            return null;
        }
    }

    private void storeAuthenticateUser(
        HttpServletRequest request, HttpServletResponse response, AuthUser user) {
        HttpSession session = request.getSession();
        if (session != null) {
            session.setAttribute(KEY_AUTH_USER, user);
        }
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(FilterConfig filterConfig) throws ServletException {
        String name = filterConfig.getInitParameter(AUTHENTICATOR_FACTORY);
        if (StringUtils.isEmpty(name)) {
            log.error("Authenticator factory not specified.");
            throw new ServletException("Authenticator factory not specified.");
        }
        setupAuthenticator(name, filterConfig);
    }

    private void setupAuthenticator(String name, FilterConfig filterConfig)
            throws ServletException {
        try {
            factory = (FeedAuthenticatorFactory) Class.forName(name).newInstance();
            factory.setContext(
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                            filterConfig.getServletContext()));
        } catch (Exception e) {
            log.error(String.format("Authenticator factory not instanciated. %s", name),
                    e);
            throw new ServletException(e);
        }
    }
}
