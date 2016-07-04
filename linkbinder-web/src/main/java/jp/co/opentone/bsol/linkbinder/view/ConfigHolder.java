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
package jp.co.opentone.bsol.linkbinder.view;

import javax.annotation.ManagedBean;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.linkbinder.view.servlet.ResourceVersion;
import jp.co.opentone.bsol.linkbinder.view.util.SessionTimeoutManager;

/**
 * アプリケーションの定義情報を格納するManagedBean.
 * @author opentone
 */
@ManagedBean
@Scope("application")
public class ConfigHolder extends AbstractPage {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = -8910543694277898548L;

    /**
     * システムのリソース管理バージョンを返す.
     * @return リソース管理バージョン
     */
    public String getResourceVersionNo() {
        return ResourceVersion.getVersionNo();
    }

    /**
     * セッションタイムアウトに関する情報を保持するオブジェクトを返す.
     * <p>
     * ここで返されるのは、メソッド呼出元のRequestに関する情報を保持するオブジェクト.
     * </p>
     * @return セッションタイムアウト管理オブジェクト
     */
    public SessionTimeoutManager getSessionTimeoutManager() {
        HttpServletRequest req =
            (HttpServletRequest) FacesContext.getCurrentInstance()
                    .getExternalContext().getRequest();
        SessionTimeoutManager result = new SessionTimeoutManager(req);

        return result;
    }

    /**
     * このシステムのヘルプ文書を表すURLを返す.
     * @return ヘルプ文書URL
     */
    public String getHelpDocumentUrl() {
        return SystemConfig.getValue("help.document.url");
    }
}
