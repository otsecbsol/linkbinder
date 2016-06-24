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
package jp.co.opentone.bsol.linkbinder.view.exception;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.application.FacesMessage;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.sun.faces.renderkit.ServerSideStateHelper;
import com.sun.faces.util.TypedCollections;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Message;
import jp.co.opentone.bsol.framework.core.message.MessageHolder;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.util.LogUtil;
import jp.co.opentone.bsol.framework.web.view.Page;
import jp.co.opentone.bsol.framework.web.view.flash.Flash;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.Constants;
import jp.co.opentone.bsol.linkbinder.dto.User;

/**
 * @author opentone
 */
public class LinkBinderExceptionHandler extends ExceptionHandlerWrapper {

    /** logger. */
    private static Logger log = LogUtil.getLogger();

    /** 元となる {@link ExceptionHandler}. */
    private ExceptionHandler wrapped;

    /**
     * エラーページ.
     */
    private String errorPage = "/error.jsf";

    /**
     * 元となる {@link ExceptionHandler} を指定してインスタンス化する.
     * @param wrapped 元となるオブジェクト
     */
    public LinkBinderExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ExceptionHandlerWrapper#getWrapped()
     */
    @Override
    public ExceptionHandler getWrapped() {
        return wrapped;
    }

    /* (non-Javadoc)
     * @see javax.faces.context.ExceptionHandlerWrapper#handle()
     */
    @Override
    public void handle() throws FacesException {
        Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator();
        while (it.hasNext()) {
            ExceptionQueuedEvent event = it.next();
            ExceptionQueuedEventContext c = (ExceptionQueuedEventContext) event.getSource();
            Throwable t = c.getException();

            HttpServletRequest req =
                (HttpServletRequest) c.getContext().getExternalContext().getRequest();
            HttpServletResponse res =
                (HttpServletResponse) c.getContext().getExternalContext().getResponse();
            handleException(req, res, t);
        }
    }

    public void handleException(HttpServletRequest req, HttpServletResponse res, Throwable e) {
        if (e.getCause() != null && e.getCause() instanceof InvalidOperationRuntimeException) {
            log.warn("invalid operation occurred. request was failed.");
        }

        // ViewExpiredException発生時に詳細ログを出力する
        if (isViewExpiredException(e)) {
            if (isLogViewExpiredException()) {
                logRequest(req);
                logViewMap();
            } else {
                log.warn("no logging for ViewExpiredException");
            }
        }

        setMessage(e);
        req.getSession().setAttribute("jp.co.opentone.bsol.exception", e);
        try {
            res.sendRedirect(req.getContextPath() + errorPage);
        } catch (IOException ex) {
            log.error("redirect failed.", ex);
        }

    }

    private void logRequest(HttpServletRequest req) {
        log.error("Requested: {} {}", req.getMethod(), req.getRequestURI());
        Enumeration<?> enm = req.getHeaderNames();
        log.error("Header:");
        while (enm.hasMoreElements()) {
            String name = (String) enm.nextElement();
            log.error("   {} = {}", name, req.getHeader(name));
        }

        log.error(" Parameters:");
        @SuppressWarnings("unchecked")
        Map<String, String[]> parameterMap = req.getParameterMap();
        for (Map.Entry<String, String[]> e : parameterMap.entrySet()) {
            log.error("  {} = {}", e.getKey(), StringUtils.join(e.getValue()));
        }
    }

