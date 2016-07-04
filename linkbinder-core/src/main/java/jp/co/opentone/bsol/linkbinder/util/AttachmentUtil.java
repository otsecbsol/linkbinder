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
package jp.co.opentone.bsol.linkbinder.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.core.util.HashUtil;
import jp.co.opentone.bsol.linkbinder.Constants;

/**
 * 添付ファイルの操作に関するユーティリティ.
 * @author opentone
 */
public class AttachmentUtil {

    /**
     * 一時ファイル名の形式.
     */
    public static final String FORMAT_FILE_NAME = "upload_%s_%s_%s";
    /**
     * ファイル名として使用できない文字を表す正規表現.
     */
    public static final String KEY_SERVER_FILE_NAME_REGEX = "server.file.name.regex";
    /**
     * ファイル名として使用できない文字を置換する文字列.
     */
    public static final String KEY_SERVER_FILE_NAME_REPLACEMENT = "server.file.name.replacement";


    /**
     * ユーティリティクラスのため外部からのインスタンス化禁止.
     */
    private AttachmentUtil() {
    }

    /**
     * サーバーに一時的にファイルを保存する.
     * @param randomId ファイル名が一意になるようなランダムな文字列
     * @param baseName 基準となる名前. パスは含まない
     * @param in 保存するデータへの入力ストリーム
     * @throws IOException 保存に失敗
     * @return 生成した一時ファイルのフルパス
     */
    public static String createTempporaryFile(
            String randomId,
            String baseName,
            InputStream in
            ) throws IOException {

        String result = createFileName(randomId, baseName);
        InputStream bin = null;
        BufferedOutputStream out =
            new BufferedOutputStream(new FileOutputStream(result));
        try {
            bin = new BufferedInputStream(in);
            //CHECKSTYLE:OFF
            byte[] buf = new byte[1024 * 4];
            //CHECKSTYLE:ON
            int len;
            while ((len = bin.read(buf, 0, buf.length)) != -1) {
                out.write(buf, 0, len);
            }

            return result;
        } finally {
            if (bin != null) {
                bin.close();
            }
            out.close();
        }
    }

    /**
     * 生成した一時ファイルのフルパスを取得する.
     * @param key Key
     * @return 生成した一時ファイルのフルパス
     */
    public static String createTempporaryFile(String key) {
        return createFileName(key);
    }

    /**
     * 一時保存ファイル名を生成する.
     * @param randomId ファイル名が一意になるようなランダムな文字列
     * @param baseName 基準となる名前. パスは含まない
     * @return 生成したファイル名
     */
    public static String createFileName(String randomId, String baseName) {
        return FilenameUtils.concat(getRoot().getAbsolutePath(),
                                    String.format(FORMAT_FILE_NAME,
                                                  randomId,
                                                  HashUtil.getRandomString(randomId),
                                                  baseName));
    }

    /**
     * 一時保存ファイル名を生成する.
     * @param key Key
     * @return 生成したファイル名
     */
    public static String createFileName(String key) {
        return FilenameUtils.concat(getRoot().getAbsolutePath(), key);
    }

    /**
     * 一時ファイル保存先のルートディレクトリを返す.
     * ディレクトリが無い場合は作った後に返す.
     * @return ディレクトリ
     */
    public static File getRoot() {
        File root = new File(SystemConfig.getValue(Constants.KEY_FILE_DIR_PATH));
        if (!root.exists()) {
            root.mkdirs();
        }
        return root;
    }

    public static String getBaseFileName(String path) {
        String result;
        if (path.lastIndexOf('\\') != -1) {
            result = path.substring(path.lastIndexOf('\\') + 1);
        } else if (path.lastIndexOf('/') != -1) {
            result = path.substring(path.lastIndexOf('/') + 1);
        } else {
            result = path;
        }
        return replaceInvalidChar(result);
    }

    public static String replaceInvalidChar(String fileName) {
        String regex = SystemConfig.getValue(KEY_SERVER_FILE_NAME_REGEX);
        String replacement = SystemConfig.getValue(KEY_SERVER_FILE_NAME_REPLACEMENT);
        if (StringUtils.isEmpty(replacement)) {
            replacement = "";
        }
        return fileName.replaceAll(regex, replacement);
    }

}
