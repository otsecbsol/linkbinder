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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect.processor;

import java.io.IOException;
import java.util.Map;

import javax.faces.context.FacesContext;

import jp.co.opentone.bsol.framework.core.exception.ApplicationFatalRuntimeException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectScreenId;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.RedirectDataPreparer;

/**
 * 汎用リダイレクト処理クラス.
 * @author opentone
 */
public class GeneralRedirectProcessor implements RedirectProcessor {
    /**
     * 遷移先画面ID.
     */
    private RedirectScreenId redirectTo;

    /**
     * 遷移用データの準備処理を実施するオブジェクトの配列.
     */
    private RedirectDataPreparer[] transitionDataPreparers;

    /**
     * 遷移用データ取得キーかつ、リダイレクトのURLパラメータを格納するマップ.
     */
    private Map<String, String> parameterMap;

    /**
     * コンストラクタ.
     * @param redirectTo リダイレクト遷移先画面ID
     * @param parameterMap リダイレクト処理パラメータ(格納値はサブクラスに依存)
     * @param redirectDataPreparers リダイレクトデータ準備クラスオブジェクトの配列
     */
    public GeneralRedirectProcessor(RedirectScreenId redirectTo,
        Map<String, String> parameterMap, RedirectDataPreparer... redirectDataPreparers) {
        this.redirectTo = redirectTo;
        this.parameterMap = parameterMap;
        this.transitionDataPreparers = redirectDataPreparers;
    }

    /**
     * リダイレクト用のデータ準備とリダイレクト状態設定を行う.
     * @param module
     *            リダイレクト処理モジュール
     * @throws RedirectProcessException リダイレクト準備処理に失敗した場合
     */
    public void setupRedirect(RedirectModule module) throws RedirectProcessException {
        setupDataForRedirect(module);
        setupRediretState(module);
    }

    private void setupDataForRedirect(RedirectModule module) throws RedirectProcessException {
        for (RedirectDataPreparer preparer : transitionDataPreparers) {
            preparer.process(module, parameterMap);
        }
    }

    private void setupRediretState(RedirectModule module) {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            String url = createRedirectPath(module);
            context.getExternalContext().redirect(url);
            context.responseComplete();
        } catch (IOException e) {
            throw new ApplicationFatalRuntimeException(e);
        }
    }

    private String createRedirectPath(RedirectModule module) {
//        return module.getViewHelper().getBasePath() + redirectTo.getAppPath()
        return module.getViewHelper().getContextPath() + redirectTo.getAppPath()
            + createParameterString();
    }

    protected String createParameterString() {
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
            if (buf.length() == 0) {
                buf.append("?");
            } else {
                buf.append("&");
            }
            buf.append(entry.getKey());
            buf.append("=");
            buf.append(entry.getValue());
        }
        return buf.toString();
    }
}