    @SuppressWarnings("unchecked")
    private void logViewMap() {
        log.error(" ViewMap entries:");
        try {
            FacesContext c = FacesContext.getCurrentInstance();
            if (c == null) {
                log.warn(" FacesContext was released.");
                return;
            }
            ExternalContext externalContext = c.getExternalContext();
            if (externalContext == null) {
                log.warn(" ExternalContext was released.");
                return;
            }

            Map<String, Object> sessionMap = externalContext.getSessionMap();
            if (sessionMap == null) {
                log.warn(" SessionMap does not exist.");
                return;
            }

            Object u = sessionMap.get("currentUser");
            if (u != null && u instanceof User) {
                log.error("  currentUser = {}", ((User)u).getUserId());
            }
            @SuppressWarnings("rawtypes")
            Map<String, Map> logicalMap = TypedCollections.dynamicallyCastMap((Map) sessionMap
                    .get(ServerSideStateHelper.LOGICAL_VIEW_MAP), String.class, Map.class);
            if (logicalMap == null) return;

            log.error("  size: {}", logicalMap.size());
            for (@SuppressWarnings("rawtypes") Map.Entry<String, Map> e : logicalMap.entrySet()) {
                log.error("  {} = [", e.getKey());
                for (Map.Entry<String, Object[]> ae :
                            ((Map<String, Object[]>)e.getValue()).entrySet()) {
                    Object[] val = ae.getValue();
                    StringBuilder sb = new StringBuilder();
                    sb.append("    ").append(ae.getKey()).append(" = ");
                    if (val.length == 2) {
                        sb.append("      [ structure = ").append(val[0]);
                        sb.append(", savedState = ").append(logViewMapEntry(val[1])).append("]");
                    } else {
                        for (Object v : val) {
                            sb.append("      ").append(v).append(',');
                        }
                    }
                    log.error(sb.toString());
                }
                log.error("    ]");
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String logViewMapEntry(Object val) {
        if (val == null) return null;
        if (!(val instanceof Map<?, ?>)) return null;

        List<String> result = new ArrayList<String>();
        Map<String, Object> m = (Map<String, Object>)val;
        for (Map.Entry<String, Object> e : m.entrySet()) {
            StringBuilder str = new StringBuilder();
            str.append(e.getKey()).append(" = ");
            Object value = e.getValue();
            if (value != null) {
                str.append("class :").append(value.getClass().getSimpleName()).append(' ');
            }
            str.append(value);

            result.add(str.toString());
        }

        return StringUtils.join(result, ",");
    }

    private boolean isLogViewExpiredException() {
        String filename = SystemConfig.getValue(Constants.KEY_LOG_SWITCH_VIEWEXPIREDEXCEPTION);
        if (StringUtils.isEmpty(filename)) return false;

        File f = new File(filename);
        return f.exists();
    }

    private boolean isViewExpiredException(Throwable t) {
        return t instanceof ViewExpiredException
                || (t.getCause() != null && t.getCause() instanceof ViewExpiredException);
    }

    /**
     * エラー画面に表示するためのメッセージを生成、セットする.
     * @param e 発生した例外
     */
    void setMessage(Throwable e) {
        //  FacesContextが無効な状態であれば
        //  何もできない
        if (FacesContext.getCurrentInstance() == null) {
            log.warn("FacesContext was released. Nothing to do.");
            return;
        }

        ViewHelper helper = new ViewHelper();
        Message message = getMessage(e);
        FacesMessage m;
        if (message != null) {
            m = helper.createFacesMessage(message);
        } else {
            m = createFatalMessage(e);
        }
        //  追跡可能にするためメッセージにトークンを付与してログ出力
        setToken(m);
        log.error(m.getDetail(), e);

        Flash flash = new Flash();
        flash.setValue(Page.KEY_FLASH_MASSAGE, m);
    }

    void setToken(FacesMessage m) {
        long token = System.currentTimeMillis();
        String org = m.getDetail();
        m.setDetail(String.format("%s (%s)", org, token));
    }

    FacesMessage createFatalMessage(Throwable e) {
        if (e.getCause() == null) {
            FacesMessage m = new FacesMessage();
            m.setSeverity(FacesMessage.SEVERITY_FATAL);
            m.setSummary(m.getSeverity().toString());
            m.setDetail(String.format(
                        "Unhandled exception : %s",
                        e.getClass().getSimpleName()));
            return m;
        } else {
            return createFatalMessage(e.getCause());
        }
    }

    /**
     * エラー画面に表示するためのメッセージを取得する.
     * @param e 発生した例外
     * @return メッセージ. メッセージを持たない例外の場合はnull
     */
    Message getMessage(Throwable e) {
        if (e instanceof MessageHolder) {
            String messageCode = ((MessageHolder) e).getMessageCode();
            Object[] vars = ((MessageHolder) e).getMessageVars();
            return Messages.getMessage(messageCode, vars);
        }
        if (e instanceof InvalidOperationRuntimeException) {
            // 不正操作を行った場合のメッセージ情報は、
            // 予め生成されたものをそのまま使う
            return ((InvalidOperationRuntimeException) e).getCreatedMessage();
        }
        if (e.getCause() == null) {
            return null;
        }
        return getMessage(e.getCause());
    }
}
