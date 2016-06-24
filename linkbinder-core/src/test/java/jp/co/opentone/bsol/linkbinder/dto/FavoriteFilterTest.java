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
package jp.co.opentone.bsol.linkbinder.dto;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.util.JSONUtil;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.linkbinder.dto.code.CorresponStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.ReadStatus;
import jp.co.opentone.bsol.linkbinder.dto.code.WorkflowStatus;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCorresponCondition;

/**
 * FavoriteFilter のテストクラス.
 * 「通常のゲッターセッター」以外のテストを実施する
 * @author opentone
 */
public class FavoriteFilterTest extends AbstractTestCase {

    private FavoriteFilter target;

    @Before
    public void setUp() {
        this.target = new FavoriteFilter();
    }


    /**
     * setSearchConditionsFromJson のテスト.
     * 当該メソッド でセットした値が、getSearchConditions メソッドで取得できる事を確認。
     * @throws Exception
     */
    @Test
    public void testSetSearchConditionsFromJson_Normal01() throws Exception {
        // prepare
        SearchCorresponCondition cond = generateDummySearchCorresponCondition();

        // execute
        this.target.setSearchConditionsToJson(cond);
        String actual = this.target.getSearchConditions();

        // verify
        assertNotNull(actual);
        assertEquals("actual not equals.", JSONUtil.encode(cond), actual);
    }

    /**
     * getSearchConditionsAsJson のテスト.
     * SearchConditions にNULLをセットするテスト。
     * @throws Exception
     */
    @Test
    public void testSearchConditionsAsJson_Normal02() throws Exception {
        // prepare
        SearchCorresponCondition cond = generateDummySearchCorresponCondition();
        this.target.setSearchConditionsToJson(cond);

        // execute
        this.target.setSearchConditions(null);
        SearchCorresponCondition actual = this.target.getSearchConditionsAsObject();
        // verify
        assertNull(actual);
    }


