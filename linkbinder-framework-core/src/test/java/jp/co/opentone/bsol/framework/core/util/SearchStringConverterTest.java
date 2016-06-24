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

import static org.junit.Assert.*;

import org.junit.Test;

/**
 *
 * <p>
 * @author opentone
 */
public class SearchStringConverterTest {
    /**
     * 半角数字配列
     */
    private static String[] halfNumbers = {
        "0", "1", "2", "3", "4",
        "5", "6", "7", "8", "9"
    };
    /**
     * 全角数字配列
     */
    private static String[] fullNumbers = {
        "０", "１", "２", "３", "４",
        "５", "６", "７", "８", "９"
    };
    /**
     * 半角小文字アルファベット文字配列
     */
    private static String[] halfLowerAlphabets = {
        "a", "b", "c", "d", "e", "f",
        "g", "h", "i", "j", "k", "l",
        "m", "n", "o", "p", "q", "r",
        "s", "t", "u", "v", "w", "x",
        "y", "z"
    };
    /**
     * 全角小文字アルファベット文字配列
     */
    private static String[] fullLowerAlphabets = {
        "ａ", "ｂ", "ｃ", "ｄ", "ｅ", "ｆ",
        "ｇ", "ｈ", "ｉ", "ｊ", "ｋ", "ｌ",
        "ｍ", "ｎ", "ｏ", "ｐ", "ｑ", "ｒ",
        "ｓ", "ｔ", "ｕ", "ｖ", "ｗ", "ｘ",
        "ｙ", "ｚ"
    };
    /**
     * 半角大文字アルファベット文字配列
     */
    private static String[] halfUpperAlphabets = {
        "A", "B", "C", "D", "E", "F",
        "G", "H", "I", "J", "K", "L",
        "M", "N", "O", "P", "Q", "R",
        "S", "T", "U", "V", "W", "X",
        "Y", "Z"
    };
    /**
     * 全角大文字アルファベット文字配列
     */
    private static String[] fullUpperAlphabets = {
        "Ａ", "Ｂ", "Ｃ", "Ｄ", "Ｅ", "Ｆ",
        "Ｇ", "Ｈ", "Ｉ", "Ｊ", "Ｋ", "Ｌ",
        "Ｍ", "Ｎ", "Ｏ", "Ｐ", "Ｑ", "Ｒ",
        "Ｓ", "Ｔ", "Ｕ", "Ｖ", "Ｗ", "Ｘ",
        "Ｙ", "Ｚ"
    };
    /**
     * 半角記号配列
     */
    private static String[] halfSigns = {
        "!", "\"", "#", "$",  "%", "&", "\'",
        "(", ")", "=", "~", "\\", "@", "{", "+",
        "*", "}", ",", ".", "/", "?", "_", "[",
        "]", " ", ":", ";",  "<", ">", "-", "|",
        "^"
    };
    /**
     * 全角記号配列
     */
    private static String[] fullSigns = {
        "！", "”", "＃", "＄",  "％", "＆", "’",
        "（", "）", "＝", "～", "￥", "＠", "｛", "＋",
        "＊", "｝", "，", "．", "／", "？", "＿", "［",
        "］", "　", "：", "；",  "＜", "＞", "－", "｜",
        "＾"
    };
    /**
     * tested value for testToUpper
     */
    private String tvTestToUpper;
    /**
     * expected value for testToUpper
     */
    private String evTestToUpper;
    /**
     * tested value for testToWideChar
     */
    private String tvTestToWideChar;
    /**
     * expected value for testToWideChar
     */
    private String evTestToWideChar;
    /**
     * tested value for testToUpperWideChar
     */
    private String tvTestToUpperWideChar;
    /**
     * expected value for testToUpperWideChar
     */
    private String evTestToUpperWideChar;

    /**
     * {@link jp.co.opentone.bsol.mer.common.util.SearchStringConverter#toUpper(java.lang.String)} のためのテスト・メソッド。
     */
    @Test
    public void testToUpper() {
        /**
         * Case1: NULLの場合
         */
        testToUpperCase1();
        /**
         * Case2: 空文字列の場合
         */
        testToUpperCase2();
        /**
         * Case3: スペースが含まれる場合
         */
        testToUpperCase3();
        /**
         * Case4: 小文字の場合.
         */
        testToUpperCase4();
        /**
         * Case5: 大文字の場合.
         */
        testToUpperCase5();
        /**
         * Case6: 小文字と大文字が混在する場合.
         */
        testToUpperCase6();
        /**
         * Case7: 日本語が含まれる場合.
         */
        testToUpperCase7();
        /**
         * Case8: 全角小文字の場合.
         */
        testToUpperCase8();
    }

