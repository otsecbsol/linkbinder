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

import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import jp.co.opentone.bsol.framework.core.extension.ibatis.DBValue;

public class SQLConvertUtilTest {

    /**
     * 'を''に、\を\\に変換するテスト.
     */
    @Test
    public void testEncode() {
        String expected;
        String data;

        // エンコード
        data = "' \\";
        expected = "'' \\\\";

        assertEquals(expected, SQLConvertUtil.encode(data));

        // 複数ある場合
        data = "a: ' \\ b: ' \\ c: ' \\";
        expected = "a: '' \\\\ b: '' \\\\ c: '' \\\\";

        assertEquals(expected, SQLConvertUtil.encode(data));

        // 対象の文字が含まれない場合
        data = "!\"#$%&()=^,.";
        expected = data;

        assertEquals(expected, SQLConvertUtil.encode(data));

        // 空文字の場合
        data = "";
        expected = "";

        assertEquals(expected, SQLConvertUtil.encode(data));

        // nullの場合
        data = null;
        expected = "";

        assertEquals(expected, SQLConvertUtil.encode(data));
    }

    /**
     * 文字列がNULLか空文字の場合、DBValue.STRING_NULLに変換するテスト.
     */
    @Test
    public void testParseNullString() {
        String data;
        String expected;

        // エンコード
        data = null;
        expected = DBValue.STRING_NULL;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        data = "";
        expected = DBValue.STRING_NULL;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        // 値が入っている場合
        data = "aaa";
        expected = data;

        assertEquals(expected, SQLConvertUtil.parseNull(data));
    }

    /**
     * 日付がNULLの場合、DBValue.DATE_NULLに変換するテスト.
     */
    @Test
    public void testParseNullDate() {
        Date data;
        Date expected;

        // エンコード
        data = null;
        expected = DBValue.DATE_NULL;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        // 値が入っている場合
        data = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        expected = data;
        assertEquals(expected, SQLConvertUtil.parseNull(data));
    }

    /**
     * 数値がNULLの場合、DBValue.DATE_NULLに変換するテスト.
     */
    @Test
    public void testParseNullInteger() {
        Integer data;
        Integer expected;

        // エンコード
        data = null;
        expected = DBValue.INTEGER_NULL;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        // 値が入っている場合
        data = Integer.parseInt("0");
        expected = data;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        data = 1;
        expected = data;
        assertEquals(expected, SQLConvertUtil.parseNull(data));

        Long longValue = 2L;
        data = longValue.intValue();
        expected = data;
        assertEquals(expected, SQLConvertUtil.parseNull(data));
    }
}
