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
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessParameterKey;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId;

/**
 * ユーザーのログイン状態を管理する{@link Filter}.
 * @author opentone
 */
public class LoginFilter extends AbstractFilter {

    /**
     * クッキーからセッションIDを取得する時のキー.
     */
    public static final String JSESSIONID = "JSESSIONID";

    /**
     * logger.
     */
    private Logger log = LoggerFactory.getLogger(getClass());

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter
     *         (javax.servlet.ServletRequest,
     *         javax.servlet.ServletResponse,
     *         javax.servlet.FilterChain)
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req  = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();

        //  ログイン状態の管理が不要なページはリクエストを処理して即終了
        if (isIgnore(uri)) {
            chain.doFilter(request, response);
            return;
        }

        //  セッションタイムアウトであれば定義済のページにリダイレクトして終了
        if (isSessionTimeout(req)) {
            redirectTo(req, res, processDirect(req, true));
            return;
        }

        //  未ログインであれば定義済のページにリダイレクトして終了
        //  ダイレクトログインかを判定
        if (!isLoggedIn(req)) {
            redirectTo(req, res, processDirect(req, false));
            return;
        }

        //  [Session Fixation対策]
        //    ログイン後に新しいsessionを開始する指定がある場合は
        //    現状のsessionを破棄し、新しいsessionを開始する
        if (isStartNewSession(req, res)) {
            startNewSession(req, res);
        }
        //  後続のFilterに処理を委譲
        chain.doFilter(request, response);
    }

    private String processDirect(HttpServletRequest req, boolean timeout) {
        String uri = req.getRequestURI();
        if (isDirectLogin(uri)) {
            try {
                setDirectLogin(req, timeout);
            } catch (Exception e) {
                // セッションがない場合もあるので、生成するように取得
                req.getSession().setAttribute("jp.co.opentone.bsol.exception", e);
                return errorPage;
            }
        }

        if (timeout) {
            return timeoutPage;
        } else {
            return redirectPage;
        }
    }

    private void setDirectLogin(HttpServletRequest request, boolean timeout) {
        // パス情報からIDを特定する
        RedirectScreenId screenId = RedirectScreenId.getPairedIdOf(request.getServletPath());
        switch (screenId) {
        case CORRESPON:
            String id = request.getParameter(RedirectProcessParameterKey.ID);
            String projectId = request.getParameter(RedirectProcessParameterKey.PROJECT_ID);

            // セッションタイムアウトの場合、ダイレクト表示以外の処理が呼ばれている可能性があるため、
            // パラメータがなければ入力チェックを行わないようにする。
            if (timeout && (StringUtils.isEmpty(id) || StringUtils.isEmpty(projectId))) {
                return;
            }

            ArgumentValidator.validateNotNull(id, RedirectProcessParameterKey.ID);
            ArgumentValidator.validateNotNull(projectId, RedirectProcessParameterKey.PROJECT_ID);

            // リダイレクトはセッションが無効な状態で処理を行うことが基本になるため、trueを指定する
            HttpSession session = request.getSession(true);
            session.setAttribute(Constants.KEY_REDIRECT_SCREEN_ID, screenId);
            session.setAttribute(RedirectProcessParameterKey.ID, id);
            session.setAttribute(RedirectProcessParameterKey.PROJECT_ID, projectId);
            break;
        default:
            throw new ApplicationFatalRuntimeException("invalid screen id");
        }
    }

    private boolean isSessionTimeout(HttpServletRequest request) {
        // WebLogicの場合、タイムアウトしてもセッションがnullにならないため、セッションがnullでなければ、
        // ログインしたことがあるものとして、タイムアウトの判定を行う。
        // Tomcatの場合、タイムアウト時にセッションがnullになるため、リクエストされたセッションIDの妥当性チェックを行う
        HttpSession session = request.getSession(false);
        return (session != null || !request.isRequestedSessionIdValid())
            && !isLoggedIn(request)
            && hasJsessionId(request);
    }

    private boolean hasJsessionId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName() != null && c.getName().equals(JSESSIONID)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isStartNewSession(
        HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        return session.getAttribute(Constants.KEY_START_NEW_SESSION) != null;
    }

    private void startNewSession(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        String oldId = session.getId();
        Map<String, Object> attributes = collectSessionAttributes(session);
        session.invalidate();
        HttpSession newSession = request.getSession(true);
        copyAttributes(newSession, attributes);

        newSession.removeAttribute(Constants.KEY_START_NEW_SESSION);

        if (log.isDebugEnabled()) {
            log.debug("session '{}' was invalidated.", oldId);
            log.debug("session '{}' was started.", newSession.getId());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> collectSessionAttributes(HttpSession session) {
        Map<String, Object> attributes = new HashMap<String, Object>();
        if (session != null) {
            for (Enumeration<String> e = session.getAttributeNames();
                    e.hasMoreElements();) {
                String key = e.nextElement();
                attributes.put(key, session.getAttribute(key));
            }
        }
        return attributes;
    }

    private void copyAttributes(HttpSession session, Map<String, Object> attributes) {
        try {
            for (Map.Entry<String, Object> e : attributes.entrySet()) {
                if (session.getAttribute(e.getKey()) != null) {
                    if (e.getValue() != null) {
                        if (log.isDebugEnabled()) {
                            log.debug("Value '{}' exists, so copy properties.", e.getKey());
                        }
                        Object dest = session.getAttribute(e.getKey());
                        PropertyUtils.copyProperties(dest, e.getValue());
                    }
                } else {
                    session.setAttribute(e.getKey(), e.getValue());
                }
            }
        } catch (IllegalAccessException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new ApplicationFatalRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        User login = (User) session.getAttribute(Constants.KEY_CURRENT_USER);
        if (login == null || StringUtils.isEmpty(login.getUserId())) {
            return false;
        }

        if (log.isDebugEnabled()) {
            log.debug("User '{}' is logged in", login.getUserId());
        }
        return true;
    }
}
