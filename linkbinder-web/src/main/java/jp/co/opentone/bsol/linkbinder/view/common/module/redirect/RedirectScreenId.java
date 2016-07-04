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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect;

import java.util.Map;

import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.ProjectDataPreparer;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer.ProjectUserPreparer;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.processor.GeneralRedirectProcessor;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.processor.RedirectProcessor;

/**
 * リダイレクト遷移先画面ID.
 * @author opentone
 */
public enum RedirectScreenId {
    /** プロジェクトホーム画面(連携起動用). */
    PROJECT_HOME_LINKBINDER("/projectHome.jsf", true),

    /** コレポン文書表示画面(連携起動用). */
    CORRESPON_LINKBINDER("/correspon/correspon.jsf", true),

    /** プロジェクトホーム画面. */
    PROJECT_HOME("/projectHome.jsf"),

    /** コレポン文書表示画面. */
    CORRESPON("/correspon/correspon.jsf");

    /** 遷移先画面パス. */
    private String appPath;

    /** 連携起動の遷移であるかを示すフラグ. */
    private boolean fromExternal;

    /**
     * コンストラクタ.
     * @param appPath リダイレクト画面のアプリケーションパス
     */
    RedirectScreenId(String appPath) {
        this.appPath = appPath;
    }

    /**
     * コンストラクタ.
     * @param appPath アリダイレクト画面のプリケーションパス
     * @param fromExternal 連携起動用該当フラグ。true:連携起動用
     */
    RedirectScreenId(String appPath, boolean fromExternal) {
        this.appPath = appPath;
        this.fromExternal = fromExternal;
    }

    public String getAppPath() {
        return appPath;
    }

    /**
     * アプリケーションパスに対応する遷移先画面IDを返す.
     * @param appPath アプリケーションパス文字列
     * @return 一致した遷移先画面ID。一致しない場合はnullを返す。
     */
    public static RedirectScreenId getPairedIdOf(String appPath) {
        for (RedirectScreenId screenId : RedirectScreenId.values()) {
            if (!screenId.fromExternal && screenId.appPath.equals(appPath)) {
                return screenId;
            }
        }
        return null;
    }

    /**
     * 遷移先画面IDに対応したリダイレクト処理を行うオブジェクトを返す.
     * @param redirectTo 遷移先画面ID
     * @param redirectProcessParam
     *  本パラメータは遷移に必要な情報の取得キーとして利用する。
     *  またリダイレクトURLへのURLパラメータとして付与する。
     *  格納するパラメータは遷移先の画面IDごとに異なる。
     *  画面IDごとのパラメータ格納値は次の通り。
     * ・PROJECT_HOME_LINKBINDERの場合
     *     projectId : プロジェクトID
     * ・CORRESPON_LINKBINDER、CORRESPONの場合
     *     projectId : プロジェクトID
     *     id        : コレポン文書ID
     * @return リダイレクト処理オブジェクト
     */
    public static RedirectProcessor createRedirectProcessor(RedirectScreenId redirectTo,
        Map<String, String> redirectProcessParam) {
        switch (redirectTo) {
        case PROJECT_HOME_LINKBINDER:
        case CORRESPON_LINKBINDER:
            return new GeneralRedirectProcessor(
                redirectTo, redirectProcessParam,
                new ProjectDataPreparer(), new ProjectUserPreparer());
        case CORRESPON:
            return new GeneralRedirectProcessor(
                redirectTo, redirectProcessParam,
                new ProjectDataPreparer(), new ProjectUserPreparer());
        default:
            throw new UnsupportedOperationException(
                "Unsupported redirect screen id:" + redirectTo);
        }
    }

}
