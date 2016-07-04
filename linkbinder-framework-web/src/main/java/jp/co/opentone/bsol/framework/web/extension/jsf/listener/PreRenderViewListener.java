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
package jp.co.opentone.bsol.framework.web.extension.jsf.listener;

import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.PreRenderViewEvent;
import javax.faces.event.SystemEvent;

import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.web.view.ViewConst;
import jp.co.opentone.bsol.framework.web.view.listener.ExtendedActionListener;

/**
 * PreRenderイベントリスナです.
 * <p>
 * 各画面のPreRenderイベントリスナが登録されている場合はそれを呼び出します.
 * @author opentone
 */
public class PreRenderViewListener extends ExtendedSystemEventListener {

    /**
     * PreRenderViewListener設定のプリフィックス.
     */
    private static final String PROP_PRERENDER_PREFIX = "prerender";

    /**
     * 管理画面のデフォルトPreRenderリスナの設定キー名.
     */
    private static final String PRERENDER_DEFAULT_KEY = "prerender.default";

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        if (event instanceof PreRenderViewEvent) {
            UIViewRoot viewRoot = getUIViewRoot(event);
            if (null != viewRoot && StringUtils.isNotEmpty(viewRoot.getViewId())) {
                ExtendedActionListener listener
                    = createActionEventListener(event, PROP_PRERENDER_PREFIX);
                if (null == listener) {
                    listener = createActionEventListener(PRERENDER_DEFAULT_KEY);
                }
                if (null != listener) {
                    ActionEvent ae = new ActionEvent(viewRoot);
                    listener.processAction(ae);
                    // 画面表示キー値、メッセージの設定
                    //merActionListener.setViewMessage();
                    String pageIdParameter = getPageIdParameter();
                    if (StringUtils.isNotEmpty(pageIdParameter)) {
                        // 画面リダイレクト処理前のエラーメッセージ設定
                        listener.addViewMessage(pageIdParameter);
                        FacesContext fc = FacesContext.getCurrentInstance();
                        Flash f = fc.getExternalContext().getFlash();
                        f.clear();
                    }
                    listener.setViewMessage();
                } else {
                    // デフォルトPreRenderも存在しない場合は処理エラーとする.
                    throw new AbortProcessingException(
                            String.format("Can't create PreRenderViewListener. for viewId=[%s]",
                                    getViewId(event)));
                }
            }
        }
    }

    /**
     * msgIdパラメータを取得します.
     * @return msgIdパラメータ
     */
    private String getPageIdParameter() {
        FacesContext fc = FacesContext.getCurrentInstance();
        Map<String, String> paramMap = fc.getExternalContext().getRequestParameterMap();
        return paramMap.get(ViewConst.PARAM_NAME_MSG_ID);
    }
}
