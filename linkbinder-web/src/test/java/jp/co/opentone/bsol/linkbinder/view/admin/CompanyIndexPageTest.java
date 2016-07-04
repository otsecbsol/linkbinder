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
import javax.faces.model.ListDataModel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jp.co.opentone.bsol.framework.core.message.Messages;
import jp.co.opentone.bsol.framework.core.service.ServiceAbortException;
import jp.co.opentone.bsol.framework.core.util.DateUtil;
import jp.co.opentone.bsol.framework.extension.jsf.FacesContextMock;
import jp.co.opentone.bsol.framework.test.AbstractDaoTestCase;
import jp.co.opentone.bsol.framework.web.view.util.ViewHelper;
import jp.co.opentone.bsol.linkbinder.dto.Company;
import jp.co.opentone.bsol.linkbinder.dto.SearchCompanyResult;
import jp.co.opentone.bsol.linkbinder.dto.condition.SearchCompanyCondition;
import jp.co.opentone.bsol.linkbinder.message.ApplicationMessageCode;
import jp.co.opentone.bsol.linkbinder.service.admin.impl.CompanyServiceImpl;
import jp.co.opentone.bsol.linkbinder.view.AbstractPage;
import mockit.Mock;
import mockit.MockUp;

/**
 * {@link CompanyIndexPage}のテストケース.
 * @author opentone
 */

public class CompanyIndexPageTest extends AbstractDaoTestCase {

    /**
     * テスト対象.
     */
    @Resource
    private CompanyIndexPage page;

    /**
     * 検証用データ
     */
    private List<Company> list;

    /**
     * テストの前準備.
     */
    @BeforeClass
    public static void testSetUp() {
        FacesContextMock.initialize();
        new MockAbstractPage();
        new MockCompanyService();
        new MockDateUtil();
        new MockViewHelper();
    }

    /**
     * 後始末.
     */
    @AfterClass
    public static void testTeardown() {
        new MockAbstractPage().tearDown();
        new MockCompanyService().tearDown();
        new MockDateUtil().tearDown();
        new MockViewHelper().tearDown();
        FacesContextMock.tearDown();
    }

    @Before
    public void setUp() {
        FacesContextMock.EXPECTED_MESSAGE = null;
        list = getList();
    }

    @After
    public void tearDown() {
        list = null;
        MockCompanyService.RET_SEARCH = null;
        MockCompanyService.RET_EXCEL = null;
        MockCompanyService.RET_DELETE = null;
        MockCompanyService.SET_ERROR_PAGE = -1;
        MockCompanyService.SET_NO_DATA_FOUND = false;
        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = null;
        MockDateUtil.RET_DATE = null;
        MockCompanyService.RET_COMPANY = null;
        MockAbstractPage.RET_PROJID = null;
        MockAbstractPage.RET_IS_SYSTEMADMIN = false;
        MockAbstractPage.RET_IS_PROJECTADMIN = false;
        MockAbstractPage.RET_IS_GROUPADMIN = false;

        FacesContextMock.EXPECTED_MESSAGE = null;
    }

    /**
     * 初期化アクションを検証する. マスタ管理の場合
     * @throws Exception
     */
    @Test
    public void testInitializeMaster() throws Exception {
        // テストに必要なデータを作成する
        SearchCompanyResult result = createCompanyResult();

        MockCompanyService.RET_SEARCH = result;

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getCompanyList().toString());
        assertEquals(10, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());

