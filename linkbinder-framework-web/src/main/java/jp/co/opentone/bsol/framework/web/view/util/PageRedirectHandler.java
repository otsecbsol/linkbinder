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

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;

/**
 * ページリダイレクト処理ハンドラクラス.
 * @author opentone
 *
 */
public class PageRedirectHandler {

    /**
     * FacesContext.
     */
    private FacesContext facesContext;

    /**
     * リダイレクト先viewId.
     */
    private String redirectViewId;

    /**
     * パラメータMap.
     */
    private Map<String, String> paramMap;

    /**
     * FacesContext.を取得します.
     * @return FacesContext.
     */
    public FacesContext getFacesContext() {
        return facesContext;
    }

    /**
     * FacesContext.を設定します.
     * @param facesContext FacesContext.
     */
    public void setFacesContext(FacesContext facesContext) {
        this.facesContext = facesContext;
    }

    /**
     * リダイレクト先viewId.を取得します.
     * @return リダイレクト先viewId.
     */
    public String getRedirectViewId() {
        return redirectViewId;
    }

    /**
     * リダイレクト先viewId.を設定します.
     * @param redirectViewId リダイレクト先viewId.
     */
    public void setRedirectViewId(String redirectViewId) {
        this.redirectViewId = redirectViewId;
    }

    /**
     * パラメータMap.を取得します.
     * @return パラメータMap.
     */
    public Map<String, String> getParamMap() {
        return paramMap;
    }

    /**
     * パラメータMap.を設定します.
     * @param paramMap パラメータMap.
     */
    public void setParamMap(Map<String, String> paramMap) {
        if (null != paramMap) {
            this.paramMap = new HashMap<String, String>(paramMap);
        } else {
            this.paramMap = null;
        }
    }

    /**
     * コンストラクタ.
     */
    @SuppressWarnings("unused")
    private PageRedirectHandler() {
    }

    /**
     * コンストラクタ.
     * @param facesContext FacesContext.
     */
    public PageRedirectHandler(FacesContext facesContext) {
        this(facesContext, null, null);
    }

    /**
     * コンストラクタ.
     * @param facesContext FacesContext.
     * @param redirectViewId リダイレクト先viewId
     */
    public PageRedirectHandler(FacesContext facesContext, String redirectViewId) {
        this(facesContext, redirectViewId, null);
    }

    /**
     * コンストラクタ.
     * @param facesContext FacesContext.
     * @param redirectViewId リダイレクト先viewId
     * @param paramMap パラメータMap
     */
    public PageRedirectHandler(FacesContext facesContext,
                               String redirectViewId,
                               Map<String, String> paramMap) {
        this.facesContext = facesContext;
        this.redirectViewId = redirectViewId;
        setParamMap(paramMap);
    }

    /**
     * リダイレクトします.
     */
    public void redirectToViewId() {
        PageNavigationUtil.redirectToViewId(facesContext, redirectViewId, paramMap);
    }

    /**
     * パラメータを追加します.
     * @param name パラメータ名
     * @param value パラメータ値
     */
    public void addParam(String name, String value) {
        if (StringUtils.isNotEmpty(name)) {
            if (null == paramMap) {
                paramMap = new HashMap<String, String>();
            }
            paramMap.put(name, value);
        }
    }
}
