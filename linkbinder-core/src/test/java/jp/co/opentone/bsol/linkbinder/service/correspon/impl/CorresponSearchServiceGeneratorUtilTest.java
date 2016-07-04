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
package jp.co.opentone.bsol.linkbinder.service.correspon.impl;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.linkbinder.dto.Correspon;
import jp.co.opentone.bsol.linkbinder.dto.CorresponGroup;
import jp.co.opentone.bsol.linkbinder.dto.CorresponIndexHeader;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;

/**
 * HTML形式への変換用ユーティリティのテストクラス.
 * @author opentone
 */
public class CorresponSearchServiceGeneratorUtilTest {

    /**
     * テスト対象.
     */
    private CorresponSearchServiceGeneratorUtil util = new CorresponSearchServiceGeneratorUtil();

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceGeneratorUtil
     *     #isView(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testIsView() {
        CorresponIndexHeader header = new CorresponIndexHeader();
        header.setNo(true);
        header.setCorresponNo(true);
        util.setHeader(header);

        // 設定
        assertTrue(util.isView("no"));
        assertTrue(util.isView("corresponNo"));
        // 未設定
        assertFalse(util.isView("from"));
        // 指定文字の誤り
        assertFalse(util.isView("NO"));
        // 引数NULL
        assertFalse(util.isView(null));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceGeneratorUtil
     *     #isView(java.lang.String)} のためのテスト・メソッド.
     */
    @Test
    public void testIsViewHeaderNull() {
        assertTrue(util.isView(null));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceGeneratorUtil
     *     #isCanceled(jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus)} のためのテスト・メソッド.
     */
    @Test
    public void testIsCanceled() {
        assertTrue(util.isCanceled(CorresponStatus.CANCELED));
        assertFalse(util.isCanceled(CorresponStatus.OPEN));
        assertFalse(util.isCanceled(CorresponStatus.CLOSED));
        assertFalse(util.isCanceled(null));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceGeneratorUtil
     *     #isDeadline(java.util.Date)} のためのテスト・メソッド.
     */
    @Test
    public void testIsDeadline() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int date = now.get(Calendar.DATE);

        Date deadline;

        // 前日
        deadline = new GregorianCalendar(year, month, date - 1).getTime();
        assertTrue(util.isDeadline(deadline));

        // 当日
        deadline = new GregorianCalendar(year, month, date).getTime();
        assertFalse(util.isDeadline(deadline));

        // 翌日
        deadline = new GregorianCalendar(year, month, date + 1).getTime();
        assertFalse(util.isDeadline(deadline));

        // null
        assertFalse(util.isDeadline(null));
    }

    /**
     * {@link jp.co.opentone.bsol.linkbinder.service.correspon.impl.CorresponSearchServiceGeneratorUtil
     *     #getUnreadStyle(java.lang.String, jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus)} のためのテスト・メソッド.
     */
    @Test
    public void testGetUnreadStyle() {
        String value = "testValue";
        String unreadValue = "<span class=\"unread\">" + value + "</span>";

        assertEquals(unreadValue, util.getUnreadStyle(value, ReadStatus.NEW));
        assertEquals(value, util.getUnreadStyle(value, ReadStatus.READ));
        assertEquals(value, util.getUnreadStyle(value, null));
    }

    @Test
    public void testGetToGroup() {
        Correspon c = new Correspon();
        CorresponGroup g = new CorresponGroup();
        g.setName("Group");
        c.setToCorresponGroup(g);

        //  To(Group)が1件の場合
        c.setToCorresponGroupCount(1L);
        assertEquals("Group", util.getToGroup(c));
        //  To(Group)が複数件の場合
        c.setToCorresponGroupCount(2L);
        assertEquals("Group...", util.getToGroup(c));
    }
}
