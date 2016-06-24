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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/**
 * Zipファイルから含まれるファイルを抽出するクラス.
 * <h3>未対応の機能</h3>
 * <ul>
 * <li>Zipパスワードが施されているZipファイルからの抽出
 * </ul>
 * @author opentone
 */
public class ZipExtractor extends ZipExecutor {

    /**
     * Zipファイルオブジェクト.
     */
    private ZipFile zipFile;

    /**
     * Zipファイルに指定されているエンコーディング.
     * <p>指定されていない場合は設定値</p>
     */
    private String encoding;

    /**
     * Zipファイルオブジェクトを取得する.
     * @return Zipファイルオブジェクト.
     */
    public ZipFile getZipFile() {
        return this.zipFile;
    }

    /**
     * Zipファイルに指定されているエンコーディングを取得する.
     * @return the encoding
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * デフォルトコンストラクタ.
     */
    @SuppressWarnings("unused")
    private ZipExtractor() {
    }

    /**
     * コンストラクタ.
     * @param filePath 対象ファイルパス.
     */
    public ZipExtractor(String filePath) {
        this(filePath, getSystemConfigZipEncoding());
    }

    /**
     * コンストラクタ.
     * @param filePath 対象ファイルパス.
     * @param encoding エンコーディング
     */
    public ZipExtractor(String filePath, String encoding) {
        // filePathがnullであればNullPointerExceptionが発生
        File f = new File(filePath);
        // ファイルとして存在していなければ例外throw
        if (!f.exists() || !f.isFile()) {
            throw new IllegalArgumentException(
                String.format("Zip file[%s] is not found, or is not file.", filePath));
        }
        try {
            this.encoding = encoding;
            this.zipFile = new ZipFile(f, encoding);
        } catch (IOException e) {
            throw new ZipUtilRuntimeException(e);
        }
    }

    /**
     * Zipファイルに含まれるすべてのZipEntryリストを取得する.
     * @return 取得したList. 該当が1件も無い場合は空のリストを返す.
     */
    public List<ZipEntry> getZipEntriesByPath() {
        return getZipEntriesByPath(null);
    }

    /**
     * パス表現で指定したディレクトリのZipEntryのリストを取得する.
     * <p>
     * expressionで指定した正規表現に対応するエントリーを取得する.
     * (前方一致)
     * </p>
     * <h3>例:expressionに"temp/dataA/xyz"を指定した場合</h3>
     * <ul>
     * <li>temp/dataA/xyzディレクトリ及びその下の階層は含まれる.
     * <li>temp/dataA/xyzzzディレクトリ及びその下の階層は含まれる.
     * <li>temp/dataA/xyzがファイルであればそのファイルが唯一含まれる.
     * <li>
     * </ul>
     * @param expression パス表現. nullの場合はすべてを対象とする.
     * @return 取得したList. 該当が1件も無い場合は空のリストを返す.
     */
    @SuppressWarnings("rawtypes")
    public List<ZipEntry> getZipEntriesByPath(String expression) {
        List<ZipEntry> resultList = new ArrayList<ZipEntry>();

        Enumeration entryEnum = this.zipFile.getEntries();
        while (entryEnum.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entryEnum.nextElement();
            if (null != expression) {
                if (0 > entry.getName().indexOf(expression)) {
                    // 取得対象に含まない場合はリストへの追加をスキップする.
                    continue;
                }
            }
            // 日本語ファイル名を変換する.
            resultList.add(entry);
        }
        return resultList;
    }

    /**
     * ルートディレクトリ名を取得する.
     * @return ルートディレクトリ名.
     */
    public String getRootDirectory() {
        String result = null;
        // 直下のファイルがあればルート無しとする。
        List<ZipEntry> list = getZipEntriesByPath();
        for (ZipEntry entry : list) {
            int pos = entry.getName().indexOf('/');
            if (-1 == pos) {
                result = "";
                break;
            } else {
                String rootName = entry.getName().substring(0, pos);
                if (null == result) {
                    result = rootName;
                } else {
                    if (!result.equals(rootName)) {
                        // 最上位に異なるディレクトリが見つかったということは親無し
                        result = "";
                        break;
                    }
                }
            }
        }
        return result;
    }
}
