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

import org.junit.Test;

/**
 * ArgumentValidatorのテストクラス.
 * <p>
 * @author opentone
 */
public class ArgumentValidatorTest {

    /**
     * validateNotNullObjectのテスト.
     * nullでない場合の判定.
     */
    @Test
    public void testValidateNotNullObject01() {
        ArgumentValidator.validateNotNull("");
    }

    /**
     * validateNotNullObjectのテスト.
     * nullの場合の例外throw.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotNullObject02() {
        ArgumentValidator.validateNotNull(null);
    }

    /**
     * validateNotNullObjectのテスト.
     * nullでない場合の判定.
     */
    @Test
    public void testValidateNotNullObjectString01() {
        ArgumentValidator.validateNotNull(new Object(), null);
        ArgumentValidator.validateNotNull(new Object(), "name");
    }

    /**
     * validateNotNullObjectのテスト.
     * nullの場合の例外throw.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotNullObjectString02() {
        ArgumentValidator.validateNotNull(null, "name");
    }

    /**
     * validateNotEmptyStringのテスト.
     * nullでない場合の判定.
     */
    @Test
    public void testValidateNotEmptyString01() {
        ArgumentValidator.validateNotEmpty("abc");
    }

    /**
     * validateNotEmptyStringのテスト.
     * nullの場合の判定.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotEmptyString02() {
        ArgumentValidator.validateNotEmpty(null);
    }

    /**
     * validateNotEmptyStringのテスト.
     * 空文字列場合の判定.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotEmptyString03() {
        ArgumentValidator.validateNotEmpty("");
    }

    /**
     * validateNotEmptyStringString
     * nullでない場合の判定.
     */
    @Test
    public void testValidateNotEmptyStringString01() {
        ArgumentValidator.validateNotEmpty("123", null);
        ArgumentValidator.validateNotEmpty("123", "");
        ArgumentValidator.validateNotEmpty("123", "name");
    }

    /**
     * validateNotEmptyStringのテスト.
     * nullの場合の判定.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotEmptyStringString02() {
        ArgumentValidator.validateNotEmpty(null, "name");
    }

    /**
     * validateNotEmptyStringのテスト.
     * 空文字列場合の判定.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateNotEmptyStringString03() {
        ArgumentValidator.validateNotEmpty("", "name");
    }

    /**
     * validateGreaterThanのテスト.
     */
    @Test
    public void testValidateGreaterThan01() {
        ArgumentValidator.validateGreaterThan(20L, 19L);
    }

    /**
     * validateGreaterThanのテスト.
     * 等しい場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateGreaterThan02() {
        ArgumentValidator.validateGreaterThan(20L, 20L);
    }

    /**
     * validateGreaterThanのテスト.
     * 小さい場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateGreaterThan03() {
        ArgumentValidator.validateGreaterThan(-20L, -19L);
    }

    /**
     * validateGreaterThanのテスト.
     * 検証対象がnullの場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateGreaterThan04() {
        ArgumentValidator.validateGreaterThan(null, -19L);
    }

    /**
     * validateGreaterThanのテスト.
     * 比較相手がnullの場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateGreaterThan05() {
        ArgumentValidator.validateGreaterThan(0L, null);
    }

    /**
     * validateEqualsのテスト.
     */
    @Test
    public void testValidateEqual01() {
        String a = "abc";
        String b = "abc";
        ArgumentValidator.validateEquals(a, b);
    }

    /**
     * validateEqualsのテスト.
     */
    @Test
    public void testValidateEqual02() {
        ArgumentValidator.validateEquals("", "");
    }

    /**
     * validateEqualsのテスト.
     * 検証対象がnullの場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateEqual04() {
        ArgumentValidator.validateEquals(null, "def");
    }

    /**
     * validateEqualsのテスト.
     * 比較相手がnullの場合
     */
    @Test(expected = IllegalArgumentException.class)
    public void testValidateEqual05() {
        ArgumentValidator.validateEquals("abc", null);
    }
}
