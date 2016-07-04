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

import static org.junit.Assert.*;

import org.junit.Test;

import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.User;


/**
 * 値を変換するクラスのテストクラス.
 * @author opentone
 */
public class ValueFormatterTest extends AbstractTestCase {

    /**
     * コードと名前を表示用の文字列に変換するテストケース.
     * @throws Exception
     */
    @Test
    public void testFormatCodeAndName() throws Exception {
        String expected = "YOC : YOKOHAMA";

        String code = "YOC";
        String name = "YOKOHAMA";

        String actual = ValueFormatter.formatCodeAndName(code, name);

        assertEquals(expected, actual);
    }

    /**
     * コードと名前を表示用の文字列に変換するテストケース.
     * 区切り文字を指定.
     * @throws Exception
     */
    @Test
    public void testFormatCodeAndNameDelim() throws Exception {
        String expected = "YOC---YOKOHAMA";

        String code = "YOC";
        String name = "YOKOHAMA";
        String delim = "---";

        String actual = ValueFormatter.formatCodeAndName(code, name, delim);

        assertEquals(expected, actual);
    }

    /**
     * コードと名前を表示用の文字列に変換するテストケース.
     * CodeがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatCodeAndNameNull_1() throws Exception {
        String expected = " : YOKOHAMA";

        String name = "YOKOHAMA";

        String actual = ValueFormatter.formatCodeAndName(null, name);

        assertEquals(expected, actual);
    }


    /**
     * コードと名前を表示用の文字列に変換するテストケース.
     * NameがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatCodeAndNameNull_2() throws Exception {
        String expected = "YOC : ";

        String code = "YOC";

        String actual = ValueFormatter.formatCodeAndName(code, null);

        assertEquals(expected, actual);
    }

    /**
     * コードと名前を表示用の文字列に変換するテストケース.
     * CodeとNameがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatCodeAndNameNull_3() throws Exception {
        String expected = "";

        String actual = ValueFormatter.formatCodeAndName(null, null);

        assertEquals(expected, actual);
    }

    /**
     * 値がチェック値と等しければ、ラベルに変換するテストケース.
     * @throws Exception
     */
    @Test
    public void testFormatLabel_1() throws Exception {
        Long value = new Long(2);
        Long code = new Long(2);
        String expected = "○";

        String actual = ValueFormatter.formatLabel(value, code);

        assertEquals(expected, actual);
    }

    /**
     * 値がチェック値と等しければ、ラベルに変換するテストケース.
     * @throws Exception
     */
    @Test
    public void testFormatLabel_2() throws Exception {
        Long value = new Long(2);
        Long code = new Long(4);
        String expected = "";

        String actual = ValueFormatter.formatLabel(value, code);

        assertEquals(expected, actual);
    }

    /**
     * 値がチェック値と等しければ、ラベルに変換するテストケース.
     * ValueがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatLabelNull_1() throws Exception {
        Long code = new Long(4);
        String expected = "";

        String actual = ValueFormatter.formatLabel(null, code);

        assertEquals(expected, actual);
    }


    /**
     * 値がチェック値と等しければ、ラベルに変換するテストケース.
     * codeがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatLabelNull_2() throws Exception {
        Long value = new Long(2);
        String expected = "";

        String actual = ValueFormatter.formatLabel(value, null);

        assertEquals(expected, actual);
    }

    /**
     * 値がチェック値と等しければ、ラベルに変換するテストケース.
     * valueとcodeがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatLabelNull_3() throws Exception {
        String expected = "";

        String actual = ValueFormatter.formatLabel(null, null);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNo() throws Exception {
        User user = new User();
        user.setNameE("Test User");
        user.setEmpNo("00001");

        String expected = "Test User/00001";

        String actual = ValueFormatter.formatUserNameAndEmpNo(user);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * NameがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoNull_1() throws Exception {
        User user = new User();
        user.setEmpNo("00001");

        String expected = "/00001";

        String actual = ValueFormatter.formatUserNameAndEmpNo(user);

        assertEquals(expected, actual);
    }


    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * EmpNoがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoNull_2() throws Exception {
        User user = new User();
        user.setNameE("Test User");

        String expected = "Test User/";

        String actual = ValueFormatter.formatUserNameAndEmpNo(user);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * EmpNoとNameがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoNull_3() throws Exception {
        String expected = "";

        String actual = ValueFormatter.formatUserNameAndEmpNo(new User());

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号と役職を表示用の文字列に変換するテストケース.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoAndRole() throws Exception {
        User user = new User();
        user.setNameE("Test User");
        user.setEmpNo("00001");
        user.setRole("ROLE");

        String expected = "Test User/00001 (ROLE)";

        String actual = ValueFormatter.formatUserNameAndEmpNoAndRole(user);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * RoleがNULL.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoAndRoleNull_1() throws Exception {
        User user = new User();
        user.setNameE("Test User");
        user.setEmpNo("00001");

        String expected = "Test User/00001";

        String actual = ValueFormatter.formatUserNameAndEmpNoAndRole(user);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * Roleが空文字.
     * @throws Exception
     */
    @Test
    public void testFormatUserNameAndEmpNoAndRoleNull_2() throws Exception {
        User user = new User();
        user.setNameE("Test User");
        user.setEmpNo("00001");
        user.setRole("");

        String expected = "Test User/00001";

        String actual = ValueFormatter.formatUserNameAndEmpNoAndRole(user);

        assertEquals(expected, actual);
    }

    /**
     * 名前と従業員番号を表示用の文字列に変換するテストケース.
     * Roleが空文字.
     * @throws Exception
     */
    @Test
    public void testFormatNumber() throws Exception {
        String expected = "9,999";
        String actual = ValueFormatter.formatNumber(9999);
        assertEquals(expected, actual);

        expected = "9,999,999";
        actual = ValueFormatter.formatNumber(9999999);
        assertEquals(expected, actual);

        expected = "999,999";
        actual = ValueFormatter.formatNumber(999999);
        assertEquals(expected, actual);

        expected = "999";
        actual = ValueFormatter.formatNumber(999);
        assertEquals(expected, actual);

        expected = "99";
        actual = ValueFormatter.formatNumber(99);
        assertEquals(expected, actual);

        expected = "0";
        actual = ValueFormatter.formatNumber(0);
        assertEquals(expected, actual);
    }

}
