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
package jp.co.opentone.bsol.framework.extension.jsf;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.context.ResponseStream;
import javax.faces.context.ResponseWriter;
import javax.faces.render.RenderKit;

import org.springframework.mock.web.MockHttpServletRequest;

import com.sun.faces.application.ApplicationImpl;

/**
 * FacesContextMock Class.
 *
 * <p>
 * $Date: 2011-06-20 19:57:39 +0900 (月, 20  6 2011) $
 * $Rev: 4186 $
 * $Author: aoyagi $
 */
public class FacesContextMock extends FacesContext {
    /**
     * externalContext.
     */
    public ExternalContextMock externalContext = new ExternalContextMock();

    /**
     * EXPECTED_CLIENT_ID.
     */
    public static String EXPECTED_CLIENT_ID;

    /**
     * EXPECTED_MESSAGE.
     */
    public static FacesMessage EXPECTED_MESSAGE;

    public static boolean IS_ADD_MESSAGE_CALLED;

    public static void initialize() {
        FacesContext.setCurrentInstance(new FacesContextMock());
    }

    public static void tearDown() {
        EXPECTED_CLIENT_ID = null;
        EXPECTED_MESSAGE = null;
        IS_ADD_MESSAGE_CALLED = false;
    }

    public FacesContextMock() {
    }

    @Override
    public void addMessage(String clientId, FacesMessage message) {
        assertEquals(EXPECTED_CLIENT_ID, clientId);
        assertEquals(EXPECTED_MESSAGE.getSeverity(), message.getSeverity());
        assertEquals(EXPECTED_MESSAGE.getSummary(), message.getSummary());
        assertEquals(EXPECTED_MESSAGE.getDetail(), message.getDetail());
        IS_ADD_MESSAGE_CALLED = true;
    }

