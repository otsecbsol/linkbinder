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

import java.io.IOException;

import javax.faces.application.Application;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.co.opentone.bsol.framework.web.extension.jsf.exception.NavigationFailureRuntimeException;

/**
 * 当フレームワークの拡張NavigationHandler.
 * @author opentone
 */
public class ExtendedNavigationHandler extends NavigationHandler {

    /**
     * デフォルトで設定されているNavigation Handler.
     */
    private NavigationHandler orgHandler;

    /**
     * logger.
     */
    private final Logger log = LoggerFactory.getLogger(ExtendedNavigationHandler.class);

    /**
     * コンストラクタ.
     */
    public ExtendedNavigationHandler() {
    }

    /**
     * このオブジェクトをJSFのNavigation Handlerとして設定する.
     * @param helper
     *            JSFヘルパー
     */
    public void setUp(FacesHelper helper) {
        Application app = helper.getContext().getApplication();
        NavigationHandler org = app.getNavigationHandler();
        if (!(org instanceof ExtendedNavigationHandler)) {
            orgHandler = org;
            app.setNavigationHandler(this);
        }
    }

    /*
     * (非 Javadoc)
     * @see
     * javax.faces.application.NavigationHandler#handleNavigation(javax.faces
     * .context.FacesContext, java.lang.String, java.lang.String)
     */
    @Override
    public void handleNavigation(FacesContext context, String fromAction, String outcome) {
        FacesHelper helper = new FacesHelper(context);
        if (StringUtils.isNotEmpty(outcome)) {
            // PRG pattern:
            // outcomeからPageオブジェクトが特定できれば
            // そのページへリダイレクトする.
            // 特定できなければfaces-configの定義通りに遷移する.
            String pageName = helper.getPageName(outcome);
            if (StringUtils.isNotEmpty(pageName) && helper.getPage(pageName) != null) {
                try {
                    String url = helper.toUrl(outcome);
                    context.getExternalContext().redirect(url);
                    context.responseComplete();

                    if (log.isDebugEnabled()) {
                        log.debug("redirected to {}", url);
                    }
                } catch (IOException e) {
                    throw new NavigationFailureRuntimeException(e);
                }
            } else {
                orgHandler.handleNavigation(context, fromAction, outcome);
                if (log.isDebugEnabled()) {
                    log.debug("navigated by default handler {}", orgHandler.getClass().getName());
                }
            }
        }
    }
}
