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
package jp.co.opentone.bsol.linkbinder.view.common.module.redirect.preparer;

import java.util.Map;

import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;

/**
 * リダイレクト画面用の必要データ準備クラスインタフェース.
 * @author opentone
 */
public interface RedirectDataPreparer {
    /**
     * 準備処理実行.
     * @param module
     *            リダイレクト処理モジュール
     * @param parameterMap
     *            必要データを取得するためのキー情報格納マップ
     * @throws RedirectProcessException
     *             データ取得に失敗した場合
     */
    void process(RedirectModule module, Map<String, String> parameterMap)
        throws RedirectProcessException;
}
