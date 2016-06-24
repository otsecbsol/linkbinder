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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.exception.InvalidOperationRuntimeException;
import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.SearchSiteResult;
import jp.co.opentone.bsol.linkbinder.dto.Site;
import jp.co.opentone.bsol.linkbinder.dto.User;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchSiteCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.SiteServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link SiteIndexPage}のテストケース.
 * @author opentone
 */
public class SiteIndexPageTest extends AbstractTestCase {

    /**
     * テスト対象クラス
     */
    @Resource
    private SiteIndexPage page;

    /**
     * 検証用データ
     */
    private List<Site> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockSiteService();
        new MockViewHelper();
        new MockDateUtil();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockSiteService().tearDown();
        new MockViewHelper().tearDown();
        new MockDateUtil().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        list = getList();
    }

    @After
    public void tearDown() {
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;
        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockSiteService.RET_SEARCH = null;
        MockSiteService.CRT_DELETE = null;
        MockSiteService.EX_SEARCH = null;
        MockSiteService.SET_SEARCH_ERROR_PAGE = -1;
        page.setCode(null);
        page.setName(null);
        page.setDataModel(null);
        if (page.getSiteList() != null) {
            page.getSiteList().clear();
        }
    }

    /**
     * 初期化アクションを検証する.
     * SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_SYSTEM_ADMIN = true;

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertTrue(page.isPermitMasterEdit());

        // 検索条件
        assertNull(page.getCode());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("PJ1", page.getCondition().getProjectId());
        assertTrue(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertFalse(page.getCondition().isGroupAdmin());

    }

    /**
     * 初期化アクションを検証する.
     * ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_PROJECT_ADMIN = true;

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertTrue(page.isPermitMasterEdit());

        // 検索条件
        assertNull(page.getCode());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("PJ1", page.getCondition().getProjectId());
        assertFalse(page.getCondition().isSystemAdmin());
        assertTrue(page.getCondition().isProjectAdmin());
        assertFalse(page.getCondition().isGroupAdmin());

    }

    /**
     * 初期化アクションを検証する.
     * GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testInitializeGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(3, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertFalse(page.isPermitMasterEdit());

        // 検索条件
        assertNull(page.getCode());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals("PJ1", page.getCondition().getProjectId());
        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());

    }

    /**
     * 初期化アクションのテスト.
     * プロジェクトが選択されていない場合.
     * @throws Exception
     */
    @Test
    public void testInitializeProjectNotSelected() throws Exception{
        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = null;

        MockAbstractPage.IS_ANY_GROUP_ADMIN = false;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, FacesMessage.SEVERITY_ERROR.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT),
                        "Initialize"));

        // テスト実行
        try {
            page.initialize();
            fail("例外が発生していない");
        } catch (InvalidOperationRuntimeException e) {
            ServiceAbortException actual = (ServiceAbortException) e.getCause();
            assertEquals(CANNOT_PERFORM_BECAUSE_ACCESS_LEVEL_INSUFFICIENT,
                        actual.getMessageCode());
        }
    }

    /**
     * 検索アクションを検証する.
     * SystemAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchSystemAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();
        result.setCount(result.getSiteList().size());

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockAbstractPage.IS_SYSTEM_ADMIN = true;


        page.setCondition(new SearchSiteCondition());
        page.setSiteList(getList());

        page.setCode("Y");

        page.search();

        assertEquals(result.getSiteList().toString(), page.getSiteList().toString());
        assertEquals(result.getCount(), page.getDataCount());
        // 検索条件
        assertEquals("Y", page.getCondition().getSiteCd());
        assertNull(page.getCondition().getName());
        assertTrue(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertFalse(page.getCondition().isGroupAdmin());
    }

    /**
     * 検索アクションを検証する.
     * ProjectAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchProjectAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();
        result.setCount(result.getSiteList().size());

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockAbstractPage.IS_PROJECT_ADMIN = true;


        page.setCondition(new SearchSiteCondition());
        page.setSiteList(getList());

        page.setCode("Y");

        page.search();

        assertEquals(result.getSiteList().toString(), page.getSiteList().toString());
        assertEquals(result.getCount(), page.getDataCount());
        // 検索条件
        assertEquals("Y", page.getCondition().getSiteCd());
        assertNull(page.getCondition().getName());
        assertFalse(page.getCondition().isSystemAdmin());
        assertTrue(page.getCondition().isProjectAdmin());
        assertFalse(page.getCondition().isGroupAdmin());
    }

    /**
     * 検索アクションを検証する.
     * GroupAdminの場合
     * @throws Exception
     */
    @Test
    public void testSearchGroupAdmin() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();
        result.setCount(result.getSiteList().size());

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";
        MockAbstractPage.IS_ANY_GROUP_ADMIN = true;
        MockAbstractPage.IS_SYSTEM_ADMIN = false;
        MockAbstractPage.IS_PROJECT_ADMIN = false;

        page.setCondition(new SearchSiteCondition());
        page.setSiteList(getList());

        page.setCode("Y");

        page.search();

        assertEquals(result.getSiteList().toString(), page.getSiteList().toString());
        assertEquals(result.getCount(), page.getDataCount());
        // 検索条件
        assertEquals("Y", page.getCondition().getSiteCd());
        assertNull(page.getCondition().getName());
        assertFalse(page.getCondition().isSystemAdmin());
        assertFalse(page.getCondition().isProjectAdmin());
        assertTrue(page.getCondition().isGroupAdmin());
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // テストに必要なデータを作成
        SearchSiteResult result = createSiteResult();
        result.setCount(30);
        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Site> newList = getNewList();
        result = new SearchSiteResult();
        result.setSiteList(newList);
        result.setCount(32); // 増加
        MockSiteService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getSiteList().toString());
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
        // ダミー部門情報を設定
        SearchSiteResult result = createSiteResult();
        result.setCount(30);
        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Site> newList = getNewList();
        result = new SearchSiteResult();
        result.setSiteList(newList);
        result.setCount(32); // 増加
        MockSiteService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getSiteList().toString());
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
        // 部門情報を設定
        SearchSiteResult result = createSiteResult();
        result.setSiteList(result.getSiteList());
        result.setCount(50);
        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchSiteCondition condition = new SearchSiteCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getSiteList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<Site> newList = getNewList();
        result = new SearchSiteResult();
        result.setSiteList(newList);
        result.setCount(54); // 増加
        MockSiteService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getSiteList().toString());
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
        MockSiteService.RET_EXCEL = expected;
        MockAbstractPage.RET_PROJID = "PJ1";

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockSiteService.RET_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * 拠点情報削除アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testDelete() throws Exception {
        // テストに必要なデータを作成
        Site site = new Site();
        site.setId(2L);
        site.setProjectId("PJ1");
        site.setProjectNameE("Test Project1");
        site.setSiteCd("SJK");
        site.setName("Shinjuku");

        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");
        site.setCreatedBy(loginUser);
        site.setUpdatedBy(loginUser);
        site.setVersionNo(1L);
        site.setDeleteNo(0L);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_CLIENT_ID = null;
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(SITE_DELETED),
                        null));


        SearchSiteResult result = createSiteResult();

        MockSiteService.RET_SEARCH = result;
        MockAbstractPage.RET_PROJID = "PJ1";

        MockAbstractPage.IS_SYSTEM_ADMIN = true;
        page.setPermitMasterEdit(true);

        page.setSiteList(result.getSiteList());
        page.getDataModel().setRowIndex(1);

        page.setSiteId(site.getId());
        page.setSiteProjId(site.getProjectId());
        page.setSiteVerNo(site.getVersionNo());

        page.setPageNo(2);

        page.delete();

        Site actual = MockSiteService.CRT_DELETE;

        assertEquals(site.getId(), actual.getId());
        assertEquals(site.getVersionNo(), actual.getVersionNo());

        assertEquals(2, page.getPageNo());
    }


    public static class MockDateUtil extends MockUp<DateUtil> {
        static Date RET_DATE;

        @Mock
        public Date getNow() {
            return RET_DATE;
        }

    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean IS_SYSTEM_ADMIN;
        static boolean IS_PROJECT_ADMIN;
        static boolean IS_ANY_GROUP_ADMIN;

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

        @Mock
        public boolean isAnyGroupAdmin(String projectId) {
            return IS_ANY_GROUP_ADMIN;
        }
    }

    public static class MockSiteService extends MockUp<SiteServiceImpl> {
        static SearchSiteResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Site CRT_DELETE;
        static ServiceAbortException EX_SEARCH;
        static int SET_SEARCH_ERROR_PAGE;

        @Mock
        public SearchSiteResult search(SearchSiteCondition condition) throws ServiceAbortException {
            if (condition.getPageNo() == SET_SEARCH_ERROR_PAGE) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }
            if (EX_SEARCH != null) {
                throw EX_SEARCH;
            }
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<Site> Sites) throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public void delete(Site site) throws ServiceAbortException {
            CRT_DELETE = site;
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

    /**
     * 部門情報リストを作成する.
     * @return SearchSiteResult 部門情報
     */
    private SearchSiteResult createSiteResult() {
        List<Site> lstSite = getList();

        SearchSiteResult result = new SearchSiteResult();
        result.setSiteList(lstSite);
        result.setCount(lstSite.size());

        return result;

    }

    private List<Site> getList() {
        List<Site> lstSite = new ArrayList<Site>();
        User loginUser = new User();
        loginUser.setEmpNo("ZZA01");
        loginUser.setNameE("Taro Yamada");

        Site dis = new Site();
        dis.setId(1L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setSiteCd("YOC");
        dis.setName("Yocohama");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstSite.add(dis);

        dis = new Site();
        dis.setId(2L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setSiteCd("SJK");
        dis.setName("Shinjuku");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstSite.add(dis);

        dis = new Site();
        dis.setId(3L);
        dis.setProjectId("PJ1");
        dis.setProjectNameE("Test Project1");
        dis.setSiteCd("KJJ");
        dis.setName("KIchijozi");

        dis.setCreatedBy(loginUser);
        dis.setUpdatedBy(loginUser);
        dis.setVersionNo(1L);
        dis.setDeleteNo(0L);
        lstSite.add(dis);

        return lstSite;
    }

    private List<Site> getNewList() {
        // ダミーの戻り値をセット
        List<Site> SiteList = new ArrayList<Site>();
        Site site = new Site();

        site.setSiteCd("KKK");
        site.setId(11L);
        site.setName("KKK");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("LLL");
        site.setId(12L);
        site.setName("LLL");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("MMM");
        site.setId(13L);
        site.setName("MMM");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("OOO");
        site.setId(14L);
        site.setName("OOO");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("PPP");
        site.setId(14L);
        site.setName("PPP Corporation");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("QQQ");
        site.setId(15L);
        site.setName("QQQ");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("RRR");
        site.setId(16L);
        site.setName("FFF");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("SSS");
        site.setId(17L);
        site.setName("GGG");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("TTT");
        site.setId(18L);
        site.setName("HHH");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("UUU");
        site.setId(19L);
        site.setName("III");
        site.setVersionNo(1L);

        SiteList.add(site);

        site = new Site();
        site.setSiteCd("VVV");
        site.setId(20L);
        site.setName("VVV");
        site.setVersionNo(1L);

        SiteList.add(site);

        SearchSiteResult result = new SearchSiteResult();
        result.setSiteList(SiteList);
        result.setCount(SiteList.size());

        return SiteList;
    }
}
