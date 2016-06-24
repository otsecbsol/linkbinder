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
package jp.co.opentone.bsol.linkbinder.view.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.HashUtil;

/**
 * @author opentone
 */
public class MockHttpServletRequest implements HttpServletRequest {
    private static int RET_TESTCASE;
    private static String RET_CURRENT_TIME;
    /**
     * インデックス更新用の制限時間.
     */
    private static final String INDEX_MAKER_TIME = "indexupdate.time";

    /**
     *
     */
    public MockHttpServletRequest(int testCase) {
        MockHttpServletRequest.RET_TESTCASE = testCase;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getAuthType()
     */
    public String getAuthType() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getContextPath()
     */
    public String getContextPath() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getCookies()
     */
    public Cookie[] getCookies() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
     */
    public long getDateHeader(String arg0) {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
     */
    public String getHeader(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration getHeaderNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public Enumeration getHeaders(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
     */
    public int getIntHeader(String arg0) {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getMethod()
     */
    public String getMethod() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getPathInfo()
     */
    public String getPathInfo() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
     */
    public String getPathTranslated() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getQueryString()
     */
    public String getQueryString() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
     */
    public String getRemoteUser() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestURI()
     */
    public String getRequestURI() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestURL()
     */
    public StringBuffer getRequestURL() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
     */
    public String getRequestedSessionId() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getServletPath()
     */
    public String getServletPath() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getSession()
     */
    public HttpSession getSession() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
     */
    public HttpSession getSession(boolean arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
     */
    public Principal getUserPrincipal() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
     */
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
     */
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
     */
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
     */
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
     */
    public boolean isUserInRole(String arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
     */
    public Object getAttribute(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getAttributeNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration getAttributeNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getCharacterEncoding()
     */
    public String getCharacterEncoding() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentLength()
     */
    public int getContentLength() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getContentType()
     */
    public String getContentType() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getInputStream()
     */
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalAddr()
     */
    public String getLocalAddr() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalName()
     */
    public String getLocalName() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocalPort()
     */
    public int getLocalPort() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocale()
     */
    public Locale getLocale() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getLocales()
     */
    @SuppressWarnings("unchecked")
    public Enumeration getLocales() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
     */
    public String getParameter(String arg0) {
        switch (RET_TESTCASE) {
        case 1:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                Long limitedTime = Long.valueOf(SystemConfig.getValue(INDEX_MAKER_TIME));
                try {
                    Thread.sleep(limitedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 2:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return HashUtil.getString(Long.toString(System.currentTimeMillis()));
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 3:
            if (StringUtils.equals("p1", arg0)) {
                return null;
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 4:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return null;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 5:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return null;
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 6:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return null;
            } else {
            }
            break;
        case 7:
            if (StringUtils.equals("p1", arg0)) {
                return "1";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        case 8:
            if (StringUtils.equals("p1", arg0)) {
                return "2";
            } else if (StringUtils.equals("p2", arg0)) {
                RET_CURRENT_TIME = Long.toString(System.currentTimeMillis());
                return RET_CURRENT_TIME;
            } else if (StringUtils.equals("p3", arg0)){
                return HashUtil.getString(RET_CURRENT_TIME);
            } else if (StringUtils.equals("p4", arg0)){
                return "test";
            } else {
            }
            break;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterMap()
     */
    @SuppressWarnings("unchecked")
    public Map getParameterMap() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterNames()
     */
    @SuppressWarnings("unchecked")
    public Enumeration getParameterNames() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
     */
    public String[] getParameterValues(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getProtocol()
     */
    public String getProtocol() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getReader()
     */
    public BufferedReader getReader() throws IOException {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
     */
    public String getRealPath(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteAddr()
     */
    public String getRemoteAddr() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemoteHost()
     */
    public String getRemoteHost() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRemotePort()
     */
    public int getRemotePort() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
     */
    public RequestDispatcher getRequestDispatcher(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getScheme()
     */
    public String getScheme() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getServerName()
     */
    public String getServerName() {
        return null;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#getServerPort()
     */
    public int getServerPort() {
        return 0;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#isSecure()
     */
    public boolean isSecure() {
        return false;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
     */
    public void removeAttribute(String arg0) {

    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#setAttribute(java.lang.String, java.lang.Object)
     */
    public void setAttribute(String arg0, Object arg1) {

    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
     */
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException {

    }

    @Override
    public AsyncContext getAsyncContext() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public long getContentLengthLong() {
        // TODO 自動生成されたメソッド・スタブ
        return 0;
    }

    @Override
    public DispatcherType getDispatcherType() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public ServletContext getServletContext() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException {
        // TODO 自動生成されたメソッド・スタブ
        return false;
    }

    @Override
    public String changeSessionId() {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Part getPart(String arg0) throws IOException, ServletException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

    @Override
    public void login(String arg0, String arg1) throws ServletException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void logout() throws ServletException {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException {
        // TODO 自動生成されたメソッド・スタブ
        return null;
    }

}
