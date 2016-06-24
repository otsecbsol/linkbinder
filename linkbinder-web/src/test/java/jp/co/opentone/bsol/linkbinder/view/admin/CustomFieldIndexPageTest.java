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
package jp.co.opentone.bsol.linkbinder.view.admin;

import static jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.Resource;
import javax.faces.application.FacesMessage;
import javax.faces.model.SelectItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.CustomField;
import jp.co.opentone.bsol.linkbinder.dto.SearchCustomFieldResult;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.code.UseWhole;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCustomFieldCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CustomFieldServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CustomFieldIndexPage}のテストケース
 * @author opentone
 */
public class CustomFieldIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private CustomFieldIndexPage page;

    /**
     * 検証用データ
     */
    private List<CustomField> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCustomFieldService();
        new MockViewHelper();
        new MockDateUtil();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCustomFieldService().tearDown();;
        new MockViewHelper().tearDown();;
        new MockDateUtil().tearDown();;
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        list = getList();
    }

    @After
    public void tearDown() {
        MockCustomFieldService.RET_GENERAL_EXCEL = null;
        MockCustomFieldService.CRT_DELETE = null;
        MockCustomFieldService.RET_ASSIGN_TO = null;
        MockCustomFieldService.CRT_ASSIGN_TO = null;
        MockCustomFieldService.RET_FIND = null;
        MockCustomFieldService.RET_SAVE = null;
        MockCustomFieldService.CRT_SAVE = null;
        MockCustomFieldService.CRT_VALIDATE = null;
        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = null;
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = null;
        MockCustomFieldService.RET_VALIDATE = false;
        MockViewHelper.RET_ACTION = null;
        MockViewHelper.RET_DATA = null;
        MockViewHelper.RET_FILENAME = null;
        MockDateUtil.RET_DATE = null;

        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        MockCustomFieldService.EX_SEARCH = null;
    }

    /**
     * 初期化アクションを検証する.
     * Adminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeAdmin() throws Exception {
        // テストに必要なデータを作成する
        SearchCustomFieldResult result = createCustomFieldResult();

        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        page.initialize();

        assertEquals(list.toString(), page.getCustomFieldList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertNull(page.getProjectId());
        assertEquals(result.getCustomFieldList().toString(), page.getCustomFieldList().toString());
        assertEquals(result.getCount(), page.getDataCount());

        // 検索条件
        assertNull(page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
    }

    /**
     * 初期化アクションを検証する.
     * Projectの場合
     * @throws Exception
     */
    @Test
    public void testInitializeProject() throws Exception {
        // テストに必要なデータを作成する
        SearchCustomFieldResult result = createCustomFieldResult();

        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;
        page.setProjectId("PJ1");
        MockAbstractPage.RET_PROJID = "PJ1";

        List<CustomField> cfNotProjectList = getNotProjectList();
        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = cfNotProjectList;

        page.initialize();

        assertEquals(list.toString(), page.getCustomFieldList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("PJ1", page.getProjectId());
        assertEquals(result.getCustomFieldList().toString(), page.getCustomFieldList().toString());
        assertEquals(result.getCount(), page.getDataCount());

        assertEquals(cfNotProjectList.toString(), page.getAssignableCustomFieldList().toString());
        assertNotNull(page.getSelectCustomFieldList());
        assertTrue(page.getSelectCustomFieldList().size() == page.getSelectCustomFieldList().size());

        // 検索条件
        assertEquals("PJ1", page.getCondition().getProjectId());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
    }

    /**
     * 検索アクションを検証する.
     * @throws Exception
     */
    @Test
    public void testSearchAdmin() throws Exception {
        // テストに必要なデータを作成する

        // アクション名
        page.setActionName("Search");

        SearchCustomFieldResult result = createCustomFieldResult();
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        page.setCondition(new SearchCustomFieldCondition());

        MockAbstractPage.RET_PROJID = "PJ1";

        // 検索条件を入力
        String label = "TEST1";
        page.setLabel(label);
        page.setPageNo(3);

        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = getNotProjectList();
        page.search();

        assertEquals(1, page.getPageNo()); // 初期化
        assertEquals(result.getCustomFieldList().toString(), page.getCustomFieldList().toString());

        assertEquals(3, page.getDataCount());

        for (int i = 0; i < page.getSelectCustomFieldList().size(); i++) {
            SelectItem item = page.getSelectCustomFieldList().get(i);
            CustomField selectCustomField = page.getAssignableCustomFieldList().get(i);
            assertEquals(selectCustomField.getId().toString(), item.getValue().toString());
            assertEquals(selectCustomField.getLabel().toString(), item.getLabel().toString());
        }

        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(label, page.getCondition().getLabel());

        // 再設定
        List<CustomField> newList = createNewList();
        List<CustomField> newSelectList = createNewSelectList();
        result = new SearchCustomFieldResult();
        result.setCustomFieldList(createNewList());

        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = createNewSelectList();

        result.setCount(54); // 増加
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        // 検索条件を入力
        String newLabel = "NewSearchName";
        page.setLabel(newLabel);
        page.setPageNo(5);

        page.search();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCustomFieldList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        for (int i = 0; i < page.getSelectCustomFieldList().size(); i++) {
            SelectItem item = page.getSelectCustomFieldList().get(i);
            CustomField selectCustomField = newSelectList.get(i);
            assertEquals(selectCustomField.getId(), item.getValue());
            assertEquals(selectCustomField.getLabel(), item.getLabel());
        }
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(newLabel, page.getCondition().getLabel());

    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // テストに必要なデータを作成
        SearchCustomFieldResult result = createCustomFieldResult();
        result.setCount(30);
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCustomFieldList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CustomField> newList = getNewList();
        result = new SearchCustomFieldResult();
        result.setCustomFieldList(newList);
        result.setCount(32); // 増加
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCustomFieldList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(1, page.getCondition().getPageNo());
    }

    /**
     * 次ページ（>>）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMoveNext() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCustomFieldResult result = createCustomFieldResult();
        result.setCount(30);
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCustomFieldList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<CustomField> newList = getNewList();
        result = new SearchCustomFieldResult();
        result.setCustomFieldList(newList);
        result.setCount(32); // 増加
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCustomFieldList().toString());
        assertEquals(32, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(3, page.getCondition().getPageNo());
    }

    /**
     * ページ遷移アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testChangePage() throws Exception {
        // ダミーのコレポン文書を設定
        SearchCustomFieldResult result = createCustomFieldResult();
        result.setCustomFieldList(result.getCustomFieldList());
        result.setCount(50);
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCustomFieldCondition condition = new SearchCustomFieldCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getCustomFieldList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<CustomField> newList = getNewList();
        result = new SearchCustomFieldResult();
        result.setCustomFieldList(newList);
        result.setCount(54); // 増加
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCustomFieldList().toString());
        assertEquals(54, page.getDataCount()); // 増加
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());
    }

    /**
     * 画面表示件数のテスト.
     * @throws Exception
     */
    @Test
    public void testPageDisplayNo() throws Exception {

        /* 全23件を10件ずつ表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(23);

        page.setPageNo(1);
        assertEquals("1-10", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("11-20", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("21-23", page.getPageDisplayNo());

        /* 全22件を7件ずつ表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(22);

        page.setPageNo(1);
        assertEquals("1-7", page.getPageDisplayNo());
        page.setPageNo(2);
        assertEquals("8-14", page.getPageDisplayNo());
        page.setPageNo(3);
        assertEquals("15-21", page.getPageDisplayNo());
        page.setPageNo(4);
        assertEquals("22-22", page.getPageDisplayNo());

        /* 全16件を20件ずつ表示の時 */
        page.setPageRowNum(20);
        page.setDataCount(16);

        page.setPageNo(1);
        assertEquals("1-16", page.getPageDisplayNo());
    }

    /**
     * ページリンク表示のテスト.
     * @throws Exception
     */
    @Test
    public void testPagingNo() throws Exception {
        String[] result = null;

        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        // 5ページまでは表示が変わらない
        String[] expected = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        for (int i = 0; i < 5; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expected[j], result[j]);
            }
        }

        // 6ページからは1ページずつ表示がずれる
        String[] expected6 = { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11" };
        page.setPageNo(6);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected6[i], result[i]);
        }
        String[] expected7 = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12" };
        page.setPageNo(7);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected7[i], result[i]);
        }
        String[] expected8 = { "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" };
        page.setPageNo(8);
        result = page.getPagingNo();

        assertEquals(10, result.length);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected8[i], result[i]);
        }

        // 9ページからは表示が変わらない
        String[] expectedEnd = { "5", "6", "7", "8", "9", "10", "11", "12", "13", "14" };
        for (int i = 8; i < 14; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(10, result.length);
            for (int j = 0; j < 10; j++) {
                assertEquals(expectedEnd[j], result[j]);
            }
        }

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        // 2ページまでは表示が変わらない
        String[] expectedHalf = { "1", "2", "3", "4", "5" };
        for (int i = 0; i < 3; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalf[j], result[j]);
            }
        }

        // 3ページからは1ページずつ表示がずれる
        String[] expectedHalf4 = { "2", "3", "4", "5", "6" };
        page.setPageNo(4);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf4[i], result[i]);
        }
        String[] expectedHalf5 = { "3", "4", "5", "6", "7" };
        page.setPageNo(5);
        result = page.getPagingNo();

        assertEquals(5, result.length);
        for (int i = 0; i < 5; i++) {
            assertEquals(expectedHalf5[i], result[i]);
        }
        // 5ページからは表示が変わらない
        String[] expectedHalfEnd = { "4", "5", "6", "7", "8" };
        for (int i = 5; i < 8; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(5, result.length);
            for (int j = 0; j < 5; j++) {
                assertEquals(expectedHalfEnd[j], result[j]);
            }
        }

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        // 10ページまでは表示が変わらない
        String[] expectedDouble =
                { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
                    "16", "17", "18", "19", "20" };
        for (int i = 0; i < 10; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDouble[j], result[j]);
            }
        }

        // 11ページで1ページ表示がずれる
        String[] expectedDouble11 =
                { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                    "17", "18", "19", "20", "21" };
        page.setPageNo(11);
        result = page.getPagingNo();

        assertEquals(20, result.length);
        for (int i = 0; i < 20; i++) {
            assertEquals(expectedDouble11[i], result[i]);
        }

        // 12ページからは表示が変わらない
        String[] expectedDoubleEnd =
                { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16",
                    "17", "18", "19", "20", "21", "22" };
        for (int i = 11; i < 22; i++) {
            page.setPageNo(i + 1);
            result = page.getPagingNo();

            assertEquals(20, result.length);
            for (int j = 0; j < 20; j++) {
                assertEquals(expectedDoubleEnd[j], result[j]);
            }
        }

        /* 全1件を1件ごと、1ページ分表示の時 */
        page.setPageRowNum(1);
        page.setDataCount(1);
        page.setPageIndex(1);
        String[] expectedMin = { "1" };

        page.setPageNo(1);
        result = page.getPagingNo();

        assertEquals(1, result.length);
        for (int i = 0; i < 1; i++) {
            assertEquals(expectedMin[i], result[i]);
        }
    }

    /**
     * 前ページ（<<）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testPrevious() throws Exception {
        // 1ページ目のみリンク表示しない
        page.setPageNo(1);
        assertFalse(page.getPrevious());

        for (int i = 2; i < 10; i++) {
            page.setPageNo(i);
            assertTrue(page.getPrevious());
        }
    }

    /**
     * 次ページ（>>）のリンク表示判定テスト.
     * @throws Exception
     */
    @Test
    public void testNext() throws Exception {
        /* 全136件を10件ごと、10ページ分表示の時 */
        page.setPageRowNum(10);
        page.setDataCount(136);
        page.setPageIndex(10);

        for (int i = 1; i < 14; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(14);
        assertFalse(page.getNext());

        /* 全56件を7件ごと、5ページ分表示の時 */
        page.setPageRowNum(7);
        page.setDataCount(56);
        page.setPageIndex(5);

        for (int i = 1; i < 8; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(8);
        assertFalse(page.getNext());

        /* 全317件を15件ごと、20ページ分表示の時 */
        page.setPageRowNum(15);
        page.setDataCount(317);
        page.setPageIndex(20);

        for (int i = 1; i < 22; i++) {
            page.setPageNo(i);
            assertTrue(page.getNext());
        }
        // 最終ページのみリンク表示しない
        page.setPageNo(22);
        assertFalse(page.getNext());
    }

    /**
     * Excelダウンロードアクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDownloadExcel() throws Exception {
        // 変換後のデータを作成
        byte[] expected = "TEST-OUT-EXCEL".getBytes();
        MockCustomFieldService.RET_GENERAL_EXCEL = expected;

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        SearchCustomFieldResult result = createCustomFieldResult();

        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockCustomFieldService.RET_GENERAL_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * カスタムフィールド追加アクションのテスト
     * @throws Exception
     */
    @Test
    public void testAssignTo() throws Exception {
        MockAbstractPage.RET_PROJID = "PJ1";
        page.setProjectId("PJ1");

        List<CustomField> notProjectList = getNotProjectList();
        page.setAssignableCustomFieldList(notProjectList);

        page.setSelectCustomField(11L);
        MockCustomFieldService.RET_ASSIGN_TO = 11L;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(Messages.getMessageAsString(CUSTOM_FIELD_ASSIGNED),
                        null));

        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = new ArrayList<CustomField>();
        MockCustomFieldService.RET_SEARCH_PAGING_LIST = createCustomFieldResult();

        page.setPageRowNum(10);
        page.setPageIndex(10);
        page.setPageNo(2);

        // テスト実行
        page.assignTo();

        CustomField actual = MockCustomFieldService.CRT_ASSIGN_TO;

        assertEquals(String.valueOf(11L), String.valueOf(actual.getId()));
        assertEquals(String.valueOf(11L), String.valueOf(actual.getNo()));
        assertEquals(String.valueOf(11L), String.valueOf(actual.getProjectCustomFieldId()));
        assertEquals("PJ1", actual.getProjectId());
        assertEquals("TEST11", actual.getLabel());
        assertEquals(String.valueOf(1L), String.valueOf(actual.getOrderNo()));
        assertEquals(UseWhole.ALL, actual.getUseWhole());

        assertEquals(2, page.getPageNo()); // 同一ページでリロード
        assertEquals(10, page.getPageRowNum());
        assertEquals(10, page.getPageRowNum());

    }

    /**
     * カスタムフィールド削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        SearchCustomFieldResult result = createCustomFieldResult();
        List<CustomField> lstCustomField = result.getCustomFieldList();
        page.setCustomFieldList(lstCustomField);
        page.getDataModel().setRowIndex(2); // 3行目を選択

        // 比較用
        CustomField deleteCustomField = lstCustomField.get(2); // 3行目を選択
        page.setCustomFieldId(deleteCustomField.getId());
        page.setCustomFieldProjId(deleteCustomField.getProjectCustomFieldId());
        page.setCustomFieldVerNo(deleteCustomField.getVersionNo());

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CUSTOM_FIELD_DELETED),
                        null));

        MockCustomFieldService.RET_SEARCH_PAGING_LIST = result;

        MockCustomFieldService.RET_SEARCH_NOT_ASSIGNED = createNewList();

        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        page.setCondition(new SearchCustomFieldCondition());

        page.setPageNo(2);

        page.delete();

        CustomField actual = MockCustomFieldService.CRT_DELETE;

        assertEquals(deleteCustomField.getId(), actual.getId());
        assertEquals(deleteCustomField.getVersionNo(), actual.getVersionNo());

        assertEquals(2, page.getPageNo());

    }

    public static class MockCustomFieldService extends MockUp<CustomFieldServiceImpl> {
        static byte[] RET_GENERAL_EXCEL;
        static CustomField CRT_DELETE;
        static Long RET_ASSIGN_TO;
        static CustomField CRT_ASSIGN_TO;
        static CustomField RET_FIND;
        static Long RET_SAVE;
        static CustomField CRT_SAVE;;
        static CustomField CRT_VALIDATE;
        static List<CustomField> RET_SEARCH_NOT_ASSIGNED;
        static SearchCustomFieldResult RET_SEARCH_PAGING_LIST;
        static boolean RET_VALIDATE;
        static ServiceAbortException EX_SEARCH;
        static int SET_SEARCH_ERROR_PAGE;

        @Mock
        public byte[] generateExcel(List<CustomField> customFields) {
            return RET_GENERAL_EXCEL;
        }

        @Mock
        public void delete(CustomField customField) throws ServiceAbortException {
            CRT_DELETE = customField;
        }

        @Mock
        public Long assignTo(CustomField customField) throws ServiceAbortException {
            CRT_ASSIGN_TO = customField;
            return RET_ASSIGN_TO;
        }

        @Mock
        public CustomField find(Long id) throws ServiceAbortException {
            return RET_FIND;
        }

        @Mock
        public boolean validate(CustomField customField) throws ServiceAbortException {
            CRT_VALIDATE = customField;
            return RET_VALIDATE;
        }

        @Mock
        public Long save(CustomField customField) throws ServiceAbortException {
            CRT_SAVE = customField;
            return RET_SAVE;
        }

        @Mock
        public List<CustomField> searchNotAssigned() throws ServiceAbortException {
            return RET_SEARCH_NOT_ASSIGNED;
        }

        @Mock
        public SearchCustomFieldResult searchPagingList(SearchCustomFieldCondition condition)
            throws ServiceAbortException {

            if (condition.getPageNo() == SET_SEARCH_ERROR_PAGE) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }

            if (EX_SEARCH != null) {
                throw EX_SEARCH;
            }

            return RET_SEARCH_PAGING_LIST;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return IS_SYSTEM_ADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String projectId) {
            return IS_PROJECT_ADMIN;
        }
    }

    public static class MockViewHelper extends MockUp<ViewHelper> {
        static String RET_ACTION;
        static byte[] RET_DATA;
        static String RET_FILENAME;

        @Mock
        public void download(String fileName, byte[] content) {
            RET_ACTION = "download";
            RET_DATA = content;
            RET_FILENAME = fileName;
        }

        @Mock
        public void requestResponse(byte[] content, String charset) {
            RET_ACTION = "requestResponse";
            RET_DATA = content;
        }

        @Mock
        public String getCookieValue(String key) {
            if (Integer.parseInt(key) % 2 == 0) {
                return "true";
            }
            return "false";
        }
    }

    public static class MockDateUtil extends MockUp<DateUtil> {
        static Date RET_DATE;

        @Mock
        public Date getNow() {
            return RET_DATE;
        }

    }

    private List<CustomField> getList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> customFields = new ArrayList<CustomField>();
        CustomField cf = new CustomField();
        cf.setId(1L);
        cf.setNo(1L);
        cf.setProjectCustomFieldId(1L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(2L);
        cf.setNo(2L);
        cf.setProjectCustomFieldId(2L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST2");
        cf.setOrderNo(2L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(3L);
        cf.setNo(3L);
        cf.setProjectCustomFieldId(3L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST3");
        cf.setOrderNo(3L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        return customFields;
    }

    private List<CustomField> getNewList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> customFields = new ArrayList<CustomField>();
        CustomField cf = new CustomField();
        cf.setId(41L);
        cf.setNo(41L);
        cf.setProjectCustomFieldId(41L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST41");
        cf.setOrderNo(41L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(42L);
        cf.setNo(42L);
        cf.setProjectCustomFieldId(42L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST42");
        cf.setOrderNo(42L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(43L);
        cf.setNo(43L);
        cf.setProjectCustomFieldId(43L);
        cf.setProjectId("PJ1");
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST43");
        cf.setOrderNo(43L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        return customFields;
    }

    private List<CustomField> getNotProjectList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> customFields = new ArrayList<CustomField>();
        CustomField cf = new CustomField();
        cf.setId(11L);
        cf.setNo(11L);
        cf.setProjectCustomFieldId(11L);
        cf.setProjectId(null);
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST11");
        cf.setOrderNo(1L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(12L);
        cf.setNo(12L);
        cf.setProjectCustomFieldId(12L);
        cf.setProjectId(null);
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST12");
        cf.setOrderNo(2L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        cf = new CustomField();

        cf.setId(13L);
        cf.setNo(13L);
        cf.setProjectCustomFieldId(13L);
        cf.setProjectId(null);
        cf.setProjectNameE("Test Project1");
        cf.setLabel("TEST13");
        cf.setOrderNo(3L);
        cf.setUseWhole(UseWhole.ALL);

        cf.setCreatedBy(user);
        cf.setUpdatedBy(user);
        cf.setVersionNo(1L);
        cf.setDeleteNo(0L);

        customFields.add(cf);

        return customFields;
    }

    public SearchCustomFieldResult createCustomFieldResult() {
        List<CustomField> customFields = getList();

        SearchCustomFieldResult result = new SearchCustomFieldResult();
        result.setCustomFieldList(customFields);
        result.setCount(customFields.size());
        return result;
    }

    private List<CustomField> createNewList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> list = new ArrayList<CustomField>();

        CustomField customField = new CustomField();

        customField.setId(21L);
        customField.setNo(21L);
        customField.setProjectCustomFieldId(21L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST21");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);

        customField = new CustomField();
        customField.setId(22L);
        customField.setNo(22L);
        customField.setProjectCustomFieldId(22L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST22");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);

        customField = new CustomField();
        customField.setId(23L);
        customField.setNo(23L);
        customField.setProjectCustomFieldId(23L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST23");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);

        customField = new CustomField();
        customField.setId(24L);
        customField.setNo(24L);
        customField.setProjectCustomFieldId(24L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST24");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);

        customField = new CustomField();
        customField.setId(25L);
        customField.setNo(25L);
        customField.setProjectCustomFieldId(25L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST25");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);

        return list;
    }

    private List<CustomField> createNewSelectList() {
        User user = new User();
        user.setEmpNo("ZZA04");
        user.setNameE("Tomoko Okada");

        List<CustomField> list = new ArrayList<CustomField>();

        CustomField customField = new CustomField();

        customField.setId(31L);
        customField.setNo(31L);
        customField.setProjectCustomFieldId(31L);
        customField.setProjectId(null);
        customField.setProjectNameE("Test Project1");
        customField.setLabel("TEST31");
        customField.setOrderNo(1L);
        customField.setUseWhole(UseWhole.ALL);

        customField.setCreatedBy(user);
        customField.setUpdatedBy(user);
        customField.setVersionNo(1L);
        customField.setDeleteNo(0L);

        list.add(customField);
        return list;
    }

}
