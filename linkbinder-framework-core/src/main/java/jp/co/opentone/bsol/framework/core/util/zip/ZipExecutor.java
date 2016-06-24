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
package jp.co.opentone.bsol.framework.core.util.zip;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * Zip処理の規定クラス.
 * @author opentone
 */
public abstract class ZipExecutor {

    /**
     * ZIPファイルエンコードをプロパティから取得する際のキー名.
     */
    private static final String KEY_ZIP_ENCODING = "zip.encoding";

    /**
     * コンストラクタ.
     */
    protected ZipExecutor() {
    }

    /**
     * Zipエンコーディングのシステム設定値を取得します.
     * @return Zipエンコーディング設定値.
     */
    public static String getSystemConfigZipEncoding() {
        return SystemConfig.getValue(KEY_ZIP_ENCODING);
    }
}
