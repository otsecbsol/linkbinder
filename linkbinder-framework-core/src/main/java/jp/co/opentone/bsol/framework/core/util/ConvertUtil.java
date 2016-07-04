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
package jp.co.opentone.bsol.framework.core.util;

import java.text.DecimalFormat;
import java.util.Base64;

import org.apache.commons.lang.StringUtils;

/**
 * 値を加工するクラス.
 * @author opentone
 */
public class ConvertUtil {

    /**
     * コンストラクタ.
     */
    private ConvertUtil() {
    }

    /**
     * 3桁区切りの文字列生成.
     * @param value 値
     * @return 3桁区切り文字列
     */
    public static String formatNumber(Integer value) {
        DecimalFormat f = new DecimalFormat("#,##0");
        return value == null ? "" : f.format(value).toString();
    }

    /**
     * 正規表現の後方参照を表す文字をエスケープする.
     * {@link String#replaceAll(String, String)}の第2引数に
     * 渡す文字が含まれている場合はこのメソッドで予めエスケープすること.
     * @param s 対象文字列
     * @return 結果
     */
    public static String escapeBackReference(String s) {
        String result = s;
        result = result.replaceAll("\\\\", "\\\\\\\\");
        result = result.replaceAll("\\$", "\\\\\\$");
        return result;
    }

    /**
     * 文字列s中の置換対象文字列を置き換えた結果を返す.
     * 置換後の文字列に"$"が含まれる場合は"$"のまま出力する.
     * 後方参照を使う場合はこのメソッドの代わりに{@link String#replaceAll(String, String)}を呼び出すこと.
     * @param s  置換対象文字列
     * @param regex 置換対象を表す正規表現
     * @param replacement 置換後の文字列
     * @return 結果
     */
    public static String replaceAllWithEscapedBackReference(
            String s, String regex, String replacement) {
        if (StringUtils.isEmpty(s)) {
            return s;
        }
        // 正規表現の後方参照を表す文字をまずは置換したうえで対象文字列を置換
        String escapedReplacement = escapeBackReference(replacement);
        return s.replaceAll(regex, escapedReplacement);
    }

    /**
     * データをBASE64形式の文字列に変換して返す.
     * @param data 変換対象
     * @return 変換後の文字列
     */
    public static String toBase64String(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