    /**
     * {@link jp.co.opentone.bsol.mer.common.util.SearchStringConverter#toWideChar(java.lang.String)} のためのテスト・メソッド。
     */
    @Test
    public void testToWideChar() {
        /**
         * Case1: NULLの場合
         */
        testToWideCharCase1();
        /**
         * Case2: 空文字列の場合
         */
        testToWideCharCase2();
        /**
         * Case3: スペースが含まれる場合
         */
        testToWideCharCase3();
        /**
         * Case4: 半角英数字・記号の場合
         */
        testToWideCharCase4();
        /**
         * Case5: 全角英数字・記号の場合
         */
        testToWideCharCase5();
        /**
         * Case6: 半角英数字・記号と全角英数字・記号が混在する場合
         */
        testToWideCharCase6();
        /**
         * Case7: 日本語が含まれる場合
         */
        testToWideCharCase7();
    }

    /**
     * {@link jp.co.opentone.bsol.mer.common.util.SearchStringConverter#toUpperWideChar(java.lang.String)} のためのテスト・メソッド。
     */
    @Test
    public void testToUpperWideChar() {
        try {
            /**
             * Case1: NULLの場合
             */
            testToUpperWideCharCase1();
            /**
             * Case2: 空文字の場合
             */
            testToUpperWideCharCase2();
            /**
             * Case3: スペースが含まれる場合
             */
            testToUpperWideCharCase3();
            /**
             * Case4: 小文字かつ半角の英数字・記号の場合
             */
            testToUpperWideCharCase4();
            /**
             * Case5: 小文字かつ全角の英数字・記号の場合
             */
            testToUpperWideCharCase5();
            /**
             * Case6: 小文字かつ半角と全角の英数字・記号が混在する場合
             */
            testToUpperWideCharCase6();
            /**
             * Case7: 大文字かつ半角の英数字・記号の場合
             */
            testToUpperWideCharCase7();
            /**
             * Case8: 大文字かつ全角の英数字・記号の場合
             */
            testToUpperWideCharCase8();
            /**
             * Case9: 大文字かつ半角と全角の英数字・記号が混在する場合
             */
            testToUpperWideCharCase9();
            /**
             * Case10: 半角かつ大文字と小文字の英数字・記号が混在する場合
             */
            testToUpperWideCharCase10();
            /**
             * Case11: 全角かつ大文字と小文字の英数字・記号が混在する場合
             */
            testToUpperWideCharCase11();
            /**
             * Case12: 日本語が含まれる場合
             */
            testToUpperWideCharCase12();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Case1: NULLの場合
     * tv: null
     * ev: N/A （IllegalArgumentException例外がスローされる）
     */
    private void testToUpperCase1(){
        tvTestToUpper = null;
        evTestToUpper = "";

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            System.out.println("testToUpperCase1: Exception has been thrown.");
            e.printStackTrace();
        }
    }
    /**
     * Case2: 空文字の場合
     * tv: [空文字]
     * ev: [空文字]
     */
    private void testToUpperCase2(){
        tvTestToUpper = "";
        evTestToUpper = "";

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case3: スペースが含まれる場合
     * tv: abc...xyz abc...xyz
     * ev: ABC...XYZ ABC...XYZ
     */
    private void testToUpperCase3(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);
        tvTestToUpper = tvTestToUpper + ' ';
        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);

        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);
        evTestToUpper = evTestToUpper + ' ';
        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case4: 小文字の場合
     * tv: abc...xyz
     * ev: ABC...XYZ
     */
    private void testToUpperCase4(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);

        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case5: 大文字の場合
     * tv: ABC...XYZ
     * ev: ABC...XYZ
     */
    private void testToUpperCase5(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, halfUpperAlphabets);

        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case6: 小文字と大文字が混在する場合.
     * tv: abc...xyzABC...XYZ
     * ev: ABC...XYZABC...XYZ
     */
    private void testToUpperCase6(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);
        tvTestToUpper = catString(tvTestToUpper, halfUpperAlphabets);

        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);
        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case7: 日本語が含まれる場合.
     * tv: abc...xyzテストabc...xyz
     * ev: ABC...XYZテストABC...XYZ
     */
    private void testToUpperCase7(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);
        tvTestToUpper = tvTestToUpper + "テスト";
        tvTestToUpper = catString(tvTestToUpper, halfLowerAlphabets);

        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);
        evTestToUpper = evTestToUpper + "テスト";
        evTestToUpper = catString(evTestToUpper, halfUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case8: 全角小文字の場合.
     * tv: ａｂｃ...ｘｙｚ
     * ev: ＡＢＣ...ＸＹＺ
     */
    private void testToUpperCase8(){
        tvTestToUpper = "";
        evTestToUpper = "";

        tvTestToUpper = catString(tvTestToUpper, fullLowerAlphabets);

        evTestToUpper = catString(evTestToUpper, fullUpperAlphabets);

        try {
            assertEquals(evTestToUpper, SearchStringConverter.toUpper(tvTestToUpper));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Case1: NULLの場合
     * tv: null
     * ev: N/A （IllegalArgumentException例外がスローされる）
     */
    private void testToWideCharCase1(){
        tvTestToWideChar = null;
        evTestToWideChar = "";

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            System.out.println("testToWideCharCase1: Exception has been thrown.");
            e.printStackTrace();
        }
    }
    /**
     * Case2: 空文字の場合
     * tv: [空文字]
     * ev: [空文字]
     */
    private void testToWideCharCase2(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case3: スペースが含まれる場合
     * tv: !\#...;<> !\#...;<>
     * ev: ！￥＃...；＜＞　！￥＃...；＜＞
     */
    private void testToWideCharCase3(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        tvTestToWideChar = catString(tvTestToWideChar, halfSigns);
        tvTestToWideChar = tvTestToWideChar + ' ';
        tvTestToWideChar = catString(tvTestToWideChar, halfSigns);

        evTestToWideChar = catString(evTestToWideChar, fullSigns);
        evTestToWideChar = evTestToWideChar + '　';
        evTestToWideChar = catString(evTestToWideChar, fullSigns);

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case4: 半角英数字・記号の場合
     * tv: abc...xyz012...789!\#...;<>
     * ev: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     */
    private void testToWideCharCase4(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        tvTestToWideChar = catString(tvTestToWideChar, halfLowerAlphabets);
        tvTestToWideChar = catString(tvTestToWideChar, halfNumbers);
        tvTestToWideChar = catString(tvTestToWideChar, halfSigns);

        evTestToWideChar = catString(evTestToWideChar, fullLowerAlphabets);
        evTestToWideChar = catString(evTestToWideChar, fullNumbers);
        evTestToWideChar = catString(evTestToWideChar, fullSigns);

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case5: 全角英数字・記号の場合
     * tv: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     * ev: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     */
    private void testToWideCharCase5(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        tvTestToWideChar = catString(tvTestToWideChar, fullLowerAlphabets);
        tvTestToWideChar = catString(tvTestToWideChar, fullNumbers);
        tvTestToWideChar = catString(tvTestToWideChar, fullSigns);

        evTestToWideChar = catString(evTestToWideChar, fullLowerAlphabets);
        evTestToWideChar = catString(evTestToWideChar, fullNumbers);
        evTestToWideChar = catString(evTestToWideChar, fullSigns);

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case6: 半角英数字・記号と全角英数字・記号が混在する場合
     * tv: abc...xyz012...789!\#...;<>ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     * ev: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     */
    private void testToWideCharCase6(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        tvTestToWideChar = catString(tvTestToWideChar, halfLowerAlphabets);
        tvTestToWideChar = catString(tvTestToWideChar, halfNumbers);
        tvTestToWideChar = catString(tvTestToWideChar, halfSigns);
        tvTestToWideChar = catString(tvTestToWideChar, fullLowerAlphabets);
        tvTestToWideChar = catString(tvTestToWideChar, fullNumbers);
        tvTestToWideChar = catString(tvTestToWideChar, fullSigns);

        evTestToWideChar = catString(evTestToWideChar, fullLowerAlphabets);
        evTestToWideChar = catString(evTestToWideChar, fullNumbers);
        evTestToWideChar = catString(evTestToWideChar, fullSigns);
        evTestToWideChar = catString(evTestToWideChar, fullLowerAlphabets);
        evTestToWideChar = catString(evTestToWideChar, fullNumbers);
        evTestToWideChar = catString(evTestToWideChar, fullSigns);

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case7: 日本語が含まれる場合
     * tv: abc...xyzテスト012...789!\#...;<>
     * ev: ａｂｃ...ｘｙｚテスト０１２...７８９！￥＃...；＜＞
     */
    private void testToWideCharCase7(){
        tvTestToWideChar = "";
        evTestToWideChar = "";

        tvTestToWideChar = catString(tvTestToWideChar, halfLowerAlphabets);
        tvTestToWideChar = tvTestToWideChar + "テスト";
        tvTestToWideChar = catString(tvTestToWideChar, halfNumbers);
        tvTestToWideChar = catString(tvTestToWideChar, halfSigns);

        evTestToWideChar = catString(evTestToWideChar, fullLowerAlphabets);
        evTestToWideChar = evTestToWideChar + "テスト";
        evTestToWideChar = catString(evTestToWideChar, fullNumbers);
        evTestToWideChar = catString(evTestToWideChar, fullSigns);

        try {
            assertEquals(evTestToWideChar, SearchStringConverter.toWideChar(tvTestToWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case1: NULLの場合
     * tv: null
     * ev: N/A （IllegalArgumentException例外がスローされる）
     */
    private void testToUpperWideCharCase1(){
        tvTestToUpperWideChar = null;
        evTestToUpperWideChar = "";

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            System.out.println("testToUpperWideCharCase1: Exception has been thrown.");
            e.printStackTrace();
        }
    }
    /**
     * Case2: 空文字の場合
     * tv: [空文字]
     * ev: [空文字]
     */
    private void testToUpperWideCharCase2(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case3: スペースが含まれる場合
     * tv: !\#...;<> !\#...;<>
     * ev: ！￥＃...；＜＞　！￥＃...；＜＞
     */
    private void testToUpperWideCharCase3(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);
        tvTestToUpperWideChar = tvTestToUpperWideChar + ' ';
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);
        evTestToUpperWideChar = evTestToUpperWideChar + '　';
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case4: 小文字かつ半角の英数字・記号の場合
     * tv: abc...xyz012...789!\#...;<>
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase4(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case5: 小文字かつ全角の英数字・記号の場合
     * tv: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase5(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case6: 大文字かつ半角の英数字・記号の場合
     * tv: ABC...XYZ012...789!\#...;<>
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase6(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfUpperAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case7: 小文字かつ半角と全角の英数字・記号が混在する場合
     * tv: abc...xyz012...789!\#...;<>ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase7(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case8: 大文字かつ全角の英数字・記号の場合
     * tv: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase8(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullUpperAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case9: 大文字かつ半角と全角の英数字・記号が混在する場合
     * tv: ABC...XYZ012...789!\#...;<>ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase9(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfUpperAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullUpperAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case10: 半角かつ大文字と小文字の英数字・記号が混在する場合
     * tv: abc...xyz012...789!\#...;<>ABC...XYZ
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞ＡＢＣ...ＸＹＺ
     */
    private void testToUpperWideCharCase10(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfUpperAlphabets);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case11: 全角かつ大文字と小文字の英数字・記号が混在する場合
     * tv: ａｂｃ...ｘｙｚ０１２...７８９！￥＃...；＜＞ＡＢＣ...ＸＹＺ
     * ev: ＡＢＣ...ＸＹＺ０１２...７８９！￥＃...；＜＞ＡＢＣ...ＸＹＺ
     */
    private void testToUpperWideCharCase11(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullLowerAlphabets);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullSigns);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, fullUpperAlphabets);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Case12: 日本語が含まれる場合
     * tv: abc...xyzテスト012...789!\#...;<>
     * ev: ＡＢＣ...ＸＹＺテスト０１２...７８９！￥＃...；＜＞
     */
    private void testToUpperWideCharCase12(){
        tvTestToUpperWideChar = "";
        evTestToUpperWideChar = "";

        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfLowerAlphabets);
        tvTestToUpperWideChar = tvTestToUpperWideChar + "テスト";
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfNumbers);
        tvTestToUpperWideChar = catString(tvTestToUpperWideChar, halfSigns);

        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullUpperAlphabets);
        evTestToUpperWideChar = evTestToUpperWideChar + "テスト";
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullNumbers);
        evTestToUpperWideChar = catString(evTestToUpperWideChar, fullSigns);

        try {
            assertEquals(evTestToUpperWideChar, SearchStringConverter.toUpperWideChar(tvTestToUpperWideChar));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 文字列と文字配列を連結する.
     * @param base 連結する文字列 addedString 連結する文字配列
     * @return 連結された文字列
     */
    private String catString(String base, String[] addedString) {
        String result = base;

        for (int i = 0; i < addedString.length; i++) {
            result = result + addedString[i];
        }
        return result;
    }
}
