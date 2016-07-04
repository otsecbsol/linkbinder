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
package jp.co.opentone.bsol.linkbinder.view.util;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.config.SystemConfig;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link RichTextUtil}のテストケース.
 * @author opentone
 */
public class RichTextUtilTest extends AbstractTestCase{

    /**
     * テスト前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        new MockSystemConfig();
    }

    /**
     * テスト後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockSystemConfig().tearDown();
    }

    /**
     * テスト前準備.
     */
    @Before
    public void setUp() {
        MockSystemConfig.RET_VALUE = "4";
    }

    /**
     * {@link RichTextUtil#createRichText(java.util.String)}の検証.
     */
    @Test
    public void testCreateRichText() {
        RichTextUtil richTextUtil = new RichTextUtil();
        assertEquals(
            "<p>Patt<wbr/>ern&<wbr/>1234<wbr/>5678<wbr/>&lt;9&0<wbr/>&&gt;12<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01</p>\n\r<p>&nbsp;</p>\n\r<p><strong>Patt<wbr/>ern0<wbr/>1</strong></p>\n\r<p><em>Patt<wbr/>ern0<wbr/>1</em></p>\n\r<p><span style=\"text-decoration: underline;\">Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01</span></p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p style=\"text-align: center;\">Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01</p>\n\r<p style=\"text-align: right;\">Patt<wbr/>ern0<wbr/>1</p>\n\r<p style=\"text-align: right;\"><span style=\"color: #ff0000;\">Patt<wbr/>ern0<wbr/>1</span></p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<ul>\n\r<li>Patt<wbr/>ern0<wbr/>1</li>\n\r<li>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01</li>\n\r</ul>\n\r<p>&nbsp;</p>\n\r<ol>\n\r<li>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1</li>\n\r<li><a href=\"http://www\">Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01P<wbr/>atte<wbr/>rn01<wbr/>Patt<wbr/>ern0<wbr/>1</a></li>\n\r</ol>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p style=\"padding-left: 30px;\">Patt<wbr/>ern0<wbr/>1Pat<wbr/>tern<wbr/>01Pa<wbr/>tter<wbr/>n01</p>",
            richTextUtil.createRichText("<p>Pattern&12345678&lt;9&0&&gt;121Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01</p>\n\r<p>&nbsp;</p>\n\r<p><strong>Pattern01</strong></p>\n\r<p><em>Pattern01</em></p>\n\r<p><span style=\"text-decoration: underline;\">Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01</span></p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p style=\"text-align: center;\">Pattern01Pattern01</p>\n\r<p style=\"text-align: right;\">Pattern01</p>\n\r<p style=\"text-align: right;\"><span style=\"color: #ff0000;\">Pattern01</span></p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<ul>\n\r<li>Pattern01</li>\n\r<li>Pattern01Pattern01Pattern01Pattern01</li>\n\r</ul>\n\r<p>&nbsp;</p>\n\r<ol>\n\r<li>Pattern01Pattern01Pattern01Pattern01Pattern01</li>\n\r<li><a href=\"http://www\">Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01Pattern01</a></li>\n\r</ol>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p>&nbsp;</p>\n\r<p style=\"padding-left: 30px;\">Pattern01Pattern01Pattern01</p>"));
        assertEquals("", richTextUtil.createRichText(""));
    }

    /**
     * {@link RichTextUtil#createRichText(java.util.String)}の検証.
     * 実態参照のテスト.
     */
    @Test
    public void testCreateRichTextEntityReference() {
        RichTextUtil richTextUtil = new RichTextUtil();
        // 「&」の後に実態参照文字がある
        assertEquals(
            "<p>12&3<wbr/>45&lt;6<wbr/>789&nbsp;<wbr/>012&<wbr/>&gt;345<wbr/>67</p>",
            richTextUtil.createRichText("<p>12&345&lt;6789&nbsp;012&&gt;34567</p>"));
        assertEquals(
            "<p>123&<wbr/>&45&lt;<wbr/>&678<wbr/>9012<wbr/>3&nbsp;&&gt;<wbr/>4567</p>",
            richTextUtil.createRichText("<p>123&&45&lt;&67890123&nbsp;&&gt;4567</p>"));

        // タグの直後に実態参照文字から始まる
//        assertEquals(
//            "<p>&lt;&gt;12<wbr/>3&45<wbr/>&&&&nbsp;<wbr/>&67890</p>",
//            richTextUtil.createRichText("<p>&lt;&gt;123&45&&&&nbsp;&67890</p>"));

        // 実態参照文字の直前で改行コードが入る
        assertEquals(
            "<p>1234<wbr/>&lt;&gt;5&<wbr/>&nbsp;&6789</p>",
            richTextUtil.createRichText("<p>1234&lt;&gt;5&&nbsp;&6789</p>"));

        // ±（&plusmn;）、→（&rarr;）を2バイト文字として換算
        assertEquals(
            "<p>&plusmn;12<wbr/>34&lt;&gt;<wbr/>5&&nbsp;&<wbr/>6&rarr;7<wbr/>89</p>",
            richTextUtil.createRichText("<p>&plusmn;1234&lt;&gt;5&&nbsp;&6&rarr;789</p>"));
    }

    /**
     * {@link RichTextUtil#createRichText(java.util.String)}の検証.
     * maxByteの設定がない.
     */
    @Test
    public void testCreateRichTextNoSetting() {
        MockSystemConfig.RET_VALUE = "";

        RichTextUtil richTextUtil = new RichTextUtil();
        assertEquals(
            "<p><wbr/>P<wbr/>a<wbr/>t<wbr/>t<wbr/>e<wbr/>r<wbr/>n<wbr/>0<wbr/>1</p>",
            richTextUtil.createRichText("<p>Pattern01</p>"));
        assertEquals("", richTextUtil.createRichText(""));
    }


    @Test
    public void testCreateRiciTextWithLink() {
        RichTextUtil util = new RichTextUtil();
        assertEquals("<a href=\"http://www.google.co.jp?1=1&amp;2=2\">http<wbr/>://w<wbr/>ww.g<wbr/>oogl<wbr/>e.co<wbr/>.jp</a>",
                    util.createRichText("<a href=\"http://www.google.co.jp?1=1&amp;2=2\">http://www.google.co.jp</a>"));

    }

    @Test
    public void testCreateRichTextWithLinkInvalid() {
        RichTextUtil util = new RichTextUtil();
        String s = "\\\\srva033\\RG-BRZN$\\General\\08_Engineering\\03_M_P&amp;ID\\P&amp;ID Change\\P&amp;ID Change No12";
        System.out.println(util.createRichText(s));

        s = "<p>aaa<a href=\"\\\\srva033\\RG-BRZN$\\General\\08_Engineering\\03_M_P&amp;ID\\P&amp;ID Change\\P&amp;ID Change No12\">foooooooo</a></p>";
        System.out.println(util.createRichText(s));

        s = "<p>fooooooooooooooo</p>";
        System.out.println(util.createRichText(s));
    }
    public static class MockSystemConfig extends MockUp<SystemConfig> {
        static String RET_VALUE;

        @Mock
        public static String getValue(String key) {
            return RET_VALUE;
        }
    }
}
