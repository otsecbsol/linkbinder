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

/**
 *
 * <p>
 * 文字列を変換（小文字⇒大文字、半角⇒全角）するクラス.
 * @author opentone
 */
public class SearchStringConverter {
    /**
     * コンストラクタ. 外部からのインスタンス化はできない.
     */
    private SearchStringConverter() {
    }

    /**
     * 全角英数字と半角英数字の文字コードのオフセット.
     */
    private static final int OFFSET = 'Ａ' - 'A';

    /**
     * 変換対象となる半角記号の変換テーブル.
     */
    private static char[][] convTable = {
        {'!', '！'}, {'\"', '”'}, {'#', '＃'}, {'$', '＄'},
        {'%', '％'}, {'&', '＆'}, {'\'', '’'}, {'(', '（'},
        {')', '）'}, {'=', '＝'}, {'~', '～'}, {'\\', '￥'},
        {'@', '＠'}, {'{', '｛'}, {'+', '＋'}, {'*', '＊'},
        {'}', '｝'}, {',', '，'}, {'.', '．'}, {'/', '／'},
        {'?', '？'}, {'_', '＿'}, {'[', '［'}, {']', '］'},
        {':', '：'}, {';', '；'}, {'<', '＜'}, {'>', '＞'},
        {'-', '－'}, {'|', '｜'}, {'^', '＾'}, {' ', '　'}, {'`', '‘'}
    };

    /**
     * 小文字英数字を大文字英数字に変換する.
     * @param value 変換対象文字列.
     * @return 大文字に変換された文字列.
     */
    public static String toUpper(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument must have not-null value.");
        }
        return value.toUpperCase();
    }

    /**
     * 半角英数字を全角英数字に変換する.
     * @param value 変換対象文字列.
     * @return 全角に変換された文字列.
     */
    public static String toWideChar(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument must have not-null value.");
        }
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < value.length(); i++) {
            strBuf.append(toFullChar(value.charAt(i)));
        }
        return strBuf.toString();
    }

    /**
     * 小文字英数字を大文字英数字に変換し、かつ半角英数字を全角英数字に変換する.
     * @param value 変換対象文字列.
     * @return 大文字かつ全角に変換された文字列.
     */
    public static String toUpperWideChar(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Argument must have not-null value.");
        }
        return toWideChar(toUpper(value));
    }

    /**
     * 受け取った1文字を全角に変換する.
     * 変換されるのは半角英数字と記号のみ、その他はそのまま返す.
     * @param value 変換対象の1文字.
     * @return 全角に変換された1文字.
     */
    private static char toFullChar(char value) {
        char newValue = value;

        /**
         *  引数が変換対象であるか判定後、全角に変換する.
         */
        if ((value >= 'a') && (value <= 'z')
                || (value >= 'A') && (value <= 'Z')
                || (value >= '0') && (value <= '9')) {
            newValue = (char) (value + SearchStringConverter.OFFSET);
        } else if (isSign(value)) {
            newValue = toFullSign(value);
        }
        return newValue;
    }

    /**
     * 受け取った1文字が変換対象となる半角記号であるか判定する.
     * @param value 判定対象の1文字.
     * @return boolean値.
     */
    private static boolean isSign(char value) {
        for (int i = 0; i < SearchStringConverter.convTable.length; i++) {
            if (value == SearchStringConverter.convTable[i][0]) {
                return true;
            }
        }
        return false;
    }
    /**
     * 受け取った記号1文字を対応する全角の記号に変換する.
     * @param value 変換対象の記号.
     * @return 全角に変換された記号.
     */
    private static char toFullSign(char value) {
        char convChar = value;
        for (int i = 0; i < SearchStringConverter.convTable.length; i++) {
            if (value == SearchStringConverter.convTable[i][0]) {
                convChar = SearchStringConverter.convTable[i][1];
            }
        }
        return convChar;
    }
}
