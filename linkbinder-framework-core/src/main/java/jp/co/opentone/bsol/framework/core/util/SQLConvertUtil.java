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
import java.util.Date;

import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;

/**
 * SQLへの変換用ユーティリティクラス.
 * @author opentone
 */
public class SQLConvertUtil implements Serializable {


    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 8867636556901579571L;

    /**
     * 空のインスタンスを生成する.
     */
    private SQLConvertUtil() {
    }

    /**
     * 'を''に、\を\\に変換する. 値がnullの場合、空文字に変換する. $value$指定をしているパラメータは必ず変換すること.
     * @param data
     *            データ
     * @return エンコード済み文字列
     */
    public static String encode(String data) {
        String str = "";
        if (data != null) {
            str = data;
            str = str.replaceAll("'", "''");
            str = str.replaceAll("\\\\", "\\\\\\\\");
        }
        return str;
    }

    /**
     * 文字列がNULLか空文字の場合、DBValue.STRING_NULLに変換する.
     * @param data
     *            文字列
     * @return NULLか空文字の場合、DBValue.STRING_NULL それ以外の場合は引数を返却
     */
    public static String parseNull(String data) {
        if (data == null || data.length() == 0) {
            return DBValue.STRING_NULL;
        }
        return data;
    }

    /**
     * 日付がNULLの場合、DBValue.DATE_NULLに変換する.
     * @param data
     *            日付
     * @return NULLの場合、DBValue.DATE_NULL それ以外の場合は引数を返却
     */
    public static Date parseNull(Date data) {
        if (data == null) {
            return DBValue.DATE_NULL;
        }
        return data;
    }

    /**
     * 数値がNULLの場合、DBValue.INTEGER_NULLに変換する.
     * @param data
     *            文字列
     * @return NULLの場合、DBValue.INTEGER_NULL それ以外の場合は引数を返却
     */
    public static Integer parseNull(Integer data) {
        if (data == null) {
            return DBValue.INTEGER_NULL;
        }
        return data;
    }
}
