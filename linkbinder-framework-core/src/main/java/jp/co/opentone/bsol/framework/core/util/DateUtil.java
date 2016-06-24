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
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * 日付データのユーティリティクラス.
 * @author opentone
 */
public class DateUtil implements Serializable {

    /**
     * SerialVersionUID.
     */
    private static final long serialVersionUID = 5754914214501662733L;

    /**
     * 画面に表示する日付のフォーマット.
     */
    private static final String DATE_FORMAT_VIEW = "yyyy-MM-dd";

    /**
     * 画面に表示する日時のフォーマット.
     */
    private static final String DATETIME_FORMAT_VIEW = "yyyy-MM-dd HH:mm:ss";

    /**
     * 入力される日付のフォーマット.
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /** W3CDTF の簡易版のフォーマット定義. */
    private static final SimpleDateFormat DATE_FORMAT_W3CDTF_SIMPLE =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    /** RFC822 のフォーマット定義. */
    private static final SimpleDateFormat DATE_FORMAT_RFC822 =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z");

    /**
     * 空のインスタンスを生成する.
     * 外部からのインタンス化禁止.
     */
    private DateUtil() {
    }

    /**
     * 現在のシステム日付を取得する.
     * @return システム日付.
     */
    public static Date getNow() {

        return new Date();
    }

    /**
     * 日付を文字列に変換する(画面表示用)
     * nullの場合nullを返却.
     * @param date 日付
     * @return 変換結果 yyyy-MM-dd
     */
    public static String convertDateToStringForView(Date date) {
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_VIEW);

        try {
            result = sdf.format(date);
        } catch (NullPointerException ignore) {
            result = null;
        }
        return result;
    }

    /**
     * 日時を文字列に変換する(画面表示用)
     * nullの場合nullを返却.
     * @param date 日時
     * @return 変換結果 yyyy-MM-dd HH:mm:ss
     */
    public static String convertDatetimeToStringForView(Date date) {
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT_VIEW);

        try {
            result = sdf.format(date);
        } catch (NullPointerException ignore) {
            result = null;
        }
        return result;
    }

    /**
     * 日付を文字列に変換する(変換できない場合にはnull).
     * @param date 日付
     * @return 変換結果 yyyy-MM-dd
     */
    public static String convertDateToString(Date date) {
        String result;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        try {
            result = sdf.format(date);
        } catch (NullPointerException ignore) {
            result = null;
        }

        return result;
    }

    /**
     * 文字列を日付に変換する(変換できない場合にはnull).
     * @param format 日付書式
     * @param date 日付文字列
     * @return 変換結果
     */
    public static Date convertStringToDate(String format, String date) {
        if (StringUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);

        ParsePosition pos = new ParsePosition(0);
        Date d = sdf.parse(date, pos);
        //  日付文字列の後ろにごみが付いていた場合、
        //  変換には成功するが解析終了位置と文字列長が異なる
        if (pos.getIndex() != date.length()) {
            return null;
        }
        return d;
    }

    /**
     * 文字列を日付に変換する(変換できない場合にはnull).
     * @param date 日付文字列 yyyy-MM-dd
     * @return 変換結果
     */
    public static Date convertStringToDate(String date) {
        return convertStringToDate(DATE_FORMAT, date);
    }

    /**
     * 期限が切れているかどうか判定する.
     * @param deadline 期限
     * @return true:期限切れ
     */
    public static boolean isExpire(Date deadline) {
        if (deadline == null) {
            return false;
        }
        // 比較用に+1日する
        Calendar cal = Calendar.getInstance();
        cal.setTime(deadline);
        cal.add(Calendar.DATE, 1);
        return cal.getTime().before(getNow());
    }

    /**
     * W3CDTF 簡易版形式または RFC822 形式から別の形式に日付文字列を変換する.
     * @param   anotherFormat  W3CDTF 簡易版形式の日付文字列
     * @param dateFormatter 変換オブジェクト
     * @return  YYYY/MM/DD 形式の日付文字列
     */
    public static String convertFromAthotherFormat(
            final String anotherFormat, final SimpleDateFormat dateFormatter) {
        String convertExpression = null;
        if (anotherFormat != null) {
            try {
                Date date = new Date(
                        (DATE_FORMAT_W3CDTF_SIMPLE.parse(anotherFormat)).getTime());
                convertExpression = dateFormatter.format(date);
            } catch (ParseException e) {
                try {
                    Date date = new Date((DATE_FORMAT_RFC822.parse(anotherFormat)).getTime());
                    convertExpression = dateFormatter.format(date);
                } catch (ParseException ex) {
                    convertExpression = anotherFormat;
                }
            }
        } else {
            convertExpression = "";
        }
        return convertExpression;
    }
}
