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

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;

/**
 * サーバーの予め決められた場所に配置されたヘルプ文書を読み込む.
 * @author opentone
 */
public class HelpContentLoader {

    /** ヘルプ文書格納ディレクトリ定義を取得するためのキー. */
    public static final String KEY_HELP_ROOT = "help.root.dir";
    /** ヘルプ文書の文字エンコーディングを取得するためのキー. */
    public static final String KEY_HELP_ENCODING = "help.encoding";

    /** ヘルプ定義格納ディレクトリ. */
    public static final String ROOT_DIR = SystemConfig.getValue(KEY_HELP_ROOT);
    /** ヘルプ文書の文字エンコーディング. */
    public static final String ENCODING = SystemConfig.getValue(KEY_HELP_ENCODING);

    /**
     * 空のインスタンスを生成する.
     */
    public HelpContentLoader() {
    }

    /**
     * 指定されたヘルプ文書を生成する.
     * @param name ヘルプ文書の名前.実際のファイル名から拡張子を除いたもの.
     * @return ヘルプ文書.読込に失敗した場合はnull
     * @throws IOException ヘルプ文書生成に失敗
     */
    public HelpContent load(String name) throws IOException {
        File file = newFile(name);
        if (file.exists()) {
            return new HelpContent(name, loadContent(file));
        } else {
            return null;
        }
    }

    private File newFile(String name) {
        return new File(ROOT_DIR, String.format("%s.txt", name));
    }

    private String loadContent(File file) throws IOException {
        return FileUtils.readFileToString(file, ENCODING);
    }
}
