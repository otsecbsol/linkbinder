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
package jp.co.opentone.bsol.framework.web.extension.jsf;

import java.io.Serializable;

import javax.faces.application.NavigationHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.el.VariableResolver;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.web.view.Page;

/**
 * Java Server Faces関連のヘルパー.
 * @author opentone
 */
public class FacesHelper implements Serializable {
    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -4574307645331583097L;
    /**
     * logger.
     */
    private static Logger log = LoggerFactory.getLogger(FacesHelper.class);

    /**
     * context.
     */
    private transient FacesContext context;

    /**
     * 処理対象のFaces Contextを指定してインスタンス化する.
     * @param context
     *            Faces Context
     */
    public FacesHelper(FacesContext context) {
        this.setContext(context);
    }

    /**
     * 現在のViewIdを返す.
     * @return viewId
     */
    public String getViewId() {
        String result = null;
        UIViewRoot viewRoot = context.getViewRoot();
        if (viewRoot != null) {
            result = viewRoot.getViewId();
        }
        return result;
    }

    private String[] splitQuery(String viewId) {
        String[] result = new String[2];
        int i = viewId.indexOf('?');
        if (i != -1) {
            result[0] = viewId.substring(0, i);
            result[1] = viewId.substring(i + 1);
        } else {
            result[0] = viewId;
        }
        return result;
    }

    /**
     * viewIdに対応するページオブジェクト名を返す.
     * @param viewId
     *            viewId
     * @return contextに格納されたページオブジェクトの名前
     */
    public String getPageName(String viewId) {
        viewId = splitQuery(viewId)[0];
        int start = viewId.lastIndexOf('/') + 1;
        int end = viewId.lastIndexOf('.');
        if (end < 0 || end < start) {
            end = viewId.length();
        }

        String page = viewId.substring(start, end);
        String result = page;
        if (!result.endsWith(Config.PAGE_CLASS_SUFFIX)) {
            result += Config.PAGE_CLASS_SUFFIX;
        }
        log.debug("page={}, result={}", page, result);
        return result;
    }

    /**
     * ページオブジェクトを返す.
     * @param pageName
     *            ページオブジェクト名
     * @return ページオブジェクト
     */
    public Page getPage(String pageName) {
        VariableResolver resolver = context.getApplication().getVariableResolver();
        return (Page) resolver.resolveVariable(context, pageName);
    }

    /**
     * contextに格納されたNavigationHandlerを返す.
     * @return navigationHandler
     */
    public NavigationHandler getNavigationHandler() {
        return context.getApplication().getNavigationHandler();
    }

    /**
     * 現在のリクエストがGETメソッドの場合はtrueを返す.
     * @return GETメソッドの場合はtrue、それ以外はfalse
     */
    public boolean isGetRequest() {
        HttpServletRequest req = (HttpServletRequest) context.getExternalContext().getRequest();
        return req.getMethod().equalsIgnoreCase("GET");
    }

    /**
     * Pageクラスのアクションメソッドから返された遷移先を表す文字列を 実際のURLに変換して返す.
     * @param outcome
     *            遷移先文字列
     * @return 変換されたURL
     */
    public String toUrl(String outcome) {
        String[] element = splitQuery(outcome);
        if (element[1] == null) {
            return String.format("%s%s", element[0], Config.PAGE_SUFFIX);
        } else {
            return String.format("%s%s?%s", element[0], Config.PAGE_SUFFIX, element[1]);
        }
    }

    /**
     * Faces Contextを格納する.
     * @param context
     *            Faces Context
     */
    public void setContext(FacesContext context) {
        this.context = context;
    }

    /**
     * Faces Contextを返す.
     * @return Faces Context
     */
    public FacesContext getContext() {
        return context;
    }
}
