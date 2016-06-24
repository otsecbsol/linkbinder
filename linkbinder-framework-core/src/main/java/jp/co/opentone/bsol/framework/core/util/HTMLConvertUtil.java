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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * HTML形式への変換用ユーティリティクラス.
 * @author opentone
 */
public class HTMLConvertUtil implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 2209345339974805041L;

    /**
     * 空のインスタンスを生成する.
     * 外部からのインスタンス化禁止.
     */
    protected HTMLConvertUtil() {
    }

    /**
     * &、<、>、"をHTML表示用にエンコードする. また、データがNULLの場合、空文字を返却する.
     * @param data
     *            データ
     * @return エンコード済み文字列
     */
    public static Object encode(Object data) {
        Object object = "";
        if (data != null) {
            if (data instanceof String) {
                String str = (String) data;
                str = str.replaceAll("&", "&amp;");
                str = str.replaceAll("<", "&lt;");
                str = str.replaceAll(">", "&gt;");
                str = str.replaceAll("\"", "&quot;");
                object = str;
            } else {
                object = data;
            }
        }
        return object;
    }

    /**
     * 日付型データを文字列に変換する.
     * @param date
     *            日付データ
     * @param format
     *            フォーマット形式
     * @return 文字列
     */
    public static String toString(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(date);
    }

    /**
     * データを文字列に変換する.
     * @param data データ
     * @return 文字列
     */
    public static String toString(Object data) {
        if (data == null) {
            return "";
        }
        return String.valueOf(data);
    }
}
