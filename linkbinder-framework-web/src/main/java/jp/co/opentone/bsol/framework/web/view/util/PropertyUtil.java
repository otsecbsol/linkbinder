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

import jp.co.opentone.bsol.framework.core.util.ArgumentValidator;

/**
 * property操作に関するユーティリティクラス.
 * @author opentone
 */
public class PropertyUtil {

    /**
     * コンストラクタ.<br/>
     * インスタンス生成は不要
     */
    private PropertyUtil() {
    }

    /**
     * viewId名称を元にしたプロパティキー名を生成します.
     * <p>
     * 例）<br/>
     * prefixに"prerender", viewIdに"/admin/adminHome.xhtml" を指定した場合、
     * 戻り値は "prerender.admin.adminHome" となります.
     * @param prefix プリフィックス
     * @param viewId viewId
     * @return String プロパティキー
     */
    public static String createPropertyKeyForViewId(String prefix, String viewId) {
        ArgumentValidator.validateNotEmpty(prefix);
        ArgumentValidator.validateNotEmpty(viewId);
        String value = viewId;
        int pos = value.lastIndexOf('.');
        if (0 < pos) {
            // 最後の ".xhtml" の部分を除去
            value = value.substring(0, pos);
        }
        String propKey = String.format("%s%s", prefix, value.replace('/', '.'));
        return propKey;
    }
}