    @Override
    public Application getApplication() {
        return new ApplicationImpl();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getClientIdsWithMessages() {
        return null;
    }

    @Override
    public ExternalContext getExternalContext() {
        return externalContext;
    }

    @Override
    public Severity getMaximumSeverity() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getMessages() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator getMessages(String clientId) {
        return null;
    }

    @Override
    public RenderKit getRenderKit() {
        return null;
    }

    @Override
    public boolean getRenderResponse() {
        return false;
    }

    @Override
    public boolean getResponseComplete() {
        return false;
    }

    @Override
    public ResponseStream getResponseStream() {
        return null;
    }

    @Override
    public ResponseWriter getResponseWriter() {
        return null;
    }

    @Override
    public UIViewRoot getViewRoot() {
        return new UIViewRoot();
    }

    @Override
    public void release() {
    }

    @Override
    public void renderResponse() {
    }

    @Override
    public void responseComplete() {
    }

    @Override
    public void setResponseStream(ResponseStream responseStream) {
    }

    @Override
    public void setResponseWriter(ResponseWriter responseWriter) {
    }

    @Override
    public void setViewRoot(UIViewRoot root) {
    }

    /**
     * ExternalContextMock.
     */
    public static class ExternalContextMock extends ExternalContext {

        /**
         * applicationMap.
         */
        private Map<String, Object> applicationMap = new HashMap<String, Object>();
        /**
         * sessionMap.
         */
        private Map<String, Object> sessionMap = new HashMap<String, Object>();
        /**
         * requestMap.
         */
        private Map<String, Object> requestMap = new HashMap<String, Object>();

        private MockHttpServletRequest req = new MockHttpServletRequest();

        @Override
        public void dispatch(String path) throws IOException {
        }

        @Override
        public String encodeActionURL(String url) {
            return null;
        }

        @Override
        public String encodeNamespace(String name) {
            return null;
        }

        @Override
        public String encodeResourceURL(String url) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getApplicationMap() {
            return applicationMap;
        }

        @Override
        public String getAuthType() {
            return null;
        }

        @Override
        public Object getContext() {
            return null;
        }

        @Override
        public String getInitParameter(String name) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getInitParameterMap() {
            return null;
        }

        @Override
        public String getRemoteUser() {
            return null;
        }

        @Override
        public Object getRequest() {
            return req;
        }

        @Override
        public String getRequestContextPath() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestCookieMap() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestHeaderMap() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestHeaderValuesMap() {
            return null;
        }

        @Override
        public Locale getRequestLocale() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator getRequestLocales() {
            List<Locale> locales = new ArrayList<Locale>();
            locales.add(Locale.getDefault());
            return locales.iterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestMap() {
            return requestMap;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestParameterMap() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Iterator getRequestParameterNames() {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getRequestParameterValuesMap() {
            return null;
        }

        @Override
        public String getRequestPathInfo() {
            return null;
        }

        @Override
        public String getRequestServletPath() {
            return null;
        }

        @Override
        public URL getResource(String path) throws MalformedURLException {
            return null;
        }

        @Override
        public InputStream getResourceAsStream(String path) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Set getResourcePaths(String path) {
            return null;
        }

        @Override
        public Object getResponse() {
            return null;
        }

        @Override
        public Object getSession(boolean create) {
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Map getSessionMap() {
            return sessionMap;
        }

        @Override
        public Principal getUserPrincipal() {
            return null;
        }

        @Override
        public boolean isUserInRole(String role) {
            return false;
        }

        @Override
        public void log(String message) {

        }

        @Override
        public void log(String message, Throwable exception) {
        }

        @Override
        public void redirect(String url) throws IOException {
        }

        /* (non-Javadoc)
         * @see javax.faces.context.ExternalContext#getFlash()
         */
        @Override
        public Flash getFlash() {
            return new FlashMock();
        }
    }

    public static class FlashMock extends Flash {

        /* (non-Javadoc)
         * @see java.util.Map#size()
         */
        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        /* (non-Javadoc)
         * @see java.util.Map#isEmpty()
         */
        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Map#containsKey(java.lang.Object)
         */
        @Override
        public boolean containsKey(Object key) {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Map#containsValue(java.lang.Object)
         */
        @Override
        public boolean containsValue(Object value) {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see java.util.Map#get(java.lang.Object)
         */
        @Override
        public Object get(Object key) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Map#put(java.lang.Object, java.lang.Object)
         */
        @Override
        public Object put(String key, Object value) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Map#remove(java.lang.Object)
         */
        @Override
        public Object remove(Object key) {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Map#putAll(java.util.Map)
         */
        @Override
        public void putAll(Map<? extends String, ? extends Object> m) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see java.util.Map#clear()
         */
        @Override
        public void clear() {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see java.util.Map#keySet()
         */
        @Override
        public Set<String> keySet() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Map#values()
         */
        @Override
        public Collection<Object> values() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see java.util.Map#entrySet()
         */
        @Override
        public Set<java.util.Map.Entry<String, Object>> entrySet() {
            // TODO Auto-generated method stub
            return null;
        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#isKeepMessages()
         */
        @Override
        public boolean isKeepMessages() {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#setKeepMessages(boolean)
         */
        @Override
        public void setKeepMessages(boolean newValue) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#isRedirect()
         */
        @Override
        public boolean isRedirect() {
            // TODO Auto-generated method stub
            return false;
        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#setRedirect(boolean)
         */
        @Override
        public void setRedirect(boolean newValue) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#putNow(java.lang.String, java.lang.Object)
         */
        @Override
        public void putNow(String key, Object value) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#keep(java.lang.String)
         */
        @Override
        public void keep(String key) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#doPrePhaseActions(javax.faces.context.FacesContext)
         */
        @Override
        public void doPrePhaseActions(FacesContext ctx) {
            // TODO Auto-generated method stub

        }

        /* (non-Javadoc)
         * @see javax.faces.context.Flash#doPostPhaseActions(javax.faces.context.FacesContext)
         */
        @Override
        public void doPostPhaseActions(FacesContext ctx) {
            // TODO Auto-generated method stub

        }

    }
}
