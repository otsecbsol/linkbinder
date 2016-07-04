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
package jp.co.opentone.bsol.linkbinder.view.util.help;

import org.apache.commons.lang.RandomStringUtils;

/**
 * @author opentone
 */
public class TemporaryFileHelper {

    /**
     * 一時ファイル名に使用する文字配列.
     */
    private static final char[] RANDOM_STRING_CHARS
        = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * 一時ファイルのファイル名長.
     */
    private static final int TMP_FILENAME_LEN = 30;

    /**
     * テンポラリディレクトリパス.
     */
    private String temporaryDirectory;

    /**
     * コンストラクタ.
     * @param temporaryDirectory テンポラリディレクトリパス
     */
    public TemporaryFileHelper(String temporaryDirectory) {
        this.temporaryDirectory = temporaryDirectory;
    }

    /**
     * テンポラリディレクトリパスを取得する.
     * @return テンポラリディレクトリパス
     */
    public String getTemporaryDirectory() {
        return temporaryDirectory;
    }

    /**
     * 一時ファイルのパスを作成します.
     * @return 一時ファイルのパス
     */
    public String createTempFilePath() {
        String path = String.format(
                        "%s/%s",
                        temporaryDirectory,
                        RandomStringUtils.random(TMP_FILENAME_LEN, RANDOM_STRING_CHARS));
        return path;
    }
}
