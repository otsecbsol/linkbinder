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

import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectModule;
import jp.co.opentone.bsol.linkbinder.view.common.module.redirect.RedirectProcessException;

/**
 * リダイレクト処理クラスインタフェース.
 * @author opentone
 */
public interface RedirectProcessor {
    /**
     * リダイレクト処理を行う.
     * @param module
     *            リダイレクト処理モジュール
     * @throws RedirectProcessException
     *             リダイレクト用データの準備に失敗
     */
    void setupRedirect(RedirectModule module) throws RedirectProcessException;
}