        // 検索条件
        assertNull(page.getCondition().getProjectId());
        assertNull(page.getCompanyCd());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());

    }

    /**
     * 初期化アクションを検証する. プロジェクトマスタ管理の場合
     * @throws Exception
     */
    @Test
    public void testInitializeProject() throws Exception {
        // テストに必要なデータを作成する
        SearchCompanyResult result = createCompanyResult();

        Company com = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);

        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = companyList;

        MockCompanyService.RET_SEARCH = result;

        MockAbstractPage.RET_PROJID = "Mock";

        // テスト実行
        page.initialize();

        assertEquals(list.toString(), page.getCompanyList().toString());
        assertEquals(10, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertFalse(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(1, page.getPageNo());
        assertEquals(10, page.getPageRowNum());
        assertEquals("Mock", page.getProjectId());

        // 検索条件
        assertEquals("Mock", page.getProjectId());
        assertNull(page.getCompanyCd());
        assertNull(page.getName());
        assertEquals(1, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals(4, page.getSelectCompanyList().size());
    }

    /**
     * 前ページ（<<）アクションのテスト.
     * @throws Exception
     */
    @Test
    public void testMovePrevious() throws Exception {
        // テストに必要なデータを作成
        SearchCompanyResult result = createCompanyResult();
        result.setCount(30);
        MockCompanyService.RET_SEARCH = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 3ページ目から1つ戻る
        page.setPageNo(3);
        page.movePrevious();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCompanyList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Company> newList = getNewList();
        result = new SearchCompanyResult();
        result.setCompanyList(newList);
        result.setCount(32); // 増加
        MockCompanyService.RET_SEARCH = result;

        // さらに1つ戻る
        page.movePrevious();

        assertEquals(1, page.getPageNo());
        assertEquals(newList.toString(), page.getCompanyList().toString());
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
        SearchCompanyResult result = createCompanyResult();
        result.setCount(30);
        MockCompanyService.RET_SEARCH = result;

        /* 全30件を10行ずつ表示 -- 全3ページ */
        page.setPageRowNum(10);
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 1ページ目から1つ進む
        page.setPageNo(1);
        page.moveNext();

        assertEquals(2, page.getPageNo());
        assertEquals(list.toString(), page.getCompanyList().toString());
        assertEquals(30, page.getDataCount());
        // 検索条件
        assertEquals(2, page.getCondition().getPageNo());

        // 再設定
        List<Company> newList = getNewList();
        result = new SearchCompanyResult();
        result.setCompanyList(newList);
        result.setCount(32); // 増加
        MockCompanyService.RET_SEARCH = result;

        // さらに1つ進む
        page.moveNext();

        assertEquals(3, page.getPageNo());
        assertEquals(newList.toString(), page.getCompanyList().toString());
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
        SearchCompanyResult result = createCompanyResult();
        result.setCompanyList(result.getCompanyList());
        result.setCount(50);
        MockCompanyService.RET_SEARCH = result;

        /* 全50件を10行ずつ表示 -- 全5ページ */
        page.setPageRowNum(10);
        page.setPageIndex(10);
        SearchCompanyCondition condition = new SearchCompanyCondition();
        condition.setPageRowNum(10);
        page.setCondition(condition);

        // 4ページ目へ
        page.setPageNo(4);
        page.changePage();

        assertEquals(4, page.getPageNo());
        assertEquals(list.toString(), page.getCompanyList().toString());
        assertEquals(50, page.getDataCount());
        // 検索条件
        assertEquals(4, page.getCondition().getPageNo());

        // 再設定
        List<Company> newList = getNewList();
        result = new SearchCompanyResult();
        result.setCompanyList(newList);
        result.setCount(54); // 増加
        MockCompanyService.RET_SEARCH = result;

        // 2ページ目へ
        page.setPageNo(2);
        page.changePage();

        assertEquals(2, page.getPageNo());
        assertEquals(newList.toString(), page.getCompanyList().toString());
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
        MockCompanyService.RET_EXCEL = expected;

        Date date = new GregorianCalendar(2009, 3, 1, 1, 1, 1).getTime();
        MockDateUtil.RET_DATE = date;

        SearchCompanyResult result = createCompanyResult();

        MockCompanyService.RET_SEARCH = result;

        page.downloadExcel();

        assertArrayEquals(expected, MockViewHelper.RET_DATA);
        assertEquals("20090401010101.xls", MockViewHelper.RET_FILENAME);

        // 2回目の作成
        byte[] newExpected = "NEW-TEST-OUT-EXCEL".getBytes();
        MockCompanyService.RET_EXCEL = newExpected;

        Date newDate = new GregorianCalendar(2009, 3, 1, 11, 11, 11).getTime();
        MockDateUtil.RET_DATE = newDate;

        page.downloadExcel();

        assertArrayEquals(newExpected, MockViewHelper.RET_DATA);
        assertEquals("20090401111111.xls", MockViewHelper.RET_FILENAME);
    }

    /**
     * 指定した会社をプロジェクトに登録する処理を検証.
     * @throws Exception
     */
    @Test
    public void testAssignToProject() throws Exception {
        // テストに必要なデータを作成する
        Company com = new Company();
        Company companyKeep = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyKeep.setCompanyCd(com.getCompanyCd());
        companyKeep.setId(com.getId());
        companyKeep.setName(com.getName());
        companyKeep.setVersionNo(com.getVersionNo());
        companyKeep.setProjectId("Mock");

        page.setPageIndex(10);
        page.setPageRowNum(10);
        page.setPageNo(2);

        page.setSelectCompany(com.getId());

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);

        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = companyList;
        page.setAssignableCompanyList(companyList);

        SearchCompanyResult result = createCompanyResult();
        page.setCompany(result);
        MockCompanyService.RET_SEARCH = result;

        MockAbstractPage.RET_PROJID = "Mock";
        page.setProjectId("Mock");

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_ASSIGNED),
                        null));

        // テスト実行
        page.setActionName("AssignTo");
        page.assignTo();

        assertEquals(companyKeep.toString(), MockCompanyService.RET_COMPANY.toString());
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(2, page.getPageNo()); // 同ページ内でリロード
        assertEquals(10, page.getPageRowNum());
        assertEquals("Mock", page.getProjectId());

        // 検索条件
        assertEquals("Mock", page.getProjectId());
        assertNull(page.getCompanyCd());
        assertNull(page.getName());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals(4, page.getSelectCompanyList().size());

    }


    /**
     * 指定した会社をプロジェクトに登録する処理を検証.
     * 削除後、レコード件数が0件.
     * @throws Exception
     */
    @Test
    public void testAssignToProjectNoDataFound() throws Exception {
        // テストに必要なデータを作成する
        Company com = new Company();
        Company companyKeep = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyKeep.setCompanyCd(com.getCompanyCd());
        companyKeep.setId(com.getId());
        companyKeep.setName(com.getName());
        companyKeep.setVersionNo(com.getVersionNo());
        companyKeep.setProjectId("Mock");

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);

        SearchCompanyResult result = createCompanyResult();
        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = companyList;
        page.setCompany(result);
        MockCompanyService.RET_SEARCH = result;

        MockAbstractPage.RET_PROJID = "Mock";

        MockCompanyService.SET_NO_DATA_FOUND = true;


        page.setPageIndex(10);
        page.setPageRowNum(10);
        page.setPageNo(2);
        page.setSelectCompany(1L);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_ASSIGNED),
                        null));

        // テスト実行
        page.setActionName("AssignTo");
        page.assignTo();

        assertEquals(companyKeep.getId(), MockCompanyService.RET_COMPANY.getId());

        assertEquals(0, page.getDataCount());
    }

    /**
     * 指定した会社をプロジェクトに登録する処理を検証.
     * 削除後、該当ページのレコードが0件.
     * @throws Exception
     */
    @Test
    public void testAssignToProjectNoPageFound() throws Exception {
        // テストに必要なデータを作成する
        Company com = new Company();
        Company companyKeep = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyKeep.setCompanyCd(com.getCompanyCd());
        companyKeep.setId(com.getId());
        companyKeep.setName(com.getName());
        companyKeep.setVersionNo(com.getVersionNo());
        companyKeep.setProjectId("Mock");

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);

        SearchCompanyResult result = createCompanyResult();
        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = companyList;
        page.setCompany(result);
        MockCompanyService.RET_SEARCH = result;

        MockAbstractPage.RET_PROJID = "Mock";

        page.setPageIndex(10);
        page.setPageRowNum(10);
        page.setPageNo(2);
        page.setSelectCompany(1L);

        MockCompanyService.SET_ERROR_PAGE = 3;
        page.setPageNo(3);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_ASSIGNED),
                        null));

        // テスト実行
        page.setActionName("AssignTo");
        page.assignTo();

        assertEquals(companyKeep.getId(), MockCompanyService.RET_COMPANY.getId());

        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(result.getCount(), page.getDataCount());
    }

    /**
     * 指定した会社を削除する処理を検証. （マスタ管理）
     * @throws Exception
     */
    @Test
    public void testDeleteMaster() throws Exception {
        // テストに必要なデータを作成する
        SearchCompanyResult result = createCompanyResult();

        MockCompanyService.RET_SEARCH = result;
        MockCompanyService.RET_COMPANY = result.getCompanyList().get(2);

        List<Company> companyList = new ArrayList<Company>(result.getCompanyList());

        ListDataModel companyModel = new ListDataModel();
        companyModel.setWrappedData(companyList);
        companyModel.setRowIndex(2); // 3行目を選択
        page.setCompanyModel(companyModel);

        page.setPageIndex(10);
        page.setPageRowNum(10);
        page.setPageNo(2);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_DELETED),
                        null));

        // テスト実行
        page.setActionName("Delete");
        page.delete();

        assertEquals(result.getCompanyList().get(2), MockCompanyService.RET_DELETE);

        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(2, page.getPageNo()); // 同ページでリロード
        assertEquals(10, page.getPageRowNum());

        // 検索条件
        assertNull(page.getCompanyCd());
        assertNull(page.getName());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
    }

    /**
     * 指定した会社を削除する処理を検証. （マスタ管理）
     * @throws Exception
     */
    @Test
    public void testDeleteProject() throws Exception {
        // テストに必要なデータを作成する
        SearchCompanyResult result = createCompanyResult();

        // プロジェクトマスタ管理の会社情報を追加するリストを作成する.
        Company com = new Company();
        List<Company> companyList = new ArrayList<Company>();

        com.setCompanyCd("ABC");
        com.setId(1L);
        com.setName("ABC Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DEF");
        com.setId(2L);
        com.setName("DEF Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GHI");
        com.setId(3L);
        com.setName("GHI Company");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JKL");
        com.setId(4L);
        com.setName("JKL Company");
        com.setVersionNo(1L);

        companyList.add(com);

        MockCompanyService.RET_SEARCH_NOT_PROJECT_COMPANY = companyList;

        MockCompanyService.RET_SEARCH = result;
        MockCompanyService.RET_COMPANY = result.getCompanyList().get(2);

        MockAbstractPage.RET_PROJID = "Mock";

        ListDataModel companyModel = new ListDataModel();
        companyModel.setWrappedData(result.getCompanyList());
        companyModel.setRowIndex(2); // 3行目を選択
        page.setCompanyModel(companyModel);

        page.setPageIndex(10);
        page.setPageRowNum(10);
        page.setPageNo(2);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_DELETED),
                        null));

        // テスト実行
        page.setActionName("Delete");
        page.delete();

        assertEquals(result.getCompanyList().get(2).toString(), MockCompanyService.RET_DELETE
            .toString());

        assertEquals(10, page.getDataCount());

        // 初期設定を確認
        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(10, page.getPageIndex());
        assertEquals(2, page.getPageNo()); // 同ページでリロード
        assertEquals(10, page.getPageRowNum());
        assertEquals("Mock", page.getProjectId());

        // 検索条件
        assertEquals("Mock", page.getProjectId());
        assertNull(page.getCompanyCd());
        assertNull(page.getName());
        assertEquals(2, page.getCondition().getPageNo());
        assertEquals(10, page.getCondition().getPageRowNum());
        assertEquals(4, page.getSelectCompanyList().size());
    }

    /**
     * 指定した会社を削除する処理を検証.
     * 削除後、該当ページのレコードが0件.
     * @throws Exception
     */
    @Test
    public void testDeleteNoPageFound() throws Exception {
        // テストに必要なデータを作成する
        SearchCompanyResult result = createCompanyResult();

        MockCompanyService.RET_SEARCH = result;
        MockCompanyService.RET_COMPANY = result.getCompanyList().get(2);

        MockCompanyService.SET_ERROR_PAGE = 3;
        page.setPageNo(3);

        List<Company> companyList = new ArrayList<Company>(result.getCompanyList());

        ListDataModel companyModel = new ListDataModel();
        companyModel.setWrappedData(companyList);
        companyModel.setRowIndex(2); // 3行目を選択
        page.setCompanyModel(companyModel);

        // 期待されるメッセージをセット
        FacesContextMock.EXPECTED_MESSAGE =
                new FacesMessage(
                    FacesMessage.SEVERITY_INFO, FacesMessage.SEVERITY_INFO.toString(),
                    createExpectedMessageString(
                        Messages.getMessageAsString(COMPANY_DELETED),
                        null));

        // テスト実行
        page.setActionName("Delete");
        page.delete();

        assertEquals(result.getCompanyList().get(2), MockCompanyService.RET_DELETE);

        assertFalse(page.getNext());
        assertTrue(page.getPrevious());
        assertEquals(result.getCount(), page.getDataCount());
    }

    /**
     * 更新権限有無を判定する処理を検証.
     * SystemAdminの場合.
     * @throws Exception
     */
    @Test
    public void testIsUpdatableCompanyAtSysAdmin() throws Exception{
        MockAbstractPage.RET_IS_SYSTEMADMIN = true;
        assertTrue(page.isUpdatableCompany());
    }

    /**
     * 更新権限有無を判定する処理を検証.
     * ProjectAdminの場合.
     * @throws Exception
     */
    @Test
    public void testIsUpdatableCompanyAtPrjAdmin() throws Exception{
        MockAbstractPage.RET_IS_PROJECTADMIN = true;
        assertTrue(page.isUpdatableCompany());
    }

    /**
     * 更新権限有無を判定する処理を検証.
     * GroupAdminの場合.
     * @throws Exception
     */
    @Test
    public void testIsUpdatableCompanyAtGroupAdmin() throws Exception{
        MockAbstractPage.RET_IS_GROUPADMIN = true;
        assertFalse(page.isUpdatableCompany());
    }

    /**
     * 更新権限有無を判定する処理を検証.
     * SystemAdmin,ProjectAdmin,GroupAdminでない場合.
     * @throws Exception
     */
    @Test
    public void testIsUpdatableCompanyAtNotAdmin() throws Exception{
        assertFalse(page.isUpdatableCompany());
    }

    /**
     * 会社情報リストを作成する.
     * @return SearchCompanyResult 会社情報
     */
    private SearchCompanyResult createCompanyResult() {
        List<Company> companyList = getList();

        SearchCompanyResult result = new SearchCompanyResult();
        result.setCompanyList(companyList);
        result.setCount(companyList.size() - 1);

        return result;
    }

    private List<Company> getList() {
        List<Company> companyList = new ArrayList<Company>();
        Company com = new Company();

        com.setCompanyCd("OT");
        com.setId(1L);
        com.setName("OT Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("AAA");
        com.setId(2L);
        com.setName("Capsule Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("BBB");
        com.setId(3L);
        com.setName("Kaisya Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("CCC");
        com.setId(4L);
        com.setName("CCC Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("DDD");
        com.setId(4L);
        com.setName("DDD Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("EEE");
        com.setId(5L);
        com.setName("EEE Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("FFF");
        com.setId(6L);
        com.setName("FFF Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("GGG");
        com.setId(7L);
        com.setName("GGG Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("HHH");
        com.setId(8L);
        com.setName("HHH Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("III");
        com.setId(9L);
        com.setName("III Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("JJJ");
        com.setId(10L);
        com.setName("JJJ Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        return companyList;
    }

    private List<Company> getNewList() {
        // ダミーの戻り値をセット
        List<Company> companyList = new ArrayList<Company>();
        Company com = new Company();

        com.setCompanyCd("KKK");
        com.setId(11L);
        com.setName("KKK Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("LLL");
        com.setId(12L);
        com.setName("LLL Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("MMM");
        com.setId(13L);
        com.setName("MMM Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("OOO");
        com.setId(14L);
        com.setName("OOO Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("PPP");
        com.setId(14L);
        com.setName("PPP Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("QQQ");
        com.setId(15L);
        com.setName("QQQ Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("RRR");
        com.setId(16L);
        com.setName("FFF Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("SSS");
        com.setId(17L);
        com.setName("GGG Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("TTT");
        com.setId(18L);
        com.setName("HHH Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("UUU");
        com.setId(19L);
        com.setName("III Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        com = new Company();
        com.setCompanyCd("VVV");
        com.setId(20L);
        com.setName("VVV Corporation");
        com.setRole("Owner");
        com.setVersionNo(1L);

        companyList.add(com);

        SearchCompanyResult result = new SearchCompanyResult();
        result.setCompanyList(companyList);
        result.setCount(companyList.size() - 1);

        return companyList;
    }

    public static class MockCompanyService extends MockUp<CompanyServiceImpl> {
        static SearchCompanyResult RET_SEARCH;
        static byte[] RET_EXCEL;
        static Company RET_COMPANY;
        static Company RET_DELETE;
        static boolean SET_NO_DATA_FOUND;
        static int SET_ERROR_PAGE;
        static List<Company> RET_SEARCH_NOT_PROJECT_COMPANY;

        @Mock
        public Company find(Long id) throws ServiceAbortException {
            if (SET_NO_DATA_FOUND) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            return RET_COMPANY;
        }

        @Mock
        public SearchCompanyResult search(SearchCompanyCondition condition)
            throws ServiceAbortException {
            if (SET_NO_DATA_FOUND) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_DATA_FOUND);
            }
            if (condition.getPageNo() == SET_ERROR_PAGE) {
                throw new ServiceAbortException(ApplicationMessageCode.NO_PAGE_FOUND);
            }
            return RET_SEARCH;
        }

        @Mock
        public byte[] generateExcel(List<Company> companies) throws ServiceAbortException {
            return RET_EXCEL;
        }

        @Mock
        public Long assignTo(Company company) throws ServiceAbortException {
            RET_COMPANY = company;
            return company.getId();
        }

        @Mock
        public void delete(Company company) {
            RET_DELETE = company;
        }

        @Mock
        public List<Company> searchNotAssigned() {
            return RET_SEARCH_NOT_PROJECT_COMPANY;
        }
    }

    public static class MockAbstractPage extends MockUp<AbstractPage> {
        static String RET_PROJID;
        static boolean RET_IS_SYSTEMADMIN;
        static boolean RET_IS_PROJECTADMIN;
        static boolean RET_IS_GROUPADMIN;

        @Mock
        public String getCurrentProjectId() {
            return RET_PROJID;
        }

        @Mock
        public boolean isSystemAdmin() {
            return RET_IS_SYSTEMADMIN;
        }

        @Mock
        public boolean isProjectAdmin(String checkProjectId) {
            return RET_IS_PROJECTADMIN;
        }

        @Mock
        public boolean isGroupAdmin(Long groupId) {
            return RET_IS_GROUPADMIN;
        }
    }

    public static class MockDateUtil extends MockUp<DateUtil> {
        static Date RET_DATE;

        @Mock
        public Date getNow() {
            return RET_DATE;
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
}