    /**
     * テスト用のダミークラスを作成.
     * @return
     * @throws ParseException
     */
    private SearchCorresponCondition generateDummySearchCorresponCondition()
        throws ParseException {
        CorresponType ct1 = new CorresponType();
        ct1.setId(1L);
        CorresponType ct2 = new CorresponType();
        ct1.setId(11L);
        CorresponType[] corresponTypes = {ct1, ct2};
        WorkflowStatus[] workflowStatuses = {WorkflowStatus.ISSUED, WorkflowStatus.DRAFT};
        ReadStatus[] readStatuses = {ReadStatus.NEW, ReadStatus.READ};
        CorresponStatus[] corresponStatuses = {CorresponStatus.OPEN, CorresponStatus.CANCELED};
        User user1 = new User();
        user1.setEmpNo("901");
        User user2 = new User();
        user1.setEmpNo("902");
        User user3 = new User();
        user1.setEmpNo("903");
        User user5 = new User();
        user1.setEmpNo("905");
        User[] addressUsers = {user1, user2, user3};
        User[] workflowUsers = {user1, user5};

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date fromCreatedOn = df.parse("2011-01-02");
        Date toCreatedOn = df.parse("2011-01-03");
        Date fromIssuedOn = df.parse("2011-01-04");
        Date toIssuedOn = df.parse("2011-01-05");
        Date fromDeadlineForReply = df.parse("2011-01-06");
        Date toDeadlineForReply = df.parse("2011-01-07");

        SearchCorresponCondition cond = new SearchCorresponCondition();
        cond.setCorresponNo("1");
        cond.setIncludingRevision(true);
        cond.setCorresponTypes(corresponTypes);
        cond.setWorkflowStatuses(workflowStatuses);
        cond.setReadStatuses(readStatuses);
        cond.setCorresponStatuses(corresponStatuses);
//        cond.setAddressUsers(addressUsers);
        cond.setFromUsers(workflowUsers);
        cond.setToUsers(addressUsers);
//        cond.setWorkflowUsers(workflowUsers);
//        cond.setGroupFrom(true);
        cond.setGroupTo(false);
        cond.setGroupCc(true);
        cond.setGroupUnreplied(false);
        cond.setCustomFieldNo(8L);
        cond.setCustomFieldValue("dummy field");
        cond.setFromCreatedOn(fromCreatedOn);
        cond.setToCreatedOn(toCreatedOn);
        cond.setFromIssuedOn(fromIssuedOn);
        cond.setToIssuedOn(toIssuedOn);
        cond.setFromDeadlineForReply(fromDeadlineForReply);
        cond.setToDeadlineForReply(toDeadlineForReply);
        cond.setSort("dummy sort");
        return cond;
    }

//    /**
//     * convertToMap のテスト.
//     * @throws Exception
//     */
//    @Test
//    public void testConvertToMap_Normal01() throws Exception {
//        // prepare
//        String cond = "key1:a, key2:bb, key3:ccc";
//
//        // execute
//        Map<String, String> actual = target.convertToMap(cond);
//
//        // verify
//        Map<String, String> expected = new HashMap<String, String>();
//        expected.put("key1", "a");
//        expected.put("key2", "bb");
//        expected.put("key3", "ccc");
//        assertEquals("map length.", 3, actual.size());
//        assertTrue("key1 not contains.", actual.containsKey("key1"));
//        assertTrue("key2 not contains.", actual.containsKey("key2"));
//        assertTrue("key3 not contains.", actual.containsKey("key3"));
//        assertEquals("key1.value is not equals.", "a", actual.get("key1"));
//        assertEquals("key2.value is not equals.", "bb", actual.get("key2"));
//        assertEquals("key3.value is not equals.", "ccc", actual.get("key3"));
//    }
//    /**
//     * convertToMap のテスト.
//     * @throws Exception
//     */
//    @Test
//    public void testConvertToMap_Normal02() throws Exception {
//        // prepare
//        String cond = "key1:a";
//
//        // execute
//        Map<String, String> actual = target.convertToMap(cond);
//
//        // verify
//        Map<String, String> expected = new HashMap<String, String>();
//        expected.put("key1", "a");
//        assertEquals("map length.", 1, actual.size());
//        assertTrue("key1 not contains.", actual.containsKey("key1"));
//        assertEquals("key1.value is not equals.", "a", actual.get("key1"));
//    }
//    /**
//     * convertToMap のテスト.
//     * 空文字列の変換テスト。
//     * @throws Exception
//     */
//    @Test
//    public void testConvertToMap_Normal03() throws Exception {
//        // prepare
//        String cond = "";
//
//        // execute
//        Map<String, String> actual = target.convertToMap(cond);
//
//        // verify
//        Map<String, String> expected = new HashMap<String, String>();
//        expected.put("key1", "a");
//        assertNull("map is not null.", actual);
//    }
//    /**
//     * convertToMap のテスト.
//     * NULL文字列の変換テスト。
//     * @throws Exception
//     */
//    @Test
//    public void testConvertToMap_Normal04() throws Exception {
//        // prepare
//        String cond = null;
//
//        // execute
//        Map<String, String> actual = target.convertToMap(cond);
//
//        // verify
//        Map<String, String> expected = new HashMap<String, String>();
//        expected.put("key1", "a");
//        assertNull("map is not null.", actual);
//    }
//    /**
//     * getSearchConditionsAsJson のテスト.
//     * ただの文字列をセットし、JSON形式で取得する
//     * @throws Exception
//     */
//    @Test
//    public void convertToString_Normal01() throws Exception {
//        // prepare
//        Map<String, String> cond = new HashMap<String, String>();
//        target.setSearchConditions(cond);
//
//        // execute
//        target.getSearchConditionsAsJson();
//
//        // verify
//        fail("JSONException must be occured.");
//    }
}
