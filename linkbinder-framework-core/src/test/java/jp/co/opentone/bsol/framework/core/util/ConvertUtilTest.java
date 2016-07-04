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
 * {@link ConvertUtil}のテストケース.
 * @author opentone
 */
public class ConvertUtilTest {

    /**
     * {@link ConvertUtil#replaceAllWithEscapedBackReference(String, String, String)}のテストケース.
     * @throws Exception
     */
    @Test
    public void testReplaceAllWithEscapedBackReference() throws Exception {
        String s;
        String regex;
        String replacement;

        // "$"が含まれる場合
        s = "foo${bar}baz";
        regex = "\\$\\{bar\\}";
        replacement = "$hoge";
        assertEquals("foo$hogebaz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        // "$"が複数含まれる場合
        replacement = "$$hoge";
        assertEquals("foo$$hogebaz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));
        replacement = "$$ho$ge";
        assertEquals("foo$$ho$gebaz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        // "$"が含まれない場合
        replacement = "hoge";
        assertEquals("foohogebaz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        // 後方参照として正しい表現の場合
        // このメソッドでは後方参照は機能しないのでそのまま出力される
        replacement = "$1hoge";
        assertEquals("foo$1hogebaz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));
        replacement = "$1hoge$2";
        assertEquals("foo$1hoge$2baz", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        // 置換対象にも置換文字列にも"$"が含まれない
        // こっちが一般的なケースと考えられる
        s = "foobarbaz";
        regex = "barbaz";
        replacement = "fuga$piyo";
        assertEquals("foofuga$piyo", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        // 置換対象文字列がnullの場合
        assertNull(ConvertUtil.replaceAllWithEscapedBackReference(null, "a", "b"));

        // "\"が含まれる場合
        s = "foo";
        regex = "f";
        replacement = "c:\\a\\b\\c$";
        assertEquals("c:\\a\\b\\c$oo", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));

        regex = "f";
        replacement = "\\\\server\\path\\file$";
        assertEquals("\\\\server\\path\\file$oo", ConvertUtil.replaceAllWithEscapedBackReference(s, regex, replacement));
    }
}
